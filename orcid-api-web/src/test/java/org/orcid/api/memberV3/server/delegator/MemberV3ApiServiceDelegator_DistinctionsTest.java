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
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the distinction endpoints of the member V3 API.
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
public class MemberV3ApiServiceDelegator_DistinctionsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.AFFILIATIONS_READ_LIMITED;

    private Distinction distinction(long putCode, Visibility visibility, String department, Source source) {
        Distinction element = new Distinction();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private DistinctionSummary summary(long putCode, Visibility visibility, String department, Source source) {
        DistinctionSummary element = new DistinctionSummary();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private AffiliationGroup<DistinctionSummary> group(DistinctionSummary... elements) {
        AffiliationGroup<DistinctionSummary> group = new AffiliationGroup<>();
        for (DistinctionSummary element : elements) {
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
    public void testViewDistinctionsWrongToken() {
        when(affiliationsManagerReadOnly.getDistinctionSummaryList(ORCID))
                .thenReturn(Arrays.asList(summary(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewDistinctions(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewDistinctionWrongToken() {
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L))
                .thenReturn(distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Distinction.class), eq(SCOPE));

        serviceDelegator.viewDistinction(ORCID, 27L);
    }

    @Test
    public void testViewDistinctionReadPublic() {
        Distinction stored = distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L)).thenReturn(stored);

        Response r = serviceDelegator.viewDistinction(ORCID, 27L);
        Distinction element = (Distinction) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinction/27", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewDistinctionSummaryWrongToken() {
        when(affiliationsManagerReadOnly.getDistinctionSummary(ORCID, 27L))
                .thenReturn(summary(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(DistinctionSummary.class), eq(SCOPE));

        serviceDelegator.viewDistinctionSummary(ORCID, 27L);
    }

    @Test
    public void testViewDistinctionsReadPublic() {
        DistinctionSummary publicSummary = summary(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<DistinctionSummary> summaries = Arrays.asList(publicSummary);
        when(affiliationsManagerReadOnly.getDistinctionSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false)).thenReturn(Arrays.asList(group(publicSummary)));

        Response r = serviceDelegator.viewDistinctions(ORCID);
        Distinctions element = (Distinctions) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinctions", element.getPath());
        for (AffiliationGroup<DistinctionSummary> group : element.retrieveGroups()) {
            for (DistinctionSummary activity : group.getActivities()) {
                assertEquals("/0000-0000-0000-0003/distinction/27", activity.getPath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewDistinctionSummaryReadPublic() {
        DistinctionSummary stored = summary(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getDistinctionSummary(ORCID, 27L)).thenReturn(stored);

        Response r = serviceDelegator.viewDistinctionSummary(ORCID, 27L);
        DistinctionSummary element = (DistinctionSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinction/27", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPublicDistinction() {
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L))
                .thenReturn(distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction element = (Distinction) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(27L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/27", element.getPath());
        assertEquals("PUBLIC Department", element.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewLimitedDistinction() {
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 30L))
                .thenReturn(distinction(30L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID)));

        Response response = serviceDelegator.viewDistinction(ORCID, 30L);
        assertNotNull(response);
        Distinction element = (Distinction) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(30L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/30", element.getPath());
        assertEquals("SELF LIMITED Department", element.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPrivateDistinction() {
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 29L))
                .thenReturn(distinction(29L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewDistinction(ORCID, 29L);
        assertNotNull(response);
        Distinction element = (Distinction) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(29L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/29", element.getPath());
        assertEquals("PRIVATE Department", element.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateDistinctionWhereYouAreNotTheSource() {
        Distinction stored = distinction(31L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 31L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(ORCID, stored, SCOPE);

        serviceDelegator.viewDistinction(ORCID, 31L);
        fail();
    }

    /**
     * The rule this proves -- that distinction 27 cannot be read through another
     * record -- lives in a SQL WHERE clause
     * ({@code OrgAffiliationRelationDaoImpl.getOrgAffiliationRelation}), so with a
     * mocked manager all that is left here is that the delegator does not swallow
     * the exception. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest, which carries the DBUnit
     * fixture and runs in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewDistinctionThatDontBelongToTheUser() {
        when(affiliationsManagerReadOnly.getDistinctionAffiliation("4444-4444-4444-4446", 27L)).thenThrow(new NoResultException());

        serviceDelegator.viewDistinction("4444-4444-4444-4446", 27L);
        fail();
    }

    @Test
    public void testViewDistinctions() {
        DistinctionSummary limited = summary(28L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1));
        DistinctionSummary priv = summary(29L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1));
        DistinctionSummary selfLimited = summary(30L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        DistinctionSummary pub = summary(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<DistinctionSummary> summaries = Arrays.asList(limited, priv, selfLimited, pub);
        when(affiliationsManagerReadOnly.getDistinctionSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false))
                .thenReturn(Arrays.asList(group(limited), group(priv), group(selfLimited, pub)));

        Response r = serviceDelegator.viewDistinctions(ORCID);
        assertNotNull(r);
        Distinctions elements = (Distinctions) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/distinctions", elements.getPath());
        Utils.verifyLastModified(elements.getLastModifiedDate());
        assertNotNull(elements.retrieveGroups());
        assertEquals(3, elements.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;

        for (AffiliationGroup<DistinctionSummary> group : elements.retrieveGroups()) {
            DistinctionSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(28).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(29).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(30).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(27), group.getActivities().get(1).getPutCode());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
                found3 = true;
            } else {
                fail("Invalid distinction found: " + element0.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_Distinctions() {
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L)).thenReturn(distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getDistinctionSummary(ORCID, 27L)).thenReturn(summary(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 28L)).thenReturn(distinction(28L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getDistinctionSummary(ORCID, 28L)).thenReturn(summary(28L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 29L)).thenReturn(distinction(29L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getDistinctionSummary(ORCID, 29L)).thenReturn(summary(29L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Distinction failing1 = distinction(30L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        DistinctionSummary failingSummary1 = summary(30L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 30L)).thenReturn(failing1);
        when(affiliationsManagerReadOnly.getDistinctionSummary(ORCID, 30L)).thenReturn(failingSummary1);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing1, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary1, SCOPE);
        Distinction failing2 = distinction(31L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        DistinctionSummary failingSummary2 = summary(31L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 31L)).thenReturn(failing2);
        when(affiliationsManagerReadOnly.getDistinctionSummary(ORCID, 31L)).thenReturn(failingSummary2);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing2, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary2, SCOPE);

        Response r = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(r);
        assertEquals(Distinction.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewDistinctionSummary(ORCID, 27L);
        assertNotNull(r);
        assertEquals(DistinctionSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewDistinction(ORCID, 28L);
        serviceDelegator.viewDistinctionSummary(ORCID, 28L);
        // Private that am the source of should work
        serviceDelegator.viewDistinction(ORCID, 29L);
        serviceDelegator.viewDistinctionSummary(ORCID, 29L);

        // Elements the security manager refuses must come back out of the
        // delegator unwrapped, not swallowed or remapped.
        try {
            serviceDelegator.viewDistinction(ORCID, 30L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewDistinctionSummary(ORCID, 30L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewDistinction(ORCID, 31L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewDistinctionSummary(ORCID, 31L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddDistinction() {
        Distinction toCreate = (Distinction) Utils.getAffiliation(AffiliationType.DISTINCTION);
        toCreate.setSource(clientSource(CLIENT_2));
        Distinction created = distinction(9999L, Visibility.PUBLIC, "My department name", clientSource(CLIENT_1));
        when(affiliationsManager.createDistinctionAffiliation(eq(ORCID), any(Distinction.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.createDistinction(ORCID, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(9999L), Utils.getPutCode(response));

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);

        // A client supplied source must never reach the manager.
        ArgumentCaptor<Distinction> captor = ArgumentCaptor.forClass(Distinction.class);
        verify(affiliationsManager).createDistinctionAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        // Remove new element
        serviceDelegator.deleteAffiliation(ORCID, 9999L);
        verify(affiliationsManager).checkSourceAndDelete(ORCID, 9999L);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddDistinctionsDuplicateExternalIDs() {
        Distinction element = (Distinction) Utils.getAffiliation(AffiliationType.DISTINCTION);
        element.setExternalIDs(duplicateExternalIDs());
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).createDistinctionAffiliation(eq(ORCID), any(Distinction.class), eq(true));

        serviceDelegator.createDistinction(ORCID, element);
    }

    @Test
    public void testUpdateDistinction() {
        Distinction stored = distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L)).thenReturn(stored);

        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction element = (Distinction) response.getEntity();
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

        when(affiliationsManager.updateDistinctionAffiliation(eq(ORCID), any(Distinction.class), eq(true))).thenReturn(element);

        response = serviceDelegator.updateDistinction(ORCID, 27L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Distinction updated = (Distinction) response.getEntity();
        assertEquals("Updated department name", updated.getDepartmentName());
        assertEquals("The updated role title", updated.getRoleTitle());

        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Distinction> captor = ArgumentCaptor.forClass(Distinction.class);
        verify(affiliationsManager).updateDistinctionAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateDistinctionYouAreNotTheSourceOf() {
        Distinction stored = distinction(30L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 30L)).thenReturn(stored);
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).updateDistinctionAffiliation(eq(ORCID), any(Distinction.class), eq(true));

        Response response = serviceDelegator.viewDistinction(ORCID, 30L);
        assertNotNull(response);
        Distinction element = (Distinction) response.getEntity();
        assertNotNull(element);
        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");
        serviceDelegator.updateDistinction(ORCID, 30L, element);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateDistinctionChangingVisibilityTest() {
        Distinction stored = distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L)).thenReturn(stored);
        doThrow(new VisibilityMismatchException())
                .when(affiliationsManager).updateDistinctionAffiliation(eq(ORCID), any(Distinction.class), eq(true));

        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction element = (Distinction) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateDistinction(ORCID, 27L, element);
        fail();
    }

    @Test
    public void testUpdateDistinctionLeavingVisibilityNullTest() {
        Distinction stored = distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L)).thenReturn(stored);
        // The manager restores the stored visibility when the request leaves it
        // null; that rule is proved in orcid-core, here we only check the
        // delegator returns what the manager produced.
        when(affiliationsManager.updateDistinctionAffiliation(eq(ORCID), any(Distinction.class), eq(true)))
                .thenReturn(distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction element = (Distinction) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(null);

        response = serviceDelegator.updateDistinction(ORCID, 27L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        element = (Distinction) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateDistinctionDuplicateExternalIDs() {
        Distinction stored = distinction(27L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(ORCID, 27L)).thenReturn(stored);
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).updateDistinctionAffiliation(eq(ORCID), any(Distinction.class), eq(true));

        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        Distinction element = (Distinction) response.getEntity();
        element.setExternalIDs(duplicateExternalIDs());
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        serviceDelegator.updateDistinction(ORCID, 27L, element);
        fail();
    }

    @Test
    public void testDeleteDistinction() {
        Distinction stored = distinction(1000L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getDistinctionAffiliation("0000-0000-0000-0002", 1000L)).thenReturn(stored).thenThrow(new NoResultException());

        Response response = serviceDelegator.viewDistinction("0000-0000-0000-0002", 1000L);
        assertNotNull(response);
        assertNotNull(response.getEntity());

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1000L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(affiliationsManager).checkSourceAndDelete("0000-0000-0000-0002", 1000L);

        try {
            serviceDelegator.viewDistinction("0000-0000-0000-0002", 1000L);
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteDistinctionYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).checkSourceAndDelete(ORCID, 30L);

        serviceDelegator.deleteAffiliation(ORCID, 30L);
        fail();
    }
}
