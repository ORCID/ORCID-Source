package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.FindMyStuffHistoryEntity;
import org.orcid.persistence.jpa.entities.keys.FindMyStuffHistoryEntityPk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class FindMyStuffDaoTest extends DBUnitTest{
    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0001";

    @Resource
    private FindMyStuffHistoryDao dao;
    
    @Resource
    private ProfileDao profileDao;

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
    public void createTest(){
        FindMyStuffHistoryEntity e = new FindMyStuffHistoryEntity();
        e.setFinderName("fn");
        e.setLastCount(1l);
        e.setOptOut(true);
        e.setOrcid(USER_ORCID);
        dao.persist(e);
        
        FindMyStuffHistoryEntity e1 = dao.find(new FindMyStuffHistoryEntityPk(USER_ORCID,"fn"));
        assertEquals("fn",e1.getFinderName());
        assertEquals(1,e1.getLastCount());
        assertEquals(true,e1.getOptOut());
        assertEquals(USER_ORCID,e1.getOrcid());
        assertNotNull(e1.getLastModified());
        assertNotNull(e1.getDateCreated());
    }
    
    @Test
    public void updateTest(){
        FindMyStuffHistoryEntity e = new FindMyStuffHistoryEntity();
        e.setFinderName("fn2");
        e.setLastCount(1l);
        e.setOptOut(true);
        e.setOrcid(USER_ORCID);
        dao.persist(e);
        
        FindMyStuffHistoryEntity e1 = dao.find(new FindMyStuffHistoryEntityPk(USER_ORCID,"fn2"));
        e1.setLastCount(2l);
        dao.merge(e1);
        
        FindMyStuffHistoryEntity e2 = dao.find(new FindMyStuffHistoryEntityPk(USER_ORCID,"fn2"));
        assertEquals(2l,e2.getLastCount());
    }
    
    @Test 
    public void markOptOut(){
        FindMyStuffHistoryEntity e = new FindMyStuffHistoryEntity();
        e.setFinderName("fn3");
        e.setLastCount(1l);
        e.setOptOut(false);
        e.setOrcid(USER_ORCID);
        dao.persist(e);
        
        FindMyStuffHistoryEntity e1 = dao.find(new FindMyStuffHistoryEntityPk(USER_ORCID,"fn3"));
        assertEquals(false,e1.getOptOut());
        
        dao.markOptOut(USER_ORCID, "fn3", true);

        FindMyStuffHistoryEntity e2 = dao.find(new FindMyStuffHistoryEntityPk(USER_ORCID,"fn3"));
        assertEquals(true,e2.getOptOut());
    }

    @Test 
    public void markActioned(){
        FindMyStuffHistoryEntity e = new FindMyStuffHistoryEntity();
        e.setFinderName("fn4");
        e.setLastCount(1l);
        e.setOptOut(true);
        e.setOrcid(USER_ORCID);
        dao.persist(e);
        dao.markActioned(USER_ORCID, "fn4");
        
        FindMyStuffHistoryEntity e1 = dao.find(new FindMyStuffHistoryEntityPk(USER_ORCID,"fn4"));
        assertEquals(true,e1.getActioned());
    }

    @Test
    public void findTest(){
        FindMyStuffHistoryEntity e = new FindMyStuffHistoryEntity();
        e.setFinderName("fn1");
        e.setLastCount(1l);
        e.setOptOut(true);
        e.setOrcid(OTHER_USER_ORCID);
        dao.persist(e);
        
        FindMyStuffHistoryEntity e2 = new FindMyStuffHistoryEntity();
        e2.setFinderName("fn2");
        e2.setLastCount(1l);
        e2.setOptOut(true);
        e2.setOrcid(OTHER_USER_ORCID);
        dao.persist(e2);
        
        List<FindMyStuffHistoryEntity> list = dao.findAll(OTHER_USER_ORCID);
        assertEquals(2,list.size());
    }
    
}
