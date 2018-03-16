package org.orcid.core.manager.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public class ResearcherUrlManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ResearcherUrlManagerReadOnly {

    @Resource
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
    public ResearcherUrls getPublicResearcherUrls(String orcid) {
        List<ResearcherUrlEntity> researcherUrlEntities = researcherUrlDao.getResearcherUrls(orcid, Visibility.PUBLIC);
        return jpaJaxbResearcherUrlAdapter.toResearcherUrlList(researcherUrlEntities);
    }
    
    /**
     * Return the list of researcher urls associated to a specific profile
     * 
     * @param orcid
     * @return the list of researcher urls associated with the orcid profile
     * */
    @Override
    public ResearcherUrls getResearcherUrls(String orcid) {
        List<ResearcherUrlEntity> researcherUrlEntities = researcherUrlDao.getResearcherUrls(orcid, getLastModified(orcid));
        return jpaJaxbResearcherUrlAdapter.toResearcherUrlList(researcherUrlEntities);
    }
    
    @Override
    public ResearcherUrl getResearcherUrl(String orcid, long id) {
        ResearcherUrlEntity researcherUrlEntity = researcherUrlDao.getResearcherUrl(orcid, id);        
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(researcherUrlEntity);
    }    
}
