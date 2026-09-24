package org.orcid.core.manager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.orcid.core.manager.PasswordGenerationManager;
import org.orcid.password.constants.OrcidPasswordConstants;

/**
 * PasswordGenerationManagerImpl has no collaborators at all: it takes a password length in its
 * constructor and calls RandomStringUtils. Booting the orcid-core Spring context bought this
 * test nothing but the constructor argument, so the constructor argument is supplied here
 * instead.
 *
 * <p>
 * 12 is the value the "passwordGenerationManager" bean is declared with in
 * orcid-core-context.xml, and it is the same 12 the length assertion below expects; if the bean
 * definition changes, this test asserts the old contract until it is changed too.
 */
public class PasswordGenerationManagerImplTest {

    private static final int PASSWORD_LENGTH = 12;

    private final PasswordGenerationManager passwordGenerationManager = new PasswordGenerationManagerImpl(PASSWORD_LENGTH);

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
