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
package org.orcid.jaxb.model.notification.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.orcid.jaxb.model.notification.NotificationType;

/**
 * 
 * @author Will Simpson
 * 
 */

public class MarshallingTest {

    @Test
    public void testUnMarshalling() throws JAXBException {
        NotificationCustom notification = getNotification();
        assertNotNull(notification);
        assertEquals(NotificationType.CUSTOM, notification.getNotificationType());
        assertEquals("Important Notification from ORCID", notification.getSubject());
        assertEquals("This is an email with important info.\n    ", notification.getBodyText());
        assertEquals("\n        <p>\n            This is an email with <em>important</em> info.\n        </p>\n    ", notification.getBodyHtml());
        assertEquals("2014-01-01T14:45:32", notification.getSentDate().toXMLFormat());
        assertEquals("en-gb", notification.getLang());
    }

    private NotificationCustom getNotification() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.notification.custom");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = MarshallingTest.class.getResourceAsStream("/notification_2.0_rc1/samples/notification-custom-2.0_rc1.xml");
        return (NotificationCustom) unmarshaller.unmarshal(inputStream);
    }

}
