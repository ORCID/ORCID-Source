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

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.common.Year;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

public class AffiliationsManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/OrgsEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/RecordNameEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource(name = "affiliationsManagerV3")
    private AffiliationsManager affiliationsManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(affiliationsManager, "sourceManager", sourceManager);        
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testAddDistinctionToUnclaimedRecordPreserveDistinctionVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        Distinction element = getDistinction();
        element = affiliationsManager.createDistinctionAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getDistinctionAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddEducationToUnclaimedRecordPreserveEducationVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        Education element = getEducation();
        element = affiliationsManager.createEducationAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getEducationAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddEmploymentToUnclaimedRecordPreserveEmploymentVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        
        Employment element = getEmployment();
        element = affiliationsManager.createEmploymentAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getEmploymentAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddInvitedPositionToUnclaimedRecordPreserveInvitedPositionVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        InvitedPosition element = getInvitedPosition();
        element = affiliationsManager.createInvitedPositionAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getInvitedPositionAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddMembershipToUnclaimedRecordPreserveMembershipVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        Membership element = getMembership();
        element = affiliationsManager.createMembershipAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getMembershipAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddQualificationToUnclaimedRecordPreserveQualificationVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        Qualification element = getQualification();
        element = affiliationsManager.createQualificationAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getQualificationAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddServiceToUnclaimedRecordPreserveServiceVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        Service element = getService();
        element = affiliationsManager.createServiceAffiliation(unclaimedOrcid, element, true);
        element = affiliationsManager.getServiceAffiliation(unclaimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }
    
    @Test
    public void testAddDistinctionToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        Distinction element = getDistinction();
        element = affiliationsManager.createDistinctionAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getDistinctionAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddEducationToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        Education element = getEducation();
        element = affiliationsManager.createEducationAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getEducationAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddEmploymentToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        

        Employment element = getEmployment();
        element = affiliationsManager.createEmploymentAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getEmploymentAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddInvitedPositionToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        

        InvitedPosition element = getInvitedPosition();
        element = affiliationsManager.createInvitedPositionAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getInvitedPositionAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddMembershipToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        

        Membership element = getMembership();
        element = affiliationsManager.createMembershipAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getMembershipAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddQualificationToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        

        Qualification element = getQualification();
        element = affiliationsManager.createQualificationAffiliation(claimedOrcid, element, true);
        element = affiliationsManager.getQualificationAffiliation(claimedOrcid, element.getPutCode());
        
        assertNotNull(element);
        assertEquals(Visibility.LIMITED, element.getVisibility());
    }
    
    @Test
    public void testAddServiceToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        

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
    
    private Distinction getDistinction() {
        Distinction element = new Distinction();
        fillAffiliation(element);
        return element;
    }
    
    private Education getEducation() {
        Education element = new Education();
        fillAffiliation(element);
        return element;
    }
    
    private Employment getEmployment() {
        Employment element = new Employment();
        fillAffiliation(element);
        return element;
    }
    
    private InvitedPosition getInvitedPosition() {
        InvitedPosition element = new InvitedPosition();
        fillAffiliation(element);
        return element;
    }
    
    private Membership getMembership() {
        Membership element = new Membership();
        fillAffiliation(element);
        return element;
    }
    
    private Qualification getQualification() {
        Qualification element = new Qualification();
        fillAffiliation(element);
        return element;
    }
    
    private Service getService() {
        Service element = new Service();
        fillAffiliation(element);
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
}
