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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.version.impl.Api2_0_rc4_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.cache.annotation.Cacheable;

public class ProfileKeywordManagerImpl implements ProfileKeywordManager {

    @Resource
    private ProfileKeywordDao profileKeywordDao;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private JpaJaxbKeywordAdapter adapter;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
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
    @Cacheable(value = "keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public Keywords getKeywords(String orcid, long lastModified) {
        return getKeywords(orcid, null);
    }

    @Override
    @Cacheable(value = "public-keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public Keywords getPublicKeywords(String orcid, long lastModified) {
        return getKeywords(orcid, Visibility.PUBLIC);
    }

    private Keywords getKeywords(String orcid, Visibility visibility) {
        List<ProfileKeywordEntity> entities = new ArrayList<ProfileKeywordEntity>();
        if(visibility == null) {
            entities = profileKeywordDao.getProfileKeywors(orcid, getLastModified(orcid));
        } else {
            entities = profileKeywordDao.getProfileKeywors(orcid, Visibility.PUBLIC);
        }
        
        Keywords result = adapter.toKeywords(entities);
        Api2_0_rc4_LastModifiedDatesHelper.calculateLatest(result);
        return result;
    }       

    @Override
    public Keyword getKeyword(String orcid, Long putCode) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);
        return adapter.toKeyword(entity);
    }

    @Override
    public boolean deleteKeyword(String orcid, Long putCode, boolean checkSource) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);

        if (checkSource) {
            orcidSecurityManager.checkSource(entity);
        }

        try {
            profileKeywordDao.deleteProfileKeyword(entity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override    
    public Keyword createKeyword(String orcid, Keyword keyword, boolean isApiRequest) { 
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the keyword
        PersonValidator.validateKeyword(keyword, sourceEntity, true, isApiRequest, null);
        // Validate it is not duplicated
        List<ProfileKeywordEntity> existingKeywords = profileKeywordDao.getProfileKeywors(orcid, getLastModified(orcid));
        for (ProfileKeywordEntity existing : existingKeywords) {
            if (isDuplicated(existing, keyword, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "keyword");
                params.put("value", keyword.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        ProfileKeywordEntity newEntity = adapter.toProfileKeywordEntity(keyword);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        newEntity.setProfile(profile);
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
        profileKeywordDao.persist(newEntity);
        return adapter.toKeyword(newEntity);
    }

    @Override
    @Transactional
    public Keyword updateKeyword(String orcid, Long putCode, Keyword keyword, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        ProfileKeywordEntity updatedEntity = profileKeywordDao.getProfileKeyword(orcid, putCode);
        Visibility originalVisibility = Visibility.fromValue(updatedEntity.getVisibility().value());
        
        //Save the original source
        String existingSourceId = updatedEntity.getSourceId();
        String existingClientSourceId = updatedEntity.getClientSourceId();
                
        // Validate the keyword
        PersonValidator.validateKeyword(keyword, sourceEntity, false, isApiRequest, originalVisibility);
        // Validate it is not duplicated
        List<ProfileKeywordEntity> existingKeywords = profileKeywordDao.getProfileKeywors(orcid, getLastModified(orcid));
        for (ProfileKeywordEntity existing : existingKeywords) {
            if (isDuplicated(existing, keyword, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "keyword");
                params.put("value", keyword.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }
                
        orcidSecurityManager.checkSource(updatedEntity);
        
        adapter.toProfileKeywordEntity(keyword, updatedEntity);
        updatedEntity.setLastModified(new Date());        
        
        //Be sure it doesn't overwrite the source
        updatedEntity.setSourceId(existingSourceId);
        updatedEntity.setClientSourceId(existingClientSourceId);
        
        profileKeywordDao.merge(updatedEntity);
        return adapter.toKeyword(updatedEntity);
    }

    @Override
    @Transactional
    public Keywords updateKeywords(String orcid, Keywords keywords) {
        List<ProfileKeywordEntity> existingKeywordsList = profileKeywordDao.getProfileKeywors(orcid, getLastModified(orcid));        
        // Delete the deleted ones
        for (ProfileKeywordEntity existing : existingKeywordsList) {
            boolean deleteMe = true;
            if(keywords.getKeywords() != null) {
                for (Keyword updatedOrNew : keywords.getKeywords()) {
                    if (existing.getId().equals(updatedOrNew.getPutCode())) {
                        deleteMe = false;
                        break;
                    }
                }
            }            

            if (deleteMe) {
                try {
                    profileKeywordDao.deleteProfileKeyword(existing);
                } catch (Exception e) {
                    throw new ApplicationException("Unable to delete keyword " + existing.getId(), e);
                }
            }
        }

        if (keywords != null && keywords.getKeywords() != null) {
            for (Keyword updatedOrNew : keywords.getKeywords()) {
                if (updatedOrNew.getPutCode() != null) {
                    // Update the existing ones
                    for (ProfileKeywordEntity existingKeyword : existingKeywordsList) {
                        if (existingKeyword.getId().equals(updatedOrNew.getPutCode())) {
                            existingKeyword.setLastModified(new Date());
                            existingKeyword.setVisibility(updatedOrNew.getVisibility());
                            existingKeyword.setKeywordName(updatedOrNew.getContent());
                            existingKeyword.setDisplayIndex(updatedOrNew.getDisplayIndex());
                            profileKeywordDao.merge(existingKeyword);
                        }
                    }
                } else {
                    // Add the new ones
                    ProfileKeywordEntity newKeyword = adapter.toProfileKeywordEntity(updatedOrNew);
                    SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
                    ProfileEntity profile = new ProfileEntity(orcid);
                    newKeyword.setProfile(profile);
                    newKeyword.setDateCreated(new Date());
                    //Set the source
                    if(sourceEntity.getSourceProfile() != null) {
                        newKeyword.setSourceId(sourceEntity.getSourceProfile().getId());
                    }
                    if(sourceEntity.getSourceClient() != null) {
                        newKeyword.setClientSourceId(sourceEntity.getSourceClient().getId());
                    } 
                    newKeyword.setVisibility(updatedOrNew.getVisibility());
                    newKeyword.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    profileKeywordDao.persist(newKeyword);

                }
            }
        }
        
        return keywords;
    }

    private boolean isDuplicated(ProfileKeywordEntity existing, org.orcid.jaxb.model.record_rc4.Keyword keyword, SourceEntity source) {
        if (!existing.getId().equals(keyword.getPutCode())) {
            String existingSourceId = existing.getElementSourceId();             
            if (!PojoUtil.isEmpty(existingSourceId) && existingSourceId.equals(source.getSourceId())) {
                if (existing.getKeywordName() != null && existing.getKeywordName().equals(keyword.getContent())) {
                    return true;
                }
            }           
        }
        return false;
    }

    private void setIncomingPrivacy(ProfileKeywordEntity entity, ProfileEntity profile) {
        org.orcid.jaxb.model.common_rc4.Visibility incomingKeywordVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility defaultKeywordVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE : org.orcid.jaxb.model.common_rc4.Visibility.fromValue(profile.getActivitiesVisibilityDefault().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            entity.setVisibility(defaultKeywordVisibility);
        } else if (incomingKeywordVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE);
        }
    }
}
