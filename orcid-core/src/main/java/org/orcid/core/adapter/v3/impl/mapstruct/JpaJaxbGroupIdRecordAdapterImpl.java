package org.orcid.core.adapter.v3.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbGroupIdRecordAdapter;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class
    }
)
public abstract class JpaJaxbGroupIdRecordAdapterImpl implements JpaJaxbGroupIdRecordAdapter {

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "name", target = "groupName")
    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "description", target = "groupDescription")
    @Mapping(source = "type", target = "groupType")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "name", target = "groupName")
    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "description", target = "groupDescription")
    @Mapping(source = "type", target = "groupType")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract GroupIdRecordEntity toGroupIdRecordEntity(GroupIdRecord groupIdRecord, @MappingTarget GroupIdRecordEntity existing);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "groupName", target = "name")
    @Mapping(source = "groupId", target = "groupId")
    @Mapping(source = "groupDescription", target = "description")
    @Mapping(source = "groupType", target = "type")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract GroupIdRecord toGroupIdRecord(GroupIdRecordEntity groupIdRecordEntity);

    @Override
    public abstract List<GroupIdRecord> toGroupIdRecords(Collection<GroupIdRecordEntity> entities);
}