package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbClientAdapter;
import org.orcid.core.adapter.mapstruct.ClientMapperV2;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientRedirectUri;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;

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
    @Mapping(target = "emailAccessReason", ignore = true)
    public abstract Client toClient(ClientDetailsEntity entity);

    @AfterMapping
    protected void populateRedirectUris(ClientDetailsEntity entity, @MappingTarget Client client) {
        if (entity.getClientRegisteredRedirectUris() == null) {
            return;
        }
        Set<ClientRedirectUri> redirectUris = new HashSet<>();
        for (ClientRedirectUriEntity redirectUriEntity : entity.getClientRegisteredRedirectUris()) {
            ClientRedirectUri element = new ClientRedirectUri();
            element.setRedirectUri(redirectUriEntity.getRedirectUri());
            element.setRedirectUriType(redirectUriEntity.getRedirectUriType());
            element.setUriActType(redirectUriEntity.getUriActType());
            element.setUriGeoArea(redirectUriEntity.getUriGeoArea());
            element.setPredefinedClientScopes(ScopePathType.getScopesFromSpaceSeparatedString(redirectUriEntity.getPredefinedClientScope()));
            redirectUris.add(element);
        }
        client.setClientRedirectUris(redirectUris);
    }

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
    @Mapping(target = "authenticationProviderId", ignore = true)
    @Mapping(target = "clientType", ignore = true)
    @Mapping(target = "groupProfileId", ignore = true)
    @Mapping(target = "emailAccessReason", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client);

    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    @Mapping(source = "decryptedSecret", target = "decryptedClientSecret")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "authenticationProviderId", ignore = true)
    @Mapping(target = "groupProfileId", ignore = true)
    @Mapping(target = "clientType", ignore = true)
    @Mapping(target = "emailAccessReason", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client, @MappingTarget ClientDetailsEntity existing);

    @AfterMapping
    protected void populateRedirectUrisEntity(Client client, @MappingTarget ClientDetailsEntity entity) {
        ClientMapperV2.INSTANCE.syncRedirectUrisFromClient(client, entity);
    }
}