package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.jaxb.model.v3.rc2.common.Locale;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.EmailRequest;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.DispatcherServlet;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml", "classpath:statistics-core-context.xml" })
public class ClaimControllerTest {

    @Resource
    private ClaimController claimController;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private EmailManager emailManager;

    @Mock
    private NotificationManager notificationManager;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(claimController, "encryptionManager", encryptionManager);
        TargetProxyHelper.injectIntoProxy(claimController, "emailManager", emailManager);
        TargetProxyHelper.injectIntoProxy(claimController, "profileEntityManager", profileEntityManager);
        TargetProxyHelper.injectIntoProxy(claimController, "profileEntityCacheManager", profileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(claimController, "notificationManager", notificationManager);
    }

    @Test
    public void testResendEmailFailIfTheProfileIsAlreadyClaimed() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(emailManager.emailExists("billie@holiday.com")).thenReturn(true);
        when(emailManager.findOrcidIdByEmail("billie@holiday.com")).thenReturn("0000-0000-0000-0000");        
        when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenReturn(getProfileEntityToTestClaimResend(true));
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("billie@holiday.com");
        emailRequest = claimController.resendClaimEmail(emailRequest);
        assertNotNull(emailRequest);
        assertNull(emailRequest.getSuccessMessage());
        assertNotNull(emailRequest.getErrors());
        assertFalse(emailRequest.getErrors().isEmpty());
    }

    @Test
    public void testResendClaimEmail() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(emailManager.emailExists("billie@holiday.com")).thenReturn(true);
        when(emailManager.findOrcidIdByEmail("billie@holiday.com")).thenReturn("0000-0000-0000-0000");
        when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenReturn(getProfileEntityToTestClaimResend(false));
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("billie@holiday.com");
        emailRequest = claimController.resendClaimEmail(emailRequest);
        assertNotNull(emailRequest);
        assertNotNull(emailRequest.getSuccessMessage());
    }

    @Test
    @Transactional
    public void testClaim() {
        String email = "public_0000-0000-0000-0001@test.orcid.org";
        SecurityContextHolder.getContext().setAuthentication(null);
        when(profileEntityCacheManager.retrieve(Mockito.anyString())).thenReturn(getProfileEntityToTestClam(false));
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn(email);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE)).thenReturn(null);
        when(request.getLocale()).thenReturn(java.util.Locale.US);
        String orcid = "0000-0000-0000-0001";
        when(emailManager.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(profileEntityManager.claimProfileAndUpdatePreferences(any(String.class), any(String.class), any(Locale.class), any(Claim.class))).thenReturn(true);

        Claim claim = new Claim();
        claim.setActivitiesVisibilityDefault(org.orcid.pojo.ajaxForm.Visibility.valueOf(Visibility.PRIVATE));
        claim.setPassword(Text.valueOf("passwordTest1"));
        claim.setPasswordConfirm(Text.valueOf("passwordTest1"));
        Checkbox checked = new Checkbox();
        checked.setValue(true);
        claim.setSendChangeNotifications(checked);
        claim.setSendOrcidNews(checked);
        claim.setTermsOfUse(checked);
        try {
            claim = claimController.submitClaimJson(request, response, email, claim);
            assertNotNull(claim);
            assertTrue(claim.getErrors().isEmpty());
            assertTrue("Value was: " + claim.getUrl(), claim.getUrl().endsWith("/my-orcid?recordClaimed"));
        } catch (UnsupportedEncodingException e) {
            fail();
        }
    }

    private ProfileEntity getProfileEntityToTestClaimResend(boolean claimed) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId("0000-0000-0000-000X");
        entity.setClaimed(claimed);
        Set<EmailEntity> emails = new HashSet<EmailEntity>();
        EmailEntity email = new EmailEntity();
        email.setEmail("billie@holiday.com");
        email.setPrimary(true);
        entity.setEmails(emails);
        return entity;
    }

    private ProfileEntity getProfileEntityToTestClam(boolean claimed) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId("0000-0000-0000-0001");
        entity.setClaimed(claimed);
        Set<EmailEntity> emails = new HashSet<EmailEntity>();
        EmailEntity email = new EmailEntity();
        email.setEmail("public_0000-0000-0000-0001@test.orcid.org");
        email.setPrimary(true);
        entity.setEmails(emails);
        return entity;
    }
}
