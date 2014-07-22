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

import org.orcid.core.manager.EmailMessage;
import org.orcid.core.manager.EmailMessageSender;
import org.orcid.jaxb.model.notification.Notification;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailMessageSenderImpl implements EmailMessageSender {

    private static final String HEADER = "Welcome to your ORCID digest.\n\nToday's Topics:\n";
    private static final String BIG_SEPARATOR = "\n\n\n----------------------------------------------------------------------\n";
    private static final String FOOTER = "\nEnd of your ORCID digest\n***********************************************\n";

    @Override
    public EmailMessage createDigest(Collection<Notification> notifications) {
        EmailMessage emailMessage = new EmailMessage();
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
            summaryText.append(notification.getSource().getSourceName());
            summaryText.append(")");

            bodyText.append("\nMessage: ");
            bodyText.append(messageNumber);
            bodyText.append("\nDate: ");
            bodyText.append(notification.getSentDate().toXMLFormat());
            bodyText.append("\nFrom: ");
            bodyText.append(notification.getSource().getSourceName());
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

}
