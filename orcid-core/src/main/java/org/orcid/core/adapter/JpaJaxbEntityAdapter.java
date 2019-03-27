package org.orcid.core.adapter;

import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * Class to convert from JPA to JAXB classes and vice-versa
 * 
 * orcid-entities - Dec 5, 2011 - DateAdapter
 * 
 * @author Declan Newman (declan)
 **/

public interface JpaJaxbEntityAdapter {

    OrcidProfile toOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions);

    ProfileEntity toProfileEntity(OrcidProfile profile);    
    
    @Deprecated
    void setWorks(ProfileEntity profileEntity, OrcidWorks orcidWorks);
}
