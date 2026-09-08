package org.orcid.api.memberV3.server.delegator;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import jakarta.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.api.common.util.ApiUtils;
import org.orcid.api.memberV3.server.delegator.impl.MemberV3ApiServiceDelegatorImpl;
import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.core.common.manager.SummaryManager;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.StatusManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly;
import org.orcid.core.manager.v3.AddressManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.ExternalIdentifierManager;
import org.orcid.core.manager.v3.GroupIdRecordManager;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.OtherNameManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.ProfileFundingManager;
import org.orcid.core.manager.v3.ProfileKeywordManager;
import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.core.manager.v3.ResearcherUrlManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.manager.v3.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.v3.read_only.AddressManagerReadOnly;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.BiographyManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.v3.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.OtherNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PersonalDetailsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearcherUrlManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.utils.Actors;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Shared wiring for the member V3 API delegator tests.
 *
 * <p>
 * {@code MemberV3ApiServiceDelegatorImpl} has forty-five {@code @Resource}
 * fields and no constructor, so {@code @InjectMocks} has to inject by field.
 * Every one of those fields is declared here, because a field that is not
 * declared as a {@code @Mock} is left null and only surfaces as an NPE in
 * whichever subclass first reaches it.
 *
 * <p>
 * The runner is {@link MockitoJUnitRunner.Silent} rather than the default
 * strict-stubs runner: the mock set shared by twenty-four subclasses is far
 * larger than any single subclass uses, and the strict runner fails a class for
 * stubs it did not need.
 *
 * <p>
 * Two collaborators are deliberately NOT plain mocks.
 * <ul>
 * <li>{@code sourceUtils} is a real {@link SourceUtils} wired to a mocked
 * {@link SourceNameCacheManager}. It is the only thing that resolves a client id
 * into the display name the tests assert on, and there is no SourceUtilsTest
 * anywhere; mocking it would silently delete every
 * {@code getSource().getSourceName().getContent()} assertion in this family.
 * <li>{@code apiUtils} is stubbed with an answer that builds the same
 * {@code 201 Created} + {@code Location} response production builds, so
 * {@code org.orcid.test.helper.v3.Utils#getPutCode(Response)} keeps working
 * without a servlet request in scope.
 * </ul>
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public abstract class MemberV3ApiServiceDelegatorMockTestBase {

    /**
     * The class under test, declared as the implementation because
     * {@code @InjectMocks} needs a concrete type.
     */
    @InjectMocks
    protected MemberV3ApiServiceDelegatorImpl serviceDelegator = new MemberV3ApiServiceDelegatorImpl();

    /** The record every new test in this family uses. */
    protected static final String ORCID = "0000-0000-0000-0003";

    /** The member API client the fixtures make the source of most elements. */
    protected static final String CLIENT_1 = "APP-5555555555555555";

    /** The display name {@link #CLIENT_1} resolves to. */
    protected static final String CLIENT_1_NAME = "Source Client 1";

    /** A different member API client: the source of items {@link #CLIENT_1} may not touch. */
    protected static final String CLIENT_2 = "APP-5555555555555556";

    protected static final String CLIENT_2_NAME = "Source Client 2";

    // ------------------------------------------------------------ write side

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

    @Mock
    protected OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    @Mock
    protected ResearchResourceManager researchResourceManager;

    // ------------------------------------------------------------- read side

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
    protected ResearchResourceManagerReadOnly researchResourceManagerReadOnly;

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

    /**
     * NOT the v3 package. The implementation explicitly imports
     * {@code org.orcid.core.manager.read_only.ClientDetailsManagerReadOnly}
     * even though a v3 type of the same name exists, and a mock of the wrong one
     * silently fails to inject.
     */
    @Mock
    protected ClientDetailsManagerReadOnly clientDetailsManagerReadOnly;

    @Mock
    protected ClientManagerReadOnly clientManagerReadOnly;

    // ----------------------------------------------------------------- other

    @Mock
    protected MessageSource messageSource;

    /** Also NOT the v3 package: {@code org.orcid.core.manager.StatusManager}. */
    @Mock
    protected StatusManager statusManager;

    @Mock
    protected SourceManager sourceManager;

    @Mock
    protected ApiUtils apiUtils;

    @Mock
    protected OrcidUrlManager orcidUrlManager;

    @Mock
    protected SummaryManager summaryManager;

    @Mock
    protected EmailDomainManager emailDomainManager;

    @Mock
    protected SourceEntityUtils sourceEntityUtils;

    /**
     * Not a collaborator of the delegator: it is the collaborator of the real
     * {@link SourceUtils} installed below.
     */
    @Mock
    protected SourceNameCacheManager sourceNameCacheManager;

    @Before
    public void beforeMemberV3Delegator() {
        SourceUtils realSourceUtils = new SourceUtils();
        realSourceUtils.setSourceNameCacheManager(sourceNameCacheManager);
        ReflectionTestUtils.setField(serviceDelegator, "sourceUtils", realSourceUtils);

        // One stub per client id, deliberately. A blanket any(String) stub would
        // make every source-name assertion in this family pass for any id at all.
        when(sourceNameCacheManager.retrieve(CLIENT_1)).thenReturn(CLIENT_1_NAME);
        when(sourceNameCacheManager.retrieve(CLIENT_2)).thenReturn(CLIENT_2_NAME);

        // The orcid argument is null for group-id records, hence nullable().
        when(apiUtils.buildApiResponse(nullable(String.class), anyString(), anyString(), anyString())).thenAnswer(invocation -> {
            String orcid = invocation.getArgument(0);
            String target = invocation.getArgument(1);
            String putCode = invocation.getArgument(2);
            return Response.created(URI.create("https://api.orcid.org/v3.0/" + (orcid != null ? orcid + "/" : "") + target + "/" + putCode)).build();
        });
    }

    @After
    public void afterMemberV3Delegator() {
        // The security context is static process state; clear it so nothing
        // leaks into the next test in the same JVM.
        Actors.clear();
    }

    /**
     * A last modified date of "now". Every element these tests build needs one,
     * because {@code Api3_0LastModifiedDatesHelper} runs for real and derives the
     * container's date from its elements'.
     */
    protected static LastModifiedDate lastModified() {
        try {
            return new LastModifiedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * An {@link ActivitiesSummary} whose eleven containers are all present but
     * empty.
     *
     * <p>
     * {@code Api3_0LastModifiedDatesHelper.calculateLastModified(GroupsContainer)}
     * dereferences its argument without a null check, so a summary built with a
     * missing container -- which the real manager never produces -- fails the
     * delegator with an NPE rather than with the assertion the test is making.
     */
    protected static ActivitiesSummary emptyActivitiesSummary() {
        ActivitiesSummary activities = new ActivitiesSummary();
        activities.setDistinctions(new Distinctions());
        activities.setEducations(new Educations());
        activities.setEmployments(new Employments());
        activities.setInvitedPositions(new InvitedPositions());
        activities.setMemberships(new Memberships());
        activities.setQualifications(new Qualifications());
        activities.setServices(new Services());
        activities.setFundings(new Fundings());
        activities.setPeerReviews(new PeerReviews());
        activities.setWorks(new Works());
        activities.setResearchResources(new ResearchResources());
        return activities;
    }

    /** A created date of "now". */
    protected static CreatedDate created() {
        try {
            return new CreatedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * A {@link Source} whose source path is a member API client, i.e. an element
     * some member created on a record.
     */
    protected static Source clientSource(String clientId) {
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(clientId));
        return source;
    }

    /**
     * A {@link Source} whose source path is a record, i.e. an element the record
     * holder created themselves.
     */
    protected static Source userSource(String orcid) {
        Source source = new Source();
        source.setSourceOrcid(new SourceOrcid(orcid));
        return source;
    }
}
