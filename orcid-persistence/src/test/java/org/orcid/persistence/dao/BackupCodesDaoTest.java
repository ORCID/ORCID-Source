package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.BackupCodeEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class BackupCodesDaoTest extends DBUnitTest {

    @Resource(name = "backupCodeDao")
    BackupCodeDao dao;

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
    public void mergeTest() {
        BackupCodeEntity e = dao.find(0L);
        e.setUsedDate(new Date());
        Date dateCreated = e.getDateCreated();
        Date lastModified = e.getLastModified();
        dao.merge(e);

        BackupCodeEntity updated = dao.find(0L);
        assertEquals(dateCreated, updated.getDateCreated());
        assertTrue(updated.getLastModified().after(lastModified));
    }
    
    @Test
    public void persistTest() {
        BackupCodeEntity e = new BackupCodeEntity();
        e.setOrcid("0000-0000-0000-0002"); 
        e.setHashedCode("HASH");
        dao.persist(e);
        assertNotNull(e.getId());
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertEquals(e.getDateCreated(), e.getLastModified());
        
        BackupCodeEntity e2 = dao.find(e.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        assertEquals(e2.getDateCreated(), e2.getLastModified());
        assertEquals(e.getDateCreated(), e2.getDateCreated());
    }
}
