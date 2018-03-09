package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record_v2.Funding;
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
