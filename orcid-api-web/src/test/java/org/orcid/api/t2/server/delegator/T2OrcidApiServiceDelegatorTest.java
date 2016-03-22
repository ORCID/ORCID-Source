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
package org.orcid.api.t2.server.delegator;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.ExternalIdCommonName;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.test.DBUnitTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.uri.UriBuilderImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class T2OrcidApiServiceDelegatorTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", 
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml",
            "/data/OrgAffiliationEntityData.xml");

    @Resource(name = "t2OrcidApiServiceDelegatorLatest")
    private T2OrcidApiServiceDelegator t2OrcidApiServiceDelegator;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Mock
    private UriInfo mockedUriInfo;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(mockedUriInfo.getBaseUriBuilder()).thenReturn(new UriBuilderImpl());
    }

    @After
    public void after() {
        SecurityContextHolder.clearContext();
        orcidProfileManager.clearOrcidProfileCache();
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testFindWorksDetails() {
        SecurityContextTestUtils.setUpSecurityContext();
        Response response = t2OrcidApiServiceDelegator.findWorksDetails("4444-4444-4444-4441");
        assertNotNull(response);
    }

    @Test
    public void testAddWorks() {
        SecurityContextTestUtils.setUpSecurityContext();
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4441"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        Response response = t2OrcidApiServiceDelegator.addWorks(mockedUriInfo, "4444-4444-4444-4441", orcidMessage);
        assertNotNull(response);
    }

    @Test
    @Transactional
    public void testUpdateWithNewWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ORCID_WORKS_UPDATE);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4446"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Added by works update"));
        orcidWork.setWorkTitle(workTitle);
        orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier wei = new WorkExternalIdentifier();
        wei.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        wei.setWorkExternalIdentifierId(new WorkExternalIdentifierId("abc123"));
        workExternalIdentifiers.getWorkExternalIdentifier().add(wei);
        orcidWork.setWorkExternalIdentifiers(workExternalIdentifiers);
        Response response = t2OrcidApiServiceDelegator.updateWorks(mockedUriInfo, "4444-4444-4444-4446", orcidMessage);
        assertNotNull(response);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4446");
        List<OrcidWork> retreivedWorksList = retrievedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        boolean foundWorkFromAnotherSource = false;
        boolean foundExisting = false;
        boolean foundNew = false;
        boolean foundExistingPrivate = false;
        // Should have the added work, plus existing private work and work from
        // another source
        for (OrcidWork retrievedWork : retreivedWorksList) {
            if ("6".equals(retrievedWork.getPutCode())) {
                assertEquals("Journal article B", retrievedWork.getWorkTitle().getTitle().getContent());
                assertEquals(Visibility.LIMITED, retrievedWork.getVisibility());
                foundWorkFromAnotherSource = true;
            } else if ("7".equals(retrievedWork.getPutCode())) {
                // Existing private work
                assertEquals("Journal article C", retrievedWork.getWorkTitle().getTitle().getContent());
                assertEquals(Visibility.PRIVATE, retrievedWork.getVisibility());
                foundExisting = true;
            } else if ("8".equals(retrievedWork.getPutCode())) {
                // Existing private work added by the user
                assertEquals("Journal article D", retrievedWork.getWorkTitle().getTitle().getContent());
                assertEquals(Visibility.PRIVATE, retrievedWork.getVisibility());
                foundExistingPrivate = true;
            } else {
                // The added work
                assertEquals("Added by works update", retrievedWork.getWorkTitle().getTitle().getContent());
                foundNew = true;
            }
        }
        assertTrue("Work from other source should be there", foundWorkFromAnotherSource);
        assertTrue("New work should be there", foundNew);
        assertTrue("Existing private work should be there", foundExisting);
        assertTrue("Existing private work added by the user should be there", foundExistingPrivate);
        assertEquals(4, retreivedWorksList.size());
    }

    @Test
    @Transactional
    public void testUpdateExistingNonPrivateWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ORCID_WORKS_UPDATE);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4446"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        orcidWork.setPutCode("5");
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Updated by works update"));
        orcidWork.setWorkTitle(workTitle);
        orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier wei = new WorkExternalIdentifier();
        wei.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        wei.setWorkExternalIdentifierId(new WorkExternalIdentifierId("abc123"));
        workExternalIdentifiers.getWorkExternalIdentifier().add(wei);
        orcidWork.setWorkExternalIdentifiers(workExternalIdentifiers);
        Response response = t2OrcidApiServiceDelegator.updateWorks(mockedUriInfo, "4444-4444-4444-4446", orcidMessage);
        assertNotNull(response);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4446");
        List<OrcidWork> retreivedWorksList = retrievedProfile.getOrcidActivities().getOrcidWorks().getOrcidWork();
        boolean foundWorkFromAnotherSource = false;
        boolean foundUpdated = false;
        boolean foundExisting = false;
        boolean foundExistingPrivate = false;
        for (OrcidWork retrievedWork : retreivedWorksList) {
            if ("5".equals(retrievedWork.getPutCode())) {
                // The updated work
                assertEquals("Updated by works update", retrievedWork.getWorkTitle().getTitle().getContent());
                assertEquals(Visibility.PUBLIC, retrievedWork.getVisibility());
                foundUpdated = true;
            } else if ("6".equals(retrievedWork.getPutCode())) {
                assertEquals("Journal article B", retrievedWork.getWorkTitle().getTitle().getContent());
                assertEquals(Visibility.LIMITED, retrievedWork.getVisibility());
                foundWorkFromAnotherSource = true;
            } else if ("7".equals(retrievedWork.getPutCode())) {
                // Existing private work
                assertEquals("Journal article C", retrievedWork.getWorkTitle().getTitle().getContent());
                assertEquals(Visibility.PRIVATE, retrievedWork.getVisibility());
                foundExisting = true;
            } else if ("8".equals(retrievedWork.getPutCode())) {
                // Existing private work added by the user
                assertEquals("Journal article D", retrievedWork.getWorkTitle().getTitle().getContent());
                assertEquals(Visibility.PRIVATE, retrievedWork.getVisibility());
                foundExistingPrivate = true;
            }
        }
        assertTrue("Work from other source should be there", foundWorkFromAnotherSource);
        assertTrue("Updated work should be there", foundUpdated);
        assertTrue("Existing private work should be there", foundExisting);
        assertTrue("Existing private work added by the user should be there", foundExistingPrivate);
        assertEquals(4, retreivedWorksList.size());
    }

    @Test(expected = WrongSourceException.class)
    @Transactional
    public void testUpdateWorkWhenNotSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ORCID_WORKS_UPDATE);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4446"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        OrcidWork orcidWork = new OrcidWork();
        orcidWorks.getOrcidWork().add(orcidWork);
        orcidWork.setPutCode("6");
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Updated by works update"));
        orcidWork.setWorkTitle(workTitle);
        orcidWork.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier wei = new WorkExternalIdentifier();
        wei.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        wei.setWorkExternalIdentifierId(new WorkExternalIdentifierId("abc123"));
        workExternalIdentifiers.getWorkExternalIdentifier().add(wei);
        orcidWork.setWorkExternalIdentifiers(workExternalIdentifiers);
        t2OrcidApiServiceDelegator.updateWorks(mockedUriInfo, "4444-4444-4444-4446", orcidMessage);
    }

    @Test
    public void testReadClaimedAsClientOnly() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        String orcid = "4444-4444-4444-4441";

        Response readResponse = t2OrcidApiServiceDelegator.findFullDetails(orcid);

        assertNotNull(readResponse);
        assertEquals(HttpStatus.SC_OK, readResponse.getStatus());
        OrcidMessage retrievedMessage = (OrcidMessage) readResponse.getEntity();
        assertEquals(orcid, retrievedMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        assertEquals("Credit Name", retrievedMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getContent());
    }

    @Test
    public void testCreateAndReadOwnCreation() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        OrcidMessage orcidMessage = createStubOrcidMessage();
        Email email = new Email("madeupemail@semantico.com");
        email.setPrimary(true);
        orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().add(email);

        Response createResponse = t2OrcidApiServiceDelegator.createProfile(mockedUriInfo, orcidMessage);

        assertNotNull(createResponse);
        assertEquals(HttpStatus.SC_CREATED, createResponse.getStatus());
        String location = ((URI) createResponse.getMetadata().getFirst("Location")).getPath();
        assertNotNull(location);
        String orcid = location.substring(1, 20);

        Response readResponse = t2OrcidApiServiceDelegator.findFullDetails(orcid);
        assertNotNull(readResponse);
        assertEquals(HttpStatus.SC_OK, readResponse.getStatus());
        OrcidMessage retrievedMessage = (OrcidMessage) readResponse.getEntity();
        assertEquals(orcid, retrievedMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        assertEquals("Test credit name", retrievedMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getContent());
    }

    @Test
    public void testReadUnclaimedWhenNotOwnCreation() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly();
        OrcidMessage orcidMessage = createStubOrcidMessage();
        Email email = new Email("madeupemail2@semantico.com");
        email.setPrimary(true);
        orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().add(email);

        Response createResponse = t2OrcidApiServiceDelegator.createProfile(mockedUriInfo, orcidMessage);

        assertNotNull(createResponse);
        assertEquals(HttpStatus.SC_CREATED, createResponse.getStatus());
        String location = ((URI) createResponse.getMetadata().getFirst("Location")).getPath();
        assertNotNull(location);
        String orcid = location.substring(1, 20);

        SecurityContextTestUtils.setUpSecurityContextForClientOnly("4444-4444-4444-4448");

        Response readResponse = t2OrcidApiServiceDelegator.findFullDetails(orcid);
        assertNotNull(readResponse);
        assertEquals(HttpStatus.SC_OK, readResponse.getStatus());
        OrcidMessage retrievedMessage = (OrcidMessage) readResponse.getEntity();
        assertEquals(orcid, retrievedMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        GivenNames givenNames = retrievedMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames();
        assertNotNull(givenNames);
        assertEquals("Reserved For Claim", givenNames.getContent());
    }

    @Test
    public void testAddAffilliations() {
        SecurityContextTestUtils.setUpSecurityContext(ScopePathType.AFFILIATIONS_CREATE);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4441"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation1 = new Affiliation();
        affiliations.getAffiliation().add(affiliation1);
        affiliation1.setType(AffiliationType.EDUCATION);
        Organization organization1 = new Organization();
        affiliation1.setOrganization(organization1);
        organization1.setName("A new affiliation");
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization1.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);
        Response response = t2OrcidApiServiceDelegator.addAffiliations(mockedUriInfo, "4444-4444-4444-4441", orcidMessage);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        String location = ((URI) response.getMetadata().getFirst("Location")).getPath();
        assertNotNull(location);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4441");
        List<Affiliation> affiliationsList = retrievedProfile.getOrcidActivities().getAffiliations().getAffiliation();
        assertEquals(1, affiliationsList.size());
        Affiliation affiliation = affiliationsList.get(0);
        assertEquals("A new affiliation", affiliation.getOrganization().getName());
        assertEquals("APP-5555555555555555", affiliation.getSource().retrieveSourcePath());
    }

    @Test
    @Transactional
    public void testUpdateWithNewAffiliation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.AFFILIATIONS_UPDATE);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4443"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation1 = new Affiliation();
        affiliations.getAffiliation().add(affiliation1);
        affiliation1.setType(AffiliationType.EDUCATION);
        Organization organization1 = new Organization();
        affiliation1.setOrganization(organization1);
        organization1.setName("A new affiliation");
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization1.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);
        Response response = t2OrcidApiServiceDelegator.updateAffiliations(mockedUriInfo, "4444-4444-4444-4443", orcidMessage);
        assertNotNull(response);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4443");
        List<Affiliation> retreivedAffiliationsList = retrievedProfile.getOrcidActivities().getAffiliations().getAffiliation();
        assertEquals(3, retreivedAffiliationsList.size());
        Affiliation newAffiliation = retreivedAffiliationsList.get(0);
        assertEquals("A new affiliation", newAffiliation.getOrganization().getName());
        assertEquals("APP-5555555555555555", newAffiliation.getSource().retrieveSourcePath());
        Affiliation existingAffiliation = retreivedAffiliationsList.get(1);
        assertEquals(Visibility.PRIVATE, existingAffiliation.getVisibility());
        assertEquals("Eine Institution", existingAffiliation.getOrganization().getName());
    }

    @Test
    @Transactional
    public void testUpdateExistingNonPrivateAffiliation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.AFFILIATIONS_UPDATE);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4443"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation1 = new Affiliation();
        affiliations.getAffiliation().add(affiliation1);
        affiliation1.setPutCode("3");
        affiliation1.setType(AffiliationType.EDUCATION);
        Organization organization1 = new Organization();
        affiliation1.setOrganization(organization1);
        organization1.setName("Different org");
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization1.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);
        Response response = t2OrcidApiServiceDelegator.updateAffiliations(mockedUriInfo, "4444-4444-4444-4443", orcidMessage);
        assertNotNull(response);

        OrcidProfile retrievedProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4443");
        List<Affiliation> retreivedAffiliationsList = retrievedProfile.getOrcidActivities().getAffiliations().getAffiliation();
        assertEquals(3, retreivedAffiliationsList.size());
        Affiliation updatedAffiliation = retreivedAffiliationsList.get(0);
        assertEquals("Different org", updatedAffiliation.getOrganization().getName());
        assertEquals("APP-5555555555555555", updatedAffiliation.getSource().retrieveSourcePath());
        Affiliation existingAffiliation = retreivedAffiliationsList.get(1);
        assertEquals(Visibility.PRIVATE, existingAffiliation.getVisibility());
        assertEquals("Eine Institution", existingAffiliation.getOrganization().getName());
    }

    @Test(expected = WrongSourceException.class)
    @Transactional
    public void testUpdateAffiliationWhenNotSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.AFFILIATIONS_UPDATE);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4443"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        Affiliations affiliations = new Affiliations();
        orcidActivities.setAffiliations(affiliations);
        Affiliation affiliation1 = new Affiliation();
        affiliations.getAffiliation().add(affiliation1);
        affiliation1.setPutCode("2");
        affiliation1.setType(AffiliationType.EDUCATION);
        Organization organization1 = new Organization();
        affiliation1.setOrganization(organization1);
        organization1.setName("Different org");
        OrganizationAddress organizationAddress = new OrganizationAddress();
        organization1.setAddress(organizationAddress);
        organizationAddress.setCity("Edinburgh");
        organizationAddress.setCountry(Iso3166Country.GB);
        t2OrcidApiServiceDelegator.updateAffiliations(mockedUriInfo, "4444-4444-4444-4443", orcidMessage);
    }    
    
    @Test
    public void testViewOtherProfileDontWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.AFFILIATIONS_UPDATE);
        try {
            t2OrcidApiServiceDelegator.findFullDetails("4444-4444-4444-4442");
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            t2OrcidApiServiceDelegator.findAffiliationsDetails("4444-4444-4444-4442");
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }        
        
        try {
            t2OrcidApiServiceDelegator.findBioDetails("4444-4444-4444-4442");
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }        
        
        try {
            t2OrcidApiServiceDelegator.findExternalIdentifiers("4444-4444-4444-4442");
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }
                
        try {
            t2OrcidApiServiceDelegator.findFundingDetails("4444-4444-4444-4442");
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }
                
        try {
            t2OrcidApiServiceDelegator.findWorksDetails("4444-4444-4444-4442");
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }                        
    }        
    
    private OrcidMessage createStubOrcidMessage() {
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidBio orcidBio = new OrcidBio();
        orcidProfile.setOrcidBio(orcidBio);
        PersonalDetails personalDetails = new PersonalDetails();
        orcidBio.setPersonalDetails(personalDetails);
        GivenNames givenNames = new GivenNames("Test given names");
        personalDetails.setGivenNames(givenNames);
        CreditName creditName = new CreditName("Test credit name");
        personalDetails.setCreditName(creditName);
        creditName.setVisibility(Visibility.LIMITED);
        ContactDetails contactDetails = new ContactDetails();
        orcidBio.setContactDetails(contactDetails);
        return orcidMessage;
    }

    @Test
    public void testRegisterAndUnregisterWebhook() {
        Set<String> scopes = new HashSet<String>();
        scopes.add(ScopePathType.WEBHOOK.value());
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", scopes);
        Response response = t2OrcidApiServiceDelegator.registerWebhook(mockedUriInfo, "4444-4444-4444-4447", "www.webhook.com");
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        response = t2OrcidApiServiceDelegator.unregisterWebhook(mockedUriInfo, "4444-4444-4444-4447", "www.webhook.com");
        assertNotNull(response);
        assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatus());
    }
    
    @Test
    public void testDefaultPrivacyOnBio(){
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4499",ScopePathType.ORCID_BIO_UPDATE);
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4499"));
        orcidMessage.setOrcidProfile(orcidProfile);
        OrcidBio orcidBio = new OrcidBio();
        orcidProfile.setOrcidBio(orcidBio);
        PersonalDetails personalDetails = new PersonalDetails();
        orcidBio.setPersonalDetails(personalDetails);        
        GivenNames givenNames = new GivenNames("Test given names");
        personalDetails.setGivenNames(givenNames);
        CreditName creditName = new CreditName("Credit Name");
        personalDetails.setCreditName(creditName);
        
        ExternalIdentifier id = new ExternalIdentifier();
        id.setExternalIdCommonName(new ExternalIdCommonName("cn1"));
        id.setExternalIdReference(new ExternalIdReference("value1"));
        orcidBio.setExternalIdentifiers(new ExternalIdentifiers());
        orcidBio.getExternalIdentifiers().getExternalIdentifier().add(id);
        
        personalDetails.setOtherNames(new OtherNames());
        personalDetails.getOtherNames().addOtherName("on1", null);
        orcidBio.setKeywords(new Keywords());
        orcidBio.getKeywords().getKeyword().add(new Keyword("kw1",null));  
        orcidBio.setResearcherUrls(new ResearcherUrls());
        orcidBio.getResearcherUrls().getResearcherUrl().add(new ResearcherUrl(new Url("http://rurl2.com"),null));
        
        t2OrcidApiServiceDelegator.updateBioDetails(mockedUriInfo, "4444-4444-4444-4499", orcidMessage);
        
        OrcidProfile p = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4499");
        assertEquals("cn1",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getVisibility());
        assertEquals("on1",p.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().get(0).getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getPersonalDetails().getOtherNames().getVisibility());
        assertEquals("kw1",p.getOrcidBio().getKeywords().getKeyword().get(0).getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getKeywords().getVisibility());
        assertEquals(new Url("http://rurl2.com"),p.getOrcidBio().getResearcherUrls().getResearcherUrl().get(0).getUrl());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getResearcherUrls().getVisibility());

    }

}