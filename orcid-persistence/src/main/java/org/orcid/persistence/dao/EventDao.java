package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.EventEntity;

import java.util.List;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface EventDao {

    void createEvent(EventEntity eventEntity);

    EventEntity find(long id);

    void delete(long id);
    
    List<EventEntity> findAll();
    
    void deleteEventsByDate(Integer numberOfDays);
}
