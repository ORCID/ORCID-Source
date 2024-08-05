package org.orcid.core.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.constants.RevokeReason;
import org.orcid.core.exception.ClientDeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.persistence.dao.MemberOBOWhitelistedClientDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.MemberOBOWhitelistedClientEntity;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenRequest;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

public class IETFExchangeTokenGranterTest {

    private static final String GRANT_TYPE = OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE;

    private static final String ORCID = "0000-0000-0000-0000";

    private static final String AUDIENCE_CLIENT_ID = "AUD_CLIENT";

    private static final String ACTIVE_CLIENT_ID = "ACTIVE";

    @Mock
    private OrcidRandomValueTokenServices tokenServicesMock;

    @Mock
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManagerMock;

    @Mock
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidatorMock;

    @Mock
    private OpenIDConnectKeyService openIDConnectKeyServiceMock;

    @Mock
    private ClientDetailsEntity activeClientMock;

    @Mock
    private MemberOBOWhitelistedClientDao memberOBOWhitelistedClientDaoMock;

    @Mock
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailServiceMock;

    @Mock
    private ProfileEntityManager profileEntityManagerMock;

    @Mock
    private ProfileDao profileDaoMock;

    private IETFExchangeTokenGranter tokenGranter;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        ClientDetailsEntity lockedClient = new ClientDetailsEntity("LOCKED");
        ClientDetailsEntity deactivatedClient = new ClientDetailsEntity("DEACTIVATED");

        when(activeClientMock.getId()).thenReturn(ACTIVE_CLIENT_ID);
        when(activeClientMock.getClientId()).thenReturn(ACTIVE_CLIENT_ID);
        when(activeClientMock.getAuthorizedGrantTypes()).thenReturn(Set.of(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE));

        // For sake of testing, ACTIVE and AUD_CLIENT could be the same mock
        when(clientDetailsEntityCacheManagerMock.retrieve(ACTIVE_CLIENT_ID)).thenReturn(activeClientMock);
        when(clientDetailsEntityCacheManagerMock.retrieve(AUDIENCE_CLIENT_ID)).thenReturn(activeClientMock);

        when(clientDetailsEntityCacheManagerMock.retrieve("LOCKED")).thenReturn(lockedClient);
        when(clientDetailsEntityCacheManagerMock.retrieve("DEACTIVATED")).thenReturn(deactivatedClient);

        doThrow(LockedException.class).when(orcidOAuth2RequestValidatorMock).validateClientIsEnabled(eq(lockedClient));
        doThrow(ClientDeactivatedException.class).when(orcidOAuth2RequestValidatorMock).validateClientIsEnabled(eq(deactivatedClient));

        when(openIDConnectKeyServiceMock.verify(any())).thenReturn(true);

        ClientDetailsEntity oboClient = new ClientDetailsEntity(AUDIENCE_CLIENT_ID);
        MemberOBOWhitelistedClientEntity e = new MemberOBOWhitelistedClientEntity();
        e.setWhitelistedClientDetailsEntity(oboClient);

        List<MemberOBOWhitelistedClientEntity> oboClients = List.of(e);

        when(memberOBOWhitelistedClientDaoMock.getWhitelistForClient(ACTIVE_CLIENT_ID)).thenReturn(oboClients);

        OrcidOauth2TokenDetail token1 = getOrcidOauth2TokenDetail(true, "/read-limited", System.currentTimeMillis() + 60000, false);

        when(orcidOauthTokenDetailServiceMock.findByClientIdAndUserName(any(), any())).thenReturn(List.of(token1));

        ProfileEntity profile = new ProfileEntity(ORCID);
        when(profileEntityManagerMock.findByOrcid(eq(ORCID))).thenReturn(profile);

        OrcidGrantedAuthority oga = new OrcidGrantedAuthority();
        oga.setAuthority("ROLE_USER");
        oga.setOrcid(ORCID);
        when(profileDaoMock.getGrantedAuthoritiesForProfile(eq(ORCID))).thenReturn(List.of(oga));

        // Active token
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("");
        token.setAdditionalInformation(new HashMap<String, Object>());
        token.setExpiration(new Date(System.currentTimeMillis() + 60000));
        token.setScope(Set.of());
        token.setTokenType("");
        token.setValue("");

