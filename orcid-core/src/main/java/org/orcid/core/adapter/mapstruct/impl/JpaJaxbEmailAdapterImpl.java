package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbEmailAdapter;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.jpa.entities.EmailEntity;

@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV2.class, VisibilityMapperV2.class}
)
public abstract class JpaJaxbEmailAdapterImpl implements JpaJaxbEmailAdapter {

    @Override
    // Orika's addV2DateFields() used fieldBToA, meaning API submissions do not 
    // overwrite database auditing dates. We ignore them on the way in.
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract EmailEntity toEmailEntity(Email email);


    @Override
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract Email toEmail(EmailEntity entity);


    // ========================================================================
    // Collection Mappings
    // ========================================================================

    @Override
    public abstract List<Email> toEmailList(Collection<EmailEntity> entities);


    @Override
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract EmailEntity toEmailEntity(Email email, @MappingTarget EmailEntity existing);
}