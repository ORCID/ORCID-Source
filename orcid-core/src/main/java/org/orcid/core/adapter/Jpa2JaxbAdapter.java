package org.orcid.core.adapter;

import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ProfileEntity;

/**
 * orcid-persistence - Dec 7, 2011 - Jaxb2JpaAdapter
 * 
 * @author Declan Newman (declan)
 **/

public interface Jpa2JaxbAdapter {    

    OrcidProfile toOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions);

}
