package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.read_only.RecordCorrectionsManagerReadOnly;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.model.record_correction.RecordCorrectionsPage;
import org.springframework.web.servlet.ModelAndView;

/**
 * The page contents themselves -- which ids land on which page, and the 10/6
 * split -- are the DAO's ORDER BY plus row limit against
 * /data/InvalidRecordDataChanges.xml, and a mocked manager cannot prove them;
 * they are supplied here as stub values so the delegation stays covered, and
 * belong in an InvalidRecordDataChangeDao database test. What these tests do
 * still prove on their own is the controller's own work: the ascending-to-
 * descending inversion in getPreviousDescending (list order, next/previous swap
 * and first/last swap), the IllegalArgumentException-to-empty-page fallback,
 * and the admin gate on evictCache.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class RecordCorrectionsControllerTest {

    private static final Long PAGE_SIZE = 10L;

    @Mock
    private RecordCorrectionsManagerReadOnly manager;

    @Mock
    private OrcidSecurityManager securityMgr;

    @Mock
    private OrcidUrlManager orcidUrlManager;

    @InjectMocks
    private RecordCorrectionsController controller = new RecordCorrectionsController();

    @Before
    public void before() {
        when(securityMgr.isAdmin()).thenReturn(true);
        when(orcidUrlManager.getBaseDomainRmProtocall()).thenReturn("testserver.orcid.org");
        // Populated from a @Value setter in production; evictCache() and
        // recordCorrections() call contains() on it unconditionally.
        controller.setDomainsAllowingRobots(Arrays.asList("testserver.orcid.org"));
    }

    @Test
    public void nextOnFirstPageTest() {
        when(manager.getInvalidRecordDataChangesDescending(null, PAGE_SIZE)).thenReturn(page(1015L, 1006L, true, false, descending(1015L, 1006L)));
        when(manager.getInvalidRecordDataChangesDescending(1006L, PAGE_SIZE)).thenReturn(page(1005L, 1000L, false, true, descending(1005L, 1000L)));

        RecordCorrectionsPage page1 = controller.getNextDescending(Optional.empty());
        assertNotNull(page1);
        assertFalse(page1.getHavePrevious());
        assertTrue(page1.getHaveNext());
        assertEquals(Long.valueOf(1015), page1.getFirstElementId());
        assertEquals(Long.valueOf(1006), page1.getLastElementId());
        assertNotNull(page1.getRecordCorrections());
        assertEquals(10, page1.getRecordCorrections().size());
        RecordCorrectionsPage page2 = controller.getNextDescending(Optional.of(page1.getLastElementId()));
        assertNotNull(page2);
        assertTrue(page2.getHavePrevious());
        assertFalse(page2.getHaveNext());
        assertEquals(Long.valueOf(1005), page2.getFirstElementId());
        assertEquals(Long.valueOf(1000), page2.getLastElementId());
        assertNotNull(page2.getRecordCorrections());
        assertEquals(6, page2.getRecordCorrections().size());
    }

    @Test
    public void previousOnFirstPageTest() {
        when(manager.getInvalidRecordDataChangesAscending(1015L, PAGE_SIZE)).thenThrow(new IllegalArgumentException("no such page"));

        RecordCorrectionsPage page = controller.getPreviousDescending(Optional.of(1015L));
        assertNotNull(page);
        assertNull(page.getFirstElementId());
        assertNull(page.getLastElementId());
        assertFalse(page.getHaveNext());
        assertFalse(page.getHavePrevious());
        assertNull(page.getLastElementId());
    }

    @Test
    public void nextOnLastPageTest() {
        when(manager.getInvalidRecordDataChangesDescending(1000L, PAGE_SIZE)).thenThrow(new IllegalArgumentException("no such page"));

        RecordCorrectionsPage page = controller.getNextDescending(Optional.of(1000L));
        assertNotNull(page);
        assertNull(page.getFirstElementId());
        assertNull(page.getLastElementId());
        assertFalse(page.getHaveNext());
        assertFalse(page.getHavePrevious());
        assertNull(page.getLastElementId());
    }

    @Test
    public void previousOnLastPageTest() {
        // The manager answers in ascending order; everything the controller does
        // to it below is the inversion under test.
        when(manager.getInvalidRecordDataChangesAscending(null, PAGE_SIZE)).thenReturn(page(1000L, 1009L, true, false, ascending(1000L, 1009L)));
        when(manager.getInvalidRecordDataChangesAscending(1009L, PAGE_SIZE)).thenReturn(page(1010L, 1015L, false, true, ascending(1010L, 1015L)));

        RecordCorrectionsPage page1 = controller.getPreviousDescending(Optional.empty());
        assertNotNull(page1);
        assertTrue(page1.getHavePrevious());
        assertFalse(page1.getHaveNext());
        assertEquals(Long.valueOf(1009), page1.getFirstElementId());
        assertEquals(Long.valueOf(1000), page1.getLastElementId());
        assertNotNull(page1.getRecordCorrections());
        assertEquals(10, page1.getRecordCorrections().size());
        // The elements themselves were reversed, not just the boundary ids.
        assertEquals(Long.valueOf(1009), page1.getRecordCorrections().get(0).getSequence());
        assertEquals(Long.valueOf(1000), page1.getRecordCorrections().get(9).getSequence());
        RecordCorrectionsPage page2 = controller.getPreviousDescending(Optional.of(page1.getFirstElementId()));
        assertNotNull(page2);
        assertFalse(page2.getHavePrevious());
        assertTrue(page2.getHaveNext());
        assertEquals(Long.valueOf(1015), page2.getFirstElementId());
        assertEquals(Long.valueOf(1010), page2.getLastElementId());
        assertNotNull(page2.getRecordCorrections());
        assertEquals(6, page2.getRecordCorrections().size());
        assertEquals(Long.valueOf(1015), page2.getRecordCorrections().get(0).getSequence());
    }

    @Test
    public void evictCache_adminEvictsTest() {
        when(securityMgr.isAdmin()).thenReturn(true);

        ModelAndView mav = controller.evictCache();

        assertEquals("record-corrections", mav.getViewName());
        verify(manager).cacheEvict();
    }

    /**
     * RecordCorrectionsController.evictCache is gated on securityMgr.isAdmin();
     * nothing exercised the refusal.
     */
    @Test
    public void evictCache_noAdminTest() {
        when(securityMgr.isAdmin()).thenReturn(false);

        ModelAndView mav = controller.evictCache();

        assertEquals("record-corrections", mav.getViewName());
        verify(manager, never()).cacheEvict();
    }

    private RecordCorrectionsPage page(Long firstElementId, Long lastElementId, boolean haveNext, boolean havePrevious, List<RecordCorrection> elements) {
        RecordCorrectionsPage page = new RecordCorrectionsPage();
        page.setFirstElementId(firstElementId);
        page.setLastElementId(lastElementId);
        page.setHaveNext(haveNext);
        page.setHavePrevious(havePrevious);
        page.setRecordCorrections(elements);
        return page;
    }

    private List<RecordCorrection> descending(long from, long to) {
        List<RecordCorrection> elements = new ArrayList<>();
        for (long id = from; id >= to; id--) {
            elements.add(element(id));
        }
        return elements;
    }

    private List<RecordCorrection> ascending(long from, long to) {
        List<RecordCorrection> elements = new ArrayList<>();
        for (long id = from; id <= to; id++) {
            elements.add(element(id));
        }
        return elements;
    }

    private RecordCorrection element(long sequence) {
        RecordCorrection element = new RecordCorrection();
        element.setSequence(sequence);
        element.setDescription("Invalid record data change " + sequence);
        return element;
    }
}
