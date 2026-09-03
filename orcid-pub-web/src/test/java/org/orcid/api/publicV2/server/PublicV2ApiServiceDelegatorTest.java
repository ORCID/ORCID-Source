package org.orcid.api.publicV2.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import org.apache.hc.core5.http.ParseException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument;
import org.orcid.api.common.writer.schemaorg.SchemaOrgMBWriterV2;
import org.orcid.api.publicV2.server.delegator.impl.PublicV2ApiServiceDelegatorImpl;
import org.orcid.api.publicV2.server.security.PublicAPISecurityManagerV2;
import org.orcid.core.common.manager.EventManager;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.OrcidNonPublicElementException;
import org.orcid.core.exception.SearchStartParameterLimitExceededException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.ClientManagerReadOnly;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.ContributorUtils;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.core.utils.SourceUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.common_v2.Country;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.VisibilityType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.Name;
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
import org.orcid.jaxb.model.search_v2.Result;
import org.orcid.jaxb.model.search_v2.Search;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The V2 twin of PublicV3ApiServiceDelegatorTest; the same reasoning applies.
 * The delegator's own behaviour is the path building, the last-modified
 * aggregation, the public-only loop in the affiliation list endpoints, and the
 * fact that PublicAPISecurityManagerV2 is invoked with the right argument
 * before anything else is done to the element. The filtering itself mutates in
 * place, so against a mock it does nothing and is proved instead by
 * PublicAPISecurityManagerV2Test.
 *
 * <p>
 * One difference from V3 worth knowing: this, the non versioned delegator, does
 * not call orcidSecurityManager.checkProfile at all. The profile state guard
 * lives on PublicV2ApiServiceVersionedDelegatorImpl, which wraps this one, and
 * is tested in PublicV2ApiServiceVersionedDelegatorTest.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PublicV2ApiServiceDelegatorTest {

    private static final String ORCID = "0000-0000-0000-0003";

    /**
     * Declared as the implementation rather than the interface because
     * {@code @InjectMocks} needs a concrete type.
     */
    @InjectMocks
    private PublicV2ApiServiceDelegatorImpl serviceDelegator = new PublicV2ApiServiceDelegatorImpl();

    @Mock
    private WorkManagerReadOnly workManagerReadOnly;

    @Mock
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Mock
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Mock
    private PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Mock
    private ActivitiesSummaryManagerReadOnly activitiesSummaryManagerReadOnly;

    @Mock
    private ResearcherUrlManagerReadOnly researcherUrlManagerReadOnly;

    @Mock
    private OtherNameManagerReadOnly otherNameManagerReadOnly;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;

    @Mock
    private PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Mock
    private ProfileKeywordManagerReadOnly profileKeywordManagerReadOnly;

    @Mock
    private AddressManagerReadOnly addressManagerReadOnly;

    @Mock
    private BiographyManagerReadOnly biographyManagerReadOnly;

    /** Distinct from personalDetailsManagerReadOnly; both exist on the impl. */
    @Mock
    private PersonDetailsManagerReadOnly personDetailsManagerReadOnly;

    @Mock
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Mock
    private RecordManagerReadOnly recordManagerReadOnly;

    @Mock
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

    /*
     * Two SourceUtils fields on the class under test, disambiguated by field
     * name: sourceUtilsReadOnly decorates every read endpoint, sourceUtils is
     * used only by viewBulkWorks.
     */
    @Mock
    private SourceUtils sourceUtilsReadOnly;

    @Mock
    private SourceUtils sourceUtils;

    @Mock
    private ContributorUtils contributorUtilsReadOnly;

    @Mock
    private OrcidSearchManager orcidSearchManager;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private PublicAPISecurityManagerV2 publicAPISecurityManagerV2;

    @Mock
    private LocaleManager localeManager;

    /** V2 uses ClientManagerReadOnly where V3 uses ClientDetailsManagerReadOnly. */
    @Mock
    private ClientManagerReadOnly clientManagerReadOnly;

    /** No V3 suffix on this field, unlike the V3 delegator. */
    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnly;

    @Mock
    private EventManager eventManager;

    /** Only collaborator of SchemaOrgMBWriterV2, which is not a Spring bean here. */
    @Mock
    private PIDNormalizationService norm;

    @After
    public void after() {
        SecurityContextHolder.clearContext();
    }

    /*
     * ------------------------------------------------------------------
     * Works
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewWork() {
        Work work = work(11L, Visibility.PUBLIC);
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(work);

        Response response = serviceDelegator.viewWork(ORCID, 11L);

        assertNotNull(response);
        assertSame(work, response.getEntity());
        assertEquals(Long.valueOf(11), work.getPutCode());
        assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(work);
        verify(sourceUtilsReadOnly).setSourceName(work);
    }

    @Test
    public void testViewWorks() {
        WorkSummary summary = workSummary(11L, Visibility.PUBLIC);
        List<WorkSummary> summaries = new ArrayList<WorkSummary>(Arrays.asList(summary));
        Works grouped = works(summary);
        when(workManagerReadOnly.getWorksSummaryList(ORCID)).thenReturn(summaries);
        when(workManagerReadOnly.groupWorks(summaries, true)).thenReturn(grouped);

        Response response = serviceDelegator.viewWorks(ORCID);

        Works entity = (Works) response.getEntity();
        assertSame(grouped, entity);
        assertEquals("/0000-0000-0000-0003/works", entity.getPath());
        assertEquals("/0000-0000-0000-0003/work/11", entity.getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals(summary.getLastModifiedDate(), entity.getLastModifiedDate());
        // the grouping must be asked for public elements only
        verify(workManagerReadOnly).groupWorks(summaries, true);
        verify(publicAPISecurityManagerV2).filter(grouped);
        verify(sourceUtilsReadOnly).setSourceName(grouped);
    }

    @Test
    public void testViewBulkWorks() {
        WorkBulk bulk = new WorkBulk();
        Work work = work(11L, Visibility.PUBLIC);
        bulk.setBulk(new ArrayList<BulkElement>(Arrays.asList(work)));
        when(profileEntityManagerReadOnly.findByOrcid(ORCID)).thenReturn(new ProfileEntity());
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13")).thenReturn(bulk);

        Response response = serviceDelegator.viewBulkWorks(ORCID, "11,12,13");

        assertNotNull(response);
        assertSame(bulk, response.getEntity());
        assertEquals("/0000-0000-0000-0003/work/11", work.getPath());
        // which put codes come back as OrcidErrors is decided by
        // PublicAPISecurityManagerV2.filter(WorkBulk); proved there
        verify(publicAPISecurityManagerV2).filter(bulk);
        // note the OTHER SourceUtils: viewBulkWorks uses sourceUtils
        verify(sourceUtils).setSourceName(bulk);
        verifyNoInteractions(sourceUtilsReadOnly);
    }

    @Test
    public void testViewBulkWorksNonExistentUser() {
        when(profileEntityManagerReadOnly.findByOrcid("0000-0000-0000-000X")).thenReturn(null);
        try {
            serviceDelegator.viewBulkWorks("0000-0000-0000-000X", "11,12,13");
            fail();
        } catch (OrcidNoResultException expected) {
        }
        verifyNoInteractions(workManagerReadOnly);
    }

    @Test
    public void testGetPublicWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Work work = work(11L, Visibility.PUBLIC);
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(work);

        Response r = serviceDelegator.viewWork(ORCID, 11L);

        assertEquals(Long.valueOf(11), ((Work) r.getEntity()).getPutCode());
        // a read-limited token must not exempt the caller from the public gate
        verify(publicAPISecurityManagerV2).checkIsPublic(work);
    }

    @Test
    public void testGetPublicWorks() {
        WorkSummary summary = workSummary(11L, Visibility.PUBLIC);
        List<WorkSummary> summaries = new ArrayList<WorkSummary>(Arrays.asList(summary));
        Works grouped = works(summary);
        when(workManagerReadOnly.getWorksSummaryList(ORCID)).thenReturn(summaries);
        when(workManagerReadOnly.groupWorks(summaries, true)).thenReturn(grouped);

        Response r = serviceDelegator.viewWorks(ORCID);

        Works entity = (Works) r.getEntity();
        assertEquals(1, entity.getWorkGroup().size());
        assertEquals(Long.valueOf(11), entity.getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(grouped);
    }

    @Test
    public void testGetPublicWorksUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        WorkSummary summary = workSummary(11L, Visibility.PUBLIC);
        List<WorkSummary> summaries = new ArrayList<WorkSummary>(Arrays.asList(summary));
        Works grouped = works(summary);
        when(workManagerReadOnly.getWorksSummaryList(ORCID)).thenReturn(summaries);
        when(workManagerReadOnly.groupWorks(summaries, true)).thenReturn(grouped);

        Response r = serviceDelegator.viewWorks(ORCID);

        assertNotNull(r.getEntity());
        verify(workManagerReadOnly).groupWorks(summaries, true);
        verify(publicAPISecurityManagerV2).filter(grouped);
    }

    @Test
    public void testGetLimitedWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Work work = work(12L, Visibility.LIMITED);
        when(workManagerReadOnly.getWork(ORCID, 12L)).thenReturn(work);
        assertNonPublicElementIsRefused(work, () -> serviceDelegator.viewWork(ORCID, 12L));
        assertNull(work.getPath());
    }

    @Test
    public void testGetPrivateWorkUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Work work = work(13L, Visibility.PRIVATE);
        when(workManagerReadOnly.getWork(ORCID, 13L)).thenReturn(work);
        assertNonPublicElementIsRefused(work, () -> serviceDelegator.viewWork(ORCID, 13L));
        assertNull(work.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Fundings
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewFunding() {
        Funding funding = funding(10L, Visibility.PUBLIC);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(funding);

        Response response = serviceDelegator.viewFunding(ORCID, 10L);

        assertSame(funding, response.getEntity());
        assertEquals("/0000-0000-0000-0003/funding/10", funding.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(funding);
        verify(sourceUtilsReadOnly).setSourceName(funding);
        // the credit name on a contributor is nulled here, not by the delegator
        verify(contributorUtilsReadOnly).filterContributorPrivateData(funding);
    }

    @Test
    public void testViewFundings() {
        FundingSummary summary = fundingSummary(10L, Visibility.PUBLIC);
        List<FundingSummary> summaries = new ArrayList<FundingSummary>(Arrays.asList(summary));
        Fundings grouped = fundings(summary);
        when(profileFundingManagerReadOnly.getFundingSummaryList(ORCID)).thenReturn(summaries);
        when(profileFundingManagerReadOnly.groupFundings(summaries, true)).thenReturn(grouped);

        Response response = serviceDelegator.viewFundings(ORCID);

        Fundings entity = (Fundings) response.getEntity();
        assertSame(grouped, entity);
        assertEquals("/0000-0000-0000-0003/fundings", entity.getPath());
        assertEquals("/0000-0000-0000-0003/funding/10", entity.getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertEquals(summary.getLastModifiedDate(), entity.getLastModifiedDate());
        verify(profileFundingManagerReadOnly).groupFundings(summaries, true);
        verify(publicAPISecurityManagerV2).filter(grouped);
        verify(sourceUtilsReadOnly).setSourceName(grouped);
    }

    @Test
    public void testGetPublicFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Funding funding = funding(10L, Visibility.PUBLIC);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(funding);

        Response r = serviceDelegator.viewFunding(ORCID, 10L);

        assertEquals(Long.valueOf(10), ((Funding) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(funding);
    }

    @Test
    public void testGetPublicFundings() {
        FundingSummary summary = fundingSummary(10L, Visibility.PUBLIC);
        List<FundingSummary> summaries = new ArrayList<FundingSummary>(Arrays.asList(summary));
        Fundings grouped = fundings(summary);
        when(profileFundingManagerReadOnly.getFundingSummaryList(ORCID)).thenReturn(summaries);
        when(profileFundingManagerReadOnly.groupFundings(summaries, true)).thenReturn(grouped);

        Response r = serviceDelegator.viewFundings(ORCID);

        Fundings entity = (Fundings) r.getEntity();
        assertEquals(1, entity.getFundingGroup().size());
        assertEquals(Long.valueOf(10), entity.getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(grouped);
    }

    @Test
    public void testGetPublicFundingsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        FundingSummary summary = fundingSummary(10L, Visibility.PUBLIC);
        List<FundingSummary> summaries = new ArrayList<FundingSummary>(Arrays.asList(summary));
        Fundings grouped = fundings(summary);
        when(profileFundingManagerReadOnly.getFundingSummaryList(ORCID)).thenReturn(summaries);
        when(profileFundingManagerReadOnly.groupFundings(summaries, true)).thenReturn(grouped);

        Response r = serviceDelegator.viewFundings(ORCID);

        assertNotNull(r.getEntity());
        verify(profileFundingManagerReadOnly).groupFundings(summaries, true);
        verify(publicAPISecurityManagerV2).filter(grouped);
    }

    @Test
    public void testGetLimitedFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Funding funding = funding(11L, Visibility.LIMITED);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 11L)).thenReturn(funding);
        assertNonPublicElementIsRefused(funding, () -> serviceDelegator.viewFunding(ORCID, 11L));
        assertNull(funding.getPath());
        verifyNoInteractions(contributorUtilsReadOnly);
    }

    @Test
    public void testGetPrivateFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Funding funding = funding(12L, Visibility.PRIVATE);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 12L)).thenReturn(funding);
        assertNonPublicElementIsRefused(funding, () -> serviceDelegator.viewFunding(ORCID, 12L));
        assertNull(funding.getPath());
        verifyNoInteractions(contributorUtilsReadOnly);
    }

    /*
     * ------------------------------------------------------------------
     * Peer reviews
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewPeerReview() {
        PeerReview peerReview = peerReview(9L, Visibility.PUBLIC);
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 9L)).thenReturn(peerReview);

        Response response = serviceDelegator.viewPeerReview(ORCID, 9L);

        assertSame(peerReview, response.getEntity());
        assertEquals("/0000-0000-0000-0003/peer-review/9", peerReview.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(peerReview);
        verify(sourceUtilsReadOnly).setSourceName(peerReview);
    }

    @Test
    public void testViewPeerReviews() {
        PeerReviewSummary summary = peerReviewSummary(9L, Visibility.PUBLIC);
        List<PeerReviewSummary> summaries = new ArrayList<PeerReviewSummary>(Arrays.asList(summary));
        PeerReviews grouped = peerReviews(summary);
        when(peerReviewManagerReadOnly.getPeerReviewSummaryList(ORCID)).thenReturn(summaries);
        when(peerReviewManagerReadOnly.groupPeerReviews(summaries, true)).thenReturn(grouped);

        Response response = serviceDelegator.viewPeerReviews(ORCID);

        PeerReviews entity = (PeerReviews) response.getEntity();
        assertSame(grouped, entity);
        assertEquals("/0000-0000-0000-0003/peer-reviews", entity.getPath());
        assertEquals("/0000-0000-0000-0003/peer-review/9", entity.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
        assertEquals(summary.getLastModifiedDate(), entity.getLastModifiedDate());
        verify(peerReviewManagerReadOnly).groupPeerReviews(summaries, true);
        verify(publicAPISecurityManagerV2).filter(grouped);
        verify(sourceUtilsReadOnly).setSourceName(grouped);
    }

    @Test
    public void testGetPublicPeerReviewUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PeerReview peerReview = peerReview(9L, Visibility.PUBLIC);
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 9L)).thenReturn(peerReview);

        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);

        assertEquals(Long.valueOf(9), ((PeerReview) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(peerReview);
    }

    @Test
    public void testGetPublicPeerReviews() {
        PeerReviewSummary summary = peerReviewSummary(9L, Visibility.PUBLIC);
        List<PeerReviewSummary> summaries = new ArrayList<PeerReviewSummary>(Arrays.asList(summary));
        PeerReviews grouped = peerReviews(summary);
        when(peerReviewManagerReadOnly.getPeerReviewSummaryList(ORCID)).thenReturn(summaries);
        when(peerReviewManagerReadOnly.groupPeerReviews(summaries, true)).thenReturn(grouped);

        Response r = serviceDelegator.viewPeerReviews(ORCID);

        PeerReviews entity = (PeerReviews) r.getEntity();
        assertEquals(1, entity.getPeerReviewGroup().size());
        assertNotNull(entity.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(grouped);
    }

    @Test
    public void testGetPublicPeerReviewsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PeerReviewSummary summary = peerReviewSummary(9L, Visibility.PUBLIC);
        List<PeerReviewSummary> summaries = new ArrayList<PeerReviewSummary>(Arrays.asList(summary));
        PeerReviews grouped = peerReviews(summary);
        when(peerReviewManagerReadOnly.getPeerReviewSummaryList(ORCID)).thenReturn(summaries);
        when(peerReviewManagerReadOnly.groupPeerReviews(summaries, true)).thenReturn(grouped);

        Response r = serviceDelegator.viewPeerReviews(ORCID);

        assertNotNull(r.getEntity());
        verify(peerReviewManagerReadOnly).groupPeerReviews(summaries, true);
        verify(publicAPISecurityManagerV2).filter(grouped);
    }

    @Test
    public void testGetLimitedPeerReviewUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PeerReview peerReview = peerReview(10L, Visibility.LIMITED);
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 10L)).thenReturn(peerReview);
        assertNonPublicElementIsRefused(peerReview, () -> serviceDelegator.viewPeerReview(ORCID, 10L));
        assertNull(peerReview.getPath());
    }

    @Test
    public void testGetPrivatePeerReviewUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PeerReview peerReview = peerReview(11L, Visibility.PRIVATE);
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 11L)).thenReturn(peerReview);
        assertNonPublicElementIsRefused(peerReview, () -> serviceDelegator.viewPeerReview(ORCID, 11L));
        assertNull(peerReview.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Educations
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewEducation() {
        Education education = education(20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(education);

        Response response = serviceDelegator.viewEducation(ORCID, 20L);

        assertSame(education, response.getEntity());
        assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(education);
        verify(sourceUtilsReadOnly).setSourceName(education);
    }

    /**
     * The affiliation list endpoints do their own visibility filtering, in the
     * delegator. That loop is the delegator's own Java and is the one place in
     * this file where a "non public elements are dropped" assertion is not
     * vacuous.
     */
    @Test
    public void testViewEducations() {
        EducationSummary publicSummary = educationSummary(20L, Visibility.PUBLIC);
        EducationSummary limitedSummary = educationSummary(21L, Visibility.LIMITED);
        EducationSummary privateSummary = educationSummary(22L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID))
                .thenReturn(new ArrayList<EducationSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));

        Response response = serviceDelegator.viewEducations(ORCID);

        Educations entity = (Educations) response.getEntity();
        assertEquals("/0000-0000-0000-0003/educations", entity.getPath());
        assertEquals(1, entity.getSummaries().size());
        assertSame(publicSummary, entity.getSummaries().get(0));
        assertEquals("/0000-0000-0000-0003/education/20", entity.getSummaries().get(0).getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
        verify(sourceUtilsReadOnly).setSourceName(publicSummary);
        verify(sourceUtilsReadOnly, never()).setSourceName(limitedSummary);
        verify(sourceUtilsReadOnly, never()).setSourceName(privateSummary);
    }

    @Test
    public void testGetPublicEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Education education = education(20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(education);

        Response r = serviceDelegator.viewEducation(ORCID, 20L);

        assertEquals(Long.valueOf(20), ((Education) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(education);
    }

    @Test
    public void testGetPublicEducations() {
        EducationSummary publicSummary = educationSummary(20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID))
                .thenReturn(new ArrayList<EducationSummary>(Arrays.asList(publicSummary)));

        Response r = serviceDelegator.viewEducations(ORCID);

        Educations entity = (Educations) r.getEntity();
        assertEquals(1, entity.getSummaries().size());
        assertEquals(Long.valueOf(20), entity.getSummaries().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicEducationsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        EducationSummary publicSummary = educationSummary(20L, Visibility.PUBLIC);
        EducationSummary limitedSummary = educationSummary(21L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID))
                .thenReturn(new ArrayList<EducationSummary>(Arrays.asList(publicSummary, limitedSummary)));

        Response r = serviceDelegator.viewEducations(ORCID);

        Educations entity = (Educations) r.getEntity();
        assertEquals(1, entity.getSummaries().size());
        assertSame(publicSummary, entity.getSummaries().get(0));
    }

    @Test
    public void testGetLimitedEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Education education = education(21L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 21L)).thenReturn(education);
        assertNonPublicElementIsRefused(education, () -> serviceDelegator.viewEducation(ORCID, 21L));
        assertNull(education.getPath());
    }

    @Test
    public void testGetPrivateEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Education education = education(22L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 22L)).thenReturn(education);
        assertNonPublicElementIsRefused(education, () -> serviceDelegator.viewEducation(ORCID, 22L));
        assertNull(education.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Employments
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewEmployment() {
        Employment employment = employment(17L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(employment);

        Response response = serviceDelegator.viewEmployment(ORCID, 17L);

        assertSame(employment, response.getEntity());
        assertEquals("/0000-0000-0000-0003/employment/17", employment.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(employment);
        verify(sourceUtilsReadOnly).setSourceName(employment);
    }

    @Test
    public void testViewEmployments() {
        EmploymentSummary publicSummary = employmentSummary(17L, Visibility.PUBLIC);
        EmploymentSummary limitedSummary = employmentSummary(18L, Visibility.LIMITED);
        EmploymentSummary privateSummary = employmentSummary(19L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID))
                .thenReturn(new ArrayList<EmploymentSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));

        Response response = serviceDelegator.viewEmployments(ORCID);

        Employments entity = (Employments) response.getEntity();
        assertEquals("/0000-0000-0000-0003/employments", entity.getPath());
        assertEquals(1, entity.getSummaries().size());
        assertSame(publicSummary, entity.getSummaries().get(0));
        assertEquals("/0000-0000-0000-0003/employment/17", entity.getSummaries().get(0).getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Employment employment = employment(17L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(employment);

        Response r = serviceDelegator.viewEmployment(ORCID, 17L);

        assertEquals(Long.valueOf(17), ((Employment) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(employment);
    }

    @Test
    public void testGetPublicEmployments() {
        EmploymentSummary publicSummary = employmentSummary(17L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID))
                .thenReturn(new ArrayList<EmploymentSummary>(Arrays.asList(publicSummary)));

        Response r = serviceDelegator.viewEmployments(ORCID);

        Employments entity = (Employments) r.getEntity();
        assertEquals(1, entity.getSummaries().size());
        assertEquals(Long.valueOf(17), entity.getSummaries().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicEmploymentsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        EmploymentSummary publicSummary = employmentSummary(17L, Visibility.PUBLIC);
        EmploymentSummary limitedSummary = employmentSummary(18L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID))
                .thenReturn(new ArrayList<EmploymentSummary>(Arrays.asList(publicSummary, limitedSummary)));

        Response r = serviceDelegator.viewEmployments(ORCID);

        Employments entity = (Employments) r.getEntity();
        assertEquals(1, entity.getSummaries().size());
        assertSame(publicSummary, entity.getSummaries().get(0));
    }

    @Test
    public void testGetLimitedEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Employment employment = employment(18L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 18L)).thenReturn(employment);
        assertNonPublicElementIsRefused(employment, () -> serviceDelegator.viewEmployment(ORCID, 18L));
        assertNull(employment.getPath());
    }

    @Test
    public void testGetPrivateEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Employment employment = employment(19L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 19L)).thenReturn(employment);
        assertNonPublicElementIsRefused(employment, () -> serviceDelegator.viewEmployment(ORCID, 19L));
        assertNull(employment.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Person elements
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewOtherName() {
        OtherName otherName = otherName(13L, Visibility.PUBLIC, "Other Name PUBLIC");
        when(otherNameManagerReadOnly.getOtherName(ORCID, 13L)).thenReturn(otherName);

        Response response = serviceDelegator.viewOtherName(ORCID, 13L);

        assertSame(otherName, response.getEntity());
        assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(otherName);
        verify(sourceUtilsReadOnly).setSourceName(otherName);
    }

    @Test
    public void testViewOtherNames() {
        OtherName otherName = otherName(13L, Visibility.PUBLIC, "Other Name PUBLIC");
        OtherNames otherNames = new OtherNames();
        otherNames.getOtherNames().add(otherName);
        when(otherNameManagerReadOnly.getPublicOtherNames(ORCID)).thenReturn(otherNames);

        Response response = serviceDelegator.viewOtherNames(ORCID);

        assertSame(otherNames, response.getEntity());
        assertEquals("/0000-0000-0000-0003/other-names", otherNames.getPath());
        assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
        assertEquals(otherName.getLastModifiedDate(), otherNames.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(otherNames);
        verify(sourceUtilsReadOnly).setSourceName(otherNames);
    }

    @Test
    public void testGetPublicOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        OtherName otherName = otherName(13L, Visibility.PUBLIC, "Other Name PUBLIC");
        when(otherNameManagerReadOnly.getOtherName(ORCID, 13L)).thenReturn(otherName);

        Response r = serviceDelegator.viewOtherName(ORCID, 13L);

        assertEquals(Long.valueOf(13), ((OtherName) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(otherName);
    }

    @Test
    public void testGetLimitedOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        OtherName otherName = otherName(14L, Visibility.LIMITED, "Other Name LIMITED");
        when(otherNameManagerReadOnly.getOtherName(ORCID, 14L)).thenReturn(otherName);
        assertNonPublicElementIsRefused(otherName, () -> serviceDelegator.viewOtherName(ORCID, 14L));
        assertNull(otherName.getPath());
    }

    @Test
    public void testGetPrivateOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        OtherName otherName = otherName(15L, Visibility.PRIVATE, "Other Name PRIVATE");
        when(otherNameManagerReadOnly.getOtherName(ORCID, 15L)).thenReturn(otherName);
        assertNonPublicElementIsRefused(otherName, () -> serviceDelegator.viewOtherName(ORCID, 15L));
        assertNull(otherName.getPath());
    }

    @Test
    public void testViewKeyword() {
        Keyword keyword = keyword(9L, Visibility.PUBLIC, "PUBLIC");
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(keyword);

        Response response = serviceDelegator.viewKeyword(ORCID, 9L);

        assertSame(keyword, response.getEntity());
        assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(keyword);
        verify(sourceUtilsReadOnly).setSourceName(keyword);
    }

    @Test
    public void testViewKeywords() {
        Keyword keyword = keyword(9L, Visibility.PUBLIC, "PUBLIC");
        Keywords keywords = new Keywords();
        keywords.getKeywords().add(keyword);
        when(profileKeywordManagerReadOnly.getPublicKeywords(ORCID)).thenReturn(keywords);

        Response response = serviceDelegator.viewKeywords(ORCID);

        assertSame(keywords, response.getEntity());
        assertEquals("/0000-0000-0000-0003/keywords", keywords.getPath());
        assertEquals("/0000-0000-0000-0003/keywords/9", keyword.getPath());
        assertEquals(keyword.getLastModifiedDate(), keywords.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(keywords);
        verify(sourceUtilsReadOnly).setSourceName(keywords);
    }

    @Test
    public void testGetPublicKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Keyword keyword = keyword(9L, Visibility.PUBLIC, "PUBLIC");
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(keyword);

        Response r = serviceDelegator.viewKeyword(ORCID, 9L);

        assertEquals(Long.valueOf(9), ((Keyword) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(keyword);
    }

    @Test
    public void testGetLimitedKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Keyword keyword = keyword(10L, Visibility.LIMITED, "LIMITED");
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 10L)).thenReturn(keyword);
        assertNonPublicElementIsRefused(keyword, () -> serviceDelegator.viewKeyword(ORCID, 10L));
        assertNull(keyword.getPath());
    }

    @Test
    public void testGetPrivateKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Keyword keyword = keyword(11L, Visibility.PRIVATE, "PRIVATE");
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 11L)).thenReturn(keyword);
        assertNonPublicElementIsRefused(keyword, () -> serviceDelegator.viewKeyword(ORCID, 11L));
        assertNull(keyword.getPath());
    }

    @Test
    public void testViewExternalIdentifier() {
        PersonExternalIdentifier extId = externalIdentifier(13L, Visibility.PUBLIC, "public_type", "http://ext-id/public_ref");
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L)).thenReturn(extId);

        Response response = serviceDelegator.viewExternalIdentifier(ORCID, 13L);

        assertSame(extId, response.getEntity());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", extId.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(extId);
        verify(sourceUtilsReadOnly).setSourceName(extId);
    }

    /**
     * The order the external identifiers arrive in is a DAO ordering concern,
     * so the old positional assertions have been dropped: against a mock they
     * only restate the order this test itself built. What is left is the
     * delegator's own contribution, a path on the container and on every
     * element.
     */
    @Test
    public void testViewExternalIdentifiers() {
        PersonExternalIdentifier first = externalIdentifier(19L, Visibility.PUBLIC, "self_public_user_obo_type", "http://ext-id/self/obo/public");
        PersonExternalIdentifier second = externalIdentifier(13L, Visibility.PUBLIC, "public_type", "http://ext-id/public_ref");
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.getExternalIdentifiers().add(first);
        extIds.getExternalIdentifiers().add(second);
        when(externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(ORCID)).thenReturn(extIds);

        Response response = serviceDelegator.viewExternalIdentifiers(ORCID);

        assertSame(extIds, response.getEntity());
        assertEquals("/0000-0000-0000-0003/external-identifiers", extIds.getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers/19", first.getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", second.getPath());
        // the container carries the latest of its elements, put code 19 here
        assertEquals(first.getLastModifiedDate(), extIds.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(extIds);
        verify(sourceUtilsReadOnly).setSourceName(extIds);
    }

    @Test
    public void testGetPublicExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PersonExternalIdentifier extId = externalIdentifier(13L, Visibility.PUBLIC, "public_type", "http://ext-id/public_ref");
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L)).thenReturn(extId);

        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);

        assertEquals(Long.valueOf(13), ((PersonExternalIdentifier) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(extId);
    }

    @Test
    public void testGetLimitedExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PersonExternalIdentifier extId = externalIdentifier(14L, Visibility.LIMITED, "limited_type", "http://ext-id/limited_ref");
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 14L)).thenReturn(extId);
        assertNonPublicElementIsRefused(extId, () -> serviceDelegator.viewExternalIdentifier(ORCID, 14L));
        assertNull(extId.getPath());
    }

    @Test
    public void testGetPrivateExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PersonExternalIdentifier extId = externalIdentifier(15L, Visibility.PRIVATE, "private_type", "http://ext-id/private_ref");
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 15L)).thenReturn(extId);
        assertNonPublicElementIsRefused(extId, () -> serviceDelegator.viewExternalIdentifier(ORCID, 15L));
        assertNull(extId.getPath());
    }

    @Test
    public void testViewResearcherUrl() {
        ResearcherUrl rUrl = researcherUrl(13L, Visibility.PUBLIC);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L)).thenReturn(rUrl);

        Response response = serviceDelegator.viewResearcherUrl(ORCID, 13L);

        assertSame(rUrl, response.getEntity());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(rUrl);
        verify(sourceUtilsReadOnly).setSourceName(rUrl);
    }

    /**
     * viewResearcherUrls is the one list endpoint that never calls
     * PublicAPISecurityManagerV2.filter; it relies entirely on
     * getPublicResearcherUrls returning public rows only. That asymmetry is
     * asserted rather than assumed.
     */
    @Test
    public void testViewResearcherUrls() {
        ResearcherUrl rUrl = researcherUrl(13L, Visibility.PUBLIC);
        ResearcherUrls rUrls = new ResearcherUrls();
        rUrls.getResearcherUrls().add(rUrl);
        when(researcherUrlManagerReadOnly.getPublicResearcherUrls(ORCID)).thenReturn(rUrls);

        Response response = serviceDelegator.viewResearcherUrls(ORCID);

        assertSame(rUrls, response.getEntity());
        assertEquals("/0000-0000-0000-0003/researcher-urls", rUrls.getPath());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", rUrl.getPath());
        assertEquals(rUrl.getLastModifiedDate(), rUrls.getLastModifiedDate());
        verify(researcherUrlManagerReadOnly).getPublicResearcherUrls(ORCID);
        verifyNoInteractions(publicAPISecurityManagerV2);
        verify(sourceUtilsReadOnly).setSourceName(rUrls);
    }

    @Test
    public void testGetPublicResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        ResearcherUrl rUrl = researcherUrl(13L, Visibility.PUBLIC);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L)).thenReturn(rUrl);

        Response r = serviceDelegator.viewResearcherUrl(ORCID, 13L);

        assertEquals(Long.valueOf(13), ((ResearcherUrl) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(rUrl);
    }

    @Test
    public void testGetLimitedResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        ResearcherUrl rUrl = researcherUrl(14L, Visibility.LIMITED);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 14L)).thenReturn(rUrl);
        assertNonPublicElementIsRefused(rUrl, () -> serviceDelegator.viewResearcherUrl(ORCID, 14L));
        assertNull(rUrl.getPath());
    }

    @Test
    public void testGetPrivateResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        ResearcherUrl rUrl = researcherUrl(15L, Visibility.PRIVATE);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 15L)).thenReturn(rUrl);
        assertNonPublicElementIsRefused(rUrl, () -> serviceDelegator.viewResearcherUrl(ORCID, 15L));
        assertNull(rUrl.getPath());
    }

    @Test
    public void testViewAddress() {
        Address address = address(9L, Visibility.PUBLIC);
        when(addressManagerReadOnly.getAddress(ORCID, 9L)).thenReturn(address);

        Response response = serviceDelegator.viewAddress(ORCID, 9L);

        assertSame(address, response.getEntity());
        assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(address);
        verify(sourceUtilsReadOnly).setSourceName(address);
    }

    @Test
    public void testViewAddresses() {
        Address address = address(9L, Visibility.PUBLIC);
        Addresses addresses = new Addresses();
        addresses.getAddress().add(address);
        when(addressManagerReadOnly.getPublicAddresses(ORCID)).thenReturn(addresses);

        Response response = serviceDelegator.viewAddresses(ORCID);

        assertSame(addresses, response.getEntity());
        assertEquals("/0000-0000-0000-0003/address", addresses.getPath());
        assertEquals("/0000-0000-0000-0003/address/9", address.getPath());
        assertEquals(address.getLastModifiedDate(), addresses.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(addresses);
        verify(sourceUtilsReadOnly).setSourceName(addresses);
    }

    @Test
    public void testGetPublicAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Address address = address(9L, Visibility.PUBLIC);
        when(addressManagerReadOnly.getAddress(ORCID, 9L)).thenReturn(address);

        Response r = serviceDelegator.viewAddress(ORCID, 9L);

        assertEquals(Long.valueOf(9), ((Address) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV2).checkIsPublic(address);
    }

    @Test
    public void testGetLimitedAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Address address = address(10L, Visibility.LIMITED);
        when(addressManagerReadOnly.getAddress(ORCID, 10L)).thenReturn(address);
        assertNonPublicElementIsRefused(address, () -> serviceDelegator.viewAddress(ORCID, 10L));
        assertNull(address.getPath());
    }

    /**
     * The database version of this test read put code 10, the same LIMITED
     * address the limited test reads, so the private case was never covered.
     * It now uses a genuinely private element.
     */
    @Test
    public void testGetPrivateAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Address address = address(11L, Visibility.PRIVATE);
        when(addressManagerReadOnly.getAddress(ORCID, 11L)).thenReturn(address);
        assertNonPublicElementIsRefused(address, () -> serviceDelegator.viewAddress(ORCID, 11L));
        assertNull(address.getPath());
    }

    /**
     * That the response holds exactly the public emails is enforced by
     * EmailDao's "visibility = 'PUBLIC'" predicate and belongs to the
     * persistence tests; stubbing getPublicEmails with public rows and then
     * counting them would prove nothing here.
     */
    @Test
    public void testViewEmails() {
        Email email = email("public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC, 1L);
        Emails emails = new Emails();
        emails.getEmails().add(email);
        when(emailManagerReadOnly.getPublicEmails(ORCID)).thenReturn(emails);

        Response response = serviceDelegator.viewEmails(ORCID);

        assertSame(emails, response.getEntity());
        assertEquals("/0000-0000-0000-0003/email", emails.getPath());
        assertEquals(email.getLastModifiedDate(), emails.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(emails);
        verify(sourceUtilsReadOnly).setSourceName(emails);
    }

    @Test
    public void testGetPublicBiographyUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Biography bio = biography(Visibility.PUBLIC);
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(bio);

        Response r = serviceDelegator.viewBiography(ORCID);

        assertSame(bio, r.getEntity());
        assertEquals("/0000-0000-0000-0003/biography", bio.getPath());
        verify(publicAPISecurityManagerV2).checkIsPublic(bio);
    }

    @Test
    public void testGetLimitedBiographyUsingToken() {
        String orcid = "0000-0000-0000-0002";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.READ_LIMITED);
        Biography bio = biography(Visibility.LIMITED);
        when(biographyManagerReadOnly.getBiography(orcid)).thenReturn(bio);
        doThrow(new OrcidNonPublicElementException()).when(publicAPISecurityManagerV2).checkIsPublic(bio);

        try {
            serviceDelegator.viewBiography(orcid);
            fail();
        } catch (OrcidNonPublicElementException expected) {
        }
        assertNull(bio.getPath());
    }

    @Test
    public void testGetPrivateBiographyUsingToken() {
        String orcid = "0000-0000-0000-0001";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.READ_LIMITED);
        Biography bio = biography(Visibility.PRIVATE);
        when(biographyManagerReadOnly.getBiography(orcid)).thenReturn(bio);
        doThrow(new OrcidNonPublicElementException()).when(publicAPISecurityManagerV2).checkIsPublic(bio);

        try {
            serviceDelegator.viewBiography(orcid);
            fail();
        } catch (OrcidNonPublicElementException expected) {
        }
        assertNull(bio.getPath());
    }

    @Test
    public void testFindPersonalDetails() {
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setName(name());
        personalDetails.setBiography(biography(Visibility.PUBLIC));
        OtherNames otherNames = new OtherNames();
        OtherName otherName = otherName(13L, Visibility.PUBLIC, "Other Name PUBLIC");
        otherNames.getOtherNames().add(otherName);
        personalDetails.setOtherNames(otherNames);
        when(personalDetailsManagerReadOnly.getPublicPersonalDetails(ORCID)).thenReturn(personalDetails);

        Response response = serviceDelegator.viewPersonalDetails(ORCID);

        assertSame(personalDetails, response.getEntity());
        assertEquals("/0000-0000-0000-0003/personal-details", personalDetails.getPath());
        assertEquals("/0000-0000-0000-0003/biography", personalDetails.getBiography().getPath());
        assertEquals("/0000-0000-0000-0003/other-names", personalDetails.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/other-names/13", otherName.getPath());
        assertNotNull(personalDetails.getLastModifiedDate());
        verify(publicAPISecurityManagerV2).filter(personalDetails);
        verify(sourceUtilsReadOnly).setSourceName(personalDetails);
    }

    @Test
    public void testFindPerson() {
        Person person = person();
        when(personDetailsManagerReadOnly.getPublicPersonDetails(ORCID)).thenReturn(person);

        Response response = serviceDelegator.viewPerson(ORCID);

        assertSame(person, response.getEntity());
        validatePersonPaths(person);
        verify(publicAPISecurityManagerV2).filter(person);
        verify(sourceUtilsReadOnly).setSourceName(person);
    }

    @Test
    public void testValidatePersonUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Person person = person();
        when(personDetailsManagerReadOnly.getPublicPersonDetails(ORCID)).thenReturn(person);

        Response response = serviceDelegator.viewPerson(ORCID);

        assertSame(person, response.getEntity());
        validatePersonPaths(person);
        // the token buys nothing on the public API: the gate still runs
        verify(publicAPISecurityManagerV2).filter(person);
    }

    /*
     * ------------------------------------------------------------------
     * Activities and record
     * ------------------------------------------------------------------
     */

    @Test
    public void testFindActivityDetails() {
        ActivitiesSummary summary = activitiesSummary();
        when(activitiesSummaryManagerReadOnly.getPublicActivitiesSummary(ORCID)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        assertSame(summary, response.getEntity());
        validateActivityPaths(summary);
        verify(publicAPISecurityManagerV2).filter(summary);
        verify(sourceUtilsReadOnly).setSourceName(summary);
    }

    @Test
    public void testValidateActivitiesUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        ActivitiesSummary summary = activitiesSummary();
        when(activitiesSummaryManagerReadOnly.getPublicActivitiesSummary(ORCID)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        assertSame(summary, response.getEntity());
        validateActivityPaths(summary);
        verify(publicAPISecurityManagerV2).filter(summary);
    }

    @Test
    public void testFindRecord() {
        Record record = record();
        when(recordManagerReadOnly.getPublicRecord(ORCID)).thenReturn(record);

        Response response = serviceDelegator.viewRecord(ORCID);

        assertSame(record, response.getEntity());
        assertEquals("/" + ORCID, record.getPath());
        validatePersonPaths(record.getPerson());
        validateActivityPaths(record.getActivitiesSummary());
        verify(publicAPISecurityManagerV2).filter(record);
        verify(sourceUtilsReadOnly).setSourceName(record.getPerson());
        verify(sourceUtilsReadOnly).setSourceName(record.getActivitiesSummary());
    }

    @Test
    public void testValidateRecordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Record record = record();
        when(recordManagerReadOnly.getPublicRecord(ORCID)).thenReturn(record);

        Response response = serviceDelegator.viewRecord(ORCID);

        assertSame(record, response.getEntity());
        assertEquals("/" + ORCID, record.getPath());
        verify(publicAPISecurityManagerV2).filter(record);
    }

    /*
     * ------------------------------------------------------------------
     * Search
     * ------------------------------------------------------------------
     */

    @Test
    public void testSearchByQuery() throws ParseException {
        Search search = new Search();
        Result result = new Result();
        result.setOrcidIdentifier(new OrcidIdentifier("some-orcid-id"));
        search.getResults().add(result);
        when(orcidSearchManager.findOrcidIds(ArgumentMatchers.<Map<String, List<String>>> any())).thenReturn(search);
        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn(null);

        Response response = serviceDelegator.searchByQuery(new HashMap<String, List<String>>());

        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Search);
        assertEquals(1, ((Search) response.getEntity()).getResults().size());
        assertEquals("some-orcid-id", ((Search) response.getEntity()).getResults().get(0).getOrcidIdentifier().getPath());
    }

    @Test
    public void testSearchByQueryTooManyRows() throws ParseException {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("rows", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_ROWS + 20)));
        when(localeManager.resolveMessage(anyString())).thenReturn("a message");

        try {
            serviceDelegator.searchByQuery(params);
            fail();
        } catch (OrcidBadRequestException expected) {
        }
        verifyNoInteractions(orcidSearchManager);
    }

    @Test
    public void testSearchByQueryIllegalStart() throws ParseException {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("start", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_START + 20)));
        when(localeManager.resolveMessage(anyString())).thenReturn("a message");
        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn(null);

        try {
            serviceDelegator.searchByQuery(params);
            fail();
        } catch (SearchStartParameterLimitExceededException expected) {
        }
        verifyNoInteractions(orcidSearchManager);
    }

    @Test
    public void testSearchByQueryLegalStart() throws ParseException {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("start", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_START)));
        when(localeManager.resolveMessage(anyString())).thenReturn("a message");
        when(orcidSearchManager.findOrcidIds(ArgumentMatchers.<Map<String, List<String>>> any())).thenReturn(new Search());
        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn(null);

        Response response = serviceDelegator.searchByQuery(params);

        assertNotNull(response);
    }

    /**
     * The start limit exists to stop anonymous deep paging, so it is skipped
     * when the request carries client credentials. That branch had no test.
     */
    @Test
    public void testSearchByQueryIllegalStartIsAllowedForAClient() throws ParseException {
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("start", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_START + 20)));
        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn("APP-5555555555555555");
        when(orcidSearchManager.findOrcidIds(ArgumentMatchers.<Map<String, List<String>>> any())).thenReturn(new Search());

        Response response = serviceDelegator.searchByQuery(params);

        assertNotNull(response);
        verify(orcidSearchManager).findOrcidIds(ArgumentMatchers.<Map<String, List<String>>> any());
    }

    /*
     * ------------------------------------------------------------------
     * Client
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewClientNonExistent() {
        when(clientManagerReadOnly.getSummary("some-client-that-doesn't-exist")).thenThrow(new NoResultException());
        try {
            serviceDelegator.viewClient("some-client-that-doesn't-exist");
            fail();
        } catch (NoResultException expected) {
        }
    }

    @Test
    public void testViewClient() throws ParseException {
        when(clientManagerReadOnly.getSummary("APP-6666666666666666")).thenReturn(clientSummary());

        Response response = serviceDelegator.viewClient("APP-6666666666666666");

        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);
        ClientSummary summary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", summary.getName());
        assertEquals("A test source client", summary.getDescription());
        verify(clientManagerReadOnly).getSummary("APP-6666666666666666");
    }

    /*
     * ------------------------------------------------------------------
     * schema.org rendering of the public record
     * ------------------------------------------------------------------
     */

    /**
     * SchemaOrgMBWriterV2 is not a collaborator of the delegator, it is the
     * writer the public record endpoint is rendered through. Its only
     * collaborator is the identifier normalisation service, so it runs here for
     * real over a hand built public record; previously the record came from the
     * database and the assertions on it were really assertions about the
     * fixture. Work and funding organisation identifiers are not covered: those
     * go through the normalisation service and belong with it.
     */
    @Test
    public void testSchemaOrgMBWriterV2() throws WebApplicationException, IOException {
        Record record = new Record();
        OrcidIdentifier orcidIdentifier = new OrcidIdentifier(ORCID);
        orcidIdentifier.setUri("https://orcid.org/" + ORCID);
        record.setOrcidIdentifier(orcidIdentifier);
        Person person = new Person();
        person.setName(name());
        OtherNames otherNames = new OtherNames();
        otherNames.getOtherNames().add(otherName(13L, Visibility.PUBLIC, "Other Name PUBLIC"));
        person.setOtherNames(otherNames);
        ResearcherUrls rUrls = new ResearcherUrls();
        rUrls.getResearcherUrls().add(researcherUrl(13L, Visibility.PUBLIC));
        person.setResearcherUrls(rUrls);
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        PersonExternalIdentifier extId = externalIdentifier(19L, Visibility.PUBLIC, "self_public_user_obo_type", "http://ext-id/self/obo/public");
        extId.setValue("self_public_user_obo_ref");
        extIds.getExternalIdentifiers().add(extId);
        person.setExternalIdentifiers(extIds);
        Addresses addresses = new Addresses();
        addresses.getAddress().add(address(9L, Visibility.PUBLIC));
        person.setAddresses(addresses);
        record.setPerson(person);
        when(recordManagerReadOnly.getPublicRecord(ORCID)).thenReturn(record);

        SchemaOrgMBWriterV2 writerV2 = new SchemaOrgMBWriterV2();
        ReflectionTestUtils.setField(writerV2, "norm", norm);

        Response response = serviceDelegator.viewRecord(ORCID);
        Record fromEndpoint = (Record) response.getEntity();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writerV2.writeTo(fromEndpoint, fromEndpoint.getClass(), null, null, null, null, out);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        SchemaOrgDocument doc = objectMapper.readerFor(SchemaOrgDocument.class).readValue(out.toString());
        assertTrue(doc.id.endsWith(ORCID));
        assertEquals("Person", doc.type);
        assertEquals("http://schema.org", doc.context);
        assertEquals("Credit Name", doc.name);
        assertEquals("Given Names", doc.givenName);
        assertEquals("Family Name", doc.familyName);
        assertEquals("Other Name PUBLIC", doc.alternateName.get(0));
        assertEquals("http://www.researcherurl.com?id=13", doc.url.get(0));
        assertEquals("self_public_user_obo_type", doc.identifier.get(0).propertyID);
        assertEquals("self_public_user_obo_ref", doc.identifier.get(0).value);
    }

    /*
     * ------------------------------------------------------------------
     * Email source, per endpoint
     * ------------------------------------------------------------------
     */

    /*
     * These three used to assert that a non verified professional email and a
     * verified non professional email keep source APP-5555555555555555 and the
     * source name "Source Client 1". Both the verified flag and the source name
     * rewriting live below this layer, in EmailManagerReadOnly and in
     * SourceUtils/SourceNameCacheManager, and both are mocks here; re-asserting
     * them would only restate the stub. What these three keep is the part that
     * is this layer's: each of the three endpoints that can return emails puts
     * them through the public gate and the source name decoration, for a
     * client-only caller.
     */

    @Test
    public void checkSourceOnEmail_RecordEndpointTest() {
        String orcid = "0000-0000-0000-0001";
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_LIMITED);
        Record record = new Record();
        Person person = new Person();
        person.setEmails(emailsOf(orcid));
        record.setPerson(person);
        when(recordManagerReadOnly.getPublicRecord(orcid)).thenReturn(record);

        Response r = serviceDelegator.viewRecord(orcid);

        Record returned = (Record) r.getEntity();
        assertNotNull(returned.getPerson());
        assertNotNull(returned.getPerson().getEmails());
        assertEquals(2, returned.getPerson().getEmails().getEmails().size());
        verify(publicAPISecurityManagerV2).filter(returned);
        verify(sourceUtilsReadOnly).setSourceName(returned.getPerson());
    }

    @Test
    public void checkSourceOnEmail_PersonEndpointTest() {
        String orcid = "0000-0000-0000-0001";
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_LIMITED);
        Person person = new Person();
        person.setEmails(emailsOf(orcid));
        when(personDetailsManagerReadOnly.getPublicPersonDetails(orcid)).thenReturn(person);

        Response r = serviceDelegator.viewPerson(orcid);

        Person p = (Person) r.getEntity();
        assertNotNull(p.getEmails());
        assertEquals(2, p.getEmails().getEmails().size());
        verify(publicAPISecurityManagerV2).filter(p);
        verify(sourceUtilsReadOnly).setSourceName(p);
    }

    @Test
    public void checkSourceOnEmail_EmailEndpointTest() {
        String orcid = "0000-0000-0000-0001";
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_LIMITED);
        Emails emails = emailsOf(orcid);
        when(emailManagerReadOnly.getPublicEmails(orcid)).thenReturn(emails);

        Response r = serviceDelegator.viewEmails(orcid);

        Emails returned = (Emails) r.getEntity();
        assertSame(emails, returned);
        assertEquals(2, returned.getEmails().size());
        assertEquals("/0000-0000-0000-0001/email", returned.getPath());
        verify(publicAPISecurityManagerV2).filter(emails);
        verify(sourceUtilsReadOnly).setSourceName(emails);
    }

    /*
     * ------------------------------------------------------------------
     * Helpers
     * ------------------------------------------------------------------
     */

    /**
     * The single element endpoints have to stop at the public gate. Asserting
     * only that the exception escaped would also pass if the element had
     * already been decorated and about to be returned, so the source name
     * decoration is checked not to have happened.
     */
    private void assertNonPublicElementIsRefused(VisibilityType element, Runnable call) {
        doThrow(new OrcidNonPublicElementException()).when(publicAPISecurityManagerV2).checkIsPublic(element);
        try {
            call.run();
            fail("expected OrcidNonPublicElementException");
        } catch (OrcidNonPublicElementException expected) {
        }
        verify(publicAPISecurityManagerV2).checkIsPublic(element);
        verifyNoInteractions(sourceUtilsReadOnly);
    }

    private static LastModifiedDate lastModified(long putCode) {
        // distinct per put code so a "latest wins" aggregation is observable
        return new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(1500000000000L + (putCode * 1000L)));
    }

    private static Work work(Long putCode, Visibility visibility) {
        Work work = new Work();
        work.setPutCode(putCode);
        work.setVisibility(visibility);
        work.setLastModifiedDate(lastModified(putCode));
        return work;
    }

    private static WorkSummary workSummary(Long putCode, Visibility visibility) {
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(putCode);
        summary.setVisibility(visibility);
        summary.setLastModifiedDate(lastModified(putCode));
        return summary;
    }

    private static Works works(WorkSummary... summaries) {
        Works works = new Works();
        for (WorkSummary summary : summaries) {
            WorkGroup group = new WorkGroup();
            group.getWorkSummary().add(summary);
            works.getWorkGroup().add(group);
        }
        return works;
    }

    private static Funding funding(Long putCode, Visibility visibility) {
        Funding funding = new Funding();
        funding.setPutCode(putCode);
        funding.setVisibility(visibility);
        funding.setLastModifiedDate(lastModified(putCode));
        return funding;
    }

    private static FundingSummary fundingSummary(Long putCode, Visibility visibility) {
        FundingSummary summary = new FundingSummary();
        summary.setPutCode(putCode);
        summary.setVisibility(visibility);
        summary.setLastModifiedDate(lastModified(putCode));
        return summary;
    }

    private static Fundings fundings(FundingSummary... summaries) {
        Fundings fundings = new Fundings();
        for (FundingSummary summary : summaries) {
            FundingGroup group = new FundingGroup();
            group.getFundingSummary().add(summary);
            fundings.getFundingGroup().add(group);
        }
        return fundings;
    }

    private static PeerReview peerReview(Long putCode, Visibility visibility) {
        PeerReview peerReview = new PeerReview();
        peerReview.setPutCode(putCode);
        peerReview.setVisibility(visibility);
        peerReview.setLastModifiedDate(lastModified(putCode));
        return peerReview;
    }

    private static PeerReviewSummary peerReviewSummary(Long putCode, Visibility visibility) {
        PeerReviewSummary summary = new PeerReviewSummary();
        summary.setPutCode(putCode);
        summary.setVisibility(visibility);
        summary.setLastModifiedDate(lastModified(putCode));
        return summary;
    }

    private static PeerReviews peerReviews(PeerReviewSummary... summaries) {
        PeerReviews peerReviews = new PeerReviews();
        PeerReviewGroup group = new PeerReviewGroup();
        for (PeerReviewSummary summary : summaries) {
            group.getPeerReviewSummary().add(summary);
        }
        peerReviews.getPeerReviewGroup().add(group);
        return peerReviews;
    }

    private static Education education(Long putCode, Visibility visibility) {
        Education education = new Education();
        education.setPutCode(putCode);
        education.setVisibility(visibility);
        education.setLastModifiedDate(lastModified(putCode));
        education.setDepartmentName("PUBLIC Department");
        return education;
    }

    private static EducationSummary educationSummary(Long putCode, Visibility visibility) {
        EducationSummary summary = new EducationSummary();
        summary.setPutCode(putCode);
        summary.setVisibility(visibility);
        summary.setLastModifiedDate(lastModified(putCode));
        summary.setDepartmentName("PUBLIC Department");
        return summary;
    }

    private static Employment employment(Long putCode, Visibility visibility) {
        Employment employment = new Employment();
        employment.setPutCode(putCode);
        employment.setVisibility(visibility);
        employment.setLastModifiedDate(lastModified(putCode));
        employment.setDepartmentName("PUBLIC Department");
        return employment;
    }

    private static EmploymentSummary employmentSummary(Long putCode, Visibility visibility) {
        EmploymentSummary summary = new EmploymentSummary();
        summary.setPutCode(putCode);
        summary.setVisibility(visibility);
        summary.setLastModifiedDate(lastModified(putCode));
        summary.setDepartmentName("PUBLIC Department");
        return summary;
    }

    private static Address address(Long putCode, Visibility visibility) {
        Address address = new Address();
        address.setPutCode(putCode);
        address.setVisibility(visibility);
        address.setLastModifiedDate(lastModified(putCode));
        address.setCountry(new Country(Iso3166Country.US));
        return address;
    }

    private static Keyword keyword(Long putCode, Visibility visibility, String content) {
        Keyword keyword = new Keyword();
        keyword.setPutCode(putCode);
        keyword.setVisibility(visibility);
        keyword.setLastModifiedDate(lastModified(putCode));
        keyword.setContent(content);
        return keyword;
    }

    private static OtherName otherName(Long putCode, Visibility visibility, String content) {
        OtherName otherName = new OtherName();
        otherName.setPutCode(putCode);
        otherName.setVisibility(visibility);
        otherName.setLastModifiedDate(lastModified(putCode));
        otherName.setContent(content);
        return otherName;
    }

    private static ResearcherUrl researcherUrl(Long putCode, Visibility visibility) {
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setPutCode(putCode);
        rUrl.setVisibility(visibility);
        rUrl.setLastModifiedDate(lastModified(putCode));
        rUrl.setUrl(new Url("http://www.researcherurl.com?id=13"));
        rUrl.setUrlName("public_rurl");
        return rUrl;
    }

    private static PersonExternalIdentifier externalIdentifier(Long putCode, Visibility visibility, String type, String url) {
        PersonExternalIdentifier extId = new PersonExternalIdentifier();
        extId.setPutCode(putCode);
        extId.setVisibility(visibility);
        extId.setLastModifiedDate(lastModified(putCode));
        extId.setType(type);
        extId.setUrl(new Url(url));
        return extId;
    }

    private static Email email(String value, Visibility visibility, long lastModifiedSeed) {
        Email email = new Email();
        email.setEmail(value);
        email.setVisibility(visibility);
        email.setLastModifiedDate(lastModified(lastModifiedSeed));
        return email;
    }

    private static Emails emailsOf(String orcid) {
        Emails emails = new Emails();
        emails.getEmails().add(email("public_" + orcid + "@test.orcid.org", Visibility.PUBLIC, 1L));
        emails.getEmails().add(email("verified_non_professional@nonprofessional.org", Visibility.PUBLIC, 2L));
        return emails;
    }

    private static Biography biography(Visibility visibility) {
        Biography biography = new Biography();
        biography.setContent("Biography for 0000-0000-0000-0003");
        biography.setVisibility(visibility);
        biography.setLastModifiedDate(lastModified(1L));
        return biography;
    }

    private static Name name() {
        Name name = new Name();
        name.setCreditName(new CreditName("Credit Name"));
        name.setFamilyName(new FamilyName("Family Name"));
        name.setGivenNames(new GivenNames("Given Names"));
        name.setVisibility(Visibility.PUBLIC);
        name.setLastModifiedDate(lastModified(1L));
        return name;
    }

    private static Person person() {
        Person person = new Person();
        person.setName(name());
        person.setBiography(biography(Visibility.PUBLIC));

        Addresses addresses = new Addresses();
        addresses.getAddress().add(address(9L, Visibility.PUBLIC));
        person.setAddresses(addresses);

        Emails emails = new Emails();
        emails.getEmails().add(email("public_0000-0000-0000-0003@test.orcid.org", Visibility.PUBLIC, 1L));
        person.setEmails(emails);

        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.getExternalIdentifiers().add(externalIdentifier(13L, Visibility.PUBLIC, "public_type", "http://ext-id/public_ref"));
        person.setExternalIdentifiers(extIds);

        Keywords keywords = new Keywords();
        keywords.getKeywords().add(keyword(9L, Visibility.PUBLIC, "PUBLIC"));
        person.setKeywords(keywords);

        OtherNames otherNames = new OtherNames();
        otherNames.getOtherNames().add(otherName(13L, Visibility.PUBLIC, "Other Name PUBLIC"));
        person.setOtherNames(otherNames);

        ResearcherUrls rUrls = new ResearcherUrls();
        rUrls.getResearcherUrls().add(researcherUrl(13L, Visibility.PUBLIC));
        person.setResearcherUrls(rUrls);

        return person;
    }

    private void validatePersonPaths(Person person) {
        assertEquals("/0000-0000-0000-0003/person", person.getPath());
        assertEquals("/0000-0000-0000-0003/address", person.getAddresses().getPath());
        assertEquals("/0000-0000-0000-0003/address/9", person.getAddresses().getAddress().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/biography", person.getBiography().getPath());
        assertEquals("/0000-0000-0000-0003/email", person.getEmails().getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers", person.getExternalIdentifiers().getPath());
        assertEquals("/0000-0000-0000-0003/external-identifiers/13", person.getExternalIdentifiers().getExternalIdentifiers().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/keywords", person.getKeywords().getPath());
        assertEquals("/0000-0000-0000-0003/keywords/9", person.getKeywords().getKeywords().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/other-names", person.getOtherNames().getPath());
        assertEquals("/0000-0000-0000-0003/other-names/13", person.getOtherNames().getOtherNames().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/researcher-urls", person.getResearcherUrls().getPath());
        assertEquals("/0000-0000-0000-0003/researcher-urls/13", person.getResearcherUrls().getResearcherUrls().get(0).getPath());
        assertNotNull(person.getLastModifiedDate());
    }

    private static ActivitiesSummary activitiesSummary() {
        ActivitiesSummary summary = new ActivitiesSummary();
        summary.setWorks(works(workSummary(11L, Visibility.PUBLIC)));
        summary.setFundings(fundings(fundingSummary(10L, Visibility.PUBLIC)));
        summary.setPeerReviews(peerReviews(peerReviewSummary(9L, Visibility.PUBLIC)));
        Educations educations = new Educations();
        educations.getSummaries().add(educationSummary(20L, Visibility.PUBLIC));
        summary.setEducations(educations);
        Employments employments = new Employments();
        employments.getSummaries().add(employmentSummary(17L, Visibility.PUBLIC));
        summary.setEmployments(employments);
        return summary;
    }

    private void validateActivityPaths(ActivitiesSummary summary) {
        assertEquals("/0000-0000-0000-0003/activities", summary.getPath());
        assertEquals("/0000-0000-0000-0003/works", summary.getWorks().getPath());
        assertEquals("/0000-0000-0000-0003/work/11", summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/fundings", summary.getFundings().getPath());
        assertEquals("/0000-0000-0000-0003/funding/10", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/peer-reviews", summary.getPeerReviews().getPath());
        assertEquals("/0000-0000-0000-0003/peer-review/9", summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/educations", summary.getEducations().getPath());
        assertEquals("/0000-0000-0000-0003/education/20", summary.getEducations().getSummaries().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/employments", summary.getEmployments().getPath());
        assertEquals("/0000-0000-0000-0003/employment/17", summary.getEmployments().getSummaries().get(0).getPath());
        assertNotNull(summary.getLastModifiedDate());
    }

    private static Record record() {
        Record record = new Record();
        OrcidIdentifier orcidIdentifier = new OrcidIdentifier(ORCID);
        orcidIdentifier.setUri("https://orcid.org/" + ORCID);
        record.setOrcidIdentifier(orcidIdentifier);
        record.setPerson(person());
        record.setActivitiesSummary(activitiesSummary());
        return record;
    }

    private static ClientSummary clientSummary() {
        ClientSummary summary = new ClientSummary();
        summary.setName("Source Client 2");
        summary.setDescription("A test source client");
        return summary;
    }
}
