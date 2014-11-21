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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.notification.custom.Notification;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailMessageSenderTest extends BaseTest {

    @Resource
    private EmailMessageSender emailMessageSender;

    @Test
    public void testCreateDigest() throws IOException {

        List<Notification> notifications = new ArrayList<>();

        Notification notification1 = new Notification();
        notification1.setSubject("Your ORCID record was updated");
        notification1.setBodyText("Your ORCID record was update by Super Institution 1.\n\nBest regards,\n\nORCID");
        notification1.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-10T13:39:31"));
        Source source1 = new Source();
        source1.setSourceName("Super Institution 1");
        notification1.setSource(source1);
        notifications.add(notification1);

        Notification notification2 = new Notification();
        notification2.setSubject("Your ORCID record was updated");
        notification2.setBodyText("Your ORCID record was update by Lovely Publisher 1.\n\nBest regards,\n\nORCID");
        notification2.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-10T08:53:56"));
        Source source2 = new Source();
        source2.setSourceName("Lovely Publisher 1");
        notification2.setSource(source2);
        notifications.add(notification2);

        EmailMessage emailMessage = emailMessageSender.createDigest(notifications);

        assertNotNull(emailMessage);
        String expectedBodyText = IOUtils.toString(getClass().getResourceAsStream("example_digest_email_body.txt"));
        assertEquals(expectedBodyText, emailMessage.getBodyText());
    }

}
