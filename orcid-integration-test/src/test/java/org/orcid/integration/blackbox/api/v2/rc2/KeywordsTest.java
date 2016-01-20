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

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
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
public class KeywordsTest extends BlackBoxBase {
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

    /**
     * PRECONDITIONS: The user should have two public keywords "keyword-1"
     * and "keyword-2"
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetKeywordsWihtMembersAPI() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        ClientResponse response = memberV2ApiClient.viewKeywords(user1OrcidId, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keywords keywords = response.getEntity(Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(2, keywords.getKeywords().size());
        assertThat(keywords.getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(keywords.getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(Visibility.PUBLIC, keywords.getKeywords().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, keywords.getKeywords().get(1).getVisibility());
    }

    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Test
    public void testCreateGetUpdateAndDeleteKeyword() throws InterruptedException, JSONException {
        String accessToken = getAccessToken(this.client1ClientId, this.client1ClientSecret, this.client1RedirectUri);
        assertNotNull(accessToken);
        Keyword keyword = new Keyword();
        keyword.setContent("keyword-3");
        keyword.setVisibility(Visibility.PUBLIC);
        //Create
        ClientResponse response = memberV2ApiClient.createKeyword(user1OrcidId, keyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List) map.get("Location");
        String location = resultWithPutCode.get(0).toString();
        Long putCode = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        
        //Get all and verify
        response = memberV2ApiClient.viewKeywords(user1OrcidId, accessToken);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keywords keywords = response.getEntity(Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(3, keywords.getKeywords().size());
        
        
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        
        for(Keyword existingKeyword : keywords.getKeywords()) {
            assertEquals(Visibility.PUBLIC, existingKeyword.getVisibility());
            if(existingKeyword.getContent().equals("keyword-1")) {
                found1 = true;
            } else if(existingKeyword.getContent().equals("keyword-2")) {
                found2 = true;
            } else {
                assertEquals("keyword-3", existingKeyword.getContent());
                assertEquals(client1ClientId, existingKeyword.getSource().retrieveSourcePath());
                found3 = true;
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
               
        //Get it
        response = memberV2ApiClient.viewKeyword(user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        keyword = response.getEntity(Keyword.class);
        assertNotNull(keyword);
        assertNotNull(keyword.getSource());
        assertEquals(client1ClientId, keyword.getSource().retrieveSourcePath());
        assertEquals("keyword-3", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
        
        //Update 
        keyword.setContent("keyword-3-updated");
        response = memberV2ApiClient.updateKeyword(user1OrcidId, keyword, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        response = memberV2ApiClient.viewKeyword(user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        Keyword updatedKeyword = response.getEntity(Keyword.class);
        assertNotNull(updatedKeyword);
        assertEquals("keyword-3-updated", updatedKeyword.getContent());
        assertEquals(keyword.getPutCode(), updatedKeyword.getPutCode());
                
        //Delete
        response = memberV2ApiClient.deleteKeyword(user1OrcidId, putCode, accessToken);
        assertNotNull(response);
        assertEquals(ClientResponse.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        //Check it was deleted
        testGetKeywordsWihtMembersAPI();
    }

    /**
     * PRECONDITIONS: The user should have two public keywords "keyword-1"
     * and "keyword-2"
     * 
     * @throws JSONException
     * @throws InterruptedException
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetKeywordWithPublicAPI() throws InterruptedException, JSONException {
        ClientResponse response = publicV2ApiClient.viewKeywordsXML(user1OrcidId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keywords keywords = response.getEntity(Keywords.class);
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(2, keywords.getKeywords().size());
        assertThat(keywords.getKeywords().get(0).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertThat(keywords.getKeywords().get(1).getContent(), anyOf(is("keyword-1"), is("keyword-2")));
        assertEquals(Visibility.PUBLIC, keywords.getKeywords().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, keywords.getKeywords().get(1).getVisibility());
    }

    public String getAccessToken(String clientId, String clientSecret, String redirectUri) throws InterruptedException, JSONException {
        if (accessTokens.containsKey(clientId)) {
            return accessTokens.get(clientId);
        }

        String accessToken = super.getAccessToken(ScopePathType.PERSON_UPDATE.value() + " " + ScopePathType.READ_LIMITED.value(), clientId, clientSecret, redirectUri);
        accessTokens.put(clientId, accessToken);
        return accessToken;
    }

}
