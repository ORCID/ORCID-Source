package org.orcid.api.publicV2.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
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
import org.orcid.api.common.writer.schemaorg.SchemaOrgMBWriterV2;
import org.orcid.api.common.writer.schemaorg.SchemaOrgMBWriterV3;
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.api.publicV2.server.delegator.impl.PublicV2ApiServiceDelegatorImpl;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidNonPublicElementException;
import org.orcid.core.exception.SearchStartParameterLimitExceededException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.locale.LocaleManagerImpl;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.impl.OrcidSearchManagerImpl;
import org.orcid.core.manager.impl.OrcidSecurityManagerImpl;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.Locale;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.History;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.PersonalDetails;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.jaxb.model.search_v2.Result;
import org.orcid.jaxb.model.search_v2.Search;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Sets;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml", "classpath:orcid-t1-security-context.xml" })
public class PublicV2ApiServiceDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/BiographyEntityData.xml", "/data/RecordNameEntityData.xml");

    private final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "publicV2ApiServiceDelegator")
    PublicV2ApiServiceDelegator<?, ?, ?, ?, ?, ?, ?, ?, ?> serviceDelegator;

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
        assertEquals(1, educations.getSummaries().size());
        EducationSummary education = educations.getSummaries().get(0);
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
        assertEquals(1, peerReviews.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        PeerReviewSummary peerReview = peerReviews.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0);
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
        assertEquals(1, employments.getSummaries().size());
        EmploymentSummary employment = employments.getSummaries().get(0);
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
        assertNotNull(educations.getSummaries());
        assertEquals(1, educations.getSummaries().size());
        assertEquals(Long.valueOf(20), educations.getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, educations.getSummaries().get(0).getVisibility());
        assertNotNull(educations.getSummaries().get(0).getLastModifiedDate());
        assertNotNull(educations.getSummaries().get(0).getLastModifiedDate().getValue());
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
        assertNotNull(educations.getSummaries());
        assertEquals(1, educations.getSummaries().size());
        assertEquals(Long.valueOf(20), educations.getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, educations.getSummaries().get(0).getVisibility());
        assertNotNull(educations.getSummaries().get(0).getLastModifiedDate());
        assertNotNull(educations.getSummaries().get(0).getLastModifiedDate().getValue());
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
        assertNotNull(employments.getSummaries());
        assertEquals(1, employments.getSummaries().size());
        assertEquals(Long.valueOf(17), employments.getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, employments.getSummaries().get(0).getVisibility());
        assertNotNull(employments.getSummaries().get(0).getLastModifiedDate());
        assertNotNull(employments.getSummaries().get(0).getLastModifiedDate().getValue());
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
        assertNotNull(employments.getSummaries());
        assertEquals(1, employments.getSummaries().size());
        assertEquals(Long.valueOf(17), employments.getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, employments.getSummaries().get(0).getVisibility());
        assertNotNull(employments.getSummaries().get(0).getLastModifiedDate());
        assertNotNull(employments.getSummaries().get(0).getLastModifiedDate().getValue());
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
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertEquals(1, p.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), p.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate().getValue());
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
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertEquals(1, p.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), p.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
        assertNotNull(p.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate().getValue());
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

        PublicV2ApiServiceDelegatorImpl delegator = new PublicV2ApiServiceDelegatorImpl();
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

        PublicV2ApiServiceDelegatorImpl delegator = new PublicV2ApiServiceDelegatorImpl();
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

        PublicV2ApiServiceDelegatorImpl delegator = new PublicV2ApiServiceDelegatorImpl();
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

        PublicV2ApiServiceDelegatorImpl delegator = new PublicV2ApiServiceDelegatorImpl();
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
    public void testViewClient() {
        Response response = serviceDelegator.viewClient("APP-6666666666666666");
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);

        ClientSummary client = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", client.getName());
        assertEquals("A test source client", client.getDescription());
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
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(9), summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/peer-review/9", summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
        assertEquals(Visibility.PUBLIC.value(), summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility().value());

        // Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getLastModifiedDate());
        assertNotNull(summary.getEducations().getLastModifiedDate().getValue());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(1, summary.getEducations().getSummaries().size());
        assertNotNull(summary.getEducations().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(summary.getEducations().getSummaries().get(0).getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(20), summary.getEducations().getSummaries().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/education/20", summary.getEducations().getSummaries().get(0).getPath());
        assertEquals("PUBLIC Department", summary.getEducations().getSummaries().get(0).getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), summary.getEducations().getSummaries().get(0).getVisibility().value());

        // Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getLastModifiedDate());
        assertNotNull(summary.getEmployments().getLastModifiedDate().getValue());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(1, summary.getEmployments().getSummaries().size());
        assertNotNull(summary.getEmployments().getSummaries().get(0).getLastModifiedDate());
        assertNotNull(summary.getEmployments().getSummaries().get(0).getLastModifiedDate().getValue());
        assertEquals(Long.valueOf(17), summary.getEmployments().getSummaries().get(0).getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/17", summary.getEmployments().getSummaries().get(0).getPath());
        assertEquals("PUBLIC Department", summary.getEmployments().getSummaries().get(0).getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), summary.getEmployments().getSummaries().get(0).getVisibility().value());
    }

    private void validateRecord(Record record) {
        assertNotNull(record);
        assertEquals("/" + ORCID, record.getPath());
        validatePerson(record.getPerson());
        validateActivities(record.getActivitiesSummary());
        assertNotNull(record.getHistory());
        assertEquals(OrcidType.USER, record.getOrcidType());
        assertNotNull(record.getPreferences());
        assertEquals(Locale.EN, record.getPreferences().getLocale());
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
    SchemaOrgMBWriterV2 writerV2;
    
    @Test
    public void testSchemaOrgMBWriterV2() throws WebApplicationException, IOException{
        Response response = serviceDelegator.viewRecord(ORCID);
        Record record = (Record) response.getEntity();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writerV2.writeTo(record, record.getClass(), null, null, null, null, out);
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
        assertEquals("WDB",doc.affiliation.iterator().next().identifier.iterator().next().propertyID);
        Set<String> fundingIds = Sets.newHashSet();
        for (SchemaOrgExternalID i: doc.worksAndFunding.funder.iterator().next().identifier)
            fundingIds.add(i.propertyID);
        assertEquals(Sets.newHashSet("WDB","grant_number"),fundingIds);
        assertEquals("PUBLIC",doc.worksAndFunding.creator.iterator().next().name);
        assertEquals("doi",doc.worksAndFunding.creator.iterator().next().identifier.iterator().next().propertyID);
        assertEquals("http://www.researcherurl.com?id=13",doc.url.get(0));
        assertEquals("public_type",doc.identifier.get(0).propertyID);
        assertEquals( "public_ref",doc.identifier.get(0).value);
    }
}
