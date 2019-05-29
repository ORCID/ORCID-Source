package org.orcid.core.manager.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IssnValidatorTest {
    
    private IssnValidator issnValidator = new IssnValidator();
    
    @Test
    public void testValidateIssn() {
        assertTrue(issnValidator.issnValid("1234-5678"));
        assertTrue(issnValidator.issnValid("1234-567X"));
        assertFalse(issnValidator.issnValid("12345678"));
        assertFalse(issnValidator.issnValid(""));
        assertFalse(issnValidator.issnValid("something"));
        assertFalse(issnValidator.issnValid("X123-4567"));
        assertFalse(issnValidator.issnValid("1234-567A"));
    }

}
