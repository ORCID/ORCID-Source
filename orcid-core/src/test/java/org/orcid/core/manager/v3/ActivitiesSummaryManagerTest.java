package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ActivitiesSummaryManagerTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String ORCID = "0000-0000-0000-0003";
    
    @Resource(name = "activitiesSummaryManagerV3")
    private ActivitiesSummaryManager activitiesSummaryManager;
    
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
    public void testGetActivitiesSummary() {
        ActivitiesSummary summary = activitiesSummaryManager.getActivitiesSummary(ORCID);
        
        assertNotNull(summary);
        assertNotNull(summary.getDistinctions());
        assertNotNull(summary.getDistinctions().retrieveGroups());
        assertEquals("Distinctions failed", 4, summary.getDistinctions().retrieveGroups().size());
        
        assertNotNull(summary);
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().retrieveGroups());
        assertEquals("Educations failed", 4, summary.getEducations().retrieveGroups().size());        
        
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().retrieveGroups());
        assertEquals("Employments failed", 4, summary.getEmployments().retrieveGroups().size());
        
        assertNotNull(summary.getInvitedPositions());
        assertNotNull(summary.getInvitedPositions().retrieveGroups());
        assertEquals("Invited positions failed", 4, summary.getInvitedPositions().retrieveGroups().size());
        
        assertNotNull(summary.getMemberships());
        assertNotNull(summary.getMemberships().retrieveGroups());
        assertEquals("Membership failed", 4, summary.getMemberships().retrieveGroups().size());
        
        assertNotNull(summary.getQualifications());
        assertNotNull(summary.getQualifications().retrieveGroups());
        assertEquals("Qualifications failed", 4, summary.getQualifications().retrieveGroups().size());
        
        assertNotNull(summary.getServices());
        assertNotNull(summary.getServices().retrieveGroups());
        assertEquals("Services failed", 4, summary.getServices().retrieveGroups().size());
        
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertEquals(5, summary.getFundings().getFundingGroup().size());
        
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(5, summary.getPeerReviews().getPeerReviewGroup().size());
        
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getWorkGroup());
        assertEquals(5, summary.getWorks().getWorkGroup().size());
    }
    
    @Test
    public void testGetPublicActivitiesSummary() {
        ActivitiesSummary summary = activitiesSummaryManager.getPublicActivitiesSummary(ORCID);
        
        assertNotNull(summary);
        assertNotNull(summary.getDistinctions());
        assertNotNull(summary.getDistinctions().retrieveGroups());
        assertEquals(1, summary.getDistinctions().retrieveGroups().size());
        assertEquals(Long.valueOf(27), summary.getDistinctions().retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().retrieveGroups());
        assertEquals(1, summary.getEducations().retrieveGroups().size());
        assertEquals(Long.valueOf(20), summary.getEducations().retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().retrieveGroups());
        assertEquals(1, summary.getEmployments().retrieveGroups().size());
        assertEquals(Long.valueOf(17), summary.getEmployments().retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        
        assertNotNull(summary.getInvitedPositions());
        assertNotNull(summary.getInvitedPositions().retrieveGroups());
        assertEquals(1, summary.getInvitedPositions().retrieveGroups().size());
        assertEquals(Long.valueOf(32), summary.getInvitedPositions().retrieveGroups().iterator().next().getActivities().get(0).getPutCode());        
        
        assertNotNull(summary.getMemberships());
        assertNotNull(summary.getMemberships().retrieveGroups());
        assertEquals(1, summary.getMemberships().retrieveGroups().size());
        assertEquals(Long.valueOf(37), summary.getMemberships().retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        
        assertNotNull(summary.getQualifications());
        assertNotNull(summary.getQualifications().retrieveGroups());
        assertEquals(1, summary.getQualifications().retrieveGroups().size());
        assertEquals(Long.valueOf(42), summary.getQualifications().retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        
        assertNotNull(summary.getServices());
        assertNotNull(summary.getServices().retrieveGroups());
        assertEquals(1, summary.getServices().retrieveGroups().size());
        assertEquals(Long.valueOf(47), summary.getServices().retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
                        
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertEquals(1, summary.getFundings().getFundingGroup().size());
        assertEquals(Long.valueOf(10), summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(1, summary.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(Long.valueOf(9), summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getWorkGroup());
        assertEquals(1, summary.getWorks().getWorkGroup().size());
        assertEquals(Long.valueOf(11), summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
    }
}
