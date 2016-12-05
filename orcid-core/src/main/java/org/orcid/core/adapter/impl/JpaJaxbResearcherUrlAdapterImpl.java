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

import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
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
