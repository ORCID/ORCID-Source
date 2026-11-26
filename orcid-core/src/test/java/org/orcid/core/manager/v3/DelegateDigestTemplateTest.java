package org.orcid.core.manager.v3;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.orcid.core.manager.impl.TemplateManagerImpl;
import org.orcid.pojo.DigestEmail;
import org.springframework.context.support.StaticMessageSource;

public class DelegateDigestTemplateTest {
    @Test
    public void rendersAllDelegateNotificationsInHtmlAndText() throws Exception {
        TemplateManagerImpl templates = new TemplateManagerImpl();
        templates.afterPropertiesSet();
        StaticMessageSource messages = new StaticMessageSource();
        messages.setUseCodeAsDefaultMessage(true);
        messages.addMessage("notification.share.record", Locale.ENGLISH, "YOUR RECORD");
        Map<String, Object> first = new HashMap<>();
        first.put("subject", "Tom <Sawyer> declined");
        first.put("bodyHtml", "<html><body><p><strong>Tom &lt;Sawyer&gt;</strong> <a href=\"https://orcid.org/123\">123</a></p></body></html>");
        first.put("bodyText", "Tom declined");
        Map<String, Object> second = new HashMap<>();
        second.put("subject", "Alice accepted");
        second.put("bodyHtml", "<p>Alice accepted</p>");
        second.put("bodyText", "Alice accepted");
        Map<String, Object> params = new HashMap<>();
        params.put("messages", messages);
        params.put("messageArgs", new Object[0]);
        params.put("locale", Locale.ENGLISH);
        params.put("subject", "Notifications");
        params.put("emailName", "Recipient");
        params.put("orcidValue", "0000-0000-0000-0001");
        params.put("baseUri", "https://orcid.org");
        params.put("digestEmail", new DigestEmail());
        params.put("verboseNotifications", false);
        params.put("delegateNotifications", Arrays.asList(first, second));

        String html = templates.processTemplate("digest_notification_html.ftl", params, Locale.ENGLISH);
        String text = templates.processTemplate("digest_notification.ftl", params, Locale.ENGLISH);
        assertTrue(html.contains("YOUR RECORD"));
        assertTrue(html.contains("Tom &lt;Sawyer&gt; declined"));
        assertTrue(html.contains("<strong>Tom &lt;Sawyer&gt;</strong>"));
        assertTrue(html.contains("<a href=\"https://orcid.org/123\">123</a>"));
        assertTrue(html.contains("Alice accepted"));
        assertEquals(html.indexOf("<body>"), html.lastIndexOf("<body>"));
        assertTrue(text.contains("Tom declined"));
        assertTrue(text.contains("Alice accepted"));
    }
}