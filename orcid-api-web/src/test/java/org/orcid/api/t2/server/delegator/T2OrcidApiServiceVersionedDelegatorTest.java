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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Affiliations;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DisambiguatedOrganization;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Organization;
import org.orcid.jaxb.model.message.OrganizationAddress;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.Source;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.context.ContextConfiguration;

import com.sun.jersey.api.uri.UriBuilderImpl;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class T2OrcidApiServiceVersionedDelegatorTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", 
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml");

    @Resource(name = "t2OrcidApiServiceDelegatorV1_2")
    private T2OrcidApiServiceDelegator t2OrcidApiServiceDelegatorV2_1;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource(name = "t2OrcidApiServiceDelegatorLatest")
    private T2OrcidApiServiceDelegator t2OrcidApiServiceDelegatorLatest;

    @Resource
    private OrgAffiliationRelationDao orgAffiliationDao;
    
    @Mock
    private UriInfo mockedUriInfo;

    private Unmarshaller unmarshaller;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() throws JAXBException {
        MockitoAnnotations.initMocks(this);
        when(mockedUriInfo.getBaseUriBuilder()).thenReturn(new UriBuilderImpl());

        JAXBContext context = JAXBContext.newInstance(OrcidMessage.class);
        unmarshaller = context.createUnmarshaller();
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
        setUpSecurityContext();
        Response response = t2OrcidApiServiceDelegatorLatest.findWorksDetails("4444-4444-4444-4441");
        assertNotNull(response);
    }

    @Test
    public void testAddWorks() {
        setUpSecurityContext();
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4441"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        OrcidWorks orcidWorks = new OrcidWorks();
        orcidActivities.setOrcidWorks(orcidWorks);
        Response response = t2OrcidApiServiceDelegatorLatest.addWorks(mockedUriInfo, "4444-4444-4444-4441", orcidMessage);
        assertNotNull(response);
    }

    @Test(expected = OrcidValidationException.class)
    public void testCreateBioWithMultiplePrimaryEmails() {
        setUpSecurityContextForClientOnly();
        OrcidMessage orcidMessage = createStubOrcidMessage();
        ContactDetails contactDetails = orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails();
        List<Email> emailList = new ArrayList<>();
        String[] emailStrings = new String[] { "madeupemail@semantico.com", "madeupemail2@semantico.com" };
        for (String emailString : emailStrings) {
            Email email = new Email(emailString);
            email.setPrimary(true);
            emailList.add(email);
        }
        contactDetails.getEmail().addAll(emailList);

        t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);
    }

    @Test
    public void testReadClaimedAsClientOnly() {
        setUpSecurityContextForClientOnly();
        String orcid = "4444-4444-4444-4441";

        Response readResponse = t2OrcidApiServiceDelegatorLatest.findFullDetails(orcid);

        assertNotNull(readResponse);
        assertEquals(HttpStatus.SC_OK, readResponse.getStatus());
        OrcidMessage retrievedMessage = (OrcidMessage) readResponse.getEntity();
        assertEquals(orcid, retrievedMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        assertEquals("S. Milligan", retrievedMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getContent());
    }

    @Test
    public void testCreateAndReadOwnCreation() {
        setUpSecurityContextForClientOnly();
        OrcidMessage orcidMessage = createStubOrcidMessage();
        Email email = new Email("madeupemail@semantico.com");
        email.setPrimary(true);
        orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().add(email);

        Response createResponse = t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);

        assertNotNull(createResponse);
        assertEquals(HttpStatus.SC_CREATED, createResponse.getStatus());
        String location = ((URI) createResponse.getMetadata().getFirst("Location")).getPath();
        assertNotNull(location);
        String orcid = location.substring(1, 20);

        Response readResponse = t2OrcidApiServiceDelegatorLatest.findFullDetails(orcid);
        assertNotNull(readResponse);
        assertEquals(HttpStatus.SC_OK, readResponse.getStatus());
        OrcidMessage retrievedMessage = (OrcidMessage) readResponse.getEntity();
        assertEquals(orcid, retrievedMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        assertEquals("Test credit name", retrievedMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getCreditName().getContent());
    }

    @Test(expected = OrcidValidationException.class)
    public void testAttemptCreateWithLaterButOtherwiseValidVersion() {
        setUpSecurityContextForClientOnly();
        OrcidMessage orcidMessage = createStubOrcidMessage();
        orcidMessage.setMessageVersion("1.0.22");
        Email email = new Email("madeupemail3@semantico.com");
        orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().add(email);

        t2OrcidApiServiceDelegatorV2_1.createProfile(mockedUriInfo, orcidMessage);
    }

    @Test(expected = OrcidValidationException.class)
    public void testAttemptCreateWithTotallyIncorrectVersion() {
        setUpSecurityContextForClientOnly();
        OrcidMessage orcidMessage = createStubOrcidMessage();
        orcidMessage.setMessageVersion("abc");
        Email email = new Email("madeupemail4@semantico.com");
        orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().add(email);

        t2OrcidApiServiceDelegatorV2_1.createProfile(mockedUriInfo, orcidMessage);
    }

    @Test
    public void testReadUnclaimedWhenNotOwnCreation() {
        setUpSecurityContextForClientOnly();
        OrcidMessage orcidMessage = createStubOrcidMessage();
        Email email = new Email("madeupemail2@semantico.com");
        email.setPrimary(true);
        orcidMessage.getOrcidProfile().getOrcidBio().getContactDetails().getEmail().add(email);

        Response createResponse = t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);

        assertNotNull(createResponse);
        assertEquals(HttpStatus.SC_CREATED, createResponse.getStatus());
        String location = ((URI) createResponse.getMetadata().getFirst("Location")).getPath();
        assertNotNull(location);
        String orcid = location.substring(1, 20);

        setUpSecurityContextForClientOnly("4444-4444-4444-4448");

        Response readResponse = t2OrcidApiServiceDelegatorLatest.findFullDetails(orcid);
        assertNotNull(readResponse);
        assertEquals(HttpStatus.SC_OK, readResponse.getStatus());
        OrcidMessage retrievedMessage = (OrcidMessage) readResponse.getEntity();
        assertEquals(orcid, retrievedMessage.getOrcidProfile().getOrcidIdentifier().getPath());
        GivenNames givenNames = retrievedMessage.getOrcidProfile().getOrcidBio().getPersonalDetails().getGivenNames();
        assertNotNull(givenNames);
        assertEquals("Reserved For Claim", givenNames.getContent());
    }

    @Test
    public void testCreateWithAffiliations() throws JAXBException {
        setUpSecurityContextForClientOnly();
        OrcidMessage orcidMessage = getOrcidMessage("/orcid-message-for-create-latest.xml");

        Response createResponse = t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);

        assertNotNull(createResponse);
        assertEquals(HttpStatus.SC_CREATED, createResponse.getStatus());
        String location = ((URI) createResponse.getMetadata().getFirst("Location")).getPath();
        assertNotNull(location);
        String orcid = location.substring(1, 20);

        Response readResponse = t2OrcidApiServiceDelegatorLatest.findFullDetails(orcid);
        assertNotNull(readResponse);
        assertEquals(HttpStatus.SC_OK, readResponse.getStatus());
        OrcidMessage retrievedMessage = (OrcidMessage) readResponse.getEntity();
        OrcidProfile orcidProfile = retrievedMessage.getOrcidProfile();
        assertEquals(orcid, orcidProfile.getOrcidIdentifier().getPath());

        Affiliations affiliations = orcidProfile.retrieveAffiliations();
        assertNotNull(affiliations);
        assertEquals(1, affiliations.getAffiliation().size());

        Affiliation affiliation = affiliations.getAffiliation().get(0);
        assertEquals(Visibility.PRIVATE, affiliation.getVisibility());

        Source source = affiliation.getSource();
        assertNotNull(source);
        String sourceOrcid = source.retrieveSourcePath();
        assertNotNull(sourceOrcid);
        assertEquals("4444-4444-4444-4445", sourceOrcid);
    }

    private OrcidMessage getOrcidMessage(String orcidMessagePath) throws JAXBException {
        return (OrcidMessage) unmarshaller.unmarshal(getClass().getResourceAsStream(orcidMessagePath));
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

    private void setUpSecurityContext() {
        setUpSecurityContext("4444-4444-4444-4441");
    }
    
    private void setUpSecurityContext(String userOrcid) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        OrcidOAuth2Authentication mockedAuthentication = mock(OrcidOAuth2Authentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        when(mockedAuthentication.getPrincipal()).thenReturn(new ProfileEntity(userOrcid));
        Set<String> scopes = new HashSet<String>();
        scopes.add(ScopePathType.ACTIVITIES_UPDATE.value());
        scopes.add(ScopePathType.READ_LIMITED.value());
        OAuth2Request authorizationRequest = new OAuth2Request(Collections.<String, String> emptyMap(), userOrcid,
                Collections.<GrantedAuthority> emptyList(), true, scopes, Collections.<String> emptySet(), null, Collections.<String> emptySet(),
                Collections.<String, Serializable> emptyMap());                
        when(mockedAuthentication.getOAuth2Request()).thenReturn(authorizationRequest);
    }

    private void setUpSecurityContextForClientOnly() {
        setUpSecurityContextForClientOnly("4444-4444-4444-4445");
    }

    private void setUpSecurityContextForClientOnly(String clientId) {
        Set<String> scopes = new HashSet<String>();
        scopes.add(ScopePathType.ORCID_PROFILE_CREATE.value());
        setUpSecurityContextForClientOnly(clientId, scopes);
    }

    private void setUpSecurityContextForClientOnly(String clientId, Set<String> scopes) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        OrcidOAuth2Authentication mockedAuthentication = mock(OrcidOAuth2Authentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        when(mockedAuthentication.getPrincipal()).thenReturn(new ProfileEntity(clientId));
        when(mockedAuthentication.isClientOnly()).thenReturn(true);
        OAuth2Request authorizationRequest = new OAuth2Request(Collections.<String, String> emptyMap(), clientId,
                Collections.<GrantedAuthority> emptyList(), true, scopes, Collections.<String> emptySet(), null, Collections.<String> emptySet(),
                Collections.<String, Serializable> emptyMap());
        when(mockedAuthentication.getOAuth2Request()).thenReturn(authorizationRequest);
    }

    @Test
    public void testRegisterAndUnregisterWebhook() {
        Set<String> scopes = new HashSet<String>();
        scopes.add(ScopePathType.WEBHOOK.value());
        setUpSecurityContextForClientOnly("4444-4444-4444-4445", scopes);
        Response response = t2OrcidApiServiceDelegatorLatest.registerWebhook(mockedUriInfo, "4444-4444-4444-4447", "www.webhook.com");
        assertNotNull(response);
        assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        response = t2OrcidApiServiceDelegatorLatest.unregisterWebhook(mockedUriInfo, "4444-4444-4444-4447", "www.webhook.com");
        assertNotNull(response);
        assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatus());
    }

    @Test
    public void testCreateProfileWithInvalidHistoryElements() {
        OrcidMessage orcidMessage = createStubOrcidMessage();
        orcidMessage.setMessageVersion("1.2");        
        //Claimed should be null
        try {
            OrcidHistory history = new OrcidHistory();
            history.setClaimed(new Claimed(false));
            orcidMessage.getOrcidProfile().setOrcidHistory(history);
            t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);
        } catch(OrcidValidationException obe) {
            assertTrue(obe.getMessage().contains("Claimed status should not be specified when creating a profile"));
        }
        
        //Creation method should be null
        try {
            OrcidHistory history = new OrcidHistory();
            history.setCreationMethod(CreationMethod.API);
            orcidMessage.getOrcidProfile().setOrcidHistory(history);
            t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);
        } catch(OrcidValidationException obe) {
            assertTrue(obe.getMessage().contains("Creation method should not be specified when creating a profile"));
        }
        
        //Completion date should be null
        try {
            OrcidHistory history = new OrcidHistory();
            history.setCompletionDate(new CompletionDate());
            orcidMessage.getOrcidProfile().setOrcidHistory(history);
            t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);
        } catch(OrcidValidationException obe) {
            assertTrue(obe.getMessage().contains("Completion date should not be specified when creating a profile"));
        }
        
        //Submission date should be null
        try {
            OrcidHistory history = new OrcidHistory();
            history.setSubmissionDate(new SubmissionDate());
            orcidMessage.getOrcidProfile().setOrcidHistory(history);
            t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);
        } catch(OrcidValidationException obe) {
            assertTrue(obe.getMessage().contains("Submission date should not be specified when creating a profile"));
        }
        
        //Last modified date should be null
        try {
            OrcidHistory history = new OrcidHistory();
            history.setLastModifiedDate(new LastModifiedDate());
            orcidMessage.getOrcidProfile().setOrcidHistory(history);
            t2OrcidApiServiceDelegatorLatest.createProfile(mockedUriInfo, orcidMessage);
        } catch(OrcidValidationException obe) {
            assertTrue(obe.getMessage().contains("Last modified date should not be specified when creating a profile"));
        }
    }
    
    @Test
    public void addAffiliationTest() {
        setUpSecurityContext();
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4441"));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        
        Affiliations affiliations = new Affiliations();
        
        Affiliation affiliation = new Affiliation();
        affiliation.setStartDate(new FuzzyDate(2010, 01, 01));
        affiliation.setEndDate(new FuzzyDate(2015, 01, 01));
        affiliation.setDepartmentName("Dep Name");        
        affiliation.setRoleTitle("Role Title");
        affiliation.setType(AffiliationType.EDUCATION);
        Organization organization = new Organization();
        organization.setName("My Org");
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("My City");
        add.setCountry(Iso3166Country.US);       
        organization.setAddress(add);
        affiliation.setOrganization(organization);
        
        affiliations.getAffiliation().add(affiliation);
        orcidActivities.setAffiliations(affiliations);
        
        Response response = t2OrcidApiServiceDelegatorLatest.addAffiliations(mockedUriInfo, "4444-4444-4444-4441", orcidMessage);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(1, orgAffiliationDao.getByUserAndType("4444-4444-4444-4441", org.orcid.jaxb.model.message.AffiliationType.EDUCATION).size());
    }      
    
    @Test
    public void preventDuplicatedAffiliationsTest() {
        setUpSecurityContext("4444-4444-4444-4446");
        OrcidMessage orcidMessage = buildMessageWithAffiliation(AffiliationType.EDUCATION, "My dept", "My Role", "4444-4444-4444-4446");
        
        Response response = t2OrcidApiServiceDelegatorLatest.addAffiliations(mockedUriInfo, "4444-4444-4444-4446", orcidMessage);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());                       
        assertEquals(4, orgAffiliationDao.getByUserAndType("4444-4444-4444-4446", org.orcid.jaxb.model.message.AffiliationType.EDUCATION).size());        
        
        orcidMessage = buildMessageWithAffiliation(AffiliationType.EDUCATION, "My dept", "My Role", "4444-4444-4444-4446");
        response = t2OrcidApiServiceDelegatorLatest.addAffiliations(mockedUriInfo, "4444-4444-4444-4446", orcidMessage);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        
        assertEquals(4, orgAffiliationDao.getByUserAndType("4444-4444-4444-4446", org.orcid.jaxb.model.message.AffiliationType.EDUCATION).size());               
    }
    
    @Test
    public void preventDuplicatedAffiliations2Test() {
        setUpSecurityContext("4444-4444-4444-4499");
        OrcidMessage orcidMessage = buildMessageWithAffiliation(AffiliationType.EDUCATION, "My dept", "My Role", "4444-4444-4444-4499");
        
        //Set an existing organization, but, with a bad disambiguated org 
        Organization organization = new Organization();
        organization.setName("An institution");
        OrganizationAddress orgAdd = new OrganizationAddress();
        orgAdd.setCity("London");
        orgAdd.setCountry(Iso3166Country.GB);                
        DisambiguatedOrganization dorg = new DisambiguatedOrganization();
        dorg.setDisambiguatedOrganizationIdentifier("XXX");
        dorg.setDisambiguationSource("123456");                
        organization.setAddress(orgAdd);
        organization.setDisambiguatedOrganization(dorg);        
        orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().get(0).setOrganization(organization);
                
        Response response = t2OrcidApiServiceDelegatorLatest.addAffiliations(mockedUriInfo, "4444-4444-4444-4499", orcidMessage);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());                       
        assertEquals(1, orgAffiliationDao.getByUserAndType("4444-4444-4444-4499", org.orcid.jaxb.model.message.AffiliationType.EDUCATION).size());        
        
        orcidMessage = buildMessageWithAffiliation(AffiliationType.EDUCATION, "My dept", "My Role", "4444-4444-4444-4499");
        
        //Set an existing organization, but, with a bad disambiguated org 
        organization = new Organization();
        organization.setName("An institution");
        orgAdd = new OrganizationAddress();
        orgAdd.setCity("London");
        orgAdd.setCountry(Iso3166Country.GB);                
        dorg = new DisambiguatedOrganization();
        dorg.setDisambiguatedOrganizationIdentifier("YYY");
        dorg.setDisambiguationSource("654321");                
        organization.setAddress(orgAdd);
        organization.setDisambiguatedOrganization(dorg);        
        orcidMessage.getOrcidProfile().getOrcidActivities().getAffiliations().getAffiliation().get(0).setOrganization(organization);
        
        response = t2OrcidApiServiceDelegatorLatest.addAffiliations(mockedUriInfo, "4444-4444-4444-4499", orcidMessage);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        
        assertEquals(1, orgAffiliationDao.getByUserAndType("4444-4444-4444-4499", org.orcid.jaxb.model.message.AffiliationType.EDUCATION).size());    
        
        OrgAffiliationRelationEntity orgEntity = orgAffiliationDao.getByUserAndType("4444-4444-4444-4499", org.orcid.jaxb.model.message.AffiliationType.EDUCATION).get(0);
        assertNotNull(orgEntity);
        assertNotNull(orgEntity.getOrg());
        assertEquals("An institution", orgEntity.getOrg().getName());
        assertEquals("London", orgEntity.getOrg().getCity());
        assertEquals(Iso3166Country.GB, orgEntity.getOrg().getCountry());
        assertEquals(Long.valueOf(1), orgEntity.getOrg().getId());
        assertNotNull(orgEntity.getOrg().getOrgDisambiguated());
        assertEquals("London", orgEntity.getOrg().getOrgDisambiguated().getCity());
        assertEquals(Iso3166Country.GB, orgEntity.getOrg().getOrgDisambiguated().getCountry());
        assertEquals(Long.valueOf(1), orgEntity.getOrg().getOrgDisambiguated().getId());
    }
    
    private OrcidMessage buildMessageWithAffiliation(AffiliationType type, String dept, String role, String orcid) {
        OrcidMessage orcidMessage = new OrcidMessage();
        orcidMessage.setMessageVersion("1.2_rc6");
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidMessage.setOrcidProfile(orcidProfile);
        orcidProfile.setOrcidIdentifier(new OrcidIdentifier(orcid));
        OrcidActivities orcidActivities = new OrcidActivities();
        orcidProfile.setOrcidActivities(orcidActivities);
        
        Affiliations affiliations = new Affiliations();
        
        Affiliation affiliation = new Affiliation();
        affiliation.setStartDate(new FuzzyDate(2010, 01, 01));
        affiliation.setEndDate(new FuzzyDate(2015, 01, 01));
        affiliation.setDepartmentName(dept);        
        affiliation.setRoleTitle(role);
        affiliation.setType(type);
        Organization organization = new Organization();
        organization.setName("My Org");
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("My City");
        add.setCountry(Iso3166Country.US);       
        organization.setAddress(add);
        DisambiguatedOrganization dorg = new DisambiguatedOrganization();
        dorg.setDisambiguatedOrganizationIdentifier("disambiguated org ID");
        dorg.setDisambiguationSource("THESOURCE");
        organization.setDisambiguatedOrganization(dorg);
        affiliation.setOrganization(organization);
        
        affiliations.getAffiliation().add(affiliation);
        orcidActivities.setAffiliations(affiliations);
        return orcidMessage;
    }
}
