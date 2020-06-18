package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class EmailFrequencyDaoTest extends DBUnitTest {    
    @Resource(name = "emailFrequencyDao")
    EmailFrequencyDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Test
    public void findTest() {
        EmailFrequencyEntity e = dao.find("UUID1");
        assertNotNull(e);
        assertEquals("0000-0000-0000-0003", e.getOrcid());
        assertEquals(Float.valueOf(0.0f), e.getSendAdministrativeChangeNotifications());
        assertEquals(Float.valueOf(1.0f), e.getSendChangeNotifications());
        assertEquals(Float.valueOf(7.0f), e.getSendMemberUpdateRequests());
        assertTrue(e.getSendQuarterlyTips());
    }

    @Test
    public void findByOrcidTest() {
        EmailFrequencyEntity e = dao.findByOrcid("0000-0000-0000-0003");
        assertNotNull(e);
        assertEquals("0000-0000-0000-0003", e.getOrcid());
        assertEquals(Float.valueOf(0.0f), e.getSendAdministrativeChangeNotifications());
        assertEquals(Float.valueOf(1.0f), e.getSendChangeNotifications());
        assertEquals(Float.valueOf(7.0f), e.getSendMemberUpdateRequests());
        assertTrue(e.getSendQuarterlyTips());
    }

    @Test
    public void updateSendChangeNotificationsTest() {
        EmailFrequencyEntity e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertEquals(Float.valueOf(0.0f), e.getSendChangeNotifications());
        assertTrue(dao.updateSendChangeNotifications("0000-0000-0000-0004", SendEmailFrequency.QUARTERLY));
        e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertEquals(Float.valueOf(SendEmailFrequency.QUARTERLY.value()), e.getSendChangeNotifications());
    }

    @Test
    public void updateSendAdministrativeChangeNotificationsTest() {
        EmailFrequencyEntity e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertEquals(Float.valueOf(0.0f), e.getSendAdministrativeChangeNotifications());
        assertTrue(dao.updateSendAdministrativeChangeNotifications("0000-0000-0000-0004", SendEmailFrequency.QUARTERLY));
        e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertEquals(Float.valueOf(SendEmailFrequency.QUARTERLY.value()), e.getSendAdministrativeChangeNotifications());
    }

    @Test
    public void updateSendMemberUpdateRequestsTest() {
        EmailFrequencyEntity e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertEquals(Float.valueOf(0.0f), e.getSendMemberUpdateRequests());
        assertTrue(dao.updateSendMemberUpdateRequests("0000-0000-0000-0004", SendEmailFrequency.QUARTERLY));
        e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertEquals(Float.valueOf(SendEmailFrequency.QUARTERLY.value()), e.getSendMemberUpdateRequests());
    }

    @Test
    public void updateSendQuarterlyTipsTest() {
        EmailFrequencyEntity e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertTrue(e.getSendQuarterlyTips());
        assertTrue(dao.updateSendQuarterlyTips("0000-0000-0000-0004", false));
        e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertFalse(e.getSendQuarterlyTips());
    }
    
    @Test
    public void mergeTest() {
        EmailFrequencyEntity e = dao.find("UUID8");
        e.setSendQuarterlyTips(Boolean.FALSE);
        Date dateCreated = e.getDateCreated();
        Date lastModified = e.getLastModified();
        dao.merge(e);

        EmailFrequencyEntity updated = dao.find("UUID8");
        assertEquals(dateCreated, updated.getDateCreated());
        assertTrue(updated.getLastModified().after(lastModified));
    }
    
    @Test
    public void persistTest() {
        EmailFrequencyEntity e = new EmailFrequencyEntity();
        e.setOrcid("0000-0000-0000-0001"); 
        e.setSendQuarterlyTips(Boolean.FALSE);
        e.setId("UUID10000");
        e.setSendAdministrativeChangeNotifications(0.0F);
        e.setSendChangeNotifications(0.0F);
        e.setSendMemberUpdateRequests(0.0F);
        dao.persist(e);
        assertNotNull(e.getId());
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertEquals(e.getDateCreated(), e.getLastModified());
        
        EmailFrequencyEntity e2 = dao.find(e.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        assertEquals(e2.getDateCreated(), e2.getLastModified());
        assertEquals(e.getDateCreated(), e2.getDateCreated());
    }
}
