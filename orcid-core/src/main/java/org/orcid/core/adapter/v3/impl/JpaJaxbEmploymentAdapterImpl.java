package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbEmploymentAdapter;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbEmploymentAdapterImpl implements JpaJaxbEmploymentAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Employment employment) {
        if(employment == null)
            return null;
        return mapperFacade.map(employment, OrgAffiliationRelationEntity.class);
    }

    @Override
    public Employment toEmployment(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, Employment.class);
    }
    
    @Override
    public EmploymentSummary toEmploymentSummary(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, EmploymentSummary.class);
    }

    @Override
    public List<Employment> toEmployment(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, Employment.class);
    }

    @Override
    public List<EmploymentSummary> toEmploymentSummary(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, EmploymentSummary.class);
    }
    
    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Employment employment, OrgAffiliationRelationEntity existing) {
        if (employment == null) {
            return null;
        }
        mapperFacade.map(employment, existing);
        return existing;
    }
}
