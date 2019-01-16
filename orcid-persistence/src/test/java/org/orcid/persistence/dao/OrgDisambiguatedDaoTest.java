package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OrgDisambiguatedDaoTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/OrgsEntityData.xml", "/data/ProfileEntityData.xml", "/data/OrgAffiliationEntityData.xml"); 
    
    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testFindDisambuguatedOrgsWithIncorrectPopularity() {
        List<Pair<Long, Integer>> results = orgDisambiguatedDao.findDisambuguatedOrgsWithIncorrectPopularity(10);
        assertNotNull(results);
        assertEquals(1, results.size());
        Pair<Long, Integer> pair = results.get(0);
        assertEquals(1, pair.getLeft().longValue());
        assertEquals(48, pair.getRight().intValue());
    }
    
    @Test
    @Transactional
    public void testFindBySourceIdAndSourceType(){
        /*<org_disambiguated
            id="1"
            name="An Institution"
            city="London"
            country="GB"
            source_id="abc456"
            source_type="WDB"
            popularity="1"
            />*/
        OrgDisambiguatedEntity e = orgDisambiguatedDao.findBySourceIdAndSourceType("abc456", "WDB");
        assertEquals("abc456",e.getSourceId());
        assertEquals("WDB",e.getSourceType());
        assertEquals("London",e.getCity());
        assertEquals("An Institution",e.getName());
        assertEquals("GB",e.getCountry());
    }
    
}
