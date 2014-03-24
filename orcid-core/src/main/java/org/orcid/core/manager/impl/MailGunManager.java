package org.orcid.core.manager.impl;
/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class MailGunManager {

    /*
     * Non-production keys will throw 500 errors
     * 
     * From mailguns home page! Yey!
     * 
     * curl -s -k --user api:key-3ax6xnjp29jd6fds4gc373sgvjxteol0 \
     *   https://api.mailgun.net/v2/samples.mailgun.org/messages \
     *    -F from='Excited User <me@samples.mailgun.org>' \
     *    -F to='Dude <dude@mailgun.net>'\
     *    -F to=devs@mailgun.net \
     *    -F subject='Hello' \
     *    -F text='Testing some Mailgun awesomeness!'
     *    
      */
    
    @Value("${com.mailgun.apiKey:key-3ax6xnjp29jd6fds4gc373sgvjxteol0}")
    private String apiKey;
    
    @Value("${com.mailgun.apiUrl::https://api.mailgun.net/v2}")
    private String apiUrl;

    @Value("${com.mailgun.verify.apiUrl:https://api.mailgun.net/v2/samples.mailgun.org/messages}")
    private String verifyApiUrl;

    @Value("${com.mailgun.notify.apiUrl:https://api.mailgun.net/v2/samples.mailgun.org/messages}")
    private String notifyApiUrl;

    @Value("${com.mailgun.testmode:yes}")
    private String testmode;
    
    @Value("${com.mailgun.regexFilter:.*(orcid\\.org|mailinator\\.com|rcpeters\\.com)$}")
    private String filter;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MailGunManager.class);

    public boolean sendEmail(String from, String to, String subject, String text, String html) {
        
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api",
                getApiKey()));
        
        // determine correct api based off domain.
        WebResource webResource = null;
        if (from.trim().endsWith("@verify.orcid.org")) 
            webResource = client.resource(getVerifyApiUrl());
        else if (from.trim().endsWith("@notify.orcid.org")) 
            webResource = client.resource(getNotifyApiUrl());
        else
            webResource = client.resource(getApiUrl());
        
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("from", from);
        formData.add("to", to);
        formData.add("subject", subject);
        formData.add("text", text);
        if (html != null) {
            formData.add("html", html);
        }
        formData.add("o:testmode", testmode);
        LOGGER.debug("Email form data: \n" + formData.toString());
        // the filter is used to prevent sending email to users in qa and sandbox
        if (to.matches(filter)) {
            ClientResponse cr = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
            if (cr.getStatus() != 200) {
                LOGGER.error("Post MailGunManager.sendEmail not accepted: " + formData.toString());
                return false;
            }
        }
        return true;
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

}
