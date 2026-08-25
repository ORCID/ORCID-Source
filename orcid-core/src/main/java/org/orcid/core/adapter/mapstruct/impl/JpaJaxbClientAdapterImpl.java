package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbClientAdapter;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

/**
 * MapStruct implementation for V2 JpaJaxbClientAdapter.
 */
@Mapper(componentModel = "spring")
public abstract class JpaJaxbClientAdapterImpl implements JpaJaxbClientAdapter {

    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientName", target = "name")
    @Mapping(source = "clientDescription", target = "description")
    @Mapping(source = "clientWebsite", target = "website")
    @Mapping(source = "decryptedClientSecret", target = "decryptedSecret")
    public abstract Client toClient(ClientDetailsEntity entity);

    @Override
    @Mapping(source = "clientName", target = "name")
    @Mapping(source = "clientDescription", target = "description")
    public abstract ClientSummary toClientSummary(ClientDetailsEntity entity);

    @Override
    public abstract Set<Client> toClientList(Collection<ClientDetailsEntity> entities);

    // ========================================================================
    // API -> Database (Creation & Update)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    @Mapping(source = "decryptedSecret", target = "decryptedClientSecret")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client);

    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    @Mapping(source = "decryptedSecret", target = "decryptedClientSecret")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client, @MappingTarget ClientDetailsEntity existing);
}