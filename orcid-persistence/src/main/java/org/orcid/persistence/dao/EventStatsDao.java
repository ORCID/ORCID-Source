package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.EventStatsEntity;

import java.util.List;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface EventStatsDao {

    void createEventStats();

    void createPapiEventStats();

    List<EventStatsEntity> findAll();
}
