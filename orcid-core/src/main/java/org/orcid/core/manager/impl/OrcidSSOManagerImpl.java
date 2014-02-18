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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientGrantedAuthorityEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.keys.ClientScopePk;

public class OrcidSSOManagerImpl implements OrcidSSOManager {

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource(name = "clientScopeDao")
    private GenericDao<ClientScopeEntity, ClientScopePk> clientScopeDao;

    @Resource
    private ClientRedirectDao clientRedirectDao;

    private final static String SSO_SCOPE = ScopePathType.AUTHORIZE.value();
    private final static String SSO_ROLE = "ROLE_PUBLIC";

    @Override
    public ClientDetailsEntity grantSSOAccess(String orcid, Set<String> redirectUris) {
        ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("ORCID does not exist for " + orcid + " cannot continue");
        } else {
            // If it already have SSO client credentials, just return them
            if (profileEntity.getClientDetails() != null) {
                ClientDetailsEntity existingClientDetails = profileEntity.getClientDetails();
                Set<ClientScopeEntity> existingScopes = existingClientDetails.getClientScopes();
                boolean alreadyHaveAuthScope = false;
                // Check if it already have the SSO scope
                for (ClientScopeEntity clientScope : existingScopes) {
                    if (SSO_SCOPE.equals(clientScope.getScopeType())) {
                        alreadyHaveAuthScope = true;
                        break;
                    }
                }

                // If it doesn't add it and persist it
                if (!alreadyHaveAuthScope) {
                    ClientScopeEntity ssoScope = getClientScopeEntity(SSO_SCOPE, existingClientDetails);
                    existingScopes.add(ssoScope);
                    existingClientDetails.setClientScopes(existingScopes);
                    clientDetailsDao.merge(existingClientDetails);
                }
                return clientDetailsDao.findByClientId(existingClientDetails.getId());
            } else {
                String clientSecret = encryptionManager.encryptForInternalUse(UUID.randomUUID().toString());
                String clientId = profileEntity.getId();
                Set<String> redirectUrisSet = new HashSet<String>();
                for (String uri : redirectUris) {
                    redirectUrisSet.add(uri);
                }
                ClientDetailsEntity clientDetailsEntity = populateClientDetailsEntity(clientId, clientSecret, redirectUrisSet);
                clientDetailsDao.persist(clientDetailsEntity);
                return clientDetailsDao.findByClientId(clientDetailsEntity.getId());
            }
        }
    }

    @Override
    public ClientDetailsEntity getUserCredentials(String orcid) {
        ClientDetailsEntity existingClientDetails = clientDetailsDao.findByClientId(orcid);
        if (existingClientDetails != null)
            existingClientDetails.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(existingClientDetails.getClientSecretForJpa()));
        return existingClientDetails;
    }

    @Override
    public void revokeSSOAccess(String orcid) {
        ProfileEntity profileEntity = profileEntityManager.findByOrcid(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("ORCID does not exist for " + orcid + " cannot continue");
        } else {
            if (profileEntity.getClientDetails() != null) {
                ClientDetailsEntity existingClientDetails = profileEntity.getClientDetails();
                Set<ClientScopeEntity> existingScopes = existingClientDetails.getClientScopes();
                if (hasSSOScope(existingScopes)) {
                    // If the SSO scope is the unique scope, delete the complete
                    // client details entity
                    if (existingScopes.size() == 1) {
                        // Delete the client details entity
                        clientDetailsDao.removeByClientId(orcid);
                    } else {
                        // If the user have more that the SSO scope
                        // Delete the SSO scope
                        ClientScopePk pk = new ClientScopePk();
                        pk.setClientDetailsEntity(orcid);
                        pk.setScopeType(SSO_SCOPE);
                        clientScopeDao.remove(pk);
                        // Delete the client redirect uris associated with SSO
                        // authentication
                        Set<ClientRedirectUriEntity> redirectUris = existingClientDetails.getClientRegisteredRedirectUris();
                        if (redirectUris != null && redirectUris.size() > 0) {
                            for (ClientRedirectUriEntity redirectUri : redirectUris) {
                                if (RedirectUriType.SSO_AUTHENTICATION.value().equals(redirectUri.getRedirectUriType())) {
                                    clientRedirectDao.removeClientRedirectUri(orcid, redirectUri.getRedirectUri());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasSSOScope(Set<ClientScopeEntity> scopes) {
        if (scopes != null && scopes.size() > 0) {
            for (ClientScopeEntity scope : scopes) {
                if (SSO_SCOPE.equals(scope.getScopeType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private ClientDetailsEntity populateClientDetailsEntity(String clientId, String clientSecret, Set<String> clientRegisteredRedirectUris) {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId(clientId);
        clientDetailsEntity.setClientSecretForJpa(clientSecret);
        clientDetailsEntity.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(clientSecret));
        Set<ClientScopeEntity> clientScopes = new HashSet<ClientScopeEntity>();
        clientScopes.add(getClientScopeEntity(SSO_SCOPE, clientDetailsEntity));
        clientDetailsEntity.setClientScopes(clientScopes);
        clientDetailsEntity.setClientRegisteredRedirectUris(getClientRegisteredRedirectUris(clientRegisteredRedirectUris, clientDetailsEntity));
        clientDetailsEntity.setClientGrantedAuthorities(getClientGrantedAuthorities(clientDetailsEntity));

        clientDetailsEntity.setClientAuthorizedGrantTypes(getClientAuthorizedGrantTypes(clientDetailsEntity));

        return clientDetailsEntity;
    }

    private ClientScopeEntity getClientScopeEntity(String clientScope, ClientDetailsEntity clientDetailsEntity) {
        ClientScopeEntity clientScopeEntity = new ClientScopeEntity();
        clientScopeEntity.setClientDetailsEntity(clientDetailsEntity);
        clientScopeEntity.setScopeType(clientScope);
        return clientScopeEntity;
    }

    private SortedSet<ClientRedirectUriEntity> getClientRegisteredRedirectUris(Set<String> redirectUris, ClientDetailsEntity clientDetailsEntity) {
        SortedSet<ClientRedirectUriEntity> clientRedirectUriEntities = new TreeSet<ClientRedirectUriEntity>();
        for (String redirectUri : redirectUris) {
            ClientRedirectUriEntity clientRedirectUriEntity = new ClientRedirectUriEntity();
            clientRedirectUriEntity.setClientDetailsEntity(clientDetailsEntity);
            clientRedirectUriEntity.setRedirectUri(redirectUri);
            clientRedirectUriEntity.setRedirectUriType(RedirectUriType.SSO_AUTHENTICATION.value());
            clientRedirectUriEntities.add(clientRedirectUriEntity);
        }
        return clientRedirectUriEntities;
    }

    private List<ClientGrantedAuthorityEntity> getClientGrantedAuthorities(ClientDetailsEntity clientDetailsEntity) {
        List<ClientGrantedAuthorityEntity> clientGrantedAuthorityEntities = new ArrayList<ClientGrantedAuthorityEntity>();
        ClientGrantedAuthorityEntity clientGrantedAuthorityEntity = new ClientGrantedAuthorityEntity();
        clientGrantedAuthorityEntity.setClientDetailsEntity(clientDetailsEntity);
        clientGrantedAuthorityEntity.setAuthority(SSO_ROLE);
        clientGrantedAuthorityEntities.add(clientGrantedAuthorityEntity);
        return clientGrantedAuthorityEntities;
    }

    private Set<ClientAuthorisedGrantTypeEntity> getClientAuthorizedGrantTypes(ClientDetailsEntity clientDetailsEntity) {
        Set<ClientAuthorisedGrantTypeEntity> clientAuthorisedGrantTypeEntities = new HashSet<ClientAuthorisedGrantTypeEntity>();
        String[] clientAuthorizedGrantTypes = { "authorization_code" };
        for (String clientAuthorisedGrantType : clientAuthorizedGrantTypes) {
            ClientAuthorisedGrantTypeEntity grantTypeEntity = new ClientAuthorisedGrantTypeEntity();
            grantTypeEntity.setClientDetailsEntity(clientDetailsEntity);
            grantTypeEntity.setGrantType(clientAuthorisedGrantType);
            clientAuthorisedGrantTypeEntities.add(grantTypeEntity);
        }
        return clientAuthorisedGrantTypeEntities;
    }
}
