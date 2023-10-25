package org.orcid.persistence;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.dao.EventDao;
import org.orcid.persistence.dao.SpamDao;
import org.orcid.persistence.jpa.entities.EventEntity;
import org.orcid.persistence.jpa.entities.SourceType;
import org.orcid.persistence.jpa.entities.SpamEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, locations = {"classpath:test-orcid-persistence-context.xml"})
public class EventDaoTest extends DBUnitTest {

    private static String USER_ORCID = "4444-4444-4444-4497";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4499";

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
    @Transactional
    public void testFindByOrcid() {
        List<EventEntity> eventEntityList = eventDao.getEvents(OTHER_USER_ORCID);
        assertNotNull(eventEntityList);
        assertEquals(OTHER_USER_ORCID, eventEntityList.get(0).getOrcid());
    }

    @Test
    public void testWriteSpam() throws IllegalAccessException {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setEventType("Sign-In");
        Date date = new Date();
        FieldUtils.writeField(eventEntity, "dateCreated", date, true);
        FieldUtils.writeField(eventEntity, "lastModified", date, true);
        eventEntity.setOrcid(USER_ORCID);

        eventDao.createEvent(eventEntity);

        List<EventEntity> eventEntities = eventDao.getEvents(USER_ORCID);
        assertNotNull(eventEntities);
        assertEquals(USER_ORCID, eventEntities.get(0).getOrcid());
    }

    @Test
    public void testRemoveSpam() throws NoResultException {
        List<EventEntity> eventEntities = eventDao.getEvents(USER_ORCID);
        assertNotNull(eventEntities);
        eventDao.removeEvents(eventEntities.get(0).getOrcid());
    }
}