        when(tokenServicesMock.createAccessToken(any())).thenReturn(token);

        // Disabled token
        DefaultOAuth2AccessToken disabledToken = new DefaultOAuth2AccessToken("");
        disabledToken.setAdditionalInformation(new HashMap<String, Object>());
        disabledToken.setExpiration(new Date(System.currentTimeMillis() + 60000));
        disabledToken.setScope(Set.of());
        disabledToken.setTokenType("");
        disabledToken.setValue("");

        when(tokenServicesMock.createRevokedAccessToken(any(), any())).thenReturn(disabledToken);

        tokenGranter = new IETFExchangeTokenGranter(tokenServicesMock);

        TargetProxyHelper.injectIntoProxy(tokenGranter, "clientDetailsEntityCacheManager", clientDetailsEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "orcidOAuth2RequestValidator", orcidOAuth2RequestValidatorMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "openIDConnectKeyService", openIDConnectKeyServiceMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "memberOBOWhitelistedClientDao", memberOBOWhitelistedClientDaoMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "orcidOauthTokenDetailService", orcidOauthTokenDetailServiceMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "profileEntityManager", profileEntityManagerMock);
        TargetProxyHelper.injectIntoProxy(tokenGranter, "profileDao", profileDaoMock);
    }

    @Test(expected = ClientDeactivatedException.class)
    public void grantDeactivatedClientTest() throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        tokenGranter.grant(GRANT_TYPE, getTokenRequest("DEACTIVATED", List.of("/read-limited")));
    }

    @Test(expected = LockedException.class)
    public void grantLockedClientTest() throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        tokenGranter.grant(GRANT_TYPE, getTokenRequest("LOCKED", List.of("/read-limited")));
    }

    @Test
    public void grantMissingRequestParamsTest() {
        String clientId = ACTIVE_CLIENT_ID;
        List<String> scope = List.of("/read-limited");

        try {
            // Missing token
            Map<String, String> requestParameters = new HashMap<String, String>();
            requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:id_token");
            requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:access_token");
            TokenRequest t1 = new TokenRequest(requestParameters, clientId, scope, GRANT_TYPE);
            tokenGranter.grant(GRANT_TYPE, t1);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Missing IETF Token exchange request parameter(s).  Required: subject_token subject_token_type requested_token_type", iae.getMessage());
        } catch (Exception e) {
            fail();
        }

        try {
            // Missing subject token type
            Map<String, String> requestParameters = new HashMap<String, String>();
            requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN, buildJWTToken(false));
            requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:access_token");
            TokenRequest t2 = new TokenRequest(requestParameters, clientId, scope, GRANT_TYPE);
            tokenGranter.grant(GRANT_TYPE, t2);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Missing IETF Token exchange request parameter(s).  Required: subject_token subject_token_type requested_token_type", iae.getMessage());
        } catch (Exception e) {
            fail();
        }

        try {
            // Missing requested token type
            Map<String, String> requestParameters = new HashMap<String, String>();
            requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN, buildJWTToken(false));
            requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:id_token");
            TokenRequest t3 = new TokenRequest(requestParameters, clientId, scope, GRANT_TYPE);
            tokenGranter.grant(GRANT_TYPE, t3);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Missing IETF Token exchange request parameter(s).  Required: subject_token subject_token_type requested_token_type", iae.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void grantClientDoesntHaveRequiredGrantTest() {
        // Remove the required grant type
        when(activeClientMock.getAuthorizedGrantTypes()).thenReturn(Set.of(OrcidOauth2Constants.GRANT_TYPE_CLIENT_CREDENTIALS));
        try {
            tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/read-limited")));
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Client does not have urn:ietf:params:oauth:grant-type:token-exchange enabled", iae.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void grantClientNotOboWhitelistedTest() {
        try {
            ClientDetailsEntity oboClient = new ClientDetailsEntity("OTHER_CLIENT");
            MemberOBOWhitelistedClientEntity e = new MemberOBOWhitelistedClientEntity();
            e.setWhitelistedClientDetailsEntity(oboClient);
            when(memberOBOWhitelistedClientDaoMock.getWhitelistForClient(AUDIENCE_CLIENT_ID)).thenReturn(List.of(e));
            tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/read-limited")));
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Client ACTIVE cannot act on behalf of client AUD_CLIENT", iae.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void grantRequestingAndOBOClientMustNotBeTheSameClientTest() throws JOSEException, NoSuchAlgorithmException, IOException, ParseException, URISyntaxException {
        Builder claims = new JWTClaimsSet.Builder();
        claims.claim("aud", AUDIENCE_CLIENT_ID);
        claims.claim("sub", ORCID);
        claims.expirationTime(new Date(System.currentTimeMillis() + 60000));

        OpenIDConnectKeyService.OpenIDConnectKeyServiceConfig config = new OpenIDConnectKeyService.OpenIDConnectKeyServiceConfig();
        config.setKeyName("OpenIDTestKey1");
        config.setJsonKey(
                "{\"keys\":[{\"kty\":\"RSA\",\"d\":\"i6C2Vdr7HDMj9wOBx28epQ7KPpzU_RDfGmQF8c81MoQU2KkpuNcFD49Rixzp3nQa58vtCOzAKeHwglpqm4elcai-uTW0bcdW1DOqYbwzQEk7pVQF-mMEUC-Rvd3Y5SIhCrHQYHGq9Q58uyuolG-Exq4h1AgyhUBX3CETCqzhPshOmB_Y4OuasdhyuVNySBbo-ZOYSd-HMrsrv1lt5WckWz22wmsREjO5AoRPpF17UVp3nMRCTy2v1acUrNtG64MdaFUpmLt9a-RqseFErE2Tm-kEUSBjYucswQ0_ZIs_VUdPWet4twqulB2bJi2ET6pP25DufOtR0x3ijvEPAfvhwQ\",\"e\":\"AQAB\",\"use\":\"sig\",\"kid\":\"OpenIDTestKey1\",\"alg\":\"RS256\",\"n\":\"qCtxWP2HppC8PBEXUh6b5RPECAzQS01khDwbxCSndO-YtS1MYpNlmtUgdtoAEoIP9TFMqXOsltKmGFioy0CeWLi53M-iX-Ygjd3zSQAbr0BU0-86somdbIlFxuvGA8v6AC7MNlICTwbGExCufL_hivrzF1XVqi5zIovM1LA8k2bP4BKMEjNwhGBGJ0E9KcQYv65foZr9K0C6YYJDFE6YqsHP_czvbI1ij7MfDvN5cwmHRGMGOyzDCmT_SmjoZAZ4vSXbl2wI5txIj70RLLSK4oahktb-09c0lDVYpCno7LqsLR8E3DuTUniYwYMHlXeBor_G7sJw2alF568m1iZ_zQ\"}]}");
        OpenIDConnectKeyService service = new OpenIDConnectKeyService(config);

        String token = service.sign(claims.build()).serialize();

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN, token);
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:id_token");
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:access_token");
        TokenRequest tokenRequest = new TokenRequest(requestParameters, AUDIENCE_CLIENT_ID, List.of("/read-limited"), GRANT_TYPE);
        try {
            tokenGranter.grant(GRANT_TYPE, tokenRequest);
            fail();
        } catch (IllegalArgumentException iae) {
            assertEquals("Attempt to exchange own id_token, use refresh token instead", iae.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void grantDisabledTokenDoesntWorkTest() throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        try {
            OrcidOauth2TokenDetail token1 = getOrcidOauth2TokenDetail(true, "/read-limited", System.currentTimeMillis() + 60000, true);
            token1.setRevokeReason(RevokeReason.USER_REVOKED.name());

            when(orcidOauthTokenDetailServiceMock.findByClientIdAndUserName(any(), any())).thenReturn(List.of(token1));
            tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/read-limited")));
            fail();
        } catch (OrcidInvalidScopeException oise) {
            assertEquals("The id_token is disabled and does not contain any valid scope", oise.getMessage());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void grantUserDisabledTokenWithActivitiesReadLimitedGenerateDeactivatedTokenTest()
            throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        OrcidOauth2TokenDetail token1 = getOrcidOauth2TokenDetail(true, "/activities/update", System.currentTimeMillis() + 60000, true);
        token1.setRevokeReason(RevokeReason.USER_REVOKED.name());

        when(orcidOauthTokenDetailServiceMock.findByClientIdAndUserName(any(), any())).thenReturn(List.of(token1));
        tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/activities/update")));
        // Verify revoke token was created
        verify(tokenServicesMock, times(1)).createRevokedAccessToken(any(), eq(RevokeReason.USER_REVOKED));
        // Verify regular token was never created
        verify(tokenServicesMock, never()).createAccessToken(any());
    }

    @Test
    public void grantClientDisabledTokenWithActivitiesReadLimitedThrowExceptionTest()
            throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        OrcidOauth2TokenDetail token1 = getOrcidOauth2TokenDetail(true, "/activities/update", System.currentTimeMillis() + 60000, true);
        token1.setRevokeReason(RevokeReason.CLIENT_REVOKED.name());

        when(orcidOauthTokenDetailServiceMock.findByClientIdAndUserName(any(), any())).thenReturn(List.of(token1));
        try {
            tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/activities/update")));
        } catch(OrcidInvalidScopeException e) {
            assertEquals("The id_token is disabled and does not contain any valid scope", e.getMessage());
        } catch(Exception e) {
            fail("Unhandled exception:" + e.getMessage());
        }
    }

    @Test
    public void grantStaffDisabledTokenWithActivitiesReadLimitedThrowExceptionTest()
            throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        OrcidOauth2TokenDetail token1 = getOrcidOauth2TokenDetail(true, "/activities/update", System.currentTimeMillis() + 60000, true);
        token1.setRevokeReason(RevokeReason.STAFF_REVOKED.name());

        when(orcidOauthTokenDetailServiceMock.findByClientIdAndUserName(any(), any())).thenReturn(List.of(token1));
        try {
            tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/activities/update")));
        } catch(OrcidInvalidScopeException e) {
            assertEquals("The id_token is disabled and does not contain any valid scope", e.getMessage());
        } catch(Exception e) {
            fail("Unhandled exception:" + e.getMessage());
        }
    }

    @Test
    public void grantDisabledTokenWithActivitiesUpdateAndOtherActiveTokenWithOtherScopesGenerateDeactivatedTokenTest()
            throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        // Deactivated token
        OrcidOauth2TokenDetail token1 = getOrcidOauth2TokenDetail(true, "/activities/update", System.currentTimeMillis() + 60000, true);
        token1.setRevokeReason(RevokeReason.USER_REVOKED.name());
        
        // Active token with other scope
        OrcidOauth2TokenDetail token2 = getOrcidOauth2TokenDetail(true, "/activities/read-limited /read-limited /read-public", System.currentTimeMillis() + 60000, false);
        token2.setApproved(true);
        token2.setScope("/activities/read-limited /read-limited /read-public");
        token2.setTokenExpiration(new Date(System.currentTimeMillis() + 60000));
        token2.setTokenDisabled(false);

        // Revoke token should be generated
        when(orcidOauthTokenDetailServiceMock.findByClientIdAndUserName(any(), any())).thenReturn(List.of(token1, token2));
        tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/activities/update")));
        // Verify revoke token was created
        verify(tokenServicesMock, times(1)).createRevokedAccessToken(any(), eq(RevokeReason.USER_REVOKED));
        // Verify regular token was never created
        verify(tokenServicesMock, never()).createAccessToken(any());
    }

    @Test
    public void grantDisabledTokenWithActivitiesUpdateAndOtherActiveTokenWithActivitiesUpdateScopesGenerateActiveTokenTest()
            throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        // Deactivated token
        OrcidOauth2TokenDetail token1 = getOrcidOauth2TokenDetail(true, "/activities/update", System.currentTimeMillis() + 60000, true);
        token1.setRevokeReason(RevokeReason.USER_REVOKED.name());
        
        // Active token with other scope
        OrcidOauth2TokenDetail token2 = getOrcidOauth2TokenDetail(true, "/activities/read-limited /read-limited /activities/update /read-public", System.currentTimeMillis() + 60000, true);
        token2.setTokenDisabled(false);

        // Revoke token should be generated
        when(orcidOauthTokenDetailServiceMock.findByClientIdAndUserName(any(), any())).thenReturn(List.of(token1, token2));
        tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/activities/update")));
        // Verify revoke token was never created
        verify(tokenServicesMock, never()).createRevokedAccessToken(any(), any());
        // Verify regular token was created
        verify(tokenServicesMock, times(1)).createAccessToken(any());
    }

    @Test
    public void grantTest() throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        tokenGranter.grant(GRANT_TYPE, getTokenRequest(ACTIVE_CLIENT_ID, List.of("/read-limited")));
        verify(tokenServicesMock, times(1)).createAccessToken(any());
    }

    private TokenRequest getTokenRequest(String clientId, List<String> scope)
            throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN, buildJWTToken(false));
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:id_token");
        requestParameters.put(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE, "urn:ietf:params:oauth:token-type:access_token");
        return new TokenRequest(requestParameters, clientId, scope, GRANT_TYPE);
    }

    private String buildJWTToken(boolean expired) throws NoSuchAlgorithmException, IOException, ParseException, URISyntaxException, JOSEException {
        Builder claims = new JWTClaimsSet.Builder();
        claims.claim("aud", AUDIENCE_CLIENT_ID);
        claims.claim("sub", ORCID);
        claims.expirationTime(expired ? new Date(0) : new Date(System.currentTimeMillis() + 60000));

        OpenIDConnectKeyService.OpenIDConnectKeyServiceConfig config = new OpenIDConnectKeyService.OpenIDConnectKeyServiceConfig();
        config.setKeyName("OpenIDTestKey1");
        config.setJsonKey(
                "{\"keys\":[{\"kty\":\"RSA\",\"d\":\"i6C2Vdr7HDMj9wOBx28epQ7KPpzU_RDfGmQF8c81MoQU2KkpuNcFD49Rixzp3nQa58vtCOzAKeHwglpqm4elcai-uTW0bcdW1DOqYbwzQEk7pVQF-mMEUC-Rvd3Y5SIhCrHQYHGq9Q58uyuolG-Exq4h1AgyhUBX3CETCqzhPshOmB_Y4OuasdhyuVNySBbo-ZOYSd-HMrsrv1lt5WckWz22wmsREjO5AoRPpF17UVp3nMRCTy2v1acUrNtG64MdaFUpmLt9a-RqseFErE2Tm-kEUSBjYucswQ0_ZIs_VUdPWet4twqulB2bJi2ET6pP25DufOtR0x3ijvEPAfvhwQ\",\"e\":\"AQAB\",\"use\":\"sig\",\"kid\":\"OpenIDTestKey1\",\"alg\":\"RS256\",\"n\":\"qCtxWP2HppC8PBEXUh6b5RPECAzQS01khDwbxCSndO-YtS1MYpNlmtUgdtoAEoIP9TFMqXOsltKmGFioy0CeWLi53M-iX-Ygjd3zSQAbr0BU0-86somdbIlFxuvGA8v6AC7MNlICTwbGExCufL_hivrzF1XVqi5zIovM1LA8k2bP4BKMEjNwhGBGJ0E9KcQYv65foZr9K0C6YYJDFE6YqsHP_czvbI1ij7MfDvN5cwmHRGMGOyzDCmT_SmjoZAZ4vSXbl2wI5txIj70RLLSK4oahktb-09c0lDVYpCno7LqsLR8E3DuTUniYwYMHlXeBor_G7sJw2alF568m1iZ_zQ\"}]}");
        OpenIDConnectKeyService service = new OpenIDConnectKeyService(config);

        return service.sign(claims.build()).serialize();
    }
    
    private OrcidOauth2TokenDetail getOrcidOauth2TokenDetail(Boolean approved, String scope, long expiration, Boolean disabled) {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setApproved(approved);
        token.setScope(scope);
        token.setTokenExpiration(new Date(expiration));
        token.setTokenDisabled(disabled);
        return token;
    }
}
