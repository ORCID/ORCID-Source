package org.orcid.core.oauth.service;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman (declan) Date: 24/04/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidAuthorizationCodeServiceTest extends DBUnitTest {

    @Resource(name = "orcidAuthorizationCodeService")
    private AuthorizationCodeServices authorizationCodeServices;

    @Resource(name = "profileEntityManager")
    private ProfileEntityManager profileEntityManager;
    
    @Resource(name = "clientDetailsManager")
    private ClientDetailsService clientDetailsService;
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;
    
    private OAuth2RequestFactory oAuth2RequestFactory;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/ClientDetailsEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }
    
    @Before
    public void before() {
        oAuth2RequestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
    }

    @Test
    @Rollback
    @Transactional
    public void testCreateAuthorizationCodeWithValidClient() {
        AuthorizationRequest request = getAuthorizationRequest("4444-4444-4444-4441");
        OAuth2Authentication oauth2Authentication = new OAuth2Authentication(oAuth2RequestFactory.createOAuth2Request(request), getUserAuthentication("0000-0000-0000-0002"));
        String authorizationCode = authorizationCodeServices.createAuthorizationCode(oauth2Authentication);
        assertNotNull(authorizationCode);
        oauth2Authentication  = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
        assertNotNull(oauth2Authentication);
    }

    @Test(expected = InvalidGrantException.class)
    @Rollback
    @Transactional
    public void testConsumeNonExistentCode() {
        authorizationCodeServices.consumeAuthorizationCode("bodus-code!");
    }

    @Test(expected = InvalidClientException.class)
    @Rollback
    @Transactional
    public void testCreateAuthorizationCodeWithInvalidClient() {
        AuthorizationRequest request = getAuthorizationRequest("6444-4444-4444-4441");        
        OAuth2Authentication auth = new OAuth2Authentication(oAuth2RequestFactory.createOAuth2Request(request), getUserAuthentication("0000-0000-0000-0002"));
        authorizationCodeServices.createAuthorizationCode(auth);
    }

    public AuthorizationRequest getAuthorizationRequest(String clientId) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        Set<String> resourceIds = new HashSet<>();
        resourceIds.add("orcid");
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(OAuth2Utils.CLIENT_ID, clientId);
        params.put(OAuth2Utils.SCOPE, "a-scope");
        
        AuthorizationRequest authorizationRequest = oAuth2RequestFactory.createAuthorizationRequest(params);
        authorizationRequest.setAuthorities(grantedAuthorities);
        authorizationRequest.setResourceIds(resourceIds);
        
        return authorizationRequest;
    }
    
    private Authentication getUserAuthentication(String orcid) {
        OrcidProfileUserDetails details = (OrcidProfileUserDetails) orcidUserDetailsService.loadUserByUsername(orcid);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details.getOrcid(), "password");
        auth.setDetails(details);
        return auth;
    }
}
