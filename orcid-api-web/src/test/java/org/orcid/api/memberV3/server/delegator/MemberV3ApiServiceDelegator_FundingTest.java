package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common.FundingType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.FundingContributor;
import org.orcid.jaxb.model.v3.release.record.FundingContributors;
import org.orcid.jaxb.model.v3.release.record.FundingTitle;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the funding endpoints of the member V3 API.
 *
 * <p>
 * Two collaborators used to do the work these tests asserted on and are mocks
 * now, so the corresponding assertions had to move rather than stay and pass
 * vacuously: {@code OrcidSecurityManager.checkAndFilter} is void and filters in
 * place (its tables are proved by orcid-core's
 * {@code OrcidSecurityManager_generalTest}), and
 * {@code ContributorUtils.filterContributorPrivateData} strips contributor
 * credit names in place (proved by orcid-core's {@code ContributorUtilsTest}).
 * At this boundary what is provable is that the delegator called each of them
 * with the right element.
 */
public class MemberV3ApiServiceDelegator_FundingTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.FUNDING_READ_LIMITED;

    private static final String USER_4442 = "4444-4444-4444-4442";
    private static final String USER_4443 = "4444-4444-4444-4443";
    private static final String USER_4446 = "4444-4444-4444-4446";
    private static final String USER_4447 = "4444-4444-4444-4447";
    private static final String USER_4499 = "4444-4444-4444-4499";

    private Funding funding(long putCode, String title, Visibility visibility, Source source) {
        Funding element = new Funding();
        element.setPutCode(putCode);
        element.setTitle(fundingTitle(title));
        element.setType(FundingType.AWARD);
        element.setOrganization(Utils.getOrganization());
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        element.setExternalIdentifiers(externalIds(String.valueOf(putCode)));
        return element;
    }

    private FundingSummary summary(long putCode, String title, Visibility visibility, Source source) {
        FundingSummary element = new FundingSummary();
        element.setPutCode(putCode);
        element.setTitle(fundingTitle(title));
        element.setType(FundingType.AWARD);
        element.setOrganization(Utils.getOrganization());
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private FundingTitle fundingTitle(String title) {
        FundingTitle fundingTitle = new FundingTitle();
        fundingTitle.setTitle(new Title(title));
        return fundingTitle;
    }

    private ExternalIDs externalIds(String value) {
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.PART_OF);
        extId.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
        extId.setUrl(new Url("http://fundingExtId.com"));
        extId.setValue(value);
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        return extIds;
    }

    private FundingGroup group(String externalIdValue, FundingSummary summary) {
        FundingGroup group = new FundingGroup();
        group.getIdentifiers().getExternalIdentifier().add(externalIds(externalIdValue).getExternalIdentifier().get(0));
        group.getFundingSummary().add(summary);
        return group;
    }

    private Fundings fundings(FundingGroup... groups) {
        Fundings container = new Fundings();
        container.getFundingGroup().addAll(Arrays.asList(groups));
        return container;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingWrongToken() {
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(funding(10L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Funding.class), eq(SCOPE));

        serviceDelegator.viewFunding(ORCID, 10L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingSummaryWrongToken() {
        when(profileFundingManagerReadOnly.getSummary(ORCID, 10L)).thenReturn(summary(10L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(FundingSummary.class), eq(SCOPE));

        serviceDelegator.viewFundingSummary(ORCID, 10L);
    }

    @Test
    public void testViewFundingReadPublic() {
        Funding stored = funding(10L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        FundingContributors contributors = new FundingContributors();
        FundingContributor contributor = new FundingContributor();
        contributor.setContributorOrcid(new ContributorOrcid("0000-0000-0000-0000"));
        contributors.getContributor().add(contributor);
        stored.setContributors(contributors);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(stored);

        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        Funding element = (Funding) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/funding/10", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        assertNotNull(element.getContributors().getContributor().get(0).getContributorOrcid());
        assertEquals("0000-0000-0000-0000", element.getContributors().getContributor().get(0).getContributorOrcid().getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
        // Stripping the contributor credit name is ContributorUtils' job and is
        // proved by ContributorUtilsTest; here we prove the delegator asks.
        verify(contributorUtils).filterContributorPrivateData(element);
    }

    @Test
    public void testViewFundingSummaryReadPublic() {
        FundingSummary stored = summary(10L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileFundingManagerReadOnly.getSummary(ORCID, 10L)).thenReturn(stored);

        Response r = serviceDelegator.viewFundingSummary(ORCID, 10L);
        FundingSummary element = (FundingSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/funding/10", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testAddFundingWithInvalidExtIdTypeFail() {
        Funding funding = Utils.getFunding();
        funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
        // The external identifier type table is enforced by ExternalIDValidator
        // inside the manager and proved by ExternalIDValidatorTest; the
        // delegator's contract is to let the exception through untouched, then
        // pass the corrected funding on.
        Funding created = funding(1000L, "Public Funding # 2", Visibility.PUBLIC, clientSource(CLIENT_1));
        doThrow(new ActivityIdentifierValidationException()).doReturn(created).when(profileFundingManager).createFunding(eq(USER_4499), any(Funding.class),
                eq(true));

        try {
            serviceDelegator.createFunding(USER_4499, funding);
            fail();
        } catch (ActivityIdentifierValidationException e) {
        } catch (Exception e) {
            fail();
        }

        funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("grant_number");

        Response response = serviceDelegator.createFunding(USER_4499, funding);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(1000L), putCode);

        // Delete it to roll back the test data
        response = serviceDelegator.deleteFunding(USER_4499, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(profileFundingManager).checkSourceAndDelete(USER_4499, 1000L);
    }

    @Test
    public void testViewPublicFunding() {
        when(profileFundingManagerReadOnly.getFunding(USER_4446, 5L)).thenReturn(funding(5L, "Public Funding", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewFunding(USER_4446, 5L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        Utils.verifyLastModified(funding.getLastModifiedDate());
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(5), funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/5", funding.getPath());
        assertEquals("Public Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, funding, SCOPE);
    }

    @Test
    public void testViewLimitedFunding() {
        when(profileFundingManagerReadOnly.getFunding(USER_4443, 1L)).thenReturn(funding(1L, "Grant # 1", Visibility.LIMITED, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewFunding(USER_4443, 1L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        Utils.verifyLastModified(funding.getLastModifiedDate());
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(1), funding.getPutCode());
        assertEquals("/4444-4444-4444-4443/funding/1", funding.getPath());
        assertEquals("Grant # 1", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(USER_4443, funding, SCOPE);
    }

    @Test
    public void testViewPrivateFunding() {
        when(profileFundingManagerReadOnly.getFunding(USER_4446, 4L)).thenReturn(funding(4L, "Private Funding", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewFunding(USER_4446, 4L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        Utils.verifyLastModified(funding.getLastModifiedDate());
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(4), funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/4", funding.getPath());
        assertEquals("Private Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, funding, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateFundingWhereYouAreNotTheSource() {
        Funding stored = funding(3L, "Private Funding", Visibility.PRIVATE, clientSource(CLIENT_2));
        when(profileFundingManagerReadOnly.getFunding(USER_4443, 3L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(USER_4443, stored, SCOPE);

        serviceDelegator.viewFunding(USER_4443, 3L);
        fail();
    }

    /**
     * The rule this proves -- that funding 1 cannot be read through record 4446 --
     * lives in a SQL WHERE clause
     * ({@code ProfileFundingDaoImpl.getProfileFunding}), so with a mocked manager
     * only the pass-through survives here. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewFundingThatDontBelongToTheUser() {
        // Funding 1 belongs to 4444-4444-4444-4443
        when(profileFundingManagerReadOnly.getFunding(USER_4446, 1L)).thenThrow(new NoResultException());

        serviceDelegator.viewFunding(USER_4446, 1L);
        fail();
    }

    @Test
    public void testViewFundings() {
        FundingSummary pub = summary(10L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        FundingSummary limited = summary(11L, "LIMITED", Visibility.LIMITED, clientSource(CLIENT_1));
        FundingSummary priv = summary(12L, "PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1));
        FundingSummary selfLimited = summary(13L, "SELF LIMITED", Visibility.LIMITED, userSource(ORCID));
        List<FundingSummary> summaries = Arrays.asList(pub, limited, priv, selfLimited);
        when(profileFundingManagerReadOnly.getFundingSummaryList(ORCID)).thenReturn(summaries);
        when(profileFundingManager.groupFundings(summaries, false))
                .thenReturn(fundings(group("1", pub), group("2", limited), group("3", priv), group("4", selfLimited)));

        Response r = serviceDelegator.viewFundings(ORCID);
        assertNotNull(r);
        Fundings fundings = (Fundings) r.getEntity();
        assertNotNull(fundings);
        assertEquals("/0000-0000-0000-0003/fundings", fundings.getPath());
        Utils.verifyLastModified(fundings.getLastModifiedDate());
        assertNotNull(fundings.getFundingGroup());
        assertEquals(4, fundings.getFundingGroup().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (FundingGroup fundingGroup : fundings.getFundingGroup()) {
            Utils.verifyLastModified(fundingGroup.getLastModifiedDate());
            assertNotNull(fundingGroup.getIdentifiers());
            assertNotNull(fundingGroup.getIdentifiers().getExternalIdentifier());
            assertEquals(1, fundingGroup.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(fundingGroup.getFundingSummary());
            assertEquals(1, fundingGroup.getFundingSummary().size());
            FundingSummary summary = fundingGroup.getFundingSummary().get(0);
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            switch (fundingGroup.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "1":
                assertEquals("PUBLIC", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(10), summary.getPutCode());
                assertEquals("/0000-0000-0000-0003/funding/10", summary.getPath());
                found1 = true;
                break;
            case "2":
                assertEquals("LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                found2 = true;
                break;
            case "3":
                assertEquals("PRIVATE", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found3 = true;
                break;
            case "4":
                assertEquals("SELF LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(13), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid external id found: " + fundingGroup.getIdentifiers().getExternalIdentifier().get(0).getValue());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_Funding() {
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(funding(10L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(profileFundingManagerReadOnly.getSummary(ORCID, 10L)).thenReturn(summary(10L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(profileFundingManagerReadOnly.getFunding(ORCID, 11L)).thenReturn(funding(11L, "LIMITED", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(profileFundingManagerReadOnly.getSummary(ORCID, 11L)).thenReturn(summary(11L, "LIMITED", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(profileFundingManagerReadOnly.getFunding(ORCID, 12L)).thenReturn(funding(12L, "PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(profileFundingManagerReadOnly.getSummary(ORCID, 12L)).thenReturn(summary(12L, "PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Funding limitedOtherSource = funding(13L, "SELF LIMITED", Visibility.LIMITED, userSource(ORCID));
        FundingSummary limitedOtherSourceSummary = summary(13L, "SELF LIMITED", Visibility.LIMITED, userSource(ORCID));
        Funding privateOtherSource = funding(14L, "SELF PRIVATE", Visibility.PRIVATE, userSource(ORCID));
        FundingSummary privateOtherSourceSummary = summary(14L, "SELF PRIVATE", Visibility.PRIVATE, userSource(ORCID));
        when(profileFundingManagerReadOnly.getFunding(ORCID, 13L)).thenReturn(limitedOtherSource);
        when(profileFundingManagerReadOnly.getSummary(ORCID, 13L)).thenReturn(limitedOtherSourceSummary);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 14L)).thenReturn(privateOtherSource);
        when(profileFundingManagerReadOnly.getSummary(ORCID, 14L)).thenReturn(privateOtherSourceSummary);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSourceSummary, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSourceSummary, SCOPE);

        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(r);
        assertEquals(Funding.class.getName(), r.getEntity().getClass().getName());
        r = serviceDelegator.viewFundingSummary(ORCID, 10L);
        assertNotNull(r);
        assertEquals(FundingSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewFunding(ORCID, 11L);
        serviceDelegator.viewFundingSummary(ORCID, 11L);
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewFunding(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        try {
            serviceDelegator.viewFundingSummary(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }

        // Private that am the source of should work
        serviceDelegator.viewFunding(ORCID, 12L);
        serviceDelegator.viewFundingSummary(ORCID, 12L);
        // Private am not the source of should fail
        try {
            serviceDelegator.viewFunding(ORCID, 14L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        try {
            serviceDelegator.viewFundingSummary(ORCID, 14L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddFunding() {
        FundingSummary existing = summary(6L, "Public Funding # 1", Visibility.PUBLIC, clientSource(CLIENT_1));
        FundingSummary added = summary(1000L, "Public Funding # 2", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(USER_4447), eq(false)))
                .thenReturn(activitiesWithFundings(fundings(group("1", existing))))
                .thenReturn(activitiesWithFundings(fundings(group("1", existing), group("2", added))));
        when(profileFundingManager.createFunding(eq(USER_4447), any(Funding.class), eq(true)))
                .thenReturn(funding(1000L, "Public Funding # 2", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewActivities(USER_4447);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getFundings());
        Utils.verifyLastModified(summary.getFundings().getLastModifiedDate());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertEquals(1, summary.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertEquals("Public Funding # 1", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent());

        Funding newFunding = Utils.getFunding();
        // Planted so that assertNull below proves clearSource ran, rather than
        // only proving the fixture never had a source to begin with.
        newFunding.setSource(clientSource(CLIENT_2));
        response = serviceDelegator.createFunding(USER_4447, newFunding);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4447, ScopePathType.FUNDING_CREATE, ScopePathType.FUNDING_UPDATE);
        // A client supplied source must never reach the manager.
        ArgumentCaptor<Funding> captor = ArgumentCaptor.forClass(Funding.class);
        verify(profileFundingManager).createFunding(eq(USER_4447), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        response = serviceDelegator.viewActivities(USER_4447);
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertEquals(2, summary.getFundings().getFundingGroup().size());
        boolean haveOld = false;
        boolean haveNew = false;
        for (FundingGroup group : summary.getFundings().getFundingGroup()) {
            assertNotNull(group.getFundingSummary().get(0));
            assertNotNull(group.getFundingSummary().get(0).getTitle());
            assertNotNull(group.getFundingSummary().get(0).getTitle().getTitle());
            assertNotNull(group.getFundingSummary().get(0).getTitle().getTitle().getContent());
            if ("Public Funding # 1".equals(group.getFundingSummary().get(0).getTitle().getTitle().getContent())) {
                haveOld = true;
            } else if ("Public Funding # 2".equals(group.getFundingSummary().get(0).getTitle().getTitle().getContent())) {
                haveNew = true;
            }
        }
        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    private ActivitiesSummary activitiesWithFundings(Fundings fundings) {
        ActivitiesSummary activities = emptyActivitiesSummary();
        activities.setFundings(fundings);
        return activities;
    }

    @Test
    public void testUpdateFunding() {
        Funding stored = funding(6L, "Public Funding # 1", Visibility.PUBLIC, clientSource(CLIENT_1));
        stored.setDescription("This is the description for funding with id 6");
        Funding updated = funding(6L, "Updated funding title", Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setDescription("This is an updated description");
        Funding rolledBack = funding(6L, "Public Funding # 1", Visibility.PUBLIC, clientSource(CLIENT_1));
        rolledBack.setDescription("This is the description for funding with id 6");
        when(profileFundingManagerReadOnly.getFunding(USER_4447, 6L)).thenReturn(stored).thenReturn(updated);
        when(profileFundingManager.updateFunding(eq(USER_4447), any(Funding.class), eq(true))).thenReturn(updated).thenReturn(rolledBack);

        Response response = serviceDelegator.viewFunding(USER_4447, 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals("Public Funding # 1", funding.getTitle().getTitle().getContent());
        assertEquals("This is the description for funding with id 6", funding.getDescription());

        funding.getTitle().getTitle().setContent("Updated funding title");
        funding.setDescription("This is an updated description");
        funding.setExternalIdentifiers(externalIds("new-funding-ext-id"));

        response = serviceDelegator.updateFunding(USER_4447, 6L, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4447, ScopePathType.FUNDING_UPDATE);

        response = serviceDelegator.viewFunding(USER_4447, 6L);
        assertNotNull(response);
        funding = (Funding) response.getEntity();
        assertNotNull(funding);
        Utils.verifyLastModified(funding.getLastModifiedDate());
        assertEquals("Updated funding title", funding.getTitle().getTitle().getContent());
        assertEquals("This is an updated description", funding.getDescription());

        // Rollback changes
        funding.getTitle().getTitle().setContent("Public Funding # 1");
        funding.setDescription("This is the description for funding with id 6");

        response = serviceDelegator.updateFunding(USER_4447, 6L, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Public Funding # 1", ((Funding) response.getEntity()).getTitle().getTitle().getContent());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateFundingYouAreNotTheSourceOf() {
        when(profileFundingManagerReadOnly.getFunding(USER_4446, 5L)).thenReturn(funding(5L, "Public Funding", Visibility.PUBLIC, clientSource(CLIENT_2)));
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(profileFundingManager).updateFunding(eq(USER_4446), any(Funding.class), eq(true));

        Response response = serviceDelegator.viewFunding(USER_4446, 5L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        funding.getTitle().getTitle().setContent("Updated funding title");
        funding.setExternalIdentifiers(externalIds("new-funding-ext-id"));

        serviceDelegator.updateFunding(USER_4446, 5L, funding);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateFundingChangingVisibilityTest() {
        when(profileFundingManagerReadOnly.getFunding(USER_4447, 6L)).thenReturn(funding(6L, "Public Funding # 1", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new VisibilityMismatchException()).when(profileFundingManager).updateFunding(eq(USER_4447), any(Funding.class), eq(true));

        Response response = serviceDelegator.viewFunding(USER_4447, 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals(Visibility.PUBLIC, funding.getVisibility());

        funding.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateFunding(USER_4447, 6L, funding);
        fail();
    }

    @Test
    public void testUpdateFundingLeavingVisibilityNullTest() {
        when(profileFundingManagerReadOnly.getFunding(USER_4447, 6L)).thenReturn(funding(6L, "Public Funding # 1", Visibility.PUBLIC, clientSource(CLIENT_1)));
        // Restoring the stored visibility is the manager's job and is proved
        // there; here the delegator must simply return what it produced.
        when(profileFundingManager.updateFunding(eq(USER_4447), any(Funding.class), eq(true)))
                .thenReturn(funding(6L, "Public Funding # 1", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewFunding(USER_4447, 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals(Visibility.PUBLIC, funding.getVisibility());

        funding.setVisibility(null);

        response = serviceDelegator.updateFunding(USER_4447, 6L, funding);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        funding = (Funding) response.getEntity();
        assertEquals(Visibility.PUBLIC, funding.getVisibility());
        // Catches a delegator that sets a visibility on the element before handing it to
        // the manager: what is submitted must still carry the null the request arrived with.
        ArgumentCaptor<Funding> submitted = ArgumentCaptor.forClass(Funding.class);
        verify(profileFundingManager).updateFunding(eq(USER_4447), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testDeleteFunding() {
        when(profileFundingManagerReadOnly.getFunding(USER_4442, 7L)).thenReturn(funding(7L, "Public Funding", Visibility.PUBLIC, clientSource(CLIENT_1)))
                .thenThrow(new NoResultException());

        Response response = serviceDelegator.viewFunding(USER_4442, 7L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);

        response = serviceDelegator.deleteFunding(USER_4442, 7L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4442, ScopePathType.FUNDING_UPDATE);
        verify(profileFundingManager).checkSourceAndDelete(USER_4442, 7L);

        serviceDelegator.viewFunding(USER_4442, 7L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteFundingYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>())).when(profileFundingManager).checkSourceAndDelete(USER_4446, 5L);

        serviceDelegator.deleteFunding(USER_4446, 5L);
        fail();
    }
}
