package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.OrganizationAddress;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceName;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.PublicRecordPersonDetails;
import org.orcid.pojo.ajaxForm.AffiliationGroupContainer;
import org.orcid.pojo.ajaxForm.AffiliationGroupForm;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.MethodNotAllowedException;

public class PublicProfileControllerTest {

    private static final String BASE_URL = "https://testserver.orcid.org";
    private static final String USER_ORCID = "0000-0000-0000-0003";
    private static final String DEPRECATED_USER_ORCID = "0000-0000-0000-0004";
    private static final String LOCKED_USER_ORCID = "0000-0000-0000-0006";
    private static final String REVIEWED_NO_INTEGRATIONS_ORCID = "0009-0000-0000-0001";
    private static final String REVIEWED_WITH_INTEGRATIONS_ORCID = "0009-0000-0000-0002";
    private static final String UNREVIEWED_NO_INTEGRATIONS_ORCID = "0009-0000-0000-0003";
    private static final String UNREVIEWED_WITH_INTEGRATIONS_ORCID = "0009-0000-0000-0004";
    private static final String UNREVIEWED_CREATED_BY_MEMBERS_WITH_ACTIVITIES_ORCID = "0009-0000-0000-0005";
    private static final String UNREVIEWED_CREATED_BY_MEMBERS_WITH_NO_ACTIVITIES_ORCID = "0009-0000-0000-0006";
    private static final String PRIMARY_RECORD = "0000-0000-0000-0000";
    private static final String PUBLIC_LAYOUT_TITLE = "ORCID | Connecting Research and Researchers";
    private static final String UNITED_STATES = "United States";

    private PublicProfileController publicProfileController;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private OrcidUrlManager orcidUrlManager;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Mock
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Mock
    private PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Mock
    private AddressManagerReadOnly addressManagerReadOnly;

    @Mock
    private ProfileKeywordManagerReadOnly profileKeywordManagerReadOnly;

    @Mock
    private ResearcherUrlManagerReadOnly researcherUrlManagerReadOnly;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        publicProfileController = new PublicProfileController();

        inject(BaseController.class, "localeManager", localeManager);
        inject(BaseController.class, "orcidUrlManager", orcidUrlManager);
        inject(BaseController.class, "orcidSecurityManager", orcidSecurityManager);
        inject(BaseController.class, "profileEntityManager", profileEntityManager);
        inject(BaseController.class, "personalDetailsManagerReadOnly", personalDetailsManagerReadOnly);
        inject(BaseController.class, "addressManagerReadOnly", addressManagerReadOnly);
        inject(BaseController.class, "keywordManagerReadOnly", profileKeywordManagerReadOnly);
        inject(BaseController.class, "researcherUrlManagerReadOnly", researcherUrlManagerReadOnly);
        inject(BaseController.class, "emailManagerReadOnly", emailManagerReadOnly);
        inject(BaseController.class, "externalIdentifierManagerReadOnly", externalIdentifierManagerReadOnly);
        inject(BaseWorkspaceController.class, "profileEntityManager", profileEntityManager);

        inject(PublicProfileController.class, "profileEntityCacheManager", profileEntityCacheManager);
        inject(PublicProfileController.class, "profileEntityManagerReadOnly", profileEntityManagerReadOnly);
        inject(PublicProfileController.class, "affiliationsManagerReadOnly", affiliationsManagerReadOnly);
        inject(PublicProfileController.class, "personalDetailsManagerReadOnly", personalDetailsManagerReadOnly);

