/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.apache.commons.lang.time.DateUtils;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientGrantedAuthorityEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientResourceIdEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.transaction.annotation.Transactional;

public class ClientDetailsManagerImpl implements ClientDetailsManager {

    @Resource
    ClientDetailsDao clientDetailsDao;

    @Resource
    ClientSecretDao clientSecretDao;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ProfileDao profileDao;

    /**
     * Load a client by the client id. This method must NOT return null.
     * 
     * @param clientId
     *            The client id.
     * @return The client details.
     * @throws org.springframework.security.oauth2.common.exceptions.OAuth2Exception
     *             If the client account is locked, expired, disabled, or for
     *             any other reason.
     */
    @Override
    public ClientDetailsEntity loadClientByClientId(String clientId) throws OAuth2Exception {
        ClientDetailsEntity clientDetails = findByClientId(clientId);
        if (clientDetails != null) {
            clientDetails.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(clientDetails.getClientSecretForJpa()));
            return clientDetails;
        } else {
            throw new InvalidClientException("Client not found: " + clientId);
        }
    }

    /**
     * Creates a new client without any knowledge of the client id or secret.
     * This can be called to create an initial client without the caller having
     * any knowledge of the clientId, and, or the client secret. This will then
     * be randomly generated and returned to the caller as part of the
     * {@link ClientDetailsEntity}
     * 
     * @param name
     *            The client name
     * @param description
     *            The client description
     * @param website
     *            The client website
     * @param clientScopes
     *            the scopes that this client can request
     * @param clientResourceIds
     *            the resource ids that this client has access to
     * @param clientAuthorizedGrantTypes
     *            the grant types that this client has been granted. Clients
     *            will commonly be granted "client_credentials" and
     *            "authorization_code"
     * @param clientRegisteredRedirectUris
     *            The redirect URIs that can be legally requested by the client.
     * @param clientGrantedAuthorities
     *            the authorities that can be used to. These are likely to be
     *            only "ROLE_CLIENT"
     * @return
     */
    @Override
    public ClientDetailsEntity createClientDetails(String orcid, String name, String description, String website, Set<String> clientScopes,
            Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes, Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities) {
        ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("ORCID does not exist for " + orcid + " cannot continue");
        } else {
            String clientSecret = encryptionManager.encryptForInternalUse(UUID.randomUUID().toString());
            StringBuilder clientId = new StringBuilder(profileEntity.getId());
            return populateClientDetailsEntity(clientId.toString(), profileEntity, name, description, website, clientSecret, clientScopes, clientResourceIds,
                    clientAuthorizedGrantTypes, clientRegisteredRedirectUris, clientGrantedAuthorities);
        }
    }

    /**
     * Creates a new {@link ClientDetailsEntity} using the component parts, and
     * not the underyling entity directly.
     * 
     * @param name
     *            The client name
     * @param description
     *            The client description
     * @param website
     *            The client website
     * @param clientId
     *            the client id that will be used to retrieve this entity from
     *            the database
     * @param clientSecret
     *            the secret that will be used for authentication/authorisation
     * @param clientScopes
     *            the scopes that this client can request
     * @param clientResourceIds
     *            the resource ids that this client has access to
     * @param clientAuthorizedGrantTypes
     *            the grant types that this client has been granted. Clients
     *            will commonly be granted "client_credentials" and
     *            "authorization_code"
     * @param clientRegisteredRedirectUris
     *            The redirect URIs that can be legally requested by the client.
     * @param clientGrantedAuthorities
     *            the authorities that can be used to. These are likely to be
     *            only "ROLE_CLIENT"
     * @return
     */
    @Override
    public ClientDetailsEntity createClientDetails(String orcid, String name, String description, String website, String clientId, String clientSecret,
            Set<String> clientScopes, Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes, Set<RedirectUri> clientRegisteredRedirectUris,
            List<String> clientGrantedAuthorities) {

        ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("The ORCID does not exist for " + orcid);
        }

        return populateClientDetailsEntity(clientId, profileEntity, name, description, website, clientSecret, clientScopes, clientResourceIds,
                clientAuthorizedGrantTypes, clientRegisteredRedirectUris, clientGrantedAuthorities);
    }

    /**
     * Creates a new {@link ClientDetailsEntity} using the actual entity. This
     * is called by
     * {@link #createClientDetails(String, String, String, java.util.Set, java.util.Set, java.util.Set, java.util.Set, java.util.List)}
     * to create a new entity
     * 
     * @param clientDetailsEntity
     * @return
     */
    @Override
    public ClientDetailsEntity createClientDetails(ClientDetailsEntity clientDetailsEntity) {
        persist(clientDetailsEntity);
        return findByClientId(clientDetailsEntity.getId());
    }

    @Override
    public void deleteClientDetail(String clientId) {
        removeByClientId(clientId);
    }

    private Set<ClientScopeEntity> getClientScopeEntities(Set<String> clientScopeStrings, ClientDetailsEntity clientDetailsEntity) {
        Set<ClientScopeEntity> clientScopeEntities = new HashSet<ClientScopeEntity>(clientScopeStrings.size());
        for (String clientScope : clientScopeStrings) {
            ClientScopeEntity clientScopeEntity = new ClientScopeEntity();
            clientScopeEntity.setClientDetailsEntity(clientDetailsEntity);
            clientScopeEntity.setScopeType(clientScope);
            clientScopeEntities.add(clientScopeEntity);
        }
        return clientScopeEntities;
    }

    private Set<ClientResourceIdEntity> getClientResourceIds(Set<String> clientResourceIds, ClientDetailsEntity clientDetailsEntity) {
        Set<ClientResourceIdEntity> clientResourceIdEntities = new HashSet<ClientResourceIdEntity>(clientResourceIds.size());
        for (String clientResourceId : clientResourceIds) {
            ClientResourceIdEntity clientResourceIdEntity = new ClientResourceIdEntity();
            clientResourceIdEntity.setClientDetailsEntity(clientDetailsEntity);
            clientResourceIdEntity.setResourceId(clientResourceId);
            clientResourceIdEntities.add(clientResourceIdEntity);
        }
        return clientResourceIdEntities;
    }

    private List<ClientGrantedAuthorityEntity> getClientGrantedAuthorities(List<String> clientGrantedAuthorities, ClientDetailsEntity clientDetailsEntity) {
        List<ClientGrantedAuthorityEntity> clientGrantedAuthorityEntities = new ArrayList<ClientGrantedAuthorityEntity>(clientGrantedAuthorities.size());
        for (String clientGrantedAuthority : clientGrantedAuthorities) {
            ClientGrantedAuthorityEntity clientGrantedAuthorityEntity = new ClientGrantedAuthorityEntity();
            clientGrantedAuthorityEntity.setClientDetailsEntity(clientDetailsEntity);
            clientGrantedAuthorityEntity.setAuthority(clientGrantedAuthority);
            clientGrantedAuthorityEntities.add(clientGrantedAuthorityEntity);
        }
        return clientGrantedAuthorityEntities;
    }

    private SortedSet<ClientRedirectUriEntity> getClientRegisteredRedirectUris(Set<RedirectUri> clientRegisteredRedirectUris, ClientDetailsEntity clientDetailsEntity) {
        SortedSet<ClientRedirectUriEntity> clientRedirectUriEntities = new TreeSet<ClientRedirectUriEntity>();
        for (RedirectUri clientRegisteredRedirectUri : clientRegisteredRedirectUris) {
            ClientRedirectUriEntity clientRedirectUriEntity = new ClientRedirectUriEntity();
            clientRedirectUriEntity.setClientDetailsEntity(clientDetailsEntity);
            clientRedirectUriEntity.setRedirectUri(clientRegisteredRedirectUri.getValue());
            clientRedirectUriEntity.setRedirectUriType(clientRegisteredRedirectUri.getType().value());
            List<ScopePathType> scopesForRedirect = clientRegisteredRedirectUri.getScope();
            String clientPredefinedScopes = scopesForRedirect != null ? ScopePathType.getScopesAsSingleString(scopesForRedirect) : null;
            clientRedirectUriEntity.setPredefinedClientScope(clientPredefinedScopes);
            clientRedirectUriEntities.add(clientRedirectUriEntity);
        }
        return clientRedirectUriEntities;
    }

    private Set<ClientAuthorisedGrantTypeEntity> getClientAuthorizedGrantTypes(Set<String> clientAuthorizedGrantTypes, ClientDetailsEntity clientDetailsEntity) {
        Set<ClientAuthorisedGrantTypeEntity> clientAuthorisedGrantTypeEntities = new HashSet<ClientAuthorisedGrantTypeEntity>(clientAuthorizedGrantTypes.size());
        for (String clientAuthorisedGrantType : clientAuthorizedGrantTypes) {
            ClientAuthorisedGrantTypeEntity grantTypeEntity = new ClientAuthorisedGrantTypeEntity();
            grantTypeEntity.setClientDetailsEntity(clientDetailsEntity);
            grantTypeEntity.setGrantType(clientAuthorisedGrantType);
            clientAuthorisedGrantTypeEntities.add(grantTypeEntity);
        }
        return clientAuthorisedGrantTypeEntities;
    }

    public ClientDetailsEntity populateClientDetailsEntity(String clientId, ProfileEntity profileEntity, String name, String description, String website,
            String clientSecret, Set<String> clientScopes, Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes,
            Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities) {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId(clientId);
        clientDetailsEntity.setClientName(name);
        clientDetailsEntity.setClientDescription(description);
        clientDetailsEntity.setClientWebsite(website);
        clientDetailsEntity.setClientSecretForJpa(clientSecret);
        clientDetailsEntity.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(clientSecret));
        clientDetailsEntity.setClientScopes(getClientScopeEntities(clientScopes, clientDetailsEntity));
        clientDetailsEntity.setClientResourceIds(getClientResourceIds(clientResourceIds, clientDetailsEntity));
        clientDetailsEntity.setClientAuthorizedGrantTypes(getClientAuthorizedGrantTypes(clientAuthorizedGrantTypes, clientDetailsEntity));
        clientDetailsEntity.setClientRegisteredRedirectUris(getClientRegisteredRedirectUris(clientRegisteredRedirectUris, clientDetailsEntity));
        clientDetailsEntity.setClientGrantedAuthorities(getClientGrantedAuthorities(clientGrantedAuthorities, clientDetailsEntity));

        return createClientDetails(clientDetailsEntity);
    }

    @Override
    public ClientDetailsEntity findByClientId(String orcid) {
        ClientDetailsEntity result = null;
        try {
            Date lastModified = clientDetailsDao.getLastModified(orcid);
            result = clientDetailsDao.findByClientId(orcid, lastModified);
        } catch (NoResultException nre) {

        }
        return result;
    }

    @Override
    public void removeByClientId(String clientId) {
        clientDetailsDao.remove(clientId);
    }

    @Override
    public void persist(ClientDetailsEntity clientDetails) {
        clientDetailsDao.persist(clientDetails);
        clientDetailsDao.updateLastModified(clientDetails.getId());
    }

    @Override
    public ClientDetailsEntity merge(ClientDetailsEntity clientDetails) {
        ClientDetailsEntity result = clientDetailsDao.merge(clientDetails);
        clientDetailsDao.updateLastModified(result.getId());
        return result;
    }

    @Override
    public void remove(String clientId) {
        clientDetailsDao.remove(clientId);
    }

    @Override
    public ClientDetailsEntity find(String clientId) {
        return clientDetailsDao.find(clientId);
    }

    @Override
    public List<ClientDetailsEntity> getAll() {
        return clientDetailsDao.getAll();
    }

    @Override
    public void updateLastModified(String clientId) {
        clientDetailsDao.updateLastModified(clientId);
    }

    /**
     * Set a new client secret for the specific client and set the other keys as
     * non primaries
     * 
     * @param clientId
     * @param clientSecret
     * @return true if the new key has been added
     * */
    @Override
    @Transactional
    public boolean resetClientSecret(String clientId, String clientSecret) {
        // #1 Set all existing client secrets as non primary
        clientSecretDao.revokeAllKeys(clientId);
        // #2 Create the new client secret as primary
        boolean result = clientSecretDao.createClientSecret(clientId, clientSecret);
        // #3 if it was created, update the last modified for the client details
        if (result)
            clientDetailsDao.updateLastModified(clientId);

        return result;
    }

    /**
     * Removes all non primary client secret keys
     * 
     * @param clientId
     * */
    @Override
    @Transactional
    public void cleanOldClientKeys() {

        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("Running cron to delete old keys");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out
                .println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        Date currentDate = new Date();
        List<ClientDetailsEntity> allClientDetails = this.getAll();
        if (allClientDetails != null && allClientDetails != null) {
            for (ClientDetailsEntity clientDetails : allClientDetails) {
                String clientId = clientDetails.getClientId();
                Set<ClientSecretEntity> clientSecrets = clientDetails.getClientSecrets();
                for (ClientSecretEntity clientSecret : clientSecrets) {
                    Date dateRevoked = clientSecret.getLastModified();
                    Date timeToDeleteMe = DateUtils.addMinutes(dateRevoked, 2);
                    // If the key have been revokend more than 24 hours ago
                    if (timeToDeleteMe.before(currentDate)) {
                        clientSecretDao.removeClientSecret(clientId, clientSecret.getClientSecret());
                    }
                }
            }
        }
    }
}
