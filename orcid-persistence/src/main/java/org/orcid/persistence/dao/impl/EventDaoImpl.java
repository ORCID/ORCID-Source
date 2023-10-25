package org.orcid.persistence.dao.impl;

import org.orcid.persistence.aop.UpdateProfileLastModified;
import org.orcid.persistence.dao.EventDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EventEntity;
import org.orcid.persistence.jpa.entities.SpamEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Daniel Palafox
 */
public class EventDaoImpl extends GenericDaoImpl<EventEntity, Long> implements EventDao {

    public EventDaoImpl() {
        super(EventEntity.class);
    }

    @Override
    public List<EventEntity> getEvents(String orcid) {
        TypedQuery<EventEntity> query = entityManager.createQuery("from EventEntity where orcid=:orcid", EventEntity.class);
        query.setParameter("orcid", orcid);
        List<EventEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }

    @Override
    @Transactional
    public void createEvent(EventEntity eventEntity) {
        entityManager.persist(eventEntity);
    }

    @Override
    @Transactional
    public boolean removeEvents(String orcid) {
        Query query = entityManager.createQuery("delete from EventEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
        return query.executeUpdate() > 0;
    }
}
