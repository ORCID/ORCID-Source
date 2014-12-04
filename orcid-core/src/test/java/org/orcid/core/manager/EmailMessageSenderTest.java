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
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common.ClientId;
import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.jaxb.model.notification.addactivities.Activities;
import org.orcid.jaxb.model.notification.addactivities.Activity;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.jaxb.model.notification.custom.NotificationCustom;
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

        NotificationAddActivities notification1 = new NotificationAddActivities();
        Activities activities1 = new Activities();
        notification1.setActivities(activities1);
        activities1.getActivities().add(createActivity("WORK", "Work 1"));
        activities1.getActivities().add(createActivity("WORK", "Work 2"));
        notification1.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-10T13:39:31"));
        Source source1 = new Source();
        source1.setSourceName("Super Institution 1");
        source1.setClientId(new ClientId("APP-5555-5555-5555-5555"));
        notification1.setSource(source1);
        notifications.add(notification1);

        NotificationAddActivities notification2 = new NotificationAddActivities();
        Activities activities2 = new Activities();
        notification2.setActivities(activities2);
        activities2.getActivities().add(createActivity("EMPLOYMENT", "Employment 1"));
        notification2.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-08-17T10:22:15"));
        Source source2 = new Source();
        source2.setSourceName("Super Institution 1");
        source2.setClientId(new ClientId("APP-5555-5555-5555-5555"));
        notification2.setSource(source2);
        notifications.add(notification2);

        NotificationAddActivities notification3 = new NotificationAddActivities();
        Activities activities3 = new Activities();
        notification3.setActivities(activities3);
        activities3.getActivities().add(createActivity("WORK", "Work 3"));
        activities3.getActivities().add(createActivity("WORK", "Work 4"));
        notification3.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-10T08:53:56"));
        Source source3 = new Source();
        source3.setSourceName("Lovely Publisher 1");
        notification3.setSource(source3);
        source3.setClientId(new ClientId("APP-ABCD-ABCD-ABCD-ABCD"));
        notifications.add(notification3);

        NotificationCustom notification4 = new NotificationCustom();
        notification4.setSubject("Message from ORCID");
        notification4.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-10T08:53:56"));
        notifications.add(notification4);

        NotificationCustom notification5 = new NotificationCustom();
        notification5.setSubject("Message from ORCID");
        notification5.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-11T06:42:18"));
        notifications.add(notification5);

        EmailMessage emailMessage = emailMessageSender.createDigest(notifications, Locale.ENGLISH);

        assertNotNull(emailMessage);
        String expectedBodyText = IOUtils.toString(getClass().getResourceAsStream("example_digest_email_body.txt"));
        assertEquals(expectedBodyText, emailMessage.getBodyText());
    }

    private Activity createActivity(String actType, String actName) {
        Activity act = new Activity();
        act.setActivityType(actType);
        act.setActivityName(actName);
        return act;
    }

}
