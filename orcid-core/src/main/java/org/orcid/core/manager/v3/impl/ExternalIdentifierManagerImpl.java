package org.orcid.core.manager.v3.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.v3.ExternalIdentifierManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.ExternalIdentifierManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifiers;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;

public class ExternalIdentifierManagerImpl extends ExternalIdentifierManagerReadOnlyImpl implements ExternalIdentifierManager {

    @Value("${org.orcid.core.validations.requireRelationship:false}")
    private boolean requireRelationshipOnExternalIdentifier;
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Override
    public PersonExternalIdentifier createExternalIdentifier(String orcid, PersonExternalIdentifier externalIdentifier, boolean isApiRequest) { 
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate external identifier
        PersonValidator.validateExternalIdentifier(externalIdentifier, sourceEntity, true, isApiRequest, null, requireRelationshipOnExternalIdentifier);
        // Validate it is not duplicated
        List<ExternalIdentifierEntity> existingExternalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid, getLastModified(orcid));
        for (ExternalIdentifierEntity existing : existingExternalIdentifiers) {
            if (isDuplicated(existing, externalIdentifier, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "external-identifier");
                params.put("value", externalIdentifier.getUrl().getValue());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        ExternalIdentifierEntity newEntity = jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(externalIdentifier);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        newEntity.setOwner(profile);
        newEntity.setDateCreated(new Date());
        
        if(sourceEntity.getSourceProfile() != null) {
            newEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        if(sourceEntity.getSourceClient() != null) {
            newEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        }
                
        setIncomingPrivacy(newEntity, profile);
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(newEntity, isApiRequest);
        externalIdentifierDao.persist(newEntity);
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(newEntity);
    }

    @Override
    public PersonExternalIdentifier updateExternalIdentifier(String orcid, PersonExternalIdentifier externalIdentifier, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        ExternalIdentifierEntity updatedExternalIdentifierEntity = externalIdentifierDao.getExternalIdentifierEntity(orcid, externalIdentifier.getPutCode());
        
        //Save the original source
        String existingSourceId = updatedExternalIdentifierEntity.getSourceId();
        String existingClientSourceId = updatedExternalIdentifierEntity.getClientSourceId();
        
        Visibility originalVisibility = Visibility.valueOf(updatedExternalIdentifierEntity.getVisibility());
        // Validate external identifier
        PersonValidator.validateExternalIdentifier(externalIdentifier, sourceEntity, false, isApiRequest, originalVisibility, requireRelationshipOnExternalIdentifier);
        // Validate it is not duplicated
        List<ExternalIdentifierEntity> existingExternalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid, getLastModified(orcid));
        for (ExternalIdentifierEntity existing : existingExternalIdentifiers) {
            if (isDuplicated(existing, externalIdentifier, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "external-identifier");
                params.put("value", externalIdentifier.getUrl().getValue());
                throw new OrcidDuplicatedElementException(params);
            }
        }
        orcidSecurityManager.checkSource(updatedExternalIdentifierEntity);
        jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(externalIdentifier, updatedExternalIdentifierEntity);
        updatedExternalIdentifierEntity.setLastModified(new Date());        
                
        //Set source
        updatedExternalIdentifierEntity.setSourceId(existingSourceId);
        updatedExternalIdentifierEntity.setClientSourceId(existingClientSourceId);
        
        externalIdentifierDao.merge(updatedExternalIdentifierEntity);
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(updatedExternalIdentifierEntity);
    }

    private boolean isDuplicated(ExternalIdentifierEntity existing, PersonExternalIdentifier newExternalIdentifier, SourceEntity source) {
        if (!existing.getId().equals(newExternalIdentifier.getPutCode())) {
            // If they have the same source
            String existingSourceId = existing.getElementSourceId();
            if (!PojoUtil.isEmpty(existingSourceId) && existingSourceId.equals(SourceEntityUtils.getSourceId(source))) {
                // And they have the same reference
                if ((PojoUtil.isEmpty(existing.getExternalIdReference()) && PojoUtil.isEmpty(newExternalIdentifier.getValue()))
                        || (!PojoUtil.isEmpty(existing.getExternalIdReference()) && existing.getExternalIdReference().equals(newExternalIdentifier.getValue()))) {
                    // And they have the same type
                    if ((PojoUtil.isEmpty(existing.getExternalIdCommonName()) && PojoUtil.isEmpty(newExternalIdentifier.getType()))
                            || (!PojoUtil.isEmpty(existing.getExternalIdCommonName()) && existing.getExternalIdCommonName().equals(newExternalIdentifier.getType()))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setIncomingPrivacy(ExternalIdentifierEntity entity, ProfileEntity profile) {
        String incomingExternalIdentifierVisibility = entity.getVisibility();
        String defaultExternalIdentifierVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name() : profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed() != null && profile.getClaimed()) {
            entity.setVisibility(defaultExternalIdentifierVisibility);            
        } else if (incomingExternalIdentifierVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        }
    }    

    @Override
    public boolean deleteExternalIdentifier(String orcid, Long id, boolean checkSource) {
        ExternalIdentifierEntity extIdEntity = externalIdentifierDao.getExternalIdentifierEntity(orcid, id);
        if (extIdEntity == null) {
            return false;
        }        
        if(checkSource) {
            orcidSecurityManager.checkSource(extIdEntity);
        }
        try {
            externalIdentifierDao.removeExternalIdentifier(orcid, id);
        } catch (Exception e) {
            return false;
        }
        return true;    
    }

    @Override
    public PersonExternalIdentifiers updateExternalIdentifiers(String orcid, PersonExternalIdentifiers externalIdentifiers) {
        List<ExternalIdentifierEntity> existingExternalIdentifiersList = externalIdentifierDao.getExternalIdentifiers(orcid, getLastModified(orcid));        
        // Delete the deleted ones
        for (ExternalIdentifierEntity existing : existingExternalIdentifiersList) {
            boolean deleteMe = true;
            if(externalIdentifiers.getExternalIdentifiers() != null) {
                for (PersonExternalIdentifier updatedOrNew : externalIdentifiers.getExternalIdentifiers()) {
                    if (existing.getId().equals(updatedOrNew.getPutCode())) {
                        deleteMe = false;
                        break;
                    }
                }
            }            

            if (deleteMe) {
                try {
                    externalIdentifierDao.removeExternalIdentifier(orcid, existing.getId());
                } catch (Exception e) {
                    throw new ApplicationException("Unable to delete external identifier " + existing.getId(), e);
                }
            }
        }

        if (externalIdentifiers != null && externalIdentifiers.getExternalIdentifiers() != null) {
            for (PersonExternalIdentifier updatedOrNew : externalIdentifiers.getExternalIdentifiers()) {
                if (updatedOrNew.getPutCode() != null) {
                    // Update the existing ones
                    for (ExternalIdentifierEntity existingExtId : existingExternalIdentifiersList) {
                        if (existingExtId.getId().equals(updatedOrNew.getPutCode())) {
                            existingExtId.setLastModified(new Date());
                            existingExtId.setVisibility(updatedOrNew.getVisibility().name());
                            existingExtId.setDisplayIndex(updatedOrNew.getDisplayIndex());
                            externalIdentifierDao.merge(existingExtId);
                        }
                    }
                } 
            }
        }
        return externalIdentifiers;
    }

    @Override
    public void removeAllExternalIdentifiers(String orcid) {
        externalIdentifierDao.removeAllExternalIdentifiers(orcid);
    }

}
