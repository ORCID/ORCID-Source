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
package org.orcid.core.manager;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface ClientDetailsManager extends ClientDetailsService {
    /**
     * Creates a new client without any knowledge of the client id or secret.
     * This to assist in the creation of clients from the automated client
     * creation process.
     * 
     * @param groupOrcid
     *            the ORCID that will be the owner of this client.
     * @param name
     *            The client name
     * @param description
     *            The client description
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
    ClientDetailsEntity createClientDetails(String groupOrcid, String name, String description, String website, ClientType clientType, Set<String> clientScopes,
            Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes, Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities);

    /**
     * Creates a new {@link ClientDetailsEntity} using the component parts, and
     * not the underyling entity directly.
     * 
     * @param groupOrcid
     *            the ORCID that will be the owner of this client. Each client
     *            can have one, and one only profile associated with it
     * @param name
     *            The client name
     * @param description
     *            The client description
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
    ClientDetailsEntity createClientDetails(String groupOrcid, String name, String description, String website, String clientId, String clientSecret,
            ClientType clientType, Set<String> clientScopes, Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes,
            Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities);

    /**
     * Create new {@link ClientDetailsEntity} using the entity object
     * 
     * @param clientDetailsEntity
     *            the {@link ClientDetailsEntity} to be persisted
     * @return the newly persisted {@link ClientDetailsEntity}
     */
    ClientDetailsEntity createClientDetails(ClientDetailsEntity clientDetailsEntity);

    /**
     * Delete the {@link ClientDetailsEntity} from the persistence layer that
     * has the corresponding id
     * 
     * @param clientId
     *            the id corresponding to the persisted
     *            {@link ClientDetailsEntity}
     */
    void deleteClientDetail(String clientId);

    ClientDetailsEntity findByClientId(String orcid);

    void removeByClientId(String clientId);

    void persist(ClientDetailsEntity clientDetails);

    public void addClientRedirectUri(String clientId, String uri);

    ClientDetailsEntity merge(ClientDetailsEntity clientDetails);

    void remove(String clientId);

    List<ClientDetailsEntity> getAll();

    void updateLastModified(String clientId);
    
    Date getLastModified(String clientId);

    /**
     * Set a new client secret for the specific client and set the other keys as
     * non primaries
     * 
     * @param clientId
     * @param clientSecret
     * @return true if the new key has been added
     * */
    boolean resetClientSecret(String clientId, String clientSecret);

    /**
     * Removes all non primary client secret keys
     * 
     * @param clientId
     * */
    void cleanOldClientKeys();

    boolean exists(String cliendId);
    
    /**
     * Verifies if a client belongs to the given group id
     * @param clientId
     * @param groupId
     * @return true if clientId belongs to groupId
     * */
    boolean belongsTo(String clientId, String groupId);
    
    /**
     * Fetch all clients that belongs to a group
     * @param groupId
     *  Group id
     * @return A list containing all clients that belongs to the given group
     * */
    List<ClientDetailsEntity> findByGroupId(String groupId);
    
    ClientDetailsEntity getPublicClient(String ownerId);
    
    String getMemberName(String clientId);

    OrcidClient toOrcidClient(ClientDetailsEntity clientEntity);
    
    /**
     * Utility function that will help us to create and persist a clientDetailsEntity giving all the details
     * */
    ClientDetailsEntity populateClientDetailsEntity(String clientId, ProfileEntity profileEntity, String name, String description, String website,
            String clientSecret, ClientType clientType, Set<String> clientScopes, Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes,
            Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities);
}
