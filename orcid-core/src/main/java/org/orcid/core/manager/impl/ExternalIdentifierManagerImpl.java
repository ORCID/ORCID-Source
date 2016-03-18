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
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.core.version.impl.LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.cache.annotation.Cacheable;

public class ExternalIdentifierManagerImpl implements ExternalIdentifierManager {

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
    
    private long getLastModified(String orcid) {
        Date lastModified = profileEntityManager.getLastModified(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
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
        LastModifiedDatesHelper.calculateLatest(extIds);
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
        PersonValidator.validateExternalIdentifier(externalIdentifier, sourceEntity, true, isApiRequest, null);
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
        ProfileEntity profile = new ProfileEntity(orcid);
        newEntity.setOwner(profile);
        newEntity.setDateCreated(new Date());
        newEntity.setSource(sourceEntity);
        setIncomingPrivacy(newEntity, profile);
        externalIdentifierDao.persist(newEntity);
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(newEntity);
    }

    @Override
    public PersonExternalIdentifier updateExternalIdentifier(String orcid, PersonExternalIdentifier externalIdentifier, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        ExternalIdentifierEntity updatedExternalIdentifierEntity = externalIdentifierDao.getExternalIdentifierEntity(orcid, externalIdentifier.getPutCode());
        Visibility originalVisibility = Visibility.fromValue(updatedExternalIdentifierEntity.getVisibility().value());
        // Validate external identifier
        PersonValidator.validateExternalIdentifier(externalIdentifier, sourceEntity, false, isApiRequest, originalVisibility);
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

        
        SourceEntity existingSource = updatedExternalIdentifierEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(externalIdentifier, updatedExternalIdentifierEntity);
        updatedExternalIdentifierEntity.setLastModified(new Date());        
        updatedExternalIdentifierEntity.setSource(existingSource);
        externalIdentifierDao.merge(updatedExternalIdentifierEntity);
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(updatedExternalIdentifierEntity);
    }

    private boolean isDuplicated(ExternalIdentifierEntity existing, PersonExternalIdentifier newExternalIdentifier, SourceEntity source) {
        if (!existing.getId().equals(newExternalIdentifier.getPutCode())) {
            if (existing.getSource() != null) {
                // If they have the same source
                if (!PojoUtil.isEmpty(existing.getSource().getSourceId()) && existing.getSource().getSourceId().equals(source.getSourceId())) {
                    // If the url is the same
                    if (existing.getExternalIdUrl() != null && existing.getExternalIdUrl().equals(newExternalIdentifier.getUrl().getValue())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setIncomingPrivacy(ExternalIdentifierEntity entity, ProfileEntity profile) {
        org.orcid.jaxb.model.common_rc2.Visibility incomingExternalIdentifierVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common_rc2.Visibility defaultExternalIdentifierVisibility = profile.getExternalIdentifiersVisibility() == null
                ? org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE : org.orcid.jaxb.model.common_rc2.Visibility.fromValue(profile.getExternalIdentifiersVisibility().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultExternalIdentifierVisibility.isMoreRestrictiveThan(incomingExternalIdentifierVisibility)) {
                entity.setVisibility(defaultExternalIdentifierVisibility);
            }
        } else if (incomingExternalIdentifierVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PRIVATE);
        }
    }    

    @Override
    public boolean deleteExternalIdentifier(String orcid, Long id, boolean checkSource) {
        ExternalIdentifierEntity extIdEntity = externalIdentifierDao.getExternalIdentifierEntity(orcid, id);
        if (extIdEntity == null) {
            return false;
        }        
        if(checkSource) {
            SourceEntity existingSource = extIdEntity.getSource();
            orcidSecurityManager.checkSource(existingSource);
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
            if(externalIdentifiers.getExternalIdentifier() != null) {
                for (PersonExternalIdentifier updatedOrNew : externalIdentifiers.getExternalIdentifier()) {
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

        if (externalIdentifiers != null && externalIdentifiers.getExternalIdentifier() != null) {
            for (PersonExternalIdentifier updatedOrNew : externalIdentifiers.getExternalIdentifier()) {
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
