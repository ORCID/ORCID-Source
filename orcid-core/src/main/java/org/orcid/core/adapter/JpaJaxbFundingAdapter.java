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

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record_rc4.Funding;
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
