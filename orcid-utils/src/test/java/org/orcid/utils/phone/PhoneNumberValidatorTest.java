package org.orcid.utils.phone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PhoneNumberValidatorTest {

    private PhoneNumberValidator validator = new PhoneNumberValidator();

    @Test
    public void validateNormalizesValidPhoneNumber() {
        PhoneNumberValidationResult result = validator.validate("+50688888888", "US");

        assertTrue(result.isValid());
        assertEquals("+50688888888", result.getE164Number());
    }

    @Test
    public void validateRejectsInvalidPhoneNumber() {
        PhoneNumberValidationResult result = validator.validate("not-a-number", "US");

        assertFalse(result.isValid());
        assertEquals(null, result.getE164Number());
    }
}
