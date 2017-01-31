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

import java.util.Arrays;
import java.util.Date;
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
import org.mockito.Mockito;
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
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.pojo.DeprecateProfile;
import org.orcid.pojo.ManageDelegate;
import org.orcid.pojo.ajaxForm.BiographyForm;
import org.orcid.pojo.ajaxForm.NamesForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.google.common.collect.Sets;

/**
 * @author Declan Newman (declan) Date: 23/02/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
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
        persistProfilesForDeprecateProfileTests();
    }

    private void persistProfilesForDeprecateProfileTests() {
        persistProfileForDeprecateProfileTest("1000-2000-3000-4000", "1000-2000-3000-4000@orcid.org", "blah", "blah", false, false);
        persistProfileForDeprecateProfileTest("1001-2002-3003-4004", "1001-2002-3003-4004@orcid.org", "singlename", null, false, false);
        persistProfileForDeprecateProfileTest("1101-2202-3303-4404", "1101-2202-3303-4404@orcid.org", "erm", "err", true, false);
        persistProfileForDeprecateProfileTest("1111-2222-3333-4444", "1111-2222-3333-4444@orcid.org", "erm", "err", false, true);
    }

    private void persistProfileForDeprecateProfileTest(String orcid, String email, String givenNames, String familyName, boolean deprecated, boolean deactivated) {
        ProfileEntity existing = profileDao.find(orcid);
        if (existing == null) {
            ProfileEntity testProfile = new ProfileEntity();
            testProfile.setId(orcid);
            testProfile.setEncryptedPassword(encryptionManager.hashForInternalUse("password"));

            EmailEntity emailEntity = new EmailEntity();
            emailEntity.setId(email);
            emailEntity.setProfile(testProfile);
            emailEntity.setVisibility(Visibility.PUBLIC);
            emailEntity.setPrimary(true);
            emailEntity.setCurrent(true);
            emailEntity.setVerified(true);
            testProfile.setEmails(Sets.newHashSet(emailEntity));

            RecordNameEntity recordName = new RecordNameEntity();
            recordName.setGivenNames(givenNames);

            if (familyName != null) {
                recordName.setFamilyName(familyName);
            }

            recordName.setProfile(testProfile);
            recordName.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
            testProfile.setRecordNameEntity(recordName);
            if (deprecated) {
                testProfile.setDeprecatedDate(new Date());
            }
            if (deactivated) {
                testProfile.setDeactivationDate(new Date());
            }
            profileDao.persist(testProfile);
        }
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
    public void testGetDeprecateProfile() {
        DeprecateProfile deprecateProfile = controller.getDeprecateProfile();
        assertNotNull(deprecateProfile);
        assertNull(deprecateProfile.getPrimaryOrcid());
        assertNull(deprecateProfile.getPrimaryAccountName());
        assertNull(deprecateProfile.getPrimaryEmails());
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNull(deprecateProfile.getDeprecatingEmails());
        assertNull(deprecateProfile.getDeprecatingOrcid());
        assertNull(deprecateProfile.getDeprecatingPassword());
        assertTrue(deprecateProfile.getErrors().isEmpty());
    }

    @Test
    public void testValidateDeprecateProfileWithValidDataUsingOrcid() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1000-2000-3000-4000");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getDeprecatingEmails());
        assertEquals("blah blah", deprecateProfile.getDeprecatingAccountName());
        assertEquals(1, deprecateProfile.getDeprecatingEmails().size());
        assertEquals("1000-2000-3000-4000", deprecateProfile.getDeprecatingOrcid());
        assertEquals("1000-2000-3000-4000@orcid.org", deprecateProfile.getDeprecatingEmails().get(0));
        assertTrue(deprecateProfile.getErrors().isEmpty());
    }

    @Test
    public void testValidateDeprecateProfileWithValidDataUsingEmail() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1001-2002-3003-4004@orcid.org");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getDeprecatingEmails());
        assertEquals("singlename", deprecateProfile.getDeprecatingAccountName());
        assertEquals(1, deprecateProfile.getDeprecatingEmails().size());
        assertEquals("1001-2002-3003-4004@orcid.org", deprecateProfile.getDeprecatingEmails().get(0));
        assertEquals("1001-2002-3003-4004", deprecateProfile.getDeprecatingOrcid());
        assertTrue(deprecateProfile.getErrors().isEmpty());
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataBadCredentialsUsingEmail() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1001-2002-3003-4004@orcid.org");
        deprecateProfile.setDeprecatingPassword("wrong password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataBadCredentialsUsingOrcid() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1000-2000-3000-4000");
        deprecateProfile.setDeprecatingPassword("wrong password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNull(deprecateProfile.getDeprecatingEmails());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataAlreadyDeprecatedProfileUsingEmail() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1101-2202-3303-4404@orcid.org");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataAlreadyDeprecatedProfileUsingOrcid() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1101-2202-3303-4404");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNull(deprecateProfile.getDeprecatingEmails());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }
    
    @Test
    public void testValidateDeprecateProfileWithInvalidDataDeactivatedProfileUsingEmail() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1111-2222-3333-4444@orcid.org");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataDeactivatedProfileUsingOrcid() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1111-2222-3333-4444");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNull(deprecateProfile.getDeprecatingEmails());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataMatchingAccounts() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("4444-4444-4444-4446");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNull(deprecateProfile.getDeprecatingEmails());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }
    
    @Test
    public void testConfirmDeprecateProfileWithValidData() {
        when(profileEntityManager.deprecateProfile(Mockito.eq("1000-2000-3000-4000"), Mockito.eq("4444-4444-4444-4446"))).thenReturn(true);
        
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcidOrEmail("1000-2000-3000-4000");
        deprecateProfile.setDeprecatingOrcid("1000-2000-3000-4000");
        deprecateProfile.setDeprecatingEmails(Arrays.asList("1000-2000-3000-4000@orcid.org"));
        deprecateProfile.setDeprecatingAccountName("blah blah");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile);
        assertTrue(deprecateProfile.getErrors().isEmpty());
    }
    
    @Test
    public void testConfirmDeprecateProfileWithValidDataUnknownProblem() {
        when(profileEntityManager.deprecateProfile(Mockito.eq("1000-2000-3000-4000"), Mockito.eq("4444-4444-4444-4446"))).thenReturn(false);
        
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcid("1000-2000-3000-4000");
        deprecateProfile.setDeprecatingEmails(Arrays.asList("1000-2000-3000-4000@orcid.org"));
        deprecateProfile.setDeprecatingAccountName("blah blah");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile);
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testConfirmDeprecateProfileWithInvalidDataBadCredentials() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcid("1000-2000-3000-4000");
        deprecateProfile.setDeprecatingEmails(Arrays.asList("1000-2000-3000-4000@orcid.org"));
        deprecateProfile.setDeprecatingAccountName("blah blah");
        deprecateProfile.setDeprecatingPassword("wrong password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testConfirmDeprecateProfileWithInvalidDataAlreadyDeprecatedProfile() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcid("1101-2202-3303-4404");
        deprecateProfile.setDeprecatingOrcidOrEmail("1101-2202-3303-4404");
        deprecateProfile.setDeprecatingEmails(Arrays.asList("1101-2202-3303-4404@orcid.org"));
        deprecateProfile.setDeprecatingAccountName("blah blah");
        deprecateProfile.setDeprecatingPassword("password");


        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testConfirmDeprecateProfileWithInvalidDataDeactivatedProfile() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingOrcid("1111-2222-3333-4444");
        deprecateProfile.setDeprecatingOrcidOrEmail("1111-2222-3333-4444");
        deprecateProfile.setDeprecatingEmails(Arrays.asList("1111-2222-3333-4444@orcid.org"));
        deprecateProfile.setDeprecatingAccountName("blah blah");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
    }

    @Test
    public void testConfirmDeprecateProfileWithInvalidDataMatchingAccounts() {
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryAccountName("B. Holiday");
        deprecateProfile.setPrimaryOrcid("4444-4444-4444-4446");
        deprecateProfile.setPrimaryEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingAccountName("B. Holiday");
        deprecateProfile.setDeprecatingOrcid("4444-4444-4444-4446");
        deprecateProfile.setDeprecatingOrcidOrEmail("4444-4444-4444-4446");
        deprecateProfile.setDeprecatingEmails(Arrays.asList("josiah_carberry@brown.edu"));
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
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
        // No NPE exception on empty bio
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

    @Test
    public void testStripHtmlFromNames() throws NoSuchRequestHandlingMethodException {
        NamesForm nf = new NamesForm();
        nf.setCreditName(Text.valueOf("<button onclick=\"alert('hello')\">Credit Name</button>"));
        nf.setGivenNames(Text.valueOf("<button onclick=\"alert('hello')\">Given Names</button>"));
        nf.setFamilyName(Text.valueOf("<button onclick=\"alert('hello')\">Family Name</button>"));
        nf = controller.setNameFormJson(nf);
        assertEquals("Credit Name", nf.getCreditName().getValue());
        assertEquals("Given Names", nf.getGivenNames().getValue());
        assertEquals("Family Name", nf.getFamilyName().getValue());

        NamesForm nfFromDB = controller.getNameForm();
        assertNotNull(nfFromDB);
        assertEquals("Credit Name", nfFromDB.getCreditName().getValue());
        assertEquals("Given Names", nfFromDB.getGivenNames().getValue());
        assertEquals("Family Name", nfFromDB.getFamilyName().getValue());
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