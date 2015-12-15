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
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.UrlName;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ResearcherUrlManagerImpl implements ResearcherUrlManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResearcherUrlManagerImpl.class);

    @Resource
    private ResearcherUrlDao researcherUrlDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private JpaJaxbResearcherUrlAdapter jpaJaxbResearcherUrlAdapter;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    /**
     * Return the list of researcher urls associated to a specific profile
     * 
     * @param orcid
     * @return the list of researcher urls associated with the orcid profile
     * */
    @Override
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid) {
        return researcherUrlDao.getResearcherUrls(orcid);
    }

    @Override
    public boolean deleteResearcherUrl(String orcid, String id) {
        boolean result = true;
        Long researcherUrlId = Long.valueOf(id);
        ResearcherUrlEntity toDelete = researcherUrlDao.getResearcherUrl(orcid, researcherUrlId);  
        SourceEntity existingSource = toDelete.getSource();
        orcidSecurityManager.checkSource(existingSource);
        
        try {            
            researcherUrlDao.deleteResearcherUrl(orcid, researcherUrlId);
        } catch(Exception e) {
            LOGGER.error("Unable to delete researcherUrl with ID: " + researcherUrlId);
            result = false;
        }
        return result;
    }

    /**
     * Retrieve a researcher url from database
     * 
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    @Override
    public ResearcherUrlEntity getResearcherUrl(String orcid, Long id) {
        return researcherUrlDao.getResearcherUrl(orcid, id);
    }

    /**
     * Adds a researcher url to a specific profile
     * 
     * @param orcid
     * @param url
     * @param urlName
     * @return true if the researcher url was successfully created on database
     * */
    @Override
    public void addResearcherUrls(String orcid, String url, String urlName) {
        SourceEntity source = sourceManager.retrieveSourceEntity();
        Date date = new Date();
        ResearcherUrlEntity entity = new ResearcherUrlEntity();
        entity.setDateCreated(date);
        entity.setLastModified(date);
        entity.setSource(source);
        entity.setUrl(url);
        entity.setUrlName(urlName);
        entity.setUser(new ProfileEntity(orcid));
        entity.setVisibility(profileEntityManager.getResearcherUrlDefaultVisibility(orcid));
    }

    /**
     * Update the researcher urls associated with a specific account
     * 
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
            // Delete non modified researcher urls from the parameter list
            if (newResearcherUrls.contains(researcherUrl)) {
                newResearcherUrls.remove(researcherUrl);
            } else {
                // Delete researcher urls deleted by user
                researcherUrlDao.deleteResearcherUrl(orcid, existingResearcherUrl.getId());
            }
        }

        SourceEntity source = sourceManager.retrieveSourceEntity();

        // Init null fields
        initNullSafeValues(newResearcherUrls);
        // At this point, only new researcher urls are in the list
        // newResearcherUrls
        // Insert all these researcher urls on database
        for (ResearcherUrl newResearcherUrl : newResearcherUrls) {
            try {
                ResearcherUrlEntity entity = new ResearcherUrlEntity();
                Date date = new Date();
                entity.setDateCreated(date);
                entity.setLastModified(date);
                entity.setSource(source);
                entity.setUrl(newResearcherUrl.getUrl().getValue());
                entity.setUrlName(newResearcherUrl.getUrlName().getContent());
                entity.setUser(new ProfileEntity(orcid));
                entity.setVisibility(profileEntityManager.getResearcherUrlDefaultVisibility(orcid));
                researcherUrlDao.merge(entity);
            } catch (PersistenceException e) {
                // If the researcher url was duplicated, log the error
                if (e.getCause() != null && e.getCause().getClass().isAssignableFrom(ConstraintViolationException.class)) {
                    LOGGER.warn("Duplicated researcher url was found, the url {} already exists for {}", newResearcherUrl.getUrl().getValue(), orcid);
                    hasErrors = true;
                } else {
                    // If the error is not a duplicated error, something else
                    // happened, so, log the error and throw an exception
                    LOGGER.warn("Error inserting new researcher url {} for {}", newResearcherUrl.getUrl().getValue(), orcid);
                    throw new IllegalArgumentException("Unable to create Researcher Url", e);
                }
            }
        }

        profileDao.updateResearcherUrlsVisibility(orcid, researcherUrls.getVisibility());
        return hasErrors;
    }

    /**
     * Init null safe values for researcher urls
     * 
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

    /**
     * Return the list of public researcher urls associated to a specific profile
     * 
     * @param orcid
     * @return the list of public researcher urls associated with the orcid profile
     * */
    @Override
    public org.orcid.jaxb.model.record_rc2.ResearcherUrls getPublicResearcherUrlsV2(String orcid) {
        return getResearcherUrlsV2(orcid, Visibility.PUBLIC);
    }
    
    /**
     * Return the list of researcher urls associated to a specific profile
     * 
     * @param orcid
     * @return the list of researcher urls associated with the orcid profile
     * */
    @Override
    public org.orcid.jaxb.model.record_rc2.ResearcherUrls getResearcherUrlsV2(String orcid) {
        return getResearcherUrlsV2(orcid, null);
    }
    
    /**
     * Return the list of researcher urls associated to a specific profile
     * 
     * @param orcid
     * @return the list of researcher urls associated with the orcid profile
     * */
    private org.orcid.jaxb.model.record_rc2.ResearcherUrls getResearcherUrlsV2(String orcid, Visibility visibility) {
        List<ResearcherUrlEntity> researcherUrlEntities = null; 
        if(visibility == null) {
            researcherUrlEntities = researcherUrlDao.getResearcherUrls(orcid);
        } else {
            researcherUrlEntities = researcherUrlDao.getResearcherUrls(orcid, visibility);
        }       
        
        return jpaJaxbResearcherUrlAdapter.toResearcherUrlList(researcherUrlEntities);
    }

    @Override
    public org.orcid.jaxb.model.record_rc2.ResearcherUrl getResearcherUrlV2(String orcid, long id) {
        ResearcherUrlEntity researcherUrlEntity = researcherUrlDao.getResearcherUrl(orcid, id);        
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(researcherUrlEntity);
    }

    @Override
    @Transactional
    public org.orcid.jaxb.model.record_rc2.ResearcherUrl updateResearcherUrlV2(String orcid, org.orcid.jaxb.model.record_rc2.ResearcherUrl researcherUrl) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();                
        // Validate the researcher url
        PersonValidator.validateResearcherUrl(researcherUrl, sourceEntity, false);        
        // Validate it is not duplicated
        List<ResearcherUrlEntity> existingResearcherUrls = researcherUrlDao.getResearcherUrls(orcid);
        
        for (ResearcherUrlEntity existing : existingResearcherUrls) {
            if (isDuplicated(existing, researcherUrl, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "researcher-url");
                params.put("value", researcherUrl.getUrlName());
                throw new OrcidDuplicatedElementException(params);
            }
        }
                
        ResearcherUrlEntity updatedResearcherUrlEntity = researcherUrlDao.getResearcherUrl(orcid, researcherUrl.getPutCode());        
        Visibility originalVisibility = Visibility.fromValue(updatedResearcherUrlEntity.getVisibility().value());        
        SourceEntity existingSource = updatedResearcherUrlEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(researcherUrl, updatedResearcherUrlEntity);        
        updatedResearcherUrlEntity.setLastModified(new Date());
        updatedResearcherUrlEntity.setVisibility(originalVisibility);
        updatedResearcherUrlEntity.setSource(existingSource);
        researcherUrlDao.merge(updatedResearcherUrlEntity);
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(updatedResearcherUrlEntity);
    }

    @Override
    public org.orcid.jaxb.model.record_rc2.ResearcherUrl createResearcherUrlV2(String orcid, org.orcid.jaxb.model.record_rc2.ResearcherUrl researcherUrl) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the researcher url
        PersonValidator.validateResearcherUrl(researcherUrl, sourceEntity, true);
        // Validate it is not duplicated
        List<ResearcherUrlEntity> existingResearcherUrls = researcherUrlDao.getResearcherUrls(orcid);
        for (ResearcherUrlEntity existing : existingResearcherUrls) {
            if (isDuplicated(existing, researcherUrl, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "researcher-url");
                params.put("value", researcherUrl.getUrlName());
                throw new OrcidDuplicatedElementException(params);
            }
        }
        
        ResearcherUrlEntity newEntity = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(researcherUrl);
        ProfileEntity profile = new ProfileEntity(orcid);
        newEntity.setUser(profile);
        newEntity.setDateCreated(new Date());
        newEntity.setSource(sourceEntity);
        setIncomingPrivacy(newEntity, profile);
        researcherUrlDao.persist(newEntity);
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(newEntity);
    }

    private boolean isDuplicated(ResearcherUrlEntity existing, org.orcid.jaxb.model.record_rc2.ResearcherUrl newResearcherUrl, SourceEntity source) {
        if (!existing.getId().equals(newResearcherUrl.getPutCode())) {
            if (existing.getSource() != null) {
                // If they have the same source
                if (!PojoUtil.isEmpty(existing.getSource().getSourceId()) && existing.getSource().getSourceId().equals(source.getSourceId())) {
                    // If the url is the same
                    if (existing.getUrl() != null && existing.getUrl().equals(newResearcherUrl.getUrl().getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setIncomingPrivacy(ResearcherUrlEntity entity, ProfileEntity profile) {
        org.orcid.jaxb.model.common.Visibility incomingWorkVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common.Visibility defaultResearcherUrlsVisibility = profile.getResearcherUrlsVisibility() == null ? org.orcid.jaxb.model.common.Visibility.PRIVATE : org.orcid.jaxb.model.common.Visibility.fromValue(profile.getResearcherUrlsVisibility()
                .value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultResearcherUrlsVisibility.isMoreRestrictiveThan(incomingWorkVisibility)) {
                entity.setVisibility(defaultResearcherUrlsVisibility);
            }
        } else if (incomingWorkVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);
        }
    }        
}
