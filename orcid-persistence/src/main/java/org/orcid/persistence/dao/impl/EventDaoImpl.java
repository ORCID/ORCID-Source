package org.orcid.persistence.dao.impl;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.EventDao;
import org.orcid.persistence.jpa.entities.EventEntity;
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
    public List<EventEntity> findAll() {
        TypedQuery<EventEntity> query = entityManager.createQuery("from EventEntity", EventEntity.class);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteEventsByDate(Integer numberOfDays) {
        String query = "DELETE FROM event where CAST(date_created as date) < CAST(now() - (CAST('1' AS INTERVAL DAY) * 1) as date) * :numberOfDays)";
        Query queryDelete = entityManager.createNativeQuery(query);
        queryDelete.setParameter("numberOfDays", numberOfDays);
        queryDelete.executeUpdate();
    }
}
