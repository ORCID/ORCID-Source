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

/**
 * Copyright 2012-2013 ORCID
 * 
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ProfileDeprecationRequest;
import org.orcid.pojo.ProfileDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AdminControllerTest extends BaseControllerTest {

    @Resource(name = "adminController")
    AdminController adminController;

    @Resource
    private ProfileDao profileDao;

    @Before
    public void init() {
        assertNotNull(adminController);
        assertNotNull(profileDao);
    }

    @Test
    public void testCheckOrcid() throws Exception {
        ProfileDetails profileDetails = adminController.checkOrcid("4444-4444-4444-4441");
        assertNotNull(profileDetails);
        assertEquals(0, profileDetails.getErrors().size());
        assertEquals("spike@milligan.com", profileDetails.getEmail());
        assertEquals("Milligan", profileDetails.getFamilyName());
        assertEquals("Spike", profileDetails.getGivenNames());
        assertEquals("4444-4444-4444-4441", profileDetails.getOrcid());

        profileDetails = adminController.checkOrcid("4444-4444-4444-4411");
        assertNotNull(profileDetails);
        assertEquals(1, profileDetails.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.inexisting_orcid", "4444-4444-4444-4411"), profileDetails.getErrors().get(0));
    }

    @Test
    @Rollback(true)
    public void testDeprecateProfile() throws Exception {
        ProfileEntity toDeprecate = profileDao.find("4444-4444-4444-4441");
        ProfileEntity primary = profileDao.find("4444-4444-4444-4442");

        boolean containsEmail = false;
        
        assertNull(toDeprecate.getPrimaryRecord());

        Set<EmailEntity> emails1 = toDeprecate.getEmails();
        assertNotNull(emails1);
        assertEquals(3, emails1.size());
        
        for(EmailEntity email : emails1){
            if(email.getId().equals("1@deprecate.com")){
                if(email.getCurrent() == false && email.getVerified() == true) {
                    containsEmail = true;
                } else {
                    containsEmail = false;
                    break;
                }
            } else if(email.getId().equals("2@deprecate.com")){
                if(email.getCurrent() == false && email.getVerified() == false) {
                    containsEmail = true;             
                } else {
                    containsEmail = false;
                    break;
                }
            } else if (email.getId().equals("spike@milligan.com")){
                if(email.getCurrent() == true && email.getVerified() == true && email.getPrimary() == true) {
                    containsEmail = true;
                } else {
                    containsEmail = false;
                    break;
                }
            }
        }
        
        assertTrue(containsEmail);

        Set<EmailEntity> emails2 = primary.getEmails();
        assertNotNull(emails2);
        assertEquals(1, emails2.size());

        ProfileDeprecationRequest result = adminController.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442");

        assertEquals(0, result.getErrors().size());

        profileDao.refresh(toDeprecate);
        profileDao.refresh(primary);

        assertNotNull(toDeprecate.getPrimaryRecord());

        emails1 = toDeprecate.getEmails();
        assertNotNull(emails1);
        assertEquals(0, emails1.size());

        emails2 = primary.getEmails();
        assertNotNull(emails2);
        assertEquals(4, emails2.size());
        
        containsEmail = false;
        
        for(EmailEntity email : emails2){
            if(email.getId().equals("1@deprecate.com")){
                if(email.getCurrent() == false && email.getVerified() == true) {
                    containsEmail = true;
                } else {
                    containsEmail = false;
                    break;
                }
            } else if(email.getId().equals("2@deprecate.com")){
                if(email.getCurrent() == false && email.getVerified() == false) {
                    containsEmail = true;             
                } else {
                    containsEmail = false;
                    break;
                }
            } else if (email.getId().equals("spike@milligan.com")){
                if(email.getCurrent() == true && email.getVerified() == true && email.getPrimary() == false) {
                    containsEmail = true;
                } else {
                    containsEmail = false;
                    break;
                }
            } else if (email.getId().equals("michael@bentine.com")){
                if(email.getCurrent() == true && email.getVerified() == true && email.getPrimary() == true) {
                    containsEmail = true;
                } else {
                    containsEmail = false;
                    break;
                }
            }
        }
        
        assertTrue(containsEmail);
    }

    @Test
    @Rollback(true)
    public void tryToDeprecateDeprecatedProfile() throws Exception {
        ProfileDeprecationRequest result = adminController.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442");
        assertEquals(0, result.getErrors().size());

        profileDao.refresh(profileDao.find("4444-4444-4444-4441"));
        profileDao.refresh(profileDao.find("4444-4444-4444-4442"));

        // Test deprecating a deprecated account
        result = adminController.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4443");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.already_deprecated", "4444-4444-4444-4441"), result.getErrors().get(0));

        // Test deprecating account with himself
        result = adminController.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4441");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.deprecated_equals_primary"), result.getErrors().get(0));

        // Test set deprecated account as a primary account
        result = adminController.deprecateProfile("4444-4444-4444-4442", "4444-4444-4444-4441");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_deprecated", "4444-4444-4444-4441"), result.getErrors().get(0));

        // Test deprecating an invalid orcid
        result = adminController.deprecateProfile("4444-4444-4444-444", "4444-4444-4444-4442");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.invalid_orcid", "4444-4444-4444-444"), result.getErrors().get(0));

        // Test use invalid orcid as primary
        result = adminController.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-444");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.invalid_orcid", "4444-4444-4444-444"), result.getErrors().get(0));

        ProfileEntity deactiveProfile = profileDao.find("4444-4444-4444-4443");
        deactiveProfile.setDeactivationDate(new Date());
        profileDao.merge(deactiveProfile);
        profileDao.flush();
        profileDao.refresh(deactiveProfile);

        // Test set deactive primary account
        result = adminController.deprecateProfile("4444-4444-4444-4442", "4444-4444-4444-4443");
        assertEquals(1, result.getErrors().size());
        assertEquals(adminController.getMessage("admin.profile_deprecation.errors.primary_account_is_deactivated", "4444-4444-4444-4443"), result.getErrors().get(0));
    }
}
