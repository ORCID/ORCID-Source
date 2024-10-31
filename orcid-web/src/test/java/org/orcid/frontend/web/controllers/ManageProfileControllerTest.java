package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.aop.ProfileLastModifiedAspect;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.TwoFactorAuthenticationManager;
import org.orcid.core.manager.v3.BiographyManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.GivenPermissionToManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.RecordNameManager;
import org.orcid.core.manager.v3.read_only.*;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.v3.release.common.*;
import org.orcid.utils.DateUtils;
import org.orcid.core.utils.v3.OrcidIdentifierUtils;
import org.orcid.frontend.email.RecordEmailSender;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.AddEmail;
import org.orcid.pojo.DelegateForm;
import org.orcid.pojo.DeprecateProfile;
import org.orcid.pojo.ManageDelegate;
import org.orcid.pojo.ajaxForm.BiographyForm;
import org.orcid.pojo.ajaxForm.Errors;
import org.orcid.pojo.ajaxForm.NamesForm;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.TargetProxyHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Declan Newman (declan) Date: 23/02/2012
 */
public class ManageProfileControllerTest {

    private ManageProfileController controller;

    private static final String USER_ORCID = "0000-0000-0000-0001";
    private static final String DEPRECATED_USER_ORCID = "0000-0000-0000-0002";
    private static final String DEPRECATED_USER_ORCID_URL = "https://localhost:8443/0000-0000-0000-0002";
    private static final String USER_CREDIT_NAME = "Credit Name";

    @Mock
    private ProfileEntityCacheManager mockProfileEntityCacheManager;

    @Mock
    private EncryptionManager mockEncryptionManager;

    @Mock
    private EmailManager mockEmailManager;

    @Mock
    private ProfileEmailDomainManagerReadOnly mockProfileEmailDomainManagerReadOnly;

    @Mock
    private LocaleManager mockLocaleManager;

    @Mock
    private ProfileEntityManager mockProfileEntityManager;
    
    @Mock
    private GivenPermissionToManager mockGivenPermissionToManager;
    
    @Mock
    private GivenPermissionToManagerReadOnly mockGivenPermissionToManagerReadOnly;
    
    @Mock
    private OrcidSecurityManager mockOrcidSecurityManager;
    
    @Mock
    private OrcidIdentifierUtils mockOrcidIdentifierUtils;
    
    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    @Mock
    private RecordNameManagerReadOnly mockRecordNameManagerReadOnlyV3;
    
    @Mock
    private TwoFactorAuthenticationManager twoFactorAuthenticationManager;
    
    @Mock
    private RecordEmailSender mockRecordEmailSender;    
    
    @Mock(name="profileEntityManagerReadOnlyV3")
    private ProfileEntityManagerReadOnly mockProfileEntityManagerReadOnly;

    @Mock
    private PersonalDetailsManagerReadOnly mockPersonalDetailsManagerReadOnly;

    @Mock
    private AddressManagerReadOnly mockAddressManagerReadOnly;

    @Mock
    private ProfileKeywordManagerReadOnly mockKeywordManagerReadOnly;

    @Mock
    private ResearcherUrlManagerReadOnly mockResearcherUrlManagerReadOnly;

    @Mock
    private ExternalIdentifierManagerReadOnly mockExternalIdentifierManagerReadOnly;

