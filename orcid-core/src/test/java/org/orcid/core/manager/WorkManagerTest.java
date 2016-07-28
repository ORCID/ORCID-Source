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
import static org.mockito.Mockito.when;

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
import org.orcid.jaxb.model.common_rc3.Title;
import org.orcid.jaxb.model.common_rc3.Url;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.record_rc3.ExternalID;
import org.orcid.jaxb.model.record_rc3.ExternalIDs;
import org.orcid.jaxb.model.record_rc3.Relationship;
import org.orcid.jaxb.model.record_rc3.Work;
import org.orcid.jaxb.model.record_rc3.WorkTitle;
import org.orcid.jaxb.model.record_rc3.WorkType;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

public class WorkManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
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
