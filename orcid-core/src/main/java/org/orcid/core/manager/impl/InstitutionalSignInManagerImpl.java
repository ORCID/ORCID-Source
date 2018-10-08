package org.orcid.core.manager.impl;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.HeaderCheckResult;
import org.orcid.pojo.HeaderMismatch;
import org.orcid.pojo.RemoteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class InstitutionalSignInManagerImpl implements InstitutionalSignInManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstitutionalSignInManagerImpl.class);

    private static final String SEPARATOR = ";";

    private static final Pattern ATTRIBUTE_SEPARATOR_PATTERN = Pattern.compile("(?<!\\\\)" + SEPARATOR);

    private static final Pattern ESCAPED_SEPARATOR_PATTERN = Pattern.compile("\\\\" + SEPARATOR);

    @Resource
    protected UserConnectionDao userConnectionDao;

    @Resource
    protected OrcidUrlManager orcidUrlManager;

    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    protected OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource
    protected NotificationManager notificationManager;

    @Resource
    private SlackManager slackManager;

    @Override
    @Transactional
    public void createUserConnectionAndNotify(String idType, String remoteUserId, String displayName, String providerId, String userOrcid, Map<String, String> headers)
            throws UnsupportedEncodingException {
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(remoteUserId, providerId, idType);
        if (userConnectionEntity == null) {
            LOGGER.info("No user connection found for idType={}, remoteUserId={}, displayName={}, providerId={}, userOrcid={}",
                    new Object[] { idType, remoteUserId, displayName, providerId, userOrcid });
            userConnectionEntity = new UserconnectionEntity();
            String randomId = Long.toString(new Random(Calendar.getInstance().getTimeInMillis()).nextLong());
            UserconnectionPK pk = new UserconnectionPK(randomId, providerId, remoteUserId);
            userConnectionEntity.setOrcid(userOrcid);
            userConnectionEntity.setProfileurl(orcidUrlManager.getBaseUriHttp() + "/" + userOrcid);
            userConnectionEntity.setDisplayname(displayName);
            userConnectionEntity.setRank(1);
            userConnectionEntity.setId(pk);
            userConnectionEntity.setLinked(true);
            userConnectionEntity.setLastLogin(new Date());
            userConnectionEntity.setIdType(idType);
            userConnectionEntity.setConnectionSatus(UserConnectionStatus.NOTIFIED);
            userConnectionEntity.setHeadersJson(JsonUtils.convertToJsonString(headers));
            userConnectionDao.persist(userConnectionEntity);
        } else {
            LOGGER.info("Found existing user connection, {}", userConnectionEntity);
        }

        sendNotification(userOrcid, providerId);
    }

    @Override
    public void sendNotification(String userOrcid, String providerId) throws UnsupportedEncodingException {
        try {
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieveByIdP(providerId);
            boolean clientKnowsUser = orcidOauth2TokenDetailService.doesClientKnowUser(clientDetails.getClientId(), userOrcid);
            // If the client doesn't know about the user yet, send a
            // notification
            if (!clientKnowsUser) {
                notificationManager.sendAcknowledgeMessage(userOrcid, clientDetails.getClientId());
            }
        } catch (IllegalArgumentException e) {
            // The provided IdP hasn't not been linked to any client yet.
        }
    }

    @Override
    public HeaderCheckResult checkHeaders(Map<String, String> originalHeaders, Map<String, String> currentHeaders) {
        HeaderCheckResult result = new HeaderCheckResult();
        List<String> headersToCheck = new ArrayList<>();
        headersToCheck.addAll(Arrays.asList(POSSIBLE_REMOTE_USER_HEADERS));
        headersToCheck.add(EPPN_HEADER);
        for (String headerName : headersToCheck) {
            String original = originalHeaders.get(headerName);
            String current = currentHeaders.get(headerName);
            // Only compare where both are not blank, because otherwise could
            // just be an IdP config change to add/remove the attribute
            if (StringUtils.isNoneBlank(original, current)) {
                Set<String> originalDeduped = dedupe(original);
                Set<String> currentDeduped = dedupe(current);
                if (!currentDeduped.equals(originalDeduped)) {
                    result.addMismatch(new HeaderMismatch(headerName, original, current));
                }
            }
        }
        if (!result.isSuccess()) {
            String message = String.format("Institutional sign in header check failed: %s, originalHeaders=%s", result, originalHeaders);
            LOGGER.info(message);
            slackManager.sendSystemAlert(message);
        }
        return result;
    }

    private Set<String> dedupe(String headerValue) {
        String[] values = ATTRIBUTE_SEPARATOR_PATTERN.split(headerValue);
        Set<String> deduped = new HashSet<>();
        for (String value : values) {
            deduped.add(value);
        }
        return deduped;
    }

    @Override
    public RemoteUser retrieveRemoteUser(Map<String, String> headers) {
        for (String possibleHeader : InstitutionalSignInManager.POSSIBLE_REMOTE_USER_HEADERS) {
            String userId = extractFirst(headers.get(possibleHeader));
            if (userId != null) {
                return new RemoteUser(userId, possibleHeader);
            }
        }
        return null;
    }

    @Override
    public String retrieveDisplayName(Map<String, String> headers) {
        String eppn = extractFirst(headers.get(InstitutionalSignInManager.EPPN_HEADER));
        if (StringUtils.isNotBlank(eppn)) {
            return eppn;
        }
        String displayName = extractFirst(headers.get(InstitutionalSignInManager.DISPLAY_NAME_HEADER));
        if (StringUtils.isNotBlank(displayName)) {
            return displayName;
        }
        String givenName = extractFirst(headers.get(InstitutionalSignInManager.GIVEN_NAME_HEADER));
        String sn = extractFirst(headers.get(InstitutionalSignInManager.SN_HEADER));
        String combinedNames = StringUtils.join(new String[] { givenName, sn }, ' ');
        if (StringUtils.isNotBlank(combinedNames)) {
            return combinedNames;
        }
        RemoteUser remoteUser = retrieveRemoteUser(headers);
        if (remoteUser != null) {
            String remoteUserId = remoteUser.getUserId();
            if (StringUtils.isNotBlank(remoteUserId)) {
                int indexOfBang = remoteUserId.lastIndexOf("!");
                if (indexOfBang != -1) {
                    return remoteUserId.substring(indexOfBang);
                } else {
                    return remoteUserId;
                }
            }
        }
        return null;
    }
    
    @Override
    public String retrieveFirstName(Map<String, String> headers) {
        String givenName = extractFirst(headers.get(InstitutionalSignInManager.GIVEN_NAME_HEADER));
        return givenName != null ? givenName : "";
    }

    @Override
    public String retrieveLastName(Map<String, String> headers) {
        String lastName = extractFirst(headers.get(InstitutionalSignInManager.SN_HEADER));
        return lastName != null ? lastName : "";
    }

    /**
     * Shibboleth SP combines multiple values by concatenating, using semicolon
     * as the separator (the escape character is '\'). Mutliple values will be
     * provided, even if it is actually the same attribute in mace and oid
     * format.
     * 
     * @param headerValue
     * @return the first attribute value
     */
    private static String extractFirst(String headerValue) {
        if (headerValue == null) {
            return null;
        }
        String[] values = ATTRIBUTE_SEPARATOR_PATTERN.split(headerValue);
        return values.length > 0 ? ESCAPED_SEPARATOR_PATTERN.matcher(values[0]).replaceAll(SEPARATOR) : "";
    }

}
