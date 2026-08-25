package org.orcid.core.adapter.mapstruct.v3.impl;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbNameAdapter;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

@Mapper(
    componentModel = "spring",
    uses = {
        VisibilityMapperV3.class
    }
)
public abstract class JpaJaxbNameAdapterImpl implements JpaJaxbNameAdapter {

    @Override
    @Mapping(source = "path", target = "orcid")
    @Mapping(source = "givenNames.content", target = "givenNames")
    @Mapping(source = "familyName.content", target = "familyName")
    @Mapping(source = "creditName.content", target = "creditName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract RecordNameEntity toRecordNameEntity(Name name);

    @Override
    @Mapping(source = "path", target = "orcid")
    @Mapping(source = "givenNames.content", target = "givenNames")
    @Mapping(source = "familyName.content", target = "familyName")
    @Mapping(source = "creditName.content", target = "creditName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract RecordNameEntity toRecordNameEntity(Name name, @MappingTarget RecordNameEntity existing);

    @Override
    @Mapping(source = "orcid", target = "path")
    @Mapping(source = "givenNames", target = "givenNames.content")
    @Mapping(source = "familyName", target = "familyName.content")
    @Mapping(source = "creditName", target = "creditName.content")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract Name toName(RecordNameEntity entity);
}