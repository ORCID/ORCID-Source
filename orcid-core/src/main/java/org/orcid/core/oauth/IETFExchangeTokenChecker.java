package org.orcid.core.oauth;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.oauth.openid.OpenIDConnectKeyService;
import org.orcid.core.oauth.service.OrcidOAuth2RequestValidator;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.TokenRequest;

import com.google.common.collect.Sets;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class IETFExchangeTokenChecker {

    @Resource
    private OpenIDConnectKeyService openIDConnectKeyService;
    
    @Resource
    private OrcidOauth2TokenDetailService orcidOauthTokenDetailService;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    @Resource
    private OrcidOAuth2RequestValidator orcidOAuth2RequestValidator;
    
    /** General request checking for both directions.
     * Additional checking required for specific direction done by token granter.
     * 
     * grant_type = "urn:ietf:params:oauth:grant-type:token-exchange"
     * subject_token = the id or access token
     * subject_token_type = "urn:ietf:params:oauth:token-type:access_token" or "urn:ietf:params:oauth:token-type:id_token"
     * requested_token_type = "urn:ietf:params:oauth:token-type:access_token" or "urn:ietf:params:oauth:token-type:id_token"
     * 
     * subject_token_type and requested_token_type must be different
     * 
     * @param grantType
     * @param tokenRequest
     */
    public void validateRequest(String grantType, TokenRequest tokenRequest) {
        if (!OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE.equals(grantType)) {
            throw new IllegalArgumentException("Missing IETF Token exchange grant type"); //this should not really happen
        }
        
        //extract params
        String subjectToken = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN);
        String subjectTokenType = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_SUBJECT_TOKEN_TYPE);
        String requestedTokenType = tokenRequest.getRequestParameters().get(OrcidOauth2Constants.IETF_EXCHANGE_REQUESTED_TOKEN_TYPE);
        
        //check we have the right request params
        if (StringUtils.isEmpty(subjectToken) ||StringUtils.isEmpty(subjectTokenType)||StringUtils.isEmpty(requestedTokenType)) {
            throw new InvalidTokenException("Missing IETF Token exchange request parameter");
        }
        
        if (!(subjectTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN) || subjectTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN)) ||
            !(requestedTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN) || requestedTokenType.equals(OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN)) ||
                subjectTokenType.equals(requestedTokenType)) {
            throw new InvalidTokenException("Invalid IETF token exchange token types supported tokens types are "+OrcidOauth2Constants.IETF_EXCHANGE_ID_TOKEN+" "+OrcidOauth2Constants.IETF_EXCHANGE_ACCESS_TOKEN);            
        }
        
        // Verify requesting client is enabled
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(tokenRequest.getClientId());
        orcidOAuth2RequestValidator.validateClientIsEnabled(clientDetails);

        //Verify requesting client has grant type
        // TODO: consider if we need a similar check to see original client has enabled OBO...?
        if (!clientDetails.getAuthorizedGrantTypes().contains(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE)) {
            throw new InvalidTokenException("Client does not have "+OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE+" enabled");            
        }
    }
}
