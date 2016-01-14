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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common.LastModifiedDate;
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.springframework.beans.factory.annotation.Value;
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
public class ResearcherUrlsTest extends BlackBoxBase {

    protected static Map<String, String> accessTokens = new HashMap<String, String>();

    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String client1RedirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;            
    @Value("${org.orcid.web.testClient2.clientId}")
    public String client2ClientId;
    @Value("${org.orcid.web.testClient2.clientSecret}")
    public String client2ClientSecret;
    @Value("${org.orcid.web.testClient2.redirectUri}")
    protected String client2RedirectUri;    
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;
    @Value("${org.orcid.web.testUser1.username}")
    public String user1UserName;
    @Value("${org.orcid.web.testUser1.password}")
    public String user1Password;    
    
    @Resource(name = "memberV2ApiClient_rc2")
    private MemberV2ApiClientImpl memberV2ApiClient;

    @Resource(name = "publicV2ApiClient_rc2")
    private PublicV2ApiClientImpl publicV2ApiClient;

    @Test
    public void testResearcherUrl() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
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
        ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        String locationPath = postResponse.getLocation().getPath();
        assertTrue("Location header path should match pattern, but was " + locationPath, locationPath.matches(".*/v2.0_rc2/" + user1OrcidId + "/researcher-urls/\\d+"));

        // Read
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        ResearcherUrl gotResearcherUrl = getResponse.getEntity(ResearcherUrl.class);
        assertNotNull(gotResearcherUrl);
        assertNotNull(gotResearcherUrl.getPutCode());
        assertNotNull(gotResearcherUrl.getSource());
        assertNotNull(gotResearcherUrl.getCreatedDate());
        assertNotNull(gotResearcherUrl.getLastModifiedDate());
        assertEquals(this.client1ClientId, gotResearcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://site1.com/" + time, gotResearcherUrl.getUrl().getValue());
        assertEquals(String.valueOf(time), gotResearcherUrl.getUrlName());
        assertEquals("public", gotResearcherUrl.getVisibility().value());

        // Update
        LastModifiedDate initialLastModified = gotResearcherUrl.getLastModifiedDate();
        Long currentTime = System.currentTimeMillis();
        gotResearcherUrl.setUrlName(gotResearcherUrl.getUrlName() + " - " + currentTime);
        gotResearcherUrl.getUrl().setValue(gotResearcherUrl.getUrl().getValue() + currentTime);
        gotResearcherUrl.setVisibility(Visibility.LIMITED);
        ClientResponse updatedResearcherUrlResponse = memberV2ApiClient.updateResearcherUrls(this.user1OrcidId, gotResearcherUrl, accessToken);
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
        ClientResponse deleteResponse = memberV2ApiClient.deleteResearcherUrl(this.user1OrcidId, gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
    }

    @Test
    public void testCantAddDuplicatedResearcherUrl() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        ResearcherUrl rUrlToCreate = new ResearcherUrl();
        rUrlToCreate.setUrl(new Url("http://newurl.com/" + now));
        rUrlToCreate.setUrlName("url-name-" + System.currentTimeMillis());
        rUrlToCreate.setVisibility(Visibility.PUBLIC);

        // Create it
        ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());

        // Add it again
        postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CONFLICT.getStatusCode(), postResponse.getStatus());

        // Check it can be created by other client
        String otherClientToken = getAccessToken(this.client2ClientId, this.client2ClientSecret, this.client2RedirectUri);
        postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, otherClientToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
    }

    @Test
    public void testAddMultipleResearcherUrlAndGetThem() throws InterruptedException, JSONException, URISyntaxException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        Long now = System.currentTimeMillis();
        ResearcherUrl rUrlToCreate = new ResearcherUrl();
        rUrlToCreate.setVisibility(Visibility.PUBLIC);

        for (int i = 0; i < 3; i++) {
            // Change the name
            rUrlToCreate.setUrlName("url-name-" + now + "-" + i);
            rUrlToCreate.setUrl(new Url("http://newurl.com/" + now + "/" + i));
            // Create it
            ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, accessToken);
            assertNotNull(postResponse);
            assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        }

        ClientResponse getAllResponse = memberV2ApiClient.getResearcherUrls(this.user1OrcidId, accessToken);
        assertNotNull(getAllResponse);
        ResearcherUrls researcherUrls = getAllResponse.getEntity(ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getPath());
        assertTrue(researcherUrls.getPath().contains(this.user1OrcidId));
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

        // Clean
        for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            memberV2ApiClient.deletePeerReviewXml(this.user1OrcidId, rUrl.getPutCode(), accessToken);
        }
    }
    
    @Test
    public void testGetWithPublicAPI() {
        ClientResponse getAllResponse = publicV2ApiClient.viewResearcherUrlsXML(user1OrcidId);
        assertNotNull(getAllResponse);
        ResearcherUrls researcherUrls = getAllResponse.getEntity(ResearcherUrls.class);
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        for(ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            assertNotNull(rUrl);
            assertEquals(Visibility.PUBLIC, rUrl.getVisibility());
            ClientResponse theRUrl = publicV2ApiClient.viewResearcherUrlXML(user1OrcidId, String.valueOf(rUrl.getPutCode()));
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
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ResearcherUrl rUrlToCreate = new ResearcherUrl();
        rUrlToCreate.setUrl(new Url(""));
        rUrlToCreate.setUrlName("");
        // Create
        ClientResponse postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        String _351Chars = new String();
        for(int i = 0; i < 531; i++) {
            _351Chars += "a";
        }
        
        rUrlToCreate.setUrl(new Url(_351Chars));
        postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        rUrlToCreate.setUrl(new Url("http://myurl.com"));
        rUrlToCreate.setUrlName(_351Chars);
        postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), postResponse.getStatus());
        
        rUrlToCreate.setUrlName("The name");
        postResponse = memberV2ApiClient.createResearcherUrls(user1OrcidId, rUrlToCreate, accessToken);
        assertNotNull(postResponse);
        assertEquals(Response.Status.CREATED.getStatusCode(), postResponse.getStatus());
        
        // Read it to delete it
        ClientResponse getResponse = memberV2ApiClient.viewLocationXml(postResponse.getLocation(), accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), getResponse.getStatus());
        ResearcherUrl gotResearcherUrl = getResponse.getEntity(ResearcherUrl.class);        
        ClientResponse deleteResponse = memberV2ApiClient.deleteResearcherUrl(this.user1OrcidId, gotResearcherUrl.getPutCode(), accessToken);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());
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
