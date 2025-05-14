package org.orcid.core.common.util;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.security.OrcidRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.util.Collection;

public class AuthenticationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtils.class);

    public static final GrantedAuthority adminAuthority = new SimpleGrantedAuthority(OrcidRoles.ROLE_ADMIN.name());

    public static String retrieveEffectiveOrcid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getDetails() != null) {
            // From the authorization server we will get the effective user from authentication.getName()
            String orcid = authentication.getName();
            if(StringUtils.isNotBlank(orcid)) {
                return orcid;
            }
        }
        return null;
    }

    public static String retrieveActiveSourceId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        } else if (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            // Token endpoint
            return ((UsernamePasswordAuthenticationToken) authentication).getName();
        } else if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            // API
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            return authorizationRequest.getClientId();
        } else {
            // Normal web user
            return AuthenticationUtils.retrieveEffectiveOrcid();
        }
    }

    public static boolean isInDelegationMode() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String realUserOrcid = getRealUserIfInDelegationMode(authentication);
        if (realUserOrcid == null) {
            return false;
        }
        return !AuthenticationUtils.retrieveEffectiveOrcid().equals(realUserOrcid);
    }

    public static String retrieveRealUserOrcid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        } else if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            // API
            OAuth2Request authorizationRequest = ((OAuth2Authentication) authentication).getOAuth2Request();
            return authorizationRequest.getClientId();
        }
        // Delegation mode
        String realUserIfInDelegationMode = getRealUserIfInDelegationMode(authentication);
        if (realUserIfInDelegationMode != null) {
            return realUserIfInDelegationMode;
        }
        // Normal web user
        return AuthenticationUtils.retrieveEffectiveOrcid();
    }

    public static boolean isDelegatedByAnAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                for (GrantedAuthority authority : authorities) {
                    if (authority instanceof SwitchUserGrantedAuthority) {
                        SwitchUserGrantedAuthority suga = (SwitchUserGrantedAuthority) authority;
                        Authentication sourceAuthentication = suga.getSource();

                        LOGGER.trace("isDelegatedByAnAdmin");
                        LOGGER.trace("Authentication: {}", sourceAuthentication);
                        LOGGER.trace("Authentication type: {}", sourceAuthentication.getClass().getName());
                        LOGGER.trace("User Details type: {}", sourceAuthentication.getDetails().getClass());

                        if (sourceAuthentication instanceof UsernamePasswordAuthenticationToken && sourceAuthentication.getDetails() instanceof UserDetails) {
                            LOGGER.trace("Authorities: {}", ((UserDetails) sourceAuthentication.getDetails()).getAuthorities());
                            LOGGER.trace("Admin authority: {}", adminAuthority);
                            return ((UserDetails) sourceAuthentication.getDetails()).getAuthorities().contains(adminAuthority);
                        }
                    }
                }
            }
        }
        return false;
    }

    private static String getRealUserIfInDelegationMode(Authentication authentication) {
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (authorities != null) {
                for (GrantedAuthority authority : authorities) {
                    if (authority instanceof SwitchUserGrantedAuthority) {
                        SwitchUserGrantedAuthority suga = (SwitchUserGrantedAuthority) authority;
                        Authentication sourceAuthentication = suga.getSource();
                        if ((sourceAuthentication instanceof UsernamePasswordAuthenticationToken
                                || sourceAuthentication instanceof PreAuthenticatedAuthenticationToken)) {
                            return sourceAuthentication.getName();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken || authentication instanceof PreAuthenticatedAuthenticationToken) {
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
