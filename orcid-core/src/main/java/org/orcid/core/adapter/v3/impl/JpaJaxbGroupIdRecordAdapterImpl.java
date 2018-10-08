package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbGroupIdRecordAdapter;
import org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord;
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
