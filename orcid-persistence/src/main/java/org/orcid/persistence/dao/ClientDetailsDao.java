package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;

/**
 * 
 * @author Declan Newman
 * 
 */
public interface ClientDetailsDao extends GenericDao<ClientDetailsEntity, String> {

    ClientDetailsEntity findByClientId(String clientId, long lastModified);

    Date getLastModified(String clientId);
    
    Date getLastModifiedByIdP(String idp);
    
    void updateLastModified(String clientId);
    
    void updateClientType(ClientType clientType, String clientId);
    
    boolean removeClientSecret(String clientId, String clientSecret);
    
    boolean createClientSecret(String clientId, String clientSecret);
    
    List<ClientSecretEntity> getClientSecretsByClientId(String clientId);
    
    boolean exists(String clientId);
    
    boolean belongsTo(String clientId, String groupId);
    
    List<ClientDetailsEntity> findByGroupId(String groupId);
    
    public void removeClient(String clientId);
    
    public ClientDetailsEntity getPublicClient(String ownerId);
    
    String getMemberName(String clientId);
    
    boolean existsAndIsNotPublicClient(String clientId);
    
    Date getLastModifiedIfNotPublicClient(String clientId);
    
    ClientDetailsEntity findByIdP(String idp);

    List<String> findLegacyClientIds();
    
    void changePersistenceTokensProperty(String clientId, boolean isPersistenTokensEnabled);
}
