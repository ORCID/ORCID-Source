package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.impl.TemplateManagerImpl;
import org.orcid.core.togglz.Features;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.togglz.junit.TogglzRule;

/**
 * These four tests assert that a FreeMarker template renders to a golden file byte for byte, so
 * the FreeMarker engine and the message bundle it reads through ARE the behaviour under test.
 * Both are built here for real -- a real TemplateManagerImpl whose afterPropertiesSet() loads the
 * templates off the classpath exactly as the Spring bean does, and a real
 * ReloadableResourceBundleMessageSource over the same i18n basenames as the "messageSource" bean
 * -- because mocking either one would mean stubbing it with the text the assertion then compares
 * against, and the test would pass with the templates deleted.
 *
 * <p>
 * What is gone is the orcid-core Spring context around them: the only other things this test took
 * from it were two property values, and those are supplied directly.
 *
 * <p>
 * BASE_URL is org.orcid.core.baseUri from orcid-test's test-core.properties. It is baked into the
 * golden files (the record link, the email preferences and privacy policy links, the footer), so
 * it is not free to change.
 *
 * <p>
 * MESSAGE_SOURCE_BASENAMES is copied verbatim from the "messageSource" bean in
 * orcid-core-context.xml. Same list, same default encoding, same fallback behaviour, so the same
 * strings resolve.
 *
 * <p>
 * LocaleManager is the one collaborator that is mocked, and it is not part of the rendering: it is
 * only read by the two argument processTemplate to pick the locale for the localized template
 * lookup. The locale actually rendered into the body is the one this test puts in the parameter
 * map. Under the Spring context this was LocaleManagerImpl over LocaleContextHolder, which with no
 * request bound returns the JVM default -- a real dependency on the machine's locale, which
 * pinning it to ENGLISH removes.
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateManagerTest {

    private static final String BASE_URL = "https://testserver.orcid.org";

    private static final String MESSAGE_SOURCE_BASENAMES = "classpath:i18n/about,classpath:i18n/api,classpath:i18n/email_deprecated,classpath:i18n/email_admin_delegate_request,classpath:i18n/email_added_as_delegate,classpath:i18n/email_auto_deprecate,classpath:i18n/email_new_claim_reminder,classpath:i18n/email_common,classpath:i18n/email_deactivate,classpath:i18n/email_digest,classpath:i18n/email_forgotten_id,classpath:i18n/email_institutional_connection,classpath:i18n/email_locked,classpath:i18n/email_notification,classpath:i18n/email_reactivation,classpath:i18n/email_removed,classpath:i18n/email_reset_password,classpath:i18n/email_reset_password_not_found,classpath:i18n/email_subject,classpath:i18n/email_tips,classpath:i18n/email_verify,classpath:i18n/email_welcome,classpath:i18n/javascript,classpath:i18n/messages,classpath:i18n/admin,classpath:i18n/identifiers,classpath:i18n/notranslate,classpath:i18n/2019-07-emailVisibilitySettings,classpath:i18n/ng_orcid,classpath:i18n/ng_orcid_signin,classpath:i18n/email2faDisabled,classpath:i18n/email2faEnabled,classpath:i18n/layout,classpath:i18n/notification_share,classpath:i18n/notification_digest,classpath:i18n/notification_delegate,classpath:i18n/notification_admin_delegate,classpath:i18n/email_add_works_to_record,classpath:i18n/papi_rate_limit_email,classpath:i18n/notification_mvp";

    /** addStandardParams asks every Feature whether it is active, which needs a FeatureManager. */
    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Mock
    private LocaleManager localeManager;

    private TemplateManager templateManager;

    private OrcidUrlManager orcidUrlManager;

    private MessageSource messages;

    @Before
    public void before() throws IOException {
        when(localeManager.getLocale()).thenReturn(Locale.ENGLISH);

        TemplateManagerImpl impl = new TemplateManagerImpl();
        ReflectionTestUtils.setField(impl, "localeManager", localeManager);
        impl.afterPropertiesSet();
        templateManager = impl;

        orcidUrlManager = new OrcidUrlManager();
        orcidUrlManager.setBaseUrl(BASE_URL);

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(MESSAGE_SOURCE_BASENAMES.split(","));
        messageSource.setDefaultEncoding("UTF-8");
        messages = messageSource;
    }

    @Test
    public void testGenerateVerifyEmailNonPrimaryPlain() throws IOException {
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_verification_email_non_primary.txt"), StandardCharsets.UTF_8);
        
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("primaryEmail", "josiah_carberry@brown.edu");
        templateParams.put("userName", "Josiah Carberry");
        templateParams.put("subject", "[ORCID] Reminder to verify your email address");
        templateParams.put("verificationUrl", "http://testserver.orcid.org/verify-email/WnhVWGhYVk9lTng4bWdqaDl0azBXY3BmN1F4dHExOW95SnNxeVJSMy9Scz0");
        templateParams.put("orcidId", "4444-4444-4444-4446");
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());        
        addStandardParams(templateParams);

        // Generate body from template
        String body = templateManager.processTemplate("verification_email_v2.ftl", templateParams);
        
        assertEquals(expectedText, body);        
    }
    
    @Test
    public void testGenerateVerifyEmailPrimaryPlain() throws IOException {
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_verification_email_primary.txt"), StandardCharsets.UTF_8);
        
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("primaryEmail", "josiah_carberry@brown.edu");
        templateParams.put("userName", "Josiah Carberry");
        templateParams.put("subject", "[ORCID] Reminder to verify your email address");
        templateParams.put("verificationUrl", "http://testserver.orcid.org/verify-email/WnhVWGhYVk9lTng4bWdqaDl0azBXY3BmN1F4dHExOW95SnNxeVJSMy9Scz0");
        templateParams.put("orcidId", "4444-4444-4444-4446");
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("isPrimary", true);
        addStandardParams(templateParams);

        // Generate body from template
        String body = templateManager.processTemplate("verification_email_v2.ftl", templateParams);
        
        assertEquals(expectedText, body);        
    }
    
    @Test
    public void testGenerateVerifyEmailNonPrimaryHtml() throws IOException {
        String expectedHtml = IOUtils.toString(getClass().getResourceAsStream("example_verification_email_non_primary.html"), StandardCharsets.UTF_8);

        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("primaryEmail", "josiah_carberry@brown.edu");
        templateParams.put("userName", "Josiah Carberry");
        templateParams.put("subject", "[ORCID] Reminder to verify your email address");
        templateParams.put("verificationUrl", "http://testserver.orcid.org/verify-email/WnhVWGhYVk9lTng4bWdqaDl0azBXY3BmN1F4dHExOW95SnNxeVJSMy9Scz0");
        templateParams.put("orcidId", "4444-4444-4444-4446");
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());        
        addStandardParams(templateParams);

        // Generate body from template
        String htmlBody = templateManager.processTemplate("verification_email_html_v2.ftl", templateParams);

        assertEquals(expectedHtml, htmlBody);
    }
    
    @Test
    public void testGenerateVerifyEmailPrimaryHtml() throws IOException {
        String expectedHtml = IOUtils.toString(getClass().getResourceAsStream("example_verification_email_primary.html"), StandardCharsets.UTF_8);

        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("primaryEmail", "josiah_carberry@brown.edu");
        templateParams.put("userName", "Josiah Carberry");
        templateParams.put("subject", "[ORCID] Reminder to verify your email address");
        templateParams.put("verificationUrl", "http://testserver.orcid.org/verify-email/WnhVWGhYVk9lTng4bWdqaDl0azBXY3BmN1F4dHExOW95SnNxeVJSMy9Scz0");
        templateParams.put("orcidId", "4444-4444-4444-4446");
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());        
        templateParams.put("isPrimary", true);
        addStandardParams(templateParams);

        // Generate body from template
        String htmlBody = templateManager.processTemplate("verification_email_html_v2.ftl", templateParams);

        assertEquals(expectedHtml, htmlBody);
    }

    private void addStandardParams(Map<String, Object> templateParams) {
        Map<String, Boolean> features = Arrays.asList(Features.values()).stream().collect(Collectors.toMap(Features::name, Features::isActive));
        templateParams.put("features", features);
        templateParams.put("messages", messages);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("locale", Locale.ENGLISH);
    }

}
