package org.orcid.utils;

import org.junit.Test;

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
        
        //Html should be removed
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
        String s1 = new String(new char[] {'\u0000','S','\u0000','t','\u0000','r','\u0000','i','\u0000','n','\u0000','g','\u0000','!'});
        String s2 = new String(new char[] {'\uffff','S','\uffff','t','\uffff','r','\uffff','i','\uffff','n','\uffff','g','\uffff','!'});
        String s3 = new String(new char[] {'\ufffe','S','\ufffe','t','\ufffe','r','\ufffe','i','\ufffe','n','\ufffe','g','\ufffe','!'});
        String s4 = new String(new char[] {'\u0000','S','\uffff','t','\ufffe','r','\u0000','i','\uffff','n','\ufffe','g','\u0000','!'});
    
        assertEquals(7, OrcidStringUtils.filterInvalidXMLCharacters(s1).length());
        assertEquals("String!", OrcidStringUtils.filterInvalidXMLCharacters(s1));
        assertEquals(7, OrcidStringUtils.filterInvalidXMLCharacters(s2).length());
        assertEquals("String!", OrcidStringUtils.filterInvalidXMLCharacters(s2));
        assertEquals(7, OrcidStringUtils.filterInvalidXMLCharacters(s3).length());
        assertEquals("String!", OrcidStringUtils.filterInvalidXMLCharacters(s3));
        assertEquals(7, OrcidStringUtils.filterInvalidXMLCharacters(s4).length());
        assertEquals("String!", OrcidStringUtils.filterInvalidXMLCharacters(s4));    
    }
    
}
