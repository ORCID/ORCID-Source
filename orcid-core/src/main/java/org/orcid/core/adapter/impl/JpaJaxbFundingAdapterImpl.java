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
package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbFundingAdapter;
import org.orcid.jaxb.model.record.Funding;
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
    public Funding toFunding(ProfileFundingEntity ProfileFundingEntity) {
        if (ProfileFundingEntity == null) {
            return null;
        }
        return mapperFacade.map(ProfileFundingEntity, Funding.class);
    }

    @Override
    public List<Funding> toFunding(Collection<ProfileFundingEntity> fundingEntities) {
        if (fundingEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(fundingEntities, Funding.class);
    }

}
