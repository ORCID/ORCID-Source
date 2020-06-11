package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceItemEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ResearchResourceDaoTest extends DBUnitTest{
    
    @Resource(name = "researchResourceDao")
    private ResearchResourceDao dao;

    @Resource(name = "profileDao")
    private ProfileDao pDao;

    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4446";
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/OrgsEntityData.xml", "/data/ResearchResourceEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("ResearchResourceEntityData.xml", "/data/OrgsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }

    @Test
    @Transactional
    public void testRead(){
        List<ResearchResourceEntity> e1 = dao.getByUser(OTHER_USER_ORCID, new Date().getTime());
        assertNotNull(e1);
        assertEquals(3,e1.size());
        //first
        assertEquals(1l,e1.get(0).getDisplayIndex().longValue());
        assertEquals("the title",e1.get(0).getTitle());
        assertEquals("the translated title",e1.get(0).getTranslatedTitle());
        assertEquals("en",e1.get(0).getTranslatedTitleLanguageCode());
        assertEquals("the proposal type",e1.get(0).getProposalType());
        assertEquals("the url",e1.get(0).getUrl());
        assertEquals("PUBLIC",e1.get(0).getVisibility());
        assertEquals(2010,e1.get(0).getStartDate().getYear().intValue());
        assertEquals(7,e1.get(0).getStartDate().getMonth().intValue());
        assertEquals(2,e1.get(0).getStartDate().getDay().intValue());
        assertEquals(2011,e1.get(0).getEndDate().getYear().intValue());
        assertEquals(7,e1.get(0).getEndDate().getMonth().intValue());
        assertEquals(2,e1.get(0).getEndDate().getDay().intValue());
        assertEquals(Date.parse("2010/07/02 15:31"),e1.get(0).getDateCreated().getTime());
        assertEquals(Date.parse("2010/07/02 15:31"),e1.get(0).getLastModified().getTime());
        assertEquals("4444-4444-4444-4446",e1.get(0).getProfile().getUsername());
        assertEquals("4444-4444-4444-4442",e1.get(0).getSourceId());
        assertEquals(2,e1.get(0).getHosts().size());
        
        assertEquals(2,e1.get(0).getResourceItems().size());
        ResearchResourceItemEntity i1 = e1.get(0).getResourceItems().iterator().next();
        assertEquals("the resource name1",i1.getResourceName());
        assertEquals("infrastructures",i1.getResourceType());
        assertEquals("the url1",i1.getUrl());
        assertEquals(2,i1.getHosts().size());
        assertEquals(e1.get(0).getTitle(), i1.getResearchResourceEntity().getTitle());
        
        //other two
        assertEquals(2l,e1.get(1).getDisplayIndex().longValue());
        assertEquals(3l,e1.get(2).getDisplayIndex().longValue());
    }
    
    @Test
    public void testWriteRR() throws IllegalAccessException{
        Calendar cal = Calendar.getInstance();
        ResearchResourceEntity e = new ResearchResourceEntity();
        e.setDisplayIndex(4l);
        e.setTitle("the title4");
        e.setTranslatedTitle("the translated title4");
        e.setTranslatedTitleLanguageCode("DE");
        e.setProposalType("the proposal type4");
        e.setUrl("the url4");
        e.setVisibility("PRIVATE");
        StartDateEntity se = new StartDateEntity();
        se.setDay(1);
        se.setMonth(2);
        se.setYear(2003);
        e.setStartDate(se);
        EndDateEntity ee = new EndDateEntity();
        ee.setDay(1);
        ee.setMonth(2);
        ee.setYear(2003);
        e.setEndDate(ee);
        e.setClientSourceId("4444-4444-4444-4442");
        e.setExternalIdentifiersJson("{&quot;workExternalIdentifier&quot;:[{&quot;workExternalIdentifierType&quot;:&quot;AGR&quot;,&quot;workExternalIdentifierId&quot;:{&quot;content&quot;:&quot;work:external-identifier-id#1&quot;}}]}");
        e.setProfile(pDao.find(USER_ORCID));
        
        dao.persist(e);
        e.setVisibility("PUBLIC");
        dao.flush();
        
        List<ResearchResourceEntity> e1 = dao.getByUser(USER_ORCID, new Date().getTime());

        assertEquals(3,e1.size());

        assertEquals(4l,e1.get(2).getDisplayIndex().longValue());
        assertEquals("the title4",e1.get(2).getTitle());
        assertEquals("the translated title4",e1.get(2).getTranslatedTitle());
        assertEquals("DE",e1.get(2).getTranslatedTitleLanguageCode());
        assertEquals("the proposal type4",e1.get(2).getProposalType());
        assertEquals("the url4",e1.get(2).getUrl());
        assertEquals("PRIVATE",e1.get(2).getVisibility());
        assertEquals(2003,e1.get(2).getStartDate().getYear().intValue());
        assertEquals(2,e1.get(2).getStartDate().getMonth().intValue());
        assertEquals(1,e1.get(2).getStartDate().getDay().intValue());
        assertEquals(2003,e1.get(2).getEndDate().getYear().intValue());
        assertEquals(2,e1.get(2).getEndDate().getMonth().intValue());
        assertEquals(1,e1.get(2).getEndDate().getDay().intValue());
        
        assertNotNull(e1.get(2).getDateCreated());
        assertNotNull(e1.get(2).getLastModified());
        assertEquals(e1.get(2).getDateCreated(), e1.get(2).getLastModified());
        
        Calendar dateCreated = Calendar.getInstance();
        dateCreated.setTime(e1.get(2).getDateCreated());
        
        assertEquals(cal.get(Calendar.YEAR), dateCreated.get(Calendar.YEAR));
        assertEquals(cal.get(Calendar.DAY_OF_YEAR), dateCreated.get(Calendar.DAY_OF_YEAR));
        
        assertEquals(USER_ORCID,e1.get(2).getProfile().getUsername());
        assertEquals("4444-4444-4444-4442",e1.get(2).getClientSourceId());
        assertEquals("{&quot;workExternalIdentifier&quot;:[{&quot;workExternalIdentifierType&quot;:&quot;AGR&quot;,&quot;workExternalIdentifierId&quot;:{&quot;content&quot;:&quot;work:external-identifier-id#1&quot;}}]}",e.getExternalIdentifiersJson());
        
    }
    
    @Test
    public void testWriteRI(){        
        List<ResearchResourceEntity> e1 = dao.getByUser(USER_ORCID, new Date().getTime());
        assertNotNull(e1);
        ResearchResourceItemEntity ei = new ResearchResourceItemEntity();
        ei.setExternalIdentifiersJson("{&quot;workExternalIdentifier&quot;:[{&quot;workExternalIdentifierType&quot;:&quot;AGR&quot;,&quot;workExternalIdentifierId&quot;:{&quot;content&quot;:&quot;work:external-identifier-id#1&quot;}}]}");
        ei.setResourceName("the resource name");
        ei.setResourceType("the resource type");
        ei.setUrl("the resource url");
        ei.setResearchResourceEntity(e1.get(0));        
        e1.get(0).setResourceItems( Lists.newArrayList(ei));
        ResearchResourceEntity eiMerged = dao.merge(e1.get(0));
        assertEquals("the resource name",eiMerged.getResourceItems().iterator().next().getResourceName());
    }


    @Test
    public void testHasPublicResearchResources() {
        assertTrue(dao.hasPublicResearchResources("0000-0000-0000-0003"));
        assertFalse(dao.hasPublicResearchResources("0000-0000-0000-0002"));
    }
    
    @Test
    public void mergeTest() {
        ResearchResourceEntity e = dao.find(6L);
        e.setProposalType("PROPOSAL_TYPE");
        Date dateCreated = e.getDateCreated();
        Date lastModified = e.getLastModified();
        dao.merge(e);

        ResearchResourceEntity updated = dao.find(6L);
        assertEquals(dateCreated, updated.getDateCreated());
        assertTrue(updated.getLastModified().after(lastModified));
    }
    
    @Test
    public void persistTest() {
        ResearchResourceEntity e = new ResearchResourceEntity();
        e.setProfile(new ProfileEntity("0000-0000-0000-0002")); 
        e.setVisibility("PRIVATE");
        e.setTitle("TITLE");
        e.setProposalType("PROPOSAL_TYPE");
        e.setExternalIdentifiersJson("{&quot;workExternalIdentifier&quot;:[{&quot;workExternalIdentifierType&quot;:&quot;AGR&quot;,&quot;workExternalIdentifierId&quot;:{&quot;content&quot;:&quot;work:external-identifier-id#5&quot;}}]}");
        
        dao.persist(e);
        assertNotNull(e.getId());
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertEquals(e.getDateCreated(), e.getLastModified());
        
        ResearchResourceEntity e2 = dao.find(e.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        assertEquals(e.getLastModified(), e2.getLastModified());
        assertEquals(e.getDateCreated(), e2.getDateCreated());
        assertEquals(e2.getDateCreated(), e2.getLastModified());
    }
}
