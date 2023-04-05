package org.orcid.core.oauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.oauth.OrcidOauth2ClientAuthentication;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.oauth.OrcidRandomValueTokenServices;
import org.orcid.core.togglz.Features;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.togglz.junit.TogglzRule;

/**
 * 
 * @author Will Simpson
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-core-context.xml" })
public class OrcidRandomValueTokenServicesTest extends DBUnitTest {

    @Resource
    private OrcidRandomValueTokenServices tokenServices;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;

    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrcidOauth2AuthorisationDetailsData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrcidOauth2AuthorisationDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SubjectEntityData.xml"));
    }
    
    @Test
    public void testCreateReadLimitedAccessToken() {
        Date earliestExpiry = oneHoursTime();        
        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(OAuth2Utils.CLIENT_ID, clientId);
        authorizationParameters.put(OAuth2Utils.SCOPE, "/orcid-profile/read-limited");
        OAuth2Request request = new OAuth2Request(Collections.<String, String> emptyMap(), clientId, Collections.<GrantedAuthority> emptyList(), true, new HashSet<String>(Arrays.asList("/orcid-profile/read-limited")), Collections.<String> emptySet(), null, Collections.<String> emptySet(), Collections.<String, Serializable> emptyMap());
                
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);

        Date latestExpiry = oneHoursTime();

        assertNotNull(oauth2AccessToken);
        assertFalse(oauth2AccessToken.getExpiration().before(earliestExpiry));
        assertFalse(oauth2AccessToken.getExpiration().after(latestExpiry));                       
    }

    @Test
    public void testCreateAddWorkAccessToken() {
        Date earliestExpiry = oneHoursTime();

        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(OAuth2Utils.CLIENT_ID, clientId);
        authorizationParameters.put(OAuth2Utils.SCOPE, "/orcid-works/create");
        OAuth2Request request = new OAuth2Request(Collections.<String, String> emptyMap(), clientId, Collections.<GrantedAuthority> emptyList(), true, new HashSet<String>(Arrays.asList("/orcid-profile/read-limited")), Collections.<String> emptySet(), null, Collections.<String> emptySet(), Collections.<String, Serializable> emptyMap());
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);

        Date latestExpiry = oneHoursTime();

        assertNotNull(oauth2AccessToken);
        assertFalse(oauth2AccessToken.getExpiration().before(earliestExpiry));
        assertFalse(oauth2AccessToken.getExpiration().after(latestExpiry));
    }

    @Test
    public void testReissuedAccessTokenHasUpdatedExpiration() throws InterruptedException {
        Date earliestExpiry = oneHoursTime();
        
        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(OAuth2Utils.CLIENT_ID, clientId);
        authorizationParameters.put(OAuth2Utils.SCOPE, "/orcid-works/create");
        OAuth2Request request = new OAuth2Request(Collections.<String, String> emptyMap(), clientId, Collections.<GrantedAuthority> emptyList(), true, new HashSet<String>(Arrays.asList("/orcid-profile/read-limited")), Collections.<String> emptySet(), null, Collections.<String> emptySet(), Collections.<String, Serializable> emptyMap());
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);

        Date latestExpiry = oneHoursTime();

        assertNotNull(oauth2AccessToken);
        assertFalse(oauth2AccessToken.getExpiration().before(earliestExpiry));
        assertFalse(oauth2AccessToken.getExpiration().after(latestExpiry));

        Thread.sleep(1000);
        earliestExpiry = oneHoursTime();
        
        OAuth2AccessToken reissuedOauth2AccessToken = tokenServices.createAccessToken(authentication);

        latestExpiry = oneHoursTime();

        assertNotNull(reissuedOauth2AccessToken);

        assertFalse(reissuedOauth2AccessToken.getExpiration().before(earliestExpiry));
        assertFalse(reissuedOauth2AccessToken.getExpiration().after(latestExpiry));
    }

    private Date twentyYearsTime() {
        Calendar earliestExpiry = new GregorianCalendar();
        // This is roughly 2 years in seconds - used in the implementation, but
        // not sure how was calculated now.
        earliestExpiry.add(Calendar.SECOND, 631138519);
        return earliestExpiry.getTime();
    }

    private Date oneHoursTime() {
        Calendar earliestExpiry = new GregorianCalendar();
        earliestExpiry.add(Calendar.HOUR, 1);
        return earliestExpiry.getTime();
    }    
    
    @Test
    public void invalidTokenThrowsInvalidTokenExceptionTest() {
        // Mock request attributes
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestAttributes attrs = new ServletRequestAttributes(mockHttpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);
        String invalidTokenValue = "invalid";
        
        mockHttpServletRequest.setMethod(RequestMethod.GET.name());
        catchInvalidTokenExceptionOnLoadAuthentication(invalidTokenValue, "Invalid access token: invalid");
        
        mockHttpServletRequest.setMethod(RequestMethod.POST.name());
        catchInvalidTokenExceptionOnLoadAuthentication(invalidTokenValue, "Invalid access token: invalid");
        
        mockHttpServletRequest.setMethod(RequestMethod.PUT.name());
        catchInvalidTokenExceptionOnLoadAuthentication(invalidTokenValue, "Invalid access token: invalid");
        
        mockHttpServletRequest.setMethod(RequestMethod.DELETE.name());
        catchInvalidTokenExceptionOnLoadAuthentication(invalidTokenValue, "Invalid access token: invalid");
    }
            
    /**
     * Check that the token created with a non persistent code will expire within an hour 
     * */
    @Test
    public void tokenExpireInAnHourTest() throws InterruptedException {
        Map<String, String> authorizationParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        authorizationParameters.put(OAuth2Utils.CLIENT_ID, clientId);
        authorizationParameters.put(OAuth2Utils.SCOPE, "/orcid-works/create");
        authorizationParameters.put("code", "code2");
        
        OAuth2Request request = new OAuth2Request(Collections.<String, String> emptyMap(), clientId, Collections.<GrantedAuthority> emptyList(), true, new HashSet<String>(Arrays.asList("/orcid-profile/read-limited")), Collections.<String> emptySet(), null, Collections.<String> emptySet(), Collections.<String, Serializable> emptyMap());
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);
                
        Date tokenExpiration = oauth2AccessToken.getExpiration();
        Thread.sleep(2000);
        
        //The token expires in less than one hour
        assertFalse(tokenExpiration.after(oneHoursTime()));        
    }

    /**
     * Check that the token created with a persistent code will expire within 20 years
     * */
    @Test
    public void tokenExpireIn20YearsTest() throws InterruptedException {
        Date in20years = twentyYearsTime();
        
        Thread.sleep(2000);
        
        Map<String, String> requestParameters = new HashMap<>();
        String clientId = "4444-4444-4444-4441";
        requestParameters.put(OAuth2Utils.CLIENT_ID, clientId);
        requestParameters.put(OAuth2Utils.SCOPE, "/orcid-works/create");
        requestParameters.put("code", "code1");
        requestParameters.put(OrcidOauth2Constants.IS_PERSISTENT, "true");
        
        OAuth2Request request = new OAuth2Request(requestParameters, clientId, Collections.<GrantedAuthority> emptyList(), true, new HashSet<String>(Arrays.asList("/orcid-profile/read-limited")), Collections.<String> emptySet(), null, Collections.<String> emptySet(), Collections.<String, Serializable> emptyMap());
        ClientDetailsEntity clientDetails = clientDetailsManager.findByClientId(clientId);
        Authentication userAuthentication = new OrcidOauth2ClientAuthentication(clientDetails);
        OAuth2Authentication authentication = new OAuth2Authentication(request, userAuthentication);
        OAuth2AccessToken oauth2AccessToken = tokenServices.createAccessToken(authentication);
        
        
        Date tokenExpiration = oauth2AccessToken.getExpiration();
        
        //The token expires in 20 years
        assertFalse(in20years.after(tokenExpiration));
        
        in20years = twentyYearsTime();
        
        //Confirm the token expires in 20 years
        assertFalse(tokenExpiration.after(in20years));
    }                            
        
    @Test    
    public void expiredTokenDoesntWorkOnGetPostPutWithTogglzOnTest() {
        // Mock request attributes  
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestAttributes attrs = new ServletRequestAttributes(mockHttpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);
        
        // Check GET requests fail
        checkAuthenticationFailsOnExpiredTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.GET);
        // Check POST requests fail
        checkAuthenticationFailsOnExpiredTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.POST);
        // Check PUT requests fail
        checkAuthenticationFailsOnExpiredTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.PUT);               
    }
    
    private void checkAuthenticationFailsOnExpiredTokenWithRequestMethod(MockHttpServletRequest mockHttpServletRequest, RequestMethod rm) {
        mockHttpServletRequest.setMethod(rm.name());        
        
        OrcidOauth2TokenDetail expiredToken = buildExpiredToken("token-value-" + rm.name());        
        expiredToken = buildExpiredToken("token-value-2-" + rm.name());        
        orcidOauthTokenDetailService.createNew(expiredToken);
        
        // The first time we try to use it, we get a InvalidTokenException with message Access token expired: token-value
        catchInvalidTokenExceptionOnLoadAuthentication("token-value-2-" + rm.name(), "Access token expired: token-value-2-" + rm.name());
                
        // Second time we try to use it, we get a InvalidTokenException with message Invalid access token: token-value
        catchInvalidTokenExceptionOnLoadAuthentication("token-value-2-" + rm.name(), "Invalid access token: token-value-2-" + rm.name());      
    }        
            
    @Test
    public void disabedTokenDoesntWorkOnGetPostPutTest() {
        // Mock request attributes  
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestAttributes attrs = new ServletRequestAttributes(mockHttpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);
        
        // All GET requests should fail, regardless of the RevokeReason
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.GET, RevokeReason.AUTH_CODE_REUSED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.GET, RevokeReason.CLIENT_REVOKED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.GET, RevokeReason.RECORD_DEACTIVATED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.GET, RevokeReason.STAFF_REVOKED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.GET, RevokeReason.USER_REVOKED);

        // All POST requests should fail, regardless of the RevokeReason
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.POST, RevokeReason.AUTH_CODE_REUSED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.POST, RevokeReason.CLIENT_REVOKED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.POST, RevokeReason.RECORD_DEACTIVATED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.POST, RevokeReason.STAFF_REVOKED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.POST, RevokeReason.USER_REVOKED);
        
        // All PUT requests should fail, regardless of the RevokeReason
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.PUT, RevokeReason.AUTH_CODE_REUSED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.PUT, RevokeReason.CLIENT_REVOKED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.PUT, RevokeReason.RECORD_DEACTIVATED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.PUT, RevokeReason.STAFF_REVOKED);
        checkAuthenticationFailsOnDisabledTokenWithRequestMethod(mockHttpServletRequest, RequestMethod.PUT, RevokeReason.USER_REVOKED);        
    }
    
    private void checkAuthenticationFailsOnDisabledTokenWithRequestMethod(MockHttpServletRequest mockHttpServletRequest, RequestMethod rm, RevokeReason revokeReason) {
        mockHttpServletRequest.setMethod(rm.name());        
        
        OrcidOauth2TokenDetail disabledToken = buildDisabledToken("token-value-" + rm.name() + revokeReason.name(), revokeReason);        
        disabledToken = buildDisabledToken("token-value-2-" + rm.name() + revokeReason.name(), revokeReason);        
        orcidOauthTokenDetailService.createNew(disabledToken);
        
        catchInvalidTokenExceptionOnLoadAuthentication("token-value-2-" + rm.name(), "Invalid access token: token-value-2-" + rm.name());    
    }
    
    @Test
    public void disabledTokenOnDeleteWithTogglzOnTest() {
        // Mock request attributes  
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        RequestAttributes attrs = new ServletRequestAttributes(mockHttpServletRequest);
        RequestContextHolder.setRequestAttributes(attrs);
        mockHttpServletRequest.setMethod(RequestMethod.DELETE.name());
        
        ///////////////////////////////
        // Active tokens should work //
        ///////////////////////////////
        String activeTokenValue = "active-token-" + Math.random(); 
        OrcidOauth2TokenDetail activeToken = buildToken(activeTokenValue, "/activites-update");
        orcidOauthTokenDetailService.createNew(activeToken);
        try {
            tokenServices.loadAuthentication(activeTokenValue);
        } catch(Exception e) {
            fail(e.getMessage());
        }
        
        //////////////////////////////
        // USER_REVOKED should work //
        //////////////////////////////
        String tokenValue = "token-value-" + RequestMethod.DELETE + RevokeReason.USER_REVOKED;
        OrcidOauth2TokenDetail userRevokedDisabledToken = buildDisabledToken(tokenValue, RevokeReason.USER_REVOKED);        
        orcidOauthTokenDetailService.createNew(userRevokedDisabledToken);
        
        try {
            tokenServices.loadAuthentication(tokenValue);
        } catch(Exception e) {            
            fail(e.getMessage());
        }
        
        ///////////////////////////
        // All other should fail //
        ///////////////////////////
        disabledTokensOnDeleteShouldAllwaysFailIfTheRevokeReasonIsNotUserRevoked();       
    }
    
    private void disabledTokensOnDeleteShouldAllwaysFailIfTheRevokeReasonIsNotUserRevoked() {
        // AUTH_CODE_REUSED should fail
        String tokenValue = "token-value-" + Math.random() + "-" + RequestMethod.DELETE + RevokeReason.AUTH_CODE_REUSED;
        OrcidOauth2TokenDetail disabledToken = buildDisabledToken(tokenValue, RevokeReason.AUTH_CODE_REUSED);        
        orcidOauthTokenDetailService.createNew(disabledToken);
        
        try {
            tokenServices.loadAuthentication(tokenValue);
            fail();
        } catch(InvalidTokenException e) {
            assertEquals("Invalid access token: " + tokenValue + ", revoke reason: AUTH_CODE_REUSED", e.getMessage());            
        }
        
        // CLIENT_REVOKED should fail
        tokenValue = "token-value-" + Math.random() + "-" + RequestMethod.DELETE + RevokeReason.CLIENT_REVOKED;
        disabledToken = buildDisabledToken(tokenValue, RevokeReason.CLIENT_REVOKED);        
        orcidOauthTokenDetailService.createNew(disabledToken);
        
        try {
            tokenServices.loadAuthentication(tokenValue);
            fail();
        } catch(InvalidTokenException e) {
            assertEquals("Invalid access token: " + tokenValue + ", revoke reason: CLIENT_REVOKED", e.getMessage());            
        }
        
        // RECORD_DEACTIVATED should fail
        tokenValue = "token-value-" + Math.random() + "-" + RequestMethod.DELETE + RevokeReason.RECORD_DEACTIVATED;
        disabledToken = buildDisabledToken(tokenValue, RevokeReason.RECORD_DEACTIVATED);        
        orcidOauthTokenDetailService.createNew(disabledToken);
        
        try {
            tokenServices.loadAuthentication(tokenValue);
            fail();
        } catch(InvalidTokenException e) {
            assertEquals("Invalid access token: " + tokenValue + ", revoke reason: RECORD_DEACTIVATED", e.getMessage());            
        }
        
        // STAFF_REVOKED should fail
        tokenValue = "token-value-"  + Math.random() + "-" + RequestMethod.DELETE + RevokeReason.STAFF_REVOKED;
        disabledToken = buildDisabledToken(tokenValue, RevokeReason.STAFF_REVOKED);        
        orcidOauthTokenDetailService.createNew(disabledToken);
        
        try {
            tokenServices.loadAuthentication(tokenValue);
            fail();
        } catch(InvalidTokenException e) {
            assertEquals("Invalid access token: " + tokenValue + ", revoke reason: STAFF_REVOKED", e.getMessage());            
        }
    }
    
    /**
     * Load authentication using a persistent token
     * */
    @Test
    public void loadAuthenticationWithPersistentTokenTest() {
        try {
            OAuth2Authentication result = tokenServices.loadAuthentication("persistent-token-2");
            assertNotNull(result);
        } catch(Exception e) {
            fail();
        }               
    }    
    
    private void catchInvalidTokenExceptionOnLoadAuthentication(String invalidTokenValue, String expectedMessage) {
        try {
            tokenServices.loadAuthentication(invalidTokenValue);
            fail("Invalid access token must fail");
        } catch (InvalidTokenException i) {
            assertEquals(expectedMessage, i.getMessage());
        } catch (Exception e) {
            fail("Invalid exception found: " + e.getCause());
        }
    }
    
    private OrcidOauth2TokenDetail buildExpiredToken(String tokenValue) {
        return buildExpiredOrDisabledToken(tokenValue, true, null);
    }
    
    private OrcidOauth2TokenDetail buildDisabledToken(String tokenValue, RevokeReason revokeReason) {
        return buildExpiredOrDisabledToken(tokenValue, false, revokeReason);
    }
    
    private OrcidOauth2TokenDetail buildExpiredOrDisabledToken(String tokenValue, Boolean buildExpired, RevokeReason revokeReason) {
        OrcidOauth2TokenDetail token = buildToken(tokenValue, "/read-limited");
        if(buildExpired) {
            token.setTokenExpiration(new Date(System.currentTimeMillis() - 60000));
        } else {
            token.setTokenExpiration(new Date(System.currentTimeMillis() + 60000));
            token.setTokenDisabled(true);
            token.setRevokeReason(revokeReason.name());
            token.setRevocationDate(new Date());
        }        
        return token;
    }
    
    private OrcidOauth2TokenDetail buildToken(String tokenValue, String scope) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(true);
        token.setAuthenticationKey("authentication-key");
        token.setClientDetailsId("4444-4444-4444-4441");        
        token.setOrcid("4444-4444-4444-4442");
        token.setResourceId("orcid");
        token.setScope(scope);
        token.setTokenValue(tokenValue);
        return token;
    }
}
