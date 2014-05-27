package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.persistence.jpa.entities.keys.ClientSecretPk;

public interface ClientSecretDao extends GenericDao<ClientSecretEntity, ClientSecretPk> {
    /**
     * Removes a client secret key
     * @param clientId
     * @param clientSecret
     * @return true if a entity is removed
     * */
    boolean removeClientSecret(String clientId, String clientSecret);    
    
    /**
     * Creates a client secret key
     * @param clientId
     * @param clientSecret
     * @return true if the entity was created
     * */
    boolean createClientSecret(String clientId, String clientSecret);    
    
    /**
     * Get the list of client secrets associated with a client
     * @param clientId
     * @return a list of all client secrets associated with a client
     * */
    List<ClientSecretEntity> getClientSecretsByClientId(String clientId);
    
    /**
     * Revoke all existing key for a user
     * @param clientId
     * @return true if all keys where successfully revoked
     * */
    boolean revokeAllKeys(String clientId);
}
