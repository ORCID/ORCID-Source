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
package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.frontend.web.forms.EmailAddressForm;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.ajaxForm.Checkbox;
import org.orcid.pojo.ajaxForm.Claim;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.google.common.collect.Lists;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-core-context.xml" })
public class ClaimControllerTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");
    
    @Resource
    private ClaimController claimController;
    
    @Mock 
    private ProfileEntityManager profileEntityManager;
    
    @Mock
    private OrcidProfileManager orcidProfileManager;
    
    @Mock
    private EncryptionManager encryptionManager;
    
    @Mock
    private EmailManager emailManager;
    
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
        TargetProxyHelper.injectIntoProxy(claimController, "orcidProfileManager", orcidProfileManager);
        TargetProxyHelper.injectIntoProxy(claimController, "encryptionManager", encryptionManager);
        TargetProxyHelper.injectIntoProxy(claimController, "emailManager", emailManager); 
        TargetProxyHelper.injectIntoProxy(claimController, "profileEntityManager", profileEntityManager);                 
    }
    
    @Test
    public void testResendEmailFailIfTheProfileIsAlreadyClaimed() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail("billie@holiday.com")).thenReturn(getOrcidToTestClaimResend(true));
        EmailAddressForm emailAddressForm = new EmailAddressForm();
        //Testing with profile 4444-4444-4444-4446
        emailAddressForm.setUserEmailAddress("billie@holiday.com");
        ModelAndView mav = claimController.resendClaimEmail(servletRequest, emailAddressForm, bindingResult);
        assertNotNull(mav);
        assertNotNull(mav.getModel());
        assertTrue(mav.getModel().containsKey("alreadyClaimed"));
        assertTrue((Boolean) mav.getModel().get("alreadyClaimed"));
    }

    @Test
    public void testResendClaimEmail() {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(orcidProfileManager.retrieveOrcidProfileByEmail("billie@holiday.com")).thenReturn(getOrcidToTestClaimResend(false));
        EmailAddressForm emailAddressForm = new EmailAddressForm();
        //Testing with profile 4444-4444-4444-4446
        emailAddressForm.setUserEmailAddress("billie@holiday.com");
        ModelAndView mav = claimController.resendClaimEmail(servletRequest, emailAddressForm, bindingResult);
        assertNotNull(mav);
        assertNotNull(mav.getModel());
        assertFalse(mav.getModel().containsKey("alreadyClaimed"));
        assertTrue(mav.getModel().containsKey("claimResendSuccessful"));
        assertTrue((Boolean) mav.getModel().get("claimResendSuccessful"));
    }
    
    @Test
    @Transactional
    public void testClaim() {
        String email = "public_0000-0000-0000-0001@test.orcid.org";
        SecurityContextHolder.getContext().setAuthentication(null);
        when(orcidProfileManager.retrieveOrcidProfileByEmail(any(String.class))).thenReturn(getOrcidToTestClaim(false));
        when(encryptionManager.decryptForExternalUse(any(String.class))).thenReturn(email);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE)).thenReturn(null);
        when(request.getLocale()).thenReturn(java.util.Locale.US);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(email, "0000-0000-0000-0001");
        when(emailManager.findOricdIdsByCommaSeparatedEmails(email)).thenReturn(data);       
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
        } catch (NoSuchRequestHandlingMethodException e) {
            fail();
        } catch (UnsupportedEncodingException e) {
            fail();
        }
    }
    
    private OrcidProfile getOrcidToTestClaimResend(boolean claimed) {
        OrcidProfile orcidProfile = new OrcidProfile();
        OrcidIdentifier orcid = new OrcidIdentifier("0000-0000-0000-000X");        
        orcidProfile.setOrcidIdentifier(orcid);
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(claimed));
        orcidProfile.setOrcidHistory(orcidHistory);
        OrcidBio orcidBio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        List<Email> emails = new ArrayList<Email>();
        Email email = new Email("billie@holiday.com");
        email.setPrimary(true);
        emails.add(email);
        contactDetails.setEmail(emails);
        orcidBio.setContactDetails(contactDetails);
        orcidProfile.setOrcidBio(orcidBio);
        return orcidProfile;
    }    
    
    private OrcidProfile getOrcidToTestClaim(boolean claimed) {
        OrcidProfile orcidProfile = new OrcidProfile();
        OrcidIdentifier orcid = new OrcidIdentifier("0000-0000-0000-0001");        
        orcidProfile.setOrcidIdentifier(orcid);
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(claimed));
        orcidProfile.setOrcidHistory(orcidHistory);
        OrcidBio orcidBio = new OrcidBio();
        ContactDetails contactDetails = new ContactDetails();
        List<Email> emails = new ArrayList<Email>();
        Email email = new Email("public_0000-0000-0000-0001@test.orcid.org");
        email.setPrimary(true);
        emails.add(email);
        contactDetails.setEmail(emails);
        orcidBio.setContactDetails(contactDetails);
        orcidProfile.setOrcidBio(orcidBio);
        return orcidProfile;
    }
}
