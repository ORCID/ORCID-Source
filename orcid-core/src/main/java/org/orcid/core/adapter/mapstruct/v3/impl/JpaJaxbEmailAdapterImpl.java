package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbEmailAdapter;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.jpa.entities.EmailEntity;

/**
 * MapStruct automatically generates the implementation and registers it as a Spring Component.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class
    }
)
public abstract class JpaJaxbEmailAdapterImpl implements JpaJaxbEmailAdapter {

    @Override
    @Mapping(source = "verificationDate.value", target = "dateVerified")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract EmailEntity toEmailEntity(Email email);

    @Override
    @Mapping(source = "verificationDate.value", target = "dateVerified")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract EmailEntity toEmailEntity(Email email, @MappingTarget EmailEntity existing);

    @Override
    @Mapping(source = "dateVerified", target = "verificationDate.value")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract Email toEmail(EmailEntity entity);


    @Override
    public abstract List<Email> toEmailList(Collection<EmailEntity> entities);
}