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
import org.xml.sax.SAXException;

/**
 * 
 * @author Will Simpson
 * 
 */

public class MarshallingTest {

    private static final String SAMPLE_PATH_RC2 = "/notification_2.0_rc2/samples/notification-permission-2.0_rc2.xml";
    private static final String SAMPLE_PATH_RC3 = "/notification_2.0_rc3/samples/notification-permission-2.0_rc3.xml";
    private static final String SAMPLE_PATH_RC4 = "/notification_2.0_rc4/samples/notification-permission-2.0_rc4.xml";
    private static final String SAMPLE_PATH_V2 = "/notification_2.0/samples/notification-permission-2.0.xml";

    @Test
    public void testMarshallingV2_0_RC2() throws JAXBException, IOException, SAXException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.notification.permission_rc2");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = MarshallingTest.class.getResourceAsStream(SAMPLE_PATH_RC2);        
        org.orcid.jaxb.model.notification.permission_rc2.NotificationPermission notification = (org.orcid.jaxb.model.notification.permission_rc2.NotificationPermission) unmarshaller.unmarshal(inputStream);
        assertNotNull(notification);
        assertEquals(org.orcid.jaxb.model.notification_rc2.NotificationType.PERMISSION, notification.getNotificationType());
        assertEquals(2, notification.getItems().getItems().size());
        assertEquals("2014-01-01T14:45:32", notification.getSentDate().toXMLFormat());

        // Back the other way
        String expected = IOUtils.toString(getClass().getResourceAsStream(SAMPLE_PATH_RC2), "UTF-8");
        Pattern pattern = Pattern.compile("<!--.*?-->\\s*", Pattern.DOTALL);
        expected = pattern.matcher(expected).replaceAll("");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.orcid.org/ns/notification ../notification-permission-2.0_rc2.xsd");
        StringWriter writer = new StringWriter();
        marshaller.marshal(notification, writer);
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, writer.toString());
        assertTrue(diff.identical());
    }
    
    @Test
    public void testMarshallingV2_0_RC3() throws JAXBException, IOException, SAXException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.notification.permission_rc3");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = MarshallingTest.class.getResourceAsStream(SAMPLE_PATH_RC3);
        org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission notification = (org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission) unmarshaller.unmarshal(inputStream);
        assertNotNull(notification);
        assertEquals(org.orcid.jaxb.model.notification_rc3.NotificationType.PERMISSION, notification.getNotificationType());
        assertEquals(2, notification.getItems().getItems().size());
        assertEquals("2014-01-01T14:45:32", notification.getSentDate().toXMLFormat());

        // Back the other way
        String expected = IOUtils.toString(getClass().getResourceAsStream(SAMPLE_PATH_RC3), "UTF-8");
        Pattern pattern = Pattern.compile("<!--.*?-->\\s*", Pattern.DOTALL);
        expected = pattern.matcher(expected).replaceAll("");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.orcid.org/ns/notification ../notification-permission-2.0_rc3.xsd");
        StringWriter writer = new StringWriter();
        marshaller.marshal(notification, writer);
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, writer.toString());
        assertTrue(diff.identical());
    }
    
    @Test
    public void testMarshallingV2_0_RC4() throws JAXBException, IOException, SAXException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.notification.permission_rc4");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = MarshallingTest.class.getResourceAsStream(SAMPLE_PATH_RC4);
        org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission notification = (org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission) unmarshaller.unmarshal(inputStream);
        assertNotNull(notification);
        assertEquals(org.orcid.jaxb.model.notification_rc4.NotificationType.PERMISSION, notification.getNotificationType());
        assertEquals(2, notification.getItems().getItems().size());
        assertEquals("2014-01-01T14:45:32", notification.getSentDate().toXMLFormat());

        // Back the other way
        String expected = IOUtils.toString(getClass().getResourceAsStream(SAMPLE_PATH_RC4), "UTF-8");
        Pattern pattern = Pattern.compile("<!--.*?-->\\s*", Pattern.DOTALL);
        expected = pattern.matcher(expected).replaceAll("");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.orcid.org/ns/notification ../notification-permission-2.0_rc4.xsd");
        StringWriter writer = new StringWriter();
        marshaller.marshal(notification, writer);
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, writer.toString());
        assertTrue(diff.identical());
    }
    
    @Test
    public void testMarshallingV2_0() throws JAXBException, IOException, SAXException {
        JAXBContext context = JAXBContext.newInstance("org.orcid.jaxb.model.notification.permission_v2");
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream inputStream = MarshallingTest.class.getResourceAsStream(SAMPLE_PATH_V2);
        org.orcid.jaxb.model.notification.permission_v2.NotificationPermission notification = (org.orcid.jaxb.model.notification.permission_v2.NotificationPermission) unmarshaller.unmarshal(inputStream);
        assertNotNull(notification);
        assertEquals(org.orcid.jaxb.model.notification_v2.NotificationType.PERMISSION, notification.getNotificationType());
        assertEquals(2, notification.getItems().getItems().size());
        assertEquals("2014-01-01T14:45:32", notification.getSentDate().toXMLFormat());

        // Back the other way
        String expected = IOUtils.toString(getClass().getResourceAsStream(SAMPLE_PATH_V2), "UTF-8");
        Pattern pattern = Pattern.compile("<!--.*?-->\\s*", Pattern.DOTALL);
        expected = pattern.matcher(expected).replaceAll("");
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.orcid.org/ns/notification ../notification-permission-2.0.xsd");
        StringWriter writer = new StringWriter();
        marshaller.marshal(notification, writer);
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, writer.toString());
        assertTrue(diff.identical());
    }
}
