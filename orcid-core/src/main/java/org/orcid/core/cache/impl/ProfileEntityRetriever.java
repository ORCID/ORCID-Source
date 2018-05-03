/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.cache.impl;

import javax.transaction.Transactional;

import org.orcid.core.cache.OrcidString;
import org.orcid.core.cache.Retriever;
import org.orcid.core.manager.read_only.ProfileEntityManagerReadOnly;
import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public class ProfileEntityRetriever implements Retriever<OrcidString, ProfileEntity> {

    private ProfileEntityManagerReadOnly profileEntityManager;

    public void setProfileEntityManager(ProfileEntityManagerReadOnly profileEntityManager) {
        this.profileEntityManager = profileEntityManager;
    }

    @Override
    @Transactional
    public ProfileEntity retrieve(OrcidString key) {
        String orcid = key.getOrcid();
        ProfileEntity profile = profileEntityManager.findByOrcid(orcid);
        if (profile == null)
            throw new IllegalArgumentException("Invalid orcid " + orcid);
        if (profile.getGivenPermissionBy() != null) {
            profile.getGivenPermissionBy().size();
        }
        if (profile.getGivenPermissionTo() != null) {
            profile.getGivenPermissionTo().size();
        }
        return profile;
    }

}
