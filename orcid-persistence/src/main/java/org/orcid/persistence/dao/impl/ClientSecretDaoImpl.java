package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ClientSecretDao;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.persistence.jpa.entities.keys.ClientSecretPk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ClientSecretDaoImpl extends GenericDaoImpl<ClientSecretEntity, ClientSecretPk> implements ClientSecretDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailsDaoImpl.class);

    public ClientSecretDaoImpl() {
        super(ClientSecretEntity.class);
    }
    
    /**
     * Removes a client secret key
     * @param clientId
     * @param clientSecret
     * @return true if a entity is removed
     * */
    @Override
    public boolean removeClientSecret(String clientId, String clientSecret) {
        Query deleteQuery = entityManager.createNativeQuery("delete from client_secret where client_details_id=:clientId and client_secret=:clientSecret");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("clientSecret", clientSecret);
        return deleteQuery.executeUpdate() > 0;
    }

    /**
     * Creates a client secret key
     * @param clientId
     * @param clientSecret
     * @return true if the entity was created
     * */
    @Override
    public boolean createClientSecret(String clientId, String clientSecret) {
        Query deleteQuery = entityManager.createNativeQuery("INSERT INTO client_secret (client_details_id, client_secret, is_primary, date_created, last_modified) VALUES (:clientId, :clientSecret, true, now(), now())");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("clientSecret", clientSecret);
        return deleteQuery.executeUpdate() > 0;
    }

    /**
     * Get the list of client secrets associated with a client
     * @param clientId
     * @return a list of all client secrets associated with a client
     * */
    @Override
    public List<ClientSecretEntity> getClientSecretsByClientId(String clientId) {
        TypedQuery<ClientSecretEntity> query = entityManager.createQuery("From ClientSecretEntity WHERE client_details_id=:clientId", ClientSecretEntity.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    /**
     * Revoke all existing key for a user
     * @param clientId
     * @return true if all keys where successfully revoked
     * */
    @Override
    @Transactional
    public boolean revokeAllKeys(String clientId) {
        Query revokeAllKeys = entityManager.createNativeQuery("UPDATE client_secret SET is_primary=false WHERE client_details_id=:clientId");
        revokeAllKeys.setParameter("clientId", clientId);
        return revokeAllKeys.executeUpdate() > 0;
    }

}
