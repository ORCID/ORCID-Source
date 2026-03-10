package org.orcid.scheduler.email.cli.manager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.orcid.core.constants.EmailConstants;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EmailMessage;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.VerifyEmailUtils;
import org.orcid.jaxb.model.common.ActionType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationAdministrative;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.model.v3.release.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EmailEventEntity;
import org.orcid.persistence.jpa.entities.EmailEventType;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationServiceAnnouncementEntity;
import org.orcid.persistence.jpa.entities.NotificationTipEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.pojo.DigestEmail;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.alerting.SlackManager;
import org.orcid.utils.email.MailGunManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailMessageSenderImpl implements EmailMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMessageSenderImpl.class);

    private final Integer MAX_RETRY_COUNT;

    ExecutorService pool;

    private int verifyReminderAfterTwoDays = 2;

    private int verifyReminderAfterSevenDays = 7;

    private int verifyReminderAfterTwentyEightDays = 28;

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private NotificationDao notificationDaoReadOnly;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource
    private MailGunManager mailGunManager;

    @Resource
    private EmailDao emailDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private TemplateManager templateManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "messageSource")
    private MessageSource messages;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource
    private GenericDao<EmailEventEntity, Long> emailEventDao;

    @Resource
    private ProfileDao profileDaoReadOnly;

    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManagerV3;

    @Resource
    private VerifyEmailUtils verifyEmailUtils;

    @Resource
    private ProfileEventDao profileEventDao;

    @Value("${org.notifications.service_announcements.batchSize:60000}")
    private Integer batchSize;

    @Value("${org.notifications.max_elements_to_show:20}")
    private Integer maxNotificationsToShowPerClient;

    @Value("${org.orcid.core.email.verify.tooOld:45}")
    private int emailTooOld;

    private int emailTooOldLegacy = 15;

    @Value("${org.orcid.core.email.addWorks.firstAttempt:7}")
    private int addWorksFirstAttemptEmail;
    
    @Value("${org.orcid.core.email.addWorks.secondAttempt:28}")
    private int addWorksSecondAttemptEmail;
    
    @Value("${org.orcid.core.email.addWorks.thirdAttempt:90}")
    private int addWorksThirdAttemptEmail;
    
    @Resource
    private SlackManager slackManager;

    public EmailMessageSenderImpl(@Value("${org.notifications.service_announcements.maxThreads:8}") Integer maxThreads,
            @Value("${org.notifications.service_announcements.maxRetry:3}") Integer maxRetry) {
        if (maxThreads == null || maxThreads > 64 || maxThreads < 1) {
            pool = Executors.newFixedThreadPool(8);
        } else {
            pool = Executors.newFixedThreadPool(maxThreads);
        }

        MAX_RETRY_COUNT = maxRetry;
    }

    @Override
    public EmailMessage createDigest(String orcid, Collection<Notification> notifications) {
        ProfileEntity record = profileEntityCacheManager.retrieve(orcid);
        Locale locale = getUserLocaleFromProfileEntity(record);
        int orcidMessageCount = 0;
        String subjectDelegate = null;
        String bodyHtmlDelegate = null;
        String bodyHtmlDelegateRecipient = null;
        String bodyHtmlAdminDelegate = null;
        DigestEmail digestEmail = new DigestEmail();

        Map<String, ClientUpdates> updatesByClient = new HashMap<String, ClientUpdates>();

        for (Notification notification : notifications) {
            if (notification instanceof NotificationAdministrative) {
                NotificationAdministrative notificationAdministrative = (NotificationAdministrative) notification;
                subjectDelegate = notificationAdministrative.getSubject();
                if (subjectDelegate.endsWith("has made you an Account Delegate for their ORCID record")) {
                    bodyHtmlDelegateRecipient = getHtmlBody(notificationAdministrative);
                } else if (subjectDelegate.endsWith("has been added as a Trusted Individual") || subjectDelegate.endsWith("has revoked their Account Delegate access to your record")) {
                    bodyHtmlDelegate = getHtmlBody(notificationAdministrative);
                } else if (subjectDelegate != null && subjectDelegate.startsWith("[ORCID] Trusting")) {
                    bodyHtmlAdminDelegate = getHtmlBody(notificationAdministrative);
                } 
            }
            digestEmail.addNotification(notification);
            if (notification.getSource() == null) {
                orcidMessageCount++;
            }
            if (notification instanceof NotificationPermission) {
                NotificationPermission permissionNotification = (NotificationPermission) notification;
                permissionNotification.setEncryptedPutCode(encryptAndEncodePutCode(permissionNotification.getPutCode()));
            } else if (notification instanceof NotificationInstitutionalConnection) {
                notification.setEncryptedPutCode(encryptAndEncodePutCode(notification.getPutCode()));
            } else if (notification instanceof NotificationAmended) {
                NotificationAmended amend = (NotificationAmended) notification;
                String clientId = amend.getSource().retrieveSourcePath();
                String clientName = amend.getSource().getSourceName() == null ? null : amend.getSource().getSourceName().getContent();
                String clientDescription = amend.getSourceDescription();
                XMLGregorianCalendar createdDate = amend.getCreatedDate();
                ClientUpdates cu = null;
                if (!updatesByClient.containsKey(clientId)) {
                    cu = new ClientUpdates();
                    cu.setUserLocale(locale);
                    cu.setClientId(clientId);
                    cu.setClientName(clientName);
                    cu.setClientDescription(clientDescription);
                    updatesByClient.put(clientId, cu);
                } else {
                    cu = updatesByClient.get(clientId);
                }
                if (amend.getItems() != null && amend.getItems().getItems() != null) {
                    for (Item item : amend.getItems().getItems()) {
                        cu.addElement(createdDate, item);
                    }
                }
            }
        }

        List<String> sortedClientIds = updatesByClient.keySet().stream().sorted().collect(Collectors.toList());
        List<ClientUpdates> sortedClientUpdates = new ArrayList<ClientUpdates>();
        sortedClientIds.stream().forEach(s -> {
            sortedClientUpdates.add(updatesByClient.get(s));
        });

        String emailName = recordNameManagerV3.deriveEmailFriendlyName(record.getId());
        String subject = messages.getMessage("email.subject.digest", new String[] { emailName }, locale);
        Map<String, Object> params = new HashMap<>();
        params.put("locale", locale);
        params.put("messages", messages);
        params.put("messageArgs", new Object[0]);
        params.put("emailName", emailName);
        params.put("digestEmail", digestEmail);
        params.put("orcidMessageCount", orcidMessageCount);
        params.put("baseUri", orcidUrlManager.getBaseUrl());
        params.put("subject", subject);
        params.put("clientUpdates", sortedClientUpdates);
        params.put("verboseNotifications", true);
        params.put("maxPerClient", maxNotificationsToShowPerClient);
        params.put("orcidValue", record.getId());
        params.put("subjectDelegate", subjectDelegate);
        params.put("bodyHtmlDelegate", bodyHtmlDelegate);
        params.put("bodyHtmlDelegateRecipient", bodyHtmlDelegateRecipient);
        params.put("bodyHtmlAdminDelegate", bodyHtmlAdminDelegate);

        String bodyText = templateManager.processTemplate("digest_notification.ftl", params, locale);
        String bodyHtml = templateManager.processTemplate("digest_notification_html.ftl", params, locale);

        EmailMessage emailMessage = new EmailMessage();

        emailMessage.setSubject(subject);
        emailMessage.setBodyText(bodyText);
        emailMessage.setBodyHtml(bodyHtml);
        return emailMessage;
    }

    private Locale getUserLocaleFromProfileEntity(ProfileEntity profile) {
        String locale = profile.getLocale();
        if (locale != null) {
            AvailableLocales loc = AvailableLocales.valueOf(locale);
            return LocaleUtils.toLocale(loc.value());
        }

        return LocaleUtils.toLocale("en");
    }

    private String encryptAndEncodePutCode(Long putCode) {
        String encryptedPutCode = encryptionManager.encryptForExternalUse(String.valueOf(putCode));
        try {
            return Base64.encodeBase64URLSafeString(encryptedPutCode.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Problem base 64 encoding notification put code for notification id = " + putCode, e);
        }
    }

    @Override
    public void sendEmailMessages() {
        List<Object[]> orcidsWithUnsentNotifications = new ArrayList<Object[]>();
        try {
	        LOGGER.info("Searching for records with unsent notifications");
	        orcidsWithUnsentNotifications = notificationDaoReadOnly.findRecordsWithUnsentNotifications();
	        LOGGER.info("Records with unsent notifications: " + orcidsWithUnsentNotifications.size());
        } catch (Exception e) {
			LOGGER.error("Exception fetching records with unsent notifications", e);
            String message = String.format("Problem while searching for records with unsent notifications");
            slackManager.sendSystemAlert(message);
		}

        for (final Object[] element : orcidsWithUnsentNotifications) {
            String orcid = (String) element[0];
            try {
                Float emailFrequencyDays = null;
                Date recordActiveDate = null;
                recordActiveDate = (Date) element[1];

                List<Notification> notifications = notificationManager.findNotificationsToSend(orcid, emailFrequencyDays, recordActiveDate);

                EmailEntity primaryEmail = null;  
                try {
                	primaryEmail = emailDao.findPrimaryEmail(orcid);
	                if(primaryEmail != null && !notifications.isEmpty()) {
                        LOGGER.info("Found {} messages to send for orcid: {}", notifications.size(), orcid);
                        EmailMessage digestMessage = createDigest(orcid, notifications);
                        digestMessage.setFrom(EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG);
                        digestMessage.setTo(primaryEmail.getEmail());

                        boolean successfullySent = mailGunManager.sendEmail(digestMessage.getFrom(), digestMessage.getTo(), digestMessage.getSubject(),
                                digestMessage.getBodyText(), digestMessage.getBodyHtml());
                        if (successfullySent) {
                            for (Notification notification : notifications) {
                                notificationDao.flagAsSent(notification.getPutCode());
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("No primary email found for ORCID: " + orcid + " - " + e.getMessage());
                }

            } catch (RuntimeException e) {
                LOGGER.warn("Problem sending email message to user: " + orcid, e);
            }
        }
    }

    @Override
    public void sendServiceAnnouncements(Integer customBatchSize) {
        LOGGER.info("About to send Service Announcements messages");
        List<NotificationEntity> serviceAnnouncementsOrTips = new ArrayList<NotificationEntity>();
        try {
            long startTime = System.currentTimeMillis();
            serviceAnnouncementsOrTips = notificationDaoReadOnly.findUnsentServiceAnnouncements(customBatchSize);
            Set<Callable<Boolean>> callables = new HashSet<Callable<Boolean>>();
            for (NotificationEntity n : serviceAnnouncementsOrTips) {
                callables.add(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        processServiceAnnouncementOrTipNotification(n);
                        return true;
                    }

                });
            }

            // Runthem all
            pool.invokeAll(callables);
            long endTime = System.currentTimeMillis();
            String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
            LOGGER.info("TimeTaken={} (H:m:s.S)", timeTaken);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void sendTips(Integer customBatchSize, String fromAddress) {
        LOGGER.info("About to send Tips messages");

        List<NotificationEntity> serviceAnnouncementsOrTips = new ArrayList<NotificationEntity>();
        try {
            long startTime = System.currentTimeMillis();
            serviceAnnouncementsOrTips = notificationDaoReadOnly.findUnsentTips(customBatchSize);
            Set<Callable<Boolean>> callables = new HashSet<Callable<Boolean>>();
            for (NotificationEntity n : serviceAnnouncementsOrTips) {
                callables.add(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        processServiceAnnouncementOrTipNotification(n, fromAddress);
                        return true;
                    }

                });
            }

            // Runthem all
            pool.invokeAll(callables);
            long endTime = System.currentTimeMillis();
            String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
            LOGGER.info("TimeTaken={} (H:m:s.S)", timeTaken);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void processServiceAnnouncementOrTipNotification(NotificationEntity n) {
        processServiceAnnouncementOrTipNotification(n, null);
    }

    private void processServiceAnnouncementOrTipNotification(NotificationEntity n, String fromAddress) {
        String orcid = n.getOrcid();
        EmailEntity primaryEmail = emailDao.findPrimaryEmail(orcid);
        if (primaryEmail == null) {
            LOGGER.info("No primary email for orcid: " + orcid);
            flagAsFailed(orcid, n);
            return;
        }
        if (!primaryEmail.getVerified()) {
            LOGGER.info("Primary email not verified for: " + orcid);
            flagAsFailed(orcid, n);
            return;
        }
        try {
            boolean successfullySent = false;
            String fromAddressParam = EmailConstants.DO_NOT_REPLY_NOTIFY_ORCID_ORG;
            if (!PojoUtil.isEmpty(fromAddress)) {
                fromAddressParam = fromAddress;
            }
            if (n instanceof NotificationServiceAnnouncementEntity) {
                NotificationServiceAnnouncementEntity nc = (NotificationServiceAnnouncementEntity) n;
                // They might be custom notifications to have the
                // html/text ready to be sent
                successfullySent = mailGunManager.sendMarketingEmail(fromAddressParam, primaryEmail.getEmail(), nc.getSubject(), nc.getBodyText(), nc.getBodyHtml());
            } else if (n instanceof NotificationTipEntity) {
                NotificationTipEntity nc = (NotificationTipEntity) n;
                // They might be custom notifications to have the
                // html/text ready to be sent
                successfullySent = mailGunManager.sendMarketingEmail(fromAddressParam, primaryEmail.getEmail(), nc.getSubject(), nc.getBodyText(), nc.getBodyHtml());
            }

            if (successfullySent) {
                flagAsSent(n.getId());
            } else {
                flagAsFailed(orcid, n);
            }
        } catch (Exception e) {
            LOGGER.warn("Problem sending service announcement message to user: " + orcid, e);
        }
    }

    private void flagAsSent(Long id) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                notificationDao.flagAsSent(Arrays.asList(id));
            }
        });
    }

    private void flagAsFailed(String orcid, NotificationEntity n) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                if (n.getRetryCount() != null && n.getRetryCount() >= MAX_RETRY_COUNT) {
                    notificationDao.flagAsNonSendable(orcid, n.getId());
                } else {
                    if (n.getRetryCount() == null) {
                        notificationDao.updateRetryCount(orcid, n.getId(), 1L);
                    } else {
                        notificationDao.updateRetryCount(orcid, n.getId(), (n.getRetryCount() + 1));
                    }
                }
            }
        });
    }

    public class ClientUpdates {
        String clientId;
        String clientName;
        String clientDescription;
        Integer counter = 0;
        Locale userLocale;

        Map<String, Map<String, Set<String>>> updates = new HashMap<>();

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public void setClientName(String clientName) {
            this.clientName = clientName;
        }

        public void setClientDescription(String clientDescription) {
            this.clientDescription = clientDescription;
        }

        public void setUserLocale(Locale locale) {
            this.userLocale = locale;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientName() {
            return clientName;
        }

        public String getClientDescription() {
            return clientDescription;
        }

        public Locale getUserLocale() {
            return userLocale;
        }

        public Map<String, Map<String, Set<String>>> getUpdates() {
            return updates;
        }

        public Integer getCounter() {
            return counter;
        }

        private String renderCreationDate(XMLGregorianCalendar createdDate) {
            StringBuilder result = new StringBuilder();
            result.append(createdDate.getYear());
            result.append("-").append(createdDate.getMonth() < 10 ? "0" + createdDate.getMonth() : createdDate.getMonth());
            result.append("-").append(createdDate.getDay() < 10 ? "0" + createdDate.getDay() : createdDate.getDay());
            return result.toString();
        }

        public void addElement(XMLGregorianCalendar createdDate, Item item) {
            init(item.getItemType().name(), item.getActionType() == null ? null : item.getActionType().name());
            StringBuilder value = new StringBuilder();
            switch (item.getItemType()) {
            case DISTINCTION:
            case EDUCATION:
            case EMPLOYMENT:
            case INVITED_POSITION:
            case MEMBERSHIP:
            case QUALIFICATION:
            case SERVICE:
                value.append("<i>").append(item.getAdditionalInfo().get("org_name")).append("</i> ").append((item.getItemName() != null ? item.getItemName() : "")).append(" (")
                        .append(renderCreationDate(createdDate)).append(')');
                break;
            default:
                value.append(item.getItemName() != null ? item.getItemName() : "");
                if (item.getExternalIdentifier() != null) {
                    value.append(" ").append(item.getExternalIdentifier().getType()).append(": ").append(item.getExternalIdentifier().getValue());
                }
                value.append(" (").append(renderCreationDate(createdDate)).append(')');
                break;
            }

            // Set the external identifiers list
            String externalIdentifiersList = generateExternalIdentifiersList(item);
            if(StringUtils.isNotBlank(externalIdentifiersList)) {
                value.append(externalIdentifiersList);
            }

            String stringValue = value.toString();

            Set<String> elements;
            if (item.getActionType() != null) {
                elements = updates.get(item.getItemType().name()).get(item.getActionType().name());
            } else {
                elements = updates.get(item.getItemType().name()).get(ActionType.UNKNOWN.name());
            }
            if (!elements.contains(stringValue)) {
                if (counter < maxNotificationsToShowPerClient) {
                    elements.add(stringValue);
                }
                counter += 1;
            }

        }

        private void init(String itemType, String actionType) {
            if (!updates.containsKey(itemType)) {
                updates.put(itemType, new HashMap<String, Set<String>>());
            }
            if (actionType == null && !updates.get(itemType).containsKey(ActionType.UNKNOWN.name())) {
                updates.get(itemType).put(ActionType.UNKNOWN.name(), new TreeSet<String>());
            } else if (!updates.get(itemType).containsKey(actionType)) {
                updates.get(itemType).put(actionType, new TreeSet<String>());
            }
        }

        private String generateExternalIdentifiersList(Item item) {
            StringBuilder extIdsHtmlList = new StringBuilder();
            if (item.getAdditionalInfo() != null) {
                if(item.getAdditionalInfo().containsKey("external_identifiers")) {
                    Map extIds = (Map) item.getAdditionalInfo().get("external_identifiers");
                    if(extIds != null && extIds.containsKey("externalIdentifier")) {
                        List<Map> extIdsList = (List<Map>) extIds.get("externalIdentifier");
                        if(extIdsList != null) {
                            extIdsHtmlList.append("<ul>");
                            for(Map extIdMap : extIdsList) {
                                String extIdType = extIdMap.containsKey("type") ? (String) extIdMap.get("type") : null;
                                // External id type must not be null, so, in case it is lets log a warning
                                if(extIdType == null) {
                                    LOGGER.warn("External ID type is null for '" + item.getPutCode() + "', '" + item.getItemName() + "'");
                                }
                                extIdsHtmlList.append("<li style=\"padding-left: 0;margin-top: 2px;\">").append(extIdType).append(": ");
                                // Check if there is an URL
                                if(extractValue(extIdMap, "url") != null) {
                                    String url = extractValue(extIdMap, "url");
                                    extIdsHtmlList.append("<a style=\"text-decoration: underline;color: #085c77;\" target=\"_blank\" href=\"").append(url).append("\">").append(url).append("</a>");
                                } else if (extractValue(extIdMap, "normalized") != null) {
                                    //If there is no URL, check for the normalized value
                                    String value = extractValue(extIdMap, "normalized");
                                    extIdsHtmlList.append(value);
                                } else if(extIdMap.containsKey("value")) {
                                    try {
                                        String value = (String) extIdMap.get("value");
                                        extIdsHtmlList.append(value);
                                    } catch (NullPointerException e) {
                                        LOGGER.warn("External ID value is null for '" + item.getPutCode() + "', '" + item.getItemName() + "'");
                                    }
                                } else {
                                    extIdsHtmlList.append("Unavailable - please contact support");
                                    LOGGER.warn("Unable to find a printable value for External ID '" + item.getPutCode() + "', '" + item.getItemName() + "'");
                                }
                                extIdsHtmlList.append("</li>");
                            }
                            extIdsHtmlList.append("</ul>");
                        }
                    }
                }
            }
            return extIdsHtmlList.toString();
        }
    }

    private String extractValue(Map extIdMap, String keyName) {
        if(extIdMap.containsKey(keyName)) {
            Map keyMap = (Map) extIdMap.get(keyName);
            try {
                String value = (String) keyMap.get("value");
                if (StringUtils.isNotBlank(value)) {
                    return value;
                }
            } catch(NullPointerException npe) {
                // Value might be null, so, just ignore it
            }
        }
        return null;
    }

    private String getHtmlBody(NotificationAdministrative notificationAdministrative) {
        int bodyTag = notificationAdministrative.getBodyHtml().indexOf("<body>");
        int bodyTagClose = notificationAdministrative.getBodyHtml().indexOf("</body>");
        return notificationAdministrative.getBodyHtml().substring(bodyTag + 6, bodyTagClose);
    }

    @Override
    synchronized public void processUnverifiedEmails2Days() {
        processUnverifiedEmails(true, verifyReminderAfterTwoDays, EmailEventType.VERIFY_EMAIL_2_DAYS_SENT, EmailEventType.VERIFY_EMAIL_2_DAYS_SENT_SKIPPED);
    }

    synchronized public void processUnverifiedEmails7Days() {
        processUnverifiedEmails(false, verifyReminderAfterSevenDays, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT, EmailEventType.VERIFY_EMAIL_7_DAYS_SENT_SKIPPED);
    }

    synchronized public void processUnverifiedEmails28Days() {
        processUnverifiedEmails(false, verifyReminderAfterTwentyEightDays, EmailEventType.VERIFY_EMAIL_28_DAYS_SENT, EmailEventType.VERIFY_EMAIL_28_DAYS_SENT_SKIPPED);
    }

    private void processUnverifiedEmails(boolean forceSending, int unverifiedDays, EmailEventType sent, EmailEventType failed) {
        if (forceSending || Features.SEND_ALL_VERIFICATION_EMAILS.isActive()) {
            LOGGER.info("About to process unverIfied emails for {}  days reminder", unverifiedDays);
            List<Triple<String, String, Boolean>> elements = Collections.<Triple<String, String, Boolean>> emptyList();
            elements = profileDaoReadOnly.findEmailsUnverfiedDays(unverifiedDays, sent);
            LOGGER.info("Got {} profiles with email event and unverified emails for {} days reminder", elements.size(), unverifiedDays);

            for (Triple<String, String, Boolean> element : elements) {
                processUnverifiedEmailsInTransaction(element.getLeft(), element.getMiddle(), element.getRight(), sent,
                            failed);
            }
        }
    }

    synchronized public void addWorksToRecordFirstReminder() {
        sendAddWorksToRecordEmailAttempt(addWorksFirstAttemptEmail, ProfileEventType.ADD_WORKS_FIRST_REMINDER_SENT);
    }
    
    synchronized public void addWorksToRecordSecondReminder() {
        sendAddWorksToRecordEmailAttempt(addWorksSecondAttemptEmail, ProfileEventType.ADD_WORKS_SECOND_REMINDER_SENT);
    }
    
    synchronized public void addWorksToRecordThirdReminder() {
        sendAddWorksToRecordEmailAttempt(addWorksThirdAttemptEmail, ProfileEventType.ADD_WORKS_THIRD_REMINDER_SENT);
    }
    
    private void sendAddWorksToRecordEmailAttempt(int addWorksAttemptEmail, ProfileEventType profileEventType){
        if (Features.SEND_ADD_WORKS_EMAILS.isActive()) {
            LOGGER.info("Sending 'Add works' email reminder for {} days", addWorksAttemptEmail);
            List<Pair<String, String>> elements = profileDaoReadOnly.findEmailsToSendAddWorksEmail(addWorksAttemptEmail);
            LOGGER.debug("Found {} add works reminders to send" , elements.size());
            for (Pair<String, String> element: elements) {
                String email = element.getLeft();
                String userOrcid = element.getRight();
                String numberAttempt = null;
                ProfileEventType skipped = null;
                
                switch (profileEventType) {
                    case ADD_WORKS_FIRST_REMINDER_SENT:
                        numberAttempt = "first";
                        skipped = ProfileEventType.ADD_WORKS_FIRST_REMINDER_SENT_SKIPPED;
                        break;
                    case ADD_WORKS_SECOND_REMINDER_SENT:
                        numberAttempt = "second";
                        skipped = ProfileEventType.ADD_WORKS_SECOND_REMINDER_SENT_SKIPPED;
                        break;
                    case ADD_WORKS_THIRD_REMINDER_SENT:
                        numberAttempt = "third";
                        skipped = ProfileEventType.ADD_WORKS_THIRD_REMINDER_SENT_SKIPPED;
                        break;
                }
                if (!profileEventDao.isAttemptSend(userOrcid, profileEventType)) {
                    try {
                        LOGGER.debug("Sending "+ numberAttempt +" attempt email to encourage user to add works to email address {}, orcid {}", email, userOrcid);
                        sendAddWorksToRecordEmail(email, userOrcid);
                        profileEventDao.persist(new ProfileEventEntity(userOrcid, profileEventType, email));
                        profileEventDao.flush();
                    } catch (Exception e) {
                        LOGGER.error("Unable to send "+ numberAttempt +" attempt email to encourage user to add works to email: " + email, e);
                        profileEventDao.persist(new ProfileEventEntity(userOrcid, skipped, email));
                        profileEventDao.flush();
                    }
                }
            }
        }
    }

    private void processUnverifiedEmailsInTransaction(final String userOrcid, final String email, final Boolean isPrimaryEmail, EmailEventType eventSent, EmailEventType eventSkipped) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            @Transactional
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    LOGGER.debug("Sending reminder {} to email address {}, orcid {}", eventSent, email, userOrcid);
                    sendVerificationReminderEmail(userOrcid, email, isPrimaryEmail);
                    emailEventDao.persist(new EmailEventEntity(email, eventSent));
                    emailEventDao.flush();
                } catch (Exception e) {
                    LOGGER.error("Unable to send unverified email reminder to email: " + email, e);
                    emailEventDao.persist(new EmailEventEntity(email, eventSkipped));
                    emailEventDao.flush();
                }
            }
        });
    }

    private void sendVerificationReminderEmail(String userOrcid, String email, Boolean isPrimaryEmail) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(userOrcid);
        Locale locale = getUserLocaleFromProfileEntity(profile);

        String emailFriendlyName = recordNameManagerV3.deriveEmailFriendlyName(userOrcid);
        Map<String, Object> templateParams = verifyEmailUtils.createParamsForVerificationEmail(emailFriendlyName, userOrcid, email, isPrimaryEmail, locale);
        String subject = (String) templateParams.get("subject");
        templateParams.put("isReminder", true);
        // Generate body from template
        String body = templateManager.processTemplate("verification_email_v2.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("verification_email_html_v2.ftl", templateParams);
        mailGunManager.sendEmail(EmailConstants.DO_NOT_REPLY_VERIFY_ORCID_ORG, email, subject, body, htmlBody);
    }

    @Override
    public EmailMessage createAddWorksToRecordEmail(String email, String orcid) {
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        Locale locale = getUserLocaleFromProfileEntity(profile);

        String subject = messages.getMessage("email.subject.add_works", null, locale);
        String emailName = recordNameManagerV3.deriveEmailFriendlyName(orcid);
        Map<String, Object> params = new HashMap<>();
        params.put("locale", locale);
        params.put("messages", messages);
        params.put("subject", subject);
        params.put("messageArgs", new Object[0]);
        params.put("emailName", emailName);
        params.put("orcidId", orcid);
        params.put("baseUri", orcidUrlManager.getBaseUrl());

        // Generate body from template
        String body = templateManager.processTemplate("add_works_to_record_email.ftl", params, locale);
        String htmlBody = templateManager.processTemplate("add_works_to_record_email_html.ftl", params, locale);

        EmailMessage emailMessage = new EmailMessage();

        emailMessage.setSubject(subject);
        emailMessage.setBodyText(body);
        emailMessage.setBodyHtml(htmlBody);
        return emailMessage;
    }

    private void sendAddWorksToRecordEmail(String email, String orcid) {
        EmailMessage addWorksMessage = createAddWorksToRecordEmail(email, orcid);
        mailGunManager.sendEmail(EmailConstants.DO_NOT_REPLY_VERIFY_ORCID_ORG, email, addWorksMessage.getSubject(), addWorksMessage.getBodyText(), addWorksMessage.getBodyHtml());
    }
}
