package org.orcid.persistence.dao;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.EventEntity;
import org.orcid.persistence.jpa.entities.EventStatsEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
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
        assertEquals(1, eventStatsEntityList.size());
    }

    private void createEvents() {
        for (int i = 0; i < 20; i++) {
            EventEntity eventEntity = new EventEntity();
            eventEntity.setEventType("Sign-In");
            eventEntity.setClientId("Client " + 1);
            eventEntity.setDateCreated(new Date());
            eventDao.createEvent(eventEntity);
        }
    }
    
}
