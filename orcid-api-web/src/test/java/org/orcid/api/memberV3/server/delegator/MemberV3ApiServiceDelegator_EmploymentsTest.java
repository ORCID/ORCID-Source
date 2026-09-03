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
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the employment endpoints of the member V3 API.
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
public class MemberV3ApiServiceDelegator_EmploymentsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.AFFILIATIONS_READ_LIMITED;

    private Employment employment(long putCode, Visibility visibility, String department, Source source) {
        Employment element = new Employment();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private EmploymentSummary summary(long putCode, Visibility visibility, String department, Source source) {
        EmploymentSummary element = new EmploymentSummary();
        element.setPutCode(putCode);
        element.setVisibility(visibility);
        element.setDepartmentName(department);
        element.setRoleTitle(visibility.value().toUpperCase());
        element.setOrganization(Utils.getOrganization());
        element.setLastModifiedDate(lastModified());
        element.setSource(source);
        return element;
    }

    private AffiliationGroup<EmploymentSummary> group(EmploymentSummary... elements) {
        AffiliationGroup<EmploymentSummary> group = new AffiliationGroup<>();
        for (EmploymentSummary element : elements) {
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
    public void testViewEmploymentsWrongToken() {
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID))
                .thenReturn(Arrays.asList(summary(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));

        serviceDelegator.viewEmployments(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentWrongToken() {
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L))
                .thenReturn(employment(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Employment.class), eq(SCOPE));

        serviceDelegator.viewEmployment(ORCID, 17L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentSummaryWrongToken() {
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 17L))
                .thenReturn(summary(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(EmploymentSummary.class), eq(SCOPE));

        serviceDelegator.viewEmploymentSummary(ORCID, 17L);
    }

    @Test
    public void testViewEmploymentsReadPublic() {
        EmploymentSummary publicSummary = summary(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<EmploymentSummary> summaries = Arrays.asList(publicSummary);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false)).thenReturn(Arrays.asList(group(publicSummary)));

        Response r = serviceDelegator.viewEmployments(ORCID);
        Employments element = (Employments) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employments", element.getPath());
        for (AffiliationGroup<EmploymentSummary> group : element.retrieveGroups()) {
            for (EmploymentSummary activity : group.getActivities()) {
                assertEquals("/0000-0000-0000-0003/employment/17", activity.getPath());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testViewEmploymentReadPublic() {
        Employment stored = employment(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(stored);

        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        Employment element = (Employment) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/17", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewEmploymentSummaryReadPublic() {
        EmploymentSummary stored = summary(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 17L)).thenReturn(stored);

        Response r = serviceDelegator.viewEmploymentSummary(ORCID, 17L);
        EmploymentSummary element = (EmploymentSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/17", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewEmployment() {
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 19L))
                .thenReturn(employment(19L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewEmployment(ORCID, 19L);
        assertNotNull(response);
        Employment element = (Employment) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(19L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/19", element.getPath());
        assertEquals("PRIVATE Department", element.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewLimitedEmployment() {
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 18L))
                .thenReturn(employment(18L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewEmployment(ORCID, 18L);
        assertNotNull(response);
        Employment element = (Employment) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(18L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/18", element.getPath());
        assertEquals("LIMITED Department", element.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test
    public void testViewPrivateEmployment() {
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 19L))
                .thenReturn(employment(19L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewEmployment(ORCID, 19L);
        assertNotNull(response);
        Employment element = (Employment) response.getEntity();
        assertNotNull(element);
        Utils.verifyLastModified(element.getLastModifiedDate());
        assertEquals(Long.valueOf(19L), element.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/19", element.getPath());
        assertEquals("PRIVATE Department", element.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), element.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(ORCID, element, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEmploymentWhereYouAreNotTheSource() {
        Employment stored = employment(24L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 24L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(ORCID, stored, SCOPE);

        serviceDelegator.viewEmployment(ORCID, 24L);
        fail();
    }

    /**
     * The rule this proves -- that employment 4 cannot be read through another
     * record -- lives in a SQL WHERE clause
     * ({@code OrgAffiliationRelationDaoImpl.getOrgAffiliationRelation}), so with a
     * mocked manager all that is left here is that the delegator does not swallow
     * the exception. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest, which carries the DBUnit
     * fixture and runs in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewEmploymentThatDontBelongToTheUser() {
        when(affiliationsManagerReadOnly.getEmploymentAffiliation("4444-4444-4444-4446", 4L)).thenThrow(new NoResultException());

        serviceDelegator.viewEmployment("4444-4444-4444-4446", 4L);
        fail();
    }

    @Test
    public void testViewEmployments() {
        EmploymentSummary limited = summary(18L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1));
        EmploymentSummary priv = summary(19L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1));
        EmploymentSummary selfLimited = summary(23L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        EmploymentSummary pub = summary(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        List<EmploymentSummary> summaries = Arrays.asList(limited, priv, selfLimited, pub);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID)).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(summaries, false))
                .thenReturn(Arrays.asList(group(limited), group(priv), group(selfLimited, pub)));

        Response r = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(r);
        Employments elements = (Employments) r.getEntity();
        assertNotNull(elements);
        assertEquals("/0000-0000-0000-0003/employments", elements.getPath());
        Utils.verifyLastModified(elements.getLastModifiedDate());
        assertNotNull(elements.retrieveGroups());
        assertEquals(3, elements.retrieveGroups().size());
        boolean found1 = false, found2 = false, found3 = false;

        for (AffiliationGroup<EmploymentSummary> group : elements.retrieveGroups()) {
            EmploymentSummary element0 = group.getActivities().get(0);
            Utils.verifyLastModified(element0.getLastModifiedDate());
            if (Long.valueOf(18).equals(element0.getPutCode())) {
                assertEquals("LIMITED Department", element0.getDepartmentName());
                found1 = true;
            } else if (Long.valueOf(19).equals(element0.getPutCode())) {
                assertEquals("PRIVATE Department", element0.getDepartmentName());
                found2 = true;
            } else if (Long.valueOf(23).equals(element0.getPutCode())) {
                assertEquals("SELF LIMITED Department", element0.getDepartmentName());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(17), group.getActivities().get(1).getPutCode());
                assertEquals("PUBLIC Department", group.getActivities().get(1).getDepartmentName());
                found3 = true;
            } else {
                fail("Invalid employment found: " + element0.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_Employments() {
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(employment(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 17L)).thenReturn(summary(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 18L)).thenReturn(employment(18L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 18L)).thenReturn(summary(18L, Visibility.LIMITED, "LIMITED Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 19L)).thenReturn(employment(19L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 19L)).thenReturn(summary(19L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_1)));

        Employment failing1 = employment(23L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        EmploymentSummary failingSummary1 = summary(23L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 23L)).thenReturn(failing1);
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 23L)).thenReturn(failingSummary1);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing1, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary1, SCOPE);
        Employment failing2 = employment(24L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        EmploymentSummary failingSummary2 = summary(24L, Visibility.PRIVATE, "PRIVATE Department", clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 24L)).thenReturn(failing2);
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 24L)).thenReturn(failingSummary2);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failing2, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, failingSummary2, SCOPE);

        Response r = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(r);
        assertEquals(Employment.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewEmploymentSummary(ORCID, 17L);
        assertNotNull(r);
        assertEquals(EmploymentSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewEmployment(ORCID, 18L);
        serviceDelegator.viewEmploymentSummary(ORCID, 18L);
        // Private that am the source of should work
        serviceDelegator.viewEmployment(ORCID, 19L);
        serviceDelegator.viewEmploymentSummary(ORCID, 19L);

        // Elements the security manager refuses must come back out of the
        // delegator unwrapped, not swallowed or remapped.
        try {
            serviceDelegator.viewEmployment(ORCID, 23L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 23L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEmployment(ORCID, 24L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 24L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddEmployment() {
        Employment toCreate = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        toCreate.setSource(clientSource(CLIENT_2));
        Employment created = employment(9999L, Visibility.PUBLIC, "My department name", clientSource(CLIENT_1));
        when(affiliationsManager.createEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(true))).thenReturn(created);

        Response response = serviceDelegator.createEmployment(ORCID, toCreate);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(9999L), Utils.getPutCode(response));

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);

        // A client supplied source must never reach the manager.
        ArgumentCaptor<Employment> captor = ArgumentCaptor.forClass(Employment.class);
        verify(affiliationsManager).createEmploymentAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        // Remove new element
        serviceDelegator.deleteAffiliation(ORCID, 9999L);
        verify(affiliationsManager).checkSourceAndDelete(ORCID, 9999L);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddEmploymentsDuplicateExternalIDs() {
        Employment element = (Employment) Utils.getAffiliation(AffiliationType.EMPLOYMENT);
        element.setExternalIDs(duplicateExternalIDs());
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).createEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(true));

        serviceDelegator.createEmployment(ORCID, element);
    }

    @Test
    public void testUpdateEmployment() {
        Employment stored = employment(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(stored);

        Response response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment element = (Employment) response.getEntity();
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

        when(affiliationsManager.updateEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(true))).thenReturn(element);

        response = serviceDelegator.updateEmployment(ORCID, 17L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Employment updated = (Employment) response.getEntity();
        assertEquals("Updated department name", updated.getDepartmentName());
        assertEquals("The updated role title", updated.getRoleTitle());

        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Employment> captor = ArgumentCaptor.forClass(Employment.class);
        verify(affiliationsManager).updateEmploymentAffiliation(eq(ORCID), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testUpdateEmploymentDuplicateExternalIDs() {
        Employment stored = employment(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(stored);
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(affiliationsManager).updateEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(true));

        Response response = serviceDelegator.viewEmployment(ORCID, 17L);
        Employment element = (Employment) response.getEntity();
        element.setExternalIDs(duplicateExternalIDs());
        assertEquals(HttpStatus.SC_OK, response.getStatus());

        serviceDelegator.updateEmployment(ORCID, 17L, element);
        fail();
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEmploymentYouAreNotTheSourceOf() {
        Employment stored = employment(23L, Visibility.LIMITED, "SELF LIMITED Department", userSource(ORCID));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 23L)).thenReturn(stored);
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).updateEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(true));

        Response response = serviceDelegator.viewEmployment(ORCID, 23L);
        assertNotNull(response);
        Employment element = (Employment) response.getEntity();
        assertNotNull(element);
        element.setDepartmentName("Updated department name");
        element.setRoleTitle("The updated role title");
        serviceDelegator.updateEmployment(ORCID, 23L, element);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEmploymentChangingVisibilityTest() {
        Employment stored = employment(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(stored);
        doThrow(new VisibilityMismatchException())
                .when(affiliationsManager).updateEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(true));

        Response response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment element = (Employment) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updateEmployment(ORCID, 17L, element);
        fail();
    }

    @Test
    public void testUpdateEmploymentLeavingVisibilityNullTest() {
        Employment stored = employment(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 17L)).thenReturn(stored);
        // The manager restores the stored visibility when the request leaves it
        // null; that rule is proved in orcid-core, here we only check the
        // delegator returns what the manager produced.
        when(affiliationsManager.updateEmploymentAffiliation(eq(ORCID), any(Employment.class), eq(true)))
                .thenReturn(employment(17L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewEmployment(ORCID, 17L);
        assertNotNull(response);
        Employment element = (Employment) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());

        element.setVisibility(null);

        response = serviceDelegator.updateEmployment(ORCID, 17L, element);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        element = (Employment) response.getEntity();
        assertNotNull(element);
        assertEquals(Visibility.PUBLIC, element.getVisibility());
    }

    @Test
    public void testDeleteEmployment() {
        Employment stored = employment(1002L, Visibility.PUBLIC, "PUBLIC Department", clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation("0000-0000-0000-0002", 1002L)).thenReturn(stored).thenThrow(new NoResultException());

        Response response = serviceDelegator.viewEmployment("0000-0000-0000-0002", 1002L);
        assertNotNull(response);
        assertNotNull(response.getEntity());

        response = serviceDelegator.deleteAffiliation("0000-0000-0000-0002", 1002L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(affiliationsManager).checkSourceAndDelete("0000-0000-0000-0002", 1002L);

        try {
            serviceDelegator.viewEmployment("0000-0000-0000-0002", 1002L);
            fail();
        } catch (NoResultException nre) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEmploymentYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(affiliationsManager).checkSourceAndDelete(ORCID, 23L);

        serviceDelegator.deleteAffiliation(ORCID, 23L);
        fail();
    }
}
