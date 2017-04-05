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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.GivenPermissionToManager;
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
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.pojo.DelegateForm;
import org.orcid.pojo.DeprecateProfile;
import org.orcid.pojo.ManageDelegate;
import org.orcid.pojo.ajaxForm.BiographyForm;
import org.orcid.pojo.ajaxForm.NamesForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.google.common.collect.Sets;

/**
 * @author Declan Newman (declan) Date: 23/02/2012
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class OldManageProfileControllerTest extends BaseControllerTest {

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

    @Mock
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
    private GivenPermissionToManager givenPermissionToManager;

    @Mock
    private ProfileEntityManager profileEntityManager;

    

    
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
        verify(mockNotificationManager, times(1)).sendNotificationToAddedDelegate(any(String.class), (argThat(onlyNewDelegateAdded())));
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

    public static ArgumentMatcher<DelegationDetails> onlyNewDelegateAdded() {
        return new ArgumentMatcher<DelegationDetails>() {

            @Override
            public boolean matches(DelegationDetails delegateAdded) {
                if (delegateAdded != null) {
                    return "5555-5555-5555-555X".equals(delegateAdded.getDelegateSummary().getOrcidIdentifier().getPath());
                }
                return false;
            }

        };
    }
}