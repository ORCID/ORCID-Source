package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidNoResultException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Subtitle;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.TranslatedTitle;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Citation;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.test.helper.Utils;
import org.orcid.utils.DateUtils;

/**
 * The work endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * Two things this class deliberately does not try to prove. First, which works
 * survive a read: {@code checkAndFilter} is void and edits the list -- and, for a
 * {@code WorkBulk}, replaces denied entries with an {@code OrcidError} -- in
 * place, so with a mocked security manager nothing is ever removed and any
 * count-based assertion would hold for the wrong reason. That is
 * {@code OrcidSecurityManager_generalTest} and
 * {@code OrcidSecurityManager_WorkBulkTest}'s work. Second, the rules enforced in
 * SQL: {@code WorkDaoImpl} selects and deletes on {@code (work_id, orcid)}
 * together, which is what stops one record reading or deleting another's work.
 * A mock stubbed to throw proves nothing about a SQL predicate, so those tests
 * below assert only that the delegator does not swallow the miss.
 */
public class MemberV2ApiServiceDelegator_WorksTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4446";
    private static final String MY_ORCID = "4444-4444-4444-4443";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewWorkWrongToken() {
        Work work = work(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(work);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, work,
                ScopePathType.ORCID_WORKS_READ_LIMITED);

        try {
            serviceDelegator.viewWork(ORCID, 11L);
        } finally {
            assertNull("the work must not be decorated once the guard has refused", work.getPath());
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewWorkSummaryWrongToken() {
        WorkSummary summary = workSummary(11L, "PUBLIC", Visibility.PUBLIC);
        when(workManagerReadOnly.getWorkSummary(ORCID, 11L)).thenReturn(summary);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, summary,
                ScopePathType.ORCID_WORKS_READ_LIMITED);

        try {
            serviceDelegator.viewWorkSummary(ORCID, 11L);
        } finally {
            assertNull("the summary must not be decorated once the guard has refused", summary.getPath());
        }
    }

    @Test
    public void testViewWorkReadPublic() {
        Work work = work(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(work);

        Response r = serviceDelegator.viewWork(ORCID, 11L);

        Work returned = (Work) r.getEntity();
        assertNotNull(returned);
        assertEquals("/0000-0000-0000-0003/work/11", returned.getPath());
        assertNotNull(returned.getLastModifiedDate());
        assertNotNull(returned.getLastModifiedDate().getValue());
        assertNotNull(returned.getWorkTitle());
        assertNotNull(returned.getWorkTitle().getTitle());
        assertEquals("PUBLIC", returned.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(11), returned.getPutCode());
        assertEquals(WorkType.JOURNAL_ARTICLE, returned.getWorkType());
        assertEquals("APP-5555555555555555", returned.getSource().retrieveSourcePath());
        assertEquals(CLIENT_1_NAME, returned.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testViewWorkSummaryReadPublic() {
        WorkSummary summary = workSummary(11L, "PUBLIC", Visibility.PUBLIC);
        when(workManagerReadOnly.getWorkSummary(ORCID, 11L)).thenReturn(summary);

        Response r = serviceDelegator.viewWorkSummary(ORCID, 11L);

        WorkSummary element = (WorkSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/work/11", element.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testViewPublicWork() {
        assertViewWorkDecorated(5L, "Journal article A", Visibility.PUBLIC, clientSource(CLIENT_1));
    }

    @Test
    public void testViewLimitedWork() {
        assertViewWorkDecorated(6L, "Journal article B", Visibility.LIMITED, userSource(OTHER_ORCID));
    }

    @Test
    public void testViewPrivateWork() {
        assertViewWorkDecorated(7L, "Journal article C", Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateWorkYouAreNotTheSourceOf() {
        Work work = work(8L, "Journal article D", Visibility.PRIVATE, userSource(OTHER_ORCID));
        when(workManagerReadOnly.getWork(OTHER_ORCID, 8L)).thenReturn(work);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, work, ScopePathType.ORCID_WORKS_READ_LIMITED);

        serviceDelegator.viewWork(OTHER_ORCID, 8L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewWorkThatDontBelongToTheUser() {
        // WorkDaoImpl selects on (id, orcid) together, so work 5 -- which belongs
        // to another record -- is simply not found. The predicate is SQL and
        // cannot be proved here; what can is that the delegator lets the miss out
        // and never asks the guard about a work it did not get.
        when(workManagerReadOnly.getWork(MY_ORCID, 5L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewWork(MY_ORCID, 5L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void viewWorksTest() {
        List<WorkSummary> stored = new ArrayList<>(Arrays.asList(workSummary(11L, "PUBLIC", Visibility.PUBLIC),
                workSummary(12L, "LIMITED", Visibility.LIMITED), workSummary(13L, "PRIVATE", Visibility.PRIVATE),
                workSummary(14L, "SELF LIMITED", Visibility.LIMITED)));
        when(workManagerReadOnly.getWorksSummaryList(ORCID)).thenReturn(stored);
        when(workManager.groupWorks(anyList(), eq(false))).thenAnswer(invocation -> groupEachSeparately(invocation.getArgument(0)));

        Response r = serviceDelegator.viewWorks(ORCID);

        assertNotNull(r);
        Works works = (Works) r.getEntity();
        assertNotNull(works);
        assertEquals("/0000-0000-0000-0003/works", works.getPath());
        Utils.verifyLastModified(works.getLastModifiedDate());
        assertNotNull(works.getWorkGroup());
        assertEquals(4, works.getWorkGroup().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;

        for (WorkGroup workGroup : works.getWorkGroup()) {
            Utils.verifyLastModified(workGroup.getLastModifiedDate());
            assertNotNull(workGroup.getIdentifiers());
            assertNotNull(workGroup.getIdentifiers().getExternalIdentifier());
            assertEquals(1, workGroup.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(workGroup.getWorkSummary());
            assertEquals(1, workGroup.getWorkSummary().size());
            WorkSummary summary = workGroup.getWorkSummary().get(0);
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            // the path is the delegator's contribution to each summary
            assertEquals("/0000-0000-0000-0003/work/" + summary.getPutCode(), summary.getPath());
            switch (workGroup.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "1":
                assertEquals("PUBLIC", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                found1 = true;
                break;
            case "2":
                assertEquals("LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found2 = true;
                break;
            case "3":
                assertEquals("PRIVATE", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(13), summary.getPutCode());
                found3 = true;
                break;
            case "4":
                assertEquals("SELF LIMITED", summary.getTitle().getTitle().getContent());
                assertEquals(Long.valueOf(14), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid external id found: " + workGroup.getIdentifiers().getExternalIdentifier().get(0).getValue());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

        // the cached summary list must be copied before it reaches a filter that
        // edits in place
        ArgumentCaptor<List<WorkSummary>> filtered = summaryListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), filtered.capture(), eq(ScopePathType.ORCID_WORKS_READ_LIMITED));
        assertNotSame(stored, filtered.getValue());
    }

    @Test
    public void testReadPublicScope_Works() {
        // Refused per work, never with a blanket matcher: refusing every work
        // would also refuse 11, 12 and 13 and the "should work" half of this test
        // would prove nothing.
        Work eleven = work(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1));
        Work twelve = work(12L, "LIMITED", Visibility.LIMITED, clientSource(CLIENT_1));
        Work thirteen = work(13L, "PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1));
        Work fourteen = work(14L, "SELF LIMITED", Visibility.LIMITED, userSource(ORCID));
        Work fifteen = work(15L, "SELF PRIVATE", Visibility.PRIVATE, userSource(ORCID));
        when(workManagerReadOnly.getWork(ORCID, 11L)).thenReturn(eleven);
        when(workManagerReadOnly.getWork(ORCID, 12L)).thenReturn(twelve);
        when(workManagerReadOnly.getWork(ORCID, 13L)).thenReturn(thirteen);
        when(workManagerReadOnly.getWork(ORCID, 14L)).thenReturn(fourteen);
        when(workManagerReadOnly.getWork(ORCID, 15L)).thenReturn(fifteen);
        WorkSummary elevenSummary = workSummary(11L, "PUBLIC", Visibility.PUBLIC);
        WorkSummary twelveSummary = workSummary(12L, "LIMITED", Visibility.LIMITED);
        WorkSummary thirteenSummary = workSummary(13L, "PRIVATE", Visibility.PRIVATE);
        WorkSummary fourteenSummary = workSummary(14L, "SELF LIMITED", Visibility.LIMITED);
        when(workManagerReadOnly.getWorkSummary(ORCID, 11L)).thenReturn(elevenSummary);
        when(workManagerReadOnly.getWorkSummary(ORCID, 12L)).thenReturn(twelveSummary);
        when(workManagerReadOnly.getWorkSummary(ORCID, 13L)).thenReturn(thirteenSummary);
        when(workManagerReadOnly.getWorkSummary(ORCID, 14L)).thenReturn(fourteenSummary);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, fourteen, ScopePathType.ORCID_WORKS_READ_LIMITED);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, fifteen, ScopePathType.ORCID_WORKS_READ_LIMITED);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, fourteenSummary, ScopePathType.ORCID_WORKS_READ_LIMITED);

        // Public works
        Response r = serviceDelegator.viewWork(ORCID, 11L);
        assertNotNull(r);
        assertEquals(Work.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewWorkSummary(ORCID, 11L);
        assertNotNull(r);
        assertEquals(WorkSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited where source is me, should work
        serviceDelegator.viewWork(ORCID, 12L);
        serviceDelegator.viewWorkSummary(ORCID, 12L);

        // Limited with other source should fail
        try {
            serviceDelegator.viewWork(ORCID, 14L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewWorkSummary(ORCID, 14L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        // Private where am the source should work
        serviceDelegator.viewWork(ORCID, 13L);
        serviceDelegator.viewWorkSummary(ORCID, 13L);

        // Private with other source should fail
        try {
            serviceDelegator.viewWork(ORCID, 15L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            serviceDelegator.viewWork(ORCID, 15L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCleanEmptyFieldsOnWorks() {
        LastModifiedDate lmd = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis()));
        Work work = new Work();
        work.setLastModifiedDate(lmd);
        work.setWorkCitation(new Citation("", CitationType.FORMATTED_UNSPECIFIED));
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("My Work"));
        title.setSubtitle(new Subtitle("My subtitle"));
        title.setTranslatedTitle(new TranslatedTitle("", ""));
        work.setWorkTitle(title);

        ActivityUtils.cleanEmptyFields(work);

        assertNotNull(work);
        Utils.verifyLastModified(work.getLastModifiedDate());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertNotNull(work.getWorkTitle().getSubtitle());
        assertEquals("My Work", work.getWorkTitle().getTitle().getContent());
        assertEquals("My subtitle", work.getWorkTitle().getSubtitle().getContent());

        assertNull(work.getWorkCitation());
        assertNull(work.getWorkTitle().getTranslatedTitle());
    }

    @Test
    public void testAddWork() {
        String orcid = "4444-4444-4444-4445";
        String title = "work # 1 " + System.currentTimeMillis();
        Work created = work(100L, title, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(workManager.createWork(eq(orcid), any(Work.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createWork(orcid, Utils.getWork(title));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(100), putCode);
        verify(orcidSecurityManager).checkClientAccessAndScopes(orcid, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.ORCID_WORKS_UPDATE);
        ArgumentCaptor<Work> submitted = ArgumentCaptor.forClass(Work.class);
        verify(workManager).createWork(eq(orcid), submitted.capture(), eq(true));
        assertEquals(title, submitted.getValue().getWorkTitle().getTitle().getContent());
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
        assertEquals(CLIENT_1_NAME, created.getSource().getSourceName().getContent());

        serviceDelegator.deleteWork(orcid, putCode);
        verify(workManager).checkSourceAndRemoveWork(orcid, putCode);
    }

    @Test
    public void testCreateWorksWithBulkAllOK() {
        Long time = System.currentTimeMillis();
        WorkBulk bulk = new WorkBulk();
        for (int i = 0; i < 5; i++) {
            Work work = work(null, "Bulk work " + i + " " + time, Visibility.PUBLIC, clientSource(CLIENT_1));
            work.setWorkExternalIdentifiers(externalIds("doi", "doi-" + i + "-" + time));
            bulk.getBulk().add(work);
        }
        WorkBulk persisted = new WorkBulk();
        for (int i = 0; i < 5; i++) {
            Work work = work((long) (i + 1), "Bulk work " + i + " " + time, Visibility.PUBLIC, clientSource(CLIENT_1));
            work.setWorkExternalIdentifiers(externalIds("doi", "doi-" + i + "-" + time));
            persisted.getBulk().add(work);
        }
        when(workManager.createWorks(eq(ORCID), any(WorkBulk.class))).thenReturn(persisted);

        Response response = serviceDelegator.createWorks(ORCID, bulk);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        WorkBulk returned = (WorkBulk) response.getEntity();
        assertNotNull(returned);
        assertEquals(5, returned.getBulk().size());
        for (int i = 0; i < 5; i++) {
            assertTrue(Work.class.isAssignableFrom(returned.getBulk().get(i).getClass()));
            Work w = (Work) returned.getBulk().get(i);
            Utils.verifyLastModified(w.getLastModifiedDate());
            assertNotNull(w.getPutCode());
            assertTrue(0L < w.getPutCode());
            assertEquals("Bulk work " + i + " " + time, w.getWorkTitle().getTitle().getContent());
            assertNotNull(w.getExternalIdentifiers().getExternalIdentifier());
            assertEquals("doi-" + i + "-" + time, w.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
            // the source name is resolved on every element of the bulk
            assertEquals(CLIENT_1_NAME, w.getSource().getSourceName().getContent());
        }
        verify(orcidSecurityManager).checkClientAccessAndScopes(ORCID, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.ORCID_WORKS_UPDATE);
        // every incoming work has had its source cleared before it is submitted
        ArgumentCaptor<WorkBulk> submitted = ArgumentCaptor.forClass(WorkBulk.class);
        verify(workManager).createWorks(eq(ORCID), submitted.capture());
        submitted.getValue().getBulk().forEach(element -> assertNull(((Work) element).getSource()));
    }

    @Test
    public void testCreateBulkWorksWithBlankTitles() {
        // A blank title becomes an OrcidError inside WorkManagerImpl, which is
        // where that rule is proved. What the delegator owes is to return the
        // mixed bulk unaltered and to decorate only the works in it -- an
        // OrcidError has no path and no source.
        Long time = System.currentTimeMillis();
        WorkBulk bulk = new WorkBulk();
        for (int i = 0; i < 5; i++) {
            Work work = work(null, i == 0 ? " " : "title " + i, Visibility.PUBLIC, clientSource(CLIENT_1));
            work.setWorkExternalIdentifiers(externalIds("doi", "doi-" + i + "-" + time));
            bulk.getBulk().add(work);
        }
        WorkBulk persisted = new WorkBulk();
        persisted.getBulk().add(orcidError(9001, "Invalid title"));
        for (int i = 1; i < 5; i++) {
            persisted.getBulk().add(work((long) (i + 1), "title " + i, Visibility.PUBLIC, clientSource(CLIENT_1)));
        }
        when(workManager.createWorks(eq(ORCID), any(WorkBulk.class))).thenReturn(persisted);

        Response response = serviceDelegator.createWorks(ORCID, bulk);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        WorkBulk returned = (WorkBulk) response.getEntity();
        assertNotNull(returned);
        assertEquals(5, returned.getBulk().size());

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                assertTrue(returned.getBulk().get(i) instanceof OrcidError);
            } else {
                assertTrue(returned.getBulk().get(i) instanceof Work);
                Work w = (Work) returned.getBulk().get(i);
                assertEquals(CLIENT_1_NAME, w.getSource().getSourceName().getContent());
                serviceDelegator.deleteWork(ORCID, w.getPutCode());
            }
        }
    }

    @Test
    public void testUpdateWork() {
        Work work = work(1L, "Updated work title", Visibility.PUBLIC, clientSource(CLIENT_1));
        work.setWorkType(WorkType.EDITED_BOOK);
        Work updated = work(1L, "Updated work title", Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setWorkType(WorkType.EDITED_BOOK);
        when(workManager.updateWork(eq(MY_ORCID), any(Work.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateWork(MY_ORCID, 1L, work);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Work returned = (Work) response.getEntity();
        assertEquals("Updated work title", returned.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.EDITED_BOOK, returned.getWorkType());
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.ORCID_WORKS_UPDATE);
        ArgumentCaptor<Work> submitted = ArgumentCaptor.forClass(Work.class);
        verify(workManager).updateWork(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateWorkYouAreNotTheSourceOf() {
        // WorkManagerImpl calls orcidSecurityManager.checkSource on the stored
        // entity; the rule belongs to that manager's tests.
        Work work = work(2L, "Another day in the life", Visibility.PUBLIC, userSource(MY_ORCID));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "work"))).when(workManager).updateWork(eq(MY_ORCID), any(Work.class),
                anyBoolean());

        serviceDelegator.updateWork(MY_ORCID, 2L, work);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateWorkChangingVisibilityTest() {
        String orcid = "4444-4444-4444-4445";
        Work work = work(3L, "A Book With Contributors JSON", Visibility.PRIVATE, userSource(orcid));
        doThrow(new VisibilityMismatchException()).when(workManager).updateWork(eq(orcid), any(Work.class), anyBoolean());

        serviceDelegator.updateWork(orcid, 3L, work);
        fail();
    }

    @Test
    public void testUpdateWorkLeavingVisibilityNullTest() {
        String orcid = "4444-4444-4444-4447";
        Work work = work(10L, "Journal article F", null, clientSource(CLIENT_1));
        Work updated = work(10L, "Journal article F", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(workManager.updateWork(eq(orcid), any(Work.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updateWork(orcid, 10L, work);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Work returned = (Work) response.getEntity();
        assertNotNull(returned);
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(Visibility.PUBLIC, returned.getVisibility());
        ArgumentCaptor<Work> submitted = ArgumentCaptor.forClass(Work.class);
        verify(workManager).updateWork(eq(orcid), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testDeleteWork() {
        String orcid = "4444-4444-4444-4447";
        when(workManagerReadOnly.getWork(orcid, 9L)).thenReturn(work(9L, "Journal article E", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewWork(orcid, 9L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);

        response = serviceDelegator.deleteWork(orcid, 9L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(orcid, ScopePathType.ORCID_WORKS_UPDATE);
        // whether the row can actually be removed is decided by the (work_id,
        // orcid) predicate in WorkDaoImpl; at this boundary the manager is asked
        verify(workManager).checkSourceAndRemoveWork(orcid, 9L);
    }

    @Test
    public void testAddWorkWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        when(workManager.createWork(eq(orcid), any(Work.class), anyBoolean())).thenAnswer(invocation -> {
            Work submitted = invocation.getArgument(1);
            if ("INVALID".equals(submitted.getExternalIdentifiers().getExternalIdentifier().get(0).getType())) {
                throw new ActivityIdentifierValidationException();
            }
            return work(100L, "work # 1", Visibility.PUBLIC, clientSource(CLIENT_1));
        });

        Work work = Utils.getWork("work # 1 " + System.currentTimeMillis());
        try {
            work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createWork(orcid, work);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        // Assert that it could be created with a valid value
        work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        Response response = serviceDelegator.createWork(orcid, work);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);

        // Delete it to roll back the test data
        response = serviceDelegator.deleteWork(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteWorkYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "work"))).when(workManager).checkSourceAndRemoveWork(OTHER_ORCID, 8L);

        serviceDelegator.deleteWork(OTHER_ORCID, 8L);
        fail();
    }

    @Test
    public void testViewBulkWorks() {
        // The fourth entry is an OrcidError because checkAndFilter replaced a
        // denied work with one -- that substitution is
        // OrcidSecurityManager_WorkBulkTest's and is modelled here. What this
        // test proves is that setPathToBulk decorates the works and leaves the
        // error alone.
        WorkBulk workBulk = new WorkBulk();
        workBulk.getBulk().add(work(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        workBulk.getBulk().add(work(12L, "LIMITED", Visibility.LIMITED, clientSource(CLIENT_1)));
        workBulk.getBulk().add(work(13L, "PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1)));
        workBulk.getBulk().add(orcidError(9018, "The work is private"));
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13,16")).thenReturn(workBulk);

        Response response = serviceDelegator.viewBulkWorks(ORCID, "11,12,13,16");

        WorkBulk returned = (WorkBulk) response.getEntity();
        assertNotNull(returned);
        assertNotNull(returned.getBulk());
        assertEquals(4, returned.getBulk().size());
        assertTrue(returned.getBulk().get(0) instanceof Work);
        assertTrue(returned.getBulk().get(1) instanceof Work);
        assertTrue(returned.getBulk().get(2) instanceof Work);
        assertTrue(returned.getBulk().get(3) instanceof OrcidError);
        assertEquals("/0000-0000-0000-0003/work/11", ((Work) returned.getBulk().get(0)).getPath());
        assertEquals("/0000-0000-0000-0003/work/12", ((Work) returned.getBulk().get(1)).getPath());
        assertEquals("/0000-0000-0000-0003/work/13", ((Work) returned.getBulk().get(2)).getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test
    public void testViewBulkWorksWithBadPutCode() {
        // An unknown put code comes back from findWorkBulk as an OrcidError --
        // WorkManagerReadOnlyImpl's doing, over a query that filters on the
        // record's own works. The delegator must pass it through untouched.
        WorkBulk workBulk = new WorkBulk();
        workBulk.getBulk().add(work(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        workBulk.getBulk().add(work(12L, "LIMITED", Visibility.LIMITED, clientSource(CLIENT_1)));
        workBulk.getBulk().add(work(13L, "PRIVATE", Visibility.PRIVATE, clientSource(CLIENT_1)));
        workBulk.getBulk().add(orcidError(9016, "No work found with put code 9999"));
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13,9999")).thenReturn(workBulk);

        Response response = serviceDelegator.viewBulkWorks(ORCID, "11,12,13,9999");

        WorkBulk returned = (WorkBulk) response.getEntity();
        assertNotNull(returned);
        assertNotNull(returned.getBulk());
        assertEquals(4, returned.getBulk().size());
        assertTrue(returned.getBulk().get(0) instanceof Work);
        assertTrue(returned.getBulk().get(1) instanceof Work);
        assertTrue(returned.getBulk().get(2) instanceof Work);
        assertTrue(returned.getBulk().get(3) instanceof OrcidError);
        verify(orcidSecurityManager).checkAndFilter(ORCID, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    @Test(expected = OrcidNoResultException.class)
    public void testViewBulkWorksWithBadOrcid() {
        // This one is genuinely the delegator's own guard, and the only place it
        // is enforced.
        when(profileEntityManager.orcidExists("non-existent")).thenReturn(false);

        try {
            serviceDelegator.viewBulkWorks("non-existent", "11,12,13");
        } finally {
            verify(workManagerReadOnly, never()).findWorkBulk(eq("non-existent"), anyString());
        }
    }

    @Test(expected = ExceedMaxNumberOfPutCodesException.class)
    public void testViewBulkWorksWithTooManyPutCodes() {
        // The limit is checked inside WorkManagerReadOnlyImpl against its own
        // configured maximum, so it is proved there.
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        when(workManagerReadOnly.findWorkBulk(eq(ORCID), anyString())).thenThrow(new ExceedMaxNumberOfPutCodesException(100));

        StringBuilder tooManyPutCodes = new StringBuilder("0");
        for (int i = 1; i <= 100; i++) {
            tooManyPutCodes.append(",").append(i);
        }
        serviceDelegator.viewBulkWorks(ORCID, tooManyPutCodes.toString());
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewBulkWrongToken() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.getBulk().add(work(11L, "PUBLIC", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(profileEntityManager.orcidExists(ORCID)).thenReturn(true);
        when(workManagerReadOnly.findWorkBulk(ORCID, "11,12,13")).thenReturn(workBulk);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, workBulk,
                ScopePathType.ORCID_WORKS_READ_LIMITED);

        try {
            serviceDelegator.viewBulkWorks(ORCID, "11,12,13");
        } finally {
            assertNull("the bulk must not be decorated once the guard has refused", ((Work) workBulk.getBulk().get(0)).getPath());
        }
    }

    // ------------------------------------------------------------- helpers

    private void assertViewWorkDecorated(long putCode, String title, Visibility visibility, Source source) {
        Work work = work(putCode, title, visibility, source);
        when(workManagerReadOnly.getWork(OTHER_ORCID, putCode)).thenReturn(work);

        Response response = serviceDelegator.viewWork(OTHER_ORCID, putCode);

        assertNotNull(response);
        Work returned = (Work) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4446/work/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertNotNull(returned.getWorkTitle());
        assertNotNull(returned.getWorkTitle().getTitle());
        assertEquals(title, returned.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(putCode), returned.getPutCode());
        assertEquals(WorkType.JOURNAL_ARTICLE, returned.getWorkType());
        assertEquals(visibility.value(), returned.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, work, ScopePathType.ORCID_WORKS_READ_LIMITED);
    }

    /**
     * A stand-in for {@code groupWorks}: one group per summary, keyed on the
     * external identifier each summary carries. The grouping algorithm itself is
     * {@code WorkManagerReadOnlyImpl}'s and is tested there.
     */
    private Works groupEachSeparately(List<WorkSummary> summaries) {
        Works works = new Works();
        for (WorkSummary summary : summaries) {
            WorkGroup group = new WorkGroup();
            group.setLastModifiedDate(summary.getLastModifiedDate());
            group.getWorkSummary().add(summary);
            group.getIdentifiers().getExternalIdentifier().addAll(summary.getExternalIdentifiers().getExternalIdentifier());
            works.getWorkGroup().add(group);
        }
        return works;
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<WorkSummary>> summaryListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private OrcidError orcidError(int code, String message) {
        OrcidError error = new OrcidError();
        error.setErrorCode(code);
        error.setResponseCode(400);
        error.setDeveloperMessage(message);
        error.setUserMessage(message);
        return error;
    }

    private Work work(Long putCode, String title, Visibility visibility, Source source) {
        Work work = new Work();
        work.setPutCode(putCode);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.JOURNAL_ARTICLE);
        work.setWorkExternalIdentifiers(externalIds("doi", String.valueOf(putCode)));
        work.setVisibility(visibility);
        work.setSource(source);
        work.setCreatedDate(createdDate());
        work.setLastModifiedDate(lastModified());
        return work;
    }

    private WorkSummary workSummary(Long putCode, String title, Visibility visibility) {
        WorkSummary summary = new WorkSummary();
        summary.setPutCode(putCode);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        summary.setTitle(workTitle);
        summary.setType(WorkType.JOURNAL_ARTICLE);
        summary.setExternalIdentifiers(externalIds("doi", String.valueOf(putCode - 10)));
        summary.setVisibility(visibility);
        summary.setSource(clientSource(CLIENT_1));
        summary.setCreatedDate(createdDate());
        summary.setLastModifiedDate(lastModified());
        return summary;
    }
}
