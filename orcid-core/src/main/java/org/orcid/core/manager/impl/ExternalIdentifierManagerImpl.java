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

import org.orcid.core.adapter.JpaJaxbExternalIdentifierAdapter;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.version.impl.Api2_0_rc4_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;

public class ExternalIdentifierManagerImpl implements ExternalIdentifierManager {

    @Value("${org.orcid.core.validations.requireRelationship:false}")
    private boolean requireRelationshipOnExternalIdentifier;
    
    @Resource
    private ExternalIdentifierDao externalIdentifierDao;

    @Resource
    private JpaJaxbExternalIdentifierAdapter jpaJaxbExternalIdentifierAdapter;

    @Resource
    private SourceManager sourceManager;

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
    @Cacheable(value = "public-external-identifiers", key = "#orcid.concat('-').concat(#lastModified)")
    public PersonExternalIdentifiers getPublicExternalIdentifiers(String orcid, long lastModified) {
        return getExternalIdentifies(orcid, Visibility.PUBLIC);
    }

    @Override
    @Cacheable(value = "external-identifiers", key = "#orcid.concat('-').concat(#lastModified)")
    public PersonExternalIdentifiers getExternalIdentifiers(String orcid, long lastModified) {
        return getExternalIdentifies(orcid, null);
    }

    private PersonExternalIdentifiers getExternalIdentifies(String orcid, Visibility visibility) {
        List<ExternalIdentifierEntity> externalIdentifiers = new ArrayList<ExternalIdentifierEntity>();
        if (visibility == null) {
            externalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid, getLastModified(orcid));
        } else {
            externalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid, visibility);
        }

        PersonExternalIdentifiers extIds = jpaJaxbExternalIdentifierAdapter.toExternalIdentifierList(externalIdentifiers);
        Api2_0_rc4_LastModifiedDatesHelper.calculateLatest(extIds);
        return extIds;
    }

    @Override
    public PersonExternalIdentifier getExternalIdentifier(String orcid, Long id) {  
        ExternalIdentifierEntity entity = externalIdentifierDao.getExternalIdentifierEntity(orcid, id);
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
    }

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
        
        Visibility originalVisibility = Visibility.fromValue(updatedExternalIdentifierEntity.getVisibility().value());
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
            if (!PojoUtil.isEmpty(existingSourceId) && existingSourceId.equals(source.getSourceId())) {
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
        org.orcid.jaxb.model.common_rc4.Visibility incomingExternalIdentifierVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common_rc4.Visibility defaultExternalIdentifierVisibility = (profile.getActivitiesVisibilityDefault() == null) ? org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE : org.orcid.jaxb.model.common_rc4.Visibility.fromValue(profile.getActivitiesVisibilityDefault().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            entity.setVisibility(defaultExternalIdentifierVisibility);            
        } else if (incomingExternalIdentifierVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.PRIVATE);
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
                            existingExtId.setVisibility(updatedOrNew.getVisibility());
                            existingExtId.setDisplayIndex(updatedOrNew.getDisplayIndex());
                            externalIdentifierDao.merge(existingExtId);
                        }
                    }
                } 
            }
        }
        return externalIdentifiers;
    }

}
