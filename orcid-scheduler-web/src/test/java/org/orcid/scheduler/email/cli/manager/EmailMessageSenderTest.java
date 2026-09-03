package org.orcid.scheduler.email.cli.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.EmailMessage;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.impl.TemplateManagerImpl;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.togglz.Features;
import org.orcid.utils.DateUtils;
import org.orcid.jaxb.model.common.ActionType;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.notification.Notification;
import org.orcid.jaxb.model.v3.release.notification.NotificationType;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationAdministrative;
import org.orcid.jaxb.model.v3.release.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.release.notification.permission.AuthorizationUrl;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.release.notification.permission.Items;
import org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.togglz.junit.TogglzRule;

/**
 *
 * @author Will Simpson
 *
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class EmailMessageSenderTest {

    private static final String ORCID = "0000-0000-0000-0000";

    /**
     * The base URL baked into the golden fixtures under src/test/resources/email.
     * Under Spring this arrived as org.orcid.core.baseUri from test-core.properties;
     * with the context gone the test has to supply it itself.
     */
    private static final String BASE_URL = "https://testserver.orcid.org";

    /**
     * Copied verbatim from the "messageSource" bean in orcid-core-context.xml.
     */
    private static final String MESSAGE_SOURCE_BASENAMES = "classpath:i18n/about,classpath:i18n/api,classpath:i18n/email_deprecated,classpath:i18n/email_admin_delegate_request,classpath:i18n/email_added_as_delegate,classpath:i18n/email_auto_deprecate,classpath:i18n/email_new_claim_reminder,classpath:i18n/email_common,classpath:i18n/email_deactivate,classpath:i18n/email_digest,classpath:i18n/email_forgotten_id,classpath:i18n/email_institutional_connection,classpath:i18n/email_locked,classpath:i18n/email_notification,classpath:i18n/email_reactivation,classpath:i18n/email_removed,classpath:i18n/email_reset_password,classpath:i18n/email_reset_password_not_found,classpath:i18n/email_subject,classpath:i18n/email_tips,classpath:i18n/email_verify,classpath:i18n/email_welcome,classpath:i18n/javascript,classpath:i18n/messages,classpath:i18n/admin,classpath:i18n/identifiers,classpath:i18n/notranslate,classpath:i18n/2019-07-emailVisibilitySettings,classpath:i18n/ng_orcid,classpath:i18n/ng_orcid_signin,classpath:i18n/email2faDisabled,classpath:i18n/email2faEnabled,classpath:i18n/layout,classpath:i18n/notification_share,classpath:i18n/notification_digest,classpath:i18n/notification_delegate,classpath:i18n/notification_admin_delegate,classpath:i18n/email_add_works_to_record,classpath:i18n/papi_rate_limit_email,classpath:i18n/notification_mvp";

    /**
     * The @Value default on EmailMessageSenderImpl. No property overrides it, so this
     * is the value that was in force under the Spring context. It is auto-unboxed by
     * ClientUpdates.addElement, so leaving it null throws NPE on the first amended
     * notification.
     */
    private static final Integer MAX_NOTIFICATIONS_PER_CLIENT = 20;

    @InjectMocks
    private EmailMessageSenderImpl emailMessageSender = new EmailMessageSenderImpl(8, 3);

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private RecordNameManager recordNameManagerV3;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void beforeClass() throws IOException {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(ORCID);
        entity.setLocale(AvailableLocales.EN.name());
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(entity);

        when(encryptionManager.encryptForExternalUse(Mockito.anyString())).thenReturn("encrypted");
        when(recordNameManagerV3.deriveEmailFriendlyName(ORCID)).thenReturn("John Watson");

        // The rendering collaborators are deliberately REAL, not mocks. Both tests below
        // assert that the rendered body equals a golden file byte for byte; the rendering
        // IS the behaviour under test. Mocking TemplateManager or MessageSource turns
        // assertEquals(expected, actual) into a tautology that passes while proving nothing.
        TemplateManagerImpl templateManager = new TemplateManagerImpl();
        templateManager.afterPropertiesSet();
        ReflectionTestUtils.setField(emailMessageSender, "templateManager", templateManager);

        ReloadableResourceBundleMessageSource messages = new ReloadableResourceBundleMessageSource();
        messages.setBasenames(MESSAGE_SOURCE_BASENAMES.split(","));
        messages.setDefaultEncoding("UTF-8");
        ReflectionTestUtils.setField(emailMessageSender, "messages", messages);

        OrcidUrlManager orcidUrlManager = new OrcidUrlManager();
        orcidUrlManager.setBaseUrl(BASE_URL);
        ReflectionTestUtils.setField(emailMessageSender, "orcidUrlManager", orcidUrlManager);

        ReflectionTestUtils.setField(emailMessageSender, "maxNotificationsToShowPerClient", MAX_NOTIFICATIONS_PER_CLIENT);
    }

    @Test
    public void testCreateDigest() throws IOException {
        EmailMessage emailMessage = emailMessageSender.createDigest(ORCID, generateNotifications());
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
        EmailMessage emailMessage = emailMessageSender.createAddWorksToRecordEmail("email@orcid.org", ORCID);
        assertNotNull(emailMessage);
        String text = emailMessage.getBodyText();
        String html = emailMessage.getBodyHtml();
        String expectedBodyText = IOUtils.toString(EmailMessageSenderTest.class.getClassLoader().getResourceAsStream("email/example_add_works_to_record.txt"));
        String expectedBodyHtml = IOUtils.toString(EmailMessageSenderTest.class.getClassLoader().getResourceAsStream("email/example_add_works_to_record.html"));
        assertEquals("[ORCID] Add Research Outputs to your ORCID record", emailMessage.getSubject());
        assertEquals(expectedBodyText, text);
        assertEquals(expectedBodyHtml, html);
    }

    /**
     * A delegate notification body is stored verbatim and re-read by the digest job. It must be
     * emitted as data, never compiled as a template: a FreeMarker expression sitting in the
     * stored HTML (it gets there through the granting user's own display name) would otherwise
     * run inside this JVM.
     */
    @Test
    public void digestDoesNotEvaluateStoredNotificationBody() {
        String payload = "${'freemarker.template.utility.Execute'?new()('/usr/bin/id')}";

        NotificationAdministrative delegateNotification = new NotificationAdministrative();
        delegateNotification.setPutCode(7L);
        delegateNotification.setNotificationType(NotificationType.ADMINISTRATIVE);
        delegateNotification.setSubject("Jane Doe has made you an Account Delegate for their ORCID record");
        delegateNotification.setBodyHtml("<html><body><p>Hello from <b>" + payload + "</b></p></body></html>");
        delegateNotification.setCreatedDate(DateUtils.convertToXMLGregorianCalendar("2014-07-12T18:44:36"));
        Source delegateSource = new Source();
        delegateSource.setSourceOrcid(new SourceOrcid("0000-0000-0000-0009"));
        delegateSource.setSourceName(new SourceName("Jane Doe"));
        delegateNotification.setSource(delegateSource);

        List<Notification> notifications = generateNotifications();
        notifications.add(delegateNotification);

        EmailMessage emailMessage = emailMessageSender.createDigest("0000-0000-0000-0000", notifications);
        assertNotNull(emailMessage);
        String html = emailMessage.getBodyHtml();

        // the expression survives as text ...
        assertTrue("stored expression should be emitted literally, not evaluated", html.contains(payload));
        // ... and nothing ran
        assertFalse("command output must not appear in the digest", html.contains("uid="));
        // the surrounding markup is still rendered as HTML rather than escaped away
        assertTrue("stored markup should still render", html.contains("<p>Hello from <b>"));
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
