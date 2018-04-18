package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.message.SendEmailFrequency;
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
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Test
    public void findTest() {
        EmailFrequencyEntity e = dao.find("UUID1");
        assertNotNull(e);
        assertEquals("0000-0000-0000-0003", e.getOrcid());
        assertEquals(Float.valueOf(0.0f), e.getSendAdministrativeChangeNotifications());
        assertEquals(Float.valueOf(1.0f), e.getSendChangeNotifications());
        assertEquals(Float.valueOf(7.0f), e.getSendOrcidNews());
        assertTrue(e.getSendQuarterlyTips());
    }

    @Test
    public void findByOrcidTest() {
        EmailFrequencyEntity e = dao.findByOrcid("0000-0000-0000-0003");
        assertNotNull(e);
        assertEquals("0000-0000-0000-0003", e.getOrcid());
        assertEquals(Float.valueOf(0.0f), e.getSendAdministrativeChangeNotifications());
        assertEquals(Float.valueOf(1.0f), e.getSendChangeNotifications());
        assertEquals(Float.valueOf(7.0f), e.getSendOrcidNews());
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
    public void updateSendOrcidNewsTest() {
        EmailFrequencyEntity e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertEquals(Float.valueOf(0.0f), e.getSendOrcidNews());
        assertTrue(dao.updateSendOrcidNews("0000-0000-0000-0004", SendEmailFrequency.QUARTERLY));
        e = dao.findByOrcid("0000-0000-0000-0004");
        assertNotNull(e);
        assertEquals(Float.valueOf(SendEmailFrequency.QUARTERLY.value()), e.getSendOrcidNews());
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
}
