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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.manager.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc4.Country;
import org.orcid.jaxb.model.common_rc4.CreditName;
import org.orcid.jaxb.model.common_rc4.Iso3166Country;
import org.orcid.jaxb.model.common_rc4.Source;
import org.orcid.jaxb.model.common_rc4.SourceClientId;
import org.orcid.jaxb.model.common_rc4.Url;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.Educations;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc4.Employments;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.Fundings;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc4.Works;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.ExternalIDs;
import org.orcid.jaxb.model.record_rc4.FamilyName;
import org.orcid.jaxb.model.record_rc4.GivenNames;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.jaxb.model.record_rc4.SourceAware;
import org.orcid.jaxb.model.record_rc4.Work;
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

	private final String EXTID_1 = "extId1";
	private final String EXTID_2 = "extId2";
	private final String EXTID_3 = "extId3";
	private final String EXTID_SHARED = "shared";

	@Resource
	private WorkManagerReadOnly workManagerReadOnly;

	@Resource
	private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

	@Resource
	private PeerReviewManagerReadOnly peerReviewManagerReadOnly;

	@After
	public void after() {
		SecurityContextTestUtils.setUpSecurityContextForAnonymous();
	}

	@Test
	public void testIShouldFail() {
		fail();
	}
	
	/**
	 * =================== checkScopes test's ===================
	 */
	@Test
	public void testCheckScopes_ReadPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC);
	}

	@Test
	public void testCheckScopes_Authenticate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AUTHENTICATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.AUTHENTICATE, ScopePathType.READ_PUBLIC);
	}

	@Test
	public void testCheckScopes_AffiliationsReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_READ_LIMITED);
	}

	@Test
	public void testCheckScopes_AffiliationsCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_CREATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_CREATE);
	}

	@Test
	public void testCheckScopes_AffiliationsUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_UPDATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.AFFILIATIONS_UPDATE);
	}

	@Test
	public void testCheckScopes_FundingReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_READ_LIMITED);
	}

	@Test
	public void testCheckScopes_FundingCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_CREATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_CREATE);
	}

	@Test
	public void testCheckScopes_FundingUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_UPDATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.FUNDING_UPDATE);
	}

	@Test
	public void testCheckScopes_PatentsReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_READ_LIMITED);
	}

	@Test
	public void testCheckScopes_PatentsCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_CREATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_CREATE);
	}

	@Test
	public void testCheckScopes_PatentsUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_UPDATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_PATENTS_UPDATE);
	}

	@Test
	public void testCheckScopes_PeerReviewReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_READ_LIMITED);
	}

	@Test
	public void testCheckScopes_PeerReviewCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_CREATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_CREATE);
	}

	@Test
	public void testCheckScopes_PeerReviewUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_UPDATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.PEER_REVIEW_UPDATE);
	}

	@Test
	public void testCheckScopes_OrcidWorksReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test
	public void testCheckScopes_OrcidWorksCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_CREATE);
	}

	@Test
	public void testCheckScopes_OrcidWorksUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_UPDATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_WORKS_UPDATE);
	}

	@Test
	public void testCheckScopes_ActivitiesReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED,
				ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.FUNDING_READ_LIMITED,
				ScopePathType.ORCID_PATENTS_READ_LIMITED, ScopePathType.PEER_REVIEW_READ_LIMITED,
				ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test
	public void testCheckScopes_ActivitiesUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_UPDATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_UPDATE,
				ScopePathType.ORCID_WORKS_UPDATE, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.FUNDING_UPDATE,
				ScopePathType.FUNDING_CREATE, ScopePathType.AFFILIATIONS_UPDATE, ScopePathType.AFFILIATIONS_CREATE,
				ScopePathType.ORCID_PATENTS_CREATE, ScopePathType.ORCID_PATENTS_UPDATE,
				ScopePathType.PEER_REVIEW_UPDATE, ScopePathType.PEER_REVIEW_CREATE, ScopePathType.ACTIVITIES_UPDATE);
	}

	@Test
	public void testCheckScopes_OrcidProfileReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PROFILE_READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED,
				ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.FUNDING_READ_LIMITED,
				ScopePathType.ORCID_PATENTS_READ_LIMITED, ScopePathType.PEER_REVIEW_READ_LIMITED,
				ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_BIO_READ_LIMITED,
				ScopePathType.PERSON_READ_LIMITED, ScopePathType.READ_LIMITED,
				ScopePathType.ORCID_PROFILE_READ_LIMITED);
	}

	@Test
	public void testCheckScopes_ReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_READ_LIMITED,
				ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.FUNDING_READ_LIMITED,
				ScopePathType.ORCID_PATENTS_READ_LIMITED, ScopePathType.PEER_REVIEW_READ_LIMITED,
				ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_BIO_READ_LIMITED,
				ScopePathType.PERSON_READ_LIMITED, ScopePathType.READ_LIMITED);
	}

	@Test
	public void testCheckScopes_OrcidBioUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_BIO_UPDATE,
				ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckScopes_OrcidBioReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test
	public void testCheckScopes_OrcidBioExternalIdentifiersCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1,
				ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
		assertCheckScopesFailForOtherScopes(ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	private void assertCheckScopesFailForOtherScopes(ScopePathType... goodOnes) {
		List<ScopePathType> list = Arrays.asList(goodOnes);
		for (ScopePathType s : ScopePathType.values()) {
			if (!list.contains(s)) {
				assertItThrowAccessControlException(s);
			} else {
				orcidSecurityManager.checkScopes(s);
			}
		}
	}

	/**
	 * =================== checkClientAccessAndScopes test's ===================
	 */
	@Test(expected = OrcidUnauthorizedException.class)
	public void testCheckClientAccessAndScopes_When_TokenIsForOtherUser() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		orcidSecurityManager.checkClientAccessAndScopes(ORCID_2, ScopePathType.ORCID_BIO_UPDATE);
		fail();
	}

	@Test
	public void testCheckClientAccessAndScopes_ReadPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC);
	}

	@Test
	public void testCheckClientAccessAndScopes_Authenticate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AUTHENTICATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.AUTHENTICATE,
				ScopePathType.READ_PUBLIC);
	}

	@Test
	public void testCheckClientAccessAndScopes_AffiliationsReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.AFFILIATIONS_READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_AffiliationsCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_CREATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.AFFILIATIONS_CREATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_AffiliationsUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_UPDATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.AFFILIATIONS_UPDATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_FundingReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.FUNDING_READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_FundingCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_CREATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.FUNDING_CREATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_FundingUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_UPDATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.FUNDING_UPDATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_PatentsReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_PATENTS_READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_PatentsCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_CREATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_PATENTS_CREATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_PatentsUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PATENTS_UPDATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_PATENTS_UPDATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_PeerReviewReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.PEER_REVIEW_READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_PeerReviewCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_CREATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.PEER_REVIEW_CREATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_PeerReviewUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_UPDATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.PEER_REVIEW_UPDATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_OrcidWorksReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_OrcidWorksCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_WORKS_CREATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_OrcidWorksUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_UPDATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_WORKS_UPDATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_ActivitiesReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.AFFILIATIONS_READ_LIMITED,
				ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED,
				ScopePathType.PEER_REVIEW_READ_LIMITED, ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_ActivitiesUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_UPDATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ACTIVITIES_UPDATE, ScopePathType.ORCID_WORKS_UPDATE, ScopePathType.ORCID_WORKS_CREATE,
				ScopePathType.FUNDING_UPDATE, ScopePathType.FUNDING_CREATE, ScopePathType.AFFILIATIONS_UPDATE,
				ScopePathType.AFFILIATIONS_CREATE, ScopePathType.ORCID_PATENTS_CREATE,
				ScopePathType.ORCID_PATENTS_UPDATE, ScopePathType.PEER_REVIEW_UPDATE, ScopePathType.PEER_REVIEW_CREATE,
				ScopePathType.ACTIVITIES_UPDATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_OrcidProfileReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_PROFILE_READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.AFFILIATIONS_READ_LIMITED,
				ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED,
				ScopePathType.PEER_REVIEW_READ_LIMITED, ScopePathType.ORCID_WORKS_READ_LIMITED,
				ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.PERSON_READ_LIMITED, ScopePathType.READ_LIMITED,
				ScopePathType.ORCID_PROFILE_READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_ReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.AFFILIATIONS_READ_LIMITED,
				ScopePathType.FUNDING_READ_LIMITED, ScopePathType.ORCID_PATENTS_READ_LIMITED,
				ScopePathType.PEER_REVIEW_READ_LIMITED, ScopePathType.ORCID_WORKS_READ_LIMITED,
				ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.PERSON_READ_LIMITED, ScopePathType.READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_OrcidBioUpdate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_UPDATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_BIO_UPDATE, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	@Test
	public void testCheckClientAccessAndScopes_OrcidBioReadLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test
	public void testCheckClientAccessAndScopes_OrcidBioExternalIdentifiersCreate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1,
				ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
		assertCheckClientAccessAndScopesFailForOtherScopes(ORCID_1, ScopePathType.READ_PUBLIC,
				ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE);
	}

	private void assertCheckClientAccessAndScopesFailForOtherScopes(String orcid, ScopePathType... goodOnes) {
		List<ScopePathType> list = Arrays.asList(goodOnes);
		for (ScopePathType s : ScopePathType.values()) {
			if (!list.contains(s)) {
				assertItThrowAccessControlException(orcid, s);
			} else {
				orcidSecurityManager.checkClientAccessAndScopes(orcid, s);
			}
		}
	}

	/**
	 * =================== checkAndFilter test's ===================
	 */
	// ---- ELEMENTS WITHOUT SOURCE ----
	// Name element tests
	@Test
	public void testName_CanRead_When_ReadPublicToken_IsPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test(expected = AccessControlException.class)
	public void testName_CantRead_When_ReadPublicToken_IsLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.LIMITED);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test(expected = AccessControlException.class)
	public void testName_CantRead_When_ReadPublicToken_IsPrivate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PRIVATE);
		orcidSecurityManager.checkAndFilter(ORCID_1, name, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
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
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Biography bio = createBiography(Visibility.PUBLIC);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
	}

	@Test(expected = AccessControlException.class)
	public void testBio_CantRead_When_ReadPublicToken_IsLimited() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Biography bio = createBiography(Visibility.LIMITED);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test(expected = AccessControlException.class)
	public void testBio_CantRead_When_ReadPublicToken_IsPrivate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Biography bio = createBiography(Visibility.PRIVATE);
		orcidSecurityManager.checkAndFilter(ORCID_1, bio, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
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

	// ---- ELEMENTS WITH SOURCE ----
	// Work element tests
	@Test
	public void testWork_CanRead_When_IsSource_And_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);

		Work work = createWork(Visibility.PUBLIC, CLIENT_1);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

		work = createWork(Visibility.LIMITED, CLIENT_1);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

		work = createWork(Visibility.PRIVATE, CLIENT_1);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test
	public void testWork_CanRead_When_ReadPublicToken_IsPublic_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Work work = createWork(Visibility.PUBLIC, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test(expected = AccessControlException.class)
	public void testWork_CantRead_When_ReadPublicToken_IsLimited_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Work work = createWork(Visibility.LIMITED, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
		fail();
	}

	@Test(expected = AccessControlException.class)
	public void testWork_CantRead_When_ReadPublicToken_IsPrivate_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Work work = createWork(Visibility.PRIVATE, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
		fail();
	}

	@Test(expected = OrcidUnauthorizedException.class)
	public void testWork_ThrowException_When_TokenIsForOtherUser_IsSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Work work = createWork(Visibility.PUBLIC, CLIENT_1);
		orcidSecurityManager.checkAndFilter(ORCID_2, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
		fail();
	}

	@Test(expected = OrcidUnauthorizedException.class)
	public void testWork_ThrowException_When_TokenIsForOtherUser_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Work work = createWork(Visibility.PUBLIC, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_2, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
		fail();
	}

	@Test
	public void testWork_CanRead_When_IsSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);

		Work work = createWork(Visibility.PUBLIC, CLIENT_1);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

		work = createWork(Visibility.LIMITED, CLIENT_1);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

		work = createWork(Visibility.PRIVATE, CLIENT_1);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test
	public void testWork_CanRead_When_DontHaveReadScope_IsPublic_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
		Work work = createWork(Visibility.PUBLIC, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test(expected = AccessControlException.class)
	public void testWork_CantRead_When_DontHaveReadScope_IsLimited_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
		Work work = createWork(Visibility.LIMITED, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
		fail();
	}

	@Test(expected = AccessControlException.class)
	public void testWork_CantRead_When_DontHaveReadScope_IsPrivate_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_CREATE);
		Work work = createWork(Visibility.PRIVATE, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
		fail();
	}

	@Test
	public void testWork_CanRead_When_HaveReadScope_IsPublic_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
		Work work = createWork(Visibility.PUBLIC, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test
	public void testWork_CanRead_When_HaveReadScope_IsLimited_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
		Work work = createWork(Visibility.LIMITED, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
	}

	@Test(expected = OrcidVisibilityException.class)
	public void testWork_CantRead_When_HaveReadScope_IsPrivate_NotSource() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
		Work work = createWork(Visibility.PRIVATE, CLIENT_2);
		orcidSecurityManager.checkAndFilter(ORCID_1, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
		fail();
	}

	// ---- COLLECTIONS OF ELEMENTS ----
	@Test(expected = OrcidUnauthorizedException.class)
	public void testCollection_When_TokenIsForOtherUser() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		List<OtherName> list = new ArrayList<OtherName>();
		list.add(createOtherName(Visibility.PUBLIC, CLIENT_1));
		list.add(createOtherName(Visibility.PUBLIC, CLIENT_1));
		list.add(createOtherName(Visibility.PUBLIC, CLIENT_1));
		orcidSecurityManager.checkAndFilter(ORCID_2, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test
	public void testCollection_When_SourceOfAll_ReadPublicScope() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_1);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_1);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(3, list.size());
		assertTrue(list.contains(o1));
		assertTrue(list.contains(o2));
		assertTrue(list.contains(o3));
	}

	@Test
	public void testCollection_When_SourceOfAll_ReadLimitedScope() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_1);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_1);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(3, list.size());
		assertTrue(list.contains(o1));
		assertTrue(list.contains(o2));
		assertTrue(list.contains(o3));
	}

	@Test
	public void testCollection_When_NotSource_ReadPublicScope() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(1, list.size());
		assertTrue(list.contains(o1));
		assertFalse(list.contains(o2));
		assertFalse(list.contains(o3));
	}

	@Test
	public void testCollection_When_SourceOfPrivate_ReadPublicScope() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(2, list.size());
		assertTrue(list.contains(o1));
		assertFalse(list.contains(o2));
		assertTrue(list.contains(o3));
	}

	@Test
	public void testCollection_When_SourceOfLimitedAndPrivate_ReadPublicScope() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_1);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(3, list.size());
		assertTrue(list.contains(o1));
		assertTrue(list.contains(o2));
		assertTrue(list.contains(o3));
	}

	@Test
	public void testCollection_When_NotSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(2, list.size());
		assertTrue(list.contains(o1));
		assertTrue(list.contains(o2));
		assertFalse(list.contains(o3));
	}

	@Test
	public void testCollection_When_NotSource_WrongReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(1, list.size());
		assertTrue(list.contains(o1));
		assertFalse(list.contains(o2));
		assertFalse(list.contains(o3));
	}

	@Test
	public void testCollection_When_NotSource_ReadLimitedToken_NothingPublic() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o4 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o5 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		list.add(o4);
		list.add(o5);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(2, list.size());
		assertFalse(list.contains(o1));
		assertTrue(list.contains(o2));
		assertFalse(list.contains(o3));
		assertTrue(list.contains(o4));
		assertFalse(list.contains(o5));
	}

	@Test
	public void testCollection_When_NotSource_ReadLimitedToken_AllPrivate() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o4 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o5 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		list.add(o4);
		list.add(o5);
		assertFalse(list.isEmpty());
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertTrue(list.isEmpty());
	}

	@Test
	public void testCollection_When_SourceOfPrivate_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		List<OtherName> list = new ArrayList<OtherName>();
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		list.add(o1);
		list.add(o2);
		list.add(o3);
		orcidSecurityManager.checkAndFilter(ORCID_1, list, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertEquals(3, list.size());
		assertTrue(list.contains(o1));
		assertTrue(list.contains(o2));
		assertTrue(list.contains(o3));
	}

	// ---- PERSONAL DETAILS ----
	@Test(expected = OrcidUnauthorizedException.class)
	public void testPersonalDetails_When_TokenForOtherUser() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		PersonalDetails p = new PersonalDetails();
		orcidSecurityManager.checkAndFilter(ORCID_2, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		fail();
	}

	@Test
	public void testPersonalDetails_When_AllPublic_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PUBLIC);
		Biography bio = createBiography(Visibility.PUBLIC);
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertEquals(bio, p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
	}

	@Test
	public void testPersonalDetails_When_SomeLimited_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.LIMITED);
		Biography bio = createBiography(Visibility.PUBLIC);
		OtherName o1 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertEquals(bio, p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		assertFalse(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertFalse(p.getOtherNames().getOtherNames().contains(o3));
	}

	@Test
	public void testPersonalDetails_When_SomePrivate_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PUBLIC);
		Biography bio = createBiography(Visibility.PRIVATE);
		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertNull(p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		assertFalse(p.getOtherNames().getOtherNames().contains(o1));
		assertFalse(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
	}

	@Test
	public void testPersonalDetails_When_AllPrivate_NoSource_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PRIVATE);
		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertNull(p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertTrue(p.getOtherNames().getOtherNames().isEmpty());
	}

	@Test
	public void testPersonalDetails_When_AllPrivate_Source_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PRIVATE);
		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertNull(p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
	}

	@Test
	public void testPersonalDetails_When_AllPublic_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Name name = createName(Visibility.PUBLIC);
		Biography bio = createBiography(Visibility.PUBLIC);
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertEquals(bio, p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
	}

	@Test
	public void testPersonalDetails_When_SomeLimited_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Name name = createName(Visibility.LIMITED);
		Biography bio = createBiography(Visibility.LIMITED);
		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertEquals(bio, p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
	}

	@Test
	public void testPersonalDetails_When_SomePrivate_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PUBLIC);
		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertEquals(bio, p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertEquals(2, p.getOtherNames().getOtherNames().size());
		assertFalse(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
	}

	@Test
	public void testPersonalDetails_When_AllPrivate_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PRIVATE);
		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertNull(p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertTrue(p.getOtherNames().getOtherNames().isEmpty());
	}

	@Test
	public void testPersonalDetails_When_AllPrivate_Source_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PRIVATE);
		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));
		PersonalDetails p = new PersonalDetails();
		p.setBiography(bio);
		p.setName(name);
		p.setOtherNames(otherNames);
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertNull(p.getBiography());
		assertNotNull(p.getOtherNames());
		assertNotNull(p.getOtherNames().getOtherNames());
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
	}

	@Test
	public void testPersonalDetails_When_ReadLimitedToken_EmptyElement() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
		PersonalDetails p = new PersonalDetails();
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.ORCID_BIO_READ_LIMITED);
		assertNotNull(p);
	}

	// ---- PERSON ----
	@Test(expected = OrcidUnauthorizedException.class)
	public void testPerson_When_TokenForOtherUser() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
		Person p = new Person();
		orcidSecurityManager.checkAndFilter(ORCID_2, p, ScopePathType.PERSON_READ_LIMITED);
		fail();
	}

	@Test
	public void testPerson_When_AllPublic_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PUBLIC);
		Biography bio = createBiography(Visibility.PUBLIC);

		Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertEquals(bio, p.getBiography());
		// Check addresses
		assertEquals(3, p.getAddresses().getAddress().size());
		assertTrue(p.getAddresses().getAddress().contains(a1));
		assertTrue(p.getAddresses().getAddress().contains(a2));
		assertTrue(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(3, p.getEmails().getEmails().size());
		assertTrue(p.getEmails().getEmails().contains(e1));
		assertTrue(p.getEmails().getEmails().contains(e2));
		assertTrue(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(3, p.getKeywords().getKeywords().size());
		assertTrue(p.getKeywords().getKeywords().contains(k1));
		assertTrue(p.getKeywords().getKeywords().contains(k2));
		assertTrue(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_SomeLimited_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.LIMITED);
		Biography bio = createBiography(Visibility.PUBLIC);

		Address a1 = createAddress(Visibility.LIMITED, CLIENT_2);
		Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Address a3 = createAddress(Visibility.LIMITED, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.LIMITED, CLIENT_2);
		Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Email e3 = createEmail(Visibility.LIMITED, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.LIMITED, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.LIMITED, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertEquals(bio, p.getBiography());
		// Check addresses
		assertEquals(1, p.getAddresses().getAddress().size());
		assertFalse(p.getAddresses().getAddress().contains(a1));
		assertTrue(p.getAddresses().getAddress().contains(a2));
		assertFalse(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(1, p.getEmails().getEmails().size());
		assertFalse(p.getEmails().getEmails().contains(e1));
		assertTrue(p.getEmails().getEmails().contains(e2));
		assertFalse(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(1, p.getKeywords().getKeywords().size());
		assertFalse(p.getKeywords().getKeywords().contains(k1));
		assertTrue(p.getKeywords().getKeywords().contains(k2));
		assertFalse(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		assertFalse(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertFalse(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_SomePrivate_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PUBLIC);
		Biography bio = createBiography(Visibility.PRIVATE);

		Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertNull(p.getBiography());
		// Check addresses
		assertEquals(1, p.getAddresses().getAddress().size());
		assertFalse(p.getAddresses().getAddress().contains(a1));
		assertFalse(p.getAddresses().getAddress().contains(a2));
		assertTrue(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(1, p.getEmails().getEmails().size());
		assertFalse(p.getEmails().getEmails().contains(e1));
		assertFalse(p.getEmails().getEmails().contains(e2));
		assertTrue(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(1, p.getKeywords().getKeywords().size());
		assertFalse(p.getKeywords().getKeywords().contains(k1));
		assertFalse(p.getKeywords().getKeywords().contains(k2));
		assertTrue(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		assertFalse(p.getOtherNames().getOtherNames().contains(o1));
		assertFalse(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_AllPrivate_NoSource_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PRIVATE);

		Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertNull(p.getBiography());
		// Check addresses
		assertEquals(0, p.getAddresses().getAddress().size());
		// Check emails
		assertEquals(0, p.getEmails().getEmails().size());
		// Check ext ids
		assertEquals(0, p.getExternalIdentifiers().getExternalIdentifiers().size());
		// Check keywords
		assertEquals(0, p.getKeywords().getKeywords().size());
		// Check other names
		assertEquals(0, p.getOtherNames().getOtherNames().size());
		// Check researcher urls
		assertEquals(0, p.getResearcherUrls().getResearcherUrls().size());
	}

	@Test
	public void testPerson_When_AllPrivate_Source_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PRIVATE);

		Address a1 = createAddress(Visibility.PRIVATE, CLIENT_1);
		Address a2 = createAddress(Visibility.PRIVATE, CLIENT_1);
		Address a3 = createAddress(Visibility.PRIVATE, CLIENT_1);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PRIVATE, CLIENT_1);
		Email e2 = createEmail(Visibility.PRIVATE, CLIENT_1);
		Email e3 = createEmail(Visibility.PRIVATE, CLIENT_1);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_1);
		Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_1);
		Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_1);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertNull(p.getBiography());
		// Check addresses
		assertEquals(3, p.getAddresses().getAddress().size());
		assertTrue(p.getAddresses().getAddress().contains(a1));
		assertTrue(p.getAddresses().getAddress().contains(a2));
		assertTrue(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(3, p.getEmails().getEmails().size());
		assertTrue(p.getEmails().getEmails().contains(e1));
		assertTrue(p.getEmails().getEmails().contains(e2));
		assertTrue(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(3, p.getKeywords().getKeywords().size());
		assertTrue(p.getKeywords().getKeywords().contains(k1));
		assertTrue(p.getKeywords().getKeywords().contains(k2));
		assertTrue(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_AllPublic_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
		Name name = createName(Visibility.PUBLIC);
		Biography bio = createBiography(Visibility.PUBLIC);

		Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Address a2 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Address a3 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Email e2 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Email e3 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertEquals(bio, p.getBiography());
		// Check addresses
		assertEquals(3, p.getAddresses().getAddress().size());
		assertTrue(p.getAddresses().getAddress().contains(a1));
		assertTrue(p.getAddresses().getAddress().contains(a2));
		assertTrue(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(3, p.getEmails().getEmails().size());
		assertTrue(p.getEmails().getEmails().contains(e1));
		assertTrue(p.getEmails().getEmails().contains(e2));
		assertTrue(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(3, p.getKeywords().getKeywords().size());
		assertTrue(p.getKeywords().getKeywords().contains(k1));
		assertTrue(p.getKeywords().getKeywords().contains(k2));
		assertTrue(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_SomeLimited_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
		Name name = createName(Visibility.LIMITED);
		Biography bio = createBiography(Visibility.LIMITED);

		Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Address a2 = createAddress(Visibility.LIMITED, CLIENT_2);
		Address a3 = createAddress(Visibility.LIMITED, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
		Email e3 = createEmail(Visibility.LIMITED, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.LIMITED, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.LIMITED, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertEquals(bio, p.getBiography());
		// Check addresses
		assertEquals(3, p.getAddresses().getAddress().size());
		assertTrue(p.getAddresses().getAddress().contains(a1));
		assertTrue(p.getAddresses().getAddress().contains(a2));
		assertTrue(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(3, p.getEmails().getEmails().size());
		assertTrue(p.getEmails().getEmails().contains(e1));
		assertTrue(p.getEmails().getEmails().contains(e2));
		assertTrue(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(3, p.getKeywords().getKeywords().size());
		assertTrue(p.getKeywords().getKeywords().contains(k1));
		assertTrue(p.getKeywords().getKeywords().contains(k2));
		assertTrue(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_SomePrivate_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PUBLIC);

		Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertEquals(bio, p.getBiography());
		// Check addresses
		assertEquals(1, p.getAddresses().getAddress().size());
		assertTrue(p.getAddresses().getAddress().contains(a1));
		assertFalse(p.getAddresses().getAddress().contains(a2));
		assertFalse(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(1, p.getEmails().getEmails().size());
		assertTrue(p.getEmails().getEmails().contains(e1));
		assertFalse(p.getEmails().getEmails().contains(e2));
		assertFalse(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(1, p.getKeywords().getKeywords().size());
		assertTrue(p.getKeywords().getKeywords().contains(k1));
		assertFalse(p.getKeywords().getKeywords().contains(k2));
		assertFalse(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertFalse(p.getOtherNames().getOtherNames().contains(o2));
		assertFalse(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_AllPrivate_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PRIVATE);

		Address a1 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Address a2 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Email e2 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertNull(p.getBiography());
		// Check addresses
		assertEquals(0, p.getAddresses().getAddress().size());
		// Check emails
		assertEquals(0, p.getEmails().getEmails().size());
		// Check ext ids
		assertEquals(0, p.getExternalIdentifiers().getExternalIdentifiers().size());
		// Check keywords
		assertEquals(0, p.getKeywords().getKeywords().size());
		// Check other names
		assertEquals(0, p.getOtherNames().getOtherNames().size());
		// Check researcher urls
		assertEquals(0, p.getResearcherUrls().getResearcherUrls().size());
	}

	@Test
	public void testPerson_When_AllPrivate_Source_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
		Name name = createName(Visibility.PRIVATE);
		Biography bio = createBiography(Visibility.PRIVATE);

		Address a1 = createAddress(Visibility.PRIVATE, CLIENT_1);
		Address a2 = createAddress(Visibility.PRIVATE, CLIENT_1);
		Address a3 = createAddress(Visibility.PRIVATE, CLIENT_1);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PRIVATE, CLIENT_1);
		Email e2 = createEmail(Visibility.PRIVATE, CLIENT_1);
		Email e3 = createEmail(Visibility.PRIVATE, CLIENT_1);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PRIVATE, CLIENT_1);
		Keyword k2 = createKeyword(Visibility.PRIVATE, CLIENT_1);
		Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_1);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherName o2 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_1);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_1);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
		ResearcherUrl r2 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_1);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertNull(p.getName());
		assertNull(p.getBiography());
		// Check addresses
		assertEquals(3, p.getAddresses().getAddress().size());
		assertTrue(p.getAddresses().getAddress().contains(a1));
		assertTrue(p.getAddresses().getAddress().contains(a2));
		assertTrue(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(3, p.getEmails().getEmails().size());
		assertTrue(p.getEmails().getEmails().contains(e1));
		assertTrue(p.getEmails().getEmails().contains(e2));
		assertTrue(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(3, p.getKeywords().getKeywords().size());
		assertTrue(p.getKeywords().getKeywords().contains(k1));
		assertTrue(p.getKeywords().getKeywords().contains(k2));
		assertTrue(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(3, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertTrue(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_MixedVisibility_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
		Name name = createName(Visibility.LIMITED);
		Biography bio = createBiography(Visibility.PUBLIC);

		Address a1 = createAddress(Visibility.PUBLIC, CLIENT_2);
		Address a2 = createAddress(Visibility.LIMITED, CLIENT_2);
		Address a3 = createAddress(Visibility.PRIVATE, CLIENT_2);
		Addresses addresses = new Addresses();
		addresses.setAddress(new ArrayList<Address>(Arrays.asList(a1, a2, a3)));

		Email e1 = createEmail(Visibility.PUBLIC, CLIENT_2);
		Email e2 = createEmail(Visibility.LIMITED, CLIENT_2);
		Email e3 = createEmail(Visibility.PRIVATE, CLIENT_2);
		Emails emails = new Emails();
		emails.setEmails(new ArrayList<Email>(Arrays.asList(e1, e2, e3)));

		Keyword k1 = createKeyword(Visibility.PUBLIC, CLIENT_2);
		Keyword k2 = createKeyword(Visibility.LIMITED, CLIENT_2);
		Keyword k3 = createKeyword(Visibility.PRIVATE, CLIENT_2);
		Keywords keywords = new Keywords();
		keywords.setKeywords(new ArrayList<Keyword>(Arrays.asList(k1, k2, k3)));

		OtherName o1 = createOtherName(Visibility.PUBLIC, CLIENT_2);
		OtherName o2 = createOtherName(Visibility.LIMITED, CLIENT_2);
		OtherName o3 = createOtherName(Visibility.PRIVATE, CLIENT_2);
		OtherNames otherNames = new OtherNames();
		otherNames.setOtherNames(new ArrayList<OtherName>(Arrays.asList(o1, o2, o3)));

		PersonExternalIdentifier ext1 = createPersonExternalIdentifier(Visibility.PUBLIC, CLIENT_2);
		PersonExternalIdentifier ext2 = createPersonExternalIdentifier(Visibility.LIMITED, CLIENT_2);
		PersonExternalIdentifier ext3 = createPersonExternalIdentifier(Visibility.PRIVATE, CLIENT_2);
		PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
		extIds.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>(Arrays.asList(ext1, ext2, ext3)));

		ResearcherUrl r1 = createResearcherUrl(Visibility.PUBLIC, CLIENT_2);
		ResearcherUrl r2 = createResearcherUrl(Visibility.LIMITED, CLIENT_2);
		ResearcherUrl r3 = createResearcherUrl(Visibility.PRIVATE, CLIENT_2);
		ResearcherUrls researcherUrls = new ResearcherUrls();
		researcherUrls.setResearcherUrls(new ArrayList<ResearcherUrl>(Arrays.asList(r1, r2, r3)));

		Person p = new Person();
		p.setBiography(bio);
		p.setName(name);
		p.setAddresses(addresses);
		p.setEmails(emails);
		p.setExternalIdentifiers(extIds);
		p.setKeywords(keywords);
		p.setOtherNames(otherNames);
		p.setResearcherUrls(researcherUrls);

		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
		assertEquals(name, p.getName());
		assertEquals(bio, p.getBiography());
		// Check addresses
		assertEquals(2, p.getAddresses().getAddress().size());
		assertTrue(p.getAddresses().getAddress().contains(a1));
		assertTrue(p.getAddresses().getAddress().contains(a2));
		assertFalse(p.getAddresses().getAddress().contains(a3));
		// Check emails
		assertEquals(2, p.getEmails().getEmails().size());
		assertTrue(p.getEmails().getEmails().contains(e1));
		assertTrue(p.getEmails().getEmails().contains(e2));
		assertFalse(p.getEmails().getEmails().contains(e3));
		// Check ext ids
		assertEquals(2, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext1));
		assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext2));
		assertFalse(p.getExternalIdentifiers().getExternalIdentifiers().contains(ext3));
		// Check keywords
		assertEquals(2, p.getKeywords().getKeywords().size());
		assertTrue(p.getKeywords().getKeywords().contains(k1));
		assertTrue(p.getKeywords().getKeywords().contains(k2));
		assertFalse(p.getKeywords().getKeywords().contains(k3));
		// Check other names
		assertEquals(2, p.getOtherNames().getOtherNames().size());
		assertTrue(p.getOtherNames().getOtherNames().contains(o1));
		assertTrue(p.getOtherNames().getOtherNames().contains(o2));
		assertFalse(p.getOtherNames().getOtherNames().contains(o3));
		// Check researcher urls
		assertEquals(2, p.getResearcherUrls().getResearcherUrls().size());
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r1));
		assertTrue(p.getResearcherUrls().getResearcherUrls().contains(r2));
		assertFalse(p.getResearcherUrls().getResearcherUrls().contains(r3));
	}

	@Test
	public void testPerson_When_ReadLimitedToken_EmptyElement() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PERSON_READ_LIMITED);
		Person p = new Person();
		orcidSecurityManager.checkAndFilter(ORCID_1, p, ScopePathType.PERSON_READ_LIMITED);
		assertNotNull(p);
	}

	// ---- ACTIVITIES ----
	@Test(expected = OrcidUnauthorizedException.class)
	public void testActivitiesSummary_When_TokenForOtherUser() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		ActivitiesSummary as = new ActivitiesSummary();
		orcidSecurityManager.checkAndFilter(ORCID_2, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		fail();
	}

	@Test
	public void testActivitiesSummary_When_AllPublic_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(3, as.getEducations().getSummaries().size());
		assertTrue(as.getEducations().getSummaries().contains(e1));
		assertTrue(as.getEducations().getSummaries().contains(e2));
		assertTrue(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(3, as.getEmployments().getSummaries().size());
		assertTrue(as.getEmployments().getSummaries().contains(em1));
		assertTrue(as.getEmployments().getSummaries().contains(em2));
		assertTrue(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_SomeLimited_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		EducationSummary e1 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.LIMITED, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(1, as.getEducations().getSummaries().size());
		assertFalse(as.getEducations().getSummaries().contains(e1));
		assertTrue(as.getEducations().getSummaries().contains(e2));
		assertFalse(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertFalse(as.getEmployments().getSummaries().contains(em1));
		assertTrue(as.getEmployments().getSummaries().contains(em2));
		assertFalse(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_SomePrivate_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(1, as.getEducations().getSummaries().size());
		assertFalse(as.getEducations().getSummaries().contains(e1));
		assertFalse(as.getEducations().getSummaries().contains(e2));
		assertTrue(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertFalse(as.getEmployments().getSummaries().contains(em1));
		assertFalse(as.getEmployments().getSummaries().contains(em2));
		assertTrue(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_AllPrivate_NoSource_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(0, as.getEducations().getSummaries().size());
		// Check employments
		assertEquals(0, as.getEmployments().getSummaries().size());
		// Check fundings
		assertEquals(0, as.getFundings().getFundingGroup().size());
		// Check peer reviews
		assertEquals(0, as.getPeerReviews().getPeerReviewGroup().size());
		// Check works
		assertEquals(0, as.getWorks().getWorkGroup().size());
	}

	@Test
	public void testActivitiesSummary_When_AllPrivate_Source_ReadPublicToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
		EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
		EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
		EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);

		FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(3, as.getEducations().getSummaries().size());
		assertTrue(as.getEducations().getSummaries().contains(e1));
		assertTrue(as.getEducations().getSummaries().contains(e2));
		assertTrue(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(3, as.getEmployments().getSummaries().size());
		assertTrue(as.getEmployments().getSummaries().contains(em1));
		assertTrue(as.getEmployments().getSummaries().contains(em2));
		assertTrue(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_AllPublic_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(3, as.getEducations().getSummaries().size());
		assertTrue(as.getEducations().getSummaries().contains(e1));
		assertTrue(as.getEducations().getSummaries().contains(e2));
		assertTrue(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(3, as.getEmployments().getSummaries().size());
		assertTrue(as.getEmployments().getSummaries().contains(em1));
		assertTrue(as.getEmployments().getSummaries().contains(em2));
		assertTrue(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_SomeLimited_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.LIMITED, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(3, as.getEducations().getSummaries().size());
		assertTrue(as.getEducations().getSummaries().contains(e1));
		assertTrue(as.getEducations().getSummaries().contains(e2));
		assertTrue(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(3, as.getEmployments().getSummaries().size());
		assertTrue(as.getEmployments().getSummaries().contains(em1));
		assertTrue(as.getEmployments().getSummaries().contains(em2));
		assertTrue(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_SomePrivate_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(1, as.getEducations().getSummaries().size());
		assertTrue(as.getEducations().getSummaries().contains(e1));
		assertFalse(as.getEducations().getSummaries().contains(e2));
		assertFalse(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertTrue(as.getEmployments().getSummaries().contains(em1));
		assertFalse(as.getEmployments().getSummaries().contains(em2));
		assertFalse(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_AllPrivate_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(0, as.getEducations().getSummaries().size());
		// Check employments
		assertEquals(0, as.getEmployments().getSummaries().size());
		// Check fundings
		assertEquals(0, as.getFundings().getFundingGroup().size());
		// Check peer reviews
		assertEquals(0, as.getPeerReviews().getPeerReviewGroup().size());
		// Check works
		assertEquals(0, as.getWorks().getWorkGroup().size());
	}

	@Test
	public void testActivitiesSummary_When_AllPrivate_Source_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
		EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
		EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);

		FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(3, as.getEducations().getSummaries().size());
		assertTrue(as.getEducations().getSummaries().contains(e1));
		assertTrue(as.getEducations().getSummaries().contains(e2));
		assertTrue(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(3, as.getEmployments().getSummaries().size());
		assertTrue(as.getEmployments().getSummaries().contains(em1));
		assertTrue(as.getEmployments().getSummaries().contains(em2));
		assertTrue(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_MixedVisibility_NoSource_ReadLimitedToken() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
		EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
		EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

		EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
		EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
		EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

		FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
		FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
		PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
		WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
		WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

		ActivitiesSummary as = new ActivitiesSummary();
		as.setEducations(createEducations(e1, e2, e3));
		as.setEmployments(createEmployments(em1, em2, em3));
		as.setFundings(createFundings(f1, f2, f3));
		as.setPeerReviews(createPeerReviews(p1, p2, p3));
		as.setWorks(createWorks(w1, w2, w3));

		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
		// Check educations
		assertEquals(2, as.getEducations().getSummaries().size());
		assertTrue(as.getEducations().getSummaries().contains(e1));
		assertTrue(as.getEducations().getSummaries().contains(e2));
		assertFalse(as.getEducations().getSummaries().contains(e3));
		// Check employments
		assertEquals(2, as.getEmployments().getSummaries().size());
		assertTrue(as.getEmployments().getSummaries().contains(em1));
		assertTrue(as.getEmployments().getSummaries().contains(em2));
		assertFalse(as.getEmployments().getSummaries().contains(em3));
		// Check fundings
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(2, as.getFundings().getFundingGroup().get(0).getActivities().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
		assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
		assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
		assertEquals(3, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
		// Check peer reviews
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
		assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
		assertEquals(1,
				as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED, "peer-review")));
		// Check works
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertEquals(2, as.getWorks().getWorkGroup().get(0).getActivities().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
		assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
		assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
		assertEquals(3, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_1)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_2)));
		assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_3)));
		assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier()
				.contains(getExtId(EXTID_SHARED)));
	}

	@Test
	public void testActivitiesSummary_When_ReadLimitedToken_EmptyElement() {
		SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
		ActivitiesSummary as = new ActivitiesSummary();
		orcidSecurityManager.checkAndFilter(ORCID_1, as, ScopePathType.ACTIVITIES_READ_LIMITED);
		assertNotNull(as);
	}

	/**
	 * Utilities
	 */
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

	private Name createName(Visibility v) {
		Name name = new Name();
		name.setVisibility(v);
		name.setCreditName(new CreditName("Credit Name"));
		name.setFamilyName(new FamilyName("Family Name"));
		name.setGivenNames(new GivenNames("Given Names"));
		return name;
	}

	private Biography createBiography(Visibility v) {
		return new Biography("Biography", v);
	}

	private Address createAddress(Visibility v, String sourceId) {
		Address a = new Address();
		a.setVisibility(v);
		Iso3166Country[] all = Iso3166Country.values();
		Random r = new Random();
		int index = r.nextInt(all.length);
		if (index < 0 || index >= all.length) {
			index = 0;
		}
		a.setCountry(new Country(all[index]));
		setSource(a, sourceId);
		return a;
	}

	private OtherName createOtherName(Visibility v, String sourceId) {
		OtherName otherName = new OtherName();
		otherName.setContent("other-name-" + System.currentTimeMillis());
		otherName.setVisibility(v);
		setSource(otherName, sourceId);
		return otherName;
	}

	private PersonExternalIdentifier createPersonExternalIdentifier(Visibility v, String sourceId) {
		PersonExternalIdentifier p = new PersonExternalIdentifier();
		p.setValue("ext-id-" + System.currentTimeMillis());
		p.setVisibility(v);
		setSource(p, sourceId);
		return p;
	}

	private ResearcherUrl createResearcherUrl(Visibility v, String sourceId) {
		ResearcherUrl r = new ResearcherUrl();
		r.setUrl(new Url("http://orcid.org/test/" + System.currentTimeMillis()));
		r.setVisibility(v);
		setSource(r, sourceId);
		return r;
	}

	private Email createEmail(Visibility v, String sourceId) {
		Email email = new Email();
		email.setEmail("test-email-" + System.currentTimeMillis() + "@test.orcid.org");
		email.setVisibility(v);
		setSource(email, sourceId);
		return email;
	}

	private Keyword createKeyword(Visibility v, String sourceId) {
		Keyword k = new Keyword();
		k.setContent("keyword-" + System.currentTimeMillis());
		k.setVisibility(v);
		setSource(k, sourceId);
		return k;
	}

	private Work createWork(Visibility v, String sourceId) {
		Work work = new Work();
		work.setVisibility(v);
		setSource(work, sourceId);
		return work;
	}

	private WorkSummary createWorkSummary(Visibility v, String sourceId, String extIdValue) {
		WorkSummary work = new WorkSummary();
		work.setVisibility(v);
		ExternalID extId = new ExternalID();
		extId.setValue(extIdValue);
		ExternalIDs extIds = new ExternalIDs();
		extIds.getExternalIdentifier().add(extId);
		work.setExternalIdentifiers(extIds);
		addSharedExtId(extIds);
		setSource(work, sourceId);
		return work;
	}

	private Works createWorks(WorkSummary... elements) {
		return workManagerReadOnly.groupWorks(new ArrayList<WorkSummary>(Arrays.asList(elements)), false);
	}

	private FundingSummary createFundingSummary(Visibility v, String sourceId, String extIdValue) {
		FundingSummary f = new FundingSummary();
		f.setVisibility(v);
		setSource(f, sourceId);
		ExternalID extId = new ExternalID();
		extId.setValue(extIdValue);
		ExternalIDs extIds = new ExternalIDs();
		extIds.getExternalIdentifier().add(extId);
		addSharedExtId(extIds);
		f.setExternalIdentifiers(extIds);
		return f;
	}

	private Fundings createFundings(FundingSummary... elements) {
		return profileFundingManagerReadOnly.groupFundings(new ArrayList<FundingSummary>(Arrays.asList(elements)),
				false);
	}

	private PeerReviewSummary createPeerReviewSummary(Visibility v, String sourceId, String extIdValue) {
		PeerReviewSummary p = new PeerReviewSummary();
		p.setVisibility(v);
		p.setGroupId(EXTID_SHARED);
		setSource(p, sourceId);
		ExternalID extId = new ExternalID();
		extId.setValue(extIdValue);
		ExternalIDs extIds = new ExternalIDs();
		extIds.getExternalIdentifier().add(extId);
		addSharedExtId(extIds);
		p.setExternalIdentifiers(extIds);
		return p;
	}

	private PeerReviews createPeerReviews(PeerReviewSummary... elements) {
		return peerReviewManagerReadOnly.groupPeerReviews(new ArrayList<PeerReviewSummary>(Arrays.asList(elements)),
				false);
	}

	private EducationSummary createEducationSummary(Visibility v, String sourceId) {
		EducationSummary e = new EducationSummary();
		e.setVisibility(v);
		setSource(e, sourceId);
		return e;
	}

	private Educations createEducations(EducationSummary... elements) {
		Educations e = new Educations();
		for (EducationSummary s : elements) {
			e.getSummaries().add(s);
		}
		return e;
	}

	private EmploymentSummary createEmploymentSummary(Visibility v, String sourceId) {
		EmploymentSummary e = new EmploymentSummary();
		e.setVisibility(v);
		setSource(e, sourceId);
		return e;
	}

	private Employments createEmployments(EmploymentSummary... elements) {
		Employments e = new Employments();
		for (EmploymentSummary s : elements) {
			e.getSummaries().add(s);
		}
		return e;
	}

	private void addSharedExtId(ExternalIDs extIds) {
		ExternalID extId = new ExternalID();
		extId.setValue(EXTID_SHARED);
		extIds.getExternalIdentifier().add(extId);
	}

	private ExternalID getExtId(String value) {
		ExternalID extId = new ExternalID();
		extId.setValue(value);
		return extId;
	}

	private ExternalID getExtId(String value, String type) {
		ExternalID extId = new ExternalID();
		extId.setValue(value);
		extId.setType(type);
		return extId;
	}

	private void setSource(SourceAware element, String sourceId) {
		Source source = new Source();
		source.setSourceClientId(new SourceClientId(sourceId));
		element.setSource(source);
	}
}
