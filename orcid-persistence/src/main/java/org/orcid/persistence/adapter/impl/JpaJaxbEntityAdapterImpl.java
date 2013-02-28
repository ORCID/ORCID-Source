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
package org.orcid.persistence.adapter.impl;

import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.adapter.Jaxb2JpaAdapter;
import org.orcid.persistence.adapter.Jpa2JaxbAdapter;
import org.orcid.persistence.adapter.JpaJaxbEntityAdapter;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * orcid-persistence - Dec 7, 2011 - JpaJaxbEntityAdapterImpl
 * 
 * @author Declan Newman (declan)
 **/

public class JpaJaxbEntityAdapterImpl implements JpaJaxbEntityAdapter {

    private Jpa2JaxbAdapter jpa2JaxbAdapter;
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    public JpaJaxbEntityAdapterImpl(Jpa2JaxbAdapter jpa2JaxbAdapter, Jaxb2JpaAdapter jaxb2JpaAdapter) {
        this.jpa2JaxbAdapter = jpa2JaxbAdapter;
        this.jaxb2JpaAdapter = jaxb2JpaAdapter;
    }

    @Override
    @Transactional
    public OrcidProfile toOrcidProfile(ProfileEntity profileEntity) {
        return jpa2JaxbAdapter.toOrcidProfile(profileEntity);

    }

    @Override
    public ProfileEntity toProfileEntity(OrcidProfile profile) {
        return jaxb2JpaAdapter.toProfileEntity(profile, null);
    }

    @Override
    public ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity) {
        return jaxb2JpaAdapter.toProfileEntity(profile, existingProfileEntity);
    }

    @Override
    public OrcidClientGroup toOrcidClientGroup(ProfileEntity profileEntity) {
        return jpa2JaxbAdapter.toOrcidClientGroup(profileEntity);
    }

    @Override
    public ProfileEntity toProfileEntity(OrcidClientGroup orcidClientGroup) {
        // TODO Auto-generated method stub
        return null;
    }

}
