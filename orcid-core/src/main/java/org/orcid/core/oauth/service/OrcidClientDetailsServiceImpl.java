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
package org.orcid.core.oauth.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidClientDetailsService;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientGrantedAuthorityEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientResourceIdEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Service;

/**
 * 2011-2012 ORCID
 * <p/>
 * * An additional layer on top of the
 * {@link org.springframework.security.oauth2.provider.ClientDetailsService} to
 * enable us to supply additional functionality for within the service layer.
 * <p/>
 * The delete and
 * {@link #createClientDetails(String, String, String, java.util.Set, java.util.Set, java.util.Set, java.util.Set, java.util.List)},
 * {@link #createClientDetails(String, java.util.Set, java.util.Set, java.util.Set, java.util.Set, java.util.List)}
 * and
 * {@link #createClientDetails(org.orcid.persistence.jpa.entities.ClientDetailsEntity)}
 * methods are all in addition to the methods required for the
 * {@link org.springframework.security.oauth2.provider .ClientDetailsService},
 * as this is will be needed for the script to generate clients.
 * 
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Service("orcidClientDetailsService")
public class OrcidClientDetailsServiceImpl implements OrcidClientDetailsService {

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private EncryptionManager encryptionManager;

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
        ClientDetailsEntity clientDetails = clientDetailsDao.findByClientId(clientId);
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
    public ClientDetailsEntity createClientDetails(String orcid, Set<String> clientScopes, Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes,
            Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities) {
        ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("ORCID does not exist for " + orcid + " cannot continue");
        } else {
            String clientSecret = encryptionManager.encryptForInternalUse(UUID.randomUUID().toString());
            StringBuilder clientId = new StringBuilder(profileEntity.getId());
            return populateClientDetailsEntity(clientId.toString(), profileEntity, clientSecret, clientScopes, clientResourceIds, clientAuthorizedGrantTypes,
                    clientRegisteredRedirectUris, clientGrantedAuthorities);
        }
    }

    /**
     * Creates a new {@link ClientDetailsEntity} using the component parts, and
     * not the underyling entity directly.
     * 
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
    public ClientDetailsEntity createClientDetails(String orcid, String clientId, String clientSecret, Set<String> clientScopes, Set<String> clientResourceIds,
            Set<String> clientAuthorizedGrantTypes, Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities) {

        ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("The ORCID does not exist for " + orcid);
        }

        return populateClientDetailsEntity(clientId, profileEntity, clientSecret, clientScopes, clientResourceIds, clientAuthorizedGrantTypes,
                clientRegisteredRedirectUris, clientGrantedAuthorities);
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
        clientDetailsDao.persist(clientDetailsEntity);
        return clientDetailsDao.findByClientId(clientDetailsEntity.getId());
    }

    @Override
    public void deleteClientDetail(String clientId) {
        clientDetailsDao.removeByClientId(clientId);
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

    private ClientDetailsEntity populateClientDetailsEntity(String clientId, ProfileEntity profileEntity, String clientSecret, Set<String> clientScopes,
            Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes, Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities) {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId(clientId);
        clientDetailsEntity.setClientSecretForJpa(clientSecret);
        clientDetailsEntity.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(clientSecret));
        clientDetailsEntity.setClientScopes(getClientScopeEntities(clientScopes, clientDetailsEntity));
        clientDetailsEntity.setClientResourceIds(getClientResourceIds(clientResourceIds, clientDetailsEntity));
        clientDetailsEntity.setClientAuthorizedGrantTypes(getClientAuthorizedGrantTypes(clientAuthorizedGrantTypes, clientDetailsEntity));
        clientDetailsEntity.setClientRegisteredRedirectUris(getClientRegisteredRedirectUris(clientRegisteredRedirectUris, clientDetailsEntity));
        clientDetailsEntity.setClientGrantedAuthorities(getClientGrantedAuthorities(clientGrantedAuthorities, clientDetailsEntity));

        return createClientDetails(clientDetailsEntity);
    }

}
