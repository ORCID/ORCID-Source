package org.orcid.core.manager.v3.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ResearcherUrlManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.ResearcherUrlManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ResearcherUrlManagerImpl extends ResearcherUrlManagerReadOnlyImpl implements ResearcherUrlManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResearcherUrlManagerImpl.class);

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;        
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Override
    public boolean deleteResearcherUrl(String orcid, Long id, boolean checkSource) {
        boolean result = true;        
        ResearcherUrlEntity toDelete = researcherUrlDao.getResearcherUrl(orcid, id);          
        if(checkSource) {            
            orcidSecurityManager.checkSourceAndThrow(toDelete);
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
                           existingEntity.setVisibility(updatedOrNew.getVisibility().name());                           
                           existingEntity.setUrl(updatedOrNew.getUrl().getValue());
                           existingEntity.setUrlName(updatedOrNew.getUrlName());
                           existingEntity.setDisplayIndex(updatedOrNew.getDisplayIndex());
                           researcherUrlDao.merge(existingEntity);
                       }
                   }
                } else {
                    //Add the new ones
                    ResearcherUrlEntity newResearcherUrl = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(updatedOrNew);
                    Source activeSource = sourceManager.retrieveActiveSource();
                    ProfileEntity profile = new ProfileEntity(orcid);
                    newResearcherUrl.setUser(profile);
                    newResearcherUrl.setDateCreated(new Date());
                    newResearcherUrl.setLastModified(new Date());
                    
                    SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, newResearcherUrl);
                    
                    newResearcherUrl.setVisibility(updatedOrNew.getVisibility().name());
                    newResearcherUrl.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    researcherUrlDao.persist(newResearcherUrl);                    
                }
            }
        }
        
        return researcherUrls;
    }

    @Override
    @Transactional
    public ResearcherUrl updateResearcherUrl(String orcid, ResearcherUrl researcherUrl, boolean isApiRequest) {
        ResearcherUrlEntity updatedResearcherUrlEntity = researcherUrlDao.getResearcherUrl(orcid, researcherUrl.getPutCode());        
        Visibility originalVisibility = Visibility.valueOf(updatedResearcherUrlEntity.getVisibility());
        Source activeSource = sourceManager.retrieveActiveSource();                
        
        //Save the original source
        Source originalSource = SourceEntityUtils.extractSourceFromEntity(updatedResearcherUrlEntity);
        
        // Validate the researcher url
        PersonValidator.validateResearcherUrl(researcherUrl, activeSource, false, isApiRequest, originalVisibility);        
        // Validate it is not duplicated
        List<ResearcherUrlEntity> existingResearcherUrls = researcherUrlDao.getResearcherUrls(orcid, getLastModified(orcid));
        
        for (ResearcherUrlEntity existing : existingResearcherUrls) {
            if (isDuplicated(existing, researcherUrl, activeSource)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "researcher-url");
                params.put("value", researcherUrl.getUrlName());
                throw new OrcidDuplicatedElementException(params);
            }
        }
        
        orcidSecurityManager.checkSourceAndThrow(updatedResearcherUrlEntity);
        jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(researcherUrl, updatedResearcherUrlEntity);        
        updatedResearcherUrlEntity.setLastModified(new Date());
                        
        //Be sure it doesn't overwrite the source
        SourceEntityUtils.populateSourceAwareEntityFromSource(originalSource, updatedResearcherUrlEntity);

        researcherUrlDao.merge(updatedResearcherUrlEntity);
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(updatedResearcherUrlEntity);
    }

    @Override
    public ResearcherUrl createResearcherUrl(String orcid, ResearcherUrl researcherUrl, boolean isApiRequest) { 
        Source activeSource = sourceManager.retrieveActiveSource();
        // Validate the researcher url
        PersonValidator.validateResearcherUrl(researcherUrl, activeSource, true, isApiRequest, null);
        // Validate it is not duplicated
        List<ResearcherUrlEntity> existingResearcherUrls = researcherUrlDao.getResearcherUrls(orcid, getLastModified(orcid));
        for (ResearcherUrlEntity existing : existingResearcherUrls) {
            if (isDuplicated(existing, researcherUrl, activeSource)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "researcher-url");
                params.put("value", researcherUrl.getUrl().getValue());
                throw new OrcidDuplicatedElementException(params);
            }
        }
        
        ResearcherUrlEntity newEntity = jpaJaxbResearcherUrlAdapter.toResearcherUrlEntity(researcherUrl);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        newEntity.setUser(profile);
        newEntity.setDateCreated(new Date());
        
        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, newEntity);
        
        setIncomingPrivacy(newEntity, profile);
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(newEntity, isApiRequest);
        researcherUrlDao.persist(newEntity);
        return jpaJaxbResearcherUrlAdapter.toResearcherUrl(newEntity);
    }

    private boolean isDuplicated(ResearcherUrlEntity existing, ResearcherUrl newResearcherUrl, Source activeSource) {
        if (!existing.getId().equals(newResearcherUrl.getPutCode())) {
            //If they have the same source 
            String existingSourceId = existing.getElementSourceId(); 
            // If they have the same source
            if (!PojoUtil.isEmpty(existingSourceId) && SourceEntityUtils.isTheSameForDuplicateChecking(activeSource,existing)) {
                // If the url is the same
                if (existing.getUrl() != null && existing.getUrl().equals(newResearcherUrl.getUrl().getValue())) {
                    return true;
                }
            }            
        }
        return false;
    }

    private void setIncomingPrivacy(ResearcherUrlEntity entity, ProfileEntity profile) {
        String incomingWorkVisibility = entity.getVisibility();
        String defaultResearcherUrlsVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name() : profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed() != null && profile.getClaimed()) {
            entity.setVisibility(defaultResearcherUrlsVisibility);
        } else if (incomingWorkVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        }
    }

    @Override
    public void removeAllResearcherUrls(String orcid) {
        researcherUrlDao.removeAllResearcherUrls(orcid);
    }        
}
