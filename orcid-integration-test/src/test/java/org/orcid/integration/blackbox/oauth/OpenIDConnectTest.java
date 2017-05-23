package org.orcid.integration.blackbox.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.pub.PublicV2ApiClientImpl;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class OpenIDConnectTest extends BlackBoxBaseV2Release{

    @Resource(name = "publicV2ApiClient")
    PublicV2ApiClientImpl client;
    
    @Before
    public void before() {
        signout();
    }
    
    //client must have openid scope.
    @Test
    public void createLongLivedTokenTest() throws InterruptedException, JSONException, ParseException, URISyntaxException, JOSEException {
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
        JSONObject jsonObject = new JSONObject(body);
        String id = jsonObject.getString("id_token");
        assertNotNull(id);
        SignedJWT signedJWT = SignedJWT.parse(id);  
        Assert.assertEquals("https://orcid.org",signedJWT.getJWTClaimsSet().getIssuer());
        Assert.assertEquals("9999-0000-0000-0004",signedJWT.getJWTClaimsSet().getSubject());
        Assert.assertEquals("APP-9999999999999901",signedJWT.getJWTClaimsSet().getAudience().get(0));
        Assert.assertEquals("yesMate",signedJWT.getJWTClaimsSet().getClaim("nonce"));        

        //get JWKS from https://localhost:8443/orcid-pub-web/v2.0/jwks
        ClientResponse jwksResponse = client.getJWKS();
        String jwkString = jwksResponse.getEntity(String.class);
        RSAKey jwk = (RSAKey) JWK.parse(jwkString);
        
        //check sig
        JWSVerifier verifier = new RSASSAVerifier(jwk);
        Assert.assertTrue(signedJWT.verify(verifier));        
        
        //get userinfo
        ClientResponse userInfo = client.getMe(jsonObject.getString("access_token"));
        String userInfoString = userInfo.getEntity(String.class);
        JSONObject user = new JSONObject(userInfoString);
        Assert.assertEquals("9999-0000-0000-0004",user.get("sub"));
        Assert.assertEquals("User One Credit name",user.get("name"));
        Assert.assertEquals("One",user.get("family_name"));
        Assert.assertEquals("User",user.get("given_name"));
    }
}
