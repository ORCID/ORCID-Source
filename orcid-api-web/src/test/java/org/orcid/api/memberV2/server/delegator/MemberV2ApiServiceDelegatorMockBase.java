package org.orcid.api.memberV2.server.delegator;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import jakarta.ws.rs.core.Response;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.api.common.util.ApiUtils;
import org.orcid.api.memberV2.server.delegator.impl.MemberV2ApiServiceDelegatorImpl;
import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AddressManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.manager.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.ClientManagerReadOnly;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.ContributorUtils;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.SourceUtils;
import org.orcid.jaxb.model.common_v2.CreatedDate;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.jaxb.model.common_v2.SourceOrcid;
import org.orcid.jaxb.model.common_v2.Organization;
import org.orcid.jaxb.model.common_v2.OrganizationAddress;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
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
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.FundingTitle;
import org.orcid.jaxb.model.record_v2.FundingType;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.utils.DateUtils;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Everything the eighteen {@code MemberV2ApiServiceDelegator_*Test} classes
 * share: the class under test, its collaborators, and the two objects that must
 * deliberately NOT be mocks.
 *
 * <p>
 * The delegator itself holds no DAO and reads no row. What it does is: ask a
 * read-only manager for an element, hand that element to
 * {@link OrcidSecurityManager} for the access-control decision, decorate what
 * comes back with paths, source names and last-modified dates, and wrap it in a
 * {@code Response}. Only the decoration is the delegator's own behaviour, so
 * that is what these tests assert; the access-control decision is asserted as
 * "the guard was called with these arguments", and proved on its own in
 * {@code org.orcid.core.manager.OrcidSecurityManager*Test}.
 *
 * <p>
 * <b>Why {@code checkAndFilter} cannot be re-implemented here.</b>
 * {@code OrcidSecurityManager.checkAndFilter} returns void and edits the
 * collection it is given in place -- it removes denied elements and, for a
 * {@code WorkBulk}, replaces them with an {@code OrcidError}. A plain mock does
 * none of that, so any assertion of the form "only the public elements came
 * back" passes for the wrong reason the moment the manager is mocked. Those
 * assertions therefore do not appear below; each one has moved to the security
 * manager's own tests, and what remains at this boundary is
 * {@code verify(orcidSecurityManager).checkAndFilter(...)} with the arguments
 * the delegator is contracted to pass, plus a {@code doThrow} case showing that
 * a refusal actually stops the operation. Writing a {@code doAnswer} that
 * filters would test the test.
 *
 * <p>
 * {@code MockitoJUnitRunner.Silent} rather than the bare runner: Mockito 3.3.0
 * defaults to strict stubs, and a shared base necessarily stubs collaborators
 * that a given subclass never touches.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class MemberV2ApiServiceDelegatorMockBase {

    /** The record every test in this family reads and writes. */
    protected static final String ORCID = "0000-0000-0000-0003";

    /** The member client that acts, and is the source of most fixture elements. */
    protected static final String CLIENT_1 = "APP-5555555555555555";

    /** A different member client: the source of elements CLIENT_1 may not touch. */
    protected static final String CLIENT_2 = "APP-5555555555555556";

    protected static final String CLIENT_1_NAME = "Source Client 1";

    /**
     * The class under test. Declared as the implementation because
     * {@code @InjectMocks} needs a concrete type; every call below goes through
     * the {@link MemberV2ApiServiceDelegator} interface.
     */
    @InjectMocks
    protected MemberV2ApiServiceDelegatorImpl serviceDelegator = new MemberV2ApiServiceDelegatorImpl();

    // Managers that go to the primary database
    @Mock
    protected WorkManager workManager;

    @Mock
    protected ProfileFundingManager profileFundingManager;

    @Mock
    protected ProfileEntityManager profileEntityManager;

    @Mock
    protected AffiliationsManager affiliationsManager;

    @Mock
    protected PeerReviewManager peerReviewManager;

    /** The guard. Every access-control negative test is a doThrow on this mock. */
    @Mock
    protected OrcidSecurityManager orcidSecurityManager;

    @Mock
    protected GroupIdRecordManager groupIdRecordManager;

    @Mock
    protected LocaleManager localeManager;

    @Mock
    protected ResearcherUrlManager researcherUrlManager;

    @Mock
    protected OtherNameManager otherNameManager;

    @Mock
    protected ExternalIdentifierManager externalIdentifierManager;

    @Mock
    protected ProfileKeywordManager profileKeywordManager;

    @Mock
    protected AddressManager addressManager;

    @Mock
    protected ContributorUtils contributorUtils;

    @Mock
    protected OrcidSearchManager orcidSearchManager;

    // Managers that go to the replication database
    @Mock
    protected WorkManagerReadOnly workManagerReadOnly;

    @Mock
    protected ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Mock
    protected AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Mock
    protected PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Mock
    protected ActivitiesSummaryManagerReadOnly activitiesSummaryManagerReadOnly;

    @Mock
    protected ResearcherUrlManagerReadOnly researcherUrlManagerReadOnly;

    @Mock
    protected OtherNameManagerReadOnly otherNameManagerReadOnly;

    @Mock
    protected EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    protected ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;

    @Mock
    protected PersonalDetailsManagerReadOnly personalDetailsManagerReadOnly;

    @Mock
    protected ProfileKeywordManagerReadOnly profileKeywordManagerReadOnly;

    @Mock
    protected AddressManagerReadOnly addressManagerReadOnly;

    @Mock
    protected BiographyManagerReadOnly biographyManagerReadOnly;

    @Mock
    protected PersonDetailsManagerReadOnly personDetailsManagerReadOnly;

    @Mock
    protected RecordManagerReadOnly recordManagerReadOnly;

    @Mock
    protected GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

    @Mock
    protected ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Mock
    protected ClientManagerReadOnly clientManagerReadOnly;

    @Mock
    protected MessageSource messageSource;

    @Mock
    protected ApiUtils apiUtils;

    @Mock
    protected EmailDomainManager emailDomainManager;

    @Mock
    protected SourceEntityUtils sourceEntityUtils;

    /**
     * Not a collaborator of the delegator: the cache the real {@link SourceUtils}
     * below reads to turn a source id into a display name.
     */
    @Mock
    protected SourceNameCacheManager sourceNameCacheManager;

    /**
     * Deliberately real, not a mock. {@code SourceUtils.setSourceName} is called
     * on essentially every element the delegator returns, and it is the only
     * thing that fills in {@code source.sourceName}. Mocking it would silently
     * delete every source-name assertion in this family, and there is no
     * {@code SourceUtilsTest} to inherit that coverage from. Its one dependency
     * is the cache manager above, so a real instance costs nothing.
     */
    protected SourceUtils sourceUtils = new SourceUtils();

    @Before
    public void wireUnmockedCollaborators() {
        sourceUtils.setSourceNameCacheManager(sourceNameCacheManager);
        // @InjectMocks only injects mocks, so the real SourceUtils has to be set
        // by hand -- and after initMocks, which the runner has already run.
        ReflectionTestUtils.setField(serviceDelegator, "sourceUtils", sourceUtils);

        when(sourceNameCacheManager.retrieve(CLIENT_1)).thenReturn(CLIENT_1_NAME);

        // Every create* method ends in apiUtils.buildApiResponse; without this
        // every create test would NPE on the null Response. The URI is shaped
        // like the real one so Utils.getPutCode can still read the put code back.
        when(apiUtils.buildApiResponse(nullable(String.class), anyString(), anyString(), anyString())).thenAnswer(invocation -> {
            String orcid = invocation.getArgument(0);
            String target = invocation.getArgument(1);
            String putCode = invocation.getArgument(2);
            return Response.created(URI.create("/" + (orcid == null ? "" : orcid + "/") + target + "/" + putCode)).build();
        });
    }

    // ------------------------------------------------------------- fixtures

    /** A source whose path is a member client id, as most fixture elements have. */
    protected static Source clientSource(String clientId) {
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(clientId));
        return source;
    }

    /** A source whose path is a record: an element the user created themselves. */
    protected static Source userSource(String orcid) {
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid(orcid));
        return source;
    }

    protected static LastModifiedDate lastModified() {
        return new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis()));
    }

    protected static CreatedDate createdDate() {
        return new CreatedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis()));
    }

    /**
     * A small but complete activities summary: one education, one employment,
     * one work group, one funding group and one peer-review group, all sourced by
     * {@link #CLIENT_1}.
     *
     * <p>
     * Works, fundings and peer reviews are always present, never null.
     * {@code Api2_0_LastModifiedDatesHelper.calculateLastModified(ActivitiesSummary)}
     * dispatches those three to its {@code GroupsContainer} overload, which does
     * not null-check its argument, so a summary with a null Works would fail
     * inside production code rather than in the assertion.
     */
    protected ActivitiesSummary activitiesSummary() {
        ActivitiesSummary summary = new ActivitiesSummary();

        EducationSummary education = new EducationSummary();
        education.setPutCode(20L);
        education.setDepartmentName("Education Dept # 1");
        education.setRoleTitle("Education");
        education.setOrganization(organization());
        education.setVisibility(Visibility.PUBLIC);
        education.setSource(clientSource(CLIENT_1));
        education.setCreatedDate(createdDate());
        education.setLastModifiedDate(lastModified());
        summary.setEducations(new Educations(new ArrayList<>(Arrays.asList(education))));

        EmploymentSummary employment = new EmploymentSummary();
        employment.setPutCode(17L);
        employment.setDepartmentName("Employment Dept # 1");
        employment.setRoleTitle("Employment");
        employment.setOrganization(organization());
        employment.setVisibility(Visibility.PUBLIC);
        employment.setSource(clientSource(CLIENT_1));
        employment.setCreatedDate(createdDate());
        employment.setLastModifiedDate(lastModified());
        summary.setEmployments(new Employments(new ArrayList<>(Arrays.asList(employment))));

        WorkSummary work = new WorkSummary();
        work.setPutCode(11L);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("PUBLIC"));
        work.setTitle(workTitle);
        work.setType(WorkType.JOURNAL_ARTICLE);
        work.setExternalIdentifiers(externalIds("doi", "1"));
        work.setVisibility(Visibility.PUBLIC);
        work.setSource(clientSource(CLIENT_1));
        work.setCreatedDate(createdDate());
        work.setLastModifiedDate(lastModified());
        WorkGroup workGroup = new WorkGroup();
        workGroup.getWorkSummary().add(work);
        workGroup.getIdentifiers().getExternalIdentifier().add(externalId("doi", "1"));
        Works works = new Works();
        works.getWorkGroup().add(workGroup);
        summary.setWorks(works);

        FundingSummary funding = new FundingSummary();
        funding.setPutCode(10L);
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title("Public Funding # 1"));
        funding.setTitle(fundingTitle);
        funding.setType(FundingType.GRANT);
        funding.setOrganization(organization());
        funding.setExternalIdentifiers(externalIds("grant_number", "1"));
        funding.setVisibility(Visibility.PUBLIC);
        funding.setSource(clientSource(CLIENT_1));
        funding.setCreatedDate(createdDate());
        funding.setLastModifiedDate(lastModified());
        FundingGroup fundingGroup = new FundingGroup();
        fundingGroup.getFundingSummary().add(funding);
        fundingGroup.getIdentifiers().getExternalIdentifier().add(externalId("grant_number", "1"));
        Fundings fundings = new Fundings();
        fundings.getFundingGroup().add(fundingGroup);
        summary.setFundings(fundings);

        PeerReviewSummary peerReview = new PeerReviewSummary();
        peerReview.setPutCode(9L);
        peerReview.setGroupId("issn:0000-0001");
        peerReview.setOrganization(organization());
        peerReview.setExternalIdentifiers(externalIds("doi", "peer-review-1"));
        peerReview.setVisibility(Visibility.PUBLIC);
        peerReview.setSource(clientSource(CLIENT_1));
        peerReview.setCreatedDate(createdDate());
        peerReview.setLastModifiedDate(lastModified());
        PeerReviewGroup peerReviewGroup = new PeerReviewGroup();
        peerReviewGroup.getPeerReviewSummary().add(peerReview);
        peerReviewGroup.getIdentifiers().getExternalIdentifier().add(externalId("doi", "peer-review-1"));
        PeerReviews peerReviews = new PeerReviews();
        peerReviews.getPeerReviewGroup().add(peerReviewGroup);
        summary.setPeerReviews(peerReviews);

        summary.setLastModifiedDate(lastModified());
        return summary;
    }

    protected static Organization organization() {
        Organization organization = new Organization();
        organization.setName("Org Name");
        OrganizationAddress address = new OrganizationAddress();
        address.setCity("city");
        address.setCountry(Iso3166Country.TT);
        organization.setAddress(address);
        return organization;
    }

    protected static ExternalID externalId(String type, String value) {
        ExternalID externalId = new ExternalID();
        externalId.setType(type);
        externalId.setValue(value);
        externalId.setRelationship(Relationship.SELF);
        externalId.setUrl(new Url("http://" + type + "/" + value));
        return externalId;
    }

    protected static ExternalIDs externalIds(String type, String value) {
        ExternalIDs externalIds = new ExternalIDs();
        externalIds.getExternalIdentifier().add(externalId(type, value));
        return externalIds;
    }
}
