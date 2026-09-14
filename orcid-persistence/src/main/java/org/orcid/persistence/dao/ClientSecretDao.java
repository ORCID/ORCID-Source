package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.persistence.jpa.entities.keys.ClientSecretPk;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ClientSecretDao extends GenericDao<ClientSecretEntity, ClientSecretPk> {
    /**
     * Removes a client secret key
     * 
     * @param clientId
     * @param clientSecret
     * @return true if a entity is removed
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeClientSecret(String clientId, String clientSecret);

    /**
     * Creates a client secret key
     * 
     * @param clientId
     * @param clientSecret
     * @return true if the entity was created
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean createClientSecret(String clientId, String clientSecret);

    /**
     * Get the list of client secrets associated with a client
     * 
     * @param clientId
     * @return a list of all client secrets associated with a client
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ClientSecretEntity> getClientSecretsByClientId(String clientId);

    /**
     * Revoke all existing key for a user
     * 
     * @param clientId
     * @return true if all keys where successfully revoked
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean revokeAllKeys(String clientId);

    /**
     * Sets the given client secret as the primary client secret
     * 
     * @param clientSecret
     * @return true if it was possible to set the client secret as primary
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean setAsPrimary(ClientSecretEntity clientSecret);    
    
    /**
     * Get a list of non-primary keys which have not been modified in over 24 hours
     * 
     * @param limit the amount of results fetched by the query
     * @return A list of client secrets with non-primary keys
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ClientSecretEntity> getNonPrimaryKeys(Integer limit);
    
    /**
     * Performs a delete query with a custom conditional statement
     * 
     * @param conditional statement for the return query
     * @return true if the condition was met and all keys were removed
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeWithCustomCondition(String condition);
}
