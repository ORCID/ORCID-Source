package org.orcid.api.publicV3.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.api.common.util.v3.PublicRecordUtils;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument;
import org.orcid.api.common.writer.schemaorg.SchemaOrgMBWriterV3;
import org.orcid.api.publicV3.server.delegator.impl.PublicV3ApiServiceDelegatorImpl;
import org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3;
import org.orcid.core.common.manager.EventManager;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.OrcidNonPublicElementException;
import org.orcid.core.exception.SearchStartParameterLimitExceededException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.StatusManager;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.RecordManager;
import org.orcid.core.manager.v3.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.v3.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.Affiliation;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.FamilyName;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.GivenNames;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.Name;
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
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.jaxb.model.v3.release.search.Result;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedResult;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedSearch;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * What this class can and cannot prove, now that its collaborators are mocks.
 *
 * <p>
 * Every read endpoint on the public V3 delegator does the same four things
 * after the profile guard: it asks a read-only manager for the element, it puts
 * the element through {@code PublicAPISecurityManagerV3}, it runs the static
 * path/last-modified helpers, and it decorates the source name. Only three of
 * those are the delegator's own behaviour and they are what is asserted here:
 * the path building, the last-modified aggregation, and the fact that the guard
 * is invoked at all - with the right argument, and before anything else
 * happens.
 *
 * <p>
 * The visibility filtering itself deliberately is NOT asserted here.
 * {@code PublicAPISecurityManagerV3.filter(...)} is void and mutates its
 * argument in place, so against a mock nothing is removed and any assertion of
 * the form "only public elements came back" passes without proving anything.
 * That rule is proved by
 * {@code org.orcid.api.publicV3.server.security.PublicAPISecurityManagerV3Test},
 * which exercises the real implementation. What is left at this boundary is
 * {@code verify(publicAPISecurityManagerV3).filter(x)} plus, for the single
 * element endpoints, a {@code doThrow} case proving the refusal stops the
 * operation before the element is decorated and returned.
 *
 * <p>
 * The old fixture-shaped assertions that a mock can only restate - titles,
 * department names, source client ids, the verified flag on an email, and the
 * positional order of external identifiers, which is a DAO ordering claim -
 * have been dropped rather than reproduced against the test's own stubs. See
 * the commit message for where each of them now lives.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PublicV3ApiServiceDelegatorTest {

    private static final String ORCID = "0000-0000-0000-0003";

    /**
     * Declared as the implementation rather than the interface because
     * {@code @InjectMocks} needs a concrete type.
     */
    @InjectMocks
    private PublicV3ApiServiceDelegatorImpl serviceDelegator = new PublicV3ApiServiceDelegatorImpl();

    @Mock
    private WorkManagerReadOnly workManagerReadOnly;

    @Mock
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Mock
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Mock
    private PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Mock
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnly;

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
     * Two SourceUtils fields on the class under test. @InjectMocks
     * disambiguates same-typed mocks by field name, so these two names must
     * stay exactly as they are on PublicV3ApiServiceDelegatorImpl:
     * sourceUtilsReadOnly decorates every read endpoint, sourceUtils is used
     * only by viewBulkWorks.
     */
    @Mock
    private SourceUtils sourceUtilsReadOnly;

    @Mock
    private SourceUtils sourceUtils;

    @Mock
    private ContributorUtils contributorUtilsReadOnly;

    @Mock
    private RecordManager recordManager;

    @Mock
    private OrcidSearchManager orcidSearchManager;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private PublicAPISecurityManagerV3 publicAPISecurityManagerV3;

    @Mock
    private PublicRecordUtils publicRecordUtils;

    @Mock
    private LocaleManager localeManager;

    @Mock
    private ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Mock
    private StatusManager statusManager;

    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnlyV3;

    @Mock
    private EventManager eventManager;

    /** Only collaborator of SchemaOrgMBWriterV3, which is not a Spring bean here. */
    @Mock
    private PIDNormalizationService norm;

    @Captor
    private ArgumentCaptor<Map<String, List<String>>> searchParamsCaptor;

    /**
     * SecurityContextHolder is static process state, so a test that installs a
     * token has to take it back out again.
     */
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(work);
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

        assertNotNull(response);
        Works entity = (Works) response.getEntity();
        assertSame(grouped, entity);
        assertEquals("/0000-0000-0000-0003/works", entity.getPath());
        assertEquals("/0000-0000-0000-0003/work/11", entity.getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals(summary.getLastModifiedDate(), entity.getLastModifiedDate());
        // the grouping must be asked for public elements only
        verify(workManagerReadOnly).groupWorks(summaries, true);
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).filter(grouped);
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
        // which of the requested put codes come back as OrcidErrors is decided
        // by PublicAPISecurityManagerV3.filter(WorkBulk); proved there.
        verify(publicAPISecurityManagerV3).filter(bulk);
        // note the OTHER SourceUtils: viewBulkWorks uses sourceUtils, not
        // sourceUtilsReadOnly, and getting that wrong is invisible at runtime
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

        assertNotNull(r);
        assertEquals(Long.valueOf(11), ((Work) r.getEntity()).getPutCode());
        // a read-limited token must not exempt the caller from the public gate
        verify(publicAPISecurityManagerV3).checkIsPublic(work);
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
        verify(publicAPISecurityManagerV3).filter(grouped);
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
        verify(publicAPISecurityManagerV3).filter(grouped);
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

        assertNotNull(response);
        assertSame(funding, response.getEntity());
        assertEquals("/0000-0000-0000-0003/funding/10", funding.getPath());
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(funding);
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).filter(grouped);
        verify(sourceUtilsReadOnly).setSourceName(grouped);
    }

    @Test
    public void testGetPublicFundingUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Funding funding = funding(10L, Visibility.PUBLIC);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(funding);

        Response r = serviceDelegator.viewFunding(ORCID, 10L);

        assertEquals(Long.valueOf(10), ((Funding) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(funding);
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
        verify(publicAPISecurityManagerV3).filter(grouped);
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
        verify(publicAPISecurityManagerV3).filter(grouped);
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(peerReview);
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
        assertEquals("/0000-0000-0000-0003/peer-review/9",
                entity.getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
        assertEquals(summary.getLastModifiedDate(), entity.getLastModifiedDate());
        verify(peerReviewManagerReadOnly).groupPeerReviews(summaries, true);
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).filter(grouped);
        verify(sourceUtilsReadOnly).setSourceName(grouped);
    }

    @Test
    public void testGetPublicPeerReviewUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PeerReview peerReview = peerReview(9L, Visibility.PUBLIC);
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 9L)).thenReturn(peerReview);

        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);

        assertEquals(Long.valueOf(9), ((PeerReview) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(peerReview);
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
        verify(publicAPISecurityManagerV3).filter(grouped);
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
        verify(publicAPISecurityManagerV3).filter(grouped);
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
        Education education = affiliation(new Education(), 20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(education);

        Response response = serviceDelegator.viewEducation(ORCID, 20L);

        assertSame(education, response.getEntity());
        assertEquals("/0000-0000-0000-0003/education/20", education.getPath());
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(education);
        verify(sourceUtilsReadOnly).setSourceName(education);
    }

    /**
     * The affiliation list endpoints do their own visibility filtering, in the
     * delegator, before handing the survivors to groupAffiliations. That loop
     * is the delegator's own Java and is the one place in this file where a
     * "non public elements are dropped" assertion is not vacuous.
     */
    @Test
    public void testViewEducations() {
        EducationSummary publicSummary = affiliationSummary(new EducationSummary(), 20L, Visibility.PUBLIC);
        EducationSummary limitedSummary = affiliationSummary(new EducationSummary(), 21L, Visibility.LIMITED);
        EducationSummary privateSummary = affiliationSummary(new EducationSummary(), 22L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID))
                .thenReturn(new ArrayList<EducationSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));
        stubAffiliationGrouping();

        Response response = serviceDelegator.viewEducations(ORCID);

        Educations entity = (Educations) response.getEntity();
        assertEquals("/0000-0000-0000-0003/educations", entity.getPath());
        assertEquals(1, entity.retrieveGroups().size());
        EducationSummary returned = entity.retrieveGroups().iterator().next().getActivities().get(0);
        assertSame(publicSummary, returned);
        assertEquals("/0000-0000-0000-0003/education/20", returned.getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
        verify(sourceUtilsReadOnly).setSourceName(publicSummary);
        verify(sourceUtilsReadOnly, never()).setSourceName(limitedSummary);
        verify(sourceUtilsReadOnly, never()).setSourceName(privateSummary);
        verify(orcidSecurityManager).checkProfile(ORCID);
    }

    @Test
    public void testGetPublicEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Education education = affiliation(new Education(), 20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(education);

        Response r = serviceDelegator.viewEducation(ORCID, 20L);

        assertEquals(Long.valueOf(20), ((Education) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(education);
    }

    @Test
    public void testGetPublicEducations() {
        EducationSummary publicSummary = affiliationSummary(new EducationSummary(), 20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID))
                .thenReturn(new ArrayList<EducationSummary>(Arrays.asList(publicSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewEducations(ORCID);

        Educations entity = (Educations) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertEquals(Long.valueOf(20), entity.retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicEducationsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        EducationSummary publicSummary = affiliationSummary(new EducationSummary(), 20L, Visibility.PUBLIC);
        EducationSummary limitedSummary = affiliationSummary(new EducationSummary(), 21L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID))
                .thenReturn(new ArrayList<EducationSummary>(Arrays.asList(publicSummary, limitedSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewEducations(ORCID);

        Educations entity = (Educations) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertSame(publicSummary, entity.retrieveGroups().iterator().next().getActivities().get(0));
    }

    @Test
    public void testGetLimitedEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Education education = affiliation(new Education(), 21L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 21L)).thenReturn(education);
        assertNonPublicElementIsRefused(education, () -> serviceDelegator.viewEducation(ORCID, 21L));
        assertNull(education.getPath());
    }

    @Test
    public void testGetPrivateEducationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Education education = affiliation(new Education(), 22L, Visibility.PRIVATE);
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
        Employment employment = affiliation(new Employment(), 17L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(employment);

        Response response = serviceDelegator.viewEmployment(ORCID, 17L);

        assertSame(employment, response.getEntity());
        assertEquals("/0000-0000-0000-0003/employment/17", employment.getPath());
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(employment);
        verify(sourceUtilsReadOnly).setSourceName(employment);
    }

    @Test
    public void testViewEmployments() {
        EmploymentSummary publicSummary = affiliationSummary(new EmploymentSummary(), 17L, Visibility.PUBLIC);
        EmploymentSummary limitedSummary = affiliationSummary(new EmploymentSummary(), 18L, Visibility.LIMITED);
        EmploymentSummary privateSummary = affiliationSummary(new EmploymentSummary(), 19L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID))
                .thenReturn(new ArrayList<EmploymentSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));
        stubAffiliationGrouping();

        Response response = serviceDelegator.viewEmployments(ORCID);

        Employments entity = (Employments) response.getEntity();
        assertEquals("/0000-0000-0000-0003/employments", entity.getPath());
        assertEquals(1, entity.retrieveGroups().size());
        EmploymentSummary returned = entity.retrieveGroups().iterator().next().getActivities().get(0);
        assertSame(publicSummary, returned);
        assertEquals("/0000-0000-0000-0003/employment/17", returned.getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
        verify(orcidSecurityManager).checkProfile(ORCID);
    }

    @Test
    public void testGetPublicEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Employment employment = affiliation(new Employment(), 17L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(employment);

        Response r = serviceDelegator.viewEmployment(ORCID, 17L);

        assertEquals(Long.valueOf(17), ((Employment) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(employment);
    }

    @Test
    public void testGetPublicEmployments() {
        EmploymentSummary publicSummary = affiliationSummary(new EmploymentSummary(), 17L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID))
                .thenReturn(new ArrayList<EmploymentSummary>(Arrays.asList(publicSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewEmployments(ORCID);

        Employments entity = (Employments) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertEquals(Long.valueOf(17), entity.retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicEmploymentsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        EmploymentSummary publicSummary = affiliationSummary(new EmploymentSummary(), 17L, Visibility.PUBLIC);
        EmploymentSummary limitedSummary = affiliationSummary(new EmploymentSummary(), 18L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID))
                .thenReturn(new ArrayList<EmploymentSummary>(Arrays.asList(publicSummary, limitedSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewEmployments(ORCID);

        Employments entity = (Employments) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertSame(publicSummary, entity.retrieveGroups().iterator().next().getActivities().get(0));
    }

    @Test
    public void testGetLimitedEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Employment employment = affiliation(new Employment(), 18L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 18L)).thenReturn(employment);
        assertNonPublicElementIsRefused(employment, () -> serviceDelegator.viewEmployment(ORCID, 18L));
        assertNull(employment.getPath());
    }

    @Test
    public void testGetPrivateEmploymentUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Employment employment = affiliation(new Employment(), 19L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 19L)).thenReturn(employment);
        assertNonPublicElementIsRefused(employment, () -> serviceDelegator.viewEmployment(ORCID, 19L));
        assertNull(employment.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Distinctions
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewDistinction() {
        Distinction distinction = affiliation(new Distinction(), 27L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L)).thenReturn(distinction);

        Response response = serviceDelegator.viewDistinction(ORCID, 27L);

        assertSame(distinction, response.getEntity());
        assertEquals("/0000-0000-0000-0003/distinction/27", distinction.getPath());
        verify(publicAPISecurityManagerV3).checkIsPublic(distinction);
        verify(sourceUtilsReadOnly).setSourceName(distinction);
    }

    @Test
    public void testViewDistinctions() {
        DistinctionSummary publicSummary = affiliationSummary(new DistinctionSummary(), 27L, Visibility.PUBLIC);
        DistinctionSummary limitedSummary = affiliationSummary(new DistinctionSummary(), 28L, Visibility.LIMITED);
        DistinctionSummary privateSummary = affiliationSummary(new DistinctionSummary(), 29L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getDistinctionSummaryList(ORCID))
                .thenReturn(new ArrayList<DistinctionSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));
        stubAffiliationGrouping();

        Response response = serviceDelegator.viewDistinctions(ORCID);

        Distinctions entity = (Distinctions) response.getEntity();
        assertEquals("/0000-0000-0000-0003/distinctions", entity.getPath());
        assertEquals(1, entity.retrieveGroups().size());
        DistinctionSummary returned = entity.retrieveGroups().iterator().next().getActivities().get(0);
        assertSame(publicSummary, returned);
        assertEquals("/0000-0000-0000-0003/distinction/27", returned.getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicDistinctionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Distinction distinction = affiliation(new Distinction(), 27L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L)).thenReturn(distinction);

        Response r = serviceDelegator.viewDistinction(ORCID, 27L);

        assertEquals(Long.valueOf(27), ((Distinction) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(distinction);
    }

    @Test
    public void testGetPublicDistinctions() {
        DistinctionSummary publicSummary = affiliationSummary(new DistinctionSummary(), 27L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getDistinctionSummaryList(ORCID))
                .thenReturn(new ArrayList<DistinctionSummary>(Arrays.asList(publicSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewDistinctions(ORCID);

        Distinctions entity = (Distinctions) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertEquals(Long.valueOf(27), entity.retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicDistinctionsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        DistinctionSummary publicSummary = affiliationSummary(new DistinctionSummary(), 27L, Visibility.PUBLIC);
        DistinctionSummary limitedSummary = affiliationSummary(new DistinctionSummary(), 28L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getDistinctionSummaryList(ORCID))
                .thenReturn(new ArrayList<DistinctionSummary>(Arrays.asList(publicSummary, limitedSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewDistinctions(ORCID);

        Distinctions entity = (Distinctions) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertSame(publicSummary, entity.retrieveGroups().iterator().next().getActivities().get(0));
    }

    @Test
    public void testGetLimitedDistinctionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Distinction distinction = affiliation(new Distinction(), 28L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 28L)).thenReturn(distinction);
        assertNonPublicElementIsRefused(distinction, () -> serviceDelegator.viewDistinction(ORCID, 28L));
        assertNull(distinction.getPath());
    }

    @Test
    public void testGetPrivateDistinctionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Distinction distinction = affiliation(new Distinction(), 29L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 29L)).thenReturn(distinction);
        assertNonPublicElementIsRefused(distinction, () -> serviceDelegator.viewDistinction(ORCID, 29L));
        assertNull(distinction.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Invited positions
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewInvitedPosition() {
        InvitedPosition invitedPosition = affiliation(new InvitedPosition(), 32L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L)).thenReturn(invitedPosition);

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);

        assertSame(invitedPosition, response.getEntity());
        assertEquals("/0000-0000-0000-0003/invited-position/32", invitedPosition.getPath());
        verify(publicAPISecurityManagerV3).checkIsPublic(invitedPosition);
        verify(sourceUtilsReadOnly).setSourceName(invitedPosition);
    }

    @Test
    public void testViewInvitedPositions() {
        InvitedPositionSummary publicSummary = affiliationSummary(new InvitedPositionSummary(), 32L, Visibility.PUBLIC);
        InvitedPositionSummary limitedSummary = affiliationSummary(new InvitedPositionSummary(), 33L, Visibility.LIMITED);
        InvitedPositionSummary privateSummary = affiliationSummary(new InvitedPositionSummary(), 34L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getInvitedPositionSummaryList(ORCID))
                .thenReturn(new ArrayList<InvitedPositionSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));
        stubAffiliationGrouping();

        Response response = serviceDelegator.viewInvitedPositions(ORCID);

        InvitedPositions entity = (InvitedPositions) response.getEntity();
        assertEquals("/0000-0000-0000-0003/invited-positions", entity.getPath());
        assertEquals(1, entity.retrieveGroups().size());
        InvitedPositionSummary returned = entity.retrieveGroups().iterator().next().getActivities().get(0);
        assertSame(publicSummary, returned);
        assertEquals("/0000-0000-0000-0003/invited-position/32", returned.getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicInvitedPositionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        InvitedPosition invitedPosition = affiliation(new InvitedPosition(), 32L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L)).thenReturn(invitedPosition);

        Response r = serviceDelegator.viewInvitedPosition(ORCID, 32L);

        assertEquals(Long.valueOf(32), ((InvitedPosition) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(invitedPosition);
    }

    @Test
    public void testGetPublicInvitedPositions() {
        InvitedPositionSummary publicSummary = affiliationSummary(new InvitedPositionSummary(), 32L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getInvitedPositionSummaryList(ORCID))
                .thenReturn(new ArrayList<InvitedPositionSummary>(Arrays.asList(publicSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewInvitedPositions(ORCID);

        InvitedPositions entity = (InvitedPositions) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertEquals(Long.valueOf(32), entity.retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicInvitedPositionsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        InvitedPositionSummary publicSummary = affiliationSummary(new InvitedPositionSummary(), 32L, Visibility.PUBLIC);
        InvitedPositionSummary limitedSummary = affiliationSummary(new InvitedPositionSummary(), 33L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getInvitedPositionSummaryList(ORCID))
                .thenReturn(new ArrayList<InvitedPositionSummary>(Arrays.asList(publicSummary, limitedSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewInvitedPositions(ORCID);

        InvitedPositions entity = (InvitedPositions) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertSame(publicSummary, entity.retrieveGroups().iterator().next().getActivities().get(0));
    }

    @Test
    public void testGetLimitedInvitedPositionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        InvitedPosition invitedPosition = affiliation(new InvitedPosition(), 33L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 33L)).thenReturn(invitedPosition);
        assertNonPublicElementIsRefused(invitedPosition, () -> serviceDelegator.viewInvitedPosition(ORCID, 33L));
        assertNull(invitedPosition.getPath());
    }

    @Test
    public void testGetPrivateInvitedPositionUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        InvitedPosition invitedPosition = affiliation(new InvitedPosition(), 34L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 34L)).thenReturn(invitedPosition);
        assertNonPublicElementIsRefused(invitedPosition, () -> serviceDelegator.viewInvitedPosition(ORCID, 34L));
        assertNull(invitedPosition.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Memberships
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewMembership() {
        Membership membership = affiliation(new Membership(), 37L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L)).thenReturn(membership);

        Response response = serviceDelegator.viewMembership(ORCID, 37L);

        assertSame(membership, response.getEntity());
        assertEquals("/0000-0000-0000-0003/membership/37", membership.getPath());
        verify(publicAPISecurityManagerV3).checkIsPublic(membership);
        verify(sourceUtilsReadOnly).setSourceName(membership);
    }

    @Test
    public void testViewMemberships() {
        MembershipSummary publicSummary = affiliationSummary(new MembershipSummary(), 37L, Visibility.PUBLIC);
        MembershipSummary limitedSummary = affiliationSummary(new MembershipSummary(), 38L, Visibility.LIMITED);
        MembershipSummary privateSummary = affiliationSummary(new MembershipSummary(), 39L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getMembershipSummaryList(ORCID))
                .thenReturn(new ArrayList<MembershipSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));
        stubAffiliationGrouping();

        Response response = serviceDelegator.viewMemberships(ORCID);

        Memberships entity = (Memberships) response.getEntity();
        assertEquals("/0000-0000-0000-0003/memberships", entity.getPath());
        assertEquals(1, entity.retrieveGroups().size());
        MembershipSummary returned = entity.retrieveGroups().iterator().next().getActivities().get(0);
        assertSame(publicSummary, returned);
        assertEquals("/0000-0000-0000-0003/membership/37", returned.getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicMembershipUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Membership membership = affiliation(new Membership(), 37L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L)).thenReturn(membership);

        Response r = serviceDelegator.viewMembership(ORCID, 37L);

        assertEquals(Long.valueOf(37), ((Membership) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(membership);
    }

    @Test
    public void testGetPublicMemberships() {
        MembershipSummary publicSummary = affiliationSummary(new MembershipSummary(), 37L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getMembershipSummaryList(ORCID))
                .thenReturn(new ArrayList<MembershipSummary>(Arrays.asList(publicSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewMemberships(ORCID);

        Memberships entity = (Memberships) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertEquals(Long.valueOf(37), entity.retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicMembershipsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        MembershipSummary publicSummary = affiliationSummary(new MembershipSummary(), 37L, Visibility.PUBLIC);
        MembershipSummary limitedSummary = affiliationSummary(new MembershipSummary(), 38L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getMembershipSummaryList(ORCID))
                .thenReturn(new ArrayList<MembershipSummary>(Arrays.asList(publicSummary, limitedSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewMemberships(ORCID);

        Memberships entity = (Memberships) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertSame(publicSummary, entity.retrieveGroups().iterator().next().getActivities().get(0));
    }

    @Test
    public void testGetLimitedMembershipUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Membership membership = affiliation(new Membership(), 38L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 38L)).thenReturn(membership);
        assertNonPublicElementIsRefused(membership, () -> serviceDelegator.viewMembership(ORCID, 38L));
        assertNull(membership.getPath());
    }

    @Test
    public void testGetPrivateMembershipUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Membership membership = affiliation(new Membership(), 39L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 39L)).thenReturn(membership);
        assertNonPublicElementIsRefused(membership, () -> serviceDelegator.viewMembership(ORCID, 39L));
        assertNull(membership.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Qualifications
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewQualification() {
        Qualification qualification = affiliation(new Qualification(), 42L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L)).thenReturn(qualification);

        Response response = serviceDelegator.viewQualification(ORCID, 42L);

        assertSame(qualification, response.getEntity());
        assertEquals("/0000-0000-0000-0003/qualification/42", qualification.getPath());
        verify(publicAPISecurityManagerV3).checkIsPublic(qualification);
        verify(sourceUtilsReadOnly).setSourceName(qualification);
    }

    @Test
    public void testViewQualifications() {
        QualificationSummary publicSummary = affiliationSummary(new QualificationSummary(), 42L, Visibility.PUBLIC);
        QualificationSummary limitedSummary = affiliationSummary(new QualificationSummary(), 43L, Visibility.LIMITED);
        QualificationSummary privateSummary = affiliationSummary(new QualificationSummary(), 44L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getQualificationSummaryList(ORCID))
                .thenReturn(new ArrayList<QualificationSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));
        stubAffiliationGrouping();

        Response response = serviceDelegator.viewQualifications(ORCID);

        Qualifications entity = (Qualifications) response.getEntity();
        assertEquals("/0000-0000-0000-0003/qualifications", entity.getPath());
        assertEquals(1, entity.retrieveGroups().size());
        QualificationSummary returned = entity.retrieveGroups().iterator().next().getActivities().get(0);
        assertSame(publicSummary, returned);
        assertEquals("/0000-0000-0000-0003/qualification/42", returned.getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicQualificationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Qualification qualification = affiliation(new Qualification(), 42L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 42L)).thenReturn(qualification);

        Response r = serviceDelegator.viewQualification(ORCID, 42L);

        assertEquals(Long.valueOf(42), ((Qualification) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(qualification);
    }

    @Test
    public void testGetPublicQualifications() {
        QualificationSummary publicSummary = affiliationSummary(new QualificationSummary(), 42L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getQualificationSummaryList(ORCID))
                .thenReturn(new ArrayList<QualificationSummary>(Arrays.asList(publicSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewQualifications(ORCID);

        Qualifications entity = (Qualifications) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertEquals(Long.valueOf(42), entity.retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicQualificationsUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        QualificationSummary publicSummary = affiliationSummary(new QualificationSummary(), 42L, Visibility.PUBLIC);
        QualificationSummary limitedSummary = affiliationSummary(new QualificationSummary(), 43L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getQualificationSummaryList(ORCID))
                .thenReturn(new ArrayList<QualificationSummary>(Arrays.asList(publicSummary, limitedSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewQualifications(ORCID);

        Qualifications entity = (Qualifications) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertSame(publicSummary, entity.retrieveGroups().iterator().next().getActivities().get(0));
    }

    @Test
    public void testGetLimitedQualificationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Qualification qualification = affiliation(new Qualification(), 43L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 43L)).thenReturn(qualification);
        assertNonPublicElementIsRefused(qualification, () -> serviceDelegator.viewQualification(ORCID, 43L));
        assertNull(qualification.getPath());
    }

    @Test
    public void testGetPrivateQualificationUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Qualification qualification = affiliation(new Qualification(), 44L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getQualificationAffiliation(ORCID, 44L)).thenReturn(qualification);
        assertNonPublicElementIsRefused(qualification, () -> serviceDelegator.viewQualification(ORCID, 44L));
        assertNull(qualification.getPath());
    }

    /*
     * ------------------------------------------------------------------
     * Services
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewService() {
        Service service = affiliation(new Service(), 47L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L)).thenReturn(service);

        Response response = serviceDelegator.viewService(ORCID, 47L);

        assertSame(service, response.getEntity());
        assertEquals("/0000-0000-0000-0003/service/47", service.getPath());
        verify(publicAPISecurityManagerV3).checkIsPublic(service);
        verify(sourceUtilsReadOnly).setSourceName(service);
    }

    @Test
    public void testViewServices() {
        ServiceSummary publicSummary = affiliationSummary(new ServiceSummary(), 47L, Visibility.PUBLIC);
        ServiceSummary limitedSummary = affiliationSummary(new ServiceSummary(), 48L, Visibility.LIMITED);
        ServiceSummary privateSummary = affiliationSummary(new ServiceSummary(), 49L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getServiceSummaryList(ORCID))
                .thenReturn(new ArrayList<ServiceSummary>(Arrays.asList(publicSummary, limitedSummary, privateSummary)));
        stubAffiliationGrouping();

        Response response = serviceDelegator.viewServices(ORCID);

        Services entity = (Services) response.getEntity();
        assertEquals("/0000-0000-0000-0003/services", entity.getPath());
        assertEquals(1, entity.retrieveGroups().size());
        ServiceSummary returned = entity.retrieveGroups().iterator().next().getActivities().get(0);
        assertSame(publicSummary, returned);
        assertEquals("/0000-0000-0000-0003/service/47", returned.getPath());
        assertEquals(publicSummary.getLastModifiedDate(), entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicServiceUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Service service = affiliation(new Service(), 47L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 47L)).thenReturn(service);

        Response r = serviceDelegator.viewService(ORCID, 47L);

        assertEquals(Long.valueOf(47), ((Service) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(service);
    }

    @Test
    public void testGetPublicServices() {
        ServiceSummary publicSummary = affiliationSummary(new ServiceSummary(), 47L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getServiceSummaryList(ORCID))
                .thenReturn(new ArrayList<ServiceSummary>(Arrays.asList(publicSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewServices(ORCID);

        Services entity = (Services) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertEquals(Long.valueOf(47), entity.retrieveGroups().iterator().next().getActivities().get(0).getPutCode());
        assertNotNull(entity.getLastModifiedDate());
    }

    @Test
    public void testGetPublicServicesUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        ServiceSummary publicSummary = affiliationSummary(new ServiceSummary(), 47L, Visibility.PUBLIC);
        ServiceSummary limitedSummary = affiliationSummary(new ServiceSummary(), 48L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getServiceSummaryList(ORCID))
                .thenReturn(new ArrayList<ServiceSummary>(Arrays.asList(publicSummary, limitedSummary)));
        stubAffiliationGrouping();

        Response r = serviceDelegator.viewServices(ORCID);

        Services entity = (Services) r.getEntity();
        assertEquals(1, entity.retrieveGroups().size());
        assertSame(publicSummary, entity.retrieveGroups().iterator().next().getActivities().get(0));
    }

    @Test
    public void testGetLimitedServiceUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Service service = affiliation(new Service(), 48L, Visibility.LIMITED);
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 48L)).thenReturn(service);
        assertNonPublicElementIsRefused(service, () -> serviceDelegator.viewService(ORCID, 48L));
        assertNull(service.getPath());
    }

    @Test
    public void testGetPrivateServiceUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Service service = affiliation(new Service(), 49L, Visibility.PRIVATE);
        when(affiliationsManagerReadOnly.getServiceAffiliation(ORCID, 49L)).thenReturn(service);
        assertNonPublicElementIsRefused(service, () -> serviceDelegator.viewService(ORCID, 49L));
        assertNull(service.getPath());
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(otherName);
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
        verify(publicAPISecurityManagerV3).filter(otherNames);
        verify(sourceUtilsReadOnly).setSourceName(otherNames);
    }

    @Test
    public void testGetPublicOtherNameUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        OtherName otherName = otherName(13L, Visibility.PUBLIC, "Other Name PUBLIC");
        when(otherNameManagerReadOnly.getOtherName(ORCID, 13L)).thenReturn(otherName);

        Response r = serviceDelegator.viewOtherName(ORCID, 13L);

        assertEquals(Long.valueOf(13), ((OtherName) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(otherName);
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(keyword);
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
        verify(publicAPISecurityManagerV3).filter(keywords);
        verify(sourceUtilsReadOnly).setSourceName(keywords);
    }

    @Test
    public void testGetPublicKeywordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Keyword keyword = keyword(9L, Visibility.PUBLIC, "PUBLIC");
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(keyword);

        Response r = serviceDelegator.viewKeyword(ORCID, 9L);

        assertEquals(Long.valueOf(9), ((Keyword) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(keyword);
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(extId);
        verify(sourceUtilsReadOnly).setSourceName(extId);
    }

    /**
     * The order the external identifiers arrive in is a DAO ordering concern,
     * so the old positional assertions (index 0 is put code 19, and so on) have
     * been dropped: against a mock they only restate the order this test itself
     * built. What is left is the delegator's own contribution, a path on the
     * container and on every element.
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
        verify(publicAPISecurityManagerV3).filter(extIds);
        verify(sourceUtilsReadOnly).setSourceName(extIds);
    }

    @Test
    public void testGetPublicExternalIdentifierUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        PersonExternalIdentifier extId = externalIdentifier(13L, Visibility.PUBLIC, "public_type", "http://ext-id/public_ref");
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L)).thenReturn(extId);

        Response r = serviceDelegator.viewExternalIdentifier(ORCID, 13L);

        assertEquals(Long.valueOf(13), ((PersonExternalIdentifier) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(extId);
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(rUrl);
        verify(sourceUtilsReadOnly).setSourceName(rUrl);
    }

    /**
     * viewResearcherUrls is the one list endpoint that never calls
     * PublicAPISecurityManagerV3.filter; it relies entirely on
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
        verifyNoInteractions(publicAPISecurityManagerV3);
        verify(sourceUtilsReadOnly).setSourceName(rUrls);
    }

    @Test
    public void testGetPublicResearcherUrlUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        ResearcherUrl rUrl = researcherUrl(13L, Visibility.PUBLIC);
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L)).thenReturn(rUrl);

        Response r = serviceDelegator.viewResearcherUrl(ORCID, 13L);

        assertEquals(Long.valueOf(13), ((ResearcherUrl) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(rUrl);
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(address);
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
        verify(publicAPISecurityManagerV3).filter(addresses);
        verify(sourceUtilsReadOnly).setSourceName(addresses);
    }

    @Test
    public void testGetPublicAddressUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Address address = address(9L, Visibility.PUBLIC);
        when(addressManagerReadOnly.getAddress(ORCID, 9L)).thenReturn(address);

        Response r = serviceDelegator.viewAddress(ORCID, 9L);

        assertEquals(Long.valueOf(9), ((Address) r.getEntity()).getPutCode());
        verify(publicAPISecurityManagerV3).checkIsPublic(address);
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
     * persistence tests; stubbing getPublicEmails with two public rows and then
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).filter(emails);
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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).checkIsPublic(bio);
    }

    @Test
    public void testGetLimitedBiographyUsingToken() {
        String orcid = "0000-0000-0000-0002";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.READ_LIMITED);
        Biography bio = biography(Visibility.LIMITED);
        when(biographyManagerReadOnly.getBiography(orcid)).thenReturn(bio);
        doThrow(new OrcidNonPublicElementException()).when(publicAPISecurityManagerV3).checkIsPublic(bio);

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
        doThrow(new OrcidNonPublicElementException()).when(publicAPISecurityManagerV3).checkIsPublic(bio);

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
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).filter(personalDetails);
        verify(sourceUtilsReadOnly).setSourceName(personalDetails);
    }

    @Test
    public void testFindPerson() {
        Person person = person();
        when(personDetailsManagerReadOnly.getPublicPersonDetails(ORCID)).thenReturn(person);

        Response response = serviceDelegator.viewPerson(ORCID);

        assertSame(person, response.getEntity());
        validatePersonPaths(person);
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).filter(person);
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
        verify(publicAPISecurityManagerV3).filter(person);
    }

    /*
     * ------------------------------------------------------------------
     * Activities and record
     * ------------------------------------------------------------------
     */

    @Test
    public void testFindActivityDetails() {
        ActivitiesSummary summary = activitiesSummary();
        when(activitiesSummaryManagerReadOnly.getPublicActivitiesSummary(ORCID, false)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        assertSame(summary, response.getEntity());
        validateActivityPaths(summary);
        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(publicAPISecurityManagerV3).filter(summary);
        verify(sourceUtilsReadOnly).setSourceName(summary);
    }

    @Test
    public void testValidateActivitiesUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        ActivitiesSummary summary = activitiesSummary();
        when(activitiesSummaryManagerReadOnly.getPublicActivitiesSummary(ORCID, false)).thenReturn(summary);

        Response response = serviceDelegator.viewActivities(ORCID);

        assertSame(summary, response.getEntity());
        validateActivityPaths(summary);
        verify(publicAPISecurityManagerV3).filter(summary);
    }

    /**
     * viewRecord is a one line pass through to PublicRecordUtils, which is
     * where the profile guard, the visibility filter, the source naming and the
     * path building for the whole record actually happen. All this endpoint can
     * be held to is that it asks for the record with the flag it was configured
     * with and returns what it got.
     */
    @Test
    public void testFindRecord() {
        Record record = new Record();
        when(publicRecordUtils.getPublicRecord(ORCID, false)).thenReturn(record);

        Response response = serviceDelegator.viewRecord(ORCID);

        assertNotNull(response);
        assertSame(record, response.getEntity());
        verify(publicRecordUtils).getPublicRecord(ORCID, false);
    }

    @Test
    public void testValidateRecordUsingToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Record record = new Record();
        when(publicRecordUtils.getPublicRecord(ORCID, false)).thenReturn(record);

        Response response = serviceDelegator.viewRecord(ORCID);

        assertSame(record, response.getEntity());
        verify(publicRecordUtils).getPublicRecord(ORCID, false);
    }

    @Test
    public void testViewRecordFiltersVersionOfIdentifiersWhenConfiguredTo() {
        serviceDelegator.setFilterVersionOfIdentifiers(true);
        try {
            Record record = new Record();
            when(publicRecordUtils.getPublicRecord(ORCID, true)).thenReturn(record);

            Response response = serviceDelegator.viewRecord(ORCID);

            assertSame(record, response.getEntity());
            verify(publicRecordUtils).getPublicRecord(ORCID, true);
        } finally {
            serviceDelegator.setFilterVersionOfIdentifiers(false);
        }
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

    @Test
    public void testExpandedSearchByQueryNoRowsParamSet() throws ParseException {
        ExpandedSearch search = new ExpandedSearch();
        search.getResults().add(new ExpandedResult());
        when(orcidSearchManager.expandedSearch(ArgumentMatchers.<Map<String, List<String>>> any())).thenReturn(search);
        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn(null);

        Map<String, List<String>> searchQuery = new HashMap<String, List<String>>();
        searchQuery.put("q", Arrays.asList("orcid"));
        serviceDelegator.expandedSearchByQuery(searchQuery);

        verify(orcidSearchManager).expandedSearch(searchParamsCaptor.capture());
        Map<String, List<String>> actualParams = searchParamsCaptor.getValue();
        assertNotNull(actualParams);
        assertNotNull(actualParams.keySet());
        assertFalse(actualParams.keySet().isEmpty());
        assertNotNull(actualParams.get("q"));
        assertEquals("orcid", actualParams.get("q").get(0));
        assertNotNull(actualParams.get("rows"));
        assertEquals(String.valueOf(OrcidSearchManager.DEFAULT_SEARCH_ROWS), actualParams.get("rows").get(0));
    }

    /*
     * ------------------------------------------------------------------
     * Client
     * ------------------------------------------------------------------
     */

    @Test
    public void testViewClientNonExistent() {
        when(clientDetailsManagerReadOnly.getClientSummary("some-client-that-doesn't-exist")).thenThrow(new NoResultException());
        try {
            serviceDelegator.viewClient("some-client-that-doesn't-exist");
            fail();
        } catch (NoResultException expected) {
        }
    }

    @Test
    public void testViewClientSummary() throws ParseException {
        when(clientDetailsManagerReadOnly.getClientSummary("APP-6666666666666666")).thenReturn(clientSummary());

        Response response = serviceDelegator.viewClient("APP-6666666666666666");

        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);
        ClientSummary summary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", summary.getName());
        assertEquals("A test source client", summary.getDescription());
        verify(clientDetailsManagerReadOnly).getClientSummary("APP-6666666666666666");
    }

    @Test
    public void testViewClient() {
        when(clientDetailsManagerReadOnly.getClientSummary("APP-6666666666666666")).thenReturn(clientSummary());

        Response response = serviceDelegator.viewClient("APP-6666666666666666");

        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);
        ClientSummary clientSummary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", clientSummary.getName());
        assertEquals("A test source client", clientSummary.getDescription());
        // viewClient is not record scoped, so there is no profile to check
        verify(orcidSecurityManager, never()).checkProfile(anyString());
    }

    /*
     * ------------------------------------------------------------------
     * schema.org rendering of the public record
     * ------------------------------------------------------------------
     */

    /**
     * SchemaOrgMBWriterV3 is not a collaborator of the delegator, it is the
     * writer the public record endpoint is rendered through. Its only
     * collaborator is the identifier normalisation service, so it runs here for
     * real over a hand built public record; previously the record came from the
     * database and the assertions on it were really assertions about the
     * fixture. Work and funding organisation identifiers are not covered:
     * those go through the normalisation service and belong with it.
     */
    @Test
    public void testSchemaOrgMBWriterV3() throws WebApplicationException, IOException {
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
        when(publicRecordUtils.getPublicRecord(ORCID, false)).thenReturn(record);

        SchemaOrgMBWriterV3 writerV3 = new SchemaOrgMBWriterV3();
        ReflectionTestUtils.setField(writerV3, "norm", norm);

        Response response = serviceDelegator.viewRecord(ORCID);
        Record fromEndpoint = (Record) response.getEntity();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writerV3.writeTo(fromEndpoint, fromEndpoint.getClass(), null, null, null, null, out);

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
     * The three checkSourceOnEmail tests used to assert that a non verified
     * professional email and a verified non professional email keep source
     * APP-5555555555555555 and the source name "Source Client 1". Both the
     * verified flag and the source name rewriting live below this layer, in
     * EmailManagerReadOnly and in SourceUtils/SourceNameCacheManager, and both
     * are mocks here; re-asserting them would only restate the stub. There is
     * no SourceUtils test in the repository to move that claim into, so it is
     * currently uncovered - see the commit message. What these three keep is
     * the part that is this layer's: each of the three endpoints that can
     * return emails puts them through the public gate and the source name
     * decoration, for a client-only caller.
     */

    @Test
    public void checkSourceOnEmail_RecordEndpointTest() {
        String orcid = "0000-0000-0000-0001";
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_LIMITED);
        Record record = new Record();
        Person person = new Person();
        person.setEmails(emailsOf(orcid));
        record.setPerson(person);
        when(publicRecordUtils.getPublicRecord(orcid, false)).thenReturn(record);

        Response r = serviceDelegator.viewRecord(orcid);

        Record returned = (Record) r.getEntity();
        assertNotNull(returned.getPerson());
        assertNotNull(returned.getPerson().getEmails());
        assertEquals(2, returned.getPerson().getEmails().getEmails().size());
        verify(publicRecordUtils).getPublicRecord(orcid, false);
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
        verify(publicAPISecurityManagerV3).filter(p);
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
        verify(publicAPISecurityManagerV3).filter(emails);
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
    private void assertNonPublicElementIsRefused(org.orcid.jaxb.model.v3.release.common.VisibilityType element, Runnable call) {
        doThrow(new OrcidNonPublicElementException()).when(publicAPISecurityManagerV3).checkIsPublic(element);
        try {
            call.run();
            fail("expected OrcidNonPublicElementException");
        } catch (OrcidNonPublicElementException expected) {
        }
        verify(publicAPISecurityManagerV3).checkIsPublic(element);
        verifyNoInteractions(sourceUtilsReadOnly);
    }

    /**
     * groupAffiliations is a pure regrouping of the list it is handed. Echoing
     * the list back one group per element keeps the delegator's own public-only
     * loop observable; a bare mock would return null and every affiliation list
     * endpoint would fail on construction instead.
     */
    private void stubAffiliationGrouping() {
        when(affiliationsManagerReadOnly.groupAffiliations(anyList(), eq(true))).thenAnswer(invocation -> {
            List<AffiliationSummary> given = invocation.getArgument(0);
            List<AffiliationGroup<AffiliationSummary>> groups = new ArrayList<AffiliationGroup<AffiliationSummary>>();
            for (AffiliationSummary summary : given) {
                AffiliationGroup<AffiliationSummary> group = new AffiliationGroup<AffiliationSummary>();
                group.getActivities().add(summary);
                groups.add(group);
            }
            return groups;
        });
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
        PeerReviewDuplicateGroup duplicateGroup = new PeerReviewDuplicateGroup();
        for (PeerReviewSummary summary : summaries) {
            duplicateGroup.getPeerReviewSummary().add(summary);
        }
        group.getPeerReviewGroup().add(duplicateGroup);
        peerReviews.getPeerReviewGroup().add(group);
        return peerReviews;
    }

    private static <T extends Affiliation> T affiliation(T affiliation, Long putCode, Visibility visibility) {
        affiliation.setPutCode(putCode);
        affiliation.setVisibility(visibility);
        affiliation.setLastModifiedDate(lastModified(putCode));
        affiliation.setDepartmentName("PUBLIC Department");
        return affiliation;
    }

    private static <T extends AffiliationSummary> T affiliationSummary(T summary, Long putCode, Visibility visibility) {
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
        summary.setEducations(new Educations(affiliationGroups(affiliationSummary(new EducationSummary(), 20L, Visibility.PUBLIC))));
        summary.setEmployments(new Employments(affiliationGroups(affiliationSummary(new EmploymentSummary(), 17L, Visibility.PUBLIC))));
        summary.setDistinctions(new Distinctions(affiliationGroups(affiliationSummary(new DistinctionSummary(), 27L, Visibility.PUBLIC))));
        summary.setInvitedPositions(new InvitedPositions(affiliationGroups(affiliationSummary(new InvitedPositionSummary(), 32L, Visibility.PUBLIC))));
        summary.setMemberships(new Memberships(affiliationGroups(affiliationSummary(new MembershipSummary(), 37L, Visibility.PUBLIC))));
        summary.setQualifications(new Qualifications(affiliationGroups(affiliationSummary(new QualificationSummary(), 42L, Visibility.PUBLIC))));
        summary.setServices(new Services(affiliationGroups(affiliationSummary(new ServiceSummary(), 47L, Visibility.PUBLIC))));
        return summary;
    }

    private static <T extends AffiliationSummary> List<AffiliationGroup<T>> affiliationGroups(T summary) {
        List<AffiliationGroup<T>> groups = new ArrayList<AffiliationGroup<T>>();
        AffiliationGroup<T> group = new AffiliationGroup<T>();
        group.getActivities().add(summary);
        groups.add(group);
        return groups;
    }

    private void validateActivityPaths(ActivitiesSummary summary) {
        assertEquals("/0000-0000-0000-0003/activities", summary.getPath());
        assertEquals("/0000-0000-0000-0003/works", summary.getWorks().getPath());
        assertEquals("/0000-0000-0000-0003/work/11", summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/fundings", summary.getFundings().getPath());
        assertEquals("/0000-0000-0000-0003/funding/10", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/peer-reviews", summary.getPeerReviews().getPath());
        assertEquals("/0000-0000-0000-0003/educations", summary.getEducations().getPath());
        assertEquals("/0000-0000-0000-0003/education/20", summary.getEducations().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/employments", summary.getEmployments().getPath());
        assertEquals("/0000-0000-0000-0003/employment/17", summary.getEmployments().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/distinctions", summary.getDistinctions().getPath());
        assertEquals("/0000-0000-0000-0003/distinction/27", summary.getDistinctions().retrieveGroups().iterator().next().getActivities().get(0).getPath());
        assertEquals("/0000-0000-0000-0003/invited-positions", summary.getInvitedPositions().getPath());
        assertEquals("/0000-0000-0000-0003/memberships", summary.getMemberships().getPath());
        assertEquals("/0000-0000-0000-0003/qualifications", summary.getQualifications().getPath());
        assertEquals("/0000-0000-0000-0003/services", summary.getServices().getPath());
        assertNotNull(summary.getLastModifiedDate());
    }

    private static ClientSummary clientSummary() {
        ClientSummary summary = new ClientSummary();
        summary.setName("Source Client 2");
        summary.setDescription("A test source client");
        return summary;
    }
}
