package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbExternalIdentifierAdapter;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV2.class, VisibilityMapperV2.class}
)
public abstract class JpaJaxbExternalIdentifierAdapterImpl implements JpaJaxbExternalIdentifierAdapter {


    @Override
    public ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier) {
        if (externalIdentifier == null) {
            return null;
        }
        
        ExternalIdentifierEntity result = mapToEntity(externalIdentifier);
        
        // Preserve original logic: default display index to 0 for new entities
        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "type", target = "externalIdCommonName")
    @Mapping(source = "value", target = "externalIdReference")
    @Mapping(source = "url.value", target = "externalIdUrl")
    // Orika used fieldBToA for these, meaning we ignore them on the way in
    @Mapping(target = "displayIndex", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    protected abstract ExternalIdentifierEntity mapToEntity(PersonExternalIdentifier externalIdentifier);


    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "externalIdCommonName", target = "type")
    @Mapping(source = "externalIdReference", target = "value")
    @Mapping(source = "externalIdUrl", target = "url.value")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract PersonExternalIdentifier toExternalIdentifier(ExternalIdentifierEntity entity);

    /**
     * Replaces the manual setting of Relationship.SELF.
     * This hook is automatically called by MapStruct after creating a PersonExternalIdentifier,
     * including when iterating through collections.
     */
    @AfterMapping
    protected void setRelationshipConstant(@MappingTarget PersonExternalIdentifier result) {
        if (result != null) {
            result.setRelationship(Relationship.SELF);
        }
    }

    @Override
    public PersonExternalIdentifiers toExternalIdentifierList(Collection<ExternalIdentifierEntity> entities) {
        if (entities == null) {
            return null;
        }

        // MapStruct generates the loop, and triggers the @AfterMapping hook for every item
        List<PersonExternalIdentifier> externalIdentifierList = toExternalIdentifierListInternal(entities);
        
        PersonExternalIdentifiers externalIdentifiers = new PersonExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers(externalIdentifierList);
        
        return externalIdentifiers;
    }

    protected abstract List<PersonExternalIdentifier> toExternalIdentifierListInternal(Collection<ExternalIdentifierEntity> entities);


    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "type", target = "externalIdCommonName")
    @Mapping(source = "value", target = "externalIdReference")
    @Mapping(source = "url.value", target = "externalIdUrl")
    @Mapping(target = "displayIndex", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier, @MappingTarget ExternalIdentifierEntity existing);
}
