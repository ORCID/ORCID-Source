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
package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.manager.v3.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.dev1.record.ResearcherUrls;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.springframework.cache.annotation.Cacheable;

public class ResearcherUrlManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ResearcherUrlManagerReadOnly {

    @Resource(name = "jpaJaxbResearcherUrlAdapterV3")
    protected JpaJaxbResearcherUrlAdapter jpaJaxbResearcherUrlAdapter;

    protected ResearcherUrlDao researcherUrlDao;      

    public void setResearcherUrlDao(ResearcherUrlDao researcherUrlDao) {
        this.researcherUrlDao = researcherUrlDao;
    }

    /**
     * Return the list of public researcher urls associated to a specific profile
     * 
     * @param orcid
     * @return the list of public researcher urls associated with the orcid profile
     * */
    @Override
    @Cacheable(value = "public-researcher-urls", key = "#orcid.concat('-').concat(#lastModified)")
    public ResearcherUrls getPublicResearcherUrls(String orcid, long lastModified) {
        return getResearcherUrls(orcid, Visibility.PUBLIC);
    }
    
    /**
     * Return the list of researcher urls associated to a specific profile
     * 
     * @param orcid
     * @return the list of researcher urls associated with the orcid profile
     * */
    @Override
    @Cacheable(value = "researcher-urls", key = "#orcid.concat('-').concat(#lastModified)")
    public ResearcherUrls getResearcherUrls(String orcid, long lastModified) {
        return getResearcherUrls(orcid, null);
    }
    
    /**
     * Return the list of researcher urls associated to a specific profile
     * 
     * @param orcid
     * @return the list of researcher urls associated with the orcid profile
     * */
    private ResearcherUrls getResearcherUrls(String orcid, Visibility visibility) {
        List<ResearcherUrlEntity> researcherUrlEntities = null; 
        if(visibility == null) {
            researcherUrlEntities = researcherUrlDao.getResearcherUrls(orcid, getLastModified(orcid));
        } else {
            researcherUrlEntities = researcherUrlDao.getResearcherUrls(orcid, org.orcid.jaxb.model.common_v2.Visibility.fromValue(visibility.value()));
        }       
        
        return jpaJaxbResearcherUrlAdapter.toResearcherUrlList(researcherUrlEntities);
    }

    @Override
    public ResearcherUrl getResearcherUrl(String orcid, long id) {
        ResearcherUrlEntity researcherUrlEntity = researcherUrlDao.getResearcherUrl(orcid, id);        
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(researcherUrlEntity);
    }    
}
