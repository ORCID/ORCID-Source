package org.orcid.core.oauth.service;

import java.util.Map;
import java.util.Set;

import org.orcid.core.constants.OrcidOauth2Constants;
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
    
    private static enum ImplicitScopes {
        OPENID("openid"), AUTHENTICATE("/authenticate");
    private String value;
    ImplicitScopes(String v){
        value=v;
    }
    public static boolean isValid(Set<String> test) {
        for (String t : test){
            boolean found = false;
            for (ImplicitScopes scope : ImplicitScopes.values()) {
                if (scope.value.equals(t)) {
                    found = true;
                }
            }
            if (!found)
                return false;
        }
        return true;
    }
    public String toString(){
        return value;
    }
    };

    private ProfileEntityCacheManager profileEntityCacheManager;    
    
    public OrcidOAuth2RequestValidator(ProfileEntityCacheManager profileEntityCacheManager) {
        this.profileEntityCacheManager = profileEntityCacheManager;
    }

    public void validateParameters(Map<String, String> parameters, ClientDetails clientDetails, String responseType) {
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
        if (responseType!=null && responseType.contains(OrcidOauth2Constants.IMPLICIT_TOKEN_RESPONSE_TYPE) && !ImplicitScopes.isValid(OAuth2Utils.parseParameterList(parameters.get("scope")))){
            throw new InvalidScopeException("Invalid response_type/scope combination.");           
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
