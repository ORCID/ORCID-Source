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

import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.jaxb.model.common.LastModifiedDate;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
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
    public ResearcherUrls toResearcherUrlList(Collection<ResearcherUrlEntity> entities) {
        if(entities == null) {
            return null;
        }
        List<ResearcherUrl> researchUrlList = mapperFacade.mapAsList(entities, ResearcherUrl.class);
        ResearcherUrls researchUrls = new ResearcherUrls();
		XMLGregorianCalendar tempDate = null;
		researchUrls.setResearcherUrls(researchUrlList);
		
		if(researchUrlList != null && !researchUrlList.isEmpty()) {
			tempDate = researchUrlList.get(0).getLastModifiedDate().getValue();
			for(ResearcherUrl researchUrl : researchUrlList) {
				if(tempDate.compare(researchUrl.getLastModifiedDate().getValue()) == -1) {
					tempDate = researchUrl.getLastModifiedDate().getValue();
				}
			}
		}
		if(tempDate != null)
			researchUrls.setLastModifiedDate(new LastModifiedDate(tempDate));
		
        return researchUrls;
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
