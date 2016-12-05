/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_rc4.Title;
import org.orcid.jaxb.model.common_rc4.Url;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.error_rc4.OrcidError;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc4.Works;
import org.orcid.jaxb.model.record_rc4.BulkElement;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.ExternalIDs;
import org.orcid.jaxb.model.record_rc4.Relationship;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkBulk;
import org.orcid.jaxb.model.record_rc4.WorkTitle;
import org.orcid.jaxb.model.record_rc4.WorkType;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

public class WorkManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/WorksEntityData.xml", "/data/RecordNameEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private static final String CLIENT_2_ID = "APP-5555555555555555";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource 
    private WorkManager workManager;
    
    @Resource
    private WorkDao workDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        workManager.setSourceManager(sourceManager);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testAddWorkToUnclaimedRecordPreserveWorkVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        Work work = getWork(null);
        
        work = workManager.createWork(unclaimedOrcid, work, true);        
        work = workManager.getWork(unclaimedOrcid, work.getPutCode(), 0);
        
        assertNotNull(work);
        assertEquals("Work title", work.getWorkTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC, work.getVisibility());
    }
    
    @Test
    public void testAddWorkToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        Work work = getWork(null);
        
        work = workManager.createWork(claimedOrcid, work, true);        
        work = workManager.getWork(claimedOrcid, work.getPutCode(), 0);
        
        assertNotNull(work);
        assertEquals("Work title", work.getWorkTitle().getTitle().getContent());
        assertEquals(Visibility.LIMITED, work.getVisibility());
    }
    
    @Test
    public void testAddMultipleModifiesIndexingStatus() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        Work w1 = getWork("extId1");
        w1 = workManager.createWork(claimedOrcid, w1, true);
        
        Work w2 = getWork("extId2");
        w2 = workManager.createWork(claimedOrcid, w2, true);
        
        Work w3 = getWork("extId3");
        w3 = workManager.createWork(claimedOrcid, w3, true);
        
        WorkEntity entity1 = workDao.find(w1.getPutCode());
        WorkEntity entity2 = workDao.find(w2.getPutCode());
        WorkEntity entity3 = workDao.find(w3.getPutCode());
        
        assertNotNull(entity1.getDisplayIndex());
        assertNotNull(entity2.getDisplayIndex());
        assertNotNull(entity3.getDisplayIndex());
        assertEquals(Long.valueOf(0), entity3.getDisplayIndex());
        
        //Rollback all changes
        workDao.remove(entity1.getId());
        workDao.remove(entity2.getId());
        workDao.remove(entity3.getId());
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        Work w1 = getWork("fromUI-1");
        w1 = workManager.createWork(claimedOrcid, w1, false);
        WorkEntity w = workDao.find(w1.getPutCode());
        
        assertNotNull(w1);
        assertEquals(Long.valueOf(1), w.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        Work w1 = getWork("fromAPI-1");
        w1 = workManager.createWork(claimedOrcid, w1, true);
        WorkEntity w = workDao.find(w1.getPutCode());
        
        assertNotNull(w1);
        assertEquals(Long.valueOf(0), w.getDisplayIndex());
    }
            
    @Test
    public void testCreateWorksWithBulkAllOK() {
        String orcid = "0000-0000-0000-0003";
        Long time = System.currentTimeMillis();
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        
        WorkBulk bulk = new WorkBulk();
        for(int i = 0; i < 5; i++) {
            Work work = new Work();
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Bulk work " + i + " " + time));
            work.setWorkTitle(title);
            
            ExternalIDs extIds = new ExternalIDs();
            ExternalID extId = new ExternalID();
            extId.setRelationship(Relationship.SELF);
            extId.setType("doi");
            extId.setUrl(new Url("http://doi/" + i + "/" + time));
            extId.setValue("doi-" + i + "-" + time);
            extIds.getExternalIdentifier().add(extId);
            work.setWorkExternalIdentifiers(extIds);
            
            work.setWorkType(WorkType.BOOK);
            bulk.getBulk().add(work);
        }
        
        bulk = workManager.createWorks(orcid, bulk);
        
        assertNotNull(bulk);
        assertEquals(5, bulk.getBulk().size());
        
        for(int i = 0; i < 5; i++) {
            assertTrue(Work.class.isAssignableFrom(bulk.getBulk().get(i).getClass()));
            Work w = (Work)bulk.getBulk().get(i);
            assertNotNull(w.getPutCode());
            assertTrue(0L < w.getPutCode());
            assertEquals("Bulk work " + i + " " + time, w.getWorkTitle().getTitle().getContent());
            assertNotNull(w.getExternalIdentifiers().getExternalIdentifier());
            assertEquals("doi-" + i + "-" + time, w.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
            
            Work w1 = workManager.getWork(orcid, w.getPutCode(), 0L);
            assertNotNull(w1);
            assertEquals("Bulk work " + i + " " + time, w1.getWorkTitle().getTitle().getContent());            
        
            //Delete the work
            assertTrue(workManager.checkSourceAndRemoveWork(orcid, w1.getPutCode()));                       
        }
    }
    
    @Test
    public void testCreateWorksWithBulkSomeOKSomeErrors() {
        String orcid = "0000-0000-0000-0003";
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_2_ID)));
        
        //Lets send a bulk of 6 works
        WorkBulk bulk = new WorkBulk();
        
        //Work # 1 - Fine
        Work work1 = getWork(null);        
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://doi/1"));
        extId.setValue("doi-1");
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        work1.getExternalIdentifiers().getExternalIdentifier().add(extId);
        work1.getWorkTitle().getTitle().setContent("Work # 1");
        bulk.getBulk().add(work1);
        
        //Work # 2 - Fine
        Work work2 = getWork(null);
        work2.getWorkTitle().getTitle().setContent("Work # 2");
        bulk.getBulk().add(work2);
        
        //Work # 3 - Duplicated of Work # 1
        Work work3 = getWork(null);
        work3.getExternalIdentifiers().getExternalIdentifier().clear();
        work3.getExternalIdentifiers().getExternalIdentifier().add(extId);
        work3.getWorkTitle().getTitle().setContent("Work # 3");
        bulk.getBulk().add(work3);
        
        //Work # 4 - Fine
        Work work4 = getWork("new-ext-id-" + System.currentTimeMillis());
        work4.getWorkTitle().getTitle().setContent("Work # 4");
        bulk.getBulk().add(work4);
        
        //Work # 5 - Duplicated of existing work
        Work work5 = getWork(null);
        ExternalID dupExtId = new ExternalID();
        dupExtId.setRelationship(Relationship.SELF);
        dupExtId.setType("doi");
        dupExtId.setValue("1");
        work5.getExternalIdentifiers().getExternalIdentifier().clear();
        work5.getExternalIdentifiers().getExternalIdentifier().add(dupExtId);
        work5.getWorkTitle().getTitle().setContent("Work # 5");
        bulk.getBulk().add(work5);        
        
        //Work # 6 - No title specified
        Work work6 = getWork(null);
        work6.getWorkTitle().getTitle().setContent(null);
        bulk.getBulk().add(work6);
        
        bulk = workManager.createWorks(orcid, bulk);
        
        assertNotNull(bulk);
        assertEquals(6, bulk.getBulk().size());
        
        List<Long> worksToDelete = new ArrayList<Long>();
        
        for(int i = 0; i < bulk.getBulk().size(); i ++) {
            BulkElement element = bulk.getBulk().get(i);
            switch(i) {
            case 0:
            case 1:
            case 3:
                assertTrue(Work.class.isAssignableFrom(element.getClass()));
                Work work = (Work) element;
                assertNotNull(work);
                assertNotNull(work.getPutCode());
                if(i == 0) {
                    assertEquals("Work # 1", work.getWorkTitle().getTitle().getContent());
                } else if (i == 1) {
                    assertEquals("Work # 2", work.getWorkTitle().getTitle().getContent());
                } else {
                    assertEquals("Work # 4", work.getWorkTitle().getTitle().getContent());
                }
                worksToDelete.add(work.getPutCode());
                break;
            case 2:
            case 4:
            case 5:
                assertTrue("Error on id: " + i, OrcidError.class.isAssignableFrom(element.getClass()));
                OrcidError error = (OrcidError) element;
                if(i == 2) {
                    assertEquals(Integer.valueOf(9021), error.getErrorCode());
                } else if(i == 4) {
                    assertEquals(Integer.valueOf(9021), error.getErrorCode());
                } else {
                    assertEquals(Integer.valueOf(9022), error.getErrorCode());                    
                }
                break;            
            }
        }                
        
        //Delete new works
        for(Long putCode : worksToDelete) {
            assertTrue(workManager.checkSourceAndRemoveWork(orcid, putCode));  
        }                
    }
    
    @Test
    public void testCreateWorksWithBulkAllErrors() {
        String orcid = "0000-0000-0000-0003";
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        
        //Set up data: 
        //Create one work with a DOI doi-1 so we can create a duplicate
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work #1"));
        work.setWorkTitle(workTitle);
        
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://doi/1"));
        extId.setValue("doi-1");
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);
        
        work.setWorkType(WorkType.BOOK);
        
        Work newWork = workManager.createWork(orcid, work, true);
        Long putCode = newWork.getPutCode();
        
        WorkBulk bulk = new WorkBulk();
        //Work # 1: No ext ids
        Work work1 = getWork("work # 1 " + System.currentTimeMillis());
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        
        //Work # 2: No title
        Work work2 = getWork("work # 2 " + System.currentTimeMillis());
        work2.getWorkTitle().getTitle().setContent(null);
        
        //Work # 3: No work type
        Work work3 = getWork("work # 3 " + System.currentTimeMillis());
        work3.setWorkType(null);
        
        //Work # 4: Ext id already exists
        Work work4 = getWork("work # 4 " + System.currentTimeMillis());
        work4.getExternalIdentifiers().getExternalIdentifier().add(extId);
        
        bulk.getBulk().add(work1);
        bulk.getBulk().add(work2);
        bulk.getBulk().add(work3);
        bulk.getBulk().add(work4);
        
        bulk = workManager.createWorks(orcid, bulk);
        
        assertNotNull(bulk);
        assertEquals(4, bulk.getBulk().size());
        
        for(int i = 0; i < bulk.getBulk().size(); i ++) {
            BulkElement element = bulk.getBulk().get(i);
            assertTrue(OrcidError.class.isAssignableFrom(element.getClass()));
            OrcidError error = (OrcidError) element;
            switch(i) {
            case 0:
                assertEquals(Integer.valueOf(9023), error.getErrorCode());
                break;
            case 1: 
                assertEquals(Integer.valueOf(9022), error.getErrorCode());
                break;
            case 2: 
                assertEquals(Integer.valueOf(9037), error.getErrorCode());
                break;
            case 3:
                assertEquals(Integer.valueOf(9021), error.getErrorCode());
                break;
            }
        }                
        
        //Delete the work
        assertTrue(workManager.checkSourceAndRemoveWork(orcid, putCode));         
    }
    
    @Test
    public void testGroupWorks() {
        /**
         * They should be grouped as
         * 
         * Group 1: Work 1 + Work 4
         * Group 2: Work 2 + Work 5
         * Group 3: Work 3
         * Group 4: Work 6
         * */
        WorkSummary s1 = getWorkSummary("Work 1", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s2 = getWorkSummary("Work 2", "ext-id-2", Visibility.LIMITED);
        WorkSummary s3 = getWorkSummary("Work 3", "ext-id-3", Visibility.PRIVATE);
        WorkSummary s4 = getWorkSummary("Work 4", "ext-id-1", Visibility.PRIVATE);
        WorkSummary s5 = getWorkSummary("Work 5", "ext-id-2", Visibility.PUBLIC);
        WorkSummary s6 = getWorkSummary("Work 6", "ext-id-4", Visibility.PRIVATE);
        
        List<WorkSummary> workList1 = Arrays.asList(s1, s2, s3, s4, s5, s6); 
        
        Works works1 = workManager.groupWorks(workList1, false);
        assertNotNull(works1);
        assertEquals(4, works1.getWorkGroup().size());
        //Group 1 have all with ext-id-1
        assertEquals(2, works1.getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", works1.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 2 have all with ext-id-2
        assertEquals(2, works1.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", works1.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 3 have ext-id-3
        assertEquals(1, works1.getWorkGroup().get(2).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-3", works1.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 4 have ext-id-4
        assertEquals(1, works1.getWorkGroup().get(3).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(3).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-4", works1.getWorkGroup().get(3).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        WorkSummary s7 = getWorkSummary("Work 7", "ext-id-4", Visibility.PRIVATE);
        //Add ext-id-3 to work 7, so, it join group 3 and group 4 in a single group
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));        
        extId.setValue("ext-id-3"); 
        s7.getExternalIdentifiers().getExternalIdentifier().add(extId);
        
        /**
         * Now, they should be grouped as
         * 
         * Group 1: Work 1 + Work 4
         * Group 2: Work 2 + Work 5
         * Group 3: Work 3 + Work 6 + Work 7
         * */
        List<WorkSummary> workList2 = Arrays.asList(s1, s2, s3, s4, s5, s6, s7);
        
        Works works2 = workManager.groupWorks(workList2, false);
        assertNotNull(works2);
        assertEquals(3, works2.getWorkGroup().size());
        //Group 1 have all with ext-id-1
        assertEquals(2, works2.getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(1, works2.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", works2.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 2 have all with ext-id-2
        assertEquals(2, works2.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals(1, works2.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", works2.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 3 have all with ext-id-3 and ext-id-4
        assertEquals(3, works2.getWorkGroup().get(2).getWorkSummary().size());
        assertEquals(2, works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertThat(works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue(), anyOf(is("ext-id-3"), is("ext-id-4")));
        assertThat(works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(1).getValue(), anyOf(is("ext-id-3"), is("ext-id-4")));
    }
    
    @Test
    public void testGroupWorks_groupOnlyPublicWorks1() {
        WorkSummary s1 = getWorkSummary("Public 1", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s2 = getWorkSummary("Limited 1", "ext-id-2", Visibility.LIMITED);
        WorkSummary s3 = getWorkSummary("Private 1", "ext-id-3", Visibility.PRIVATE);
        WorkSummary s4 = getWorkSummary("Public 2", "ext-id-4", Visibility.PUBLIC);
        WorkSummary s5 = getWorkSummary("Limited 2", "ext-id-5", Visibility.LIMITED);
        WorkSummary s6 = getWorkSummary("Private 2", "ext-id-6", Visibility.PRIVATE);
        WorkSummary s7 = getWorkSummary("Public 3", "ext-id-7", Visibility.PUBLIC);
        WorkSummary s8 = getWorkSummary("Limited 3", "ext-id-8", Visibility.LIMITED);
        WorkSummary s9 = getWorkSummary("Private 3", "ext-id-9", Visibility.PRIVATE);
        
        List<WorkSummary> workList = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);
        
        /**
         * They should be grouped as
         * 
         * Group 1: Public 1
         * Group 2: Public 2
         * Group 3: Public 3
         * */
        Works works = workManager.groupWorks(workList, true);
        assertNotNull(works);
        assertEquals(3, works.getWorkGroup().size());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, works.getWorkGroup().get(0).getWorkSummary().size());
        assertEquals("ext-id-1", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 1", works.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(1, works.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, works.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals("ext-id-4", works.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 2", works.getWorkGroup().get(1).getWorkSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(1, works.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, works.getWorkGroup().get(2).getWorkSummary().size());
        assertEquals("ext-id-7", works.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 3", works.getWorkGroup().get(2).getWorkSummary().get(0).getTitle().getTitle().getContent());
    }
    
    @Test
    public void testGroupWorks_groupOnlyPublicWorks2() {
        WorkSummary s1 = getWorkSummary("Public 1", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s2 = getWorkSummary("Limited 1", "ext-id-1", Visibility.LIMITED);
        WorkSummary s3 = getWorkSummary("Private 1", "ext-id-1", Visibility.PRIVATE);
        WorkSummary s4 = getWorkSummary("Public 2", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s5 = getWorkSummary("Limited 2", "ext-id-1", Visibility.LIMITED);
        WorkSummary s6 = getWorkSummary("Private 2", "ext-id-1", Visibility.PRIVATE);
        WorkSummary s7 = getWorkSummary("Public 3", "ext-id-2", Visibility.PUBLIC);
        WorkSummary s8 = getWorkSummary("Limited 3", "ext-id-2", Visibility.LIMITED);
        WorkSummary s9 = getWorkSummary("Private 3", "ext-id-2", Visibility.PRIVATE);        
        
        List<WorkSummary> workList = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);
        
        /**
         * They should be grouped as
         * 
         * Group 1: Public 1 + Public 2
         * Group 2: Public 3
         * */
        Works works = workManager.groupWorks(workList, true);
        assertNotNull(works);
        assertEquals(2, works.getWorkGroup().size());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(2, works.getWorkGroup().get(0).getWorkSummary().size());
        assertThat(works.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent(), anyOf(is("Public 1"), is("Public 2")));
        assertThat(works.getWorkGroup().get(0).getWorkSummary().get(1).getTitle().getTitle().getContent(), anyOf(is("Public 1"), is("Public 2")));
        assertEquals(1, works.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", works.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(1, works.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals("Public 3", works.getWorkGroup().get(1).getWorkSummary().get(0).getTitle().getTitle().getContent());
    }
    
    private WorkSummary getWorkSummary(String titleValue, String extIdValue, Visibility visibility) {
        WorkSummary summary = new WorkSummary();
        summary.setDisplayIndex("0");        
        Title title = new Title(titleValue);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(title);
        summary.setTitle(workTitle);
        summary.setType(WorkType.ARTISTIC_PERFORMANCE);
        summary.setVisibility(visibility);        
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));        
        extId.setValue(extIdValue);               
        extIds.getExternalIdentifier().add(extId);
        summary.setExternalIdentifiers(extIds);
        return summary;
    }
    
    private Work getWork(String extIdValue) {
        Work work = new Work();
        WorkTitle title = new WorkTitle();
        if(extIdValue == null) {
            title.setTitle(new Title("Work title"));
        } else {
            title.setTitle(new Title("Work title " + extIdValue));
        }
        work.setWorkTitle(title);        
        work.setWorkType(WorkType.BOOK);
        
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        if(extIdValue == null) {
            extId.setValue("ext-id-value");
        } else {
            extId.setValue("ext-id-value-" + extIdValue);
        }        
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);
        
        work.setVisibility(Visibility.PUBLIC);
        return work;
    }
}
