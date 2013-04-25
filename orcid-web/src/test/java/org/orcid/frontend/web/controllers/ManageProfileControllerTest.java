/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidIndexManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.impl.OrcidProfileManagerImpl;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.frontend.web.forms.AddDelegateForm;
import org.orcid.frontend.web.forms.ChangePasswordForm;
import org.orcid.frontend.web.forms.ChangePersonalInfoForm;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.forms.CurrentAffiliationsForm;
import org.orcid.frontend.web.forms.ManagePasswordOptionsForm;
import org.orcid.frontend.web.forms.PersonalInfoForm;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 23/02/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ManageProfileControllerTest extends BaseControllerTest {

    @Resource(name = "manageProfileController")
    private ManageProfileController controller;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Mock
    private OrcidIndexManager mockOrcidIndexManager;

    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private NotificationManager mockNotificationManager;

    /**
     * The classes loaded from the app context are in fact proxies to the
     * OrcidProfileManagerImpl class, required for transactionality. However we
     * can only return the proxied interface from the app context
     * 
     * We need to mock the call to the OrcidIndexManager whenever a persist
     * method is called, but this dependency is only accessible on the impl (as
     * it should be).
     * 
     * To preserve the transactionality AND allow us to mock a dependency that
     * exists on the Impl we use the getTargetObject() method in the superclass
     * 
     * @throws Exception
     */
    @Before
    public void initMocks() throws Exception {

        OrcidProfileManagerImpl orcidProfileManagerImpl = getTargetObject(orcidProfileManager, OrcidProfileManagerImpl.class);
        orcidProfileManagerImpl.setOrcidIndexManager(mockOrcidIndexManager);
        orcidProfileManagerImpl.setNotificationManager(mockNotificationManager);
        controller.setEncryptionManager(encryptionManager);
        controller.setOrcidProfileManager(orcidProfileManager);

    }

    @Before
    public void init() {
        assertNotNull(controller);
    }

    // TODO: Test the data values too
    @Test
    public void testManageProfile() throws Exception {
        ModelAndView mav = controller.manageProfile("ManagePersonalInfo");
        Map<String, Object> model = mav.getModel();
        assertEquals("manage", mav.getViewName());
        assertNotNull(model.get("managePasswordOptionsForm"));
        assertNotNull(model.get("profile"));
        assertNotNull(model.get("sponsors"));
        String activeTab = (String) model.get("activeTab");
        assertNotNull(activeTab);
        assertEquals("ManagePersonalInfo", activeTab);
        assertNotNull(model.get("securityQuestions"));
        assertNotNull(model.get("currentAffiliationsForm"));
    }

    @Test
    public void testUnchangedEmailDoesNotInvokeNotificationManager() throws Exception {

        controller.setNotificationManager(mockNotificationManager);
        controller.setOrcidProfileManager(mock(OrcidProfileManager.class));

        BindingResult bindingResult = mock(BindingResult.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        ChangePersonalInfoForm changePersonalInfoForm = new ChangePersonalInfoForm(getOrcidProfile());
        ModelAndView successView = controller.saveEditedBio(request, changePersonalInfoForm, bindingResult, mock(RedirectAttributes.class));
        verify(mockNotificationManager, times(0)).sendEmailAddressChangedNotification(any(OrcidProfile.class), any(Email.class), any(URI.class));
        assertEquals("redirect:manage-bio-settings", successView.getViewName());
    }

    @Test
    public void testUpdateAffiliations() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        MapBindingResult bindingResult = new MapBindingResult(map, "currentAffiliationsForm");
        OrcidProfileUserDetails profileUserDetails = controller.getCurrentUser();
        CurrentAffiliationsForm currentAffiliationsForm = new CurrentAffiliationsForm(profileUserDetails.getEffectiveProfile());
        ModelAndView mav = controller.updateAffiliations(currentAffiliationsForm, bindingResult);
        assertNotNull(mav);

        Map<String, Object> model = mav.getModel();

        ManagePasswordOptionsForm passwordOptionsForm = (ManagePasswordOptionsForm) model.get("managePasswordOptionsForm");
        OrcidProfile profile = (OrcidProfile) model.get("profile");
        model.get("startYears");
        String activeTab = (String) model.get("activeTab");
        Map<String, String> securityQuestions = (Map<String, String>) model.get("securityQuestions");
        currentAffiliationsForm = (CurrentAffiliationsForm) model.get("currentAffiliationsForm");

        assertEquals("manage", mav.getViewName());
        assertNotNull(passwordOptionsForm);
        assertNotNull(profile);
        assertEquals("4444-4444-4444-4446", profile.getOrcid().getValue());
        assertEquals("affiliations-tab", activeTab);
        assertEquals(2, securityQuestions.size());
        assertNotNull(currentAffiliationsForm);

    }

    @Test
    public void testUpdatePastAffiliations() throws Exception {

    }

    @Test
    public void testDeletePastAffiliations() throws Exception {

    }

    @Test
    public void testChangePasswordHappyPath() throws Exception {

        BindingResult bindingResult = mock(BindingResult.class);
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        changePasswordForm.setOldPassword("0ldPassword");
        changePasswordForm.setPassword("n3wPassword");
        changePasswordForm.setRetypedPassword("n3wPassword");
        when(bindingResult.hasErrors()).thenReturn(false);
        when(encryptionManager.hashMatches(any(String.class), any(String.class))).thenReturn(true);

        ModelAndView modelAndView = controller.updateWithChangedPassword(changePasswordForm, bindingResult);
        assertEquals("change_password", modelAndView.getViewName());
        assertNotNull(modelAndView.getModel().get("passwordOptionsSaved"));

    }

    @Test
    public void testChangePasswordOldPasswordWrong() throws Exception {

        BindingResult bindingResult = mock(BindingResult.class);
        ChangePasswordForm changePasswordForm = new ChangePasswordForm();
        changePasswordForm.setOldPassword("0ldPassword");
        changePasswordForm.setPassword("n3wPassword");
        changePasswordForm.setRetypedPassword("n3wPassword");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(encryptionManager.hashMatches(any(String.class), any(String.class))).thenReturn(false);

        ModelAndView modelAndView = controller.updateWithChangedPassword(changePasswordForm, bindingResult);
        assertEquals("change_password", modelAndView.getViewName());
        assertNull(modelAndView.getModel().get("passwordOptionsSaved"));

    }

    @Test
    public void testChangeSecurityDetailsSuccess() throws Exception {

        BindingResult bindingResult = mock(BindingResult.class);
        ChangeSecurityQuestionForm changeSecurityQuestionForm = new ChangeSecurityQuestionForm();
        changeSecurityQuestionForm.setSecurityQuestionId(9);
        changeSecurityQuestionForm.setSecurityQuestionAnswer("securityQuestionAnswer");

        when(bindingResult.hasErrors()).thenReturn(false);
        ModelAndView modelAndView = controller.updateWithChangedSecurityQuestion(changeSecurityQuestionForm, bindingResult);
        assertEquals("change_security_question", modelAndView.getViewName());
        Boolean updatedSuccess = (Boolean) modelAndView.getModel().get("securityQuestionSaved");
        assertEquals(Boolean.TRUE, updatedSuccess);

    }

    @Test
    public void testChangeSecurityDetailsFailedValidation() throws Exception {

        BindingResult bindingResult = mock(BindingResult.class);
        ChangeSecurityQuestionForm changeSecurityQuestionForm = new ChangeSecurityQuestionForm();
        changeSecurityQuestionForm.setSecurityQuestionId(9);
        changeSecurityQuestionForm.setSecurityQuestionAnswer("securityQuestionAnswer");

        when(bindingResult.hasErrors()).thenReturn(true);
        ModelAndView modelAndView = controller.updateWithChangedSecurityQuestion(changeSecurityQuestionForm, bindingResult);

        assertEquals("change_security_question", modelAndView.getViewName());
        Boolean updatedSuccess = (Boolean) modelAndView.getModel().get("securityQuestionSaved");
        assertNull(updatedSuccess);
    }

    @Test
    public void testAddDelegateSendsEmailToOnlyNewDelegates() throws Exception {

        // for this test we want to mock part of the profile manager
        ProfileDao mockProfileDao = mock(ProfileDao.class);
        OrcidProfileManagerImpl orcidProfileManagerImpl = getTargetObject(orcidProfileManager, OrcidProfileManagerImpl.class);
        orcidProfileManagerImpl.setProfileDao(mockProfileDao);
        AddDelegateForm delegateForm = new AddDelegateForm();
        delegateForm.setDelegateOrcid("delegateOrcid");
        when(mockProfileDao.find(any(String.class))).thenReturn(orcidWithExistingSingleDelegate());
        when(mockProfileDao.merge(any(ProfileEntity.class))).thenReturn(orcidWithExistingSingleDelegate());
        ModelAndView successView = controller.addDelegate(delegateForm);
        verify(mockNotificationManager, times(1)).sendNotificationToAddedDelegate(any(OrcidProfile.class), (argThat(onlyNewDelegateAdded())));
        assertEquals("redirect:/account?activeTab=delegation-tab", successView.getViewName());

    }

    private ProfileEntity orcidWithExistingSingleDelegate() {
        ProfileEntity mockEntity = new ProfileEntity();
        mockEntity.setEmail("josiah.carberry@brown.edu");
        mockEntity.setId("4444-4444-4444-4446");
        Set<GivenPermissionToEntity> existingPermissions = new HashSet<GivenPermissionToEntity>();
        GivenPermissionToEntity singlePermission = new GivenPermissionToEntity();

        singlePermission.setGiver("1234");
        ProfileSummaryEntity receiver = new ProfileSummaryEntity();
        receiver.setId("5678");
        receiver.setCreditName("existing receiver creditName");
        singlePermission.setReceiver(receiver);

        existingPermissions.add(singlePermission);
        mockEntity.setGivenPermissionTo(existingPermissions);

        return mockEntity;
    }

    public static TypeSafeMatcher<List<DelegationDetails>> onlyNewDelegateAdded() {
        return new TypeSafeMatcher<List<DelegationDetails>>() {

            @Override
            public boolean matchesSafely(List<DelegationDetails> delegatesAdded) {
                if (delegatesAdded != null && delegatesAdded.size() == 1) {
                    DelegationDetails delegationDetails = delegatesAdded.get(0);
                    return "delegateOrcid".equals(delegationDetails.getDelegateSummary().getOrcid().getValue());
                }

                return false;

            }

            @Override
            public void describeTo(Description arg0) {

            }

        };
    }
}