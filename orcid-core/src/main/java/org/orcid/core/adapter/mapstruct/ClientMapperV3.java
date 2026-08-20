package org.orcid.core.adapter.mapstruct;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.client.ClientRedirectUri;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriStatus;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

@Mapper
public interface ClientMapperV3 {

    ClientMapperV3 INSTANCE = Mappers.getMapper(ClientMapperV3.class);
    String IETF_EXCHANGE_GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";

    default void syncRedirectUrisFromClient(Client client, ClientDetailsEntity entity) {
        Map<String, ClientRedirectUriEntity> existingRedirectUriEntitiesMap = new java.util.HashMap<String, ClientRedirectUriEntity>();
        if (entity.getClientRegisteredRedirectUris() != null && !entity.getClientRegisteredRedirectUris().isEmpty()) {
            existingRedirectUriEntitiesMap = ClientRedirectUriEntity.mapByUriAndType(entity.getClientRegisteredRedirectUris());
        }
        if (entity.getClientRegisteredRedirectUris() != null) {
            entity.getClientRegisteredRedirectUris().clear();
        } else {
            entity.setClientRegisteredRedirectUris(new TreeSet<ClientRedirectUriEntity>());
        }

        if (client.getClientRedirectUris() != null) {
            for (ClientRedirectUri cru : client.getClientRedirectUris()) {
                String rUriKey = ClientRedirectUriEntity.getUriAndTypeKey(cru.getRedirectUri(), cru.getRedirectUriType());
                if (existingRedirectUriEntitiesMap.containsKey(rUriKey)) {
                    ClientRedirectUriEntity existingEntity = existingRedirectUriEntitiesMap.get(rUriKey);
                    existingEntity.setPredefinedClientScope(ScopePathType.getScopesAsSingleString(cru.getPredefinedClientScopes()));
                    existingEntity.setUriActType(cru.getUriActType());
                    existingEntity.setUriGeoArea(cru.getUriGeoArea());
                    existingEntity.setStatus(ClientRedirectUriStatus.valueOf(cru.getStatus()));
                    entity.getClientRegisteredRedirectUris().add(existingEntity);
                } else {
                    ClientRedirectUriEntity newEntity = new ClientRedirectUriEntity();
                    newEntity.setClientId(entity.getClientId());
                    newEntity.setRedirectUri(cru.getRedirectUri());
                    newEntity.setRedirectUriType(cru.getRedirectUriType());
                    newEntity.setPredefinedClientScope(ScopePathType.getScopesAsSingleString(cru.getPredefinedClientScopes()));
                    newEntity.setUriActType(cru.getUriActType());
                    newEntity.setUriGeoArea(cru.getUriGeoArea());
                    newEntity.setStatus(ClientRedirectUriStatus.valueOf(cru.getStatus()));
                    entity.getClientRegisteredRedirectUris().add(newEntity);
                }
            }
        }
    }

    default void populateClientFromEntity(ClientDetailsEntity entity, Client client, EncryptionManager encryptionManager) {
        if (entity.getClientSecrets() != null) {
            for (ClientSecretEntity secretEntity : entity.getClientSecrets()) {
                if (secretEntity.isPrimary()) {
                    String clientSecret = secretEntity.getClientSecret();
                    if (encryptionManager != null) {
                        clientSecret = encryptionManager.decryptForInternalUse(clientSecret);
                    }
                    client.setDecryptedSecret(clientSecret);
                }
            }
        }
        if (entity.getRegisteredRedirectUri() != null) {
            Set<ClientRedirectUri> redirectUris = new HashSet<ClientRedirectUri>();
            for (ClientRedirectUriEntity redirectUriEntity : entity.getClientRegisteredRedirectUris()) {
                ClientRedirectUri element = new ClientRedirectUri();
                element.setRedirectUri(redirectUriEntity.getRedirectUri());
                element.setRedirectUriType(redirectUriEntity.getRedirectUriType());
                element.setUriActType(redirectUriEntity.getUriActType());
                element.setUriGeoArea(redirectUriEntity.getUriGeoArea());
                element.setPredefinedClientScopes(ScopePathType.getScopesFromSpaceSeparatedString(redirectUriEntity.getPredefinedClientScope()));
                element.setStatus(redirectUriEntity.getStatus().name());
                redirectUris.add(element);
            }
            client.setClientRedirectUris(redirectUris);
        }
        if (entity.getAuthorizedGrantTypes() != null && entity.getAuthorizedGrantTypes().contains(IETF_EXCHANGE_GRANT_TYPE)) {
            client.setOboEnabled(true);
        } else {
            client.setOboEnabled(false);
        }
    }
}
