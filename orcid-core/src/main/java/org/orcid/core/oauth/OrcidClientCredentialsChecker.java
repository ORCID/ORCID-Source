/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import java.util.Collection;
import java.util.Set;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 10/05/2012
 */
public class OrcidClientCredentialsChecker {

    private final ClientDetailsService clientDetailsService;

    public OrcidClientCredentialsChecker(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    public AuthorizationRequest validateCredentials(String grantType, String clientId, Set<String> scopes) {

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        validateGrantType(grantType, clientDetails);
        if (scopes != null) {
            validateScope(clientDetails, scopes);
        }
        return new AuthorizationRequest(clientId, scopes, clientDetails.getAuthorities(), clientDetails.getResourceIds());

    }

    private void validateScope(ClientDetails clientDetails, Set<String> scopes) {

        if (clientDetails.isScoped()) {
            Set<String> validScope = clientDetails.getScope();
            if (scopes.isEmpty()) {
                throw new InvalidScopeException("Invalid scope (none)", validScope);
            } else if (!validScope.contains(ScopePathType.ORCID_PROFILE_CREATE.value()) && !(scopes.contains(ScopePathType.READ_PUBLIC.value()) && scopes.size() == 1)) {
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

    private void validateGrantType(String grantType, ClientDetails clientDetails) {
        Collection<String> authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        if (authorizedGrantTypes != null && !authorizedGrantTypes.isEmpty() && !authorizedGrantTypes.contains(grantType)) {
            throw new InvalidGrantException("Unauthorized grant type: " + grantType);
        }
    }

}
