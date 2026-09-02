package org.orcid.core.adapter.mapstruct.v3.impl;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbNameAdapter;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
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
    @Mapping(source = "givenNames", target = "givenNames")
    @Mapping(source = "familyName", target = "familyName")
    @Mapping(source = "creditName", target = "creditName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract RecordNameEntity toRecordNameEntity(Name name);

    @Override
    @Mapping(source = "path", target = "orcid")
    @Mapping(source = "givenNames", target = "givenNames")
    @Mapping(source = "familyName", target = "familyName")
    @Mapping(source = "creditName", target = "creditName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract RecordNameEntity toRecordNameEntity(Name name, @MappingTarget RecordNameEntity existing);

    @Override
    @Mapping(source = "orcid", target = "path")
    @Mapping(source = "givenNames", target = "givenNames")
    @Mapping(source = "familyName", target = "familyName")
    @Mapping(source = "creditName", target = "creditName")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract Name toName(RecordNameEntity entity);

    // Custom Wrapper / Empty-String-To-Null Converters
    protected String map(CreditName creditName) {
        return (creditName == null || StringUtils.isBlank(creditName.getContent()))
            ? null : creditName.getContent().trim();
    }

    protected CreditName mapCreditName(String creditName) {
        if (StringUtils.isBlank(creditName)) {
            return null;
        }
        CreditName cn = new CreditName();
        cn.setContent(creditName.trim());
        return cn;
    }

    protected String map(GivenNames givenNames) {
        return (givenNames == null || StringUtils.isBlank(givenNames.getContent()))
            ? null : givenNames.getContent().trim();
    }

    protected GivenNames mapGivenNames(String givenNames) {
        if (StringUtils.isBlank(givenNames)) {
            return null;
        }
        GivenNames gn = new GivenNames();
        gn.setContent(givenNames.trim());
        return gn;
    }

    protected String map(FamilyName familyName) {
        return (familyName == null || StringUtils.isBlank(familyName.getContent()))
            ? null : familyName.getContent().trim();
    }

    protected FamilyName mapFamilyName(String familyName) {
        if (StringUtils.isBlank(familyName)) {
            return null;
        }
        FamilyName fn = new FamilyName();
        fn.setContent(familyName.trim());
        return fn;
    }
}