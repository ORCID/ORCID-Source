package org.orcid.frontend.spring.session.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.orcid.api.common.analytics.APIEndpointParser;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.session.*;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionExpiredEvent;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class OrcidRedisIndexedSessionRepository implements FindByIndexNameSessionRepository<OrcidRedisIndexedSessionRepository.RedisSession>, MessageListener {
    private static final Log logger = LogFactory.getLog(OrcidRedisIndexedSessionRepository.class);
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    public static final int DEFAULT_DATABASE = 0;
    public static final String DEFAULT_NAMESPACE = "spring:session";
    private int database = 0;
    private String namespace = "spring:session:";
    private String sessionCreatedChannelPrefix;
    private byte[] sessionCreatedChannelPrefixBytes;
    private String sessionDeletedChannel;
    private byte[] sessionDeletedChannelBytes;
    private String sessionExpiredChannel;
    private byte[] sessionExpiredChannelBytes;
    private String expiredKeyPrefix;
    private byte[] expiredKeyPrefixBytes;
    private final RedisOperations<Object, Object> sessionRedisOperations;
    private final OrcidRedisSessionExpirationPolicy expirationPolicy;
    private ApplicationEventPublisher eventPublisher = (event) -> {
    };
    private Integer defaultMaxInactiveInterval;
    private IndexResolver<Session> indexResolver = new DelegatingIndexResolver(new IndexResolver[]{new PrincipalNameIndexResolver()});
    private RedisSerializer<Object> defaultSerializer = new JdkSerializationRedisSerializer();
    private FlushMode flushMode;
    private SaveMode saveMode;
    private final String PUBLIC_ORCID_PAGE_REGEX = "/(\\d{4}-){3,}\\d{3}[\\dX](/.+)";
    private final List<String> urisToSkip = List.of("/2FA/status.json", "/account/", "/account/biographyForm.json", "/account/countryForm.json", "/account/delegates.json", "/account/emails.json",
            "/account/get-trusted-orgs.json", "/account/nameForm.json", "/account/preferences.json", "/account/socialAccounts.json", "/affiliations/affiliationDetails.json", "/affiliations/affiliationGroups.json",
            "/assets/vectors/orcid.logo.icon.svg", "/config.json", "/delegators/delegators-and-me.json", "/fundings/fundingDetails.json", "/fundings/fundingGroups.json", "/inbox/notifications.json",
            "/inbox/totalCount.json", "/inbox/unreadCount.json", "/my-orcid/externalIdentifiers.json", "/my-orcid/keywordsForms.json", "/my-orcid/otherNamesForms.json", "/my-orcid/websitesForms.json",
            "/ng-cli-ws", "/not-found", "/notifications/frequencies/view", "/orgs/disambiguated/FUNDREF", "/orgs/disambiguated/GRID", "/orgs/disambiguated/LEI", "/orgs/disambiguated/RINGGOLD",
            "/orgs/disambiguated/ROR", "/peer-reviews/peer-review.json", "/peer-reviews/peer-reviews-by-group-id.json", "/peer-reviews/peer-reviews-minimized.json", "/qr-code.png",
            "/research-resources/researchResource.json", "/research-resources/researchResourcePage.json", "/works/getWorkInfo.json", "/works/groupingSuggestions.json", "/works/idTypes.json", "/works/work.json",
            "/works/worksExtendedPage.json");
    private final Set<String> SKIP_SAVE_SESSION = new HashSet<>(urisToSkip);

    public OrcidRedisIndexedSessionRepository(RedisOperations<Object, Object> sessionRedisOperations) {
        this.flushMode = FlushMode.ON_SAVE;
        this.saveMode = SaveMode.ON_SET_ATTRIBUTE;
        Assert.notNull(sessionRedisOperations, "sessionRedisOperations cannot be null");
        this.sessionRedisOperations = sessionRedisOperations;
        this.expirationPolicy = new OrcidRedisSessionExpirationPolicy(sessionRedisOperations, this::getExpirationsKey, this::getSessionKey);
        this.configureSessionChannels();
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        Assert.notNull(applicationEventPublisher, "applicationEventPublisher cannot be null");
        this.eventPublisher = applicationEventPublisher;
    }

    public void setDefaultMaxInactiveInterval(int defaultMaxInactiveInterval) {
        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
    }

    public void setIndexResolver(IndexResolver<Session> indexResolver) {
        Assert.notNull(indexResolver, "indexResolver cannot be null");
        this.indexResolver = indexResolver;
    }

    public void setDefaultSerializer(RedisSerializer<Object> defaultSerializer) {
        Assert.notNull(defaultSerializer, "defaultSerializer cannot be null");
        this.defaultSerializer = defaultSerializer;
    }

    public void setFlushMode(FlushMode flushMode) {
        Assert.notNull(flushMode, "flushMode cannot be null");
        this.flushMode = flushMode;
    }

    public void setSaveMode(SaveMode saveMode) {
        Assert.notNull(saveMode, "saveMode must not be null");
        this.saveMode = saveMode;
    }

    public void setDatabase(int database) {
        this.database = database;
        this.configureSessionChannels();
    }

    private void configureSessionChannels() {
        this.sessionCreatedChannelPrefix = this.namespace + "event:" + this.database + ":created:";
        this.sessionCreatedChannelPrefixBytes = this.sessionCreatedChannelPrefix.getBytes();
        this.sessionDeletedChannel = "__keyevent@" + this.database + "__:del";
        this.sessionDeletedChannelBytes = this.sessionDeletedChannel.getBytes();
        this.sessionExpiredChannel = "__keyevent@" + this.database + "__:expired";
        this.sessionExpiredChannelBytes = this.sessionExpiredChannel.getBytes();
        this.expiredKeyPrefix = this.namespace + "sessions:expires:";
        this.expiredKeyPrefixBytes = this.expiredKeyPrefix.getBytes();
    }

    public RedisOperations<Object, Object> getSessionRedisOperations() {
        return this.sessionRedisOperations;
    }

    public void save(OrcidRedisIndexedSessionRepository.RedisSession session) {
        // TODO: REMOVE ALL THIS BEFORE GOING LIVE!!!
        ServletRequestAttributes att = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = att.getRequest();
        ///////////////////////////////////////////////

        if(updateSession()) {
            //TODO: REMOVE THIS LOG ENTRY BEFORE GOING LIVE!!!!
            logger.info("Saving session for " + request.getRequestURI() + " - " + request.getMethod());
            session.save();
            if (session.isNew) {
                String sessionCreatedKey = this.getSessionCreatedChannel(session.getId());
                this.sessionRedisOperations.convertAndSend(sessionCreatedKey, session.delta);
                session.isNew = false;
            }
        } else {
            //TODO: REMOVE THIS LOG ENTRY BEFORE GOING LIVE!!!!
            logger.info("Skip save session id " + request.getRequestURI() + " - " + request.getMethod());
        }
    }

    public void cleanupExpiredSessions() {
        this.expirationPolicy.cleanExpiredSessions();
    }

    public OrcidRedisIndexedSessionRepository.RedisSession findById(String id) {
        return this.getSession(id, false);
    }

    public Map<String, OrcidRedisIndexedSessionRepository.RedisSession> findByIndexNameAndIndexValue(String indexName, String indexValue) {
        if (!PRINCIPAL_NAME_INDEX_NAME.equals(indexName)) {
            return Collections.emptyMap();
        } else {
            String principalKey = this.getPrincipalKey(indexValue);
            Set<Object> sessionIds = this.sessionRedisOperations.boundSetOps(principalKey).members();
            Map<String, OrcidRedisIndexedSessionRepository.RedisSession> sessions = new HashMap(sessionIds.size());
            Iterator var6 = sessionIds.iterator();

            while(var6.hasNext()) {
                Object id = var6.next();
                OrcidRedisIndexedSessionRepository.RedisSession session = this.findById((String)id);
                if (session != null) {
                    sessions.put(session.getId(), session);
                }
            }

            return sessions;
        }
    }

    private OrcidRedisIndexedSessionRepository.RedisSession getSession(String id, boolean allowExpired) {
        Map<Object, Object> entries = this.getSessionBoundHashOperations(id).entries();
        if (entries.isEmpty()) {
            return null;
        } else {
            MapSession loaded = this.loadSession(id, entries);
            if (!allowExpired && loaded.isExpired()) {
                return null;
            } else {
                OrcidRedisIndexedSessionRepository.RedisSession result = new OrcidRedisIndexedSessionRepository.RedisSession(loaded, false);
                result.originalLastAccessTime = loaded.getLastAccessedTime();
                return result;
            }
        }
    }

    private MapSession loadSession(String id, Map<Object, Object> entries) {
        MapSession loaded = new MapSession(id);
        Iterator var4 = entries.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry)var4.next();
            String key = (String)entry.getKey();
            if ("creationTime".equals(key)) {
                loaded.setCreationTime(Instant.ofEpochMilli((Long)entry.getValue()));
            } else if ("maxInactiveInterval".equals(key)) {
                loaded.setMaxInactiveInterval(Duration.ofSeconds((long)(Integer)entry.getValue()));
            } else if ("lastAccessedTime".equals(key)) {
                // TODO: REMOVE ALL THIS BEFORE GOING LIVE!!!
                ServletRequestAttributes att = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = att.getRequest();
                ///////////////////////////////////////////////
                if(updateSession()) {
                    // TODO: REMOVE THIS LOG ENTRY BEFORE GOING LIVE!!!
                    logger.info("Updating last accessed time for " + request.getRequestURI() + " - " + request.getMethod());
                    loaded.setLastAccessedTime(Instant.ofEpochMilli((Long) entry.getValue()));
                } else {
                    // TODO: REMOVE THIS LOG ENTRY BEFORE GOING LIVE!!!
                    logger.info("Ignoring last accessed time for " + request.getRequestURI() + " - " + request.getMethod());
                }
            } else if (key.startsWith("sessionAttr:")) {
                loaded.setAttribute(key.substring("sessionAttr:".length()), entry.getValue());
            }
        }

        return loaded;
    }

    public void deleteById(String sessionId) {
        OrcidRedisIndexedSessionRepository.RedisSession session = this.getSession(sessionId, true);
        if (session != null) {
            this.cleanupPrincipalIndex(session);
            this.expirationPolicy.onDelete(session);
            String expireKey = this.getExpiredKey(session.getId());
            this.sessionRedisOperations.delete(expireKey);
            session.setMaxInactiveInterval(Duration.ZERO);
            this.save(session);
        }
    }

    public OrcidRedisIndexedSessionRepository.RedisSession createSession() {
        MapSession cached = new MapSession();
        if (this.defaultMaxInactiveInterval != null) {
            cached.setMaxInactiveInterval(Duration.ofSeconds((long)this.defaultMaxInactiveInterval));
        }

        OrcidRedisIndexedSessionRepository.RedisSession session = new OrcidRedisIndexedSessionRepository.RedisSession(cached, true);
        session.flushImmediateIfNecessary();
        return session;
    }

    public void onMessage(Message message, byte[] pattern) {
        byte[] messageChannel = message.getChannel();
        if (ByteUtils.startsWith(messageChannel, this.sessionCreatedChannelPrefixBytes)) {
            Map<Object, Object> loaded = (Map)this.defaultSerializer.deserialize(message.getBody());
            this.handleCreated(loaded, new String(messageChannel));
        } else {
            byte[] messageBody = message.getBody();
            if (ByteUtils.startsWith(messageBody, this.expiredKeyPrefixBytes)) {
                boolean isDeleted = Arrays.equals(messageChannel, this.sessionDeletedChannelBytes);
                if (isDeleted || Arrays.equals(messageChannel, this.sessionExpiredChannelBytes)) {
                    String body = new String(messageBody);
                    int beginIndex = body.lastIndexOf(":") + 1;
                    int endIndex = body.length();
                    String sessionId = body.substring(beginIndex, endIndex);
                    OrcidRedisIndexedSessionRepository.RedisSession session = this.getSession(sessionId, true);
                    if (session == null) {
                        logger.warn("Unable to publish SessionDestroyedEvent for session " + sessionId);
                        return;
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("Publishing SessionDestroyedEvent for session " + sessionId);
                    }

                    this.cleanupPrincipalIndex(session);
                    if (isDeleted) {
                        this.handleDeleted(session);
                    } else {
                        this.handleExpired(session);
                    }
                }

            }
        }
    }

    private void cleanupPrincipalIndex(OrcidRedisIndexedSessionRepository.RedisSession session) {
        String sessionId = session.getId();
        Map<String, String> indexes = this.indexResolver.resolveIndexesFor(session);
        String principal = (String)indexes.get(PRINCIPAL_NAME_INDEX_NAME);
        if (principal != null) {
            this.sessionRedisOperations.boundSetOps(this.getPrincipalKey(principal)).remove(new Object[]{sessionId});
        }

    }

    private void handleCreated(Map<Object, Object> loaded, String channel) {
        String id = channel.substring(channel.lastIndexOf(":") + 1);
        Session session = this.loadSession(id, loaded);
        this.publishEvent(new SessionCreatedEvent(this, session));
    }

    private void handleDeleted(OrcidRedisIndexedSessionRepository.RedisSession session) {
        this.publishEvent(new SessionDeletedEvent(this, session));
    }

    private void handleExpired(OrcidRedisIndexedSessionRepository.RedisSession session) {
        this.publishEvent(new SessionExpiredEvent(this, session));
    }

    private void publishEvent(ApplicationEvent event) {
        try {
            this.eventPublisher.publishEvent(event);
        } catch (Throwable var3) {
            Throwable ex = var3;
            logger.error("Error publishing " + event + ".", ex);
        }

    }

    public void setRedisKeyNamespace(String namespace) {
        Assert.hasText(namespace, "namespace cannot be null or empty");
        this.namespace = namespace.trim() + ":";
        this.configureSessionChannels();
    }

    String getSessionKey(String sessionId) {
        return this.namespace + "sessions:" + sessionId;
    }

    String getPrincipalKey(String principalName) {
        return this.namespace + "index:" + FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME + ":" + principalName;
    }

    String getExpirationsKey(long expiration) {
        return this.namespace + "expirations:" + expiration;
    }

    private String getExpiredKey(String sessionId) {
        return this.getExpiredKeyPrefix() + sessionId;
    }

    private String getSessionCreatedChannel(String sessionId) {
        return this.getSessionCreatedChannelPrefix() + sessionId;
    }

    private String getExpiredKeyPrefix() {
        return this.expiredKeyPrefix;
    }

    public String getSessionCreatedChannelPrefix() {
        return this.sessionCreatedChannelPrefix;
    }

    public String getSessionDeletedChannel() {
        return this.sessionDeletedChannel;
    }

    public String getSessionExpiredChannel() {
        return this.sessionExpiredChannel;
    }

    private BoundHashOperations<Object, Object, Object> getSessionBoundHashOperations(String sessionId) {
        String key = this.getSessionKey(sessionId);
        return this.sessionRedisOperations.boundHashOps(key);
    }

    private boolean updateSession() {
        ServletRequestAttributes att = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = att.getRequest();
        if(request.getMethod().equals("GET")) {
            String url = request.getRequestURI().substring(request.getContextPath().length());
            if(SKIP_SAVE_SESSION.contains(url) || url.matches(PUBLIC_ORCID_PAGE_REGEX)) {
                return false;
            }
        }
        return true;
    }

    static String getSessionAttrNameKey(String attributeName) {
        return "sessionAttr:" + attributeName;
    }

    final class RedisSession implements Session {
        private final MapSession cached;
        private Instant originalLastAccessTime;
        private Map<String, Object> delta = new HashMap();
        private boolean isNew;
        private String originalPrincipalName;
        private String originalSessionId;

        RedisSession(MapSession cached, boolean isNew) {
            this.cached = cached;
            this.isNew = isNew;
            this.originalSessionId = cached.getId();
            Map<String, String> indexes = OrcidRedisIndexedSessionRepository.this.indexResolver.resolveIndexesFor(this);
            this.originalPrincipalName = (String)indexes.get(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME);
            if (this.isNew) {
                this.delta.put("creationTime", cached.getCreationTime().toEpochMilli());
                this.delta.put("maxInactiveInterval", (int)cached.getMaxInactiveInterval().getSeconds());
                this.delta.put("lastAccessedTime", cached.getLastAccessedTime().toEpochMilli());
            }

            if (this.isNew || OrcidRedisIndexedSessionRepository.this.saveMode == SaveMode.ALWAYS) {
                this.getAttributeNames().forEach((attributeName) -> {
                    this.delta.put(OrcidRedisIndexedSessionRepository.getSessionAttrNameKey(attributeName), cached.getAttribute(attributeName));
                });
            }

        }

        public void setLastAccessedTime(Instant lastAccessedTime) {
            if(updateSession()) {
                // TODO: REMOVE THIS BEFORE GOING LIVE!!!!
                ServletRequestAttributes att = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = att.getRequest();
                System.out.println("REDIS_SESSION: setLastAccessedTime: " + request.getRequestURI().toString() + " - " + request.getMethod());

                this.cached.setLastAccessedTime(lastAccessedTime);
                this.delta.put("lastAccessedTime", this.getLastAccessedTime().toEpochMilli());
                this.flushImmediateIfNecessary();
            }
        }

        public boolean isExpired() {
            return this.cached.isExpired();
        }

        public Instant getCreationTime() {
            return this.cached.getCreationTime();
        }

        public String getId() {
            return this.cached.getId();
        }

        public String changeSessionId() {
            return this.cached.changeSessionId();
        }

        public Instant getLastAccessedTime() {
            return this.cached.getLastAccessedTime();
        }

        public void setMaxInactiveInterval(Duration interval) {
            this.cached.setMaxInactiveInterval(interval);
            this.delta.put("maxInactiveInterval", (int)this.getMaxInactiveInterval().getSeconds());
            this.flushImmediateIfNecessary();
        }

        public Duration getMaxInactiveInterval() {
            return this.cached.getMaxInactiveInterval();
        }

        public <T> T getAttribute(String attributeName) {
            T attributeValue = this.cached.getAttribute(attributeName);
            if (attributeValue != null && OrcidRedisIndexedSessionRepository.this.saveMode.equals(SaveMode.ON_GET_ATTRIBUTE)) {
                this.delta.put(OrcidRedisIndexedSessionRepository.getSessionAttrNameKey(attributeName), attributeValue);
            }

            return attributeValue;
        }

        public Set<String> getAttributeNames() {
            return this.cached.getAttributeNames();
        }

        public void setAttribute(String attributeName, Object attributeValue) {
            this.cached.setAttribute(attributeName, attributeValue);
            this.delta.put(OrcidRedisIndexedSessionRepository.getSessionAttrNameKey(attributeName), attributeValue);
            this.flushImmediateIfNecessary();
        }

        public void removeAttribute(String attributeName) {
            this.cached.removeAttribute(attributeName);
            this.delta.put(OrcidRedisIndexedSessionRepository.getSessionAttrNameKey(attributeName), (Object)null);
            this.flushImmediateIfNecessary();
        }

        private void flushImmediateIfNecessary() {
            if (OrcidRedisIndexedSessionRepository.this.flushMode == FlushMode.IMMEDIATE) {
                this.save();
            }

        }

        private void save() {
            // TODO: REMOVE THIS LOG ENTRY BEFORE GOING LIVE!!!!
            logger.info("REDIS_SESSION: save");
            this.saveChangeSessionId();
            this.saveDelta();
        }

        private void saveDelta() {
            if (!this.delta.isEmpty()) {String sessionId = this.getId();
                OrcidRedisIndexedSessionRepository.this.getSessionBoundHashOperations(sessionId).putAll(this.delta);
                String principalSessionKey = OrcidRedisIndexedSessionRepository.getSessionAttrNameKey(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME);
                String securityPrincipalSessionKey = OrcidRedisIndexedSessionRepository.getSessionAttrNameKey("SPRING_SECURITY_CONTEXT");
                if (this.delta.containsKey(principalSessionKey) || this.delta.containsKey(securityPrincipalSessionKey)) {
                    if (this.originalPrincipalName != null) {
                        String originalPrincipalRedisKey = OrcidRedisIndexedSessionRepository.this.getPrincipalKey(this.originalPrincipalName);
                        OrcidRedisIndexedSessionRepository.this.sessionRedisOperations.boundSetOps(originalPrincipalRedisKey).remove(new Object[]{sessionId});
                    }

                    Map<String, String> indexes = OrcidRedisIndexedSessionRepository.this.indexResolver.resolveIndexesFor(this);
                    String principal = (String)indexes.get(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME);
                    this.originalPrincipalName = principal;
                    if (principal != null) {
                        String principalRedisKey = OrcidRedisIndexedSessionRepository.this.getPrincipalKey(principal);
                        OrcidRedisIndexedSessionRepository.this.sessionRedisOperations.boundSetOps(principalRedisKey).add(new Object[]{sessionId});
                    }
                }

                this.delta = new HashMap(this.delta.size());
                Long originalExpiration = this.originalLastAccessTime != null ? this.originalLastAccessTime.plus(this.getMaxInactiveInterval()).toEpochMilli() : null;
                OrcidRedisIndexedSessionRepository.this.expirationPolicy.onExpirationUpdated(originalExpiration, this);
            }
        }

        private void saveChangeSessionId() {
            String sessionId = this.getId();
            if (!sessionId.equals(this.originalSessionId)) {
                if (!this.isNew) {
                    String originalSessionIdKey = OrcidRedisIndexedSessionRepository.this.getSessionKey(this.originalSessionId);
                    String sessionIdKey = OrcidRedisIndexedSessionRepository.this.getSessionKey(sessionId);

                    try {
                        OrcidRedisIndexedSessionRepository.this.sessionRedisOperations.rename(originalSessionIdKey, sessionIdKey);
                    } catch (NonTransientDataAccessException var8) {
                        NonTransientDataAccessException exx = var8;
                        this.handleErrNoSuchKeyError(exx);
                    }

                    String originalExpiredKey = OrcidRedisIndexedSessionRepository.this.getExpiredKey(this.originalSessionId);
                    String expiredKey = OrcidRedisIndexedSessionRepository.this.getExpiredKey(sessionId);

                    try {
                        OrcidRedisIndexedSessionRepository.this.sessionRedisOperations.rename(originalExpiredKey, expiredKey);
                    } catch (NonTransientDataAccessException var7) {
                        NonTransientDataAccessException ex = var7;
                        this.handleErrNoSuchKeyError(ex);
                    }

                    if (this.originalPrincipalName != null) {
                        String originalPrincipalRedisKey = OrcidRedisIndexedSessionRepository.this.getPrincipalKey(this.originalPrincipalName);
                        OrcidRedisIndexedSessionRepository.this.sessionRedisOperations.boundSetOps(originalPrincipalRedisKey).remove(new Object[]{this.originalSessionId});
                        OrcidRedisIndexedSessionRepository.this.sessionRedisOperations.boundSetOps(originalPrincipalRedisKey).add(new Object[]{sessionId});
                    }
                }

                this.originalSessionId = sessionId;
            }
        }

        private void handleErrNoSuchKeyError(NonTransientDataAccessException ex) {
            String message = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
            if (!StringUtils.startsWithIgnoreCase(message, "ERR no such key")) {
                throw ex;
            }
        }
    }
}
