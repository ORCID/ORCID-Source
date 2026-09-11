package org.orcid.core.utils.activities;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExternalIdentifierFundedByHelperTest {

    @Test
    public void testAllowedExtIdTypes() {
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("grant_number"));
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("proposal-id"));
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("uri"));
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("doi"));
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("rrid"));
    }

    @Test
    public void testAllowedExtIdTypesAreCaseInsensitive() {
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("RRID"));
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("Rrid"));
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("DOI"));
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("GRANT_NUMBER"));
    }

    @Test
    public void testNotAllowedExtIdTypes() {
        assertFalse(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("source-work-id"));
        assertFalse(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("handle"));
        assertFalse(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("other-id"));
        assertFalse(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy("isbn"));
        assertFalse(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy(""));
    }

    @Test
    public void testNullExtIdTypeIsAllowed() {
        // A null type is left to the other external identifier validations to reject
        assertTrue(ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy(null));
    }
}
