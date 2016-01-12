/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.jaxb.model.message;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

public class ScopePathTypeTest {

    @Test
    public void testCombinedReadLimited() {
        // Test ORCID_PROFILE_READ_LIMITED
        Set<ScopePathType> combined = ScopePathType.ORCID_PROFILE_READ_LIMITED.combined();
        assertEquals(10, combined.size());
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_PROFILE_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PERSON_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));

        // Test READ_LIMITED
        combined = ScopePathType.READ_LIMITED.combined();
        assertEquals(9, combined.size());
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PERSON_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));

        // Test ACTIVITIES_READ_LIMITED
        combined = ScopePathType.ACTIVITIES_READ_LIMITED.combined();
        assertEquals(6, combined.size());
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));

        // Test ORCID_WORKS_READ_LIMITED
        combined = ScopePathType.ORCID_WORKS_READ_LIMITED.combined();
        assertEquals(2, combined.size());
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));    
        
        // Test FUNDING_READ_LIMITED
        combined = ScopePathType.FUNDING_READ_LIMITED.combined();        
        assertEquals(2, combined.size());
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test PEER_REVIEW_READ_LIMITED
        combined = ScopePathType.PEER_REVIEW_READ_LIMITED.combined();        
        assertEquals(2, combined.size());
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test AFFILIATIONS_READ_LIMITED
        combined = ScopePathType.AFFILIATIONS_READ_LIMITED.combined();        
        assertEquals(2, combined.size());
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
    }

    @Test
    public void testCombinedUpdate() {
        // Test ORCID_PROFILE_READ_LIMITED
        Set<ScopePathType> combined = ScopePathType.PERSON_UPDATE.combined();        
        assertEquals(5, combined.size());
        assertTrue(combined.contains(ScopePathType.PERSON_UPDATE));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_UPDATE));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test ORCID_WORKS_UPDATE
        combined = ScopePathType.ORCID_WORKS_UPDATE.combined();        
        assertEquals(3, combined.size());
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test FUNDING_UPDATE
        combined = ScopePathType.FUNDING_UPDATE.combined();        
        assertEquals(3, combined.size());
        assertTrue(combined.contains(ScopePathType.FUNDING_UPDATE));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test PEER_REVIEW_UPDATE
        combined = ScopePathType.PEER_REVIEW_UPDATE.combined();        
        assertEquals(3, combined.size());
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test AFFILIATIONS_UPDATE
        combined = ScopePathType.AFFILIATIONS_UPDATE.combined();        
        assertEquals(3, combined.size());
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
    }

    @Test
    public void testCombinedCreate() {
        // Test ORCID_PROFILE_READ_LIMITED
        Set<ScopePathType> combined = ScopePathType.ORCID_PROFILE_CREATE.combined();
        assertEquals(27, combined.size());
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_UPDATE));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
        assertTrue(combined.contains(ScopePathType.AUTHENTICATE));        
        assertTrue(combined.contains(ScopePathType.FUNDING_CREATE));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.FUNDING_UPDATE));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_BIO_UPDATE));
        assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_CREATE));
        assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_UPDATE));
        assertTrue(combined.contains(ScopePathType.ORCID_PROFILE_CREATE));
        assertTrue(combined.contains(ScopePathType.ORCID_PROFILE_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
        assertTrue(combined.contains(ScopePathType.PERSON_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PERSON_UPDATE));
        assertTrue(combined.contains(ScopePathType.READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test ACTIVITIES_UPDATE
        combined = ScopePathType.ACTIVITIES_UPDATE.combined();
        assertEquals(17, combined.size());
        assertTrue(combined.contains(ScopePathType.ACTIVITIES_UPDATE));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
        assertTrue(combined.contains(ScopePathType.FUNDING_CREATE));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.FUNDING_UPDATE));
        assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_CREATE));
        assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_UPDATE));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));        
        
        // Test ORCID_WORKS_CREATE
        combined = ScopePathType.ORCID_WORKS_CREATE.combined();
        assertEquals(3, combined.size());
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
        assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test FUNDING_CREATE
        combined = ScopePathType.FUNDING_CREATE.combined();        
        assertEquals(3, combined.size());
        assertTrue(combined.contains(ScopePathType.FUNDING_CREATE));
        assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test PEER_REVIEW_CREATE
        combined = ScopePathType.PEER_REVIEW_CREATE.combined();        
        assertEquals(3, combined.size());
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
        assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        
        // Test AFFILIATIONS_CREATE
        combined = ScopePathType.AFFILIATIONS_CREATE.combined();        
        assertEquals(3, combined.size());
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
        assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertTrue(combined.contains(ScopePathType.READ_PUBLIC));

    }
}
