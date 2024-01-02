package org.orcid.persistence.dao.impl;

import org.orcid.persistence.dao.EventStatsDao;
import org.orcid.persistence.jpa.entities.EventStatsEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Daniel Palafox
 */
public class EventStatsDaoImpl implements EventStatsDao {

    @Resource(name = "entityManager")
    protected EntityManager entityManager;

    @Override
    @Transactional
    public void createEventStats() {
        String query = 
                "INSERT INTO event_stats (event_type, client_id, count, date, date_created, last_modified) " +
                "SELECT event_type, client_id, COUNT(id), CAST(e.date_created as date), now(), now() " +
                "FROM event as e " +
                "WHERE event_type != 'Public-Page' " +
                "AND CAST(e.date_created as date) = CAST(now() - (CAST('1' AS INTERVAL DAY) * 1) as date) " +
                "GROUP BY event_type, client_id, CAST(e.date_created as date) " +
                "ORDER BY CAST(e.date_created as date) DESC;";
        Query insertQuery = entityManager.createNativeQuery(query);
        insertQuery.executeUpdate();
    }

    @Override
    public List<EventStatsEntity> findAll() {
        TypedQuery<EventStatsEntity> query = entityManager.createQuery("from EventStatsEntity", EventStatsEntity.class);
        return query.getResultList();
    }
}
