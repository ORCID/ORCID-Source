package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
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
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.test.helper.Utils;

/**
 * The employment endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * The delegator's own behaviour with an employment is: fetch it, guard it, set its
 * path, clean the empty organization fields, resolve its source name. That is
 * what is asserted here. Visibility filtering is
 * {@code OrcidSecurityManager_generalTest}'s, the source-ownership rule is
 * {@code AffiliationsManagerImpl}'s, and "an affiliation of another record is not
 * readable or deletable" is a predicate in {@code OrgAffiliationRelationDaoImpl}'s
 * SQL.
 */
public class MemberV2ApiServiceDelegator_EmploymentsTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4443";
    private static final String MY_ORCID = "4444-4444-4444-4442";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentsWrongToken() {
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID)).thenReturn(new ArrayList<>(Arrays.asList(employmentSummary(20L, Visibility.PUBLIC))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(),
                eq(ScopePathType.AFFILIATIONS_READ_LIMITED));

        try {
            serviceDelegator.viewEmployments(ORCID);
        } finally {
            verifyNoInteractions(sourceNameCacheManager);
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentWrongToken() {
        Employment employment = employment(20L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 20L)).thenReturn(employment);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, employment,
                ScopePathType.AFFILIATIONS_READ_LIMITED);

        try {
            serviceDelegator.viewEmployment(ORCID, 20L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", employment.getPath());
        }
    }

    @Test
    public void testViewEmploymentReadPublic() {
        Employment employment = employment(20L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 20L)).thenReturn(employment);

        Response r = serviceDelegator.viewEmployment(ORCID, 20L);

        Employment element = (Employment) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/20", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, employment, ScopePathType.AFFILIATIONS_READ_LIMITED);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentSummaryWrongToken() {
        EmploymentSummary summary = employmentSummary(20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 20L)).thenReturn(summary);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, summary,
                ScopePathType.AFFILIATIONS_READ_LIMITED);

        try {
            serviceDelegator.viewEmploymentSummary(ORCID, 20L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", summary.getPath());
        }
    }

    @Test
    public void testViewEmploymentsReadPublic() {
        List<EmploymentSummary> stored = new ArrayList<>(Arrays.asList(employmentSummary(20L, Visibility.PUBLIC)));
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID)).thenReturn(stored);

        Response r = serviceDelegator.viewEmployments(ORCID);

        Employments element = (Employments) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employments", element.getPath());
        assertEquals("/0000-0000-0000-0003/employment/20", element.getSummaries().get(0).getPath());
        // the cached list must be copied before it is handed to a filter that
        // edits in place
        ArgumentCaptor<List<EmploymentSummary>> filtered = summaryListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), filtered.capture(), eq(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertNotSame(stored, filtered.getValue());
    }

    @Test
    public void testViewEmploymentSummaryReadPublic() {
        EmploymentSummary summary = employmentSummary(20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEmploymentSummary(ORCID, 20L)).thenReturn(summary);

        Response r = serviceDelegator.viewEmploymentSummary(ORCID, 20L);

        EmploymentSummary element = (EmploymentSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/20", element.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary, ScopePathType.AFFILIATIONS_READ_LIMITED);
    }

    @Test
    public void testViewEmployment() {
        assertViewEmploymentDecorated(20L, Visibility.PUBLIC, clientSource(CLIENT_1));
    }

    @Test
    public void testViewLimitedEmployment() {
        assertViewEmploymentDecorated(21L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivateEmployment() {
        assertViewEmploymentDecorated(22L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEmploymentWhereYouAreNotTheSource() {
        Employment employment = employment(23L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(OTHER_ORCID, 23L)).thenReturn(employment);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, employment, ScopePathType.AFFILIATIONS_READ_LIMITED);

        serviceDelegator.viewEmployment(OTHER_ORCID, 23L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEmploymentThatDontBelongToTheUser() {
        // The (orcid, id) predicate is in OrgAffiliationRelationDaoImpl's query.
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(OTHER_ORCID, 1L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewEmployment(OTHER_ORCID, 1L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testViewEmployments() {
        List<EmploymentSummary> stored = new ArrayList<>(
                Arrays.asList(employmentSummary(20L, Visibility.PUBLIC), employmentSummary(21L, Visibility.LIMITED), employmentSummary(22L, Visibility.PRIVATE)));
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID)).thenReturn(stored);

        Response response = serviceDelegator.viewEmployments(ORCID);

        assertNotNull(response);
        Employments employments = (Employments) response.getEntity();
        assertNotNull(employments);
        assertEquals("/0000-0000-0000-0003/employments", employments.getPath());
        Utils.verifyLastModified(employments.getLastModifiedDate());
        assertEquals(3, employments.getSummaries().size());
        for (EmploymentSummary summary : employments.getSummaries()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertEquals("/0000-0000-0000-0003/employment/" + summary.getPutCode(), summary.getPath());
            assertEquals(CLIENT_1_NAME, summary.getSource().getSourceName().getContent());
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.AFFILIATIONS_READ_LIMITED));
    }

    @Test
    public void testReadPublicScope_Employments() {
        // Refused per element, never with a blanket matcher: a matcher that
        // refused everything would also refuse 20 and 21, and the positive half
        // of this test would prove nothing.
        Employment twenty = employment(20L, Visibility.PUBLIC, clientSource(CLIENT_1));
        Employment twentyOne = employment(21L, Visibility.LIMITED, clientSource(CLIENT_1));
        Employment twentyTwo = employment(22L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 20L)).thenReturn(twenty);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 21L)).thenReturn(twentyOne);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, 22L)).thenReturn(twentyTwo);
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(ORCID))
                .thenReturn(new ArrayList<>(Arrays.asList(employmentSummary(20L, Visibility.PUBLIC), employmentSummary(21L, Visibility.LIMITED))));
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, twentyTwo, ScopePathType.AFFILIATIONS_READ_LIMITED);

        Response r = serviceDelegator.viewEmployments(ORCID);
        assertNotNull(r);
        assertEquals(Employments.class.getName(), r.getEntity().getClass().getName());
        assertEquals(2, ((Employments) r.getEntity()).getSummaries().size());

        r = serviceDelegator.viewEmployment(ORCID, 20L);
        assertNotNull(r);
        assertEquals(Employment.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewEmployment(ORCID, 21L);

        try {
            // Private am not the source should fail
            serviceDelegator.viewEmployment(ORCID, 22L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddEmployment() {
        Employment created = employment(100L, Visibility.LIMITED, clientSource(CLIENT_1));
        created.setDepartmentName("My department name");
        when(affiliationsManager.createEmploymentAffiliation(eq(MY_ORCID), any(Employment.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createEmployment(MY_ORCID, Utils.getEmployment());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Employment> submitted = ArgumentCaptor.forClass(Employment.class);
        verify(affiliationsManager).createEmploymentAffiliation(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
        assertEquals("My department name", submitted.getValue().getDepartmentName());
    }

    @Test
    public void testUpdateEmployment() {
        Employment employment = employment(3L, Visibility.PUBLIC, clientSource(CLIENT_1));
        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");
        Employment updated = employment(3L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setDepartmentName("Updated department name");
        updated.setRoleTitle("The updated role title");
        when(affiliationsManager.updateEmploymentAffiliation(eq(OTHER_ORCID), any(Employment.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateEmployment(OTHER_ORCID, 3L, employment);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Employment returned = (Employment) response.getEntity();
        assertEquals("Updated department name", returned.getDepartmentName());
        assertEquals("The updated role title", returned.getRoleTitle());
        verify(orcidSecurityManager).checkClientAccessAndScopes(OTHER_ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Employment> submitted = ArgumentCaptor.forClass(Employment.class);
        verify(affiliationsManager).updateEmploymentAffiliation(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEmploymentYouAreNotTheSourceOf() {
        // AffiliationsManagerImpl calls orcidSecurityManager.checkSource on the
        // stored entity; the rule belongs to that manager's tests.
        Employment employment = employment(1L, Visibility.PUBLIC, clientSource(CLIENT_2));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "employment"))).when(affiliationsManager)
                .updateEmploymentAffiliation(eq(MY_ORCID), any(Employment.class), anyBoolean());

        serviceDelegator.updateEmployment(MY_ORCID, 1L, employment);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEmploymentChangingVisibilityTest() {
        Employment employment = employment(3L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(affiliationsManager).updateEmploymentAffiliation(eq(OTHER_ORCID), any(Employment.class), anyBoolean());

        serviceDelegator.updateEmployment(OTHER_ORCID, 3L, employment);
        fail();
    }

    @Test
    public void testUpdateEmploymentLeavingVisibilityNullTest() {
        Employment employment = employment(3L, null, clientSource(CLIENT_1));
        Employment updated = employment(3L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(affiliationsManager.updateEmploymentAffiliation(eq(OTHER_ORCID), any(Employment.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateEmployment(OTHER_ORCID, 3L, employment);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((Employment) response.getEntity()).getVisibility());
        ArgumentCaptor<Employment> submitted = ArgumentCaptor.forClass(Employment.class);
        verify(affiliationsManager).updateEmploymentAffiliation(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testDeleteEmployment() {
        String orcid = "4444-4444-4444-4447";
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(orcid, 12L)).thenReturn(employment(12L, Visibility.PUBLIC, clientSource(CLIENT_1)))
                .thenThrow(new NoResultException());

        Response response = serviceDelegator.viewEmployment(orcid, 12L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);

        response = serviceDelegator.deleteAffiliation(orcid, 12L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(orcid, ScopePathType.AFFILIATIONS_UPDATE);
        verify(affiliationsManager).checkSourceAndDelete(orcid, 12L);

        serviceDelegator.viewEmployment(orcid, 12L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEmploymentYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "employment"))).when(affiliationsManager)
                .checkSourceAndDelete("4444-4444-4444-4446", 9L);

        serviceDelegator.deleteAffiliation("4444-4444-4444-4446", 9L);
        fail();
    }

    // ------------------------------------------------------------- helpers

    private void assertViewEmploymentDecorated(long putCode, Visibility visibility, Source source) {
        Employment employment = employment(putCode, visibility, source);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(ORCID, putCode)).thenReturn(employment);

        Response response = serviceDelegator.viewEmployment(ORCID, putCode);

        assertNotNull(response);
        Employment returned = (Employment) response.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003/employment/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(ORCID, employment, ScopePathType.AFFILIATIONS_READ_LIMITED);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<EmploymentSummary>> summaryListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private Employment employment(Long putCode, Visibility visibility, Source source) {
        Employment employment = new Employment();
        employment.setPutCode(putCode);
        employment.setDepartmentName("A Department");
        employment.setRoleTitle("Employee");
        employment.setOrganization(organization());
        employment.setVisibility(visibility);
        employment.setSource(source);
        employment.setCreatedDate(createdDate());
        employment.setLastModifiedDate(lastModified());
        return employment;
    }

    private EmploymentSummary employmentSummary(Long putCode, Visibility visibility) {
        EmploymentSummary summary = new EmploymentSummary();
        summary.setPutCode(putCode);
        summary.setDepartmentName("A Department");
        summary.setRoleTitle("Employee");
        summary.setOrganization(organization());
        summary.setVisibility(visibility);
        summary.setSource(clientSource(CLIENT_1));
        summary.setCreatedDate(createdDate());
        summary.setLastModifiedDate(lastModified());
        return summary;
    }
}
