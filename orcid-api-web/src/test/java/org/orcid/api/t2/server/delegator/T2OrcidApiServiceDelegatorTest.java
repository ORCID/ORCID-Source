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

import static org.junit.Assert.assertEquals;
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
import org.orcid.core.exception.OrcidDeprecatedException;
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
import org.orcid.jaxb.model.message.ExternalIdUrl;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.Funding;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.message.FundingList;
import org.orcid.jaxb.model.message.FundingTitle;
import org.orcid.jaxb.model.message.FundingType;
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
import org.orcid.jaxb.model.message.OrganizationDefinedFundingSubType;
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
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml", "/data/WorksEntityData.xml", 
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
        assertEquals(201, response.getStatus());
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
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE,
                ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.AFFILIATIONS_UPDATE, ScopePathType.AUTHENTICATE,
                ScopePathType.FUNDING_CREATE, ScopePathType.FUNDING_READ_LIMITED, ScopePathType.FUNDING_UPDATE, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE,
                ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.ORCID_BIO_UPDATE, ScopePathType.ORCID_PATENTS_CREATE, ScopePathType.ORCID_PATENTS_READ_LIMITED,
                ScopePathType.ORCID_PATENTS_UPDATE, ScopePathType.ORCID_PROFILE_CREATE, ScopePathType.ORCID_PROFILE_READ_LIMITED, ScopePathType.ORCID_WORKS_CREATE,
                ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_WORKS_UPDATE, ScopePathType.PEER_REVIEW_CREATE, ScopePathType.PEER_REVIEW_READ_LIMITED,
                ScopePathType.PEER_REVIEW_UPDATE, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED, ScopePathType.READ_PUBLIC);        
        String orcid = "4444-4444-4444-4442";                
        try {
            t2OrcidApiServiceDelegator.findAffiliationsDetails(orcid);
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }  
        
        try {
            t2OrcidApiServiceDelegator.findFullDetails(orcid);
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            t2OrcidApiServiceDelegator.findBioDetails(orcid);
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }        
        
        try {
            t2OrcidApiServiceDelegator.findExternalIdentifiers(orcid);
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }
                
        try {
            t2OrcidApiServiceDelegator.findFundingDetails(orcid);
            fail();
        } catch(AccessControlException e) {
            assertEquals("You do not have the required permissions.", e.getMessage());
        } catch(Exception e) {
            fail();
        }
                
        try {
            t2OrcidApiServiceDelegator.findWorksDetails(orcid);
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

        //now test what happens if we add a new one.
        
    }
    
    @Test
    public void testReadPrivacyOnBioAsPublic(){
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4497",ScopePathType.READ_PUBLIC);
        /*Example A List on 4444-4444-4444-4497:
        Item 1 Private (client is source)
        Item 2 Private (other source)
        Item 3 Limited
        Item 4 Public 
        */
        OrcidProfile p = ((OrcidMessage)t2OrcidApiServiceDelegator.findBioDetails("4444-4444-4444-4497").getEntity()).getOrcidProfile();
        assertEquals(1,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("type4",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getVisibility());
    }
    
    @Test
    public void testReadPrivacyOnBio(){
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4497",ScopePathType.READ_LIMITED);
        /*Example A List on 4444-4444-4444-4497:
        Item 1 Private (client is source)
        Item 2 Private (other source)
        Item 3 Limited
        Item 4 Public 
        */
        OrcidProfile p = ((OrcidMessage)t2OrcidApiServiceDelegator.findBioDetails("4444-4444-4444-4497").getEntity()).getOrcidProfile();
        assertEquals(3,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("type2",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals(Visibility.PRIVATE,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getVisibility());
        assertEquals("type3",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(1).getExternalIdCommonName().getContent());
        assertEquals(Visibility.LIMITED,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(1).getVisibility());
        assertEquals("type4",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(2).getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(2).getVisibility());
        assertEquals(Visibility.PRIVATE,p.getOrcidBio().getExternalIdentifiers().getVisibility());
    }
    
    @Test
    public void testReadPrivacyOnBio2(){
        /*Example B List:
        Item 1 Limited
        Item 2 Public 
         */
                
        //read the profile with LIMITED
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441",ScopePathType.READ_LIMITED);
        OrcidProfile p = ((OrcidMessage)t2OrcidApiServiceDelegator.findBioDetails("4444-4444-4444-4441").getEntity()).getOrcidProfile();
        assertEquals(2,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("A-0001",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getVisibility());
        assertEquals("A-0002",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(1).getExternalIdCommonName().getContent());
        assertEquals(Visibility.LIMITED,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(1).getVisibility());
        assertEquals(Visibility.LIMITED,p.getOrcidBio().getExternalIdentifiers().getVisibility());
        
        //read the profile with PUBLIC
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441",ScopePathType.READ_PUBLIC);
        p = ((OrcidMessage)t2OrcidApiServiceDelegator.findBioDetails("4444-4444-4444-4441").getEntity()).getOrcidProfile();
        assertEquals(1,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("A-0001",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getVisibility());        
    }
    
    @Test
    public void testReadPrivacyOnBio3(){
        /*Example C List:
        Item 1 Public
         */
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443",ScopePathType.READ_LIMITED);
        OrcidProfile p = ((OrcidMessage)t2OrcidApiServiceDelegator.findBioDetails("4444-4444-4444-4443").getEntity()).getOrcidProfile();
        System.out.println(p.toString());        
        assertEquals(1,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("Facebook",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getVisibility());

        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443",ScopePathType.READ_PUBLIC);
        p = ((OrcidMessage)t2OrcidApiServiceDelegator.findBioDetails("4444-4444-4444-4443").getEntity()).getOrcidProfile();
        System.out.println(p.toString());        
        assertEquals(1,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("Facebook",p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getVisibility());
        assertEquals(Visibility.PUBLIC,p.getOrcidBio().getExternalIdentifiers().getVisibility());
    }
    
    @Test
    public void testAddDuplicatedExtIdsWithDifferentClientWorks() {        
        String orcid = "0000-0000-0000-0002";
        
        OrcidMessage message = new OrcidMessage();
        message.setMessageVersion("1.2_rc6");
        message.setOrcidProfile(new OrcidProfile());
        message.getOrcidProfile().setOrcidBio(new OrcidBio());
        ExternalIdentifiers extIds = new ExternalIdentifiers();
        ExternalIdentifier extId1 = new ExternalIdentifier();
        String commonName = "common-name-1-" + System.currentTimeMillis();
        extId1.setExternalIdCommonName(new ExternalIdCommonName(commonName));
        extId1.setExternalIdReference(new ExternalIdReference("ext-id-reference-1"));
        extId1.setExternalIdUrl(new ExternalIdUrl("http://test.orcid.org/" + System.currentTimeMillis()));
        extIds.getExternalIdentifier().add(extId1);
        message.getOrcidProfile().getOrcidBio().setExternalIdentifiers(extIds);
        
        //Add for client 1
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", "APP-5555555555555555", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response r = t2OrcidApiServiceDelegator.addExternalIdentifiers(null, orcid, message);
        assertNotNull(r);
        OrcidMessage newMessage1 = (OrcidMessage)r.getEntity();
        assertNotNull(newMessage1);
        assertNotNull(newMessage1.getOrcidProfile());
        assertNotNull(newMessage1.getOrcidProfile().getOrcidBio());
        assertNotNull(newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers());
        assertNotNull(newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(commonName, newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("APP-5555555555555555", newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getSource().retrieveSourcePath());
        
        //Reset message for source # 2
        message = new OrcidMessage();
        message.setMessageVersion("1.2_rc6");
        message.setOrcidProfile(new OrcidProfile());
        message.getOrcidProfile().setOrcidBio(new OrcidBio());
        extId1.setSource(null);
        message.getOrcidProfile().getOrcidBio().setExternalIdentifiers(extIds);
        
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", "APP-5555555555555556", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        r = t2OrcidApiServiceDelegator.addExternalIdentifiers(null, orcid, message);
        OrcidMessage newMessage2 = (OrcidMessage)r.getEntity();
        assertNotNull(newMessage2);
        assertNotNull(newMessage2.getOrcidProfile());
        assertNotNull(newMessage2.getOrcidProfile().getOrcidBio());
        assertNotNull(newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers());
        assertNotNull(newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier());
        assertEquals(2, newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(commonName, newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("APP-5555555555555555", newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getSource().retrieveSourcePath());
        assertEquals(commonName, newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(1).getExternalIdCommonName().getContent());
        assertEquals("APP-5555555555555556", newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(1).getSource().retrieveSourcePath());
        
    }
    
    @Test
    public void testAddDuplicatedExtIdsDontModifyProfile() {
        String userOrcid = "0000-0000-0000-0005";
        
        OrcidMessage message = new OrcidMessage();
        message.setMessageVersion("1.2_rc6");
        message.setOrcidProfile(new OrcidProfile());
        message.getOrcidProfile().setOrcidBio(new OrcidBio());
        ExternalIdentifiers extIds = new ExternalIdentifiers();
        ExternalIdentifier extId1 = new ExternalIdentifier();
        String commonName = "common-name-1-" + System.currentTimeMillis();
        extId1.setExternalIdCommonName(new ExternalIdCommonName(commonName));
        extId1.setExternalIdReference(new ExternalIdReference("ext-id-reference-1"));
        extId1.setExternalIdUrl(new ExternalIdUrl("http://test.orcid.org/" + System.currentTimeMillis()));
        extIds.getExternalIdentifier().add(extId1);
        message.getOrcidProfile().getOrcidBio().setExternalIdentifiers(extIds);
        
        //Add for client 1
        SecurityContextTestUtils.setUpSecurityContext(userOrcid, "APP-5555555555555555", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response r = t2OrcidApiServiceDelegator.addExternalIdentifiers(null, userOrcid, message);
        assertNotNull(r);
        OrcidMessage newMessage1 = (OrcidMessage)r.getEntity();
        assertNotNull(newMessage1);
        assertNotNull(newMessage1.getOrcidProfile());
        assertNotNull(newMessage1.getOrcidProfile().getOrcidBio());
        assertNotNull(newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers());
        assertNotNull(newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(commonName, newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("APP-5555555555555555", newMessage1.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getSource().retrieveSourcePath());
        
        //Reset message to add it again
        message = new OrcidMessage();
        message.setMessageVersion("1.2_rc6");
        message.setOrcidProfile(new OrcidProfile());
        message.getOrcidProfile().setOrcidBio(new OrcidBio());
        extId1.setSource(null);
        message.getOrcidProfile().getOrcidBio().setExternalIdentifiers(extIds);
        r = t2OrcidApiServiceDelegator.addExternalIdentifiers(null, userOrcid, message);
        //If we add it again, no error, but it is not duplicated
        assertNotNull(r);
        OrcidMessage newMessage2 = (OrcidMessage)r.getEntity();
        assertNotNull(newMessage2);
        assertNotNull(newMessage2.getOrcidProfile());
        assertNotNull(newMessage2.getOrcidProfile().getOrcidBio());
        assertNotNull(newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers());
        assertNotNull(newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals(commonName, newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getExternalIdCommonName().getContent());
        assertEquals("APP-5555555555555555", newMessage2.getOrcidProfile().getOrcidBio().getExternalIdentifiers().getExternalIdentifier().get(0).getSource().retrieveSourcePath());
    }
    
    @Test(expected = OrcidDeprecatedException.class)
    public void testAddWorkToDeprecatedAccount() {
    	SecurityContextTestUtils.setUpSecurityContext();
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-444X"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWork work = new OrcidWork();
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Title"));
        work.setWorkTitle(title);
        
        WorkExternalIdentifiers weis = new WorkExternalIdentifiers();
        WorkExternalIdentifier wei = new WorkExternalIdentifier();
        wei.setWorkExternalIdentifierId(new WorkExternalIdentifierId("00000001"));
        wei.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        weis.getWorkExternalIdentifier().add(wei);
        work.setWorkExternalIdentifiers(weis);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        t2OrcidApiServiceDelegator.addWorks(mockedUriInfo, "4444-4444-4444-444X", orcidMessage);        
    }
    
    @Test(expected = OrcidDeprecatedException.class)
    public void testAddFundingToDeprecatedAccount() {
    	SecurityContextTestUtils.setUpSecurityContext();
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-444X"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        
        FundingList fundings = new FundingList();
        Funding funding = new Funding();

        OrganizationAddress address = new OrganizationAddress();
        address.setCity("City");
        address.setCountry(Iso3166Country.US);
        
        Organization org = new Organization();
        org.setAddress(address);
        org.setName("Testing org name");
        
        funding.setOrganization(org);
        
        FundingExternalIdentifiers fExtIds = new FundingExternalIdentifiers();
        FundingExternalIdentifier fExtId = new FundingExternalIdentifier();
        fExtId.setType(FundingExternalIdentifierType.GRANT_NUMBER);
        fExtId.setValue("FExtId");
        fExtIds.getFundingExternalIdentifier().add(fExtId);
        funding.setFundingExternalIdentifiers(fExtIds);
        
        funding.setType(FundingType.AWARD);        
        funding.setOrganizationDefinedFundingType(new OrganizationDefinedFundingSubType("fType"));
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Funding title"));
        funding.setTitle(title);
        fundings.getFundings().add(funding);
        orcidActivities.setFundings(fundings);
        t2OrcidApiServiceDelegator.addFunding(mockedUriInfo, "4444-4444-4444-444X", orcidMessage);  
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void testAddAffiliationToDeprecatedAccount() {
    	SecurityContextTestUtils.setUpSecurityContext();
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-444X"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("City");
        address.setCountry(Iso3166Country.US);
        
        Organization org = new Organization();
        org.setAddress(address);
        org.setName("Testing org name");
        
        Affiliations affiliations = new Affiliations();
        Affiliation affiliation = new Affiliation();
        affiliation.setDepartmentName("Dept name");
        affiliation.setOrganization(org);
        affiliation.setType(AffiliationType.EMPLOYMENT);
        affiliations.getAffiliation().add(affiliation);        
        orcidActivities.setAffiliations(affiliations);        
        t2OrcidApiServiceDelegator.addAffiliations(mockedUriInfo, "4444-4444-4444-444X", orcidMessage);  
    }
    
    @Test(expected = OrcidDeprecatedException.class)
    public void testAddExternalIdentifiersToDeprecatedAccount() {
    	SecurityContextTestUtils.setUpSecurityContext();
    	OrcidMessage orcidMessage = new OrcidMessage();
    	orcidMessage.setMessageVersion("1.2_rc6");
    	orcidMessage.setOrcidProfile(new OrcidProfile());
    	orcidMessage.getOrcidProfile().setOrcidBio(new OrcidBio());
        ExternalIdentifiers extIds = new ExternalIdentifiers();
        ExternalIdentifier extId1 = new ExternalIdentifier();
        String commonName = "common-name-1-" + System.currentTimeMillis();
        extId1.setExternalIdCommonName(new ExternalIdCommonName(commonName));
        extId1.setExternalIdReference(new ExternalIdReference("ext-id-reference-1"));
        extId1.setExternalIdUrl(new ExternalIdUrl("http://test.orcid.org/" + System.currentTimeMillis()));
        extIds.getExternalIdentifier().add(extId1);
        orcidMessage.getOrcidProfile().getOrcidBio().setExternalIdentifiers(extIds);                
        t2OrcidApiServiceDelegator.addExternalIdentifiers(mockedUriInfo, "4444-4444-4444-444X", orcidMessage);  
    }
    
}