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
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResearcherUrlManagerImpl implements ResearcherUrlManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResearcherUrlManagerImpl.class);

    @Resource
    private ResearcherUrlDao researcherUrlDao;

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    @Override
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid) {
        return researcherUrlDao.getResearcherUrls(orcid);
    }

    /**
     * Deleted a researcher url from database
     * @param id
     * @return true if the researcher url was successfully deleted
     * */
    @Override
    public boolean deleteResearcherUrl(long id) {
        return researcherUrlDao.deleteResearcherUrl(id);
    }

    /**
     * Retrieve a researcher url from database
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    @Override
    public ResearcherUrlEntity getResearcherUrl(long id) {
        return researcherUrlDao.getResearcherUrl(id);
    }

    /**
     * Adds a researcher url to a specific profile
     * @param orcid
     * @param url
     * @param urlName
     * @return true if the researcher url was successfully created on database
     * */
    @Override
    public void addResearcherUrls(String orcid, String url, String urlName) {
        researcherUrlDao.addResearcherUrls(orcid, url, urlName);
    }

    /**
     * Update the researcher urls associated with a specific account
     * @param orcid
     * @param researcherUrls
     * */
    @Override
    public boolean updateResearcherUrls(String orcid, ResearcherUrls researcherUrls) {
        boolean hasErrors = false;
        List<ResearcherUrlEntity> currentResearcherUrls = this.getResearcherUrls(orcid);
        Iterator<ResearcherUrlEntity> currentIterator = currentResearcherUrls.iterator();
        ArrayList<ResearcherUrl> newResearcherUrls = new ArrayList<ResearcherUrl>(researcherUrls.getResearcherUrl());

        while (currentIterator.hasNext()) {
            ResearcherUrlEntity existingResearcherUrl = currentIterator.next();
            ResearcherUrl researcherUrl = new ResearcherUrl(new Url(existingResearcherUrl.getUrl()), new UrlName(existingResearcherUrl.getUrlName()));
            //Delete non modified researcher urls from the parameter list
            if (newResearcherUrls.contains(researcherUrl)) {
                newResearcherUrls.remove(researcherUrl);
            } else {
                //Delete researcher urls deleted by user
                researcherUrlDao.deleteResearcherUrl(existingResearcherUrl.getId());
            }
        }

        //Init null fields
        initNullSafeValues(newResearcherUrls);
        //At this point, only new researcher urls are in the list newResearcherUrls
        //Insert all these researcher urls on database
        for (ResearcherUrl newResearcherUrl : newResearcherUrls) {
            try {
                researcherUrlDao.addResearcherUrls(orcid, newResearcherUrl.getUrl().getValue(), newResearcherUrl.getUrlName().getContent());
            } catch (PersistenceException e) {
                //If the researcher url was duplicated, log the error
                if (e.getCause() != null && e.getCause().getClass().isAssignableFrom(ConstraintViolationException.class)) {
                    LOGGER.warn("Duplicated researcher url was found, the url {} already exists for {}", newResearcherUrl.getUrl().getValue(), orcid);
                    hasErrors = true;
                } else {
                    //If the error is not a duplicated error, something else happened, so, log the error and throw an exception
                    LOGGER.warn("Error inserting new researcher url {} for {}", newResearcherUrl.getUrl().getValue(), orcid);
                    throw new IllegalArgumentException("Unable to create Researcher Url", e);
                }
            }
        }
        return hasErrors;
    }

    /**
     * Init null safe values for researcher urls
     * @param researcherUrls
     * */
    private void initNullSafeValues(ArrayList<ResearcherUrl> newResearcherUrls) {
        for (ResearcherUrl researcherUrl : newResearcherUrls) {
            if (researcherUrl.getUrl() == null)
                researcherUrl.setUrl(new Url(new String()));
            if (researcherUrl.getUrlName() == null)
                researcherUrl.setUrlName(new UrlName(new String()));
        }
    }
}
