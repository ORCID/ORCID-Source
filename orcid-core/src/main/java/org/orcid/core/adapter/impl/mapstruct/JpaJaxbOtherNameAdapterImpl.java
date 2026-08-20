package org.orcid.core.adapter.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV2.class, VisibilityMapperV2.class}
)
public abstract class JpaJaxbOtherNameAdapterImpl implements JpaJaxbOtherNameAdapter {

    @Override
    public OtherNameEntity toOtherNameEntity(OtherName otherName) {
        if (otherName == null) {
            return null;
        }

        OtherNameEntity result = mapToEntity(otherName);

        // Preserve original logic: default display index to 0 for new entities
        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }

        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "content", target = "displayName")
    // Orika mapped these using fieldBToA (Database -> API only)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract OtherNameEntity mapToEntity(OtherName otherName);


    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "displayName", target = "content")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract OtherName toOtherName(OtherNameEntity entity);


    // Collection Mappings
    @Override
    public OtherNames toOtherNameList(Collection<OtherNameEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<OtherName> otherNameList = toOtherNameListInternal(entities);

        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(otherNameList);
        return otherNames;
    }

    @Override
    public OtherNames toMinimizedOtherNameList(Collection<OtherNameEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<OtherName> otherNameList = toOtherNameListInternal(entities);

        // Preserve minimization logic: scrub dates and sources
        for (OtherName otherName : otherNameList) {
            otherName.setCreatedDate(null);
            otherName.setSource(null);
        }

        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(otherNameList);
        return otherNames;
    }

    protected abstract List<OtherName> toOtherNameListInternal(Collection<OtherNameEntity> entities);


    // API -> Database (Update Existing)
    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "content", target = "displayName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract OtherNameEntity toOtherNameEntity(OtherName otherName, @MappingTarget OtherNameEntity existing);
}
