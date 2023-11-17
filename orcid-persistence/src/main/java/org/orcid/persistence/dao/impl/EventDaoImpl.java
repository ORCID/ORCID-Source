package org.orcid.persistence.dao.impl;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.orcid.persistence.dao.EventDao;
import org.orcid.persistence.jpa.entities.EventEntity;
import org.springframework.transaction.annotation.Transactional;

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
    
}
