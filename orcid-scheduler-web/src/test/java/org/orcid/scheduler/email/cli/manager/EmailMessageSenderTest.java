package org.orcid.scheduler.email.cli.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.EmailMessage;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.togglz.Features;
import org.orcid.utils.DateUtils;
import org.orcid.jaxb.model.common.ActionType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.release.notification.permission.Items;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.togglz.junit.TogglzRule;

/**
 *
 * @author Will Simpson
 *
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-scheduler-context.xml" })
public class EmailMessageSenderTest {

    @Resource
    private EmailMessageSender emailMessageSender;

    @Resource(name = "recordNameManagerV3")
    private RecordNameManager recordNameManagerV3;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private RecordNameDao recordNameDao;

    @Mock
    private ProfileEntityCacheManager mockProfileEntityCacheManager;

    @Mock
    private EncryptionManager mockEncryptionManager;

    @Mock
    private RecordNameDao mockRecordNameDao;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
        String orcid = "0000-0000-0000-0000";
        ProfileEntity entity = new ProfileEntity();
        entity.setId(orcid);
        entity.setLocale(AvailableLocales.EN.name());
        when(mockProfileEntityCacheManager.retrieve(anyString())).thenReturn(entity);

        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setCreditName("John Watson");
        recordName.setGivenNames("Watson");
        recordName.setFamilyName("John");
        recordName.setOrcid(orcid);

        when(mockEncryptionManager.encryptForExternalUse(Mockito.anyString())).thenReturn("encrypted");
        when(mockRecordNameDao.getRecordName(anyString(), anyLong())).thenReturn(recordName);

        TargetProxyHelper.injectIntoProxy(emailMessageSender, "profileEntityCacheManager", mockProfileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(emailMessageSender, "encryptionManager", mockEncryptionManager);
        TargetProxyHelper.injectIntoProxy(recordNameManagerV3, "recordNameDao", mockRecordNameDao);
    }

    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(emailMessageSender, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(emailMessageSender, "encryptionManager", encryptionManager);
        TargetProxyHelper.injectIntoProxy(recordNameManagerV3, "recordNameDao", recordNameDao);
    }

    @Test
    public void testCreateDigest() throws IOException {
        EmailMessage emailMessage = emailMessageSender.createDigest("0000-0000-0000-0000", generateNotifications());
        assertNotNull(emailMessage);
        String html = emailMessage.getBodyHtml();
        String text = emailMessage.getBodyText();
        String expectedBodyText = IOUtils.toString(EmailMessageSenderTest.class.getClassLoader().getResourceAsStream("email/example_digest_email_body.txt"));
        String expectedBodyHtml = IOUtils.toString(EmailMessageSenderTest.class.getClassLoader().getResourceAsStream("email/example_digest_email_body.html"));
        assertEquals("[ORCID] John Watson you have new notifications", emailMessage.getSubject());
        assertEquals(expectedBodyHtml, html);
        assertEquals(expectedBodyText, text);
    }

    @Test
    public void testAddWorksToRecordEmail() throws IOException {
        EmailMessage emailMessage = emailMessageSender.createAddWorksToRecordEmail("email@orcid.org", "0000-0000-0000-0000");
        assertNotNull(emailMessage);
        String text = emailMessage.getBodyText();
        String html = emailMessage.getBodyHtml();
        String expectedBodyText = IOUtils.toString(EmailMessageSenderTest.class.getClassLoader().getResourceAsStream("email/example_add_works_to_record.txt"));
        String expectedBodyHtml = IOUtils.toString(EmailMessageSenderTest.class.getClassLoader().getResourceAsStream("email/example_add_works_to_record.html"));
        assertEquals("[ORCID] Add Research Outputs to your ORCID record", emailMessage.getSubject());
        assertEquals(expectedBodyText, text);
        assertEquals(expectedBodyHtml, html);
    }

    private List<Notification> generateNotifications() {
        List<Notification> notifications = new ArrayList<>();

        NotificationPermission notification1 = new NotificationPermission();
        notification1.setPutCode(1L);
        Items activities1 = new Items();
        notification1.setItems(activities1);
        activities1.getItems().add(createActivity(ItemType.WORK, "Work 1", "123446/67654", "issn1", null, null));
        activities1.getItems().add(createActivity(ItemType.WORK, "Work 2", "http://dx.doi.org/123446/67655", "issn2", null, null));
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
        activities2.getItems().add(createActivity(ItemType.EMPLOYMENT, "Employment 1 ", "12345/abc", null, "dept", "org"));
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
        activities3.getItems().add(createActivity(ItemType.WORK, "Work 3", "12345/def", "doi01", null, null));
        activities3.getItems().add(createActivity(ItemType.WORK, "Work 4", "12345/ghi", "doi01", null, null));
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
        Items items = new Items();
        items.getItems().add(createActivity(ItemType.WORK, "work-1", "doi01", "issn01", null, null));
        items.getItems().add(createActivity(ItemType.EDUCATION, "education-1", null, null, "department", "org name"));
        notification6.setItems(items);
        notifications.add(notification6);

        return notifications;
    }

    private Item createActivity(ItemType actType, String actName, String doi1, String issn1, String dept, String org) {
        Item item = new Item();
        item.setItemType(actType);
        item.setItemName(actName);
        if (doi1 != null) {
            ExternalID extId = new ExternalID();
            extId.setType("doi");
            extId.setValue(doi1);
            item.setExternalIdentifier(extId);
        }
        item.setActionType(ActionType.CREATE);
        Map<String, Object> additionalInfo = new HashMap<String, Object>();
        List<Map> extIds = new ArrayList<>();
        if (doi1 != null && issn1 != null) {
            Map<String, String> map1 = new HashMap<>();
            map1.put("type", "doi");
            map1.put("value", doi1);
            map1.put("relationship", Relationship.SELF.name());
            extIds.add(map1);

            Map<String, Object> map2 = new HashMap<>();
            map2.put("type", "doi");
            Map<String, String> urlMap = new HashMap<>();
            urlMap.put("value", "https://doi.org/100/100");
            map2.put("url", urlMap);
            map2.put("relationship", Relationship.SELF.name());
            extIds.add(map2);

            Map<String, Object> extIdsMap = new HashMap<>();
            extIdsMap.put("externalIdentifier", extIds);
            additionalInfo.put("external_identifiers", extIdsMap);
        } else if (dept != null && org != null) {
            additionalInfo.put("department", dept);
            additionalInfo.put("org_name", org);
        }

        if (!additionalInfo.isEmpty()) {
            item.setAdditionalInfo(additionalInfo);
        }

        return item;
    }

}
