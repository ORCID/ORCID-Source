/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.EmailMessage;
import org.orcid.core.manager.EmailMessageSender;
import org.orcid.core.manager.NotificationManager;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.jaxb.model.notification.Source;
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
    private static final String HEADER = "Welcome to your ORCID digest.\n\nToday's Topics:\n";
    private static final String BIG_SEPARATOR = "\n\n\n----------------------------------------------------------------------\n";
    private static final String FOOTER = "\nEnd of your ORCID digest\n***********************************************\n";

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

    @Override
    public EmailMessage createDigest(Collection<Notification> notifications) {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject("Your digest from ORCID");
        StringBuilder summaryText = new StringBuilder();
        StringBuilder bodyText = new StringBuilder();

        summaryText.append(HEADER);
        int messageNumber = 0;
        for (Notification notification : notifications) {
            messageNumber++;
            summaryText.append("\n   ");
            summaryText.append(messageNumber);
            summaryText.append(". ");
            summaryText.append(notification.getSubject());
            summaryText.append(" (");
            Source source = notification.getSource();
            String sourceName = source != null ? source.getSourceName() : "ORCID";
            summaryText.append(sourceName);
            summaryText.append(")");

            bodyText.append("\nMessage: ");
            bodyText.append(messageNumber);
            bodyText.append("\nDate: ");
            bodyText.append(notification.getCreatedDate().toXMLFormat());
            bodyText.append("\nFrom: ");
            bodyText.append(sourceName);
            bodyText.append("\nSubject: ");
            bodyText.append(notification.getSubject());
            bodyText.append("\n\n");
            bodyText.append(notification.getBodyText());
            bodyText.append("\n\n------------------------------\n");
        }

        bodyText.append(FOOTER);

        emailMessage.setBodyText(summaryText.toString() + BIG_SEPARATOR + bodyText.toString());
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
                    EmailMessage digestMessage = createDigest(notifications);
                    digestMessage.setFrom(DIGEST_FROM_ADDRESS);
                    digestMessage.setTo(profileDao.find(orcid).getPrimaryEmail().getId());
                    // XXX Need to add html
                    mailGunManager.sendEmail(digestMessage.getFrom(), digestMessage.getTo(), digestMessage.getSubject(), digestMessage.getBodyText(), "<html><body><pre>"
                            + digestMessage.getBodyText() + "</pre></body></html>");
                }
            });
        }
        LOGGER.info("Finished sending email messages");
    }

}
