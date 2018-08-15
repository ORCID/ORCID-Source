package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

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
        if(education == null)
            return null;
        OrgAffiliationRelationEntity entity = mapperFacade.map(education, OrgAffiliationRelationEntity.class);
        if(entity.getDisplayIndex() == null) {
            entity.setDisplayIndex(0L);
        }
        return entity;
    }

    @Override
    public Education toEducation(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, Education.class);
    }

    public EducationSummary toEducationSummary(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, EducationSummary.class);
    }
    
    @Override
    public List<Education> toEducation(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, Education.class);
    }

    @Override
    public List<EducationSummary> toEducationSummary(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, EducationSummary.class);
    }
    
    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Education education, OrgAffiliationRelationEntity existing) {
        if (education == null) {
            return null;
        }
        mapperFacade.map(education, existing);
        return existing;
    }
}
