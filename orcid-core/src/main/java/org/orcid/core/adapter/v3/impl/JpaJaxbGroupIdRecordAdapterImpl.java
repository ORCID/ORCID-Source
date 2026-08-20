package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.impl.MapperFacadeSupport;
import org.orcid.core.adapter.v3.JpaJaxbGroupIdRecordAdapter;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

public class JpaJaxbGroupIdRecordAdapterImpl implements JpaJaxbGroupIdRecordAdapter {

    private final MapperFacadeSupport mapperFacade = new MapperFacadeSupport();

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade.setMapperFacade(mapperFacade);
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
    public GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord, GroupIdRecordEntity existing) {
        if (groupIdRecord == null) {
            return null;
        }
        mapperFacade.map(groupIdRecord, existing);
        return existing;
    }
    
    @Override
    public List<GroupIdRecord> toGroupIdRecords(Collection<GroupIdRecordEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, GroupIdRecord.class);
    }
}
