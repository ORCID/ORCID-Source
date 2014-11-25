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
package org.orcid.jaxb.model.notification.addactivities;

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
        NotificationAddActivities notification = getNotification();
        assertNotNull(notification);
        assertEquals(NotificationType.ADD_ACTIVITIES, notification.getNotificationType());
        assertEquals(2, notification.getActivities().getActivities().size());
        assertEquals("2014-01-01T14:45:32", notification.getSentDate().toXMLFormat());
    }

    private NotificationAddActivities getNotification() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.notification.addactivities");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = MarshallingTest.class.getResourceAsStream("/notification-add-activities.xml");
        return (NotificationAddActivities) unmarshaller.unmarshal(inputStream);
    }

}
