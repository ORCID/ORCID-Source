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

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.JpaJaxbExternalIdentifierAdapter;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbExternalIdentifierAdapterImpl implements JpaJaxbExternalIdentifierAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public ExternalIdentifierEntity toExternalIdentifierEntity(ExternalIdentifier externalIdentifier) {
        if (externalIdentifier == null) {
            return null;
        }
        
        ExternalIdentifierEntity result = mapperFacade.map(externalIdentifier, ExternalIdentifierEntity.class);
        if(result.getDisplayIndex() == null) {
            result.setDisplayIndex(-1L);
        }
        
        return result;
    }

    @Override
    public ExternalIdentifier toExternalIdentifier(ExternalIdentifierEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, ExternalIdentifier.class);
    }

    @Override
    public ExternalIdentifiers toExternalIdentifierList(Collection<ExternalIdentifierEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<ExternalIdentifier> externalIdentifier = mapperFacade.mapAsList(entities, ExternalIdentifier.class);
        ExternalIdentifiers externalIdentifiers = new ExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers(externalIdentifier);
        return externalIdentifiers;
    }

    @Override
    public ExternalIdentifierEntity toExternalIdentifierEntity(ExternalIdentifier externalIdentifier, ExternalIdentifierEntity existing) {
        if (externalIdentifier == null) {
            return null;
        }

        mapperFacade.map(externalIdentifier, existing);

        return existing;
    }

}
