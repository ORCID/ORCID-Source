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
package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
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
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangesEntity;
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
public class InvalidRecordDataChangesDaoTest extends DBUnitTest {

    @Resource
    private InvalidRecordDataChangesDao dao;
    
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
        List<InvalidRecordDataChangesEntity> onePage = dao.getByDateCreated(null, 16L, true);
        assertNotNull(onePage);
        assertEquals(16, onePage.size());
        //Assert the first one
        assertEquals(Long.valueOf(1015), onePage.get(0).getId());
        //Assert the last one
        assertEquals(Long.valueOf(1000), onePage.get(15).getId());
        
        Iterator<InvalidRecordDataChangesEntity> it = onePage.iterator();
        Long initial = null;
        Long next = null;
        //Verify they respect the descendant ordered
        do {
            InvalidRecordDataChangesEntity current = it.next();
            if(initial == null) {
                initial = current.getId();
                continue;
            } 
            next = current.getId();
            assertTrue((initial - 1L) == next);
            initial = next;
            next = null;
        } while(it.hasNext());                                
    }
    
    @Test
    public void fetchAllAscendantTest() {
        List<InvalidRecordDataChangesEntity> onePage = dao.getByDateCreated(null, 16L, false);
        assertNotNull(onePage);
        assertEquals(16, onePage.size());
        //Assert the first one
        assertEquals(Long.valueOf(1000), onePage.get(0).getId());
        //Assert the last one
        assertEquals(Long.valueOf(1015), onePage.get(15).getId());
        
        Iterator<InvalidRecordDataChangesEntity> it = onePage.iterator();
        Long initial = null;
        Long next = null;
        //Verify they respect the descendant ordered
        do {
            InvalidRecordDataChangesEntity current = it.next();
            if(initial == null) {
                initial = current.getId();
                continue;
            } 
            next = current.getId();
            assertTrue((initial + 1L) == next);
            initial = next;
            next = null;
        } while(it.hasNext());                                
    }
    
    @Test
    public void fetchTwoPagesTest() {
        List<InvalidRecordDataChangesEntity> page1 = dao.getByDateCreated(null, 8L, true);
        assertNotNull(page1);
        assertEquals(8, page1.size());
        for(int i = 1; i <= page1.size(); i++) {
            InvalidRecordDataChangesEntity entity = page1.get(i - 1);
            assertEquals(Long.valueOf(1000 + i), entity.getId());
            assertEquals("test query # " + i, entity.getDescription());
        }
        
        //Assert the last one is 8th element
        InvalidRecordDataChangesEntity lastElement = page1.get(page1.size() - 1);
        assertEquals("test query # 8", lastElement.getDescription());
        
        List<InvalidRecordDataChangesEntity> page2 = dao.getByDateCreated(lastElement.getId(), 8L, true);
        assertNotNull(page2);
        assertEquals(8, page2.size());
        for(int i = 1; i <= page1.size(); i++) {
            InvalidRecordDataChangesEntity entity = page2.get(i - 1);
            assertEquals(Long.valueOf(1000 + 8 + i), entity.getId());
            assertEquals("test query # " + (8 + i), entity.getDescription());
        }
        
        lastElement = page2.get(page2.size() - 1);
        assertEquals("test query # 16", lastElement.getDescription());
        List<InvalidRecordDataChangesEntity> page3 = dao.getByDateCreated(lastElement.getId(), 8L, true);
        assertNotNull(page3);
        assertTrue(page3.isEmpty());
    }
    
    @Test
    public void fetchThreePagesTest() {
        
    }
    
    @Test
    public void fetchFourPagesTest() {
        
    }
    
    @Test
    public void fetchFivePagesTest() {
        
    }
}
