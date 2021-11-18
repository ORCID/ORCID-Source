package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ClientDetailsDaoTest extends DBUnitTest {

    private static String CLIENT_ID = "APP-5555555555555557";
    private static String CLIENT_ID_TWO = "APP-5555555555555558";

    @Resource
    ClientSecretDao clientSecretDao;

    @Resource
    ClientDetailsDao clientDetailsDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Test
    public void testUpdateLastModifiedBulk() {
        List<String> clientList = new ArrayList<String>();
        clientList.add(CLIENT_ID);
        clientList.add(CLIENT_ID_TWO);
        int updateClients = clientDetailsDao.updateLastModifiedBulk(clientList);
        assertEquals(updateClients, 2);
    }

}
