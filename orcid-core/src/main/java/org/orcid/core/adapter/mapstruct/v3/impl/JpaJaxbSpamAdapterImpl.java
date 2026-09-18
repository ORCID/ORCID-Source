package org.orcid.core.adapter.mapstruct.v3.impl;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.orcid.core.adapter.v3.JpaJaxbSpamAdapter;
import org.orcid.jaxb.model.v3.release.record.Spam;
import org.orcid.persistence.jpa.entities.SpamEntity;

@Mapper(componentModel = "spring")
public abstract class JpaJaxbSpamAdapterImpl implements JpaJaxbSpamAdapter {

    @Override
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract SpamEntity toSpamEntity(Spam spam);

    @Override
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract Spam toSpam(SpamEntity spamEntity);
}