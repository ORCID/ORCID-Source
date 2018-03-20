package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.read_only.RecordCorrectionsManagerReadOnly;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.model.record_correction.RecordCorrectionsPage;
import org.orcid.persistence.dao.InvalidRecordDataChangeDao;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class RecordCorrectionsManagerReadOnlyTest {

    @Resource
    private RecordCorrectionsManagerReadOnly manager;

    @Mock
    private InvalidRecordDataChangeDao dao;

    /**
     * Simulates a list of 10 record corrections from 1 to 10.     
     * */
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(manager, "dao", dao);
        when(dao.getByDateCreated(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
                .then(new Answer<List<InvalidRecordDataChangeEntity>>() {
                    @Override
                    public List<InvalidRecordDataChangeEntity> answer(InvocationOnMock invocation) throws Throwable {
                        Long first = (Long) invocation.getArgument(0);
                        Long size = (Long) invocation.getArgument(1);
                        Boolean order = (Boolean) invocation.getArgument(2);
                        List<InvalidRecordDataChangeEntity> elements = new ArrayList<InvalidRecordDataChangeEntity>();
                        if (order) {
                            for (long i = first; i > (first - size); i--) {
                                if(i < 1) {
                                    break;
                                }
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
                        } else {
                            for (long i = first; i < (first + size); i++) {
                                if(i > 10) {
                                    break;
                                }
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
                        }
                        return elements;
                    }
                });
        when(dao.haveNext(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Long sequence = (Long) invocation.getArgument(0);
                Boolean order = (Boolean) invocation.getArgument(1);
                if (order) {
                    if (sequence <= 1L) {
                        return false;
                    }
                } else {
                    if (sequence >= 10) {
                        return false;
                    }
                }
                return true;
            }
        });

        when(dao.havePrevious(ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean())).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Long sequence = (Long) invocation.getArgument(0);
                Boolean order = (Boolean) invocation.getArgument(1);
                if (order) {
                    if (sequence >= 10L) {
                        return false;
                    }
                } else {
                    if (sequence <= 1L) {
                        return false;
                    }
                }
                return true;
            }
        });
        manager.cacheEvict();
    }

    @Test
    public void getElementsDescendingTest() {
        // Page 1
        RecordCorrectionsPage page1 = manager.getInvalidRecordDataChangesDescending(10L, 4L);
        assertNotNull(page1);
        assertEquals(Long.valueOf(10), page1.getFirstElementId());
        assertEquals(Long.valueOf(7), page1.getLastElementId());
        assertTrue(page1.getHaveNext());
        assertFalse(page1.getHavePrevious());
        assertNotNull(page1.getRecordCorrections());
        assertEquals(4, page1.getRecordCorrections().size());
        Long currentId = null;
        for (RecordCorrection element : page1.getRecordCorrections()) {
            if (currentId == null) {
                assertEquals(page1.getFirstElementId(), element.getSequence());
                currentId = element.getSequence();
            } else {
                assertTrue((currentId - 1) == element.getSequence());
                currentId = element.getSequence();
            }
        }
        assertEquals(page1.getLastElementId(), currentId);

        // Page 2
        RecordCorrectionsPage page2 = manager.getInvalidRecordDataChangesDescending(page1.getLastElementId() - 1, 4L);
        assertNotNull(page2);
        assertEquals(Long.valueOf(6), page2.getFirstElementId());
        assertEquals(Long.valueOf(3), page2.getLastElementId());
        assertTrue(page2.getHaveNext());
        assertTrue(page2.getHavePrevious());
        assertNotNull(page2.getRecordCorrections());
        assertEquals(4, page2.getRecordCorrections().size());
        currentId = null;
        for (RecordCorrection element : page2.getRecordCorrections()) {
            if (currentId == null) {
                assertEquals(page2.getFirstElementId(), element.getSequence());
                currentId = element.getSequence();
            } else {
                assertTrue((currentId - 1) == element.getSequence());
                currentId = element.getSequence();
            }
        }
        assertEquals(page2.getLastElementId(), currentId);
        
        //Page 3
        RecordCorrectionsPage page3 = manager.getInvalidRecordDataChangesDescending(page2.getLastElementId() - 1, 4L);
        assertNotNull(page3);
        assertEquals(Long.valueOf(2), page3.getFirstElementId());
        assertEquals(Long.valueOf(1), page3.getLastElementId());
        assertFalse(page3.getHaveNext());
        assertTrue(page3.getHavePrevious());
        assertEquals(2, page3.getRecordCorrections().size());
        
        currentId = null;
        for (RecordCorrection element : page3.getRecordCorrections()) {
            if (currentId == null) {
                assertEquals(page3.getFirstElementId(), element.getSequence());
                currentId = element.getSequence();
            } else {
                assertTrue((currentId - 1) == element.getSequence());
                currentId = element.getSequence();
            }
        }
        assertEquals(page3.getLastElementId(), currentId);
    }

    @Test
    public void getElementsAscendingTest() {
        // Page 1
        RecordCorrectionsPage page1 = manager.getInvalidRecordDataChangesAscending(1L, 4L);
        assertNotNull(page1);
        assertEquals(Long.valueOf(1), page1.getFirstElementId());
        assertEquals(Long.valueOf(4), page1.getLastElementId());
        assertTrue(page1.getHaveNext());
        assertFalse(page1.getHavePrevious());
        assertNotNull(page1.getRecordCorrections());
        assertEquals(4, page1.getRecordCorrections().size());
        Long currentId = null;
        for (RecordCorrection element : page1.getRecordCorrections()) {
            if (currentId == null) {
                assertEquals(page1.getFirstElementId(), element.getSequence());
                currentId = element.getSequence();
            } else {
                assertTrue((currentId + 1) == element.getSequence());
                currentId = element.getSequence();
            }
        }
        assertEquals(page1.getLastElementId(), currentId);

        // Page 2
        RecordCorrectionsPage page2 = manager.getInvalidRecordDataChangesAscending(page1.getLastElementId() + 1, 4L);
        assertNotNull(page2);
        assertEquals(Long.valueOf(5), page2.getFirstElementId());
        assertEquals(Long.valueOf(8), page2.getLastElementId());
        assertTrue(page2.getHaveNext());
        assertTrue(page2.getHavePrevious());
        assertNotNull(page2.getRecordCorrections());
        assertEquals(4, page2.getRecordCorrections().size());
        currentId = null;
        for (RecordCorrection element : page2.getRecordCorrections()) {
            if (currentId == null) {
                assertEquals(page2.getFirstElementId(), element.getSequence());
                currentId = element.getSequence();
            } else {
                assertTrue((currentId + 1) == element.getSequence());
                currentId = element.getSequence();
            }
        }
        assertEquals(page2.getLastElementId(), currentId);
        
        // Page 3
        RecordCorrectionsPage page3 = manager.getInvalidRecordDataChangesAscending(page2.getLastElementId() + 1, 4L);
        assertNotNull(page3);
        assertEquals(Long.valueOf(9), page3.getFirstElementId());
        assertEquals(Long.valueOf(10), page3.getLastElementId());
        assertFalse(page3.getHaveNext());
        assertTrue(page3.getHavePrevious());
        assertNotNull(page3.getRecordCorrections());
        assertEquals(2, page3.getRecordCorrections().size());
        currentId = null;
        for (RecordCorrection element : page3.getRecordCorrections()) {
            if (currentId == null) {
                assertEquals(page3.getFirstElementId(), element.getSequence());
                currentId = element.getSequence();
            } else {
                assertTrue((currentId + 1) == element.getSequence());
                currentId = element.getSequence();
            }
        }
        assertEquals(page3.getLastElementId(), currentId);
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
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(1), Long.valueOf(3));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(2), Long.valueOf(3));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(3), Long.valueOf(3));
        verify(dao, times(3)).getByDateCreated(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void cacheIsWorking3Test() {
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(1), Long.valueOf(3));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(2), Long.valueOf(3));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(3), Long.valueOf(3));

        manager.getInvalidRecordDataChangesDescending(Long.valueOf(1), Long.valueOf(3));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(2), Long.valueOf(3));
        manager.getInvalidRecordDataChangesDescending(Long.valueOf(3), Long.valueOf(3));

        verify(dao, times(3)).getByDateCreated(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void invalidValueOnNextTest() {
        manager.getInvalidRecordDataChangesDescending(0L, 5L);
        fail();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void invalidValueOnPreviousTest() {
        manager.getInvalidRecordDataChangesAscending(11L, 5L);
        fail();
    }
}
