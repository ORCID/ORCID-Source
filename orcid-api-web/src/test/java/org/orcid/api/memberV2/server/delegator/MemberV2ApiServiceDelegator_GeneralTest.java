package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.apache.hc.core5.http.ParseException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.OrcidSearchManager;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.jaxb.model.common_v2.Country;
import org.orcid.jaxb.model.common_v2.Iso3166Country;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.common_v2.VisibilityType;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Biography;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
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
import org.orcid.jaxb.model.search_v2.Result;
import org.orcid.jaxb.model.search_v2.Search;
import org.orcid.test.helper.Utils;

/**
 * The cross-cutting behaviour of the member v2 delegator, on mocks: that every
 * endpoint consults {@link org.orcid.core.manager.OrcidSecurityManager} before it
 * touches a manager, and the two endpoints -- search and view-client -- that
 * belong to no element family.
 *
 * <p>
 * The four {@code testOrcidProfileCreate_CANT_*} cases used to prove a rule of
 * the security manager: a client-credentials token carrying
 * {@code /orcid-profile/create} may act only on an unclaimed record, and
 * {@code OrcidSecurityManagerImpl.isMyToken} raises
 * {@code IllegalStateException("Non client credential scope found in client
 * request")} otherwise. That rule is proved in
 * {@code OrcidSecurityManager_generalTest}. What survives here -- and is worth
 * keeping, because it is a property of this class and of nothing else -- is that
 * when the guard raises, every single endpoint lets it through and writes
 * nothing.
 */
