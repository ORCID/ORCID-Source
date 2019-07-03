package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.test.TargetProxyHelper;

public class AffiliationsManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/RecordNameEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private static final String CLIENT_2_ID = "APP-5555555555555555";//for obo
    private static final String CLIENT_3_ID = "APP-5555555555555556";//for obo
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager mockSourceManager;
        
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource(name = "orcidSecurityManagerV3")
    OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(affiliationsManager, "sourceManager", mockSourceManager);  
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", mockSourceManager);        
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(affiliationsManager, "sourceManager", sourceManager);        
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", sourceManager);        
    }
  
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testAddDistinctionToUnclaimedRecordPreserveDistinctionVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        Distinction element = getDistinction();
        element = affiliationsManager.createDistinctionAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getDistinctionAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddEducationToUnclaimedRecordPreserveEducationVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        Education element = getEducation();
        element = affiliationsManager.createEducationAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getEducationAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddEmploymentToUnclaimedRecordPreserveEmploymentVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        
        Employment element = getEmployment();
        element = affiliationsManager.createEmploymentAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getEmploymentAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddInvitedPositionToUnclaimedRecordPreserveInvitedPositionVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        InvitedPosition element = getInvitedPosition();
        element = affiliationsManager.createInvitedPositionAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getInvitedPositionAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddMembershipToUnclaimedRecordPreserveMembershipVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        Membership element = getMembership();
        element = affiliationsManager.createMembershipAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getMembershipAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddQualificationToUnclaimedRecordPreserveQualificationVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        Qualification element = getQualification();
        element = affiliationsManager.createQualificationAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getQualificationAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddServiceToUnclaimedRecordPreserveServiceVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        Service element = getService();
        element = affiliationsManager.createServiceAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getServiceAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddDistinctionToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID)); 
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        Distinction element = getDistinction();
        element = affiliationsManager.createDistinctionAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getDistinctionAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddEducationToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));        
        Education element = getEducation();
        element = affiliationsManager.createEducationAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getEducationAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddEmploymentToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));        

        Employment element = getEmployment();
        element = affiliationsManager.createEmploymentAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getEmploymentAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddInvitedPositionToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));        

        InvitedPosition element = getInvitedPosition();
        element = affiliationsManager.createInvitedPositionAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getInvitedPositionAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddMembershipToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));        

        Membership element = getMembership();
        element = affiliationsManager.createMembershipAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getMembershipAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddQualificationToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));        

        Qualification element = getQualification();
        element = affiliationsManager.createQualificationAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getQualificationAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddServiceToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));        

        Service element = getService();
        element = affiliationsManager.createServiceAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getServiceAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testGetAllDistinctions() {
        String orcid = "0000-0000-0000-0003";
        List<DistinctionSummary> elements = affiliationsManager.getDistinctionSummaryList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false; 
        for(DistinctionSummary element : elements) {
            if(27 == element.getPutCode()) {
                found1 = true;
            } else if(28 == element.getPutCode()) {
                found2 = true;
            } else if(29 == element.getPutCode()) {
                found3 = true;
            } else if(30 == element.getPutCode()) {
                found4 = true;
            } else if(31 == element.getPutCode()) {
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
    public void testGetAllEducations() {
        String orcid = "0000-0000-0000-0003";
        List<EducationSummary> elements = affiliationsManager.getEducationSummaryList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false; 
        for(EducationSummary element : elements) {
            if(20 == element.getPutCode()) {
                found1 = true;
            } else if(21 == element.getPutCode()) {
                found2 = true;
            } else if(22 == element.getPutCode()) {
                found3 = true;
            } else if(25 == element.getPutCode()) {
                found4 = true;
            } else if(26 == element.getPutCode()) {
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
    public void testGetAllEmployments() {
        String orcid = "0000-0000-0000-0003";
        List<EmploymentSummary> elements = affiliationsManager.getEmploymentSummaryList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false; 
        for(EmploymentSummary element : elements) {
            if(17 == element.getPutCode()) {
                found1 = true;
            } else if(18 == element.getPutCode()) {
                found2 = true;
            } else if(19 == element.getPutCode()) {
                found3 = true;
            } else if(23 == element.getPutCode()) {
                found4 = true;
            } else if(24 == element.getPutCode()) {
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
    public void testGetAllInvitedPositions() {
        String orcid = "0000-0000-0000-0003";
        List<InvitedPositionSummary> elements = affiliationsManager.getInvitedPositionSummaryList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false; 
        for(InvitedPositionSummary element : elements) {
            if(32 == element.getPutCode()) {
                found1 = true;
            } else if(33 == element.getPutCode()) {
                found2 = true;
            } else if(34 == element.getPutCode()) {
                found3 = true;
            } else if(35 == element.getPutCode()) {
                found4 = true;
            } else if(36 == element.getPutCode()) {
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
    public void testGetAllMemberships() {
        String orcid = "0000-0000-0000-0003";
        List<MembershipSummary> elements = affiliationsManager.getMembershipSummaryList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false; 
        for(MembershipSummary element : elements) {
            if(37 == element.getPutCode()) {
                found1 = true;
            } else if(38 == element.getPutCode()) {
                found2 = true;
            } else if(39 == element.getPutCode()) {
                found3 = true;
            } else if(40 == element.getPutCode()) {
                found4 = true;
            } else if(41 == element.getPutCode()) {
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
    public void testGetAllQualifications() {
        String orcid = "0000-0000-0000-0003";
        List<QualificationSummary> elements = affiliationsManager.getQualificationSummaryList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false; 
        for(QualificationSummary element : elements) {
            if(42 == element.getPutCode()) {
                found1 = true;
            } else if(43 == element.getPutCode()) {
                found2 = true;
            } else if(44 == element.getPutCode()) {
                found3 = true;
            } else if(45 == element.getPutCode()) {
                found4 = true;
            } else if(46 == element.getPutCode()) {
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
    public void testGetAllService() {
        String orcid = "0000-0000-0000-0003";
        List<ServiceSummary> elements = affiliationsManager.getServiceSummaryList(orcid);
        assertNotNull(elements);
        assertEquals(5, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false; 
        for(ServiceSummary element : elements) {
            if(47 == element.getPutCode()) {
                found1 = true;
            } else if(48 == element.getPutCode()) {
                found2 = true;
            } else if(49 == element.getPutCode()) {
                found3 = true;
            } else if(50 == element.getPutCode()) {
                found4 = true;
            } else if(51 == element.getPutCode()) {
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
    public void testGroupAffiliationsDistinctions() {
        List<DistinctionSummary> distinctionSummaries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DistinctionSummary distinctionSummary = getDistinctionSummary();
            distinctionSummary.setDepartmentName("department" + i);
            distinctionSummary.setDisplayIndex(String.valueOf(i));
            distinctionSummary.setRoleTitle("role" + i);
            ExternalIDs externalIDs = new ExternalIDs();
            externalIDs.getExternalIdentifier().add(getExternalID("some-external-id-type", i % 2 == 0 ? "0" : "1"));
            distinctionSummary.setExternalIDs(externalIDs);
            distinctionSummaries.add(distinctionSummary);
        }
        List<AffiliationGroup<DistinctionSummary>> groups = affiliationsManager.groupAffiliations(distinctionSummaries, true);
        assertEquals(2, groups.size());
        
        AffiliationGroup<DistinctionSummary> group1 = groups.get(0);
        AffiliationGroup<DistinctionSummary> group2 = groups.get(1);
        
        assertEquals(5, group1.getActivities().size());
        assertEquals(5, group2.getActivities().size());
    }
    
    @Test
    public void testGroupAffiliationsEducations() {
        List<EducationSummary> educationSummaries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            EducationSummary educationSummary = getEducationSummary();
            educationSummary.setDepartmentName("department" + i);
            educationSummary.setDisplayIndex(String.valueOf(i));
            educationSummary.setRoleTitle("role" + i);
            ExternalIDs externalIDs = new ExternalIDs();
            externalIDs.getExternalIdentifier().add(getExternalID("some-external-id-type", i % 2 == 0 ? "0" : "1"));
            educationSummary.setExternalIDs(externalIDs);
            educationSummaries.add(educationSummary);
        }
        List<AffiliationGroup<EducationSummary>> groups = affiliationsManager.groupAffiliations(educationSummaries, true);
        assertEquals(2, groups.size());
        
        AffiliationGroup<EducationSummary> group1 = groups.get(0);
        AffiliationGroup<EducationSummary> group2 = groups.get(1);
        
        assertEquals(5, group1.getActivities().size());
        assertEquals(5, group2.getActivities().size());
    }
    
    @Test
    public void testGroupAffiliationsEmployments() {
        List<EmploymentSummary> employmentSummaries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            EmploymentSummary employmentSummary = getEmploymentSummary();
            employmentSummary.setDepartmentName("department" + i);
            employmentSummary.setDisplayIndex(String.valueOf(i));
            employmentSummary.setRoleTitle("role" + i);
            ExternalIDs externalIDs = new ExternalIDs();
            externalIDs.getExternalIdentifier().add(getExternalID("some-external-id-type", i % 2 == 0 ? "0" : "1"));
            employmentSummary.setExternalIDs(externalIDs);
            employmentSummaries.add(employmentSummary);
        }
        List<AffiliationGroup<EmploymentSummary>> groups = affiliationsManager.groupAffiliations(employmentSummaries, true);
        assertEquals(2, groups.size());
        
        AffiliationGroup<EmploymentSummary> group1 = groups.get(0);
        AffiliationGroup<EmploymentSummary> group2 = groups.get(1);
        
        assertEquals(5, group1.getActivities().size());
        assertEquals(5, group2.getActivities().size());
    }
    
    @Test
    public void testGroupAffiliationsInvitedPositions() {
        List<InvitedPositionSummary> invitedPositionSummaries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            InvitedPositionSummary invitedPositionSummary = getInvitedPositionSummary();
            invitedPositionSummary.setDepartmentName("department" + i);
            invitedPositionSummary.setDisplayIndex(String.valueOf(i));
            invitedPositionSummary.setRoleTitle("role" + i);
            ExternalIDs externalIDs = new ExternalIDs();
            externalIDs.getExternalIdentifier().add(getExternalID("some-external-id-type", i % 2 == 0 ? "0" : "1"));
            invitedPositionSummary.setExternalIDs(externalIDs);
            invitedPositionSummaries.add(invitedPositionSummary);
        }
        List<AffiliationGroup<InvitedPositionSummary>> groups = affiliationsManager.groupAffiliations(invitedPositionSummaries, true);
        assertEquals(2, groups.size());
        
        AffiliationGroup<InvitedPositionSummary> group1 = groups.get(0);
        AffiliationGroup<InvitedPositionSummary> group2 = groups.get(1);
        
        assertEquals(5, group1.getActivities().size());
        assertEquals(5, group2.getActivities().size());
    }
    
    @Test
    public void testGroupAffiliationsMemberships() {
        List<MembershipSummary> membershipSummaries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MembershipSummary membershipSummary = getMembershipSummary();
            membershipSummary.setDepartmentName("department" + i);
            membershipSummary.setDisplayIndex(String.valueOf(i));
            membershipSummary.setRoleTitle("role" + i);
            ExternalIDs externalIDs = new ExternalIDs();
            externalIDs.getExternalIdentifier().add(getExternalID("some-external-id-type", i % 2 == 0 ? "0" : "1"));
            membershipSummary.setExternalIDs(externalIDs);
            membershipSummaries.add(membershipSummary);
        }
        List<AffiliationGroup<MembershipSummary>> groups = affiliationsManager.groupAffiliations(membershipSummaries, true);
        assertEquals(2, groups.size());
        
        AffiliationGroup<MembershipSummary> group1 = groups.get(0);
        AffiliationGroup<MembershipSummary> group2 = groups.get(1);
        
        assertEquals(5, group1.getActivities().size());
        assertEquals(5, group2.getActivities().size());
        
    }
    
    @Test
    public void testGroupAffiliationsQualifications() {
        List<QualificationSummary> qualificationSummaries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            QualificationSummary qualificationSummary = getQualificationSummary();
            qualificationSummary.setDepartmentName("department" + i);
            qualificationSummary.setDisplayIndex(String.valueOf(i));
            qualificationSummary.setRoleTitle("role" + i);
            ExternalIDs externalIDs = new ExternalIDs();
            externalIDs.getExternalIdentifier().add(getExternalID("some-external-id-type", i % 2 == 0 ? "0" : "1"));
            qualificationSummary.setExternalIDs(externalIDs);
            qualificationSummaries.add(qualificationSummary);
        }
        List<AffiliationGroup<QualificationSummary>> groups = affiliationsManager.groupAffiliations(qualificationSummaries, true);
        assertEquals(2, groups.size());
        
        AffiliationGroup<QualificationSummary> group1 = groups.get(0);
        AffiliationGroup<QualificationSummary> group2 = groups.get(1);
        
        assertEquals(5, group1.getActivities().size());
        assertEquals(5, group2.getActivities().size());
    }
    
    @Test
    public void testGroupAffiliationsServices() {
        List<ServiceSummary> serviceSummaries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ServiceSummary serviceSummary = getServiceSummary();
            serviceSummary.setDepartmentName("departmentName" + i);
            serviceSummary.setDisplayIndex(String.valueOf(i));
            serviceSummary.setRoleTitle("roleTitle" + i);
            ExternalIDs externalIDs = new ExternalIDs();
            externalIDs.getExternalIdentifier().add(getExternalID("some-external-id-type", i % 2 == 0 ? "0" : "1"));
            serviceSummary.setExternalIDs(externalIDs);
            serviceSummaries.add(serviceSummary);
        }
        List<AffiliationGroup<ServiceSummary>> groups = affiliationsManager.groupAffiliations(serviceSummaries, true);
        assertEquals(2, groups.size());
        
        AffiliationGroup<ServiceSummary> group1 = groups.get(0);
        AffiliationGroup<ServiceSummary> group2 = groups.get(1);
        
        assertEquals(5, group1.getActivities().size());
        assertEquals(5, group2.getActivities().size());
    }
    
    @Test
    public void testGetGroupedAffiliations() {
        String orcid = "0000-0000-0000-0003";
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> map = affiliationsManager.getGroupedAffiliations(orcid, false);
        assertNotNull(map);
        
        // Check distinctions
        assertTrue(map.containsKey(AffiliationType.DISTINCTION));
        List<AffiliationGroup<AffiliationSummary>> groups = map.get(AffiliationType.DISTINCTION);
        assertNotNull(groups);
        assertEquals(map.toString(), 4, groups.size());
        
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(30L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(27), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(28L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(29L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(31L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
            
        // Check educations
        assertTrue(map.containsKey(AffiliationType.EDUCATION));
        groups = map.get(AffiliationType.EDUCATION);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(25L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(20), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(21L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(22L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(26L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
        
        // Check employments
        assertTrue(map.containsKey(AffiliationType.EMPLOYMENT));
        groups = map.get(AffiliationType.EMPLOYMENT);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(23L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(17), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(18L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(19L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(24L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
        
        // Check invited positions
        assertTrue(map.containsKey(AffiliationType.INVITED_POSITION));
        groups = map.get(AffiliationType.INVITED_POSITION);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(35L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(32), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(33L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(34L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(36L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
        
        // Check memberships
        assertTrue(map.containsKey(AffiliationType.MEMBERSHIP));
        groups = map.get(AffiliationType.MEMBERSHIP);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(40L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(37), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(38L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(39L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(41L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;        
        
        // Check qualifications
        assertTrue(map.containsKey(AffiliationType.QUALIFICATION));
        groups = map.get(AffiliationType.QUALIFICATION);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(45L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(42), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(43L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(44L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(46L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
        
        // Check services
        assertTrue(map.containsKey(AffiliationType.SERVICE));
        groups = map.get(AffiliationType.SERVICE);
        assertNotNull(groups);
        assertEquals(4, groups.size());
        
        for(AffiliationGroup<AffiliationSummary> g : groups) {
            AffiliationSummary element0 = g.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if(putCode.equals(50L)) {
                assertEquals(2, g.getActivities().size());
                assertEquals(Long.valueOf(47), g.getActivities().get(1).getPutCode());
                found1 = true;
            } else if(putCode.equals(48L)) {
                assertEquals(1, g.getActivities().size());
                found2 = true;
            } else if(putCode.equals(49L)) {
                assertEquals(1, g.getActivities().size());
                found3 = true;
            } else if(putCode.equals(51L)) {
                assertEquals(1, g.getActivities().size());
                found4 = true;
            } else {
                fail("Invalid put code found:  " + putCode);
            }
        }
        
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        
        found1 = found2 = found3 = found4 = false;
    }
    
    /** Test create, update with valid source
     * Test update with invlaid sources
     * 
     */
    @Test
    public void testAssertionOriginUpdate() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_2_ID));                
        
        Service element = getService();
        element = affiliationsManager.createServiceAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getServiceAffiliation(claimedOrcid, element.getPutCode());
        element.setDepartmentName("xxx");
        element = affiliationsManager.updateServiceAffiliation(claimedOrcid, element, true);
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
        Source s = element.getSource();
        assertEquals(s.getSourceClientId().getPath(),CLIENT_1_ID);
        assertEquals(s.getSourceClientId().getUri(),"https://testserver.orcid.org/client/"+CLIENT_1_ID);
        assertEquals(s.getAssertionOriginClientId().getPath(),CLIENT_2_ID);
        assertEquals(s.getAssertionOriginClientId().getUri(),"https://testserver.orcid.org/client/"+CLIENT_2_ID);

        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));
            element = affiliationsManager.updateServiceAffiliation(claimedOrcid, element, true);
            fail();
        }catch(WrongSourceException e) {
        }
        
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
            element = affiliationsManager.updateServiceAffiliation(claimedOrcid, element, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_2_ID));                
            element = affiliationsManager.updateServiceAffiliation(claimedOrcid, element, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
    }
    
    private ExternalID getExternalID(String type, String value) {
        ExternalID externalID = new ExternalID();
        externalID.setType(type);
        externalID.setValue(value);
        return externalID;
    }
    
    private Distinction getDistinction() {
        Distinction element = new Distinction();
        fillAffiliation(element);
        return element;
    }
    
    private DistinctionSummary getDistinctionSummary() {
        DistinctionSummary element = new DistinctionSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Education getEducation() {
        Education element = new Education();
        fillAffiliation(element);
        return element;
    }
    
    private EducationSummary getEducationSummary() {
        EducationSummary element = new EducationSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Employment getEmployment() {
        Employment element = new Employment();
        fillAffiliation(element);
        return element;
    }
    
    private EmploymentSummary getEmploymentSummary() {
        EmploymentSummary element = new EmploymentSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private InvitedPosition getInvitedPosition() {
        InvitedPosition element = new InvitedPosition();
        fillAffiliation(element);
        return element;
    }
    
    private InvitedPositionSummary getInvitedPositionSummary() {
        InvitedPositionSummary element = new InvitedPositionSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Membership getMembership() {
        Membership element = new Membership();
        fillAffiliation(element);
        return element;
    }
    
    private MembershipSummary getMembershipSummary() {
        MembershipSummary element = new MembershipSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Qualification getQualification() {
        Qualification element = new Qualification();
        fillAffiliation(element);
        return element;
    }
    
    private QualificationSummary getQualificationSummary() {
        QualificationSummary element = new QualificationSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private Service getService() {
        Service element = new Service();
        fillAffiliation(element);
        return element;
    }
    
    private ServiceSummary getServiceSummary() {
        ServiceSummary element = new ServiceSummary();
        fillAffiliationSummary(element);
        return element;
    }
    
    private void fillAffiliation(Affiliation aff) {
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org.setDisambiguatedOrganization(disambiguatedOrg);
        aff.setOrganization(org);
        aff.setStartDate(new FuzzyDate(new Year(2016), new Month(3), new Day(29)));
        aff.setVisibility(Visibility.PUBLIC);
    }
    
    private void fillAffiliationSummary(AffiliationSummary aff) {
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("def456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        org.setDisambiguatedOrganization(disambiguatedOrg);
        aff.setOrganization(org);
        aff.setStartDate(new FuzzyDate(new Year(2016), new Month(3), new Day(29)));
        aff.setVisibility(Visibility.PUBLIC);
    } 
}
