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
package org.orcid.jaxb.model.notification.permission;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission;
import org.orcid.jaxb.model.notification_rc3.NotificationType;
import org.xml.sax.SAXException;

/**
 * 
 * @author Will Simpson
 * 
 */

public class MarshallingTest {

    private static final String SAMPLE_PATH = "/notification_2.0_rc2/samples/notification-permission-2.0_rc2.xml";

    @Test
    public void testMarshalling() throws JAXBException, IOException, SAXException {
        NotificationPermission notification = getNotification();
        assertNotNull(notification);
        assertEquals(NotificationType.PERMISSION, notification.getNotificationType());
        assertEquals(2, notification.getItems().getItems().size());
        assertEquals("2014-01-01T14:45:32", notification.getSentDate().toXMLFormat());

        // Back the other way
        String expected = IOUtils.toString(getClass().getResourceAsStream(SAMPLE_PATH), "UTF-8");
        Pattern pattern = Pattern.compile("<!--.*?-->\\s*", Pattern.DOTALL);
        expected = pattern.matcher(expected).replaceAll("");
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.notification.permission_rc3");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.orcid.org/ns/notification ../notification-permission-2.0_rc2.xsd");
        StringWriter writer = new StringWriter();
        marshaller.marshal(notification, writer);
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, writer.toString());
        assertTrue(diff.identical());
    }

    private NotificationPermission getNotification() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.notification.permission_rc3");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = MarshallingTest.class.getResourceAsStream(SAMPLE_PATH);
        return (NotificationPermission) unmarshaller.unmarshal(inputStream);
    }

}
