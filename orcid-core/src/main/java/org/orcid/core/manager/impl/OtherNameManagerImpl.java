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

import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.version.impl.Api2_0_rc4_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class OtherNameManagerImpl implements OtherNameManager {

    @Resource
    private OtherNameDao otherNameDao;

    @Resource
    private JpaJaxbOtherNameAdapter jpaJaxbOtherNameAdapter;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private SourceManager sourceManager;
    
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
    @Cacheable(value = "other-names", key = "#orcid.concat('-').concat(#lastModified)")
    public OtherNames getOtherNames(String orcid, long lastModified) {
        return getOtherNames(orcid, null);
    }
    
    @Override
    @Cacheable(value = "public-other-names", key = "#orcid.concat('-').concat(#lastModified)")
    public OtherNames getPublicOtherNames(String orcid, long lastModified) {
        return getOtherNames(orcid, Visibility.PUBLIC);        
    }
    
    private OtherNames getOtherNames(String orcid, Visibility visibility) {
        List<OtherNameEntity> otherNameEntityList = new ArrayList<OtherNameEntity>();
        if(visibility == null) {
            otherNameEntityList = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        } else {
            otherNameEntityList = otherNameDao.getOtherNames(orcid, visibility);
        }
        
        OtherNames result = jpaJaxbOtherNameAdapter.toOtherNameList(otherNameEntityList);
        Api2_0_rc4_LastModifiedDatesHelper.calculateLatest(result);
        return result;
    }
    
    @Override
    @Cacheable(value = "minimized-other-names", key = "#orcid.concat('-').concat(#lastModified)")
    public OtherNames getMinimizedOtherNames(String orcid, long lastModified) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherNames(orcid, lastModified);
        return jpaJaxbOtherNameAdapter.toMinimizedOtherNameList(otherNameEntityList);
    }
    

    @Override
    public OtherName getOtherName(String orcid, Long putCode) {        
        OtherNameEntity otherNameEntity = otherNameDao.getOtherName(orcid, putCode);
        return jpaJaxbOtherNameAdapter.toOtherName(otherNameEntity);
    }

    @Override
    public boolean deleteOtherName(String orcid, Long putCode, boolean checkSource) {        
        OtherNameEntity otherNameEntity = otherNameDao.getOtherName(orcid, putCode);        
        
        if(checkSource) {            
            orcidSecurityManager.checkSource(otherNameEntity);
        }        

        try {
            otherNameDao.deleteOtherName(otherNameEntity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override    
    public OtherName createOtherName(String orcid, OtherName otherName, boolean isApiRequest) { 
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the otherName
        PersonValidator.validateOtherName(otherName, sourceEntity, true, isApiRequest, null);
        // Validate it is not duplicated
        List<OtherNameEntity> existingOtherNames = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        for (OtherNameEntity existing : existingOtherNames) {
            if (isDuplicated(existing, otherName, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "other-name");
                params.put("value", otherName.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        OtherNameEntity newEntity = jpaJaxbOtherNameAdapter.toOtherNameEntity(otherName);
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
        otherNameDao.persist(newEntity);
        return jpaJaxbOtherNameAdapter.toOtherName(newEntity);
    }

    @Override
    public OtherName updateOtherName(String orcid, Long putCode, OtherName otherName, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        OtherNameEntity updatedOtherNameEntity = otherNameDao.getOtherName(orcid, putCode);
        Visibility originalVisibility = Visibility.fromValue(updatedOtherNameEntity.getVisibility().value());        
        //Save the original source
        String existingSourceId = updatedOtherNameEntity.getSourceId();
        String existingClientSourceId = updatedOtherNameEntity.getClientSourceId();
        // Validate the other name
        PersonValidator.validateOtherName(otherName, sourceEntity, false, isApiRequest, originalVisibility);

        // Validate it is not duplicated
        List<OtherNameEntity> existingOtherNames = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        for (OtherNameEntity existing : existingOtherNames) {
            if (isDuplicated(existing, otherName, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "otherName");
                params.put("value", otherName.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }
               
        orcidSecurityManager.checkSource(updatedOtherNameEntity);
        jpaJaxbOtherNameAdapter.toOtherNameEntity(otherName, updatedOtherNameEntity);
        updatedOtherNameEntity.setLastModified(new Date());        
        //Be sure it doesn't overwrite the source
        updatedOtherNameEntity.setSourceId(existingSourceId);
        updatedOtherNameEntity.setClientSourceId(existingClientSourceId);
        
        otherNameDao.merge(updatedOtherNameEntity);
        return jpaJaxbOtherNameAdapter.toOtherName(updatedOtherNameEntity);
    }
    
    @Override
    @Transactional
    public OtherNames updateOtherNames(String orcid, OtherNames otherNames) {
        List<OtherNameEntity> existingOtherNamesEntityList = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        //Delete the deleted ones
        for(OtherNameEntity existingOtherName : existingOtherNamesEntityList) {
            boolean deleteMe = true;
            if(otherNames.getOtherNames() != null) {
                for(OtherName updatedOrNew : otherNames.getOtherNames()) {
                    if(existingOtherName.getId().equals(updatedOrNew.getPutCode())) {
                        deleteMe = false;
                        break;
                    }
                }
            }            
            
            if(deleteMe) {
                try {
                    otherNameDao.deleteOtherName(existingOtherName);
                } catch (Exception e) {
                    throw new ApplicationException("Unable to delete other name " + existingOtherName.getId(), e);
                }
            }
        }
        
        if(otherNames != null && otherNames.getOtherNames() != null) {
            for(OtherName updatedOrNew : otherNames.getOtherNames()) {
                if(updatedOrNew.getPutCode() != null) {
                    //Update the existing ones
                   for(OtherNameEntity existingOtherName : existingOtherNamesEntityList) {
                       if(existingOtherName.getId().equals(updatedOrNew.getPutCode())) {
                           existingOtherName.setLastModified(new Date());
                           existingOtherName.setVisibility(updatedOrNew.getVisibility());
                           existingOtherName.setDisplayName(updatedOrNew.getContent());
                           existingOtherName.setDisplayIndex(updatedOrNew.getDisplayIndex());
                           otherNameDao.merge(existingOtherName);
                       }
                   }
                } else {
                    //Add the new ones
                    OtherNameEntity newOtherName = jpaJaxbOtherNameAdapter.toOtherNameEntity(updatedOrNew);
                    SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
                    ProfileEntity profile = new ProfileEntity(orcid);
                    newOtherName.setProfile(profile);
                    newOtherName.setDateCreated(new Date());
                    //Set the source
                    if(sourceEntity.getSourceProfile() != null) {
                        newOtherName.setSourceId(sourceEntity.getSourceProfile().getId());
                    }
                    if(sourceEntity.getSourceClient() != null) {
                        newOtherName.setClientSourceId(sourceEntity.getSourceClient().getId());
                    } 
                    newOtherName.setVisibility(updatedOrNew.getVisibility());
                    newOtherName.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    otherNameDao.persist(newOtherName);
                    
                }
            }
        }
        
        return otherNames;
    }

    private boolean isDuplicated(OtherNameEntity existing, OtherName otherName, SourceEntity source) {
        if (!existing.getId().equals(otherName.getPutCode())) {
            String existingSourceId = existing.getElementSourceId(); 
            if (!PojoUtil.isEmpty(existingSourceId) && existingSourceId.equals(source.getSourceId())) {
                if (existing.getDisplayName() != null && existing.getDisplayName().equals(otherName.getContent())) {
                    return true;
                }
            }            
        }
        return false;
    }

    
    private void setIncomingPrivacy(OtherNameEntity entity, ProfileEntity profile) {
        org.orcid.jaxb.model.common_rc4.Visibility incomingOtherNameVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility defaultOtherNamesVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE : org.orcid.jaxb.model.common_rc4.Visibility.fromValue(profile.getActivitiesVisibilityDefault().value());
        if (profile.getClaimed()) {            
            entity.setVisibility(defaultOtherNamesVisibility);            
        } else if (incomingOtherNameVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE);
        }
    }
}
