package org.orcid.core.manager.v3.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.exception.OrcidNotFoundException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.FindMyStuffManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.core.manager.v3.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.common.OrcidType;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.NotificationType;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationAdministrative;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.Items;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermissions;
import org.orcid.model.v3.release.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.model.v3.release.notification.internal.NotificationFindMyStuff;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.ActionableNotificationEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.EmailEventEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationFindMyStuffEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.core.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Will Simpson
 */
public class NotificationManagerImpl extends ManagerReadOnlyBaseImpl implements NotificationManager {

    private static final String AUTHORIZATION_END_POINT = "{0}/oauth/authorize?response_type=code&client_id={1}&scope={2}&redirect_uri={3}";

    public static final int DELETE_BATCH_SIZE = 500;
    
    @Resource(name = "messageSource")
    private MessageSource messages;
    
    @Resource(name = "messageSourceNoFallback")
    private MessageSource messageSourceNoFallback;      

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Value("${org.orcid.core.mail.apiRecordCreationEmailEnabled:true}")
    private boolean apiRecordCreationEmailEnabled;

    private TemplateManager templateManager;

    private EncryptionManager encryptionManager;

    @Resource
    private ProfileEventDao profileEventDao;

    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private ProfileDao profileDaoReadOnly;

    @Resource(name = "jpaJaxbNotificationAdapterV3")
    private JpaJaxbNotificationAdapter notificationAdapter;

    @Resource
    private NotificationDao notificationDao;    
    
    @Resource
    private NotificationDao notificationDaoReadOnly;    
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManager;
    
    @Resource
    private GenericDao<EmailEventEntity, Long> emailEventDao;
    
    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource
    private GivenPermissionToManagerReadOnly givenPermissionToManagerReadOnly;

    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Value("${org.orcid.notifications.archive.offset:100}")
    private Integer notificationArchiveOffset;
    
    @Value("${org.orcid.notifications.delete.offset:10000}")
    private Integer notificationDeleteOffset;
    
    @Value("${org.orcid.notifications.delete.offset.records:10}")
    private Integer recordsPerBatch;
    
    @Resource
    FindMyStuffManager findMyStuffManager;
    
