package org.orcid.core.oauth.service;

import java.util.Map;
import java.util.Set;

import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequestManager;

public class OrcidAuthorizationRequestManager extends DefaultAuthorizationRequestManager {

    public OrcidAuthorizationRequestManager(ClientDetailsService clientDetailsService) {
        super(clientDetailsService);
    }

    @Override
    public void validateParameters(Map<String, String> parameters, ClientDetails clientDetails) {
        if (parameters.containsKey("scope")) {
            if (clientDetails.isScoped()) {
                Set<String> validScope = clientDetails.getScope();
                for (String scope : OAuth2Utils.parseParameterList(parameters.get("scope"))) {
                    if (ScopePathType.READ_PUBLIC.equals(ScopePathType.fromValue(scope)) || ScopePathType.ORCID_PROFILE_CREATE.equals(ScopePathType.fromValue(scope)))
                        throw new InvalidScopeException("Invalid scope: " + scope);
                    if (!validScope.contains(scope))
                        throw new InvalidScopeException("Invalid scope: " + scope);
                }
            }
        }
    }

}
