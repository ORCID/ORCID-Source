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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.togglz.Features;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class TemplateManagerTest {

    @Resource
    private TemplateManager templateManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource(name = "messageSource")
    private MessageSource messages;

    @Test
    public void testProcessTemplate() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Declan");
        params.put("word", "cucumber");
        String result = templateManager.processTemplate("test.ftl", params);
        assertNotNull(result);
        assertEquals("Hello, Declan. Did you say cucumber?", result);
    }

    @Test
    public void testGenerateVerifyEmailBody() throws IOException {
        String expectedText = IOUtils.toString(getClass().getResourceAsStream("example_verification_email_body.txt"));
        String expectedHtml = IOUtils.toString(getClass().getResourceAsStream("example_verification_email_body.html"));

        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("primaryEmail", "josiah_carberry@brown.edu");
        templateParams.put("emailName", "Josiah Carberry");
        templateParams.put("subject", "[ORCID] Reminder to verify your email address");
        templateParams.put("verificationUrl", "http://testserver.orcid.org/verify-email/WnhVWGhYVk9lTng4bWdqaDl0azBXY3BmN1F4dHExOW95SnNxeVJSMy9Scz0");
        templateParams.put("orcid", "4444-4444-4444-4446");
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        addStandardParams(templateParams);

        // Generate body from template
        String body = templateManager.processTemplate("verification_email_v2.ftl", templateParams);
        String htmlBody = templateManager.processTemplate("verification_email_html_v2.ftl", templateParams);

        assertEquals(expectedText, body);
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
