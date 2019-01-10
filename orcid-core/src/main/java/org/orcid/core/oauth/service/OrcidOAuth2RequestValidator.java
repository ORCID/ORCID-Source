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
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;

import com.google.common.collect.Sets;

public class OrcidOAuth2RequestValidator extends DefaultOAuth2RequestValidator {
    
    public static enum ImplicitScopes {
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
    
    public static enum OpenIDConnectScopesToIgnore {
        PROFILE("profile"), EMAIL("email"), ADDRESS("address"), PHONE("phone"), OFFLINE_ACCESS("offline_access");
        private String value;
        OpenIDConnectScopesToIgnore(String v){
            value = v;
        }
        public static boolean contains(String test) {
            for (OpenIDConnectScopesToIgnore scope : OpenIDConnectScopesToIgnore.values()) {
                if (scope.value.equals(test)) {
                    return true;
                }
            }
            return false;
        }        
        public String toString(){
            return value;
        }
    }

    private ProfileEntityCacheManager profileEntityCacheManager;    
    
    public OrcidOAuth2RequestValidator(ProfileEntityCacheManager profileEntityCacheManager) {
        this.profileEntityCacheManager = profileEntityCacheManager;
    }

    //called by spring AuthorizationEndpoint using unmodified parameter map
    //also called by LoginController before login.
    public void validateParameters(Map<String, String> parameters, ClientDetails clientDetails, String responseType) {
        if (parameters.containsKey("scope")) {
            if (clientDetails.isScoped()) {
                Set<String> validScope = clientDetails.getScope();
                for (String scope : OAuth2Utils.parseParameterList(parameters.get("scope"))) {
                    if (OpenIDConnectScopesToIgnore.contains(scope)) {//ignore openid scopes
                        continue;
                    }
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

    //included for clarity
    @Override
    public void validateScope(AuthorizationRequest authorizationRequest, ClientDetails client) throws InvalidScopeException {
        super.validateScope(authorizationRequest, client);
    }

    @Override
    public void validateScope(TokenRequest tokenRequest, ClientDetails client) throws InvalidScopeException {
        super.validateScope(tokenRequest, client);
    }
    
}
