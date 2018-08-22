package org.orcid.core.manager;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.jaxb.model.common_v2.SourceName;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_v2.NotificationCustom;
import org.orcid.jaxb.model.notification.permission_v2.AuthorizationUrl;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.ItemType;
import org.orcid.jaxb.model.notification.permission_v2.Items;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailMessageSenderTest extends BaseTest {

    @Resource
    private EmailMessageSender emailMessageSender;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManagerMock;
    
    @Mock
    private EncryptionManager encryptionManagerMock;
    
    @Before
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
        ProfileEntity entity = new ProfileEntity();
        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setGivenNames("John");
        recordName.setFamilyName("Watson");
        recordName.setVisibility(Visibility.LIMITED.name());
        entity.setLocale(Locale.EN.name());
        entity.setRecordNameEntity(recordName);
        when(profileEntityCacheManagerMock.retrieve(anyString())).thenReturn(entity);
        TargetProxyHelper.injectIntoProxy(emailMessageSender, "profileEntityCacheManager", profileEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(emailMessageSender, "encryptionManager", encryptionManagerMock);  
        
        when(encryptionManagerMock.encryptForExternalUse(Mockito.anyString())).thenReturn("encrypted");
    }
    
    @Test
    public void testCreateDigest() throws IOException {
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

        EmailMessage emailMessage = emailMessageSender.createDigest("0000-0000-0000-0000", notifications);

        assertNotNull(emailMessage);
        String html = emailMessage.getBodyHtml();
        String text = emailMessage.getBodyText();
        String expectedBodyText = IOUtils.toString(getClass().getResourceAsStream("example_digest_email_body.txt"));
        String expectedBodyHtml = IOUtils.toString(getClass().getResourceAsStream("example_digest_email_body.html"));
        assertEquals("[ORCID] John Watson you have 6 new notifications", emailMessage.getSubject());
        assertEquals(expectedBodyHtml, html);
        assertEquals(expectedBodyText, text);
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
