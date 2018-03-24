package org.orcid.integration.blackbox.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.OauthAuthorizationPageHelper;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.api.t2.OrcidJerseyT2ClientOAuthConfig;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.claims.AccessTokenHash;
import com.nimbusds.openid.connect.sdk.validators.AccessTokenValidator;
import com.nimbusds.openid.connect.sdk.validators.InvalidHashException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;

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
    public void checkIDTokenAndUserInfo() throws InterruptedException, JSONException, ParseException, URISyntaxException, JOSEException, InvalidHashException {
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
    
    @Test
    public void checkAuthenticateScopeUserInfo() throws InterruptedException, JSONException{
      //Get id token
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String clientSecret = getClient1ClientSecret();
        String userId = getUser1OrcidId();
        String password = getUser1Password();
        String scope = "/authenticate";
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("nonce", "yesMate");
        String authorizationCode = getAuthorizationCode(clientId, clientRedirectUri, scope, userId, password, true,params);
        assertNotNull(authorizationCode);
        ClientResponse tokenResponse = getAccessTokenResponse(clientId, clientSecret, clientRedirectUri, authorizationCode);
        assertEquals(200, tokenResponse.getStatus());
        String body = tokenResponse.getEntity(String.class);
        JSONObject tokenJSON = new JSONObject(body);

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
    }

    private SignedJWT checkJWT(String id) throws ParseException, JOSEException, InvalidHashException {
        SignedJWT signedJWT = SignedJWT.parse(id);  
        Assert.assertEquals("https://orcid.org",signedJWT.getJWTClaimsSet().getIssuer());
        Assert.assertEquals("https://orcid.org/9999-0000-0000-0004",signedJWT.getJWTClaimsSet().getSubject());
        Assert.assertEquals("9999-0000-0000-0004",signedJWT.getJWTClaimsSet().getClaim("id_path"));
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
                
        return signedJWT;
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
        //test prompt none for logged in user
        /*The Authorization Server MUST NOT display any authentication or consent user interface pages. 
         * An error is returned if an End-User is not already authenticated or the Client does not have 
         * pre-configured consent for the requested Claims or does not fulfill other conditions for 
         * processing the request. The error code will typically be login_required, interaction_required.
         */
        HashMap<String,String> params = new HashMap<String,String>();
        params.put("nonce", "yesMate");
        try{
            params.put("prompt", "none");
            String formattedAuthorizationScreen = String.format(OauthAuthorizationPageHelper.authorizationScreenUrlWithCode, baseUri, getClient1ClientId(), "token", "openid", getClient1RedirectUri());
            ClientConfig config = new OrcidJerseyT2ClientOAuthConfig();
            config.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, false);
            Client client = Client.create(config);
            WebResource webResource1 = client.resource(formattedAuthorizationScreen+"&prompt=none");
            ClientResponse noneResponse1 = webResource1.get(ClientResponse.class);
            WebResource webResource2 = client.resource(noneResponse1.getLocation());
            ClientResponse noneResponse2 = webResource2.get(ClientResponse.class);
            assertTrue(noneResponse2.getLocation().toString().contains("oauth/playground?error=login_required"));
        }catch(Exception e){
            throw e;
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
    public void testImplicitOauth() throws URISyntaxException, ParseException, JOSEException, JSONException, InvalidHashException{
        HashMap<String,String> requestParams = new HashMap<String,String>();
        requestParams.put("nonce", "yesMate");
        requestParams.put("state", "Boaty McBoatface");
        String response = getImplicitTokenResponse(Lists.newArrayList("openid"),requestParams,true);
        assertTrue(response.contains("#")); //check it's got a fragment
        response = response.replace('#', '?'); //switch to query param for ease of parsing
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(response), "UTF-8");
        Map<String,String> map = new HashMap<String,String>();
        for (NameValuePair pair: params){
            map.put(pair.getName(), pair.getValue());
        }
        assertEquals(map.get("access_token").length(), 36); //guid length
        assertTrue(map.get("id_token")!=null);
        assertEquals(map.get("token_type"),"bearer");
        assertEquals(map.get("name"),null);
        assertEquals(map.get("orcid"),null);
        assertEquals(map.get("state"),"Boaty McBoatface");
        //check expiry about 10 minutes
        assertTrue((Integer.parseInt(map.get("expires_in")) <= 600));
        assertTrue((Integer.parseInt(map.get("expires_in")) > 590));
        //check id_token
        SignedJWT signedJWT = checkJWT(map.get("id_token"));   
        //check hash
        assertNotNull(signedJWT.getJWTClaimsSet().getClaim("at_hash"));
        AccessTokenValidator.validate(new BearerAccessToken(map.get("access_token")), JWSAlgorithm.RS256, new AccessTokenHash(signedJWT.getJWTClaimsSet().getClaim("at_hash").toString()));

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
    public void testImplicitOauthAuthenticate() throws URISyntaxException, ParseException, JOSEException, JSONException, InvalidHashException{
        HashMap<String,String> requestParams = new HashMap<String,String>();
        requestParams.put("nonce", "yesMate");
        requestParams.put("state", "Boaty McBoatface");
        String response = getImplicitTokenResponse(Lists.newArrayList("/authenticate"),requestParams,true);
        assertTrue(response.contains("#")); //check it's got a fragment
        response = response.replace('#', '?'); //switch to query param for ease of parsing
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(response), "UTF-8");
        Map<String,String> map = new HashMap<String,String>();
        for (NameValuePair pair: params){
            map.put(pair.getName(), pair.getValue());
        }
        assertEquals(map.get("access_token").length(), 36); //guid length
        assertTrue(map.get("id_token")==null);
    }
    
    @Test
    public void testImplicitWithNoAuthorizedGrant() throws URISyntaxException {
        HashMap<String,String> requestParams = new HashMap<String,String>();
        requestParams.put("nonce", "noMate");
        
        //check a client without implicit fails
        String clientId = getClient2ClientId();
        String clientRedirectUri = getClient2RedirectUri();
        String userName = getUser1OrcidId();
        String userPassword = getUser1Password();
        List<String> scope = Lists.newArrayList("openid");
        String nope = super.getImplicitTokenResponse(clientId, scope, userName, userPassword, clientRedirectUri, requestParams, "token",true);
        assertTrue(nope.contains("#")); //check it's got a fragment
        nope = nope.replace('#', '?'); //switch to query param for ease of parsing
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(nope), "UTF-8");
        Map<String,String> map = new HashMap<String,String>();
        for (NameValuePair pair: params){
            map.put(pair.getName(), pair.getValue());
        }
        assertEquals(map.get("error"),"invalid_client");
        assertEquals(map.get("error_description"),"Unauthorized grant type: implicit");         
    }
    
    @Test
    public void testImplicitInvalidScope(){
        //Live service behaviour is that error does not appear until after login.
        //Behaves weird anyway - check behaviour on live service.
        HashMap<String,String> requestParams = new HashMap<String,String>();
        requestParams.put("nonce", "yesMate");
        requestParams.put("state", "Boaty McBoatface");
        
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String formattedAuthorizationScreen = String.format(OauthAuthorizationPageHelper.authorizationScreenUrlWithCode, baseUri, clientId, "token", "/read-limited", clientRedirectUri);
        getWebDriver().get(formattedAuthorizationScreen);
        assertTrue(getWebDriver().getCurrentUrl().contains("error=invalid_scope"));
        
        formattedAuthorizationScreen = String.format(OauthAuthorizationPageHelper.authorizationScreenUrlWithCode, baseUri, clientId, "token", "openid /read-limited", clientRedirectUri);
        getWebDriver().get(formattedAuthorizationScreen);
        assertTrue(getWebDriver().getCurrentUrl().contains("error=invalid_scope"));
    }
    
    @Test
    public void testImplicitForLoggedInUser() throws InterruptedException, URISyntaxException{
        //log in, then send request again.  Should get new token straight away.
        HashMap<String,String> requestParams = new HashMap<String,String>();
        requestParams.put("nonce", "yesMate");
        requestParams.put("state", "Boaty McBoatface");
        String response = getImplicitTokenResponse(Lists.newArrayList("openid"),requestParams,true);    
        
        String clientId = getClient1ClientId();
        String clientRedirectUri = getClient1RedirectUri();
        String formattedAuthorizationScreen = String.format(OauthAuthorizationPageHelper.authorizationScreenUrlWithCode, baseUri, clientId, "token", "openid", clientRedirectUri);
        getWebDriver().get(formattedAuthorizationScreen);
        assertTrue(getWebDriver().getCurrentUrl().contains("#")); //check it's got a fragment
        String url = getWebDriver().getCurrentUrl().replace('#', '?'); //switch to query param for ease of parsing
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(url), "UTF-8");
        Map<String,String> map = new HashMap<String,String>();
        for (NameValuePair pair: params){
            map.put(pair.getName(), pair.getValue());
        }
        assertEquals(map.get("access_token").length(),36);
        assertTrue((map.get("id_token") !=null));
        
        //check prompt = none
        getWebDriver().get(formattedAuthorizationScreen+"&prompt=none");
        assertTrue(getWebDriver().getCurrentUrl().contains("access_token="));

        //check prompt = confirm
        getWebDriver().get(formattedAuthorizationScreen+"&prompt=confirm");
        assertTrue(getWebDriver().getCurrentUrl().contains("oauth/authorize"));
}
    
    public void checkAllowOrigin(){
        Client client = Client.create();
        WebResource webResource = client.resource(baseUri+"/oauth/jwks");
        ClientResponse jwks = webResource
                .header("Origin", "http://example.com")
                .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
        assertEquals(jwks.getHeaders().get("Access-Control-Allow-Origin"),"http://example.com");
    }
    
}
