package org.orcid.persistence.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Declan Newman
 * 
 */
public interface ClientDetailsDao extends GenericDao<ClientDetailsEntity, String> {

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    ClientDetailsEntity findByClientId(String clientId, long lastModified);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Date getLastModified(String clientId);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Map<String, Date> getLastModifiedByClientIds(List<String> clientIds);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ClientDetailsEntity> findByClientIds(List<String> clientIds);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Date getLastModifiedByIdP(String idp);
    
    @Transactional(propagation = Propagation.REQUIRED)
    void updateLastModified(String clientId);
    
    @Transactional(propagation = Propagation.REQUIRED)
    int updateLastModifiedBulk(List<String> clientIds);
    
    @Transactional(propagation = Propagation.REQUIRED)
    void updateClientType(String clientType, String clientId);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeClientSecret(String clientId, String clientSecret);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean createClientSecret(String clientId, String clientSecret);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ClientSecretEntity> getClientSecretsByClientId(String clientId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean exists(String clientId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean belongsTo(String clientId, String groupId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ClientDetailsEntity> findByGroupId(String groupId);
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void removeClient(String clientId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public ClientDetailsEntity getPublicClient(String ownerId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    String getMemberName(String clientId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean existsAndIsNotPublicClient(String clientId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Date getLastModifiedIfNotPublicClient(String clientId);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    ClientDetailsEntity findByIdP(String idp);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<String> findLegacyClientIds();
    
    @Transactional(propagation = Propagation.REQUIRED)
    void changePersistenceTokensProperty(String clientId, boolean isPersistenTokensEnabled);

    @Transactional(propagation = Propagation.REQUIRED)
    void activateClient(String clientDetailsId);

    @Transactional(propagation = Propagation.REQUIRED)
    void deactivateClient(String clientDetailsId, String deactivatedBy);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean convertPublicClientToMember(String clientId, String groupId, String name);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateClientGrantedAuthority(String clientId, String grantedAuthority);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateNotificationInfo(String clientId, boolean notificationEnabled, String notificationWebUrl, String notificationDomains);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ClientDetailsEntity> findMVPEnabled();

}
