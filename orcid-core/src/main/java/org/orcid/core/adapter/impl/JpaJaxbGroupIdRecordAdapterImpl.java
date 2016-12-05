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

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbGroupIdRecordAdapter;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public class JpaJaxbGroupIdRecordAdapterImpl implements JpaJaxbGroupIdRecordAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public GroupIdRecord toGroupIdRecord(GroupIdRecordEntity groupIdRecordEntity) {
        if (groupIdRecordEntity == null) {
            return null;
        }
        return mapperFacade.map(groupIdRecordEntity, GroupIdRecord.class);
    }

    @Override
    public GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord) {
        if (groupIdRecord == null) {
            return null;
        }
        return mapperFacade.map(groupIdRecord, GroupIdRecordEntity.class);
    }

    @Override
    public List<GroupIdRecord> toGroupIdRecords(Collection<GroupIdRecordEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, GroupIdRecord.class);
    }
}
