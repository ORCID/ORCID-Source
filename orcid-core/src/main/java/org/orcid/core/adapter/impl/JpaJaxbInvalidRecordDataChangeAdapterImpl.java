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

import org.orcid.core.adapter.JpaJaxbInvalidRecordDataChangeAdapter;
import org.orcid.model.invalid_record_data_change.InvalidRecordDataChange;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbInvalidRecordDataChangeAdapterImpl implements JpaJaxbInvalidRecordDataChangeAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public InvalidRecordDataChange toInvalidRecordDataChange(InvalidRecordDataChangeEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, InvalidRecordDataChange.class);
    }

    @Override
    public List<InvalidRecordDataChange> toInvalidRecordDataChanges(Collection<InvalidRecordDataChangeEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, InvalidRecordDataChange.class);
    }

}
