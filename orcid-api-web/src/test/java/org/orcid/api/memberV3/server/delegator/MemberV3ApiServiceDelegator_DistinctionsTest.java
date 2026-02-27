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
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_DistinctionsTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewDistinctionsWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewDistinctions(ORCID);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewDistinctionWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewDistinction(ORCID, 27L);
    }

    @Test
    public void testViewDistinctionReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Distinction distinction = new Distinction();
        distinction.setPutCode(27L);
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(eq(ORCID), eq(27L))).thenReturn(distinction);
        Response r = serviceDelegator.viewDistinction(ORCID, 27L);
        Distinction element = (Distinction) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinction/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewDistinctionsReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        List<DistinctionSummary> summaries = new ArrayList<>();
        when(affiliationsManagerReadOnly.getDistinctionSummaryList(eq(ORCID))).thenReturn(summaries);
        when(affiliationsManagerReadOnly.groupAffiliations(eq(summaries), anyBoolean())).thenReturn(new ArrayList<>());
        
        Response r = serviceDelegator.viewDistinctions(ORCID);
        Distinctions element = (Distinctions) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinctions", element.getPath());
    }

    @Test
    public void testViewDistinctionSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        DistinctionSummary summary = new DistinctionSummary();
        summary.setPutCode(27L);
        when(affiliationsManagerReadOnly.getDistinctionSummary(eq(ORCID), eq(27L))).thenReturn(summary);
        Response r = serviceDelegator.viewDistinctionSummary(ORCID, 27L);
        DistinctionSummary element = (DistinctionSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/distinction/27", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicDistinction() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Distinction distinction = createDistinction(27L, Visibility.PUBLIC, ORCID, null);
        distinction.setDepartmentName("PUBLIC Department");
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(eq(ORCID), eq(27L))).thenReturn(distinction);
        Response response = serviceDelegator.viewDistinction(ORCID, 27L);
        assertNotNull(response);
        Distinction result = (Distinction) response.getEntity();
        assertNotNull(result);
        assertEquals(Long.valueOf(27L), result.getPutCode());
        assertEquals("/0000-0000-0000-0003/distinction/27", result.getPath());
        assertEquals("PUBLIC Department", result.getDepartmentName());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateDistinctionWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(eq(ORCID), eq(31L))).thenReturn(createDistinction(31L));
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Distinction.class), any(ScopePathType.class));
        serviceDelegator.viewDistinction(ORCID, 31L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewDistinctionThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        when(affiliationsManagerReadOnly.getDistinctionAffiliation(eq("4444-4444-4444-4446"), anyLong())).thenThrow(new NoResultException());
        serviceDelegator.viewDistinction("4444-4444-4444-4446", 27L);
        fail();
    }

    @Test
    public void testAddDistinction() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        ActivitiesSummary summary = new ActivitiesSummary();
        summary.setDistinctions(new Distinctions());
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(ORCID), anyBoolean())).thenReturn(summary);
        
        Distinction saved = createDistinction(12345L);
        saved.setDepartmentName("My department name");
        when(affiliationsManager.createDistinctionAffiliation(eq(ORCID), any(Distinction.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(any(), any(), any(), any())).thenReturn(Response.status(Response.Status.CREATED).build());
        
        Response response = serviceDelegator.createDistinction(ORCID, createDistinction(null));
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private Distinction createDistinction(Long putCode) {
        return createDistinction(putCode, Visibility.PUBLIC, null, null);
    }

    private Distinction createDistinction(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        Distinction d = new Distinction();
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

    private DistinctionSummary createDistinctionSummary(Long putCode) {
        DistinctionSummary d = new DistinctionSummary();
        d.setPutCode(putCode);
        d.setVisibility(Visibility.PUBLIC);
        return d;
    }
}
