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

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.EmailManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.AdminChangePassword;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AdminControllerTest extends BaseControllerTest {

    @Resource(name = "adminController")
    AdminController adminController;

    @Resource
    private ProfileDao profileDao;

    @Resource
    protected OrcidProfileManager orcidProfileManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Resource
    private EmailManager emailManager;
    
    @Resource
    private EmailDao emailDao;
    
    @Resource
    OrcidClientGroupManager orcidClientGroupManager;
    
    @Resource
    GroupAdministratorController groupAdministratorController;
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml"));
    }

    @Before
    public void beforeInstance() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        assertNotNull(adminController);
        assertNotNull(profileDao);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }
    
    @Override
    protected Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4440");

        OrcidProfileUserDetails details = null;
        if(orcidProfile.getType() != null){             
                details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail()
                    .get(0).getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent(), orcidProfile.getType(), orcidProfile.getGroupType());
        } else {
                details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail()
                    .get(0).getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent());
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, "4444-4444-4444-4440", getRole());
        return auth;
    }
    
    protected List<OrcidWebRole> getRole() {
        return Arrays.asList(OrcidWebRole.ROLE_ADMIN);
    }
    
    @Test
    public void testCheckOrcid() throws Exception {
        ProfileDetails profileDetails = adminController.checkOrcidToDeprecate("4444-4444-4444-4447");
        assertNotNull(profileDetails);
        assertEquals(0, profileDetails.getErrors().size());
        assertEquals("otis@reading.com", profileDetails.getEmail());
        assertEquals("Redding", profileDetails.getFamilyName());
        assertEquals("Otis", profileDetails.getGivenNames());
        assertEquals("4444-4444-4444-4447", profileDetails.getOrcid());

        //Must throw exception
        profileDetails = adminController.checkOrcidToDeprecate("4444-4444-4444-4411");
        assertNotNull(profileDetails);
        assertEquals(1, profileDetails.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.inexisting_orcid", "4444-4444-4444-4411"), profileDetails.getErrors().get(0));
    }

    @Test
    public void testDeprecateProfile() throws Exception {        
        OrcidProfile toDeprecate = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4441");               
        OrcidProfile primary = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");

        boolean containsEmail = false;

        assertNull(toDeprecate.getOrcidDeprecated());
        assertNotNull(toDeprecate.getOrcidBio());
        assertNotNull(toDeprecate.getOrcidBio().getPersonalDetails());
        assertEquals("One", toDeprecate.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        assertEquals("User", toDeprecate.getOrcidBio().getPersonalDetails().getFamilyName().getContent());
        assertEquals("Credit Name", toDeprecate.getOrcidBio().getPersonalDetails().getCreditName().getContent());
        assertNotNull(toDeprecate.getOrcidBio().getKeywords());
        assertNotNull(toDeprecate.getOrcidBio().getKeywords().getKeyword());
        assertEquals(2, toDeprecate.getOrcidBio().getKeywords().getKeyword().size());
        assertNotNull(toDeprecate.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName());
        assertEquals(2, toDeprecate.getOrcidBio().getPersonalDetails().getOtherNames().getOtherName().size());
        
        List<Email> emails1 = toDeprecate.getOrcidBio().getContactDetails().getEmail();
        assertNotNull(emails1);
        assertEquals(5, emails1.size());

        for (Email email : emails1) {
            if (email.getValue().equals("1@deprecate.com")) {
                if (email.isCurrent() == false && email.isVerified() == true) {
                    containsEmail = true;
                } else {
                    containsEmail = false;
                    break;
                }
            } else if (email.getValue().equals("2@deprecate.com")) {
                if (email.isCurrent() == false && email.isVerified() == false) {
                    containsEmail = true;
                } else {
                    containsEmail = false;
                    break;
                }
            } else if (email.getValue().equals("spike@milligan.com")) {
                if (email.isCurrent() == true && email.isVerified() == true && email.isPrimary() == true) {
                    containsEmail = true;
                } else {
                    containsEmail = false;
                    break;
                }
            }
        }

        assertTrue(containsEmail);

        List<Email> emails2 = primary.getOrcidBio().getContactDetails().getEmail();
        assertNotNull(emails2);
        assertEquals(1, emails2.size());

        ProfileDeprecationRequest result = adminController.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442");
        
        assertEquals(0, result.getErrors().size());      
        
        
        Map<String, String> emails = adminController.findIdByEmailHelper("1@deprecate.com,2@deprecate.com,spike@milligan.com,michael@bentine.com");
        assertEquals("4444-4444-4444-4442", emails.get("1@deprecate.com"));
        assertEquals("4444-4444-4444-4442", emails.get("2@deprecate.com"));
        assertEquals("4444-4444-4444-4442", emails.get("spike@milligan.com"));
        assertEquals("4444-4444-4444-4442", emails.get("michael@bentine.com"));
                
        ProfileEntity deprecated = adminController.getProfileEntityManager().findByOrcid("4444-4444-4444-4441");
                
        if(deprecated.getRecordNameEntity() != null) {
            assertEquals("Given Names Deactivated", deprecated.getRecordNameEntity().getGivenNames());
            assertEquals("Family Name Deactivated", deprecated.getRecordNameEntity().getFamilyName());
        } else {
            assertEquals("Given Names Deactivated", deprecated.getGivenNames());
            assertEquals("Family Name Deactivated", deprecated.getFamilyName());
        }
    }

    @Test         
    public void tryToDeprecateDeprecatedProfile() throws Exception {        
        // Test deprecating a deprecated account
        ProfileDeprecationRequest result = adminController.deprecateProfile("4444-4444-4444-444X", "4444-4444-4444-4443");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.already_deprecated", "4444-4444-4444-444X"), result.getErrors().get(0));
        
        // Test deprecating account with himself
        result = adminController.deprecateProfile("4444-4444-4444-4440", "4444-4444-4444-4440");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.deprecated_equals_primary"), result.getErrors().get(0));

        // Test set deprecated account as a primary account
        result = adminController.deprecateProfile("4444-4444-4444-4443", "4444-4444-4444-444X");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_deprecated", "4444-4444-4444-444X"), result.getErrors().get(0));
        
        
        // Test deprecating an invalid orcid
        result = adminController.deprecateProfile("4444-4444-4444-444", "4444-4444-4444-4443");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.invalid_orcid", "4444-4444-4444-444"), result.getErrors().get(0));

        // Test use invalid orcid as primary
        result = adminController.deprecateProfile("4444-4444-4444-4440", "4444-4444-4444-444");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.invalid_orcid", "4444-4444-4444-444"), result.getErrors().get(0));

        ProfileEntity deactiveProfile = profileDao.find("4444-4444-4444-4443");
        deactiveProfile.setDeactivationDate(new Date());
        profileDao.merge(deactiveProfile);
        profileDao.flush();
        profileDao.refresh(deactiveProfile);

        // Test set deactive primary account
        result = adminController.deprecateProfile("4444-4444-4444-4440", "4444-4444-4444-4443");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_is_deactivated", "4444-4444-4444-4443"), result.getErrors().get(0));
    }

    @Test
    public void deactivateAndReactivateProfileTest() throws Exception {
        // Test deactivate
        Map<String, Set<String>> result = adminController.deactivateOrcidAccount("4444-4444-4444-4445");
        assertEquals(1, result.get("deactivateSuccessfulList").size());

        profileDao.refresh(profileDao.find("4444-4444-4444-4445"));
        ProfileEntity deactivated = profileDao.find("4444-4444-4444-4445");
        assertNotNull(deactivated.getDeactivationDate());
        assertEquals(deactivated.getRecordNameEntity().getFamilyName(), "Family Name Deactivated");
        assertEquals(deactivated.getRecordNameEntity().getGivenNames(), "Given Names Deactivated");

        // Test try to deactivate an already deactive account
        result = adminController.deactivateOrcidAccount("4444-4444-4444-4445");
        assertEquals(1, result.get("alreadyDeactivatedList").size());

        // Test reactivate
        ProfileDetails proDetails = adminController.reactivateOrcidAccount("4444-4444-4444-4445");
        assertEquals(0, proDetails.getErrors().size());

        profileDao.refresh(profileDao.find("4444-4444-4444-4445"));
        deactivated = profileDao.find("4444-4444-4444-4445");
        assertNull(deactivated.getDeactivationDate());

        // Try to reactivate an already active account
        proDetails = adminController.reactivateOrcidAccount("4444-4444-4444-4445");
        assertEquals(1, proDetails.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_reactivation.errors.already_active", new ArrayList<String>()), proDetails.getErrors().get(0));
    }    
    
    @Test
    public void findIdsTest(){
        Map<String, String> ids = adminController.findIdByEmailHelper("spike@milligan.com,michael@bentine.com,peter@sellers.com,invalid@email.com");
        assertNotNull(ids);
        assertEquals(3, ids.size());
        assertTrue(ids.containsKey("spike@milligan.com"));
        assertEquals("4444-4444-4444-4441", ids.get("spike@milligan.com"));
        assertTrue(ids.containsKey("michael@bentine.com"));
        assertEquals("4444-4444-4444-4442", ids.get("michael@bentine.com"));
        assertTrue(ids.containsKey("peter@sellers.com"));
        assertEquals("4444-4444-4444-4443", ids.get("peter@sellers.com"));
        assertFalse(ids.containsKey("invalid@email.com"));
    }
    
    @Test
    public void removeSecurityQuestionTest() {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4440"); 
        assertNotNull(orcidProfile.getSecurityQuestionAnswer());
        adminController.removeSecurityQuestion(null, "4444-4444-4444-4440");
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4440");
        assertNull(orcidProfile.getSecurityQuestionAnswer());
    }
    
    @Test
    public void removeSecurityQuestionUsingEmailTest() {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442"); 
        assertNotNull(orcidProfile.getSecurityQuestionAnswer());
        adminController.removeSecurityQuestion(null, "michael@bentine.com");
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");
        assertNull(orcidProfile.getSecurityQuestionAnswer());
    }
    
    @Test
    public void resetPasswordTest() {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4441");
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=",orcidProfile.getPassword());
        AdminChangePassword form = new AdminChangePassword();
        form.setOrcidOrEmail("4444-4444-4444-4441");
        form.setPassword("password1");
        adminController.resetPassword(null, form);
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4441");
        assertFalse("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=".equals(orcidProfile.getPassword()));
    }
    
    @Test
    public void resetPasswordUsingEmailTest() {
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");
        assertEquals("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=",orcidProfile.getPassword());
        AdminChangePassword form = new AdminChangePassword();
        form.setOrcidOrEmail("michael@bentine.com");
        form.setPassword("password1");
        adminController.resetPassword(null, form);
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("4444-4444-4444-4442");
        assertFalse("e9adO9I4UpBwqI5tGR+qDodvAZ7mlcISn+T+kyqXPf2Z6PPevg7JijqYr6KGO8VOskOYqVOEK2FEDwebxWKGDrV/TQ9gRfKWZlzxssxsOnA=".equals(orcidProfile.getPassword()));
    }
    
    @Test
    public void verifyEmailTest() {
        //Add not verified email
        Email email = new Email("not-verified@email.com");
        email.setCurrent(false);
        email.setPrimary(false);
        email.setVerified(false);
        email.setVisibility(Visibility.PUBLIC);
        emailManager.addEmail("4444-4444-4444-4499", email);
                
        //Verify the email
        adminController.adminVerifyEmail("not-verified@email.com");
        EmailEntity emailEntity = emailDao.find("not-verified@email.com");
        assertNotNull(emailEntity);
        assertTrue(emailEntity.getVerified());
    }            
}