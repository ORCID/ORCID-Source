package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public class JpaJaxbResearcherUrlAdapterImpl implements JpaJaxbResearcherUrlAdapter {

    private final MapperFacadeSupport mapperFacade = new MapperFacadeSupport();

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade.setMapperFacade(mapperFacade);
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
