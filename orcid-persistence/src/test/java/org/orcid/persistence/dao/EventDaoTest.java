package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.EventEntity;
import org.orcid.persistence.jpa.entities.EventType;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, locations = {"classpath:test-orcid-persistence-context.xml"})
public class EventDaoTest extends DBUnitTest {

    private static String CLIENT_ID = "APP-5555555555555555";
    
    @Resource(name = "eventDao")
    private EventDao eventDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/EventEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/EventEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }

    @Test
    public void deleteEventsByDate() {
        List<EventEntity> eventEntityList = eventDao.findAll();

        assertNotNull(eventEntityList);
        assertEquals(3, eventEntityList.size());

        eventDao.deleteEventsByDate(90);

        eventEntityList = eventDao.findAll();

        assertNotNull(eventEntityList);
        assertEquals(0, eventEntityList.size());
    }

    @Test
    public void testWriteEvent() throws IllegalAccessException {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventType("Sign-In");
        eventEntity.setLabel("This is the label");
        FieldUtils.writeField(eventEntity, "dateCreated", new Date(), true);
        eventEntity.setClientId(CLIENT_ID);

        // Id should be null before creating the event
        assertNull(eventEntity.getId());

        eventDao.createEvent(eventEntity);

        // Id should be populated now
        assertNotNull(eventEntity.getId());
        
        EventEntity fromDb = eventDao.find(eventEntity.getId());
        assertNotNull(fromDb);
        assertEquals(eventEntity.getClientId(), fromDb.getClientId());        
        assertEquals(eventEntity.getEventType(), fromDb.getEventType());
        assertEquals(eventEntity.getId(), fromDb.getId());
        assertEquals(eventEntity.getLabel(), fromDb.getLabel());
        assertNotNull(fromDb.getDateCreated());

        eventDao.delete(eventEntity.getId());
    }

    @Test
    public void deletePapiEventsByDate() {
        List<EventEntity> eventEntityList = eventDao.findByEventType(EventType.PAPI);

        assertNotNull(eventEntityList);
        assertEquals(3, eventEntityList.size());

        eventDao.deletePapiEvents(90);

        eventEntityList = eventDao.findByEventType(EventType.PAPI);

        assertNotNull(eventEntityList);
        assertEquals(0, eventEntityList.size());
    }
}
