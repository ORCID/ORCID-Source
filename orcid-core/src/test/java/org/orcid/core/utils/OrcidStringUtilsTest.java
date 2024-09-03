package org.orcid.core.utils;

import org.junit.Test;
import org.orcid.utils.OrcidStringUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * User: Declan Newman (declan) Date: 10/02/2012
 * </p>
 */
public class OrcidStringUtilsTest {

    @Test
    public void testIsValidOrcid() {
        assertTrue(OrcidStringUtils.isValidOrcid("4444-4444-4444-4446"));
    }

    @Test
    public void testStripHtml() {
        String _1 = "Test&apos;s";
        String _2 = "Test&quot;s";
        String _3 = "Test &gt; s";
        String _4 = "Test &lt; s";
        String _5 = "Test&amp;s";

        assertEquals("Test's", OrcidStringUtils.stripHtml(_1));
        assertEquals("Test\"s", OrcidStringUtils.stripHtml(_2));
        assertEquals("Test > s", OrcidStringUtils.stripHtml(_3));
        assertEquals(_4, OrcidStringUtils.stripHtml(_4));
        assertEquals("Test&s", OrcidStringUtils.stripHtml(_5));

        // Html should be removed
        String html_1 = "<a href=\"orcid.org\">This is a link</a>";
        String html_2 = "<a href=\"orcid.org\">This is a link's</a>";
        String html_3 = "<a href=\"orcid.org\">This is a link\"s</a>";
        String html_4 = "<a href=\"orcid.org\">This is a link&s</a>";
        String html_5 = "<a href=\"orcid.org\">This is a link > s</a>";
        String html_6 = "<a href=\"orcid.org\">This is a link < s</a>";

        assertEquals("This is a link", OrcidStringUtils.stripHtml(html_1));
        assertEquals("This is a link's", OrcidStringUtils.stripHtml(html_2));
        assertEquals("This is a link\"s", OrcidStringUtils.stripHtml(html_3));
        assertEquals("This is a link&s", OrcidStringUtils.stripHtml(html_4));
        assertEquals("This is a link > s", OrcidStringUtils.stripHtml(html_5));
        assertEquals("This is a link &lt; s", OrcidStringUtils.stripHtml(html_6));

        assertEquals("Name", OrcidStringUtils.stripHtml("<head><script/><script/><head><body>Name</body></html>"));
        assertEquals(">Name", OrcidStringUtils.stripHtml("<head><script/><script/><head><body>>Name</body></html>"));
        assertEquals("Name&lt;", OrcidStringUtils.stripHtml("<head><script/><script/><head><body>Name<</body></html>"));
        assertEquals(">Name&lt;", OrcidStringUtils.stripHtml("<head><script/><script/><head><body>>Name<</body></html>"));
    }

    @Test
    public void testHasHtml() {
        assertTrue(OrcidStringUtils.hasHtml("<a>hello</a>"));
        assertTrue(OrcidStringUtils.hasHtml("This is a test <span>"));
        assertTrue(OrcidStringUtils.hasHtml("<this is a test>"));
        assertTrue(OrcidStringUtils.hasHtml("This is <script>another</script> test"));
        assertTrue(OrcidStringUtils.hasHtml("This is a <div>test</div>"));
        assertTrue(OrcidStringUtils.hasHtml("<div>This is a test</div>"));

        assertFalse(OrcidStringUtils.hasHtml("This is a test"));
        assertFalse(OrcidStringUtils.hasHtml("This is a test's"));
        assertFalse(OrcidStringUtils.hasHtml("This < is a test >"));
        assertFalse(OrcidStringUtils.hasHtml("This \"is a test\""));
        assertFalse(OrcidStringUtils.hasHtml("This \" is a test \""));
        assertFalse(OrcidStringUtils.hasHtml("This&this are tests"));
        assertFalse(OrcidStringUtils.hasHtml("Users's test"));
    }

