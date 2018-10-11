package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.manager.v3.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

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
    public ResearcherUrls getPublicResearcherUrls(String orcid) {
        List<ResearcherUrlEntity> researcherUrlEntities = researcherUrlDao.getPublicResearcherUrls(orcid, getLastModified(orcid));
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
