/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.controllers;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.model.record_correction.RecordCorrectionsPage;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
public class RecordCorrectionsControllerTest extends DBUnitTest {

    @Resource
    private RecordCorrectionsController controller;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/InvalidRecordDataChanges.xml"));
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
    
    @Test(expected = IllegalArgumentException.class)
    public void previousOnFirstPageTest() {
        controller.getPreviousDescending(Optional.of(1015L));
        fail();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void nextOnLastPageTest() {
        controller.getNextDescending(Optional.of(1000L));
        fail();
    }
    
    @Test
    public void previousOnLastPageTest() {
        RecordCorrectionsPage page1 = controller.getPreviousDescending(Optional.empty());
        assertNotNull(page1);
        assertTrue(page1.getHavePrevious());
        assertFalse(page1.getHaveNext());
        assertEquals(Long.valueOf(1000), page1.getFirstElementId());
        assertEquals(Long.valueOf(1009), page1.getLastElementId());
        assertNotNull(page1.getRecordCorrections());
        assertEquals(10, page1.getRecordCorrections().size());
        RecordCorrectionsPage page2 = controller.getPreviousDescending(Optional.of(page1.getLastElementId()));
        assertNotNull(page2);
        assertFalse(page2.getHavePrevious());
        assertTrue(page2.getHaveNext());
        assertEquals(Long.valueOf(1010), page2.getFirstElementId());
        assertEquals(Long.valueOf(1015), page2.getLastElementId());
        assertNotNull(page2.getRecordCorrections());
        assertEquals(6, page2.getRecordCorrections().size());
    }
    
    @Test
    public void navigateTest() {
        
    }
}
