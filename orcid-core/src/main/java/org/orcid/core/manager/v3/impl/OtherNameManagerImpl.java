package org.orcid.core.manager.v3.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.OtherNameManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.OtherNameManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.transaction.annotation.Transactional;

public class OtherNameManagerImpl extends OtherNameManagerReadOnlyImpl implements OtherNameManager {

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;     
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;        
        
    @Override
    @Transactional
    public boolean deleteOtherName(String orcid, Long putCode, boolean checkSource) {        
        OtherNameEntity otherNameEntity = otherNameDao.getOtherName(orcid, putCode);        
        
        if(checkSource) {            
            orcidSecurityManager.checkSourceAndThrow(otherNameEntity);
        }        

        try {
            otherNameDao.deleteOtherName(otherNameEntity);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override    
    @Transactional
    public OtherName createOtherName(String orcid, OtherName otherName, boolean isApiRequest) { 
        Source activeSource = sourceManager.retrieveActiveSource();
        // Validate the otherName
        PersonValidator.validateOtherName(otherName, activeSource, true, isApiRequest, null);
        // Validate it is not duplicated
        List<OtherNameEntity> existingOtherNames = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        for (OtherNameEntity existing : existingOtherNames) {
            if (isDuplicated(existing, otherName, activeSource)) {
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
        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, newEntity);
         
        setIncomingPrivacy(newEntity, profile);
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(newEntity, isApiRequest);
        otherNameDao.persist(newEntity);
        return jpaJaxbOtherNameAdapter.toOtherName(newEntity);
    }

    @Override
    @Transactional
    public OtherName updateOtherName(String orcid, Long putCode, OtherName otherName, boolean isApiRequest) {
        Source activeSource = sourceManager.retrieveActiveSource();
        OtherNameEntity updatedOtherNameEntity = otherNameDao.getOtherName(orcid, putCode);
        Visibility originalVisibility = Visibility.fromValue(updatedOtherNameEntity.getVisibility());        
        //Save the original source
        Source originalSource = SourceEntityUtils.extractSourceFromEntity(updatedOtherNameEntity);
        // Validate the other name
        PersonValidator.validateOtherName(otherName, activeSource, false, isApiRequest, originalVisibility);

        // Validate it is not duplicated
        List<OtherNameEntity> existingOtherNames = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        for (OtherNameEntity existing : existingOtherNames) {
            if (isDuplicated(existing, otherName, activeSource)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "otherName");
                params.put("value", otherName.getContent());
                throw new OrcidDuplicatedElementException(params);
            }
        }
               
        orcidSecurityManager.checkSourceAndThrow(updatedOtherNameEntity);
        jpaJaxbOtherNameAdapter.toOtherNameEntity(otherName, updatedOtherNameEntity);
        updatedOtherNameEntity.setLastModified(new Date());        
        //Be sure it doesn't overwrite the source
        SourceEntityUtils.populateSourceAwareEntityFromSource(originalSource, updatedOtherNameEntity);
        
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
                           existingOtherName.setVisibility(updatedOrNew.getVisibility().name());
                           existingOtherName.setDisplayName(updatedOrNew.getContent());
                           existingOtherName.setDisplayIndex(updatedOrNew.getDisplayIndex());
                           otherNameDao.merge(existingOtherName);
                       }
                   }
                } else {
                    //Add the new ones
                    OtherNameEntity newOtherName = jpaJaxbOtherNameAdapter.toOtherNameEntity(updatedOrNew);
                    Source activeSource = sourceManager.retrieveActiveSource();
                    ProfileEntity profile = new ProfileEntity(orcid);
                    newOtherName.setProfile(profile);
                    newOtherName.setDateCreated(new Date());
                    //Set the source
                    SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, newOtherName);
                    newOtherName.setVisibility(updatedOrNew.getVisibility().name());
                    newOtherName.setDisplayIndex(updatedOrNew.getDisplayIndex());
                    otherNameDao.persist(newOtherName);
                }
            }
        }        
        return otherNames;
    }

    private boolean isDuplicated(OtherNameEntity existing, OtherName otherName, Source activeSource) {
        if (!existing.getId().equals(otherName.getPutCode())) {
            String existingSourceId = existing.getElementSourceId(); 
            if (!PojoUtil.isEmpty(existingSourceId) && SourceEntityUtils.isTheSameForDuplicateChecking(activeSource,existing)) {
                if (existing.getDisplayName() != null && existing.getDisplayName().equals(otherName.getContent())) {
                    return true;
                }
            }            
        }
        return false;
    }
    
    private void setIncomingPrivacy(OtherNameEntity entity, ProfileEntity profile) {
        String incomingOtherNameVisibility = entity.getVisibility();
        String defaultOtherNamesVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name() : profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {            
            entity.setVisibility(defaultOtherNamesVisibility);            
        } else if (incomingOtherNameVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        }
    }

    @Override
    public void removeAllOtherNames(String orcid) {
        otherNameDao.removeAllOtherNames(orcid);
    }
}
