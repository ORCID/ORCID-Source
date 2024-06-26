package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.IdentifierTypeEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext
public class IdentifierTypeDaoTest extends DBUnitTest{

    @Resource
    private IdentifierTypeDao idTypeDao;
    
    @Resource
    private ClientDetailsDao clientDetailsDao;

    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml","/data/ClientDetailsEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        
    }

    @Test
    public void test1AddUpdateFetchID(){
        IdentifierTypeEntity e1 = new IdentifierTypeEntity();
        e1.setName("TEST_A");
        e1.setResolutionPrefix("http://whatever.com/{id}");
        e1.setValidationRegex("blah");        
        e1.setSourceClient(clientDetailsDao.findByClientId("APP-6666666666666666",new Date().getTime()));  
        e1.setIsCaseSensitive(true);
        e1.setPrimaryUse("pu");
        IdentifierTypeEntity e2 = idTypeDao.addIdentifierType(e1);
        assertNotNull(e2.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        Date dateCreated = e2.getDateCreated();
        Date lastModified = e2.getLastModified();
        
        
        e1 = idTypeDao.getEntityByName("TEST_A");
        assertEquals("TEST_A",e1.getName());
        assertEquals("http://whatever.com/{id}",e1.getResolutionPrefix());
        assertEquals("blah",e1.getValidationRegex());
        assertFalse(e1.getIsDeprecated());
        assertNotNull(e1.getId());
        assertEquals("APP-6666666666666666",e1.getSourceClient().getClientId());
        assertTrue((new Date()).after(e1.getDateCreated()));
        assertTrue((new Date()).after(e1.getLastModified()));
        assertEquals("APP-6666666666666666",e1.getSourceClient().getId());
        assertTrue(e1.getIsCaseSensitive());
        assertEquals("pu",e1.getPrimaryUse());
        assertEquals(dateCreated, e1.getDateCreated());
        assertEquals(lastModified, e1.getLastModified());
        
        //update
        //e1 = idTypeDao.getEntityByName("TEST_A");
        e1.setResolutionPrefix("http://whatever2.com/{id}");
        e1.setValidationRegex("blah2");
        e1.setIsDeprecated(true);
        e1 = idTypeDao.updateIdentifierType(e1);
        assertEquals("TEST_A",e1.getName());
        assertEquals("http://whatever2.com/{id}",e1.getResolutionPrefix());
        assertEquals("blah2",e1.getValidationRegex());
        assertTrue(e1.getIsDeprecated());
        assertNotNull(e1.getId());
        assertEquals("APP-6666666666666666",e1.getSourceClient().getClientId());
        assertEquals(dateCreated, e1.getDateCreated());
        assertTrue(lastModified.before(e1.getLastModified()));        
    }
    
    @Test
    public void test4FetchIDList(){
        List<IdentifierTypeEntity> list = idTypeDao.getEntities();
        int startSize = list.size();
        IdentifierTypeEntity e1 = new IdentifierTypeEntity();
        e1.setName("TEST_B");
        ClientDetailsEntity sourceClient = new ClientDetailsEntity();
        sourceClient.setId("APP-6666666666666666");
        e1.setSourceClient(sourceClient);
        idTypeDao.addIdentifierType(e1);
        list = idTypeDao.getEntities();
        assertEquals(startSize+1, list.size());        
    }    
}
