package org.orcid.core.oauth;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService;
import org.orcid.core.oauth.openid.OpenIDConnectTokenEnhancer;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidGrantedAuthority;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.google.common.collect.Sets;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

/** Spec: https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-15
 * 
 * @author tom
 *
 */
public class IETFExchangeTokenGranter implements TokenGranter {

    public static final String IETF_EXCHANGE = "urn:ietf:params:oauth:grant-type:token-exchange";
    private AuthorizationServerTokenServices tokenServices;
    
    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    @Resource(name = "orcidTokenStore")
    private TokenStore tokenStore;
    @Resource
    private OpenIDConnectKeyService openIDConnectKeyService;
    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
    @Resource
    private ProfileEntityManager profileEntityManager;
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    OpenIDConnectTokenEnhancer openIDConnectTokenEnhancer;
    
    public IETFExchangeTokenGranter(AuthorizationServerTokenServices tokenServices) {
        this.tokenServices = tokenServices;
    }
    
    /** Invoked by OrcidClientCredentialEndPointDelegatorImpl.obtainOauth2Token
     * and OrcidClientCredentialEndPointDelegatorImpl.generateToken
     * 
     */
    @Override
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        if (!OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE.equals(grantType)) {
            return null;
        }
        
        //General request validation
        //extract params
        String subjectToken = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN);
        String subjectTokenType = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE);
        String requestedTokenType = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE);
        
        //check we have the right request params
        if (StringUtils.isEmpty(subjectToken) ||StringUtils.isEmpty(subjectTokenType)||StringUtils.isEmpty(requestedTokenType)) {
            throw new IllegalArgumentException("Missing IETF Token exchange request parameter(s).  Required: "+OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN+" "+OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE+" "+OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE);
        }
        
        //Must have one of each token type
        if (!(subjectTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN) || subjectTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN)) ||
            !(requestedTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN) || requestedTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN)) ||
                subjectTokenType.equals(requestedTokenType)) {
            throw new IllegalArgumentException("Invalid IETF token exchange token type(s) supported tokens types are "+OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN+" "+OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN);            
        }
        
        // Verify requesting client is enabled
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(tokenRequest.getClientId());
        orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);

        //Verify requesting client has grant type
        // TODO: consider if we need a similar check to see original client has enabled OBO...?
        if (!clientDetails.getAuthorizedGrantTypes().contains(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE)) {
            throw new IllegalArgumentException("Client does not have "+OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE+" enabled");            
        }
        
        if (requestedTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN) && subjectTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN)) {
            return generateAccessToken(tokenRequest, subjectToken);
        }else if (requestedTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN) && subjectTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN)) { 
            return generateIdToken(tokenRequest, subjectToken);
        }
        throw new IllegalArgumentException("Supported tokens types are "+OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN+" "+OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN);
    }

    /** Create an id token and return it.
     * Note this uses the weird convention of returning the id_token in the access_token property of the response.
     * Example:
     * 
     *     {
     "access_token":"eyJhbGciOiJFUzI1NiIsImtpZCI6IjcyIn0.eyJhdWQiOiJ1cm4
       6ZXhhbXBsZTpjb29wZXJhdGlvbi1jb250ZXh0IiwiaXNzIjoiaHR0cHM6Ly9hcy5l
       eGFtcGxlLmNvbSIsImV4cCI6MTQ0MTkxMzYxMCwic2NvcGUiOiJzdGF0dXMgZmVlZ
       CIsInN1YiI6InVzZXJAZXhhbXBsZS5uZXQiLCJhY3QiOnsic3ViIjoiYWRtaW5AZX
       hhbXBsZS5uZXQifX0.3paKl9UySKYB5ng6_cUtQ2qlO8Rc_y7Mea7IwEXTcYbNdwG
       9-G1EKCFe5fW3H0hwX-MSZ49Wpcb1SiAZaOQBtw",
     "issued_token_type":"urn:ietf:params:oauth:token-type:id_token",
     "token_type":"N_A",
     "expires_in":3600
    }
     * 
     * @param tokenRequest
     * @param subjectToken
     * @return
     */
    private OAuth2AccessToken generateIdToken(TokenRequest tokenRequest, String subjectToken) {
        OAuth2AccessToken existing = tokenStore.readAccessToken(subjectToken); 
        OrcidOauth2TokenDetail detail = orcidOauthTokenDetailService.findNonDisabledByTokenValue(subjectToken);
        if (detail == null) {
            throw new IllegalArgumentException("access_token does not exist or is disabled");            
        }
        if (!detail.getClientDetailsId().equals(tokenRequest.getClientId())) {
            throw new IllegalArgumentException("Clients can only exchange their own access_tokens for id_tokens");
        }
        if (existing.isExpired()) {
            throw new IllegalArgumentException("access_token has expired");
        }
        
        try {
            String idTok = openIDConnectTokenEnhancer.buildIdToken(existing,detail.getProfile().getId(), tokenRequest.getClientId(),tokenRequest.getRequestParameters().get(OrcidOauth2Constants.NONCE) );
            return new DefaultOAuth2AccessToken(IETFTokenExchangeResponse.idToken(idTok));
        } catch (JOSEException e) {
            throw new RuntimeException("Could not sign ID token");
        } catch (ParseException e) {
            throw new RuntimeException("Generated unparsable ID token");
        }
    }
    
    /** Create new id_token based on existing access token.       
     * 
     * @param tokenRequest
     * @param subjectToken
     * @return
     */
    private OAuth2AccessToken generateAccessToken(TokenRequest tokenRequest, String subjectToken) {
        //parse id_token
        String OBOClient = null;
        String OBOOrcid = null;
        try {
            SignedJWT claims = SignedJWT.parse(subjectToken);
            if (!openIDConnectKeyService.verify(claims)) {
                throw new IllegalArgumentException("Invalid id token signature");
            }
            OBOClient = claims.getJWTClaimsSet().getAudience().get(0);
            OBOOrcid = claims.getJWTClaimsSet().getSubject();
            //NOTE: we do not check expiration.  id_token exchange is independent of token life.            
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unexpected id token value, cannot parse the id_token");
        }
        
        // Verify the token DOES NOT belong to requesting client (use refresh instead!)
        // NOTE: this is now disabled.  You can generate your own.
        if (OBOClient.equals(tokenRequest.getClientId())) {
            throw new IllegalArgumentException("Attempt to exchange own id_token, use refresh token instead");
        }
        
        //verify OBO client is enabled
        ClientDetailsEntity clientDetailsOBO = clientDetailsEntityCacheManager.retrieve(OBOClient);
        orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetailsOBO);
        
        //Calculate scopes (include in response additionalInformation)
        //get list of all tokens for original client.  We have to base this on previous tokens, as you can't revoke a code.
        //this means only "token id_token" requests will work (not code id_token).  Balls.  Just means we must never enable "code id_token".
        List<OrcidOauth2TokenDetail> details = orcidOauthTokenDetailService.findByClientIdAndUserName(OBOClient, OBOOrcid);
        Set<ScopePathType> scopesOBO = Sets.newHashSet();
        for (OrcidOauth2TokenDetail d: details) {
            if ((d.getTokenDisabled() == null || !d.getTokenDisabled()) && d.getTokenExpiration().after(new Date())) {
                //TODO: do we need to check revocation dates?
                scopesOBO.addAll(ScopePathType.getScopesFromSpaceSeparatedString(d.getScope()));                    
            }
        }
        if (scopesOBO.isEmpty()) {
            throw new OrcidInvalidScopeException("The id_token is not associated with a valid scope");
        }
        Set<ScopePathType> combinedOBOScopes = new HashSet<ScopePathType>();
        for (ScopePathType scope : scopesOBO) {
            combinedOBOScopes.addAll(scope.combined());
        }
        
        //do we have requested scopes?
        String requestedScopesString = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.SCOPE_PARAM);
        Set<ScopePathType> requestedScopes = ScopePathType.getScopesFromSpaceSeparatedString(requestedScopesString);
        if (!requestedScopes.isEmpty()) {
            scopesOBO = Sets.intersection(combinedOBOScopes, requestedScopes);            
        }
        if (scopesOBO.isEmpty()) {
            throw new OrcidInvalidScopeException("The requested scope(s) are not available from this id_token");
        }
        Set<String> tokenScopes = Sets.newHashSet();
        for (ScopePathType s: scopesOBO) {
            tokenScopes.add(s.value());
        }
        
        //Create access token for calling client
        ProfileEntity profileEntity = profileEntityManager.findByOrcid(OBOOrcid);
        List<OrcidGrantedAuthority> authorities = profileDao.getGrantedAuthoritiesForProfile(profileEntity.getId());
        profileEntity.setAuthorities(authorities);
        OrcidOauth2UserAuthentication userAuth = new OrcidOauth2UserAuthentication(profileEntity,true);
        
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String clientId = tokenRequest.getClientId();
        boolean approved = true;
        Set<String> resourceIds = null;
        String redirectUri = null;
        Set<String> responseTypes = Sets.newHashSet("token");
        Map<String, Serializable> extensionProperties = null;
        OAuth2Request request = new OAuth2Request(requestParameters, clientId, authorities, approved, tokenScopes,
                resourceIds, redirectUri, responseTypes,extensionProperties);
        
        OAuth2Authentication authentication = new OAuth2Authentication(request , userAuth);
        OAuth2AccessToken token = tokenServices.createAccessToken(authentication);
        
        return new DefaultOAuth2AccessToken(IETFTokenExchangeResponse.accessToken(token));
        //Note, redirect_uri is left blank.                
    }

}
