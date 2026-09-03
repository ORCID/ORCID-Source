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

import org.apache.hc.core5.http.HttpStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.DisambiguatedOrganization;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the invited-position endpoints of the member V3 API.
 *
 * <p>
 * These used to run the real {@code OrcidSecurityManager} against a DBUnit
 * fixture, so a "view" test proved both the delegator's own work (look the
 * element up, set its path, resolve the source name, return 200) and the
 * security manager's visibility table. Only the first half can be proved here:
 * {@code checkAndFilter} is void and filters in place, so a mocked security
 * manager filters nothing and any assertion of the form "only public elements
 * came back" would pass without proving anything. What each test asserts now is
 * the delegator's contract, plus a {@code verify} that the element really was
 * handed to the security manager with the right scope. The visibility and scope
 * tables themselves are proved in orcid-core, by
 * {@code OrcidSecurityManager_generalTest} and its siblings.
 */
public class MemberV3ApiServiceDelegator_InvitedPositionsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.AFFILIATIONS_READ_LIMITED;

    private InvitedPosition invitedPosition(long putCode, Visibility visibility, String department, Source source) {
        InvitedPosition element = new InvitedPosition();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private InvitedPositionSummary summary(long putCode, Visibility visibility, String department, Source source) {
        InvitedPositionSummary element = new InvitedPositionSummary();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private AffiliationGroup<InvitedPositionSummary> group(InvitedPositionSummary... elements) {
        AffiliationGroup<InvitedPositionSummary> group = new AffiliationGroup<>();
        for (InvitedPositionSummary element : elements) {
            group.getActivities().add(element);
        }
        return group;
    }

    private ExternalIDs duplicateExternalIDs() {
        ExternalID e1 = new ExternalID();
        e1.setRelationship(Relationship.SELF);
        e1.setType("erm");
        e1.setUrl(new Url("https://orcid.org"));
        e1.setValue("err");

        ExternalID e2 = new ExternalID();
        e2.setRelationship(Relationship.SELF);
        e2.setType("err");
        e2.setUrl(new Url("http://bbc.co.uk"));
        e2.setValue("erm");

        ExternalIDs externalIDs = new ExternalIDs();
        externalIDs.getExternalIdentifier().add(e1);
        externalIDs.getExternalIdentifier().add(e2);
        return externalIDs;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewInvitedPositionsWrongToken() {
        when(affiliationsManagerReadOnly.getInvitedPositionSummaryList(ORCID))
                .thenReturn(Arrays.asList(summary(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewInvitedPositions(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewInvitedPositionWrongToken() {
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L))
                .thenReturn(invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(InvitedPosition.class), eq(SCOPE));

        serviceDelegator.viewInvitedPosition(ORCID, 32L);
    }

    @Test
    public void testViewInvitedPositionReadPublic() {
        InvitedPosition stored = invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L)).thenReturn(stored);

        Response r = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        InvitedPosition element = (InvitedPosition) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/invited-position/32", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewInvitedPositionSummaryWrongToken() {
        when(affiliationsManagerReadOnly.getInvitedPositionSummary(ORCID, 32L))
                .thenReturn(summary(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(InvitedPositionSummary.class), eq(SCOPE));

        serviceDelegator.viewInvitedPositionSummary(ORCID, 32L);
    }

    @Test
    public void testViewInvitedPositionsReadPublic() {
        InvitedPositionSummary publicSummary = summary(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<InvitedPositionSummary> summaries = Arrays.asList(publicSummary);
        when(affiliationsManagerReadOnly.getInvitedPositionSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false)).thenReturn(Arrays.asList(group(publicSummary)));

        Response r = serviceDelegator.viewInvitedPositions(ORCID);
        InvitedPositions element = (InvitedPositions) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/invited-positions", element.getPath());
        for (AffiliationGroup<InvitedPositionSummary> group : element.retrieveGroups()) {
            for (InvitedPositionSummary activity : group.getActivities()) {
                assertEquals("/0000-0000-0000-0003/invited-position/32", activity.getPath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewInvitedPositionSummaryReadPublic() {
        InvitedPositionSummary stored = summary(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getInvitedPositionSummary(ORCID, 32L)).thenReturn(stored);

        Response r = serviceDelegator.viewInvitedPositionSummary(ORCID, 32L);
        InvitedPositionSummary element = (InvitedPositionSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/invited-position/32", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPublicInvitedPosition() {
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L))
                .thenReturn(invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition element = (InvitedPosition) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(32L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/32", element.getPath());
        assertEquals("PUBLIC Department", element.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewLimitedInvitedPosition() {
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 35L))
                .thenReturn(invitedPosition(35L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID)));

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 35L);
        assertNotNull(response);
        InvitedPosition element = (InvitedPosition) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(35L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/35", element.getPath());
        assertEquals("SELF LIMITED Department", element.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPrivateInvitedPosition() {
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 34L))
                .thenReturn(invitedPosition(34L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 34L);
        assertNotNull(response);
        InvitedPosition element = (InvitedPosition) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(34L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/34", element.getPath());
        assertEquals("PRIVATE Department", element.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateInvitedPositionWhereYouAreNotTheSource() {
        InvitedPosition stored = invitedPosition(36L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 36L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(ORCID, stored, SCOPE);

        serviceDelegator.viewInvitedPosition(ORCID, 36L);
        fail();
    }

    /**
     * The rule this proves -- that invited-position 32 cannot be read through another
     * record -- lives in a SQL WHERE clause
     * ({@code OrgAffiliationRelationDaoImpl.getOrgAffiliationRelation}), so with a
     * mocked manager all that is left here is that the delegator does not swallow
     * the exception. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest, which carries the DBUnit
     * fixture and runs in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewInvitedPositionThatDontBelongToTheUser() {
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation("4444-4444-4444-4446", 32L)).thenThrow(new NoResultException());

        serviceDelegator.viewInvitedPosition("4444-4444-4444-4446", 32L);
        fail();
    }

    @Test
    public void testViewInvitedPositions() {
        InvitedPositionSummary limited = summary(33L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1));
        InvitedPositionSummary priv = summary(34L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1));
        InvitedPositionSummary selfLimited = summary(35L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        InvitedPositionSummary pub = summary(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<InvitedPositionSummary> summaries = Arrays.asList(limited, priv, selfLimited, pub);
        when(affiliationsManagerReadOnly.getInvitedPositionSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false))
                .thenReturn(Arrays.asList(group(limited), group(priv), group(selfLimited, pub)));

        Response r = serviceDelegator.viewInvitedPositions(ORCID);
        assertNotNull(r);
        InvitedPositions elements = (InvitedPositions) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/invited-positions", elements.getPath());
        Utils.verifyLastModified(elements.getLastModifiedDate());
        assertNotNull(elements.retrieveGroups());
        assertEquals(3, elements.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;

        for (AffiliationGroup<InvitedPositionSummary> group : elements.retrieveGroups()) {
            InvitedPositionSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(33).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(34).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(35).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(32), group.getActivities().get(1).getPutCode());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
                found3 = true;
            } else {
                fail("Invalid invitedPosition found: " + element0.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_InvitedPositions() {
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L)).thenReturn(invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getInvitedPositionSummary(ORCID, 32L)).thenReturn(summary(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 33L)).thenReturn(invitedPosition(33L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getInvitedPositionSummary(ORCID, 33L)).thenReturn(summary(33L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 34L)).thenReturn(invitedPosition(34L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getInvitedPositionSummary(ORCID, 34L)).thenReturn(summary(34L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        InvitedPosition failing1 = invitedPosition(35L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        InvitedPositionSummary failingSummary1 = summary(35L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 35L)).thenReturn(failing1);
        when(affiliationsManagerReadOnly.getInvitedPositionSummary(ORCID, 35L)).thenReturn(failingSummary1);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing1, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary1, SCOPE);

        Response r = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(r);
        assertEquals(InvitedPosition.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewInvitedPositionSummary(ORCID, 32L);
        assertNotNull(r);
        assertEquals(InvitedPositionSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewInvitedPosition(ORCID, 33L);
        serviceDelegator.viewInvitedPositionSummary(ORCID, 33L);
        // Private that am the source of should work
        serviceDelegator.viewInvitedPosition(ORCID, 34L);
        serviceDelegator.viewInvitedPositionSummary(ORCID, 34L);

        // Elements the security manager refuses must come back out of the
        // delegator unwrapped, not swallowed or remapped.
        try {
            serviceDelegator.viewInvitedPosition(ORCID, 35L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewInvitedPositionSummary(ORCID, 35L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewInvitedPosition(ORCID, 35L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewInvitedPositionSummary(ORCID, 35L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddInvitedPosition() {
        InvitedPosition toCreate = (InvitedPosition) Utils.getAffiliation(AffiliationType.INVITED_POSITION);
        toCreate.setSource(clientSource(CLIENT_2));
        InvitedPosition created = invitedPosition(9999L, Visibility.PUBLIC, "My department name", clientSource(CLIENT_1));
        when(affiliationsManager.createInvitedPositionAffiliation(eq(ORCID), any(InvitedPosition.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.createInvitedPosition(ORCID, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(9999L), Utils.getPutCode(response));

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);

        // A client supplied source must never reach the manager.
        ArgumentCaptor<InvitedPosition> captor = ArgumentCaptor.forClass(InvitedPosition.class);
        verify(affiliationsManager).createInvitedPositionAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        // Remove new element
        serviceDelegator.deleteAffiliation(ORCID, 9999L);
        verify(affiliationsManager).checkSourceAndDelete(ORCID, 9999L);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddInvitedPositionsDuplicateExternalIDs() {
        InvitedPosition element = (InvitedPosition) Utils.getAffiliation(AffiliationType.INVITED_POSITION);
        element.setExternalIDs(duplicateExternalIDs());
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).createInvitedPositionAffiliation(eq(ORCID), any(InvitedPosition.class), eq(true));

        serviceDelegator.createInvitedPosition(ORCID, element);
    }

    @Test
    public void testUpdateInvitedPosition() {
        InvitedPosition stored = invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L)).thenReturn(stored);

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition element = (InvitedPosition) response.getEntity();
        assertNotNull(element);
        assertEquals("PUBLIC Department", element.getDepartmentName());
        assertEquals("PUBLIC", element.getRoleTitle());
        Utils.verifyLastModified(element.getLastModifiedDate());

        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");

        // disambiguated org is required in API v3
        DisambiguatedOrganization disambiguatedOrg = new DisambiguatedOrganization();
        disambiguatedOrg.setDisambiguatedOrganizationIdentifier("abc456");
        disambiguatedOrg.setDisambiguationSource("WDB");
        element.getOrganization().setDisambiguatedOrganization(disambiguatedOrg);

        when(affiliationsManager.updateInvitedPositionAffiliation(eq(ORCID), any(InvitedPosition.class), eq(true))).thenReturn(element);

        response = serviceDelegator.updateInvitedPosition(ORCID, 32L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        InvitedPosition updated = (InvitedPosition) response.getEntity();
        assertEquals("Updated department name", updated.getDepartmentName());
        assertEquals("The updated role title", updated.getRoleTitle());

        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<InvitedPosition> captor = ArgumentCaptor.forClass(InvitedPosition.class);
        verify(affiliationsManager).updateInvitedPositionAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateInvitedPositionYouAreNotTheSourceOf() {
        InvitedPosition stored = invitedPosition(35L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 35L)).thenReturn(stored);
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).updateInvitedPositionAffiliation(eq(ORCID), any(InvitedPosition.class), eq(true));

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 35L);
        assertNotNull(response);
        InvitedPosition element = (InvitedPosition) response.getEntity();
        assertNotNull(element);
        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");
        serviceDelegator.updateInvitedPosition(ORCID, 35L, element);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateInvitedPositionChangingVisibilityTest() {
        InvitedPosition stored = invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L)).thenReturn(stored);
        doThrow(new VisibilityMismatchException())
                .when(affiliationsManager).updateInvitedPositionAffiliation(eq(ORCID), any(InvitedPosition.class), eq(true));

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition element = (InvitedPosition) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateInvitedPosition(ORCID, 32L, element);
        fail();
    }

    @Test
    public void testUpdateInvitedPositionLeavingVisibilityNullTest() {
        InvitedPosition stored = invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L)).thenReturn(stored);
        // The manager restores the stored visibility when the request leaves it
        // null; that rule is proved in orcid-core, here we only check the
        // delegator returns what the manager produced.
        when(affiliationsManager.updateInvitedPositionAffiliation(eq(ORCID), any(InvitedPosition.class), eq(true)))
                .thenReturn(invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        assertNotNull(response);
        InvitedPosition element = (InvitedPosition) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(null);

        response = serviceDelegator.updateInvitedPosition(ORCID, 32L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        element = (InvitedPosition) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateInvitedPositionDuplicateExternalIDs() {
        InvitedPosition stored = invitedPosition(32L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(ORCID, 32L)).thenReturn(stored);
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).updateInvitedPositionAffiliation(eq(ORCID), any(InvitedPosition.class), eq(true));

        Response response = serviceDelegator.viewInvitedPosition(ORCID, 32L);
        InvitedPosition element = (InvitedPosition) response.getEntity();
        element.setExternalIDs(duplicateExternalIDs());
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        serviceDelegator.updateInvitedPosition(ORCID, 32L, element);
        fail();
    }

    @Test
    public void testDeleteInvitedPosition() {
        InvitedPosition stored = invitedPosition(1003L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation("0000-0000-0000-0002", 1003L)).thenReturn(stored).thenThrow(new NoResultException());

        Response response = serviceDelegator.viewInvitedPosition("0000-0000-0000-0002", 1003L);
        assertNotNull(response);
        assertNotNull(response.getEntity());

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1003L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(affiliationsManager).checkSourceAndDelete("0000-0000-0000-0002", 1003L);

        try {
            serviceDelegator.viewInvitedPosition("0000-0000-0000-0002", 1003L);
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteInvitedPositionYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).checkSourceAndDelete(ORCID, 35L);

        serviceDelegator.deleteAffiliation(ORCID, 35L);
        fail();
    }
}
