/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public class ResearcherUrlManagerImpl implements ResearcherUrlManager {

    @Resource
    private ResearcherUrlDao researcherUrlDao;
    
    @Override
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid) {
        return researcherUrlDao.getResearcherUrls(orcid);
    }

    @Override
    public boolean deleteResearcherUrl(long id) {
        return researcherUrlDao.deleteResearcherUrl(id);
    }

    @Override
    public ResearcherUrlEntity getResearcherUrl(long id) {
        return researcherUrlDao.getResearcherUrl(id);
    }

    @Override
    public void addResearcherUrls(String orcid, String url, String urlName) {
        researcherUrlDao.addResearcherUrls(orcid, url, urlName);
    }

    @Override
    public void updateResearcherUrls(String orcid, List<ResearcherUrl> researcherUrls){
        List<ResearcherUrlEntity> currentResearcherUrls = this.getResearcherUrls(orcid);
        Iterator<ResearcherUrlEntity> currentIterator = currentResearcherUrls.iterator();
        ArrayList<ResearcherUrl> newResearcherUrls = new ArrayList<ResearcherUrl>(researcherUrls);
        
        while(currentIterator.hasNext()){
            ResearcherUrlEntity existingResearcherUrl = currentIterator.next();
            ResearcherUrl researcherUrl = new ResearcherUrl(new Url(existingResearcherUrl.getUrl()), new UrlName(existingResearcherUrl.getUrlName()));
            //Delete non modified researcher urls from the parameter list
            if(researcherUrls.contains(researcherUrl)) {
                newResearcherUrls.remove(researcherUrl);
            } else {
                //Delete researcher urls deleted by user
                researcherUrlDao.deleteResearcherUrl(existingResearcherUrl.getId());
            }
        }
        
        //At this point, only new researcher urls are in the list newResearcherUrls
        //Insert all these researcher urls on database
        for(ResearcherUrl newResearcherUrl : newResearcherUrls){
            researcherUrlDao.addResearcherUrls(orcid, newResearcherUrl.getUrl().getValue(), newResearcherUrl.getUrlName().getContent());            
        }
    }
}
