package org.orcid.frontend.web.controllers;

import org.orcid.core.security.OrcidRoles;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseControllerUtil {

    // TODO: DEPRECATED, please use AuthenticationUtils.getCurrentUser() instead
    @Deprecated
    public UserDetails getCurrentUser(SecurityContext context) {
        if (context == null)
            return null;
        Authentication authentication = context.getAuthentication();
        if ((authentication instanceof UsernamePasswordAuthenticationToken || authentication instanceof PreAuthenticatedAuthenticationToken)) {
            // From the authorization server we will get a
            String orcid = authentication.getName();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            return new User(orcid, "", authorities);
        } else {
            return null;
        }
    }

}
