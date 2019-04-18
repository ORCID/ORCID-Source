package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbDistinctionAdapter;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbDistinctionAdapterImpl implements JpaJaxbDistinctionAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Distinction distinction) {
        if(distinction == null)
            return null;
        return mapperFacade.map(distinction, OrgAffiliationRelationEntity.class);
    }

    @Override
    public Distinction toDistinction(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, Distinction.class);
    }

    public DistinctionSummary toDistinctionSummary(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, DistinctionSummary.class);
    }
    
    @Override
    public List<Distinction> toDistinction(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, Distinction.class);
    }

    @Override
    public List<DistinctionSummary> toDistinctionSummary(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, DistinctionSummary.class);
    }
    
    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Distinction distinction, OrgAffiliationRelationEntity existing) {
        if (distinction == null) {
            return null;
        }
        mapperFacade.map(distinction, existing);
        return existing;
    }
}
