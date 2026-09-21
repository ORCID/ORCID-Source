package org.orcid.api.common.filter;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Provider
public class TokenTargetFilter implements ContainerRequestFilter {
    private static final String READ_PUBLIC_SCOPE = "/read-public";
    private static final String ROLE_PUBLIC = "ROLE_PUBLIC";

    //TODO: this method is doing exactly the same that the OrcidSecutiryManagerImpl.isMyToken does, so, lets review it and leave only one.

    @Override
    public void filter(ContainerRequestContext request) {
        Matcher m = OrcidStringUtils.orcidPattern.matcher(request.getUriInfo().getPath());
        if (m.find()) {
            validateTargetRecord(m.group());
        }
        return ;
    }

    private void validateTargetRecord(String targetOrcid) {
        // Verify if it is the owner of the token
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null && context.getAuthentication() != null) {
            Authentication authentication = context.getAuthentication();
            if (OrcidBearerTokenAuthentication.class.isAssignableFrom(authentication.getClass())) {
                OrcidBearerTokenAuthentication authDetails = (OrcidBearerTokenAuthentication) authentication;
                if (authDetails != null) {
                    if (isClientOnlyPublicReadToken(authDetails)) {
                        return;
                    }

                    String userOrcid = authDetails.getUserOrcid();
                    if (userOrcid != null) {
                        // Token has a specific user ORCID - validate it matches
                        if (!targetOrcid.equals(userOrcid)) {
                            throw new OrcidUnauthorizedException("Access token is for a different record");
                        }
                    }
                    // Allow through for client-only tokens and malformed ORCIDs so endpoint
                    // logic can resolve to 404 when the record does not exist.
                }
            }
        }
    }

    private boolean isClientOnlyPublicReadToken(OrcidBearerTokenAuthentication authDetails) {
        if (authDetails == null || authDetails.getUserOrcid() != null) {
            return false;
        }

        Set<String> scopes = authDetails.getScopes();
        if (scopes != null && scopes.contains(READ_PUBLIC_SCOPE)) {
            return true;
        }

        Collection<? extends GrantedAuthority> authorities = authDetails.getAuthorities();
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority authority : authorities) {
            if (authority != null && ROLE_PUBLIC.equals(authority.getAuthority())) {
                return true;
            }
        }

        return false;
    }

}
