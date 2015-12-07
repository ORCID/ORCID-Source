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

import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.exception.OtherNameNotFoundException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.OtherNames;
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

    /**
     * Get other names for an specific orcid account
     * 
     * @param orcid
     * @return The list of other names related with the specified orcid profile
     * */
    @Override
    public List<OtherNameEntity> getOtherName(String orcid) {
        return otherNameDao.getOtherName(orcid);
    }

    /**
     * Update other name entity with new values
     * 
     * @param otherName
     * @return true if the other name was sucessfully updated, false otherwise
     * */
    @Override
    public boolean updateOtherName(OtherNameEntity otherName) {
        return otherNameDao.updateOtherName(otherName);
    }

    /**
     * Create other name for the specified account
     * 
     * @param orcid
     * @param displayName
     * @return true if the other name was successfully created, false otherwise
     * */
    @Override
    public boolean addOtherName(String orcid, String displayName) {
        SourceEntity source = sourceManager.retrieveSourceEntity();

        OtherNameEntity newEntity = new OtherNameEntity();
        ProfileEntity profile = new ProfileEntity(orcid);
        newEntity.setProfile(profile);
        newEntity.setDateCreated(new Date());
        newEntity.setSource(source);
        newEntity.setDisplayName(displayName);
        setIncomingPrivacy(newEntity, profile);
        otherNameDao.persist(newEntity);

        return newEntity.getId() == null ? false : true;
    }

    /**
     * Delete other name from database
     * 
     * @param otherName
     * @return true if the other name was successfully deleted, false otherwise
     * */
    @Override
    public boolean deleteOtherName(OtherNameEntity otherName) {
        return otherNameDao.deleteOtherName(otherName);
    }

    /**
     * Get a list of other names and decide which other names might be deleted,
     * and which ones should be created
     * 
     * @param orcid
     * @param List
     *            <String> otherNames
     * */
    @Override
    public void updateOtherNames(String orcid, OtherNames otherNames) {
        SourceEntity source = sourceManager.retrieveSourceEntity();
        List<OtherNameEntity> currentOtherNames = this.getOtherName(orcid);
        Iterator<OtherNameEntity> currentIt = currentOtherNames.iterator();
        ArrayList<String> newOtherNames = new ArrayList<String>(otherNames.getOtherNamesAsStrings());

        while (currentIt.hasNext()) {
            OtherNameEntity existingOtherName = currentIt.next();
            // Delete non modified other names from the parameter list
            if (newOtherNames.contains(existingOtherName.getDisplayName())) {
                newOtherNames.remove(existingOtherName.getDisplayName());
            } else {
                // Delete other names deleted by user
                otherNameDao.deleteOtherName(existingOtherName);
            }
        }

        // At this point, only new other names are in the parameter list
        // otherNames
        // Insert all these other names on database
        for (String newOtherName : newOtherNames) {
            OtherNameEntity newEntity = new OtherNameEntity();
            ProfileEntity profile = new ProfileEntity(orcid);
            newEntity.setProfile(profile);
            newEntity.setDateCreated(new Date());
            newEntity.setSource(source);
            newEntity.setDisplayName(newOtherName);
            setIncomingPrivacy(newEntity, profile);
            otherNameDao.persist(newEntity);
        }
        if (otherNames.getVisibility() != null)
            otherNameDao.updateOtherNamesVisibility(orcid, otherNames.getVisibility());

    }

    @Override
    public org.orcid.jaxb.model.record_rc2.OtherNames getOtherNamesV2(String orcid) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherName(orcid);
        return jpaJaxbOtherNameAdapter.toOtherNameList(otherNameEntityList);
    }
    
    @Override
    public org.orcid.jaxb.model.record_rc2.OtherNames getMinimizedOtherNamesV2(String orcid) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherName(orcid);
        return jpaJaxbOtherNameAdapter.toMinimizedOtherNameList(otherNameEntityList);
    }
    

    @Override
    public org.orcid.jaxb.model.record_rc2.OtherName getOtherNameV2(String orcid, String putCode) {
        long putCodeNum = convertToLong(putCode);
        OtherNameEntity otherNameEntity = otherNameDao.find(putCodeNum);
        if (otherNameEntity == null) {
            throw new OtherNameNotFoundException();
        }
        return jpaJaxbOtherNameAdapter.toOtherName(otherNameEntity);
    }

    @Override
    public boolean deleteOtherNameV2(String orcid, String putCode) {
        long putCodeNum = convertToLong(putCode);
        OtherNameEntity otherNameEntity = otherNameDao.find(putCodeNum);
        if (otherNameEntity == null) {
            throw new OtherNameNotFoundException();
        }
        SourceEntity existingSource = otherNameEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);

        try {
            otherNameDao.deleteOtherName(otherNameEntity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public org.orcid.jaxb.model.record_rc2.OtherName createOtherNameV2(String orcid, org.orcid.jaxb.model.record_rc2.OtherName otherName) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate the otherName
        PersonValidator.validateOtherName(otherName, sourceEntity, true);
        // Validate it is not duplicated
        List<OtherNameEntity> existingOtherNames = otherNameDao.getOtherName(orcid);
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
    public org.orcid.jaxb.model.record_rc2.OtherName updateOtherNameV2(String orcid, String putCode, org.orcid.jaxb.model.record_rc2.OtherName otherName) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Validate the other name
        PersonValidator.validateOtherName(otherName, sourceEntity, false);

        // Validate it is not duplicated
        List<OtherNameEntity> existingOtherNames = otherNameDao.getOtherName(orcid);
        for (OtherNameEntity existing : existingOtherNames) {
            if (isDuplicated(existing, otherName, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "otherName");
                params.put("value", otherName.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }
        long putCodeNum = convertToLong(putCode);
        OtherNameEntity updatedOtherNameEntity = otherNameDao.find(putCodeNum);
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
    public org.orcid.jaxb.model.record_rc2.OtherNames updateOtherNamesV2(String orcid, org.orcid.jaxb.model.record_rc2.OtherNames otherNames, org.orcid.jaxb.model.common.Visibility defaultVisiblity) {
        List<OtherNameEntity> existingOtherNamesEntityList = otherNameDao.getOtherName(orcid);
        //Delete the deleted ones
        for(OtherNameEntity existingOtherName : existingOtherNamesEntityList) {
            boolean deleteMe = true;
            for(org.orcid.jaxb.model.record_rc2.OtherName updatedOrNew : otherNames.getOtherNames()) {
                if(existingOtherName.getId().equals(updatedOrNew.getPutCode())) {
                    deleteMe = false;
                    break;
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
            for(org.orcid.jaxb.model.record_rc2.OtherName updatedOrNew : otherNames.getOtherNames()) {
                if(updatedOrNew.getPutCode() != null) {
                    //Update the existing ones or create new ones
                   for(OtherNameEntity existingOtherName : existingOtherNamesEntityList) {
                       if(existingOtherName.getId().equals(updatedOrNew.getPutCode())) {
                           existingOtherName.setLastModified(new Date());
                           existingOtherName.setVisibility(Visibility.fromValue(defaultVisiblity.value()));
                           existingOtherName.setDisplayName(updatedOrNew.getContent());
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
                    newOtherName.setVisibility(Visibility.fromValue(defaultVisiblity.value()));
                    otherNameDao.persist(newOtherName);
                    
                }
            }
        }
        
        if (defaultVisiblity != null)
            otherNameDao.updateOtherNamesVisibility(orcid, org.orcid.jaxb.model.message.Visibility.fromValue(defaultVisiblity.value()));
        
        return otherNames;
    }

    private boolean isDuplicated(OtherNameEntity existing, org.orcid.jaxb.model.record_rc2.OtherName otherName, SourceEntity source) {
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
        org.orcid.jaxb.model.common.Visibility incomingWorkVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common.Visibility defaultOtherNamesVisibility = profile.getOtherNamesVisibility() == null ? org.orcid.jaxb.model.common.Visibility.PRIVATE
                : org.orcid.jaxb.model.common.Visibility.fromValue(profile.getResearcherUrlsVisibility().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultOtherNamesVisibility.isMoreRestrictiveThan(incomingWorkVisibility)) {
                entity.setVisibility(defaultOtherNamesVisibility);
            }
        } else if (incomingWorkVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);
        }
    }

    private long convertToLong(String param) {
        long returnVal = 0;
        try {
            returnVal = Long.valueOf(param);
        } catch (NumberFormatException e) {
            throw new OrcidValidationException();
        }
        return returnVal;
    }
}
