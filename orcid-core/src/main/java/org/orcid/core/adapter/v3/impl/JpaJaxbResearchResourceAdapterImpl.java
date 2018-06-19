package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbResearchResourceAdapter;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;

public class JpaJaxbResearchResourceAdapterImpl implements JpaJaxbResearchResourceAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
    
    @Override
    public ResearchResourceEntity toEntity(ResearchResource researchResource) {
        if (researchResource == null) {
            return null;
        }
        return mapperFacade.map(researchResource, ResearchResourceEntity.class);
    }

    @Override
    public ResearchResource toModel(ResearchResourceEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, ResearchResource.class);
    }

    @Override
    public ResearchResourceSummary toSummary(ResearchResourceEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return mapperFacade.map(entity, ResearchResourceSummary.class);
    }

    @Override
    public List<ResearchResource> toModels(Collection<ResearchResourceEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, ResearchResource.class);
    }

    @Override
    public List<ResearchResourceSummary> toSummaries(Collection<ResearchResourceEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, ResearchResourceSummary.class);
    }

    @Override
    public ResearchResourceEntity toEntity(ResearchResource ResearchResource, ResearchResourceEntity existing) {
        if (ResearchResource == null) {
            return null;
        }
        mapperFacade.map(ResearchResource, existing);
        return existing;
    }
}
