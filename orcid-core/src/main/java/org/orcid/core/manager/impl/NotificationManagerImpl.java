package org.orcid.core.manager.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.orcid.core.adapter.JpaJaxbNotificationAdapter;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.permission_v2.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.Items;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermissions;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.jaxb.model.notification_v2.NotificationType;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.ActionableNotificationEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Will Simpson
 */
@Deprecated
public class NotificationManagerImpl extends ManagerReadOnlyBaseImpl implements NotificationManager {    

    private static final String AUTHORIZATION_END_POINT = "{0}/oauth/authorize?response_type=code&client_id={1}&scope={2}&redirect_uri={3}";

    @Resource(name = "messageSource")
    private MessageSource messages;
    
    @Resource(name = "messageSourceNoFallback")
    private MessageSource messageSourceNoFallback;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    private EncryptionManager encryptionManager;

    @Resource
    private ProfileEventDao profileEventDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private JpaJaxbNotificationAdapter notificationAdapter;

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private NotificationDao notificationDaoReadOnly;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnly")
    private EmailManagerReadOnly emailManager;
    
    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Resource
    private GivenPermissionToManagerReadOnly givenPermissionToManagerReadOnly;

    @Resource
    private SourceEntityUtils sourceEntityUtils;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManagerImpl.class);

    @Required
    public void setEncryptionManager(EncryptionManager encryptionManager) {
        this.encryptionManager = encryptionManager;
    }

    public void setProfileEventDao(ProfileEventDao profileEventDao) {
        this.profileEventDao = profileEventDao;
    }

    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public void setSourceManager(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    public void setNotificationDao(NotificationDao notificationDao) {
        this.notificationDao = notificationDao;
    }
    
    public void addMessageParams(Map<String, Object> templateParams, Locale locale) {
        Map<String, Boolean> features = getFeatures();
        templateParams.put("messages", this.messages);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("locale", locale);
        templateParams.put("features", features);
    }
    
    private Map<String, Boolean> getFeatures() {
        Map<String, Boolean> features = new HashMap<String, Boolean>();
        for(Features f : Features.values()) {
            features.put(f.name(), f.isActive());
        }
        return features;
    }

    private String getSubject(String code, Locale locale) {
        return messages.getMessage(code, null, locale);
    }    

    @Override
    public Notification sendAmendEmail(String userOrcid, AmendedSection amendedSection, Collection<Item> items) {
        String amenderOrcid = sourceManager.retrieveSourceOrcid();
        
        if (amenderOrcid == null) {
            LOGGER.info("Not sending amend email to {} because amender is null", userOrcid);
            return null;
        }
        if (amenderOrcid.equals(userOrcid)) {
            LOGGER.debug("Not sending amend email, because self edited: {}", userOrcid);
            return null;
        }

        String amenderType = profileDao.retrieveOrcidType(amenderOrcid);
        if (amenderType != null && OrcidType.ADMIN.equals(OrcidType.valueOf(amenderType))) {
            LOGGER.debug("Not sending amend email, because modified by admin ({}): {}", amenderOrcid, userOrcid);
            return null;
        }
        
        NotificationAmended notification = new NotificationAmended();
        notification.setNotificationType(NotificationType.AMENDED);
        notification.setAmendedSection(amendedSection);
        if (items != null) {
            notification.setItems(new Items(new ArrayList<>(items)));
        }
        return createNotification(userOrcid, notification);        
    }

    public String createEmailBaseUrl(String unencryptedParams, String baseUri, String path) {
        // Encrypt and encode params
        String encryptedUrlParams = encryptionManager.encryptForExternalUse(unencryptedParams);
        String base64EncodedParams = null;
        try {
            base64EncodedParams = Base64.encodeBase64URLSafeString(encryptedUrlParams.getBytes("UTF-8"));

        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        return String.format("%s/%s/%s", baseUri, path, base64EncodedParams);
    }    

    @Override 
    public Notification createPermissionNotification(String orcid, NotificationPermission notification) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        if (profile == null) {
            throw OrcidNotFoundException.newInstance(orcid);
        }
        
        return createNotification(orcid, notification);
    }
    
    private Notification createNotification(String orcid, Notification notification) {
        if (notification.getPutCode() != null) {
            throw new IllegalArgumentException("Put code must be null when creating a new notification");
        }
        NotificationEntity notificationEntity = notificationAdapter.toNotificationEntity(notification);        
        notificationEntity.setOrcid(orcid);

        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        if (sourceEntity != null) {
            // Set source id
            if (sourceEntity.getSourceProfile() != null) {
                notificationEntity.setSourceId(sourceEntity.getSourceProfile().getId());
            }

            if (sourceEntity.getSourceClient() != null) {
                notificationEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
            }
        } else {
            // If we can't find source id, set the user as the source
            notificationEntity.setSourceId(orcid);
        }

        notificationDao.persist(notificationEntity);
        return notificationAdapter.toNotification(notificationEntity);
    }

    @Override
    public List<Notification> findUnsentByOrcid(String orcid) {
        return notificationAdapter.toNotification(notificationDaoReadOnly.findUnsentByOrcid(orcid));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findByOrcid(String orcid, boolean includeArchived, int firstResult, int maxResults) {
        return notificationAdapter.toNotification(notificationDao.findByOrcid(orcid, includeArchived, firstResult, maxResults));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPermissions findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults) {
        NotificationPermissions notifications = new NotificationPermissions();
        List<Notification> notificationsForOrcidAndClient = notificationAdapter
                .toNotification(notificationDao.findPermissionsByOrcidAndClient(orcid, client, firstResult, maxResults));
        List<NotificationPermission> notificationPermissions = new ArrayList<>();
        notificationsForOrcidAndClient.forEach(n -> notificationPermissions.add((NotificationPermission) n));
        notifications.setNotifications(notificationPermissions);
        return notifications;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findNotificationAlertsByOrcid(String orcid) {
        return notificationAdapter.toNotification(notificationDao.findNotificationAlertsByOrcid(orcid));
    }

    @Override
    public List<Notification> filterActionedNotificationAlerts(Collection<Notification> notifications, String userOrcid) {
        return notifications.stream().filter(n -> {
            // Filter only INSTITUTIONAL_CONNECTION notifications
            if (NotificationType.INSTITUTIONAL_CONNECTION.equals(n.getNotificationType())) {
                boolean alreadyConnected = orcidOauth2TokenDetailService.doesClientKnowUser(n.getSource().retrieveSourcePath(), userOrcid);
                if (alreadyConnected) {
                    flagAsArchived(userOrcid, n.getPutCode(), false);
                }
                return !alreadyConnected;
            }
            return true;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findById(Long id) {
        return notificationAdapter.toNotification(notificationDao.find(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Notification findByOrcidAndId(String orcid, Long id) {
        return notificationAdapter.toNotification(notificationDao.findByOricdAndId(orcid, id));
    }

    @Override
    @Transactional
    public Notification flagAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        return flagAsArchived(orcid, id, true);
    }

    @Override
    @Transactional
    public Notification flagAsArchived(String orcid, Long id, boolean validateForApi) throws OrcidNotificationAlreadyReadException {
        NotificationEntity notificationEntity = notificationDao.findByOricdAndId(orcid, id);
        if (notificationEntity == null) {
            return null;
        }
        String sourceId = sourceManager.retrieveSourceOrcid();
        if (validateForApi) {
            if (sourceId != null && !sourceId.equals(notificationEntity.getElementSourceId())) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("activity", "notification");
                throw new WrongSourceException(params);
            }
            if (notificationEntity.getReadDate() != null) {
                throw new OrcidNotificationAlreadyReadException();
            }
        }
        if (notificationEntity.getArchivedDate() == null) {
            notificationEntity.setArchivedDate(new Date());
            notificationDao.merge(notificationEntity);
        }
        return notificationAdapter.toNotification(notificationEntity);
    }

    @Override
    @Transactional
    public Notification setActionedAndReadDate(String orcid, Long id) {
        NotificationEntity notificationEntity = notificationDao.findByOricdAndId(orcid, id);
        if (notificationEntity == null) {
            return null;
        }

        Date now = new Date();

        if (notificationEntity.getActionedDate() == null) {
            notificationEntity.setActionedDate(now);
            notificationDao.merge(notificationEntity);
        }

        if (notificationEntity.getReadDate() == null) {
            notificationEntity.setReadDate(now);
            notificationDao.merge(notificationEntity);
        }

        return notificationAdapter.toNotification(notificationEntity);
    }

    @Override
    public void sendAcknowledgeMessage(String userOrcid, String clientId) throws UnsupportedEncodingException {
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);
        String authorizationUrl = buildAuthorizationUrlForInstitutionalSignIn(clientDetails);

        NotificationInstitutionalConnection notification = new NotificationInstitutionalConnection();
        notification.setNotificationType(NotificationType.INSTITUTIONAL_CONNECTION);
        notification.setAuthorizationUrl(new AuthorizationUrl(authorizationUrl));
        NotificationInstitutionalConnectionEntity notificationEntity = (NotificationInstitutionalConnectionEntity) notificationAdapter
                .toNotificationEntity(notification);
        notificationEntity.setOrcid(userOrcid);
        notificationEntity.setClientSourceId(clientId);
        notificationEntity.setAuthenticationProviderId(clientDetails.getAuthenticationProviderId());
        notificationDao.persist(notificationEntity);
    }

    public String buildAuthorizationUrlForInstitutionalSignIn(ClientDetailsEntity clientDetails) throws UnsupportedEncodingException {
        ClientRedirectUriEntity rUri = getRedirectUriForInstitutionalSignIn(clientDetails);
        if (rUri == null) {
            return null;
        }
        String urlEncodedScopes = URLEncoder.encode(rUri.getPredefinedClientScope(), "UTF-8");
        String urlEncodedRedirectUri = URLEncoder.encode(rUri.getRedirectUri(), "UTF-8");
        return MessageFormat.format(AUTHORIZATION_END_POINT, orcidUrlManager.getBaseUrl(), clientDetails.getClientId(), urlEncodedScopes, urlEncodedRedirectUri);
    }

    private ClientRedirectUriEntity getRedirectUriForInstitutionalSignIn(ClientDetailsEntity clientDetails) {
        if (clientDetails == null) {
            throw new IllegalArgumentException("Unable to find valid redirect uris for null client details");
        }

        if (clientDetails.getClientRegisteredRedirectUris() == null) {
            throw new IllegalArgumentException("Unable to find valid redirect uris for client: " + clientDetails.getId());
        }

        ClientRedirectUriEntity result = null;

        // Look for the redirect uri of INSTITUTIONAL_SIGN_IN type or if none if
        // found, return the first DEFAULT one
        for (ClientRedirectUriEntity redirectUri : clientDetails.getClientRegisteredRedirectUris()) {
            if (RedirectUriType.INSTITUTIONAL_SIGN_IN.value().equals(redirectUri.getRedirectUriType())) {
                result = redirectUri;
                break;
            }
        }

        return result;
    }

    public int getUnreadCount(String orcid) {
        return notificationDao.getUnreadCount(orcid);
    }

    @Override
    public void flagAsRead(String orcid, Long id) {
        notificationDao.flagAsRead(orcid, id);
    }

    @Override
    public ActionableNotificationEntity findActionableNotificationEntity(Long id) {
        return (ActionableNotificationEntity) notificationDao.find(id);
    }

    @Override
    public List<Notification> findNotificationsToSend(String orcid, Float emailFrequencyDays, Date recordActiveDate) {
        List<NotificationEntity> notifications = new ArrayList<NotificationEntity>();
        notifications = notificationDao.findNotificationsToSend(new Date(), orcid, recordActiveDate);          
        return notificationAdapter.toNotification(notifications);
    }

    @Override
    public void processOldNotificationsToAutoArchive() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, -6);
        Date createdBefore = calendar.getTime();
        LOGGER.info("About to auto archive notifications created before {}", createdBefore);
        int numArchived = 0;
        do {
            numArchived = notificationDao.archiveNotificationsCreatedBefore(createdBefore, 100);
            LOGGER.info("Archived {} old notifications", numArchived);
        } while (numArchived != 0);
    }

    @Override
    public void processOldNotificationsToAutoDelete() {
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.YEAR, -1);
        Date createdBefore = calendar.getTime();
        LOGGER.info("About to auto delete notifications created before {}", createdBefore);
        List<NotificationEntity> notificationsToDelete = Collections.<NotificationEntity> emptyList();
        do {
            notificationsToDelete = notificationDao.findNotificationsCreatedBefore(createdBefore, 100);
            LOGGER.info("Got batch of {} old notifications to delete", notificationsToDelete.size());
            for (NotificationEntity notification : notificationsToDelete) {
                LOGGER.info("About to delete old notification: id={}, orcid={}, dateCreated={}",
                        new Object[] { notification.getId(), notification.getOrcid(), notification.getDateCreated() });
                removeNotification(notification.getId());
            }
        } while (!notificationsToDelete.isEmpty());
    }
    
    @Override
    public void removeNotification(Long notificationId) {
        notificationDao.remove(notificationId);
    }    

}
