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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidIndexManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.impl.OrcidProfileManagerImpl;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.pojo.ManageDelegate;
import org.orcid.pojo.ajaxForm.BiographyForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Declan Newman (declan) Date: 23/02/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ManageProfileControllerTest extends BaseControllerTest {

    @Resource(name = "manageProfileController")
    private ManageProfileController controller;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;
    
    @Resource(name = "adminController")
    AdminController adminController;

    @Resource(name = "profileEntityCacheManager")
    ProfileEntityCacheManager profileEntityCacheManager;
    
    @Mock
    private OrcidIndexManager mockOrcidIndexManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Mock
    private NotificationManager mockNotificationManager;

    @Mock
    private ResearcherUrlManager researcherUrlManager;

    @Mock
    private ProfileKeywordManager profileKeywordManager;

    @Mock
    private GivenPermissionToDao givenPermissionToDao;

    @Mock
    private ProfileEntityManager profileEntityManager;        

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
        controller.setOrcidProfileManager(orcidProfileManager);
        controller.setGivenPermissionToDao(givenPermissionToDao);
        controller.setNotificationManager(mockNotificationManager);
        controller.setProfileEntityManager(profileEntityManager);
    }

    @Before
    public void init() {
        assertNotNull(controller);
    }

    @Before
    public void setUpTestProfiles() {
        createProfileStub("4444-4444-4444-4446");
        createProfileStub("5555-5555-5555-555X");
    }

    private void createProfileStub(String testOrcid) {
        ProfileEntity existing = profileDao.find(testOrcid);
        if (existing == null) {
            ProfileEntity testProfile = new ProfileEntity();
            testProfile.setId(testOrcid);
            profileDao.persist(testProfile);
        }
    }

    @Before
    public void setUpSecurityQuestion() {
        Integer testQuestionId = 1;
        SecurityQuestionEntity existing = securityQuestionDao.find(testQuestionId);
        if (existing == null) {
            SecurityQuestionEntity question = new SecurityQuestionEntity();
            question.setId(testQuestionId);
            question.setQuestion("What?");
            securityQuestionDao.persist(question);
        }
    }

    @After
    public void after() {
        orcidProfileManager.clearOrcidProfileCache();
    }

    // TODO: Test the data values too
    @Test
    public void testManageProfile() throws Exception {
        ModelAndView mav = controller.manageProfile("ManagePersonalInfo");
        Map<String, Object> model = mav.getModel();
        assertEquals("manage", mav.getViewName());
        assertNotNull(model.get("managePasswordOptionsForm"));
        assertNotNull(model.get("profile"));
        String activeTab = (String) model.get("activeTab");
        assertNotNull(activeTab);
        assertEquals("ManagePersonalInfo", activeTab);
        assertNotNull(model.get("securityQuestions"));
    }
    
    @Test
    public void testUpdatePastAffiliations() throws Exception {

    }

    @Test
    public void testDeletePastAffiliations() throws Exception {

    }

    @Test
    public void testChangeSecurityDetailsSuccess() throws Exception {
        BindingResult bindingResult = mock(BindingResult.class);
        ChangeSecurityQuestionForm changeSecurityQuestionForm = new ChangeSecurityQuestionForm();
        changeSecurityQuestionForm.setSecurityQuestionId(1);
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
        changeSecurityQuestionForm.setSecurityQuestionId(1);
        changeSecurityQuestionForm.setSecurityQuestionAnswer("securityQuestionAnswer");

        when(bindingResult.hasErrors()).thenReturn(true);
        ModelAndView modelAndView = controller.updateWithChangedSecurityQuestion(changeSecurityQuestionForm, bindingResult);

        assertEquals("change_security_question", modelAndView.getViewName());
        Boolean updatedSuccess = (Boolean) modelAndView.getModel().get("securityQuestionSaved");
        assertNull(updatedSuccess);
    }

    @Test
    public void testAddDelegateSendsEmailToOnlyNewDelegates() throws Exception {
        ProfileEntity delegateProfile = new ProfileEntity("5555-5555-5555-555X");
        delegateProfile.setRecordNameEntity(new RecordNameEntity());
        delegateProfile.getRecordNameEntity().setCreditName("Test Delegate Credit Name");        
        when(profileEntityManager.findByOrcid("5555-5555-5555-555X")).thenReturn(delegateProfile);
        ManageDelegate addDelegate = new ManageDelegate();
        addDelegate.setDelegateToManage("5555-5555-5555-555X");
        addDelegate.setPassword("password");
        controller.addDelegate(addDelegate);
        verify(mockNotificationManager, times(1)).sendNotificationToAddedDelegate(any(OrcidProfile.class), (argThat(onlyNewDelegateAdded())));
    }   
    
    @Test
    public void testValidateBiography() {        
        BiographyForm bf = new BiographyForm();
        //No NPE exception on empty bio
        controller.setBiographyFormJson(bf);
        assertNotNull(bf.getErrors());
        assertTrue(bf.getErrors().isEmpty());
        String bio = StringUtils.repeat('a', 5001);
        bf.setBiography(Text.valueOf(bio));
        controller.setBiographyFormJson(bf);
        assertEquals(1, bf.getErrors().size());        
        assertEquals(controller.getMessage("Length.changePersonalInfoForm.biography"), bf.getErrors().get(0));
        bio = StringUtils.repeat('a', 5000);
        bf.setBiography(Text.valueOf(bio));        
        controller.setBiographyFormJson(bf);
        assertTrue(bf.getErrors().isEmpty());
        BiographyForm updatedBf = controller.getBiographyForm();
        assertNotNull(updatedBf);
        assertTrue(updatedBf.getErrors().isEmpty());
        assertNotNull(updatedBf.getBiography());
    }
    
    public static TypeSafeMatcher<List<DelegationDetails>> onlyNewDelegateAdded() {
        return new TypeSafeMatcher<List<DelegationDetails>>() {

            @Override
            public boolean matchesSafely(List<DelegationDetails> delegatesAdded) {
                if (delegatesAdded != null && delegatesAdded.size() == 1) {
                    DelegationDetails delegationDetails = delegatesAdded.get(0);
                    return "5555-5555-5555-555X".equals(delegationDetails.getDelegateSummary().getOrcidIdentifier().getPath());
                }
                return false;
            }

            @Override
            public void describeTo(Description arg0) {
            }

        };
    }
}