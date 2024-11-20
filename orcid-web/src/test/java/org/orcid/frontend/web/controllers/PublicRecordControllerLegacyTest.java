package org.orcid.frontend.web.controllers;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.locale.LocaleManager;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.pojo.PublicRecord;
import org.orcid.pojo.summary.RecordSummaryPojo;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-frontend-web-servlet.xml" })
@Deprecated(forRemoval = true)
public class PublicRecordControllerLegacyTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/BiographyEntityData.xml", "/data/OrgsEntityData.xml",
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/WorksEntityData.xml");

    private String unclaimedUserOrcid = "0000-0000-0000-0001";
    private String userOrcid = "0000-0000-0000-0003";
    private String deprecatedUserOrcid = "0000-0000-0000-0004";
    private String lockedUserOrcid = "0000-0000-0000-0006";
    private String deactivatedUserOrcid = "0000-0000-0000-0007";

    @Resource
    PublicRecordController publicRecordController;

    @Resource
    private LocaleManager localeManager;

    @Mock
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    @Mock
    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        assertNotNull(publicRecordController);
        Mockito.when(request.getRequestURI()).thenReturn("/");
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }


    @Test
    public void testGetPublicRecord() {
        PublicRecord record = publicRecordController.getPublicRecord(userOrcid);

        assertNotNull(record.getBiography());
        assertEquals("Biography for 0000-0000-0000-0003", record.getBiography().getBiography().getValue());

        assertNotNull(record.getDisplayName());
        assertEquals("Credit Name", record.getDisplayName());

        assertNotNull(record.getTitle());
        assertEquals((record.getDisplayName() + " (0000-0000-0000-0003) - " + localeManager.resolveMessage("layout.public-layout.title")), record.getTitle());
        assertNotNull(record.getOtherNames());
        assertEquals(1, record.getOtherNames().getOtherNames().size());

        assertNotNull(record.getNames());
        assertEquals("Given Names", record.getNames().getGivenNames().getValue());
        assertEquals("Family Name", record.getNames().getFamilyName().getValue());

        assertNotNull(record.getCountries());
        assertEquals(1, record.getCountries().getAddresses().size());
        assertEquals(String.valueOf(9), record.getCountries().getAddresses().get(0).getPutCode());
        assertEquals(Iso3166Country.US.name(), record.getCountries().getAddresses().get(0).getIso2Country().getValue().value());
        assertEquals(Visibility.PUBLIC.value(), record.getCountries().getAddresses().get(0).getVisibility().getVisibility().value());

        assertNotNull(record.getKeyword());
        assertEquals(1, record.getKeyword().getKeywords().size());
        assertEquals(String.valueOf(9), record.getKeyword().getKeywords().get(0).getPutCode());
        assertEquals("PUBLIC", record.getKeyword().getKeywords().get(0).getContent());
        assertEquals(Visibility.PUBLIC.value(), record.getKeyword().getKeywords().get(0).getVisibility().getVisibility().value());

        assertNotNull(record.getWebsite());

        assertNotNull(record.getEmails());
        assertEquals(2, record.getEmails().getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", record.getEmails().getEmails().get(0).getValue());
        assertEquals("public_0000-0000-0000-0003@orcid.org", record.getEmails().getEmails().get(1).getValue());
        assertEquals(Visibility.PUBLIC, record.getEmails().getEmails().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, record.getEmails().getEmails().get(1).getVisibility());

        assertNotNull(record.getExternalIdentifier());
        assertEquals(3, record.getExternalIdentifier().getExternalIdentifiers().size());
        // Added by member
        assertEquals(String.valueOf(19), record.getExternalIdentifier().getExternalIdentifiers().get(0).getPutCode());
        assertEquals("http://ext-id/self/obo/public", record.getExternalIdentifier().getExternalIdentifiers().get(0).getUrl());
        assertEquals(Visibility.PUBLIC.value(), record.getExternalIdentifier().getExternalIdentifiers().get(0).getVisibility().getVisibility().value());
        // Added by user
        assertEquals(String.valueOf(18), record.getExternalIdentifier().getExternalIdentifiers().get(1).getPutCode());
        assertEquals("http://ext-id/self/public", record.getExternalIdentifier().getExternalIdentifiers().get(1).getUrl());
        assertEquals(Visibility.PUBLIC.value(), record.getExternalIdentifier().getExternalIdentifiers().get(1).getVisibility().getVisibility().value());
        // User OBO
        assertEquals(String.valueOf(13), record.getExternalIdentifier().getExternalIdentifiers().get(2).getPutCode());
        assertEquals("http://ext-id/public_ref", record.getExternalIdentifier().getExternalIdentifiers().get(2).getUrl());
        assertEquals(Visibility.PUBLIC.value(), record.getExternalIdentifier().getExternalIdentifiers().get(2).getVisibility().getVisibility().value());

        assertNotNull(record.getWebsite());
        assertEquals(1, record.getWebsite().getWebsites().size());
        assertEquals(String.valueOf(13), record.getWebsite().getWebsites().get(0).getPutCode());
        assertEquals("public_rurl", record.getWebsite().getWebsites().get(0).getUrlName());
        assertEquals(Visibility.PUBLIC.value(), record.getWebsite().getWebsites().get(0).getVisibility().getVisibility().value());
    }

    @Test
    public void testGetRecordSummary() {
        RecordSummaryPojo record = publicRecordController.getSummaryRecord(userOrcid);

        assertEquals("active", record.getStatus());
        assertNotNull(record.getName());
        assertEquals("Credit Name", record.getName());

        assertNotNull(record.getEmploymentAffiliations());
        assertEquals(1, record.getEmploymentAffiliations().size());
        assertEquals(1, record.getEmploymentAffiliationsCount());

        assertEquals("An institution", record.getEmploymentAffiliations().get(0).getOrganizationName());
        
        // Check external identifiers
        assertNotNull(record.getExternalIdentifiers());
        assertEquals(3, record.getExternalIdentifiers().size());

        // User OBO
        assertEquals(String.valueOf(19), record.getExternalIdentifiers().get(0).getId());
        assertEquals("self_public_user_obo_type", record.getExternalIdentifiers().get(0).getCommonName());
        assertEquals("self_public_user_obo_ref", record.getExternalIdentifiers().get(0).getReference());
        assertEquals("http://ext-id/self/obo/public", record.getExternalIdentifiers().get(0).getUrl());
        assertFalse(record.getExternalIdentifiers().get(0).isValidated());
        // Added by user
        assertEquals(String.valueOf(18), record.getExternalIdentifiers().get(1).getId());
        assertEquals("self_public_type", record.getExternalIdentifiers().get(1).getCommonName());
        assertEquals("self_public_ref", record.getExternalIdentifiers().get(1).getReference());
        assertEquals("http://ext-id/self/public", record.getExternalIdentifiers().get(1).getUrl());
        assertFalse(record.getExternalIdentifiers().get(1).isValidated());
        // Added by member
        assertEquals(String.valueOf(13), record.getExternalIdentifiers().get(2).getId());
        assertEquals("public_type", record.getExternalIdentifiers().get(2).getCommonName());
        assertEquals("public_ref", record.getExternalIdentifiers().get(2).getReference());
        assertEquals("http://ext-id/public_ref", record.getExternalIdentifiers().get(2).getUrl());
        assertTrue(record.getExternalIdentifiers().get(2).isValidated());
        
        assertEquals(1, record.getValidatedWorks());
        assertEquals(0, record.getSelfAssertedWorks());

        assertEquals(0, record.getPeerReviewsTotal());
        assertEquals(0, record.getPeerReviewPublicationGrants());

        assertEquals(1, record.getValidatedFunds());
        assertEquals(0, record.getSelfAssertedFunds());

        assertNotNull(record.getProfessionalActivities());
        assertEquals(4, record.getProfessionalActivitiesCount());
    }

    @Test
    public void testGetRecordSummaryDeactivated() {
        RecordSummaryPojo record = publicRecordController.getSummaryRecord(deactivatedUserOrcid);

        assertEquals("Given Names Deactivated Family Name Deactivated", record.getName());

        assertEquals("deactivated", record.getStatus());
    }

    @Test
    public void testGetRecordSummaryLocked() {
        RecordSummaryPojo record = publicRecordController.getSummaryRecord(lockedUserOrcid);

        assertNotNull(record.getName());
        assertEquals("Given Names Deactivated Family Name Deactivated", record.getName());

        assertEquals("locked", record.getStatus());
    }

    @Test
    public void testGetRecordSummaryDeprecated() {
        RecordSummaryPojo record = publicRecordController.getSummaryRecord(deprecatedUserOrcid);

        assertNull(record.getName());

        assertEquals("deprecated", record.getStatus());
    }

    @Test
    public void testGetRecordSummaryPrivateName() {
        RecordSummaryPojo record = publicRecordController.getSummaryRecord(unclaimedUserOrcid);

        assertNull(record.getName());
    }

    @Test
    public void testGetRecordSummaryProfessionalActivitiesSortedByEndDate() {
        RecordSummaryPojo record = publicRecordController.getSummaryRecord("0000-0000-0000-0008");

        assertEquals(5, record.getProfessionalActivitiesCount());
        assertNull(record.getProfessionalActivities().get(0).getEndDate());
        assertEquals("2023-01-01", record.getProfessionalActivities().get(1).getEndDate());
        assertEquals("2021-12-05", record.getProfessionalActivities().get(2).getEndDate());
    }
}
