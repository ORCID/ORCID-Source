package org.orcid.core.adapter;

import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.UpdateOptions;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
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
    
    ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity, UpdateOptions updateOptions);

    OrcidClient toOrcidClient(ClientDetailsEntity clientDetailsEntity);

    OrcidClientGroup toOrcidClientGroup(ProfileEntity profileEntity);

    ProfileEntity toProfileEntity(OrcidClientGroup orcidClientGroup);
    
    DisambiguatedOrganization getDisambiguatedOrganization(OrgDisambiguatedEntity orgDisambiguatedEntity);
    
    @Deprecated
    void setWorks(ProfileEntity profileEntity, OrcidWorks orcidWorks);
}
