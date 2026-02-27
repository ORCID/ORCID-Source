package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.test.helper.v3.Utils;

public class MemberV3ApiServiceDelegator_WorksTest extends MemberV3ApiServiceDelegatorMockTest {

    protected final String ORCID = "0000-0000-0000-0003";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewWorkWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork(ORCID, 11L);
    }

    @Test
    public void testViewWorkReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Work work = createWork(11L, Visibility.PUBLIC, null, "APP-5555555555555555");
        when(workManagerReadOnly.getWork(eq(ORCID), eq(11L))).thenReturn(work);
        
        Response r = serviceDelegator.viewWork(ORCID, 11L);
        Work element = (Work) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/work/11", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewWorkSummaryReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(11L);
        when(workManagerReadOnly.getWorkSummary(eq(ORCID), eq(11L))).thenReturn(summary);
        
        Response r = serviceDelegator.viewWorkSummary(ORCID, 11L);
        WorkSummary element = (WorkSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/work/11", element.getPath());
        Utils.assertIsPublicOrSource(element, "APP-5555555555555555");
    }

    @Test
    public void testViewPublicWork() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Work work = createWork(5L, Visibility.PUBLIC, ORCID, null);
        when(workManagerReadOnly.getWork(eq(ORCID), eq(5L))).thenReturn(work);
        
        Response response = serviceDelegator.viewWork(ORCID, 5L);
        assertNotNull(response);
        Work result = (Work) response.getEntity();
        assertNotNull(result);
        assertEquals(Long.valueOf(5), result.getPutCode());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateWorkYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Work work = createWork(8L, Visibility.PRIVATE, "SOME-OTHER-SOURCE", null);
        when(workManagerReadOnly.getWork(eq(ORCID), eq(8L))).thenReturn(work);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(Work.class), any(ScopePathType.class));
        
        serviceDelegator.viewWork(ORCID, 8L);
        fail();
    }

    @Test
    public void viewWorksTest() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        List<WorkSummary> summaries = new ArrayList<>();
        when(workManagerReadOnly.getWorksSummaryList(eq(ORCID))).thenReturn(summaries);
        when(workManager.groupWorks(eq(summaries), anyBoolean())).thenReturn(new Works());
        
        Response r = serviceDelegator.viewWorks(ORCID);
        assertNotNull(r);
        Works works = (Works) r.getEntity();
        assertNotNull(works);
        assertEquals("/0000-0000-0000-0003/works", works.getPath());
    }

    @Test
    public void testAddWork() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.ACTIVITIES_UPDATE);
        Work input = createWork(null, Visibility.PUBLIC, null, null);
        Work saved = createWork(999L, Visibility.PUBLIC, null, "APP-5555555555555555");
        
        when(workManager.createWork(eq(ORCID), any(Work.class), anyBoolean())).thenReturn(saved);
        when(apiUtils.buildApiResponse(any(), any(), any(), any())).thenReturn(Response.status(Response.Status.CREATED).build());
        
        Response response = serviceDelegator.createWork(ORCID, input);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    private Work createWork(Long putCode, Visibility visibility, String sourceOrcid, String sourceClientId) {
        Work w = new Work();
        w.setPutCode(putCode);
        w.setVisibility(visibility);
        w.setWorkTitle(new WorkTitle());
        w.getWorkTitle().setTitle(new Title("Title " + putCode));
        w.setWorkType(WorkType.JOURNAL_ARTICLE);
        if (sourceOrcid != null || sourceClientId != null) {
            Source s = new Source();
            if (sourceOrcid != null) s.setSourceOrcid(new org.orcid.jaxb.model.v3.release.common.SourceOrcid(sourceOrcid));
            if (sourceClientId != null) s.setSourceClientId(new SourceClientId(sourceClientId));
            w.setSource(s);
        }
        return w;
    }
}
