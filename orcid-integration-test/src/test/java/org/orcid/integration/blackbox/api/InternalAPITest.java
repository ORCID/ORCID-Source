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

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.api.internal.InternalOAuthOrcidApiClientImpl;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class InternalAPITest {

    @Value("${org.orcid.web.testClient1.redirectUri}")
    private String redirectUri;
    @Value("${org.orcid.web.testClient1.clientId}")
    public String client1ClientId;
    @Value("${org.orcid.web.testClient1.clientSecret}")
    public String client1ClientSecret;
    @Value("${org.orcid.web.testUser1.orcidId}")
    protected String user1OrcidId;
    @Value("${org.orcid.web.member.id}")
    public String memberId;
    
    @Resource
    private OauthHelper oauthHelper;
    
    @Resource
    protected InternalOAuthOrcidApiClientImpl internalApiClient;

    @Test
    public void testGetTokenForInternalScopes() throws JSONException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", client1ClientId);
        params.add("client_secret", client1ClientSecret);
        params.add("grant_type", "client_credentials");
        params.add("scope", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED.value());
        ClientResponse clientResponse = oauthHelper.getResponse(params, APIRequestType.INTERNAL);
        assertNotNull(clientResponse);     
        assertEquals(Response.Status.OK.getStatusCode(), clientResponse.getStatus());
        String body = clientResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
    }
    
    @Test
    public void testGetTokenForInternalScopesFailForPublicAPI() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", client1ClientId);
        params.add("client_secret", client1ClientSecret);
        params.add("grant_type", "client_credentials");
        params.add("scope", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED.value());
        ClientResponse clientResponse = oauthHelper.getResponse(params, APIRequestType.PUBLIC);
        assertNotNull(clientResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), clientResponse.getStatus());
    }
    
    @Test
    public void testGetTokenForInternalScopesFailForMembersAPI() {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", client1ClientId);
        params.add("client_secret", client1ClientSecret);
        params.add("grant_type", "client_credentials");
        params.add("scope", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED.value());
        ClientResponse clientResponse = oauthHelper.getResponse(params, APIRequestType.MEMBER);
        assertNotNull(clientResponse);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), clientResponse.getStatus());
    }
    
    @Test
    public void testGetLastModifiedDate() throws JSONException {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", client1ClientId);
        params.add("client_secret", client1ClientSecret);
        params.add("grant_type", "client_credentials");
        params.add("scope", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED.value());
        ClientResponse clientResponse = oauthHelper.getResponse(params, APIRequestType.INTERNAL);
        assertNotNull(clientResponse);     
        assertEquals(Response.Status.OK.getStatusCode(), clientResponse.getStatus());
        String body = clientResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        assertFalse(PojoUtil.isEmpty(accessToken));
        
        ClientResponse lastModifiedResponse = internalApiClient.viewPersonLastModified(user1OrcidId, accessToken);
        assertNotNull(lastModifiedResponse);
        String lastModified = lastModifiedResponse.getEntity(String.class);
        jsonObject = new JSONObject(lastModified);
        assertNotNull(jsonObject);
        assertEquals(user1OrcidId, (String)jsonObject.getString("orcid"));
        assertNotNull((String)jsonObject.getString("last-modified"));                
    }
    
    @Test
    public void testGetMemberInfo() {
        ClientResponse response = internalApiClient.viewMemberDetails(memberId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        response = internalApiClient.viewMemberDetails("invalid name");
        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        OrcidError error = response.getEntity(OrcidError.class);
        assertNotNull(error);
        assertEquals(new Integer(0), error.getErrorCode());
        assertEquals("Member id or name not found for: invalid name", error.getDeveloperMessage());
    }
}
