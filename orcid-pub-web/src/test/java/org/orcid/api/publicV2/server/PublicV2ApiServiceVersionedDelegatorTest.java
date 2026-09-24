package org.orcid.api.publicV2.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.orcid.api.publicV2.server.delegator.PublicV2ApiServiceDelegator;
import org.orcid.api.publicV2.server.delegator.impl.PublicV2ApiServiceVersionedDelegatorImpl;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNoBioException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverterChain;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.search_v2.Result;
import org.orcid.jaxb.model.search_v2.Search;

/**
 * The versioned delegator owns exactly two behaviours: it calls
 * {@code orcidSecurityManager.checkProfile} before forwarding a record scoped
 * request to the wrapped delegator, and it routes the wrapped response through
 * one of the two version converter chains depending on the external version it
 * was configured with. Everything else - what an endpoint reads, and what is
 * public - belongs to the wrapped delegator and to
 * {@code PublicAPISecurityManagerV2}, both of which are mocks here.
 *
 * The profile state that used to come from ProfileEntityData.xml (non existing,
 * locked, deprecated, unclaimed, deactivated) is now a {@code doThrow} on the
 * security manager. The rule that decides which of those five a real record
 * hits lives in {@code OrcidSecurityManagerImpl.checkProfile} and is proved by
 * org.orcid.core.manager.OrcidSecurityManagerTest, which is already mock based.
 * What is proved here, and was never proved by the database version, is that
 * the guard runs BEFORE the delegation: every guard test below asserts the
 * wrapped delegator was not touched at all.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class PublicV2ApiServiceVersionedDelegatorTest {

    /**
     * Declared as the implementation rather than the interface because
     * {@code @InjectMocks} needs a concrete type.
     */
    @InjectMocks
    private PublicV2ApiServiceVersionedDelegatorImpl serviceDelegator = new PublicV2ApiServiceVersionedDelegatorImpl();

    /**
     * The wrapped, non versioned delegator. The nine Object type arguments
     * match the field on the class under test exactly; without them
     * {@code @InjectMocks} has nothing to bind.
     */
    @Mock
    private PublicV2ApiServiceDelegator<Object, Object, Object, Object, Object, Object, Object, Object, Object> publicV2ApiServiceDelegator;

    /*
     * Two mocks of the same type. @InjectMocks disambiguates them by field
     * name, so these two names have to stay exactly as they are on
     * PublicV2ApiServiceVersionedDelegatorImpl.
     */
    @Mock
    private V2VersionConverterChain v2VersionConverterChain;

    @Mock
    private V2VersionConverterChain v2_1VersionConverterChain;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    private final String nonExistingUser = "0000-0000-0000-000X";
    private final String unclaimedUserOrcid = "0000-0000-0000-0001";
    private final String deprecatedUserOrcid = "0000-0000-0000-0004";
    private final String lockedUserOrcid = "0000-0000-0000-0006";
    private final String userWithNoBio = "1000-0000-0000-0001";
    private final String deactivatedUserOrcid = "0000-0000-0000-0007";

    @Before
    public void before() {
        // externalVersion is a plain String wired from XML in production, not a
        // bean, so @InjectMocks leaves it null and processReponse() would NPE.
        serviceDelegator.setExternalVersion("2.0");
    }

    /**
     * Asserts the profile guard both refuses the call and refuses it early.
     * Checking only that the exception escaped would pass equally well if the
     * delegation had already happened, so the wrapped delegator is checked to
     * be untouched.
     */
    private void assertGuardStopsCall(String orcid, RuntimeException refusal, Runnable call) {
        doThrow(refusal).when(orcidSecurityManager).checkProfile(orcid);
        try {
            call.run();
            fail("expected " + refusal.getClass().getSimpleName());
        } catch (RuntimeException actual) {
            assertSame(refusal, actual);
        }
        verify(orcidSecurityManager).checkProfile(orcid);
        verifyNoInteractions(publicV2ApiServiceDelegator);
    }

    /**
     * Makes the converter chain a pass through, so a test can assert on the
     * entity the wrapped delegator produced. A bare mock returns null here and
     * processReponse() would NPE on getObjectToConvert().
     */
    private void passThroughDowngrade() {
        when(v2VersionConverterChain.downgrade(any(V2Convertible.class), eq("2.0"))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    /**
     * Security checks
     */

    /**
     * 404 for invalid orcids
     */
    @Test
    public void test00ViewRecord() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewRecord(nonExistingUser));
    }

    @Test
    public void test00ViewActivities() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewActivities(nonExistingUser));
    }

    @Test
    public void test00ViewWork() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewWork(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewWorkSummary() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewWorkSummary(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewFunding() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewFunding(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewFundingSummary() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewFundingSummary(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewEducation() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewEducation(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewEducationSummary() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewEducationSummary(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewEmployment() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewEmployment(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewEmploymentSummary() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewEmploymentSummary(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewPeerReview() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewPeerReview(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewPeerReviewSummary() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewPeerReviewSummary(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewResearcherUrls() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewResearcherUrls(nonExistingUser));
    }

    @Test
    public void test00ViewResearcherUrl() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewResearcherUrl(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewEmails() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewEmails(nonExistingUser));
    }

    @Test
    public void test00ViewOtherNames() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewOtherNames(nonExistingUser));
    }

    @Test
    public void test00ViewOtherName() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewOtherName(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewPersonalDetails() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewPersonalDetails(nonExistingUser));
    }

    @Test
    public void test00ViewExternalIdentifiers() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewExternalIdentifiers(nonExistingUser));
    }

    @Test
    public void test00ViewExternalIdentifier() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewExternalIdentifier(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewBiography() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewBiography(nonExistingUser));
    }

    @Test
    public void test00ViewKeywords() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewKeywords(nonExistingUser));
    }

    @Test
    public void test00ViewKeyword() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewKeyword(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewAddresses() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewAddresses(nonExistingUser));
    }

    @Test
    public void test00ViewAddress() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewAddress(nonExistingUser, 0L));
    }

    @Test
    public void test00ViewPerson() {
        assertGuardStopsCall(nonExistingUser, new NoResultException(), () -> serviceDelegator.viewPerson(nonExistingUser));
    }

    /**
     * Locked accounts
     */
    @Test
    public void test01ViewRecord() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewRecord(lockedUserOrcid));
    }

    @Test
    public void test01ViewActivities() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewActivities(lockedUserOrcid));
    }

    @Test
    public void test01ViewWork() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewWork(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewWorkSummary() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewWorkSummary(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewFunding() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewFunding(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewFundingSummary() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewFundingSummary(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewEducation() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewEducation(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewEducationSummary() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewEducationSummary(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewEmployment() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewEmployment(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewEmploymentSummary() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewEmploymentSummary(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewPeerReview() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewPeerReview(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewPeerReviewSummary() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewPeerReviewSummary(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewResearcherUrls() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewResearcherUrls(lockedUserOrcid));
    }

    @Test
    public void test01ViewResearcherUrl() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewResearcherUrl(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewEmails() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewEmails(lockedUserOrcid));
    }

    @Test
    public void test01ViewOtherNames() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewOtherNames(lockedUserOrcid));
    }

    @Test
    public void test01ViewOtherName() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewOtherName(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewPersonalDetails() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewPersonalDetails(lockedUserOrcid));
    }

    @Test
    public void test01ViewExternalIdentifiers() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewExternalIdentifiers(lockedUserOrcid));
    }

    @Test
    public void test01ViewExternalIdentifier() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewExternalIdentifier(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewBiography() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewBiography(lockedUserOrcid));
    }

    @Test
    public void test01ViewKeywords() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewKeywords(lockedUserOrcid));
    }

    @Test
    public void test01ViewKeyword() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewKeyword(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewAddresses() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewAddresses(lockedUserOrcid));
    }

    @Test
    public void test01ViewAddress() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewAddress(lockedUserOrcid, 0L));
    }

    @Test
    public void test01ViewPerson() {
        assertGuardStopsCall(lockedUserOrcid, new LockedException(), () -> serviceDelegator.viewPerson(lockedUserOrcid));
    }

    /**
     * Deprecated accounts
     */
    @Test
    public void test02ViewRecord() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewRecord(deprecatedUserOrcid));
    }

    @Test
    public void test0102ViewActivities() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewActivities(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewWork() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewWork(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewWorkSummary() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewWorkSummary(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewFunding() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewFunding(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewFundingSummary() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewFundingSummary(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewEducation() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewEducation(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewEducationSummary() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewEducationSummary(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewEmployment() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewEmployment(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewEmploymentSummary() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewEmploymentSummary(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewPeerReview() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewPeerReview(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewPeerReviewSummary() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewPeerReviewSummary(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewResearcherUrls() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewResearcherUrls(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewResearcherUrl() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewResearcherUrl(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewEmails() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewEmails(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewOtherNames() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewOtherNames(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewOtherName() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewOtherName(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewPersonalDetails() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewPersonalDetails(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewExternalIdentifiers() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewExternalIdentifiers(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewExternalIdentifier() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewExternalIdentifier(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewBiography() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewBiography(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewKeywords() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewKeywords(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewKeyword() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewKeyword(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewAddresses() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewAddresses(deprecatedUserOrcid));
    }

    @Test
    public void test02ViewAddress() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewAddress(deprecatedUserOrcid, 0L));
    }

    @Test
    public void test02ViewPerson() {
        assertGuardStopsCall(deprecatedUserOrcid, new OrcidDeprecatedException(), () -> serviceDelegator.viewPerson(deprecatedUserOrcid));
    }

    /**
     * Unclaimed account throws an exception before the claim period ends
     */
    @Test
    public void test03ViewRecord() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewRecord(unclaimedUserOrcid));
    }

    @Test
    public void test0103ViewActivities() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewActivities(unclaimedUserOrcid));
    }

    @Test
    public void test03ViewWork() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewWork(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewWorkSummary() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewWorkSummary(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewFunding() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewFunding(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewFundingSummary() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewFundingSummary(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewEducation() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewEducation(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewEducationSummary() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewEducationSummary(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewEmployment() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewEmployment(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewEmploymentSummary() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewEmploymentSummary(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewPeerReview() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewPeerReview(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewPeerReviewSummary() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewPeerReviewSummary(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewResearcherUrls() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewResearcherUrls(unclaimedUserOrcid));
    }

    @Test
    public void test03ViewResearcherUrl() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewResearcherUrl(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewEmails() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewEmails(unclaimedUserOrcid));
    }

    @Test
    public void test03ViewOtherNames() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewOtherNames(unclaimedUserOrcid));
    }

    @Test
    public void test03ViewOtherName() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewOtherName(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewPersonalDetails() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewPersonalDetails(unclaimedUserOrcid));
    }

    @Test
    public void test03ViewExternalIdentifiers() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewExternalIdentifiers(unclaimedUserOrcid));
    }

    @Test
    public void test03ViewExternalIdentifier() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewExternalIdentifier(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewBiography() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewBiography(unclaimedUserOrcid));
    }

    /**
     * A record with no biography row makes the wrapped delegator's
     * PublicAPISecurityManagerV2.checkIsPublic(Biography) throw. Here that is a
     * stub; the rule itself is proved by
     * PublicAPISecurityManagerV2Test.checkIsPublicBiography_NullTest.
     */
    @Test(expected = OrcidNoBioException.class)
    public void testViewBiographyWhereBiographyIsNull() {
        when(publicV2ApiServiceDelegator.viewBiography(userWithNoBio)).thenThrow(new OrcidNoBioException());
        serviceDelegator.viewBiography(userWithNoBio);
        fail();
    }

    @Test
    public void test03ViewKeywords() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewKeywords(unclaimedUserOrcid));
    }

    @Test
    public void test03ViewKeyword() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewKeyword(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewAddresses() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewAddresses(unclaimedUserOrcid));
    }

    @Test
    public void test03ViewAddress() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewAddress(unclaimedUserOrcid, 0L));
    }

    @Test
    public void test03ViewPerson() {
        assertGuardStopsCall(unclaimedUserOrcid, new OrcidNotClaimedException(), () -> serviceDelegator.viewPerson(unclaimedUserOrcid));
    }

    @Test
    public void testSearchByQuery() throws ParseException {
        Search search = new Search();
        Result result = new Result();
        result.setOrcidIdentifier(new OrcidIdentifier("some-orcid-id"));
        search.getResults().add(result);
        when(publicV2ApiServiceDelegator.searchByQuery(ArgumentMatchers.<Map<String, List<String>>> any())).thenReturn(Response.ok(search).build());
        passThroughDowngrade();

        Map<String, List<String>> params = new HashMap<String, List<String>>();
        Response response = serviceDelegator.searchByQuery(params);

        // just testing the wrapped delegator's response is returned
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Search);
        assertEquals(1, ((Search) response.getEntity()).getResults().size());
        assertEquals("some-orcid-id", ((Search) response.getEntity()).getResults().get(0).getOrcidIdentifier().getPath());
        verify(publicV2ApiServiceDelegator).searchByQuery(params);
        // search is not record scoped, so there is no profile to check
        verify(orcidSecurityManager, never()).checkProfile(ArgumentMatchers.anyString());
    }

    /**
     * An unknown client id produces no row. That is the client manager's
     * behaviour, not the delegator's; all this asserts is that the versioned
     * delegator lets it through rather than swallowing it.
     */
    @Test(expected = NoResultException.class)
    public void testViewClientNonExistent() {
        when(publicV2ApiServiceDelegator.viewClient("some-client-that-doesn't-exist")).thenThrow(new NoResultException());
        serviceDelegator.viewClient("some-client-that-doesn't-exist");
        fail();
    }

    @Test
    public void testViewClient() throws ParseException {
        ClientSummary summary = new ClientSummary();
        summary.setName("Source Client 2");
        summary.setDescription("A test source client");
        when(publicV2ApiServiceDelegator.viewClient("APP-6666666666666666")).thenReturn(Response.ok(summary).build());

        Response response = serviceDelegator.viewClient("APP-6666666666666666");

        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);
        ClientSummary client = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", client.getName());
        assertEquals("A test source client", client.getDescription());
        // viewClient is a bare forward: no profile guard and no version chain
        verify(publicV2ApiServiceDelegator).viewClient("APP-6666666666666666");
        verifyNoInteractions(v2VersionConverterChain, v2_1VersionConverterChain);
    }

    /**
     * Which of the requested works come back as works and which as OrcidErrors
     * is decided by PublicAPISecurityManagerV2.filter(WorkBulk), below the
     * wrapped delegator; asserting it here against a mock would only restate
     * the stub. That rule is proved by
     * PublicAPISecurityManagerV2Test.filterWorkBulkTest. What is left for this
     * layer is that the request is forwarded verbatim and the bulk comes back
     * through the version chain unchanged.
     */
    @Test
    public void testViewBulkWorks() {
        WorkBulk stubbed = new WorkBulk();
        stubbed.setBulk(new ArrayList<BulkElement>(Arrays.asList(new Work(), new OrcidError(), new OrcidError(), new OrcidError())));
        when(publicV2ApiServiceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13,16")).thenReturn(Response.ok(stubbed).build());
        passThroughDowngrade();

        Response response = serviceDelegator.viewBulkWorks("0000-0000-0000-0003", "11,12,13,16");

        WorkBulk workBulk = (WorkBulk) response.getEntity();
        assertNotNull(workBulk);
        assertSame(stubbed, workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(4, workBulk.getBulk().size());
        verify(publicV2ApiServiceDelegator).viewBulkWorks("0000-0000-0000-0003", "11,12,13,16");
    }

    /**
     * The no-such-profile check is the wrapped delegator's own code
     * (profileEntityManagerReadOnly.findByOrcid returning null); it is proved
     * in PublicV2ApiServiceDelegatorTest.testViewBulkWorksNonExistentUser. Here
     * it only has to propagate.
     */
    @Test(expected = OrcidNoResultException.class)
    public void testViewBulkWorksNonExistentUser() {
        when(publicV2ApiServiceDelegator.viewBulkWorks(nonExistingUser, "11,12,13,16")).thenThrow(new OrcidNoResultException());
        serviceDelegator.viewBulkWorks(nonExistingUser, "11,12,13,16");
        fail();
    }

    /**
     * Deactivated elements tests
     */
    @Test
    public void testDeactivatedRecordViewActivities() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewActivities(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewRecord() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewRecord(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewPerson() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewPerson(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewAddresses() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewAddresses(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewEducations() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewEducations(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewEmails() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewEmails(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewEmployments() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewEmployments(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewExternalIdentifiers() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewExternalIdentifiers(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewFundings() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewFundings(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewKeywords() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewKeywords(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewOtherNames() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewOtherNames(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewPeerReviews() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewPeerReviews(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewPersonalDetails() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewPersonalDetails(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewResearcherUrls() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewResearcherUrls(deactivatedUserOrcid));
    }

    @Test
    public void testDeactivatedRecordViewWorks() {
        assertGuardStopsCall(deactivatedUserOrcid, new DeactivatedException(), () -> serviceDelegator.viewWorks(deactivatedUserOrcid));
    }

    /**
     * The version routing is the versioned delegator's only real logic and had
     * no test at all. At 2.1 the response has to go up the 2.1 chain; at
     * anything else it has to go down the 2.0 chain. Getting this backwards
     * would hand a 2.0 client a 2.1 document.
     */
    @Test
    public void testResponseIsUpgradedWhenExternalVersionIs2_1() {
        serviceDelegator.setExternalVersion("2.1");
        Search entity = new Search();
        Search upgraded = new Search();
        when(publicV2ApiServiceDelegator.viewRecord("0000-0000-0000-0003")).thenReturn(Response.ok(entity).build());
        when(v2_1VersionConverterChain.upgrade(any(V2Convertible.class), eq("2.1"))).thenReturn(new V2Convertible(upgraded, "2.1"));

        Response response = serviceDelegator.viewRecord("0000-0000-0000-0003");

        assertSame(upgraded, response.getEntity());
        verify(v2_1VersionConverterChain).upgrade(any(V2Convertible.class), eq("2.1"));
        verifyNoInteractions(v2VersionConverterChain);
    }

    @Test
    public void testResponseIsDowngradedWhenExternalVersionIsNot2_1() {
        Search entity = new Search();
        Search downgraded = new Search();
        when(publicV2ApiServiceDelegator.viewRecord("0000-0000-0000-0003")).thenReturn(Response.ok(entity).build());
        when(v2VersionConverterChain.downgrade(any(V2Convertible.class), eq("2.0"))).thenReturn(new V2Convertible(downgraded, "2.0"));

        Response response = serviceDelegator.viewRecord("0000-0000-0000-0003");

        assertSame(downgraded, response.getEntity());
        verify(v2VersionConverterChain).downgrade(any(V2Convertible.class), eq("2.0"));
        verifyNoInteractions(v2_1VersionConverterChain);
    }

    /**
     * A null entity has nothing to convert, so the response has to come back
     * untouched rather than through the chain.
     */
    @Test
    public void testResponseWithNoEntityIsNotConverted() {
        Response empty = Response.noContent().build();
        when(publicV2ApiServiceDelegator.viewRecord("0000-0000-0000-0003")).thenReturn(empty);

        Response response = serviceDelegator.viewRecord("0000-0000-0000-0003");

        assertSame(empty, response);
        verifyNoInteractions(v2VersionConverterChain, v2_1VersionConverterChain);
    }
}
