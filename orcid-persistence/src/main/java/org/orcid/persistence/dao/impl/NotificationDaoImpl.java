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
package org.orcid.persistence.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.notification_rc3.NotificationType;
import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class NotificationDaoImpl extends GenericDaoImpl<NotificationEntity, Long> implements NotificationDao {

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
        return query.getResultList();
    }

    @Override
    public List<NotificationEntity> findNotificationAlertsByOrcid(String orcid) {
        TypedQuery<NotificationEntity> query = entityManager
                .createQuery(
                        "select n from NotificationEntity n, ClientRedirectUriEntity r where n.notificationType = 'INSTITUTIONAL_CONNECTION' and n.readDate is null and n.archivedDate is null and n.profile.id = :orcid and n.clientSourceId = r.clientDetailsEntity.id and r.redirectUriType = 'institutional-sign-in' order by n.dateCreated desc",
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
    public List<String> findOrcidsWithNotificationsToSend() {
        return findOrcidsWithNotificationsToSend(new Date());
    }

    @Override
    public List<String> findOrcidsWithNotificationsToSend(Date effectiveNow) {
        TypedQuery<String> query = entityManager.createNamedQuery(NotificationEntity.FIND_ORCIDS_WITH_NOTIFICATIONS_TO_SEND, String.class);
        query.setParameter("effectiveNow", effectiveNow);
        return query.getResultList();
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
    public List<NotificationEntity> findPermissionsByOrcidAndClient(String orcid, String client, int firstResult, int maxResults) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery(
                "from NotificationEntity where orcid = :orcid and clientSourceId = :client and notificationType = :notificationType", NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("client", client);
        query.setParameter("notificationType", NotificationType.PERMISSION);
        return query.getResultList();
    }

}