    @Before
    public void initMocks() throws Exception {
        controller = new ManageProfileController();
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        TargetProxyHelper.injectIntoProxy(controller, "profileEntityCacheManager", mockProfileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(controller, "encryptionManager", mockEncryptionManager);
        TargetProxyHelper.injectIntoProxy(controller, "emailManager", mockEmailManager);
        TargetProxyHelper.injectIntoProxy(controller, "emailManagerReadOnly", mockEmailManager);
        TargetProxyHelper.injectIntoProxy(controller, "profileEmailDomainManagerReadOnly", mockProfileEmailDomainManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(controller, "localeManager", mockLocaleManager);
        TargetProxyHelper.injectIntoProxy(controller, "profileEntityManager", mockProfileEntityManager);
        TargetProxyHelper.injectIntoProxy(controller, "givenPermissionToManager", mockGivenPermissionToManager); 
        TargetProxyHelper.injectIntoProxy(controller, "givenPermissionToManagerReadOnly", mockGivenPermissionToManagerReadOnly); 
        TargetProxyHelper.injectIntoProxy(controller, "orcidSecurityManager", mockOrcidSecurityManager);  
        TargetProxyHelper.injectIntoProxy(controller, "orcidIdentifierUtils", mockOrcidIdentifierUtils);
        TargetProxyHelper.injectIntoProxy(controller, "profileLastModifiedAspect", profileLastModifiedAspect);
        TargetProxyHelper.injectIntoProxy(controller, "recordNameManagerReadOnlyV3", mockRecordNameManagerReadOnlyV3);
        TargetProxyHelper.injectIntoProxy(controller, "twoFactorAuthenticationManager", twoFactorAuthenticationManager);
        TargetProxyHelper.injectIntoProxy(controller, "recordEmailSender", mockRecordEmailSender);
        TargetProxyHelper.injectIntoProxy(controller, "profileEntityManagerReadOnly", mockProfileEntityManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(controller, "personalDetailsManagerReadOnly", mockPersonalDetailsManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(controller, "addressManagerReadOnly", mockAddressManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(controller, "keywordManagerReadOnly", mockKeywordManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(controller, "researcherUrlManagerReadOnly", mockResearcherUrlManagerReadOnly);
        TargetProxyHelper.injectIntoProxy(controller, "externalIdentifierManagerReadOnly", mockExternalIdentifierManagerReadOnly);

                
        when(mockOrcidSecurityManager.isPasswordConfirmationRequired()).thenReturn(true);
        when(mockEncryptionManager.hashMatches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(mockEncryptionManager.hashMatches(eq("invalid password"), Mockito.anyString())).thenReturn(false);
        when(mockProfileEntityManager.deprecateProfile(eq(DEPRECATED_USER_ORCID), eq(USER_ORCID), eq(ProfileEntity.USER_DRIVEN_DEPRECATION), Mockito.isNull())).thenReturn(true);
        when(mockProfileEntityManager.deprecateProfile(eq(DEPRECATED_USER_ORCID), eq(USER_ORCID), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(mockProfileEntityManager.deprecateProfile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        when(profileLastModifiedAspect.retrieveLastModifiedDate(anyString())).thenReturn(new Date());
        when(mockOrcidIdentifierUtils.buildOrcidIdentifier(Mockito.anyString())).thenAnswer(new Answer<OrcidIdentifier>() {

            @Override
            public OrcidIdentifier answer(InvocationOnMock invocation) throws Throwable {
                OrcidIdentifier result = new OrcidIdentifier();
                result.setPath(invocation.getArgument(0));
                return result;
            }
            
        });
        
        when(mockLocaleManager.resolveMessage(Mockito.anyString(), any())).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }

        });

        when(mockProfileEntityCacheManager.retrieve(Mockito.anyString())).then(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity entity = new ProfileEntity();
                entity.setId(invocation.getArgument(0));
                entity.setEncryptedPassword("password");
                return entity;
            }
        });

        when(mockEmailManager.getEmails(Mockito.anyString())).thenAnswer(new Answer<Emails>() {

            @Override
            public Emails answer(InvocationOnMock invocation) throws Throwable {
                Emails emails = new Emails();
                Email email1 = new Email();
                email1.setEmail(invocation.getArgument(0) + "_1@test.orcid.org");
                email1.setSource(new Source());
                email1.setVisibility(Visibility.PUBLIC);
                emails.getEmails().add(email1);

                Email email2 = new Email();
                email2.setEmail(invocation.getArgument(0) + "_2@test.orcid.org");
                email2.setSource(new Source());
                email2.getSource().setSourceName(new SourceName(USER_CREDIT_NAME));
                email2.setVisibility(Visibility.PUBLIC);
                emails.getEmails().add(email2);

                Email email3 = new Email();
                email3.setEmail(invocation.getArgument(0) + "_3@test.orcid.org");
                email3.setSource(new Source());
                email3.getSource().setSourceClientId(new SourceClientId(USER_ORCID));
                email3.setVisibility(Visibility.PUBLIC);
                emails.getEmails().add(email3);
                return emails;
            }

        });

        when(mockEmailManager.find(Mockito.anyString())).thenAnswer(new Answer<EmailEntity>() {

            @Override
            public EmailEntity answer(InvocationOnMock invocation) throws Throwable {
                String emailString = invocation.getArgument(0);
                String orcidString = emailString.substring(0, (emailString.indexOf("_")));
                EmailEntity email = new EmailEntity();
                email.setEmail(emailString);
                email.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name());
                ProfileEntity entity = new ProfileEntity(orcidString);
                entity.setEncryptedPassword("password");                
                email.setOrcid(orcidString);
                return email;
            }
        });
        
        when(mockGivenPermissionToManagerReadOnly.findByGiver(anyString(), anyLong())).thenAnswer(new Answer<List<DelegateForm>>(){

            @Override
            public List<DelegateForm> answer(InvocationOnMock invocation) throws Throwable {
                XMLGregorianCalendar now = DateUtils.convertToXMLGregorianCalendar(new Date());
                List<DelegateForm> list = new ArrayList<DelegateForm>();
                DelegateForm one = new DelegateForm();
                one.setGiverOrcid(new OrcidIdentifier(USER_ORCID));
                one.setReceiverOrcid(new OrcidIdentifier("0000-0000-0000-0004"));
                one.setReceiverName(Text.valueOf("Credit Name"));
                one.setApprovalDate(now);
                list.add(one);
                DelegateForm two = new DelegateForm();
                two.setGiverOrcid(new OrcidIdentifier(USER_ORCID));
                two.setReceiverOrcid(new OrcidIdentifier("0000-0000-0000-0005"));
                two.setReceiverName(Text.valueOf("Given Names Family Name"));
                two.setApprovalDate(now);
                list.add(two);
                return list;
            }
            
        });
        
        when(mockRecordNameManagerReadOnlyV3.fetchDisplayablePublicName(anyString())).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0) + " Given Names " + invocation.getArgument(0) + " Family Name";
            }
            
        });
        
        ProfileEntity u1 = new ProfileEntity(USER_ORCID);
        u1.setEncryptedPassword("password");
        
        when(mockProfileEntityManagerReadOnly.findByOrcid(eq(USER_ORCID))).thenReturn(u1); 
        
        ProfileEntity u2 = new ProfileEntity(DEPRECATED_USER_ORCID);
        u2.setEncryptedPassword("password");        
        
        when(mockProfileEntityManagerReadOnly.findByOrcid(eq(DEPRECATED_USER_ORCID))).thenReturn(u2); 
    }

