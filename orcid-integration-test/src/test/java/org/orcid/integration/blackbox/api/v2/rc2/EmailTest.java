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
package org.orcid.integration.blackbox.api.v2.rc2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.BBBUtil;
import org.orcid.integration.blackbox.client.AccountSettingsPage;
import org.orcid.integration.blackbox.client.AccountSettingsPage.EmailsSection;
import org.orcid.integration.blackbox.client.OrcidUi;
import org.orcid.integration.blackbox.web.SigninTest;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-publicV2-context.xml" })
public class EmailTest extends BlackBoxBaseRC2 {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();
    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> t2OAuthClient;

    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;

    @Resource
    private OauthHelper oauthHelper;

    static String accessToken = null;
    
    private String limitedEmailValue = "limited@test.orcid.org";
    
    @Before
    public void before() {
        OrcidUi orcidUi = new OrcidUi(getWebBaseUrl(), webDriver);        
        logUserOut();
        webDriver.get(getWebBaseUrl() + "/userStatus.json?logUserOut=true");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        webDriver.get(getWebBaseUrl() + "/my-orcid");
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.documentReady());
        (new WebDriverWait(webDriver, BBBUtil.TIMEOUT_SECONDS, BBBUtil.SLEEP_MILLISECONDS)).until(BBBUtil.angularHasFinishedProcessing());
        SigninTest.signIn(webDriver, getUser1UserName(), getUser1Password());
        AccountSettingsPage accountSettingsPage = orcidUi.getAccountSettingsPage();
        accountSettingsPage.visit();
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);        
        EmailsSection emailsSection = accountSettingsPage.getEmailsSection();
        emailsSection.toggleEdit();
        BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
        BBBUtil.noSpinners(webDriver);        
        assertTrue("Should be able to add email", emailsSection.canAddEmail());
        
        // Get primary email and verify it is public
        List<org.orcid.integration.blackbox.client.AccountSettingsPage.Email> emails = emailsSection.getEmails();
        org.orcid.integration.blackbox.client.AccountSettingsPage.Email primary = emails.stream().filter(e -> e.isPrimary()).findFirst().get();
        assertNotNull(primary);
        assertEquals(getUser1UserName(), primary.getEmail());
        Visibility primaryVisibility = primary.getVisibility();
        if(!Visibility.PUBLIC.equals(primaryVisibility)) {
            primary.changeVisibility(Visibility.PUBLIC);
        }
        
        // Get limited email and verify it is limited or created it
        Optional<org.orcid.integration.blackbox.client.AccountSettingsPage.Email> limited = emails.stream().filter(e -> e.getEmail().equals(limitedEmailValue)).findFirst();
        if(limited.isPresent()) {
            org.orcid.integration.blackbox.client.AccountSettingsPage.Email limitedEmail = limited.get();
            Visibility visibility = limitedEmail.getVisibility();
            if(!Visibility.LIMITED.equals(visibility)) {
                limitedEmail.changeVisibility(Visibility.LIMITED);
            }
        } else {
            emailsSection.addEmail(limitedEmailValue);
            BBBUtil.extremeWaitFor(BBBUtil.angularHasFinishedProcessing(), webDriver);
            BBBUtil.noSpinners(webDriver);        
            emails = emailsSection.getEmails();
            org.orcid.integration.blackbox.client.AccountSettingsPage.Email limitedEmail = emails.stream().filter(e -> e.getEmail().equals(limitedEmailValue)).findFirst().get();
            Visibility visibility = limitedEmail.getVisibility();
            if(!Visibility.LIMITED.equals(visibility)) {
                limitedEmail.changeVisibility(Visibility.LIMITED);
            }            
        }
    }        
    
    /**
     * PRECONDITIONS: 
     *          The primary email must be public
     * */
    @Test
    public void testGetWithPublicAPI() {
        ClientResponse getAllResponse = publicV2ApiClient.viewEmailXML(getUser1OrcidId());
        assertNotNull(getAllResponse);
        Emails emails = getAllResponse.getEntity(Emails.class);
        assertListContainsEmail(getUser1UserName(), Visibility.PUBLIC, emails);
    }
    
    /**
     * PRECONDITIONS: 
     *          The primary email must be public
     *          The user must have a limited email limited@email.com
     * @throws JSONException 
     * @throws InterruptedException 
     * */
    @Test
    public void testGetWithMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        ClientResponse getAllResponse = memberV2ApiClient.getEmails(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        Emails emails = getAllResponse.getEntity(Emails.class);
        assertListContainsEmail(getUser1UserName(), Visibility.PUBLIC, emails);
        assertListContainsEmail("limited@test.orcid.org", Visibility.LIMITED, emails);
    }
    
    public static void assertListContainsEmail(String emailString, Visibility visibility, Emails emails) {
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertFalse(emails.getEmails().isEmpty());        
        for(Email email : emails.getEmails()) {
            if(email.getEmail().equals(emailString)) {
                assertEquals(visibility, email.getVisibility());
                return;
            }
        }
        
        fail();
    }
    
    public String getAccessToken(String clientId, String clientSecret, String clientRedirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.READ_LIMITED.value(), clientId, clientSecret, clientRedirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
}
