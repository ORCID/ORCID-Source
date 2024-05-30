package org.orcid.persistence.dao.impl;

import org.orcid.persistence.dao.EventStatsDao;
import org.orcid.persistence.jpa.entities.EventType;
import org.orcid.persistence.jpa.entities.EventStatsEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.math.BigInteger;
import java.util.ArrayList;
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
                "WHERE event_type != '"+ EventType.PAPI.getValue() + "' " +
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

    @Override
    @Transactional
    public void createPapiEventStats() {
        String query = 
                "SELECT event_type, client_id, ip, label, count(*), CAST(date_created as date) as date, now() as date_created, now() as last_modified " +
                "FROM event " +
                "WHERE event_type = '"+ EventType.PAPI.getValue() + "' " +
                "AND CAST(date_created as date) = CAST(now() - (CAST('1' AS INTERVAL DAY) * 1) as date) " +
                "GROUP BY event_type, client_id, ip, label, CAST(date_created as date) " +
                "ORDER BY CAST(date_created as date) DESC;";

        Query queryList = entityManager.createNativeQuery(query);
        List<Object[]> eventsList = queryList.getResultList();
        List<Object[]> eventsListToRemove = new ArrayList<>();
        if (eventsList.size() > 0) {
            eventsList.forEach(item -> {
                if ("anonymous".equals(item[3]) && item[4] != null && ((BigInteger) item[4]).intValue() < 1000) {
                    eventsListToRemove.add(item);
                }
            });
            eventsList.removeAll(eventsListToRemove);
            eventsList.forEach(item -> {
                String insertQuery = "INSERT INTO event_stats (event_type, client_id, ip, count, date, date_created, last_modified) VALUES (:eventType, :clientId, :ip, :count, :date, :dateCreated, :lastModified)";
                Query insertQueryClients = entityManager.createNativeQuery(insertQuery);
                insertQueryClients.setParameter("eventType", item[0]);
                insertQueryClients.setParameter("clientId", item[1]);
                insertQueryClients.setParameter("ip", item[2]);
                insertQueryClients.setParameter("count", item[4]);
                insertQueryClients.setParameter("date", item[5]);
                insertQueryClients.setParameter("dateCreated", item[6]);
                insertQueryClients.setParameter("lastModified", item[7]);
                insertQueryClients.executeUpdate();
            });
        }
    }

    @Override
    @Transactional
    public void deleteStatsByType(EventType eventType) {
        String query = "DELETE FROM event_stats where event_type = :eventType";
        Query queryDelete = entityManager.createNativeQuery(query);
        queryDelete.setParameter("eventType", eventType.getValue());
        queryDelete.executeUpdate();
    }
}
