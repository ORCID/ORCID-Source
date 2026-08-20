package org.orcid.core.adapter.v3.impl.mapstruct;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.v3.JpaJaxbClientAdapter;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.client.ClientRedirectUri;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriStatus;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;


@Mapper(componentModel = "spring")
public abstract class JpaJaxbClientAdapterImpl implements JpaJaxbClientAdapter {

    @Autowired
    protected EncryptionManager encryptionManager;


    @Override
    @Mapping(source = "clientName", target = "name")
    @Mapping(source = "clientDescription", target = "description")
    @Mapping(source = "clientWebsite", target = "website")
    @Mapping(source = "id", target = "clientId")
    @Mapping(source = "clientType", target = "clientType")
    @Mapping(source = "groupProfileId", target = "groupProfileId")
    @Mapping(source = "authenticationProviderId", target = "authenticationProviderId")
    @Mapping(source = "persistentTokensEnabled", target = "persistentTokensEnabled")
    @Mapping(source = "userOBOEnabled", target = "userOBOEnabled")
    @Mapping(target = "clientRedirectUris", ignore = true) // Handled in @AfterMapping
    @Mapping(target = "decryptedSecret", ignore = true)    // Handled in @AfterMapping
    @Mapping(target = "oboEnabled", ignore = true)         // Handled in @AfterMapping
    public abstract Client toClient(ClientDetailsEntity entity);

    @AfterMapping
    protected void afterToClient(ClientDetailsEntity entity, @MappingTarget Client client) {
        // Decrypt primary secret
        if (entity.getClientSecrets() != null) {
            for (ClientSecretEntity secretEntity : entity.getClientSecrets()) {
                if (secretEntity.isPrimary()) {
                    String clientSecret = secretEntity.getClientSecret();
                    if (encryptionManager != null && clientSecret != null) {
                        clientSecret = encryptionManager.decryptForInternalUse(clientSecret);
                    }
                    client.setDecryptedSecret(clientSecret);
                }
            }
        }

        // Map registered redirect URIs
        if (entity.getRegisteredRedirectUri() != null) {
            Set<ClientRedirectUri> redirectUris = new HashSet<>();
            for (ClientRedirectUriEntity uriEntity : entity.getClientRegisteredRedirectUris()) {
                ClientRedirectUri element = new ClientRedirectUri();
                element.setRedirectUri(uriEntity.getRedirectUri());
                element.setRedirectUriType(uriEntity.getRedirectUriType());
                element.setUriActType(uriEntity.getUriActType());
                element.setUriGeoArea(uriEntity.getUriGeoArea());
                element.setPredefinedClientScopes(ScopePathType.getScopesFromSpaceSeparatedString(uriEntity.getPredefinedClientScope()));
                if (uriEntity.getStatus() != null) {
                    element.setStatus(uriEntity.getStatus().name());
                }
                redirectUris.add(element);
            }
            client.setClientRedirectUris(redirectUris);
        }

        // Evaluate OBO enabled flag
        if (entity.getAuthorizedGrantTypes() != null && entity.getAuthorizedGrantTypes().contains(OrcidOauth2Constants.IETF_EXCHANGE_GRANT_TYPE)) {
            client.setOboEnabled(true);
        } else {
            client.setOboEnabled(false);
        }
    }

    @Override
    @Mapping(source = "clientName", target = "name")
    @Mapping(source = "clientDescription", target = "description")
    public abstract ClientSummary toClientSummary(ClientDetailsEntity entity);

    @Override
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    @Mapping(source = "allowAutoDeprecate", target = "allowAutoDeprecate")
    // Orika mapped these using fieldBToA (Database -> API only)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientType", ignore = true)
    @Mapping(target = "groupProfileId", ignore = true)
    @Mapping(target = "authenticationProviderId", ignore = true)
    @Mapping(target = "persistentTokensEnabled", ignore = true)
    @Mapping(target = "userOBOEnabled", ignore = true)
    @Mapping(target = "clientRegisteredRedirectUris", ignore = true) // Handled in @AfterMapping
    public abstract ClientDetailsEntity toEntity(Client client);

    @Override
    @Mapping(source = "name", target = "clientName")
    @Mapping(source = "description", target = "clientDescription")
    @Mapping(source = "website", target = "clientWebsite")
    @Mapping(source = "allowAutoDeprecate", target = "allowAutoDeprecate")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "clientType", ignore = true)
    @Mapping(target = "groupProfileId", ignore = true)
    @Mapping(target = "authenticationProviderId", ignore = true)
    @Mapping(target = "persistentTokensEnabled", ignore = true)
    @Mapping(target = "userOBOEnabled", ignore = true)
    @Mapping(target = "clientRegisteredRedirectUris", ignore = true)
    public abstract ClientDetailsEntity toEntity(Client client, @MappingTarget ClientDetailsEntity existing);

    @AfterMapping
    protected void afterToEntity(Client client, @MappingTarget ClientDetailsEntity entity) {
        Map<String, ClientRedirectUriEntity> existingRedirectUriEntitiesMap = new HashMap<>();
        if (entity.getClientRegisteredRedirectUris() != null && !entity.getClientRegisteredRedirectUris().isEmpty()) {
            existingRedirectUriEntitiesMap = ClientRedirectUriEntity.mapByUriAndType(entity.getClientRegisteredRedirectUris());
        }
        
        if (entity.getClientRegisteredRedirectUris() != null) {
            entity.getClientRegisteredRedirectUris().clear();
        } else {
            entity.setClientRegisteredRedirectUris(new TreeSet<>());
        }

        if (client.getClientRedirectUris() != null) {
            for (ClientRedirectUri cru : client.getClientRedirectUris()) {
                String rUriKey = ClientRedirectUriEntity.getUriAndTypeKey(cru.getRedirectUri(), cru.getRedirectUriType());
                if (existingRedirectUriEntitiesMap.containsKey(rUriKey)) {
                    ClientRedirectUriEntity existingEntity = existingRedirectUriEntitiesMap.get(rUriKey);
                    existingEntity.setPredefinedClientScope(ScopePathType.getScopesAsSingleString(cru.getPredefinedClientScopes()));
                    existingEntity.setUriActType(cru.getUriActType());
                    existingEntity.setUriGeoArea(cru.getUriGeoArea());
                    if (cru.getStatus() != null) {
                        existingEntity.setStatus(ClientRedirectUriStatus.valueOf(cru.getStatus()));
                    }
                    entity.getClientRegisteredRedirectUris().add(existingEntity);
                } else {
                    ClientRedirectUriEntity newEntity = new ClientRedirectUriEntity();
                    newEntity.setClientId(entity.getClientId());
                    newEntity.setRedirectUri(cru.getRedirectUri());
                    newEntity.setRedirectUriType(cru.getRedirectUriType());
                    newEntity.setPredefinedClientScope(ScopePathType.getScopesAsSingleString(cru.getPredefinedClientScopes()));
                    newEntity.setUriActType(cru.getUriActType());
                    newEntity.setUriGeoArea(cru.getUriGeoArea());
                    if (cru.getStatus() != null) {
                        newEntity.setStatus(ClientRedirectUriStatus.valueOf(cru.getStatus()));
                    }
                    entity.getClientRegisteredRedirectUris().add(newEntity);
                }
            }
        }
    }


    @Override
    public abstract Set<Client> toClientList(Collection<ClientDetailsEntity> entities);
}