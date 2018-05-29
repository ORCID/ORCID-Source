package org.orcid.core.adapter;

import org.orcid.core.manager.UpdateOptions;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * orcid-persistence - Dec 7, 2011 - Jaxb2JpaAdapter
 * 
 * @author Declan Newman (declan)
 **/

public interface Jaxb2JpaAdapter {

    ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity);
    
    ProfileEntity toProfileEntity(OrcidProfile profile, ProfileEntity existingProfileEntity, UpdateOptions updateOptions);

    OrgAffiliationRelationEntity getNewOrgAffiliationRelationEntity(Affiliation updatedAffiliation, ProfileEntity profileEntity);
    
    OrgAffiliationRelationEntity getUpdatedAffiliationRelationEntity(Affiliation updatedAffiliation);

    ProfileFundingEntity getNewProfileFundingEntity(Funding updatedFunding, ProfileEntity profileEntity);
    
    ProfileFundingEntity getUpdatedProfileFundingEntity(Funding updatedFunding);        

    @Deprecated
    WorkEntity getWorkEntity(String orcid, OrcidWork orcidWork, WorkEntity workEntity);
    
    @Deprecated
    void setWorks(ProfileEntity profileEntity, OrcidWorks orcidWorks);
}
