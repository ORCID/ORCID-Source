package org.orcid.core.adapter;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * orcid-persistence - Dec 7, 2011 - Jaxb2JpaAdapter
 * 
 * @author Declan Newman (declan)
 **/

public interface Jaxb2JpaAdapter {

    ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity);
    
    @Deprecated
    void setWorks(ProfileEntity profileEntity, OrcidWorks orcidWorks);
}
