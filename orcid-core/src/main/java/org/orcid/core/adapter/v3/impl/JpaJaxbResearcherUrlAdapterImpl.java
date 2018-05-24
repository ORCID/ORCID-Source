package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.v3.JpaJaxbResearcherUrlAdapter;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbResearcherUrlAdapterImpl implements JpaJaxbResearcherUrlAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl) {
        if (researcherUrl == null) {
            return null;
        }
        ResearcherUrlEntity result = mapperFacade.map(researcherUrl, ResearcherUrlEntity.class);
        
        if(result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    @Override
    public ResearcherUrl toResearcherUrl(ResearcherUrlEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, ResearcherUrl.class);
    }

    @Override
    public ResearcherUrls toResearcherUrlList(Collection<ResearcherUrlEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<ResearcherUrl> researchUrlList = mapperFacade.mapAsList(entities, ResearcherUrl.class);
        ResearcherUrls researchUrls = new ResearcherUrls();
        researchUrls.setResearcherUrls(researchUrlList);
        return researchUrls;
    }

    @Override
    public ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl, ResearcherUrlEntity existing) {
        if (researcherUrl == null) {
            return null;
        }
        mapperFacade.map(researcherUrl, existing);
        return existing;
    }

}
