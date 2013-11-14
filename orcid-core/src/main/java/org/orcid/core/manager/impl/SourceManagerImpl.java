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
package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.SourceManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SourceManagerImpl implements SourceManager {

    @Resource
    private ProfileDao profileDao;

    @Override
    public String retrieveSourceOrcid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        if (OAuth2Authentication.class.isAssignableFrom(authentication.getClass())) {
            AuthorizationRequest authorizationRequest = ((OAuth2Authentication) authentication).getAuthorizationRequest();
            return authorizationRequest.getClientId();
        }
        if (OrcidProfileUserDetails.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            return ((OrcidProfileUserDetails) authentication.getPrincipal()).getRealOrcid();
        }
        return null;
    }

    @Override
    public ProfileEntity retrieveSourceProfileEntity() {
        String sourceOrcid = retrieveSourceOrcid();
        if (sourceOrcid == null) {
            return null;
        }
        return profileDao.find(sourceOrcid);
    }

}
