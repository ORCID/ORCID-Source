package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.apache.hc.core5.http.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.api.memberV2.server.delegator.impl.MemberV2ApiServiceVersionedDelegatorImpl;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverterChain;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.search_v2.Result;
import org.orcid.jaxb.model.search_v2.Search;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * The versioned member v2 delegator, on mocks.
 *
 * <p>
 * This class is a thin wrapper: for every method it calls
 * {@code checkProfileStatus}, which does nothing but
 * {@code orcidSecurityManager.checkProfile(orcid)}, then forwards to the inner
 * delegator and runs the result through a version converter chain. There is
 * therefore exactly one thing worth asserting per method, and it is asserted
 * once per method below: <em>this</em> method asks the guard, and when the guard
 * refuses, <em>this</em> method does not forward. The per-method coverage is the
 * point -- {@code viewGroupIdRecord}, {@code viewBulkWorks}, {@code viewClient}
 * and {@code searchByQuery} deliberately do not call the guard, and only a
 * per-method test can tell that apart from an oversight.
 *
 * <p>
 * Which of the five record states produces which exception is not asserted here
 * and cannot be: {@code checkProfileStatus} never looks at the exception, so all
 * five collapse to the same forwarding question at this boundary. The states are
 * proved in {@code org.orcid.core.manager.OrcidSecurityManagerTest}
 * ({@code checkProfile_InvalidOrcidTest}, {@code checkProfile_LockedTest},
 * {@code checkProfile_DeprecatedTest}, {@code checkProfile_DeactivatedTest},
 * {@code checkProfile_NotClaimed_NotOldEnough_NotSourceTest}). The names of the
 * five families below are kept so that the mapping from record state to expected
 * exception stays visible from here.
 *
 * <p>
 * {@code schemaValidator} has no mock on purpose. It is an inline-initialised
 * field with no annotation, and declaring a mock of its type would let
 * {@code @InjectMocks} replace the working validator and silently switch schema
 * validation off for every create and update below.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class MemberV2ApiServiceVersionedDelegatorTest {

    /** The 2.0 delegator: {@code processReponse} downgrades through v2VersionConverterChain. */
    @InjectMocks
    private MemberV2ApiServiceVersionedDelegatorImpl serviceDelegator = new MemberV2ApiServiceVersionedDelegatorImpl();

    /** The 2.1 delegator: {@code processReponse} upgrades through v2_1VersionConverterChain. */
    @InjectMocks
    private MemberV2ApiServiceVersionedDelegatorImpl serviceDelegator_v2_1 = new MemberV2ApiServiceVersionedDelegatorImpl();

    /**
     * The inner, unversioned delegator. Every method of the class under test
     * forwards to this one, so "did the guard stop the operation" is the same
     * question as "was this mock left untouched".
     */
    @Mock
    private MemberV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object, Object> memberV2ApiServiceDelegator;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    /*
     * The implementation declares two fields of this same type, so Mockito falls
     * back to matching on the field name. These two names must stay spelled
     * exactly as they are in MemberV2ApiServiceVersionedDelegatorImpl or one of
     * them is silently left null.
     */
    @Mock
    private V2VersionConverterChain v2VersionConverterChain;

    @Mock
    private V2VersionConverterChain v2_1VersionConverterChain;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Mock
    private OrcidSearchManager orcidSearchManager;

    @Mock
    private OrcidCoreExceptionMapper orcidCoreExceptionMapper;

    private String nonExistingUser = "0000-0000-0000-000X";
    private String unclaimedUserOrcid = "0000-0000-0000-0001";
    private String deprecatedUserOrcid = "0000-0000-0000-0004";
    private String lockedUserOrcid = "0000-0000-0000-0006";
    private String deactivatedUserOrcid = "0000-0000-0000-0007";

    @Before
    public void before() {
        // externalVersion is a bare private String wired only by the Spring XML
        // (<property name="externalVersion" value="2.0"/>), so @InjectMocks does
        // not populate it -- and processReponse dereferences it on the first call.
        ReflectionTestUtils.setField(serviceDelegator, "externalVersion", "2.0");
        ReflectionTestUtils.setField(serviceDelegator_v2_1, "externalVersion", "2.1");

        // The conversion itself belongs to V2VersionConverterChainTest; here the
        // chains hand back what they were given, so that a test can see which
        // chain a version reached for without also depending on what it did.
        when(v2VersionConverterChain.downgrade(any(V2Convertible.class), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(v2VersionConverterChain.upgrade(any(V2Convertible.class), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(v2_1VersionConverterChain.downgrade(any(V2Convertible.class), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(v2_1VersionConverterChain.upgrade(any(V2Convertible.class), anyString())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    /*
     * An ORCID iD that does not exist. checkProfile turns the cache miss into a
     * NoResultException; every method below has to ask before it forwards.
     */

    @Test(expected = NoResultException.class)
    public void test00ViewRecord() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewRecord(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewActivities() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewActivities(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewWork() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewWork(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewWorkSummary() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewWorkSummary(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateWork() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createWork(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateWork() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateWork(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeleteWork() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deleteWork(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewFunding() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewFunding(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewFundingSummary() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewFundingSummary(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateFunding() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createFunding(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateFunding() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateFunding(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeleteFunding() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deleteFunding(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEducation() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewEducation(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEducationSummary() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewEducationSummary(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateEducation() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createEducation(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateEducation() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateEducation(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEmployment() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewEmployment(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEmploymentSummary() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewEmploymentSummary(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateEmployment() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createEmployment(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateEmployment() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateEmployment(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeleteAffiliation() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deleteAffiliation(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewPeerReview() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewPeerReview(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewPeerReviewSummary() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewPeerReviewSummary(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00CreatePeerReview() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createPeerReview(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdatePeerReview() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updatePeerReview(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeletePeerReview() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deletePeerReview(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewResearcherUrls() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewResearcherUrls(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewResearcherUrl() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewResearcherUrl(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateResearcherUrl() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateResearcherUrl(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateResearcherUrl() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createResearcherUrl(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeleteResearcherUrl() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deleteResearcherUrl(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewEmails() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewEmails(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewOtherNames() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewOtherNames(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewOtherName() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewOtherName(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateOtherName() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createOtherName(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateOtherName() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateOtherName(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeleteOtherName() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deleteOtherName(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewPersonalDetails() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewPersonalDetails(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewExternalIdentifiers() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewExternalIdentifiers(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewExternalIdentifier() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewExternalIdentifier(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateExternalIdentifier() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateExternalIdentifier(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateExternalIdentifier() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createExternalIdentifier(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeleteExternalIdentifier() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deleteExternalIdentifier(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewBiography() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewBiography(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewKeywords() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewKeywords(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewKeyword() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewKeyword(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateKeyword() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createKeyword(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateKeyword() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateKeyword(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeleteKeyword() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deleteKeyword(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewAddresses() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewAddresses(nonExistingUser));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewAddress() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewAddress(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00CreateAddress() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.createAddress(nonExistingUser, null));
    }

    @Test(expected = NoResultException.class)
    public void test00UpdateAddress() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.updateAddress(nonExistingUser, 0L, null));
    }

    @Test(expected = NoResultException.class)
    public void test00DeleteAddress() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.deleteAddress(nonExistingUser, 0L));
    }

    @Test(expected = NoResultException.class)
    public void test00ViewPerson() {
        assertRefusedBeforeForwarding(nonExistingUser, notFound(), () -> serviceDelegator.viewPerson(nonExistingUser));
    }


    /*
     * A locked record.
     */

    @Test(expected = LockedException.class)
    public void test01ViewRecord() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewRecord(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewActivities() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewActivities(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewWork() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewWork(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewWorkSummary() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewWorkSummary(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01CreateWork() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createWork(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateWork() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateWork(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeleteWork() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deleteWork(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewFunding() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewFunding(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewFundingSummary() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewFundingSummary(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01CreateFunding() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createFunding(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateFunding() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateFunding(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeleteFunding() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deleteFunding(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewEducation() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewEducation(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewEducationSummary() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewEducationSummary(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01CreateEducation() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createEducation(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateEducation() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateEducation(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01ViewEmployment() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewEmployment(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewEmploymentSummary() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewEmploymentSummary(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01CreateEmployment() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createEmployment(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateEmployment() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateEmployment(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeleteAffiliation() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deleteAffiliation(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewPeerReview() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewPeerReview(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewPeerReviewSummary() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewPeerReviewSummary(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01CreatePeerReview() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createPeerReview(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01UpdatePeerReview() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updatePeerReview(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeletePeerReview() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deletePeerReview(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewResearcherUrls() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewResearcherUrls(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewResearcherUrl() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewResearcherUrl(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateResearcherUrl() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateResearcherUrl(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01CreateResearcherUrl() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createResearcherUrl(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeleteResearcherUrl() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deleteResearcherUrl(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewEmails() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewEmails(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewOtherNames() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewOtherNames(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewOtherName() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewOtherName(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01CreateOtherName() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createOtherName(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateOtherName() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateOtherName(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeleteOtherName() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deleteOtherName(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewPersonalDetails() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewPersonalDetails(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewExternalIdentifiers() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewExternalIdentifiers(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewExternalIdentifier() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewExternalIdentifier(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateExternalIdentifier() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateExternalIdentifier(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01CreateExternalIdentifier() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createExternalIdentifier(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeleteExternalIdentifier() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deleteExternalIdentifier(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewBiography() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewBiography(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewKeywords() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewKeywords(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewKeyword() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewKeyword(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01CreateKeyword() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createKeyword(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateKeyword() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateKeyword(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeleteKeyword() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deleteKeyword(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewAddresses() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewAddresses(lockedUserOrcid));
    }

    @Test(expected = LockedException.class)
    public void test01ViewAddress() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewAddress(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01CreateAddress() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.createAddress(lockedUserOrcid, null));
    }

    @Test(expected = LockedException.class)
    public void test01UpdateAddress() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.updateAddress(lockedUserOrcid, 0L, null));
    }

    @Test(expected = LockedException.class)
    public void test01DeleteAddress() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.deleteAddress(lockedUserOrcid, 0L));
    }

    @Test(expected = LockedException.class)
    public void test01ViewPerson() {
        assertRefusedBeforeForwarding(lockedUserOrcid, locked(), () -> serviceDelegator.viewPerson(lockedUserOrcid));
    }


    /*
     * A deprecated record.
     */

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewRecord() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewRecord(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test0102ViewActivities() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewActivities(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewWork() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewWork(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewWorkSummary() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewWorkSummary(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateWork() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createWork(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateWork() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateWork(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteWork() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deleteWork(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewFunding() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewFunding(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewFundingSummary() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewFundingSummary(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateFunding() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createFunding(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateFunding() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateFunding(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteFunding() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deleteFunding(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEducation() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewEducation(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEducationSummary() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewEducationSummary(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateEducation() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createEducation(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateEducation() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateEducation(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEmployment() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewEmployment(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEmploymentSummary() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewEmploymentSummary(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateEmployment() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createEmployment(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateEmployment() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateEmployment(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteAffiliation() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deleteAffiliation(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewPeerReview() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewPeerReview(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewPeerReviewSummary() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewPeerReviewSummary(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreatePeerReview() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createPeerReview(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdatePeerReview() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updatePeerReview(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeletePeerReview() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deletePeerReview(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewResearcherUrls() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewResearcherUrls(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewResearcherUrl() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewResearcherUrl(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateResearcherUrl() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateResearcherUrl(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateResearcherUrl() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createResearcherUrl(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteResearcherUrl() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deleteResearcherUrl(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewEmails() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewEmails(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewOtherNames() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewOtherNames(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewOtherName() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewOtherName(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateOtherName() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createOtherName(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateOtherName() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateOtherName(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteOtherName() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deleteOtherName(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewPersonalDetails() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewPersonalDetails(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewExternalIdentifiers() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewExternalIdentifiers(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewExternalIdentifier() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewExternalIdentifier(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateExternalIdentifier() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateExternalIdentifier(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateExternalIdentifier() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createExternalIdentifier(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteExternalIdentifier() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deleteExternalIdentifier(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewBiography() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewBiography(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewKeywords() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewKeywords(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewKeyword() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewKeyword(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateKeyword() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createKeyword(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateKeyword() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateKeyword(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteKeyword() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deleteKeyword(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewAddresses() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewAddresses(deprecatedUserOrcid));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewAddress() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewAddress(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02CreateAddress() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.createAddress(deprecatedUserOrcid, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02UpdateAddress() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.updateAddress(deprecatedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02DeleteAddress() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.deleteAddress(deprecatedUserOrcid, 0L));
    }

    @Test(expected = OrcidDeprecatedException.class)
    public void test02ViewPerson() {
        assertRefusedBeforeForwarding(deprecatedUserOrcid, deprecated(), () -> serviceDelegator.viewPerson(deprecatedUserOrcid));
    }


    /*
     * A record that has not been claimed and is not yet old enough. Whether it is
     * old enough is claim-wait-period arithmetic inside OrcidSecurityManagerImpl,
     * proved in OrcidSecurityManagerTest; here the refusal is simply given.
     */

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewRecord() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewRecord(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test0103ViewActivities() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewActivities(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewWork() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewWork(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewWorkSummary() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewWorkSummary(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateWork() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createWork(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateWork() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateWork(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteWork() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deleteWork(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewFunding() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewFunding(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewFundingSummary() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewFundingSummary(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateFunding() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createFunding(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateFunding() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateFunding(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteFunding() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deleteFunding(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEducation() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewEducation(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEducationSummary() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewEducationSummary(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateEducation() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createEducation(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateEducation() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateEducation(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmployment() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewEmployment(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmploymentSummary() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewEmploymentSummary(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateEmployment() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createEmployment(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateEmployment() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateEmployment(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteAffiliation() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deleteAffiliation(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPeerReview() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewPeerReview(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPeerReviewSummary() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewPeerReviewSummary(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreatePeerReview() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createPeerReview(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdatePeerReview() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updatePeerReview(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeletePeerReview() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deletePeerReview(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearcherUrls() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewResearcherUrls(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewResearcherUrl() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewResearcherUrl(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateResearcherUrl() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateResearcherUrl(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateResearcherUrl() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createResearcherUrl(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteResearcherUrl() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deleteResearcherUrl(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewEmails() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewEmails(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewOtherNames() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewOtherNames(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewOtherName() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewOtherName(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateOtherName() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createOtherName(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateOtherName() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateOtherName(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteOtherName() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deleteOtherName(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPersonalDetails() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewPersonalDetails(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewExternalIdentifiers() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewExternalIdentifiers(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewExternalIdentifier() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewExternalIdentifier(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateExternalIdentifier() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateExternalIdentifier(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateExternalIdentifier() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createExternalIdentifier(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteExternalIdentifier() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deleteExternalIdentifier(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewBiography() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewBiography(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewKeywords() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewKeywords(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewKeyword() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewKeyword(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateKeyword() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createKeyword(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateKeyword() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateKeyword(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteKeyword() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deleteKeyword(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewAddresses() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewAddresses(unclaimedUserOrcid));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewAddress() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewAddress(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03CreateAddress() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.createAddress(unclaimedUserOrcid, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03UpdateAddress() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.updateAddress(unclaimedUserOrcid, 0L, null));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03DeleteAddress() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.deleteAddress(unclaimedUserOrcid, 0L));
    }

    @Test(expected = OrcidNotClaimedException.class)
    public void test03ViewPerson() {
        assertRefusedBeforeForwarding(unclaimedUserOrcid, notClaimed(), () -> serviceDelegator.viewPerson(unclaimedUserOrcid));
    }


    /*
     * A deactivated record.
     */

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewActivities() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewActivities(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewRecord() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewRecord(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPerson() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewPerson(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewAddresses() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewAddresses(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEducations() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewEducations(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEmails() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewEmails(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewEmployments() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewEmployments(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewExternalIdentifiers() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewExternalIdentifiers(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewFundings() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewFundings(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewKeywords() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewKeywords(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewOtherNames() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewOtherNames(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPeerReviews() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewPeerReviews(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewPersonalDetails() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewPersonalDetails(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewResearcherUrls() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewResearcherUrls(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordViewWorks() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.viewWorks(deactivatedUserOrcid));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateWork() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createWork(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateWork() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateWork(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteWork() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deleteWork(deactivatedUserOrcid, 0L));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateFunding() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createFunding(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateFunding() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateFunding(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteFunding() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deleteFunding(deactivatedUserOrcid, 0L));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateEducation() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createEducation(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateEducation() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateEducation(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateEmployment() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createEmployment(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateEmployment() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateEmployment(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteAffiliation() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deleteAffiliation(deactivatedUserOrcid, 0L));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreatePeerReview() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createPeerReview(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdatePeerReview() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updatePeerReview(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeletePeerReview() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deletePeerReview(deactivatedUserOrcid, 0L));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateResearcherUrl() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateResearcherUrl(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateResearcherUrl() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createResearcherUrl(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteResearcherUrl() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deleteResearcherUrl(deactivatedUserOrcid, 0L));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateOtherName() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createOtherName(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateOtherName() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateOtherName(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteOtherName() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deleteOtherName(deactivatedUserOrcid, 0L));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateExternalIdentifier() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateExternalIdentifier(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateExternalIdentifier() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createExternalIdentifier(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteExternalIdentifier() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deleteExternalIdentifier(deactivatedUserOrcid, 0L));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateKeyword() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createKeyword(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateKeyword() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateKeyword(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteKeyword() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deleteKeyword(deactivatedUserOrcid, 0L));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordCreateAddress() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.createAddress(deactivatedUserOrcid, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordUpdateAddress() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.updateAddress(deactivatedUserOrcid, 0L, null));
    }

    @Test(expected = DeactivatedException.class)
    public void testDeactivatedRecordDeleteAddress() {
        assertRefusedBeforeForwarding(deactivatedUserOrcid, deactivated(), () -> serviceDelegator.deleteAddress(deactivatedUserOrcid, 0L));
    }

    /*
     * The rest: the methods that do something of their own.
     */

    @Test
    public void testSearchByQuery() throws ParseException {
        Search search = new Search();
        Result result = new Result();
        result.setOrcidIdentifier(new OrcidIdentifier("some-orcid-id"));
        search.getResults().add(result);
        Response searchResponse = Response.ok(search).build();
        when(memberV2ApiServiceDelegator.searchByQuery(ArgumentMatchers.<Map<String, List<String>>> any())).thenReturn(searchResponse);

        Response response = serviceDelegator.searchByQuery(new HashMap<String, List<String>>());

        // just testing MemberV2ApiServiceDelegatorImpl's response is returned
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Search);
        assertEquals(1, ((Search) response.getEntity()).getResults().size());
        assertEquals("some-orcid-id", ((Search) response.getEntity()).getResults().get(0).getOrcidIdentifier().getPath());
        // a search is not about one record, so there is no profile to check
        verify(orcidSecurityManager, never()).checkProfile(anyString());
    }

    @Test(expected = NoResultException.class)
    public void testViewClientNonExistent() {
        when(memberV2ApiServiceDelegator.viewClient("some-client-that-doesn't-exist")).thenThrow(new NoResultException());

        serviceDelegator.viewClient("some-client-that-doesn't-exist");
    }

    @Test
    public void testViewClient() throws ParseException {
        ClientSummary clientSummary = new ClientSummary();
        clientSummary.setName("Source Client 2");
        clientSummary.setDescription("A test source client");
        when(memberV2ApiServiceDelegator.viewClient("APP-6666666666666666")).thenReturn(Response.ok(clientSummary).build());

        Response response = serviceDelegator.viewClient("APP-6666666666666666");

        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);
        ClientSummary returned = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", returned.getName());
        assertEquals("A test source client", returned.getDescription());
        // viewClient is the one method that is not run through a converter chain
        verify(v2VersionConverterChain, never()).downgrade(any(V2Convertible.class), anyString());
        verify(v2_1VersionConverterChain, never()).upgrade(any(V2Convertible.class), anyString());
    }

    @Test
    public void testViewBulkWorks() {
        // The fourth entry is an OrcidError because the security manager replaced
        // a denied work with one, inside the inner delegator. That substitution
        // is OrcidSecurityManager_WorkBulkTest's; here the versioned delegator
        // must hand the mixed bulk on unchanged.
        WorkBulk workBulk = new WorkBulk();
        workBulk.getBulk().add(work(11L, "PUBLIC"));
        workBulk.getBulk().add(work(12L, "LIMITED"));
        workBulk.getBulk().add(work(13L, "PRIVATE"));
        workBulk.getBulk().add(orcidError(9018, "The work is private"));
        when(memberV2ApiServiceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13,16")).thenReturn(Response.ok(workBulk).build());

        Response response = serviceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13,16");

        WorkBulk returned = (WorkBulk) response.getEntity();
        assertNotNull(returned);
        assertNotNull(returned.getBulk());
        assertEquals(4, returned.getBulk().size());
        assertTrue(returned.getBulk().get(0) instanceof Work);
        assertTrue(returned.getBulk().get(1) instanceof Work);
        assertTrue(returned.getBulk().get(2) instanceof Work);
        assertTrue(returned.getBulk().get(3) instanceof OrcidError);
        // viewBulkWorks does not check the profile: the inner delegator does that
        // for itself, through profileEntityManager.orcidExists
        verify(orcidSecurityManager, never()).checkProfile(anyString());
    }

    @Test
    public void testViewBulkWorksWithBadPutCode() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.getBulk().add(work(11L, "PUBLIC"));
        workBulk.getBulk().add(work(12L, "LIMITED"));
        workBulk.getBulk().add(work(13L, "PRIVATE"));
        workBulk.getBulk().add(orcidError(9016, "No work found with put code 9999"));
        when(memberV2ApiServiceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13,9999")).thenReturn(Response.ok(workBulk).build());

        Response response = serviceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13,9999");

        WorkBulk returned = (WorkBulk) response.getEntity();
        assertNotNull(returned);
        assertNotNull(returned.getBulk());
        assertEquals(4, returned.getBulk().size());
        assertTrue(returned.getBulk().get(0) instanceof Work);
        assertTrue(returned.getBulk().get(1) instanceof Work);
        assertTrue(returned.getBulk().get(2) instanceof Work);
        assertTrue(returned.getBulk().get(3) instanceof OrcidError);
    }

    @Test(expected = ExceedMaxNumberOfPutCodesException.class)
    public void testViewBulkWorksWithTooManyPutCodes() {
        // The limit is enforced in WorkManagerReadOnlyImpl against its own
        // configured maximum and proved there; here it must not be swallowed.
        StringBuilder tooManyPutCodes = new StringBuilder("0");
        for (int i = 1; i <= 100; i++) {
            tooManyPutCodes.append(",").append(i);
        }
        when(memberV2ApiServiceDelegator.viewBulkWorks(eq("0000-0000-0000-0003"), anyString())).thenThrow(new ExceedMaxNumberOfPutCodesException(100));

        serviceDelegator.viewBulkWorks("0000-0000-0000-0003", tooManyPutCodes.toString());
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBulkWrongToken() {
        when(memberV2ApiServiceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13"))
                .thenThrow(new OrcidUnauthorizedException("Access token is for a different record"));

        serviceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13");
    }

    @Test
    public void test2_0() {
        // At 2.0 a response is downgraded, and it is v2VersionConverterChain that
        // does it. What the downgrade changes -- among other things the source
        // URIs, which the database version of this test asserted are http rather
        // than https -- is V2VersionConverterChainTest's subject; which chain is
        // reached for is this one's.
        Record record = new Record();
        when(memberV2ApiServiceDelegator.viewRecord("0000-0000-0000-0003")).thenReturn(Response.ok(record).build());

        Response response = serviceDelegator.viewRecord("0000-0000-0000-0003");

        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Record);
        verify(orcidSecurityManager).checkProfile("0000-0000-0000-0003");
        verify(v2VersionConverterChain).downgrade(any(V2Convertible.class), eq("2.0"));
        verify(v2_1VersionConverterChain, never()).upgrade(any(V2Convertible.class), anyString());
    }

    @Test
    public void test2_1() {
        // And at 2.1 a response is upgraded, through the other chain.
        Record record = new Record();
        when(memberV2ApiServiceDelegator.viewRecord("0000-0000-0000-0003")).thenReturn(Response.ok(record).build());

        Response response = serviceDelegator_v2_1.viewRecord("0000-0000-0000-0003");

        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Record);
        verify(orcidSecurityManager).checkProfile("0000-0000-0000-0003");
        verify(v2_1VersionConverterChain).upgrade(any(V2Convertible.class), eq("2.1"));
        verify(v2VersionConverterChain, never()).downgrade(any(V2Convertible.class), anyString());
    }

    // ------------------------------------------------------------- helpers

    /**
     * The single fact this class exists to prove, once per delegator method:
     * the guard is asked about the record before anything is forwarded, and when
     * it refuses, nothing is forwarded at all.
     */
    private void assertRefusedBeforeForwarding(String orcid, RuntimeException refusal, Runnable operation) {
        doThrow(refusal).when(orcidSecurityManager).checkProfile(orcid);
        try {
            operation.run();
        } finally {
            verifyNoInteractions(memberV2ApiServiceDelegator);
        }
    }

    private NoResultException notFound() {
        return new NoResultException("ORCID iD " + nonExistingUser + " not found");
    }

    private LockedException locked() {
        return new LockedException();
    }

    private OrcidDeprecatedException deprecated() {
        return new OrcidDeprecatedException();
    }

    private OrcidNotClaimedException notClaimed() {
        return new OrcidNotClaimedException();
    }

    private DeactivatedException deactivated() {
        return new DeactivatedException();
    }

    private Work work(Long putCode, String title) {
        Work work = new Work();
        work.setPutCode(putCode);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        return work;
    }

    private OrcidError orcidError(int code, String message) {
        OrcidError error = new OrcidError();
        error.setErrorCode(code);
        error.setResponseCode(400);
        error.setDeveloperMessage(message);
        error.setUserMessage(message);
        return error;
    }
}
