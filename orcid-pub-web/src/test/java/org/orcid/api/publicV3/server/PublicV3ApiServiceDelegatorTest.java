package org.orcid.api.publicV3.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgExternalID;
import org.orcid.api.common.writer.schemaorg.SchemaOrgMBWriterV3;
import org.orcid.api.publicV3.server.delegator.PublicV3ApiServiceDelegator;
import org.orcid.api.publicV3.server.delegator.impl.PublicV3ApiServiceDelegatorImpl;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidNonPublicElementException;
import org.orcid.core.exception.SearchStartParameterLimitExceededException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.locale.LocaleManagerImpl;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.impl.OrcidSearchManagerImpl;
import org.orcid.core.manager.v3.impl.OrcidSecurityManagerImpl;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.OrcidType;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.History;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Person;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.PersonalDetails;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.jaxb.model.v3.release.search.Result;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml", "classpath:orcid-t1-security-context.xml" })
public class PublicV3ApiServiceDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/BiographyEntityData.xml", "/data/RecordNameEntityData.xml");

    private final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "publicV3ApiServiceDelegator")
    PublicV3ApiServiceDelegator<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        Authentication auth = new AnonymousAuthenticationToken("anonymous", "anonymous", roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testViewWork() {
        Response response = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertNotNull(work.getLastModifiedDate());
        assertNotNull(work.getLastModifiedDate().getValue());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("PUBLIC", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(11), work.getPutCode());
        assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
        assertNotNull(work.getWorkContributors());
        assertNotNull(work.getWorkContributors().getContributor());
        assertEquals(1, work.getWorkContributors().getContributor().size());
        assertNotNull(work.getWorkContributors().getContributor().get(0).getContributorOrcid());
        assertEquals("0000-0000-0000-0000", work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertNull(work.getWorkContributors().getContributor().get(0).getCreditName());
    }

    @Test
    public void testViewWorks() {
        Response response = serviceDelegator.viewWorks(ORCID);
        assertNotNull(response);
        Works works = (Works) response.getEntity();
        assertNotNull(works);
        assertEquals("/0000-0000-0000-0003/works", works.getPath());
        assertNotNull(works.getLastModifiedDate());
        assertNotNull(works.getLastModifiedDate().getValue());
        assertEquals(1, works.getWorkGroup().size());
        assertEquals(1, works.getWorkGroup().get(0).getWorkSummary().size());
        WorkSummary work = works.getWorkGroup().get(0).getWorkSummary().get(0);
        assertEquals(Long.valueOf(11), work.getPutCode());
        assertNotNull(work.getLastModifiedDate());
        assertNotNull(work.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getType());
        assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewBulkWorks() {
        Response response = serviceDelegator.viewBulkWorks(ORCID, "11,12,13");
        assertNotNull(response);
        WorkBulk workBulk = (WorkBulk) response.getEntity();
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(3, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof OrcidError);
        assertTrue(workBulk.getBulk().get(2) instanceof OrcidError);
        Work work = (Work) workBulk.getBulk().get(0);
        assertNotNull(work);
        assertNotNull(work.getLastModifiedDate());
        assertNotNull(work.getLastModifiedDate().getValue());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("PUBLIC", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(11), work.getPutCode());
        assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals("APP-5555555555555555", work.getSource().retrieveSourcePath());
        assertNotNull(work.getWorkContributors());
        assertNotNull(work.getWorkContributors().getContributor());
        assertEquals(1, work.getWorkContributors().getContributor().size());
        assertNotNull(work.getWorkContributors().getContributor().get(0).getContributorOrcid());
        assertEquals("0000-0000-0000-0000", work.getWorkContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertNull(work.getWorkContributors().getContributor().get(0).getCreditName());
    }

    @Test
    public void testViewFunding() {
        Response response = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getLastModifiedDate());
        assertNotNull(funding.getLastModifiedDate().getValue());
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(10), funding.getPutCode());
        assertEquals("/0000-0000-0000-0003/funding/10", funding.getPath());
        assertEquals("PUBLIC", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
        assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
        assertNotNull(funding.getContributors().getContributor().get(0).getContributorOrcid());
        assertEquals("0000-0000-0000-0000", funding.getContributors().getContributor().get(0).getContributorOrcid().getPath());
        assertNull(funding.getContributors().getContributor().get(0).getCreditName());
    }

    @Test
    public void testViewFundings() {
        Response response = serviceDelegator.viewFundings(ORCID);
        assertNotNull(response);
        Fundings fundings = (Fundings) response.getEntity();
        assertNotNull(fundings);
        assertEquals("/0000-0000-0000-0003/fundings", fundings.getPath());
        assertNotNull(fundings.getLastModifiedDate());
        assertNotNull(fundings.getLastModifiedDate().getValue());
        assertEquals(1, fundings.getFundingGroup().size());
        assertEquals(1, fundings.getFundingGroup().get(0).getFundingSummary().size());
        FundingSummary funding = fundings.getFundingGroup().get(0).getFundingSummary().get(0);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(10), funding.getPutCode());
        assertNotNull(funding.getLastModifiedDate());
        assertNotNull(funding.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/funding/10", funding.getPath());
        assertEquals("PUBLIC", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
        assertEquals("APP-5555555555555555", funding.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewEducation() {
        Response response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertNotNull(education.getLastModifiedDate());
        assertNotNull(education.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(20), education.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
        assertEquals("PUBLIC Department", education.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
        assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewEducations() {
        Response response = serviceDelegator.viewEducations(ORCID);
        assertNotNull(response);
        Educations educations = (Educations) response.getEntity();
        assertNotNull(educations);
        assertEquals("/0000-0000-0000-0003/educations", educations.getPath());
        assertNotNull(educations.getLastModifiedDate());
        assertNotNull(educations.getLastModifiedDate().getValue());
        assertEquals(1, educations.retrieveGroups().size());
        EducationSummary education = educations.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(20), education.getPutCode());
        assertNotNull(education.getLastModifiedDate());
        assertNotNull(education.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
        assertEquals("PUBLIC Department", education.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
        assertEquals("APP-5555555555555555", education.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewPeerReview() {
        Response response = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/0000-0000-0000-0003/peer-review/9", peerReview.getPath());
        assertNotNull(peerReview.getLastModifiedDate());
        assertNotNull(peerReview.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), peerReview.getPutCode());
        assertEquals("02", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("02", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2016", peerReview.getCompletionDate().getYear().getValue());
        assertNotNull(peerReview.getExternalIdentifiers());
        assertNotNull(peerReview.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, peerReview.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Visibility.PUBLIC.value(), peerReview.getVisibility().value());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewPeerReviews() {
        Response response = serviceDelegator.viewPeerReviews(ORCID);
        assertNotNull(response);
        PeerReviews peerReviews = (PeerReviews) response.getEntity();
        assertNotNull(peerReviews);
        assertEquals("/0000-0000-0000-0003/peer-reviews", peerReviews.getPath());
        assertNotNull(peerReviews.getLastModifiedDate());
        assertNotNull(peerReviews.getLastModifiedDate().getValue());
        assertEquals(1, peerReviews.getPeerReviewGroup().size());
        assertEquals(1, peerReviews.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        PeerReviewSummary peerReview = peerReviews.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0);
        assertEquals(Long.valueOf(9), peerReview.getPutCode());
        assertNotNull(peerReview.getLastModifiedDate());
        assertNotNull(peerReview.getLastModifiedDate().getValue());
        assertEquals("02", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("02", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2016", peerReview.getCompletionDate().getYear().getValue());
        assertNotNull(peerReview.getExternalIdentifiers());
        assertNotNull(peerReview.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(1, peerReview.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("/0000-0000-0000-0003/peer-review/9", peerReview.getPath());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(Visibility.PUBLIC.value(), peerReview.getVisibility().value());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewEmployment() {
        Response response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertNotNull(employment.getLastModifiedDate());
        assertNotNull(employment.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(17), employment.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/17", employment.getPath());
        assertEquals("PUBLIC Department", employment.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), employment.getVisibility().value());
        assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewEmployments() {
        Response response = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(response);
        Employments employments = (Employments) response.getEntity();
        assertNotNull(employments);
        assertEquals("/0000-0000-0000-0003/employments", employments.getPath());
        assertNotNull(employments.getLastModifiedDate());
        assertNotNull(employments.getLastModifiedDate().getValue());
        assertEquals(1, employments.retrieveGroups().size());
        EmploymentSummary employment = employments.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(17), employment.getPutCode());
        assertNotNull(employment.getLastModifiedDate());
        assertNotNull(employment.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/employment/17", employment.getPath());
        assertEquals("PUBLIC Department", employment.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), employment.getVisibility().value());
        assertEquals("APP-5555555555555555", employment.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewOtherName() {
        Response response = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertNotNull(otherName.getLastModifiedDate());
        assertNotNull(otherName.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(13), otherName.getPutCode());
        assertEquals("Other Name PUBLIC", otherName.getContent());
        assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewOtherNames() {
        Response response = serviceDelegator.viewOtherNames(ORCID);
        assertNotNull(response);
        OtherNames otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getLastModifiedDate());
        assertNotNull(otherNames.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/other-names", otherNames.getPath());
        assertEquals(1, otherNames.getOtherNames().size());
        OtherName otherName = otherNames.getOtherNames().get(0);
        assertNotNull(otherName);
        assertNotNull(otherName.getLastModifiedDate());
        assertNotNull(otherName.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(13), otherName.getPutCode());
        assertEquals("Other Name PUBLIC", otherName.getContent());
        assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewKeyword() {
        Response response = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), keyword.getPutCode());
        assertEquals("PUBLIC", keyword.getContent());
        assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewKeywords() {
        Response response = serviceDelegator.viewKeywords(ORCID);
        assertNotNull(response);
        Keywords keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getLastModifiedDate());
        assertNotNull(keywords.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/keywords", keywords.getPath());
        assertEquals(1, keywords.getKeywords().size());
        Keyword keyword = keywords.getKeywords().get(0);
        assertNotNull(keyword);
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), keyword.getPutCode());
        assertEquals("PUBLIC", keyword.getContent());
        assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewExternalIdentifier() {
        Response response = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertNotNull(extId.getLastModifiedDate());
        assertNotNull(extId.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(13), extId.getPutCode());
        assertEquals("public_type", extId.getType());
        assertNotNull(extId.getUrl());
        assertEquals("http://ext-id/public_ref", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewExternalIdentifiers() {
        Response response = serviceDelegator.viewExternalIdentifiers(ORCID);
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getLastModifiedDate());
        assertNotNull(extIds.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/external-identifiers", extIds.getPath());
        assertEquals(1, extIds.getExternalIdentifiers().size());
        PersonExternalIdentifier extId = extIds.getExternalIdentifiers().get(0);
        assertNotNull(extId);
        assertNotNull(extId.getLastModifiedDate());
        assertNotNull(extId.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(13), extId.getPutCode());
        assertEquals("public_type", extId.getType());
        assertNotNull(extId.getUrl());
        assertEquals("http://ext-id/public_ref", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewResearcherUrl() {
        Response response = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(response);
        ResearcherUrl rUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(rUrl);
        assertNotNull(rUrl.getLastModifiedDate());
        assertNotNull(rUrl.getLastModifiedDate().getValue());
        assertNotNull(rUrl.getUrl());
        assertEquals("http://www.researcherurl.com?id=13", rUrl.getUrl().getValue());
        assertEquals("public_rurl", rUrl.getUrlName());
        assertEquals(Visibility.PUBLIC.value(), rUrl.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
        assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewResearcherUrls() {
        Response response = serviceDelegator.viewResearcherUrls(ORCID);
        assertNotNull(response);
        ResearcherUrls rUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(rUrls);
        assertNotNull(rUrls.getLastModifiedDate());
        assertNotNull(rUrls.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/researcher-urls", rUrls.getPath());
        assertEquals(1, rUrls.getResearcherUrls().size());
        ResearcherUrl rUrl = rUrls.getResearcherUrls().get(0);
        assertNotNull(rUrl);
        assertNotNull(rUrl.getLastModifiedDate());
        assertNotNull(rUrl.getLastModifiedDate().getValue());
        assertNotNull(rUrl.getUrl());
        assertEquals("http://www.researcherurl.com?id=13", rUrl.getUrl().getValue());
        assertEquals("public_rurl", rUrl.getUrlName());
        assertEquals(Visibility.PUBLIC.value(), rUrl.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
        assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewEmails() {
        Response response = serviceDelegator.viewEmails(ORCID);
        assertNotNull(response);
        Emails emails = (Emails) response.getEntity();
        assertNotNull(emails);
        assertNotNull(emails.getLastModifiedDate());
        assertNotNull(emails.getLastModifiedDate().getValue());
        assertNotNull(emails.getEmails());
        assertEquals(1, emails.getEmails().size());
        Email email = emails.getEmails().get(0);
        assertNotNull(email.getLastModifiedDate());
        assertNotNull(email.getLastModifiedDate().getValue());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmail());
        assertTrue(email.isCurrent());
        assertTrue(email.isPrimary());
        assertTrue(email.isVerified());
        assertEquals(Visibility.PUBLIC.value(), email.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/email", emails.getPath());
        assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewAddress() {
        Response response = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertNotNull(address.getLastModifiedDate());
        assertNotNull(address.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), address.getPutCode());
        assertNotNull(address.getCountry());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC.value(), address.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewAddresses() {
        Response response = serviceDelegator.viewAddresses(ORCID);
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getLastModifiedDate());
        assertNotNull(addresses.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/address", addresses.getPath());
        assertEquals(1, addresses.getAddress().size());
        Address address = addresses.getAddress().get(0);
        assertNotNull(address);
        assertNotNull(address.getLastModifiedDate());
        assertNotNull(address.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), address.getPutCode());
        assertNotNull(address.getCountry());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC.value(), address.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
    }

    @Test
    public void testFindPersonalDetails() {
        Response response = serviceDelegator.viewPersonalDetails(ORCID);
        assertNotNull(response);
        PersonalDetails personalDetails = (PersonalDetails) response.getEntity();
        assertNotNull(personalDetails);
        assertNotNull(personalDetails.getLastModifiedDate());
        assertNotNull(personalDetails.getLastModifiedDate().getValue());
        assertNotNull(personalDetails.getBiography());
        assertNotNull(personalDetails.getBiography().getLastModifiedDate());
        assertNotNull(personalDetails.getBiography().getLastModifiedDate().getValue());
        assertEquals("Biography for 0000-0000-0000-0003", personalDetails.getBiography().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getBiography().getVisibility().value());
        assertEquals("/0000-0000-0000-0003/biography", personalDetails.getBiography().getPath());
        assertNotNull(personalDetails.getLastModifiedDate());
        assertNotNull(personalDetails.getLastModifiedDate().getValue());
        assertNotNull(personalDetails.getName());
        assertNotNull(personalDetails.getName().getCreatedDate().getValue());
        assertEquals("Credit Name", personalDetails.getName().getCreditName().getContent());
        assertEquals("Family Name", personalDetails.getName().getFamilyName().getContent());
        assertEquals("Given Names", personalDetails.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getName().getVisibility().value());
        assertNotNull(personalDetails.getName().getLastModifiedDate());
        assertNotNull(personalDetails.getName().getLastModifiedDate().getValue());
        assertNotNull(personalDetails.getOtherNames());
        assertNotNull(personalDetails.getOtherNames().getLastModifiedDate());
        assertNotNull(personalDetails.getOtherNames().getLastModifiedDate().getValue());
        assertEquals(1, personalDetails.getOtherNames().getOtherNames().size());
        assertEquals("Other Name PUBLIC", personalDetails.getOtherNames().getOtherNames().get(0).getContent());
        assertEquals(Visibility.PUBLIC.value(), personalDetails.getOtherNames().getOtherNames().get(0).getVisibility().value());
        assertEquals("/0000-0000-0000-0003/other-names", personalDetails.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/personal-details", personalDetails.getPath());
    }

    @Test
    public void testFindPerson() {
        Response response = serviceDelegator.viewPerson(ORCID);
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        validatePerson(person);
    }

    @Test
    public void testFindActivityDetails() {
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        validateActivities(summary);
    }

    @Test
    public void testFindRecord() {
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        validateRecord(record);
    }

    @Test
    public void testValidateActivitiesUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        validateActivities(summary);
    }

    @Test
    public void testValidatePersonUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPerson(ORCID);
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        validatePerson(person);
    }

    @Test
    public void testValidateRecordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewRecord(ORCID);
        assertNotNull(response);
        Record record = (Record) response.getEntity();
        validateRecord(record);
    }

    // Education
    @Test
    public void testGetPublicEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(r);
        Education e = (Education) r.getEntity();
        assertNotNull(e);
        assertNotNull(e.getLastModifiedDate());
        assertNotNull(e.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(20), e.getPutCode());
    }

    @Test
    public void testGetPublicEducations() {
        Response r = serviceDelegator.viewEducations(ORCID);
        assertNotNull(r);
        Educations educations = (Educations) r.getEntity();
        assertNotNull(educations);
        assertNotNull(educations.getLastModifiedDate());
        assertNotNull(educations.getLastModifiedDate().getValue());
        assertNotNull(educations.retrieveGroups());
        assertEquals(1, educations.retrieveGroups().size());
        
        EducationSummary summary = educations.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(20), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicEducationsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEducations(ORCID);
        assertNotNull(r);
        Educations educations = (Educations) r.getEntity();
        assertNotNull(educations);
        assertNotNull(educations.getLastModifiedDate());
        assertNotNull(educations.getLastModifiedDate().getValue());
        assertNotNull(educations.retrieveGroups());
        assertEquals(1, educations.retrieveGroups().size());
        
        EducationSummary summary = educations.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(20), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation(ORCID, 21L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation(ORCID, 22L);
        fail();
    }

    // Employment
    @Test
    public void testGetPublicEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(r);
        Employment e = (Employment) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(17), e.getPutCode());
        assertNotNull(e.getLastModifiedDate());
        assertNotNull(e.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicEmployments() {
        Response r = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(r);
        Employments employments = (Employments) r.getEntity();
        assertNotNull(employments);
        assertNotNull(employments.getLastModifiedDate());
        assertNotNull(employments.getLastModifiedDate().getValue());
        assertNotNull(employments.retrieveGroups());
        assertEquals(1, employments.retrieveGroups().size());
        
        EmploymentSummary summary = employments.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(17), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicEmploymentsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(r);
        Employments employments = (Employments) r.getEntity();
        assertNotNull(employments);
        assertNotNull(employments.getLastModifiedDate());
        assertNotNull(employments.getLastModifiedDate().getValue());
        assertNotNull(employments.retrieveGroups());
        assertEquals(1, employments.retrieveGroups().size());
        
        EmploymentSummary summary = employments.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(17), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 18L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 19L);
        fail();
    }

    // Funding
    @Test
    public void testGetPublicFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(r);
        Funding f = (Funding) r.getEntity();
        assertNotNull(f);
        assertNotNull(f.getLastModifiedDate());
        assertNotNull(f.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(10), f.getPutCode());
    }

    @Test
    public void testGetPublicFundings() {
        Response r = serviceDelegator.viewFundings(ORCID);
        assertNotNull(r);
        Fundings fundings = (Fundings) r.getEntity();
        assertNotNull(fundings);
        assertNotNull(fundings.getLastModifiedDate());
        assertNotNull(fundings.getLastModifiedDate().getValue());
        assertNotNull(fundings.getFundingGroup());
        assertEquals(1, fundings.getFundingGroup().size());
        assertNotNull(fundings.getFundingGroup().get(0).getIdentifiers());
        assertEquals(1, fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("http://test.orcid.org/1.com", fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertNotNull(fundings.getFundingGroup().get(0).getFundingSummary());
        assertEquals(1, fundings.getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), fundings.getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertNotNull(fundings.getFundingGroup().get(0).getFundingSummary().get(0).getLastModifiedDate());
        assertNotNull(fundings.getFundingGroup().get(0).getFundingSummary().get(0).getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicFundingsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewFundings(ORCID);
        assertNotNull(r);
        Fundings fundings = (Fundings) r.getEntity();
        assertNotNull(fundings);
        assertNotNull(fundings.getLastModifiedDate());
        assertNotNull(fundings.getLastModifiedDate().getValue());
        assertNotNull(fundings.getFundingGroup());
        assertEquals(1, fundings.getFundingGroup().size());
        assertNotNull(fundings.getFundingGroup().get(0).getIdentifiers());
        assertEquals(1, fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("http://test.orcid.org/1.com", fundings.getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getUrl().getValue());
        assertNotNull(fundings.getFundingGroup().get(0).getFundingSummary());
        assertEquals(1, fundings.getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), fundings.getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertNotNull(fundings.getFundingGroup().get(0).getFundingSummary().get(0).getLastModifiedDate());
        assertNotNull(fundings.getFundingGroup().get(0).getFundingSummary().get(0).getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding(ORCID, 11L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding(ORCID, 12L);
        fail();
    }

    // Work
    @Test
    public void testGetPublicWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(r);
        Work w = (Work) r.getEntity();
        assertNotNull(w);
        assertNotNull(w.getLastModifiedDate());
        assertNotNull(w.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(11), w.getPutCode());
    }

    @Test
    public void testGetPublicWorks() {
        Response r = serviceDelegator.viewWorks(ORCID);
        assertNotNull(r);
        Works works = (Works) r.getEntity();
        assertNotNull(works);
        assertNotNull(works.getLastModifiedDate());
        assertNotNull(works.getLastModifiedDate().getValue());
        assertNotNull(works.getWorkGroup());
        assertEquals(1, works.getWorkGroup().size());
        assertNotNull(works.getWorkGroup().get(0).getIdentifiers());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("1", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(works.getWorkGroup().get(0).getWorkSummary());
        assertEquals(Long.valueOf(11), works.getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertNotNull(works.getWorkGroup().get(0).getWorkSummary().get(0).getLastModifiedDate());
        assertNotNull(works.getWorkGroup().get(0).getWorkSummary().get(0).getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicWorksUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewWorks(ORCID);
        assertNotNull(r);
        Works works = (Works) r.getEntity();
        assertNotNull(works);
        assertNotNull(works.getLastModifiedDate());
        assertNotNull(works.getLastModifiedDate().getValue());
        assertNotNull(works.getWorkGroup());
        assertEquals(1, works.getWorkGroup().size());
        assertNotNull(works.getWorkGroup().get(0).getIdentifiers());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("1", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertNotNull(works.getWorkGroup().get(0).getWorkSummary());
        assertEquals(Long.valueOf(11), works.getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertNotNull(works.getWorkGroup().get(0).getWorkSummary().get(0).getLastModifiedDate());
        assertNotNull(works.getWorkGroup().get(0).getWorkSummary().get(0).getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork(ORCID, 12L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork(ORCID, 13L);
        fail();
    }

    // Peer review
    @Test
    public void testGetPublicPeerReviewUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(r);
        PeerReview p = (PeerReview) r.getEntity();
        assertNotNull(p);
        assertNotNull(p.getLastModifiedDate());
        assertNotNull(p.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), p.getPutCode());
    }

    @Test
    public void testGetPublicPeerReviews() {
        Response r = serviceDelegator.viewPeerReviews(ORCID);
        assertNotNull(r);
        PeerReviews p = (PeerReviews) r.getEntity();
        assertNotNull(p);
        assertNotNull(p.getLastModifiedDate());
        assertNotNull(p.getLastModifiedDate().getValue());
        assertNotNull(p.getPeerReviewGroup());
        assertEquals(1, p.getPeerReviewGroup().size());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertEquals(1, p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicPeerReviewsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewPeerReviews(ORCID);
        assertNotNull(r);
        PeerReviews p = (PeerReviews) r.getEntity();
        assertNotNull(p);
        assertNotNull(p.getLastModifiedDate());
        assertNotNull(p.getLastModifiedDate().getValue());
        assertNotNull(p.getPeerReviewGroup());
        assertEquals(1, p.getPeerReviewGroup().size());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertEquals(1, p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedPeerReviewUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview(ORCID, 10L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivatePeerReviewUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview(ORCID, 11L);
        fail();
    }

    // Biography
    @Test
    public void testGetPublicBiographyUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewBiography(ORCID);
        assertNotNull(r);
        Biography b = (Biography) r.getEntity();
        assertNotNull(b);
        assertNotNull(b.getLastModifiedDate());
        assertNotNull(b.getLastModifiedDate().getValue());
        assertEquals(Visibility.PUBLIC, b.getVisibility());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedBiographyUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0002", ScopePathType.READ_LIMITED);
        serviceDelegator.viewBiography("0000-0000-0000-0002");
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateBiographyUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext("0000-0000-0000-0001", ScopePathType.READ_LIMITED);
        serviceDelegator.viewBiography("0000-0000-0000-0001");
    }

    // Address
    @Test
    public void testGetPublicAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(r);
        Address a = (Address) r.getEntity();
        assertNotNull(a);
        assertNotNull(a.getLastModifiedDate());
        assertNotNull(a.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), a.getPutCode());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddress(ORCID, 10L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewAddress(ORCID, 10L);
        fail();
    }

    // Keyword
    @Test
    public void testGetPublicKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(r);
        Keyword k = (Keyword) r.getEntity();
        assertNotNull(k);
        assertNotNull(k.getLastModifiedDate());
        assertNotNull(k.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), k.getPutCode());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeyword(ORCID, 10L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewKeyword(ORCID, 11L);
        fail();
    }

    // External identifiers
    @Test
    public void testGetPublicExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(r);
        PersonExternalIdentifier e = (PersonExternalIdentifier) r.getEntity();
        assertNotNull(e);
        assertNotNull(e.getLastModifiedDate());
        assertNotNull(e.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(13), e.getPutCode());

    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifier(ORCID, 14L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewExternalIdentifier(ORCID, 15L);
        fail();
    }

    // Other names
    @Test
    public void testGetPublicOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(r);
        OtherName o = (OtherName) r.getEntity();
        assertNotNull(o);
        assertNotNull(o.getLastModifiedDate());
        assertNotNull(o.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(13), o.getPutCode());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewOtherName(ORCID, 14L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewOtherName(ORCID, 15L);
        fail();
    }

    // Researcher urls
    @Test
    public void testGetPublicResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(r);
        ResearcherUrl ru = (ResearcherUrl) r.getEntity();
        assertNotNull(ru);
        assertNotNull(ru.getLastModifiedDate());
        assertNotNull(ru.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(13), ru.getPutCode());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrl(ORCID, 14L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrl(ORCID, 15L);
        fail();
    }

    @Test
    public void testSearchByQuery() {
        Search search = new Search();
        Result result = new Result();
        result.setOrcidIdentifier(new OrcidIdentifier("some-orcid-id"));
        search.getResults().add(result);
        OrcidSearchManager orcidSearchManager = Mockito.mock(OrcidSearchManagerImpl.class);
        Mockito.when(orcidSearchManager.findOrcidIds(Matchers.<Map<String, List<String>>> any())).thenReturn(search);

        PublicV3ApiServiceDelegatorImpl delegator = new PublicV3ApiServiceDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "orcidSearchManager", orcidSearchManager);

        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManagerImpl.class);
        Mockito.when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn(null);
        ReflectionTestUtils.setField(delegator, "orcidSecurityManager", orcidSecurityManager);

        Response response = delegator.searchByQuery(new HashMap<String, List<String>>());

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Search);
        assertEquals(1, ((Search) response.getEntity()).getResults().size());
        assertEquals("some-orcid-id", ((Search) response.getEntity()).getResults().get(0).getOrcidIdentifier().getPath());
    }

    @Test(expected = OrcidBadRequestException.class)
    public void testSearchByQueryTooManyRows() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("rows", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_ROWS + 20)));

        LocaleManager localeManager = Mockito.mock(LocaleManagerImpl.class);
        Mockito.when(localeManager.resolveMessage(Mockito.anyString())).thenReturn("a message");

        PublicV3ApiServiceDelegatorImpl delegator = new PublicV3ApiServiceDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "localeManager", localeManager);
        delegator.searchByQuery(params);
    }

    @Test(expected = SearchStartParameterLimitExceededException.class)
    public void testSearchByQueryIllegalStart() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("start", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_START + 20)));

        LocaleManager localeManager = Mockito.mock(LocaleManagerImpl.class);
        Mockito.when(localeManager.resolveMessage(Mockito.anyString())).thenReturn("a message");

        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManagerImpl.class);
        Mockito.when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn(null);

        PublicV3ApiServiceDelegatorImpl delegator = new PublicV3ApiServiceDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "localeManager", localeManager);
        ReflectionTestUtils.setField(delegator, "orcidSecurityManager", orcidSecurityManager);
        delegator.searchByQuery(params);
    }

    @Test
    public void testSearchByQueryLegalStart() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("start", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_START)));

        LocaleManager localeManager = Mockito.mock(LocaleManagerImpl.class);
        Mockito.when(localeManager.resolveMessage(Mockito.anyString())).thenReturn("a message");

        OrcidSearchManager orcidSearchManager = Mockito.mock(OrcidSearchManagerImpl.class);
        Mockito.when(orcidSearchManager.findOrcidIds(Mockito.anyMap())).thenReturn(new Search());

        OrcidSecurityManager orcidSecurityManager = Mockito.mock(OrcidSecurityManagerImpl.class);
        Mockito.when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn(null);

        PublicV3ApiServiceDelegatorImpl delegator = new PublicV3ApiServiceDelegatorImpl();
        ReflectionTestUtils.setField(delegator, "localeManager", localeManager);
        ReflectionTestUtils.setField(delegator, "orcidSearchManager", orcidSearchManager);
        ReflectionTestUtils.setField(delegator, "orcidSecurityManager", orcidSecurityManager);
        Response response = delegator.searchByQuery(params);
        assertNotNull(response);
    }

    @Test(expected = NoResultException.class)
    public void testViewClientNonExistent() {
        serviceDelegator.viewClient("some-client-that-doesn't-exist");
        fail();
    }

    @Test
    public void testViewClientSummary() {
        Response response = serviceDelegator.viewClient("APP-6666666666666666");
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);

        ClientSummary summary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", summary.getName());
        assertEquals("A test source client", summary.getDescription());
    }

    // Distinction
    @Test
    public void testViewDistinction() {
        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction distinction = (Distinction) response.getEntity();
        assertNotNull(distinction);
        assertNotNull(distinction.getLastModifiedDate());
        assertNotNull(distinction.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(27), distinction.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/27", distinction.getPath());
        assertEquals("PUBLIC Department", distinction.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), distinction.getVisibility().value());
        assertEquals("APP-5555555555555555", distinction.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewDistinctions() {
        Response response = serviceDelegator.viewDistinctions(ORCID);
        assertNotNull(response);
        Distinctions distinctions = (Distinctions) response.getEntity();
        assertNotNull(distinctions);
        assertEquals("/0000-0000-0000-0003/distinctions", distinctions.getPath());
        assertNotNull(distinctions.getLastModifiedDate());
        assertNotNull(distinctions.getLastModifiedDate().getValue());
        assertEquals(1, distinctions.retrieveGroups().size());
        DistinctionSummary distinction = distinctions.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(27), distinction.getPutCode());
        assertNotNull(distinction.getLastModifiedDate());
        assertNotNull(distinction.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/distinction/27", distinction.getPath());
        assertEquals("PUBLIC Department", distinction.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), distinction.getVisibility().value());
        assertEquals("APP-5555555555555555", distinction.getSource().retrieveSourcePath());
    }

    @Test
    public void testGetPublicDistinctionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(r);
        Distinction e = (Distinction) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(27), e.getPutCode());
        assertNotNull(e.getLastModifiedDate());
        assertNotNull(e.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicDistinctions() {
        Response r = serviceDelegator.viewDistinctions(ORCID);
        assertNotNull(r);
        Distinctions distinctions = (Distinctions) r.getEntity();
        assertNotNull(distinctions);
        assertNotNull(distinctions.getLastModifiedDate());
        assertNotNull(distinctions.getLastModifiedDate().getValue());
        assertNotNull(distinctions.retrieveGroups());
        assertEquals(1, distinctions.retrieveGroups().size());
        
        DistinctionSummary summary = distinctions.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(27), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicDistinctionsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewDistinctions(ORCID);
        assertNotNull(r);
        Distinctions distinctions = (Distinctions) r.getEntity();
        assertNotNull(distinctions);
        assertNotNull(distinctions.getLastModifiedDate());
        assertNotNull(distinctions.getLastModifiedDate().getValue());
        assertNotNull(distinctions.retrieveGroups());
        assertEquals(1, distinctions.retrieveGroups().size());
        
        DistinctionSummary summary = distinctions.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(27), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedDistinctionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewDistinction(ORCID, 28L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateDistinctionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewDistinction(ORCID, 29L);
        fail();
    }

    // InvitedPosition
    @Test
    public void testViewInvitedPosition() {
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition invitedPosition = (InvitedPosition) response.getEntity();
        assertNotNull(invitedPosition);
        assertNotNull(invitedPosition.getLastModifiedDate());
        assertNotNull(invitedPosition.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(32), invitedPosition.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/32", invitedPosition.getPath());
        assertEquals("PUBLIC Department", invitedPosition.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), invitedPosition.getVisibility().value());
        assertEquals("APP-5555555555555555", invitedPosition.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewInvitedPositions() {
        Response response = serviceDelegator.viewInvitedPositions(ORCID);
        assertNotNull(response);
        InvitedPositions invitedPositions = (InvitedPositions) response.getEntity();
        assertNotNull(invitedPositions);
        assertEquals("/0000-0000-0000-0003/invited-positions", invitedPositions.getPath());
        assertNotNull(invitedPositions.getLastModifiedDate());
        assertNotNull(invitedPositions.getLastModifiedDate().getValue());
        assertEquals(1, invitedPositions.retrieveGroups().size());
        InvitedPositionSummary invitedPosition = invitedPositions.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(32), invitedPosition.getPutCode());
        assertNotNull(invitedPosition.getLastModifiedDate());
        assertNotNull(invitedPosition.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/invited-position/32", invitedPosition.getPath());
        assertEquals("PUBLIC Department", invitedPosition.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), invitedPosition.getVisibility().value());
        assertEquals("APP-5555555555555555", invitedPosition.getSource().retrieveSourcePath());
    }

    @Test
    public void testGetPublicInvitedPositionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(r);
        InvitedPosition e = (InvitedPosition) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(32), e.getPutCode());
        assertNotNull(e.getLastModifiedDate());
        assertNotNull(e.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicInvitedPositions() {
        Response r = serviceDelegator.viewInvitedPositions(ORCID);
        assertNotNull(r);
        InvitedPositions invitedPositions = (InvitedPositions) r.getEntity();
        assertNotNull(invitedPositions);
        assertNotNull(invitedPositions.getLastModifiedDate());
        assertNotNull(invitedPositions.getLastModifiedDate().getValue());
        assertNotNull(invitedPositions.retrieveGroups());
        assertEquals(1, invitedPositions.retrieveGroups().size());
        
        InvitedPositionSummary summary = invitedPositions.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(32), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicInvitedPositionsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewInvitedPositions(ORCID);
        assertNotNull(r);
        InvitedPositions invitedPositions = (InvitedPositions) r.getEntity();
        assertNotNull(invitedPositions);
        assertNotNull(invitedPositions.getLastModifiedDate());
        assertNotNull(invitedPositions.getLastModifiedDate().getValue());
        assertNotNull(invitedPositions.retrieveGroups());
        assertEquals(1, invitedPositions.retrieveGroups().size());
        
        InvitedPositionSummary summary = invitedPositions.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(32), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedInvitedPositionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewInvitedPosition(ORCID, 33L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateInvitedPositionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewInvitedPosition(ORCID, 34L);
        fail();
    }

    // Membership
    @Test
    public void testViewMembership() {
        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(response);
        Membership membership = (Membership) response.getEntity();
        assertNotNull(membership);
        assertNotNull(membership.getLastModifiedDate());
        assertNotNull(membership.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(37), membership.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/37", membership.getPath());
        assertEquals("PUBLIC Department", membership.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), membership.getVisibility().value());
        assertEquals("APP-5555555555555555", membership.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewMemberships() {
        Response response = serviceDelegator.viewMemberships(ORCID);
        assertNotNull(response);
        Memberships memberships = (Memberships) response.getEntity();
        assertNotNull(memberships);
        assertEquals("/0000-0000-0000-0003/memberships", memberships.getPath());
        assertNotNull(memberships.getLastModifiedDate());
        assertNotNull(memberships.getLastModifiedDate().getValue());
        assertEquals(1, memberships.retrieveGroups().size());
        MembershipSummary membership = memberships.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(37), membership.getPutCode());
        assertNotNull(membership.getLastModifiedDate());
        assertNotNull(membership.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/membership/37", membership.getPath());
        assertEquals("PUBLIC Department", membership.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), membership.getVisibility().value());
        assertEquals("APP-5555555555555555", membership.getSource().retrieveSourcePath());
    }

    @Test
    public void testGetPublicMembershipUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(r);
        Membership e = (Membership) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(37), e.getPutCode());
        assertNotNull(e.getLastModifiedDate());
        assertNotNull(e.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicMemberships() {
        Response r = serviceDelegator.viewMemberships(ORCID);
        assertNotNull(r);
        Memberships memberships = (Memberships) r.getEntity();
        assertNotNull(memberships);
        assertNotNull(memberships.getLastModifiedDate());
        assertNotNull(memberships.getLastModifiedDate().getValue());
        assertNotNull(memberships.retrieveGroups());
        assertEquals(1, memberships.retrieveGroups().size());
        
        MembershipSummary summary = memberships.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(37), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicMembershipsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewMemberships(ORCID);
        assertNotNull(r);
        Memberships memberships = (Memberships) r.getEntity();
        assertNotNull(memberships);
        assertNotNull(memberships.getLastModifiedDate());
        assertNotNull(memberships.getLastModifiedDate().getValue());
        assertNotNull(memberships.retrieveGroups());
        assertEquals(1, memberships.retrieveGroups().size());
        
        MembershipSummary summary = memberships.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(37), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedMembershipUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewMembership(ORCID, 38L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateMembershipUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewMembership(ORCID, 39L);
        fail();
    }

    // Qualification
    @Test
    public void testViewQualification() {
        Response response = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(response);
        Qualification qualification = (Qualification) response.getEntity();
        assertNotNull(qualification);
        assertNotNull(qualification.getLastModifiedDate());
        assertNotNull(qualification.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(42), qualification.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/42", qualification.getPath());
        assertEquals("PUBLIC Department", qualification.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), qualification.getVisibility().value());
        assertEquals("APP-5555555555555555", qualification.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewQualifications() {
        Response response = serviceDelegator.viewQualifications(ORCID);
        assertNotNull(response);
        Qualifications qualifications = (Qualifications) response.getEntity();
        assertNotNull(qualifications);
        assertEquals("/0000-0000-0000-0003/qualifications", qualifications.getPath());
        assertNotNull(qualifications.getLastModifiedDate());
        assertNotNull(qualifications.getLastModifiedDate().getValue());
        assertEquals(1, qualifications.retrieveGroups().size());
        
        QualificationSummary qualification = qualifications.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(42), qualification.getPutCode());
        assertNotNull(qualification.getLastModifiedDate());
        assertNotNull(qualification.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/qualification/42", qualification.getPath());
        assertEquals("PUBLIC Department", qualification.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), qualification.getVisibility().value());
        assertEquals("APP-5555555555555555", qualification.getSource().retrieveSourcePath());
    }

    @Test
    public void testGetPublicQualificationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewQualification(ORCID, 42L);
        assertNotNull(r);
        Qualification e = (Qualification) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(42), e.getPutCode());
        assertNotNull(e.getLastModifiedDate());
        assertNotNull(e.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicQualifications() {
        Response r = serviceDelegator.viewQualifications(ORCID);
        assertNotNull(r);
        Qualifications qualifications = (Qualifications) r.getEntity();
        assertNotNull(qualifications);
        assertNotNull(qualifications.getLastModifiedDate());
        assertNotNull(qualifications.getLastModifiedDate().getValue());
        assertNotNull(qualifications.retrieveGroups());
        assertEquals(1, qualifications.retrieveGroups().size());
        
        QualificationSummary summary = qualifications.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(42), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicQualificationsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewQualifications(ORCID);
        assertNotNull(r);
        Qualifications qualifications = (Qualifications) r.getEntity();
        assertNotNull(qualifications);
        assertNotNull(qualifications.getLastModifiedDate());
        assertNotNull(qualifications.getLastModifiedDate().getValue());
        assertNotNull(qualifications.retrieveGroups());
        assertEquals(1, qualifications.retrieveGroups().size());
        
        QualificationSummary summary = qualifications.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(42), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedQualificationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewQualification(ORCID, 43L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateQualificationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewQualification(ORCID, 44L);
        fail();
    }

    // Service
    @Test
    public void testViewService() {
        Response response = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(response);
        Service service = (Service) response.getEntity();
        assertNotNull(service);
        assertNotNull(service.getLastModifiedDate());
        assertNotNull(service.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(47), service.getPutCode());
        assertEquals("/0000-0000-0000-0003/service/47", service.getPath());
        assertEquals("PUBLIC Department", service.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), service.getVisibility().value());
        assertEquals("APP-5555555555555555", service.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewServices() {
        Response response = serviceDelegator.viewServices(ORCID);
        assertNotNull(response);
        Services services = (Services) response.getEntity();
        assertNotNull(services);
        assertEquals("/0000-0000-0000-0003/services", services.getPath());
        assertNotNull(services.getLastModifiedDate());
        assertNotNull(services.getLastModifiedDate().getValue());
        assertEquals(1, services.retrieveGroups().size());
        ServiceSummary service = services.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(47), service.getPutCode());
        assertNotNull(service.getLastModifiedDate());
        assertNotNull(service.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/service/47", service.getPath());
        assertEquals("PUBLIC Department", service.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), service.getVisibility().value());
        assertEquals("APP-5555555555555555", service.getSource().retrieveSourcePath());
    }

    @Test
    public void testGetPublicServiceUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewService(ORCID, 47L);
        assertNotNull(r);
        Service e = (Service) r.getEntity();
        assertNotNull(e);
        assertEquals(Long.valueOf(47), e.getPutCode());
        assertNotNull(e.getLastModifiedDate());
        assertNotNull(e.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicServices() {
        Response r = serviceDelegator.viewServices(ORCID);
        assertNotNull(r);
        Services services = (Services) r.getEntity();
        assertNotNull(services);
        assertNotNull(services.getLastModifiedDate());
        assertNotNull(services.getLastModifiedDate().getValue());
        assertNotNull(services.retrieveGroups());
        assertEquals(1, services.retrieveGroups().size());
        
        ServiceSummary summary = services.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(47), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test
    public void testGetPublicServicesUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response r = serviceDelegator.viewServices(ORCID);
        assertNotNull(r);
        Services services = (Services) r.getEntity();
        assertNotNull(services);
        assertNotNull(services.getLastModifiedDate());
        assertNotNull(services.getLastModifiedDate().getValue());
        assertNotNull(services.retrieveGroups());
        assertEquals(1, services.retrieveGroups().size());
        
        ServiceSummary summary = services.retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(47), summary.getPutCode());
        assertEquals(Visibility.PUBLIC, summary.getVisibility());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetLimitedServiceUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewService(ORCID, 48L);
        fail();
    }

    @Test(expected = OrcidNonPublicElementException.class)
    public void testGetPrivateServiceUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        serviceDelegator.viewService(ORCID, 49L);
        fail();
    }

    private void validatePerson(Person person) {
        assertNotNull(person);
        assertNotNull(person.getLastModifiedDate());
        assertNotNull(person.getLastModifiedDate().getValue());
        assertNotNull(person.getAddresses());
        assertEquals("/0000-0000-0000-0003/address", person.getAddresses().getPath());
        assertNotNull(person.getAddresses().getLastModifiedDate());
        assertNotNull(person.getAddresses().getLastModifiedDate().getValue());
        assertEquals(1, person.getAddresses().getAddress().size());
        Address address = person.getAddresses().getAddress().get(0);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC.value(), address.getVisibility().value());
        assertEquals(Long.valueOf(9), address.getPutCode());
        assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
        assertNotNull(person.getBiography());
        assertEquals(Visibility.PUBLIC.value(), person.getBiography().getVisibility().value());
        assertEquals("Biography for 0000-0000-0000-0003", person.getBiography().getContent());
        assertNotNull(person.getBiography().getLastModifiedDate());
        assertNotNull(person.getBiography().getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/biography", person.getBiography().getPath());
        assertNotNull(person.getEmails());
        assertNotNull(person.getEmails().getLastModifiedDate());
        assertNotNull(person.getEmails().getLastModifiedDate().getValue());
        assertEquals(1, person.getEmails().getEmails().size());
        Email email = person.getEmails().getEmails().get(0);
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmail());
        assertNotNull(email.getLastModifiedDate());
        assertNotNull(email.getLastModifiedDate().getValue());
        assertEquals("APP-5555555555555555", email.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC.value(), email.getVisibility().value());
        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getLastModifiedDate());
        assertNotNull(person.getExternalIdentifiers().getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/external-identifiers", person.getExternalIdentifiers().getPath());
        assertEquals(1, person.getExternalIdentifiers().getExternalIdentifiers().size());
        PersonExternalIdentifier extId = person.getExternalIdentifiers().getExternalIdentifiers().get(0);
        assertNotNull(extId);
        assertNotNull(extId.getLastModifiedDate());
        assertNotNull(extId.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(13), extId.getPutCode());
        assertEquals("public_type", extId.getType());
        assertNotNull(extId.getUrl());
        assertEquals("http://ext-id/public_ref", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC.value(), extId.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getLastModifiedDate());
        assertNotNull(person.getKeywords().getLastModifiedDate().getValue());
        assertEquals(1, person.getKeywords().getKeywords().size());
        assertNotNull(person.getKeywords().getLastModifiedDate());
        assertEquals("/0000-0000-0000-0003/keywords", person.getKeywords().getPath());
        Keyword keyword = person.getKeywords().getKeywords().get(0);
        assertNotNull(keyword);
        assertNotNull(keyword.getLastModifiedDate());
        assertNotNull(keyword.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), keyword.getPutCode());
        assertEquals("PUBLIC", keyword.getContent());
        assertEquals(Visibility.PUBLIC.value(), keyword.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
        assertNotNull(person.getName());
        assertNotNull(person.getName().getLastModifiedDate());
        assertNotNull(person.getName().getLastModifiedDate().getValue());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());
        assertEquals(Visibility.PUBLIC.value(), person.getName().getVisibility().value());
        assertNotNull(person.getOtherNames());
        assertEquals("/0000-0000-0000-0003/other-names", person.getOtherNames().getPath());
        assertNotNull(person.getOtherNames().getLastModifiedDate());
        assertNotNull(person.getOtherNames().getLastModifiedDate().getValue());
        assertEquals(1, person.getOtherNames().getOtherNames().size());
        OtherName otherName = person.getOtherNames().getOtherNames().get(0);
        assertEquals("Other Name PUBLIC", otherName.getContent());
        assertNotNull(otherName.getLastModifiedDate());
        assertNotNull(otherName.getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
        assertEquals(Long.valueOf(13), otherName.getPutCode());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
        assertEquals(Visibility.PUBLIC.value(), otherName.getVisibility().value());
        assertNotNull(person.getResearcherUrls());
        assertEquals(1, person.getResearcherUrls().getResearcherUrls().size());
        assertNotNull(person.getResearcherUrls().getLastModifiedDate());
        assertNotNull(person.getResearcherUrls().getLastModifiedDate().getValue());
        assertEquals("/0000-0000-0000-0003/researcher-urls", person.getResearcherUrls().getPath());
        ResearcherUrl rUrl = person.getResearcherUrls().getResearcherUrls().get(0);
        assertNotNull(rUrl);
        assertNotNull(rUrl.getLastModifiedDate());
        assertNotNull(rUrl.getLastModifiedDate().getValue());
        assertNotNull(rUrl.getUrl());
        assertEquals("http://www.researcherurl.com?id=13", rUrl.getUrl().getValue());
        assertEquals("public_rurl", rUrl.getUrlName());
        assertEquals(Visibility.PUBLIC.value(), rUrl.getVisibility().value());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
        assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
    }

    private void validateActivities(ActivitiesSummary summary) {
        assertNotNull(summary);
        assertEquals(OrcidApiConstants.ACTIVITIES.replace("{orcid}", ORCID), summary.getPath());
        assertNotNull(summary.getLastModifiedDate());
        assertNotNull(summary.getLastModifiedDate().getValue());
        // Check works
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getLastModifiedDate());
        assertNotNull(summary.getWorks().getLastModifiedDate().getValue());
        assertEquals(1, summary.getWorks().getWorkGroup().size());
        assertNotNull(summary.getWorks().getWorkGroup().get(0).getLastModifiedDate());
        assertNotNull(summary.getWorks().getWorkGroup().get(0).getLastModifiedDate().getValue());
        assertNotNull(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getLastModifiedDate());
        assertNotNull(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(11), summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/work/11", summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals("PUBLIC", summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getVisibility().value());

        // Check fundings
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getLastModifiedDate());
        assertNotNull(summary.getFundings().getLastModifiedDate().getValue());
        assertEquals(1, summary.getFundings().getFundingGroup().size());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getLastModifiedDate());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getLastModifiedDate().getValue());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getLastModifiedDate());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(10), summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/funding/10", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertEquals("PUBLIC", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility().value());

        // Check PeerReviews
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getLastModifiedDate());
        assertNotNull(summary.getPeerReviews().getLastModifiedDate().getValue());
        assertEquals(1, summary.getPeerReviews().getPeerReviewGroup().size());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getLastModifiedDate());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getLastModifiedDate().getValue());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/peer-review/9", summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
        assertEquals(Visibility.PUBLIC.value(), summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility().value());

        // Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getLastModifiedDate());
        assertNotNull(summary.getEducations().getLastModifiedDate().getValue());
        assertNotNull(summary.getEducations().retrieveGroups());
        assertEquals(1, summary.getEducations().retrieveGroups().size());
        
        EducationSummary educationSumamry = summary.getEducations().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(educationSumamry.getLastModifiedDate());
        assertNotNull(educationSumamry.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(20), educationSumamry.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/20", educationSumamry.getPath());
        assertEquals("PUBLIC Department", educationSumamry.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), educationSumamry.getVisibility().value());

        // Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getLastModifiedDate());
        assertNotNull(summary.getEmployments().getLastModifiedDate().getValue());
        assertNotNull(summary.getEmployments().retrieveGroups());
        assertEquals(1, summary.getEmployments().retrieveGroups().size());
        
        EmploymentSummary employmentSummary = summary.getEmployments().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(employmentSummary.getLastModifiedDate());
        assertNotNull(employmentSummary.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(17), employmentSummary.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/17", employmentSummary.getPath());
        assertEquals("PUBLIC Department", employmentSummary.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), employmentSummary.getVisibility().value());

        // Check Distinctions
        assertNotNull(summary.getDistinctions());
        assertNotNull(summary.getDistinctions().getLastModifiedDate());
        assertNotNull(summary.getDistinctions().getLastModifiedDate().getValue());
        assertNotNull(summary.getDistinctions().retrieveGroups());
        assertEquals(1, summary.getDistinctions().retrieveGroups().size());
        
        DistinctionSummary distinctionSummary = summary.getDistinctions().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(distinctionSummary.getLastModifiedDate());
        assertNotNull(distinctionSummary.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(27), distinctionSummary.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/27", distinctionSummary.getPath());
        assertEquals("PUBLIC Department", distinctionSummary.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), distinctionSummary.getVisibility().value());

        // Check InvitedPositions
        assertNotNull(summary.getInvitedPositions());
        assertNotNull(summary.getInvitedPositions().getLastModifiedDate());
        assertNotNull(summary.getInvitedPositions().getLastModifiedDate().getValue());
        assertNotNull(summary.getInvitedPositions().retrieveGroups());
        assertEquals(1, summary.getInvitedPositions().retrieveGroups().size());
        
        InvitedPositionSummary invitedPositionSummary = summary.getInvitedPositions().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(invitedPositionSummary.getLastModifiedDate());
        assertNotNull(invitedPositionSummary.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(32), invitedPositionSummary.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/32", invitedPositionSummary.getPath());
        assertEquals("PUBLIC Department", invitedPositionSummary.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), invitedPositionSummary.getVisibility().value());
    
        // Check Memberships 
        assertNotNull(summary.getMemberships());
        assertNotNull(summary.getMemberships().getLastModifiedDate());
        assertNotNull(summary.getMemberships().getLastModifiedDate().getValue());
        assertNotNull(summary.getMemberships().retrieveGroups());
        assertEquals(1, summary.getMemberships().retrieveGroups().size());
        
        MembershipSummary membershipSummary = summary.getMemberships().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(membershipSummary.getLastModifiedDate());
        assertNotNull(membershipSummary.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(37), membershipSummary.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/37", membershipSummary.getPath());
        assertEquals("PUBLIC Department", membershipSummary.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), membershipSummary.getVisibility().value());
    
        // Check Qualifications
        assertNotNull(summary.getQualifications());
        assertNotNull(summary.getQualifications().getLastModifiedDate());
        assertNotNull(summary.getQualifications().getLastModifiedDate().getValue());
        assertNotNull(summary.getQualifications().retrieveGroups());
        assertEquals(1, summary.getQualifications().retrieveGroups().size());
        
        QualificationSummary qualificationSummary = summary.getQualifications().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(qualificationSummary.getLastModifiedDate());
        assertNotNull(qualificationSummary.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(42), qualificationSummary.getPutCode());
        assertEquals("/0000-0000-0000-0003/qualification/42", qualificationSummary.getPath());
        assertEquals("PUBLIC Department", qualificationSummary.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), qualificationSummary.getVisibility().value());
    
        // Check Services
        assertNotNull(summary.getServices());
        assertNotNull(summary.getServices().getLastModifiedDate());
        assertNotNull(summary.getServices().getLastModifiedDate().getValue());
        assertNotNull(summary.getServices().retrieveGroups());
        assertEquals(1, summary.getServices().retrieveGroups().size());
        
        ServiceSummary serviceSummary = summary.getServices().retrieveGroups().iterator().next().getActivities().get(0);
        assertNotNull(serviceSummary.getLastModifiedDate());
        assertNotNull(serviceSummary.getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(47), serviceSummary.getPutCode());
        assertEquals("/0000-0000-0000-0003/service/47", serviceSummary.getPath());
        assertEquals("PUBLIC Department", serviceSummary.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), serviceSummary.getVisibility().value());
    }

    private void validateRecord(Record record) {
        assertNotNull(record);
        assertEquals("/" + ORCID, record.getPath());
        validatePerson(record.getPerson());
        validateActivities(record.getActivitiesSummary());
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());
        assertNotNull(record.getPreferences());
        assertEquals(AvailableLocales.EN, record.getPreferences().getLocale());
        History history = record.getHistory();
        assertTrue(history.getClaimed());
        assertNotNull(history.getCompletionDate());
        assertEquals(CreationMethod.INTEGRATION_TEST, history.getCreationMethod());
        assertNull(history.getDeactivationDate());
        assertNotNull(history.getLastModifiedDate());
        assertNotNull(history.getLastModifiedDate().getValue());
        assertNotNull(history.getSource());
        assertEquals("APP-5555555555555555", history.getSource().retrieveSourcePath());
        assertNotNull(history.getSubmissionDate());
        assertNotNull(record.getOrcidIdentifier());
        OrcidIdentifier id = record.getOrcidIdentifier();
        assertEquals("0000-0000-0000-0003", id.getPath());
    }
    
    @Resource
    SchemaOrgMBWriterV3 writerV3;
    
    @Test
    public void testSchemaOrgMBWriterV3() throws WebApplicationException, IOException{
        Response response = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) response.getEntity();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writerV3.writeTo(record, record.getClass(), null, null, null, null, out);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        SchemaOrgDocument doc = objectMapper.readerFor(SchemaOrgDocument.class).readValue(out.toString());
        assertTrue(doc.id.endsWith(ORCID));
        assertEquals("Person",doc.type);
        assertEquals("http://schema.org",doc.context);
        assertEquals("Credit Name",doc.name);
        assertEquals("Given Names",doc.givenName);
        assertEquals("Family Name",doc.familyName);
        assertEquals("Other Name PUBLIC",doc.alternateName.get(0));
        assertEquals("WDB",doc.alumniOf.iterator().next().identifier.iterator().next().propertyID);
        //they've been squashed into one because they're all the same Org.
        assertEquals("WDB",doc.affiliation.iterator().next().identifier.iterator().next().propertyID);
        Set<String> fundingIds = Sets.newHashSet();
        for (SchemaOrgExternalID i: doc.worksAndFunding.funder.iterator().next().identifier)
            fundingIds.add(i.propertyID);
        assertEquals(Sets.newHashSet("WDB","grant_number"),fundingIds);
        assertEquals("PUBLIC",doc.worksAndFunding.creator.iterator().next().name);
        assertEquals("http://www.researcherurl.com?id=13",doc.url.get(0));
        assertEquals("public_type",doc.identifier.get(0).propertyID);
        assertEquals( "public_ref",doc.identifier.get(0).value);
    }
}
