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
package org.orcid.integration.blackbox.oauth;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class OpenIDConnectTest extends BlackBoxBaseV2Release{

    @Resource(name = "publicV2ApiClient")
    PublicV2ApiClientImpl client;
    
    @Value("${org.orcid.web.baseUri}")
    public String baseUri;
    
    @Before
    public void before() {
        signout();
    }
    
    //client must have openid scope.
    @Test
    public void checkIDTokenAndUserInfo() throws InterruptedException, JSONException, ParseException, URISyntaxException, JOSEException {
        //Get id token
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String scope = "openid";
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("nonce", "yesMate");
        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, scope, userId, password, true,params);
        assertNotNull(authorizationCode);
        ClientResponse tokenResponse = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject tokenJSON = new JSONObject(body);
        String id = tokenJSON.getString("id_token");
        assertNotNull(id);
        SignedJWT signedJWT = SignedJWT.parse(id);  
        Assert.assertEquals("https://orcid.org",signedJWT.getJWTClaimsSet().getIssuer());
        Assert.assertEquals("9999-0000-0000-0004",signedJWT.getJWTClaimsSet().getSubject());
        Assert.assertEquals("APP-9999999999999901",signedJWT.getJWTClaimsSet().getAudience().get(0));
        Assert.assertEquals("yesMate",signedJWT.getJWTClaimsSet().getClaim("nonce"));        

        //get JWKS         
        Client client = Client.create();
        WebResource webResource = client.resource(baseUri+"/oauth/jwks");
        ClientResponse jwksResponse = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        String jwkString = jwksResponse.getEntity(String.class);
        RSAKey jwk = (RSAKey) JWKSet.parse(jwkString).getKeyByKeyId(signedJWT.getHeader().getKeyID());
        
        //check sig
        JWSVerifier verifier = new RSASSAVerifier(jwk);
        Assert.assertTrue(signedJWT.verify(verifier));        
        
        //get userinfo
        webResource = client.resource(baseUri+"/oauth/userinfo");
        ClientResponse userInfo = webResource
                .header("Authorization", "Bearer "+tokenJSON.getString("access_token"))
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        String userInfoString = userInfo.getEntity(String.class);
        JSONObject user = new JSONObject(userInfoString);
        Assert.assertEquals("9999-0000-0000-0004",user.get("sub"));
        Assert.assertEquals("User One Credit name",user.get("name"));
        Assert.assertEquals("One",user.get("family_name"));
        Assert.assertEquals("User",user.get("given_name"));
    }
    
    @Test
    public void check403UserInfoWithoutToken() throws JSONException{
        //get userinfo
        Client client = Client.create();
        WebResource webResource = client.resource(baseUri+"/oauth/userinfo");
        ClientResponse userInfo = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals(403,userInfo.getStatus());
    }
    
    @Test
    public void checkNoIDTokenWithoutOpenIDScope() throws InterruptedException, JSONException{
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String scope = "/authenticate";
        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, scope, userId, password, true);
        assertNotNull(authorizationCode);
        ClientResponse tokenResponse = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject jsonObject = new JSONObject(body);        
        assertFalse(jsonObject.has("id_token"));
    }
    
    @Test
    public void testPromptNone() throws InterruptedException{
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String scope = "openid";
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("nonce", "yesMate");
        params.put("prompt", "none");
        try{
            String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, scope, userId, password, true,params);            
            fail();
        }catch(Exception e){
        }
    }
    
    @Test
    public void checkDiscovery() throws JSONException{
        Client client = Client.create();        
        WebResource dWebResource = client.resource(baseUri+"/.well-known/openid-configuration");
        ClientResponse d = dWebResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        Assert.assertEquals(200,d.getStatus());
        JSONObject dObj = d.getEntity(JSONObject.class);
        assertEquals(dObj.get("issuer").toString(),"https://orcid.org");

    }
}
