package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.AddressEntity;
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
public class AddressDaoTest extends DBUnitTest {
    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0001";
    
    @Resource(name = "addressDao")
    AddressDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Test
    public void findTest() {
        AddressEntity address = dao.getAddress("4444-4444-4444-4442", 1L);
        assertNotNull(address);
        assertNotNull(address.getLastModified());
        assertNotNull(address.getDateCreated());
        assertEquals(Long.valueOf(1), address.getId());
        assertNotNull(address.getUser());
        assertEquals("4444-4444-4444-4442", address.getUser().getId());
        assertEquals("US", address.getIso2Country());        

        address = dao.getAddress("4444-4444-4444-4447", 2L);
        assertNotNull(address);
        assertNotNull(address.getLastModified());
        assertNotNull(address.getDateCreated());
        assertEquals(Long.valueOf(2), address.getId());
        assertNotNull(address.getUser());
        assertEquals("4444-4444-4444-4447", address.getUser().getId());
        assertEquals("US", address.getIso2Country());

        address = dao.getAddress("4444-4444-4444-4447", 3L);
        assertNotNull(address);
        assertNotNull(address.getLastModified());
        assertNotNull(address.getDateCreated());
        assertEquals(Long.valueOf(3), address.getId());
        assertNotNull(address.getUser());
        assertEquals("4444-4444-4444-4447", address.getUser().getId());
        assertEquals("CR", address.getIso2Country());       
    }
    
    @Test
    public void findByOrcidTest() {
        List<AddressEntity> addresses = dao.getAddresses("4444-4444-4444-4447", 0L);
        assertNotNull(addresses);
        assertEquals(4, addresses.size());
    }

    @Test
    public void pendingToMigrateTest() {
        List<Object[]> pendingToMigrate = dao.findAddressesToMigrate();
        assertNotNull(pendingToMigrate);
        assertEquals(1, pendingToMigrate.size());

        for (Object[] item : pendingToMigrate) {
            String orcid = (String) item[0];
            String iso2Country = (String) item[1];
            assertEquals("5555-5555-5555-5558", orcid);
            assertEquals("GB", iso2Country);
        }
    }
    
    @Test
    public void removeAllTest() {
        long initialNumber = dao.countAll();
        long elementThatBelogsToUser = dao.getAddresses(USER_ORCID, 0L).size();
        long otherUserElements = dao.getAddresses(OTHER_USER_ORCID, 0L).size();
        assertEquals(5, elementThatBelogsToUser);
        assertTrue(elementThatBelogsToUser < initialNumber);
        assertEquals(3, otherUserElements);
        //Remove all elements that belongs to USER_ORCID
        dao.removeAllAddress(USER_ORCID);
        
        long finalNumberOfElements = dao.countAll();
        long finalNumberOfOtherUserElements = dao.getAddresses(OTHER_USER_ORCID, 0L).size();
        long finalNumberOfElementsThatBelogsToUser = dao.getAddresses(USER_ORCID, 0L).size();
        assertEquals(0, finalNumberOfElementsThatBelogsToUser);
        assertEquals(otherUserElements, finalNumberOfOtherUserElements);
        assertEquals((initialNumber - elementThatBelogsToUser), finalNumberOfElements);
    }
}
