/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.OrcidSSOManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.keys.ClientScopePk;
import org.springframework.transaction.annotation.Transactional;

public class OrcidSSOManagerImpl implements OrcidSSOManager {

    private final static String SSO_REDIRECT_URI_TYPE = RedirectUriType.SSO_AUTHENTICATION.value();
    private final static ScopePathType [] PUBLIC_CLIENT_SCOPES = {ScopePathType.AUTHENTICATE, ScopePathType.READ_PUBLIC};    
    private final static String SSO_ROLE = "ROLE_PUBLIC";
    private final static String RESOURCE_ID = "orcid";
    
    @Resource
    private ProfileEntityManager profileEntityManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource(name = "clientScopeDao")
    private GenericDao<ClientScopeEntity, ClientScopePk> clientScopeDao;

    @Resource
    private ClientRedirectDao clientRedirectDao;

    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private OrcidProfileManager orcidProfileManager;
    
    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
   
    @Override
    @Transactional
    public ClientDetailsEntity grantSSOAccess(String orcid, String name, String description, String website, Set<String> redirectUris) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("ORCID does not exist for " + orcid + " cannot continue");
        }
        String clientId = null;

        ClientDetailsEntity existingPublicClient = clientDetailsManager.getPublicClient(orcid);
        // If it already have SSO client credentials, just return them
        if (existingPublicClient != null) {            
            clientId = existingPublicClient.getId();
        } else {
            Set<String> clientScopes = new HashSet<>();
            for(ScopePathType publicClientScope : PUBLIC_CLIENT_SCOPES) {
                clientScopes.add(publicClientScope.getContent());
            }            
            Set<String> clientResourceIds = new HashSet<>();
            clientResourceIds.add(RESOURCE_ID);

            Set<String> redirectUrisSet = new HashSet<String>();
            for (String uri : redirectUris) {
                redirectUrisSet.add(uri);
            }
            ClientDetailsEntity clientDetailsEntity = clientDetailsManager.createClientDetails(orcid, name, description, null, website, ClientType.PUBLIC_CLIENT, clientScopes,
                    clientResourceIds, getClientAuthorizedGrantTypes(), getClientRegisteredRedirectUris(redirectUrisSet), getClientGrantedAuthorities(), false);
            clientId = clientDetailsEntity.getId();
        }

        ClientDetailsEntity clientDetailsEntity = clientDetailsManager.findByClientId(clientId);
        if (clientDetailsEntity.getClientSecrets() != null) {
            for (ClientSecretEntity updatedClientSecret : clientDetailsEntity.getClientSecrets()) {
                updatedClientSecret.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(updatedClientSecret.getClientSecret()));
            }
        }
        return clientDetailsEntity;
    }

    @Override
    @Transactional
    public void revokeSSOAccess(String orcid) {
        if(profileEntityManager.orcidExists(orcid)) {
            ClientDetailsEntity publicClient = clientDetailsManager.getPublicClient(orcid);
            if (publicClient != null) {
                clientDetailsManager.removeByClientId(publicClient.getId());
            }
        }
    }
    
    @Override    
    public ClientDetailsEntity getUserCredentials(String orcid) {
        ClientDetailsEntity existingClientDetails = clientDetailsManager.getPublicClient(orcid);
        if (existingClientDetails != null) {
            SortedSet<ClientRedirectUriEntity> allRedirectUris = existingClientDetails.getClientRegisteredRedirectUris();
            SortedSet<ClientRedirectUriEntity> onlySSORedirectUris = new TreeSet<ClientRedirectUriEntity>();
            if (allRedirectUris != null) {
                for (ClientRedirectUriEntity rUri : allRedirectUris) {
                    // Leave only the redirect uris used for SSO authentication
                    if (SSO_REDIRECT_URI_TYPE.equals(rUri.getRedirectUriType())) {
                        onlySSORedirectUris.add(rUri);
                    }
                }
            }
            existingClientDetails.setClientRegisteredRedirectUris(onlySSORedirectUris);
            if (existingClientDetails.getClientSecrets() != null) {
                for (ClientSecretEntity clientSecret : existingClientDetails.getClientSecrets()) {
                    clientSecret.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(clientSecret.getClientSecret()));
                }
            }
        }
        return existingClientDetails;
    }        
    
    @Override
    public boolean resetClientSecret(String clientDetailsId) {
        ClientDetailsEntity clientDetailsEntity = clientDetailsManager.findByClientId(clientDetailsId);
        if (clientDetailsEntity == null) {
            throw new IllegalArgumentException("ORCID " + clientDetailsId + " doesnt have client details assigned yet");
        }
        // Generate new client secret
        String clientSecret = encryptionManager.encryptForInternalUse(UUID.randomUUID().toString());

        return clientDetailsManager.resetClientSecret(clientDetailsEntity.getClientId(), clientSecret);
    }
    
    private Set<RedirectUri> getClientRegisteredRedirectUris(Set<String> redirectUris) {
        Set<RedirectUri> clientRedirectUris = new HashSet<>();
        for (String redirectUri : redirectUris) {
            RedirectUri clientRedirectUriEntity = populateClientRedirectUri(redirectUri);
            clientRedirectUris.add(clientRedirectUriEntity);
        }
        return clientRedirectUris;
    }

    private RedirectUri populateClientRedirectUri(String redirectUri) {
        RedirectUri clientRedirectUri = new RedirectUri();
        clientRedirectUri.setValue(redirectUri);
        clientRedirectUri.setType(RedirectUriType.SSO_AUTHENTICATION);
        return clientRedirectUri;
    }

    private ClientRedirectUriEntity populateClientRedirectUriEntity(String redirectUri, ClientDetailsEntity clientDetailsEntity) {
        ClientRedirectUriEntity clientRedirectUriEntity = new ClientRedirectUriEntity();
        clientRedirectUriEntity.setClientDetailsEntity(clientDetailsEntity);
        clientRedirectUriEntity.setRedirectUri(redirectUri);
        clientRedirectUriEntity.setRedirectUriType(SSO_REDIRECT_URI_TYPE);
        return clientRedirectUriEntity;
    }

    private List<String> getClientGrantedAuthorities() {
        List<String> clientGrantedAuthorities = new ArrayList<>();
        clientGrantedAuthorities.add(SSO_ROLE);
        return clientGrantedAuthorities;
    }

    private Set<String> getClientAuthorizedGrantTypes() {
        Set<String> clientAuthorizedGrantTypes = new HashSet<>();
        clientAuthorizedGrantTypes.add("authorization_code");
        clientAuthorizedGrantTypes.add("client_credentials");
        clientAuthorizedGrantTypes.add("refresh_token");
        return clientAuthorizedGrantTypes;
    }

    @Override
    @Transactional
    public ClientDetailsEntity updateUserCredentials(String orcid, String name, String description, String website, Set<String> redirectUris) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        if (profileEntity == null) {
            throw new IllegalArgumentException("ORCID does not exist for " + orcid + " cannot continue");
        } else {
            ClientDetailsEntity existingPublicClient = clientDetailsManager.getPublicClient(orcid);
            if (existingPublicClient != null) {
                // Set the decrypted secret
                existingPublicClient.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(existingPublicClient.getClientSecretForJpa()));
                // Update the name
                existingPublicClient.setClientName(name);
                // Update the description
                existingPublicClient.setClientDescription(description);
                // Update the website if needed
                existingPublicClient.setClientWebsite(website);

                // Get the existing redirect uris
                SortedSet<ClientRedirectUriEntity> clientRedirectUriEntities = existingPublicClient.getClientRegisteredRedirectUris();

                // Create a set with the redirect uris that are not SSO and the
                // ones that wasnt modified
                Set<ClientRedirectUriEntity> redirectUrisToAdd = new HashSet<ClientRedirectUriEntity>();
                for (ClientRedirectUriEntity existingEntity : clientRedirectUriEntities) {
                    // Add to the set all non SSO redirect uris
                    if (!SSO_REDIRECT_URI_TYPE.equals(existingEntity.getRedirectUriType())) {
                        redirectUrisToAdd.add(existingEntity);
                    } else {
                        // If the redirect uri exists and also comes in the new
                        // set of redirect uris, leave it
                        if (redirectUris.contains(existingEntity.getRedirectUri())) {
                            redirectUrisToAdd.add(existingEntity);
                        }
                    }
                }

                Map<String, ClientRedirectUriEntity> existingClientRedirectUriEntitiesMap = ClientRedirectUriEntity.mapByUri(redirectUrisToAdd);
                // Now we need to check which redirect uris are new, in order to
                // add them
                for (String redirectUri : redirectUris) {
                    if (!existingClientRedirectUriEntitiesMap.containsKey(redirectUri)) {
                        // Add the new key
                        ClientRedirectUriEntity newRedirectUri = populateClientRedirectUriEntity(redirectUri, existingPublicClient);
                        redirectUrisToAdd.add(newRedirectUri);
                    }
                }

                // Clear the set for orphan removal
                clientRedirectUriEntities.clear();
                // Fill the collection with the redirect uris that should be
                // kept
                clientRedirectUriEntities.addAll(redirectUrisToAdd);

                existingPublicClient = clientDetailsManager.merge(existingPublicClient);
                if (existingPublicClient.getClientSecrets() != null) {
                    for (ClientSecretEntity updatedClientSecret : existingPublicClient.getClientSecrets()) {
                        updatedClientSecret.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(updatedClientSecret.getClientSecret()));
                    }
                }

                return existingPublicClient;
            }
        }
        return null;
    }
}
