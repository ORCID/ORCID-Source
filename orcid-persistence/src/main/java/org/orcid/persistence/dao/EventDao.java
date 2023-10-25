package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.EventEntity;

import java.util.List;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface EventDao extends GenericDao<EventEntity, Long>{

    boolean removeEvents(String orcid);

    List<EventEntity> getEvents(String orcid);

    void createEvent(EventEntity eventEntity);

}
