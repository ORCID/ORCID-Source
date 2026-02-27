package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_EducationsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducations(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEducationWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation(ORCID, 27L);
    }

    @Test
    public void testViewEducationReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Education education = new Education();
        education.setPutCode(27L);
        when(affiliationsManagerReadOnly.getEducationAffiliation(eq(ORCID), eq(27L))).thenReturn(education);
        Response r = serviceDelegator.viewEducation(ORCID, 27L);
        Education element = (Education) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/education/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewEducationsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        List<EducationSummary> summaries = new ArrayList<>();
        when(affiliationsManagerReadOnly.getEducationSummaryList(eq(ORCID))).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(eq(summaries), anyBoolean())).thenReturn(new ArrayList<>());
        
        Response r = serviceDelegator.viewEducations(ORCID);
        Educations element = (Educations) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/educations", element.getPath());
    }

    @Test
    public void testViewEducationSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        EducationSummary summary = new EducationSummary();
        summary.setPutCode(27L);
        when(affiliationsManagerReadOnly.getEducationSummary(eq(ORCID), eq(27L))).thenReturn(summary);
        Response r = serviceDelegator.viewEducationSummary(ORCID, 27L);
        EducationSummary element = (EducationSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/education/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicEducation() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Education education = createEducation(27L, Visibility.PUBLIC, ORCID, null);
        education.setDepartmentName("PUBLIC Department");
        when(affiliationsManagerReadOnly.getEducationAffiliation(eq(ORCID), eq(27L))).thenReturn(education);
        Response response = serviceDelegator.viewEducation(ORCID, 27L);
        assertNotNull(response);
        Education result = (Education) response.getEntity();
        assertNotNull(result);
        assertEquals(Long.valueOf(27L), result.getPutCode());
        assertEquals("/0000-0000-0000-0003/education/27", result.getPath());
        assertEquals("PUBLIC Department", result.getDepartmentName());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEducationWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getEducationAffiliation(eq(ORCID), eq(31L))).thenReturn(createEducation(31L));
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Education.class), any(ScopePathType.class));
        serviceDelegator.viewEducation(ORCID, 31L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEducationThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getEducationAffiliation(eq("4444-4444-4444-4446"), anyLong())).thenThrow(new NoResultException());
        serviceDelegator.viewEducation("4444-4444-4444-4446", 27L);
        fail();
    }

    @Test
    public void testAddEducation() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        ActivitiesSummary summary = new ActivitiesSummary();
        summary.setEducations(new Educations());
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        Education saved = createEducation(12345L);
        saved.setDepartmentName("My department name");
        when(affiliationsManager.createEducationAffiliation(eq(ORCID), any(Education.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(any(), any(), any(), any())).thenReturn(Response.status(Response.Status.CREATED).build());
        
        Response response = serviceDelegator.createEducation(ORCID, createEducation(null));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateEducation() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Education education = createEducation(27L, Visibility.PUBLIC, ORCID, null);
        education.setDepartmentName("PUBLIC Department");
        when(affiliationsManagerReadOnly.getEducationAffiliation(eq(ORCID), eq(27L))).thenReturn(education);
        when(affiliationsManager.updateEducationAffiliation(eq(ORCID), any(Education.class), anyBoolean())).thenReturn(education);
        
        Response response = serviceDelegator.updateEducation(ORCID, 27L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    private Education createEducation(Long putCode) {
        return createEducation(putCode, Visibility.PUBLIC, null, null);
    }

    private Education createEducation(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        Education d = new Education();
        d.setPutCode(putCode);
        d.setVisibility(visibility);
        if (sourceOrcid != null || sourceClientId != null) {
            Source s = new Source();
            if (sourceOrcid != null) s.setSourceOrcid(new SourceOrcid(sourceOrcid));
            if (sourceClientId != null) s.setSourceClientId(new SourceClientId(sourceClientId));
            d.setSource(s);
        }
        return d;
    }
}
