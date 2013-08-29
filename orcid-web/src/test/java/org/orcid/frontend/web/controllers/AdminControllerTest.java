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
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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

    @Before
    public void initMocks() throws Exception {

    }

    @Before
    public void init() {
        assertNotNull(adminController);
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
        ProfileEntity toDeprecate = adminController.getProfileEntityManager().findByOrcid("4444-4444-4444-4441");
        ProfileEntity primary = adminController.getProfileEntityManager().findByOrcid("4444-4444-4444-4442");

        assertNull(toDeprecate.getPrimaryRecord());
        
        Set<EmailEntity> emails1 = toDeprecate.getEmails();
        assertNotNull(emails1);
        assertEquals(3, emails1.size());
        
        Set<EmailEntity> emails2 = primary.getEmails();
        assertNotNull(emails2);
        assertEquals(1, emails2.size());
        
        adminController.deprecateProfile("4444-4444-4444-4441", "4444-4444-4444-4442");
        
        toDeprecate = adminController.getProfileEntityManager().findByOrcid("4444-4444-4444-4441");
        primary = adminController.getProfileEntityManager().findByOrcid("4444-4444-4444-4442");
        
        assertNotNull(toDeprecate.getPrimaryRecord());
        
        emails1 = toDeprecate.getEmails();
        assertNull(emails1);        
        
        emails2 = primary.getEmails();
        assertNotNull(emails2);
        assertEquals(4, emails2.size());
    }
}
