package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.dev1.record.Funding;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

/**
 * 
 * @author Angel Montenegro
 *
 */
public interface JpaJaxbFundingAdapter {

    ProfileFundingEntity toProfileFundingEntity(Funding funding);

    Funding toFunding(ProfileFundingEntity profileFundingEntity);

    List<Funding> toFunding(Collection<ProfileFundingEntity> fundingEntities);
    
    List<FundingSummary> toFundingSummary(Collection<ProfileFundingEntity> fundingEntities);
    
    FundingSummary toFundingSummary(ProfileFundingEntity profileFundingEntity);

    ProfileFundingEntity toProfileFundingEntity(Funding funding, ProfileFundingEntity existing);
}
