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
package org.orcid.api.publicV2.server.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common_rc3.Filterable;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.common_rc3.VisibilityType;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc3.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.ActivitiesContainer;
import org.orcid.jaxb.model.record_rc3.Address;
import org.orcid.jaxb.model.record_rc3.Addresses;
import org.orcid.jaxb.model.record_rc3.Biography;
import org.orcid.jaxb.model.record_rc3.Email;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.jaxb.model.record_rc3.GroupsContainer;
import org.orcid.jaxb.model.record_rc3.Keyword;
import org.orcid.jaxb.model.record_rc3.Keywords;
import org.orcid.jaxb.model.record_rc3.Name;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.OtherNames;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc3.PersonalDetails;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.orcid.jaxb.model.record_rc3.ResearcherUrls;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml", "classpath:orcid-t1-security-context.xml" })
public class PublicAPISecurityManagerV2Test {

	@Resource
	PublicAPISecurityManagerV2 publicAPISecurityManagerV2;

	@Test
	public void checkIsPublicFilterableTest() {
		publicAPISecurityManagerV2.checkIsPublic(getFilterableElement(Visibility.PUBLIC));

		try {
			publicAPISecurityManagerV2.checkIsPublic(getFilterableElement(Visibility.LIMITED));
			fail();
		} catch (OrcidUnauthorizedException e) {

		}

		try {
			publicAPISecurityManagerV2.checkIsPublic(getFilterableElement(Visibility.PRIVATE));
			fail();
		} catch (OrcidUnauthorizedException e) {

		}
	}

	@Test
	public void checkIsPublicVisibilityTypeTest() {
		publicAPISecurityManagerV2.checkIsPublic(getVisibilityTypeElement(Visibility.PUBLIC));

		try {
			publicAPISecurityManagerV2.checkIsPublic(getVisibilityTypeElement(Visibility.LIMITED));
			fail();
		} catch (OrcidUnauthorizedException e) {

		}

		try {
			publicAPISecurityManagerV2.checkIsPublic(getVisibilityTypeElement(Visibility.PRIVATE));
			fail();
		} catch (OrcidUnauthorizedException e) {

		}
	}

	@Test
	public void checkIsPublicBiographyTest() {
		Biography b = new Biography();
		b.setVisibility(Visibility.PUBLIC);
		publicAPISecurityManagerV2.checkIsPublic(b);

		try {
			b.setVisibility(Visibility.LIMITED);
			publicAPISecurityManagerV2.checkIsPublic(b);
			fail();
		} catch (OrcidUnauthorizedException e) {

		}

		try {
			b.setVisibility(Visibility.PRIVATE);
			publicAPISecurityManagerV2.checkIsPublic(b);
			fail();
		} catch (OrcidUnauthorizedException e) {

		}
	}

	@Test
	public void checkIsPublicNameTest() {
		Name n = new Name();
		n.setVisibility(Visibility.PUBLIC);
		publicAPISecurityManagerV2.checkIsPublic(n);

		try {
			n.setVisibility(Visibility.LIMITED);
			publicAPISecurityManagerV2.checkIsPublic(n);
			fail();
		} catch (OrcidUnauthorizedException e) {

		}

		try {
			n.setVisibility(Visibility.PRIVATE);
			publicAPISecurityManagerV2.checkIsPublic(n);
			fail();
		} catch (OrcidUnauthorizedException e) {

		}
	}

	@Test
	public void filterAddressesTest() {
		Addresses x = getAddressesElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, x.getAddress().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(3, x.getAddress().size());
		assertAllArePublic(x.getAddress());

		x = getAddressesElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, x.getAddress().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(2, x.getAddress().size());
		assertAllArePublic(x.getAddress());

		x = getAddressesElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, x.getAddress().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(1, x.getAddress().size());
		assertAllArePublic(x.getAddress());
	}

