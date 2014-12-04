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
import org.orcid.jaxb.model.common.ClientId;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private OrcidUrlManager orcidUrlManager;

    @Override
    public EmailMessage createDigest(String orcid, Collection<Notification> notifications) {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid, LoadOptions.BIO_AND_INTERNAL_ONLY);
        Locale locale = localeManager.getLocalFromOrcidProfile(orcidProfile);
        return createDigest(notifications, locale);
    }

    @Override
    public EmailMessage createDigest(Collection<Notification> notifications, Locale locale) {
        int orcidMessageCount = 0;
        int memberMessageCount = 0;
        int activityCount = 0;
        Set<String> memberIds = new HashSet<>();
        for (Notification notification : notifications) {
            if (notification.getSource() == null) {
                orcidMessageCount++;
            } else {
                ClientId clientId = notification.getSource().getClientId();
                if (clientId != null) {
                    memberMessageCount++;
                    memberIds.add(clientId.getPath());
                }
            }
            if (notification instanceof NotificationAddActivities) {
                NotificationAddActivities addActsNotification = (NotificationAddActivities) notification;
                activityCount += addActsNotification.getActivities().getActivities().size();
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("orcidMessageCount", orcidMessageCount);
        params.put("memberMessageCount", memberMessageCount);
        params.put("activityCount", activityCount);
        params.put("memberIdsCount", memberIds.size());
        params.put("baseUrl", orcidUrlManager.getBaseUrl());
        String emailBody = templateManager.processTemplate("digest_email.ftl", params, locale);
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject("Your digest from ORCID");
        emailMessage.setBodyText(emailBody);
        return emailMessage;

    }

    @Override
    public void sendEmailMessages() {
        LOGGER.info("About to send email messages");
        List<String> orcidsWithMessagesToSend = notificationDao.findOrcidsWithNotificationsToSend();
        for (final String orcid : orcidsWithMessagesToSend) {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    LOGGER.info("Sending messages for orcid: {}", orcid);
                    List<Notification> notifications = notificationManager.findUnsentByOrcid(orcid);
                    LOGGER.info("Found {} messages to send for orcid: {}", notifications.size(), orcid);
                    EmailMessage digestMessage = createDigest(orcid, notifications);
                    digestMessage.setFrom(DIGEST_FROM_ADDRESS);
                    digestMessage.setTo(profileDao.find(orcid).getPrimaryEmail().getId());
                    // XXX Need to add html
                    boolean successfullySent = mailGunManager.sendEmail(digestMessage.getFrom(), digestMessage.getTo(), digestMessage.getSubject(),
                            digestMessage.getBodyText(), "<html><body><pre>" + digestMessage.getBodyText() + "</pre></body></html>");
                    if (successfullySent) {
                        flagAsSent(notifications);
                    }
                }
            });
        }
        LOGGER.info("Finished sending email messages");
    }

    private void flagAsSent(List<Notification> notifications) {
        List<Long> notificationIds = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationIds.add(Long.valueOf(notification.getPutCode().getPath()));
        }
        notificationDao.flagAsSent(notificationIds);
    }

}
