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
package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.pub.PublicV1ApiClientImpl;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
public class PublicV1Test {
    @Value("${org.orcid.web.base.url:https://localhost:8443/orcid-web}")
    private String webBaseUrl;
    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testUser1.orcidId}")
    public String user1OrcidId;      
    @Value("${org.orcid.web.publicClient1.clientId}")
    public String publicClientId;
    @Value("${org.orcid.web.publicClient1.clientSecret}")
    public String publicClientSecret;

    @Resource
    private PublicV1ApiClientImpl publicV1ApiClient;
    
    @Resource
    private OauthHelper oauthHelper;

    static String accessToken = null;
    
    @Test
    public void testGetInfoWithEmptyToken() throws InterruptedException, JSONException {
        ClientResponse response = publicV1ApiClient.viewRootProfile(user1OrcidId, "");
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OrcidMessage message = response.getEntity(OrcidMessage.class);
        assertNotNull(message);
    }
    
    @Test
    public void testViewPublicProfileAnonymously() throws JSONException, InterruptedException {
        ClientResponse response = publicV1ApiClient.viewRootProfile(user1OrcidId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());    
        OrcidMessage message = response.getEntity(OrcidMessage.class);
        assertNotNull(message);
        assertNotNull(message.getOrcidProfile());
        assertNotNull(message.getOrcidProfile().getOrcidIdentifier());
        assertEquals(user1OrcidId, message.getOrcidProfile().getOrcidIdentifier().getPath());
        
        response = publicV1ApiClient.viewPublicProfile(user1OrcidId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());    
        message = response.getEntity(OrcidMessage.class);
        assertNotNull(message);
        assertNotNull(message.getOrcidProfile());
        assertNotNull(message.getOrcidProfile().getOrcidIdentifier());
        assertEquals(user1OrcidId, message.getOrcidProfile().getOrcidIdentifier().getPath());
        
    }
    
    @Test
    public void testViewPublicProfileUsingToken() throws JSONException, InterruptedException {
        String accessToken = getAccessToken();
        ClientResponse response = publicV1ApiClient.viewRootProfile(user1OrcidId, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());    
        OrcidMessage message = response.getEntity(OrcidMessage.class);
        assertNotNull(message);
        assertNotNull(message.getOrcidProfile());
        assertNotNull(message.getOrcidProfile().getOrcidIdentifier());
        assertEquals(user1OrcidId, message.getOrcidProfile().getOrcidIdentifier().getPath());
        
        response = publicV1ApiClient.viewPublicProfile(user1OrcidId, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());    
        message = response.getEntity(OrcidMessage.class);
        assertNotNull(message);
        assertNotNull(message.getOrcidProfile());
        assertNotNull(message.getOrcidProfile().getOrcidIdentifier());
        assertEquals(user1OrcidId, message.getOrcidProfile().getOrcidIdentifier().getPath());
    }

    @Test
    public void testViewPublicProfileUsingInvalidToken() throws JSONException, InterruptedException {
        String accessToken = getAccessToken();
        accessToken += "X";
        ClientResponse response = publicV1ApiClient.viewRootProfile(user1OrcidId, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());    
        String errorMessage = response.getEntity(String.class);
        assertFalse(PojoUtil.isEmpty(errorMessage));
        assertTrue(errorMessage.contains("invalid_token"));
    }
    
    
    @Test
    public void testPublicSearchAnonymously() {
        ClientResponse response = publicV1ApiClient.doPublicSearch(user1OrcidId);
        assertNotNull(response);
        OrcidMessage orcidMessage = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidSearchResults());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0));
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier());
        assertEquals(user1OrcidId, orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier().getPath());
    }
    
    @Test
    public void testPublicSearchUsingToken() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        ClientResponse response = publicV1ApiClient.doPublicSearch(user1OrcidId, accessToken);
        assertNotNull(response);
        OrcidMessage orcidMessage = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidSearchResults());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0));
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier());
        assertEquals(user1OrcidId, orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier().getPath());
    }
    
    @Test
    public void testPublicSearchUsingPublicClient() throws InterruptedException, JSONException {
        String accessToken = oauthHelper.getClientCredentialsAccessToken(publicClientId, publicClientSecret, ScopePathType.READ_PUBLIC, APIRequestType.PUBLIC);
        ClientResponse response = publicV1ApiClient.doPublicSearch(user1OrcidId, accessToken);
        assertNotNull(response);
        OrcidMessage orcidMessage = response.getEntity(OrcidMessage.class);
        assertNotNull(orcidMessage);
        assertNotNull(orcidMessage.getOrcidSearchResults());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0));
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile());
        assertNotNull(orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier());
        assertEquals(user1OrcidId, orcidMessage.getOrcidSearchResults().getOrcidSearchResult().get(0).getOrcidProfile().getOrcidIdentifier().getPath());
    }
    
    @Test
    public void testPublicSearchUsingInvalidToken() throws InterruptedException, JSONException {
        String accessToken = getAccessToken();
        accessToken += "X";
        ClientResponse response = publicV1ApiClient.doPublicSearch(user1OrcidId, accessToken);
        assertNotNull(response);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());    
        String errorMessage = response.getEntity(String.class);
        assertFalse(PojoUtil.isEmpty(errorMessage));
        assertTrue(errorMessage.contains("invalid_token"));
    }
    
    private String getAccessToken() throws InterruptedException, JSONException {
        if (accessToken == null) {            
            accessToken = oauthHelper.getClientCredentialsAccessToken(client1ClientId, client1ClientSecret, ScopePathType.READ_PUBLIC);            
        }
        return accessToken;
    }

    
}
