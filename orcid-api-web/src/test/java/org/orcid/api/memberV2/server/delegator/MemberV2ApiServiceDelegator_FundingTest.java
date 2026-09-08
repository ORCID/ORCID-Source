package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingTitle;
import org.orcid.jaxb.model.record_v2.FundingType;
import org.orcid.test.helper.Utils;

/**
 * The funding endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * {@code viewFunding} is the one activity read that also runs the contributor
 * filter, so that call is verified explicitly. Grouping is
 * {@code ProfileFundingManagerReadOnlyImpl}'s and is stubbed here; visibility
 * filtering is {@code OrcidSecurityManager_generalTest}'s; the source-ownership
 * and visibility-mismatch rules are {@code ProfileFundingManagerImpl}'s; and
 * "a funding of another record is not readable or deletable" is a predicate in
 * {@code ProfileFundingDaoImpl}'s SQL.
 */
public class MemberV2ApiServiceDelegator_FundingTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4447";
    private static final String MY_ORCID = "4444-4444-4444-4442";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingWrongToken() {
        Funding funding = funding(10L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(funding);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, funding,
                ScopePathType.FUNDING_READ_LIMITED);

        try {
            serviceDelegator.viewFunding(ORCID, 10L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", funding.getPath());
            verifyNoInteractions(contributorUtils);
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewFundingSummaryWrongToken() {
        FundingSummary summary = fundingSummary(10L, Visibility.PUBLIC, "Public Funding # 1");
        when(profileFundingManagerReadOnly.getSummary(ORCID, 10L)).thenReturn(summary);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, summary,
                ScopePathType.FUNDING_READ_LIMITED);

        try {
            serviceDelegator.viewFundingSummary(ORCID, 10L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", summary.getPath());
        }
    }

    @Test
    public void testViewFundingReadPublic() {
        Funding funding = funding(10L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(funding);

        Response r = serviceDelegator.viewFunding(ORCID, 10L);

        Funding element = (Funding) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/funding/10", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, funding, ScopePathType.FUNDING_READ_LIMITED);
        // private contributor data is stripped on the way out
        verify(contributorUtils).filterContributorPrivateData(funding);
    }

    @Test
    public void testViewFundingSummaryReadPublic() {
        FundingSummary summary = fundingSummary(10L, Visibility.PUBLIC, "Public Funding # 1");
        when(profileFundingManagerReadOnly.getSummary(ORCID, 10L)).thenReturn(summary);

        Response r = serviceDelegator.viewFundingSummary(ORCID, 10L);

        FundingSummary element = (FundingSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/funding/10", element.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary, ScopePathType.FUNDING_READ_LIMITED);
    }

    @Test
    public void testAddFundingWithInvalidExtIdTypeFail() {
        // The external identifier type is validated inside
        // ProfileFundingManagerImpl (through ExternalIDValidator). At this
        // boundary the observable is that the delegator lets the refusal out and
        // does not build a 201 response for it.
        String orcid = "4444-4444-4444-4499";
        Funding funding = Utils.getFunding();
        funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
        when(profileFundingManager.createFunding(eq(orcid), any(Funding.class), anyBoolean())).thenAnswer(invocation -> {
            Funding submitted = invocation.getArgument(1);
            if ("INVALID".equals(submitted.getExternalIdentifiers().getExternalIdentifier().get(0).getType())) {
                throw new ActivityIdentifierValidationException();
            }
            return funding(100L, Visibility.PUBLIC, clientSource(CLIENT_1));
        });

        try {
            serviceDelegator.createFunding(orcid, funding);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("grant_number");
        Response response = serviceDelegator.createFunding(orcid, funding);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(100), putCode);

        response = serviceDelegator.deleteFunding(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(profileFundingManager).checkSourceAndDelete(orcid, putCode);
    }

    @Test
    public void testViewPublicFunding() {
        assertViewFundingDecorated(10L, Visibility.PUBLIC, clientSource(CLIENT_1));
    }

    @Test
    public void testViewLimitedFunding() {
        assertViewFundingDecorated(11L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivateFunding() {
        assertViewFundingDecorated(12L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateFundingWhereYouAreNotTheSource() {
        Funding funding = funding(13L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(profileFundingManagerReadOnly.getFunding(OTHER_ORCID, 13L)).thenReturn(funding);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, funding, ScopePathType.FUNDING_READ_LIMITED);

        serviceDelegator.viewFunding(OTHER_ORCID, 13L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewFundingThatDontBelongToTheUser() {
        // The (orcid, id) predicate is in ProfileFundingDaoImpl's query.
        when(profileFundingManagerReadOnly.getFunding(OTHER_ORCID, 1L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewFunding(OTHER_ORCID, 1L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testViewFundings() {
        List<FundingSummary> stored = new ArrayList<>(Arrays.asList(fundingSummary(10L, Visibility.PUBLIC, "Public Funding # 1"),
                fundingSummary(11L, Visibility.LIMITED, "Limited Funding # 1"), fundingSummary(12L, Visibility.PRIVATE, "Private Funding # 1")));
        when(profileFundingManagerReadOnly.getFundingSummaryList(ORCID)).thenReturn(stored);
        when(profileFundingManager.groupFundings(anyList(), eq(false))).thenAnswer(invocation -> groupEachSeparately(invocation.getArgument(0)));

        Response response = serviceDelegator.viewFundings(ORCID);

        assertNotNull(response);
        Fundings fundings = (Fundings) response.getEntity();
        assertNotNull(fundings);
        assertEquals("/0000-0000-0000-0003/fundings", fundings.getPath());
        Utils.verifyLastModified(fundings.getLastModifiedDate());
        assertEquals(3, fundings.getFundingGroup().size());
        for (FundingGroup group : fundings.getFundingGroup()) {
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary summary = group.getFundingSummary().get(0);
            assertEquals("/0000-0000-0000-0003/funding/" + summary.getPutCode(), summary.getPath());
            assertEquals(CLIENT_1_NAME, summary.getSource().getSourceName().getContent());
        }
        // the cached list must be copied before it reaches a filter that edits
        // in place
        ArgumentCaptor<List<FundingSummary>> filtered = summaryListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), filtered.capture(), eq(ScopePathType.FUNDING_READ_LIMITED));
        assertNotSame(stored, filtered.getValue());
    }

    @Test
    public void testReadPublicScope_Funding() {
        // Refused per element, never with a blanket matcher.
        Funding ten = funding(10L, Visibility.PUBLIC, clientSource(CLIENT_1));
        Funding eleven = funding(11L, Visibility.LIMITED, clientSource(CLIENT_1));
        Funding twelve = funding(12L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(profileFundingManagerReadOnly.getFunding(ORCID, 10L)).thenReturn(ten);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 11L)).thenReturn(eleven);
        when(profileFundingManagerReadOnly.getFunding(ORCID, 12L)).thenReturn(twelve);
        FundingSummary summary = fundingSummary(10L, Visibility.PUBLIC, "Public Funding # 1");
        when(profileFundingManagerReadOnly.getSummary(ORCID, 10L)).thenReturn(summary);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, twelve, ScopePathType.FUNDING_READ_LIMITED);

        Response r = serviceDelegator.viewFunding(ORCID, 10L);
        assertNotNull(r);
        assertEquals(Funding.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewFundingSummary(ORCID, 10L);
        assertNotNull(r);
        assertEquals(FundingSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewFunding(ORCID, 11L);

        try {
            // Private am not the source should fail
            serviceDelegator.viewFunding(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddFunding() {
        Funding created = funding(100L, Visibility.PUBLIC, clientSource(CLIENT_1));
        created.getTitle().getTitle().setContent("Public Funding # 2");
        when(profileFundingManager.createFunding(eq(OTHER_ORCID), any(Funding.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createFunding(OTHER_ORCID, Utils.getFunding());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        assertTrue("Public Funding # 2".equals(created.getTitle().getTitle().getContent()));
        verify(orcidSecurityManager).checkClientAccessAndScopes(OTHER_ORCID, ScopePathType.FUNDING_CREATE, ScopePathType.FUNDING_UPDATE);
        ArgumentCaptor<Funding> submitted = ArgumentCaptor.forClass(Funding.class);
        verify(profileFundingManager).createFunding(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
    }

    @Test
    public void testUpdateFunding() {
        Funding funding = funding(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        funding.getTitle().getTitle().setContent("Updated funding title");
        Funding updated = funding(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.getTitle().getTitle().setContent("Updated funding title");
        when(profileFundingManager.updateFunding(eq(OTHER_ORCID), any(Funding.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateFunding(OTHER_ORCID, 6L, funding);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Updated funding title", ((Funding) response.getEntity()).getTitle().getTitle().getContent());
        verify(orcidSecurityManager).checkClientAccessAndScopes(OTHER_ORCID, ScopePathType.FUNDING_UPDATE);
        ArgumentCaptor<Funding> submitted = ArgumentCaptor.forClass(Funding.class);
        verify(profileFundingManager).updateFunding(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateFundingYouAreNotTheSourceOf() {
        // ProfileFundingManagerImpl calls orcidSecurityManager.checkSource on the
        // stored entity; the rule belongs to that manager's tests.
        Funding funding = funding(5L, Visibility.PUBLIC, clientSource(CLIENT_2));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "funding"))).when(profileFundingManager).updateFunding(eq(OTHER_ORCID),
                any(Funding.class), anyBoolean());

        serviceDelegator.updateFunding(OTHER_ORCID, 5L, funding);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateFundingChangingVisibilityTest() {
        Funding funding = funding(6L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(profileFundingManager).updateFunding(eq(OTHER_ORCID), any(Funding.class), anyBoolean());

        serviceDelegator.updateFunding(OTHER_ORCID, 6L, funding);
        fail();
    }

    @Test
    public void testUpdateFundingLeavingVisibilityNullTest() {
        Funding funding = funding(6L, null, clientSource(CLIENT_1));
        Funding updated = funding(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(profileFundingManager.updateFunding(eq(OTHER_ORCID), any(Funding.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateFunding(OTHER_ORCID, 6L, funding);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((Funding) response.getEntity()).getVisibility());
        ArgumentCaptor<Funding> submitted = ArgumentCaptor.forClass(Funding.class);
        verify(profileFundingManager).updateFunding(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testDeleteFunding() {
        when(profileFundingManagerReadOnly.getFunding(MY_ORCID, 7L)).thenReturn(funding(7L, Visibility.PUBLIC, clientSource(CLIENT_1)))
                .thenThrow(new NoResultException());

        Response response = serviceDelegator.viewFunding(MY_ORCID, 7L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);

        response = serviceDelegator.deleteFunding(MY_ORCID, 7L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.FUNDING_UPDATE);
        verify(profileFundingManager).checkSourceAndDelete(MY_ORCID, 7L);

        serviceDelegator.viewFunding(MY_ORCID, 7L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteFundingYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "funding"))).when(profileFundingManager)
                .checkSourceAndDelete("4444-4444-4444-4446", 5L);

        serviceDelegator.deleteFunding("4444-4444-4444-4446", 5L);
        fail();
    }

    // ------------------------------------------------------------- helpers

    private void assertViewFundingDecorated(long putCode, Visibility visibility, Source source) {
        Funding funding = funding(putCode, visibility, source);
        when(profileFundingManagerReadOnly.getFunding(OTHER_ORCID, putCode)).thenReturn(funding);

        Response response = serviceDelegator.viewFunding(OTHER_ORCID, putCode);

        assertNotNull(response);
        Funding returned = (Funding) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4447/funding/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, funding, ScopePathType.FUNDING_READ_LIMITED);
        verify(contributorUtils).filterContributorPrivateData(funding);
    }

    /**
     * A stand-in for {@code groupFundings}: one group per summary, which is what
     * the real grouper produces when no two fundings share an external
     * identifier. The grouping algorithm itself is the read-only manager's and is
     * tested there.
     */
    private Fundings groupEachSeparately(List<FundingSummary> summaries) {
        Fundings fundings = new Fundings();
        for (FundingSummary summary : summaries) {
            FundingGroup group = new FundingGroup();
            group.getFundingSummary().add(summary);
            group.getIdentifiers().getExternalIdentifier().add(externalId("grant_number", String.valueOf(summary.getPutCode())));
            fundings.getFundingGroup().add(group);
        }
        return fundings;
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<FundingSummary>> summaryListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private Funding funding(Long putCode, Visibility visibility, Source source) {
        Funding funding = new Funding();
        funding.setPutCode(putCode);
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Public Funding # 1"));
        funding.setTitle(title);
        funding.setType(FundingType.GRANT);
        funding.setOrganization(organization());
        funding.setExternalIdentifiers(externalIds("grant_number", String.valueOf(putCode)));
        funding.setVisibility(visibility);
        funding.setSource(source);
        funding.setCreatedDate(createdDate());
        funding.setLastModifiedDate(lastModified());
        return funding;
    }

    private FundingSummary fundingSummary(Long putCode, Visibility visibility, String titleContent) {
        FundingSummary summary = new FundingSummary();
        summary.setPutCode(putCode);
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title(titleContent));
        summary.setTitle(title);
        summary.setType(FundingType.GRANT);
        summary.setOrganization(organization());
        summary.setExternalIdentifiers(externalIds("grant_number", String.valueOf(putCode)));
        summary.setVisibility(visibility);
        summary.setSource(clientSource(CLIENT_1));
        summary.setCreatedDate(createdDate());
        summary.setLastModifiedDate(lastModified());
        return summary;
    }
}
