package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.RegistrationManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.web.forms.OneTimeResetPasswordForm;
import org.orcid.jaxb.model.message.DeactivationDate;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SecurityDetails;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.togglz.junit.TogglzRule;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml", "classpath:statistics-core-context.xml" })
public class PasswordResetControllerTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml",
            "/data/BiographyEntityData.xml");

    @Resource(name = "passwordResetController")
    private PasswordResetController passwordResetController;

    @Mock
    private RegistrationManager registrationManager;

    @Mock
    private OrcidProfileManager orcidProfileManager;
    
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
    private NotificationManager notificationManager;
    
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
        TargetProxyHelper.injectIntoProxy(passwordResetController, "orcidProfileManager", orcidProfileManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "emailManager", emailManager); 
        TargetProxyHelper.injectIntoProxy(passwordResetController, "encryptionManager", encryptionManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "emailManagerReadOnly", mockEmailManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "profileEntityManager", profileEntityManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(passwordResetController, "notificationManager", notificationManager);
        
        // Disable all features by default
        togglzRule.disableAll();
    }
    
    @Test
    public void testPasswordResetUnclaimedSendEmail() throws DatatypeConfigurationException {
        //TODO: remove togglzRule.enable togglz when feature is live
        togglzRule.enable(Features.RESET_PASSWORD_EMAIL);
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
    //TODO: remove this test when RESET_PASSWORD_EMAIL feature is live
    public void testPasswordResetUserNotFound() {
        when(orcidProfileManager.retrieveOrcidProfileByEmail(Mockito.anyString(), Mockito.any(LoadOptions.class))).thenReturn(null);
        EmailRequest resetRequest = new EmailRequest();
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertFalse(resetRequest.getErrors().isEmpty());
    }
    
    @Test
    public void testPasswordResetUserNotFoundSendEmail() {
        //TODO: remove togglzRule.enable togglz when feature is live
        togglzRule.enable(Features.RESET_PASSWORD_EMAIL);
        when(orcidProfileManager.retrieveOrcidProfileByEmail(Mockito.anyString(), Mockito.any(LoadOptions.class))).thenReturn(null);
        EmailRequest resetRequest = new EmailRequest();
        resetRequest.setEmail("not_in_orcid@test.orcid.org");
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertTrue(resetRequest.getErrors().isEmpty());
    }

    @Test
    //TODO: remove this test when RESET_PASSWORD_EMAIL feature is live
    public void testPasswordResetUserDeactivated() throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(new Date());
        XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setDeactivationDate(new DeactivationDate(date));

        OrcidProfile deactivatedProfile = new OrcidProfile();
        deactivatedProfile.setOrcidHistory(orcidHistory);

        when(orcidProfileManager.retrieveOrcidProfileByEmail(Mockito.anyString(), Mockito.any(LoadOptions.class))).thenReturn(deactivatedProfile);
        EmailRequest resetRequest = new EmailRequest();
        
        resetRequest = passwordResetController.issuePasswordResetRequest(new MockHttpServletRequest(), resetRequest).getBody();
        assertNotNull(resetRequest.getErrors());
        assertFalse(resetRequest.getErrors().isEmpty());
    }
    
    @Test
    public void testPasswordResetUserDeactivatedSendEmail() throws DatatypeConfigurationException {
        //TODO: remove togglzRule.enable togglz when feature is live
        togglzRule.enable(Features.RESET_PASSWORD_EMAIL);
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
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("redirect:/reset-password?expired=true", modelAndView.getViewName());
        verify(redirectAttributes, times(1)).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testPasswordResetLinkValidLinkDirectsToConsolidatedScreenDirectly() throws Exception {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(new OrcidProfile());
        ModelAndView modelAndView = passwordResetController.resetPasswordEmail(servletRequest, "randomString", redirectAttributes);

        assertEquals("password_one_time_reset", modelAndView.getViewName());
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

    }

    @Test
    public void testSubmitConsolidatedPasswordReset() throws Exception {
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        BindingResult bindingResult = mock(BindingResult.class);

        OneTimeResetPasswordForm oneTimeResetPasswordForm = new OneTimeResetPasswordForm();
        oneTimeResetPasswordForm.setEncryptedEmail("encrypted string not expired");
        MockHttpSession session = new MockHttpSession();
        when(servletRequest.getSession()).thenReturn(session);
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=2070-05-29T17:04:27");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(mockEmailManagerReadOnly.findOrcidIdByEmail("any@orcid.org")).thenReturn("0000-0000-0000-0000");
        oneTimeResetPasswordForm = passwordResetController.submitPasswordReset(servletRequest, servletResponse, oneTimeResetPasswordForm);
        assertFalse(oneTimeResetPasswordForm.getPassword().getErrors().isEmpty());

        oneTimeResetPasswordForm.setPassword(Text.valueOf("Password#123"));
        oneTimeResetPasswordForm.setRetypedPassword(Text.valueOf("Password#123"));
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail(eq("any@orcid.org"), Matchers.<LoadOptions> any())).thenReturn(new OrcidProfile());
        oneTimeResetPasswordForm = passwordResetController.submitPasswordReset(servletRequest, servletResponse, oneTimeResetPasswordForm);
        assertTrue(oneTimeResetPasswordForm.getSuccessRedirectLocation().equals("https://testserver.orcid.org/my-orcid")
                || oneTimeResetPasswordForm.getSuccessRedirectLocation().equals("https://localhost:8443/orcid-web/my-orcid"));
        verify(redirectAttributes, never()).addFlashAttribute("passwordResetLinkExpired", true);

        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn("email=any@orcid.org&issueDate=1970-05-29T17:04:27");

        oneTimeResetPasswordForm = passwordResetController.submitPasswordReset(servletRequest, servletResponse, oneTimeResetPasswordForm);
        assertFalse(oneTimeResetPasswordForm.getErrors().isEmpty());
    }

    @Test
    public void testResetPasswordDontFailIfAnyFieldIsEmtpy() {
        OneTimeResetPasswordForm form = new OneTimeResetPasswordForm();
        passwordResetController.resetPasswordConfirmValidate(form);
        form.setPassword(Text.valueOf(""));
        form.setRetypedPassword(null);
        passwordResetController.resetPasswordConfirmValidate(form);
        form.setPassword(null);
        form.setRetypedPassword(Text.valueOf(""));
        passwordResetController.resetPasswordConfirmValidate(form);
    }

}
