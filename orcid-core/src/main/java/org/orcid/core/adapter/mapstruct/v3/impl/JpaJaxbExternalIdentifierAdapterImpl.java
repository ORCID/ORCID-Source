package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.UrlMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbExternalIdentifierAdapter;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        UrlMapperV3.class,
        VisibilityMapperV3.class
    }
)
public abstract class JpaJaxbExternalIdentifierAdapterImpl implements JpaJaxbExternalIdentifierAdapter {

    @Override
    public ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier) {
        if (externalIdentifier == null) {
            return null;
        }

        ExternalIdentifierEntity result = mapToEntity(externalIdentifier);

        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }

        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "type", target = "externalIdCommonName")
    @Mapping(source = "value", target = "externalIdReference")
    @Mapping(source = "url", target = "externalIdUrl")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract ExternalIdentifierEntity mapToEntity(PersonExternalIdentifier externalIdentifier);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "type", target = "externalIdCommonName")
    @Mapping(source = "value", target = "externalIdReference")
    @Mapping(source = "url", target = "externalIdUrl")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier, @MappingTarget ExternalIdentifierEntity existing);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "externalIdCommonName", target = "type")
    @Mapping(source = "externalIdReference", target = "value")
    @Mapping(source = "externalIdUrl", target = "url")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract PersonExternalIdentifier toExternalIdentifier(ExternalIdentifierEntity entity);

    @AfterMapping
    protected void afterToExternalIdentifier(ExternalIdentifierEntity entity, @MappingTarget PersonExternalIdentifier result) {
        result.setRelationship(Relationship.SELF);
    }

    @Override
    public PersonExternalIdentifiers toExternalIdentifierList(Collection<ExternalIdentifierEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<PersonExternalIdentifier> list = toExternalIdentifierListInternal(entities);
        PersonExternalIdentifiers result = new PersonExternalIdentifiers();
        result.setExternalIdentifiers(list);
        return result;
    }

    protected abstract List<PersonExternalIdentifier> toExternalIdentifierListInternal(Collection<ExternalIdentifierEntity> entities);
}