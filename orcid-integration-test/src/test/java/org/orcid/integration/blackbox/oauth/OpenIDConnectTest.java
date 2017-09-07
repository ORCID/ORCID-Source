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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.hsqldb.types.Charset;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.orcid.api.common.OauthAuthorizationPageHelper;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
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
        checkJWT(id);
        WebResource webResource;
        Client client = Client.create();        
        
        //get userinfo
        webResource = client.resource(baseUri+"/oauth/userinfo");
        ClientResponse userInfo = webResource
                .header("Authorization", "Bearer "+tokenJSON.getString("access_token"))
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        String userInfoString = userInfo.getEntity(String.class);
        JSONObject user = new JSONObject(userInfoString);
        Assert.assertTrue(user.get("id").toString().startsWith("http"));
        Assert.assertTrue(user.get("id").toString().endsWith("9999-0000-0000-0004"));
        Assert.assertEquals("9999-0000-0000-0004",user.get("sub"));
        Assert.assertEquals("User One Credit name",user.get("name"));
        Assert.assertEquals("One",user.get("family_name"));
        Assert.assertEquals("User",user.get("given_name"));
        
        //test other 'bearer' case
        userInfo = webResource
                .header("Authorization", "bearer "+tokenJSON.getString("access_token"))
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        userInfoString = userInfo.getEntity(String.class);
        user = new JSONObject(userInfoString);
        Assert.assertEquals("9999-0000-0000-0004",user.get("sub"));
    }

    private void checkJWT(String id) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(id);  
        Assert.assertEquals("https://orcid.org",signedJWT.getJWTClaimsSet().getIssuer());
        Assert.assertEquals("9999-0000-0000-0004",signedJWT.getJWTClaimsSet().getSubject());
        Assert.assertEquals("APP-9999999999999901",signedJWT.getJWTClaimsSet().getAudience().get(0));
        Assert.assertEquals("yesMate",signedJWT.getJWTClaimsSet().getClaim("nonce"));   
        Assert.assertEquals("User One Credit name",signedJWT.getJWTClaimsSet().getClaim("name"));
        Assert.assertEquals("One",signedJWT.getJWTClaimsSet().getClaim("family_name"));
        Assert.assertEquals("User",signedJWT.getJWTClaimsSet().getClaim("given_name"));
        
        //get JWKS         
        Client client = Client.create();
        WebResource webResource = client.resource(baseUri+"/oauth/jwks");
        ClientResponse jwksResponse = webResource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        String jwkString = jwksResponse.getEntity(String.class);
        RSAKey jwk = (RSAKey) JWKSet.parse(jwkString).getKeyByKeyId(signedJWT.getHeader().getKeyID());
        
        //check sig
        JWSVerifier verifier = new RSASSAVerifier(jwk);
        Assert.assertTrue(signedJWT.verify(verifier));
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
    
    
    @Test
    public void testImplicitOauth() throws URISyntaxException, ParseException, JOSEException, JSONException{
        //Get id token
        /*
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String scope = "openid";
        */
        HashMap<String,String> requestParams = new HashMap<String,String>();
        requestParams.put("nonce", "yesMate");
        String response = getImplicitTokenResponse(Lists.newArrayList("openid"),requestParams);
        assertTrue(response.contains("#")); //check it's got a fragment
        response = response.replace('#', '?'); //switch to query param for ease of parsing
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(response), "UTF-8");
        Map<String,String> map = new HashMap<String,String>();
        for (NameValuePair pair: params){
            map.put(pair.getName(), pair.getValue());
        }
        //check response
        /*https://localhost:8443/orcid-web/oauth/playground#
        access_token=215b2797-aa3d-4e00-8911-13e933112487&
                token_type=bearer&expires_in=599&
                tokenVersion=1&
                name=User%20One%20Credit%20name&
                orcid=9999-0000-0000-0004&
                persistent=false&
                id_token=eyJraWQiOiJPcGVuSURUZXN0S2V5MSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoiSkg3SVkxMG9vbmxRVHk4YXozY3VPRXZEemlUYnZtZVJyejlFZWIwVkJPQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUE9PSIsImF1ZCI6IkFQUC05OTk5OTk5OTk5OTk5OTAxIiwic3ViIjoiOTk5OS0wMDAwLTAwMDAtMDAwNCIsImF1dGhfdGltZSI6MTUwNDc4MjY0MSwiaXNzIjoiaHR0cHM6XC9cL29yY2lkLm9yZyIsIm5hbWUiOiJVc2VyIE9uZSBDcmVkaXQgbmFtZSIsImV4cCI6MTUwNDc4MzI0MSwiZ2l2ZW5fbmFtZSI6IlVzZXIiLCJpYXQiOjE1MDQ3ODI2NDEsIm5vbmNlIjoieWVzTWF0ZSIsImZhbWlseV9uYW1lIjoiT25lIiwianRpIjoiMzAxMmRlNmItOTIyZC00ZTI3LTkxZDEtNTM3ODMxZjU5ZDVmIn0.LHzMIam4Z-pW-d0tJJLwS8eaK0Jpm9V90qGbBvrUdSWD3M3dAdJyFcQjw1gJiR6Xap-xr-NKB2xxTNfvz1sHZd8sZu02H1ak0A6Y6liwQlRQgvd2HmBnZyrZXdPU1CRTeIR8E_MWtRe5MIffUcvdMOFU36w2Ime0Gdnxq4Nu8M6ipg5CZhO-O3ma5Wtkr53B1ozmgqUh2NaJwNWzhFNyjrhwl9rOZEQbArLH637Gk2rT4gW3TTXz8p7GwiYc_JtVho_OnYADNgbVKw0i9w4lBicswwGHpVF7cOiCOKJ7fuTeU49TN4CMmwT0L6AO1j9bwx7XEUD_GS5Gcd9uhydMXw
                */
        assertEquals(map.get("access_token").length(), 36); //guid length
        assertTrue(map.get("id_token")!=null);
        assertEquals(map.get("token_type"),"bearer");
        assertEquals(map.get("name"),"User One Credit name");
        assertEquals(map.get("orcid"),"9999-0000-0000-0004");
        //check expiry about 10 minutes
        assertTrue((Integer.parseInt(map.get("expires_in")) <= 600));
        assertTrue((Integer.parseInt(map.get("expires_in")) > 590));
        //check id_token
        checkJWT(map.get("id_token"));        
        //check access token works
        Client client = Client.create();
        WebResource webResource = client.resource(baseUri+"/oauth/userinfo");
        ClientResponse userInfo = webResource
                .header("Authorization", "Bearer "+map.get("access_token"))
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        String userInfoString = userInfo.getEntity(String.class);
        JSONObject user = new JSONObject(userInfoString);
        Assert.assertEquals("9999-0000-0000-0004",user.get("sub"));
    }
    
    @Test
    public void testImplicitOauthErrors() throws URISyntaxException {
        HashMap<String,String> requestParams = new HashMap<String,String>();
        requestParams.put("nonce", "noMate");
        
        //check a client without implicit fails
        String clientId = getClient2ClientId();
        String clientRedirectUri = getClient2RedirectUri();
        String userName = getUser1OrcidId();
        String userPassword = getUser1Password();
        List<String> scope = Lists.newArrayList("openid");
        String nope = super.getImplicitTokenResponse(clientId, scope, userName, userPassword, clientRedirectUri, requestParams, "token");
        assertTrue(nope.contains("#")); //check it's got a fragment
        nope = nope.replace('#', '?'); //switch to query param for ease of parsing
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(nope), "UTF-8");
        Map<String,String> map = new HashMap<String,String>();
        for (NameValuePair pair: params){
            map.put(pair.getName(), pair.getValue());
        }
        assertEquals(map.get("error"),"invalid_client");
        assertEquals(map.get("error_description"),"Unauthorized grant type: implicit");
        
        //TODO: check you can't ask for implicit update permissions
        //Behaves weird anyway - check behaviour on live service.
        //String response = getImplicitTokenResponse(Lists.newArrayList("/activities/update"),requestParams);        
        
        /*        String formattedAuthorizationScreen = String.format(OauthAuthorizationPageHelper.authorizationScreenUrlWithCode, baseUri, getClient1ClientId(), "token", "/activites/update", getClient1RedirectUri());
        Client client = Client.create();
        WebResource webResource = client.resource(formattedAuthorizationScreen);
        ClientResponse invalid = webResource.get(ClientResponse.class);
        
        System.out.println(invalid.getEntity(String.class));        
        */
    }
    
    
}
