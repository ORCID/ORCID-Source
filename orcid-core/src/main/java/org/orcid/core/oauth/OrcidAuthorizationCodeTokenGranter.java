package org.orcid.core.oauth;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.constants.RevokeReason;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.core.togglz.Features;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class OrcidAuthorizationCodeTokenGranter extends AbstractTokenGranter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAuthorizationCodeTokenGranter.class);

    private static final String GRANT_TYPE = "authorization_code";

    private final AuthorizationCodeServices authorizationCodeServices;

    @Value("${org.orcid.core.oauth.auth_code.expiration_minutes:1440}")    
    private int authorizationCodeExpiration;

    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;    
    
    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;
    
    public OrcidAuthorizationCodeTokenGranter(AuthorizationServerTokenServices tokenServices, AuthorizationCodeServices authorizationCodeServices,
            ClientDetailsService clientDetailsService, OAuth2RequestFactory oAuth2RequestFactory) {
        super(tokenServices, clientDetailsService, oAuth2RequestFactory, GRANT_TYPE);
        this.authorizationCodeServices = authorizationCodeServices;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = tokenRequest.getRequestParameters();
        String authorizationCode = parameters.get("code");
        String redirectUri = parameters.get(OAuth2Utils.REDIRECT_URI);

        LOGGER.info("Getting OAuth2 authentication: code={}, clientId={}, scope={}", new Object[] { authorizationCode, 
                tokenRequest.getClientId(), tokenRequest.getScope() });

        if (authorizationCode == null) {
            throw new OAuth2Exception("An authorization code must be supplied.");
        }

        //Validate the client is active
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(tokenRequest.getClientId());                        
        orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);
        
        //Validate scopes
        OrcidOauth2AuthoriziationCodeDetail codeDetails = orcidOauth2AuthoriziationCodeDetailDao.find(authorizationCode);        
        if(codeDetails == null) {
            if (Features.REVOKE_TOKEN_ON_CODE_REUSE.isActive()){
                int numDisabled = orcidOauthTokenDetailService.disableAccessTokenByCodeAndClient(authorizationCode, tokenRequest.getClientId(), RevokeReason.AUTH_CODE_REUSED);
                if (numDisabled >0){
                    throw new InvalidGrantException("Reused authorization code: " + authorizationCode);                                
                }                
            }
            throw new InvalidGrantException("Invalid authorization code: " + authorizationCode);                
        } else {
            // Check auth code expiration
            Date tokenCreationDate = codeDetails.getDateCreated();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tokenCreationDate);
            calendar.add(Calendar.MINUTE, authorizationCodeExpiration);            
            Date tokenExpirationDate = calendar.getTime();
            
            if(tokenExpirationDate.before(new Date())) {
                throw new IllegalArgumentException("Authorization code has expired");
            }
            
            // Check granted scopes
            Set<String> grantedScopes = codeDetails.getScopes();
            Set<String> requestScopes = tokenRequest.getScope();
            
            for(String requestScope : requestScopes) {
                if(!grantedScopes.contains(requestScope)) {
                    throw new InvalidScopeException("Invalid scopes: " + requestScope + " available scopes for this code are: " + grantedScopes);
                }
            }                        
            
        }        
        
        //Consume code
        OAuth2Authentication storedAuth;
        try{
            storedAuth = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);            
        }catch(InvalidGrantException e){            
            throw e;
        }               

        OAuth2Request pendingAuthorizationRequest = storedAuth.getOAuth2Request();
        //Regenerate the authorization request but now with the request parameters
        pendingAuthorizationRequest = pendingAuthorizationRequest.createOAuth2Request(parameters);
        
        LOGGER.debug("Found pending authorization request: redirectUri={}, clientId={}, scope={}, is_approved={}", new Object[] { pendingAuthorizationRequest.getRedirectUri(),
                pendingAuthorizationRequest.getClientId(), pendingAuthorizationRequest.getScope(), pendingAuthorizationRequest.isApproved() });
        // https://jira.springsource.org/browse/SECOAUTH-333
        // This might be null, if the authorization was done without the
        // redirect_uri parameter
        String redirectUriApprovalParameter = pendingAuthorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
        
        if ((redirectUri != null || redirectUriApprovalParameter != null) && !pendingAuthorizationRequest.getRedirectUri().equals(redirectUri)) {
            throw new RedirectMismatchException("Redirect URI mismatch.");
        }

        String pendingClientId = pendingAuthorizationRequest.getClientId();
        String clientId = client.getClientId();
        if (clientId != null && !clientId.equals(pendingClientId)) {
            LOGGER.error("Exception exchanging authentication code {}, client ID mismatch: pendingClientId={}, authorizationRequest.clientId={}",
                    new Object[] { authorizationCode, pendingClientId, clientId });
            // just a sanity check.
            throw new InvalidClientException("Client ID mismatch");
        }        
                
        Authentication userAuth = storedAuth.getUserAuthentication();
        return new OAuth2Authentication(pendingAuthorizationRequest, userAuth);

    }        
}
