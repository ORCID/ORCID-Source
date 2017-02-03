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
package org.orcid.core.adapter.impl;

import java.util.Date;

import org.orcid.core.adapter.impl.jsonidentifiers.ExternalIdentifierTypeConverter;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.pojo.IdentifierType;

public class IdentifierTypePOJOConverter {

    private ExternalIdentifierTypeConverter externalIdentifierTypeConverter = new ExternalIdentifierTypeConverter();

    public IdentifierTypeEntity fromPojo(IdentifierType id){
        IdentifierTypeEntity entity = new IdentifierTypeEntity();
        entity.setId(id.getPutCode());
        entity.setName(externalIdentifierTypeConverter.convertTo(id.getName(),null));
        entity.setIsDeprecated(id.getDeprecated());
        entity.setResolutionPrefix(id.getResolutionPrefix());
        entity.setValidationRegex(id.getValidationRegex());   
        entity.setDateCreated(id.getDateCreated());
        entity.setLastModified(id.getLastModified());
        entity.setSourceClient(id.getSourceClient());
        entity.setPrimaryUse(id.getPrimaryUse());
        entity.setIsCaseSensitive(id.getCaseSensitive());
        return entity;
    }
    
    public IdentifierType fromEntity(IdentifierTypeEntity entity){
        IdentifierType id = new IdentifierType();
        id.setPutCode(entity.getId());
        id.setName(externalIdentifierTypeConverter.convertFrom(entity.getName(),null));
        id.setDeprecated(entity.getIsDeprecated());
        id.setResolutionPrefix(entity.getResolutionPrefix());
        id.setValidationRegex(entity.getValidationRegex());   
        id.setDateCreated(new Date(entity.getDateCreated().getTime()));
        id.setLastModified(new Date(entity.getLastModified().getTime()));
        id.setSourceClient(entity.getSourceClient());
        id.setCaseSensitive(entity.getIsCaseSensitive());
        id.setPrimaryUse(entity.getPrimaryUse());
        return id;
    }
}
