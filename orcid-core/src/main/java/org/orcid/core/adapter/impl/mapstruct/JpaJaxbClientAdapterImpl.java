package org.orcid.core.adapter.impl.mapstruct;

import java.util.Collection;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.JpaJaxbClientAdapter;
import org.orcid.core.adapter.mapstruct.ClientMapperV2;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

@Mapper(componentModel = "spring")
public abstract class JpaJaxbClientAdapterImpl implements JpaJaxbClientAdapter {

    @Autowired
    protected EncryptionManager encryptionManager;

    // --- Entity to Client mappings ---

    @Override
    @Mapping(source = "clientName", target = "name")
    @Mapping(source = "clientDescription", target = "description")
    @Mapping(source = "clientWebsite", target = "website")
    @Mapping(source = "id", target = "clientId")
    public abstract Client toClient(ClientDetailsEntity entity);

    /**
     * Replaces Orika's mapBtoA logic.
     * Populates redirect URIs and primary client secret from the entity.
     */
    @AfterMapping
    protected void populateClientAfterMapping(ClientDetailsEntity entity, @MappingTarget Client client) {
        if (entity != null && client != null) {
            ClientMapperV2.INSTANCE.populateClientFromEntity(entity, client, encryptionManager);
        }
    }

    @Override
    @Mapping(source = "clientName", target = "name")
    @Mapping(source = "clientDescription", target = "description")
    public abstract ClientSummary toClientSummary(ClientDetailsEntity entity);

    @Override
    public abstract Set<Client> toClientList(Collection<ClientDetailsEntity> entities);


    // --- Client to Entity mappings ---

    @Override
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    // Orika fieldBToA mapped these to the API, but ignored them when updating the DB
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientType", ignore = true)
    @Mapping(target = "groupProfileId", ignore = true)
    @Mapping(target = "authenticationProviderId", ignore = true)
    @Mapping(target = "persistentTokensEnabled", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client);

    @Override
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientType", ignore = true)
    @Mapping(target = "groupProfileId", ignore = true)
    @Mapping(target = "authenticationProviderId", ignore = true)
    @Mapping(target = "persistentTokensEnabled", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client, @MappingTarget ClientDetailsEntity existing);

    /**
     * Replaces Orika's mapAtoB logic.
     * Syncs redirect URIs back to the database entity.
     */
    @AfterMapping
    protected void syncRedirectUrisAfterMapping(Client client, @MappingTarget ClientDetailsEntity entity) {
        if (client != null && entity != null) {
            ClientMapperV2.INSTANCE.syncRedirectUrisFromClient(client, entity);
        }
    }
}
