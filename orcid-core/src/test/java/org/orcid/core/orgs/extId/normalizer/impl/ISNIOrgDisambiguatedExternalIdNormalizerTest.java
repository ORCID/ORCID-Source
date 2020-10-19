package org.orcid.core.orgs.extId.normalizer.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class ISNIOrgDisambiguatedExternalIdNormalizerTest {

    private ISNIOrgDisambiguatedExternalIdNormalizer normalizer = new ISNIOrgDisambiguatedExternalIdNormalizer();
    
    @Test
    public void testGetType() {
        assertEquals("ISNI", normalizer.getType());
    }
    
    @Test
    public void testNormalize() {
        assertEquals("1234567890123456", normalizer.normalize("1234567890123456")); // legal
        assertEquals("123456789012345X", normalizer.normalize("123456789012345X")); // legal
        assertEquals("123456789012345X", normalizer.normalize("0123456789012345X")); // too long
        assertEquals("1234567889123456", normalizer.normalize("1234567889123456789012345")); // too long
        assertEquals("0123456789012345", normalizer.normalize("123456789012345")); // too short
        
        // too long, illegal character, and space \0000 0004 5898 943X
        assertEquals("000000045898943X", normalizer.normalize("\\0000 0004 5898 943X")); 
        
        // illegal characters, too short once stripped
        assertEquals("0123456789012345", normalizer.normalize("/123456789012345")); 
        assertEquals("0000000000000005", normalizer.normalize("erthisismadness5")); 
        
        // unable to resolve
        assertEquals("XXXX123456789012345", normalizer.normalize("XXXX123456789012345"));
        assertNull(normalizer.normalize(null));
    }

}
