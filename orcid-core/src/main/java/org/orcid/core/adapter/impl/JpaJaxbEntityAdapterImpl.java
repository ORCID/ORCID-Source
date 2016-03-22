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
package org.orcid.core.adapter.impl;

import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;

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
    public OrcidProfile toOrcidProfile(ProfileEntity profileEntity) {
        return toOrcidProfile(profileEntity, LoadOptions.ALL);
    }

    @Override
    public OrcidProfile toOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions) {
        return jpa2JaxbAdapter.toOrcidProfile(profileEntity, loadOptions);

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
    public OrcidClient toOrcidClient(ClientDetailsEntity clientDetailsEntity){
        return jpa2JaxbAdapter.toOrcidClient(clientDetailsEntity);
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
    
    @Override
    public DisambiguatedOrganization getDisambiguatedOrganization(OrgDisambiguatedEntity orgDisambiguatedEntity) {
        return jpa2JaxbAdapter.getDisambiguatedOrganization(orgDisambiguatedEntity);
    }

}
