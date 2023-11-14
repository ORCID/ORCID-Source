package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.EventEntity;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface EventDao {

    void createEvent(EventEntity eventEntity);

    EventEntity find(long id);
}
