package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.read_only.InvalidRecordDataChangesManagerReadOnly;
import org.orcid.model.invalid_record_data_change.InvalidRecordDataChange;
import org.orcid.persistence.dao.InvalidRecordDataChangeDao;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;


@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class InvalidRecordDataChangesManagerReadOnlyTest {

    @Resource
    InvalidRecordDataChangesManagerReadOnly manager;
    
    @Mock
    private InvalidRecordDataChangeDao dao;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(manager, "dao", dao);
        List<InvalidRecordDataChangeEntity> elements = new ArrayList<InvalidRecordDataChangeEntity>();
        for(int i = 0; i < 3; i++) {
            InvalidRecordDataChangeEntity element = new InvalidRecordDataChangeEntity();
            element.setDateCreated(new Date());
            element.setDescription("description " + i);
            element.setId(Long.valueOf(i));
            element.setLastModified(new Date());
            element.setNumChanged(Long.valueOf(i));
            element.setSqlUsedToUpdate("select * from table");
            element.setType("type " + i);
            elements.add(element);
        }
        
        when(dao.getByDateCreated(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).thenReturn(elements);        
    }
    
    @Test
    public void getElementsAscTest() {
        List<InvalidRecordDataChange> elements = manager.getInvalidRecordDataChangesAscending(Long.valueOf(0), Long.valueOf(3));
        assertNotNull(elements);
        assertEquals(3, elements.size());
        for(InvalidRecordDataChange element : elements) {
            int i = 0;
            if(element.getSequence() == Long.valueOf(0));
            else if(element.getSequence() == Long.valueOf(1)) {
                i = 1;
            } else if(element.getSequence() == Long.valueOf(2)) {
                i = 2;
            } else {
                fail("Invalid sequence code " + element.getSequence());
            }
            
            assertNotNull(element.getDateCreated());
            assertNotNull(element.getLastModified());
            assertEquals(Long.valueOf(i), element.getSequence());
            assertEquals("description " + i, element.getDescription());
            assertEquals(Long.valueOf(i), element.getNumChanged());
            assertEquals("select * from table", element.getSqlUsedToUpdate());
            assertEquals("type " + i, element.getType());
        }
    }
    
    @Test
    public void getElementsDescTest() {
        List<InvalidRecordDataChange> elements = manager.getInvalidRecordDataChangesDescending(Long.valueOf(0), Long.valueOf(3));
        assertNotNull(elements);
        assertEquals(3, elements.size());
        for(InvalidRecordDataChange element : elements) {
            int i = 0;
            if(element.getSequence() == Long.valueOf(0));
            else if(element.getSequence() == Long.valueOf(1)) {
                i = 1;
            } else if(element.getSequence() == Long.valueOf(2)) {
                i = 2;
            } else {
                fail("Invalid sequence code " + element.getSequence());
            }
            
            assertNotNull(element.getDateCreated());
            assertNotNull(element.getLastModified());
            assertEquals(Long.valueOf(i), element.getSequence());
            assertEquals("description " + i, element.getDescription());
            assertEquals(Long.valueOf(i), element.getNumChanged());
            assertEquals("select * from table", element.getSqlUsedToUpdate());
            assertEquals("type " + i, element.getType());
        }
    }
    
    @Test
    public void cacheIsWorking1Test() {
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(1), Long.valueOf(3));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(1), Long.valueOf(3));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(1), Long.valueOf(3));
        verify(dao, times(1)).getByDateCreated(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
    }
    
    @Test
    public void cacheIsWorking2Test() {
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(10), Long.valueOf(30));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(20), Long.valueOf(30));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(30), Long.valueOf(30));
        verify(dao, times(3)).getByDateCreated(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
    }
    
    @Test
    public void cacheIsWorking3Test() {
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(100), Long.valueOf(300));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(200), Long.valueOf(300));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(300), Long.valueOf(300));
        
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(100), Long.valueOf(300));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(200), Long.valueOf(300));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(300), Long.valueOf(300));
                
        verify(dao, times(3)).getByDateCreated(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
    }
}
