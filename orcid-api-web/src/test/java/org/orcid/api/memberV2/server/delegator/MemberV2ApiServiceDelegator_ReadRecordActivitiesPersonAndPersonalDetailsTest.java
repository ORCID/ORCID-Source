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
package org.orcid.api.memberV2.server.delegator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc4.Iso3166Country;
import org.orcid.jaxb.model.common_rc4.LastModifiedDate;
import org.orcid.jaxb.model.common_rc4.OrcidIdentifier;
import org.orcid.jaxb.model.common_rc4.Title;
import org.orcid.jaxb.model.common_rc4.TranslatedTitle;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc4.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc4.Works;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Biography;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.FundingType;
import org.orcid.jaxb.model.record_rc4.History;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.PeerReview;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkBulk;
import org.orcid.jaxb.model.record_rc4.WorkTitle;
import org.orcid.jaxb.model.record_rc4.WorkType;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.test.helper.Utils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegator_ReadRecordActivitiesPersonAndPersonalDetailsTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV2ApiServiceDelegator")
    protected MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @Mock
    private ProfileLastModifiedAspect profileLastModifiedAspect;

    @Resource
    private AddressManager addressManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(addressManager, "profileLastModifiedAspect", profileLastModifiedAspect);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testViewRecordWrongScope() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response response = serviceDelegator.viewRecord(ORCID);
        // Verify everything inside is public
        Record record = (Record) response.getEntity();
        assertNotNull(record);
        Utils.assertIsPublicOrSource(record.getActivitiesSummary(), SecurityContextTestUtils.DEFAULT_CLIENT_ID);
        Utils.assertIsPublicOrSource(record.getPerson(), SecurityContextTestUtils.DEFAULT_CLIENT_ID);
    }

    @Test
    public void testViewRecordReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) r.getEntity();
        assertNotNull(record);
        Utils.assertIsPublicOrSource(record.getActivitiesSummary(), "APP-5555555555555555");
        Utils.assertIsPublicOrSource(record.getPerson(), "APP-5555555555555555");
    }

    @Test
    public void testViewActivitiesReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
        assertNotNull(as);
        Utils.assertIsPublicOrSource(as, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewRecordWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewRecord(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewActivitiesWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewActivities(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonalDetailsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPersonalDetails(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPersonWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPerson(ORCID);
    }

    @Test
    public void testViewActitivies() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4446");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getPath());
        Utils.verifyLastModified(summary.getLastModifiedDate());
        // Check works
        assertNotNull(summary.getWorks());
        Utils.verifyLastModified(summary.getWorks().getLastModifiedDate());
        assertEquals(3, summary.getWorks().getWorkGroup().size());
        boolean foundPrivateWork = false;
        for (WorkGroup group : summary.getWorks().getWorkGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getWorkSummary());
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary work = group.getWorkSummary().get(0);
            Utils.verifyLastModified(work.getLastModifiedDate());
            assertThat(work.getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(6)), is(Long.valueOf(7))));
            assertThat(work.getPath(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
            assertThat(work.getTitle().getTitle().getContent(), anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
            if (work.getPutCode().equals(Long.valueOf(7))) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
                foundPrivateWork = true;
            }
        }
        assertTrue(foundPrivateWork);

        // Check fundings
        assertNotNull(summary.getFundings());
        Utils.verifyLastModified(summary.getFundings().getLastModifiedDate());
        assertEquals(3, summary.getFundings().getFundingGroup().size());
        boolean foundPrivateFunding = false;
        for (FundingGroup group : summary.getFundings().getFundingGroup()) {
            assertNotNull(group.getFundingSummary());
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary funding = group.getFundingSummary().get(0);
            Utils.verifyLastModified(funding.getLastModifiedDate());
            assertThat(funding.getPutCode(), anyOf(is(Long.valueOf(4)), is(Long.valueOf(5)), is(Long.valueOf(8))));
            assertThat(funding.getPath(), anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5"), is("/4444-4444-4444-4446/funding/8")));
            assertThat(funding.getTitle().getTitle().getContent(), anyOf(is("Private Funding"), is("Public Funding"), is("Limited Funding")));
            if (funding.getPutCode().equals(4L)) {
                assertEquals(Visibility.PRIVATE, funding.getVisibility());
                foundPrivateFunding = true;
            }
        }

        assertTrue(foundPrivateFunding);

        // Check Educations
        assertNotNull(summary.getEducations());
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(3, summary.getEducations().getSummaries().size());

        boolean foundPrivateEducation = false;
        for (EducationSummary education : summary.getEducations().getSummaries()) {
            Utils.verifyLastModified(education.getLastModifiedDate());
            assertThat(education.getPutCode(), anyOf(is(Long.valueOf(6)), is(Long.valueOf(7)), is(Long.valueOf(9))));
            assertThat(education.getPath(),
                    anyOf(is("/4444-4444-4444-4446/education/6"), is("/4444-4444-4444-4446/education/7"), is("/4444-4444-4444-4446/education/9")));
            assertThat(education.getDepartmentName(), anyOf(is("Education Dept # 1"), is("Education Dept # 2"), is("Education Dept # 3")));

            if (education.getPutCode().equals(6L)) {
                assertEquals(Visibility.PRIVATE, education.getVisibility());
                foundPrivateEducation = true;
            }
        }

        assertTrue(foundPrivateEducation);

        // Check Employments
        assertNotNull(summary.getEmployments());
        Utils.verifyLastModified(summary.getEmployments().getLastModifiedDate());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(3, summary.getEmployments().getSummaries().size());

        boolean foundPrivateEmployment = false;

        for (EmploymentSummary employment : summary.getEmployments().getSummaries()) {
            Utils.verifyLastModified(employment.getLastModifiedDate());
            assertThat(employment.getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(8)), is(Long.valueOf(11))));
            assertThat(employment.getPath(),
                    anyOf(is("/4444-4444-4444-4446/employment/5"), is("/4444-4444-4444-4446/employment/8"), is("/4444-4444-4444-4446/employment/11")));
            assertThat(employment.getDepartmentName(), anyOf(is("Employment Dept # 1"), is("Employment Dept # 2"), is("Employment Dept # 4")));
            if (employment.getPutCode().equals(5L)) {
                assertEquals(Visibility.PRIVATE, employment.getVisibility());
                foundPrivateEmployment = true;
            }
        }

        assertTrue(foundPrivateEmployment);

        // Check Peer reviews
        assertNotNull(summary.getPeerReviews());
        Utils.verifyLastModified(summary.getPeerReviews().getLastModifiedDate());
        assertEquals(3, summary.getPeerReviews().getPeerReviewGroup().size());

        boolean foundPrivatePeerReview = false;
        for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            assertNotNull(group.getPeerReviewSummary());
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary peerReview = group.getPeerReviewSummary().get(0);
            Utils.verifyLastModified(peerReview.getLastModifiedDate());
            assertThat(peerReview.getPutCode(), anyOf(is(Long.valueOf(1)), is(Long.valueOf(3)), is(Long.valueOf(4))));
            assertThat(peerReview.getGroupId(), anyOf(is("issn:0000001"), is("issn:0000002"), is("issn:0000003")));
            if (peerReview.getPutCode().equals(4L)) {
                assertEquals(Visibility.PRIVATE, peerReview.getVisibility());
                foundPrivatePeerReview = true;
            }
        }

        assertTrue(foundPrivatePeerReview);
    }

    @Test
    public void testReadPublicScope_Activities() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);

        // Check that lists returns only PUBLIC elements
        /**
         * ACTIVITIES
         */
        try {
            // Check you get only public activities
            Response r = serviceDelegator.viewActivities(ORCID);
            ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
            testActivities(as, ORCID);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testReadPublicScope_Record() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        testPerson(record.getPerson(), ORCID);
        testActivities(record.getActivitiesSummary(), ORCID);
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());
        assertNotNull(record.getPreferences());
        assertEquals(Locale.EN, record.getPreferences().getLocale());
        History history = record.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        Utils.verifyLastModified(history.getLastModifiedDate());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());
        assertNotNull(record.getOrcidIdentifier());
        OrcidIdentifier id = record.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());
    }

    @Test
    public void testViewPersonalDetailsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPersonalDetails(ORCID);
        PersonalDetails element = (PersonalDetails) r.getEntity();
        assertNotNull(element);
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPersonReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPerson(ORCID);
        Person element = (Person) r.getEntity();
        assertNotNull(element);
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewRecord() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        assertNotNull(record);
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());
        assertNotNull(record.getPreferences());
        assertEquals(Locale.EN, record.getPreferences().getLocale());
        History history = record.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        Utils.verifyLastModified(history.getLastModifiedDate());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());
        assertNotNull(record.getOrcidIdentifier());
        OrcidIdentifier id = record.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());
        // Validate person
        Person person = record.getPerson();
        assertNotNull(person);
        Utils.verifyLastModified(person.getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        assertNotNull(person.getAddresses());
        Utils.verifyLastModified(person.getAddresses().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/address", person.getAddresses().getPath());
        assertEquals(4, person.getAddresses().getAddress().size());
        for (Address address : person.getAddresses().getAddress()) {
            Utils.verifyLastModified(address.getLastModifiedDate());
            if (address.getPutCode().equals(Long.valueOf(9))) {
                assertEquals(Iso3166Country.US, address.getCountry().getValue());
                assertEquals(Long.valueOf(0), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), address.getVisibility().value());
            } else if (address.getPutCode().equals(Long.valueOf(10))) {
                assertEquals(Iso3166Country.CR, address.getCountry().getValue());
                assertEquals(Long.valueOf(1), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/10", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), address.getVisibility().value());
            } else if (address.getPutCode().equals(Long.valueOf(11))) {
                assertEquals(Iso3166Country.GB, address.getCountry().getValue());
                assertEquals(Long.valueOf(2), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/11", address.getPath());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), address.getVisibility().value());
            } else if (address.getPutCode().equals(Long.valueOf(12))) {
                assertEquals(Iso3166Country.MX, address.getCountry().getValue());
                assertEquals(Long.valueOf(3), address.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/address/12", address.getPath());
                assertEquals("0000-0000-0000-0003", address.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), address.getVisibility().value());
            } else {
                fail("Invalid address found with put code: " + address.getPutCode());
            }
        }

        assertNotNull(person.getBiography());
        Utils.verifyLastModified(person.getBiography().getLastModifiedDate());
        assertEquals(Visibility.PUBLIC.value(), person.getBiography().getVisibility().value());
        assertEquals("Biography for 0000-0000-0000-0003", person.getBiography().getContent());

        assertNotNull(person.getEmails());
        Utils.verifyLastModified(person.getEmails().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/email", person.getEmails().getPath());
        assertEquals(4, person.getEmails().getEmails().size());
        for (Email email : person.getEmails().getEmails()) {
            Utils.verifyLastModified(email.getLastModifiedDate());
            if (email.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), email.getVisibility().value());
            } else if (email.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), email.getVisibility().value());
            } else if (email.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), email.getVisibility().value());
            } else if (email.getEmail().equals("self_limited_0000-0000-0000-0003@test.orcid.org")) {
                assertEquals("0000-0000-0000-0003", email.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), email.getVisibility().value());
            } else {
                fail("Invalid email found: " + email.getEmail());
            }
        }

        assertNotNull(person.getExternalIdentifiers());
        Utils.verifyLastModified(person.getExternalIdentifiers().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/external-identifiers", person.getExternalIdentifiers().getPath());
        assertEquals(4, person.getExternalIdentifiers().getExternalIdentifiers().size());
        for (PersonExternalIdentifier extId : person.getExternalIdentifiers().getExternalIdentifiers()) {
            Utils.verifyLastModified(extId.getLastModifiedDate());
            if (extId.getPutCode().equals(Long.valueOf(13))) {
                assertEquals(Long.valueOf(0), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("public_type", extId.getType());
                assertEquals("http://ext-id/public_ref", extId.getUrl().getValue());
                assertEquals("public_ref", extId.getValue());
                assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
            } else if (extId.getPutCode().equals(Long.valueOf(14))) {
                assertEquals(Long.valueOf(1), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/14", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("limited_type", extId.getType());
                assertEquals("http://ext-id/limited_ref", extId.getUrl().getValue());
                assertEquals("limited_ref", extId.getValue());
                assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
            } else if (extId.getPutCode().equals(Long.valueOf(15))) {
                assertEquals(Long.valueOf(2), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/15", extId.getPath());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("private_type", extId.getType());
                assertEquals("http://ext-id/private_ref", extId.getUrl().getValue());
                assertEquals("private_ref", extId.getValue());
                assertEquals(Visibility.PRIVATE.value(), extId.getVisibility().value());
            } else if (extId.getPutCode().equals(Long.valueOf(16))) {
                assertEquals(Long.valueOf(3), extId.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/external-identifiers/16", extId.getPath());
                assertEquals("0000-0000-0000-0003", extId.getSource().retrieveSourcePath());
                assertEquals("self_limited_type", extId.getType());
                assertEquals("http://ext-id/self/limited", extId.getUrl().getValue());
                assertEquals("self_limited_ref", extId.getValue());
                assertEquals(Visibility.LIMITED.value(), extId.getVisibility().value());
            } else {
                fail("Invalid external identifier found: " + extId.getPutCode());
            }
        }

        assertNotNull(person.getKeywords());
        Utils.verifyLastModified(person.getKeywords().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/keywords", person.getKeywords().getPath());
        assertEquals(4, person.getKeywords().getKeywords().size());
        for (Keyword keyword : person.getKeywords().getKeywords()) {
            Utils.verifyLastModified(keyword.getLastModifiedDate());
            if (keyword.getPutCode().equals(Long.valueOf(9))) {
                assertEquals("PUBLIC", keyword.getContent());
                assertEquals(Long.valueOf(0), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
            } else if (keyword.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("LIMITED", keyword.getContent());
                assertEquals(Long.valueOf(1), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/10", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), keyword.getVisibility().value());
            } else if (keyword.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("PRIVATE", keyword.getContent());
                assertEquals(Long.valueOf(2), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/11", keyword.getPath());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), keyword.getVisibility().value());
            } else if (keyword.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("SELF LIMITED", keyword.getContent());
                assertEquals(Long.valueOf(3), keyword.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/keywords/12", keyword.getPath());
                assertEquals("0000-0000-0000-0003", keyword.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), keyword.getVisibility().value());
            } else {
                fail("Invalid keyword found: " + keyword.getPutCode());
            }
        }

        assertNotNull(person.getName());
        Utils.verifyLastModified(person.getName().getLastModifiedDate());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), person.getName().getVisibility().value());

        assertNotNull(person.getOtherNames());
        Utils.verifyLastModified(person.getOtherNames().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/other-names", person.getOtherNames().getPath());
        assertEquals(4, person.getOtherNames().getOtherNames().size());
        for (OtherName otherName : person.getOtherNames().getOtherNames()) {
            Utils.verifyLastModified(otherName.getLastModifiedDate());
            if (otherName.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("Other Name PUBLIC", otherName.getContent());
                assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("Other Name LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/14", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(15))) {
                assertEquals("Other Name PRIVATE", otherName.getContent());
                assertEquals(Long.valueOf(2), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/15", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(16))) {
                assertEquals("Other Name SELF LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(3), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/16", otherName.getPath());
                assertEquals("0000-0000-0000-0003", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else {
                fail("Invalid other name found: " + otherName.getPutCode());
            }
        }

        assertNotNull(person.getResearcherUrls());
        Utils.verifyLastModified(person.getResearcherUrls().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/researcher-urls", person.getResearcherUrls().getPath());
        assertEquals(4, person.getResearcherUrls().getResearcherUrls().size());
        for (ResearcherUrl rUrl : person.getResearcherUrls().getResearcherUrls()) {
            Utils.verifyLastModified(rUrl.getLastModifiedDate());
            if (rUrl.getPutCode().equals(Long.valueOf(13))) {
                assertEquals(Long.valueOf(0), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=13", rUrl.getUrl().getValue());
                assertEquals("public_rurl", rUrl.getUrlName());
                assertEquals(Visibility.PUBLIC.value(), rUrl.getVisibility().value());
            } else if (rUrl.getPutCode().equals(Long.valueOf(14))) {
                assertEquals(Long.valueOf(1), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/14", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=14", rUrl.getUrl().getValue());
                assertEquals("limited_rurl", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED.value(), rUrl.getVisibility().value());
            } else if (rUrl.getPutCode().equals(Long.valueOf(15))) {
                assertEquals(Long.valueOf(2), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/15", rUrl.getPath());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=15", rUrl.getUrl().getValue());
                assertEquals("private_rurl", rUrl.getUrlName());
                assertEquals(Visibility.PRIVATE.value(), rUrl.getVisibility().value());
            } else if (rUrl.getPutCode().equals(Long.valueOf(16))) {
                assertEquals(Long.valueOf(3), rUrl.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/researcher-urls/16", rUrl.getPath());
                assertEquals("0000-0000-0000-0003", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://www.researcherurl.com?id=16", rUrl.getUrl().getValue());
                assertEquals("self_limited_rurl", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED.value(), rUrl.getVisibility().value());
            } else {
                fail("Invalid researcher url found: " + rUrl.getPutCode());
            }
        }

        // Validate activities
        ActivitiesSummary activities = record.getActivitiesSummary();
        assertNotNull(activities);
        Utils.verifyLastModified(activities.getLastModifiedDate());

        assertNotNull(activities.getEducations());
        Utils.verifyLastModified(activities.getEducations().getLastModifiedDate());
        assertEquals(4, activities.getEducations().getSummaries().size());
        for (EducationSummary education : activities.getEducations().getSummaries()) {
            Utils.verifyLastModified(education.getLastModifiedDate());
            assertNotNull(education.getStartDate());
            assertEquals("2016", education.getStartDate().getYear().getValue());
            assertEquals("04", education.getStartDate().getMonth().getValue());
            assertEquals("01", education.getStartDate().getDay().getValue());
            assertNotNull(education.getEndDate());
            assertEquals("2030", education.getEndDate().getYear().getValue());
            assertEquals("01", education.getEndDate().getMonth().getValue());
            assertEquals("01", education.getEndDate().getDay().getValue());
            if (education.getPutCode().equals(Long.valueOf(20))) {
                assertEquals("PUBLIC Department", education.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
                assertEquals("PUBLIC", education.getRoleTitle());
                assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
            } else if (education.getPutCode().equals(Long.valueOf(21))) {
                assertEquals("LIMITED Department", education.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/education/21", education.getPath());
                assertEquals("LIMITED", education.getRoleTitle());
                assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
            } else if (education.getPutCode().equals(Long.valueOf(22))) {
                assertEquals("PRIVATE Department", education.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/education/22", education.getPath());
                assertEquals("PRIVATE", education.getRoleTitle());
                assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), education.getVisibility().value());
            } else if (education.getPutCode().equals(Long.valueOf(25))) {
                assertEquals("SELF LIMITED Department", education.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/education/25", education.getPath());
                assertEquals("SELF LIMITED", education.getRoleTitle());
                assertEquals("0000-0000-0000-0003", education.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
            } else {
                fail("Invalid education found: " + education.getPutCode());
            }
        }

        assertNotNull(activities.getEmployments());
        Utils.verifyLastModified(activities.getEmployments().getLastModifiedDate());
        assertEquals(4, activities.getEmployments().getSummaries().size());
        for (EmploymentSummary employment : activities.getEmployments().getSummaries()) {
            Utils.verifyLastModified(employment.getLastModifiedDate());
            if (employment.getPutCode().equals(Long.valueOf(17))) {
                assertEquals("PUBLIC Department", employment.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/employment/17", employment.getPath());
                assertEquals("PUBLIC", employment.getRoleTitle());
                assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), employment.getVisibility().value());
            } else if (employment.getPutCode().equals(Long.valueOf(18))) {
                assertEquals("LIMITED Department", employment.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/employment/18", employment.getPath());
                assertEquals("LIMITED", employment.getRoleTitle());
                assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
            } else if (employment.getPutCode().equals(Long.valueOf(19))) {
                assertEquals("PRIVATE Department", employment.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/employment/19", employment.getPath());
                assertEquals("PRIVATE", employment.getRoleTitle());
                assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
            } else if (employment.getPutCode().equals(Long.valueOf(23))) {
                assertEquals("SELF LIMITED Department", employment.getDepartmentName());
                assertEquals("/0000-0000-0000-0003/employment/23", employment.getPath());
                assertEquals("SELF LIMITED", employment.getRoleTitle());
                assertEquals("0000-0000-0000-0003", employment.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
            } else {
                fail("Invalid employment found: " + employment.getPutCode());
            }
        }

        assertNotNull(activities.getFundings());
        Utils.verifyLastModified(activities.getFundings().getLastModifiedDate());
        assertEquals(4, activities.getFundings().getFundingGroup().size());
        for (FundingGroup group : activities.getFundings().getFundingGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getFundingSummary());
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary funding = group.getFundingSummary().get(0);
            Utils.verifyLastModified(funding.getLastModifiedDate());
            if (funding.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("0", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("1", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/0000-0000-0000-0003/funding/10", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("PUBLIC", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
            } else if (funding.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("1", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("2", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/0000-0000-0000-0003/funding/11", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("LIMITED", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
            } else if (funding.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("2", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("3", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/0000-0000-0000-0003/funding/12", funding.getPath());
                assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
                assertEquals("PRIVATE", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
            } else if (funding.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("3", funding.getDisplayIndex());
                assertNotNull(funding.getExternalIdentifiers());
                assertEquals(1, funding.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("4", funding.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("/0000-0000-0000-0003/funding/13", funding.getPath());
                assertEquals("0000-0000-0000-0003", funding.getSource().retrieveSourcePath());
                assertEquals("SELF LIMITED", funding.getTitle().getTitle().getContent());
                assertEquals(FundingType.SALARY_AWARD.value(), funding.getType().value());
                assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
            } else {
                fail("Invalid funding found: " + funding.getPutCode());
            }
        }

        assertNotNull(activities.getPeerReviews());
        Utils.verifyLastModified(activities.getPeerReviews().getLastModifiedDate());
        assertEquals(4, activities.getPeerReviews().getPeerReviewGroup().size());
        for (PeerReviewGroup group : activities.getPeerReviews().getPeerReviewGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getPeerReviewSummary());
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary peerReview = group.getPeerReviewSummary().get(0);
            Utils.verifyLastModified(peerReview.getLastModifiedDate());
            assertNotNull(peerReview.getCompletionDate());
            assertEquals("2016", peerReview.getCompletionDate().getYear().getValue());
            assertEquals("02", peerReview.getCompletionDate().getMonth().getValue());
            assertEquals("02", peerReview.getCompletionDate().getDay().getValue());
            assertNotNull(peerReview.getExternalIdentifiers());
            assertEquals(1, peerReview.getExternalIdentifiers().getExternalIdentifier().size());
            assertEquals("agr", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
            if (peerReview.getPutCode().equals(Long.valueOf(9))) {
                assertEquals("0", peerReview.getDisplayIndex());
                assertEquals("issn:0000009", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/9", peerReview.getPath());
                assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), peerReview.getVisibility().value());
            } else if (peerReview.getPutCode().equals(Long.valueOf(10))) {
                assertEquals("1", peerReview.getDisplayIndex());
                assertEquals("issn:0000010", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/10", peerReview.getPath());
                assertEquals("work:external-identifier-id#2", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), peerReview.getVisibility().value());
            } else if (peerReview.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("2", peerReview.getDisplayIndex());
                assertEquals("issn:0000011", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/11", peerReview.getPath());
                assertEquals("work:external-identifier-id#3", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), peerReview.getVisibility().value());
            } else if (peerReview.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("3", peerReview.getDisplayIndex());
                assertEquals("issn:0000012", peerReview.getGroupId());
                assertEquals("/0000-0000-0000-0003/peer-review/12", peerReview.getPath());
                assertEquals("work:external-identifier-id#4", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("0000-0000-0000-0003", peerReview.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), peerReview.getVisibility().value());
            } else {
                fail("Invalid peer review found: " + peerReview.getPutCode());
            }
        }

        assertNotNull(activities.getWorks());
        Utils.verifyLastModified(activities.getWorks().getLastModifiedDate());
        assertEquals(4, activities.getWorks().getWorkGroup().size());
        for (WorkGroup group : activities.getWorks().getWorkGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getWorkSummary());
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary work = group.getWorkSummary().get(0);
            Utils.verifyLastModified(work.getLastModifiedDate());
            if (work.getPutCode().equals(Long.valueOf(11))) {
                assertEquals("0", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("1", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("PUBLIC", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.PUBLIC.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
            } else if (work.getPutCode().equals(Long.valueOf(12))) {
                assertEquals("1", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("2", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("LIMITED", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/12", work.getPath());
            } else if (work.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("2", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("3", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
                assertEquals("PRIVATE", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.PRIVATE.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/13", work.getPath());
            } else if (work.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("3", work.getDisplayIndex());
                assertNotNull(work.getExternalIdentifiers());
                assertEquals(1, work.getExternalIdentifiers().getExternalIdentifier().size());
                assertEquals("doi", work.getExternalIdentifiers().getExternalIdentifier().get(0).getType());
                assertEquals("4", work.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                assertEquals("2016", work.getPublicationDate().getYear().getValue());
                assertEquals("01", work.getPublicationDate().getMonth().getValue());
                assertEquals("01", work.getPublicationDate().getDay().getValue());
                assertEquals("0000-0000-0000-0003", work.getSource().retrieveSourcePath());
                assertEquals("SELF LIMITED", work.getTitle().getTitle().getContent());
                assertEquals(WorkType.JOURNAL_ARTICLE.value(), work.getType().value());
                assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
                assertEquals("/0000-0000-0000-0003/work/14", work.getPath());
            } else {
                fail("Invalid work found: " + work.getPutCode());
            }
        }
    }

    @Test
    public void testReadPublicScope_PersonalDetails() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        PersonalDetails p = (PersonalDetails) r.getEntity();
        Utils.verifyLastModified(p.getLastModifiedDate());
        Utils.verifyLastModified(p.getBiography().getLastModifiedDate());
        Utils.verifyLastModified(p.getName().getLastModifiedDate());
        Utils.verifyLastModified(p.getOtherNames().getLastModifiedDate());
        assertEquals("Biography for 0000-0000-0000-0003", p.getBiography().getContent());
        assertEquals("Credit Name", p.getName().getCreditName().getContent());
        assertEquals("Given Names", p.getName().getGivenNames().getContent());
        assertEquals("Family Name", p.getName().getFamilyName().getContent());
        assertEquals(3, p.getOtherNames().getOtherNames().size());

        boolean found13 = false, found14 = false, found15 = false;
        for (OtherName element : p.getOtherNames().getOtherNames()) {
            if (element.getPutCode() == 13) {
                found13 = true;
            } else if (element.getPutCode() == 14) {
                found14 = true;
            } else if (element.getPutCode() == 15) {
                found15 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found13);
        assertTrue(found14);
        assertTrue(found15);

        String otherOrcid = "0000-0000-0000-0002";
        SecurityContextTestUtils.setUpSecurityContext(otherOrcid, ScopePathType.READ_PUBLIC);
        r = serviceDelegator.viewPersonalDetails(otherOrcid);
        assertNotNull(r);
        assertEquals(PersonalDetails.class.getName(), r.getEntity().getClass().getName());
        p = (PersonalDetails) r.getEntity();
        assertNull(p.getBiography());
        assertNull(p.getName());
        assertNull(p.getOtherNames());
    }

    @Test
    public void testReadPublicScope_Person() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewPerson(ORCID);
        assertNotNull(r);
        assertEquals(Person.class.getName(), r.getEntity().getClass().getName());
        Person p = (Person) r.getEntity();
        testPerson(p, ORCID);
    }

    @Test
    public void testViewPersonalDetails() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(response);
        PersonalDetails personalDetails = (PersonalDetails) response.getEntity();
        assertNotNull(personalDetails);
        Utils.verifyLastModified(personalDetails.getLastModifiedDate());
        assertNotNull(personalDetails.getBiography());
        Utils.verifyLastModified(personalDetails.getBiography().getLastModifiedDate());
        assertEquals("Biography for 0000-0000-0000-0003", personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        assertEquals("/0000-0000-0000-0003/biography", personalDetails.getBiography().getPath());
        assertNotNull(personalDetails.getName());
        Utils.verifyLastModified(personalDetails.getName().getLastModifiedDate());
        assertNotNull(personalDetails.getName().getCreatedDate().getValue());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Given Names", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getName().getVisibility().value());
        assertNotNull(personalDetails.getOtherNames());
        Utils.verifyLastModified(personalDetails.getOtherNames().getLastModifiedDate());
        assertEquals(4, personalDetails.getOtherNames().getOtherNames().size());

        for (OtherName otherName : personalDetails.getOtherNames().getOtherNames()) {
            Utils.verifyLastModified(otherName.getLastModifiedDate());
            if (otherName.getPutCode().equals(Long.valueOf(13))) {
                assertEquals("Other Name PUBLIC", otherName.getContent());
                assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(14))) {
                assertEquals("Other Name LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/14", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(15))) {
                assertEquals("Other Name PRIVATE", otherName.getContent());
                assertEquals(Long.valueOf(2), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/15", otherName.getPath());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.PRIVATE.value(), otherName.getVisibility().value());
            } else if (otherName.getPutCode().equals(Long.valueOf(16))) {
                assertEquals("Other Name SELF LIMITED", otherName.getContent());
                assertEquals(Long.valueOf(3), otherName.getDisplayIndex());
                assertEquals("/0000-0000-0000-0003/other-names/16", otherName.getPath());
                assertEquals("0000-0000-0000-0003", otherName.getSource().retrieveSourcePath());
                assertEquals(Visibility.LIMITED.value(), otherName.getVisibility().value());
            } else {
                fail("Invalid put code found: " + otherName.getPutCode());
            }
        }

        assertEquals("/0000-0000-0000-0003/other-names", personalDetails.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/personal-details", personalDetails.getPath());
    }

    @Test
    public void testViewPerson() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewPerson("4444-4444-4444-4442");
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        assertNotNull(person);
        assertNotNull(person.getPath());
        Utils.verifyLastModified(person.getLastModifiedDate());
        assertNotNull(person.getName());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());
        Utils.verifyLastModified(person.getName().getLastModifiedDate());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());

        assertNotNull(person.getAddresses());
        Utils.verifyLastModified(person.getAddresses().getLastModifiedDate());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCreatedDate());
        Utils.verifyLastModified(person.getAddresses().getAddress().get(0).getLastModifiedDate());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertEquals(Long.valueOf(1), person.getAddresses().getAddress().get(0).getPutCode());
        assertNotNull(person.getAddresses().getAddress().get(0).getSource());
        assertEquals("APP-5555555555555555", person.getAddresses().getAddress().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", person.getAddresses().getAddress().get(0).getSource().retriveSourceUri());
        assertEquals(Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        Utils.verifyLastModified(person.getBiography().getLastModifiedDate());
        assertEquals("Biography for 4444-4444-4444-4442", person.getBiography().getContent());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        Utils.verifyLastModified(person.getEmails().getLastModifiedDate());
        assertNotNull(person.getEmails().getEmails());
        assertEquals(1, person.getEmails().getEmails().size());
        Utils.verifyLastModified(person.getEmails().getEmails().get(0).getLastModifiedDate());
        assertEquals("michael@bentine.com", person.getEmails().getEmails().get(0).getEmail());
        assertEquals(Visibility.LIMITED, person.getEmails().getEmails().get(0).getVisibility());
        assertNotNull(person.getEmails().getEmails().get(0).getSource());
        assertEquals("4444-4444-4444-4442", person.getEmails().getEmails().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", person.getEmails().getEmails().get(0).getSource().retriveSourceUri());
        assertNull(person.getEmails().getEmails().get(0).getPutCode());
        Utils.verifyLastModified(person.getEmails().getEmails().get(0).getLastModifiedDate());
        assertNotNull(person.getEmails().getEmails().get(0).getCreatedDate());

        assertNotNull(person.getExternalIdentifiers());
        Utils.verifyLastModified(person.getExternalIdentifiers().getLastModifiedDate());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(3, person.getExternalIdentifiers().getExternalIdentifiers().size());

        boolean found2 = false, found3 = false, found5 = false;

        List<PersonExternalIdentifier> extIds = person.getExternalIdentifiers().getExternalIdentifiers();
        for (PersonExternalIdentifier extId : extIds) {
            Utils.verifyLastModified(extId.getLastModifiedDate());
            assertThat(extId.getPutCode(), anyOf(is(2L), is(3L), is(5L)));
            assertNotNull(extId.getCreatedDate());
            Utils.verifyLastModified(extId.getLastModifiedDate());
            assertNotNull(extId.getSource());
            if (extId.getPutCode() == 2L) {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc123", extId.getValue());
                assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", extId.getSource().retriveSourceUri());
                found2 = true;
            } else if (extId.getPutCode() == 3L) {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc456", extId.getValue());
                assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
                assertEquals(Visibility.LIMITED, extId.getVisibility());
                assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", extId.getSource().retriveSourceUri());
                found3 = true;
            } else {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc012", extId.getValue());
                assertEquals("http://www.facebook.com/abc012", extId.getUrl().getValue());
                assertEquals(Visibility.PRIVATE, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", extId.getSource().retriveSourceUri());
                found5 = true;
            }
        }

        assertTrue(found2 && found3 && found5);

        assertNotNull(person.getKeywords());
        Utils.verifyLastModified(person.getKeywords().getLastModifiedDate());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(1, person.getKeywords().getKeywords().size());
        Utils.verifyLastModified(person.getKeywords().getKeywords().get(0).getLastModifiedDate());
        assertEquals("My keyword", person.getKeywords().getKeywords().get(0).getContent());
        assertEquals(Long.valueOf(7), person.getKeywords().getKeywords().get(0).getPutCode());
        assertEquals("APP-5555555555555555", person.getKeywords().getKeywords().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", person.getKeywords().getKeywords().get(0).getSource().retriveSourceUri());
        assertEquals(Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertNotNull(person.getKeywords().getKeywords().get(0).getCreatedDate());
        Utils.verifyLastModified(person.getKeywords().getKeywords().get(0).getLastModifiedDate());

        assertNotNull(person.getOtherNames());
        Utils.verifyLastModified(person.getOtherNames().getLastModifiedDate());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());

        boolean found9 = false, found10 = false;

        for (OtherName otherName : person.getOtherNames().getOtherNames()) {
            Utils.verifyLastModified(otherName.getLastModifiedDate());
            assertThat(otherName.getPutCode(), anyOf(is(10L), is(11L)));
            assertNotNull(otherName.getSource());
            assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
            assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", otherName.getSource().retriveSourceUri());
            if (otherName.getPutCode() == 10L) {
                assertEquals("Other Name # 1", otherName.getContent());
                assertEquals(Visibility.PUBLIC, otherName.getVisibility());
                found9 = true;
            } else {
                assertEquals("Other Name # 2", otherName.getContent());
                assertEquals(Visibility.PRIVATE, otherName.getVisibility());
                found10 = true;
            }
        }

        assertTrue(found9 && found10);

        assertNotNull(person.getResearcherUrls());
        Utils.verifyLastModified(person.getResearcherUrls().getLastModifiedDate());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(3, person.getResearcherUrls().getResearcherUrls().size());

        found9 = false;
        found10 = false;
        boolean found12 = false;

        for (ResearcherUrl rUrl : person.getResearcherUrls().getResearcherUrls()) {
            Utils.verifyLastModified(rUrl.getLastModifiedDate());
            assertNotNull(rUrl.getCreatedDate());
            Utils.verifyLastModified(rUrl.getLastModifiedDate());
            assertNotNull(rUrl.getSource());
            assertThat(rUrl.getPutCode(), anyOf(is(9L), is(10L), is(12L)));
            assertNotNull(rUrl.getUrl());
            if (rUrl.getPutCode().equals(9L)) {
                assertEquals("4444-4444-4444-4442", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=9", rUrl.getUrl().getValue());
                assertEquals("1", rUrl.getUrlName());
                assertEquals(Visibility.PUBLIC, rUrl.getVisibility());
                found9 = true;
            } else if (rUrl.getPutCode().equals(10L)) {
                assertEquals("4444-4444-4444-4442", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=10", rUrl.getUrl().getValue());
                assertEquals("2", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED, rUrl.getVisibility());
                found10 = true;
            } else {
                assertEquals(Long.valueOf(12), rUrl.getPutCode());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=12", rUrl.getUrl().getValue());
                assertEquals("4", rUrl.getUrlName());
                assertEquals(Visibility.PRIVATE, rUrl.getVisibility());
                found12 = true;
            }
        }

        assertTrue(found9 && found10 && found12);
    }

    @Test
    public void testCleanEmptyFieldsOnActivities() {
        LastModifiedDate lmd = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis()));
        Works works = new Works();
        works.setLastModifiedDate(lmd);
        WorkGroup group = new WorkGroup();
        group.setLastModifiedDate(lmd);
        for (int i = 0; i < 5; i++) {
            WorkSummary summary = new WorkSummary();
            summary.setLastModifiedDate(lmd);
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Work " + i));
            title.setTranslatedTitle(new TranslatedTitle("", ""));
            summary.setTitle(title);
            group.getWorkSummary().add(summary);
        }
        works.getWorkGroup().add(group);
        ActivitiesSummary as = new ActivitiesSummary();
        as.setWorks(works);

        ActivityUtils.cleanEmptyFields(as);

        assertNotNull(as);
        assertNotNull(as.getWorks());
        Utils.verifyLastModified(as.getWorks().getLastModifiedDate());
        assertNotNull(as.getWorks().getWorkGroup());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertNotNull(as.getWorks().getWorkGroup().get(0).getWorkSummary());
        Utils.verifyLastModified(as.getWorks().getWorkGroup().get(0).getLastModifiedDate());
        assertEquals(5, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        for (WorkSummary summary : as.getWorks().getWorkGroup().get(0).getWorkSummary()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            assertTrue(summary.getTitle().getTitle().getContent().startsWith("Work "));
            assertNull(summary.getTitle().getTranslatedTitle());
        }

    }

    private void testActivities(ActivitiesSummary as, String orcid) {
        boolean found1 = false, found2 = false, found3 = false;
        // This is more an utility that will work only for 0000-0000-0000-0003
        assertEquals("0000-0000-0000-0003", orcid);

        assertNotNull(as);
        assertNotNull(as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());
        assertNotNull(as.getEducations());
        assertEquals(3, as.getEducations().getSummaries().size());

        for (EducationSummary element : as.getEducations().getSummaries()) {
            if (element.getPutCode().equals(Long.valueOf(20))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(21))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(22))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getEmployments());
        assertEquals(3, as.getEmployments().getSummaries().size());

        for (EmploymentSummary element : as.getEmployments().getSummaries()) {
            if (element.getPutCode().equals(Long.valueOf(17))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(18))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(19))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getFundings());
        assertEquals(3, as.getFundings().getFundingGroup().size());

        for (FundingGroup group : as.getFundings().getFundingGroup()) {
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary element = group.getFundingSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(10))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getPeerReviews());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());

        for (PeerReviewGroup group : as.getPeerReviews().getPeerReviewGroup()) {
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary element = group.getPeerReviewSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(9))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(10))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getWorks());
        assertEquals(3, as.getWorks().getWorkGroup().size());

        for (WorkGroup group : as.getWorks().getWorkGroup()) {
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary element = group.getWorkSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(11))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(13))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;
    }

    private void testPerson(Person p, String orcid) {
        // This is more an utility that will work only for 0000-0000-0000-0003
        assertEquals("0000-0000-0000-0003", orcid);
        assertNotNull(p);
        Utils.verifyLastModified(p.getLastModifiedDate());
        // Address
        assertNotNull(p.getAddresses());
        Addresses a = p.getAddresses();
        assertNotNull(a);
        Utils.verifyLastModified(a.getLastModifiedDate());
        assertEquals(3, a.getAddress().size());

        boolean found1 = false, found2 = false, found3 = false;
        for (Address element : a.getAddress()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 9) {
                found1 = true;
            } else if (element.getPutCode() == 10) {
                found2 = true;
            } else if (element.getPutCode() == 11) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Biography
        assertNotNull(p.getBiography());
        Biography b = p.getBiography();
        assertNotNull(b);
        Utils.verifyLastModified(b.getLastModifiedDate());

        assertEquals("Biography for 0000-0000-0000-0003", b.getContent());

        // Email
        assertNotNull(p.getEmails());
        Emails email = p.getEmails();
        assertNotNull(email);
        Utils.verifyLastModified(email.getLastModifiedDate());
        assertEquals(3, email.getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmails().get(0).getEmail());

        found1 = false;
        found2 = false;
        found3 = false;

        for (Email element : email.getEmails()) {
            if (element.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                found1 = true;
            } else if (element.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                found2 = true;
            } else if (element.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                found3 = true;
            } else {
                fail("Invalid email " + element.getEmail());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // External identifiers
        assertNotNull(p.getExternalIdentifiers());
        PersonExternalIdentifiers extIds = p.getExternalIdentifiers();
        assertNotNull(extIds);
        Utils.verifyLastModified(extIds.getLastModifiedDate());
        assertEquals(3, extIds.getExternalIdentifiers().size());
        found1 = false;
        found2 = false;
        found3 = false;
        for (PersonExternalIdentifier element : extIds.getExternalIdentifiers()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Keywords
        assertNotNull(p.getKeywords());
        Keywords k = p.getKeywords();
        assertNotNull(k);
        Utils.verifyLastModified(k.getLastModifiedDate());
        assertEquals(3, k.getKeywords().size());
        found1 = false;
        found2 = false;
        found3 = false;
        for (Keyword element : k.getKeywords()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 9) {
                found1 = true;
            } else if (element.getPutCode() == 10) {
                found2 = true;
            } else if (element.getPutCode() == 11) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Name
        assertNotNull(p.getName());
        assertEquals("Credit Name", p.getName().getCreditName().getContent());
        assertEquals("Given Names", p.getName().getGivenNames().getContent());
        assertEquals("Family Name", p.getName().getFamilyName().getContent());

        // Other names
        assertNotNull(p.getOtherNames());
        OtherNames o = p.getOtherNames();
        assertNotNull(o);
        Utils.verifyLastModified(o.getLastModifiedDate());
        assertEquals(3, o.getOtherNames().size());
        found1 = false;
        found2 = false;
        found3 = false;
        for (OtherName element : o.getOtherNames()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Researcher urls
        assertNotNull(p.getResearcherUrls());
        ResearcherUrls ru = p.getResearcherUrls();
        assertNotNull(ru);
        Utils.verifyLastModified(ru.getLastModifiedDate());
        assertEquals(3, ru.getResearcherUrls().size());
        found1 = false;
        found2 = false;
        found3 = false;
        for (ResearcherUrl element : ru.getResearcherUrls()) {
            Utils.verifyLastModified(element.getLastModifiedDate());
            if (element.getPutCode() == 13) {
                found1 = true;
            } else if (element.getPutCode() == 14) {
                found2 = true;
            } else if (element.getPutCode() == 15) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }

        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        assertNotNull(p.getPath());
    }
}
