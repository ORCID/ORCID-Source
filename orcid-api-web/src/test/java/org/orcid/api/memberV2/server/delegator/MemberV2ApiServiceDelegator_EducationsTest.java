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
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.test.helper.Utils;

/**
 * The education endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * The delegator's own behaviour with an education is: fetch it, guard it, set its
 * path, clean the empty organization fields, resolve its source name. That is
 * what is asserted here. Visibility filtering is
 * {@code OrcidSecurityManager_generalTest}'s, the source-ownership rule is
 * {@code AffiliationsManagerImpl}'s, and "an affiliation of another record is not
 * readable or deletable" is a predicate in {@code OrgAffiliationRelationDaoImpl}'s
 * SQL.
 */
public class MemberV2ApiServiceDelegator_EducationsTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4443";
    private static final String MY_ORCID = "4444-4444-4444-4442";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationsWrongToken() {
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID)).thenReturn(new ArrayList<>(Arrays.asList(educationSummary(20L, Visibility.PUBLIC))));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(),
                eq(ScopePathType.AFFILIATIONS_READ_LIMITED));

        try {
            serviceDelegator.viewEducations(ORCID);
        } finally {
            verifyNoInteractions(sourceNameCacheManager);
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationWrongToken() {
        Education education = education(20L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(education);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, education,
                ScopePathType.AFFILIATIONS_READ_LIMITED);

        try {
            serviceDelegator.viewEducation(ORCID, 20L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", education.getPath());
        }
    }

    @Test
    public void testViewEducationReadPublic() {
        Education education = education(20L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(education);

        Response r = serviceDelegator.viewEducation(ORCID, 20L);

        Education element = (Education) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/education/20", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, education, ScopePathType.AFFILIATIONS_READ_LIMITED);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationSummaryWrongToken() {
        EducationSummary summary = educationSummary(20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 20L)).thenReturn(summary);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, summary,
                ScopePathType.AFFILIATIONS_READ_LIMITED);

        try {
            serviceDelegator.viewEducationSummary(ORCID, 20L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", summary.getPath());
        }
    }

    @Test
    public void testViewEducationsReadPublic() {
        List<EducationSummary> stored = new ArrayList<>(Arrays.asList(educationSummary(20L, Visibility.PUBLIC)));
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID)).thenReturn(stored);

        Response r = serviceDelegator.viewEducations(ORCID);

        Educations element = (Educations) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/educations", element.getPath());
        assertEquals("/0000-0000-0000-0003/education/20", element.getSummaries().get(0).getPath());
        // the cached list must be copied before it is handed to a filter that
        // edits in place
        ArgumentCaptor<List<EducationSummary>> filtered = summaryListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), filtered.capture(), eq(ScopePathType.AFFILIATIONS_READ_LIMITED));
        assertNotSame(stored, filtered.getValue());
    }

    @Test
    public void testViewEducationSummaryReadPublic() {
        EducationSummary summary = educationSummary(20L, Visibility.PUBLIC);
        when(affiliationsManagerReadOnly.getEducationSummary(ORCID, 20L)).thenReturn(summary);

        Response r = serviceDelegator.viewEducationSummary(ORCID, 20L);

        EducationSummary element = (EducationSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/education/20", element.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary, ScopePathType.AFFILIATIONS_READ_LIMITED);
    }

    @Test
    public void testViewPublicEducation() {
        assertViewEducationDecorated(20L, Visibility.PUBLIC, clientSource(CLIENT_1));
    }

    @Test
    public void testViewLimitedEducation() {
        assertViewEducationDecorated(21L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivateEducation() {
        assertViewEducationDecorated(22L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEducationWhereYouAreNotTheSource() {
        Education education = education(23L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getEducationAffiliation(OTHER_ORCID, 23L)).thenReturn(education);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, education, ScopePathType.AFFILIATIONS_READ_LIMITED);

        serviceDelegator.viewEducation(OTHER_ORCID, 23L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEducationThatDontBelongToTheUser() {
        // The (orcid, id) predicate is in OrgAffiliationRelationDaoImpl's query.
        when(affiliationsManagerReadOnly.getEducationAffiliation(OTHER_ORCID, 1L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewEducation(OTHER_ORCID, 1L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testViewEducations() {
        List<EducationSummary> stored = new ArrayList<>(
                Arrays.asList(educationSummary(20L, Visibility.PUBLIC), educationSummary(21L, Visibility.LIMITED), educationSummary(22L, Visibility.PRIVATE)));
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID)).thenReturn(stored);

        Response response = serviceDelegator.viewEducations(ORCID);

        assertNotNull(response);
        Educations educations = (Educations) response.getEntity();
        assertNotNull(educations);
        assertEquals("/0000-0000-0000-0003/educations", educations.getPath());
        Utils.verifyLastModified(educations.getLastModifiedDate());
        assertEquals(3, educations.getSummaries().size());
        for (EducationSummary summary : educations.getSummaries()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertEquals("/0000-0000-0000-0003/education/" + summary.getPutCode(), summary.getPath());
            assertEquals(CLIENT_1_NAME, summary.getSource().getSourceName().getContent());
        }
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(ScopePathType.AFFILIATIONS_READ_LIMITED));
    }

    @Test
    public void testReadPublicScope_Educations() {
        // Refused per element, never with a blanket matcher: a matcher that
        // refused everything would also refuse 20 and 21, and the positive half
        // of this test would prove nothing.
        Education twenty = education(20L, Visibility.PUBLIC, clientSource(CLIENT_1));
        Education twentyOne = education(21L, Visibility.LIMITED, clientSource(CLIENT_1));
        Education twentyTwo = education(22L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 20L)).thenReturn(twenty);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 21L)).thenReturn(twentyOne);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, 22L)).thenReturn(twentyTwo);
        when(affiliationsManagerReadOnly.getEducationSummaryList(ORCID))
                .thenReturn(new ArrayList<>(Arrays.asList(educationSummary(20L, Visibility.PUBLIC), educationSummary(21L, Visibility.LIMITED))));
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, twentyTwo, ScopePathType.AFFILIATIONS_READ_LIMITED);

        Response r = serviceDelegator.viewEducations(ORCID);
        assertNotNull(r);
        assertEquals(Educations.class.getName(), r.getEntity().getClass().getName());
        assertEquals(2, ((Educations) r.getEntity()).getSummaries().size());

        r = serviceDelegator.viewEducation(ORCID, 20L);
        assertNotNull(r);
        assertEquals(Education.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewEducation(ORCID, 21L);

        try {
            // Private am not the source should fail
            serviceDelegator.viewEducation(ORCID, 22L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testAddEducation() {
        Education created = education(100L, Visibility.LIMITED, clientSource(CLIENT_1));
        created.setDepartmentName("My department name");
        when(affiliationsManager.createEducationAffiliation(eq(MY_ORCID), any(Education.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createEducation(MY_ORCID, Utils.getEducation());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Education> submitted = ArgumentCaptor.forClass(Education.class);
        verify(affiliationsManager).createEducationAffiliation(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
        assertEquals("My department name", submitted.getValue().getDepartmentName());
    }

    @Test
    public void testUpdateEducation() {
        Education education = education(3L, Visibility.PUBLIC, clientSource(CLIENT_1));
        education.setDepartmentName("Updated department name");
        education.setRoleTitle("The updated role title");
        Education updated = education(3L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setDepartmentName("Updated department name");
        updated.setRoleTitle("The updated role title");
        when(affiliationsManager.updateEducationAffiliation(eq(OTHER_ORCID), any(Education.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateEducation(OTHER_ORCID, 3L, education);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Education returned = (Education) response.getEntity();
        assertEquals("Updated department name", returned.getDepartmentName());
        assertEquals("The updated role title", returned.getRoleTitle());
        verify(orcidSecurityManager).checkClientAccessAndScopes(OTHER_ORCID, ScopePathType.AFFILIATIONS_UPDATE);
        ArgumentCaptor<Education> submitted = ArgumentCaptor.forClass(Education.class);
        verify(affiliationsManager).updateEducationAffiliation(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull(submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEducationYouAreNotTheSourceOf() {
        // AffiliationsManagerImpl calls orcidSecurityManager.checkSource on the
        // stored entity; the rule belongs to that manager's tests.
        Education education = education(1L, Visibility.PUBLIC, clientSource(CLIENT_2));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "education"))).when(affiliationsManager)
                .updateEducationAffiliation(eq(MY_ORCID), any(Education.class), anyBoolean());

        serviceDelegator.updateEducation(MY_ORCID, 1L, education);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEducationChangingVisibilityTest() {
        Education education = education(3L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(affiliationsManager).updateEducationAffiliation(eq(OTHER_ORCID), any(Education.class), anyBoolean());

        serviceDelegator.updateEducation(OTHER_ORCID, 3L, education);
        fail();
    }

    @Test
    public void testUpdateEducationLeavingVisibilityNullTest() {
        Education education = education(3L, null, clientSource(CLIENT_1));
        Education updated = education(3L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(affiliationsManager.updateEducationAffiliation(eq(OTHER_ORCID), any(Education.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateEducation(OTHER_ORCID, 3L, education);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((Education) response.getEntity()).getVisibility());
        ArgumentCaptor<Education> submitted = ArgumentCaptor.forClass(Education.class);
        verify(affiliationsManager).updateEducationAffiliation(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testDeleteEducation() {
        String orcid = "4444-4444-4444-4447";
        when(affiliationsManagerReadOnly.getEducationAffiliation(orcid, 12L)).thenReturn(education(12L, Visibility.PUBLIC, clientSource(CLIENT_1)))
                .thenThrow(new NoResultException());

        Response response = serviceDelegator.viewEducation(orcid, 12L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);

        response = serviceDelegator.deleteAffiliation(orcid, 12L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(orcid, ScopePathType.AFFILIATIONS_UPDATE);
        verify(affiliationsManager).checkSourceAndDelete(orcid, 12L);

        serviceDelegator.viewEducation(orcid, 12L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEducationYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "education"))).when(affiliationsManager)
                .checkSourceAndDelete("4444-4444-4444-4446", 9L);

        serviceDelegator.deleteAffiliation("4444-4444-4444-4446", 9L);
        fail();
    }

    // ------------------------------------------------------------- helpers

    private void assertViewEducationDecorated(long putCode, Visibility visibility, Source source) {
        Education education = education(putCode, visibility, source);
        when(affiliationsManagerReadOnly.getEducationAffiliation(ORCID, putCode)).thenReturn(education);

        Response response = serviceDelegator.viewEducation(ORCID, putCode);

        assertNotNull(response);
        Education returned = (Education) response.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003/education/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(ORCID, education, ScopePathType.AFFILIATIONS_READ_LIMITED);
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<EducationSummary>> summaryListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private Education education(Long putCode, Visibility visibility, Source source) {
        Education education = new Education();
        education.setPutCode(putCode);
        education.setDepartmentName("A Department");
        education.setRoleTitle("Student");
        education.setOrganization(organization());
        education.setVisibility(visibility);
        education.setSource(source);
        education.setCreatedDate(createdDate());
        education.setLastModifiedDate(lastModified());
        return education;
    }

    private EducationSummary educationSummary(Long putCode, Visibility visibility) {
        EducationSummary summary = new EducationSummary();
        summary.setPutCode(putCode);
        summary.setDepartmentName("A Department");
        summary.setRoleTitle("Student");
        summary.setOrganization(organization());
        summary.setVisibility(visibility);
        summary.setSource(clientSource(CLIENT_1));
        summary.setCreatedDate(createdDate());
        summary.setLastModifiedDate(lastModified());
        return summary;
    }
}
