package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbFundingAdapter;
import org.orcid.jaxb.model.v3.rc1.record.Funding;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbFundingAdapterImpl implements JpaJaxbFundingAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public ProfileFundingEntity toProfileFundingEntity(Funding funding) {
        if (funding == null) {
            return null;
        }
        return mapperFacade.map(funding, ProfileFundingEntity.class);
    }

    @Override
    public Funding toFunding(ProfileFundingEntity profileFundingEntity) {
        if (profileFundingEntity == null) {
            return null;
        }
        return mapperFacade.map(profileFundingEntity, Funding.class);
    }

    @Override
    public FundingSummary toFundingSummary(ProfileFundingEntity profileFundingEntity) {
        if (profileFundingEntity == null) {
            return null;
        }
        
        return mapperFacade.map(profileFundingEntity, FundingSummary.class);
    }
    
    @Override
    public List<Funding> toFunding(Collection<ProfileFundingEntity> fundingEntities) {
        if (fundingEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(fundingEntities, Funding.class);
    }
    
    @Override
    public List<FundingSummary> toFundingSummary(Collection<ProfileFundingEntity> fundingEntities) {
        if (fundingEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(fundingEntities, FundingSummary.class);
    }
    
    @Override
    public ProfileFundingEntity toProfileFundingEntity(Funding funding, ProfileFundingEntity existing) {
        if (funding == null) {
            return null;
        }
        mapperFacade.map(funding, existing);
        return existing;
    }

}
