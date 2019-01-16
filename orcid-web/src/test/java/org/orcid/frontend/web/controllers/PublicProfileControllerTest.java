package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc2.record.Biography;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PublicRecordPersonDetails;
import org.orcid.pojo.ajaxForm.AffiliationGroupContainer;
import org.orcid.pojo.ajaxForm.AffiliationGroupForm;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Angel Montenegro
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class PublicProfileControllerTest extends DBUnitTest {
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/BiographyEntityData.xml", "/data/OrgsEntityData.xml", 
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/RecordNameEntityData.xml", 
            "/data/WorksEntityData.xml");
    
    private String unclaimedUserOrcid = "0000-0000-0000-0001";
    private String userOrcid = "0000-0000-0000-0003";    
    private String deprecatedUserOrcid = "0000-0000-0000-0004";
    private String lockedUserOrcid = "0000-0000-0000-0006";    
    
    @Resource
    PublicProfileController publicProfileController;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Mock
    //private HttpServletRequest request;
    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    
    @Mock
    //private HttpServletRequest request;
    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        assertNotNull(publicProfileController);
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
        assertEquals(1, emails.keySet().size());
        assertTrue(emails.containsKey("public_0000-0000-0000-0003@test.orcid.org"));        
        List<Email> publicEmails = emails.get("public_0000-0000-0000-0003@test.orcid.org");
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", publicEmails.get(0).getEmail());
        assertEquals(Visibility.PUBLIC, publicEmails.get(0).getVisibility());
        
        assertNotNull(personDetails.getPublicGroupedPersonExternalIdentifiers());
        Map<String, List<PersonExternalIdentifier>> extIds = personDetails.getPublicGroupedPersonExternalIdentifiers();        
        assertNotNull(extIds);
        assertEquals(1, extIds.keySet().size());
        assertTrue(extIds.containsKey("public_type:public_ref"));
        List<PersonExternalIdentifier> publicExternalIdentifiers = extIds.get("public_type:public_ref");        
        assertEquals(Long.valueOf(13), publicExternalIdentifiers.get(0).getPutCode());
        assertEquals("http://ext-id/public_ref", publicExternalIdentifiers.get(0).getUrl().getValue());
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
    public void testViewLockedUser() {
        String displayName = localeManager.resolveMessage("public_profile.deactivated.given_names") + " " + localeManager.resolveMessage("public_profile.deactivated.family_name");
        ModelAndView mav = publicProfileController.publicPreview(request, response, 1, 0, 15, lockedUserOrcid);
        Map<String, Object> model = mav.getModel();
        assertUnavailableProfileBasicData(mav, lockedUserOrcid, displayName);    
        assertTrue(model.containsKey("locked"));
        assertTrue(Boolean.TRUE.equals(model.get("locked")));
    }
    
    @Test
    public void testViewDeprecatedUser() {
        ModelAndView mav = publicProfileController.publicPreview(request, response, 1, 0, 15, deprecatedUserOrcid);
        Map<String, Object> model = mav.getModel();
        assertUnavailableProfileBasicData(mav, deprecatedUserOrcid, null);
        assertTrue(model.containsKey("deprecated"));
        assertTrue(Boolean.TRUE.equals(model.get("deprecated")));
        assertTrue(model.containsKey("primaryRecord"));
        assertEquals("0000-0000-0000-0003", model.get("primaryRecord"));
    }
    
    @Test
    public void testViewClaimedUserBeforeIsLongEnough() {
        ProfileEntity profile = profileDao.find(unclaimedUserOrcid);
        profile.setSubmissionDate(new Date());
        profileDao.merge(profile);
        profileDao.flush();
        String displayName = localeManager.resolveMessage("orcid.reserved_for_claim");
        ModelAndView mav = publicProfileController.publicPreview(request, response, 1, 0, 15, unclaimedUserOrcid);
        assertUnavailableProfileBasicData(mav, unclaimedUserOrcid, displayName);        
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
    
    private void assertUnavailableProfileBasicData(ModelAndView mav, String orcid, String displayName) {
        assertEquals("public_profile_unavailable", mav.getViewName());
        Map<String, Object> model = mav.getModel();
        assertTrue(model.containsKey("effectiveUserOrcid"));
        assertEquals(orcid, model.get("effectiveUserOrcid"));  
        if(displayName != null) {
            assertTrue(model.containsKey("displayName"));
            assertEquals(displayName, model.get("displayName"));
            assertTrue(model.containsKey("title"));
            assertEquals(localeManager.resolveMessage("layout.public-layout.title", displayName, orcid), model.get("title"));
        }
    }
}
