package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbOtherNameAdapter;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class
    }
)
public abstract class JpaJaxbOtherNameAdapterImpl implements JpaJaxbOtherNameAdapter {
    @Override
    public OtherNameEntity toOtherNameEntity(OtherName otherName) {
        if (otherName == null) {
            return null;
        }

        OtherNameEntity result = mapToEntity(otherName);

        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }

        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "content", target = "displayName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract OtherNameEntity mapToEntity(OtherName otherName);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "content", target = "displayName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract OtherNameEntity toOtherNameEntity(OtherName otherName, @MappingTarget OtherNameEntity existing);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "displayName", target = "content")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract OtherName toOtherName(OtherNameEntity entity);


    // ========================================================================
    // Collection Mappings
    // ========================================================================

    @Override
    public OtherNames toOtherNameList(Collection<OtherNameEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<OtherName> list = toOtherNameListInternal(entities);
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(list);
        return otherNames;
    }

    @Override
    public OtherNames toMinimizedOtherNameList(Collection<OtherNameEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<OtherName> list = toOtherNameListInternal(entities);
        for (OtherName otherName : list) {
            otherName.setCreatedDate(null);
            otherName.setSource(null);
        }

        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(list);
        return otherNames;
    }

    protected abstract List<OtherName> toOtherNameListInternal(Collection<OtherNameEntity> entities);
}