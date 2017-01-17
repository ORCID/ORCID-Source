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
package org.orcid.core.oauth.service;

import java.util.Map;
import java.util.Set;

import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;

public class OrcidOAuth2RequestValidator extends DefaultOAuth2RequestValidator {
    
    private ProfileEntityCacheManager profileEntityCacheManager;    
    
    public OrcidOAuth2RequestValidator(ProfileEntityCacheManager profileEntityCacheManager) {
        this.profileEntityCacheManager = profileEntityCacheManager;
    }

    public void validateParameters(Map<String, String> parameters, ClientDetails clientDetails) {
        if (parameters.containsKey("scope")) {
            if (clientDetails.isScoped()) {
                Set<String> validScope = clientDetails.getScope();
                for (String scope : OAuth2Utils.parseParameterList(parameters.get("scope"))) {
                	ScopePathType scopeType = null;
                	try {
                    	scopeType = ScopePathType.fromValue(scope);
                    } catch(Exception e) {
                    	throw new InvalidScopeException("Invalid scope: " + scope);
                    }
                	
                    if (scopeType.isClientCreditalScope())
                        throw new InvalidScopeException("Invalid scope: " + scope);
                    if (!validScope.contains(scope))
                        throw new InvalidScopeException("Invalid scope: " + scope);
                }
            }
        }
    }
    
    public void validateClientIsEnabled(ClientDetailsEntity clientDetails) throws LockedException {
        ProfileEntity memberEntity = profileEntityCacheManager.retrieve(clientDetails.getGroupProfileId());
        //If it is locked
        if(!memberEntity.isAccountNonLocked()) {
            throw new LockedException("The client is locked");
        }
    }

}
