package org.orcid.frontend.web.controllers;

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.math3.util.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.admin.LockReason;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.ClientDetailsManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.SpamManager;
import org.orcid.core.manager.v3.impl.ProfileHistoryEventManagerImpl;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.core.utils.VerifyEmailUtils;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.common.OrcidType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.AdminChangePassword;
import org.orcid.pojo.AdminDelegatesRequest;
import org.orcid.pojo.AdminResetPasswordLink;
import org.orcid.pojo.ConvertClient;
import org.orcid.pojo.LockAccounts;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.DateUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;


@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-frontend-web-servlet.xml" })
public class AdminControllerTest extends BaseControllerTest {

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
    @Resource(name = "adminController")
    AdminController adminController;

    @Resource
    private ProfileDao profileDao;

    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;    
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Mock
    private EmailManager mockEmailManager;
    
    @Mock
    private EmailManagerReadOnly mockEmailManagerReadOnly;
    
    @Mock
    private ProfileHistoryEventManager profileHistoryEventManager;
    
    @Mock
    private EmailFrequencyManager mockEmailFrequencyManager;
    
    @Mock
    private OrcidSecurityManager mockOrcidSecurityManager;
    
    @Mock
    private ClientDetailsManager mockClientDetailsManager;
    
    @Resource(name = "clientDetailsManagerV3")
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource 
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Mock
    private RecordEmailSender mockRecordEmailSender;
    
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    
    @Mock
    private OrcidUrlManager mockOrcidUrlManager;
    
    @Mock
    private VerifyEmailUtils mockVerifyEmailUtils;
    
    @Mock
    private EncryptionManager mockEncryptionManager;
    
    
    
