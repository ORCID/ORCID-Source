package org.orcid.core.groupIds.issn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IssnGroupIdPatternMatcherTest {
    
    @Test
    public void testIsIssnGroupType() {
        assertTrue(IssnGroupIdPatternMatcher.isIssnGroupType("issn:1234-5678"));
        assertTrue(IssnGroupIdPatternMatcher.isIssnGroupType("issn:1234-567X"));
        assertTrue(IssnGroupIdPatternMatcher.isIssnGroupType("issn:this-still-claims-to-be-issn-group-type-so-therefore-is-despite-obviously-not-being-an-issn"));
        assertFalse(IssnGroupIdPatternMatcher.isIssnGroupType("1234-567X"));
        assertFalse(IssnGroupIdPatternMatcher.isIssnGroupType("erm"));
        assertFalse(IssnGroupIdPatternMatcher.isIssnGroupType("issn1234-567X"));
    }
    
    @Test
    public void testGetIssnFromIssnGroupId() {
        assertEquals("1234-5678", IssnGroupIdPatternMatcher.getIssnFromIssnGroupId("issn:1234-5678"));
        assertEquals("1234-567X", IssnGroupIdPatternMatcher.getIssnFromIssnGroupId("issn:1234-567X"));
        assertEquals("this-still-claims-to-be-issn-group-type-so-therefore-is-despite-obviously-not-being-an-issn", IssnGroupIdPatternMatcher.getIssnFromIssnGroupId("issn:this-still-claims-to-be-issn-group-type-so-therefore-is-despite-obviously-not-being-an-issn"));
    }
    
    @Test(expected = NotAnIssnGroupIdException.class)
    public void testGetIssnFromInvalidIssnGroupId() {
        assertEquals("1234-5678", IssnGroupIdPatternMatcher.getIssnFromIssnGroupId("1234-5678"));
    }

}
