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

import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.core.adapter.JpaJaxbFundingAdapter;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbEducationAdapterImpl implements JpaJaxbEducationAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Education education) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Education toEducation(OrgAffiliationRelationEntity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Education> toEducation(Collection<OrgAffiliationRelationEntity> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    

}
