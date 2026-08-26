package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbGroupIdRecordAdapter;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV2.class}
)
public abstract class JpaJaxbGroupIdRecordAdapterImpl implements JpaJaxbGroupIdRecordAdapter {


    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "groupName", target = "name")
    @Mapping(source = "groupDescription", target = "description")
    @Mapping(source = "groupType", target = "type")
    // MapStruct automatically maps "groupId" because the names match on both sides
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract GroupIdRecord toGroupIdRecord(GroupIdRecordEntity groupIdRecordEntity);


    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "name", target = "groupName")
    @Mapping(source = "description", target = "groupDescription")
    @Mapping(source = "type", target = "groupType")
    // Orika mapped auditing dates using fieldBToA (Database -> API only). 
    // We explicitly ignore them here so API updates don't overwrite DB timestamps.
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord);


    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "name", target = "groupName")
    @Mapping(source = "description", target = "groupDescription")
    @Mapping(source = "type", target = "groupType")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord, @MappingTarget GroupIdRecordEntity existing);

    @Override
    public abstract List<GroupIdRecord> toGroupIdRecords(Collection<GroupIdRecordEntity> entities);
}
