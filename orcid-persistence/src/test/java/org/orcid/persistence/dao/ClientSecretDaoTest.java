package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClientSecretDaoTest extends DBUnitTest {
    private static String CLIENT_ID = "APP-5555555555555557";
    private static String CLIENT_ID_TWO = "APP-5555555555555558";
    private static String CLIENT_SECRET = "DaVVhgVl3ab+6HYDyGCBbg==";
    private static String CLIENT_SECRET_TWO ="DhkFj5EI0qp6GsUKi55Vja+h+bsaKpBx";
    private static String CONDITION = String.format("(client_details_id = '%1$s' and client_secret = '%2$s') or (client_details_id = '%3$s' and client_secret = '%4$s')", CLIENT_ID, CLIENT_SECRET_TWO, CLIENT_ID_TWO, CLIENT_SECRET);

    @Resource
    ClientSecretDao clientSecretDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Test
    public void testGetNonPrimaryKeys() {
        List<ClientSecretEntity> nonPrimaryKeys = clientSecretDao.getNonPrimaryKeys(100);
        assertEquals(nonPrimaryKeys.size(), 3);
        for (ClientSecretEntity secret : nonPrimaryKeys) {           
            assertNotNull(secret.getDateCreated());
            assertNotNull(secret.getLastModified());
            assertEquals(secret.getDateCreated(), secret.getLastModified());
        }

        // Delete one for client one and one for client two
        boolean removeNonPrimaryKeys = clientSecretDao.removeWithCustomCondition(CONDITION);
        assertTrue(removeNonPrimaryKeys);

        // Now there should be only 1
        nonPrimaryKeys = clientSecretDao.getNonPrimaryKeys(100);
        assertEquals(1, nonPrimaryKeys.size());
        assertNotNull(nonPrimaryKeys.get(0).getDateCreated());
        assertNotNull(nonPrimaryKeys.get(0).getLastModified());
        assertEquals(nonPrimaryKeys.get(0).getDateCreated(), nonPrimaryKeys.get(0).getLastModified());
        assertEquals(nonPrimaryKeys.get(0).getClientId(), CLIENT_ID);
        assertEquals(nonPrimaryKeys.get(0).getClientSecret(), CLIENT_SECRET);
    }
}
