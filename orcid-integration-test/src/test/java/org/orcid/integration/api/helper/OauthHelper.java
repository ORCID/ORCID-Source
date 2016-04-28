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
package org.orcid.integration.api.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.api.common.WebDriverHelper;
import org.orcid.integration.api.internal.InternalOAuthAPIService;
import org.orcid.integration.api.t1.T1OAuthAPIService;
import org.orcid.integration.api.t2.T2OAuthAPIService;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.pojo.ajaxForm.PojoUtil;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class OauthHelper {

    private WebDriverHelper webDriverHelper;
    
    private InternalOAuthAPIService<ClientResponse> internalClient;
    private T2OAuthAPIService<ClientResponse> oauthT2Client;
    private T1OAuthAPIService<ClientResponse> oauthT1Client;        

    private List<String> items = new ArrayList<String>();
    
    {items.add("enablePersistentToken");}
    
    public void setWebDriverHelper(WebDriverHelper webDriverHelper) {
        this.webDriverHelper = webDriverHelper;
    }

    public T2OAuthAPIService<ClientResponse> getOauthT2Client() {
        return oauthT2Client;
    }
    
    public T1OAuthAPIService<ClientResponse> getOauthT1Client() {
        return oauthT1Client;
    }

    public void setOauthT1Client(T1OAuthAPIService<ClientResponse> oauthT1Client) {
        this.oauthT1Client = oauthT1Client;
    }

    public void setOauthT2Client(T2OAuthAPIService<ClientResponse> oauthT2Client) {
        this.oauthT2Client = oauthT2Client;
    }
    
    public InternalOAuthAPIService<ClientResponse> getInternalClient() {
        return internalClient;
    }

    public void setInternalClient(InternalOAuthAPIService<ClientResponse> internalClient) {
        this.internalClient = internalClient;
    }

    public String obtainAccessToken(String clientId, String clientSecret, String scopes, String email, String password, String redirectUri) throws JSONException, InterruptedException {
        return obtainAccessToken(clientId, clientSecret, scopes, email, password, redirectUri, false);
    }
    
    public String obtainAccessToken(String clientId, String clientSecret, String scopes, String email, String password, String redirectUri, boolean persistent) throws JSONException, InterruptedException {
        String authorizationCode = null;
        authorizationCode = getAuthorizationCode(clientId, scopes, email, password, persistent);        
        assertNotNull(authorizationCode);
        assertFalse(PojoUtil.isEmpty(authorizationCode));              
        ClientResponse tokenResponse = getClientResponse(clientId, clientSecret, scopes, redirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        assertNotNull(accessToken);
        return accessToken;
    }
    
    public String getAuthorizationCode(String clientId, String scopes, String email, String password, boolean persistent) throws InterruptedException {
        return webDriverHelper.obtainAuthorizationCode(scopes, clientId, email, password, items, persistent);
    }
    
    public ClientResponse getClientResponse(String clientId, String clientSecret, String scopes, String redirectUri, String authorizationCode) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "authorization_code");
        if(scopes != null)
            params.add("scope", scopes);
        params.add("redirect_uri", redirectUri);
        params.add("code", authorizationCode);
        return oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
    }
    
    
    public boolean elementExists(String page, String elementId) {
        return webDriverHelper.elementExists(page, elementId);
    } 
    
    public String getClientCredentialsAccessToken(String clientId, String clientSecret, ScopePathType scope) throws JSONException {
        return getClientCredentialsAccessToken(clientId, clientSecret, scope, APIRequestType.MEMBER);
    }
    
    public String getClientCredentialsAccessToken(String clientId, String clientSecret, ScopePathType scope, APIRequestType apiRequerstType) throws JSONException {
        List<ScopePathType> scopes = new ArrayList<ScopePathType>();
        scopes.add(scope);
        ClientResponse clientResponse = getClientCredentialsTokenResponse(clientId, clientSecret, scopes, apiRequerstType);        
        assertEquals(200, clientResponse.getStatus());
        String body = clientResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        String accessToken = (String) jsonObject.get("access_token");
        return accessToken;
    }
    
    public ClientResponse getClientCredentialsTokenResponse(String clientId, String clientSecret, List<ScopePathType> scopes, APIRequestType apiRequerstType) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("grant_type", "client_credentials");
        String scopesString = "";
        if(scopes != null) {
            for(ScopePathType scope : scopes) {
                scopesString += scope.value() + " ";
            }
        }
        params.add("scope", scopesString);
        return getResponse(params, apiRequerstType);     
    }
    
    public String getAccessTokenFromClientResponse(ClientResponse response) throws JSONException {
        String body = response.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        return (String) jsonObject.get("access_token");
    }
    
    public String getErrorFromClientResponse(ClientResponse response) throws JSONException {
        String body = response.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);
        JSONObject errorObject = jsonObject.getJSONObject("error-desc");
        
        return (String) errorObject.get("value");         
    }
    
    public ClientResponse getResponse(MultivaluedMap<String, String> params, APIRequestType apiRequerstType) {
        ClientResponse clientResponse = null;
        switch(apiRequerstType) {
        case INTERNAL:
            clientResponse = internalClient.obtainOauth2TokenPost("client_credentials", params);
            break;
        case MEMBER:
            clientResponse = oauthT2Client.obtainOauth2TokenPost("client_credentials", params);
            break;
        case PUBLIC:
            clientResponse = oauthT1Client.obtainOauth2TokenPost("client_credentials", params);
            break;
        }
        return clientResponse;
    }
    
    public void closeWebDriver() {
        webDriverHelper.close();
    }
}
