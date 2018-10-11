package org.orcid.core.oauth;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

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
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.google.common.collect.Sets;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

public class IETFExchangeTokenGranter implements TokenGranter {

    public static final String IETF_EXCHANGE = "urn:ietf:params:oauth:grant-type:token-exchange";
    private IETFExchangeTokenChecker checker;
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
    
    public IETFExchangeTokenGranter(IETFExchangeTokenChecker checker, AuthorizationServerTokenServices tokenServices) {
        this.checker = checker;
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
        checker.validateRequest(grantType, tokenRequest);
        
        String subjectToken = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN);
        String subjectTokenType = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE);
        String requestedTokenType = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE);   
        
        if (requestedTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN) && subjectTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN)) {
            return generateAccessToken(tokenRequest, subjectToken);
        }else if (requestedTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN) && subjectTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN)) { 
            return generateIdToken(tokenRequest, subjectToken);
        }
        throw new IllegalArgumentException("Supported tokens types are "+OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN+" "+OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN);
    }

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
        if (false) {
            throw new IllegalArgumentException("You cannot exchange an OBO access_token for an id_token");
            //TODO: prevent people exchanging OBO id tokens for access tokens.
        }
        
        /*
        //generate an id token and attach to the original token for the response.
        ProfileEntity profileEntity = detail.getProfile();
        List<OrcidGrantedAuthority> authorities = profileDao.getGrantedAuthoritiesForProfile(profileEntity.getId());
        profileEntity.setAuthorities(authorities);
        OrcidOauth2UserAuthentication userAuth = new OrcidOauth2UserAuthentication(profileEntity,true);
        Map<String, String> requestParameters = tokenRequest.getClientId();
        OAuth2Request request = new OAuth2Request(requestParameters, tokenRequest.getClientId(), authorities, true, existing.getScope(),
                OAuth2Utils.parseParameterList(detail.getResourceId()), detail.getRedirectUri(), Sets.newHashSet("token"),null);
        
        OAuth2Authentication authentication = new OAuth2Authentication(request , userAuth);
        
//        openIDConnectTokenEnhancer.enhance(existing, authentication);
 * */

        try {
            String idTok = openIDConnectTokenEnhancer.buildIdToken(existing,detail.getProfile().getId(), tokenRequest.getClientId(),tokenRequest.getRequestParameters().get(OrcidOauth2Constants.NONCE) );
            existing.getAdditionalInformation().put(OrcidOauth2Constants.ID_TOKEN, idTok);
            return existing;
        } catch (JOSEException e) {
            throw new RuntimeException("Could not sign ID token");
        }
        
        //openIDConnectTokenEnhancer.buildIdToken(existing, detail.getProfile().getId() ,tokenRequest.getClientId(), OrcidOauth2Constants.NONCE);
        
    }

    /** Generate an Access Token and exchange it for an id_token.       
     * 
     * @param tokenRequest
     * @param subjectToken
     * @return
     */
    public OAuth2AccessToken generateAccessToken(TokenRequest tokenRequest, String subjectToken) {
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
        } catch (ParseException e) {
            throw new IllegalArgumentException("Unexpected id token value, cannot parse the id_token");
        }
        
        // Verify the token DOES NOT belong to requesting client (use refresh instead!)
        //TODO: consider if this is correct...
        if (OBOClient.equals(tokenRequest.getClientId())) {
            throw new IllegalArgumentException("Attempt to exchange own id_token, use refresh token instead");
        }
        
        //verify OBO client is enabled
        ClientDetailsEntity clientDetailsOBO = clientDetailsEntityCacheManager.retrieve(OBOClient);
        orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetailsOBO);
        
        //Calculate scopes (include in response additionalInformation)
        //get list of all tokens for original client.  We have to base this on previous tokens, as you can't revoke a code.
        //this means we MUST generate token for OBO clients that request id_tokens, or they won't work!
        //this means only "token id_token" requests will work (not code id_token).  Balls.  Just means we must never enable "code id_token".
        
        //what are the possible scopes for the OBO client?
        List<OrcidOauth2TokenDetail> details = orcidOauthTokenDetailService.findByClientIdAndUserName(OBOClient, OBOOrcid);
        Set<ScopePathType> scopesOBO = Sets.newHashSet();
        for (OrcidOauth2TokenDetail d: details) {
            if (d.getTokenDisabled() != null && !d.getTokenDisabled() && d.getTokenExpiration().after(new Date())) {
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
        
        //Create access token for calling client - model on OrcidRandomValueTokenServicesImpl.refreshAccessToken() ??
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
        return token;
        //TODO: what do we put in redirect_uri?
        //TODO: update OBO table or add OBO to token detail
        //Need to update to add OBO table - token - new client id (sp) - original client id (m) - id_token (decoded as JSON field).
        //TODO: when revoking M, also revoke M tokens from this table.
        //TODO: update all code that modifies database via API to also look at possible OBO and populate assertion origin.
        //DO we need to both with revoking if tokens only last an hour?
    }

}
