package org.orcid.core.manager.v3.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ProfileKeywordManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.ProfileKeywordManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.jaxb.model.v3.dev1.record.Keywords;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ProfileKeywordManagerImpl extends ProfileKeywordManagerReadOnlyImpl implements ProfileKeywordManager {

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
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
        List<ProfileKeywordEntity> existingKeywords = profileKeywordDao.getProfileKeywords(orcid, getLastModified(orcid));
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
        Visibility originalVisibility = Visibility.valueOf(updatedEntity.getVisibility());
        
        //Save the original source
        String existingSourceId = updatedEntity.getSourceId();
        String existingClientSourceId = updatedEntity.getClientSourceId();
                
        // Validate the keyword
        PersonValidator.validateKeyword(keyword, sourceEntity, false, isApiRequest, originalVisibility);
        // Validate it is not duplicated
        List<ProfileKeywordEntity> existingKeywords = profileKeywordDao.getProfileKeywords(orcid, getLastModified(orcid));
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
        List<ProfileKeywordEntity> existingKeywordsList = profileKeywordDao.getProfileKeywords(orcid, getLastModified(orcid));        
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
                            existingKeyword.setVisibility(updatedOrNew.getVisibility().name());
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
                    newKeyword.setVisibility(updatedOrNew.getVisibility().name());
                    newKeyword.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    profileKeywordDao.persist(newKeyword);

                }
            }
        }
        
        return keywords;
    }

    private boolean isDuplicated(ProfileKeywordEntity existing, org.orcid.jaxb.model.v3.dev1.record.Keyword keyword, SourceEntity source) {
        if (!existing.getId().equals(keyword.getPutCode())) {
            String existingSourceId = existing.getElementSourceId();             
            if (!PojoUtil.isEmpty(existingSourceId) && existingSourceId.equals(SourceEntityUtils.getSourceId(source))) {
                if (existing.getKeywordName() != null && existing.getKeywordName().equals(keyword.getContent())) {
                    return true;
                }
            }           
        }
        return false;
    }

    private void setIncomingPrivacy(ProfileKeywordEntity entity, ProfileEntity profile) {
        String incomingKeywordVisibility = entity.getVisibility();
        String defaultKeywordVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name() : profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed() != null && profile.getClaimed()) {
            entity.setVisibility(defaultKeywordVisibility);
        } else if (incomingKeywordVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        }
    }

    @Override
    public void removeAllKeywords(String orcid) {
        profileKeywordDao.removeAllKeywords(orcid);
    }
}
