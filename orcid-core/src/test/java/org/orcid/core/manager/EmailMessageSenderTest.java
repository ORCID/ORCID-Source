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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_rc4.Source;
import org.orcid.jaxb.model.common_rc4.SourceClientId;
import org.orcid.jaxb.model.common_rc4.SourceName;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.notification.amended_rc4.AmendedSection;
import org.orcid.jaxb.model.notification.amended_rc4.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_rc4.NotificationCustom;
import org.orcid.jaxb.model.notification.permission_rc4.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_rc4.Item;
import org.orcid.jaxb.model.notification.permission_rc4.ItemType;
import org.orcid.jaxb.model.notification.permission_rc4.Items;
import org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission;
import org.orcid.jaxb.model.notification_rc4.Notification;
import org.orcid.jaxb.model.record_rc4.ExternalID;
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

        OrcidProfile orcidProfile = new OrcidProfile();
        OrcidBio orcidBio = new OrcidBio();
        orcidProfile.setOrcidBio(orcidBio);
        PersonalDetails personalDetails = new PersonalDetails();
        orcidBio.setPersonalDetails(personalDetails);
        personalDetails.setGivenNames(new GivenNames("John"));
        personalDetails.setFamilyName(new FamilyName("Watson"));
        OrcidInternal orcidInternal = new OrcidInternal();
        Preferences preferences = new Preferences();
        orcidProfile.setOrcidInternal(orcidInternal);
        orcidInternal.setPreferences(preferences);
        preferences.setSendEmailFrequencyDays("7.0");

        List<Notification> notifications = new ArrayList<>();

        NotificationPermission notification1 = new NotificationPermission();
        notification1.setPutCode(1L);
        Items activities1 = new Items();
        notification1.setItems(activities1);
        activities1.getItems().add(createActivity(ItemType.WORK, "Work 1", "123446/67654"));
        activities1.getItems().add(createActivity(ItemType.WORK, "Work 2", "http://dx.doi.org/123446/67655"));
        notification1.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-10T13:39:31"));
        notification1.setAuthorizationUrl(new AuthorizationUrl("https://thirdparty.com/add-to-orcid/12345"));
        Source source1 = new Source();
        source1.setSourceName(new SourceName("Super Institution 1"));
        source1.setSourceClientId(new SourceClientId("APP-5555-5555-5555-5555"));
        notification1.setSource(source1);
        notifications.add(notification1);

        NotificationPermission notification2 = new NotificationPermission();
        notification2.setPutCode(2L);
        Items activities2 = new Items();
        notification2.setItems(activities2);
        activities2.getItems().add(createActivity(ItemType.EMPLOYMENT, "Employment 1 ", "12345/abc"));
        notification2.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-08-17T10:22:15"));
        notification2.setAuthorizationUrl(new AuthorizationUrl("https://thirdparty.com/add-to-orcid/abc"));
        Source source2 = new Source();
        source2.setSourceName(new SourceName("Super Institution 1"));
        source2.setSourceClientId(new SourceClientId("APP-5555-5555-5555-5555"));
        notification2.setSource(source2);
        notifications.add(notification2);

        NotificationPermission notification3 = new NotificationPermission();
        notification3.setPutCode(3L);
        Items activities3 = new Items();
        notification3.setItems(activities3);
        activities3.getItems().add(createActivity(ItemType.WORK, "Work 3", "12345/def"));
        activities3.getItems().add(createActivity(ItemType.WORK, "Work 4", "12345/ghi"));
        notification3.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-10T08:53:56"));
        notification3.setAuthorizationUrl(new AuthorizationUrl("https://thirdparty.com/add-to-orcid/def"));
        Source source3 = new Source();
        source3.setSourceName(new SourceName("Lovely Publisher 1"));
        notification3.setSource(source3);
        source3.setSourceClientId(new SourceClientId("APP-ABCD-ABCD-ABCD-ABCD"));
        notifications.add(notification3);

        NotificationCustom notification4 = new NotificationCustom();
        notification4.setPutCode(4L);
        notification4.setSubject("We have release a new messaging feature");
        notification4.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-10T08:53:56"));
        notifications.add(notification4);

        NotificationCustom notification5 = new NotificationCustom();
        notification5.setPutCode(5L);
        notification5.setSubject("The ORCID registry is now available in Orc");
        notification5.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-11T06:42:18"));
        notifications.add(notification5);

        NotificationAmended notification6 = new NotificationAmended();
        notification6.setPutCode(6L);
        notification6.setSubject("Amended by member");
        notification6.setAmendedSection(AmendedSection.FUNDING);
        notification6.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-12T18:44:36"));
        notification6.setSource(source3);
        notifications.add(notification6);

        EmailMessage emailMessage = emailMessageSender.createDigest(orcidProfile, notifications, Locale.ENGLISH);

        assertNotNull(emailMessage);
        String expectedBodyText = IOUtils.toString(getClass().getResourceAsStream("example_digest_email_body.txt"));
        String expectedBodyHtml = IOUtils.toString(getClass().getResourceAsStream("example_digest_email_body.html"));
        assertTrue(expectedBodyText.contains("Lovely Publisher 1 has updated recent funding on your ORCID record."));
        assertTrue(expectedBodyHtml.contains("Lovely Publisher 1 has updated recent funding on your ORCID record."));
        assertTrue(expectedBodyText.contains("Super Institution 1: Request to add items"));
        assertTrue(expectedBodyHtml.contains("Super Institution 1: Request to add items"));
        assertTrue(expectedBodyText.contains("/action"));
        assertTrue(expectedBodyHtml.contains("/action"));
        assertEquals("[ORCID] John Watson you have 6 new notifications", emailMessage.getSubject());
    }

    private Item createActivity(ItemType actType, String actName, String doi) {
        Item act = new Item();
        act.setItemType(actType);
        act.setItemName(actName);
        ExternalID extId = new ExternalID();
        extId.setType("doi");
        extId.setValue(doi);
        act.setExternalIdentifier(extId);
        return act;
    }

}