    @Captor
    private ArgumentCaptor<String> adminUser;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml", "/data/ClientDetailsEntityData.xml"));
    }

    @Before
    public void beforeInstance() {
        MockitoAnnotations.initMocks(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put(EmailFrequencyManager.ADMINISTRATIVE_CHANGE_NOTIFICATIONS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.CHANGE_NOTIFICATIONS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.MEMBER_UPDATE_REQUESTS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.QUARTERLY_TIPS, String.valueOf(true));

        when(mockEmailFrequencyManager.getEmailFrequency(anyString())).thenReturn(map);

        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        assertNotNull(adminController);
        assertNotNull(profileDao);

        TargetProxyHelper.injectIntoProxy(adminController, "orcidSecurityManager", mockOrcidSecurityManager);
        when(mockOrcidSecurityManager.isAdmin()).thenReturn(true);
        
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }
    @After
    public void after() {
        //Restore the original beans
        TargetProxyHelper.injectIntoProxy(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManagerReadOnly", emailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManager", emailManager);
    }

    @Override
    protected Authentication getAuthentication() {
        String orcid = "4444-4444-4444-4440";
        ProfileEntity p = profileEntityManager.findByOrcid(orcid);
        Email e = emailManager.findPrimaryEmail(orcid);
        List<OrcidWebRole> roles = getRole();
        OrcidProfileUserDetails details = new OrcidProfileUserDetails(orcid,
                e.getEmail(), null, roles);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(orcid, p.getPassword(), getRole());
        auth.setDetails(details);
        return auth;
    }

    protected List<OrcidWebRole> getRole() {
        return Arrays.asList(OrcidWebRole.ROLE_ADMIN);
    }

    @Test
    public void testCheckOrcid() throws Exception {
        ProfileDeprecationRequest r = new ProfileDeprecationRequest();
        ProfileDetails toDeprecate = new ProfileDetails();
        toDeprecate.setOrcid("4444-4444-4444-4447");
        r.setDeprecatedAccount(toDeprecate);
        ProfileDetails primary = new ProfileDetails();
        primary.setOrcid("4444-4444-4444-4411");
        r.setPrimaryAccount(primary);
        
        r = adminController.checkOrcidToDeprecate(mockRequest, mockResponse, r);
        assertNotNull(r);
        assertEquals(1, r.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.inexisting_orcid", "4444-4444-4444-4411"), r.getErrors().get(0));
        assertEquals("otis@reading.com", r.getDeprecatedAccount().getEmail());
        assertEquals("Family Name", r.getDeprecatedAccount().getFamilyName());
        assertEquals("Given Names", r.getDeprecatedAccount().getGivenNames());
        assertEquals("4444-4444-4444-4447", r.getDeprecatedAccount().getOrcid());
        
        assertEquals("4444-4444-4444-4411", r.getPrimaryAccount().getOrcid());
        assertNull(r.getPrimaryAccount().getEmail());
        assertNull(r.getPrimaryAccount().getFamilyName());
        assertNull(r.getPrimaryAccount().getGivenNames());        

        toDeprecate.setOrcid("https://orcid.org/4444-4444-4444-4447");
        r = adminController.checkOrcidToDeprecate(mockRequest, mockResponse, r);
        assertNotNull(r);
        assertEquals(1, r.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.inexisting_orcid", "4444-4444-4444-4411"), r.getErrors().get(0));
        assertEquals("otis@reading.com", r.getDeprecatedAccount().getEmail());
        assertEquals("Family Name", r.getDeprecatedAccount().getFamilyName());
        assertEquals("Given Names", r.getDeprecatedAccount().getGivenNames());
        assertEquals("4444-4444-4444-4447", r.getDeprecatedAccount().getOrcid());
        
        assertEquals("4444-4444-4444-4411", r.getPrimaryAccount().getOrcid());
        assertNull(r.getPrimaryAccount().getEmail());
        assertNull(r.getPrimaryAccount().getFamilyName());
        assertNull(r.getPrimaryAccount().getGivenNames());
    }

    @Test
    public void tryToDeprecateDeprecatedProfile() throws Exception {
        ProfileDeprecationRequest r = new ProfileDeprecationRequest();
        ProfileDetails toDeprecate = new ProfileDetails();
        toDeprecate.setOrcid("4444-4444-4444-444X");
        r.setDeprecatedAccount(toDeprecate);
        ProfileDetails primary = new ProfileDetails();
        primary.setOrcid("4444-4444-4444-4443");
        r.setPrimaryAccount(primary);
        
        // Test deprecating a deprecated account
        ProfileDeprecationRequest result = adminController.deprecateProfile(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.already_deprecated", "4444-4444-4444-444X"), result.getErrors().get(0));

        // Test deprecating account with himself
        toDeprecate.setOrcid("4444-4444-4444-4440");
        primary.setOrcid("4444-4444-4444-4440");
        result = adminController.deprecateProfile(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.deprecated_equals_primary"), result.getErrors().get(0));

        // Test set deprecated account as a primary account
        toDeprecate.setOrcid("4444-4444-4444-4443");
        primary.setOrcid("4444-4444-4444-444X");
        result = adminController.deprecateProfile(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_deprecated", "4444-4444-4444-444X"), result.getErrors().get(0));

        // Test deprecating an invalid orcid
        toDeprecate.setOrcid("4444-4444-4444-444");
        primary.setOrcid("4444-4444-4444-4443");
        result = adminController.deprecateProfile(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.invalid_orcid", "4444-4444-4444-444"), result.getErrors().get(0));

        // Test use invalid orcid as primary
        toDeprecate.setOrcid("4444-4444-4444-4440");
        primary.setOrcid("4444-4444-4444-444");
        result = adminController.deprecateProfile(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.invalid_orcid", "4444-4444-4444-444"), result.getErrors().get(0));

        // Deactivate primary record
        adminController.deactivateOrcidRecords(mockRequest, mockResponse, "4444-4444-4444-4443");
        
        // Test set deactive primary account
        toDeprecate.setOrcid("4444-4444-4444-4440");
        primary.setOrcid("4444-4444-4444-4443");
        result = adminController.deprecateProfile(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_is_deactivated", "4444-4444-4444-4443"), result.getErrors().get(0));
        
        // Deactivate primary record
        adminController.deactivateOrcidRecords(mockRequest, mockResponse, "4444-4444-4444-4443");
        
        // Test set deactive primary account with ORCURL 
        toDeprecate.setOrcid("https://orcid.org/4444-4444-4444-4440");
        primary.setOrcid("4444-4444-4444-4443");
        result = adminController.deprecateProfile(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_is_deactivated", "4444-4444-4444-4443"), result.getErrors().get(0));
    }


    @Test
    public void addANewEmailToARecord() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManagerMock = Mockito.mock(ProfileEntityCacheManager.class);
        TargetProxyHelper.injectIntoProxy(adminController, "profileEntityCacheManager", profileEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManager", mockEmailManager);


        ProfileDetails r = new ProfileDetails();
        r.setOrcid("1111-1111-1111-111X");
        r.setEmail("test.com");
        Mockito.when(profileEntityCacheManagerMock.retrieve(Mockito.eq("1111-1111-1111-111X"))).thenReturn(null);


        // Handle invalid id
        r.setOrcid("4");
        r.setEmail("test@test.com");
        ProfileDetails result = adminController.addEmailToRecord(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.errors.unexisting_orcid"), result.getErrors().get(0));


        // Handle empty id
        r.setOrcid("");
        r.setEmail("test@test.com");
        result = adminController.addEmailToRecord(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.errors.unexisting_orcid"), result.getErrors().get(0));
        Mockito.when(profileEntityCacheManagerMock.retrieve(Mockito.eq("1111-1111-1111-111X"))).thenReturn(new ProfileEntity());
    
        // Handle a invalid email
        r.setOrcid("1111-1111-1111-111X");
        r.setEmail("test");
        result = adminController.addEmailToRecord(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.error_invalid_email_addres"), result.getErrors().get(0));
        
        // Handle a empty email
        r.setOrcid("1111-1111-1111-111X");
        r.setEmail("");
        result = adminController.addEmailToRecord(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.error_please_provided_an_email_to_be_added"), result.getErrors().get(0));


    }
    
   
    
    @Test
    public void addANewEmailToARecordThatIsDuplicated() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManagerMock = Mockito.mock(ProfileEntityCacheManager.class);
        TargetProxyHelper.injectIntoProxy(adminController, "profileEntityCacheManager", profileEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManager", mockEmailManager);

        // Handle an email that was already added to the user 
        Mockito.when(profileEntityCacheManagerMock.retrieve(Mockito.eq("1111-1111-1111-111X"))).thenReturn(new ProfileEntity());
        Emails emails = new Emails();
        java.util.List<Email> emailsList = new ArrayList<Email>();
        Email emailObj = new Email();
        emailObj.setEmail("test@test.com"); 
        emailsList.add(emailObj);
        emails.setEmails(emailsList);
        when(mockEmailManagerReadOnly.getEmails(Mockito.eq("1111-1111-1111-111X"))).thenReturn(emails);
        Mockito.when(mockEmailManager.emailExists(Mockito.eq("test@test.com"))).thenReturn(true);
        ProfileDetails r = new ProfileDetails();
        r.setOrcid("1111-1111-1111-111X");
        r.setEmail("test@test.com");
        ProfileDetails result = adminController.addEmailToRecord(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.error_this_user_already_has_this_email"), result.getErrors().get(0));

    }
    @Test
    public void addANewEmailToARecordThatAlreadyExistOnOtherAccount() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManagerMock = Mockito.mock(ProfileEntityCacheManager.class);
        TargetProxyHelper.injectIntoProxy(adminController, "profileEntityCacheManager", profileEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManager", mockEmailManager);

        // Handle an email that was already added to the user 
        Mockito.when(profileEntityCacheManagerMock.retrieve(Mockito.eq("1111-1111-1111-111X"))).thenReturn(new ProfileEntity());
        Emails emails = new Emails();
        java.util.List<Email> emailsList = new ArrayList<Email>();
        Email emailObj = new Email();
        emailObj.setEmail("test@test.com"); 
        emails.setEmails(emailsList);
        when(mockEmailManagerReadOnly.getEmails(Mockito.eq("1111-1111-1111-111X"))).thenReturn(emails);
        Mockito.when(mockEmailManager.emailExists(Mockito.eq("test@test.com"))).thenReturn(true);
        ProfileDetails r = new ProfileDetails();
        r.setOrcid("1111-1111-1111-111X");
        r.setEmail("test@test.com");
        ProfileDetails result = adminController.addEmailToRecord(mockRequest, mockResponse, r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.error_other_account_has_this_email"), result.getErrors().get(0));

    }   
   
    

    @Test
    public void deactivateAndReactivateProfileTest() throws Exception {
        ProfileHistoryEventManager profileHistoryEventManager = Mockito.mock(ProfileHistoryEventManagerImpl.class);
        ProfileEntityManager profileEntityManager = (ProfileEntityManager) ReflectionTestUtils.getField(adminController, "profileEntityManager");
        ReflectionTestUtils.setField(profileEntityManager, "profileHistoryEventManager", profileHistoryEventManager);
        Mockito.doNothing().when(profileHistoryEventManager).recordEvent(Mockito.any(ProfileHistoryEventType.class), Mockito.anyString(), Mockito.anyString());
        
        // Test deactivate
        Map<String, Set<String>> result = adminController.deactivateOrcidRecords(mockRequest, mockResponse, "4444-4444-4444-4445");
        assertEquals(1, result.get("success").size());

        ProfileEntity deactivated = profileDao.find("4444-4444-4444-4445");
        assertNotNull(deactivated.getDeactivationDate());
        RecordNameEntity name = recordNameDao.getRecordName("4444-4444-4444-4445", System.currentTimeMillis());
        assertEquals("Family Name Deactivated", name.getFamilyName());
        assertEquals("Given Names Deactivated", name.getGivenNames());

        // Test try to deactivate an already deactive account
        result = adminController.deactivateOrcidRecords(mockRequest, mockResponse, "4444-4444-4444-4445");
        assertEquals(1, result.get("alreadyDeactivated").size());

        // Test reactivate using an email address that belongs to other record
        ProfileDetails proDetails = new ProfileDetails();
        proDetails.setEmail("public_0000-0000-0000-0003@test.orcid.org");
        proDetails.setOrcid("4444-4444-4444-4445");
        proDetails = adminController.reactivateOrcidRecord(mockRequest, mockResponse, proDetails);
        assertEquals(1, proDetails.getErrors().size());
        assertEquals(adminController.getMessage("admin.errors.deactivated_account.orcid_id_dont_match", "0000-0000-0000-0003"), proDetails.getErrors().get(0));
        
        // Test reactivate using empty primary email
        proDetails.setEmail("");
        try {
            proDetails = adminController.reactivateOrcidRecord(mockRequest, mockResponse, proDetails);
            fail();
        } catch(RuntimeException re) {
            assertEquals("Unable to filter empty email address", re.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        // Test reactivate
        proDetails.setEmail("aNdReW@tImOtHy.com");
        proDetails = adminController.reactivateOrcidRecord(mockRequest, mockResponse, proDetails);
        assertEquals(0, proDetails.getErrors().size());

        deactivated = profileDao.find("4444-4444-4444-4445");
        assertNull(deactivated.getDeactivationDate());

        // Try to reactivate an already active account
        proDetails = adminController.reactivateOrcidRecord(mockRequest, mockResponse, proDetails);
        assertEquals(1, proDetails.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_reactivation.errors.already_active", new ArrayList<String>()), proDetails.getErrors().get(0));
        
        // Test deactivate
        result = adminController.deactivateOrcidRecords(mockRequest, mockResponse, "https://orcid.org/4444-4444-4444-4445");
        assertEquals(1, result.get("success").size());

        // Test deactivate
        proDetails.setOrcid("https://orcid.org/4444-4444-4444-4445");
        proDetails = adminController.reactivateOrcidRecord(mockRequest, mockResponse, proDetails);
        assertEquals(1, result.get("success").size());
        
    }
    
    @Test
    public void preventDisablingMembersTest() throws IllegalAccessException, UnsupportedEncodingException {
        ProfileHistoryEventManager profileHistoryEventManager = Mockito.mock(ProfileHistoryEventManagerImpl.class);
        ProfileEntityManager profileEntityManager = (ProfileEntityManager) ReflectionTestUtils.getField(adminController, "profileEntityManager");
        ReflectionTestUtils.setField(profileEntityManager, "profileHistoryEventManager", profileHistoryEventManager);
        Mockito.doNothing().when(profileHistoryEventManager).recordEvent(Mockito.any(ProfileHistoryEventType.class), Mockito.anyString(), Mockito.anyString());
        
        // Test deactivate
        Map<String, Set<String>> result = adminController.deactivateOrcidRecords(mockRequest, mockResponse, "5555-5555-5555-5558");
        assertEquals(0, result.get("notFoundList").size());
        assertEquals(0, result.get("alreadyDeactivated").size());
        assertEquals(0, result.get("success").size());
        assertEquals(1, result.get("members").size());
        assertTrue(result.get("members").contains("5555-5555-5555-5558"));
    }

    @Test
    public void findIdsTest() {
        Map<String, String> ids = adminController.findIdByEmailHelper("spike@milligan.com,michael@bentine.com,peter@sellers.com,mixed@case.com,invalid@email.com");
        assertNotNull(ids);
        assertEquals(4, ids.size());
        assertTrue(ids.containsKey("spike@milligan.com"));
        assertEquals("4444-4444-4444-4441", ids.get("spike@milligan.com"));
        assertTrue(ids.containsKey("michael@bentine.com"));
        assertEquals("4444-4444-4444-4442", ids.get("michael@bentine.com"));
        assertTrue(ids.containsKey("peter@sellers.com"));
        assertEquals("4444-4444-4444-4443", ids.get("peter@sellers.com"));
        assertTrue(ids.containsKey("mixed@case.com"));
        assertEquals("4444-4444-4444-4442", ids.get("mixed@case.com"));
        assertFalse(ids.containsKey("invalid@email.com"));
    }

    @Test
    public void resetPasswordTest() throws IllegalAccessException, UnsupportedEncodingException {
        ProfileEntity p = profileEntityManager.findByOrcid("4444-4444-4444-4441");
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=", p.getPassword());
        AdminChangePassword form = new AdminChangePassword();
        form.setOrcidOrEmail("4444-4444-4444-4441");
        form.setPassword("password1");
        adminController.resetPassword(mockRequest, mockResponse, form);
        p = profileEntityManager.findByOrcid("4444-4444-4444-4441");
        assertFalse("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=".equals(p.getPassword()));
    }

    @Test
    public void resetPasswordUsingEmailTest() throws IllegalAccessException, UnsupportedEncodingException {
        TargetProxyHelper.injectIntoProxy(adminController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        
        ProfileEntity p = profileEntityManager.findByOrcid("4444-4444-4444-4442");
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=", p.getPassword());
        AdminChangePassword form = new AdminChangePassword();
        form.setOrcidOrEmail("michael@bentine.com");
        form.setPassword("password1");
        adminController.resetPassword(mockRequest, mockResponse, form);
        p = profileEntityManager.findByOrcid("4444-4444-4444-4442");
        assertFalse("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=".equals(p.getPassword()));
    }
    
    @Test
    public void resetPasswordTestOrcidURL() throws IllegalAccessException, UnsupportedEncodingException {
        ProfileEntity p = profileEntityManager.findByOrcid("4444-4444-4444-4443");
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=", p.getPassword());
        AdminChangePassword form = new AdminChangePassword();
        form.setOrcidOrEmail("https://orcid.org/4444-4444-4444-4443");
        form.setPassword("password1");
        adminController.resetPassword(mockRequest, mockResponse, form);
        p = profileEntityManager.findByOrcid("4444-4444-4444-4443");
        assertFalse("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=".equals(p.getPassword()));
    }

    @Test
    public void verifyEmailTest() throws Exception {
        TargetProxyHelper.injectIntoProxy(adminController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("not-verified@email.com")).thenReturn("4444-4444-4444-4499");
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        
        // Add not verified email
        Email email = new Email();
        email.setEmail("not-verified@email.com");
        email.setCurrent(false);
        email.setPrimary(false);
        email.setVerified(false);
        email.setVisibility(Visibility.PUBLIC);
        emailManager.addEmail("4444-4444-4444-4499", email);

        // Verify the email
        adminController.adminVerifyEmail(request, response, "not-verified@email.com");
        EmailEntity emailEntity = emailManager.find("not-verified@email.com");
        assertNotNull(emailEntity);
        assertTrue(emailEntity.getVerified());
    }

    @Test
    public void testLockAccounts() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);        

        AdminController adminController = new AdminController();        
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);

        String commaSeparatedValues = "not-found-email1@test.com,not-found-email2@test.com,record-locked-email1@test.com,record-locked-email2@test.com,successful-email1@test.com,successful-email2@test.com,successful-email3@test.com,successful-email4@test.com,reviewed-email@test.com,0000-0000-0000-0001,https://orcid.org/0000-0000-0000-0002,0000-0000-0000-0003,https://orcid.org/0000-0000-0000-0004,notAnOrcidIdOrEmail";

        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);

        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("record-locked-email1@test.com", "record-locked-email1@test.com");
        map.put("record-locked-email2@test.com", "record-locked-email2@test.com");
        map.put("successful-email1@test.com", "successful-email1@test.com");
        map.put("successful-email2@test.com", "successful-email2@test.com");
        map.put("successful-email3@test.com", "successful-email3@test.com");
        map.put("successful-email4@test.com", "successful-email4@test.com");
        map.put("reviewed-email@test.com", "reviewed-email@test.com");

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);

        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("record-locked-email1@test.com") || ar1.equals("record-locked-email2@test.com") 
                        || ar1.equals("0000-0000-0000-0001") || ar1.equals("0000-0000-0000-0002")) {
                    p.setRecordLocked(true);
                } else {
                    p.setRecordLocked(false);
                }

                if (ar1.contentEquals("reviewed-email@test.com")) {
                    p.setReviewed(true);
                } else {
                    p.setReviewed(false);
                }
                return p;
            }

        });

        LockAccounts lockAccounts = new LockAccounts();
        lockAccounts.setOrcidsToLock(commaSeparatedValues);
        lockAccounts.setLockReason(LockReason.SPAM.getLabel());
        
        Map<String, Set<String>> results = adminController.lockRecords(mockRequest, mockResponse, lockAccounts);
        assertEquals(3, results.get("notFound").size());
        assertTrue(results.get("notFound").contains("not-found-email1@test.com"));
        assertTrue(results.get("notFound").contains("not-found-email2@test.com"));
        assertTrue(results.get("notFound").contains("notAnOrcidIdOrEmail"));

        assertEquals(4, results.get("alreadyLocked").size());
        assertTrue(results.get("alreadyLocked").contains("record-locked-email1@test.com"));
        assertTrue(results.get("alreadyLocked").contains("record-locked-email2@test.com"));
        assertTrue(results.get("alreadyLocked").contains("0000-0000-0000-0001"));
        assertTrue(results.get("alreadyLocked").contains("https://orcid.org/0000-0000-0000-0002"));
        
        assertTrue(results.get("descriptionMissing").contains("successful-email1@test.com"));
        
        Mockito.verify(profileEntityManager, Mockito.times(0)).lockProfile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        
        lockAccounts.setDescription("Test description");
        results = adminController.lockRecords(mockRequest, mockResponse, lockAccounts);

        assertEquals(6, results.get("successful").size());
        assertTrue(results.get("successful").contains("successful-email1@test.com"));
        assertTrue(results.get("successful").contains("successful-email2@test.com"));
        assertTrue(results.get("successful").contains("successful-email3@test.com"));
        assertTrue(results.get("successful").contains("successful-email4@test.com"));
        assertTrue(results.get("successful").contains("0000-0000-0000-0003"));
        assertTrue(results.get("successful").contains("https://orcid.org/0000-0000-0000-0004"));
        

        assertEquals(1, results.get("reviewed").size());
        assertTrue(results.get("reviewed").contains("reviewed-email@test.com"));

        Mockito.verify(emailManager, Mockito.times(18)).emailExists(Mockito.anyString());
        Mockito.verify(profileEntityManager, Mockito.times(6)).lockProfile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), adminUser.capture());
        assertEquals(adminUser.getValue(), "4444-4444-4444-4440");
    }

    @Test
    public void testUnlockAccounts() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);        

        AdminController adminController = new AdminController();        
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);

        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
        String commaSeparatedValues = "not-found-email1@test.com,not-found-email2@test.com,record-unlocked-email1@test.com,record-unlocked-email2@test.com,successful-email1@test.com,successful-email2@test.com,successful-email3@test.com,successful-email4@test.com, 0000-0000-0000-0001,https://orcid.org/0000-0000-0000-0002,0000-0000-0000-0003,https://orcid.org/0000-0000-0000-0004,notAnOrcidIdOrEmail";

        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("record-unlocked-email1@test.com", "record-unlocked-email1@test.com");
        map.put("record-unlocked-email2@test.com", "record-unlocked-email2@test.com");
        map.put("successful-email1@test.com", "successful-email1@test.com");
        map.put("successful-email2@test.com", "successful-email2@test.com");
        map.put("successful-email3@test.com", "successful-email3@test.com");
        map.put("successful-email4@test.com", "successful-email4@test.com");

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);

        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("record-unlocked-email1@test.com") || ar1.equals("record-unlocked-email2@test.com")
                        || ar1.equals("0000-0000-0000-0001") || ar1.equals("0000-0000-0000-0002")) {
                    p.setRecordLocked(false);
                } else {
                    p.setRecordLocked(true);
                }

                return p;
            }

        });

        Map<String, Set<String>> results = adminController.unlockRecords(mockRequest, mockResponse, commaSeparatedValues);
        assertEquals(3, results.get("notFound").size());
        assertTrue(results.get("notFound").contains("not-found-email1@test.com"));
        assertTrue(results.get("notFound").contains("not-found-email2@test.com"));
        assertTrue(results.get("notFound").contains("notAnOrcidIdOrEmail"));

        assertEquals(4, results.get("alreadyUnlocked").size());
        assertTrue(results.get("alreadyUnlocked").contains("record-unlocked-email1@test.com"));
        assertTrue(results.get("alreadyUnlocked").contains("record-unlocked-email2@test.com"));
        assertTrue(results.get("alreadyUnlocked").contains("0000-0000-0000-0001"));
        assertTrue(results.get("alreadyUnlocked").contains("https://orcid.org/0000-0000-0000-0002"));

        assertEquals(6, results.get("successful").size());
        assertTrue(results.get("successful").contains("successful-email1@test.com"));
        assertTrue(results.get("successful").contains("successful-email2@test.com"));
        assertTrue(results.get("successful").contains("successful-email3@test.com"));
        assertTrue(results.get("successful").contains("successful-email4@test.com"));
        assertTrue(results.get("successful").contains("0000-0000-0000-0003"));
        assertTrue(results.get("successful").contains("https://orcid.org/0000-0000-0000-0004"));

        Mockito.verify(emailManager, Mockito.times(8)).emailExists(Mockito.anyString());
        Mockito.verify(profileEntityManager, Mockito.times(6)).unlockProfile(Mockito.anyString());
    }

    @Test
    public void testReviewAccounts() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        SpamManager spamManager = Mockito.mock(SpamManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);        

        AdminController adminController = new AdminController();        
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "spamManager", spamManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);

        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
        String commaSeparatedValues = "not-found-email1@test.com,not-found-email2@test.com,record-reviewed-email1@test.com,record-reviewed-email2@test.com,successful-email1@test.com,successful-email2@test.com,successful-email3@test.com,successful-email4@test.com,0000-0000-0000-0001,https://orcid.org/0000-0000-0000-0002,0000-0000-0000-0003,https://orcid.org/0000-0000-0000-0004,notAnOrcidIdOrEmail";

        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("record-reviewed-email1@test.com", "record-reviewed-email1@test.com");
        map.put("record-reviewed-email2@test.com", "record-reviewed-email2@test.com");
        map.put("successful-email1@test.com", "successful-email1@test.com");
        map.put("successful-email2@test.com", "successful-email2@test.com");
        map.put("successful-email3@test.com", "successful-email3@test.com");
        map.put("successful-email4@test.com", "successful-email4@test.com");              

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {

            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("record-reviewed-email1@test.com") || ar1.equals("record-reviewed-email2@test.com")
                        || ar1.equals("0000-0000-0000-0001") || ar1.equals("0000-0000-0000-0002")) {
                    p.setReviewed(true);
                } else {
                    p.setReviewed(false);
                }
                return p;
            }

        });

        Mockito.when(profileEntityManager.reviewProfile("not-found-email1@test.com")).thenThrow(new RuntimeException("Controller shouldn't try to review null profile"));
        Mockito.when(profileEntityManager.reviewProfile("not-found-email2@test.com")).thenThrow(new RuntimeException("Controller shouldn't try to review null profile"));
        Mockito.when(profileEntityManager.reviewProfile("record-reviewed-email1@test.com")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("record-reviewed-email2@test.com")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));        

        Mockito.when(profileEntityManager.reviewProfile("successful-email1@test.com")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("successful-email2@test.com")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("successful-email3@test.com")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("successful-email4@test.com")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("0000-0000-0000-0001")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("0000-0000-0000-0002")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("0000-0000-0000-0003")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("0000-0000-0000-0004")).thenReturn(true);

        Map<String, Set<String>> results = adminController.reviewRecords(mockRequest, mockResponse, commaSeparatedValues);
        assertEquals(3, results.get("notFound").size());
        assertTrue(results.get("notFound").contains("not-found-email1@test.com"));
        assertTrue(results.get("notFound").contains("not-found-email2@test.com"));
        assertTrue(results.get("notFound").contains("notAnOrcidIdOrEmail"));

        assertEquals(4, results.get("alreadyReviewed").size());
        assertTrue(results.get("alreadyReviewed").contains("record-reviewed-email1@test.com"));
        assertTrue(results.get("alreadyReviewed").contains("record-reviewed-email2@test.com"));
        assertTrue(results.get("alreadyReviewed").contains("0000-0000-0000-0001"));
        assertTrue(results.get("alreadyReviewed").contains("https://orcid.org/0000-0000-0000-0002"));

        assertEquals(6, results.get("successful").size());
        assertTrue(results.get("successful").contains("successful-email1@test.com"));
        assertTrue(results.get("successful").contains("successful-email2@test.com"));
        assertTrue(results.get("successful").contains("successful-email3@test.com"));
        assertTrue(results.get("successful").contains("successful-email4@test.com"));
        assertTrue(results.get("successful").contains("0000-0000-0000-0003"));
        assertTrue(results.get("successful").contains("https://orcid.org/0000-0000-0000-0004"));

        Mockito.verify(emailManager, Mockito.times(8)).emailExists(Mockito.anyString());
        Mockito.verify(profileEntityManager, Mockito.times(6)).reviewProfile(Mockito.anyString());
    }

    @Test
    public void testUnreviewAccounts() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);        

        AdminController adminController = new AdminController();        
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);

        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
        String commaSeparatedValues = "not-found-email1@test.com,not-found-email2@test.com,record-unreviewed-email1@test.com,record-unreviewed-email2@test.com,successful-email1@test.com,successful-email2@test.com,successful-email3@test.com,successful-email4@test.com,0000-0000-0000-0001,https://orcid.org/0000-0000-0000-0002,0000-0000-0000-0003,https://orcid.org/0000-0000-0000-0004,notAnOrcidIdOrEmail";

        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("record-unreviewed-email1@test.com", "record-unreviewed-email1@test.com");
        map.put("record-unreviewed-email2@test.com", "record-unreviewed-email2@test.com");
        map.put("successful-email1@test.com", "successful-email1@test.com");
        map.put("successful-email2@test.com", "successful-email2@test.com");
        map.put("successful-email3@test.com", "successful-email3@test.com");
        map.put("successful-email4@test.com", "successful-email4@test.com");

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {

            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("record-unreviewed-email1@test.com") || ar1.equals("record-unreviewed-email2@test.com")
                        || ar1.equals("0000-0000-0000-0001") || ar1.equals("0000-0000-0000-0002")) {
                    p.setReviewed(false);
                } else {
                    p.setReviewed(true);
                }
                return p;
            }

        });

        Mockito.when(profileEntityManager.reviewProfile("not-found-email1@test.com")).thenThrow(new RuntimeException("Controller shouldn't try to review null profile"));
        Mockito.when(profileEntityManager.reviewProfile("not-found-email2@test.com")).thenThrow(new RuntimeException("Controller shouldn't try to review null profile"));
        Mockito.when(profileEntityManager.reviewProfile("record-unreviewed-email1@test.com")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("record-unreviewed-email2@test.com")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("0000-0000-0000-0001")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("0000-0000-0000-0002")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("successful-email1@test.com")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("successful-email2@test.com")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("successful-email3@test.com")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("successful-email4@test.com")).thenReturn(true);       
        Mockito.when(profileEntityManager.reviewProfile("0000-0000-0000-0003")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("0000-0000-0000-0004")).thenReturn(true);

        Map<String, Set<String>> results = adminController.unreviewRecords(mockRequest, mockResponse, commaSeparatedValues);
        assertEquals(3, results.get("notFound").size());
        assertTrue(results.get("notFound").contains("not-found-email1@test.com"));
        assertTrue(results.get("notFound").contains("not-found-email2@test.com"));
        assertTrue(results.get("notFound").contains("notAnOrcidIdOrEmail"));

        assertEquals(4, results.get("alreadyUnreviewed").size());
        assertTrue(results.get("alreadyUnreviewed").contains("record-unreviewed-email1@test.com"));
        assertTrue(results.get("alreadyUnreviewed").contains("record-unreviewed-email2@test.com"));
        assertTrue(results.get("alreadyUnreviewed").contains("0000-0000-0000-0001"));
        assertTrue(results.get("alreadyUnreviewed").contains("https://orcid.org/0000-0000-0000-0002"));

        assertEquals(6, results.get("successful").size());
        assertTrue(results.get("successful").contains("successful-email1@test.com"));
        assertTrue(results.get("successful").contains("successful-email2@test.com"));
        assertTrue(results.get("successful").contains("successful-email3@test.com"));
        assertTrue(results.get("successful").contains("successful-email4@test.com"));
        assertTrue(results.get("successful").contains("0000-0000-0000-0003"));
        assertTrue(results.get("successful").contains("https://orcid.org/0000-0000-0000-0004"));


        Mockito.verify(emailManager, Mockito.times(8)).emailExists(Mockito.anyString());
        Mockito.verify(profileEntityManager, Mockito.times(6)).unreviewProfile(Mockito.anyString());
    }

    @Test
    public void testGetLockReasons() {
        AdminManager adminManager = Mockito.mock(AdminManager.class);
        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "adminManager", adminManager);
        TargetProxyHelper.injectIntoProxy(adminController, "orcidSecurityManager", mockOrcidSecurityManager);
        List<String> reasons = adminController.getLockReasons();
        Mockito.verify(adminManager, Mockito.times(1)).getLockReasons();
    }

    @Test
    public void resendClaimEmail() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);
        NotificationManager notificationManager = Mockito.mock(NotificationManager.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);        

        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);                                                                    
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(adminController, "notificationManager", notificationManager);
        ReflectionTestUtils.setField(adminController, "recordEmailSender", mockRecordEmailSender);

        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
        String commaSeparatedValues = "not-found-email1@test.com,not-found-email2@test.com,record-claimed-email1@test.com,record-claimed-email2@test.com,successful-email1@test.com,successful-email2@test.com,successful-email3@test.com,successful-email4@test.com,0000-0000-0000-0001,https://orcid.org/0000-0000-0000-0002,0000-0000-0000-0003,https://orcid.org/0000-0000-0000-0004,notAnOrcidIdOrEmail";
        
        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("record-claimed-email1@test.com", "record-claimed-email1@test.com");
        map.put("record-claimed-email2@test.com", "record-claimed-email2@test.com");
        map.put("successful-email1@test.com", "successful-email1@test.com");
        map.put("successful-email2@test.com", "successful-email2@test.com");
        map.put("successful-email3@test.com", "successful-email3@test.com");
        map.put("successful-email4@test.com", "successful-email4@test.com");              

        Email emailFromOrcid = new Email();
        emailFromOrcid.setEmail("email@test.com");
        Mockito.when(emailManager.findPrimaryEmail(Mockito.anyString())).thenReturn(emailFromOrcid);
        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);
        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {

            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("record-claimed-email1@test.com") || ar1.equals("record-claimed-email2@test.com")
                        || ar1.equals("0000-0000-0000-0001") || ar1.equals("0000-0000-0000-0002")) {
                    p.setClaimed(true);
                } else {
                    p.setClaimed(false);
                }
                return p;
            }

        });              
      
        Map<String, List<String>> results = adminController.resendClaimEmail(mockRequest, mockResponse, commaSeparatedValues);
        assertEquals(3, results.get("notFound").size());
        assertTrue(results.get("notFound").contains("not-found-email1@test.com"));
        assertTrue(results.get("notFound").contains("not-found-email2@test.com"));
        assertTrue(results.get("notFound").contains("notAnOrcidIdOrEmail"));

        assertEquals(4, results.get("alreadyClaimed").size());
        assertTrue(results.get("alreadyClaimed").contains("record-claimed-email1@test.com"));
        assertTrue(results.get("alreadyClaimed").contains("record-claimed-email2@test.com"));
        assertTrue(results.get("alreadyClaimed").contains("0000-0000-0000-0001"));
        assertTrue(results.get("alreadyClaimed").contains("https://orcid.org/0000-0000-0000-0002"));

        assertEquals(6, results.get("successful").size());
        assertTrue(results.get("successful").contains("successful-email1@test.com"));
        assertTrue(results.get("successful").contains("successful-email2@test.com"));
        assertTrue(results.get("successful").contains("successful-email3@test.com"));
        assertTrue(results.get("successful").contains("successful-email4@test.com"));
        assertTrue(results.get("successful").contains("0000-0000-0000-0003"));
        assertTrue(results.get("successful").contains("https://orcid.org/0000-0000-0000-0004"));

        Mockito.verify(emailManager, Mockito.times(8)).emailExists(Mockito.anyString());        
        Mockito.verify(mockRecordEmailSender, Mockito.times(6)).sendClaimReminderEmail(Mockito.anyString(),Mockito.anyInt(), Mockito.nullable(String.class));
    }

    @Test
    public void deactivateOrcidRecords() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);        
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);

        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);        

        String commaSeparatedValues = "not-found-email1@test.com,not-found-email2@test.com,record-deactivated-email1@test.com,record-deactivated-email2@test.com,successful-email1@test.com,successful-email2@test.com,successful-email3@test.com,successful-email4@test.com,0000-0000-0000-0001,https://orcid.org/0000-0000-0000-0002,0000-0000-0000-0003,https://orcid.org/0000-0000-0000-0004,notAnOrcidIdOrEmail";
        
        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("record-deactivated-email1@test.com", "record-deactivated-email1@test.com");
        map.put("record-deactivated-email2@test.com", "record-deactivated-email2@test.com");
        map.put("successful-email1@test.com", "successful-email1@test.com");
        map.put("successful-email2@test.com", "successful-email2@test.com");
        map.put("successful-email3@test.com", "successful-email3@test.com");
        map.put("successful-email4@test.com", "successful-email4@test.com");              

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {

            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("record-deactivated-email1@test.com") || ar1.equals("record-deactivated-email2@test.com")
                        || ar1.equals("0000-0000-0000-0001") || ar1.equals("0000-0000-0000-0002")) {
                    p.setDeactivationDate(new Date());
                } else {
                    p.setDeactivationDate(null);
                }
                return p;
            }

        });              
      
        Map<String, Set<String>> results = adminController.deactivateOrcidRecords(mockRequest, mockResponse, commaSeparatedValues);
        assertEquals(3, results.get("notFoundList").size());
        assertTrue(results.get("notFoundList").contains("not-found-email1@test.com"));
        assertTrue(results.get("notFoundList").contains("not-found-email2@test.com"));
        assertTrue(results.get("notFoundList").contains("notAnOrcidIdOrEmail"));

        assertEquals(4, results.get("alreadyDeactivated").size());
        assertTrue(results.get("alreadyDeactivated").contains("record-deactivated-email1@test.com"));
        assertTrue(results.get("alreadyDeactivated").contains("record-deactivated-email2@test.com"));
        assertTrue(results.get("alreadyDeactivated").contains("0000-0000-0000-0001"));
        assertTrue(results.get("alreadyDeactivated").contains("https://orcid.org/0000-0000-0000-0002"));

        assertEquals(6, results.get("success").size());
        assertTrue(results.get("success").contains("successful-email1@test.com"));
        assertTrue(results.get("success").contains("successful-email2@test.com"));
        assertTrue(results.get("success").contains("successful-email3@test.com"));
        assertTrue(results.get("success").contains("successful-email4@test.com"));
        assertTrue(results.get("success").contains("0000-0000-0000-0003"));
        assertTrue(results.get("success").contains("https://orcid.org/0000-0000-0000-0004"));

        Mockito.verify(emailManager, Mockito.times(8)).emailExists(Mockito.anyString());        
        Mockito.verify(profileEntityManager, Mockito.times(6)).deactivateRecord(Mockito.anyString());

    }

    @Test
    public void startDelegationProcess() throws Exception {
        
        AdminManager adminManager = Mockito.mock(AdminManager.class);                
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);        
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);        
        LocaleManager localeManager = Mockito.mock(LocaleManager.class);                                                
        
        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "adminManager", adminManager);                                                                       
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);        
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(adminController, "localeManager", localeManager);
        
        AdminDelegatesRequest adminDelegatesRequest = new AdminDelegatesRequest(); 
        Text trusted = new Text();
        trusted.setValue("https://orcid.org/0000-0000-0000-00020000-0000-0000-0001");
        Text managed = new Text();
        managed.setValue("0000-0000-0000-0002");
        adminDelegatesRequest.setTrusted(trusted);       
        adminDelegatesRequest.setManaged(managed);                      
        
        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);
                       
        Mockito.when(localeManager.resolveMessage(Mockito.anyString(), Mockito.any())).thenReturn("Email or ORCID iD is 0000-0000-0000-0001 invalid");                                     
        
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {

            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("0000-0000-0000-0001") || ar1.equals("0000-0000-0000-0002")) {                    
                    p.setRecordLocked(false);                                   
                } else {
                    p.setRecordLocked(true);
                }
                return p;
            }

        });              
      
        adminController.startDelegationProcess(mockRequest, mockResponse, adminDelegatesRequest);
                
        Mockito.verify(adminManager, Mockito.times(1)).startDelegationProcess(Mockito.any(), Mockito.anyString(), Mockito.anyString());
        
        adminDelegatesRequest = new AdminDelegatesRequest(); 
        trusted = new Text();
        trusted.setValue("not-found-email1@test.com");
        managed = new Text();
        managed.setValue("not-found-email2@test.com");
        adminDelegatesRequest.setTrusted(trusted);       
        adminDelegatesRequest.setManaged(managed);   
        
        AdminDelegatesRequest results = adminController.startDelegationProcess(mockRequest, mockResponse, adminDelegatesRequest);

        assertEquals(1, results.getManaged().getErrors().size());
        assertEquals(1, results.getTrusted().getErrors().size());
    }
    
    @Test
    public void adminSwitchUser() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);        
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);

        AdminController adminController = new AdminController();
               
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);                
        
        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);                            
        
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);                
      
        Map<String, String> results = adminController.adminSwitchUser(mockRequest, mockResponse, "not-found-email1@test.com");

        assertEquals("Invalid id not-found-email1@test.com", results.get("errorMessg"));        
   
        results = adminController.adminSwitchUser(mockRequest, mockResponse, "not-found-email2@test.com");
        
        assertEquals("Invalid id not-found-email2@test.com", results.get("errorMessg"));
        
        results = adminController.adminSwitchUser(mockRequest, mockResponse, "0000-0000-0000-0001");
        
        assertEquals("0000-0000-0000-0001", results.get("id"));
        
        results = adminController.adminSwitchUser(mockRequest, mockResponse, "https://orcid.org/0000-0000-0000-0002");
        
        assertEquals("0000-0000-0000-0002", results.get("id"));

    }     
 
    @Test
    public void resetPasswordValidateId() throws Exception {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);        
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);
        LocaleManager localeManager = Mockito.mock(LocaleManager.class);                                                
        
        AdminController adminController = new AdminController();
               
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(adminController, "localeManager", localeManager);
        
        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);                            
        
        Mockito.when(localeManager.resolveMessage(Mockito.anyString(), Mockito.any())).thenReturn("That ORCID iD is not on our records");       
        
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);                
      
        AdminChangePassword adminChangePassword = new AdminChangePassword();
        adminChangePassword.setOrcidOrEmail("0000-0000-0000-0001");
        
        AdminChangePassword results = adminController.resetPasswordValidateId(mockRequest, mockResponse, adminChangePassword);        

        assertEquals(null, results.getError());        

        adminChangePassword = new AdminChangePassword();
        adminChangePassword.setOrcidOrEmail("https://orcid.org/0000-0000-0000-0002");
        
        results = adminController.resetPasswordValidateId(mockRequest, mockResponse, adminChangePassword);        

        assertEquals(null, results.getError());   
        
        adminChangePassword = new AdminChangePassword();
        adminChangePassword.setOrcidOrEmail("not-found-email1@test.com");
        
        results = adminController.resetPasswordValidateId(mockRequest, mockResponse, adminChangePassword);
        
        assertEquals("That ORCID iD is not on our records", results.getError());
        
        adminChangePassword = new AdminChangePassword();
        adminChangePassword.setOrcidOrEmail("not-found-email2@test.com");
        
        results = adminController.resetPasswordValidateId(mockRequest, mockResponse, adminChangePassword);
        
        assertEquals("That ORCID iD is not on our records", results.getError());        

    }
    
    @Test
    public void testValidateClientConversion() throws IllegalAccessException, UnsupportedEncodingException {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ClientDetailsManager clientDetailsManager = Mockito.mock(ClientDetailsManager.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);
        
        ClientDetailsEntity publicClient = new ClientDetailsEntity();
        publicClient.setClientType(ClientType.PUBLIC_CLIENT.name());
        
        ClientDetailsEntity memberClient = new ClientDetailsEntity();
        memberClient.setClientType(ClientType.PREMIUM_UPDATER.name());
        
        ProfileEntity group = new ProfileEntity();
        group.setId("legal-group");
        group.setOrcidType(OrcidType.GROUP.name());
        
        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        Mockito.when(clientDetailsManager.exists(Mockito.eq("public-client"))).thenReturn(true);
        Mockito.when(clientDetailsManager.exists(Mockito.eq("member-client"))).thenReturn(true);
        Mockito.when(clientDetailsManager.exists(Mockito.eq("nothing"))).thenReturn(false);
        Mockito.when(clientDetailsManager.findByClientId(Mockito.eq("public-client"))).thenReturn(publicClient);
        Mockito.when(clientDetailsManager.findByClientId(Mockito.eq("member-client"))).thenReturn(memberClient);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.eq("nothing"))).thenThrow(new IllegalArgumentException());
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.eq("no-group"))).thenReturn(null);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.eq("legal-group"))).thenReturn(group);
        
        AdminController adminController = new AdminController();
        
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(adminController, "clientDetailsManager", clientDetailsManager);
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);
        
        ConvertClient data = new ConvertClient();
        
        data = adminController.validateClientConversion(mockRequest, mockResponse, data);
        assertTrue(data.isClientNotFound());
        
        data.setClientId("invalid");
        data = adminController.validateClientConversion(mockRequest, mockResponse, data);
        assertTrue(data.isClientNotFound());
        
        data.setClientId("member-client");
        data = adminController.validateClientConversion(mockRequest, mockResponse, data);
        assertTrue(data.isAlreadyMember());

        data.setClientId("public-client");
        data = adminController.validateClientConversion(mockRequest, mockResponse, data);
        assertTrue(data.isGroupIdNotFound());
        
        data.setGroupId("nothing");
        data = adminController.validateClientConversion(mockRequest, mockResponse, data);
        assertTrue(data.isGroupIdNotFound());

        data.setGroupId("no-group");
        data = adminController.validateClientConversion(mockRequest, mockResponse, data);
        assertTrue(data.isGroupIdNotFound());
        
        data.setGroupId("legal-group");
        data = adminController.validateClientConversion(mockRequest, mockResponse, data);
        assertFalse(data.isGroupIdNotFound());
    }
    
    @Test
    public void testConvertClient() throws IllegalAccessException, UnsupportedEncodingException {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ClientDetailsManager clientDetailsManager = Mockito.mock(ClientDetailsManager.class);
        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);
        
        ClientDetailsEntity publicClient = new ClientDetailsEntity();
        publicClient.setClientType(ClientType.PUBLIC_CLIENT.name());
        
        ClientDetailsEntity memberClient = new ClientDetailsEntity();
        memberClient.setClientType(ClientType.PREMIUM_UPDATER.name());
        
        ProfileEntity group = new ProfileEntity();
        group.setId("legal-group");
        group.setOrcidType(OrcidType.GROUP.name());
        
        Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        Mockito.when(clientDetailsManager.exists(Mockito.eq("public-client"))).thenReturn(true);
        Mockito.when(clientDetailsManager.exists(Mockito.eq("member-client"))).thenReturn(true);
        Mockito.when(clientDetailsManager.exists(Mockito.eq("nothing"))).thenReturn(false);
        Mockito.when(clientDetailsManager.findByClientId(Mockito.eq("public-client"))).thenReturn(publicClient);
        Mockito.when(clientDetailsManager.findByClientId(Mockito.eq("member-client"))).thenReturn(memberClient);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.eq("nothing"))).thenThrow(new IllegalArgumentException());
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.eq("no-group"))).thenReturn(null);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.eq("legal-group"))).thenReturn(group);
        Mockito.doNothing().when(clientDetailsManager).convertPublicClientToMember(Mockito.eq("public-client"), Mockito.eq("legal-group"));
        
        AdminController adminController = new AdminController();
        
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);
        ReflectionTestUtils.setField(adminController, "clientDetailsManager", clientDetailsManager);
        ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);
        
        ConvertClient data = new ConvertClient();
        
        data = adminController.convertClient(mockRequest, mockResponse, data);
        assertTrue(data.isClientNotFound());
        assertFalse(data.isSuccess());
        
        data.setClientId("invalid");
        data = adminController.convertClient(mockRequest, mockResponse, data);
        assertTrue(data.isClientNotFound());
        assertFalse(data.isSuccess());
        
        data.setClientId("member-client");
        data = adminController.convertClient(mockRequest, mockResponse, data);
        assertTrue(data.isAlreadyMember());
        assertFalse(data.isSuccess());

        data.setClientId("public-client");
        data = adminController.convertClient(mockRequest, mockResponse, data);
        assertTrue(data.isGroupIdNotFound());
        assertFalse(data.isSuccess());
        
        data.setGroupId("nothing");
        data = adminController.convertClient(mockRequest, mockResponse, data);
        assertTrue(data.isGroupIdNotFound());
        assertFalse(data.isSuccess());

        data.setGroupId("no-group");
        data = adminController.convertClient(mockRequest, mockResponse, data);
        assertTrue(data.isGroupIdNotFound());
        assertFalse(data.isSuccess());
        
        data.setGroupId("legal-group");
        data = adminController.convertClient(mockRequest, mockResponse, data);
        assertFalse(data.isGroupIdNotFound());
        assertTrue(data.isSuccess());

        Mockito.verify(clientDetailsManager).convertPublicClientToMember(Mockito.eq("public-client"), Mockito.eq("legal-group"));
    }
    
    @Test
    public void resetPasswordLink() throws Exception {
       VerifyEmailUtils verifyEmailUtils = Mockito.mock(VerifyEmailUtils.class);
       EncryptionManager encryptionManager= Mockito.mock(EncryptionManager.class);  
       OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManager.class);
       AdminController adminController = new AdminController();
       EmailManager emailManager = Mockito.mock(EmailManager.class);    
       LocaleManager localeManager = Mockito.mock(LocaleManager.class);  
       ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);

               
       ReflectionTestUtils.setField(adminController, "verifyEmailUtils", verifyEmailUtils);
       ReflectionTestUtils.setField(adminController, "encryptionManager", encryptionManager);
       ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
       ReflectionTestUtils.setField(adminController, "localeManager", localeManager);
       ReflectionTestUtils.setField(adminController, "orcidSecurityManager", orcidSecurityManager);
       ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        
       Mockito.when(orcidSecurityManager.isAdmin()).thenReturn(true);
        
       Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
       Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email1@test.com"))).thenReturn(false);
       Mockito.when(emailManager.emailExists(Mockito.eq("not-found-email2@test.com"))).thenReturn(false);                            
        
       Mockito.when(localeManager.resolveMessage(Mockito.anyString(), Mockito.any())).thenReturn("That email address is not on our records");       
       Mockito.when(verifyEmailUtils.createResetLinkForAdmin(Mockito.anyString(), Mockito.any())).thenReturn(new Pair<String, Date>("xyz", new Date())); 
       Mockito.when(localeManager.resolveMessage(Mockito.anyString(), Mockito.any())).thenReturn("That email address is not on our records"); 
       Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);

      
       AdminResetPasswordLink adminResetPasswordLink = new AdminResetPasswordLink();
       adminResetPasswordLink.setOrcidOrEmail("not-found-email1@test.com");
        
       adminResetPasswordLink = adminController.resetPasswordLink(mockRequest, mockResponse, adminResetPasswordLink);
        
       assertEquals("That email address is not on our records", adminResetPasswordLink.getError());
        
       adminResetPasswordLink = new AdminResetPasswordLink();
       Mockito.when(emailManager.findOrcidIdByEmail(Mockito.anyString())).thenReturn("0000-0002-0551-5914"); 
       adminResetPasswordLink.setOrcidOrEmail("existent_email@test.com");
       XMLGregorianCalendar date = DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(new Date());
       Mockito.when(encryptionManager.decryptForExternalUse(Mockito.anyString())).thenReturn("email=existent_email@test.com&issueDate="+ date.toXMLFormat()+ "&h=24"); 
       adminResetPasswordLink = adminController.resetPasswordLink(mockRequest, mockResponse, adminResetPasswordLink);
       assertNotNull(adminResetPasswordLink.getResetLink());
       assertEquals(24,adminResetPasswordLink.getDurationInHours());
       
    }
    
}