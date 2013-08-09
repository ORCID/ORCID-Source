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
package org.orcid.api.t2.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.orcid.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@ContextConfiguration(locations = { "classpath:test-oauth-orcid-t2-client-context.xml" })
public abstract class BaseT2OrcidOAuthApiClientIntegrationTest {

    protected String clientSecret;
    protected String clientId;
    protected String groupOrcid;
    protected String orcid;
    protected String accessToken;
    protected String grantType = "client_credentials";

    @Resource
    protected OrcidClientDataHelper orcidClientDataHelper;

    @Resource
    protected T2OAuthAPIService<ClientResponse> oauthT2Client;

    @Resource
    protected Client jerseyClient;

    @Value("${org.orcid.t2.client.base_url}")
    protected URI t2BaseUrl;

    protected void createAccessTokenFromCredentials() throws Exception {
        createAccessTokenFromCredentials(ScopePathType.ORCID_PROFILE_CREATE.value());
    }

    protected void createAccessTokenFromCredentials(String scopes) throws Exception {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", grantType);
        params.add("scope", scopes);
        ClientResponse clientResponse = oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
        assertEquals(200, clientResponse.getStatus());
        String body = clientResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        this.accessToken = (String) jsonObject.get("access_token");
        assertNotNull(this.accessToken);
    }

    @Before
    public void createClientCredentialsAndAccessToken() throws Exception {
        OrcidClientGroup orcidClientGroup = orcidClientDataHelper.createAndPersistClientGroupSingle();
        this.groupOrcid = orcidClientGroup.getGroupOrcid();
        List<OrcidClient> createdClients = orcidClientGroup.getOrcidClient();
        OrcidClient complexityClient = createdClients.get(0);
        this.clientId = complexityClient.getClientId();
        this.clientSecret = complexityClient.getClientSecret();
        createAccessTokenFromCredentials();

    }

    @After
    public void clearOrcid() throws Exception {
        // remove any client data if it exists -- plus each dependant profile
        orcidClientDataHelper.deleteClientId(clientId);
        orcidClientDataHelper.deleteOrcidProfile(groupOrcid);
        // XXX Not sure why I can't delete these guys, but not too worried at
        // this point, because deleting profiles is not part of the app
        // functionality.
        // orcidClientDataHelper.deleteOrcidProfile(orcid);
        // orcidClientDataHelper.deleteOrcidProfile(clientId);

        clientSecret = null;
        clientId = null;
        groupOrcid = null;
        orcid = null;
        accessToken = null;
    }

    protected ClientResponse createNewOrcidUsingAccessToken() throws Exception {
        OrcidMessage profile = orcidClientDataHelper.createFromXML(OrcidClientDataHelper.ORCID_INTERNAL_NO_SPONSOR_XML);
        ClientResponse clientResponse = oauthT2Client.createProfileXML(profile, accessToken);
        // assign orcid any time it's created for use in tear-down
        this.orcid = orcidClientDataHelper.extractOrcidFromResponseCreated(clientResponse);
        return clientResponse;
    }

    protected void assertClientResponse401Details(ClientResponse clientResponse) throws Exception {
        // we've created client details but not tied them to an access token
        assertEquals(401, clientResponse.getStatus());
        assertTrue(clientResponse.getHeaders().containsKey("WWW-Authenticate"));
        List<String> authHeaders = clientResponse.getHeaders().get("WWW-Authenticate");
        assertTrue(authHeaders.contains("Bearer realm=\"ORCID T2 API\", error=\"invalid_token\", error_description=\"Invalid access token: null\""));
    }
}
