package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.ajaxForm.CustomEmailForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CustomEmailControllerTest extends DBUnitTest {

    protected OrcidProfile orcidProfile;
    
    @Resource
    CustomEmailController customEmailController;

    @Resource
    protected OrcidProfileManager orcidProfileManager;
    
    @Resource
    private OrcidUserDetailsService orcidUserDetailsService;

    @Mock
    private EmailFrequencyManager mockEmailFrequencyManager;
    
    @Resource
    private EmailFrequencyManager emailFrequencyManager;
    
    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put(EmailFrequencyManager.ADMINISTRATIVE_CHANGE_NOTIFICATIONS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.CHANGE_NOTIFICATIONS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.MEMBER_UPDATE_REQUESTS, String.valueOf(Float.MAX_VALUE));
        map.put(EmailFrequencyManager.QUARTERLY_TIPS, String.valueOf(true));
        
        ReflectionTestUtils.setField(jpa2JaxbAdapter, "emailFrequencyManager", mockEmailFrequencyManager);
        when(mockEmailFrequencyManager.getEmailFrequency(anyString())).thenReturn(map);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        assertNotNull(customEmailController);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/ClientDetailsEntityData.xml"));
    }

    @After
    public void after() {
        ReflectionTestUtils.setField(jpa2JaxbAdapter, "emailFrequencyManager", emailFrequencyManager);
    }   
    
    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SecurityQuestionEntityData.xml"));
    }

    private Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("5555-5555-5555-5558");
        OrcidProfileUserDetails details = (OrcidProfileUserDetails) orcidUserDetailsService.loadUserByUsername(orcidProfile.retrieveOrcidPath());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("5555-5555-5555-5558", details.getPassword(), getRole());
        auth.setDetails(details);
        return auth;
    }

    protected List<OrcidWebRole> getRole() {
        return Arrays.asList(OrcidWebRole.ROLE_ADMIN);
    }

    @Test
    public void validateContentTest() {
        CustomEmailForm customEmail = customEmailController.getEmptyCustomEmailForm("APP-5555555555555555");
        customEmail = customEmailController.validateContent(customEmail);
        assertNotNull(customEmail);
        assertEquals(1, customEmail.getContent().getErrors().size());
        assertEquals(customEmailController.getMessage("custom_email.content.not_blank"), customEmail.getContent().getErrors().get(0));

        customEmail.setContent(Text.valueOf("This is a test"));
        customEmail = customEmailController.validateContent(customEmail);
        assertNotNull(customEmail);
        assertEquals(1, customEmail.getContent().getErrors().size());
        assertEquals(customEmailController.getMessage("custom_email.content.verification_url_required"), customEmail.getContent().getErrors().get(0));

        customEmail.setContent(Text.valueOf("${verification_url}"));
        customEmail = customEmailController.validateContent(customEmail);
        assertNotNull(customEmail);
        assertEquals(0, customEmail.getContent().getErrors().size());

        customEmail.setContent(Text.valueOf("This is a test ${verification_url} <a>"));
        customEmail.setHtml(false);
        customEmail = customEmailController.validateContent(customEmail);
        assertNotNull(customEmail);
        assertEquals(1, customEmail.getContent().getErrors().size());
        assertEquals(customEmailController.getMessage("custom_email.content.html"), customEmail.getContent().getErrors().get(0));

        customEmail.setHtml(true);
        customEmail = customEmailController.validateContent(customEmail);
        assertNotNull(customEmail);
        assertEquals(0, customEmail.getContent().getErrors().size());
    }

    @Test
    public void validateSubjectTest() {
        CustomEmailForm customEmail = customEmailController.getEmptyCustomEmailForm("APP-5555555555555555");
        customEmail.setSubject(Text.valueOf("This is a subject <a>"));
        customEmail = customEmailController.validateSubject(customEmail);
        assertEquals(1, customEmail.getSubject().getErrors().size());
        assertEquals(customEmailController.getMessage("custom_email.subject.html"), customEmail.getSubject().getErrors().get(0));

        customEmail.setSubject(Text.valueOf("This is a subject"));
        customEmail = customEmailController.validateSubject(customEmail);
        assertEquals(0, customEmail.getSubject().getErrors().size());
    }

    @Test
    public void validateOnlyOwnerCanAskForCustomEmailTest() {
        try {
            customEmailController.getEmptyCustomEmailForm("4444-4444-4444-4441");
            fail();
        } catch (IllegalArgumentException ie) {

        }
    }

    @Test
    public void validateOnlyValidClientIdsCanAskForCustomEmailTest() {
        try {
            customEmailController.getEmptyCustomEmailForm(null);
            fail();
        } catch (IllegalArgumentException ie) {

        }

        try {
            customEmailController.getEmptyCustomEmailForm("4444-4444-4444-XXXX");
            fail();
        } catch (IllegalArgumentException ie) {

        }
    }
}