    private void mocksForDeprecatedAccounts() {
        when(mockProfileEntityCacheManager.retrieve(eq(DEPRECATED_USER_ORCID))).then(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity entity = new ProfileEntity();
                entity.setId(invocation.getArgument(0));                

                // Mark it as deprecated
                entity.setDeprecatedDate(new Date());
                entity.setEncryptedPassword("password");
                return entity;
            }
        });

        when(mockEmailManager.find(eq("0000-0000-0000-0002_1@test.orcid.org"))).thenAnswer(new Answer<EmailEntity>() {

            @Override
            public EmailEntity answer(InvocationOnMock invocation) throws Throwable {
                String emailString = invocation.getArgument(0);
                String orcidString = emailString.substring(0, (emailString.indexOf("_")));
                EmailEntity email = new EmailEntity();
                email.setEmail(emailString);
                email.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name());
                email.setOrcid(orcidString);
                return email;
            }
        });
        
        ProfileEntity p = new ProfileEntity(DEPRECATED_USER_ORCID);
        p.setEncryptedPassword("password");
        p.setDeprecatedDate(new Date());
        
        when(mockProfileEntityManagerReadOnly.findByOrcid(eq(DEPRECATED_USER_ORCID))).thenReturn(p);     
    }

    private void mocksForDeactivatedAccounts() {
        when(mockProfileEntityCacheManager.retrieve(eq(DEPRECATED_USER_ORCID))).then(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity entity = new ProfileEntity();
                entity.setId(invocation.getArgument(0));

                // Mark it as deactivated
                entity.setDeactivationDate(new Date());
                entity.setEncryptedPassword("password");
                return entity;
            }
        });

        when(mockEmailManager.find(eq("0000-0000-0000-0002_1@test.orcid.org"))).thenAnswer(new Answer<EmailEntity>() {

            @Override
            public EmailEntity answer(InvocationOnMock invocation) throws Throwable {
                String emailString = invocation.getArgument(0);
                String orcidString = emailString.substring(0, (emailString.indexOf("_")));
                EmailEntity email = new EmailEntity();
                email.setEmail(emailString);
                email.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC.name());
                email.setOrcid(orcidString);
                return email;
            }
        });
        
        ProfileEntity p = new ProfileEntity(DEPRECATED_USER_ORCID);
        p.setEncryptedPassword("password");
        p.setDeactivationDate(new Date());
        
        when(mockProfileEntityManagerReadOnly.findByOrcid(eq(DEPRECATED_USER_ORCID))).thenReturn(p); 
    }

    @Test
    public void testGetDelegates() {
        List<DelegateForm> list = controller.getDelegates();
        assertNotNull(list);
        assertEquals(2, list.size());
        boolean found1 = false, found2 = false;
        for (DelegateForm form : list) {
            assertNotNull(form);
            assertNotNull(form.getApprovalDate());
            assertEquals(USER_ORCID, form.getGiverOrcid().getPath());
            assertNotNull(form.getReceiverOrcid());
            if (form.getReceiverOrcid().getPath().equals("0000-0000-0000-0004")) {
                assertEquals("Credit Name", form.getReceiverName().getValue());
                found1 = true;
            } else {
                assertEquals("0000-0000-0000-0005", form.getReceiverOrcid().getPath());
                assertEquals("Given Names Family Name", form.getReceiverName().getValue());
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
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
    public void testValidateDeprecateProfileWithValidData() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));

        // Using email
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid(USER_ORCID);
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0002_1@test.orcid.org");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getDeprecatingEmails());
        assertEquals("0000-0000-0000-0002", deprecateProfile.getDeprecatingOrcid());
        assertEquals("0000-0000-0000-0002 Given Names 0000-0000-0000-0002 Family Name", deprecateProfile.getDeprecatingAccountName());
        assertEquals(3, deprecateProfile.getDeprecatingEmails().size());
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_1@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_2@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_3@test.orcid.org"));

        assertEquals("0000-0000-0000-0001", deprecateProfile.getPrimaryOrcid());
        assertEquals("0000-0000-0000-0001 Given Names 0000-0000-0000-0001 Family Name", deprecateProfile.getPrimaryAccountName());
        assertNotNull(deprecateProfile.getPrimaryEmails());
        assertEquals(3, deprecateProfile.getPrimaryEmails().size());
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_1@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_2@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_3@test.orcid.org"));
        assertTrue(deprecateProfile.getErrors().isEmpty());

        // Using orcid
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getDeprecatingEmails());
        assertEquals("0000-0000-0000-0002", deprecateProfile.getDeprecatingOrcid());
        assertEquals("0000-0000-0000-0002 Given Names 0000-0000-0000-0002 Family Name", deprecateProfile.getDeprecatingAccountName());
        assertEquals(3, deprecateProfile.getDeprecatingEmails().size());
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_1@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_2@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_3@test.orcid.org"));


        assertEquals("0000-0000-0000-0001", deprecateProfile.getPrimaryOrcid());
        assertEquals("0000-0000-0000-0001 Given Names 0000-0000-0000-0001 Family Name", deprecateProfile.getPrimaryAccountName());
        assertNotNull(deprecateProfile.getPrimaryEmails());
        assertEquals(3, deprecateProfile.getPrimaryEmails().size());
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_1@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_2@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_3@test.orcid.org"));
        assertTrue(deprecateProfile.getErrors().isEmpty());
        
        // Using orcid URL
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID_URL);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getDeprecatingEmails());
        assertEquals("0000-0000-0000-0002", deprecateProfile.getDeprecatingOrcid());
        assertEquals("0000-0000-0000-0002 Given Names 0000-0000-0000-0002 Family Name", deprecateProfile.getDeprecatingAccountName());
        assertEquals(3, deprecateProfile.getDeprecatingEmails().size());
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_1@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_2@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_3@test.orcid.org"));

        assertEquals("0000-0000-0000-0001", deprecateProfile.getPrimaryOrcid());
        assertEquals("0000-0000-0000-0001 Given Names 0000-0000-0000-0001 Family Name", deprecateProfile.getPrimaryAccountName());
        assertNotNull(deprecateProfile.getPrimaryEmails());
        assertEquals(3, deprecateProfile.getPrimaryEmails().size());
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_1@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_2@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_3@test.orcid.org"));

        assertTrue(deprecateProfile.getErrors().isEmpty());
        
        // Using orcid trim space
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID + " ");
        deprecateProfile.setDeprecatingPassword("password");
        
        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getDeprecatingEmails());
        assertEquals("0000-0000-0000-0002", deprecateProfile.getDeprecatingOrcid());
        assertEquals("0000-0000-0000-0002 Given Names 0000-0000-0000-0002 Family Name", deprecateProfile.getDeprecatingAccountName());

        assertEquals(3, deprecateProfile.getDeprecatingEmails().size());
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_1@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_2@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_3@test.orcid.org"));


        assertEquals("0000-0000-0000-0001", deprecateProfile.getPrimaryOrcid());
        assertEquals("0000-0000-0000-0001 Given Names 0000-0000-0000-0001 Family Name", deprecateProfile.getPrimaryAccountName());
        assertNotNull(deprecateProfile.getPrimaryEmails());
        assertEquals(3, deprecateProfile.getPrimaryEmails().size());
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_1@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_2@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_3@test.orcid.org"));
        assertTrue(deprecateProfile.getErrors().isEmpty());
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataBadCredentials() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));

        // Using email
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid(USER_ORCID);
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0002_1@test.orcid.org");
        deprecateProfile.setDeprecatingPassword("invalid password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("check_password_modal.incorrect_password", deprecateProfile.getErrors().get(0));

        // Using orcid
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("invalid password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("check_password_modal.incorrect_password", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataAlreadyDeprecatedProfile() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));

        // Apply mocks
        mocksForDeprecatedAccounts();

        // Using orcid
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid(USER_ORCID);
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.already_deprecated", deprecateProfile.getErrors().get(0));

        // Using email
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0002_1@test.orcid.org");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.already_deprecated", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataDeactivatedProfile() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));

        // Apply mocks
        mocksForDeactivatedAccounts();

        // Using orcid
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid(USER_ORCID);
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.already_deactivated", deprecateProfile.getErrors().get(0));

        // Using email
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0002_1@test.orcid.org");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.already_deactivated", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testValidateDeprecateProfileWithInvalidDataMatchingAccounts() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(DEPRECATED_USER_ORCID));

        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setPrimaryOrcid(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.validateDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.profile_matches_current", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testConfirmDeprecateProfile() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid(USER_ORCID);
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile);
        assertTrue(deprecateProfile.getErrors().isEmpty());
    }
    
    @Test
    public void testConfirmDeprecateProfileCurrentProfileDeprecated() {
        ProfileEntity deprecatedEntity = new ProfileEntity();
        deprecatedEntity.setId("0000-0000-0000-0123");
        deprecatedEntity.setDeprecatedDate(new Date());
        when(mockProfileEntityCacheManager.retrieve("0000-0000-0000-0123")).thenReturn(deprecatedEntity);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication("0000-0000-0000-0123"));
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid("0000-0000-0000-0123");
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0124");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile);
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.this_profile_deprecated", deprecateProfile.getErrors().get(0));
    }
    
    @Test
    public void testConfirmDeprecateProfileCurrentProfileDeactivated() {
        ProfileEntity deprecatedEntity = new ProfileEntity();
        deprecatedEntity.setId("0000-0000-0000-0123");
        deprecatedEntity.setDeactivationDate(new Date());
        when(mockProfileEntityCacheManager.retrieve("0000-0000-0000-0123")).thenReturn(deprecatedEntity);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication("0000-0000-0000-0123"));
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid("0000-0000-0000-0123");
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0124");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile);
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.this_profile_deactivated", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testConfirmDeprecateProfileUnkownError() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid("0000-0000-0000-0003");
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0004");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNotNull(deprecateProfile);
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.problem_deprecating", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testConfirmDeprecateProfileWithInvalidDataBadCredentials() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));

        // Using email
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid(USER_ORCID);
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0002_1@test.orcid.org");
        deprecateProfile.setDeprecatingPassword("invalid password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("check_password_modal.incorrect_password", deprecateProfile.getErrors().get(0));

        // Using orcid
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("invalid password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("check_password_modal.incorrect_password", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testConfirmDeprecateProfileWithInvalidDataAlreadyDeprecatedProfile() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        // Apply mocks
        mocksForDeprecatedAccounts();

        // Using orcid
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid(USER_ORCID);
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.already_deprecated", deprecateProfile.getErrors().get(0));

        // Using email
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0002_1@test.orcid.org");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.already_deprecated", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testConfirmDeprecateProfileWithInvalidDataDeactivatedProfile() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));

        // Apply mocks
        mocksForDeactivatedAccounts();

        // Using orcid
        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setPrimaryOrcid(USER_ORCID);
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.already_deactivated", deprecateProfile.getErrors().get(0));

        // Using email
        deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail("0000-0000-0000-0002_1@test.orcid.org");
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.already_deactivated", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testConfirmDeprecateProfileWithInvalidDataMatchingAccounts() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(DEPRECATED_USER_ORCID));

        DeprecateProfile deprecateProfile = new DeprecateProfile();
        deprecateProfile.setDeprecatingOrcidOrEmail(DEPRECATED_USER_ORCID);
        deprecateProfile.setPrimaryOrcid(DEPRECATED_USER_ORCID);
        deprecateProfile.setDeprecatingPassword("password");

        deprecateProfile = controller.confirmDeprecateProfile(deprecateProfile);
        assertNull(deprecateProfile.getDeprecatingAccountName());
        assertNotNull(deprecateProfile.getErrors());
        assertEquals(1, deprecateProfile.getErrors().size());
        assertEquals("deprecate_orcid.profile_matches_current", deprecateProfile.getErrors().get(0));
    }

    @Test
    public void testAddDelegate() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));

        when(mockProfileEntityCacheManager.retrieve(Mockito.anyString())).then(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity entity = new ProfileEntity();
                entity.setId(invocation.getArgument(0));
                // Mark it as deactivated
                entity.setDeactivationDate(new Date());
                entity.setEncryptedPassword("password");
                return entity;
            }
        });
        
        ManageDelegate addDelegate = new ManageDelegate();
        addDelegate.setDelegateToManage("0000-0000-0000-0000");
        addDelegate.setPassword("password");
        controller.addDelegate(addDelegate);
        verify(mockGivenPermissionToManager, times(1)).create(USER_ORCID, "0000-0000-0000-0000");
    }
    
    @Test
    public void testStripHtmlFromNames() {
        RecordNameManager mockRecordNameManager = Mockito.mock(RecordNameManager.class);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        TargetProxyHelper.injectIntoProxy(controller, "recordNameManager", mockRecordNameManager);
        
        when(mockRecordNameManager.exists(Mockito.anyString())).thenReturn(true);
        
        NamesForm nf = new NamesForm();
        nf.setCreditName(Text.valueOf("<button onclick=\"alert('hello')\">Credit Name</button>"));
        nf.setGivenNames(Text.valueOf("<button onclick=\"alert('hello')\">Given Names</button>"));
        nf.setFamilyName(Text.valueOf("<button onclick=\"alert('hello')\">Family Name</button>"));
        nf.setVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(Visibility.PUBLIC));
        nf = controller.setNameFormJson(nf);
        assertEquals("Credit Name", nf.getCreditName().getValue());
        assertEquals("Given Names", nf.getGivenNames().getValue());
        assertEquals("Family Name", nf.getFamilyName().getValue());
    
        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setVisibility(Visibility.PUBLIC);
        
        verify(mockRecordNameManager, times(1)).updateRecordName(eq(USER_ORCID), eq(name));
    }
    
    @Test
    public void testValidateBiography() {
        BiographyManager mockBiographyManager = Mockito.mock(BiographyManager.class);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        TargetProxyHelper.injectIntoProxy(controller, "biographyManager", mockBiographyManager);
        
        when(mockBiographyManager.exists(Mockito.anyString())).thenReturn(true);
                
        BiographyForm bf = new BiographyForm();
        bf.setVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(Visibility.PUBLIC));
        // No NPE exception on empty bio
        controller.setBiographyFormJson(bf);
        assertNotNull(bf.getErrors());
        assertTrue(bf.getErrors().isEmpty());
        String bio = StringUtils.repeat('a', 5001);
        bf.setBiography(Text.valueOf(bio));
        controller.setBiographyFormJson(bf);
        assertEquals(1, bf.getErrors().size());
        assertEquals("Length.changePersonalInfoForm.biography", bf.getErrors().get(0));        
        bio = StringUtils.repeat('a', 5000);
        bf.setBiography(Text.valueOf(bio));
        bf.setVisibility(null);
        controller.setBiographyFormJson(bf);
        assertEquals(1, bf.getErrors().size());
        assertEquals("common.visibility.not_blank", bf.getErrors().get(0));        
        
        bf.setBiography(Text.valueOf(bio));
        bf.setVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(Visibility.PUBLIC));
        controller.setBiographyFormJson(bf);
        assertTrue(bf.getErrors().isEmpty()); 
        
        Biography bioElement = new Biography();
        bioElement.setContent(bio);      
        bioElement.setVisibility(Visibility.PUBLIC);
        
        verify(mockBiographyManager, times(1)).updateBiography(eq(USER_ORCID), eq(bioElement));
    }
    
    @Test
    public void testRevokeDelegate() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        ManageDelegate manageDelegate = new ManageDelegate();
        manageDelegate.setDelegateToManage("0000-0000-0000-0000");
        manageDelegate.setPassword("password");
        
        controller.revokeDelegate(manageDelegate);
        
        assertEquals(0, manageDelegate.getErrors().size());
        
        verify(mockGivenPermissionToManager, times(1)).remove(eq(USER_ORCID), eq("0000-0000-0000-0000"));      
    }
    
    @Test
    public void validateEmailAddressTest() {
    	assertTrue(controller.validateEmailAddress("test@orcid.org"));
    	assertTrue(controller.validateEmailAddress("test.test@orcid.org"));
    	assertTrue(controller.validateEmailAddress("test-test@orcid.org"));
    	assertTrue(controller.validateEmailAddress("test_test@orcid.org"));
    	assertTrue(controller.validateEmailAddress("1test@orcid.org"));
    	assertTrue(controller.validateEmailAddress("test1@orcid.org"));
    	// Examples from https://en.wikipedia.org/wiki/Email_address#Examples
    	assertTrue(controller.validateEmailAddress("user.name+tag+sorting@example.com"));
    	assertTrue(controller.validateEmailAddress("example-indeed@strange-example.com"));
    	assertTrue(controller.validateEmailAddress("#!$%&'*+-/=?^_`{}|~@example.org"));
    	
    	assertFalse(controller.validateEmailAddress(null));
    	assertFalse(controller.validateEmailAddress(""));
    	assertFalse(controller.validateEmailAddress("@"));
    	assertFalse(controller.validateEmailAddress("@."));
    	assertFalse(controller.validateEmailAddress("test"));
    	assertFalse(controller.validateEmailAddress("test@"));

    	assertFalse(controller.validateEmailAddress("@test"));
    	assertFalse(controller.validateEmailAddress("@test.com"));
    	// Examples from https://en.wikipedia.org/wiki/Email_address#Examples
    	assertFalse(controller.validateEmailAddress("Abc.example.com"));
    	assertFalse(controller.validateEmailAddress("A@b@c@example.com"));
    	assertFalse(controller.validateEmailAddress("john.doe@example..com"));

        assertFalse(controller.validateEmailAddress("test@test"));
    	assertTrue(controller.validateEmailAddress("john..doe@example.com"));
    	assertTrue(controller.validateEmailAddress("a\"b(c)d,e:f;g<h>i[j\\k]l@example.com"));

        assertTrue(controller.validateEmailAddress("test@test.inc"));
        assertTrue(controller.validateEmailAddress("test@test.africa"));
        assertTrue(controller.validateEmailAddress("test@test.中国"));
        assertTrue(controller.validateEmailAddress("test@test.llc"));
    }
    
    @Test
    public void testDeleteEmailJsonBlankEmail() {
        Errors errors = controller.deleteEmailJson("");
        assertNotNull(errors);
        assertEquals(1, errors.getErrors().size());
    }
    
    @Test
    public void testDeleteEmailJsonWrongOwner() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        when(mockEmailManager.findOrcidIdByEmail(eq("email@email.com"))).thenReturn("another-orcid-id");
        
        Errors errors = controller.deleteEmailJson("email@email.com");
        assertNotNull(errors);
        assertEquals(1, errors.getErrors().size());
        
        verify(mockEmailManager, Mockito.times(1)).findOrcidIdByEmail(eq("email@email.com"));
    }
    
    @Test
    public void testDeleteEmailPrimaryEmail() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        when(mockEmailManager.findOrcidIdByEmail(eq("email@email.com"))).thenReturn(USER_ORCID);
        when(mockEmailManager.isPrimaryEmail(eq(USER_ORCID), eq("email@email.com"))).thenReturn(true);
        
        Errors errors = controller.deleteEmailJson("email@email.com");
        assertNotNull(errors);
        assertEquals(1, errors.getErrors().size());
        
        verify(mockEmailManager, Mockito.times(1)).findOrcidIdByEmail(eq("email@email.com"));
        verify(mockEmailManager, Mockito.times(1)).isPrimaryEmail(eq(USER_ORCID), eq("email@email.com"));
    }
    
    @Test
    public void testDeleteEmailOnlyEmail() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        when(mockEmailManager.findOrcidIdByEmail(eq("email@email.com"))).thenReturn(USER_ORCID);
        when(mockEmailManager.isPrimaryEmail(eq(USER_ORCID), eq("email@email.com"))).thenReturn(false);
        when(mockEmailManager.isUsersOnlyEmail(eq(USER_ORCID), eq("email@email.com"))).thenReturn(true);
        
        Errors errors = controller.deleteEmailJson("email@email.com");
        assertNotNull(errors);
        assertEquals(1, errors.getErrors().size());
        
        verify(mockEmailManager, Mockito.times(1)).findOrcidIdByEmail(eq("email@email.com"));
        verify(mockEmailManager, Mockito.times(1)).isPrimaryEmail(eq(USER_ORCID), eq("email@email.com"));
        verify(mockEmailManager, Mockito.times(1)).isUsersOnlyEmail(eq(USER_ORCID), eq("email@email.com"));
    }
    
    @Test
    public void testDeleteEmail() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        when(mockEmailManager.findOrcidIdByEmail(eq("email@email.com"))).thenReturn(USER_ORCID);
        when(mockEmailManager.isPrimaryEmail(eq(USER_ORCID), eq("email@email.com"))).thenReturn(false);
        when(mockEmailManager.isUsersOnlyEmail(eq(USER_ORCID), eq("email@email.com"))).thenReturn(false);
        Mockito.doNothing().when(mockEmailManager).removeEmail(eq(USER_ORCID), eq("email@email.com"));
        
        Errors errors = controller.deleteEmailJson("email@email.com");
        assertNotNull(errors);
        assertEquals(0, errors.getErrors().size());
        
        verify(mockEmailManager, Mockito.times(1)).findOrcidIdByEmail(eq("email@email.com"));
        verify(mockEmailManager, Mockito.times(1)).isPrimaryEmail(eq(USER_ORCID), eq("email@email.com"));
        verify(mockEmailManager, Mockito.times(1)).isUsersOnlyEmail(eq(USER_ORCID), eq("email@email.com"));
        verify(mockEmailManager, Mockito.times(1)).removeEmail(eq(USER_ORCID), eq("email@email.com"));        
    }
    
    @Test
    public void testVerifyEmail() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        Email email = new Email();
        email.setEmail("email@email.com");
        when(mockEmailManager.findPrimaryEmail(eq(USER_ORCID))).thenReturn(email);
        when(mockEmailManager.findOrcidIdByEmail(eq("email@email.com"))).thenReturn(USER_ORCID);
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(); 
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        controller.verifyEmail(mockRequest, "email@email.com");
        
        verify(mockRecordEmailSender, Mockito.times(1)).sendVerificationEmail(eq(USER_ORCID), eq("email@email.com"), eq(false));
    }
    
    @Test
    public void testAddEmail_noPrimaryEmailChange() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(); 
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        AddEmail newEmail = new AddEmail();
        newEmail.setValue("new@email.com");
        newEmail.setPassword("password");
        newEmail.setCurrent(true);
        newEmail.setPrimary(false);
        newEmail.setVerified(false);        
        
        when(mockEmailManager.addEmail(eq(USER_ORCID), eq(newEmail.toV3Email()))).thenReturn(Map.of());                        
        when(mockEmailManager.emailExists(eq("new@email.com"))).thenReturn(false);
        
        controller.addEmails(mockRequest, newEmail);

        verify(mockRecordEmailSender, Mockito.times(1)).sendVerificationEmail(eq(USER_ORCID), eq("new@email.com"), eq(false));
    }
    
    @Test
    public void testAddEmail_primaryEmailChange() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(); 
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        AddEmail newEmail = new AddEmail();
        newEmail.setValue("new@email.com");
        newEmail.setPassword("password");
        newEmail.setCurrent(true);
        newEmail.setPrimary(false);
        newEmail.setVerified(false);
        
        when(mockEmailManager.addEmail(eq(USER_ORCID), eq(newEmail.toV3Email()))).thenReturn(Map.of("new", "new@email.com", "old", "old@email.com"));                     
        when(mockEmailManager.emailExists(eq("new@email.com"))).thenReturn(false);        
        
        controller.addEmails(mockRequest, newEmail);
        
        verify(mockRecordEmailSender, Mockito.times(1)).sendVerificationEmail(eq(USER_ORCID), eq("new@email.com"), eq(false));
    }
    
    @Test
    public void testSetPrimary_nothingChange() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(); 
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));       
        when(mockEmailManager.findOrcidIdByEmail(eq("email@orcid.org"))).thenReturn(USER_ORCID);
        
        org.orcid.pojo.ajaxForm.Email email = new org.orcid.pojo.ajaxForm.Email();
        email.setValue("email@orcid.org");
        
        when(mockEmailManager.setPrimary(eq(USER_ORCID), eq("email@orcid.org"), eq(mockRequest))).thenReturn(Map.of());
        
        controller.setPrimary(mockRequest, email);
        
        verify(mockRecordEmailSender, Mockito.never()).sendVerificationEmail(any(), any(), any());
    }
    
    @Test
    public void testSetPrimary_primaryEmailChange() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(); 
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));       
        when(mockEmailManager.findOrcidIdByEmail(eq("email@orcid.org"))).thenReturn(USER_ORCID);
        
        org.orcid.pojo.ajaxForm.Email email = new org.orcid.pojo.ajaxForm.Email();
        email.setValue("email@orcid.org");
                
        when(mockEmailManager.setPrimary(eq(USER_ORCID), eq("email@orcid.org"), eq(mockRequest))).thenReturn(Map.of("new", "email@orcid.org", "old", "old@orcid.org"));
        
        controller.setPrimary(mockRequest, email);
        
        verify(mockRecordEmailSender, Mockito.never()).sendVerificationEmail(any(), any(), any());
    }
    
    @Test
    public void testSetPrimary_primaryEmailChangeAndPrimaryIsNotVerified() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(); 
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));       
        when(mockEmailManager.findOrcidIdByEmail(eq("email@orcid.org"))).thenReturn(USER_ORCID);
        
        org.orcid.pojo.ajaxForm.Email email = new org.orcid.pojo.ajaxForm.Email();
        email.setValue("email@orcid.org");
        email.setCurrent(true);
        email.setPrimary(false);
        email.setVerified(false);
        
        when(mockEmailManager.setPrimary(eq(USER_ORCID), eq("email@orcid.org"), eq(mockRequest))).thenReturn(Map.of("new", "email@orcid.org", "old", "old@orcid.org", "sendVerification", "true"));
        
        controller.setPrimary(mockRequest, email);
        
        verify(mockRecordEmailSender, Mockito.times(1)).sendVerificationEmail(eq(USER_ORCID), eq("email@orcid.org"), eq(true));
    }
    
    @Test
    public void testEditEmail_noPrimaryChange() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(); 
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));       
        when(mockEmailManager.findOrcidIdByEmail(eq("old@orcid.org"))).thenReturn(USER_ORCID);
        
        org.orcid.pojo.ajaxForm.EditEmail email = new org.orcid.pojo.ajaxForm.EditEmail();
        email.setEdited("email@orcid.org");
        email.setOriginal("old@orcid.org");
        
        when(mockEmailManager.editEmail(eq(USER_ORCID), eq("old@orcid.org"), eq("email@orcid.org"), any())).thenReturn(Map.of("verifyAddress", "email@orcid.org"));
        when(mockEmailManager.emailExists(eq("old@orcid.org"))).thenReturn(true);
        when(mockEmailManager.emailExists(eq("email@orcid.org"))).thenReturn(false);
        
        controller.editEmail(mockRequest, email);
        
        verify(mockRecordEmailSender, Mockito.times(1)).sendVerificationEmail(eq(USER_ORCID), eq("email@orcid.org"), eq(false));
    }
    
    @Test
    public void testEditEmail_primaryEmailChange() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        MockHttpServletRequest mockRequest = new MockHttpServletRequest(); 
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));       
        when(mockEmailManager.findOrcidIdByEmail(eq("old@orcid.org"))).thenReturn(USER_ORCID);
        
        org.orcid.pojo.ajaxForm.EditEmail email = new org.orcid.pojo.ajaxForm.EditEmail();
        email.setEdited("email@orcid.org");
        email.setOriginal("old@orcid.org");
        
        when(mockEmailManager.editEmail(eq(USER_ORCID), eq("old@orcid.org"), eq("email@orcid.org"), any())).thenReturn(Map.of("verifyAddress", "email@orcid.org", "new", "email@orcid.org", "old", "old@orcid.org"));
        controller.editEmail(mockRequest, email);
        
        verify(mockRecordEmailSender, Mockito.times(1)).sendVerificationEmail(eq(USER_ORCID), eq("email@orcid.org"), eq(true));
    }

    @Test
    public void testEmptyEmailSource() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        when(mockProfileEmailDomainManagerReadOnly.getEmailDomains(eq(USER_ORCID))).thenReturn(null);
        when(mockEmailManager.getPublicEmails(eq(USER_ORCID))).thenReturn(new Emails());
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        org.orcid.pojo.ajaxForm.Emails emails = controller.getEmails(mockRequest);

        assertEquals(3, emails.getEmails().size());

        org.orcid.pojo.ajaxForm.Email email1 = emails.getEmails().get(0);
        assertEquals(email1.getValue(), USER_ORCID + "_1@test.orcid.org");
        assertEquals(email1.getSource(), USER_ORCID);
        assertNull(email1.getSourceName());

        org.orcid.pojo.ajaxForm.Email email2 = emails.getEmails().get(1);
        assertEquals(email2.getValue(), USER_ORCID + "_2@test.orcid.org");
        assertNull(email2.getSource());
        assertEquals(email2.getSourceName(), USER_CREDIT_NAME);
    }

    @Test
    public void testEmailSourceWithSourceName() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        when(mockProfileEmailDomainManagerReadOnly.getEmailDomains(eq(USER_ORCID))).thenReturn(null);
        when(mockEmailManager.getPublicEmails(eq(USER_ORCID))).thenReturn(new Emails());
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        org.orcid.pojo.ajaxForm.Emails emails = controller.getEmails(mockRequest);

        assertEquals(3, emails.getEmails().size());

        org.orcid.pojo.ajaxForm.Email email2 = emails.getEmails().get(1);
        assertEquals(email2.getValue(), USER_ORCID + "_2@test.orcid.org");
        assertNull(email2.getSource());
        assertEquals(email2.getSourceName(), USER_CREDIT_NAME);
    }

    @Test
    public void testEmailSourceWithSourceId() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        when(mockProfileEmailDomainManagerReadOnly.getEmailDomains(eq(USER_ORCID))).thenReturn(null);
        when(mockEmailManager.getPublicEmails(eq(USER_ORCID))).thenReturn(new Emails());
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        MockHttpSession mockSession = new MockHttpSession();
        mockRequest.setSession(mockSession);
        org.orcid.pojo.ajaxForm.Emails emails = controller.getEmails(mockRequest);

        assertEquals(3, emails.getEmails().size());

        org.orcid.pojo.ajaxForm.Email email3 = emails.getEmails().get(2);
        assertEquals(email3.getValue(), USER_ORCID + "_3@test.orcid.org");
        assertNull(email3.getSourceName());
        assertEquals(email3.getSource(), USER_ORCID);
    }



    protected Authentication getAuthentication(String orcid) {
        List<OrcidWebRole> roles = Arrays.asList(OrcidWebRole.ROLE_USER);
        OrcidProfileUserDetails details = new OrcidProfileUserDetails(orcid, "user_1@test.orcid.org", null, roles);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(orcid, null, roles);
        auth.setDetails(details);
        return auth;
    }
}