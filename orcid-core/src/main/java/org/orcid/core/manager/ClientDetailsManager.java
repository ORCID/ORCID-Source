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
package org.orcid.core.manager;

import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface ClientDetailsManager extends ClientDetailsService {
    /**
     * Creates a new client without any knowledge of the client id or secret.
     * This to assist in the creation of clients from the automated client
     * creation process.
     * 
     * @param orcid
     *            the ORCID that will be the owner of this client. Each client
     *            can have one, and one only profile associated with it
     * @param name
     *          The client name
     * @param description
     *          The client description          
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
    ClientDetailsEntity createClientDetails(String orcid, String name, String description, String website, Set<String> clientScopes, Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes,
            Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities);

    /**
     * Creates a new {@link ClientDetailsEntity} using the component parts, and
     * not the underyling entity directly.
     * 
     * @param orcid
     *            the ORCID that will be the owner of this client. Each client
     *            can have one, and one only profile associated with it
     * @param name
     *          The client name
     * @param description
     *          The client description 
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
    ClientDetailsEntity createClientDetails(String orcid, String name, String description, String website, String clientId, String clientSecret, Set<String> clientScopes, Set<String> clientResourceIds,
            Set<String> clientAuthorizedGrantTypes, Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities);

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

    ClientDetailsEntity merge(ClientDetailsEntity clientDetails);
    
    void remove(String clientId);
    
    ClientDetailsEntity find(String clientId);
    
    List<ClientDetailsEntity> getAll();
    
    void updateLastModified(String clientId);
    
    boolean removeClientSecret(String clientId, String clientSecret);
    
    boolean addClientSecret(String clientId, String clientSecret);
}
