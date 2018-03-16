package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class InvalidRecordDataChangeDaoTest extends DBUnitTest {

    @Resource
    private InvalidRecordDataChangeDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/InvalidRecordDataChanges.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/InvalidRecordDataChanges.xml"));
    }

    @Test
    public void fetchAllDescendantTest() {
        List<InvalidRecordDataChangeEntity> onePage = dao.getByDateCreated(null, 16L, true);
        assertNotNull(onePage);
        assertEquals(16, onePage.size());
        // Assert the first one
        assertEquals(Long.valueOf(1015), onePage.get(0).getId());
        // Assert the last one
        assertEquals(Long.valueOf(1000), onePage.get(15).getId());

        Iterator<InvalidRecordDataChangeEntity> it = onePage.iterator();
        Long initial = null;
        Long next = null;
        // Verify they respect the descendant ordered
        do {
            InvalidRecordDataChangeEntity current = it.next();
            if (initial == null) {
                initial = current.getId();
                continue;
            }
            next = current.getId();
            assertTrue((initial - 1L) == next);
            initial = next;
            next = null;
        } while (it.hasNext());
    }

    @Test
    public void fetchAllAscendantTest() {
        List<InvalidRecordDataChangeEntity> onePage = dao.getByDateCreated(null, 16L, false);
        assertNotNull(onePage);
        assertEquals(16, onePage.size());
        // Assert the first one
        assertEquals(Long.valueOf(1000), onePage.get(0).getId());
        // Assert the last one
        assertEquals(Long.valueOf(1015), onePage.get(15).getId());

        Iterator<InvalidRecordDataChangeEntity> it = onePage.iterator();
        Long initial = null;
        Long next = null;
        // Verify they respect the descendant ordered
        do {
            InvalidRecordDataChangeEntity current = it.next();
            if (initial == null) {
                initial = current.getId();
                continue;
            }
            next = current.getId();
            assertTrue((initial + 1L) == next);
            initial = next;
            next = null;
        } while (it.hasNext());
    }

    /**
     * 1 Page
     */
    @Test
    public void fetchPageSize16Test() {
        int pageSize = 16;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(1, expectedNumberOfPages);
        assertEquals(0, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    /**
     * 2 Pages
     */
    @Test
    public void fetchPageSize15Test() {
        int pageSize = 15;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(2, expectedNumberOfPages);
        assertEquals(1, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize14Test() {
        int pageSize = 14;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(2, expectedNumberOfPages);
        assertEquals(2, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize13Test() {
        int pageSize = 13;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(2, expectedNumberOfPages);
        assertEquals(3, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize12Test() {
        int pageSize = 12;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(2, expectedNumberOfPages);
        assertEquals(4, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize11Test() {
        int pageSize = 11;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(2, expectedNumberOfPages);
        assertEquals(5, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize10Test() {
        int pageSize = 10;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(2, expectedNumberOfPages);
        assertEquals(6, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize9Test() {
        int pageSize = 9;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(2, expectedNumberOfPages);
        assertEquals(7, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize8Test() {
        int pageSize = 8;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(2, expectedNumberOfPages);
        assertEquals(0, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    /**
     * 3 Pages
     */
    @Test
    public void fetchPageSize7Test() {
        int pageSize = 7;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(3, expectedNumberOfPages);
        assertEquals(2, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize6Test() {
        int pageSize = 6;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(3, expectedNumberOfPages);
        assertEquals(4, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    /**
     * 4 Pages
     */
    @Test
    public void fetchPageSize5Test() {
        int pageSize = 5;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(4, expectedNumberOfPages);
        assertEquals(1, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    @Test
    public void fetchPageSize4Test() {
        int pageSize = 4;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(4, expectedNumberOfPages);
        assertEquals(0, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    /**
     * 6 Pages
     */
    @Test
    public void fetchPageSize3Test() {
        int pageSize = 3;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(6, expectedNumberOfPages);
        assertEquals(1, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    /**
     * 8 Pages
     */
    @Test
    public void fetchPageSize2Test() {
        int pageSize = 2;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(8, expectedNumberOfPages);
        assertEquals(0, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    /**
     * 16 Pages
     */
    @Test
    public void fetchPageSize1Test() {
        int pageSize = 1;
        int expectedNumberOfPages = 16 / pageSize + ((16 % pageSize == 0) ? 0 : 1);
        int expectedNumberOfElementsOnLastPage = 16 % pageSize;
        // Simple math, but lets verify it before proceeding
        assertEquals(16, expectedNumberOfPages);
        assertEquals(0, expectedNumberOfElementsOnLastPage);
        assertAllPagesDescendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
        assertAllPagesAscendingOrder(pageSize, expectedNumberOfPages, expectedNumberOfElementsOnLastPage);
    }

    private void assertAllPagesDescendingOrder(int pageSize, int expectedNumberOfPages, int expectedNumberOfElementsInLastPage) {
        int firstElementIdOnDB = 1015;
        int lastElementIdOnDB = 1000;
        Long lastSequence = null;
        List<InvalidRecordDataChangeEntity> page = dao.getByDateCreated(lastSequence, Long.valueOf(pageSize), true);

        Long firstElementId = Long.valueOf(firstElementIdOnDB);
        Long lastElementId = Long.valueOf(firstElementIdOnDB - (pageSize - 1));

        int pageCount = 0;

        do {
            pageCount++;
            // Assert the page is not null
            assertNotNull(page);
            // Check the first element is the one we expect
            assertEquals(Long.valueOf(firstElementId), page.get(0).getId());
            // Iterate over each element and verify the id is decreasing
            for (InvalidRecordDataChangeEntity element : page) {
                if (lastSequence == null) {
                    assertEquals(Long.valueOf(firstElementIdOnDB), element.getId());
                    lastSequence = element.getId();
                } else {
                    assertTrue(element.getId() == (lastSequence - 1));
                    lastSequence = element.getId();
                }
            }

            // For the last page we might have less elements than on the other
            // pages
            if (expectedNumberOfElementsInLastPage != 0 && pageCount == expectedNumberOfPages) {
                assertEquals(expectedNumberOfElementsInLastPage, page.size());
                assertEquals(Long.valueOf(lastElementIdOnDB), page.get(expectedNumberOfElementsInLastPage - 1).getId());
            } else {
                assertEquals(pageSize, page.size());
                assertEquals(Long.valueOf(lastElementId), page.get(pageSize - 1).getId());
            }

            assertEquals(lastElementId, lastSequence);
            firstElementId = lastElementId - 1;
            lastElementId = (lastElementId - pageSize) > lastElementIdOnDB ? (lastElementId - pageSize) : lastElementIdOnDB;
            page = dao.getByDateCreated(lastSequence, Long.valueOf(pageSize), true);
        } while (!page.isEmpty());
        assertEquals(expectedNumberOfPages, pageCount);
    }

    private void assertAllPagesAscendingOrder(int pageSize, int expectedNumberOfPages, int expectedNumberOfElementsInLastPage) {
        int firstElementIdOnDB = 1000;
        int lastElementIdOnDB = 1015;
        Long lastSequence = null;
        List<InvalidRecordDataChangeEntity> page = dao.getByDateCreated(lastSequence, Long.valueOf(pageSize), false);

        Long firstElementId = Long.valueOf(firstElementIdOnDB);
        Long lastElementId = Long.valueOf(firstElementIdOnDB + (pageSize - 1));

        int pageCount = 0;

        do {
            pageCount++;
            // Assert the page is not null
            assertNotNull(page);
            // Check the first element is the one we expect
            assertEquals(Long.valueOf(firstElementId), page.get(0).getId());
            // Iterate over each element and verify the id is decreasing
            for (InvalidRecordDataChangeEntity element : page) {
                if (lastSequence == null) {
                    assertEquals(Long.valueOf(firstElementIdOnDB), element.getId());
                    lastSequence = element.getId();
                } else {
                    assertTrue(element.getId() == (lastSequence + 1));
                    lastSequence = element.getId();
                }
            }

            // For the last page we might have less elements than on the other
            // pages
            if (expectedNumberOfElementsInLastPage != 0 && pageCount == expectedNumberOfPages) {
                assertEquals(expectedNumberOfElementsInLastPage, page.size());
                assertEquals(Long.valueOf(lastElementIdOnDB), page.get(expectedNumberOfElementsInLastPage - 1).getId());
            } else {
                assertEquals(pageSize, page.size());
                assertEquals(Long.valueOf(lastElementId), page.get(pageSize - 1).getId());
            }

            assertEquals(lastElementId, lastSequence);
            firstElementId = lastElementId + 1;
            lastElementId = (lastElementId + pageSize) < lastElementIdOnDB ? (lastElementId + pageSize) : lastElementIdOnDB;
            page = dao.getByDateCreated(lastSequence, Long.valueOf(pageSize), false);
        } while (!page.isEmpty());
        assertEquals(expectedNumberOfPages, pageCount);
    }

    @Test
    public void haveNextTest() {
        //Descendant order
        assertTrue(dao.haveNext(1015L, true));
        assertTrue(dao.haveNext(1008L, true));
        assertTrue(dao.haveNext(1001L, true));
        assertFalse(dao.haveNext(1000L, true));
        assertFalse(dao.haveNext(0L, true));
        
        //Ascendant order
        assertFalse(dao.haveNext(1015L, false));
        assertFalse(dao.haveNext(1016L, false));
        assertTrue(dao.haveNext(1000L, false));
        assertTrue(dao.haveNext(1001L, false));
        assertTrue(dao.haveNext(1008L, false));
        assertTrue(dao.haveNext(1014L, false));
    }

    @Test
    public void havePreviousTest() {
        //Descendant order
        assertFalse(dao.havePrevious(1015L, true));
        assertFalse(dao.havePrevious(1020L, true));
        assertTrue(dao.havePrevious(1014L, true));
        assertTrue(dao.havePrevious(1008L, true));
        assertTrue(dao.havePrevious(1000L, true));
        
        //Ascendant order
        assertFalse(dao.havePrevious(1000L, false));
        assertFalse(dao.havePrevious(-1L, false));
        assertFalse(dao.havePrevious(-10L, false));
        assertTrue(dao.havePrevious(1001L, false));
        assertTrue(dao.havePrevious(1008L, false));
        assertTrue(dao.havePrevious(1014L, false));
        assertTrue(dao.havePrevious(1015L, false));
    }
    
    @Test
    public void invalidValueAscendingTest() {
        List<InvalidRecordDataChangeEntity> elements = dao.getByDateCreated(1015L, 5L, false);
        assertNotNull(elements);
        assertTrue(elements.isEmpty());
        
        elements = dao.getByDateCreated(1016L, 5L, false);
        assertNotNull(elements);
        assertTrue(elements.isEmpty());
        
        elements = dao.getByDateCreated(2000L, 5L, false);
        assertNotNull(elements);
        assertTrue(elements.isEmpty());
    }
    
    @Test
    public void invalidValueDescendingTest() {
        List<InvalidRecordDataChangeEntity> elements = dao.getByDateCreated(1000L, 5L, true);
        assertNotNull(elements);
        assertTrue(elements.isEmpty());
        
        elements = dao.getByDateCreated(900L, 5L, true);
        assertNotNull(elements);
        assertTrue(elements.isEmpty());
        
        elements = dao.getByDateCreated(0L, 5L, true);
        assertNotNull(elements);
        assertTrue(elements.isEmpty());
    }
}
