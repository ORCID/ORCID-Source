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
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.DelegateForm;
import org.orcid.pojo.DeprecateProfile;
import org.orcid.test.TargetProxyHelper;
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

    @Mock
    private ProfileEntityCacheManager mockProfileEntityCacheManager;

    @Mock
    private EncryptionManager mockEncryptionManager;

    @Mock
    private EmailManager mockEmailManager;

    @Mock
    private LocaleManager mockLocaleManager;

    @Mock
    private ProfileEntityManager mockProfileEntityManager;

    private RecordNameEntity getRecordName(String orcidId) {
        RecordNameEntity recordName = new RecordNameEntity();
        recordName.setVisibility(Visibility.PUBLIC);
        recordName.setFamilyName(orcidId + " Family Name");
        recordName.setGivenNames(orcidId + " Given Names");
        return recordName;
    }

    @Before
    public void initMocks() throws Exception {
        controller = new ManageProfileController();
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(USER_ORCID));
        TargetProxyHelper.injectIntoProxy(controller, "profileEntityCacheManager", mockProfileEntityCacheManager);
        TargetProxyHelper.injectIntoProxy(controller, "encryptionManager", mockEncryptionManager);
        TargetProxyHelper.injectIntoProxy(controller, "emailManager", mockEmailManager);
        TargetProxyHelper.injectIntoProxy(controller, "localeManager", mockLocaleManager);
        TargetProxyHelper.injectIntoProxy(controller, "profileEntityManager", mockProfileEntityManager);

        when(mockEncryptionManager.hashMatches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(mockEncryptionManager.hashMatches(Mockito.eq("invalid password"), Mockito.anyString())).thenReturn(false);
        when(mockProfileEntityManager.deprecateProfile(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        when(mockProfileEntityManager.deprecateProfile(Mockito.eq(DEPRECATED_USER_ORCID), Mockito.eq(USER_ORCID))).thenReturn(true);

        when(mockLocaleManager.resolveMessage(Mockito.anyString(), Mockito.any())).thenAnswer(new Answer<String>() {

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
                Set<GivenPermissionToEntity> givenPermissionTo = new HashSet<GivenPermissionToEntity>();

                IntStream.range(0, 2).forEachOrdered(i -> {
                    GivenPermissionToEntity e1 = new GivenPermissionToEntity();
                    e1.setId(Long.valueOf(i));
                    Date now = new Date();
                    e1.setApprovalDate(now);
                    e1.setDateCreated(now);
                    e1.setGiver(invocation.getArgument(0));
                    ProfileSummaryEntity ps = new ProfileSummaryEntity();
                    RecordNameEntity recordName = new RecordNameEntity();
                    recordName.setVisibility(Visibility.PUBLIC);
                    if (i == 0) {
                        ps.setId("0000-0000-0000-0004");
                        recordName.setCreditName("Credit Name");
                    } else {
                        ps.setId("0000-0000-0000-0005");
                        recordName.setFamilyName("Family Name");
                        recordName.setGivenNames("Given Names");
                    }
                    ps.setRecordNameEntity(recordName);
                    e1.setReceiver(ps);
                    givenPermissionTo.add(e1);
                });
                entity.setGivenPermissionTo(givenPermissionTo);
                EmailEntity email1 = new EmailEntity();
                email1.setId(invocation.getArgument(0) + "_1@test.orcid.org");
                email1.setVerified(true);
                email1.setCurrent(true);
                email1.setDateCreated(new Date());
                email1.setLastModified(new Date());
                email1.setPrimary(true);
                email1.setVisibility(Visibility.PUBLIC);

                EmailEntity email2 = new EmailEntity();
                email2.setId(invocation.getArgument(0) + "_2@test.orcid.org");
                email2.setVerified(true);
                email2.setCurrent(false);
                email2.setDateCreated(new Date());
                email2.setLastModified(new Date());
                email2.setPrimary(false);
                email2.setVisibility(Visibility.PUBLIC);

                Set<EmailEntity> emails = new HashSet<EmailEntity>();
                emails.add(email1);
                emails.add(email2);
                entity.setEmails(emails);

                entity.setRecordNameEntity(getRecordName(invocation.getArgument(0)));
                entity.setEncryptedPassword("password");
                return entity;
            }
        });

        when(mockEmailManager.getEmails(Mockito.anyString(), Mockito.anyLong())).thenAnswer(new Answer<Emails>() {

            @Override
            public Emails answer(InvocationOnMock invocation) throws Throwable {
                Emails emails = new Emails();
                Email email1 = new Email();
                email1.setEmail(invocation.getArgument(0) + "_1@test.orcid.org");
                email1.setVisibility(Visibility.PUBLIC);
                emails.getEmails().add(email1);

                Email email2 = new Email();
                email2.setEmail(invocation.getArgument(0) + "_2@test.orcid.org");
                email2.setVisibility(Visibility.PUBLIC);
                emails.getEmails().add(email2);
                return emails;
            }

        });

        when(mockEmailManager.findCaseInsensitive(Mockito.anyString())).thenAnswer(new Answer<EmailEntity>() {

            @Override
            public EmailEntity answer(InvocationOnMock invocation) throws Throwable {
                String emailString = invocation.getArgument(0);
                String orcidString = emailString.substring(0, (emailString.indexOf("_")));
                EmailEntity email = new EmailEntity();
                email.setId(emailString);
                email.setVisibility(Visibility.PUBLIC);
                ProfileEntity entity = new ProfileEntity(orcidString);
                entity.setEncryptedPassword("password");
                entity.setRecordNameEntity(getRecordName(orcidString));
                email.setProfile(entity);
                return email;
            }
        });
    }

    private void mocksForDeprecatedAccounts() {
        when(mockProfileEntityCacheManager.retrieve(Mockito.eq(DEPRECATED_USER_ORCID))).then(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity entity = new ProfileEntity();
                entity.setId(invocation.getArgument(0));

                EmailEntity email1 = new EmailEntity();
                email1.setId(invocation.getArgument(0) + "_1@test.orcid.org");
                email1.setVerified(true);
                email1.setCurrent(true);
                email1.setDateCreated(new Date());
                email1.setLastModified(new Date());
                email1.setPrimary(true);
                email1.setVisibility(Visibility.PUBLIC);

                Set<EmailEntity> emails = new HashSet<EmailEntity>();
                emails.add(email1);
                entity.setEmails(emails);

                entity.setRecordNameEntity(getRecordName(invocation.getArgument(0)));
                // Mark it as deprecated
                entity.setDeprecatedDate(new Date());
                entity.setEncryptedPassword("password");
                return entity;
            }
        });

        when(mockEmailManager.findCaseInsensitive(Mockito.eq("0000-0000-0000-0002_1@test.orcid.org"))).thenAnswer(new Answer<EmailEntity>() {

            @Override
            public EmailEntity answer(InvocationOnMock invocation) throws Throwable {
                String emailString = invocation.getArgument(0);
                String orcidString = emailString.substring(0, (emailString.indexOf("_")));
                EmailEntity email = new EmailEntity();
                email.setId(emailString);
                email.setVisibility(Visibility.PUBLIC);
                ProfileEntity entity = new ProfileEntity(orcidString);
                entity.setEncryptedPassword("password");
                entity.setRecordNameEntity(getRecordName(orcidString));
                // Mark it as deprecated
                entity.setDeprecatedDate(new Date());
                email.setProfile(entity);
                return email;
            }
        });
    }

    private void mocksForDeactivatedAccounts() {
        when(mockProfileEntityCacheManager.retrieve(Mockito.eq(DEPRECATED_USER_ORCID))).then(new Answer<ProfileEntity>() {
            @Override
            public ProfileEntity answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity entity = new ProfileEntity();
                entity.setId(invocation.getArgument(0));

                EmailEntity email1 = new EmailEntity();
                email1.setId(invocation.getArgument(0) + "_1@test.orcid.org");
                email1.setVerified(true);
                email1.setCurrent(true);
                email1.setDateCreated(new Date());
                email1.setLastModified(new Date());
                email1.setPrimary(true);
                email1.setVisibility(Visibility.PUBLIC);

                Set<EmailEntity> emails = new HashSet<EmailEntity>();
                emails.add(email1);
                entity.setEmails(emails);

                entity.setRecordNameEntity(getRecordName(invocation.getArgument(0)));
                // Mark it as deactivated
                entity.setDeactivationDate(new Date());
                entity.setEncryptedPassword("password");
                return entity;
            }
        });

        when(mockEmailManager.findCaseInsensitive(Mockito.eq("0000-0000-0000-0002_1@test.orcid.org"))).thenAnswer(new Answer<EmailEntity>() {

            @Override
            public EmailEntity answer(InvocationOnMock invocation) throws Throwable {
                String emailString = invocation.getArgument(0);
                String orcidString = emailString.substring(0, (emailString.indexOf("_")));
                EmailEntity email = new EmailEntity();
                email.setId(emailString);
                email.setVisibility(Visibility.PUBLIC);
                ProfileEntity entity = new ProfileEntity(orcidString);
                entity.setEncryptedPassword("password");
                entity.setRecordNameEntity(getRecordName(orcidString));
                // Mark it as deactivated
                entity.setDeactivationDate(new Date());
                email.setProfile(entity);
                return email;
            }
        });
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
            assertEquals(USER_ORCID, form.getGiverOrcid().getValue());
            assertNotNull(form.getReceiverOrcid());
            if (form.getReceiverOrcid().getValue().equals("0000-0000-0000-0004")) {
                assertEquals("Credit Name", form.getReceiverName().getValue());
                found1 = true;
            } else {
                assertEquals("0000-0000-0000-0005", form.getReceiverOrcid().getValue());
                assertEquals("Given Names Family Name", form.getReceiverName().getValue());
                found2 = true;
            }
        }

        assertTrue(found1);
        assertTrue(found2);
    }

    @Test
    public void testAddDelegate() {
        fail();
    }
    
    @Test
    public void testAddDelegateByEmail() {
        fail();
    }
    
    @Test
    public void testRevokeDelegate() {
        fail();
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
        assertEquals(2, deprecateProfile.getDeprecatingEmails().size());
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_1@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_2@test.orcid.org"));

        assertEquals("0000-0000-0000-0001", deprecateProfile.getPrimaryOrcid());
        assertEquals("0000-0000-0000-0001 Given Names 0000-0000-0000-0001 Family Name", deprecateProfile.getPrimaryAccountName());
        assertNotNull(deprecateProfile.getPrimaryEmails());
        assertEquals(2, deprecateProfile.getPrimaryEmails().size());
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_1@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_2@test.orcid.org"));
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
        assertEquals(2, deprecateProfile.getDeprecatingEmails().size());
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_1@test.orcid.org"));
        assertTrue(deprecateProfile.getDeprecatingEmails().contains("0000-0000-0000-0002_2@test.orcid.org"));

        assertEquals("0000-0000-0000-0001", deprecateProfile.getPrimaryOrcid());
        assertEquals("0000-0000-0000-0001 Given Names 0000-0000-0000-0001 Family Name", deprecateProfile.getPrimaryAccountName());
        assertNotNull(deprecateProfile.getPrimaryEmails());
        assertEquals(2, deprecateProfile.getPrimaryEmails().size());
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_1@test.orcid.org"));
        assertTrue(deprecateProfile.getPrimaryEmails().contains("0000-0000-0000-0001_2@test.orcid.org"));
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

    protected Authentication getAuthentication(String orcid) {
        OrcidProfileUserDetails details = new OrcidProfileUserDetails(orcid, "user_1@test.orcid.org", null);
        details.setOrcidType(OrcidType.USER);
        return new UsernamePasswordAuthenticationToken(details, orcid, Arrays.asList(OrcidWebRole.ROLE_USER));
    }
}