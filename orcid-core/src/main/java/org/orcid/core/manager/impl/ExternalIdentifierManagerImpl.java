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
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.PersonValidator;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ExternalIdentifierManagerImpl implements ExternalIdentifierManager {

    @Resource
    private ExternalIdentifierDao externalIdentifierDao;

    @Resource
    private JpaJaxbExternalIdentifierAdapter jpaJaxbExternalIdentifierAdapter;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    /**
     * Removes an external identifier from database based on his ID. The ID for
     * external identifiers consists of the "orcid" of the owner and the
     * "externalIdReference" which is an identifier of the external id.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     */
    @Override
    public void removeExternalIdentifier(String orcid, String externalIdReference) {
        externalIdentifierDao.removeExternalIdentifier(orcid, externalIdReference);
    }

    @Override
    public ExternalIdentifiers getPublicExternalIdentifiersV2(String orcid) {
        return getExternalIdentifiesV2(orcid, Visibility.PUBLIC);
    }

    @Override
    public ExternalIdentifiers getExternalIdentifiersV2(String orcid) {
        return getExternalIdentifiesV2(orcid, null);
    }

    private ExternalIdentifiers getExternalIdentifiesV2(String orcid, Visibility visibility) {
        List<ExternalIdentifierEntity> externalIdentifiers = new ArrayList<ExternalIdentifierEntity>();
        if (visibility == null) {
            externalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid);
        } else {
            externalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid, visibility);
        }

        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifierList(externalIdentifiers);
    }

    @Override
    public ExternalIdentifier getExternalIdentifierV2(String orcid, Long id) {  
        ExternalIdentifierEntity entity = externalIdentifierDao.getExternalIdentifierEntity(orcid, id);
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
    }

    @Override
    public ExternalIdentifier createExternalIdentifierV2(String orcid, ExternalIdentifier externalIdentifier) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate external identifier
        PersonValidator.validateExternalIdentifier(externalIdentifier, sourceEntity, true);
        // Validate it is not duplicated
        List<ExternalIdentifierEntity> existingExternalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid);
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
    public ExternalIdentifier updateExternalIdentifierV2(String orcid, ExternalIdentifier externalIdentifier) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        // Validate external identifier
        PersonValidator.validateExternalIdentifier(externalIdentifier, sourceEntity, false);
        // Validate it is not duplicated
        List<ExternalIdentifierEntity> existingExternalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid);
        for (ExternalIdentifierEntity existing : existingExternalIdentifiers) {
            if (isDuplicated(existing, externalIdentifier, sourceEntity)) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type", "external-identifier");
                params.put("value", externalIdentifier.getUrl().getValue());
                throw new OrcidDuplicatedElementException(params);
            }
        }

        ExternalIdentifierEntity updatedExternalIdentifierEntity = externalIdentifierDao.getExternalIdentifierEntity(orcid, externalIdentifier.getPutCode());
        Visibility originalVisibility = Visibility.fromValue(updatedExternalIdentifierEntity.getVisibility().value());
        SourceEntity existingSource = updatedExternalIdentifierEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        jpaJaxbExternalIdentifierAdapter.toExternalIdentifierEntity(externalIdentifier, updatedExternalIdentifierEntity);

        updatedExternalIdentifierEntity.setLastModified(new Date());
        updatedExternalIdentifierEntity.setVisibility(originalVisibility);
        updatedExternalIdentifierEntity.setSource(existingSource);
        externalIdentifierDao.merge(updatedExternalIdentifierEntity);
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(updatedExternalIdentifierEntity);
    }

    private boolean isDuplicated(ExternalIdentifierEntity existing, ExternalIdentifier newExternalIdentifier, SourceEntity source) {
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
        org.orcid.jaxb.model.common.Visibility incomingExternalIdentifierVisibility = entity.getVisibility();
        org.orcid.jaxb.model.common.Visibility defaultExternalIdentifierVisibility = profile.getExternalIdentifiersVisibility() == null
                ? org.orcid.jaxb.model.common.Visibility.PRIVATE : org.orcid.jaxb.model.common.Visibility.fromValue(profile.getExternalIdentifiersVisibility().value());
        if (profile.getClaimed() != null && profile.getClaimed()) {
            if (defaultExternalIdentifierVisibility.isMoreRestrictiveThan(incomingExternalIdentifierVisibility)) {
                entity.setVisibility(defaultExternalIdentifierVisibility);
            }
        } else if (incomingExternalIdentifierVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common.Visibility.PRIVATE);
        }
    }    

    @Override
    public boolean deleteExternalIdentifier(String orcid, Long id) {
        ExternalIdentifierEntity extIdEntity = externalIdentifierDao.find(id);
        if (extIdEntity == null) {
            return false;
        }
        SourceEntity existingSource = extIdEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        return externalIdentifierDao.removeExternalIdentifier(orcid, id);        
    }

}
