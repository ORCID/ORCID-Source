package org.orcid.core.common.util;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtils {

    public static String retrieveEffectiveOrcid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getDetails() != null) {
            if(OrcidProfileUserDetails.class.isAssignableFrom(authentication.getDetails().getClass())) {
                return ((OrcidProfileUserDetails) authentication.getDetails()).getOrcid();
            } else {
                // From the authorization server we will get the effective user from authentication.getName()
                String orcid = authentication.getName();
                if(StringUtils.isNotBlank(orcid)) {
                    return orcid;
                }
            }
        }
        return null;
    }

}