public class MemberV2ApiServiceDelegator_GeneralTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String REFUSAL = "Non client credential scope found in client request";

    @Test
    public void testOrcidProfileCreate_CANT_AddOnClaimedAccounts() {
        refuseEveryGuardCall();

        // Test can't create
        assertRefused(() -> serviceDelegator.createAddress(ORCID, Utils.getAddress()));
        assertRefused(() -> serviceDelegator.createEducation(ORCID, Utils.getEducation()));
        assertRefused(() -> serviceDelegator.createEmployment(ORCID, Utils.getEmployment()));
        assertRefused(() -> serviceDelegator.createExternalIdentifier(ORCID, Utils.getPersonExternalIdentifier()));
        assertRefused(() -> serviceDelegator.createFunding(ORCID, Utils.getFunding()));
        assertRefused(() -> serviceDelegator.createKeyword(ORCID, Utils.getKeyword()));
        assertRefused(() -> serviceDelegator.createOtherName(ORCID, Utils.getOtherName()));
        assertRefused(() -> serviceDelegator.createPeerReview(ORCID, Utils.getPeerReview()));
        assertRefused(() -> serviceDelegator.createResearcherUrl(ORCID, Utils.getResearcherUrl()));
        assertRefused(() -> serviceDelegator.createWork(ORCID, Utils.getWork("work # 1 " + System.currentTimeMillis())));

        // and nothing at all was written
        verifyNoInteractions(addressManager, affiliationsManager, externalIdentifierManager, profileFundingManager, profileKeywordManager, otherNameManager,
                peerReviewManager, researcherUrlManager, workManager);
    }

    @Test
    public void testOrcidProfileCreate_CANT_ViewOnClaimedAccounts() {
        refuseEveryGuardCall();
        // the five reads that dereference their result before reaching the guard
        when(addressManagerReadOnly.getAddresses(ORCID)).thenReturn(new Addresses());
        when(researcherUrlManagerReadOnly.getResearcherUrls(ORCID)).thenReturn(new ResearcherUrls());
        when(otherNameManagerReadOnly.getOtherNames(ORCID)).thenReturn(new OtherNames());
        when(externalIdentifierManagerReadOnly.getExternalIdentifiers(ORCID)).thenReturn(new PersonExternalIdentifiers());
        when(profileKeywordManagerReadOnly.getKeywords(ORCID)).thenReturn(new Keywords());
        when(biographyManagerReadOnly.getBiography(ORCID)).thenReturn(new Biography());

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
        // Group ids are not a record's data, so a client-credentials token may
        // read and create them. The guard permits, and the delegator forwards.
        when(groupIdRecordManagerReadOnly.getGroupIdRecord(1L)).thenReturn(groupIdRecord(1L, "issn:0000-0001"));
        when(groupIdRecordManagerReadOnly.getGroupIdRecords("10", "1")).thenReturn(new GroupIdRecords());
        GroupIdRecord groupIdRecord = Utils.getNonIssnGroupIdRecord();
        when(groupIdRecordManager.exists(groupIdRecord.getGroupId())).thenReturn(false);
        when(groupIdRecordManager.createGroupIdRecord(any(GroupIdRecord.class))).thenReturn(groupIdRecord(70L, groupIdRecord.getGroupId()));

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

        try {
            serviceDelegator.createGroupIdRecord(groupIdRecord);
        } catch (Exception e) {
            fail();
        }

        verify(groupIdRecordManager).createGroupIdRecord(any(GroupIdRecord.class));
    }

    @Test
    public void testOrcidProfileCreate_CANT_DeleteOnClaimedAccounts() {
        refuseEveryGuardCall();

        assertRefused(() -> serviceDelegator.deleteAddress(ORCID, 9L));
        assertRefused(() -> serviceDelegator.deleteAffiliation(ORCID, 20L));
        assertRefused(() -> serviceDelegator.deleteExternalIdentifier(ORCID, 13L));
        assertRefused(() -> serviceDelegator.deleteFunding(ORCID, 10L));
        assertRefused(() -> serviceDelegator.deleteKeyword(ORCID, 9L));
        assertRefused(() -> serviceDelegator.deleteOtherName(ORCID, 13L));
        assertRefused(() -> serviceDelegator.deletePeerReview(ORCID, 9L));
        assertRefused(() -> serviceDelegator.deleteResearcherUrl(ORCID, 13L));
        assertRefused(() -> serviceDelegator.deleteWork(ORCID, 11L));

        verifyNoInteractions(addressManager, affiliationsManager, externalIdentifierManager, profileFundingManager, profileKeywordManager, otherNameManager,
                peerReviewManager, researcherUrlManager, workManager);
    }

    @Test
    public void testOrcidProfileCreate_CANT_UpdateOnClaimedAccounts() {
        refuseEveryGuardCall();

        Address address = new Address();
        address.setPutCode(9L);
        address.setCountry(new Country(Iso3166Country.US));
        assertRefused(() -> serviceDelegator.updateAddress(ORCID, 9L, address));

        Education education = Utils.getEducation();
        education.setPutCode(20L);
        assertRefused(() -> serviceDelegator.updateEducation(ORCID, 20L, education));

        Employment employment = Utils.getEmployment();
        employment.setPutCode(17L);
        assertRefused(() -> serviceDelegator.updateEmployment(ORCID, 17L, employment));

        PersonExternalIdentifier extId = Utils.getPersonExternalIdentifier();
        extId.setPutCode(13L);
        assertRefused(() -> serviceDelegator.updateExternalIdentifier(ORCID, 13L, extId));

        Funding funding = Utils.getFunding();
        funding.setPutCode(10L);
        assertRefused(() -> serviceDelegator.updateFunding(ORCID, 10L, funding));

        Keyword keyword = Utils.getKeyword();
        keyword.setPutCode(9L);
        assertRefused(() -> serviceDelegator.updateKeyword(ORCID, 9L, keyword));

        OtherName otherName = Utils.getOtherName();
        otherName.setPutCode(13L);
        assertRefused(() -> serviceDelegator.updateOtherName(ORCID, 13L, otherName));

        PeerReview peerReview = Utils.getPeerReview();
        peerReview.setPutCode(9L);
        assertRefused(() -> serviceDelegator.updatePeerReview(ORCID, 9L, peerReview));

        ResearcherUrl researcherUrl = Utils.getResearcherUrl();
        researcherUrl.setPutCode(13L);
        assertRefused(() -> serviceDelegator.updateResearcherUrl(ORCID, 13L, researcherUrl));

        Work work = Utils.getWork("A work");
        work.setPutCode(11L);
        assertRefused(() -> serviceDelegator.updateWork(ORCID, 11L, work));

        verifyNoInteractions(addressManager, affiliationsManager, externalIdentifierManager, profileFundingManager, profileKeywordManager, otherNameManager,
                peerReviewManager, researcherUrlManager, workManager);
    }

    @Test
    public void testOrcidProfileCreate_CAN_CRUDOnUnclaimedAccounts() {
        // The mirror image of the four tests above: when the guard permits -- as
        // it does for a client-credentials token acting on an unclaimed record --
        // every endpoint completes and reaches its manager. Whether this
        // particular actor is permitted is OrcidSecurityManager_generalTest's
        // question; that each endpoint then does its work is this one's.
        String orcid = "0000-0000-0000-0001";
        stubEveryWriteManager(orcid);

        // Test address
        Response response = serviceDelegator.createAddress(orcid, Utils.getAddress());
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        response = serviceDelegator.updateAddress(orcid, putCode, addressWithPutCode(putCode));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAddress(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test education
        response = serviceDelegator.createEducation(orcid, Utils.getEducation());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        Education education = Utils.getEducation();
        education.setPutCode(putCode);
        education.setDepartmentName("Updated department name");
        response = serviceDelegator.updateEducation(orcid, putCode, education);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAffiliation(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test employment
        response = serviceDelegator.createEmployment(orcid, Utils.getEmployment());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        Employment employment = Utils.getEmployment();
        employment.setPutCode(putCode);
        employment.setDepartmentName("Updated department name");
        response = serviceDelegator.updateEmployment(orcid, putCode, employment);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteAffiliation(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test external identifiers
        response = serviceDelegator.createExternalIdentifier(orcid, Utils.getPersonExternalIdentifier());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        PersonExternalIdentifier extId = Utils.getPersonExternalIdentifier();
        extId.setPutCode(putCode);
        response = serviceDelegator.updateExternalIdentifier(orcid, putCode, extId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteExternalIdentifier(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test funding
        response = serviceDelegator.createFunding(orcid, Utils.getFunding());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        Funding funding = Utils.getFunding();
        funding.setPutCode(putCode);
        response = serviceDelegator.updateFunding(orcid, putCode, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteFunding(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test keyword
        response = serviceDelegator.createKeyword(orcid, Utils.getKeyword());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        Keyword keyword = Utils.getKeyword();
        keyword.setPutCode(putCode);
        response = serviceDelegator.updateKeyword(orcid, putCode, keyword);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteKeyword(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test other names
        response = serviceDelegator.createOtherName(orcid, Utils.getOtherName());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        OtherName otherName = Utils.getOtherName();
        otherName.setPutCode(putCode);
        response = serviceDelegator.updateOtherName(orcid, putCode, otherName);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteOtherName(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test peer review
        response = serviceDelegator.createPeerReview(orcid, Utils.getPeerReview());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        PeerReview peerReview = Utils.getPeerReview();
        peerReview.setPutCode(putCode);
        response = serviceDelegator.updatePeerReview(orcid, putCode, peerReview);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deletePeerReview(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test researcher url
        response = serviceDelegator.createResearcherUrl(orcid, Utils.getResearcherUrl());
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        ResearcherUrl researcherUrl = Utils.getResearcherUrl();
        researcherUrl.setPutCode(putCode);
        response = serviceDelegator.updateResearcherUrl(orcid, putCode, researcherUrl);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteResearcherUrl(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Test work
        response = serviceDelegator.createWork(orcid, Utils.getWork("work # 1 " + System.currentTimeMillis()));
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        putCode = Utils.getPutCode(response);
        Work work = Utils.getWork("work # 1");
        work.setPutCode(putCode);
        response = serviceDelegator.updateWork(orcid, putCode, work);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.deleteWork(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testSearchByQuery() throws ParseException {
        Search search = new Search();
        Result result = new Result();
        result.setOrcidIdentifier(new OrcidIdentifier("some-orcid-id"));
        search.getResults().add(result);
        when(orcidSearchManager.findOrcidIds(any())).thenReturn(search);

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
        // Pure delegator logic: validateSearchParams rejects a rows parameter
        // above OrcidSearchManager.MAX_SEARCH_ROWS before any search is run.
        Map<String, List<String>> params = new HashMap<>();
        params.put("rows", Arrays.asList(Integer.toString(OrcidSearchManager.MAX_SEARCH_ROWS + 20)));
        when(localeManager.resolveMessage(anyString(), eq(OrcidSearchManager.MAX_SEARCH_ROWS))).thenReturn("a message");

        try {
            serviceDelegator.searchByQuery(params);
        } finally {
            verifyNoInteractions(orcidSearchManager);
        }
    }

    @Test(expected = AccessControlException.class)
    public void testSearchByQueryBadScope() throws ParseException {
        doThrow(new AccessControlException("some problem with scope")).when(orcidSecurityManager).checkScopes(any(ScopePathType.class));

        try {
            serviceDelegator.searchByQuery(new HashMap<>());
        } finally {
            verifyNoInteractions(orcidSearchManager);
        }
    }

    @Test(expected = NoResultException.class)
    public void testViewClientNonExistent() {
        when(clientManagerReadOnly.getSummary("some-client-that-doesn't-exist")).thenThrow(new NoResultException());

        serviceDelegator.viewClient("some-client-that-doesn't-exist");
        fail();
    }

    @Test
    public void testViewClient() throws ParseException {
        ClientSummary stored = new ClientSummary();
        stored.setName("Source Client 2");
        stored.setDescription("A test source client");
        when(clientManagerReadOnly.getSummary("APP-6666666666666666")).thenReturn(stored);

        Response response = serviceDelegator.viewClient("APP-6666666666666666");

        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ClientSummary);

        ClientSummary clientSummary = (ClientSummary) response.getEntity();
        assertEquals("Source Client 2", clientSummary.getName());
        assertEquals("A test source client", clientSummary.getDescription());
        verify(orcidSecurityManager).checkScopes(ScopePathType.READ_PUBLIC);
    }

    // ------------------------------------------------------------- helpers

    /**
     * Makes every guard method raise the refusal that
     * {@code OrcidSecurityManagerImpl.isMyToken} raises for a client-credentials
     * token on a claimed record. One {@code doThrow} per overload, because
     * {@code checkAndFilter} is overloaded on the type of what it filters.
     */
    private void refuseEveryGuardCall() {
        IllegalStateException refusal = new IllegalStateException(REFUSAL);
        doThrow(refusal).when(orcidSecurityManager).checkClientAccessAndScopes(anyString(), any());
        // ArgumentMatchers.any() rather than any(SomeType.class): several reads
        // hand the guard a null when the record has no such element, and the
        // typed matcher does not match null.
        doThrow(refusal).when(orcidSecurityManager).checkAndFilter(anyString(), ArgumentMatchers.<VisibilityType> any(), any(ScopePathType.class));
        doThrow(refusal).when(orcidSecurityManager).checkAndFilter(anyString(), ArgumentMatchers.<Collection<? extends VisibilityType>> any(),
                any(ScopePathType.class));
        doThrow(refusal).when(orcidSecurityManager).checkAndFilter(anyString(), ArgumentMatchers.<WorkBulk> any(), any(ScopePathType.class));
        doThrow(refusal).when(orcidSecurityManager).checkAndFilter(anyString(), ArgumentMatchers.<ActivitiesSummary> any());
        doThrow(refusal).when(orcidSecurityManager).checkAndFilter(anyString(), ArgumentMatchers.<PersonalDetails> any());
        doThrow(refusal).when(orcidSecurityManager).checkAndFilter(anyString(), ArgumentMatchers.<Person> any());
        doThrow(refusal).when(orcidSecurityManager).checkAndFilter(anyString(), ArgumentMatchers.<Record> any());
    }

    private void assertRefused(Runnable call) {
        try {
            call.run();
            fail();
        } catch (IllegalStateException e) {
            assertEquals(REFUSAL, e.getMessage());
        }
    }

    private void stubEveryWriteManager(String orcid) {
        Address address = new Address();
        address.setPutCode(1L);
        address.setCountry(new Country(Iso3166Country.ES));
        address.setVisibility(Visibility.LIMITED);
        when(addressManager.createAddress(eq(orcid), any(Address.class), anyBoolean())).thenReturn(address);
        when(addressManager.updateAddress(eq(orcid), anyLong(), any(Address.class), anyBoolean())).thenReturn(address);

        Education education = Utils.getEducation();
        education.setPutCode(1L);
        when(affiliationsManager.createEducationAffiliation(eq(orcid), any(Education.class), anyBoolean())).thenReturn(education);
        when(affiliationsManager.updateEducationAffiliation(eq(orcid), any(Education.class), anyBoolean())).thenReturn(education);

        Employment employment = Utils.getEmployment();
        employment.setPutCode(1L);
        when(affiliationsManager.createEmploymentAffiliation(eq(orcid), any(Employment.class), anyBoolean())).thenReturn(employment);
        when(affiliationsManager.updateEmploymentAffiliation(eq(orcid), any(Employment.class), anyBoolean())).thenReturn(employment);

        PersonExternalIdentifier extId = Utils.getPersonExternalIdentifier();
        extId.setPutCode(1L);
        when(externalIdentifierManager.createExternalIdentifier(eq(orcid), any(PersonExternalIdentifier.class), anyBoolean())).thenReturn(extId);
        when(externalIdentifierManager.updateExternalIdentifier(eq(orcid), any(PersonExternalIdentifier.class), anyBoolean())).thenReturn(extId);

        Funding funding = Utils.getFunding();
        funding.setPutCode(1L);
        when(profileFundingManager.createFunding(eq(orcid), any(Funding.class), anyBoolean())).thenReturn(funding);
        when(profileFundingManager.updateFunding(eq(orcid), any(Funding.class), anyBoolean())).thenReturn(funding);

        Keyword keyword = Utils.getKeyword();
        keyword.setPutCode(1L);
        when(profileKeywordManager.createKeyword(eq(orcid), any(Keyword.class), anyBoolean())).thenReturn(keyword);
        when(profileKeywordManager.updateKeyword(eq(orcid), anyLong(), any(Keyword.class), anyBoolean())).thenReturn(keyword);

        OtherName otherName = Utils.getOtherName();
        otherName.setPutCode(1L);
        when(otherNameManager.createOtherName(eq(orcid), any(OtherName.class), anyBoolean())).thenReturn(otherName);
        when(otherNameManager.updateOtherName(eq(orcid), anyLong(), any(OtherName.class), anyBoolean())).thenReturn(otherName);

        PeerReview peerReview = Utils.getPeerReview();
        peerReview.setPutCode(1L);
        when(peerReviewManager.createPeerReview(eq(orcid), any(PeerReview.class), anyBoolean())).thenReturn(peerReview);
        when(peerReviewManager.updatePeerReview(eq(orcid), any(PeerReview.class), anyBoolean())).thenReturn(peerReview);

        ResearcherUrl researcherUrl = Utils.getResearcherUrl();
        researcherUrl.setPutCode(1L);
        when(researcherUrlManager.createResearcherUrl(eq(orcid), any(ResearcherUrl.class), anyBoolean())).thenReturn(researcherUrl);
        when(researcherUrlManager.updateResearcherUrl(eq(orcid), any(ResearcherUrl.class), anyBoolean())).thenReturn(researcherUrl);

        Work work = Utils.getWork("work # 1");
        work.setPutCode(1L);
        when(workManager.createWork(eq(orcid), any(Work.class), anyBoolean())).thenReturn(work);
        when(workManager.updateWork(eq(orcid), any(Work.class), anyBoolean())).thenReturn(work);
    }

    private Address addressWithPutCode(Long putCode) {
        Address address = new Address();
        address.setPutCode(putCode);
        address.setCountry(new Country(Iso3166Country.ZW));
        return address;
    }

    private GroupIdRecord groupIdRecord(Long putCode, String groupId) {
        GroupIdRecord record = new GroupIdRecord();
        record.setPutCode(putCode);
        record.setGroupId(groupId);
        record.setName("TestGroup");
        record.setDescription("TestDescription");
        record.setType("publisher");
        record.setCreatedDate(createdDate());
        record.setLastModifiedDate(lastModified());
        return record;
    }
}
