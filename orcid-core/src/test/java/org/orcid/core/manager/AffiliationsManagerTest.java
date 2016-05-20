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
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc2.Day;
import org.orcid.jaxb.model.common_rc2.FuzzyDate;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Month;
import org.orcid.jaxb.model.common_rc2.Organization;
import org.orcid.jaxb.model.common_rc2.OrganizationAddress;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.common_rc2.Year;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class AffiliationsManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");
    
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
        affiliationsManager.setSourceManager(sourceManager);
        SecurityContextTestUtils.setUpSecurityContextForAnonymous();
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
}
