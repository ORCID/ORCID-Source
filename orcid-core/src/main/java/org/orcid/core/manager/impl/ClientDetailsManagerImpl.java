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

import org.orcid.core.manager.AppIdGenerationManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.read_only.impl.ClientDetailsManagerReadOnlyImpl;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.dao.ClientAuthorizedGrantTypeDao;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientScopeDao;
import org.orcid.persistence.jpa.entities.ClientAuthorisedGrantTypeEntity;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientGrantedAuthorityEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientResourceIdEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.keys.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ClientDetailsManagerImpl extends ClientDetailsManagerReadOnlyImpl implements ClientDetailsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailsManagerImpl.class);

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private ClientDetailsDao clientDetailsDaoReadOnly;
    
    @Resource
    private AppIdGenerationManager appIdGenerationManager;
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private ClientScopeDao clientScopeDao;
    
    @Resource
    private ClientAuthorizedGrantTypeDao clientAuthorizedGrantTypeDao;
    
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
     * @param allowAutoDeprecate
     *          Indicates if the client will enable auto deprecating unclaimed records.
     * @return
     */
    @Override
    public ClientDetailsEntity createClientDetails(String memberId, String name, String description, String idp, String website, ClientType clientType, Set<String> clientScopes,
            Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes, Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities, Boolean allowAutoDeprecate) {        
        if (!profileEntityManager.orcidExists(memberId)) {
            throw new IllegalArgumentException("ORCID does not exist for " + memberId + " cannot continue");
        } else {
            String clientSecret = encryptionManager.encryptForInternalUse(UUID.randomUUID().toString());
            String clientId = appIdGenerationManager.createNewAppId();
            return populateClientDetailsEntity(clientId, memberId, name, description, idp, website, clientSecret, clientType, clientScopes, clientResourceIds,
                    clientAuthorizedGrantTypes, clientRegisteredRedirectUris, clientGrantedAuthorities, allowAutoDeprecate);
        }
    }   

    @Override
    public void addClientRedirectUri(String clientId, String uri) {
        clientRedirectDao.addClientRedirectUri(clientId, uri);
        clientDetailsDao.updateLastModified(clientId);
    }
    
    @Override
    public void addClientRedirectUri(String clientId, String uri, RedirectUriType uriType, ScopePathType activitiesUpdate) {
        clientRedirectDao.addClientRedirectUri(clientId, uri,uriType.value(),activitiesUpdate.value());
        clientDetailsDao.updateLastModified(clientId);
    }

    private Set<ClientScopeEntity> getClientScopeEntities(Set<String> clientScopeStrings, ClientDetailsEntity clientDetailsEntity) {
        Set<ClientScopeEntity> clientScopeEntities = new HashSet<ClientScopeEntity>(clientScopeStrings.size());
        for (String clientScope : clientScopeStrings) {
            ClientScopeEntity clientScopeEntity = new ClientScopeEntity();
            ClientScopePk pk = new ClientScopePk();
            pk.setClientId(clientDetailsEntity.getClientId());
            pk.setScopeType(clientScope);
            clientScopeEntity.setId(pk);
            clientScopeEntities.add(clientScopeEntity);
        }
        return clientScopeEntities;
    }

    private Set<ClientResourceIdEntity> getClientResourceIds(Set<String> clientResourceIds, ClientDetailsEntity clientDetailsEntity) {
        Set<ClientResourceIdEntity> clientResourceIdEntities = new HashSet<ClientResourceIdEntity>(clientResourceIds.size());
        for (String clientResourceId : clientResourceIds) {
            ClientResourceIdEntity clientResourceIdEntity = new ClientResourceIdEntity();
            ClientResourceIdPk pk = new ClientResourceIdPk();
            pk.setClientId(clientDetailsEntity.getClientId());
            pk.setResourceId(clientResourceId);
            clientResourceIdEntity.setId(pk);
            clientResourceIdEntities.add(clientResourceIdEntity);
        }
        return clientResourceIdEntities;
    }

    private List<ClientGrantedAuthorityEntity> getClientGrantedAuthorities(List<String> clientGrantedAuthorities, ClientDetailsEntity clientDetailsEntity) {
        List<ClientGrantedAuthorityEntity> clientGrantedAuthorityEntities = new ArrayList<ClientGrantedAuthorityEntity>(clientGrantedAuthorities.size());
        for (String clientGrantedAuthority : clientGrantedAuthorities) {
            ClientGrantedAuthorityEntity clientGrantedAuthorityEntity = new ClientGrantedAuthorityEntity();
            ClientGrantedAuthorityPk pk = new ClientGrantedAuthorityPk();
            pk.setClientId(clientDetailsEntity.getClientId());
            pk.setAuthority(clientGrantedAuthority);
            clientGrantedAuthorityEntity.setId(pk);
            clientGrantedAuthorityEntities.add(clientGrantedAuthorityEntity);
        }
        return clientGrantedAuthorityEntities;
    }

    private SortedSet<ClientRedirectUriEntity> getClientRegisteredRedirectUris(Set<RedirectUri> clientRegisteredRedirectUris, ClientDetailsEntity clientDetailsEntity) {
        SortedSet<ClientRedirectUriEntity> clientRedirectUriEntities = new TreeSet<ClientRedirectUriEntity>();
        for (RedirectUri clientRegisteredRedirectUri : clientRegisteredRedirectUris) {
            ClientRedirectUriEntity clientRedirectUriEntity = new ClientRedirectUriEntity();
            ClientRedirectUriPk pk = new ClientRedirectUriPk();
            pk.setClientId(clientDetailsEntity.getClientId());
            pk.setRedirectUri(clientRegisteredRedirectUri.getValue());
            pk.setRedirectUriType(clientRegisteredRedirectUri.getType().value());
            clientRedirectUriEntity.setId(pk);
            List<ScopePathType> scopesForRedirect = clientRegisteredRedirectUri.getScope();
            String clientPredefinedScopes = scopesForRedirect != null ? ScopePathType.getScopesAsSingleString(scopesForRedirect) : null;
            clientRedirectUriEntity.setPredefinedClientScope(clientPredefinedScopes);
            clientRedirectUriEntity.setUriActType(clientRegisteredRedirectUri.getActType());
            clientRedirectUriEntity.setUriGeoArea(clientRegisteredRedirectUri.getGeoArea());
            clientRedirectUriEntities.add(clientRedirectUriEntity);
        }
        return clientRedirectUriEntities;
    }

    private Set<ClientAuthorisedGrantTypeEntity> getClientAuthorizedGrantTypes(Set<String> clientAuthorizedGrantTypes, ClientDetailsEntity clientDetailsEntity) {
        Set<ClientAuthorisedGrantTypeEntity> clientAuthorisedGrantTypeEntities = new HashSet<ClientAuthorisedGrantTypeEntity>(clientAuthorizedGrantTypes.size());
        for (String clientAuthorisedGrantType : clientAuthorizedGrantTypes) {
            ClientAuthorisedGrantTypeEntity grantTypeEntity = new ClientAuthorisedGrantTypeEntity();
            ClientAuthorisedGrantTypePk pk = new ClientAuthorisedGrantTypePk();
            pk.setClientId(clientDetailsEntity.getClientId());
            pk.setGrantType(clientAuthorisedGrantType);
            grantTypeEntity.setId(pk);
            clientAuthorisedGrantTypeEntities.add(grantTypeEntity);
        }
        return clientAuthorisedGrantTypeEntities;
    }

    @Override
    public ClientDetailsEntity populateClientDetailsEntity(String clientId, String memberId, String name, String description, String idp, String website,
            String clientSecret, ClientType clientType, Set<String> clientScopes, Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes,
            Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities, Boolean allowAutoDeprecate) {
        ClientDetailsEntity clientDetailsEntity = new ClientDetailsEntity();
        clientDetailsEntity.setId(clientId);
        clientDetailsEntity.setClientType(clientType.name());
        clientDetailsEntity.setClientName(name);
        clientDetailsEntity.setClientDescription(description);
        clientDetailsEntity.setAuthenticationProviderId(idp);
        clientDetailsEntity.setClientWebsite(website);
        clientDetailsEntity.setClientSecretForJpa(clientSecret, true);
        clientDetailsEntity.setDecryptedClientSecret(encryptionManager.decryptForInternalUse(clientSecret));
        clientDetailsEntity.setClientScopes(getClientScopeEntities(clientScopes, clientDetailsEntity));
        clientDetailsEntity.setClientResourceIds(getClientResourceIds(clientResourceIds, clientDetailsEntity));
        clientDetailsEntity.setClientAuthorizedGrantTypes(getClientAuthorizedGrantTypes(clientAuthorizedGrantTypes, clientDetailsEntity));
        clientDetailsEntity.setClientRegisteredRedirectUris(getClientRegisteredRedirectUris(clientRegisteredRedirectUris, clientDetailsEntity));
        clientDetailsEntity.setPersistentTokensEnabled(true);
        clientDetailsEntity.setClientGrantedAuthorities(getClientGrantedAuthorities(clientGrantedAuthorities, clientDetailsEntity));
        clientDetailsEntity.setGroupProfileId(memberId);
        clientDetailsEntity.setAllowAutoDeprecate(allowAutoDeprecate == null ? false : allowAutoDeprecate);
        clientDetailsDao.persist(clientDetailsEntity);
        return clientDetailsEntity;
    }    

    @Override
    public void removeByClientId(String clientId) {
        clientDetailsDao.remove(clientId);
    }    

    @Override
    public ClientDetailsEntity merge(ClientDetailsEntity clientDetails) {
        ClientDetailsEntity result = clientDetailsDao.merge(clientDetails);
        clientDetailsDao.updateLastModified(result.getId());
        // Evict the name in the source name manager
        sourceNameCacheManager.remove(result.getId());        
        return result;
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


    @Override
    public List<ClientDetailsEntity> getAll() {
        return clientDetailsDaoReadOnly.getAll();
    }
    
    @Override
    public boolean exists(String clientId) {
        return clientDetailsDao.exists(clientId);
    }

    /**
     * Verifies if a client belongs to the given group id
     * 
     * @param clientId
     * @param groupId
     * @return true if clientId belongs to groupId
     * */
    @Override
    public boolean belongsTo(String clientId, String groupId) {
        return clientDetailsDao.belongsTo(clientId, groupId);
    }

    /**
     * Fetch all clients that belongs to a group
     * 
     * @param groupId
     *            Group id
     * @return A list containing all clients that belongs to the given group
     * */
    @Override
    public List<ClientDetailsEntity> findByGroupId(String groupId) {
        return clientDetailsDao.findByGroupId(groupId);
    }

    /**
     * Get the public profile that belongs to the given orcid ID
     * 
     * @param ownerId
     *            The user or group id
     * @return the public client that belongs to the given user
     * */
    @Override
    public ClientDetailsEntity getPublicClient(String ownerId) {
        return clientDetailsDao.getPublicClient(ownerId);
    }
    
    /**
     * Get member name
     * 
     * @param clientId
     *            The client id
     * @return the name of the member owner of the given client 
     * */
    @Override
    public String getMemberName(String clientId) {
        return clientDetailsDao.getMemberName(clientId);
    }
    
    @Override    
    public Date getLastModified(String clientId) {
        return clientDetailsDao.getLastModified(clientId);
    }

    @Override    
    public Date getLastModifiedByIdp(String idp) {
        try {
            return clientDetailsDao.getLastModifiedByIdP(idp);
        } catch(Exception e) {
            LOGGER.warn("There is no client with the IdP: " + idp);
        }
        return null;
    }
    
    @Override
    public ClientDetailsEntity findByIdP(String idp) {
        try {
            ClientDetailsEntity result = clientDetailsDao.findByIdP(idp);
            return result;
        } catch(Exception e) {
            LOGGER.warn("There is no client with the IdP: " + idp);
        }
        return null;
    }

    @Override
    public void addScopesToClient(Set<String> scopes, ClientDetailsEntity clientDetails) {
        for (String scope : scopes) {
            if (!clientDetails.getScope().contains(scope)) {
                clientScopeDao.insertClientScope(clientDetails.getId(), scope);
            }
        }
    }
    
    @Override
    public void addAuthorizedGrantTypeToClient(Set<String> types, ClientDetailsEntity clientDetails) {
        for (String type : types) {
            if (!clientDetails.getAuthorizedGrantTypes().contains(type)) {
                clientAuthorizedGrantTypeDao.insertClientAuthorizedGrantType(clientDetails.getId(), type);
            }
        }
    }
    
}
