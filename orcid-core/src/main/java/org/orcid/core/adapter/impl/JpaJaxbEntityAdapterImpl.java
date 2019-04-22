package org.orcid.core.adapter.impl;

import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWorks;
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
    public OrcidProfile toOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions) {
        return jpa2JaxbAdapter.toOrcidProfile(profileEntity, loadOptions);

    }

    @Override
    public ProfileEntity toProfileEntity(OrcidProfile profile) {
        return jaxb2JpaAdapter.toProfileEntity(profile, null);
    }  

    @Override
    @Deprecated
    public void setWorks(ProfileEntity profileEntity, OrcidWorks orcidWorks) {
        jaxb2JpaAdapter.setWorks(profileEntity, orcidWorks);        
    }

}
