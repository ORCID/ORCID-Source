/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.filter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.SourceManager;
import org.orcid.frontend.web.exception.SwitchUserAuthenticationException;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidSwitchUserFilter extends SwitchUserFilter {

    @Resource
    private SourceManager sourceManager;

    @Override
    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        String targetUserOrcid = request.getParameter(SPRING_SECURITY_SWITCH_USERNAME_KEY);
        ProfileEntity profileEntity = sourceManager.retrieveSourceProfileEntity();
        if (OrcidType.ADMIN.equals(profileEntity.getOrcidType())) {
            return super.attemptSwitchUser(request);
        }
        // If we are switching back to me it is OK
        if(targetUserOrcid.equals(profileEntity.getId())){
            return super.attemptSwitchUser(request);
        }
        for (GivenPermissionByEntity gpbe : profileEntity.getGivenPermissionBy()) {
            if (gpbe.getGiver().getId().equals(targetUserOrcid)) {
                return super.attemptSwitchUser(request);
            }
        }
        throw new SwitchUserAuthenticationException("Insufficient permissions");
    }

}
