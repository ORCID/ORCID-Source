package org.orcid.core.manager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.PasswordGenerationManager;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class PasswordGenerationManagerImplTest {

    @Resource
    private PasswordGenerationManager passwordGenerationManager;

    @Test
    public void testCreateNewPassword() {
        char[] newPassword = passwordGenerationManager.createNewPassword();
        assertNotNull(newPassword);
        assertTrue(newPassword.length == 12);
        Pattern passwordPattern = Pattern.compile(OrcidPasswordConstants.ORCID_PASSWORD_REGEX);
        Matcher matcher = passwordPattern.matcher(new String(newPassword));
        assertTrue(matcher.matches());

    }

}
