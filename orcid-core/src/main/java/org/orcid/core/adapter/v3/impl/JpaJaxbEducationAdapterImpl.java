package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbEducationAdapter;
import org.orcid.jaxb.model.v3.dev1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.dev1.record.Education;
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
        return mapperFacade.map(education, OrgAffiliationRelationEntity.class);
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
