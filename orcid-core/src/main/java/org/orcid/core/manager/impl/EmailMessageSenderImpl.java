/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EmailMessage;
import org.orcid.core.manager.EmailMessageSender;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.jaxb.model.common.SourceClientId;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.jaxb.model.notification.amended.NotificationAmended;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailMessageSenderImpl implements EmailMessageSender {

    private static final String DIGEST_FROM_ADDRESS = "update@notify.orcid.org";
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailMessageSenderImpl.class);

    @Resource
    private NotificationDao notificationDao;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private MailGunManager mailGunManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private TemplateManager templateManager;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private MessageSource messages;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Override
    public EmailMessage createDigest(String orcid, Collection<Notification> notifications) {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid, LoadOptions.BIO_AND_INTERNAL_ONLY);
        Locale locale = localeManager.getLocaleFromOrcidProfile(orcidProfile);
        return createDigest(orcidProfile, notifications, locale);
    }

    @Override
    public EmailMessage createDigest(OrcidProfile orcidProfile, Collection<Notification> notifications, Locale locale) {
        int totalMessageCount = 0;
        int orcidMessageCount = 0;
        int addActivitiesMessageCount = 0;
        int amendedMessageCount = 0;
        int activityCount = 0;
        Set<String> memberIds = new HashSet<>();
        for (Notification notification : notifications) {
            totalMessageCount++;
            if (notification.getSource() == null) {
                orcidMessageCount++;
            } else {
                SourceClientId clientId = notification.getSource().getSourceClientId();
                if (clientId != null) {
                    memberIds.add(clientId.getPath());
                }
            }
            if (notification instanceof NotificationAddActivities) {
                addActivitiesMessageCount++;
                NotificationAddActivities addActsNotification = (NotificationAddActivities) notification;
                activityCount += addActsNotification.getActivities().getActivities().size();
            }
            if (notification instanceof NotificationAmended) {
                amendedMessageCount++;
            }
        }
        String emailName = notificationManager.deriveEmailFriendlyName(orcidProfile);
        String subject = messages.getMessage("email.subject.digest", new String[] { emailName, String.valueOf(totalMessageCount) }, locale);
        Map<String, Object> params = new HashMap<>();
        params.put("locale", locale);
        params.put("messages", messages);
        params.put("messageArgs", new Object[0]);
        params.put("emailName", emailName);
        params.put("orcidMessageCount", orcidMessageCount);
        params.put("addActivitiesMessageCount", addActivitiesMessageCount);
        params.put("activityCount", activityCount);
        params.put("amendedMessageCount", amendedMessageCount);
        params.put("memberIdsCount", memberIds.size());
        params.put("baseUri", orcidUrlManager.getBaseUrl());
        params.put("subject", subject);
        String bodyText = templateManager.processTemplate("digest_email.ftl", params, locale);
        String bodyHtml = templateManager.processTemplate("digest_email_html.ftl", params, locale);
        EmailMessage emailMessage = new EmailMessage();

        emailMessage.setSubject(subject);
        emailMessage.setBodyText(bodyText);
        emailMessage.setBodyHtml(bodyHtml);
        return emailMessage;

    }

    @Override
    public void sendEmailMessages() {
        LOGGER.info("About to send email messages");
        List<String> orcidsWithMessagesToSend = notificationDao.findOrcidsWithNotificationsToSend();
        for (final String orcid : orcidsWithMessagesToSend) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        LOGGER.info("Sending messages for orcid: {}", orcid);
                        List<Notification> notifications = notificationManager.findUnsentByOrcid(orcid);
                        LOGGER.info("Found {} messages to send for orcid: {}", notifications.size(), orcid);
                        EmailMessage digestMessage = createDigest(orcid, notifications);
                        digestMessage.setFrom(DIGEST_FROM_ADDRESS);
                        EmailEntity primaryEmail = profileDao.find(orcid).getPrimaryEmail();
                        if (primaryEmail == null) {
                            LOGGER.info("No primary email for orcid: " + orcid);
                            return;
                        }
                        digestMessage.setTo(primaryEmail.getId());
                        boolean successfullySent = mailGunManager.sendEmail(digestMessage.getFrom(), digestMessage.getTo(), digestMessage.getSubject(),
                                digestMessage.getBodyText(), digestMessage.getBodyHtml());
                        if (successfullySent) {
                            flagAsSent(notifications);
                        }
                    }
                });
            } catch (RuntimeException e) {
                LOGGER.warn("Problem sending email message to user: " + orcid, e);
            }
        }
        LOGGER.info("Finished sending email messages");
    }

    private void flagAsSent(List<Notification> notifications) {
        List<Long> notificationIds = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationIds.add(notification.getPutCode());
        }
        notificationDao.flagAsSent(notificationIds);
    }

}
