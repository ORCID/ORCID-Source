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
            if(orcid != null) {
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                String password = (String) authentication.getCredentials();
                // This is a hack, password cannot be null on the constructor, but, will be erased anyway, so, we can set any password here
                User user = new User(orcid, password == null ? "DUMMY_PASSWORD" : password, authorities);
                user.eraseCredentials();
                return user;
            }
        }
        return null;
    }

}
