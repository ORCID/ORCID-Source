package org.orcid.jaxb.model.message;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

public class ScopePathTypeTest {

    @Test
    public void testCombinedReadLimited() {
        //Test ORCID_PROFILE_READ_LIMITED
        Set<ScopePathType> combined = ScopePathType.ORCID_PROFILE_READ_LIMITED.combined();        
        assertEquals(10, combined.size());
        assertTrue(combined.contains(ScopePathType.ORCID_PROFILE_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        assertTrue(combined.contains(ScopePathType.READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PERSON_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));        
        
        assertFalse(combined.contains(ScopePathType.ORCID_PROFILE_CREATE));
        assertFalse(combined.contains(ScopePathType.ACTIVITIES_UPDATE));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
        assertFalse(combined.contains(ScopePathType.AUTHENTICATE));
        assertFalse(combined.contains(ScopePathType.BASIC_NOTIFICATION));
        assertFalse(combined.contains(ScopePathType.FUNDING_CREATE));
        assertFalse(combined.contains(ScopePathType.FUNDING_UPDATE));
        assertFalse(combined.contains(ScopePathType.GROUP_ID_RECORD_READ));
        assertFalse(combined.contains(ScopePathType.GROUP_ID_RECORD_UPDATE));
        assertFalse(combined.contains(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_UPDATE));
        assertFalse(combined.contains(ScopePathType.ORCID_PATENTS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_PATENTS_UPDATE));
        assertFalse(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
        assertFalse(combined.contains(ScopePathType.PERSON_UPDATE));
        assertFalse(combined.contains(ScopePathType.PREMIUM_NOTIFICATION));
        assertFalse(combined.contains(ScopePathType.WEBHOOK));
        
        //Test READ_LIMITED
        combined = ScopePathType.READ_LIMITED.combined();
        assertEquals(9, combined.size());
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        assertTrue(combined.contains(ScopePathType.READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PERSON_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));        
        
        assertFalse(combined.contains(ScopePathType.ORCID_PROFILE_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.ORCID_PROFILE_CREATE));
        assertFalse(combined.contains(ScopePathType.ACTIVITIES_UPDATE));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
        assertFalse(combined.contains(ScopePathType.AUTHENTICATE));
        assertFalse(combined.contains(ScopePathType.BASIC_NOTIFICATION));
        assertFalse(combined.contains(ScopePathType.FUNDING_CREATE));
        assertFalse(combined.contains(ScopePathType.FUNDING_UPDATE));
        assertFalse(combined.contains(ScopePathType.GROUP_ID_RECORD_READ));
        assertFalse(combined.contains(ScopePathType.GROUP_ID_RECORD_UPDATE));
        assertFalse(combined.contains(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_UPDATE));
        assertFalse(combined.contains(ScopePathType.ORCID_PATENTS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_PATENTS_UPDATE));
        assertFalse(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
        assertFalse(combined.contains(ScopePathType.PERSON_UPDATE));
        assertFalse(combined.contains(ScopePathType.PREMIUM_NOTIFICATION));
        assertFalse(combined.contains(ScopePathType.WEBHOOK));
        
        //Test ACTIVITIES_READ_LIMITED
        combined = ScopePathType.ACTIVITIES_READ_LIMITED.combined();
        assertEquals(6, combined.size());
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));        
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));        
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
                       
        assertFalse(combined.contains(ScopePathType.PERSON_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED)); 
        assertFalse(combined.contains(ScopePathType.READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.ORCID_PROFILE_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.ORCID_PROFILE_CREATE));
        assertFalse(combined.contains(ScopePathType.ACTIVITIES_UPDATE));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
        assertFalse(combined.contains(ScopePathType.AUTHENTICATE));
        assertFalse(combined.contains(ScopePathType.BASIC_NOTIFICATION));
        assertFalse(combined.contains(ScopePathType.FUNDING_CREATE));
        assertFalse(combined.contains(ScopePathType.FUNDING_UPDATE));
        assertFalse(combined.contains(ScopePathType.GROUP_ID_RECORD_READ));
        assertFalse(combined.contains(ScopePathType.GROUP_ID_RECORD_UPDATE));
        assertFalse(combined.contains(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_UPDATE));
        assertFalse(combined.contains(ScopePathType.ORCID_PATENTS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_PATENTS_UPDATE));
        assertFalse(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
        assertFalse(combined.contains(ScopePathType.PERSON_UPDATE));
        assertFalse(combined.contains(ScopePathType.PREMIUM_NOTIFICATION));
        assertFalse(combined.contains(ScopePathType.WEBHOOK));
        
        //Test
        combined = ScopePathType.ORCID_WORKS_READ_LIMITED.combined();
        assertEquals(2, combined.size());
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));        
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
                
        assertFalse(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.PERSON_READ_LIMITED));        
        assertFalse(combined.contains(ScopePathType.READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.ORCID_PROFILE_READ_LIMITED));
        assertFalse(combined.contains(ScopePathType.ORCID_PROFILE_CREATE));
        assertFalse(combined.contains(ScopePathType.ACTIVITIES_UPDATE));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
        assertFalse(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
        assertFalse(combined.contains(ScopePathType.AUTHENTICATE));
        assertFalse(combined.contains(ScopePathType.BASIC_NOTIFICATION));
        assertFalse(combined.contains(ScopePathType.FUNDING_CREATE));
        assertFalse(combined.contains(ScopePathType.FUNDING_UPDATE));
        assertFalse(combined.contains(ScopePathType.GROUP_ID_RECORD_READ));
        assertFalse(combined.contains(ScopePathType.GROUP_ID_RECORD_UPDATE));
        assertFalse(combined.contains(ScopePathType.INTERNAL_PERSON_LAST_MODIFIED));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_BIO_UPDATE));
        assertFalse(combined.contains(ScopePathType.ORCID_PATENTS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_PATENTS_UPDATE));
        assertFalse(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
        assertFalse(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
        assertFalse(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
        assertFalse(combined.contains(ScopePathType.PERSON_UPDATE));
        assertFalse(combined.contains(ScopePathType.PREMIUM_NOTIFICATION));
        assertFalse(combined.contains(ScopePathType.WEBHOOK));
    }
    
    @Test
    public void testCombinedUpdate() {
        
    }
}
