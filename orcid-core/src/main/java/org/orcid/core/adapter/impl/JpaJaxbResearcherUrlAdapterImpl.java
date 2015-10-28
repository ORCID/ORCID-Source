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

import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.jaxb.model.record_rc1.ResearcherUrl;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public class JpaJaxbResearcherUrlAdapterImpl implements JpaJaxbResearcherUrlAdapter {

    private MapperFacade mapperFacade;
    
    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }
    
    @Override
    public ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl) {
        if(researcherUrl == null) {
            return null;
        }
        return mapperFacade.map(researcherUrl, ResearcherUrlEntity.class);
    }

    @Override
    public ResearcherUrl toResearcherUrl(ResearcherUrlEntity entity) {
        if(entity == null) {
            return null;
        }
        return mapperFacade.map(entity, ResearcherUrl.class);
    }

    @Override
    public List<ResearcherUrl> toResearcherUrlList(Collection<ResearcherUrlEntity> entities) {
        if(entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, ResearcherUrl.class);
    }

    @Override
    public ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl, ResearcherUrlEntity existing) {
        if(researcherUrl == null) {
            return null;
        }
        mapperFacade.map(researcherUrl, existing);
        return existing;
    }

}
