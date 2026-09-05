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
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the membership endpoints of the member V3 API.
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
public class MemberV3ApiServiceDelegator_MembershipsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.AFFILIATIONS_READ_LIMITED;

    private Membership membership(long putCode, Visibility visibility, String department, Source source) {
        Membership element = new Membership();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private MembershipSummary summary(long putCode, Visibility visibility, String department, Source source) {
        MembershipSummary element = new MembershipSummary();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private AffiliationGroup<MembershipSummary> group(MembershipSummary... elements) {
        AffiliationGroup<MembershipSummary> group = new AffiliationGroup<>();
        for (MembershipSummary element : elements) {
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
    public void testViewMembershipsWrongToken() {
        when(affiliationsManagerReadOnly.getMembershipSummaryList(ORCID))
                .thenReturn(Arrays.asList(summary(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewMemberships(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewMembershipWrongToken() {
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L))
                .thenReturn(membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Membership.class), eq(SCOPE));

        serviceDelegator.viewMembership(ORCID, 37L);
    }

    @Test
    public void testViewMembershipReadPublic() {
        Membership stored = membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L)).thenReturn(stored);

        Response r = serviceDelegator.viewMembership(ORCID, 37L);
        Membership element = (Membership) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/membership/37", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewMembershipSummaryWrongToken() {
        when(affiliationsManagerReadOnly.getMembershipSummary(ORCID, 37L))
                .thenReturn(summary(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(MembershipSummary.class), eq(SCOPE));

        serviceDelegator.viewMembershipSummary(ORCID, 37L);
    }

    @Test
    public void testViewMembershipsReadPublic() {
        MembershipSummary publicSummary = summary(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<MembershipSummary> summaries = Arrays.asList(publicSummary);
        when(affiliationsManagerReadOnly.getMembershipSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false)).thenReturn(Arrays.asList(group(publicSummary)));

        Response r = serviceDelegator.viewMemberships(ORCID);
        Memberships element = (Memberships) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/memberships", element.getPath());
        for (AffiliationGroup<MembershipSummary> group : element.retrieveGroups()) {
            for (MembershipSummary activity : group.getActivities()) {
                assertEquals("/0000-0000-0000-0003/membership/37", activity.getPath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewMembershipSummaryReadPublic() {
        MembershipSummary stored = summary(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getMembershipSummary(ORCID, 37L)).thenReturn(stored);

        Response r = serviceDelegator.viewMembershipSummary(ORCID, 37L);
        MembershipSummary element = (MembershipSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/membership/37", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPublicMembership() {
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L))
                .thenReturn(membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(response);
        Membership element = (Membership) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(37L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/37", element.getPath());
        assertEquals("PUBLIC Department", element.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewLimitedMembership() {
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 40L))
                .thenReturn(membership(40L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID)));

        Response response = serviceDelegator.viewMembership(ORCID, 40L);
        assertNotNull(response);
        Membership element = (Membership) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(40L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/40", element.getPath());
        assertEquals("SELF LIMITED Department", element.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPrivateMembership() {
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 39L))
                .thenReturn(membership(39L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewMembership(ORCID, 39L);
        assertNotNull(response);
        Membership element = (Membership) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(39L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/39", element.getPath());
        assertEquals("PRIVATE Department", element.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateMembershipWhereYouAreNotTheSource() {
        Membership stored = membership(41L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 41L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(ORCID, stored, SCOPE);

        serviceDelegator.viewMembership(ORCID, 41L);
        fail();
    }

    /**
     * The rule this proves -- that membership 37 cannot be read through another
     * record -- lives in a SQL WHERE clause
     * ({@code OrgAffiliationRelationDaoImpl.getOrgAffiliationRelation}), so with a
     * mocked manager all that is left here is that the delegator does not swallow
     * the exception. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest, which carries the DBUnit
     * fixture and runs in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewMembershipThatDontBelongToTheUser() {
        when(affiliationsManagerReadOnly.getMembershipAffiliation("4444-4444-4444-4446", 37L)).thenThrow(new NoResultException());

        serviceDelegator.viewMembership("4444-4444-4444-4446", 37L);
        fail();
    }

    @Test
    public void testViewMemberships() {
        MembershipSummary limited = summary(38L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1));
        MembershipSummary priv = summary(39L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1));
        MembershipSummary selfLimited = summary(40L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        MembershipSummary pub = summary(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<MembershipSummary> summaries = Arrays.asList(limited, priv, selfLimited, pub);
        when(affiliationsManagerReadOnly.getMembershipSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false))
                .thenReturn(Arrays.asList(group(limited), group(priv), group(selfLimited, pub)));

        Response r = serviceDelegator.viewMemberships(ORCID);
        assertNotNull(r);
        Memberships elements = (Memberships) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/memberships", elements.getPath());
        Utils.verifyLastModified(elements.getLastModifiedDate());
        assertNotNull(elements.retrieveGroups());
        assertEquals(3, elements.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;

        for (AffiliationGroup<MembershipSummary> group : elements.retrieveGroups()) {
            MembershipSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(38).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(39).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(40).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(37), group.getActivities().get(1).getPutCode());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
                found3 = true;
            } else {
                fail("Invalid membership found: " + element0.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_Memberships() {
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L)).thenReturn(membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getMembershipSummary(ORCID, 37L)).thenReturn(summary(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 38L)).thenReturn(membership(38L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getMembershipSummary(ORCID, 38L)).thenReturn(summary(38L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 39L)).thenReturn(membership(39L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getMembershipSummary(ORCID, 39L)).thenReturn(summary(39L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Membership failing1 = membership(40L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        MembershipSummary failingSummary1 = summary(40L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 40L)).thenReturn(failing1);
        when(affiliationsManagerReadOnly.getMembershipSummary(ORCID, 40L)).thenReturn(failingSummary1);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing1, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary1, SCOPE);

        Response r = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(r);
        assertEquals(Membership.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewMembershipSummary(ORCID, 37L);
        assertNotNull(r);
        assertEquals(MembershipSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewMembership(ORCID, 38L);
        serviceDelegator.viewMembershipSummary(ORCID, 38L);
        // Private that am the source of should work
        serviceDelegator.viewMembership(ORCID, 39L);
        serviceDelegator.viewMembershipSummary(ORCID, 39L);

        // Elements the security manager refuses must come back out of the
        // delegator unwrapped, not swallowed or remapped.
        try {
            serviceDelegator.viewMembership(ORCID, 40L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewMembershipSummary(ORCID, 40L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewMembership(ORCID, 40L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewMembershipSummary(ORCID, 40L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddMembership() {
        Membership toCreate = (Membership) Utils.getAffiliation(AffiliationType.MEMBERSHIP);
        toCreate.setSource(clientSource(CLIENT_2));
        Membership created = membership(9999L, Visibility.PUBLIC, "My department name", clientSource(CLIENT_1));
        when(affiliationsManager.createMembershipAffiliation(eq(ORCID), any(Membership.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.createMembership(ORCID, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(9999L), Utils.getPutCode(response));

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);

        // A client supplied source must never reach the manager.
        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(affiliationsManager).createMembershipAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        // Remove new element
        serviceDelegator.deleteAffiliation(ORCID, 9999L);
        verify(affiliationsManager).checkSourceAndDelete(ORCID, 9999L);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddMembershipsDuplicateExternalIDs() {
        Membership element = (Membership) Utils.getAffiliation(AffiliationType.MEMBERSHIP);
        element.setExternalIDs(duplicateExternalIDs());
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).createMembershipAffiliation(eq(ORCID), any(Membership.class), eq(true));

        serviceDelegator.createMembership(ORCID, element);
    }

    @Test
    public void testUpdateMembership() {
        Membership stored = membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L)).thenReturn(stored);

        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(response);
        Membership element = (Membership) response.getEntity();
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

        when(affiliationsManager.updateMembershipAffiliation(eq(ORCID), any(Membership.class), eq(true))).thenReturn(element);

        response = serviceDelegator.updateMembership(ORCID, 37L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Membership updated = (Membership) response.getEntity();
        assertEquals("Updated department name", updated.getDepartmentName());
        assertEquals("The updated role title", updated.getRoleTitle());

        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Membership> captor = ArgumentCaptor.forClass(Membership.class);
        verify(affiliationsManager).updateMembershipAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateMembershipYouAreNotTheSourceOf() {
        Membership stored = membership(40L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 40L)).thenReturn(stored);
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).updateMembershipAffiliation(eq(ORCID), any(Membership.class), eq(true));

        Response response = serviceDelegator.viewMembership(ORCID, 40L);
        assertNotNull(response);
        Membership element = (Membership) response.getEntity();
        assertNotNull(element);
        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");
        serviceDelegator.updateMembership(ORCID, 40L, element);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateMembershipChangingVisibilityTest() {
        Membership stored = membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L)).thenReturn(stored);
        doThrow(new VisibilityMismatchException())
                .when(affiliationsManager).updateMembershipAffiliation(eq(ORCID), any(Membership.class), eq(true));

        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(response);
        Membership element = (Membership) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateMembership(ORCID, 37L, element);
        fail();
    }

    @Test
    public void testUpdateMembershipLeavingVisibilityNullTest() {
        Membership stored = membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L)).thenReturn(stored);
        // The manager restores the stored visibility when the request leaves it
        // null; that rule is proved in orcid-core, here we only check the
        // delegator returns what the manager produced.
        when(affiliationsManager.updateMembershipAffiliation(eq(ORCID), any(Membership.class), eq(true)))
                .thenReturn(membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        assertNotNull(response);
        Membership element = (Membership) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(null);

        response = serviceDelegator.updateMembership(ORCID, 37L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        element = (Membership) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
        // Catches a delegator that sets a visibility on the element before handing it to
        // the manager: what is submitted must still carry the null the request arrived with.
        ArgumentCaptor<Membership> submitted = ArgumentCaptor.forClass(Membership.class);
        verify(affiliationsManager).updateMembershipAffiliation(eq(ORCID), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateMembershipDuplicateExternalIDs() {
        Membership stored = membership(37L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getMembershipAffiliation(ORCID, 37L)).thenReturn(stored);
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).updateMembershipAffiliation(eq(ORCID), any(Membership.class), eq(true));

        Response response = serviceDelegator.viewMembership(ORCID, 37L);
        Membership element = (Membership) response.getEntity();
        element.setExternalIDs(duplicateExternalIDs());
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        serviceDelegator.updateMembership(ORCID, 37L, element);
        fail();
    }

    @Test
    public void testDeleteMembership() {
        Membership stored = membership(1004L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getMembershipAffiliation("0000-0000-0000-0002", 1004L)).thenReturn(stored).thenThrow(new NoResultException());

        Response response = serviceDelegator.viewMembership("0000-0000-0000-0002", 1004L);
        assertNotNull(response);
        assertNotNull(response.getEntity());

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1004L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(affiliationsManager).checkSourceAndDelete("0000-0000-0000-0002", 1004L);

        try {
            serviceDelegator.viewMembership("0000-0000-0000-0002", 1004L);
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteMembershipYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).checkSourceAndDelete(ORCID, 40L);

        serviceDelegator.deleteAffiliation(ORCID, 40L);
        fail();
    }
}
