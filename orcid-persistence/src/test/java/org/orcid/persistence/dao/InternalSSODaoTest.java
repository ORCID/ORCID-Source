package org.orcid.persistence.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.InternalSSOEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author Angel Montenegro
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class InternalSSODaoTest extends DBUnitTest {
    @Resource
    private InternalSSODao internalSSODao;

    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml");

    private final String ORCID = "4444-4444-4444-4441";

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);        
    }

    @Before
    public void before() {
        internalSSODao.delete(ORCID);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    public void internalSSODaoTest() {
        String token = "Token_" + System.currentTimeMillis();
        
        Date now = new Date();
        
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.MINUTE, -5);
        Date _5MinutesAgo = c.getTime();
        
        c.setTime(now);
        c.add(Calendar.MINUTE, 15);
        Date _15MinutesAhead = c.getTime();        
        
        // Create token
        InternalSSOEntity entity = internalSSODao.insert(ORCID, token);
        assertNotNull(entity);        
        // Check the token is not expired yet
        assertTrue(internalSSODao.verify(ORCID, token, _5MinutesAgo));
        // Update it
        assertTrue(internalSSODao.update(ORCID, token));        
        // Assert that it is expired
        assertFalse(internalSSODao.verify(ORCID, token, _15MinutesAhead));

        // Create an invalid token
        String updatedToken = token + "!";
        // Cannot update invalid token
        assertFalse(internalSSODao.update(ORCID, updatedToken));
        // Cannot verify invalid token
        assertFalse(internalSSODao.verify(ORCID, updatedToken, _5MinutesAgo));
    }
    
    @Test
    public void RecordLastModifiedTest() {
        Date now = new Date();
        
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.add(Calendar.MINUTE, -5);
        Date _5MinutesAgo = c.getTime();
        
        c.setTime(now);
        c.add(Calendar.MINUTE, 15);
        Date _15MinutesAhead = c.getTime();
        
        String token = "Token_" + System.currentTimeMillis();
        // Create token
        internalSSODao.insert(ORCID, token);
        assertNotNull(internalSSODao.getRecordLastModified(ORCID, token, _5MinutesAgo));
        assertNull(internalSSODao.getRecordLastModified(ORCID, token, _15MinutesAhead));
    }
}
