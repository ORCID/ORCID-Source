package org.orcid.api.common.filter;

import java.security.AccessControlException;
import java.util.regex.Matcher;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@Provider
public class TokenTargetFilter implements ContainerRequestFilter {

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
                    if (!targetOrcid.equals(authDetails.getUserOrcid())) {
                        throw new AccessControlException("You do not have the required permissions.");
                    }
                }
            }
        }
    }

}