        when(orcidUrlManager.getBaseUrl()).thenReturn(BASE_URL);
        when(localeManager.resolveMessage(eq("layout.public-layout.title"), any(Object[].class))).thenReturn(PUBLIC_LAYOUT_TITLE);
        when(localeManager.getLocale()).thenReturn(Locale.US);
        Map<String, String> countries = new HashMap<>();
        countries.put("US", UNITED_STATES);
        when(localeManager.getCountries(Locale.US)).thenReturn(countries);
        when(profileEntityManager.getLastModified(anyString())).thenReturn(1L);
    }

    @Test
    public void testGetPersonDetails() {
        PublicProfileController spyController = spy(publicProfileController);
        PublicRecordPersonDetails expected = createExpectedPersonDetails();
        doReturn(expected).when(spyController).getPersonDetails(USER_ORCID, true);
        when(profileEntityCacheManager.retrieve(USER_ORCID)).thenReturn(new ProfileEntity(USER_ORCID));

        PublicRecordPersonDetails personDetails = spyController.getPersonDetails(USER_ORCID);

        assertNotNull(personDetails.getBiography());
        assertEquals("Biography for 0000-0000-0000-0003", personDetails.getBiography().getContent());
        assertEquals("Credit Name", personDetails.getDisplayName());
        assertEquals("Credit Name (0000-0000-0000-0003) - " + PUBLIC_LAYOUT_TITLE, personDetails.getTitle());

        Map<String, List<OtherName>> groupedOtherNames = personDetails.getPublicGroupedOtherNames();
        assertNotNull(groupedOtherNames);
        assertEquals(1, groupedOtherNames.size());
        assertTrue(groupedOtherNames.containsKey("Other Name PUBLIC"));
        List<OtherName> publicOtherNames = groupedOtherNames.get("Other Name PUBLIC");
        assertEquals(1, publicOtherNames.size());
        assertEquals(Long.valueOf(13), publicOtherNames.get(0).getPutCode());
        assertEquals("Other Name PUBLIC", publicOtherNames.get(0).getContent());
        assertEquals(Visibility.PUBLIC, publicOtherNames.get(0).getVisibility());

        Map<String, List<Address>> groupedAddresses = personDetails.getPublicGroupedAddresses();
        assertNotNull(groupedAddresses);
        assertEquals(1, groupedAddresses.size());
        assertTrue(groupedAddresses.containsKey("US"));
        List<Address> publicAddresses = groupedAddresses.get("US");
        assertEquals(1, publicAddresses.size());
        assertEquals(Long.valueOf(9), publicAddresses.get(0).getPutCode());
        assertEquals(Iso3166Country.US, publicAddresses.get(0).getCountry().getValue());
        assertEquals(Visibility.PUBLIC, publicAddresses.get(0).getVisibility());

        Map<String, String> expectedCountry = new HashMap<>();
        expectedCountry.put("US", UNITED_STATES);
        assertEquals(expectedCountry, personDetails.getCountryNames());

        Map<String, List<Keyword>> groupedKeywords = personDetails.getPublicGroupedKeywords();
        assertNotNull(groupedKeywords);
        assertEquals(1, groupedKeywords.size());
        assertTrue(groupedKeywords.containsKey("PUBLIC"));
        List<Keyword> publicKeywords = groupedKeywords.get("PUBLIC");
        assertEquals(1, publicKeywords.size());
        assertEquals(Long.valueOf(9), publicKeywords.get(0).getPutCode());
        assertEquals("PUBLIC", publicKeywords.get(0).getContent());
        assertEquals(Visibility.PUBLIC, publicKeywords.get(0).getVisibility());

        Map<String, List<ResearcherUrl>> groupedResearcherUrls = personDetails.getPublicGroupedResearcherUrls();
        assertNotNull(groupedResearcherUrls);
        assertEquals(1, groupedResearcherUrls.size());
        assertTrue(groupedResearcherUrls.containsKey("http://www.researcherurl.com?id=13"));
        List<ResearcherUrl> publicResearcherUrls = groupedResearcherUrls.get("http://www.researcherurl.com?id=13");
        assertEquals(Long.valueOf(13), publicResearcherUrls.get(0).getPutCode());
        assertEquals("public_rurl", publicResearcherUrls.get(0).getUrlName());
        assertEquals(Visibility.PUBLIC, publicResearcherUrls.get(0).getVisibility());

        Map<String, List<Email>> emails = personDetails.getPublicGroupedEmails();
        assertNotNull(emails);
        assertEquals(2, emails.size());
        assertTrue(emails.containsKey("public_0000-0000-0000-0003@test.orcid.org"));
        assertTrue(emails.containsKey("public_0000-0000-0000-0003@orcid.org"));
        List<Email> email1 = emails.get("public_0000-0000-0000-0003@test.orcid.org");
        List<Email> email2 = emails.get("public_0000-0000-0000-0003@orcid.org");
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email1.get(0).getEmail());
        assertEquals("public_0000-0000-0000-0003@orcid.org", email2.get(0).getEmail());
        assertEquals(Visibility.PUBLIC, email1.get(0).getVisibility());
        assertEquals(Visibility.PUBLIC, email2.get(0).getVisibility());
        assertEquals("APP-5555555555555555", email1.get(0).getSource().retrieveSourcePath());
        assertEquals("Source Client 1", email1.get(0).getSource().getSourceName().getContent());
        assertNull(email1.get(0).getSource().getSourceOrcid());
        assertEquals(USER_ORCID, email2.get(0).getSource().retrieveSourcePath());
        assertEquals("Credit Name", email2.get(0).getSource().getSourceName().getContent());
        assertNotNull(email2.get(0).getSource().getSourceOrcid());

        Map<String, List<PersonExternalIdentifier>> externalIdentifiers = personDetails.getPublicGroupedPersonExternalIdentifiers();
        assertNotNull(externalIdentifiers);
        assertEquals(3, externalIdentifiers.size());
        assertTrue(externalIdentifiers.containsKey("public_type:public_ref"));
        assertTrue(externalIdentifiers.containsKey("self_public_type:self_public_ref"));
        assertTrue(externalIdentifiers.containsKey("self_public_user_obo_type:self_public_user_obo_ref"));

        List<PersonExternalIdentifier> publicExternalIdentifiers = externalIdentifiers.get("public_type:public_ref");
        assertEquals(Long.valueOf(13), publicExternalIdentifiers.get(0).getPutCode());
        assertEquals("http://ext-id/public_ref", publicExternalIdentifiers.get(0).getUrl().getValue());
        assertEquals(Visibility.PUBLIC, publicExternalIdentifiers.get(0).getVisibility());

        publicExternalIdentifiers = externalIdentifiers.get("self_public_type:self_public_ref");
        assertEquals(Long.valueOf(18), publicExternalIdentifiers.get(0).getPutCode());
        assertEquals("http://ext-id/self/public", publicExternalIdentifiers.get(0).getUrl().getValue());
        assertEquals(Visibility.PUBLIC, publicExternalIdentifiers.get(0).getVisibility());

        publicExternalIdentifiers = externalIdentifiers.get("self_public_user_obo_type:self_public_user_obo_ref");
        assertEquals(Long.valueOf(19), publicExternalIdentifiers.get(0).getPutCode());
        assertEquals("http://ext-id/self/obo/public", publicExternalIdentifiers.get(0).getUrl().getValue());
        assertEquals(Visibility.PUBLIC, publicExternalIdentifiers.get(0).getVisibility());
    }

    @Test
    public void testGetGroupedAffiliations() {
        when(affiliationsManagerReadOnly.getGroupedAffiliations(USER_ORCID, true)).thenReturn(createGroupedAffiliations());

        AffiliationGroupContainer container = publicProfileController.getGroupedAffiliations(USER_ORCID);
        Map<AffiliationType, List<AffiliationGroupForm>> map = container.getAffiliationGroups();

        boolean distinctions = false;
        boolean invitedPositions = false;
        boolean educations = false;
        boolean memberships = false;
        boolean employments = false;
        boolean qualifications = false;
        boolean services = false;

        for (AffiliationType type : map.keySet()) {
            List<AffiliationGroupForm> elements = map.get(type);
            assertEquals(1, elements.size());
            assertEquals(Visibility.PUBLIC.name(), elements.get(0).getActiveVisibility());
            assertEquals(Visibility.PUBLIC.name(), elements.get(0).getAffiliations().get(0).getVisibility().getVisibility().name());
            Long activePutCode = elements.get(0).getActivePutCode();
            Long elementPutCode = Long.valueOf(elements.get(0).getAffiliations().get(0).getPutCode().getValue());
            assertEquals(activePutCode, elementPutCode);
            switch (type) {
            case DISTINCTION:
                distinctions = true;
                assertEquals(Long.valueOf(27), activePutCode);
                break;
            case EDUCATION:
                educations = true;
                assertEquals(Long.valueOf(20), activePutCode);
                break;
            case EMPLOYMENT:
                employments = true;
                assertEquals(Long.valueOf(17), activePutCode);
                break;
            case INVITED_POSITION:
                invitedPositions = true;
                assertEquals(Long.valueOf(32), activePutCode);
                break;
            case MEMBERSHIP:
                memberships = true;
                assertEquals(Long.valueOf(37), activePutCode);
                break;
            case QUALIFICATION:
                qualifications = true;
                assertEquals(Long.valueOf(42), activePutCode);
                break;
            case SERVICE:
                services = true;
                assertEquals(Long.valueOf(47), activePutCode);
                break;
            }
        }

        assertTrue(distinctions);
        assertTrue(educations);
        assertTrue(employments);
        assertTrue(invitedPositions);
        assertTrue(memberships);
        assertTrue(qualifications);
        assertTrue(services);
    }

    @Test
    public void getUserInfoTest() {
        setupUserInfoMocks();

        Map<String, String> map1 = publicProfileController.getUserInfo(UNREVIEWED_NO_INTEGRATIONS_ORCID);
        assertEquals(UNREVIEWED_NO_INTEGRATIONS_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("false", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("false", map1.get("READY_FOR_INDEXING"));
        assertFalse(map1.containsKey("PRIMARY_RECORD"));

        map1 = publicProfileController.getUserInfo(UNREVIEWED_WITH_INTEGRATIONS_ORCID);
        assertEquals(UNREVIEWED_WITH_INTEGRATIONS_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("false", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("true", map1.get("READY_FOR_INDEXING"));
        assertFalse(map1.containsKey("PRIMARY_RECORD"));

        map1 = publicProfileController.getUserInfo(UNREVIEWED_CREATED_BY_MEMBERS_WITH_NO_ACTIVITIES_ORCID);
        assertEquals(UNREVIEWED_CREATED_BY_MEMBERS_WITH_NO_ACTIVITIES_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("false", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("false", map1.get("READY_FOR_INDEXING"));
        assertFalse(map1.containsKey("PRIMARY_RECORD"));

        map1 = publicProfileController.getUserInfo(UNREVIEWED_CREATED_BY_MEMBERS_WITH_ACTIVITIES_ORCID);
        assertEquals(UNREVIEWED_CREATED_BY_MEMBERS_WITH_ACTIVITIES_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("false", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("true", map1.get("READY_FOR_INDEXING"));
        assertFalse(map1.containsKey("PRIMARY_RECORD"));

        map1 = publicProfileController.getUserInfo(REVIEWED_NO_INTEGRATIONS_ORCID);
        assertEquals(REVIEWED_NO_INTEGRATIONS_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("false", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("true", map1.get("READY_FOR_INDEXING"));
        assertFalse(map1.containsKey("PRIMARY_RECORD"));

        map1 = publicProfileController.getUserInfo(REVIEWED_WITH_INTEGRATIONS_ORCID);
        assertEquals(REVIEWED_WITH_INTEGRATIONS_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("false", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("true", map1.get("READY_FOR_INDEXING"));
        assertFalse(map1.containsKey("PRIMARY_RECORD"));

        map1 = publicProfileController.getUserInfo(DEPRECATED_USER_ORCID);
        assertEquals(DEPRECATED_USER_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("false", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("false", map1.get("READY_FOR_INDEXING"));
        assertEquals(PRIMARY_RECORD, map1.get("PRIMARY_RECORD"));

        map1 = publicProfileController.getUserInfo(LOCKED_USER_ORCID);
        assertEquals(LOCKED_USER_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("true", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("false", map1.get("READY_FOR_INDEXING"));
        assertFalse(map1.containsKey("PRIMARY_RECORD"));

        map1 = publicProfileController.getUserInfo(USER_ORCID);
        assertEquals(USER_ORCID, map1.get("EFFECTIVE_USER_ORCID"));
        assertEquals("false", map1.get("IS_LOCKED"));
        assertEquals("false", map1.get("IS_DEACTIVATED"));
        assertEquals("true", map1.get("READY_FOR_INDEXING"));
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
    }

    @Test
    public void ifModifiedSinceCheckEndpoint_returns304WhenRecordNotModified() throws IOException {
        long lastModifiedTime = 1735689600000L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader("If-Modified-Since", formatHttpDate(lastModifiedTime + 60_000L));
        when(profileEntityManager.getLastModified(USER_ORCID)).thenReturn(lastModifiedTime);

        publicProfileController.ifModifiedSinceCheckEndpoint(request, response, USER_ORCID);

        assertEquals(HttpServletResponse.SC_NOT_MODIFIED, response.getStatus());
    }

    @Test
    public void ifModifiedSinceCheckEndpoint_returns200WhenRecordWasModified() throws IOException {
        long lastModifiedTime = 1735689600000L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader("If-Modified-Since", formatHttpDate(lastModifiedTime - 86_400_000L));
        when(profileEntityManager.getLastModified(USER_ORCID)).thenReturn(lastModifiedTime);

        publicProfileController.ifModifiedSinceCheckEndpoint(request, response, USER_ORCID);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void ifModifiedSinceCheckEndpoint_returns200WhenHeaderIsMissing() throws IOException {
        long lastModifiedTime = 1735689600000L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setMethod("GET");
        when(profileEntityManager.getLastModified(USER_ORCID)).thenReturn(lastModifiedTime);

        publicProfileController.ifModifiedSinceCheckEndpoint(request, response, USER_ORCID);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void ifModifiedSinceCheckEndpoint_returns307WhenRecordIsMissing() throws IOException {
        long ifModifiedSince = 1735689600000L;
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setMethod("GET");
        request.addHeader("If-Modified-Since", formatHttpDate(ifModifiedSince));
        when(profileEntityManager.getLastModified(USER_ORCID)).thenReturn(0L);

        publicProfileController.ifModifiedSinceCheckEndpoint(request, response, USER_ORCID);

        assertEquals(HttpServletResponse.SC_TEMPORARY_REDIRECT, response.getStatus());
        assertEquals(BASE_URL + "/404", response.getHeader("Location"));
    }

    @Test
    public void publicPreview_getInvalidRecordReturnsServiceUnavailableTest() throws IOException {
        String a = "0000-0000-0000-0000";
        String b = "0000-0000-0000-0000-0000";
        String c = " 0000-0000-0000-0000";
        String d = "0000-0000-0000-0000 ";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader("If-Modified-Since", formatHttpDate(1735689600000L));

        when(profileEntityManager.getLastModified(a)).thenThrow(new IllegalArgumentException());
        when(profileEntityManager.getLastModified(b)).thenThrow(new IllegalArgumentException());
        when(profileEntityManager.getLastModified(c)).thenThrow(new IllegalArgumentException());
        when(profileEntityManager.getLastModified(d)).thenThrow(new IllegalArgumentException());

        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        publicProfileController.ifModifiedSinceCheckEndpoint(request, firstResponse, a);
        assertTrue(firstResponse.containsHeader("Location"));
        assertEquals(HttpServletResponse.SC_SERVICE_UNAVAILABLE, firstResponse.getStatus());
        assertEquals(BASE_URL + "/404", firstResponse.getHeader("Location"));

        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        publicProfileController.ifModifiedSinceCheckEndpoint(request, secondResponse, b);
        assertTrue(secondResponse.containsHeader("Location"));
        assertEquals(HttpServletResponse.SC_SERVICE_UNAVAILABLE, secondResponse.getStatus());
        assertEquals(BASE_URL + "/404", secondResponse.getHeader("Location"));

        MockHttpServletResponse thirdResponse = new MockHttpServletResponse();
        publicProfileController.ifModifiedSinceCheckEndpoint(request, thirdResponse, c);
        assertTrue(thirdResponse.containsHeader("Location"));
        assertEquals(HttpServletResponse.SC_SERVICE_UNAVAILABLE, thirdResponse.getStatus());
        assertEquals(BASE_URL + "/404", thirdResponse.getHeader("Location"));

        MockHttpServletResponse fourthResponse = new MockHttpServletResponse();
        publicProfileController.ifModifiedSinceCheckEndpoint(request, fourthResponse, d);
        assertTrue(fourthResponse.containsHeader("Location"));
        assertEquals(HttpServletResponse.SC_SERVICE_UNAVAILABLE, fourthResponse.getStatus());
        assertEquals(BASE_URL + "/404", fourthResponse.getHeader("Location"));
    }

    private void setupUserInfoMocks() {
        SourceEntity sourceEntity = new SourceEntity(new ClientDetailsEntity("APP-000000000001"));

        when(profileEntityManagerReadOnly.haveMemberPushedWorksOrAffiliationsToRecord(anyString(), anyString())).thenThrow(MethodNotAllowedException.class);
        when(profileEntityManagerReadOnly.haveMemberPushedWorksOrAffiliationsToRecord(eq(UNREVIEWED_CREATED_BY_MEMBERS_WITH_ACTIVITIES_ORCID), anyString()))
                .thenReturn(true);
        when(profileEntityManagerReadOnly.haveMemberPushedWorksOrAffiliationsToRecord(eq(UNREVIEWED_CREATED_BY_MEMBERS_WITH_NO_ACTIVITIES_ORCID), anyString()))
                .thenReturn(false);

        ProfileEntity reviewedNoIntegrations = createProfile(REVIEWED_NO_INTEGRATIONS_ORCID, true, false, null, null);
        when(profileEntityManagerReadOnly.hasToken(eq(REVIEWED_NO_INTEGRATIONS_ORCID), anyLong())).thenReturn(false);
        when(profileEntityCacheManager.retrieve(REVIEWED_NO_INTEGRATIONS_ORCID)).thenReturn(reviewedNoIntegrations);

        ProfileEntity reviewedWithIntegrations = createProfile(REVIEWED_WITH_INTEGRATIONS_ORCID, true, false, null, null);
        when(profileEntityManagerReadOnly.hasToken(eq(REVIEWED_WITH_INTEGRATIONS_ORCID), anyLong())).thenReturn(true);
        when(profileEntityCacheManager.retrieve(REVIEWED_WITH_INTEGRATIONS_ORCID)).thenReturn(reviewedWithIntegrations);

        ProfileEntity unreviewedNoIntegrations = createProfile(UNREVIEWED_NO_INTEGRATIONS_ORCID, false, false, null, null);
        when(profileEntityManagerReadOnly.hasToken(eq(UNREVIEWED_NO_INTEGRATIONS_ORCID), anyLong())).thenReturn(false);
        when(profileEntityCacheManager.retrieve(UNREVIEWED_NO_INTEGRATIONS_ORCID)).thenReturn(unreviewedNoIntegrations);

        ProfileEntity unreviewedWithIntegrations = createProfile(UNREVIEWED_WITH_INTEGRATIONS_ORCID, false, false, null, null);
        when(profileEntityManagerReadOnly.hasToken(eq(UNREVIEWED_WITH_INTEGRATIONS_ORCID), anyLong())).thenReturn(true);
        when(profileEntityCacheManager.retrieve(UNREVIEWED_WITH_INTEGRATIONS_ORCID)).thenReturn(unreviewedWithIntegrations);

        ProfileEntity unreviewedCreatedByMembersWithNoActivities =
                createProfile(UNREVIEWED_CREATED_BY_MEMBERS_WITH_NO_ACTIVITIES_ORCID, false, false, null, sourceEntity);
        when(profileEntityManagerReadOnly.hasToken(eq(UNREVIEWED_CREATED_BY_MEMBERS_WITH_NO_ACTIVITIES_ORCID), anyLong())).thenReturn(false);
        when(profileEntityCacheManager.retrieve(UNREVIEWED_CREATED_BY_MEMBERS_WITH_NO_ACTIVITIES_ORCID))
                .thenReturn(unreviewedCreatedByMembersWithNoActivities);

        ProfileEntity unreviewedCreatedByMembersWithActivities =
                createProfile(UNREVIEWED_CREATED_BY_MEMBERS_WITH_ACTIVITIES_ORCID, false, false, null, sourceEntity);
        when(profileEntityManagerReadOnly.hasToken(eq(UNREVIEWED_CREATED_BY_MEMBERS_WITH_ACTIVITIES_ORCID), anyLong())).thenReturn(false);
        when(profileEntityCacheManager.retrieve(UNREVIEWED_CREATED_BY_MEMBERS_WITH_ACTIVITIES_ORCID))
                .thenReturn(unreviewedCreatedByMembersWithActivities);

        ProfileEntity deprecated = createProfile(DEPRECATED_USER_ORCID, true, false, PRIMARY_RECORD, null);
        when(profileEntityManagerReadOnly.hasToken(eq(DEPRECATED_USER_ORCID), anyLong())).thenReturn(true);
        when(profileEntityCacheManager.retrieve(DEPRECATED_USER_ORCID)).thenReturn(deprecated);

        ProfileEntity locked = createProfile(LOCKED_USER_ORCID, true, true, null, null);
        when(profileEntityManagerReadOnly.hasToken(eq(LOCKED_USER_ORCID), anyLong())).thenReturn(true);
        when(profileEntityCacheManager.retrieve(LOCKED_USER_ORCID)).thenReturn(locked);

        ProfileEntity allOk = createProfile(USER_ORCID, true, false, null, null);
        when(profileEntityManagerReadOnly.hasToken(eq(USER_ORCID), anyLong())).thenReturn(true);
        when(profileEntityCacheManager.retrieve(USER_ORCID)).thenReturn(allOk);
    }

    private ProfileEntity createProfile(String orcid, boolean reviewed, boolean locked, String primaryRecordOrcid, SourceEntity source) {
        ProfileEntity profile = new ProfileEntity(orcid);
        profile.setReviewed(reviewed);
        profile.setRecordLocked(locked);
        profile.setSource(source);
        if (primaryRecordOrcid != null) {
            profile.setPrimaryRecord(new ProfileEntity(primaryRecordOrcid));
        }
        return profile;
    }

    private PersonalDetails createPublicPersonalDetails() {
        PersonalDetails personalDetails = new PersonalDetails();

        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setVisibility(Visibility.PUBLIC);
        personalDetails.setName(name);

        Biography biography = new Biography();
        biography.setContent("Biography for " + USER_ORCID);
        biography.setVisibility(Visibility.PUBLIC);
        personalDetails.setBiography(biography);

        OtherName otherName = new OtherName();
        otherName.setPutCode(13L);
        otherName.setContent("Other Name PUBLIC");
        otherName.setVisibility(Visibility.PUBLIC);
        OtherNames otherNames = new OtherNames();
        otherNames.getOtherNames().add(otherName);
        personalDetails.setOtherNames(otherNames);

        return personalDetails;
    }

    private PublicRecordPersonDetails createExpectedPersonDetails() {
        PublicRecordPersonDetails personDetails = new PublicRecordPersonDetails();
        personDetails.setBiography(createPublicPersonalDetails().getBiography());
        personDetails.setDisplayName("Credit Name");
        personDetails.setTitle("Credit Name (" + USER_ORCID + ") - " + PUBLIC_LAYOUT_TITLE);
        personDetails.setPublicGroupedOtherNames(Collections.singletonMap("Other Name PUBLIC", createPublicPersonalDetails().getOtherNames().getOtherNames()));
        personDetails.setPublicGroupedAddresses(Collections.singletonMap("US", createPublicAddresses().getAddress()));

        Map<String, String> countryNames = new HashMap<>();
        countryNames.put("US", UNITED_STATES);
        personDetails.setCountryNames(countryNames);

        personDetails.setPublicGroupedKeywords(Collections.singletonMap("PUBLIC", createPublicKeywords().getKeywords()));
        personDetails.setPublicGroupedResearcherUrls(
                Collections.singletonMap("http://www.researcherurl.com?id=13", createPublicResearcherUrls().getResearcherUrls()));

        Map<String, List<Email>> emails = new HashMap<>();
        emails.put("public_0000-0000-0000-0003@test.orcid.org", Collections.singletonList(createPublicEmails().getEmails().get(0)));
        emails.put("public_0000-0000-0000-0003@orcid.org", Collections.singletonList(createPublicEmails().getEmails().get(1)));
        personDetails.setPublicGroupedEmails(emails);

        Map<String, List<PersonExternalIdentifier>> externalIdentifiers = new HashMap<>();
        externalIdentifiers.put("public_type:public_ref", Collections.singletonList(createPublicExternalIdentifiers().getExternalIdentifiers().get(0)));
        externalIdentifiers.put("self_public_type:self_public_ref", Collections.singletonList(createPublicExternalIdentifiers().getExternalIdentifiers().get(1)));
        externalIdentifiers.put("self_public_user_obo_type:self_public_user_obo_ref",
                Collections.singletonList(createPublicExternalIdentifiers().getExternalIdentifiers().get(2)));
        personDetails.setPublicGroupedPersonExternalIdentifiers(externalIdentifiers);

        return personDetails;
    }

    private Addresses createPublicAddresses() {
        Address address = new Address();
        address.setPutCode(9L);
        address.setCountry(new Country(Iso3166Country.US));
        address.setVisibility(Visibility.PUBLIC);

        Addresses addresses = new Addresses();
        addresses.getAddress().add(address);
        return addresses;
    }

    private Keywords createPublicKeywords() {
        Keyword keyword = new Keyword();
        keyword.setPutCode(9L);
        keyword.setContent("PUBLIC");
        keyword.setVisibility(Visibility.PUBLIC);

        Keywords keywords = new Keywords();
        keywords.getKeywords().add(keyword);
        return keywords;
    }

    private ResearcherUrls createPublicResearcherUrls() {
        ResearcherUrl researcherUrl = new ResearcherUrl();
        researcherUrl.setPutCode(13L);
        researcherUrl.setUrl(new Url("http://www.researcherurl.com?id=13"));
        researcherUrl.setUrlName("public_rurl");
        researcherUrl.setVisibility(Visibility.PUBLIC);

        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.getResearcherUrls().add(researcherUrl);
        return researcherUrls;
    }

    private Emails createPublicEmails() {
        Email clientEmail = new Email();
        clientEmail.setEmail("public_0000-0000-0000-0003@test.orcid.org");
        clientEmail.setVerified(true);
        clientEmail.setVisibility(Visibility.PUBLIC);
        Source clientSource = new Source();
        clientSource.setSourceClientId(new SourceClientId("APP-5555555555555555"));
        clientSource.setSourceName(new SourceName("Source Client 1"));
        clientEmail.setSource(clientSource);

        Email memberEmail = new Email();
        memberEmail.setEmail("public_0000-0000-0000-0003@orcid.org");
        memberEmail.setVerified(true);
        memberEmail.setVisibility(Visibility.PUBLIC);
        Source memberSource = new Source();
        memberSource.setSourceOrcid(new SourceOrcid(USER_ORCID));
        memberSource.setSourceName(new SourceName("Credit Name"));
        memberEmail.setSource(memberSource);

        Emails emails = new Emails();
        emails.getEmails().add(clientEmail);
        emails.getEmails().add(memberEmail);
        return emails;
    }

    private PersonExternalIdentifiers createPublicExternalIdentifiers() {
        PersonExternalIdentifiers identifiers = new PersonExternalIdentifiers();
        identifiers.getExternalIdentifiers().add(createExternalIdentifier(13L, "public_type", "public_ref", "http://ext-id/public_ref"));
        identifiers.getExternalIdentifiers().add(createExternalIdentifier(18L, "self_public_type", "self_public_ref", "http://ext-id/self/public"));
        identifiers.getExternalIdentifiers().add(createExternalIdentifier(19L, "self_public_user_obo_type", "self_public_user_obo_ref", "http://ext-id/self/obo/public"));
        return identifiers;
    }

    private PersonExternalIdentifier createExternalIdentifier(Long putCode, String type, String value, String url) {
        PersonExternalIdentifier identifier = new PersonExternalIdentifier();
        identifier.setPutCode(putCode);
        identifier.setType(type);
        identifier.setValue(value);
        identifier.setUrl(new Url(url));
        identifier.setVisibility(Visibility.PUBLIC);
        return identifier;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> createGroupedAffiliations() {
        Map groupedAffiliations = new HashMap();
        groupedAffiliations.put(AffiliationType.DISTINCTION, Collections.singletonList(createAffiliationGroup(new DistinctionSummary(), 27L)));
        groupedAffiliations.put(AffiliationType.EDUCATION, Collections.singletonList(createAffiliationGroup(new EducationSummary(), 20L)));
        groupedAffiliations.put(AffiliationType.EMPLOYMENT, Collections.singletonList(createAffiliationGroup(new EmploymentSummary(), 17L)));
        groupedAffiliations.put(AffiliationType.INVITED_POSITION, Collections.singletonList(createAffiliationGroup(new InvitedPositionSummary(), 32L)));
        groupedAffiliations.put(AffiliationType.MEMBERSHIP, Collections.singletonList(createAffiliationGroup(new MembershipSummary(), 37L)));
        groupedAffiliations.put(AffiliationType.QUALIFICATION, Collections.singletonList(createAffiliationGroup(new QualificationSummary(), 42L)));
        groupedAffiliations.put(AffiliationType.SERVICE, Collections.singletonList(createAffiliationGroup(new ServiceSummary(), 47L)));
        return groupedAffiliations;
    }

    private <T extends AffiliationSummary> AffiliationGroup<T> createAffiliationGroup(T summary, long putCode) {
        summary.setPutCode(putCode);
        summary.setVisibility(Visibility.PUBLIC);
        summary.setDisplayIndex("0");
        summary.setStartDate(FuzzyDate.valueOf(2019, 12, 31));
        summary.setOrganization(createOrganization("Organization " + putCode));

        AffiliationGroup<T> group = new AffiliationGroup<>();
        group.getActivities().add(summary);
        return group;
    }

    private Organization createOrganization(String name) {
        Organization organization = new Organization();
        organization.setName(name);

        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setRegion("region");
        address.setCountry(Iso3166Country.US);
        organization.setAddress(address);

        return organization;
    }

    private void inject(Class<?> owner, String fieldName, Object value) {
        try {
            Field field = owner.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(publicProfileController, value);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Missing field " + fieldName + " on " + owner.getSimpleName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to inject field " + fieldName, e);
        }
    }

    private String formatHttpDate(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(timestamp));
    }
}
