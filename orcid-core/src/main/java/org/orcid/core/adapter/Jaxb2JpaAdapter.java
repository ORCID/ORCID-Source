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
package org.orcid.core.adapter;

import org.orcid.core.manager.UpdateOptions;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
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
    
    WorkEntity getWorkEntity(OrcidWork orcidWork, WorkEntity workEntity);

}
