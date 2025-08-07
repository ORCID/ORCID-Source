package org.orcid.utils;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OrcidStringUtilsTest {

    @Test
    public void testContainsDomain_PositiveCases() {
        // Simple case
        assertTrue("Should find a simple domain.", OrcidStringUtils.containsDomain("visit example.com for more info"));
        // Domain at the start of the string
        assertTrue("Should find domain at the start.", OrcidStringUtils.containsDomain("google.co.uk is a search engine"));
        // Domain with a hyphen
        assertTrue("Should find domain with a hyphen.", OrcidStringUtils.containsDomain("my-site.org"));
        // Part of an email address
        assertTrue("Should find domain in an email.", OrcidStringUtils.containsDomain("contact support@example.com for help"));
        // Multiple domains in string
        assertTrue("Should find the first domain in a string with multiple.", OrcidStringUtils.containsDomain("Check out google.com or yahoo.net"));
    }

    @Test
    public void testContainsDomain_NegativeCases() {
        // No domain present
        assertFalse("Should not find a domain in a simple sentence.", OrcidStringUtils.containsDomain("This is a test string without any domains."));
        // Invalid TLD (Top-Level Domain) - too short
        assertFalse("Should not treat '.c' as a valid TLD.", OrcidStringUtils.containsDomain("This is just a file.c"));
        // IP Address
        assertFalse("Should not identify an IP address as a domain.", OrcidStringUtils.containsDomain("An IP 192.168.1.1 is not a domain."));
        // String with only a dot
        assertFalse("Should not match a single dot.", OrcidStringUtils.containsDomain("test . test"));
    }

    @Test
    public void testContainsDomain_EdgeCases() {
        // blank/null strings should return false.
        assertFalse("Should return false for a null string.", OrcidStringUtils.containsDomain(null));
        assertFalse("Should return false for an empty string.", OrcidStringUtils.containsDomain(""));
        assertFalse("Should return false for a blank string.", OrcidStringUtils.containsDomain("   "));
    }
}
