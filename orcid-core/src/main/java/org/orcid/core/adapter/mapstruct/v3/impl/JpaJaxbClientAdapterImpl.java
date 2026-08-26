package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.mapstruct.ClientMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbClientAdapter;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.client.ClientRedirectUri;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;

/**
 * MapStruct implementation for V3 JpaJaxbClientAdapter.
 */
@Mapper(componentModel = "spring")
public abstract class JpaJaxbClientAdapterImpl implements JpaJaxbClientAdapter {

    @Autowired
    protected EncryptionManager encryptionManager;

    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientName", target = "name")
    @Mapping(source = "clientDescription", target = "description")
    @Mapping(source = "clientWebsite", target = "website")
    @Mapping(source = "allowAutoDeprecate", target = "allowAutoDeprecate")
    @Mapping(source = "persistentTokensEnabled", target = "persistentTokensEnabled")
    @Mapping(source = "userOBOEnabled", target = "userOBOEnabled")
    @Mapping(source = "clientType", target = "clientType")
    @Mapping(source = "groupProfileId", target = "groupProfileId")
    @Mapping(source = "authenticationProviderId", target = "authenticationProviderId")
    @Mapping(source = "emailAccessReason", target = "emailAccessReason")
    @Mapping(target = "decryptedSecret", ignore = true) // Handled in AfterMapping
    public abstract Client toClient(ClientDetailsEntity entity);

    @AfterMapping
    protected void populateClient(ClientDetailsEntity entity, @MappingTarget Client client) {
        // Runs your custom method to decrypt the secret and calculate oboEnabled
        ClientMapperV3.INSTANCE.populateClientFromEntity(entity, client, encryptionManager);

        // Map Redirect URIs
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
            if (redirectUriEntity.getStatus() != null) {
                element.setStatus(redirectUriEntity.getStatus().name());
            }
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
    // API -> Database (Creation)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    @Mapping(target = "decryptedClientSecret", ignore = true)
    
    // IGNORED: Config fields handled manually by ClientManagerImpl during creation
    @Mapping(target = "allowAutoDeprecate", ignore = true)
    @Mapping(target = "persistentTokensEnabled", ignore = true)
    @Mapping(target = "userOBOEnabled", ignore = true)
    @Mapping(target = "authenticationProviderId", ignore = true)
    @Mapping(target = "emailAccessReason", ignore = true)
    
    // IGNORED: Core/Audit fields
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "clientType", ignore = true)
    @Mapping(target = "groupProfileId", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client);

    // ========================================================================
    // API -> Database (Update Existing)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    @Mapping(target = "decryptedClientSecret", ignore = true)
    
    // IGNORED: Un-editable core fields (Prevents FK Violations)
    @Mapping(target = "groupProfileId", ignore = true)
    @Mapping(target = "clientType", ignore = true)
    
    // IGNORED: Config fields handled manually by ClientManagerImpl during updates
    @Mapping(target = "allowAutoDeprecate", ignore = true)
    @Mapping(target = "persistentTokensEnabled", ignore = true)
    @Mapping(target = "userOBOEnabled", ignore = true)
    @Mapping(target = "authenticationProviderId", ignore = true)
    @Mapping(target = "emailAccessReason", ignore = true)
    
    // IGNORED: Auditing fields
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client, @MappingTarget ClientDetailsEntity existing);

    @AfterMapping
    protected void populateRedirectUrisEntity(Client client, @MappingTarget ClientDetailsEntity entity) {
        ClientMapperV3.INSTANCE.syncRedirectUrisFromClient(client, entity);
    }
}