	@Test
	public void filterEmailsTest() {
		Emails x = getEmailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, x.getEmails().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(3, x.getEmails().size());
		assertAllArePublic(x.getEmails());

		x = getEmailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, x.getEmails().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(2, x.getEmails().size());
		assertAllArePublic(x.getEmails());

		x = getEmailsElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, x.getEmails().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(1, x.getEmails().size());
		assertAllArePublic(x.getEmails());
	}

	@Test
	public void filterExternalIdentifiersTest() {
		PersonExternalIdentifiers x = getPersonExternalIdentifiersElement(Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC);
		assertEquals(3, x.getExternalIdentifiers().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(3, x.getExternalIdentifiers().size());
		assertAllArePublic(x.getExternalIdentifiers());

		x = getPersonExternalIdentifiersElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, x.getExternalIdentifiers().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(2, x.getExternalIdentifiers().size());
		assertAllArePublic(x.getExternalIdentifiers());

		x = getPersonExternalIdentifiersElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, x.getExternalIdentifiers().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(1, x.getExternalIdentifiers().size());
		assertAllArePublic(x.getExternalIdentifiers());
	}

	@Test
	public void filterKeywordsTest() {
		Keywords x = getKeywordsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, x.getKeywords().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(3, x.getKeywords().size());
		assertAllArePublic(x.getKeywords());

		x = getKeywordsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, x.getKeywords().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(2, x.getKeywords().size());
		assertAllArePublic(x.getKeywords());

		x = getKeywordsElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, x.getKeywords().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(1, x.getKeywords().size());
		assertAllArePublic(x.getKeywords());
	}

	@Test
	public void filterOtherNamesTest() {
		OtherNames x = getOtherNamesElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, x.getOtherNames().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(3, x.getOtherNames().size());
		assertAllArePublic(x.getOtherNames());

		x = getOtherNamesElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, x.getOtherNames().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(2, x.getOtherNames().size());
		assertAllArePublic(x.getOtherNames());

		x = getOtherNamesElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, x.getOtherNames().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(1, x.getOtherNames().size());
		assertAllArePublic(x.getOtherNames());
	}

	@Test
	public void filterResearcherUrlsTest() {
		ResearcherUrls x = getResearcherUrlsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, x.getResearcherUrls().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(3, x.getResearcherUrls().size());
		assertAllArePublic(x.getResearcherUrls());

		x = getResearcherUrlsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, x.getResearcherUrls().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(2, x.getResearcherUrls().size());
		assertAllArePublic(x.getResearcherUrls());

		x = getResearcherUrlsElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, x.getResearcherUrls().size());
		publicAPISecurityManagerV2.filter(x);
		assertEquals(1, x.getResearcherUrls().size());
		assertAllArePublic(x.getResearcherUrls());
	}

	@Test
	public void filterEmploymentsTest() {
		Employments e = getEmployments(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, e.getSummaries().size());
		publicAPISecurityManagerV2.filter(e);
		assertEquals(3, e.getSummaries().size());
		assertContainerContainsOnlyPublicElements(e);

		e = getEmployments(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, e.getSummaries().size());
		publicAPISecurityManagerV2.filter(e);
		assertEquals(2, e.getSummaries().size());
		assertContainerContainsOnlyPublicElements(e);

		e = getEmployments(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, e.getSummaries().size());
		publicAPISecurityManagerV2.filter(e);
		assertEquals(1, e.getSummaries().size());
		assertContainerContainsOnlyPublicElements(e);
	}

	@Test
	public void filterEducationsTest() {
		Educations e = getEducations(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, e.getSummaries().size());
		publicAPISecurityManagerV2.filter(e);
		assertEquals(3, e.getSummaries().size());
		assertContainerContainsOnlyPublicElements(e);

		e = getEducations(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, e.getSummaries().size());
		publicAPISecurityManagerV2.filter(e);
		assertEquals(2, e.getSummaries().size());
		assertContainerContainsOnlyPublicElements(e);

		e = getEducations(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, e.getSummaries().size());
		publicAPISecurityManagerV2.filter(e);
		assertEquals(1, e.getSummaries().size());
		assertContainerContainsOnlyPublicElements(e);
	}

