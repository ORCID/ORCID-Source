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
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.pojo.ajaxForm.CustomEmailForm;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CustomEmailControllerTest extends BaseControllerTest {
    
    @Resource    
    CustomEmailController customEmailController;

    @Resource
    protected OrcidProfileManager orcidProfileManager;
    
    @Before
    public void init() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        assertNotNull(customEmailController);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml"));
    }

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }
    
    @Override
    protected Authentication getAuthentication() {
        orcidProfile = orcidProfileManager.retrieveOrcidProfile("5555-5555-5555-5558");

        OrcidProfileUserDetails details = null;
        if(orcidProfile.getType() != null){             
                details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail()
                    .get(0).getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent(), orcidProfile.getType(), orcidProfile.getGroupType());
        } else {
                details = new OrcidProfileUserDetails(orcidProfile.getOrcidIdentifier().getPath(), orcidProfile.getOrcidBio().getContactDetails().getEmail()
                    .get(0).getValue(), orcidProfile.getOrcidInternal().getSecurityDetails().getEncryptedPassword().getContent());
        }
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, "5555-5555-5555-5558", getRole());
        return auth;
    }
    
    protected List<OrcidWebRole> getRole() {
        return Arrays.asList(OrcidWebRole.ROLE_ADMIN);
    }
    
    
    @Test   
    public void validateContentTest() {
        CustomEmailForm  customEmail = customEmailController.getEmptyCustomEmailForm("APP-5555555555555555");  
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
        customEmail = customEmailController.validateContent(customEmail);
        assertNotNull(customEmail);
        assertEquals(1, customEmail.getContent().getErrors().size());
        assertEquals(customEmailController.getMessage("custom_email.content.html"), customEmail.getContent().getErrors().get(0));                
    }
    
    @Test 
    public void validateSubjectTest() {
        CustomEmailForm  customEmail = customEmailController.getEmptyCustomEmailForm("APP-5555555555555555");
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
        } catch(IllegalArgumentException ie) {
            
        }
    }        
    
    @Test
    public void validateOnlyValidClientIdsCanAskForCustomEmailTest() {
        try {
            customEmailController.getEmptyCustomEmailForm(null);
            fail();
        } catch(IllegalArgumentException ie) {
            
        }
        
        try {
            customEmailController.getEmptyCustomEmailForm("4444-4444-4444-XXXX");
            fail();
        } catch(IllegalArgumentException ie) {
            
        }
    }
}
