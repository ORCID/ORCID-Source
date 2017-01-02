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
package org.orcid.integration.blackbox.api.v2.rc3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc3.LastModifiedDate;
import org.orcid.jaxb.model.common_rc3.Url;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.error_rc1.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.orcid.jaxb.model.record_rc3.ResearcherUrls;
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
public class ResearcherUrlsTest extends BlackBoxBaseRC3 {
    @Resource(name = "memberV2ApiClient_rc3")
    private MemberV2ApiClientImpl memberV2ApiClient;
    @Resource(name = "publicV2ApiClient_rc3")
    private PublicV2ApiClientImpl publicV2ApiClient;
    
    private static String researcherUrl1 = "http://test.orcid.org/1/" + System.currentTimeMillis();
    private static String researcherUrl2 = "http://test.orcid.org/2/" + System.currentTimeMillis();
    
    @BeforeClass
    public static void setup(){
        signin();        
        openEditResearcherUrlsModal();        
        createResearcherUrl(researcherUrl1);
        createResearcherUrl(researcherUrl2);        
        changeResearcherUrlsVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        saveResearcherUrlsModal();
    }
    
    @AfterClass
    public static void after() {  
        showMyOrcidPage();
        openEditResearcherUrlsModal(); 
        deleteResearcherUrls();
        saveResearcherUrlsModal();
        signout();
    }
    
    @Test
    public void testResearcherUrl() throws InterruptedException, JSONException, URISyntaxException {
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        String accessToken = getAccessToken();
        assertNotNull(accessToken);
        ResearcherUrl rUrlToCreate = new ResearcherUrl();
        long time = System.currentTimeMillis();
        String url = "http://test.orcid.org/test/" + time;
        rUrlToCreate.setUrl(new Url(url));
        rUrlToCreate.setUrlName(url);
        
        // Create
        ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc3/" + getUser1OrcidId() + "/researcher-urls/\\d+"));

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
        assertEquals("http://test.orcid.org/test/" + time, gotResearcherUrl.getUrl().getValue());
        assertEquals("http://test.orcid.org/test/" + time, gotResearcherUrl.getUrlName());
        assertEquals("public", gotResearcherUrl.getVisibility().value());
        assertNotNull(gotResearcherUrl.getDisplayIndex());
        Long originalDisplayIndex = gotResearcherUrl.getDisplayIndex();
        
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
        assertEquals("http://test.orcid.org/test/" + time + currentTime, updatedResearcherUrl.getUrl().getValue());
        assertEquals("http://test.orcid.org/test/" + time + " - " + currentTime, updatedResearcherUrl.getUrlName());
        assertEquals(originalDisplayIndex, updatedResearcherUrl.getDisplayIndex());
        
        // Keep it public, since it is more restrictive than the user visibility
        // default
        assertEquals(Visibility.PUBLIC, updatedResearcherUrl.getVisibility());
        assertFalse(initialLastModified.equals(updatedResearcherUrl.getLastModifiedDate()));

        // Delete
        ClientResponse deleteResponse = memberV2ApiClient.deleteResearcherUrl(getUser1OrcidId(), gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testCantAddDuplicatedResearcherUrl() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
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
        String otherClientToken = getAccessToken(getUser1OrcidId(), getUser1Password(), getScopes(ScopePathType.PERSON_UPDATE), getClient2ClientId(), getClient2ClientSecret(), getClient2RedirectUri());
        postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, otherClientToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
    }

    @Test
    public void testAddMultipleResearcherUrlAndGetThem() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
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
            if(rUrl.getUrlName() != null) {
                if (rUrl.getUrlName().equals("url-name-" + now + "-0")) {
                    found1 = true;
                } else if (rUrl.getUrlName().equals("url-name-" + now + "-1")) {
                    found2 = true;
                } else if (rUrl.getUrlName().equals("url-name-" + now + "-2")) {
                    found3 = true;
                }
            }
        }

        assertTrue(found1 && found2 && found3);        
    }
    
    @Test
    public void testGetWithPublicAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();        
        //Create some researcher urls
        ResearcherUrl rUrlToCreate = new ResearcherUrl();        
        Long now = System.currentTimeMillis();
        List<String> rUrlsToFind = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            // Change the name
            String urlName = "url-name-" + now + "-" + i;            
            rUrlToCreate.setUrlName(urlName);
            rUrlToCreate.setUrl(new Url("http://newurl.com/" + now + "/" + i));
            if(i == 3) {
                changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.LIMITED);
                rUrlToCreate.setVisibility(Visibility.LIMITED);
                // Create it
                ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
                assertNotNull(postResponse);
                assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
            } else if(i == 4) {
                changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
                rUrlToCreate.setVisibility(Visibility.PRIVATE);
                // Create it
                ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
                assertNotNull(postResponse);
                assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
            } else {
                //Add public rUrls to the list of elements to find
                rUrlsToFind.add(urlName);
                changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
                rUrlToCreate.setVisibility(Visibility.PUBLIC);
                // Create it
                ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(getUser1OrcidId(), rUrlToCreate, accessToken);
                assertNotNull(postResponse);
                assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
            }            
        }
        
        //Set the default visibility in public
        changeDefaultUserVisibility(webDriver, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        
        ClientResponse getAllResponse = publicV2ApiClient.viewResearcherUrlsXML(getUser1OrcidId()); 
        assertNotNull(getAllResponse);
        ResearcherUrls researcherUrls = getAllResponse.getEntity(ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());        
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
            if(rUrlsToFind.contains(researcherUrl.getUrlName())) {
                //If the rurl is found, remove it from the list of elements to find
                rUrlsToFind.remove(researcherUrl.getUrlName());
            }
        }
        
        assertTrue("Items not found: " + rUrlsToFind.size(), rUrlsToFind.isEmpty());
    }

    @Test
    public void testTryingToAddInvalidResearcherUrls() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken();
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
        String accessToken = getAccessToken();
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
    
    public String getAccessToken() throws InterruptedException, JSONException {
        return getAccessToken(getScopes(ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED));
    }
}