	@Test
	public void filterWorksTest() {
		Works w = getWorks(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, w.getWorkGroup().size());
		publicAPISecurityManagerV2.filter(w);
		assertEquals(3, w.getWorkGroup().size());
		assertGroupContainsOnlyPublicElements(w);

		w = getWorks(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, w.getWorkGroup().size());
		publicAPISecurityManagerV2.filter(w);
		assertEquals(2, w.getWorkGroup().size());
		assertGroupContainsOnlyPublicElements(w);

		w = getWorks(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, w.getWorkGroup().size());
		publicAPISecurityManagerV2.filter(w);
		assertEquals(1, w.getWorkGroup().size());
		assertGroupContainsOnlyPublicElements(w);
	}

	@Test
	public void filterFundingsTest() {
		Fundings f = getFundings(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, f.getFundingGroup().size());
		publicAPISecurityManagerV2.filter(f);
		assertEquals(3, f.getFundingGroup().size());
		assertGroupContainsOnlyPublicElements(f);

		f = getFundings(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, f.getFundingGroup().size());
		publicAPISecurityManagerV2.filter(f);
		assertEquals(2, f.getFundingGroup().size());
		assertGroupContainsOnlyPublicElements(f);

		f = getFundings(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, f.getFundingGroup().size());
		publicAPISecurityManagerV2.filter(f);
		assertEquals(1, f.getFundingGroup().size());
		assertGroupContainsOnlyPublicElements(f);
	}

