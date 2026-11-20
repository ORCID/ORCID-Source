package org.orcid.core.manager.v3.validator;

import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.security.core.Authentication;

import java.util.Set;
import java.util.stream.Collectors;

public class ScopeValidator {
    private final static Set<String> SCOPES_TO_IGNORE = Set.of("profile", "email");

    public static void validateRequiredScopes(Authentication authentication, ScopePathType... requiredScopes){
        Set<String> allowedScopes = ((OrcidBearerTokenAuthentication) authentication).getScopes().stream().filter(scope -> !SCOPES_TO_IGNORE.contains(scope)).collect(Collectors.toSet());
        for (String scope : allowedScopes) {
            ScopePathType allowed = ScopePathType.fromValue(scope);
            for (ScopePathType requiredScope : requiredScopes) {
                if (allowed.hasScope(requiredScope)) {
                    return;
                }
            }
        }
        throw new OrcidAccessControlException();
    }
}
