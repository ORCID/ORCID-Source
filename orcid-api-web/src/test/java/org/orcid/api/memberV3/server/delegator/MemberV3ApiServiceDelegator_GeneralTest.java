package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.AccessControlException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.v3.OrcidSearchManager;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.common.VisibilityType;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecords;
import org.orcid.jaxb.model.v3.release.record.Address;
import org.orcid.jaxb.model.v3.release.record.Addresses;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Biography;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.search.Result;
import org.orcid.jaxb.model.v3.release.search.Search;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedResult;
import org.orcid.jaxb.model.v3.release.search.expanded.ExpandedSearch;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked cross-cutting tests for the member V3 API delegator.
 *
 * <p>
 * The three "client credentials token on a claimed account" tests keep their
 * shape but move the refusal to the collaborator that actually makes it: a
 * client-only token is refused by {@code OrcidSecurityManager}, and what the
 * delegator owes is to let that refusal out of every endpoint rather than
 * swallowing it or turning it into a 200. Which security manager method refuses
 * is the security manager's business and is proved in orcid-core.
 *
 * <p>
 * {@code validateSearchParams} is by contrast genuine delegator logic -- it
 * compares the requested row count against {@code OrcidSearchManager} and
 * defaults it when absent -- so the search tests are real tests of this class
 * and are kept as they were.
 */
