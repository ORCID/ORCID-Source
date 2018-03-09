package org.orcid.integration.blackbox.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.ws.rs.core.MultivaluedMap;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.OauthAuthorizationPageHelper;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * @author Shobhit Tyagi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class OauthExchangeCodeTest extends BlackBoxBaseV2Release {
    private static final Pattern AUTHORIZATION_CODE_PATTERN = Pattern.compile("code=(.{6})");

    @Resource
    private OauthHelper oauthHelper;

    @Test
    public void pubTokenTest() throws Exception {
        signout();
        String code = getAuthorizationCode();
        ClientResponse tokenResponse = oauthHelper.getResponse(getParamMap(code), APIRequestType.PUBLIC);

        assertEquals(200, tokenResponse.getStatus());
        JSONObject jsonObject = new JSONObject(tokenResponse.getEntity(String.class));
        String token = (String) jsonObject.get("access_token");
        assertFalse(PojoUtil.isEmpty(token));
    }

    @Test
    public void apiTokenTest() throws Exception {
        signout();
        String code = getAuthorizationCode();
        ClientResponse tokenResponse = oauthHelper.getResponse(getParamMap(code), APIRequestType.MEMBER);

        assertEquals(200, tokenResponse.getStatus());
        JSONObject jsonObject = new JSONObject(tokenResponse.getEntity(String.class));
        String token = (String) jsonObject.get("access_token");
        assertFalse(PojoUtil.isEmpty(token));
    }

    @Test
    public void rootTokenTest() throws Exception {
        signout();
        String code = getAuthorizationCode();
        ClientResponse tokenResponse = oauthHelper.getResponse(getParamMap(code), APIRequestType.WEB);

        assertEquals(200, tokenResponse.getStatus());
        JSONObject jsonObject = new JSONObject(tokenResponse.getEntity(String.class));
        String token = (String) jsonObject.get("access_token");
        assertFalse(PojoUtil.isEmpty(token));
    }

    private String getAuthorizationCode() {
        String currentUrl = OauthAuthorizationPageHelper.loginAndAuthorize(this.getWebBaseUrl(), this.getClient1ClientId(), this.getClient1RedirectUri(), "/activities/update", null, this.getUser1UserName(), this.getUser1Password(), true, webDriver);        
        Matcher matcher = AUTHORIZATION_CODE_PATTERN.matcher(currentUrl);
        assertTrue(matcher.find());
        return matcher.group(1);
    }

    public MultivaluedMap<String, String> getParamMap(String authorizationCode) {
        MultivaluedMap<String, String> params = new MultivaluedMapImpl();
        params.add("client_id", this.getClient1ClientId());
        params.add("client_secret", this.getClient1ClientSecret());        
        params.add("redirect_uri", this.getClient1RedirectUri());
        params.add("grant_type", "authorization_code");
        params.add("scope", "/activities/update");
        params.add("code", authorizationCode);
        return params;
    }
}
