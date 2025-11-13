package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.EventEntity;
import org.orcid.persistence.jpa.entities.EventType;

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

    List<EventEntity> findByEventType(EventType eventType);
    
    void deleteEventsByDate(Integer numberOfDays);

    void deletePapiEvents(Integer numberOfDays);
}
