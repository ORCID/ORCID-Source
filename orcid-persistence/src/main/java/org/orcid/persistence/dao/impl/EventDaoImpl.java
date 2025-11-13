package org.orcid.persistence.dao.impl;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.EventDao;
import org.orcid.persistence.jpa.entities.EventEntity;
import org.orcid.persistence.jpa.entities.EventType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Daniel Palafox
 */
public class EventDaoImpl implements EventDao {

    @Resource(name="entityManager")
    protected EntityManager entityManager;
    
    public EventDaoImpl() {
        
    }    

    @Override
    @Transactional
    public void createEvent(EventEntity eventEntity) {
        entityManager.persist(eventEntity);
    }

    @Override
    public EventEntity find(long id) {
        return entityManager.find(EventEntity.class, id);
    }

    @Override
    @Transactional
    public void delete(long id) {
        entityManager.remove(find(id));
    }

    @Override
    public List<EventEntity> findAll() {
        TypedQuery<EventEntity> query = entityManager.createQuery("from EventEntity", EventEntity.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteEventsByDate(Integer numberOfDays) {
        String query = "DELETE FROM event where CAST(date_created as date) < CAST(now() - (CAST('1' AS INTERVAL DAY) * :numberOfDays) as date) AND event_type != :eventType";
        Query queryDelete = entityManager.createNativeQuery(query);
        queryDelete.setParameter("eventType", EventType.PAPI.getValue());
        queryDelete.setParameter("numberOfDays", numberOfDays);
        queryDelete.executeUpdate();
    }

    @Override
    @Transactional
    public void deletePapiEvents(Integer numberOfDays) {
        String query = "DELETE FROM event where CAST(date_created as date) < CAST(now() - (CAST('1' AS INTERVAL DAY) * :numberOfDays) as date) AND event_type = :eventType";
        Query queryDelete = entityManager.createNativeQuery(query);
        queryDelete.setParameter("eventType", EventType.PAPI.getValue());
        queryDelete.setParameter("numberOfDays", numberOfDays);
        queryDelete.executeUpdate();
    }

    @Override
    public List<EventEntity> findByEventType(EventType eventType) {
        TypedQuery<EventEntity> query = entityManager.createQuery("from EventEntity where eventType = :eventType", EventEntity.class);
        query.setParameter("eventType", eventType.getValue());
        return query.getResultList();
    }
}
