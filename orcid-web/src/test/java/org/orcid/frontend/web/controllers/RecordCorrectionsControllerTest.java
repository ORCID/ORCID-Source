package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.model.record_correction.RecordCorrectionsPage;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class RecordCorrectionsControllerTest extends DBUnitTest {

    @Resource
    private RecordCorrectionsController controller;
    
    @Mock
    private OrcidSecurityManager securityMgr;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/InvalidRecordDataChanges.xml"));
    }
    
    @Before
    public void before() {
        controller.evictCache();
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(controller, "securityMgr", securityMgr);
        when(securityMgr.isAdmin()).thenReturn(true);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/InvalidRecordDataChanges.xml"));
    }
    
    @Test
    public void nextOnFirstPageTest() {
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
        RecordCorrectionsPage page1 = controller.getPreviousDescending(Optional.empty());
        assertNotNull(page1);
        assertTrue(page1.getHavePrevious());
        assertFalse(page1.getHaveNext());
        assertEquals(Long.valueOf(1009), page1.getFirstElementId());
        assertEquals(Long.valueOf(1000), page1.getLastElementId());
        assertNotNull(page1.getRecordCorrections());
        assertEquals(10, page1.getRecordCorrections().size());
        RecordCorrectionsPage page2 = controller.getPreviousDescending(Optional.of(page1.getFirstElementId()));
        assertNotNull(page2);
        assertFalse(page2.getHavePrevious());
        assertTrue(page2.getHaveNext());
        assertEquals(Long.valueOf(1015), page2.getFirstElementId());
        assertEquals(Long.valueOf(1010), page2.getLastElementId());
        assertNotNull(page2.getRecordCorrections());
        assertEquals(6, page2.getRecordCorrections().size());
    }    
}