	@Test
	public void filterPeerReviewsTest() {
		PeerReviews p = getPeerReviews(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		assertEquals(3, p.getPeerReviewGroup().size());
		publicAPISecurityManagerV2.filter(p);
		assertEquals(3, p.getPeerReviewGroup().size());
		assertGroupContainsOnlyPublicElements(p);

		p = getPeerReviews(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		assertEquals(3, p.getPeerReviewGroup().size());
		publicAPISecurityManagerV2.filter(p);
		assertEquals(2, p.getPeerReviewGroup().size());
		assertGroupContainsOnlyPublicElements(p);

		p = getPeerReviews(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
		assertEquals(3, p.getPeerReviewGroup().size());
		publicAPISecurityManagerV2.filter(p);
		assertEquals(1, p.getPeerReviewGroup().size());
		assertGroupContainsOnlyPublicElements(p);
	}

	@Test
	public void checkIsPublicActivitiesSummaryTest() {
		ActivitiesSummary as = getActivitiesSummaryElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(as);
		// Assert it have all activities
		assertEquals(1, as.getEducations().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEducations());
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEmployments());
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertGroupContainsOnlyPublicElements(as.getFundings());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertGroupContainsOnlyPublicElements(as.getPeerReviews());
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertGroupContainsOnlyPublicElements(as.getWorks());

		// Assert it filters educations
		as = getActivitiesSummaryElement(Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC);
		assertNull(as.getEducations());
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEmployments());
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertGroupContainsOnlyPublicElements(as.getFundings());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertGroupContainsOnlyPublicElements(as.getPeerReviews());
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertGroupContainsOnlyPublicElements(as.getWorks());

		// Assert it filters employments
		as = getActivitiesSummaryElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC);
		assertEquals(1, as.getEducations().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEducations());
		assertTrue(as.getEmployments().getSummaries().isEmpty());
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertGroupContainsOnlyPublicElements(as.getFundings());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertGroupContainsOnlyPublicElements(as.getPeerReviews());
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertGroupContainsOnlyPublicElements(as.getWorks());

		// Assert it filters funding
		as = getActivitiesSummaryElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC,
				Visibility.PUBLIC);
		assertEquals(1, as.getEducations().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEducations());
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEmployments());
		assertTrue(as.getFundings().getFundingGroup().isEmpty());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertGroupContainsOnlyPublicElements(as.getPeerReviews());
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertGroupContainsOnlyPublicElements(as.getWorks());

		// Assert it filters peer reviews
		as = getActivitiesSummaryElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED,
				Visibility.PUBLIC);
		assertEquals(1, as.getEducations().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEducations());
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEmployments());
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertGroupContainsOnlyPublicElements(as.getFundings());
		assertTrue(as.getPeerReviews().getPeerReviewGroup().isEmpty());
		assertEquals(1, as.getWorks().getWorkGroup().size());
		assertGroupContainsOnlyPublicElements(as.getWorks());

		// Assert it filters works
		as = getActivitiesSummaryElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.LIMITED);
		assertEquals(1, as.getEducations().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEducations());
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertContainerContainsOnlyPublicElements(as.getEmployments());
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertGroupContainsOnlyPublicElements(as.getFundings());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
		assertGroupContainsOnlyPublicElements(as.getPeerReviews());
		assertTrue(as.getWorks().getWorkGroup().isEmpty());
	}

	@Test
	public void checkIsPublicPersonalDetailsTest() {
		PersonalDetails p = getPersonalDetailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		assertNotNull(p.getOtherNames().getOtherNames());
		p.getOtherNames().getOtherNames().forEach(e -> {
			assertIsPublic(e);
		});

		// Should not fail, but name should be empty
		p = getPersonalDetailsElement(Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertNull(p.getName());
		assertNotNull(p.getBiography());
		assertNotNull(p.getOtherNames().getOtherNames());
		p.getOtherNames().getOtherNames().forEach(e -> {
			assertIsPublic(e);
		});

		// Should not fail, but bio should be empty
		p = getPersonalDetailsElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertNotNull(p.getName());
		assertNull(p.getBiography());
		assertNotNull(p.getOtherNames().getOtherNames());
		p.getOtherNames().getOtherNames().forEach(e -> {
			assertIsPublic(e);
		});

		p = getPersonalDetailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		publicAPISecurityManagerV2.filter(p);
		assertNotNull(p.getName());
		assertNotNull(p.getBiography());
		assertTrue(p.getOtherNames().getOtherNames().isEmpty());

	}

	@Test
	public void checkIsPublicPersonTest() {
		Person p = getPersonElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);

		// Nothing is filtered yet
		assertEquals(1, p.getAddresses().getAddress().size());
		p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		assertEquals(1, p.getEmails().getEmails().size());
		p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getKeywords().getKeywords().size());
		p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

		// Addresses filtered
		p = getPersonElement(Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		// --- filtered ---
		assertNull(p.getAddresses());
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		assertEquals(1, p.getEmails().getEmails().size());
		p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getKeywords().getKeywords().size());
		p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

		// Bio filtered
		p = getPersonElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertEquals(1, p.getAddresses().getAddress().size());
		p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
		// --- filtered ---
		assertNull(p.getBiography());
		assertEquals(1, p.getEmails().getEmails().size());
		p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getKeywords().getKeywords().size());
		p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

		// Emails filtered
		p = getPersonElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertEquals(1, p.getAddresses().getAddress().size());
		p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		// --- filtered ---
		assertNull(p.getEmails());
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getKeywords().getKeywords().size());
		p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

		// External ids filtered
		p = getPersonElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertEquals(1, p.getAddresses().getAddress().size());
		p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		assertEquals(1, p.getEmails().getEmails().size());
		p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
		// --- filtered ---
		assertNull(p.getExternalIdentifiers());
		assertEquals(1, p.getKeywords().getKeywords().size());
		p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

		// Keywords filtered
		p = getPersonElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertEquals(1, p.getAddresses().getAddress().size());
		p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		assertEquals(1, p.getEmails().getEmails().size());
		p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
		// --- filtered ---
		assertNull(p.getKeywords());
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

		// Name filtered
		p = getPersonElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertEquals(1, p.getAddresses().getAddress().size());
		p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		assertEquals(1, p.getEmails().getEmails().size());
		p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getKeywords().getKeywords().size());
		p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
		// --- filtered ---
		assertNull(p.getName());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

		// Other names filtered
		p = getPersonElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(p);
		assertEquals(1, p.getAddresses().getAddress().size());
		p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		assertEquals(1, p.getEmails().getEmails().size());
		p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getKeywords().getKeywords().size());
		p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		// --- filtered ---
		assertNull(p.getOtherNames());
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

		// Researcher urls filtered
		p = getPersonElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
		publicAPISecurityManagerV2.filter(p);
		assertEquals(1, p.getAddresses().getAddress().size());
		p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
		assertEquals(1, p.getEmails().getEmails().size());
		p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
		assertEquals(1, p.getKeywords().getKeywords().size());
		p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
		assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
		// --- filtered ---
		assertNull(p.getResearcherUrls());
	}

	@Test
	public void checkIsPublicRecordTest() {
		/**
		 * addressesVisibility bioVisibility emailsVisibility extIdsVisibility
		 * keywordsVisibility nameVisibility otherNamesVisibility
		 * rUrlsVisibility educationsVisibility employmentsVisibility
		 * worksVisibility fundingsVisibility peerReviewsVisibility
		 */

		Record r = getRecordElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(r);

		// Verify activities - nothing filtered
		ActivitiesSummary as = r.getActivitiesSummary();
		assertEquals(1, as.getEducations().getSummaries().size());
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
		assertEquals(1, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());

		// Verify bio sections - nothing filtered
		Person p = r.getPerson();
		assertEquals(1, p.getAddresses().getAddress().size());
		assertEquals(1, p.getEmails().getEmails().size());
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertEquals(1, p.getKeywords().getKeywords().size());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		assertNotNull(p.getBiography());
		assertNotNull(p.getName());

		// Filter biography, name, educations and funding
		r = getRecordElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC);
		publicAPISecurityManagerV2.filter(r);

		// Verify activities - educations and funding filtered
		as = r.getActivitiesSummary();
		assertNull(as.getEducations());
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertNull(as.getFundings());
		assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
		assertEquals(1, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());

		// Verify bio sections - bio and name filtered
		p = r.getPerson();
		assertEquals(1, p.getAddresses().getAddress().size());
		assertEquals(1, p.getEmails().getEmails().size());
		assertEquals(1, p.getExternalIdentifiers().getExternalIdentifiers().size());
		assertEquals(1, p.getKeywords().getKeywords().size());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		assertNull(p.getBiography());
		assertNull(p.getName());
		
		// Filter emails, external identifiers, peer reviews and works
		r = getRecordElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED, Visibility.LIMITED,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC,
				Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED, Visibility.LIMITED);
		publicAPISecurityManagerV2.filter(r);
		
		// Verify activities - peer reviews and works filtered
		as = r.getActivitiesSummary();
		assertEquals(1, as.getEducations().getSummaries().size());
		assertEquals(1, as.getEmployments().getSummaries().size());
		assertEquals(1, as.getFundings().getFundingGroup().size());
		assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
		assertNull(as.getPeerReviews());
		assertNull(as.getWorks());

		// Verify bio sections - emails, external identifiers filtered
		p = r.getPerson();
		assertEquals(1, p.getAddresses().getAddress().size());
		assertNull(p.getEmails());
		assertNull(p.getExternalIdentifiers());
		assertEquals(1, p.getKeywords().getKeywords().size());
		assertEquals(1, p.getOtherNames().getOtherNames().size());
		assertEquals(1, p.getResearcherUrls().getResearcherUrls().size());
		assertNotNull(p.getBiography());
		assertNotNull(p.getName());
	}

	private Filterable getFilterableElement(Visibility v) {
		EducationSummary s = new EducationSummary();
		s.setVisibility(v);
		return s;
	}

	private VisibilityType getVisibilityTypeElement(Visibility v) {
		EducationSummary s = new EducationSummary();
		s.setVisibility(v);
		return s;
	}

	private Employments getEmployments(Visibility... vs) {
		Employments e = new Employments();
		for (Visibility v : vs) {
			EmploymentSummary s = new EmploymentSummary();
			s.setVisibility(v);
			e.getSummaries().add(s);
		}
		return e;
	}

	private Educations getEducations(Visibility... vs) {
		Educations e = new Educations();
		for (Visibility v : vs) {
			EducationSummary s = new EducationSummary();
			s.setVisibility(v);
			e.getSummaries().add(s);
		}
		return e;
	}

	private Works getWorks(Visibility... vs) {
		Works works = new Works();
		for (Visibility v : vs) {
			WorkGroup g = new WorkGroup();
			WorkSummary s = new WorkSummary();
			s.setVisibility(v);
			g.getWorkSummary().add(s);
			works.getWorkGroup().add(g);
		}
		return works;
	}

	private Fundings getFundings(Visibility... vs) {
		Fundings fundings = new Fundings();
		for (Visibility v : vs) {
			FundingGroup g = new FundingGroup();
			FundingSummary s = new FundingSummary();
			s.setVisibility(v);
			g.getFundingSummary().add(s);
			fundings.getFundingGroup().add(g);
		}
		return fundings;
	}

	private PeerReviews getPeerReviews(Visibility... vs) {
		PeerReviews peerReviews = new PeerReviews();
		for (Visibility v : vs) {
			PeerReviewGroup g = new PeerReviewGroup();
			PeerReviewSummary s = new PeerReviewSummary();
			s.setVisibility(v);
			g.getPeerReviewSummary().add(s);
			peerReviews.getPeerReviewGroup().add(g);
		}
		return peerReviews;
	}

	private ActivitiesSummary getActivitiesSummaryElement() {
		ActivitiesSummary s = new ActivitiesSummary();
		s.setEducations(getEducations(Visibility.PUBLIC));
		s.setEmployments(getEmployments(Visibility.PUBLIC));
		s.setFundings(getFundings(Visibility.PUBLIC));
		s.setPeerReviews(getPeerReviews(Visibility.PUBLIC));
		s.setWorks(getWorks(Visibility.PUBLIC));
		return s;
	}

	private void setVisibility(ActivitiesSummary as, Visibility v, Class c) {
		///TODO!!!!
		if(c.isAssignableFrom(Educations.class)) {
			
		} else if(c.isAssignableFrom(Employments.class)) {
			
		} else if(c.isAssignableFrom(Fundings.class)) {
			
		} else if(c.isAssignableFrom(Works.class)) {
			
		} else if(c.isAssignableFrom(PeerReviews.class)) {
			
		}
	}
	
	private OtherNames getOtherNamesElement(Visibility... vs) {
		OtherNames otherNames = new OtherNames();
		for (Visibility v : vs) {
			OtherName o = new OtherName();
			o.setVisibility(v);
			if (otherNames.getOtherNames() == null) {
				otherNames.setOtherNames(new ArrayList<OtherName>());
			}
			otherNames.getOtherNames().add(o);
		}
		return otherNames;
	}

	private Addresses getAddressesElement(Visibility... vs) {
		Addresses elements = new Addresses();
		for (Visibility v : vs) {
			Address element = new Address();
			element.setVisibility(v);
			if (elements.getAddress() == null) {
				elements.setAddress(new ArrayList<Address>());
			}
			elements.getAddress().add(element);
		}
		return elements;
	}

	private Emails getEmailsElement(Visibility... vs) {
		Emails elements = new Emails();
		for (Visibility v : vs) {
			Email element = new Email();
			element.setVisibility(v);
			if (elements.getEmails() == null) {
				elements.setEmails(new ArrayList<Email>());
			}
			elements.getEmails().add(element);
		}
		return elements;
	}

	private PersonExternalIdentifiers getPersonExternalIdentifiersElement(Visibility... vs) {
		PersonExternalIdentifiers elements = new PersonExternalIdentifiers();
		for (Visibility v : vs) {
			PersonExternalIdentifier element = new PersonExternalIdentifier();
			element.setVisibility(v);
			if (elements.getExternalIdentifiers() == null) {
				elements.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>());
			}
			elements.getExternalIdentifiers().add(element);
		}
		return elements;
	}

	private Keywords getKeywordsElement(Visibility... vs) {
		Keywords elements = new Keywords();
		for (Visibility v : vs) {
			Keyword element = new Keyword();
			element.setVisibility(v);
			if (elements.getKeywords() == null) {
				elements.setKeywords(new ArrayList<Keyword>());
			}
			elements.getKeywords().add(element);
		}
		return elements;
	}

	private ResearcherUrls getResearcherUrlsElement(Visibility... vs) {
		ResearcherUrls elements = new ResearcherUrls();
		for (Visibility v : vs) {
			ResearcherUrl element = new ResearcherUrl();
			element.setVisibility(v);
			if (elements.getResearcherUrls() == null) {
				elements.setResearcherUrls(new ArrayList<ResearcherUrl>());
			}
			elements.getResearcherUrls().add(element);
		}
		return elements;
	}

	private PersonalDetails getPersonalDetailsElement(Visibility nameVisibility, Visibility bioVisiblity,
			Visibility otherNamesVisibility) {
		PersonalDetails p = new PersonalDetails();
		Name name = new Name();
		name.setVisibility(nameVisibility);
		p.setName(name);
		Biography bio = new Biography();
		bio.setVisibility(bioVisiblity);
		p.setBiography(bio);
		p.setOtherNames(getOtherNamesElement(otherNamesVisibility));
		return p;
	}

	private Person getPersonElement(Visibility addressesVisibility, Visibility bioVisibility,
			Visibility emailsVisibility, Visibility extIdsVisibility, Visibility keywordsVisibility,
			Visibility nameVisibility, Visibility otherNamesVisibility, Visibility rUrlsVisibility) {
		Person p = new Person();
		p.setAddresses(getAddressesElement(addressesVisibility));
		p.setEmails(getEmailsElement(emailsVisibility));
		p.setExternalIdentifiers(getPersonExternalIdentifiersElement(extIdsVisibility));
		p.setKeywords(getKeywordsElement(keywordsVisibility));
		p.setOtherNames(getOtherNamesElement(otherNamesVisibility));
		p.setResearcherUrls(getResearcherUrlsElement(rUrlsVisibility));

		Name name = new Name();
		name.setVisibility(nameVisibility);
		p.setName(name);

		Biography b = new Biography();
		b.setVisibility(bioVisibility);
		p.setBiography(b);

		return p;
	}

	private Record getRecordElement(Visibility addressesVisibility, Visibility bioVisibility,
			Visibility emailsVisibility, Visibility extIdsVisibility, Visibility keywordsVisibility,
			Visibility nameVisibility, Visibility otherNamesVisibility, Visibility rUrlsVisibility,
			Visibility educationsVisibility, Visibility employmentsVisibility, Visibility fundingsVisibility, Visibility worksVisibility, Visibility peerReviewsVisibility) {
		Record r = new Record();
		r.setActivitiesSummary(getActivitiesSummaryElement(employmentsVisibility, educationsVisibility, worksVisibility,
				fundingsVisibility, peerReviewsVisibility));
		r.setPerson(getPersonElement(addressesVisibility, bioVisibility, emailsVisibility, extIdsVisibility,
				keywordsVisibility, nameVisibility, otherNamesVisibility, rUrlsVisibility));
		return r;
	}

	private void assertIsPublic(Filterable a) {
		assertEquals(Visibility.PUBLIC, a.getVisibility());
	}

	private void assertAllArePublic(List<? extends Filterable> list) {
		if (list == null) {
			return;
		}
		list.forEach(e -> {
			assertIsPublic(e);
		});
	}

	private void assertGroupContainsOnlyPublicElements(GroupsContainer countainer) {
		countainer.retrieveGroups().forEach(x -> {
			assertNotNull(x.getActivities());
			x.getActivities().forEach(e -> {
				assertIsPublic(e);
			});
		});
	}

	private void assertContainerContainsOnlyPublicElements(ActivitiesContainer countainer) {
		countainer.retrieveActivities().forEach(x -> {
			assertIsPublic(x);
		});
	}
}
