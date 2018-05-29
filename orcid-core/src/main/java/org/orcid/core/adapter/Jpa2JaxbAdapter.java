package org.orcid.core.adapter;

import org.orcid.core.manager.LoadOptions;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.OrcidIdBase;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

/**
 * orcid-persistence - Dec 7, 2011 - Jaxb2JpaAdapter
 * 
 * @author Declan Newman (declan)
 **/

public interface Jpa2JaxbAdapter {    

    OrcidProfile toOrcidProfile(ProfileEntity profileEntity, LoadOptions loadOptions);

    OrcidClientGroup toOrcidClientGroup(ProfileEntity profileEntity);

    OrcidClient toOrcidClient(ClientDetailsEntity clientDetailsEntity);
    
    Funding getFunding(ProfileFundingEntity profileFundingEntity);

    OrcidIdBase getOrcidIdBase(String id);
    
    DisambiguatedOrganization getDisambiguatedOrganization(OrgDisambiguatedEntity orgDisambiguatedEntity);
}
