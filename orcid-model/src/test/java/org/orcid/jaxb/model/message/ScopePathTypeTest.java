package org.orcid.jaxb.model.message;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

public class ScopePathTypeTest {

	@Test
	public void test_ORCID_PROFILE_READ_LIMITED() {
		// Test ORCID_PROFILE_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.ORCID_PROFILE_READ_LIMITED.combined();
		assertEquals(11, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_PROFILE_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.PERSON_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_READ_LIMITED() {
		// Test READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.READ_LIMITED.combined();
		assertEquals(10, combined.size());
		assertTrue(combined.contains(ScopePathType.READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.PERSON_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ORCID_BIO_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_ACTIVITIES_READ_LIMITED() {
		// Test ACTIVITIES_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.ACTIVITIES_READ_LIMITED.combined();
		assertEquals(7, combined.size());
		assertTrue(combined.contains(ScopePathType.ACTIVITIES_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_ORCID_WORKS_READ_LIMITED() {
		// Test ORCID_WORKS_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.ORCID_WORKS_READ_LIMITED.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_WORKS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_FUNDING_READ_LIMITED() {
		// Test FUNDING_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.FUNDING_READ_LIMITED.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.FUNDING_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_PEER_REVIEW_READ_LIMITED() {
		// Test PEER_REVIEW_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.PEER_REVIEW_READ_LIMITED.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_AFFILIATIONS_READ_LIMITED() {
		// Test AFFILIATIONS_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.AFFILIATIONS_READ_LIMITED.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.AFFILIATIONS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_ORCID_PATENTS_READ_LIMITED() {
		// Test ORCID_PATENTS_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.ORCID_PATENTS_READ_LIMITED.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_PERSON_UPDATE() {
		// Test ORCID_PROFILE_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.PERSON_UPDATE.combined();
		assertEquals(4, combined.size());
		assertTrue(combined.contains(ScopePathType.PERSON_UPDATE));
		assertTrue(combined.contains(ScopePathType.ORCID_BIO_UPDATE));
		assertTrue(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_ORCID_WORKS_UPDATE() {
		// Test ORCID_WORKS_UPDATE
		Set<ScopePathType> combined = ScopePathType.ORCID_WORKS_UPDATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_FUNDING_UPDATE() {
		// Test FUNDING_UPDATE
		Set<ScopePathType> combined = ScopePathType.FUNDING_UPDATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.FUNDING_UPDATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_PEER_REVIEW_UPDATE() {
		// Test PEER_REVIEW_UPDATE
		Set<ScopePathType> combined = ScopePathType.PEER_REVIEW_UPDATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_AFFILIATIONS_UPDATE() {
		// Test AFFILIATIONS_UPDATE
		Set<ScopePathType> combined = ScopePathType.AFFILIATIONS_UPDATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}
	
	@Test
	public void test_ORCID_PATENTS_UPDATE() {
		// Test ORCID_PATENTS_UPDATE
		Set<ScopePathType> combined = ScopePathType.ORCID_PATENTS_UPDATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_UPDATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_ORCID_PROFILE_CREATE() {
		// Test ORCID_PROFILE_READ_LIMITED
		Set<ScopePathType> combined = ScopePathType.ORCID_PROFILE_CREATE.combined();
		assertEquals(30, combined.size());
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
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_READ_LIMITED));
		assertTrue(combined.contains(ScopePathType.GROUP_ID_RECORD_READ));
		assertTrue(combined.contains(ScopePathType.GROUP_ID_RECORD_UPDATE));
		assertTrue(combined.contains(ScopePathType.OPENID));
	}

	@Test
	public void test_ACTIVITIES_UPDATE() {
		// Test ACTIVITIES_UPDATE
		Set<ScopePathType> combined = ScopePathType.ACTIVITIES_UPDATE.combined();
		assertEquals(12, combined.size());
		assertTrue(combined.contains(ScopePathType.ACTIVITIES_UPDATE));
		assertTrue(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
		assertTrue(combined.contains(ScopePathType.AFFILIATIONS_UPDATE));
		assertTrue(combined.contains(ScopePathType.FUNDING_CREATE));
		assertTrue(combined.contains(ScopePathType.FUNDING_UPDATE));
		assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_CREATE));
		assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_UPDATE));
		assertTrue(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
		assertTrue(combined.contains(ScopePathType.ORCID_WORKS_UPDATE));
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_UPDATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_ORCID_WORKS_CREATE() {
		// Test ORCID_WORKS_CREATE
		Set<ScopePathType> combined = ScopePathType.ORCID_WORKS_CREATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_WORKS_CREATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_FUNDING_CREATE() {
		// Test FUNDING_CREATE
		Set<ScopePathType> combined = ScopePathType.FUNDING_CREATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.FUNDING_CREATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_PEER_REVIEW_CREATE() {
		// Test PEER_REVIEW_CREATE
		Set<ScopePathType> combined = ScopePathType.PEER_REVIEW_CREATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.PEER_REVIEW_CREATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_AFFILIATIONS_CREATEE() {
		// Test AFFILIATIONS_CREATE
		Set<ScopePathType> combined = ScopePathType.AFFILIATIONS_CREATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.AFFILIATIONS_CREATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}
	
	@Test
	public void test_ORCID_PATENTS_CREATE() {
		// Test ORCID_PATENTS_CREATE
		Set<ScopePathType> combined = ScopePathType.ORCID_PATENTS_CREATE.combined();
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_PATENTS_CREATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_ORCID_BIO_UPDATE() {
		// Test ORCID_BIO_UPDATE
		Set<ScopePathType> combined = ScopePathType.ORCID_BIO_UPDATE.combined;
		assertEquals(3, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_BIO_UPDATE));
		assertTrue(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}

	@Test
	public void test_ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE() {
		// Test ORCID_BIO_UPDATE
		Set<ScopePathType> combined = ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE.combined;
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}
	
	@Test
	public void test_READ_PUBLIC() {
		// Test READ_PUBLIC
		Set<ScopePathType> combined = ScopePathType.READ_PUBLIC.combined;
		assertEquals(1, combined.size());		
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}
	
	@Test
	public void test_AUTHENTICATE() {
		// Test AUTHENTICATE
		Set<ScopePathType> combined = ScopePathType.AUTHENTICATE.combined;
		assertEquals(2, combined.size());
		assertTrue(combined.contains(ScopePathType.AUTHENTICATE));
		assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
	}
	
	@Test
        public void test_OPENID() {
                // Test AUTHENTICATE
                Set<ScopePathType> combined = ScopePathType.OPENID.combined;
                assertEquals(3, combined.size());
                assertTrue(combined.contains(ScopePathType.OPENID));
                assertTrue(combined.contains(ScopePathType.AUTHENTICATE));
                assertTrue(combined.contains(ScopePathType.READ_PUBLIC));
        }
}