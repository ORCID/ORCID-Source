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
package org.orcid.integration.blackbox.web.account;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.blackbox.api.BlackBoxBase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class AccountSettingsTest extends BlackBoxBase {
    @Before
    public void before() {
        signin();
    }

    @After
    public void after() {
        signout();
    }

    @Test
    public void emailsTest() {
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        //Add an email
        String emailValue = "added.email." + System.currentTimeMillis() + "@test.com";
        addEmail(emailValue, org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        //Reload the account settings to confirm it was actually added
        showAccountSettingsPage();
        try {
          assertTrue(emailExists(emailValue));  
        } catch(Exception e) {
            fail("Unable to find email " + emailValue);
        }
        
        //Remove it
        showAccountSettingsPage();
        openEditEmailsSectionOnAccountSettingsPage();
        removeEmail(emailValue);
        //Reload the account settings to confirm it was actually removed
        showAccountSettingsPage();
        // Look if it is still present
        assertFalse("Email " + emailValue + " should not be there", emailExists(emailValue));
    }

    @Test
    public void emailsTestAsDelegate() {
        showAccountSettingsPage();
        
        //Verify user can add emails
        openEditEmailsSectionOnAccountSettingsPage();
        assertTrue(allowedToAddEmails());
        
        boolean haveDelegate = false;
        try {
            haveDelegate(getUser2OrcidId());
            haveDelegate = true;
        } catch(Exception e) {
            
        }
        
        if(haveDelegate) {
            removeDelegate(getUser2OrcidId());
        }
        
        addDelegate(getUser2OrcidId());
                
        // Now sign in as user 2
        signout();
        signin(getUser2UserName(), getUser2Password());
        
        switchUser(getUser1OrcidId());
        
        showAccountSettingsPage();
        // Check that add email section is not there
        openEditEmailsSectionOnAccountSettingsPage();
        assertFalse(allowedToAddEmails());        
    }

}
