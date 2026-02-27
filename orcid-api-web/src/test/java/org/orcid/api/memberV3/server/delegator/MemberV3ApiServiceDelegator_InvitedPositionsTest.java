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
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_InvitedPositionsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewInvitedPositionsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewInvitedPositions(ORCID);
    }

    @Test
    public void testViewInvitedPositionReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        InvitedPosition invitedPosition = new InvitedPosition();
        invitedPosition.setPutCode(27L);
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(eq(ORCID), eq(27L))).thenReturn(invitedPosition);
        Response r = serviceDelegator.viewInvitedPosition(ORCID, 27L);
        InvitedPosition element = (InvitedPosition) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/invited-position/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewInvitedPositionsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        List<InvitedPositionSummary> summaries = new ArrayList<>();
        when(affiliationsManagerReadOnly.getInvitedPositionSummaryList(eq(ORCID))).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(eq(summaries), anyBoolean())).thenReturn(new ArrayList<>());
        
        Response r = serviceDelegator.viewInvitedPositions(ORCID);
        InvitedPositions element = (InvitedPositions) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/invited-positions", element.getPath());
    }

    @Test
    public void testViewPublicInvitedPosition() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        InvitedPosition invitedPosition = createInvitedPosition(27L, Visibility.PUBLIC, ORCID, null);
        invitedPosition.setDepartmentName("PUBLIC Department");
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(eq(ORCID), eq(27L))).thenReturn(invitedPosition);
        Response response = serviceDelegator.viewInvitedPosition(ORCID, 27L);
        assertNotNull(response);
        InvitedPosition result = (InvitedPosition) response.getEntity();
        assertNotNull(result);
        assertEquals(Long.valueOf(27L), result.getPutCode());
        assertEquals("/0000-0000-0000-0003/invited-position/27", result.getPath());
        assertEquals("PUBLIC Department", result.getDepartmentName());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateInvitedPositionWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getInvitedPositionAffiliation(eq(ORCID), eq(31L))).thenReturn(createInvitedPosition(31L));
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(InvitedPosition.class), any(ScopePathType.class));
        serviceDelegator.viewInvitedPosition(ORCID, 31L);
        fail();
    }

    @Test
    public void testAddInvitedPosition() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        ActivitiesSummary summary = new ActivitiesSummary();
        summary.setInvitedPositions(new InvitedPositions());
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        InvitedPosition saved = createInvitedPosition(12345L);
        saved.setDepartmentName("My department name");
        when(affiliationsManager.createInvitedPositionAffiliation(eq(ORCID), any(InvitedPosition.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(any(), any(), any(), any())).thenReturn(Response.status(Response.Status.CREATED).build());
        
        Response response = serviceDelegator.createInvitedPosition(ORCID, createInvitedPosition(null));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private InvitedPosition createInvitedPosition(Long putCode) {
        return createInvitedPosition(putCode, Visibility.PUBLIC, null, null);
    }

    private InvitedPosition createInvitedPosition(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        InvitedPosition d = new InvitedPosition();
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
