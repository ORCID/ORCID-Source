package org.orcid.persistence.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.EventEntity;
import org.orcid.persistence.jpa.entities.EventStatsEntity;
import org.orcid.persistence.jpa.entities.EventType;
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
import static org.junit.Assert.assertNull;

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

        eventStatsDao.deleteStatsByType(EventType.SIGN_IN);
    }

    @Test
    public void createPapiEventStats() {
        createPapiEvents();

        eventStatsDao.createPapiEventStats();

        List<EventStatsEntity> eventStatsEntityList = eventStatsDao.findAll();

        assertNotNull(eventStatsEntityList);
        assertEquals(3, eventStatsEntityList.size());

        assertEquals("Client 1", eventStatsEntityList.get(0).getClientId());
        assertEquals("105.21.229.72", eventStatsEntityList.get(0).getIp());
        assertEquals(Integer.valueOf(10), eventStatsEntityList.get(0).getCount());

        assertEquals("Client 2", eventStatsEntityList.get(1).getClientId());
        assertEquals("105.21.229.73", eventStatsEntityList.get(1).getIp());
        assertEquals(Integer.valueOf(10), eventStatsEntityList.get(1).getCount());

        assertNull(eventStatsEntityList.get(2).getClientId());
        assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:7348", eventStatsEntityList.get(2).getIp());
        assertEquals(Integer.valueOf(1100), eventStatsEntityList.get(2).getCount());



        eventStatsDao.deleteStatsByType(EventType.PAPI);
    }

    private void createPapiEvents() {
        for (int i = 0; i < 20; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEventType(EventType.PAPI.getValue());
            eventEntity.setClientId("Client " + (i % 2 == 0 ? 1 : 2));
            eventEntity.setIp("105.21.229.7" + (i % 2 == 0 ? 2 : 3));
            LocalDate date = LocalDate.now().minusDays(1);
            Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            eventEntity.setDateCreated(Date.from(instant));
            eventDao.createEvent(eventEntity);
        }

        for (int i = 0; i < 10; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEventType(EventType.PAPI.getValue());
            eventEntity.setIp("105.21.229.71");
            eventEntity.setLabel("anonymous");
            LocalDate date = LocalDate.now().minusDays(1);
            Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            eventEntity.setDateCreated(Date.from(instant));
            eventDao.createEvent(eventEntity);
        }

        for (int i = 0; i < 1100; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEventType(EventType.PAPI.getValue());
            eventEntity.setIp("2001:db8:85a3:8d3:1319:8a2e:370:7348");
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
            eventEntity.setEventType(EventType.SIGN_IN.getValue());
            eventEntity.setClientId("Client " + (i % 2 == 0 ? 1 : 2)) ;
            LocalDate date = LocalDate.now().minusDays(1);
            Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
            eventEntity.setDateCreated(Date.from(instant));
            eventDao.createEvent(eventEntity);
        }
    }
    
}
