package org.orcid.persistence.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Declan Newman
 */
@PersistenceContext(unitName = "orcid")
public class ClientDetailsDaoImpl extends GenericDaoImpl<ClientDetailsEntity, String> implements ClientDetailsDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDetailsDaoImpl.class);

    private static final String PUBLIC_CLIENT = "PUBLIC_CLIENT";

    public ClientDetailsDaoImpl() {
        super(ClientDetailsEntity.class);
    }

    @Override
    @Cacheable(value = "client-details", key = "#clientId.concat('-').concat(#lastModified)")
    public ClientDetailsEntity findByClientId(String clientId, long lastModified) {
        TypedQuery<ClientDetailsEntity> query = entityManager.createQuery("from ClientDetailsEntity where id = :clientId", ClientDetailsEntity.class);
        query.setParameter("clientId", clientId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.debug("No client found for {}", clientId, e);
            return null;
        }
    }

    @Override
    public Date getLastModified(String clientId) {
        TypedQuery<Date> query = entityManager.createQuery("select lastModified from ClientDetailsEntity where id = :clientId", Date.class);
        query.setParameter("clientId", clientId);
        return query.getSingleResult();
    }

    @Override
    public Date getLastModifiedByIdP(String idp) {
        TypedQuery<Date> query = entityManager.createQuery("select lastModified from ClientDetailsEntity where authenticationProviderId = :idp", Date.class);
        query.setParameter("idp", idp);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateLastModified(String clientId) {
        Query updateQuery = entityManager.createQuery("update ClientDetailsEntity set lastModified = now() where id = :clientId");
        updateQuery.setParameter("clientId", clientId);
        updateQuery.executeUpdate();
    }

    /**
     * Update the last modified dates of given client ids
     * 
     * @param clientIds
     *            A list of client ids
     * @return the amount of modified rows
     */

    @Override
    @Transactional
    public int updateLastModifiedBulk(List<String> clientIds) {
        Query updateQuery = entityManager.createQuery("update ClientDetailsEntity set lastModified = now() where id in :clientIds");
        updateQuery.setParameter("clientIds", clientIds);
        return updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public boolean removeClientSecret(String clientId, String clientSecret) {
        Query deleteQuery = entityManager.createNativeQuery("delete from client_secret where client_details_id=:clientId and client_secret=:clientSecret");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("clientSecret", clientSecret);
        return deleteQuery.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean createClientSecret(String clientId, String clientSecret) {
        Query deleteQuery = entityManager.createNativeQuery(
                "INSERT INTO client_secret (client_details_id, client_secret, date_created, last_modified) VALUES (:clientId, :clientSecret, now(), now())");
        deleteQuery.setParameter("clientId", clientId);
        deleteQuery.setParameter("clientSecret", clientSecret);
        return deleteQuery.executeUpdate() > 0;
    }

    @Override
    public List<ClientSecretEntity> getClientSecretsByClientId(String clientId) {
        TypedQuery<ClientSecretEntity> query = entityManager.createQuery("From ClientSecretEntity WHERE client_details_id=:clientId", ClientSecretEntity.class);
        query.setParameter("clientId", clientId);
        return query.getResultList();
    }

    @Override
    public boolean exists(String clientId) {
        TypedQuery<Long> query = entityManager.createQuery("select count(*) from ClientDetailsEntity where client_details_id=:clientId", Long.class);
        query.setParameter("clientId", clientId);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public boolean belongsTo(String clientId, String groupId) {
        TypedQuery<ClientDetailsEntity> query = entityManager.createQuery("from ClientDetailsEntity where id = :clientId and groupProfileId = :groupId",
                ClientDetailsEntity.class);
        query.setParameter("clientId", clientId);
        query.setParameter("groupId", groupId);
        try {
            query.getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public void updateClientType(String clientType, String clientId) {
        Query updateQuery = entityManager.createQuery("update ClientDetailsEntity set clientType = :clientType, lastModified = now() where id = :clientId");
        updateQuery.setParameter("clientType", clientType);
        updateQuery.setParameter("clientId", clientId);
        updateQuery.executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClientDetailsEntity> findByGroupId(String groupId) {
        Query query = entityManager.createQuery("from ClientDetailsEntity where groupProfileId = :groupId");
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void removeClient(String clientId) {
        ClientDetailsEntity clientDetailsEntity = this.find(clientId);
        this.remove(clientDetailsEntity);
    }

    /**
     * Get the public client that belongs to the given orcid ID
     * 
     * @param ownerId
     *            The user or group id
     * @return the public client that belongs to the given user
     */
    @Override
    public ClientDetailsEntity getPublicClient(String ownerId) {
        TypedQuery<ClientDetailsEntity> query = entityManager.createQuery("from ClientDetailsEntity where groupProfileId = :ownerId and clientType = :clientType",
                ClientDetailsEntity.class);
        query.setParameter("ownerId", ownerId);
        query.setParameter("clientType", PUBLIC_CLIENT);
        try {
            return query.getSingleResult();
        } catch (NoResultException nre) {
            LOGGER.warn("There is not public client for " + ownerId);
            return null;
        }
    }

    /**
     * Get member name
     * 
     * @param clientId
     *            The client id
     * @return the name of the member owner of the given client
     */
    public String getMemberName(String clientId) {
        TypedQuery<String> query = entityManager
                .createQuery("select creditName from RecordNameEntity where orcid = (select groupProfileId from ClientDetailsEntity where id=:clientId)", String.class);
        query.setParameter("clientId", clientId);
        return query.getSingleResult();
    }

    @Override
    public boolean existsAndIsNotPublicClient(String clientId) {
        TypedQuery<Long> query = entityManager
                .createQuery("select count(*) from ClientDetailsEntity where client_details_id=:clientId and client_type != 'PUBLIC_CLIENT'", Long.class);
        query.setParameter("clientId", clientId);
        Long result = query.getSingleResult();
        return (result != null && result > 0);
    }

    @Override
    public Date getLastModifiedIfNotPublicClient(String clientId) {
        Query query = entityManager.createQuery("SELECT lastModified FROM ClientDetailsEntity WHERE id = :id AND clientType != :type");
        query.setParameter("id", clientId);
        query.setParameter("type", PUBLIC_CLIENT);
        Date result = (Date) query.getSingleResult();
        return result;
    }

    @Override
    public ClientDetailsEntity findByIdP(String idp) {
        TypedQuery<ClientDetailsEntity> query = entityManager.createQuery("from ClientDetailsEntity where authenticationProviderId = :idp", ClientDetailsEntity.class);
        query.setParameter("idp", idp);
        return query.getSingleResult();
    }

    @Override
    public List<String> findLegacyClientIds() {
        TypedQuery<String> query = entityManager.createQuery("select id from ClientDetailsEntity where id not like 'APP-%'", String.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void changePersistenceTokensProperty(String clientId, boolean isPersistenTokensEnabled) {
        Query updateQuery = entityManager
                .createQuery("update ClientDetailsEntity set lastModified = now(), persistentTokensEnabled = :isPersistenTokensEnabled where id = :clientId");
        updateQuery.setParameter("clientId", clientId);
        updateQuery.setParameter("isPersistenTokensEnabled", isPersistenTokensEnabled);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void activateClient(String clientDetailsId) {
        Query updateQuery = entityManager
                .createQuery("update ClientDetailsEntity set lastModified = now(), deactivatedDate = null, deactivatedBy = null where id = :clientId");
        updateQuery.setParameter("clientId", clientDetailsId);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void deactivateClient(String clientDetailsId, String deactivatedBy) {
        Query updateQuery = entityManager
                .createQuery("update ClientDetailsEntity set lastModified = now(), deactivatedDate = now(), deactivatedBy = :deactivatedBy where id = :clientId");
        updateQuery.setParameter("clientId", clientDetailsId);
        updateQuery.setParameter("deactivatedBy", deactivatedBy);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public boolean convertPublicClientToMember(String clientId, String groupId, String clientType) {
        Query updateQuery = entityManager.createNativeQuery(
                "UPDATE client_details SET last_modified = now(), group_orcid = :groupId, client_type = :clientType WHERE client_details_id = :clientId");
        updateQuery.setParameter("clientId", clientId);
        updateQuery.setParameter("groupId", groupId);
        updateQuery.setParameter("clientType", clientType);
        return updateQuery.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateClientGrantedAuthority(String clientId, String grantedAuthority) {
        Query updateGrantedAuthorityQuery = entityManager
                .createNativeQuery("UPDATE client_granted_authority SET granted_authority = :authority WHERE client_details_id = :clientId");
        updateGrantedAuthorityQuery.setParameter("authority", grantedAuthority);
        updateGrantedAuthorityQuery.setParameter("clientId", clientId);
        return updateGrantedAuthorityQuery.executeUpdate() > 0;
    }

    @Override
    @Transactional
    public boolean updateNotificationInfo(String clientId, boolean userNotificationEnabled, String notificationWebpageUrl, String notificationDomains) {
        Query updateNotificationInfoQuery = entityManager.createNativeQuery(
                "UPDATE client_details SET user_notification_enabled= :userNotificationEnabled, notification_webpage_url= :notificationWebpageUrl, notification_domains= :notificationDomains  WHERE id = :clientId");
        updateNotificationInfoQuery.setParameter("clientId", clientId);
        updateNotificationInfoQuery.setParameter("userNotificationEnabled", userNotificationEnabled);
        updateNotificationInfoQuery.setParameter("notificationWebpageUrl", notificationWebpageUrl);
        updateNotificationInfoQuery.setParameter("notificationDomains", notificationDomains);
        return updateNotificationInfoQuery.executeUpdate() > 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ClientDetailsEntity> findMVPEnabled() {
        Query query = entityManager.createQuery("from ClientDetailsEntity where userNotificationEnabled = :userNotificationEnabled");
        query.setParameter("userNotificationEnabled", true);
        return query.getResultList();
    }

}
