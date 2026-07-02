package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;

import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.*;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.togglz.junit.TogglzRule;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:test-frontend-web-servlet.xml" })
public class PasswordResetControllerTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml");

    @Resource(name = "passwordResetController")
    private PasswordResetController passwordResetController;

    @Mock
    private RegistrationManager registrationManager;    
    
    @Mock
    private EmailManager emailManager;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;
    
    @Mock
    private EmailManagerReadOnly mockEmailManagerReadOnly;
    
    @Mock
    private ProfileEntityManager profileEntityManager;
    
    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Mock
    private RecordEmailSender mockRecordEmailSender;

    @Mock
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;

    @Mock
    private BackupCodeManager backupCodeManager;
        
    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Lists.reverse(DATA_FILES));
    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "registrationManager", registrationManager);       
        TargetProxyHelper.injectIntoProxy(passwordResetController, "emailManager", emailManager); 
        TargetProxyHelper.injectIntoProxy(passwordResetController, "encryptionManager", encryptionManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "profileEntityManager", profileEntityManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "recordEmailSender", mockRecordEmailSender);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "twoFactorAuthenticationManager", twoFactorAuthenticationManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "backupCodeManager", backupCodeManager);
    }
    
    @Test
    public void testPasswordResetUnclaimedSendEmail() throws DatatypeConfigurationException {
        String email = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(email)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(false);
        when(profileEntityManager.isProfileClaimedByEmail(orcid)).thenReturn(false);
        ProfileEntity record= new ProfileEntity();
        when(profileEntityCacheManager.retrieve(orcid)).thenReturn(record);
        EmailRequest resetRequest = new EmailRequest();
        resetRequest.setEmail("email1@test.orcid.org");
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertTrue(resetRequest.getErrors().isEmpty());   
    }    
    
    @Test
    public void testPasswordResetUserNotFoundSendEmail() {
        EmailRequest resetRequest = new EmailRequest();
        resetRequest.setEmail("not_in_orcid@test.orcid.org");
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertTrue(resetRequest.getErrors().isEmpty());
    }

    @Test
    public void testPasswordResetUserDeactivatedSendEmail() throws DatatypeConfigurationException {
        String email = "email1@test.orcid.org";
        String orcid = "0000-0000-0000-0000";
        when(emailManager.emailExists(email)).thenReturn(true); 
        when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(profileEntityManager.isDeactivated(orcid)).thenReturn(true);
        ProfileEntity record = new ProfileEntity();
        when(profileEntityCacheManager.retrieve(orcid)).thenReturn(record);
        EmailRequest resetRequest = new EmailRequest();
        resetRequest.setEmail("email1@test.orcid.org");
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertTrue(resetRequest.getErrors().isEmpty());   
    }

    @Test
    public void testPasswordResetLinkExpired() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString");

        assertEquals("redirect:https://testserver.orcid.org/reset-password?expired=true", modelAndView.getViewName());
    }

    @Test
    public void testPasswordResetLinkValidLinkDirectsToConsolidatedScreenDirectly() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString");

        assertEquals("password_one_time_reset", modelAndView.getViewName());
    }

    @Test
    public void testSubmitConsolidatedPasswordReset() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);

        OneTimeResetPasswordForm oneTimeResetPasswordForm = new OneTimeResetPasswordForm();
        oneTimeResetPasswordForm.setEncryptedEmail("encrypted string not expired");
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        oneTimeResetPasswordForm = passwordResetController.submitPasswordReset(servletRequest, servletResponse, oneTimeResetPasswordForm);
        assertFalse(oneTimeResetPasswordForm.getNewPassword().getErrors().isEmpty());

        oneTimeResetPasswordForm.setNewPassword(Text.valueOf("Password#123"));
        oneTimeResetPasswordForm.setRetypedPassword(Text.valueOf("Password#123"));
        when(bindingResult.hasErrors()).thenReturn(false);        
        oneTimeResetPasswordForm = passwordResetController.submitPasswordReset(servletRequest, servletResponse, oneTimeResetPasswordForm);
        assertTrue(oneTimeResetPasswordForm.getSuccessRedirectLocation().equals("https://testserver.orcid.org/my-orcid")
                || oneTimeResetPasswordForm.getSuccessRedirectLocation().equals("https://localhost:8443/orcid-web/my-orcid"));

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        oneTimeResetPasswordForm = passwordResetController.submitPasswordReset(servletRequest, servletResponse, oneTimeResetPasswordForm);
        assertFalse(oneTimeResetPasswordForm.getErrors().isEmpty());
    }

    @Test
    public void testResetPasswordDontFailIfAnyFieldIsEmtpy() {
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        passwordResetController.resetPasswordConfirmValidate(form);
        form.setNewPassword(Text.valueOf(""));
        form.setRetypedPassword(null);
        passwordResetController.resetPasswordConfirmValidate(form);
        form.setPassword(null);
        form.setRetypedPassword(Text.valueOf(""));
        passwordResetController.resetPasswordConfirmValidate(form);
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_ValidToken() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("encrypted_string");

        form = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_ExpiredToken() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("encrypted_string");

        form = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);

        assertFalse(form.getErrors().isEmpty());
        assertTrue(form.getErrors().contains("expiredPasswordResetToken"));
    }

    @Test
    public void testSubmitPasswordEmailValidatePassword_InvalidToken() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenThrow(new EncryptionOperationNotPossibleException());

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("bad_string");

        form = passwordResetController.submitPasswordEmailValidatePassword(servletRequest, servletResponse, form);

        assertFalse(form.getErrors().isEmpty());
        assertTrue(form.getErrors().contains("invalidPasswordResetToken"));
    }

    @Test
    public void testSubmitPasswordResetV2_SuccessNo2FA() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(false);
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
        assertFalse(form.isTwoFactorEnabled());
        verify(profileEntityManager).updatePassword("0000-0000-0000-0000", "Password#123");
        verify(profileEntityManager).resetSigninLock("0000-0000-0000-0000");
    }

    @Test
    public void testSubmitPasswordResetV2_PromptsFor2FA() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
        assertTrue(form.isTwoFactorEnabled());
    }

    @Test
    public void testSubmitPasswordResetV2_SuccessWith2FACode() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);
        when(twoFactorAuthenticationManager.verificationCodeIsValid("123456", "0000-0000-0000-0000")).thenReturn(true);
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        form.setTwoFactorCode("123456");

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
        verify(profileEntityManager).updatePassword("0000-0000-0000-0000", "Password#123");
    }

    @Test
    public void testSubmitPasswordResetV2_FailsWithInvalid2FACode() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);
        when(twoFactorAuthenticationManager.verificationCodeIsValid("999999", "0000-0000-0000-0000")).thenReturn(false);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        form.setTwoFactorCode("999999");

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.isInvalidTwoFactorCode());
    }

    @Test
    public void testSubmitPasswordResetV2_SuccessWithBackupCode() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);
        when(backupCodeManager.verify("0000-0000-0000-0000", "ABCDEF1234")).thenReturn(true);
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        form.setTwoFactorRecoveryCode("ABCDEF1234");

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.getErrors().isEmpty());
        verify(profileEntityManager).updatePassword("0000-0000-0000-0000", "Password#123");
    }

    @Test
    public void testSubmitPasswordResetV2_FailsWithInvalidBackupCode() throws Exception {
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        when(twoFactorAuthenticationManager.userUsing2FA("0000-0000-0000-0000")).thenReturn(true);
        when(backupCodeManager.verify("0000-0000-0000-0000", "BADCODE123")).thenReturn(false);

        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        form.setEncryptedEmail("valid_token");
        form.setNewPassword(Text.valueOf("Password#123"));
        form.setRetypedPassword(Text.valueOf("Password#123"));
        form.setTwoFactorRecoveryCode("BADCODE123");

        form = passwordResetController.submitPasswordResetV2(servletRequest, servletResponse, form);

        assertTrue(form.isInvalidTwoFactorRecoveryCode());
    }
}