    @Test
    public void testFilterInvalidXMLCharacters() {
        String s1 = new String(new char[] { '\u0000', 'S', '\u0000', 't', '\u0000', 'r', '\u0000', 'i', '\u0000', 'n', '\u0000', 'g', '\u0000', '!' });
        String s2 = new String(new char[] { '\uffff', 'S', '\uffff', 't', '\uffff', 'r', '\uffff', 'i', '\uffff', 'n', '\uffff', 'g', '\uffff', '!' });
        String s3 = new String(new char[] { '\ufffe', 'S', '\ufffe', 't', '\ufffe', 'r', '\ufffe', 'i', '\ufffe', 'n', '\ufffe', 'g', '\ufffe', '!' });
        String s4 = new String(new char[] { '\u0000', 'S', '\uffff', 't', '\ufffe', 'r', '\u0000', 'i', '\uffff', 'n', '\ufffe', 'g', '\u0000', '!' });

        assertEquals(7, OrcidStringUtils.filterInvalidXMLCharacters(s1).length());
        assertEquals("String!", OrcidStringUtils.filterInvalidXMLCharacters(s1));
        assertEquals(7, OrcidStringUtils.filterInvalidXMLCharacters(s2).length());
        assertEquals("String!", OrcidStringUtils.filterInvalidXMLCharacters(s2));
        assertEquals(7, OrcidStringUtils.filterInvalidXMLCharacters(s3).length());
        assertEquals("String!", OrcidStringUtils.filterInvalidXMLCharacters(s3));
        assertEquals(7, OrcidStringUtils.filterInvalidXMLCharacters(s4).length());
        assertEquals("String!", OrcidStringUtils.filterInvalidXMLCharacters(s4));
    }

    @Test
    public void filterEmailAddressTest() {
        assertEquals("1@test1.com", OrcidStringUtils.filterEmailAddress("1@test1.com"));

        // All space characters from the list
        // https://www.fileformat.info/info/unicode/category/Zs/list.htm
        char[] chars = { ' ', '\n', '\t', '\u00a0', '\u0020', '\u1680', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008',
                '\u2009', '\u200a', '\u202f', '\u205f', '\u3000' };

        for (char c : chars) {
            assertEquals("1@test1.com", OrcidStringUtils.filterEmailAddress(c + "1" + c + "@test1" + c + ".com" + c));
            assertEquals("1@test1.com", OrcidStringUtils.filterEmailAddress(c + "1" + c + "@test1.com" + c));
            assertEquals("1@test1.com", OrcidStringUtils.filterEmailAddress(c + "1" + c + "@test1.com"));
            assertEquals("1@test1.com", OrcidStringUtils.filterEmailAddress(c + "1@test1.com"));
        }

    }

    @Test
    public void testIsValidURL() {
        assertTrue(OrcidStringUtils.isValidURL("https://www.example.com"));
        assertTrue(OrcidStringUtils.isValidURL("http://www.example.com"));
        assertFalse(OrcidStringUtils.isValidURL("www.example.com"));
        assertFalse(OrcidStringUtils.isValidURL("example.com"));
        assertTrue(OrcidStringUtils.isValidURL("http://blog.example.com"));
        assertTrue(OrcidStringUtils.isValidURL("http://www.example.com/product"));
        assertTrue(OrcidStringUtils.isValidURL("http://www.example.com/products?id=1&page=2"));
        assertTrue(OrcidStringUtils.isValidURL("http://www.example.com#up"));
        assertTrue(OrcidStringUtils.isValidURL("http://255.255.255.255"));
        assertFalse(OrcidStringUtils.isValidURL("255.255.255.255"));
        assertTrue(OrcidStringUtils.isValidURL("http://www.site.com:8008"));
        assertTrue(OrcidStringUtils.isValidURL("http://www.site.com:8008?test/ok/test%20test"));
        assertTrue(OrcidStringUtils.isValidURL("https://doi.org/10.1175/1520-0426(2000)017<0854:aeosrb>2.0.co;%20a"));
        assertFalse(OrcidStringUtils.isValidURL("https://notvalid"));
        assertTrue(OrcidStringUtils.isValidURL("HTTPS://valid.com"));
        assertTrue(OrcidStringUtils.isValidURL("HTTPS://VALID.COM"));
    }
}
