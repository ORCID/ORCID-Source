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
package org.orcid.persistence.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.NotificationDao;
import org.orcid.persistence.jpa.entities.NotificationEntity;

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
    public List<NotificationEntity> findByOrcid(String orcid, int firstResult, int maxResults) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery("from NotificationEntity where orcid = :orcid order by dateCreated desc",
                NotificationEntity.class);
        query.setParameter("orcid", orcid);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public NotificationEntity findLatestByOrcid(String orcid) {
        List<NotificationEntity> results = findByOrcid(orcid, 0, 1);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<NotificationEntity> findUnsentByOrcid(String orcid) {
        TypedQuery<NotificationEntity> query = entityManager.createQuery("from NotificationEntity where sentDate is null and orcid = :orcid", NotificationEntity.class);
        query.setParameter("orcid", orcid);
        return query.getResultList();
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
        return query.getSingleResult();
    }

}
