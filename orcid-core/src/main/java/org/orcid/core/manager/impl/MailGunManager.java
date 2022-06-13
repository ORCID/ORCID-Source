package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.utils.rest.RESTHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

public class MailGunManager {

    /*
     * Non-production keys will throw 500 errors
     * 
     * From mailguns home page! Yey!
     * 
     * curl -s -k --user api:key-3ax6xnjp29jd6fds4gc373sgvjxteol0 \
     * https://api.mailgun.net/v2/samples.mailgun.org/messages \ -F
     * from='Excited User <me@samples.mailgun.org>' \ -F to='Dude
     * <dude@mailgun.net>'\ -F to=devs@mailgun.net \ -F subject='Hello' \ -F
     * text='Testing some Mailgun awesomeness!'
     */

    @Value("${com.mailgun.apiKey:key-3ax6xnjp29jd6fds4gc373sgvjxteol0}")
    private String apiKey;

    @Value("${com.mailgun.apiUrl:https://api.mailgun.net/v2}")
    private String apiUrl;

    @Value("${com.mailgun.verify.apiUrl:https://api.mailgun.net/v2/samples.mailgun.org/messages}")
    private String verifyApiUrl;

    @Value("${com.mailgun.notify.apiUrl:https://api.mailgun.net/v2/samples.mailgun.org/messages}")
    private String notifyApiUrl;
    
    @Value("${com.mailgun.marketing.apiUrl:https://api.mailgun.net/v2/samples.mailgun.org/messages}")
    private String marketingApiUrl;

    @Value("${com.mailgun.testmode:yes}")
    private String testmode;

    @Value("${com.mailgun.regexFilter:.*(orcid\\.org|mailinator\\.com|rcpeters\\.com)$}")
    private String filter;
    
    @Resource
    private RESTHelper restHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailGunManager.class);

    public boolean sendMarketingEmail(String from, String to, String subject, String text, String html) {
        return sendEmail(from, to, subject, text, html, true);
    }
    
    public boolean sendEmail(String from, String to, String subject, String text, String html) {
        return sendEmail(from, to, subject, text, html, false);
    }
    
    public boolean sendEmail(String from, String to, String subject, String text, String html, boolean marketing) {

        // determine correct api based off domain.
        String url;
        
        String fromEmail = getFromEmail(from);
        if(marketing)
            url = getMarketingApiUrl();
        else if (fromEmail.endsWith("@verify.orcid.org"))
            url = getVerifyApiUrl();
        else if (fromEmail.endsWith("@notify.orcid.org"))
            url = getNotifyApiUrl();
        else
            url = getApiUrl();        
        
        Form form = new Form();
        form.param("from", from);
        form.param("to", to);
        form.param("subject", subject);
        form.param("text", text);
        if (html != null) {
            form.param("html", html);
        }
        form.param("o:testmode", testmode);

        // the filter is used to prevent sending email to users in qa and
        // sandbox
        if (to.matches(filter)) {
            Response response = restHelper.postWithAuthentication(url, "api", getApiKey(), null);
            if (response.getStatus() != 200) {
                LOGGER.warn("Post MailGunManager.sendEmail to {} not accepted\nstatus: {}\nbody: {}", 
                        new Object[] { to, response.getStatus(), response.readEntity(String.class) });
                return false;
            }
            return true;
        } else {
            LOGGER.debug("Email not sent to {} due to regex mismatch", to);
            return false;
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getVerifyApiUrl() {
        return verifyApiUrl;
    }

    public void setVerifyApiUrl(String verifyApiUrl) {
        this.verifyApiUrl = verifyApiUrl;
    }

    public String getNotifyApiUrl() {
        return notifyApiUrl;
    }
    
    public void setNotifyApiUrl(String notifyApiUrl) {
        this.notifyApiUrl = notifyApiUrl;
    }

    public String getMarketingApiUrl() {
        return marketingApiUrl;
    }

    public void setMarketingApiUrl(String marketingApiUrl) {
        this.marketingApiUrl = marketingApiUrl;
    }

    private String getFromEmail(String from) {
        int indexOfLt = from.indexOf('<');
        if (indexOfLt != -1) {
            int indexOfGt = from.indexOf('>');
            from = from.substring(indexOfLt + 1, indexOfGt);
        }
        return from.trim();
    }
}
