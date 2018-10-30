package org.orcid.core.manager.v3;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.v3.rc2.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc2.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc2.common.Organization;
import org.orcid.jaxb.model.v3.rc2.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.SourceClientId;
import org.orcid.jaxb.model.v3.rc2.common.SourceName;
import org.orcid.jaxb.model.v3.rc2.common.SourceOrcid;
import org.orcid.jaxb.model.v3.rc2.common.Title;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PeerReviewType;
import org.orcid.jaxb.model.v3.rc2.record.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.Role;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviews;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

public class PeerReviewManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/RecordNameEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private static final String CLIENT_2_ID = "APP-5555555555555555";//obo
    private static final String CLIENT_3_ID = "APP-5555555555555556";//obo

    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager mockSourceManager;
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;
    
    @Resource(name = "orcidSecurityManagerV3")
    OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "peerReviewManagerV3")
    private PeerReviewManager peerReviewManager;
    
    @Resource
    private PeerReviewDao peerReviewDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(peerReviewManager, "sourceManager", mockSourceManager);
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", mockSourceManager);        
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(peerReviewManager, "sourceManager", sourceManager);        
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", sourceManager);        
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testAddPeerReviewToUnclaimedRecordPreservePeerReviewVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        PeerReview peer = getPeerReview(null);
        
        peer = peerReviewManager.createPeerReview(unclaimedOrcid, peer, true);
        peer = peerReviewManager.getPeerReview(unclaimedOrcid, peer.getPutCode());
        
        assertNotNull(peer);
        assertEquals(Visibility.PUBLIC, peer.getVisibility());        
    }
    
    @Test
    public void testAddPeerReviewToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        PeerReview peer = getPeerReview(null);
        
        peer = peerReviewManager.createPeerReview(claimedOrcid, peer, true);
        peer = peerReviewManager.getPeerReview(claimedOrcid, peer.getPutCode());
        
        assertNotNull(peer);
        assertEquals(Visibility.LIMITED, peer.getVisibility());       
    }
    
    @Test
    public void testAddMultipleModifiesIndexingStatus() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        PeerReview p1 = getPeerReview("extId1");
        p1 = peerReviewManager.createPeerReview(claimedOrcid, p1, true);
        
        PeerReview p2 = getPeerReview("extId2");
        p2 = peerReviewManager.createPeerReview(claimedOrcid, p2, true);
        
        PeerReview p3 = getPeerReview("extId3");
        p3 = peerReviewManager.createPeerReview(claimedOrcid, p3, true);
        
        PeerReviewEntity entity1 = peerReviewDao.find(p1.getPutCode());
        PeerReviewEntity entity2 = peerReviewDao.find(p2.getPutCode());
        PeerReviewEntity entity3 = peerReviewDao.find(p3.getPutCode());
        
        assertNotNull(entity1.getDisplayIndex());
        assertNotNull(entity2.getDisplayIndex());
        assertNotNull(entity3.getDisplayIndex());
        assertEquals(Long.valueOf(0), entity3.getDisplayIndex());
        
        //Rollback all changes
        peerReviewDao.remove(entity1.getId());
        peerReviewDao.remove(entity2.getId());
        peerReviewDao.remove(entity3.getId());
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        PeerReview p1 = getPeerReview("fromUI-1");
        p1 = peerReviewManager.createPeerReview(claimedOrcid, p1, false);
        PeerReviewEntity p = peerReviewDao.find(p1.getPutCode());
        
        assertNotNull(p);
        assertEquals(Long.valueOf(1), p.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        PeerReview p1 = getPeerReview("fromAPI-1");
        p1 = peerReviewManager.createPeerReview(claimedOrcid, p1, true);
        PeerReviewEntity p = peerReviewDao.find(p1.getPutCode());
        
        assertNotNull(91);
        assertEquals(Long.valueOf(0), p.getDisplayIndex());
    }
    
    @Test
    public void testGroupPeerReviews() {
        /**
         * They should be grouped as
         * 
         * Group 1: ext-id-1 + ext-id-4
         * Group 2: ext-id-2 + ext-id-5
         * Group 3: ext-id-3 
         * Group 4: ext-id-6
         * */
        PeerReviewSummary s1 = getPeerReviewSummary("peer-review-group-id-1", "ext-id-1", Visibility.PUBLIC);
        PeerReviewSummary s2 = getPeerReviewSummary("peer-review-group-id-2", "ext-id-2", Visibility.LIMITED);
        PeerReviewSummary s2a = getPeerReviewSummaryWithDifferentSource("peer-review-group-id-2", "ext-id-2", Visibility.LIMITED);
        PeerReviewSummary s3 = getPeerReviewSummary("peer-review-group-id-3", "ext-id-3", Visibility.PRIVATE);
        PeerReviewSummary s4 = getPeerReviewSummary("peer-review-group-id-1", "ext-id-4", Visibility.PRIVATE);
        PeerReviewSummary s5 = getPeerReviewSummary("peer-review-group-id-2", "ext-id-5", Visibility.PUBLIC);
        PeerReviewSummary s6 = getPeerReviewSummary("peer-review-group-id-4", "ext-id-6", Visibility.PRIVATE);
        
        List<PeerReviewSummary> peerReviewList1 = Arrays.asList(s1, s2, s2a, s3, s4, s5, s6); 
        
        PeerReviews peerReviews1 = peerReviewManager.groupPeerReviews(peerReviewList1, false);
        assertNotNull(peerReviews1);
        assertEquals(4, peerReviews1.getPeerReviewGroup().size());
        
        // 2 different duplicate groups...
        assertEquals(2, peerReviews1.getPeerReviewGroup().get(0).getPeerReviewGroup().size());
        
        // .. each with 1 summary (ie no duplicates)
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(0).getPeerReviewGroup().get(1).getPeerReviewSummary().size());
        
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review-group-id-1", peerReviews1.getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        // group peer-review-group-id-2...
        assertEquals("peer-review-group-id-2", peerReviews1.getPeerReviewGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        // Group 2 have group2 - two different duplicate groups...
        assertEquals(2, peerReviews1.getPeerReviewGroup().get(1).getPeerReviewGroup().size());
        
        // ... one of the two duplicate groups has two summaries (ie there are two duplicates)
        int duplicateGroupIndex = -1;
        for (int i = 0; i < peerReviews1.getPeerReviewGroup().get(1).getPeerReviewGroup().size(); i++) {
            if (peerReviews1.getPeerReviewGroup().get(1).getPeerReviewGroup().get(i).getPeerReviewSummary().size() == 2) {
                duplicateGroupIndex = i;
            }
        }
        assertTrue(duplicateGroupIndex >= 0); // duplicate group found
        
        // .. these have been grouped on external id ext-id-2
        assertEquals("ext-id-2", peerReviews1.getPeerReviewGroup().get(1).getPeerReviewGroup().get(duplicateGroupIndex).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("ext-id-2", peerReviews1.getPeerReviewGroup().get(1).getPeerReviewGroup().get(duplicateGroupIndex).getPeerReviewSummary().get(1).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        
        // other peer review from group peer-review-group-id-2 has no duplicates (duplicate list is of size 1)
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(1).getPeerReviewGroup().get(duplicateGroupIndex == 0 ? 1 : 0).getPeerReviewSummary().size());
        
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        
        //Group 3 have group3
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(2).getPeerReviewGroup().size());
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(2).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review-group-id-3", peerReviews1.getPeerReviewGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 4 have group4
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(3).getPeerReviewGroup().size());
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(3).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(3).getIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review-group-id-4", peerReviews1.getPeerReviewGroup().get(3).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        PeerReviewSummary s7 = getPeerReviewSummary("peer-review-group-id-1", "ext-id-7", Visibility.PRIVATE);
        
        /**
         * Now, they should be grouped as
         * 
         * Group 1: ext-id-1 + ext-id-4 + ext-id-7
         * Group 2: ext-id-2 + ext-id-5
         * Group 3: ext-id-3 
         * Group 4: ext-id-6
         * */
        List<PeerReviewSummary> peerReviewList2 = Arrays.asList(s1, s2, s3, s4, s5, s6, s7);
        
        PeerReviews peerReviews2 = peerReviewManager.groupPeerReviews(peerReviewList2, false);
        assertNotNull(peerReviews2);
        assertEquals(4, peerReviews2.getPeerReviewGroup().size());
        //Group 1 have peer-review-group-id-1, so, it will now have 1 more
        assertEquals(3, peerReviews2.getPeerReviewGroup().get(0).getPeerReviewGroup().size());
        assertEquals(1, peerReviews2.getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review-group-id-1", peerReviews2.getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 2 have peer-review-group-id-2
        assertEquals(2, peerReviews2.getPeerReviewGroup().get(1).getPeerReviewGroup().size());
        assertEquals(1, peerReviews2.getPeerReviewGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review-group-id-2", peerReviews2.getPeerReviewGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 3 have peer-review-group-id-3
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(2).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review-group-id-3", peerReviews1.getPeerReviewGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        //Group 4 have peer-review-group-id-4
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(3).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(1, peerReviews1.getPeerReviewGroup().get(3).getIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review-group-id-4", peerReviews1.getPeerReviewGroup().get(3).getIdentifiers().getExternalIdentifier().get(0).getValue());        
    }
    
    @Test
    public void testGroupPeerReviews_groupOnlyPublicPeerReviews1() {
        PeerReviewSummary s1 = getPeerReviewSummary("public-peer-review-group-id-1", "ext-id-1", Visibility.PUBLIC);
        PeerReviewSummary s2 = getPeerReviewSummary("limited-peer-review-group-id-1", "ext-id-2", Visibility.LIMITED);
        PeerReviewSummary s2a = getPeerReviewSummaryWithDifferentSource("peer-review-group-id-1", "ext-id-2", Visibility.LIMITED);
        PeerReviewSummary s3 = getPeerReviewSummary("private-peer-review-group-id-1", "ext-id-3", Visibility.PRIVATE);
        PeerReviewSummary s4 = getPeerReviewSummary("public-peer-review-group-id-2", "ext-id-4", Visibility.PUBLIC);
        PeerReviewSummary s4a = getPeerReviewSummaryWithDifferentSource("public-peer-review-group-id-2", "ext-id-4", Visibility.PUBLIC);
        PeerReviewSummary s5 = getPeerReviewSummary("limited-peer-review-group-id-2", "ext-id-5", Visibility.LIMITED);
        PeerReviewSummary s6 = getPeerReviewSummary("private-peer-review-group-id-2", "ext-id-6", Visibility.PRIVATE);
        PeerReviewSummary s7 = getPeerReviewSummary("public-peer-review-group-id-3", "ext-id-7", Visibility.PUBLIC);
        PeerReviewSummary s8 = getPeerReviewSummary("limited-peer-review-group-id-3", "ext-id-8", Visibility.LIMITED);
        PeerReviewSummary s9 = getPeerReviewSummary("private-peer-review-group-id-3", "ext-id-9", Visibility.PRIVATE);
        
        List<PeerReviewSummary> workList = Arrays.asList(s1, s2, s2a, s3, s4, s4a, s5, s6, s7, s8, s9);
        
        /**
         * They should be grouped as
         * 
         * Group 1: Public 1
         * Group 2: Public 2
         * Group 3: Public 3
         * */
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(workList, true);
        assertNotNull(peerReviews);
        assertEquals(3, peerReviews.getPeerReviewGroup().size());
        assertEquals(1, peerReviews.getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, peerReviews.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals("public-peer-review-group-id-1", peerReviews.getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("public-peer-review-group-id-1", peerReviews.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
        assertEquals(1, peerReviews.getPeerReviewGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals(2, peerReviews.getPeerReviewGroup().get(1).getPeerReviewGroup().get(0).getPeerReviewSummary().size());  // two duplicates for public-peer-review-group-id-2
        assertEquals("public-peer-review-group-id-2", peerReviews.getPeerReviewGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("public-peer-review-group-id-2", peerReviews.getPeerReviewGroup().get(1).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
        assertEquals("public-peer-review-group-id-2", peerReviews.getPeerReviewGroup().get(1).getPeerReviewGroup().get(0).getPeerReviewSummary().get(1).getGroupId());
        assertEquals("ext-id-4", peerReviews.getPeerReviewGroup().get(1).getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("ext-id-4", peerReviews.getPeerReviewGroup().get(1).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("ext-id-4", peerReviews.getPeerReviewGroup().get(1).getPeerReviewGroup().get(0).getPeerReviewSummary().get(1).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(1, peerReviews.getPeerReviewGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, peerReviews.getPeerReviewGroup().get(2).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals("public-peer-review-group-id-3", peerReviews.getPeerReviewGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("public-peer-review-group-id-3", peerReviews.getPeerReviewGroup().get(2).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
    }
    
    @Test
    public void testGroupPeerReviews_groupOnlyPublicPeerReviews2() {
        PeerReviewSummary s1 = getPeerReviewSummary("public-peer-review-group-id-1", "ext-id-1", Visibility.PUBLIC);
        PeerReviewSummary s2 = getPeerReviewSummary("limited-peer-review-group-id-1", "ext-id-2", Visibility.LIMITED);
        PeerReviewSummary s3 = getPeerReviewSummary("private-peer-review-group-id-1", "ext-id-3", Visibility.PRIVATE);
        PeerReviewSummary s4 = getPeerReviewSummary("public-peer-review-group-id-1", "ext-id-4", Visibility.PUBLIC);
        PeerReviewSummary s5 = getPeerReviewSummary("limited-peer-review-group-id-1", "ext-id-5", Visibility.LIMITED);
        PeerReviewSummary s6 = getPeerReviewSummary("private-peer-review-group-id-1", "ext-id-6", Visibility.PRIVATE);
        PeerReviewSummary s7 = getPeerReviewSummary("public-peer-review-group-id-2", "ext-id-7", Visibility.PUBLIC);
        PeerReviewSummary s8 = getPeerReviewSummary("limited-peer-review-group-id-2", "ext-id-8", Visibility.LIMITED);
        PeerReviewSummary s9 = getPeerReviewSummary("private-peer-review-group-id-2", "ext-id-9", Visibility.PRIVATE);        
        
        List<PeerReviewSummary> peerReviewList = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);
        
        /**
         * They should be grouped as
         * 
         * Group 1: ext-id-1 + ext-id-4
         * Group 2: ext-id-7
         * */
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(peerReviewList, true);
        assertNotNull(peerReviews);
        assertEquals(2, peerReviews.getPeerReviewGroup().size());
        assertEquals(1, peerReviews.getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("public-peer-review-group-id-1", peerReviews.getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(2, peerReviews.getPeerReviewGroup().get(0).getPeerReviewGroup().size());                
        assertThat(peerReviews.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue(), anyOf(is("ext-id-1"), is("ext-id-4")));
        assertThat(peerReviews.getPeerReviewGroup().get(0).getPeerReviewGroup().get(1).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue(), anyOf(is("ext-id-1"), is("ext-id-4")));                                                       
        assertEquals(1, peerReviews.getPeerReviewGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("public-peer-review-group-id-2", peerReviews.getPeerReviewGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(1, peerReviews.getPeerReviewGroup().get(1).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals("ext-id-7", peerReviews.getPeerReviewGroup().get(1).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
    }
    
    @Test
    public void testGetAll() {
        String orcid = "0000-0000-0000-0003";
        List<PeerReview> elements = peerReviewManager.findPeerReviews(orcid);
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        for(PeerReview element : elements) {
            if(9 == element.getPutCode()) {
                found1 = true;
            } else if(10 == element.getPutCode()) {
                found2 = true;
            } else if(11 == element.getPutCode()) {
                found3 = true;
            } else if(12 == element.getPutCode()) {
                found4 = true;
            } else if(13 == element.getPutCode()) {
                found5 = true;
            } else {
                fail("Invalid element found: " + element.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5); 
    }
    
    @Test
    public void testGetAllSummaries() {
        String orcid = "0000-0000-0000-0003";
        List<PeerReviewSummary> elements = peerReviewManager.getPeerReviewSummaryList(orcid);
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        for(PeerReviewSummary element : elements) {
            if(9 == element.getPutCode()) {
                found1 = true;
            } else if(10 == element.getPutCode()) {
                found2 = true;
            } else if(11 == element.getPutCode()) {
                found3 = true;
            } else if(12 == element.getPutCode()) {
                found4 = true;
            } else if(13 == element.getPutCode()) {
                found5 = true;
            } else {
                fail("Invalid element found: " + element.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
    }
    
    @Test
    public void nonGroupableIdsGenerateEmptyIdsListTest() {
        PeerReviewSummary s1 = getPeerReviewSummary("Element 1", "ext-id-1", Visibility.PUBLIC);
        PeerReviewSummary s2 = getPeerReviewSummary("Element 2", "ext-id-2", Visibility.LIMITED);
        PeerReviewSummary s3 = getPeerReviewSummary("Element 3", "ext-id-3", Visibility.PRIVATE);
        
        // Remove the grouping id from s1
        s1.setGroupId(null);
        
        List<PeerReviewSummary> peerReviewsList = Arrays.asList(s1, s2, s3);
        
        /**
         * They should be grouped as
         * 
         * Group 1: Element 1
         * Group 2: Element 2
         * Group 3: Element 3
         * */
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(peerReviewsList, false);
        assertNotNull(peerReviews);
        assertEquals(3, peerReviews.getPeerReviewGroup().size());
        boolean foundEmptyGroup = false;
        boolean found2 = false;
        boolean found3 = false;
        for(PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            assertEquals(1, group.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            if(group.getIdentifiers().getExternalIdentifier().get(0).getValue() == null) {
                assertEquals("ext-id-1", group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                foundEmptyGroup = true;
            } else if (group.getIdentifiers().getExternalIdentifier().get(0).getValue().equals("Element 2")) {
                assertEquals("ext-id-2", group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                found2 = true;
            } else if (group.getIdentifiers().getExternalIdentifier().get(0).getValue().equals("Element 3")) {
                assertEquals("ext-id-3", group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                found3 = true;
            } else {
                fail("Invalid ext id found " + group.getIdentifiers().getExternalIdentifier().get(0).getValue());
            }            
        }
        assertTrue(foundEmptyGroup);
        assertTrue(found2);
        assertTrue(found3);
    }
    
    @Test
    public void testAssertionOriginUpdate() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID, CLIENT_2_ID));                
        
        PeerReview p1 = getPeerReview("extId1");
        p1 = peerReviewManager.createPeerReview(claimedOrcid, p1, true);
        
        PeerReview pr = peerReviewManager.getPeerReview(claimedOrcid, p1.getPutCode());        
        assertEquals(pr.getSource().getSourceOrcid().getPath(),CLIENT_1_ID);
        assertEquals(pr.getSource().getSourceOrcid().getUri(),"https://testserver.orcid.org/"+CLIENT_1_ID);
        assertEquals(pr.getSource().getSourceName().getContent(),"U. Test");
        assertEquals(pr.getSource().getAssertionOriginClientId().getPath(),CLIENT_2_ID);
        assertEquals(pr.getSource().getAssertionOriginClientId().getUri(),"https://testserver.orcid.org/client/"+CLIENT_2_ID);
        assertEquals(pr.getSource().getAssertionOriginName().getContent(),"Source Client 1");
        
        //make a duplicate
        PeerReview p2 = getPeerReview("extId1");
        try {
            p2 = peerReviewManager.createPeerReview(claimedOrcid, p2, true);
            fail();
        }catch(OrcidDuplicatedActivityException e) {
            
        }
        
        //make a duplicate as a different assertion origin
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID, CLIENT_3_ID));                
        p2 = peerReviewManager.createPeerReview(claimedOrcid, p2, true);
        
        //wrong sources:
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID, CLIENT_3_ID));
            peerReviewManager.updatePeerReview(claimedOrcid, pr, true);
            fail();
        }catch(WrongSourceException e) {
        }
        
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));  
            peerReviewManager.updatePeerReview(claimedOrcid, pr, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_2_ID));  
            peerReviewManager.updatePeerReview(claimedOrcid, pr, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
        
        //Rollback all changes
        peerReviewDao.remove(p1.getPutCode());
        peerReviewDao.remove(p2.getPutCode());
    }
    
    private PeerReviewSummary getPeerReviewSummary(String titleValue, String extIdValue, Visibility visibility) {
        PeerReviewSummary summary = new PeerReviewSummary();
        summary.setGroupId(titleValue);
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
    
    private PeerReviewSummary getPeerReviewSummaryWithDifferentSource(String titleValue, String extIdValue, Visibility visibility) {
        PeerReviewSummary summary = getPeerReviewSummary(titleValue, extIdValue, visibility);
        Source source = new Source();
        source.setSourceClientId(new SourceClientId("a-client-id"));
        source.setSourceName(new SourceName("some different client"));
        source.setSourceOrcid(new SourceOrcid("erm"));
        summary.setSource(source);
        return summary;
    }
    
    private PeerReview getPeerReview(String extIdValue) {
        PeerReview peerReview = new PeerReview();
        peerReview.setRole(Role.CHAIR);
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
        peerReview.setExternalIdentifiers(extIds);       
        if(extIdValue == null) {
            peerReview.setSubjectContainerName(new Title("Peer review title"));
        } else {
            peerReview.setSubjectContainerName(new Title("Peer review title " + extIdValue));
        }
        peerReview.setSubjectExternalIdentifier(extId);
        
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org.setDisambiguatedOrganization(disambiguatedOrg);
        peerReview.setOrganization(org);
        peerReview.setType(PeerReviewType.EVALUATION);
        peerReview.setVisibility(Visibility.PUBLIC);
        
        return peerReview;
    }
}
