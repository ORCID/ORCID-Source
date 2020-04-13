package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.SourceType;
import org.orcid.persistence.jpa.entities.SpamEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, locations = {"classpath:orcid-persistence-context.xml"})
public class SpamDaoTest extends DBUnitTest {

    private static String USER_ORCID = "4444-4444-4444-4497";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4499";

    @Resource(name = "spamDao")
    private SpamDao spamDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SpamEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SpamEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }

    @Test
    @Transactional
    public void testfindByOrcid() {
        SpamEntity spamEntity = spamDao.getSpam(OTHER_USER_ORCID);
        assertNotNull(spamEntity);
        assertEquals(OTHER_USER_ORCID, spamEntity.getOrcid());
        assertEquals(SourceType.USER, spamEntity.getSourceType());
        assertEquals(Integer.valueOf(1), spamEntity.getSpamCounter());
    }

    @Test
    public void testWriteSpam() {
        SpamEntity spamEntity = new SpamEntity();
        spamEntity.setOrcid(USER_ORCID);
        spamEntity.setSourceType(SourceType.USER);
        spamEntity.setSpamCounter(1);

        Date d = new Date();
        spamEntity.setDateCreated(d);
        spamEntity.setLastModified(d);

        spamDao.createSpam(spamEntity);

        spamEntity = spamDao.getSpam(USER_ORCID);
        assertNotNull(spamEntity);
        assertEquals(USER_ORCID, spamEntity.getOrcid());
        assertEquals(SourceType.USER, spamEntity.getSourceType());
        assertEquals(Integer.valueOf(1), spamEntity.getSpamCounter());

    }

    @Test
    public void testUpdateSpamCount() {
        SpamEntity spamEntity = spamDao.getSpam(OTHER_USER_ORCID);
        assertNotNull(spamEntity);
        assertEquals(OTHER_USER_ORCID, spamEntity.getOrcid());
        assertEquals(SourceType.USER, spamEntity.getSourceType());
        assertEquals(Integer.valueOf(1), spamEntity.getSpamCounter());

        spamDao.updateSpamCount(spamEntity, 2);

        SpamEntity spamEntityUpdated = spamDao.getSpam(OTHER_USER_ORCID);
        assertEquals(Integer.valueOf(2), spamEntityUpdated.getSpamCounter());

    }

    @Test
    public void testRemoveSpam() throws NoResultException {
        SpamEntity spamEntity = spamDao.getSpam(OTHER_USER_ORCID);
        assertNotNull(spamEntity);
        spamDao.removeSpam(spamEntity.getOrcid());
    }

    @Test
    public void testExists() {
        assertTrue(spamDao.exists("0000-0000-0000-0003"));
        assertTrue(spamDao.exists("0000-0000-0000-0004"));

        assertFalse(spamDao.exists("0000-0000-0000-0005"));
        assertFalse(spamDao.exists("0000-0000-0000-0006"));
    }

}