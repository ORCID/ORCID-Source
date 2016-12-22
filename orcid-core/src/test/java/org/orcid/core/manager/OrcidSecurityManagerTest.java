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
package org.orcid.core.manager;

import static org.junit.Assert.fail;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc4.CreditName;
import org.orcid.jaxb.model.common_rc4.Source;
import org.orcid.jaxb.model.common_rc4.SourceClientId;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Will Simpson
 *
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class OrcidSecurityManagerTest {

	@Resource
	private OrcidSecurityManager orcidSecurityManager;

	private final String ORCID_1 = "0000-0000-0000-0001";
	private final String ORCID_2 = "0000-0000-0000-0002";

	private final String CLIENT_1 = "APP-0000000000000001";
	private final String CLIENT_2 = "APP-0000000000000002";

	@Before
	public void before() {

	}

	@After
	public void after() {
		SecurityContextTestUtils.setUpSecurityContextForAnonymous();
	}

	// checkScopes test's
	@Test
	public void testCheckScopes_ReadPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_Authenticate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AUTHENTICATE);
		orcidSecurityManager.checkScopes(ScopePathType.AUTHENTICATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);

		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_AffiliationsReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.AFFILIATIONS_READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_AffiliationsCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.AFFILIATIONS_CREATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_AffiliationsUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.AFFILIATIONS_UPDATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_FundingReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.FUNDING_READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_FundingCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.FUNDING_CREATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_FundingUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.FUNDING_UPDATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_PatentsReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PATENTS_READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_PatentsCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PATENTS_CREATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_PatentsUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PATENTS_UPDATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_PeerReviewReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.PEER_REVIEW_READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_PeerReviewCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.PEER_REVIEW_CREATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_PeerReviewUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.PEER_REVIEW_UPDATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_OrcidWorksReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_WORKS_READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_OrcidWorksCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_WORKS_CREATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_OrcidWorksUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_WORKS_UPDATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_ActivitiesReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ACTIVITIES_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.AFFILIATIONS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.FUNDING_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.PEER_REVIEW_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_WORKS_READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_ActivitiesUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ACTIVITIES_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_WORKS_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_WORKS_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.FUNDING_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.FUNDING_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.AFFILIATIONS_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.AFFILIATIONS_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PATENTS_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PATENTS_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.PEER_REVIEW_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.PEER_REVIEW_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.ACTIVITIES_UPDATE);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_OrcidProfileReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PROFILE_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ACTIVITIES_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.AFFILIATIONS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.FUNDING_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.PEER_REVIEW_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_WORKS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_BIO_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PROFILE_READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_ReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ACTIVITIES_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.AFFILIATIONS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.FUNDING_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.PEER_REVIEW_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_WORKS_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_BIO_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_OrcidBioUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_BIO_UPDATE);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);

		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PROFILE_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
	}

	@Test
	public void testCheckScopes_OrcidBioReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_BIO_READ_LIMITED);

		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PROFILE_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_OrcidBioExternalIdentifiersCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1,
				ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
		orcidSecurityManager.checkScopes(ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkScopes(ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);

		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.FUNDING_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.ORCID_PROFILE_READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.READ_LIMITED);
		assertItThrowAccessControlException(ScopePathType.AUTHENTICATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_WORKS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_UPDATE);
		assertItThrowAccessControlException(ScopePathType.FUNDING_CREATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.AFFILIATIONS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_CREATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_PATENTS_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_UPDATE);
		assertItThrowAccessControlException(ScopePathType.PEER_REVIEW_CREATE);
		assertItThrowAccessControlException(ScopePathType.ACTIVITIES_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_UPDATE);
		assertItThrowAccessControlException(ScopePathType.ORCID_BIO_READ_LIMITED);
	}	
	
	private void assertItFailForOtherScopes(ScopePathType ... goodOnes) {
		List<ScopePathType> list = Arrays.asList(goodOnes);
		for(ScopePathType s : ScopePathType.values()) {
			if(!list.contains(s)) {
				assertItThrowAccessControlException(s);
			} else {
				orcidSecurityManager.checkScopes(s);
			}
		}
	}

	// checkClientAccessAndScopes test's
	@Test(expected = OrcidUnauthorizedException.class)
	public void testCheckClientAccessAndScopes_When_TokenIsForOtherUser() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		orcidSecurityManager.checkClientAccessAndScopes(ORCID_2, ScopePathType.ORCID_BIO_UPDATE);
		fail();
	}
	
	@Test
	public void testCheckClientAccessAndScopes_CheckScopes() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		//Should work
		orcidSecurityManager.checkClientAccessAndScopes(ORCID_1, ScopePathType.READ_PUBLIC);
		orcidSecurityManager.checkClientAccessAndScopes(ORCID_1, ScopePathType.ORCID_BIO_UPDATE);
		
		//Should not work
		assertItThrowAccessControlException(ORCID_1, ScopePathType.ORCID_BIO_UPDATE);
		
	}
	
	// Name element tests
	@Test
	public void testName_CanRead_When_ReadPublicToken_IsPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Name name = createName(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test(expected = OrcidUnauthorizedException.class)
	public void testName_ThrowException_When_TokenIsForOtherUser() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Name name = createName(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_2, name, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test
	public void testName_CanRead_When_DontHaveReadScope_IsPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Name name = createName(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test(expected = AccessControlException.class)
	public void testName_CantRead_When_DontHaveReadScope_IsLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Name name = createName(Visibility.LIMITED);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test(expected = AccessControlException.class)
	public void testName_CantRead_When_DontHaveReadScope_IsPrivate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Name name = createName(Visibility.PRIVATE);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test
	public void testName_CanRead_When_HaveReadScope_IsPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Name name = createName(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test
	public void testName_CanRead_When_HaveReadScope_IsLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Name name = createName(Visibility.LIMITED);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test(expected = OrcidVisibilityException.class)
	public void testName_CantRead_When_HaveReadScope_IsPrivate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Name name = createName(Visibility.PRIVATE);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	// Biography element tests
	@Test
	public void testBio_CanRead_When_ReadPublicToken_IsPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Biography bio = createBiography(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test(expected = OrcidUnauthorizedException.class)
	public void testBio_ThrowException_When_TokenIsForOtherUser() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Biography bio = createBiography(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_2, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test
	public void testBio_CanRead_When_DontHaveReadScope_IsPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Biography bio = createBiography(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test(expected = AccessControlException.class)
	public void testBio_CantRead_When_DontHaveReadScope_IsLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Biography bio = createBiography(Visibility.LIMITED);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test(expected = AccessControlException.class)
	public void testBio_CantRead_When_DontHaveReadScope_IsPrivate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		Biography bio = createBiography(Visibility.PRIVATE);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test
	public void testBio_CanRead_When_HaveReadScope_IsPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Biography bio = createBiography(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test
	public void testBio_CanRead_When_HaveReadScope_IsLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Biography bio = createBiography(Visibility.LIMITED);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test(expected = OrcidVisibilityException.class)
	public void testBio_CantRead_When_HaveReadScope_IsPrivate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Biography bio = createBiography(Visibility.PRIVATE);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void assertItThrowAccessControlException(String orcid, ScopePathType s) {
		try {
			orcidSecurityManager.checkClientAccessAndScopes(orcid, s);
			fail();
		} catch (AccessControlException e) {
			return;
		} catch (Exception e) {
			fail();
		}
		fail();
	}
	
	private void assertItThrowAccessControlException(ScopePathType s) {
		try {
			orcidSecurityManager.checkScopes(s);
			fail();
		} catch (AccessControlException e) {
			return;
		} catch (Exception e) {
			fail();
		}
		fail();
	}
	
	private Biography createBiography(Visibility v) {
		return new Biography("Biography", v);
	}

	private OtherName createOtherName(Visibility v, String sourceId) {
		OtherName otherName = new OtherName();
		otherName.setContent("other-name");
		otherName.setVisibility(v);
		Source source = new Source();
		source.setSourceClientId(new SourceClientId(sourceId));
		otherName.setSource(source);
		return otherName;
	}

	private Work createWork(Visibility v, String sourceId) {
		Work work = new Work();
		work.setVisibility(v);
		Source source = new Source();
		source.setSourceClientId(new SourceClientId(sourceId));
		work.setSource(source);
		return work;
	}

	private Name createName(Visibility v) {
		Name name = new Name();
		name.setVisibility(v);
		name.setCreditName(new CreditName("Credit Name"));
		return name;
	}

	private ProfileEntity createProfileEntity() {
		ProfileEntity entity = new ProfileEntity();
		entity.setClaimed(true);
		entity.setDeactivationDate(null);
		entity.setDeprecatedDate(null);
		entity.setId("0000-0000-0000-0000");
		entity.setRecordLocked(false);
		entity.setSource(new SourceEntity(new ClientDetailsEntity("APP-0000000000000000")));
		entity.setSubmissionDate(null);
		return entity;
	}
}
