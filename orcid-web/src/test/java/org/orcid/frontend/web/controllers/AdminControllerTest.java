package org.orcid.frontend.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AdminManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.*;
import org.orcid.core.manager.v3.read_only.*;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.VerifyEmailUtils;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.common.OrcidType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.*;
import org.orcid.pojo.ajaxForm.*;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    private static final String INVALID_ID = "0000-0000-0000-0000";
    private static final String MEMBER_ID = "0000-0000-0000-0001";

    HttpServletRequest requestMock = mock(HttpServletRequest.class);

    HttpServletResponse responseMock = mock(HttpServletResponse.class);

    @Mock
    private ProfileDao profileDaoReadOnlyMock;

    @Mock
    private ClientManager clientManagerMock;

    @Mock
    private ClientManagerReadOnly clientManagerReadOnlyMock;

    @Mock
    private OrcidSecurityManager orcidSecurityManagerMock;

    @Mock
    private EmailManager emailManagerMock;

    @Mock
    private ProfileEntityManager profileEntityManagerMock;

    @Mock
    private AdminManager adminManagerMock;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManagerMock;

    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnlyMock;

    @Mock
    private ClientDetailsManager clientDetailsManagerMock;

    @Mock
    private VerifyEmailUtils verifyEmailUtilsMock;

    @Mock
    private SpamManager spamManagerMock;

    @Mock
    private RecordEmailSender recordEmailSenderMock;

    @Mock
    private TwoFactorAuthenticationManager twoFactorAuthenticationManagerMock;

    @Mock
    private EncryptionManager encryptionManagerMock;

    @Mock
    private LocaleManager localeManagerMock;

    @Mock
    private OrcidUrlManager orcidUrlManagerMock;

    @Mock
    private SourceManager sourceManagerMock;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnlyMock;

    @Mock
    private PersonalDetailsManagerReadOnly personalDetailsManagerReadOnlyMock;

    @Mock
    private AddressManagerReadOnly addressManagerReadOnlyMock;

    @Mock
    private ProfileKeywordManagerReadOnly keywordManagerReadOnlyMock;

    @Mock
    private ResearcherUrlManagerReadOnly researcherUrlManagerReadOnlyMock;

    @Mock
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnlyMock;

    @InjectMocks
    private AdminController adminController = new AdminController() {
        @Override
        public String getCurrentUserOrcid() {
            return "0000-0000-0000-0002";
        }

        @Override
        public String getMessage(String messageCode, Object... messageParams) {
            return messageCode;
        }

        @Override
        public void logoutCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        }

        @Override
        public Map<String, String> findIdByEmailHelper(String csvEmails) {
            Map<String, String> map = new HashMap<>();
            if (csvEmails.contains("exists@test.com")) {
                map.put("exists@test.com", "0000-0000-0000-0001");
            }
            return map;
        }
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(profileDaoReadOnlyMock.getGroupType(eq(INVALID_ID))).thenReturn(null);
        when(profileDaoReadOnlyMock.getGroupType(eq(MEMBER_ID))).thenReturn("PREMIUM_INSTITUTION");
        when(orcidSecurityManagerMock.isAdmin()).thenReturn(true);
    }

    @Test
    public void loadAdminPageTest() throws Exception {
        ModelAndView mav = adminController.loadAdminPage(requestMock, responseMock);
        assertNotNull(mav);
        assertEquals("/admin/admin_actions", mav.getViewName());
    }

    @Test(expected = IllegalAccessException.class)
    public void loadAdminPage_noAdminTest() throws Exception {
        when(orcidSecurityManagerMock.isAdmin()).thenReturn(false);
        adminController.loadAdminPage(requestMock, responseMock);
    }

    @Test
    public void deprecateProfileTest() throws Exception {
        ProfileDeprecationRequest request = new ProfileDeprecationRequest();
        request.getDeprecatedAccount().setOrcid(MEMBER_ID);
        request.getPrimaryAccount().setOrcid("0000-0000-0000-0002");
        
        Emails emails = new Emails();
        when(emailManagerMock.getEmails(eq(MEMBER_ID))).thenReturn(emails);
        when(adminManagerMock.deprecateProfile(any(), eq(MEMBER_ID), eq("0000-0000-0000-0002"), anyString())).thenReturn(true);
        
        ProfileDeprecationRequest result = adminController.deprecateProfile(requestMock, responseMock, request);
        
        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        assertEquals("admin.profile_deprecation.deprecate_account.success_message", result.getSuccessMessage());
    }

    @Test
    public void deprecateProfile_invalidOrcidTest() throws Exception {
        ProfileDeprecationRequest request = new ProfileDeprecationRequest();
        request.getDeprecatedAccount().setOrcid("invalid");
        request.getPrimaryAccount().setOrcid("0000-0000-0000-0002");
        
        ProfileDeprecationRequest result = adminController.deprecateProfile(requestMock, responseMock, request);
        
        assertFalse(result.getErrors().isEmpty());
        assertEquals("admin.profile_deprecation.errors.invalid_orcid", result.getErrors().get(0));
    }

    @Test
    public void checkOrcidToDeprecateTest() throws Exception {
        ProfileDeprecationRequest request = new ProfileDeprecationRequest();
        request.getDeprecatedAccount().setOrcid(MEMBER_ID);
        request.getPrimaryAccount().setOrcid("0000-0000-0000-0002");
        
        ProfileEntity entity = new ProfileEntity();
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(entity);
        when(profileEntityCacheManagerMock.retrieve(eq("0000-0000-0000-0002"))).thenReturn(new ProfileEntity());
        
        org.orcid.jaxb.model.v3.release.record.Name name = new org.orcid.jaxb.model.v3.release.record.Name();
        name.setFamilyName(new org.orcid.jaxb.model.v3.release.record.FamilyName("Family"));
        name.setGivenNames(new org.orcid.jaxb.model.v3.release.record.GivenNames("Given"));
        when(recordNameManagerReadOnlyMock.getRecordName(eq(MEMBER_ID))).thenReturn(name);
        when(recordNameManagerReadOnlyMock.getRecordName(eq("0000-0000-0000-0002"))).thenReturn(name);
        
        Email email = new Email();
        email.setEmail("test@test.com");
        when(emailManagerMock.findPrimaryEmail(eq(MEMBER_ID))).thenReturn(email);
        when(emailManagerMock.findPrimaryEmail(eq("0000-0000-0000-0002"))).thenReturn(email);
        
        ProfileDeprecationRequest result = adminController.checkOrcidToDeprecate(requestMock, responseMock, request);
        
        assertEquals("Family", result.getDeprecatedAccount().getFamilyName());
        assertEquals("Given", result.getDeprecatedAccount().getGivenNames());
        assertEquals("test@test.com", result.getDeprecatedAccount().getEmail());
    }

    @Test
    public void reactivateOrcidRecordTest() throws Exception {
        ProfileDetails details = new ProfileDetails();
        details.setOrcid(MEMBER_ID);
        details.setEmail("test@test.com");
        
        ProfileEntity entity = new ProfileEntity();
        entity.setDeactivationDate(new java.util.Date());
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(entity);
        when(emailManagerReadOnlyMock.emailExists(eq("test@test.com"))).thenReturn(true);
        when(emailManagerMock.findOrcidIdByEmail(eq("test@test.com"))).thenReturn(MEMBER_ID);
        
        when(profileEntityManagerMock.reactivate(eq(MEMBER_ID), eq("test@test.com"), any())).thenReturn(Collections.singletonList("test@test.com"));
        
        ProfileDetails result = adminController.reactivateOrcidRecord(requestMock, responseMock, details);
        
        assertEquals("admin.success", result.getStatus());
        verify(recordEmailSenderMock).sendVerificationEmail(eq(MEMBER_ID), eq("test@test.com"), anyBoolean());
    }

    @Test
    public void findIdByEmailTest() throws Exception {
        String csvEmails = "test@test.com,exists@test.com";
        List<ProfileDetails> result = adminController.findIdByEmail(requestMock, responseMock, csvEmails);
        
        assertEquals(1, result.size());
        assertEquals(MEMBER_ID, result.get(0).getOrcid());
    }

    @Test
    public void addEmailToRecordTest() throws Exception {
        ProfileDetails details = new ProfileDetails();
        details.setOrcid(MEMBER_ID);
        details.setEmail("new@test.com");
        
        ProfileEntity entity = new ProfileEntity();
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(entity);
        when(emailManagerMock.emailExists(eq("new@test.com"))).thenReturn(false);
        
        ProfileDetails result = adminController.addEmailToRecord(requestMock, responseMock, details);
        
        assertNotNull(result);
        assertTrue(result.getErrors().isEmpty());
        verify(emailManagerMock).addEmail(eq(MEMBER_ID), any());
    }

    @Test
    public void resetPasswordTest() throws Exception {
        AdminChangePassword form = new AdminChangePassword();
        form.setOrcidOrEmail(MEMBER_ID);
        form.setPassword("NewPassword123!");
        
        when(profileEntityManagerMock.orcidExists(eq(MEMBER_ID))).thenReturn(true);
        
        AdminChangePassword result = adminController.resetPassword(requestMock, responseMock, form);
        
        assertNull(result.getError());
        verify(profileEntityManagerMock).updatePassword(eq(MEMBER_ID), eq("NewPassword123!"));
    }

    @Test
    public void lockRecordsTest() throws Exception {
        LockAccounts lockAccounts = new LockAccounts();
        lockAccounts.setOrcidsToLock(MEMBER_ID);
        lockAccounts.setLockReason("Reason");
        lockAccounts.setDescription("Description");
        
        ProfileEntity entity = new ProfileEntity();
        when(profileEntityManagerMock.orcidExists(eq(MEMBER_ID))).thenReturn(true);
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(entity);
        when(profileEntityManagerMock.lockProfile(eq(MEMBER_ID), anyString(), anyString(), anyString())).thenReturn(true);
        
        Map<String, Set<String>> result = adminController.lockRecords(requestMock, responseMock, lockAccounts);
        
        assertTrue(result.get("successful").contains(MEMBER_ID));
        verify(recordEmailSenderMock).sendOrcidLockedEmail(eq(MEMBER_ID));
    }

    @Test
    public void disable2FATest() throws Exception {
        String orcid = MEMBER_ID;
        
        ProfileEntity entity = new ProfileEntity();
        entity.setUsing2FA(true);
        when(profileEntityManagerMock.orcidExists(eq(orcid))).thenReturn(true);
        when(profileEntityCacheManagerMock.retrieve(eq(orcid))).thenReturn(entity);
        
        Map<String, List<String>> result = adminController.disable2FA(requestMock, responseMock, orcid);
        
        assertTrue(result.get("disabledIds").contains(orcid));
        verify(twoFactorAuthenticationManagerMock).adminDisable2FA(eq(orcid), anyString());
    }

    @Test
    public void removeEmailsTest() throws Exception {
        RemoveEmailsRequest request = new RemoveEmailsRequest(MEMBER_ID, Collections.singletonList("old@test.com"));
        
        Email email = new Email();
        email.setEmail("primary@test.com");
        when(emailManagerMock.findPrimaryEmail(eq(MEMBER_ID))).thenReturn(email);
        when(emailManagerMock.getEmails(eq(MEMBER_ID))).thenReturn(new Emails());
        
        ResponseEntity<RemoveEmailsResponse> response = adminController.removeEmails(requestMock, responseMock, request);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(emailManagerMock).removeEmails(eq(MEMBER_ID), anyList());
    }

    @Test
    public void validateClientConversionTest() throws Exception {
        ConvertClient data = new ConvertClient();
        data.setClientId("APP-123456789");
        data.setGroupId(MEMBER_ID);
        
        when(clientDetailsManagerMock.exists(eq("APP-123456789"))).thenReturn(true);
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setClientType(ClientType.PUBLIC_CLIENT.name());
        when(clientDetailsManagerMock.findByClientId(eq("APP-123456789"))).thenReturn(clientDetails);
        
        ProfileEntity group = new ProfileEntity();
        group.setOrcidType(OrcidType.GROUP.name());
        group.setEnabled(true);
        group.setRecordLocked(false);
        group.setGroupType(MemberType.PREMIUM.name());
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(group);
        
        ConvertClient result = adminController.validateClientConversion(requestMock, responseMock, data);
        
        assertFalse(result.isClientNotFound());
        assertFalse(result.isGroupIdNotFound());
        assertEquals(ClientType.PREMIUM_UPDATER.name(), result.getTargetClientType());
    }

    @Test
    public void convertClientTest() throws Exception {
        ConvertClient data = new ConvertClient();
        data.setClientId("APP-123456789");
        data.setGroupId(MEMBER_ID);
        
        // Setup validation success
        when(clientDetailsManagerMock.exists(eq("APP-123456789"))).thenReturn(true);
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setClientType(ClientType.PUBLIC_CLIENT.name());
        when(clientDetailsManagerMock.findByClientId(eq("APP-123456789"))).thenReturn(clientDetails);
        
        ProfileEntity group = new ProfileEntity();
        group.setOrcidType(OrcidType.GROUP.name());
        group.setEnabled(true);
        group.setRecordLocked(false);
        group.setGroupType(MemberType.PREMIUM.name());
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(group);
        
        ConvertClient result = adminController.convertClient(requestMock, responseMock, data);
        
        assertTrue(result.isSuccess());
        verify(clientDetailsManagerMock).convertPublicClientToMember(eq("APP-123456789"), eq(MEMBER_ID));
    }

    @Test
    public void moveClientTest() throws Exception {
        ConvertClient data = new ConvertClient();
        data.setClientId("APP-123456789");
        data.setGroupId(MEMBER_ID);
        
        when(clientDetailsManagerMock.exists(eq("APP-123456789"))).thenReturn(true);
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setClientType(ClientType.PREMIUM_UPDATER.name());
        when(clientDetailsManagerMock.findByClientId(eq("APP-123456789"))).thenReturn(clientDetails);
        
        ProfileEntity group = new ProfileEntity();
        group.setOrcidType(OrcidType.GROUP.name());
        group.setEnabled(true);
        group.setRecordLocked(false);
        group.setGroupType(MemberType.PREMIUM.name());
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(group);
        
        ConvertClient result = adminController.moveClient(requestMock, responseMock, data);
        
        assertTrue(result.isSuccess());
        verify(clientDetailsManagerMock).moveClientGroupId(eq("APP-123456789"), eq(MEMBER_ID));
    }

    @Test
    public void checkClaimedStatusTest() throws Exception {
        when(profileEntityManagerMock.orcidExists(eq(MEMBER_ID))).thenReturn(true);
        when(profileEntityManagerMock.isProfileClaimed(eq(MEMBER_ID))).thenReturn(true);
        
        boolean result = adminController.checkClaimedStatus(requestMock, responseMock, MEMBER_ID);
        
        assertTrue(result);
    }

    @Test
    public void getLockReasonsTest() throws Exception {
        List<String> reasons = Arrays.asList("Reason 1", "Reason 2");
        when(adminManagerMock.getLockReasons()).thenReturn(reasons);
        
        List<String> result = adminController.getLockReasons();
        
        assertEquals(reasons, result);
    }

    @Test
    public void resendClaimEmailTest() throws Exception {
        String orcid = MEMBER_ID;
        
        ProfileEntity entity = new ProfileEntity();
        entity.setClaimed(false);
        when(profileEntityManagerMock.orcidExists(eq(orcid))).thenReturn(true);
        when(profileEntityCacheManagerMock.retrieve(eq(orcid))).thenReturn(entity);
        
        Email primary = new Email();
        primary.setEmail("test@test.com");
        when(emailManagerMock.findPrimaryEmail(eq(orcid))).thenReturn(primary);
        
        Map<String, List<String>> result = adminController.resendClaimEmail(requestMock, responseMock, orcid);
        
        assertTrue(result.get("successful").contains(orcid));
        verify(recordEmailSenderMock).sendClaimReminderEmail(eq(orcid), eq(0), eq("test@test.com"));
    }
    
    @Test
    public void adminVerifyEmailTest() throws Exception {
        String email = "test@test.com";
        when(emailManagerMock.emailExists(eq(email))).thenReturn(true);
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(eq(email))).thenReturn(MEMBER_ID);
        
        String result = adminController.adminVerifyEmail(requestMock, responseMock, email);
        
        assertNotNull(result);
        verify(emailManagerMock).verifyEmail(eq(MEMBER_ID), eq(email));
    }

    @Test
    public void startDelegationProcessTest() throws Exception {
        AdminDelegatesRequest form = new AdminDelegatesRequest();
        form.setManaged(Text.valueOf(MEMBER_ID));
        form.setTrusted(Text.valueOf("0000-0000-0000-0002"));
        
        ProfileEntity managed = new ProfileEntity();
        managed.setRecordLocked(false);
        ProfileEntity trusted = new ProfileEntity();
        trusted.setRecordLocked(false);
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(managed);
        when(profileEntityCacheManagerMock.retrieve(eq("0000-0000-0000-0002"))).thenReturn(trusted);
        when(profileEntityManagerMock.orcidExists(anyString())).thenReturn(true);
        
        AdminDelegatesRequest result = adminController.startDelegationProcess(requestMock, responseMock, form);
        
        assertNotNull(result);
        verify(adminManagerMock).startDelegationProcess(eq(form), eq("0000-0000-0000-0002"), eq(MEMBER_ID));
    }

    @Test
    public void lookupIdOrEmailsTest() throws Exception {
        String input = MEMBER_ID + "\n" + "test@test.com";
        when(emailManagerMock.emailExists(eq("test@test.com"))).thenReturn(true);
        
        String result = adminController.lookupIdOrEmails(requestMock, responseMock, input);
        
        assertNotNull(result);
        assertTrue(result.contains(MEMBER_ID));
    }

    @Test
    public void deactivateOrcidRecordsTest() throws Exception {
        String input = MEMBER_ID;
        when(profileEntityManagerMock.orcidExists(eq(MEMBER_ID))).thenReturn(true);
        ProfileEntity entity = new ProfileEntity();
        entity.setOrcidType(OrcidType.USER.name());
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(entity);
        
        Map<String, Set<String>> result = adminController.deactivateOrcidRecords(requestMock, responseMock, input);
        
        assertNotNull(result);
        assertTrue(result.get("success").contains(MEMBER_ID));
        verify(profileEntityManagerMock).deactivateRecord(eq(MEMBER_ID));
    }

    @Test
    public void unlockRecordsTest() throws Exception {
        String input = MEMBER_ID;
        when(profileEntityManagerMock.orcidExists(eq(MEMBER_ID))).thenReturn(true);
        ProfileEntity entity = new ProfileEntity();
        entity.setRecordLocked(true);
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(entity);
        
        Map<String, Set<String>> result = adminController.unlockRecords(requestMock, responseMock, input);
        
        assertNotNull(result);
        assertTrue(result.get("successful").contains(MEMBER_ID));
        verify(profileEntityManagerMock).unlockProfile(eq(MEMBER_ID));
    }

    @Test
    public void reviewRecordsTest() throws Exception {
        String input = MEMBER_ID;
        when(profileEntityManagerMock.orcidExists(eq(MEMBER_ID))).thenReturn(true);
        ProfileEntity entity = new ProfileEntity();
        entity.setReviewed(false);
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(entity);
        
        Map<String, Set<String>> result = adminController.reviewRecords(requestMock, responseMock, input);
        
        assertNotNull(result);
        assertTrue(result.get("successful").contains(MEMBER_ID));
        verify(profileEntityManagerMock).reviewProfile(eq(MEMBER_ID));
    }

    @Test
    public void unreviewRecordsTest() throws Exception {
        String input = MEMBER_ID;
        when(profileEntityManagerMock.orcidExists(eq(MEMBER_ID))).thenReturn(true);
        ProfileEntity entity = new ProfileEntity();
        entity.setReviewed(true);
        when(profileEntityCacheManagerMock.retrieve(eq(MEMBER_ID))).thenReturn(entity);
        
        Map<String, Set<String>> result = adminController.unreviewRecords(requestMock, responseMock, input);
        
        assertNotNull(result);
        assertTrue(result.get("successful").contains(MEMBER_ID));
        verify(profileEntityManagerMock).unreviewProfile(eq(MEMBER_ID));
    }

    @Test
    public void resetPasswordLinkTest() throws Exception {
        AdminResetPasswordLink form = new AdminResetPasswordLink();
        form.setOrcidOrEmail(MEMBER_ID);
        
        when(profileEntityManagerMock.orcidExists(eq(MEMBER_ID))).thenReturn(true);
        java.util.Date now = new java.util.Date();
        org.apache.commons.math3.util.Pair<String, java.util.Date> pair = new org.apache.commons.math3.util.Pair<>("http://link", now);
        when(verifyEmailUtilsMock.createResetLinkForAdmin(eq(MEMBER_ID), any())).thenReturn(pair);
        
        AdminResetPasswordLink result = adminController.resetPasswordLink(requestMock, responseMock, form);
        
        assertEquals("http://link", result.getResetLink());
        assertEquals(now, result.getIssueDate());
    }
    
    @Test(expected = IllegalAccessException.class)
    public void createClient_noAdminRequestTest() throws Exception {
        when(orcidSecurityManagerMock.isAdmin()).thenReturn(false);
        adminController.createClient(requestMock, responseMock, getClient());
    }

    @Test
    public void createClient_nullClientTest() throws Exception {
        Client c = adminController.createClient(requestMock, responseMock, null);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Client object cannot be null", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyMemberIdTest() throws Exception {
        Client c = getClient();
        c.setMemberId(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Member ID is required", c.getErrors().get(0));
    }

    @Test
    public void createClient_invalidMemberIdTest() throws Exception {
        Client c = getClient();
        c.setMemberId(Text.valueOf(INVALID_ID));
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Member with ID " + INVALID_ID + " does not exists", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyClientNameTest() throws Exception {
        Client c = getClient();
        c.setDisplayName(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Display name is required", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyDescriptionTest() throws Exception {
        Client c = getClient();
        c.setShortDescription(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Description is required", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyWebsiteTest() throws Exception {
        Client c = getClient();
        c.setWebsite(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Website is required", c.getErrors().get(0));
    }

    @Test
    public void createClient_emptyRedirecturisTest() throws Exception {
        Client c = getClient();
        c.getRedirectUris().remove(0);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Redirect URIs are required", c.getErrors().get(0));
    }

    @Test
    public void createClient_redirectUrisMissingTypeTest() throws Exception {
        Client c = getClient();
        c.getRedirectUris().get(0).setType(null);
        c = adminController.createClient(requestMock, responseMock, c);
        assertEquals(c.getErrors().size(), 1);
        assertEquals("Redirect uri type missing on redirect uri https://test.orcid.org/ruri", c.getErrors().get(0));
    }

    @Test
    public void createClientTest() throws IllegalAccessException {
        when(clientManagerMock.createWithConfigValues(any())).thenAnswer(
                (Answer<org.orcid.jaxb.model.v3.release.client.Client>) invocation -> {
                    org.orcid.jaxb.model.v3.release.client.Client c = invocation.getArgument(0, org.orcid.jaxb.model.v3.release.client.Client.class);
                    // Mock the client secret to prevent a NPE
                    c.setDecryptedSecret("SECRET");
                    // Mock client type to prevent a NPE
                    c.setClientType(ClientType.PREMIUM_UPDATER);
                    return c;
                }
        );
        Client c = getClient();
        // Mock the client id to prevent a NPE
        c.setClientId(Text.valueOf("APP-0"));
        Client newClient = adminController.createClient(requestMock, responseMock, c);
        assertTrue(c.getErrors().isEmpty());
        assertFalse(PojoUtil.isEmpty(newClient.getClientId()));
        org.orcid.jaxb.model.v3.release.client.Client modelObject = newClient.toModelObject();
        verify(clientManagerMock, times(1)).createWithConfigValues(any(org.orcid.jaxb.model.v3.release.client.Client.class));
    }

    @Test(expected = IllegalAccessException.class)
    public void resetClientSecret_noAdminTest() throws Exception {
        when(orcidSecurityManagerMock.isAdmin()).thenReturn(false);
        adminController.resetClientSecret(requestMock, responseMock, new Client());
    }

    @Test
    public void resetClientSecretTest() throws Exception {
        Client client = new Client();
        client.setClientId(Text.valueOf("APP-123"));
        
        org.orcid.jaxb.model.v3.release.client.Client model = new org.orcid.jaxb.model.v3.release.client.Client();
        when(clientManagerReadOnlyMock.get(eq("APP-123"))).thenReturn(model);
        when(clientManagerMock.resetAndGetClientSecret(eq("APP-123"))).thenReturn("new-secret");
        
        ResponseEntity<Map<String, String>> response = adminController.resetClientSecret(requestMock, responseMock, client);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("new-secret", response.getBody().get("newSecret"));
    }

    private Client getClient() {
        Client client = new Client();
        client.setMemberId(Text.valueOf(MEMBER_ID));
        client.setDisplayName(Text.valueOf("Client name"));
        client.setShortDescription(Text.valueOf("Short description"));
        client.setWebsite(Text.valueOf("https://test.orcid.org/website"));
        List<RedirectUri> rUris = new ArrayList<>();
        RedirectUri rUri1 = new RedirectUri();
        rUri1.setValue(Text.valueOf("https://test.orcid.org/ruri"));
        rUri1.setType(Text.valueOf("default"));
        rUris.add(rUri1);
        client.setRedirectUris(rUris);
        return client;
    }
}
