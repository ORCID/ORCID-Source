package org.orcid.core.manager.v3;

import java.util.List;
import java.util.Set;

import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface ClientDetailsManager extends ClientDetailsManagerReadOnly {
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
     * @param allowAutoDeprecate
     *          Indicates if the client will enable auto deprecating unclaimed records.           
     * @return
     */
    ClientDetailsEntity createClientDetails(String memberId, String name, String description, String idp, String website, ClientType clientType, Set<String> clientScopes,
            Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes, Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities, Boolean allowAutoDeprecate);    

    void removeByClientId(String clientId);    

    public void addClientRedirectUri(String clientId, String uri);

    ClientDetailsEntity merge(ClientDetailsEntity clientDetails);    

    void updateLastModified(String clientId);
    
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

    /**
     * Utility function that will help us to create and persist a clientDetailsEntity giving all the details
     * */
    ClientDetailsEntity populateClientDetailsEntity(String clientId, String memberId, String name, String description, String idp, String website,
            String clientSecret, ClientType clientType, Set<String> clientScopes, Set<String> clientResourceIds, Set<String> clientAuthorizedGrantTypes,
            Set<RedirectUri> clientRegisteredRedirectUris, List<String> clientGrantedAuthorities, Boolean allowAutoDeprecate);
    
    ClientDetailsEntity findByIdP(String idp);    
    
    void addScopesToClient(Set<String> clientScopeStrings, ClientDetailsEntity clientDetails);

    void addAuthorizedGrantTypeToClient(Set<String> types, ClientDetailsEntity clientDetails);

    void addClientRedirectUri(String clientId, String uri, RedirectUriType importWorksWizard);

}