public class MemberV3ApiServiceDelegator_GeneralTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final String UNCLAIMED = "0000-0000-0000-0001";

    private static final String CLIENT_ONLY_REFUSAL = "Non client credential scope found in client request";

    @Before
    public void emptyContainersSoReadsReachTheSecurityManager() {
        // The read managers are asked before the security manager filters, so a
        // read endpoint needs a container to hand over; these are the empty
        // shapes the real managers return for a record with nothing on it.
        when(addressManagerReadOnly.getAddresses(anyString())).thenReturn(emptyAddresses());
        when(profileKeywordManagerReadOnly.getKeywords(anyString())).thenReturn(emptyKeywords());
        when(otherNameManagerReadOnly.getOtherNames(anyString())).thenReturn(emptyOtherNames());
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(anyString())).thenReturn(emptyExternalIdentifiers());
        when(researcherUrlManagerReadOnly.getResearcherUrls(anyString())).thenReturn(emptyResearcherUrls());
        when(biographyManagerReadOnly.getBiography(anyString())).thenReturn(new Biography());
        when(affiliationsManagerReadOnly.getEducationSummaryList(anyString())).thenReturn(new ArrayList<>());
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(anyString())).thenReturn(new ArrayList<>());
        when(profileFundingManagerReadOnly.getFundingSummaryList(anyString())).thenReturn(new ArrayList<>());
        when(peerReviewManagerReadOnly.getPeerReviewSummaryList(anyString())).thenReturn(new ArrayList<>());
        when(workManagerReadOnly.getWorksSummaryList(anyString())).thenReturn(new ArrayList<>());
    }

    private Addresses emptyAddresses() {
        Addresses addresses = new Addresses();
        addresses.setAddress(new ArrayList<>());
        return addresses;
    }

    private Keywords emptyKeywords() {
        Keywords keywords = new Keywords();
        keywords.setKeywords(new ArrayList<>());
        return keywords;
    }

    private OtherNames emptyOtherNames() {
        OtherNames otherNames = new OtherNames();
        otherNames.setOtherNames(new ArrayList<>());
        return otherNames;
    }

    private PersonExternalIdentifiers emptyExternalIdentifiers() {
        PersonExternalIdentifiers extIds = new PersonExternalIdentifiers();
        extIds.setExternalIdentifiers(new ArrayList<>());
        return extIds;
    }

    private ResearcherUrls emptyResearcherUrls() {
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(new ArrayList<>());
        return researcherUrls;
    }

    /** The security manager refuses every read for this actor. */
    private void refuseEveryRead() {
        doThrow(new IllegalStateException(CLIENT_ONLY_REFUSAL)).when(orcidSecurityManager).checkAndFilter(anyString(), ArgumentMatchers.<VisibilityType> any(),
                any(ScopePathType.class));
        doThrow(new IllegalStateException(CLIENT_ONLY_REFUSAL)).when(orcidSecurityManager).checkAndFilter(anyString(), anyList(), any(ScopePathType.class));
        doThrow(new IllegalStateException(CLIENT_ONLY_REFUSAL)).when(orcidSecurityManager).checkAndFilter(anyString(),
                ArgumentMatchers.<org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary> any());
        doThrow(new IllegalStateException(CLIENT_ONLY_REFUSAL)).when(orcidSecurityManager).checkAndFilter(anyString(),
                ArgumentMatchers.<org.orcid.jaxb.model.v3.release.record.PersonalDetails> any());
        doThrow(new IllegalStateException(CLIENT_ONLY_REFUSAL)).when(orcidSecurityManager).checkAndFilter(anyString(),
                ArgumentMatchers.<org.orcid.jaxb.model.v3.release.record.Person> any());
        doThrow(new IllegalStateException(CLIENT_ONLY_REFUSAL)).when(orcidSecurityManager).checkAndFilter(anyString(),
                ArgumentMatchers.<org.orcid.jaxb.model.v3.release.record.Record> any());
    }

    /** The security manager refuses every write for this actor. */
    private void refuseEveryWrite() {
        // any() -- not any(ScopePathType[].class). The latter builds an
        // instance-of matcher that is compared against each expanded vararg, so
        // it never matches checkClientAccessAndScopes(orcid, SOME_SCOPE); the
        // guard then did not throw and the test failed inside the manager with
        // a NullPointerException instead. Bare any() is Mockito's VarargMatcher
        // and matches any number of varargs, which here is one or two.
        doThrow(new IllegalStateException(CLIENT_ONLY_REFUSAL)).when(orcidSecurityManager).checkClientAccessAndScopes(anyString(),
                any());
    }

    private void assertRefused(Runnable call) {
        try {
            call.run();
            fail();
        } catch (IllegalStateException e) {
            assertEquals(CLIENT_ONLY_REFUSAL, e.getMessage());
        }
    }

    @Test
    public void testOrcidProfileCreate_CANT_AddOnClaimedAccounts() {
        refuseEveryWrite();

        assertRefused(() -> serviceDelegator.createAddress(ORCID, Utils.getAddress()));
        assertRefused(() -> serviceDelegator.createEducation(ORCID, (Education) Utils.getAffiliation(AffiliationType.EDUCATION)));
        assertRefused(() -> serviceDelegator.createEmployment(ORCID, (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT)));
        assertRefused(() -> serviceDelegator.createExternalIdentifier(ORCID, Utils.getPersonExternalIdentifier()));
        assertRefused(() -> serviceDelegator.createFunding(ORCID, Utils.getFunding()));
        assertRefused(() -> serviceDelegator.createKeyword(ORCID, Utils.getKeyword()));
        assertRefused(() -> serviceDelegator.createOtherName(ORCID, Utils.getOtherName()));
        assertRefused(() -> serviceDelegator.createPeerReview(ORCID, Utils.getPeerReview()));
        assertRefused(() -> serviceDelegator.createResearcherUrl(ORCID, Utils.getResearcherUrl()));
        assertRefused(() -> serviceDelegator.createWork(ORCID, Utils.getWork("work # 1 " + System.currentTimeMillis())));
    }

    @Test
    public void testOrcidProfileCreate_CANT_ViewOnClaimedAccounts() {
        refuseEveryRead();
        // viewEmails is refused at the scope check rather than the filter.
        refuseEveryWrite();

        assertRefused(() -> serviceDelegator.viewActivities(ORCID));
        assertRefused(() -> serviceDelegator.viewAddress(ORCID, 9L));
        assertRefused(() -> serviceDelegator.viewAddresses(ORCID));
        assertRefused(() -> serviceDelegator.viewBiography(ORCID));
        assertRefused(() -> serviceDelegator.viewEducation(ORCID, 20L));
        assertRefused(() -> serviceDelegator.viewEducationSummary(ORCID, 20L));
        assertRefused(() -> serviceDelegator.viewEducations(ORCID));
        assertRefused(() -> serviceDelegator.viewEmails(ORCID));
        assertRefused(() -> serviceDelegator.viewEmployment(ORCID, 17L));
        assertRefused(() -> serviceDelegator.viewEmploymentSummary(ORCID, 17L));
        assertRefused(() -> serviceDelegator.viewEmployments(ORCID));
        assertRefused(() -> serviceDelegator.viewExternalIdentifier(ORCID, 13L));
        assertRefused(() -> serviceDelegator.viewExternalIdentifiers(ORCID));
        assertRefused(() -> serviceDelegator.viewFunding(ORCID, 10L));
        assertRefused(() -> serviceDelegator.viewFundingSummary(ORCID, 10L));
        assertRefused(() -> serviceDelegator.viewFundings(ORCID));
        assertRefused(() -> serviceDelegator.viewKeyword(ORCID, 9L));
        assertRefused(() -> serviceDelegator.viewKeywords(ORCID));
        assertRefused(() -> serviceDelegator.viewOtherName(ORCID, 13L));
        assertRefused(() -> serviceDelegator.viewOtherNames(ORCID));
        assertRefused(() -> serviceDelegator.viewPeerReview(ORCID, 9L));
        assertRefused(() -> serviceDelegator.viewPeerReviewSummary(ORCID, 9L));
        assertRefused(() -> serviceDelegator.viewPeerReviews(ORCID));
        assertRefused(() -> serviceDelegator.viewPerson(ORCID));
        assertRefused(() -> serviceDelegator.viewPersonalDetails(ORCID));
        assertRefused(() -> serviceDelegator.viewResearcherUrl(ORCID, 13L));
        assertRefused(() -> serviceDelegator.viewResearcherUrls(ORCID));
        assertRefused(() -> serviceDelegator.viewWork(ORCID, 11L));
        assertRefused(() -> serviceDelegator.viewWorkSummary(ORCID, 11L));
        assertRefused(() -> serviceDelegator.viewWorks(ORCID));
        assertRefused(() -> serviceDelegator.viewRecord(ORCID));
    }

    @Test
    public void testOrcidProfileCreateCanViewAndCreateGroupIds() {
        GroupIdRecord record = new GroupIdRecord();
        record.setPutCode(1L);
        record.setGroupId("issn:0000-0001");
        record.setName("TestGroup1");
        record.setLastModifiedDate(lastModified());
        when(groupIdRecordManagerReadOnly.getGroupIdRecord(1L)).thenReturn(record);
        GroupIdRecords records = new GroupIdRecords();
        records.getGroupIdRecord().add(record);
        when(groupIdRecordManagerReadOnly.getGroupIdRecords("10", "1")).thenReturn(records);
        when(groupIdRecordManager.exists("publons:errrmmmmm")).thenReturn(false);
        when(groupIdRecordManager.createGroupIdRecord(any(GroupIdRecord.class))).thenReturn(record);

        // A group id token is a client credentials token, and these three
        // endpoints are the ones it is allowed to use.
        try {
            serviceDelegator.viewGroupIdRecord(1L);
        } catch (Exception e) {
            fail();
        }
        try {
            serviceDelegator.viewGroupIdRecords("10", "1");
        } catch (Exception e) {
            fail();
        }
        GroupIdRecord groupIdRecord = Utils.getNonIssnGroupIdRecord();
        try {
            serviceDelegator.createGroupIdRecord(groupIdRecord);
        } catch (Exception e) {
            fail();
        }
        verify(groupIdRecordManager).createGroupIdRecord(any(GroupIdRecord.class));
    }

    @Test
    public void testOrcidProfileCreate_CANT_DeleteOnClaimedAccounts() {
        refuseEveryWrite();

        assertRefused(() -> serviceDelegator.deleteAddress(ORCID, 9L));
        assertRefused(() -> serviceDelegator.deleteAffiliation(ORCID, 20L));
        assertRefused(() -> serviceDelegator.deleteExternalIdentifier(ORCID, 13L));
        assertRefused(() -> serviceDelegator.deleteFunding(ORCID, 10L));
        assertRefused(() -> serviceDelegator.deleteKeyword(ORCID, 9L));
        assertRefused(() -> serviceDelegator.deleteOtherName(ORCID, 13L));
        assertRefused(() -> serviceDelegator.deletePeerReview(ORCID, 9L));
        assertRefused(() -> serviceDelegator.deleteResearcherUrl(ORCID, 13L));
        assertRefused(() -> serviceDelegator.deleteWork(ORCID, 11L));
    }

    @Test
    public void testOrcidProfileCreate_CANT_UpdateOnClaimedAccounts() {
        // Reads succeed (the user's own token could make them); the update is
        // what a client credentials token is refused.
        when(addressManagerReadOnly.getAddress(ORCID, 9L)).thenReturn(address(9L));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(education(20L));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(employment(17L));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(ORCID, 13L)).thenReturn(externalIdentifier(13L));
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(funding(10L));
        when(profileKeywordManagerReadOnly.getKeyword(ORCID, 9L)).thenReturn(keyword(9L));
        when(otherNameManagerReadOnly.getOtherName(ORCID, 13L)).thenReturn(otherName(13L));
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 9L)).thenReturn(peerReview(9L));
        when(researcherUrlManagerReadOnly.getResearcherUrl(ORCID, 13L)).thenReturn(researcherUrl(13L));
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(work(11L));
        refuseEveryWrite();

        Response response = serviceDelegator.viewAddress(ORCID, 9L);
        assertNotNull(response);
        Address a = (Address) response.getEntity();
        assertNotNull(a);
        assertRefused(() -> serviceDelegator.updateAddress(ORCID, a.getPutCode(), a));

        response = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(response);
        Education edu = (Education) response.getEntity();
        assertNotNull(edu);
        assertRefused(() -> serviceDelegator.updateEducation(ORCID, edu.getPutCode(), edu));

        response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment emp = (Employment) response.getEntity();
        assertNotNull(emp);
        assertRefused(() -> serviceDelegator.updateEmployment(ORCID, emp.getPutCode(), emp));

        response = serviceDelegator.viewExternalIdentifier(ORCID, 13L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertRefused(() -> serviceDelegator.updateExternalIdentifier(ORCID, extId.getPutCode(), extId));

        response = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(response);
        Funding f = (Funding) response.getEntity();
        assertNotNull(f);
        assertRefused(() -> serviceDelegator.updateFunding(ORCID, f.getPutCode(), f));

        response = serviceDelegator.viewKeyword(ORCID, 9L);
        assertNotNull(response);
        Keyword k = (Keyword) response.getEntity();
        assertNotNull(k);
        assertRefused(() -> serviceDelegator.updateKeyword(ORCID, k.getPutCode(), k));

        response = serviceDelegator.viewOtherName(ORCID, 13L);
        assertNotNull(response);
        OtherName o = (OtherName) response.getEntity();
        assertNotNull(o);
        assertRefused(() -> serviceDelegator.updateOtherName(ORCID, o.getPutCode(), o));

        response = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(response);
        PeerReview p = (PeerReview) response.getEntity();
        assertNotNull(p);
        assertRefused(() -> serviceDelegator.updatePeerReview(ORCID, p.getPutCode(), p));

        response = serviceDelegator.viewResearcherUrl(ORCID, 13L);
        assertNotNull(response);
        ResearcherUrl r = (ResearcherUrl) response.getEntity();
        assertNotNull(r);
        assertRefused(() -> serviceDelegator.updateResearcherUrl(ORCID, r.getPutCode(), r));

        response = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(response);
        Work w = (Work) response.getEntity();
        assertNotNull(w);
        assertRefused(() -> serviceDelegator.updateWork(ORCID, w.getPutCode(), w));
    }

    @Test
    public void testOrcidProfileCreate_CAN_CRUDOnUnclaimedAccounts() {
        // Whether an account is old enough to be off limits to a client
        // credentials token is OrcidSecurityManager's decision and is proved
        // there; here the security manager allows, and what is asserted is that
        // every endpoint then completes and returns the documented status.
        when(addressManager.createAddress(eq(UNCLAIMED), any(Address.class), eq(true))).thenReturn(address(10L));
        when(addressManagerReadOnly.getAddress(UNCLAIMED, 10L)).thenReturn(address(10L));
        when(addressManager.updateAddress(eq(UNCLAIMED), eq(10L), any(Address.class), eq(true))).thenReturn(address(10L));
        Response response = serviceDelegator.createAddress(UNCLAIMED, Utils.getAddress());
        assertCreated(response);
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(10L), putCode);
        response = serviceDelegator.viewAddress(UNCLAIMED, putCode);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        address.getCountry().setValue(Iso3166Country.ZW);
        assertOk(serviceDelegator.updateAddress(UNCLAIMED, putCode, address));
        assertNoContent(serviceDelegator.deleteAddress(UNCLAIMED, putCode));

        // Test education
        when(affiliationsManager.createEducationAffiliation(eq(UNCLAIMED), any(Education.class), eq(true))).thenReturn(education(1L));
        when(affiliationsManagerReadOnly.getEducationAffiliation(UNCLAIMED, 1L)).thenReturn(education(1L));
        when(affiliationsManager.updateEducationAffiliation(eq(UNCLAIMED), any(Education.class), eq(true))).thenReturn(education(1L));
        response = serviceDelegator.createEducation(UNCLAIMED, (Education) Utils.getAffiliation(AffiliationType.EDUCATION));
        assertCreated(response);
        assertEquals(Long.valueOf(1L), Utils.getPutCode(response));
        response = serviceDelegator.viewEducation(UNCLAIMED, 1L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        education.setDepartmentName("Updated department name");
        assertOk(serviceDelegator.updateEducation(UNCLAIMED, 1L, education));
        assertNoContent(serviceDelegator.deleteAffiliation(UNCLAIMED, 1L));

        // Test employment
        when(affiliationsManager.createEmploymentAffiliation(eq(UNCLAIMED), any(Employment.class), eq(true))).thenReturn(employment(2L));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(UNCLAIMED, 2L)).thenReturn(employment(2L));
        when(affiliationsManager.updateEmploymentAffiliation(eq(UNCLAIMED), any(Employment.class), eq(true))).thenReturn(employment(2L));
        response = serviceDelegator.createEmployment(UNCLAIMED, (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT));
        assertCreated(response);
        response = serviceDelegator.viewEmployment(UNCLAIMED, 2L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        employment.setDepartmentName("Updated department name");
        assertOk(serviceDelegator.updateEmployment(UNCLAIMED, 2L, employment));
        assertNoContent(serviceDelegator.deleteAffiliation(UNCLAIMED, 2L));

        // Test external identifiers
        when(externalIdentifierManager.createExternalIdentifier(eq(UNCLAIMED), any(PersonExternalIdentifier.class), eq(true))).thenReturn(externalIdentifier(3L));
        when(externalIdentifierManagerReadOnly.getExternalIdentifier(UNCLAIMED, 3L)).thenReturn(externalIdentifier(3L));
        when(externalIdentifierManager.updateExternalIdentifier(eq(UNCLAIMED), any(PersonExternalIdentifier.class), eq(true)))
                .thenReturn(externalIdentifier(3L));
        response = serviceDelegator.createExternalIdentifier(UNCLAIMED, Utils.getPersonExternalIdentifier());
        assertCreated(response);
        response = serviceDelegator.viewExternalIdentifier(UNCLAIMED, 3L);
        assertNotNull(response);
        PersonExternalIdentifier externalIdentifier = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(externalIdentifier);
        assertOk(serviceDelegator.updateExternalIdentifier(UNCLAIMED, 3L, externalIdentifier));
        assertNoContent(serviceDelegator.deleteExternalIdentifier(UNCLAIMED, 3L));

        // Test funding
        when(profileFundingManager.createFunding(eq(UNCLAIMED), any(Funding.class), eq(true))).thenReturn(funding(4L));
        when(profileFundingManagerReadOnly.getFunding(UNCLAIMED, 4L)).thenReturn(funding(4L));
        when(profileFundingManager.updateFunding(eq(UNCLAIMED), any(Funding.class), eq(true))).thenReturn(funding(4L));
        response = serviceDelegator.createFunding(UNCLAIMED, Utils.getFunding());
        assertCreated(response);
        response = serviceDelegator.viewFunding(UNCLAIMED, 4L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertOk(serviceDelegator.updateFunding(UNCLAIMED, 4L, funding));
        assertNoContent(serviceDelegator.deleteFunding(UNCLAIMED, 4L));

        // Test keyword
        when(profileKeywordManager.createKeyword(eq(UNCLAIMED), any(Keyword.class), eq(true))).thenReturn(keyword(5L));
        when(profileKeywordManagerReadOnly.getKeyword(UNCLAIMED, 5L)).thenReturn(keyword(5L));
        when(profileKeywordManager.updateKeyword(eq(UNCLAIMED), eq(5L), any(Keyword.class), eq(true))).thenReturn(keyword(5L));
        response = serviceDelegator.createKeyword(UNCLAIMED, Utils.getKeyword());
        assertCreated(response);
        response = serviceDelegator.viewKeyword(UNCLAIMED, 5L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertOk(serviceDelegator.updateKeyword(UNCLAIMED, 5L, keyword));
        assertNoContent(serviceDelegator.deleteKeyword(UNCLAIMED, 5L));

        // Test other names
        when(otherNameManager.createOtherName(eq(UNCLAIMED), any(OtherName.class), eq(true))).thenReturn(otherName(6L));
        when(otherNameManagerReadOnly.getOtherName(UNCLAIMED, 6L)).thenReturn(otherName(6L));
        when(otherNameManager.updateOtherName(eq(UNCLAIMED), eq(6L), any(OtherName.class), eq(true))).thenReturn(otherName(6L));
        response = serviceDelegator.createOtherName(UNCLAIMED, Utils.getOtherName());
        assertCreated(response);
        response = serviceDelegator.viewOtherName(UNCLAIMED, 6L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertOk(serviceDelegator.updateOtherName(UNCLAIMED, 6L, otherName));
        assertNoContent(serviceDelegator.deleteOtherName(UNCLAIMED, 6L));

        // Test peer review
        when(peerReviewManager.createPeerReview(eq(UNCLAIMED), any(PeerReview.class), eq(true))).thenReturn(peerReview(7L));
        when(peerReviewManagerReadOnly.getPeerReview(UNCLAIMED, 7L)).thenReturn(peerReview(7L));
        when(peerReviewManager.updatePeerReview(eq(UNCLAIMED), any(PeerReview.class), eq(true))).thenReturn(peerReview(7L));
        response = serviceDelegator.createPeerReview(UNCLAIMED, Utils.getPeerReview());
        assertCreated(response);
        response = serviceDelegator.viewPeerReview(UNCLAIMED, 7L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertOk(serviceDelegator.updatePeerReview(UNCLAIMED, 7L, peerReview));
        assertNoContent(serviceDelegator.deletePeerReview(UNCLAIMED, 7L));

        // Test researcher url
        when(researcherUrlManager.createResearcherUrl(eq(UNCLAIMED), any(ResearcherUrl.class), eq(true))).thenReturn(researcherUrl(8L));
        when(researcherUrlManagerReadOnly.getResearcherUrl(UNCLAIMED, 8L)).thenReturn(researcherUrl(8L));
        when(researcherUrlManager.updateResearcherUrl(eq(UNCLAIMED), any(ResearcherUrl.class), eq(true))).thenReturn(researcherUrl(8L));
        response = serviceDelegator.createResearcherUrl(UNCLAIMED, Utils.getResearcherUrl());
        assertCreated(response);
        response = serviceDelegator.viewResearcherUrl(UNCLAIMED, 8L);
        assertNotNull(response);
        ResearcherUrl rUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(rUrl);
        assertOk(serviceDelegator.updateResearcherUrl(UNCLAIMED, 8L, rUrl));
        assertNoContent(serviceDelegator.deleteResearcherUrl(UNCLAIMED, 8L));

        // Test work
        when(workManager.createWork(eq(UNCLAIMED), any(Work.class), eq(true))).thenReturn(work(9L));
        when(workManagerReadOnly.getWork(UNCLAIMED, 9L)).thenReturn(work(9L));
        when(workManager.updateWork(eq(UNCLAIMED), any(Work.class), eq(true))).thenReturn(work(9L));
        response = serviceDelegator.createWork(UNCLAIMED, Utils.getWork("work # 1 " + System.currentTimeMillis()));
        assertCreated(response);
        response = serviceDelegator.viewWork(UNCLAIMED, 9L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertOk(serviceDelegator.updateWork(UNCLAIMED, 9L, work));
        assertNoContent(serviceDelegator.deleteWork(UNCLAIMED, 9L));
    }

    @Test
    public void testSearchByQuery() throws ParseException {
        Search search = new Search();
        Result result = new Result();
        result.setOrcidIdentifier(new OrcidIdentifier("some-orcid-id"));
        search.getResults().add(result);
        when(orcidSearchManager.findOrcidIds(ArgumentMatchers.<Map<String, List<String>>> any())).thenReturn(search);

        Response response = serviceDelegator.searchByQuery(new HashMap<String, List<String>>());
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof Search);
        assertEquals(1, ((Search) response.getEntity()).getResults().size());
        assertEquals("some-orcid-id", ((Search) response.getEntity()).getResults().get(0).getOrcidIdentifier().getPath());
        verify(orcidSecurityManager).checkScopes(ScopePathType.READ_PUBLIC);
    }

    @Test(expected = OrcidBadRequestException.class)
    public void testSearchByQueryTooManyRows() throws ParseException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("rows", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_ROWS + 20)));
        when(localeManager.resolveMessage(anyString(), any(Object[].class))).thenReturn("a message");

        serviceDelegator.searchByQuery(params);
    }

    @Test(expected = AccessControlException.class)
    public void testSearchByQueryBadScope() throws ParseException {
        doThrow(new AccessControlException("some problem with scope")).when(orcidSecurityManager).checkScopes(any());

        serviceDelegator.searchByQuery(new HashMap<>());
    }

    @Test(expected = NoResultException.class)
    public void testViewClientNonExistent() {
        when(clientManagerReadOnly.getSummary("some-client-that-doesn't-exist")).thenThrow(new NoResultException());

        serviceDelegator.viewClient("some-client-that-doesn't-exist");
        fail();
    }

    @Test
    public void testViewClient() throws ParseException {
        ClientSummary summary = new ClientSummary();
        summary.setName("Source Client 2");
        summary.setDescription("A test source client");
        when(clientManagerReadOnly.getSummary("APP-6666666666666666")).thenReturn(summary);

        Response response = serviceDelegator.viewClient("APP-6666666666666666");
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);

        ClientSummary clientSummary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", clientSummary.getName());
        assertEquals("A test source client", clientSummary.getDescription());
        verify(orcidSecurityManager).checkScopes(ScopePathType.READ_PUBLIC);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExpandedSearchByQueryNoRowsParamSet() {
        ExpandedSearch search = new ExpandedSearch();
        ExpandedResult result = new ExpandedResult();
        search.getResults().add(result);
        when(orcidSearchManager.expandedSearch(ArgumentMatchers.<Map<String, List<String>>> any())).thenReturn(search);

        Map<String, List<String>> searchQuery = new HashMap<>();
        searchQuery.put("q", Arrays.asList("orcid"));
        serviceDelegator.expandedSearchByQuery(searchQuery);

        @SuppressWarnings("rawtypes")
        ArgumentCaptor<Map> searchParamsCaptor = ArgumentCaptor.forClass(Map.class);
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

    @Test(expected = OrcidBadRequestException.class)
    public void testExpandedSearchByQueryRowsParamTooGreat() {
        Map<String, List<String>> searchQuery = new HashMap<>();
        searchQuery.put("q", Arrays.asList("orcid"));
        searchQuery.put("rows", Arrays.asList(String.valueOf(OrcidSearchManager.MAX_SEARCH_ROWS * 2)));
        when(localeManager.resolveMessage(anyString(), any(Object[].class))).thenReturn("a message");

        serviceDelegator.expandedSearchByQuery(searchQuery);
    }

    // ------------------------------------------------------------- fixtures

    private void assertCreated(Response response) {
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private void assertOk(Response response) {
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    private void assertNoContent(Response response) {
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    private Address address(Long putCode) {
        Address element = new Address();
        element.setPutCode(putCode);
        element.setCountry(new Country(Iso3166Country.ES));
        element.setVisibility(Visibility.PUBLIC);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Education education(Long putCode) {
        Education element = new Education();
        element.setPutCode(putCode);
        element.setDepartmentName("Department");
        element.setOrganization(Utils.getOrganization());
        element.setVisibility(Visibility.PUBLIC);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Employment employment(Long putCode) {
        Employment element = new Employment();
        element.setPutCode(putCode);
        element.setDepartmentName("Department");
        element.setOrganization(Utils.getOrganization());
        element.setVisibility(Visibility.PUBLIC);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private PersonExternalIdentifier externalIdentifier(Long putCode) {
        PersonExternalIdentifier element = Utils.getPersonExternalIdentifier();
        element.setPutCode(putCode);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Funding funding(Long putCode) {
        Funding element = Utils.getFunding();
        element.setPutCode(putCode);
        element.setVisibility(Visibility.PUBLIC);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Keyword keyword(Long putCode) {
        Keyword element = Utils.getKeyword();
        element.setPutCode(putCode);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private OtherName otherName(Long putCode) {
        OtherName element = Utils.getOtherName();
        element.setPutCode(putCode);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private PeerReview peerReview(Long putCode) {
        PeerReview element = Utils.getPeerReview();
        element.setPutCode(putCode);
        element.setVisibility(Visibility.PUBLIC);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private ResearcherUrl researcherUrl(Long putCode) {
        ResearcherUrl element = Utils.getResearcherUrl();
        element.setPutCode(putCode);
        element.setUrl(new Url("http://www.myRUrl.com"));
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private Work work(Long putCode) {
        Work element = Utils.getWork("work # 1");
        element.setPutCode(putCode);
        element.setSource(clientSource(CLIENT_1));
        element.setLastModifiedDate(lastModified());
        return element;
    }
}
