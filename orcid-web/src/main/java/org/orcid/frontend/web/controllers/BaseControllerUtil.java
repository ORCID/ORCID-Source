package org.orcid.frontend.web.controllers;

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.management.relation.InvalidRoleValueException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseControllerUtil {

    public OrcidProfileUserDetails getCurrentUser(SecurityContext context) {
        if (context == null) 
            return null;
        Authentication authentication = context.getAuthentication();
        Object details = authentication.getDetails();
        if ((authentication instanceof UsernamePasswordAuthenticationToken || authentication instanceof PreAuthenticatedAuthenticationToken)) {
            if(authentication.getDetails() instanceof OrcidProfileUserDetails) {
                return ((OrcidProfileUserDetails) authentication.getDetails());
            } else {
                // From the authorization server we will get a
                String orcid = authentication.getName();
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

                List<OrcidWebRole> orcidAuthorities = new ArrayList<OrcidWebRole>();
                authorities.forEach(x -> {
                    switch (x.getAuthority()) {
                        case "ROLE_USER":
                            orcidAuthorities.add(OrcidWebRole.ROLE_USER);
                            break;
                        case "ROLE_ADMIN":
                            orcidAuthorities.add(OrcidWebRole.ROLE_ADMIN);
                            break;
                        case "ROLE_GROUP":
                            orcidAuthorities.add(OrcidWebRole.ROLE_GROUP);
                            break;
                        case "ROLE_BASIC":
                            orcidAuthorities.add(OrcidWebRole.ROLE_BASIC);
                            break;
                        case "ROLE_PREMIUM":
                            orcidAuthorities.add(OrcidWebRole.ROLE_PREMIUM);
                            break;
                        case "ROLE_BASIC_INSTITUTION":
                            orcidAuthorities.add(OrcidWebRole.ROLE_BASIC_INSTITUTION);
                            break;
                        case "ROLE_PREMIUM_INSTITUTION":
                            orcidAuthorities.add(OrcidWebRole.ROLE_PREMIUM_INSTITUTION);
                            break;
                        case "ROLE_CREATOR":
                            orcidAuthorities.add(OrcidWebRole.ROLE_CREATOR);
                            break;
                        case "ROLE_PREMIUM_CREATOR":
                            orcidAuthorities.add(OrcidWebRole.ROLE_PREMIUM_CREATOR);
                            break;
                        case "ROLE_UPDATER":
                            orcidAuthorities.add(OrcidWebRole.ROLE_UPDATER);
                            break;
                        case "ROLE_PREMIUM_UPDATER":
                            orcidAuthorities.add(OrcidWebRole.ROLE_PREMIUM_UPDATER);
                            break;
                        case "ROLE_SELF_SERVICE":
                            orcidAuthorities.add(OrcidWebRole.ROLE_SELF_SERVICE);
                            break;
                        default:
                            throw new RuntimeException("Unsupported orcid authority for" + orcid + ": '" + x.getAuthority() + "'");
                    }
                });
                return new OrcidProfileUserDetails(orcid, null, orcidAuthorities);
            }
        } else {
            return null;
        }
    }

}
