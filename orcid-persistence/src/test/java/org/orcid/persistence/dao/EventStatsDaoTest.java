package org.orcid.persistence.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.EventEntity;
import org.orcid.persistence.jpa.entities.EventStatsEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, locations = {"classpath:test-orcid-persistence-context.xml"})
public class EventStatsDaoTest {

    @Resource(name = "eventDao")
    private EventDao eventDao;

    @Resource(name = "eventStatsDao")
    private EventStatsDao eventStatsDao;
    
    @Test
    public void createEventStats() {
        createEvents();

        eventStatsDao.createEventStats();

        List<EventStatsEntity> eventStatsEntityList = eventStatsDao.findAll();

        assertNotNull(eventStatsEntityList);
        assertEquals(2, eventStatsEntityList.size());
        assertEquals(Integer.valueOf(20), eventStatsEntityList.get(0).getCount());
        assertEquals(Integer.valueOf(20), eventStatsEntityList.get(1).getCount());
    }

    @Test
    public void createPapiEventStats() {
        createPapiEvents();

        eventStatsDao.createPapiEventStats();

        List<EventStatsEntity> eventStatsEntityList = eventStatsDao.findAll();

        assertNotNull(eventStatsEntityList);
        assertEquals(3, eventStatsEntityList.size());
        assertEquals(Integer.valueOf(10), eventStatsEntityList.get(0).getCount());
        assertEquals(Integer.valueOf(10), eventStatsEntityList.get(1).getCount());
        assertEquals(Integer.valueOf(1100), eventStatsEntityList.get(2).getCount());
    }

    private void createPapiEvents() {
        for (int i = 0; i < 20; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEventType("Public-API");
            eventEntity.setClientId("Client " + (i % 2 == 0 ? 1 : 2));
            LocalDate date = LocalDate.now().minusDays(1);
            Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            eventEntity.setDateCreated(Date.from(instant));
            eventDao.createEvent(eventEntity);
        }

        for (int i = 0; i < 10; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEventType("Public-API");
            eventEntity.setClientId("105.21.229.71");
            eventEntity.setLabel("anonymous");
            LocalDate date = LocalDate.now().minusDays(1);
            Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            eventEntity.setDateCreated(Date.from(instant));
            eventDao.createEvent(eventEntity);
        }

        for (int i = 0; i < 1100; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEventType("Public-API");
            eventEntity.setClientId("104.20.228.70");
            eventEntity.setLabel("anonymous");
            LocalDate date = LocalDate.now().minusDays(1);
            Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            eventEntity.setDateCreated(Date.from(instant));
            eventDao.createEvent(eventEntity);
        }
    }

    private void createEvents() {
        for (int i = 0; i < 40; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEventType(i % 2 == 0 ? "Sign-In" : "Public-PAPI");
            eventEntity.setClientId("Client " + 1);
            LocalDate date = LocalDate.now().minusDays(1);
            Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            eventEntity.setDateCreated(Date.from(instant));
            eventDao.createEvent(eventEntity);
        }
    }
    
}
