package org.orcid.utils.email;

import java.util.AbstractMap.SimpleEntry;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

@Component
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
    
    @Value("${com.mailgun.apiKey}")
    private String apiKey;

    @Value("${com.mailgun.regexFilter:.*(orcid\\.org|mailinator\\.com)$}")
    private String filter;
    
    private JerseyClientHelper jerseyClientHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(MailGunManager.class);

    @PostConstruct
    public void initJerseyClientHelper() {
        // Mailgun username and password 
        SimpleEntry<String, String> auth = new SimpleEntry<String, String>("api", apiKey);
        // Setup our own jersey helper with the mailgun credentials 
        jerseyClientHelper = new JerseyClientHelper(auth);    
    }
    
    public boolean sendMarketingEmail(String from, String to, String subject, String text, String html) {
        return sendEmail(from, to, null, subject, text, html, true);
    }
    
    public boolean sendEmail(String from, String to, String subject, String text, String html) {
        return sendEmail(from, to, null, subject, text, html, false);
    }

    public boolean sendEmailWithCC(String from, String to, String cc, String subject, String text, String html) {
        return sendEmail(from, to, cc, subject, text, html, false);
    }

    private boolean sendEmail(String from, String to, String cc, String subject, String text, String html, boolean marketing) {
        String fromEmail = getFromEmail(from);
        String apiUrl;
        if(marketing)
            apiUrl = getMarketingApiUrl();
        else if (fromEmail.endsWith("@verify.orcid.org"))
            apiUrl = getVerifyApiUrl();
        else if (fromEmail.endsWith("@notify.orcid.org") || fromEmail.endsWith("@orcid.org"))
            apiUrl = getNotifyApiUrl();
        else
            apiUrl = getApiUrl();        
        Form formData = new Form();
        formData.param("from", from);
        formData.param("to", to);
        if(StringUtils.isNotBlank(cc)) {
            formData.param("cc", cc);
        }
        formData.param("subject", subject);
        formData.param("text", text);
        if (html != null) {
            formData.param("html", html);
        }
        formData.param("o:testmode", testmode);

        // the filter is used to prevent sending email to users in qa and
        // sandbox
        if (to.matches(filter)) {            
            JerseyClientResponse<String, String> cr = jerseyClientHelper.executePostRequest(apiUrl, MediaType.APPLICATION_FORM_URLENCODED_TYPE, formData, String.class, String.class);
            if (cr.getStatus() != 200) {
                LOGGER.warn("Post MailGunManager.sendEmail to {} not accepted\nstatus: {}\nbody: {}", 
                        new Object[] { to, cr.getStatus(), cr.getEntity() });
                return false;
            }
            return true;
        } else {
            LOGGER.debug("Email not sent to {} due to regex mismatch", to);
            return false;
        }
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
