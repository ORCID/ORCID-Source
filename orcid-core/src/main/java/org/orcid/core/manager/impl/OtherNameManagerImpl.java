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

import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.exception.OtherNameNotFoundException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
    
    @Override
    public OtherNames getOtherNames(String orcid) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherNames(orcid);
        return jpaJaxbOtherNameAdapter.toOtherNameList(otherNameEntityList);
    }
    
    @Override
    public OtherNames getPublicOtherNames(String orcid) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherNames(orcid, Visibility.PUBLIC);
        return jpaJaxbOtherNameAdapter.toOtherNameList(otherNameEntityList);
    }
    
    @Override
    public OtherNames getMinimizedOtherNames(String orcid) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherNames(orcid);
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
            SourceEntity existingSource = otherNameEntity.getSource();
            orcidSecurityManager.checkSource(existingSource);
        }        

        try {
            otherNameDao.deleteOtherName(otherNameEntity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public OtherName createOtherName(String orcid, OtherName otherName) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the otherName
        PersonValidator.validateOtherName(otherName, sourceEntity, true);
        // Validate it is not duplicated
        List<OtherNameEntity> existingOtherNames = otherNameDao.getOtherNames(orcid);
        for (OtherNameEntity existing : existingOtherNames) {
            if (isDuplicated(existing, otherName, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "other-name");
                params.put("value", otherName.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        OtherNameEntity newEntity = jpaJaxbOtherNameAdapter.toOtherNameEntity(otherName);
        ProfileEntity profile = new ProfileEntity(orcid);
        newEntity.setProfile(profile);
        newEntity.setDateCreated(new Date());
        newEntity.setSource(sourceEntity);
        setIncomingPrivacy(newEntity, profile);
        otherNameDao.persist(newEntity);
        return jpaJaxbOtherNameAdapter.toOtherName(newEntity);
    }

    @Override
    public OtherName updateOtherName(String orcid, Long putCode, OtherName otherName) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Validate the other name
        PersonValidator.validateOtherName(otherName, sourceEntity, false);

        // Validate it is not duplicated
        List<OtherNameEntity> existingOtherNames = otherNameDao.getOtherNames(orcid);
        for (OtherNameEntity existing : existingOtherNames) {
            if (isDuplicated(existing, otherName, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "otherName");
                params.put("value", otherName.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }
       
        OtherNameEntity updatedOtherNameEntity = otherNameDao.find(putCode);
        if (updatedOtherNameEntity == null) {
            throw new OtherNameNotFoundException();
        }

        Visibility originalVisibility = Visibility.fromValue(updatedOtherNameEntity.getVisibility().value());
        SourceEntity existingSource = updatedOtherNameEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        jpaJaxbOtherNameAdapter.toOtherNameEntity(otherName, updatedOtherNameEntity);
        updatedOtherNameEntity.setLastModified(new Date());
        updatedOtherNameEntity.setVisibility(originalVisibility);
        updatedOtherNameEntity.setSource(existingSource);
        otherNameDao.merge(updatedOtherNameEntity);
        return jpaJaxbOtherNameAdapter.toOtherName(updatedOtherNameEntity);
    }
    
    @Override
    @Transactional
    public OtherNames updateOtherNames(String orcid, OtherNames otherNames, org.orcid.jaxb.model.common.Visibility defaultVisibility) {
        List<OtherNameEntity> existingOtherNamesEntityList = otherNameDao.getOtherNames(orcid);
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
                    newOtherName.setSource(sourceEntity);
                    newOtherName.setVisibility(updatedOrNew.getVisibility());
                    newOtherName.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    otherNameDao.persist(newOtherName);
                    
                }
            }
        }
        
        if (defaultVisibility != null)
            otherNameDao.updateOtherNamesVisibility(orcid, org.orcid.jaxb.model.message.Visibility.fromValue(defaultVisibility.value()));
        
        return otherNames;
    }

    private boolean isDuplicated(OtherNameEntity existing, OtherName otherName, SourceEntity source) {
        if (!existing.getId().equals(otherName.getPutCode())) {
            if (existing.getSource() != null) {
                if (!PojoUtil.isEmpty(existing.getSource().getSourceId()) && existing.getSource().getSourceId().equals(source.getSourceId())) {
                    if (existing.getDisplayName() != null && existing.getDisplayName().equals(otherName.getContent())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setIncomingPrivacy(OtherNameEntity entity, ProfileEntity profile) {
        org.orcid.jaxb.model.common.Visibility incomingOtherNameVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common.Visibility defaultOtherNamesVisibility = profile.getOtherNamesVisibility() == null ? org.orcid.jaxb.model.common.Visibility.fromValue(OrcidVisibilityDefaults.OTHER_NAMES_DEFAULT.getVisibility().value())
                : org.orcid.jaxb.model.common.Visibility.fromValue(profile.getOtherNamesVisibility().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultOtherNamesVisibility.isMoreRestrictiveThan(incomingOtherNameVisibility)) {
                entity.setVisibility(defaultOtherNamesVisibility);
            }
        } else if (incomingOtherNameVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);
        }
    }
}
