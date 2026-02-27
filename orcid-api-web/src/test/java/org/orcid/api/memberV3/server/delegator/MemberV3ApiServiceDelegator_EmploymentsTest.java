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
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_EmploymentsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployments(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewEmploymentWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment(ORCID, 27L);
    }

    @Test
    public void testViewEmploymentReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Employment employment = new Employment();
        employment.setPutCode(27L);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(eq(ORCID), eq(27L))).thenReturn(employment);
        Response r = serviceDelegator.viewEmployment(ORCID, 27L);
        Employment element = (Employment) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewEmploymentsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        List<EmploymentSummary> summaries = new ArrayList<>();
        when(affiliationsManagerReadOnly.getEmploymentSummaryList(eq(ORCID))).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(eq(summaries), anyBoolean())).thenReturn(new ArrayList<>());
        
        Response r = serviceDelegator.viewEmployments(ORCID);
        Employments element = (Employments) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employments", element.getPath());
    }

    @Test
    public void testViewEmploymentSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        EmploymentSummary summary = new EmploymentSummary();
        summary.setPutCode(27L);
        when(affiliationsManagerReadOnly.getEmploymentSummary(eq(ORCID), eq(27L))).thenReturn(summary);
        Response r = serviceDelegator.viewEmploymentSummary(ORCID, 27L);
        EmploymentSummary element = (EmploymentSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/employment/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicEmployment() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Employment employment = createEmployment(27L, Visibility.PUBLIC, ORCID, null);
        employment.setDepartmentName("PUBLIC Department");
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(eq(ORCID), eq(27L))).thenReturn(employment);
        Response response = serviceDelegator.viewEmployment(ORCID, 27L);
        assertNotNull(response);
        Employment result = (Employment) response.getEntity();
        assertNotNull(result);
        assertEquals(Long.valueOf(27L), result.getPutCode());
        assertEquals("/0000-0000-0000-0003/employment/27", result.getPath());
        assertEquals("PUBLIC Department", result.getDepartmentName());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEmploymentWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(eq(ORCID), eq(31L))).thenReturn(createEmployment(31L));
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Employment.class), any(ScopePathType.class));
        serviceDelegator.viewEmployment(ORCID, 31L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEmploymentThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(eq("4444-4444-4444-4446"), anyLong())).thenThrow(new NoResultException());
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 27L);
        fail();
    }

    @Test
    public void testAddEmployment() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        ActivitiesSummary summary = new ActivitiesSummary();
        summary.setEmployments(new Employments());
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        Employment saved = createEmployment(12345L);
        saved.setDepartmentName("My department name");
        when(affiliationsManager.createEmploymentAffiliation(eq(ORCID), any(Employment.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(any(), any(), any(), any())).thenReturn(Response.status(Response.Status.CREATED).build());
        
        Response response = serviceDelegator.createEmployment(ORCID, createEmployment(null));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Employment employment = createEmployment(27L, Visibility.PUBLIC, ORCID, null);
        employment.setDepartmentName("PUBLIC Department");
        when(affiliationsManagerReadOnly.getEmploymentAffiliation(eq(ORCID), eq(27L))).thenReturn(employment);
        when(affiliationsManager.updateEmploymentAffiliation(eq(ORCID), any(Employment.class), anyBoolean())).thenReturn(employment);
        
        Response response = serviceDelegator.updateEmployment(ORCID, 27L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    private Employment createEmployment(Long putCode) {
        return createEmployment(putCode, Visibility.PUBLIC, null, null);
    }

    private Employment createEmployment(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        Employment d = new Employment();
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
