package org.orcid.core.manager;

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
import org.orcid.core.exception.InvalidDisambiguatedOrgException;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.DisambiguatedOrganization;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.Organization;
import org.orcid.jaxb.model.common_v2.OrganizationAddress;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
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
    
    @Resource 
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
    public void testAddEducationToUnclaimedRecordPreserveEducationVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        Education education = getEducation();
        education = affiliationsManager.createEducationAffiliation(unclaimedOrcid, education, true);
        education = affiliationsManager.getEducationAffiliation(unclaimedOrcid, education.getPutCode());
        
        assertNotNull(education);
        assertEquals(Visibility.PUBLIC, education.getVisibility());
    }
    
    @Test
    public void testAddEmploymentToUnclaimedRecordPreserveEmploymentVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        
        Employment employment = getEmployment();
        employment = affiliationsManager.createEmploymentAffiliation(unclaimedOrcid, employment, true);
        employment = affiliationsManager.getEmploymentAffiliation(unclaimedOrcid, employment.getPutCode());
        
        assertNotNull(employment);
        assertEquals(Visibility.PUBLIC, employment.getVisibility());
    }
    
    @Test
    public void testAddEducationToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        Education education = getEducation();
        education = affiliationsManager.createEducationAffiliation(claimedOrcid, education, true);
        education = affiliationsManager.getEducationAffiliation(claimedOrcid, education.getPutCode());
        
        assertNotNull(education);
        assertEquals(Visibility.LIMITED, education.getVisibility());
    }
    
    @Test
    public void testAddEmploymentToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        

        Employment employment = getEmployment();
        employment = affiliationsManager.createEmploymentAffiliation(claimedOrcid, employment, true);
        employment = affiliationsManager.getEmploymentAffiliation(claimedOrcid, employment.getPutCode());
        
        assertNotNull(employment);
        assertEquals(Visibility.LIMITED, employment.getVisibility());
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
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void testEducationWithInvalidDisambiguatedOrg() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        Education education = getEducation();
        education.getOrganization().setDisambiguatedOrganization(getDisambiguatedOrganization());
        education = affiliationsManager.createEducationAffiliation(claimedOrcid, education, true);
    }
    
    @Test(expected = InvalidDisambiguatedOrgException.class)
    public void testEmploymentWithInvalidDisambiguatedOrg() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));        
        Employment employment = getEmployment();
        employment.getOrganization().setDisambiguatedOrganization(getDisambiguatedOrganization());
        employment = affiliationsManager.createEmploymentAffiliation(claimedOrcid, employment, true);
    }
    
    private Education getEducation() {
        Education education = new Education();
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        education.setOrganization(org);
        education.setStartDate(new FuzzyDate(new Year(2016), new Month(3), new Day(29)));
        education.setVisibility(Visibility.PUBLIC);
        return education;
    }
    
    private Employment getEmployment() {
        Employment employment = new Employment();
        Organization org = new Organization();
        org.setName("org-name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.US);
        org.setAddress(address);
        employment.setOrganization(org);
        employment.setStartDate(new FuzzyDate(new Year(2016), new Month(3), new Day(29)));
        employment.setVisibility(Visibility.PUBLIC);
        return employment;
    }
    
    private DisambiguatedOrganization getDisambiguatedOrganization() {
        DisambiguatedOrganization disambiguatedOrganization = new DisambiguatedOrganization();
        disambiguatedOrganization.setDisambiguatedOrganizationIdentifier("some-identifier");
        disambiguatedOrganization.setDisambiguationSource(OrgDisambiguatedSourceType.FUNDREF.name());
        return disambiguatedOrganization;
    }
}
