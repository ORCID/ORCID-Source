package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.aop.UpdateProfileLastModified;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class NotificationDaoImpl extends GenericDaoImpl<NotificationEntity, Long> implements NotificationDao {

    private static final String NOTIFICATION_TYPE_PERMISSION = "PERMISSION";
    
    @Value("${org.orcid.postgres.query.timeout:30000}")
    private Integer queryTimeout;
    
    @Autowired
    @Qualifier("notification_queries")
    private Properties notificationQueries;
    
    public NotificationDaoImpl() {
        super(NotificationEntity.class);
    }

    @Override
    public List<NotificationEntity> findByOrcid(String orcid, boolean includeArchived, int firstResult, int maxResults) {
        StringBuilder builder = new StringBuilder("from NotificationEntity where orcid = :orcid");
        if (!includeArchived) {
            builder.append(" and archivedDate is null");
        }
        builder.append(" order by dateCreated desc");
        TypedQuery<NotificationEntity> query = entityManager.createQuery(builder.toString(), NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public NotificationEntity findLatestByOrcid(String orcid) {
        List<NotificationEntity> results = findByOrcid(orcid, false, 0, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<NotificationEntity> findUnsentByOrcid(String orcid) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery("from NotificationEntity where sentDate is null and orcid = :orcid", NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setHint("javax.persistence.query.timeout", queryTimeout);
        return query.getResultList();
    }

    @Override
    public List<NotificationEntity> findNotificationAlertsByOrcid(String orcid) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
                "select n from NotificationEntity n, ClientRedirectUriEntity r where n.notificationType = 'INSTITUTIONAL_CONNECTION' and n.readDate is null and n.archivedDate is null and n.orcid = :orcid and n.clientSourceId = r.clientId and r.redirectUriType = 'institutional-sign-in' order by n.dateCreated desc",
                NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setMaxResults(3);
        return query.getResultList();
    }

    @Override
    public int getUnreadCount(String orcid) {
        TypedQuery<Long> query = entityManager.createQuery("select count(*) from NotificationEntity where readDate is null and archivedDate is null and orcid = :orcid",
                Long.class);
        query.setParameter("orcid", orcid);
        return query.getSingleResult().intValue();
    }


    @Override
    public int getTotalCount(String orcid, boolean archived) {
        StringBuffer sb = new StringBuffer();
        sb.append("select count(*) from NotificationEntity where orcid = :orcid");
        if (!archived) {
            sb.append(" and archivedDate is null");
        } 
        TypedQuery<Long> query = entityManager.createQuery(sb.toString(),
                Long.class);
        query.setParameter("orcid", orcid);       
        return query.getSingleResult().intValue();
    }

    @Override
    public NotificationEntity findByOricdAndId(String orcid, Long id) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery("from NotificationEntity where orcid = :orcid and id = :id", NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        List<NotificationEntity> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    @Override
    @Transactional
    public void flagAsSent(Long id) {
        Query query = entityManager.createQuery("update NotificationEntity set sentDate = now() where id in :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void flagAsSent(Collection<Long> ids) {
        Query query = entityManager.createQuery("update NotificationEntity set sentDate = now() where id in :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void flagAsRead(String orcid, Long id) {
        Query query = entityManager.createQuery("update NotificationEntity set readDate = now() where orcid = :orcid and id = :id and readDate is null");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void flagAsArchived(String orcid, Long id) {
        Query query = entityManager.createQuery("update NotificationEntity set archivedDate = now() where orcid = :orcid and id = :id and archivedDate is null");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteNotificationItemByNotificationId(Long notificationId) {
        Query query = entityManager.createNativeQuery("delete from notification_item where notification_id = :id");
        query.setParameter("id", notificationId);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteNotificationWorkByNotificationId(Long notificationId) {
        Query query = entityManager.createNativeQuery("delete from notification_work where notification_id = :id");
        query.setParameter("id", notificationId);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void deleteNotificationById(Long notificationId) {
        Query query = entityManager.createNativeQuery("delete from notification where id = :id");
        query.setParameter("id", notificationId);
        query.executeUpdate();
    }
    
    @Override
    @Transactional
    public boolean deleteNotificationsForRecord(String orcid, int batchSize) {
        TypedQuery<Long> idsQuery = entityManager.createQuery("SELECT id from NotificationEntity where orcid = :orcid", Long.class);
        idsQuery.setParameter("orcid", orcid);
        idsQuery.setMaxResults(batchSize);
        List<Long> ids = idsQuery.getResultList();
        
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        
        Query deleteQuery = entityManager.createNativeQuery("delete from notification_item where notification_id in (:ids)");
        deleteQuery.setParameter("ids", ids);
        int affected = deleteQuery.executeUpdate();
        
        deleteQuery = entityManager.createNativeQuery("delete from notification_work where notification_id in (:ids)");
        deleteQuery.setParameter("ids", ids);
        affected += deleteQuery.executeUpdate();
        
        deleteQuery = entityManager.createNativeQuery("delete from notification where id in (:ids)");
        deleteQuery.setParameter("ids", ids);
        affected += deleteQuery.executeUpdate();
        
        return affected > 0;
    }

    @Override
    public List<NotificationEntity> findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
                "from NotificationEntity where orcid = :orcid and clientSourceId = :client and notificationType = :notificationType", NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("client", client);
        query.setParameter("notificationType", NOTIFICATION_TYPE_PERMISSION);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findRecordsWithUnsentNotifications() {
        Query query = entityManager.createNamedQuery(NotificationEntity.FIND_ORCIDS_WITH_UNSENT_NOTIFICATIONS_ON_EMAIL_FREQUENCIES_TABLE);
        query.setParameter("never", Float.MAX_VALUE);  
        query.setHint("javax.persistence.query.timeout", queryTimeout);
        return query.getResultList();
    }
               

    @Override
    public List<NotificationEntity> findNotificationsToSendLegacy(Date effectiveDate, String orcid, Float emailFrequency, Date recordActiveDate) {
        TypedQuery<NotificationEntity> query = entityManager.createNamedQuery(NotificationEntity.FIND_NOTIFICATIONS_TO_SEND_BY_ORCID, NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("effective_date", effectiveDate);
        query.setParameter("record_email_frequency", emailFrequency);
        query.setParameter("record_active_date", recordActiveDate);
        return query.getResultList();        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NotificationEntity> findNotificationsToSend(Date effectiveDate, String orcid, Date recordActiveDate) {
        String unsentNotificationsQuery = notificationQueries.getProperty("notifications.unsent");
        Query query = entityManager.createNativeQuery(unsentNotificationsQuery, NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("effective_date", effectiveDate);
        query.setParameter("record_active_date", recordActiveDate);
        return query.getResultList();
    }
        
    @Override
    @Transactional
    public int archiveNotificationsCreatedBefore(Date createdBefore, int batchSize) {
        Query archiveQuery = entityManager.createNativeQuery("update notification set archived_date=now() where id in (select id from notification where archived_date is null and date_created < :createdBefore limit :batchSize)");
        archiveQuery.setParameter("createdBefore", createdBefore);
        archiveQuery.setParameter("batchSize", batchSize);
        return archiveQuery.executeUpdate();
    }

    @Override
    @Transactional
    public int deleteNotificationsCreatedBefore(Date createdBefore, int batchSize) {
        TypedQuery<Long> query = entityManager.createQuery("select id from NotificationEntity where dateCreated < :createdBefore", Long.class);
        query.setParameter("createdBefore", createdBefore);
        query.setMaxResults(batchSize);
        List<Long> ids = query.getResultList();
        if(ids != null && !ids.isEmpty()) {
            // Remove notification items
            Query deleteNotificationItems = entityManager.createNativeQuery("delete from notification_item where notification_id in (:ids)");
            deleteNotificationItems.setParameter("ids", ids);
            deleteNotificationItems.executeUpdate();

            // Remove notification works
            Query deleteNotificationWorks = entityManager.createNativeQuery("delete from notification_work where notification_id in (:ids)");
            deleteNotificationWorks.setParameter("ids", ids);
            deleteNotificationWorks.executeUpdate();

            Query deleteNotification = entityManager.createNativeQuery("delete from notification where id in (:ids)");
            deleteNotification.setParameter("ids", ids);
            return deleteNotification.executeUpdate();
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NotificationEntity> findUnsentServiceAnnouncements(int batchSize) {
        Query query = entityManager.createNativeQuery("select n.* from notification n where n.sent_date is NULL AND n.sendable != false AND n.notification_type = 'SERVICE_ANNOUNCEMENT'", NotificationEntity.class);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<NotificationEntity> findUnsentTips(int batchSize) {
        Query query = entityManager.createNativeQuery("select n.* from notification n join email_frequency ef on n.orcid = ef.orcid AND ef.send_quarterly_tips IS true where n.notification_type = 'TIP' AND n.sent_date is NULL AND n.sendable != false", NotificationEntity.class);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }
    
    @Override
    public void flagAsSendable(String orcid, Long id) {
        Query query = entityManager.createQuery("update NotificationEntity set sendable=true where orcid = :orcid and id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        query.executeUpdate();
    }
    
    @Override
    public void flagAsNonSendable(String orcid, Long id) {
        Query query = entityManager.createQuery("update NotificationEntity set sendable=false where orcid = :orcid and id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        query.executeUpdate();
    }    
    
    @Override
    public void updateRetryCount(String orcid, Long id, Long retryCount) {
        Query query = entityManager.createQuery("update NotificationEntity set retryCount = :count where orcid = :orcid and id = :id");
        query.setParameter("count", retryCount);
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public Integer archiveOffsetNotifications(Integer offset) {
        Query selectQuery = entityManager.createNativeQuery("SELECT orcid FROM notification WHERE archived_date IS NULL group by orcid having count(*) > :offset");
        selectQuery.setParameter("offset", offset);
        @SuppressWarnings("unchecked")
        List<String> ids = selectQuery.getResultList();
        if (ids.isEmpty()) {
            return 0;
        }
        
        int result = 0;
        
        for(String orcid : ids) {
            Query archiveQuery = entityManager.createNativeQuery("UPDATE notification SET archived_date=now() WHERE id in (SELECT id FROM notification WHERE orcid=:orcid AND archived_date IS NULL order by date_created desc OFFSET :offset)");
            archiveQuery.setParameter("orcid", orcid);
            archiveQuery.setParameter("offset", offset);
            result += archiveQuery.executeUpdate();
        }
        
        return result;
    }

    @Override
    public List<Object[]> findNotificationsToDeleteByOffset(Integer offset, Integer recordsPerBatch) {
        Query selectQuery = entityManager.createNativeQuery("SELECT orcid FROM notification group by orcid having count(*) > :offset order by count(*) desc limit :limit");
        selectQuery.setParameter("offset", offset);
        selectQuery.setParameter("limit", recordsPerBatch);
        @SuppressWarnings("unchecked")
        List<String> ids = selectQuery.getResultList();
        if (ids.isEmpty()) {
            return new ArrayList<Object[]>();
        }
        
        List<Object[]> results = new ArrayList<Object[]>();
        
        for(String orcid : ids) {
            Query archiveQuery = entityManager.createNativeQuery("SELECT id, orcid FROM notification WHERE orcid=:orcid order by date_created desc OFFSET :offset");
            archiveQuery.setParameter("orcid", orcid);
            archiveQuery.setParameter("offset", offset);
            results.addAll(archiveQuery.getResultList());
        }
        
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM notification WHERE client_source_id = source_id AND client_source_id IN :nonPublicClients");
        query.setParameter("nonPublicClients", nonPublicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctClientSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE notification SET client_source_id = source_id, source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM notification WHERE client_source_id = source_id AND client_source_id IN :publicClients");
        query.setParameter("publicClients", publicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctUserSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE notification SET source_id = client_source_id, client_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsOfNotificationsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds) {
        Query query = entityManager.createNativeQuery("SELECT id FROM notification WHERE source_id IN :ids");
        query.setParameter("ids", clientProfileOrcidIds);
        query.setMaxResults(max);
        return query.getResultList();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<NotificationEntity> findNotificationsByOrcidAndClientAndFamilyNoClientToken(String orcid, String clientId, String notificationFamily){
        Query query = entityManager.createNativeQuery("SELECT * FROM notification WHERE client_source_id = :clientId AND orcid = :orcid "
                + "AND notification_family = :notificationFamily AND NOT EXISTS (SELECT 1  FROM oauth2_token_detail WHERE "
                + "oauth2_token_detail.user_orcid = notification.orcid AND oauth2_token_detail.client_details_id = notification.client_source_id)", NotificationEntity.class);
        query.setParameter("clientId", clientId);
        query.setParameter("orcid", orcid);
        query.setParameter("notificationFamily", notificationFamily);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void persist(NotificationEntity entity) {
        super.persist(entity);
    }
    
    @Override
    @Transactional
    public NotificationEntity merge(NotificationEntity entity) {
        return super.merge(entity);
    }
}
