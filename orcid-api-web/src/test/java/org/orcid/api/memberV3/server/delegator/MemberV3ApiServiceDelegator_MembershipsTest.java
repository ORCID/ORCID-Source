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
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.SourceOrcid;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_MembershipsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewMembershipsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewMemberships(ORCID);
    }

    @Test
    public void testViewMembershipReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Membership membership = new Membership();
        membership.setPutCode(27L);
        when(affiliationsManagerReadOnly.getMembershipAffiliation(eq(ORCID), eq(27L))).thenReturn(membership);
        Response r = serviceDelegator.viewMembership(ORCID, 27L);
        Membership element = (Membership) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/membership/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewMembershipsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        List<MembershipSummary> summaries = new ArrayList<>();
        when(affiliationsManagerReadOnly.getMembershipSummaryList(eq(ORCID))).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(eq(summaries), anyBoolean())).thenReturn(new ArrayList<>());
        
        Response r = serviceDelegator.viewMemberships(ORCID);
        Memberships element = (Memberships) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/memberships", element.getPath());
    }

    @Test
    public void testViewPublicMembership() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Membership membership = createMembership(27L, Visibility.PUBLIC, ORCID, null);
        membership.setDepartmentName("PUBLIC Department");
        when(affiliationsManagerReadOnly.getMembershipAffiliation(eq(ORCID), eq(27L))).thenReturn(membership);
        Response response = serviceDelegator.viewMembership(ORCID, 27L);
        assertNotNull(response);
        Membership result = (Membership) response.getEntity();
        assertNotNull(result);
        assertEquals(Long.valueOf(27L), result.getPutCode());
        assertEquals("/0000-0000-0000-0003/membership/27", result.getPath());
        assertEquals("PUBLIC Department", result.getDepartmentName());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateMembershipWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getMembershipAffiliation(eq(ORCID), eq(31L))).thenReturn(createMembership(31L));
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Membership.class), any(ScopePathType.class));
        serviceDelegator.viewMembership(ORCID, 31L);
        fail();
    }

    @Test
    public void testAddMembership() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        ActivitiesSummary summary = new ActivitiesSummary();
        summary.setMemberships(new Memberships());
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        Membership saved = createMembership(12345L);
        saved.setDepartmentName("My department name");
        when(affiliationsManager.createMembershipAffiliation(eq(ORCID), any(Membership.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(any(), any(), any(), any())).thenReturn(Response.status(Response.Status.CREATED).build());
        
        Response response = serviceDelegator.createMembership(ORCID, createMembership(null));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private Membership createMembership(Long putCode) {
        return createMembership(putCode, Visibility.PUBLIC, null, null);
    }

    private Membership createMembership(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        Membership d = new Membership();
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
