package org.orcid.frontend.web.controllers;

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.admin.LockReason;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.manager.v3.impl.ProfileHistoryEventManagerImpl;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Email;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.AdminChangePassword;
import org.orcid.pojo.LockAccounts;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:statistics-core-context.xml", "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
public class AdminControllerTest extends BaseControllerTest {

    @Resource(name = "adminController")
    AdminController adminController;

    @Resource
    private ProfileDao profileDao;

    @Resource
    protected OrcidProfileManager orcidProfileManager;

    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;
    
    @Mock
    private NotificationManager mockNotificationManager;
    
    @Mock
    private ProfileLastModifiedAspect mockProfileLastModifiedAspect; 
    
    @Mock
    private EmailManagerReadOnly mockEmailManagerReadOnly;
    
    @Mock
    private ProfileHistoryEventManager profileHistoryEventManager;
    
    @Mock
    private EmailFrequencyManager mockEmailFrequencyManager;
    
    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
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
        
        ReflectionTestUtils.setField(jpa2JaxbAdapter, "emailFrequencyManager", mockEmailFrequencyManager);
        when(mockEmailFrequencyManager.getEmailFrequency(anyString())).thenReturn(map);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        assertNotNull(adminController);
        assertNotNull(profileDao);
    }

    @After
    public void after() {
        ReflectionTestUtils.setField(jpa2JaxbAdapter, "emailFrequencyManager", emailFrequencyManager);
    }    
    
    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Override
    protected Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4440");
        List<OrcidWebRole> roles = getRole();
        OrcidProfileUserDetails details = new OrcidProfileUserDetails(orcidProfile.retrieveOrcidPath(),
                orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue(), null, roles);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(orcidProfile.retrieveOrcidPath(), orcidProfile.getPassword(), getRole());
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
        
        r = adminController.checkOrcidToDeprecate(r);
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
        ProfileDeprecationRequest result = adminController.deprecateProfile(r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.already_deprecated", "4444-4444-4444-444X"), result.getErrors().get(0));

        // Test deprecating account with himself
        toDeprecate.setOrcid("4444-4444-4444-4440");
        primary.setOrcid("4444-4444-4444-4440");
        result = adminController.deprecateProfile(r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.deprecated_equals_primary"), result.getErrors().get(0));

        // Test set deprecated account as a primary account
        toDeprecate.setOrcid("4444-4444-4444-4443");
        primary.setOrcid("4444-4444-4444-444X");
        result = adminController.deprecateProfile(r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_deprecated", "4444-4444-4444-444X"), result.getErrors().get(0));

        // Test deprecating an invalid orcid
        toDeprecate.setOrcid("4444-4444-4444-444");
        primary.setOrcid("4444-4444-4444-4443");
        result = adminController.deprecateProfile(r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.invalid_orcid", "4444-4444-4444-444"), result.getErrors().get(0));

        // Test use invalid orcid as primary
        toDeprecate.setOrcid("4444-4444-4444-4440");
        primary.setOrcid("4444-4444-4444-444");
        result = adminController.deprecateProfile(r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.invalid_orcid", "4444-4444-4444-444"), result.getErrors().get(0));

        // Deactivate primary record
        adminController.deactivateOrcidAccount("4444-4444-4444-4443");
        
        // Test set deactive primary account
        toDeprecate.setOrcid("4444-4444-4444-4440");
        primary.setOrcid("4444-4444-4444-4443");
        result = adminController.deprecateProfile(r);
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_is_deactivated", "4444-4444-4444-4443"), result.getErrors().get(0));
    }

    @Test
    public void deactivateAndReactivateProfileTest() throws Exception {
        ProfileHistoryEventManager profileHistoryEventManager = Mockito.mock(ProfileHistoryEventManagerImpl.class);
        ProfileEntityManager profileEntityManager = (ProfileEntityManager) ReflectionTestUtils.getField(adminController, "profileEntityManager");
        ReflectionTestUtils.setField(profileEntityManager, "profileHistoryEventManager", profileHistoryEventManager);
        Mockito.doNothing().when(profileHistoryEventManager).recordEvent(Mockito.any(ProfileHistoryEventType.class), Mockito.anyString(), Mockito.anyString());
        
        // Test deactivate
        Map<String, Set<String>> result = adminController.deactivateOrcidAccount("4444-4444-4444-4445");
        assertEquals(1, result.get("success").size());

        ProfileEntity deactivated = profileDao.find("4444-4444-4444-4445");
        assertNotNull(deactivated.getDeactivationDate());
        assertEquals(deactivated.getRecordNameEntity().getFamilyName(), "Family Name Deactivated");
        assertEquals(deactivated.getRecordNameEntity().getGivenNames(), "Given Names Deactivated");

        // Test try to deactivate an already deactive account
        result = adminController.deactivateOrcidAccount("4444-4444-4444-4445");
        assertEquals(1, result.get("alreadyDeactivated").size());

        // Test reactivate using an email address that belongs to other record
        ProfileDetails proDetails = new ProfileDetails();
        proDetails.setEmail("public_0000-0000-0000-0003@test.orcid.org");
        proDetails.setOrcid("4444-4444-4444-4445");
        proDetails = adminController.reactivateOrcidAccount(proDetails);
        assertEquals(1, proDetails.getErrors().size());
        assertEquals(adminController.getMessage("admin.errors.deactivated_account.orcid_id_dont_match", "0000-0000-0000-0003"), proDetails.getErrors().get(0));
        
        // Test reactivate using empty primary email
        proDetails.setEmail("");
        proDetails = adminController.reactivateOrcidAccount(proDetails);
        assertEquals(1, proDetails.getErrors().size());
        assertEquals(adminController.getMessage("admin.errors.deactivated_account.primary_email_required"), proDetails.getErrors().get(0));
        
        // Test reactivate
        proDetails.setEmail("andrew@timothy.com");
        proDetails = adminController.reactivateOrcidAccount(proDetails);
        assertEquals(0, proDetails.getErrors().size());

        deactivated = profileDao.find("4444-4444-4444-4445");
        assertNull(deactivated.getDeactivationDate());

        // Try to reactivate an already active account
        proDetails = adminController.reactivateOrcidAccount(proDetails);
        assertEquals(1, proDetails.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_reactivation.errors.already_active", new ArrayList<String>()), proDetails.getErrors().get(0));
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
    public void removeSecurityQuestionTest() {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4440");
        assertNotNull(orcidProfile.getSecurityQuestionAnswer());
        adminController.removeSecurityQuestion(null, "4444-4444-4444-4440");
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4440");
        assertNull(orcidProfile.getSecurityQuestionAnswer());
    }

    @Test
    public void removeSecurityQuestionUsingEmailTest() {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");
        assertNotNull(orcidProfile.getSecurityQuestionAnswer());
        adminController.removeSecurityQuestion(null, "michael@bentine.com");
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");
        assertNull(orcidProfile.getSecurityQuestionAnswer());
    }

    @Test
    public void resetPasswordTest() {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4441");
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=", orcidProfile.getPassword());
        AdminChangePassword form = new AdminChangePassword();
        form.setOrcidOrEmail("4444-4444-4444-4441");
        form.setPassword("password1");
        adminController.resetPassword(form);
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4441");
        assertFalse("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=".equals(orcidProfile.getPassword()));
    }

    @Test
    public void resetPasswordUsingEmailTest() {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=", orcidProfile.getPassword());
        AdminChangePassword form = new AdminChangePassword();
        form.setOrcidOrEmail("michael@bentine.com");
        form.setPassword("password1");
        adminController.resetPassword(form);
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");
        assertFalse("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=".equals(orcidProfile.getPassword()));
    }

    @Test
    public void verifyEmailTest() {
        TargetProxyHelper.injectIntoProxy(emailManager, "notificationManager", mockNotificationManager);
        TargetProxyHelper.injectIntoProxy(adminController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("not-verified@email.com")).thenReturn("4444-4444-4444-4499");
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        
        // Add not verified email
        Email email = new Email();
        email.setEmail("not-verified@email.com");
        email.setCurrent(false);
        email.setPrimary(false);
        email.setVerified(false);
        email.setVisibility(Visibility.PUBLIC);
        emailManager.addEmail(request, "4444-4444-4444-4499", email);

        // Verify the email
        adminController.adminVerifyEmail("not-verified@email.com");
        EmailEntity emailEntity = emailManager.find("not-verified@email.com");
        assertNotNull(emailEntity);
        assertTrue(emailEntity.getVerified());
        TargetProxyHelper.injectIntoProxy(emailManager, "notificationManager", notificationManager);
    }

    @Test
    public void testLockAccounts() {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);

        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);

        String commaSeparatedValues = "some,orcid,ids,or,emails,to,test,with,reviewed";

        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("some"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("orcid"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("ids", "ids");
        map.put("or", "or");
        map.put("emails", "emails");
        map.put("to", "to");
        map.put("test", "test");
        map.put("with", "with");
        map.put("reviewed", "reviewed");

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);

        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("some"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("orcid"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("ids") || ar1.equals("or")) {
                    p.setRecordLocked(true);
                } else {
                    p.setRecordLocked(false);
                }

                if (ar1.contentEquals("reviewed")) {
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

        Map<String, Set<String>> results = adminController.lockAccounts(lockAccounts);
        assertEquals(2, results.get("notFoundList").size());
        assertTrue(results.get("notFoundList").contains("some"));
        assertTrue(results.get("notFoundList").contains("orcid"));

        assertEquals(2, results.get("alreadyLockedList").size());
        assertTrue(results.get("alreadyLockedList").contains("ids"));
        assertTrue(results.get("alreadyLockedList").contains("or"));

        assertEquals(4, results.get("lockSuccessfulList").size());
        assertTrue(results.get("lockSuccessfulList").contains("emails"));
        assertTrue(results.get("lockSuccessfulList").contains("to"));
        assertTrue(results.get("lockSuccessfulList").contains("test"));
        assertTrue(results.get("lockSuccessfulList").contains("with"));

        assertEquals(1, results.get("reviewedList").size());
        assertTrue(results.get("reviewedList").contains("reviewed"));

        Mockito.verify(emailManager, Mockito.times(9)).emailExists(Mockito.anyString());
        Mockito.verify(profileEntityManager, Mockito.times(4)).lockProfile(Mockito.anyString(), Mockito.anyString(), isNull());
    }

    @Test
    public void testUnlockAccounts() {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);

        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);

        String commaSeparatedValues = "some,orcid,ids,or,emails,to,test,with";

        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("some"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("orcid"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("ids", "ids");
        map.put("or", "or");
        map.put("emails", "emails");
        map.put("to", "to");
        map.put("test", "test");
        map.put("with", "with");

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);

        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("some"))).thenReturn(false);
        Mockito.when(profileEntityManager.orcidExists(Mockito.eq("orcid"))).thenReturn(false);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("ids") || ar1.equals("or")) {
                    p.setRecordLocked(false);
                } else {
                    p.setRecordLocked(true);
                }

                return p;
            }

        });

        Map<String, Set<String>> results = adminController.unlockAccounts(commaSeparatedValues);
        assertEquals(2, results.get("notFoundList").size());
        assertTrue(results.get("notFoundList").contains("some"));
        assertTrue(results.get("notFoundList").contains("orcid"));

        assertEquals(2, results.get("alreadyUnlockedList").size());
        assertTrue(results.get("alreadyUnlockedList").contains("ids"));
        assertTrue(results.get("alreadyUnlockedList").contains("or"));

        assertEquals(4, results.get("unlockSuccessfulList").size());
        assertTrue(results.get("unlockSuccessfulList").contains("emails"));
        assertTrue(results.get("unlockSuccessfulList").contains("to"));
        assertTrue(results.get("unlockSuccessfulList").contains("test"));
        assertTrue(results.get("unlockSuccessfulList").contains("with"));

        Mockito.verify(emailManager, Mockito.times(8)).emailExists(Mockito.anyString());
        Mockito.verify(profileEntityManager, Mockito.times(4)).unlockProfile(Mockito.anyString());
    }

    @Test
    public void testReviewAccounts() {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);

        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);

        String commaSeparatedValues = "some,orcid,ids,or,emails,to,test,with";

        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("some"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("orcid"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("ids", "ids");
        map.put("or", "or");
        map.put("emails", "emails");
        map.put("to", "to");
        map.put("test", "test");
        map.put("with", "with");

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {

            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("ids") || ar1.equals("or")) {
                    p.setReviewed(true);
                } else {
                    p.setReviewed(false);
                }
                return p;
            }

        });

        Mockito.when(profileEntityManager.reviewProfile("some")).thenThrow(new RuntimeException("Controller shouldn't try to review null profile"));
        Mockito.when(profileEntityManager.reviewProfile("orcid")).thenThrow(new RuntimeException("Controller shouldn't try to review null profile"));
        Mockito.when(profileEntityManager.reviewProfile("ids")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("or")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));

        Mockito.when(profileEntityManager.reviewProfile("emails")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("to")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("test")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("with")).thenReturn(true);

        Map<String, Set<String>> results = adminController.reviewAccounts(commaSeparatedValues);
        assertEquals(2, results.get("notFoundList").size());
        assertTrue(results.get("notFoundList").contains("some"));
        assertTrue(results.get("notFoundList").contains("orcid"));

        assertEquals(2, results.get("alreadyReviewedList").size());
        assertTrue(results.get("alreadyReviewedList").contains("ids"));
        assertTrue(results.get("alreadyReviewedList").contains("or"));

        assertEquals(4, results.get("reviewSuccessfulList").size());
        assertTrue(results.get("reviewSuccessfulList").contains("emails"));
        assertTrue(results.get("reviewSuccessfulList").contains("to"));
        assertTrue(results.get("reviewSuccessfulList").contains("test"));
        assertTrue(results.get("reviewSuccessfulList").contains("with"));

        Mockito.verify(emailManager, Mockito.times(8)).emailExists(Mockito.anyString());
        Mockito.verify(profileEntityManager, Mockito.times(4)).reviewProfile(Mockito.anyString());
    }

    @Test
    public void testUnreviewAccounts() {
        ProfileEntityCacheManager profileEntityCacheManager = Mockito.mock(ProfileEntityCacheManager.class);
        ProfileEntityManager profileEntityManager = Mockito.mock(ProfileEntityManager.class);
        EmailManager emailManager = Mockito.mock(EmailManager.class);

        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "profileEntityManager", profileEntityManager);
        ReflectionTestUtils.setField(adminController, "emailManager", emailManager);
        ReflectionTestUtils.setField(adminController, "profileEntityCacheManager", profileEntityCacheManager);

        String commaSeparatedValues = "some,orcid,ids,or,emails,to,test,with";

        Mockito.when(emailManager.emailExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(emailManager.emailExists(Mockito.eq("some"))).thenReturn(false);
        Mockito.when(emailManager.emailExists(Mockito.eq("orcid"))).thenReturn(false);

        Map<String, String> map = new HashMap<String, String>();
        map.put("ids", "ids");
        map.put("or", "or");
        map.put("emails", "emails");
        map.put("to", "to");
        map.put("test", "test");
        map.put("with", "with");

        Mockito.when(emailManager.findOricdIdsByCommaSeparatedEmails(Mockito.anyString())).thenReturn(map);
        Mockito.when(profileEntityManager.orcidExists(Mockito.anyString())).thenReturn(true);
        Mockito.when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenAnswer(new Answer<ProfileEntity>() {

            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                String ar1 = invocation.getArgument(0);
                ProfileEntity p = new ProfileEntity();
                p.setId(ar1);
                if (ar1.equals("ids") || ar1.equals("or")) {
                    p.setReviewed(false);
                } else {
                    p.setReviewed(true);
                }
                return p;
            }

        });

        Mockito.when(profileEntityManager.reviewProfile("some")).thenThrow(new RuntimeException("Controller shouldn't try to review null profile"));
        Mockito.when(profileEntityManager.reviewProfile("orcid")).thenThrow(new RuntimeException("Controller shouldn't try to review null profile"));
        Mockito.when(profileEntityManager.reviewProfile("ids")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("or")).thenThrow(new RuntimeException("Controller shouldn't try to review reviewed profile"));
        Mockito.when(profileEntityManager.reviewProfile("emails")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("to")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("test")).thenReturn(true);
        Mockito.when(profileEntityManager.reviewProfile("with")).thenReturn(true);

        Map<String, Set<String>> results = adminController.unreviewAccounts(commaSeparatedValues);
        assertEquals(2, results.get("notFoundList").size());
        assertTrue(results.get("notFoundList").contains("some"));
        assertTrue(results.get("notFoundList").contains("orcid"));

        assertEquals(2, results.get("alreadyUnreviewedList").size());
        assertTrue(results.get("alreadyUnreviewedList").contains("ids"));
        assertTrue(results.get("alreadyUnreviewedList").contains("or"));

        assertEquals(4, results.get("unreviewSuccessfulList").size());
        assertTrue(results.get("unreviewSuccessfulList").contains("emails"));
        assertTrue(results.get("unreviewSuccessfulList").contains("to"));
        assertTrue(results.get("unreviewSuccessfulList").contains("test"));
        assertTrue(results.get("unreviewSuccessfulList").contains("with"));

        Mockito.verify(emailManager, Mockito.times(8)).emailExists(Mockito.anyString());
        Mockito.verify(profileEntityManager, Mockito.times(4)).unreviewProfile(Mockito.anyString());
    }

    @Test
    public void testGetLockReasons() {
        AdminManager adminManager = Mockito.mock(AdminManager.class);
        AdminController adminController = new AdminController();
        ReflectionTestUtils.setField(adminController, "adminManager", adminManager);

        adminController.getLockReasons();

        Mockito.verify(adminManager, Mockito.times(1)).getLockReasons();
    }

}