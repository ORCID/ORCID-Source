package org.orcid.utils;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OrcidStringUtilsTest {

 // --- Tests for containsDomain ---
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
    

    // --- Tests for containsIPv4Address ---

    @Test
    public void testContainsIPv4Address_PositiveCases() {
        assertTrue("Should find a standard private IP.", OrcidStringUtils.containsIPv4Address("Server is at 192.168.1.1 today."));
        assertTrue("Should find a public IP.", OrcidStringUtils.containsIPv4Address("Use DNS 8.8.8.8 for speed."));
        assertTrue("Should find the loopback address.", OrcidStringUtils.containsIPv4Address("Connect to 127.0.0.1 for local testing."));
        assertTrue("Should find IP with 0.", OrcidStringUtils.containsIPv4Address("0.0.0.0 is a non-routable address."));
        assertTrue("Should find IP with 255.", OrcidStringUtils.containsIPv4Address("Broadcast is 255.255.255.255"));
        assertTrue("Should match a string that is only an IP.", OrcidStringUtils.containsIPv4Address("10.0.0.1"));
    }

    @Test
    public void testContainsIPv4Address_NegativeCases() {
        assertFalse("Should not match an IP with a number > 255.", OrcidStringUtils.containsIPv4Address("Invalid IP: 256.10.20.30"));
        assertFalse("Should not match an incomplete IP.", OrcidStringUtils.containsIPv4Address("This is not an IP: 192.168.1"));
        assertFalse("Should not match an IP with letters.", OrcidStringUtils.containsIPv4Address("No letters allowed 192.168.1.a"));
        assertFalse("Should not find an IP in a regular sentence.", OrcidStringUtils.containsIPv4Address("This is just a normal sentence."));
        assertFalse("Should not match numbers that are part of a larger number.", OrcidStringUtils.containsIPv4Address("The number is 19216811."));
    }

    @Test
    public void testContainsIPv4Address_EdgeCases() {
        assertFalse("Should return false for a null string.", OrcidStringUtils.containsIPv4Address(null));
        assertFalse("Should return false for an empty string.", OrcidStringUtils.containsIPv4Address(""));
        assertFalse("Should return false for a blank string.", OrcidStringUtils.containsIPv4Address("   \t   "));
    }

    // --- Tests for containsIPv6Address ---

    @Test
    public void testContainsIPv6Address_PositiveCases() {
        assertTrue("Should find a full, uncompressed IPv6 address.", OrcidStringUtils.containsIPv6Address("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
        assertTrue("Should find a compressed IPv6 address.", OrcidStringUtils.containsIPv6Address("The address is 2001:db8::8a2e:370:7334"));
        assertTrue("Should find the unspecified IPv6 address.", OrcidStringUtils.containsIPv6Address("The address is ::"));
        assertTrue("Should find an IPv4-mapped IPv6 address.", OrcidStringUtils.containsIPv6Address("::ffff:192.0.2.128"));
        assertTrue("Should find a link-local address with zone index.", OrcidStringUtils.containsIPv6Address("fe80::1%eth0"));
        assertTrue("Should find a compressed address with leading zeros omitted.", OrcidStringUtils.containsIPv6Address("2001:db8:a0b:12f0::1"));
    }

    @Test
    public void testContainsIPv6Address_NegativeCases() {
        assertFalse("Should not match an IPv6 with more than 7 colons.", OrcidStringUtils.containsIPv6Address("2001:0db8:1:2:3:4:5:6:7"));
        assertFalse("Should not match an IPv6 with two double-colons.", OrcidStringUtils.containsIPv6Address("2001::db8::1"));
        assertFalse("Should not match an invalid IPv4-mapped address.", OrcidStringUtils.containsIPv6Address("::ffff:256.0.2.128"));
        assertFalse("Should not find an IPv6 in a regular sentence.", OrcidStringUtils.containsIPv6Address("This sentence has no IP address."));
    }

    @Test
    public void testContainsIPv6Address_EdgeCases() {
        assertFalse("Should return false for a null string.", OrcidStringUtils.containsIPv6Address(null));
        assertFalse("Should return false for an empty string.", OrcidStringUtils.containsIPv6Address(""));
        assertFalse("Should return false for a blank string.", OrcidStringUtils.containsIPv6Address("    "));
    }
    
    // --- Tests for isValidEmailFriendlyName ---
    @Test
    public void testIsValidEmailFriendlyName_PositiveCases() {
        assertTrue("A simple name should be valid.", OrcidStringUtils.isValidEmailFriendlyName("John Smith"));
        assertTrue("A name with numbers should be valid.", OrcidStringUtils.isValidEmailFriendlyName("Support Team 123"));
        assertTrue("The official ORCID name should be valid.", OrcidStringUtils.isValidEmailFriendlyName("ORCID Support"));
        assertTrue("A name with special characters should be valid.", OrcidStringUtils.isValidEmailFriendlyName("Renée's Team (Support)"));
    }

    @Test
    public void testIsValidEmailFriendlyName_NegativeCases_ContainsDomain() {
        assertFalse("Should be invalid if it contains a domain.", OrcidStringUtils.isValidEmailFriendlyName("Visit example.com"));
        assertFalse("Should be invalid if the name is just a domain.", OrcidStringUtils.isValidEmailFriendlyName("support.orcid.org"));
        assertFalse("Should be invalid even if domain is part of a word.", OrcidStringUtils.isValidEmailFriendlyName("Go to my-site.net for info"));
    }

    @Test
    public void testIsValidEmailFriendlyName_NegativeCases_ContainsIPv4() {
        assertFalse("Should be invalid if it contains an IPv4 address.", OrcidStringUtils.isValidEmailFriendlyName("Server 192.168.1.1"));
        assertFalse("Should be invalid if the name is just an IPv4 address.", OrcidStringUtils.isValidEmailFriendlyName("8.8.8.8"));
    }

    @Test
    public void testIsValidEmailFriendlyName_NegativeCases_ContainsIPv6() {
        assertFalse("Should be invalid if it contains an IPv6 address.", OrcidStringUtils.isValidEmailFriendlyName("Connect to 2001:db8::8a2e:370:7334"));
        assertFalse("Should be invalid if the name is just an IPv6 address.", OrcidStringUtils.isValidEmailFriendlyName("2001:db8::8a2e:370:7334"));
        assertFalse("Should be invalid if it contains a mapped IPv6.", OrcidStringUtils.isValidEmailFriendlyName("Address ::ffff:192.0.2.128"));
    }

    @Test
    public void testIsValidEmailFriendlyName_EdgeCases() {
        assertTrue("A null name should be considered valid.", OrcidStringUtils.isValidEmailFriendlyName(null));
        assertTrue("An empty name should be considered valid.", OrcidStringUtils.isValidEmailFriendlyName(""));
        assertTrue("A blank name should be considered valid.", OrcidStringUtils.isValidEmailFriendlyName("   \t "));
    }
}