    @Resource
    private SourceEntityUtils sourceEntityUtils;
    
    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManagerV3;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationManagerImpl.class);

    @Required
    public void setTemplateManager(TemplateManager templateManager) {
        this.templateManager = templateManager;
    }

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

    public String createUpdateEmailFrequencyUrl(String email) {
        return createEmailBaseUrl(email, orcidUrlManager.getBaseUrl(), "notifications/frequencies");
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
        String amenderOrcid = sourceManager.retrieveActiveSourceId();
        
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

    @Override
    @Transactional
    public void sendNotificationToAddedDelegate(String userGrantingPermission, String userReceivingPermission) {
        ProfileEntity delegateProfileEntity = profileEntityCacheManager.retrieve(userReceivingPermission);

        Locale userLocale = getUserLocaleFromProfileEntity(delegateProfileEntity);
        String subject = getSubject("email.subject.added_as_delegate", userLocale);

        String emailNameGrantingPermission = recordNameManagerV3.deriveEmailFriendlyName(userGrantingPermission);

        StringBuffer sb = new StringBuffer();
        sb.append(emailNameGrantingPermission);
        sb.append(" ");
        sb.append(getSubject("notification.delegate.receipt.trustedIndividual", userLocale));
        subject = sb.toString();

        org.orcid.jaxb.model.v3.release.record.Email primaryEmail = emailManager.findPrimaryEmail(userGrantingPermission);
        String grantingOrcidEmail = primaryEmail.getEmail();
        String emailNameForDelegate = recordNameManagerV3.deriveEmailFriendlyName(userReceivingPermission);
        String assetsUrl = getAssetsUrl();
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailNameForDelegate", emailNameForDelegate);
        templateParams.put("emailName", emailNameForDelegate);
        templateParams.put("orcidValue", userReceivingPermission);
        templateParams.put("emailNameGrantingPermission", emailNameGrantingPermission);
        templateParams.put("emailNameGrantingPermissionWithApostrophe", emailNameGrantingPermission + "'s");
        templateParams.put("grantingOrcidValue", userGrantingPermission);
        templateParams.put("grantingOrcidName", recordNameManagerV3.deriveEmailFriendlyName(userGrantingPermission));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("grantingOrcidEmail", grantingOrcidEmail);
        templateParams.put("subject", subject);
        templateParams.put("assetsUrl", assetsUrl);
        
        addMessageParams(templateParams, userLocale);

        String text = null;        
        String html = null;

        text = templateManager.processTemplate("delegate_recipient_notification.ftl", templateParams);
        html = templateManager.processTemplate("delegate_recipient_notification_html.ftl", templateParams);

        NotificationAdministrative notification = new NotificationAdministrative();
        notification.setNotificationType(NotificationType.ADMINISTRATIVE);
        notification.setSubject(subject);
        notification.setBodyHtml(html);
        notification.setBodyText(text);
        createNotification(userReceivingPermission, notification);        
    }

    @Override
    public void sendNotificationToUserGrantingPermission(String userGrantingPermission, String userReceivingPermission) {
        ProfileEntity userGrantingProfileEntity = profileEntityCacheManager.retrieve(userGrantingPermission);
        String emailName = recordNameManagerV3.deriveEmailFriendlyName(userGrantingPermission);

        Locale userLocale = getUserLocaleFromProfileEntity(userGrantingProfileEntity);

        String subject = getSubject("email.subject.delegate.recipient", userLocale);
        String emailNameForDelegate = recordNameManagerV3.deriveEmailFriendlyName(userReceivingPermission);

        StringBuffer sb = new StringBuffer();
        sb.append(emailNameForDelegate);
        sb.append(" ");
        sb.append(getSubject("notification.delegate.trustedIndividual", userLocale));
        subject = sb.toString();

        org.orcid.jaxb.model.v3.release.record.Email primaryEmail = emailManager.findPrimaryEmail(userGrantingPermission);
        String grantingOrcidEmail = primaryEmail.getEmail();
        String assetsUrl = getAssetsUrl();
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", emailName);
        templateParams.put("orcidValue", userGrantingPermission);
        templateParams.put("emailNameForDelegate", emailNameForDelegate);
        templateParams.put("orcidValueForDelegate", userReceivingPermission);
        templateParams.put("grantingOrcidValue", userGrantingPermission);
        templateParams.put("grantingOrcidName", recordNameManagerV3.deriveEmailFriendlyName(userGrantingPermission));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("grantingOrcidEmail", grantingOrcidEmail);
        templateParams.put("subject", subject);
        templateParams.put("assetsUrl", assetsUrl);

        addMessageParams(templateParams, userLocale);

        // Generate body from template
        String text = templateManager.processTemplate("delegate_notification.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("delegate_notification_html.ftl", templateParams);

        NotificationAdministrative notification = new NotificationAdministrative();
        notification.setNotificationType(NotificationType.ADMINISTRATIVE);
        notification.setSubject(subject);
        notification.setBodyHtml(html);
        notification.setBodyText(text);
        createNotification(userGrantingPermission, notification);
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
    public void sendDelegationRequestEmail(String managedOrcid, String trustedOrcid, String link) {
        // Create map of template params        
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("link", link);

        ProfileEntity managedEntity = profileEntityCacheManager.retrieve(managedOrcid);
        
        String emailNameForDelegate = recordNameManagerV3.deriveEmailFriendlyName(managedOrcid);
        String trustedOrcidName = recordNameManagerV3.deriveEmailFriendlyName(trustedOrcid);
        templateParams.put("emailNameForDelegate", emailNameForDelegate);
        templateParams.put("trustedOrcidName", trustedOrcidName);
        templateParams.put("trustedOrcidValue", trustedOrcid);
        templateParams.put("trustedOrcidValueWithParenthesis", trustedOrcid + ")");
        templateParams.put("managedOrcidValue", managedOrcid);
        templateParams.put("managedOrcidValueWithFullPoint", managedOrcid + ".");

        String primaryEmail = emailManager.findPrimaryEmail(managedOrcid).getEmail();
        if (primaryEmail == null) {
            LOGGER.info("Cant send admin delegate email if primary email is null: {}", managedOrcid);
            return;
        }
        
        String locale = managedEntity.getLocale();
        Locale userLocale = LocaleUtils.toLocale("en");
        
        if(locale != null) {
            AvailableLocales loc = AvailableLocales.valueOf(managedEntity.getLocale());
            userLocale = LocaleUtils.toLocale(loc.value());
        }        

        addMessageParams(templateParams, userLocale);
        
        String htmlBody = null;

        htmlBody = templateManager.processTemplate("admin_delegate_request_notification_html.ftl", templateParams);

        // Send message
        if (apiRecordCreationEmailEnabled) {
            String subject = messages.getMessage("email.subject.admin_as_delegate", new Object[]{trustedOrcidName}, userLocale);
            NotificationAdministrative notification = new NotificationAdministrative();
            notification.setNotificationType(NotificationType.ADMINISTRATIVE);
            notification.setSubject(subject);
            notification.setBodyHtml(htmlBody);
            createNotification(managedOrcid, notification);
            profileEventDao.persist(new ProfileEventEntity(managedOrcid, ProfileEventType.ADMIN_PROFILE_DELEGATION_REQUEST));
        } else {
            LOGGER.debug("Not sending admin delegate email, because API record creation email option is disabled. Message would have been: {}", htmlBody);
        }
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

        SourceEntity sourceEntity = sourceManager.retrieveActiveSourceEntity();

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
            //Filter only INSTITUTIONAL_CONNECTION notifications
            if(NotificationType.INSTITUTIONAL_CONNECTION.equals(n.getNotificationType())) {
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
        String sourceId = sourceManager.retrieveActiveSourceId();
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
        
        if (NotificationType.FIND_MY_STUFF.value().equals(notificationEntity.getNotificationType())){
            findMyStuffManager.markAsActioned(orcid, notificationEntity.getClientSourceId());
        }

        return notificationAdapter.toNotification(notificationEntity);
    }

    /** Create a basic notification.  No details, basically says this service has found some stuff.  Includes actionable url.
     * 
     * @param userOrcid
     * @param clientId
     * @param authorizationUrl
     */
    @Override
    public NotificationFindMyStuffEntity createFindMyStuffNotification(String userOrcid, String clientId, String authorizationUrl){
        NotificationFindMyStuff notification = new NotificationFindMyStuff();
        notification.setNotificationType(NotificationType.FIND_MY_STUFF);
        notification.setAuthorizationUrl(new AuthorizationUrl(authorizationUrl));
        NotificationFindMyStuffEntity notificationEntity = (NotificationFindMyStuffEntity) notificationAdapter
                .toNotificationEntity(notification);
        notificationEntity.setOrcid(userOrcid);
        //notificationEntity.setProfile(new ProfileEntity(userOrcid));
        notificationEntity.setClientSourceId(clientId);
        notificationDao.persist(notificationEntity);   
        return notificationEntity;
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

    @Override
    public void sendAutoDeprecateNotification(String primaryOrcid, String deprecatedOrcid) {
        ProfileEntity primaryProfileEntity = profileEntityCacheManager.retrieve(primaryOrcid);
        ProfileEntity deprecatedProfileEntity = profileEntityCacheManager.retrieve(deprecatedOrcid);
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(SourceEntityUtils.getSourceId(deprecatedProfileEntity.getSource()));
        Locale userLocale = LocaleUtils
                .toLocale(primaryProfileEntity.getLocale() == null ? org.orcid.jaxb.model.common_v2.Locale.EN.value() : org.orcid.jaxb.model.common_v2.Locale.valueOf(primaryProfileEntity.getLocale()).value());

        // Create map of template params
        Map<String, Object> templateParams = new HashMap<String, Object>();
        String subject = getSubject("email.subject.auto_deprecate", userLocale);
        String assetsUrl = getAssetsUrl();
        Date deprecatedAccountCreationDate = deprecatedProfileEntity.getDateCreated();

        // Create map of template params
        templateParams.put("primaryId", primaryOrcid);
        templateParams.put("name", recordNameManagerV3.deriveEmailFriendlyName(primaryOrcid));
        templateParams.put("assetsUrl", assetsUrl);
        templateParams.put("subject", subject);
        templateParams.put("clientName", clientDetails.getClientName());
        templateParams.put("deprecatedAccountCreationDate", deprecatedAccountCreationDate);
        templateParams.put("deprecatedId", deprecatedOrcid);

        addMessageParams(templateParams, userLocale);

        // Generate html from template
        String html = templateManager.processTemplate("auto_deprecated_account_html.ftl", templateParams);

        NotificationAdministrative notification = new NotificationAdministrative();
        notification.setNotificationType(NotificationType.ADMINISTRATIVE);
        notification.setSubject(subject);
        notification.setBodyHtml(html);
        createNotification(primaryOrcid, notification);
    }

    public int getUnreadCount(String orcid) {
        return notificationDaoReadOnly.getUnreadCount(orcid);
    }

    public int getTotalCount(String orcid, boolean archived) {
        return notificationDaoReadOnly.getTotalCount(orcid, archived);
    }

    @Override
    public void flagAsRead(String orcid, Long id) {
        notificationDao.flagAsRead(orcid, id);
    }

    @Override
    public ActionableNotificationEntity findActionableNotificationEntity(Long id) {
        return (ActionableNotificationEntity) notificationDao.find(id);
    }

    private Locale getUserLocaleFromProfileEntity(ProfileEntity profile) {
        AvailableLocales locale = AvailableLocales.valueOf(profile.getLocale());
        if (locale != null) {
            return LocaleUtils.toLocale(locale.value());
        }

        return LocaleUtils.toLocale("en");
    }

    @Override
    public List<Notification> findNotificationsToSend(String orcid, Float emailFrequencyDays, Date recordActiveDate) {
        List<NotificationEntity> notifications = new ArrayList<NotificationEntity>();
        notifications = notificationDao.findNotificationsToSend(new Date(), orcid, recordActiveDate);          
        return notificationAdapter.toNotification(notifications);
    }
    
    private String getAssetsUrl() {
        String baseUrl = orcidUrlManager.getBaseUrl();
        if(!baseUrl.endsWith("/")) {
            baseUrl += '/';
        }
        
        return baseUrl + "static/" + ReleaseNameUtils.getReleaseName();
    }

    @Override
    public Integer archiveOffsetNotifications() {
        return notificationDao.archiveOffsetNotifications(notificationArchiveOffset == null ? 100 : notificationArchiveOffset);
    }

    @Override    
    public Integer deleteOffsetNotifications() {
        List<Object[]> toDelete = new ArrayList<Object[]>();
        Integer deleted = 0;
        do {
            toDelete = notificationDao.findNotificationsToDeleteByOffset((notificationDeleteOffset == null ? 10000 : notificationDeleteOffset), recordsPerBatch);
            LOGGER.info("Got batch of {} notifications to delete", toDelete.size());
            for(Object[] o : toDelete) {
                BigInteger big = (BigInteger) o[0];
                Long id = big.longValue();
                String orcid = (String) o[1];            
                LOGGER.info("About to delete old notification: id={}, orcid={}",
                            new Object[] { id, orcid });
                    notificationDao.deleteNotificationItemByNotificationId(id);
                    notificationDao.deleteNotificationWorkByNotificationId(id);
                    notificationDao.deleteNotificationById(id);            
            }         
            deleted += toDelete.size();
        } while(!toDelete.isEmpty());
        
        return deleted;
    }

    @Override
    public void deleteNotificationsForRecord(String orcid) {
        boolean notDoneYet = notificationDao.deleteNotificationsForRecord(orcid, DELETE_BATCH_SIZE);
        while (notDoneYet) {
            notDoneYet = notificationDao.deleteNotificationsForRecord(orcid, DELETE_BATCH_SIZE);
        }
    }

}
