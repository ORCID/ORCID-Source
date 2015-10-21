/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.oauth;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.constants.OauthTokensConstants;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;

/**
 * @author Declan Newman (declan) Date: 10/05/2012
 */ 
public class OrcidClientCredentialsChecker {
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    private final OAuth2RequestFactory oAuth2RequestFactory;

    public OrcidClientCredentialsChecker(OAuth2RequestFactory oAuth2RequestFactory) {
        this.oAuth2RequestFactory = oAuth2RequestFactory;
    }

    public OAuth2Request validateCredentials(String grantType, TokenRequest tokenRequest) {
        String clientId = tokenRequest.getClientId();
        Set<String> scopes = tokenRequest.getScope();
        ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieve(clientId);                        
        validateMemberIsActive(clientDetails.getGroupProfileId(), clientId);
        validateGrantType(grantType, clientDetails);
        if (scopes != null) {
            validateScope(clientDetails, scopes);
        }
        
        Map<String, String> authorizationParams = new HashMap<String, String>();
        authorizationParams.putAll(tokenRequest.getRequestParameters());
        authorizationParams.put(OauthTokensConstants.GRANT_TYPE, grantType);
        authorizationParams.put(OAuth2Utils.SCOPE, StringUtils.join(scopes, ' '));
        authorizationParams.put(OAuth2Utils.CLIENT_ID, clientId);
        
        AuthorizationRequest authorizationRequest = oAuth2RequestFactory.createAuthorizationRequest(authorizationParams);
        authorizationRequest.setAuthorities(clientDetails.getAuthorities());
        authorizationRequest.setResourceIds(clientDetails.getResourceIds());
        authorizationRequest.setApproved(true);
                        
        return oAuth2RequestFactory.createOAuth2Request(authorizationRequest);
    }

    private void validateScope(ClientDetails clientDetails, Set<String> scopes) {

        if (clientDetails.isScoped()) {
            Set<String> validScope = clientDetails.getScope();
            if (scopes.isEmpty()) {
                throw new InvalidScopeException("Invalid scope (none)", validScope);
            } else if (!containsAny(validScope, ScopePathType.ORCID_PROFILE_CREATE, ScopePathType.WEBHOOK,
            		ScopePathType.PREMIUM_NOTIFICATION, ScopePathType.GROUP_ID_RECORD_READ, ScopePathType.GROUP_ID_RECORD_UPDATE)
            		&& !scopes.contains(ScopePathType.READ_PUBLIC.value()) && scopes.size() == 1) {
                throw new InvalidScopeException("Invalid scope" + (scopes != null && scopes.size() > 1 ? "s: " : ": " + "") + OAuth2Utils.formatParameterList(scopes),
                        validScope);
            }

            // The Read public does not have to be granted. It's the implied
            // read level. We let this through, regardless
            if (scopes.size() == 1 && scopes.iterator().next().equals(ScopePathType.READ_PUBLIC.value())) {
                return;
            }

            for (String scope : scopes) {
                if (!validScope.contains(scope)) {
                    throw new InvalidScopeException("Invalid scope: " + scope, validScope);
                }
            }
        }

    }

    private boolean containsAny(Set<String> scopes, ScopePathType... scopePathTypes) {
        for (ScopePathType scopePathType : scopePathTypes) {
            if (scopes.contains(scopePathType.value())) {
                return true;
            }
        }
        return false;
    }

    private void validateGrantType(String grantType, ClientDetails clientDetails) {
        Collection<String> authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        if (authorizedGrantTypes != null && !authorizedGrantTypes.isEmpty() && !authorizedGrantTypes.contains(grantType)) {
            throw new InvalidGrantException("Unauthorized grant type: " + grantType);
        }
    }

    private void validateMemberIsActive(String memberId, String clientId) {
        ProfileEntity memberEntity = profileEntityCacheManager.retrieve(memberId);
        //If it is locked
        if(!memberEntity.isAccountNonLocked()) {
            throw new LockedException("The given client " + clientId + " is locked because his member " + memberId + " is also locked", memberId); 
        }                 
    }    
}
