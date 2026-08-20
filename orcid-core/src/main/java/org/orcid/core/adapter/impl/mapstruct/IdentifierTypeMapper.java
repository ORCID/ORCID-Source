package org.orcid.core.adapter.impl.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.orcid.core.adapter.mapstruct.ExternalIdentifierTypeMapper;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.pojo.IdentifierType;

/**
 * Replaces org.orcid.core.adapter.impl.IdentifierTypePOJOConverter.
 * Spring-managed MapStruct mapper for IdentifierType POJO <-> JPA Entity.
 */
@Mapper(
    componentModel = "spring",
    uses = {ExternalIdentifierTypeMapper.class}
)
public abstract class IdentifierTypeMapper {

    // ========================================================================
    // POJO -> Entity
    // ========================================================================

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "deprecated", target = "isDeprecated")
    @Mapping(source = "caseSensitive", target = "isCaseSensitive")
    // Preserve original logic: auditing timestamps are ignored during POJO-to-Entity mapping
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract IdentifierTypeEntity fromPojo(IdentifierType id);


    // ========================================================================
    // Entity -> POJO
    // ========================================================================

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "isDeprecated", target = "deprecated")
    @Mapping(source = "isCaseSensitive", target = "caseSensitive")
    public abstract IdentifierType fromEntity(IdentifierTypeEntity entity);
}