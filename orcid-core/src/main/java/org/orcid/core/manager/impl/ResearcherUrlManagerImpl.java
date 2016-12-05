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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.version.impl.Api2_0_rc4_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ResearcherUrlDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
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
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    private long getLastModified(String orcid) {
        Date lastModified = profileEntityManager.getLastModified(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
    }

    @Override
    public void setSourceManager(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }
    
    @Override
    public boolean deleteResearcherUrl(String orcid, Long id, boolean checkSource) {
        boolean result = true;        
        ResearcherUrlEntity toDelete = researcherUrlDao.getResearcherUrl(orcid, id);          
        if(checkSource) {            
            orcidSecurityManager.checkSource(toDelete);
        }
        
        try {            
            researcherUrlDao.deleteResearcherUrl(orcid, id);
        } catch(Exception e) {
            LOGGER.error("Unable to delete researcherUrl with ID: " + id);
            result = false;
        }
        return result;
    }   

    /**
     * Update the researcher urls associated with a specific account
     * 
     * @param orcid
     * @param researcherUrls
     * */
    @Override
    @Transactional
    public ResearcherUrls updateResearcherUrls(String orcid, ResearcherUrls researcherUrls) {
        List<ResearcherUrlEntity> existingEntities = researcherUrlDao.getResearcherUrls(orcid, getLastModified(orcid));
        //Delete the deleted ones
        for(ResearcherUrlEntity existingEntity : existingEntities) {
            boolean deleteMe = true;
            if(researcherUrls.getResearcherUrls() != null) {
                for(ResearcherUrl updatedOrNew : researcherUrls.getResearcherUrls()) {
                    if(existingEntity.getId().equals(updatedOrNew.getPutCode())) {
                        deleteMe = false;
                        break;
                    }
                }
            }            
            
            if(deleteMe) {
                try {
                    researcherUrlDao.deleteResearcherUrl(existingEntity.getProfile().getId(), existingEntity.getId());
                } catch (Exception e) {
                    throw new ApplicationException("Unable to delete researcher url " + existingEntity.getId(), e);
                }
            }
        }
        
        // Update or create new
        if(researcherUrls != null && researcherUrls.getResearcherUrls() != null) {
            for(ResearcherUrl updatedOrNew : researcherUrls.getResearcherUrls()) {
                if(updatedOrNew.getPutCode() != null) {
                    //Update the existing ones
                   for(ResearcherUrlEntity existingEntity : existingEntities) {
                       if(existingEntity.getId().equals(updatedOrNew.getPutCode())) {
                           existingEntity.setLastModified(new Date());
                           existingEntity.setVisibility(updatedOrNew.getVisibility());                           
                           existingEntity.setUrl(updatedOrNew.getUrl().getValue());
                           existingEntity.setUrlName(updatedOrNew.getUrlName());
                           existingEntity.setDisplayIndex(updatedOrNew.getDisplayIndex());
                           researcherUrlDao.merge(existingEntity);
                       }
                   }
                } else {
                    //Add the new ones
                    ResearcherUrlEntity newResearcherUrl = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(updatedOrNew);
                    SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
                    ProfileEntity profile = new ProfileEntity(orcid);
                    newResearcherUrl.setUser(profile);
                    newResearcherUrl.setDateCreated(new Date());
                    newResearcherUrl.setLastModified(new Date());
                    
                    if(sourceEntity.getSourceProfile() != null) {
                        newResearcherUrl.setSourceId(sourceEntity.getSourceProfile().getId());
                    }
                    if(sourceEntity.getSourceClient() != null) {
                        newResearcherUrl.setClientSourceId(sourceEntity.getSourceClient().getId());
                    }
                    
                    newResearcherUrl.setVisibility(updatedOrNew.getVisibility());
                    newResearcherUrl.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    researcherUrlDao.persist(newResearcherUrl);                    
                }
            }
        }
        
        return researcherUrls;
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
            researcherUrlEntities = researcherUrlDao.getResearcherUrls(orcid, visibility);
        }       
        
        ResearcherUrls rUrls = jpaJaxbResearcherUrlAdapter.toResearcherUrlList(researcherUrlEntities);
        Api2_0_rc4_LastModifiedDatesHelper.calculateLatest(rUrls);
        return rUrls;
    }

    @Override
    public org.orcid.jaxb.model.record_rc4.ResearcherUrl getResearcherUrl(String orcid, long id) {
        ResearcherUrlEntity researcherUrlEntity = researcherUrlDao.getResearcherUrl(orcid, id);        
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(researcherUrlEntity);
    }

    @Override
    @Transactional
    public ResearcherUrl updateResearcherUrl(String orcid, ResearcherUrl researcherUrl, boolean isApiRequest) {
        ResearcherUrlEntity updatedResearcherUrlEntity = researcherUrlDao.getResearcherUrl(orcid, researcherUrl.getPutCode());        
        Visibility originalVisibility = Visibility.fromValue(updatedResearcherUrlEntity.getVisibility().value());
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();                
        
        //Save the original source
        String existingSourceId = updatedResearcherUrlEntity.getSourceId();
        String existingClientSourceId = updatedResearcherUrlEntity.getClientSourceId();
        
        
        // Validate the researcher url
        PersonValidator.validateResearcherUrl(researcherUrl, sourceEntity, false, isApiRequest, originalVisibility);        
        // Validate it is not duplicated
        List<ResearcherUrlEntity> existingResearcherUrls = researcherUrlDao.getResearcherUrls(orcid, getLastModified(orcid));
        
        for (ResearcherUrlEntity existing : existingResearcherUrls) {
            if (isDuplicated(existing, researcherUrl, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "researcher-url");
                params.put("value", researcherUrl.getUrlName());
                throw new OrcidDuplicatedElementException(params);
            }
        }
        
        orcidSecurityManager.checkSource(updatedResearcherUrlEntity);
        jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(researcherUrl, updatedResearcherUrlEntity);        
        updatedResearcherUrlEntity.setLastModified(new Date());
                        
        //Be sure it doesn't overwrite the source
        updatedResearcherUrlEntity.setSourceId(existingSourceId);
        updatedResearcherUrlEntity.setClientSourceId(existingClientSourceId);
        
        researcherUrlDao.merge(updatedResearcherUrlEntity);
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(updatedResearcherUrlEntity);
    }

    @Override
    public ResearcherUrl createResearcherUrl(String orcid, ResearcherUrl researcherUrl, boolean isApiRequest) { 
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the researcher url
        PersonValidator.validateResearcherUrl(researcherUrl, sourceEntity, true, isApiRequest, null);
        // Validate it is not duplicated
        List<ResearcherUrlEntity> existingResearcherUrls = researcherUrlDao.getResearcherUrls(orcid, getLastModified(orcid));
        for (ResearcherUrlEntity existing : existingResearcherUrls) {
            if (isDuplicated(existing, researcherUrl, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "researcher-url");
                params.put("value", researcherUrl.getUrlName());
                throw new OrcidDuplicatedElementException(params);
            }
        }
        
        ResearcherUrlEntity newEntity = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(researcherUrl);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        newEntity.setUser(profile);
        newEntity.setDateCreated(new Date());
        
        //Set the source
        if(sourceEntity.getSourceProfile() != null) {
                newEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        if(sourceEntity.getSourceClient() != null) {
                newEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        } 
        
        setIncomingPrivacy(newEntity, profile);
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(newEntity, isApiRequest);
        researcherUrlDao.persist(newEntity);
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(newEntity);
    }

    private boolean isDuplicated(ResearcherUrlEntity existing, ResearcherUrl newResearcherUrl, SourceEntity source) {
        if (!existing.getId().equals(newResearcherUrl.getPutCode())) {
            //If they have the same source 
            String existingSourceId = existing.getElementSourceId(); 
            // If they have the same source
            if (!PojoUtil.isEmpty(existingSourceId) && existingSourceId.equals(source.getSourceId())) {
                // If the url is the same
                if (existing.getUrl() != null && existing.getUrl().equals(newResearcherUrl.getUrl().getValue())) {
                    return true;
                }
            }            
        }
        return false;
    }

    private void setIncomingPrivacy(ResearcherUrlEntity entity, ProfileEntity profile) {
        org.orcid.jaxb.model.common_rc4.Visibility incomingWorkVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility defaultResearcherUrlsVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE : org.orcid.jaxb.model.common_rc4.Visibility.fromValue(profile.getActivitiesVisibilityDefault().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            entity.setVisibility(defaultResearcherUrlsVisibility);
        } else if (incomingWorkVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE);
        }
    }        
}
