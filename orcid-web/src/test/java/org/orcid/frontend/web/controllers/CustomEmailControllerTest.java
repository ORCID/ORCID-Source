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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.pojo.ajaxForm.CustomEmailForm;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
public class CustomEmailControllerTest {
    
    @Resource    
    CustomEmailController customEmailController;

    @Before
    public void init() {
        assertNotNull(customEmailController);
    }

    
    @Test   
    public void testValidateContent() {
        CustomEmailForm  customEmail = customEmailController.getEmptyCustomEmailForm(null);  
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
    }
}
