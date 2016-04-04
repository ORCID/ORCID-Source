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

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
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
public class ResearcherUrlsTest extends BlackBoxBaseRC2 {
    protected static Map<String, String> accessTokens = new HashMap<String, String>();
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;

    protected static WebDriver webDriver;
    
    @BeforeClass
    public static void beforeClass() {
        webDriver = new FirefoxDriver();
    }
    
    @AfterClass
    public static void afterClass() {
        webDriver.quit();
    }
    
    @After
    public void after() throws InterruptedException, JSONException {
        String client1accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());        
        //Get all researcher urls created by client # 1
        ClientResponse getAllResponse = memberV2ApiClient.getResearcherUrls(getUser1OrcidId(), client1accessToken);
        assertNotNull(getAllResponse);
        ResearcherUrls researcherUrls = getAllResponse.getEntity(ResearcherUrls.class);
        assertNotNull(researcherUrls);
        if(researcherUrls.getResearcherUrls() != null && !researcherUrls.getResearcherUrls().isEmpty()) {
            // And delete them
            for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
                if(rUrl.getSource() != null) {
                    if(rUrl.getSource().retrieveSourcePath().equals(getClient1ClientId())) {
                        ClientResponse deletedResponse = memberV2ApiClient.deleteResearcherUrl(getUser1OrcidId(), rUrl.getPutCode(), client1accessToken);
                        assertNotNull(deletedResponse);
                        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletedResponse.getStatus());
                    } 
                }            
            }
        }
        
        String client2accessToken = getAccessToken(getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        //Get all researcher urls created by client # 2
        getAllResponse = memberV2ApiClient.getResearcherUrls(getUser1OrcidId(), client2accessToken);
        assertNotNull(getAllResponse);
        researcherUrls = getAllResponse.getEntity(ResearcherUrls.class);
        assertNotNull(researcherUrls);
        if(researcherUrls.getResearcherUrls() != null && !researcherUrls.getResearcherUrls().isEmpty()) {
            // And delete them
            for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
                if(rUrl.getSource() != null) {
                    if(rUrl.getSource().retrieveSourcePath().equals(getClient2ClientId())) {
                        ClientResponse deletedResponse = memberV2ApiClient.deleteResearcherUrl(getUser1OrcidId(), rUrl.getPutCode(), client2accessToken);
                        assertNotNull(deletedResponse);
                        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deletedResponse.getStatus());
                    } 
                }            
            }
        }
    }
    
    @Test
    public void testResearcherUrl() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        ResearcherUrl rUrlToCreate = (ResearcherUrl) unmarshallFromPath("/record_2.0_rc2/samples/researcher-url-2.0_rc2.xml", ResearcherUrl.class);
        assertNotNull(rUrlToCreate);
        Long time = System.currentTimeMillis();
        rUrlToCreate.setCreatedDate(null);
        rUrlToCreate.setLastModifiedDate(null);
        rUrlToCreate.setPath(null);
        rUrlToCreate.setPutCode(null);
        rUrlToCreate.setSource(null);
        rUrlToCreate.setUrl(new Url(rUrlToCreate.getUrl().getValue() + time));
        rUrlToCreate.setUrlName(String.valueOf(time));
        
        // Create
        ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc2/" + getUser1OrcidId() + "/researcher-urls/\\d+"));

        // Read
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        ResearcherUrl gotResearcherUrl = getResponse.getEntity(ResearcherUrl.class);
        assertNotNull(gotResearcherUrl);
        assertNotNull(gotResearcherUrl.getPutCode());
        assertNotNull(gotResearcherUrl.getSource());
        assertNotNull(gotResearcherUrl.getCreatedDate());
        assertNotNull(gotResearcherUrl.getLastModifiedDate());
        assertEquals(getClient1ClientId(), gotResearcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://site1.com/" + time, gotResearcherUrl.getUrl().getValue());
        assertEquals(String.valueOf(time), gotResearcherUrl.getUrlName());
        assertEquals("public", gotResearcherUrl.getVisibility().value());

        //Save the original visibility
        Visibility originalVisibility = gotResearcherUrl.getVisibility();
        Visibility updatedVisibility = Visibility.PRIVATE.equals(originalVisibility) ? Visibility.LIMITED : Visibility.PRIVATE;
        
        //Verify you cant update the visibility
        gotResearcherUrl.setVisibility(updatedVisibility);              
        ClientResponse putResponse = memberV2ApiClient.updateLocationXml(postResponse.getLocation(), accessToken, gotResearcherUrl);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), putResponse.getStatus());
        OrcidError error = putResponse.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9035), error.getErrorCode());
                        
        //Set the visibility again to the initial one
        gotResearcherUrl.setVisibility(originalVisibility);
        
        // Update
        LastModifiedDate initialLastModified = gotResearcherUrl.getLastModifiedDate();
        Long currentTime = System.currentTimeMillis();
        gotResearcherUrl.setUrlName(gotResearcherUrl.getUrlName() + " - " + currentTime);
        gotResearcherUrl.getUrl().setValue(gotResearcherUrl.getUrl().getValue() + currentTime);
        ClientResponse updatedResearcherUrlResponse = memberV2ApiClient.updateResearcherUrls(getUser1OrcidId(), gotResearcherUrl, accessToken);
        assertNotNull(updatedResearcherUrlResponse);
        assertEquals(Response.Status.OK.getStatusCode(), updatedResearcherUrlResponse.getStatus());
        ResearcherUrl updatedResearcherUrl = updatedResearcherUrlResponse.getEntity(ResearcherUrl.class);
        assertNotNull(updatedResearcherUrl);
        assertEquals("http://site1.com/" + time + currentTime, updatedResearcherUrl.getUrl().getValue());
        assertEquals(String.valueOf(time) + " - " + currentTime, updatedResearcherUrl.getUrlName());
        // Keep it public, since it is more restrictive than the user visibility
        // default
        assertEquals("public", updatedResearcherUrl.getVisibility().value());
        assertFalse(initialLastModified.equals(updatedResearcherUrl.getLastModifiedDate()));

        // Delete
        ClientResponse deleteResponse = memberV2ApiClient.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testCantAddDuplicatedResearcherUrl() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        ResearcherUrl rUrlToCreate = new ResearcherUrl();
        rUrlToCreate.setUrl(new Url("http://newurl.com/" + now));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(Visibility.PUBLIC);

        // Create it
        ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        // Add it again
        postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());

        // Check it can be created by other client
        String otherClientToken = getAccessToken(getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, otherClientToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
    }

    @Test
    public void testAddMultipleResearcherUrlAndGetThem() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        ResearcherUrl rUrlToCreate = new ResearcherUrl();
        rUrlToCreate.setVisibility(Visibility.PUBLIC);

        for (int i = 0; i < 3; i++) {
            // Change the name
            rUrlToCreate.setUrlName("url-name-" + now + "-" + i);
            rUrlToCreate.setUrl(new Url("http://newurl.com/" + now + "/" + i));
            // Create it
            ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        ClientResponse getAllResponse = memberV2ApiClient.getResearcherUrls(getUser1OrcidId(), accessToken);
        assertNotNull(getAllResponse);
        ResearcherUrls researcherUrls = getAllResponse.getEntity(ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getPath());
        assertTrue(researcherUrls.getPath().contains(getUser1OrcidId()));
        assertNotNull(researcherUrls.getResearcherUrls());

        boolean found1 = false, found2 = false, found3 = false;

        for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            if (rUrl.getUrlName().equals("url-name-" + now + "-0")) {
                found1 = true;
            } else if (rUrl.getUrlName().equals("url-name-" + now + "-1")) {
                found2 = true;
            } else if (rUrl.getUrlName().equals("url-name-" + now + "-2")) {
                found3 = true;
            }
        }

        assertTrue(found1 && found2 && found3);        
    }
    
    @Test
    public void testGetWithPublicAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());        
        //Create some researcher urls
        ResearcherUrl rUrlToCreate = new ResearcherUrl();        
        Long now = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            // Change the name
            rUrlToCreate.setUrlName("url-name-" + now + "-" + i);
            rUrlToCreate.setUrl(new Url("http://newurl.com/" + now + "/" + i));
            if(i == 3) {
                changeDefaultUserVisibility(webDriver, Visibility.LIMITED);
                rUrlToCreate.setVisibility(Visibility.LIMITED);
                // Create it
                ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
                assertNotNull(postResponse);
                assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
            } else if(i == 4) {
                changeDefaultUserVisibility(webDriver, Visibility.PRIVATE);
                rUrlToCreate.setVisibility(Visibility.PRIVATE);
                // Create it
                ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
                assertNotNull(postResponse);
                assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
            } else {
                changeDefaultUserVisibility(webDriver, Visibility.PUBLIC);
                rUrlToCreate.setVisibility(Visibility.PUBLIC);
                // Create it
                ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
                assertNotNull(postResponse);
                assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
            }            
        }
        
        //Set the default visibility in public
        changeDefaultUserVisibility(webDriver, Visibility.PUBLIC);
        
        ClientResponse getAllResponse = publicV2ApiClient.viewResearcherUrlsXML(getUser1OrcidId()); 
        assertNotNull(getAllResponse);
        ResearcherUrls researcherUrls = getAllResponse.getEntity(ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(3, researcherUrls.getResearcherUrls().size());
        for(ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            assertNotNull(rUrl);
            assertEquals(Visibility.PUBLIC, rUrl.getVisibility());
            ClientResponse theRUrl = publicV2ApiClient.viewResearcherUrlXML(getUser1OrcidId(), String.valueOf(rUrl.getPutCode()));
            assertNotNull(theRUrl);
            ResearcherUrl researcherUrl = theRUrl.getEntity(ResearcherUrl.class);
            assertEquals(researcherUrl.getCreatedDate(), rUrl.getCreatedDate());
            assertEquals(researcherUrl.getLastModifiedDate(), rUrl.getLastModifiedDate());
            assertEquals(researcherUrl.getPutCode(), rUrl.getPutCode());
            assertEquals(researcherUrl.getSource(), rUrl.getSource());
            assertEquals(researcherUrl.getUrl(), rUrl.getUrl());
            assertEquals(researcherUrl.getUrlName(), rUrl.getUrlName());
            assertEquals(researcherUrl.getVisibility(), rUrl.getVisibility());
        }
    }

    @Test
    public void testTryingToAddInvalidResearcherUrls() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        ResearcherUrl rUrlToCreate = new ResearcherUrl();
        rUrlToCreate.setUrl(new Url(""));
        rUrlToCreate.setUrlName("");
        // Create
        ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        String _351Chars = new String();
        for(int i = 0; i < 531; i++) {
            _351Chars += "a";
        }
        
        rUrlToCreate.setUrl(new Url(_351Chars));
        postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        rUrlToCreate.setUrl(new Url("http://myurl.com"));
        rUrlToCreate.setUrlName(_351Chars);
        postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        rUrlToCreate.setUrlName("The name");
        postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        // Read it to delete it
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        ResearcherUrl gotResearcherUrl = getResponse.getEntity(ResearcherUrl.class);        
        ClientResponse deleteResponse = memberV2ApiClient.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }
    
    @Test
    public void testInvalidPutCodeReturns404() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(getClient1ClientId(), getClient1ClientSecret(), getClient1RedirectUri());
        assertNotNull(accessToken);
        
        ResearcherUrl rUrlToCreate = new ResearcherUrl();
        rUrlToCreate.setUrl(new Url("http://newurl.com/" + System.currentTimeMillis()));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(Visibility.PUBLIC);    
        rUrlToCreate.setPutCode(1234567890L);
        
        ClientResponse response = memberV2ApiClient.updateResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }       
    
    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_UPDATE.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }
}
