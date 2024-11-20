package org.orcid.frontend.web.controllers;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.PublicRecordPersonDetails;
import org.orcid.pojo.ajaxForm.AffiliationGroupContainer;
import org.orcid.pojo.ajaxForm.AffiliationGroupForm;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Angel Montenegro
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-frontend-web-servlet.xml" })
public class PublicProfileControllerTest extends DBUnitTest {
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/BiographyEntityData.xml", "/data/OrgsEntityData.xml", 
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/RecordNameEntityData.xml", 
            "/data/WorksEntityData.xml");
    
    private String unclaimedUserOrcid = "0000-0000-0000-0001";
    private String userOrcid = "0000-0000-0000-0003";    
    private String deprecatedUserOrcid = "0000-0000-0000-0004";
    private String lockedUserOrcid = "0000-0000-0000-0006";  
    
    String reviewedNoIntegrationsOrcid = "0009-0000-0000-0001";
    String reviewedWithIntegrationsOrcid = "0009-0000-0000-0002";
    String unreviewedNoIntegrationsOrcid = "0009-0000-0000-0003";
    String unreviewedWithIntegrationsOrcid = "0009-0000-0000-0004";
    String unreviewedCreatedByMembersWithActivitiesOrcid = "0009-0000-0000-0005";
    String unreviewedCreatedByMembersWithNoActivitiesOrcid = "0009-0000-0000-0006";
    String primaryRecord = "0000-0000-0000-0000";
    
    @Resource
    PublicProfileController publicProfileController;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Mock
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    
    @Mock
    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManagerMock;
    
    @Mock
    private OrcidOauth2TokenDetailService orcidOauth2TokenServiceMock;
    
    @Mock
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnlyMock;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);        
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
    public void testGetPersonDetails() {
        PublicRecordPersonDetails personDetails = publicProfileController.getPersonDetails(userOrcid);
        assertNotNull(personDetails.getBiography());
        assertEquals("Biography for 0000-0000-0000-0003", personDetails.getBiography().getContent());
        
        assertNotNull(personDetails.getDisplayName());
        assertEquals("Credit Name", personDetails.getDisplayName());
        
        assertNotNull(personDetails.getTitle());
        assertEquals((personDetails.getDisplayName() + " (0000-0000-0000-0003) - " + localeManager.resolveMessage("layout.public-layout.title")), personDetails.getTitle());
        assertNotNull(personDetails.getPublicGroupedOtherNames());
        Map<String, List<OtherName>> groupedOtherNames = personDetails.getPublicGroupedOtherNames();
        assertNotNull(groupedOtherNames);        
        assertEquals(1, groupedOtherNames.keySet().size());
        assertTrue(groupedOtherNames.containsKey("Other Name PUBLIC"));
        List<OtherName> publicOtherNames = groupedOtherNames.get("Other Name PUBLIC");
        assertEquals(1, publicOtherNames.size());       
        assertEquals(Long.valueOf(13), publicOtherNames.get(0).getPutCode());
        assertEquals("Other Name PUBLIC", publicOtherNames.get(0).getContent());
        assertEquals(Visibility.PUBLIC, publicOtherNames.get(0).getVisibility());        
              
        assertNotNull(personDetails.getPublicGroupedAddresses());
        Map<String, List<Address>> groupedAddresses = personDetails.getPublicGroupedAddresses();        
        assertNotNull(groupedAddresses);
        assertEquals(1, groupedAddresses.keySet().size());
        assertTrue(groupedAddresses.containsKey("US"));
        List<Address> publicAddresses = groupedAddresses.get("US");
        assertEquals(1, publicAddresses.size());
        assertEquals(Long.valueOf(9), publicAddresses.get(0).getPutCode());
        assertEquals(Iso3166Country.US, publicAddresses.get(0).getCountry().getValue());         
        assertEquals(Visibility.PUBLIC, publicAddresses.get(0).getVisibility());
        
        assertNotNull(personDetails.getCountryNames());
        Map<String, String> countryNames = personDetails.getCountryNames();
        Map<String, String> testCountry = new HashMap<String, String>();
        testCountry.put("US", localeManager.resolveMessage("org.orcid.persistence.jpa.entities.CountryIsoEntity.US"));        
        assertEquals(testCountry, countryNames);
        
        assertNotNull(personDetails.getPublicGroupedKeywords());
        Map<String, List<Keyword>> groupedKeywords = personDetails.getPublicGroupedKeywords();
        assertNotNull(groupedKeywords);        
        assertEquals(1, groupedKeywords.keySet().size());
        assertTrue(groupedKeywords.containsKey("PUBLIC"));
                List<Keyword> publicKeywords = groupedKeywords.get("PUBLIC");
        assertEquals(1, publicKeywords.size());       
        assertEquals(Long.valueOf(9), publicKeywords.get(0).getPutCode());
        assertEquals("PUBLIC", publicKeywords.get(0).getContent());
        assertEquals(Visibility.PUBLIC, publicKeywords.get(0).getVisibility());          
        
        assertNotNull(personDetails.getPublicGroupedResearcherUrls());
        Map<String, List<ResearcherUrl>> rUrls = personDetails.getPublicGroupedResearcherUrls();        
        assertNotNull(rUrls);
        assertEquals(1, rUrls.keySet().size());
        assertTrue(rUrls.containsKey("http://www.researcherurl.com?id=13"));
        List<ResearcherUrl> publicResearchUrls = rUrls.get("http://www.researcherurl.com?id=13");
        assertEquals(Long.valueOf(13), publicResearchUrls.get(0).getPutCode());
        assertEquals("public_rurl", publicResearchUrls.get(0).getUrlName());
        assertEquals(Visibility.PUBLIC, publicResearchUrls.get(0).getVisibility());
        
        assertNotNull(personDetails.getPublicGroupedEmails());
        Map<String, List<Email>> emails = personDetails.getPublicGroupedEmails();        
        assertNotNull(emails);
        assertEquals(2, emails.keySet().size());
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
        assertEquals("0000-0000-0000-0003", email2.get(0).getSource().retrieveSourcePath());
        assertEquals("Credit Name", email2.get(0).getSource().getSourceName().getContent());
        assertNotNull(email2.get(0).getSource().getSourceOrcid());

        assertNotNull(personDetails.getPublicGroupedPersonExternalIdentifiers());
        Map<String, List<PersonExternalIdentifier>> extIds = personDetails.getPublicGroupedPersonExternalIdentifiers();        
        assertNotNull(extIds);
        assertEquals(3, extIds.keySet().size());
        assertTrue(extIds.containsKey("public_type:public_ref"));
        assertTrue(extIds.containsKey("self_public_type:self_public_ref"));
        assertTrue(extIds.containsKey("self_public_user_obo_type:self_public_user_obo_ref"));
        List<PersonExternalIdentifier> publicExternalIdentifiers = extIds.get("public_type:public_ref");        
        assertEquals(Long.valueOf(13), publicExternalIdentifiers.get(0).getPutCode());
        assertEquals("http://ext-id/public_ref", publicExternalIdentifiers.get(0).getUrl().getValue());
        assertEquals(Visibility.PUBLIC, publicExternalIdentifiers.get(0).getVisibility());
        publicExternalIdentifiers = extIds.get("self_public_type:self_public_ref");        
        assertEquals(Long.valueOf(18), publicExternalIdentifiers.get(0).getPutCode());
        assertEquals("http://ext-id/self/public", publicExternalIdentifiers.get(0).getUrl().getValue());
        assertEquals(Visibility.PUBLIC, publicExternalIdentifiers.get(0).getVisibility());
        publicExternalIdentifiers = extIds.get("self_public_user_obo_type:self_public_user_obo_ref");        
        assertEquals(Long.valueOf(19), publicExternalIdentifiers.get(0).getPutCode());
        assertEquals("http://ext-id/self/obo/public", publicExternalIdentifiers.get(0).getUrl().getValue());
        assertEquals(Visibility.PUBLIC, publicExternalIdentifiers.get(0).getVisibility());        
    }
    
    @Test
    public void testViewValidUser() {
        ModelAndView mav = publicProfileController.publicPreview(request, response, 1, 0, 15, userOrcid);
        assertEquals("public_profile_v3", mav.getViewName());
        Map<String, Object> model = mav.getModel();
        assertNotNull(model);
        assertTrue(model.containsKey("isPublicProfile"));
        assertTrue(model.containsKey("effectiveUserOrcid"));
        assertEquals(userOrcid, model.get("effectiveUserOrcid"));
        assertFalse(model.containsKey("noIndex"));
    }       
    
    @Test
    public void testViewClaimedUserWhenIsLongEnough() {
        //Update the submission date so it is long enough
        ProfileEntity profileEntity = profileDao.find(unclaimedUserOrcid);
        profileEntity.setSubmissionDate(DateUtils.addDays(new Date(), -10));
        profileDao.merge(profileEntity);
        profileDao.flush();
        ModelAndView mav = publicProfileController.publicPreview(request, response, 1, 0, 15, unclaimedUserOrcid);        
        assertEquals("public_profile_v3", mav.getViewName());
        Map<String, Object> model = mav.getModel();
        assertNotNull(model);
        assertTrue(model.containsKey("isPublicProfile"));
        assertTrue(model.containsKey("effectiveUserOrcid"));
        assertEquals(unclaimedUserOrcid, model.get("effectiveUserOrcid"));

        assertFalse(model.containsKey("noIndex"));
        
        //Update the submission date so it is not long enough
        profileEntity = profileDao.find(unclaimedUserOrcid);
        profileEntity.setSubmissionDate(new Date());
        profileDao.merge(profileEntity);
        profileDao.flush();
    }
    
    @Test
    public void testGetGroupedAffiliations() {
        AffiliationGroupContainer container = publicProfileController.getGroupedAffiliations(userOrcid);
        Map<AffiliationType, List<AffiliationGroupForm>> map = container.getAffiliationGroups();
        boolean distinctions = false, invitedPositions = false, educations = false, memberships = false, employments = false, qualifications = false, services = false;
        for (AffiliationType type : map.keySet()) {
            List<AffiliationGroupForm> elements = map.get(type);
            // There should be only one public element
            assertEquals(1, elements.size());
            assertEquals(Visibility.PUBLIC.name(), elements.get(0).getActiveVisibility());
            assertEquals(Visibility.PUBLIC.name(), elements.get(0).getAffiliations().get(0).getVisibility().getVisibility().name());
            Long activePutCode = Long.valueOf(elements.get(0).getActivePutCode());
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
        // Check un-reviewed record with no integrations
        Map<String, String> map1 = publicProfileController.getUserInfo(unreviewedNoIntegrationsOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), unreviewedNoIntegrationsOrcid);
        assertEquals(map1.get("IS_LOCKED"), "false");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "false");
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
        
        // Check un-reviewed record with integrations
        map1 = publicProfileController.getUserInfo(unreviewedWithIntegrationsOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), unreviewedWithIntegrationsOrcid);
        assertEquals(map1.get("IS_LOCKED"), "false");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "true");
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
        
        // Check member created, un-reviewed record with no integrations and no activities
        map1 = publicProfileController.getUserInfo(unreviewedCreatedByMembersWithNoActivitiesOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), unreviewedCreatedByMembersWithNoActivitiesOrcid);
        assertEquals(map1.get("IS_LOCKED"), "false");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "false");
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
        
        // Check member created, un-reviewed record with no integrations and activities
        map1 = publicProfileController.getUserInfo(unreviewedCreatedByMembersWithActivitiesOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), unreviewedCreatedByMembersWithActivitiesOrcid);
        assertEquals(map1.get("IS_LOCKED"), "false");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "true");
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
        
        // Check reviewed record with no integrations
        map1 = publicProfileController.getUserInfo(reviewedNoIntegrationsOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), reviewedNoIntegrationsOrcid);
        assertEquals(map1.get("IS_LOCKED"), "false");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "true");
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
        
        // Check reviewed record with integrations
        map1 = publicProfileController.getUserInfo(reviewedWithIntegrationsOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), reviewedWithIntegrationsOrcid);
        assertEquals(map1.get("IS_LOCKED"), "false");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "true");
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
        
        // Check deprecated
        map1 = publicProfileController.getUserInfo(deprecatedUserOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), deprecatedUserOrcid);
        assertEquals(map1.get("IS_LOCKED"), "false");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "false");
        assertEquals(map1.get("PRIMARY_RECORD"), primaryRecord);
        
        // Check locked
        map1 = publicProfileController.getUserInfo(lockedUserOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), lockedUserOrcid);
        assertEquals(map1.get("IS_LOCKED"), "true");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "false");
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
        
        // Check all OK user
        map1 = publicProfileController.getUserInfo(userOrcid);
        assertEquals(map1.get("EFFECTIVE_USER_ORCID"), userOrcid);
        assertEquals(map1.get("IS_LOCKED"), "false");
        assertEquals(map1.get("IS_DEACTIVATED"), "false");
        assertEquals(map1.get("READY_FOR_INDEXING"), "true");
        assertFalse(map1.containsKey("PRIMARY_RECORD"));
    }
    
    private void setupUserInfoMocks() {
        SourceEntity sourceEntity = new SourceEntity(new ClientDetailsEntity("APP-000000000001"));
        TargetProxyHelper.injectIntoProxy(publicProfileController, "orcidOauth2TokenService", orcidOauth2TokenServiceMock);
        TargetProxyHelper.injectIntoProxy(publicProfileController, "profileEntityCacheManager", profileEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(publicProfileController, "profileEntityManagerReadOnly", profileEntityManagerReadOnlyMock);
        
        //This function must be used just by a specific user, if it is called by any other throw an exception
        when(profileEntityManagerReadOnlyMock.haveMemberPushedWorksOrAffiliationsToRecord(anyString(), anyString())).thenThrow(MethodNotAllowedException.class);
        when(profileEntityManagerReadOnlyMock.haveMemberPushedWorksOrAffiliationsToRecord(eq(unreviewedCreatedByMembersWithActivitiesOrcid), anyString())).thenReturn(true);
        when(profileEntityManagerReadOnlyMock.haveMemberPushedWorksOrAffiliationsToRecord(eq(unreviewedCreatedByMembersWithNoActivitiesOrcid), anyString())).thenReturn(false);
        
        // Reviewed record with no integrations
        ProfileEntity reviewedNoIntegrations = new ProfileEntity(reviewedNoIntegrationsOrcid);
        reviewedNoIntegrations.setRecordLocked(false);
        reviewedNoIntegrations.setReviewed(true);
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(reviewedNoIntegrationsOrcid), anyLong())).thenReturn(false);
        when(profileEntityCacheManagerMock.retrieve(reviewedNoIntegrationsOrcid)).thenReturn(reviewedNoIntegrations);        
        
        // Reviewed record with integrations
        ProfileEntity reviewedWithIntegrations = new ProfileEntity(reviewedWithIntegrationsOrcid);
        reviewedWithIntegrations.setRecordLocked(false);
        reviewedWithIntegrations.setReviewed(true);
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(reviewedWithIntegrationsOrcid), anyLong())).thenReturn(true);
        when(profileEntityCacheManagerMock.retrieve(reviewedWithIntegrationsOrcid)).thenReturn(reviewedWithIntegrations);
        
        // Un-reviewed record with no integrations
        ProfileEntity unreviewedNoIntegrations = new ProfileEntity(unreviewedNoIntegrationsOrcid);
        unreviewedNoIntegrations.setRecordLocked(false);
        unreviewedNoIntegrations.setReviewed(false);
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(unreviewedNoIntegrationsOrcid), anyLong())).thenReturn(false);
        when(profileEntityCacheManagerMock.retrieve(unreviewedNoIntegrationsOrcid)).thenReturn(unreviewedNoIntegrations);
        
        // Un-reviewed record with integrations
        ProfileEntity unreviewedWithIntegrations = new ProfileEntity(unreviewedWithIntegrationsOrcid);
        unreviewedWithIntegrations.setRecordLocked(false);
        unreviewedWithIntegrations.setReviewed(false);
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(unreviewedWithIntegrationsOrcid), anyLong())).thenReturn(true);
        when(profileEntityCacheManagerMock.retrieve(unreviewedWithIntegrationsOrcid)).thenReturn(unreviewedWithIntegrations);
        
        // Un reviewed record, created by member, with no integrations and no activities
        ProfileEntity unreviewedCreatedByMembersWithNoActivities = new ProfileEntity(unreviewedCreatedByMembersWithNoActivitiesOrcid);
        unreviewedCreatedByMembersWithNoActivities.setSource(sourceEntity);
        unreviewedWithIntegrations.setRecordLocked(false);
        unreviewedWithIntegrations.setReviewed(false);
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(unreviewedCreatedByMembersWithNoActivitiesOrcid), anyLong())).thenReturn(false);
        when(profileEntityCacheManagerMock.retrieve(eq(unreviewedCreatedByMembersWithNoActivitiesOrcid))).thenReturn(unreviewedCreatedByMembersWithNoActivities);
        
        
        // Un reviewed record, created by member, with no integrations and activities
        ProfileEntity unreviewedCreatedByMembersWithActivities = new ProfileEntity(unreviewedCreatedByMembersWithActivitiesOrcid);
        unreviewedCreatedByMembersWithActivities.setSource(sourceEntity);
        unreviewedWithIntegrations.setRecordLocked(false);
        unreviewedWithIntegrations.setReviewed(false);
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(unreviewedCreatedByMembersWithActivitiesOrcid), anyLong())).thenReturn(false);
        when(profileEntityCacheManagerMock.retrieve(eq(unreviewedCreatedByMembersWithActivitiesOrcid))).thenReturn(unreviewedCreatedByMembersWithActivities);
        
        // Deprecated 
        ProfileEntity deprecated = new ProfileEntity(deprecatedUserOrcid);
        deprecated.setRecordLocked(false);
        deprecated.setReviewed(true);
        deprecated.setPrimaryRecord(new ProfileEntity(primaryRecord));
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(deprecatedUserOrcid), anyLong())).thenReturn(true);
        when(profileEntityCacheManagerMock.retrieve(deprecatedUserOrcid)).thenReturn(deprecated);
        
        // Locked
        ProfileEntity locked = new ProfileEntity(lockedUserOrcid);
        locked.setRecordLocked(true);
        locked.setReviewed(true);
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(lockedUserOrcid), anyLong())).thenReturn(true);
        when(profileEntityCacheManagerMock.retrieve(lockedUserOrcid)).thenReturn(locked);
        
        
        // All OK user
        ProfileEntity allOk = new ProfileEntity(userOrcid);
        allOk.setRecordLocked(false);
        allOk.setReviewed(true);
        
        when(orcidOauth2TokenServiceMock.hasToken(eq(userOrcid), anyLong())).thenReturn(true);
        when(profileEntityCacheManagerMock.retrieve(userOrcid)).thenReturn(allOk);        
    }
}
