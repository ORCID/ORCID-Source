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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.orcid.core.adapter.MockedMapStructAdapters;
import org.orcid.core.adapter.mapstruct.impl.JpaJaxbInvalidRecordDataChangeAdapterImpl;
import org.orcid.core.manager.read_only.RecordCorrectionsManagerReadOnly;
import org.orcid.core.manager.read_only.impl.RecordCorrectionsManagerReadOnlyImpl;
import org.orcid.model.record_correction.RecordCorrection;
import org.orcid.model.record_correction.RecordCorrectionsPage;
import org.orcid.persistence.dao.InvalidRecordDataChangeDao;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * RecordCorrectionsManagerReadOnlyImpl is built here by hand instead of pulled from the
 * orcid-core Spring context, but two of its collaborators stay real because they are what the
 * assertions are about.
 *
 * <p>
 * <b>The caching proxy is real.</b> Three of the seven tests -- cacheIsWorking1Test,
 * cacheIsWorking2Test and cacheIsWorking3Test -- assert how many times the DAO is reached for a
 * given sequence of calls, and the only thing that makes that number smaller than the number of
 * calls is Spring's {@code @Cacheable} interception and the SpEL key on it. Calling the bare
 * implementation would hit the DAO once per call and those three tests would fail; stubbing a
 * cache would decide the answer they are asking for. So the object under test is wrapped in the
 * same {@link CacheInterceptor} Spring's {@code <cache:annotation-driven/>} installs, reading the
 * same annotations off the same class, over an in-memory {@link ConcurrentMapCacheManager}
 * instead of the application's cache. What the context provided and this does not is a database
 * and the other nine hundred beans.
 *
 * <p>
 * {@code afterSingletonsInstantiated()} is not optional: CacheAspectSupport.execute falls
 * straight through to the target method until that call flips its {@code initialized} flag, so
 * without it the proxy would silently do no caching at all and the three cache tests would fail
 * with a DAO call count equal to the call count.
 *
 * <p>
 * <b>The adapter is real</b> too, over the real Orika facade from
 * {@link MockedMapStructAdapters}: the paging assertions read
 * {@code RecordCorrection.getSequence()}, which is produced by the entity-to-model mapping, and a
 * mocked adapter would have to be handed the RecordCorrections the test then inspects.
 *
 * <p>
 * Silent runner, and NOT because of the three stubs in {@code @Before}. Mockito groups stubbings
 * by source location and counts a location as used when any one test method used it -- see
 * {@code UnusedStubbingsFinder.getUnusedStubbingsByLocation} -- so getByDateCreated, haveNext and
 * havePrevious are all covered by the two paging tests even though the two invalid-argument tests
 * throw before reaching haveNext / havePrevious. What the strict runner WOULD report is the two
 * stubs in the {@link MockedMapStructAdapters} constructor, sourceNameCacheManager.retrieve and
 * clientDetailsEntityCacheManager.retrieve: they are created while the runner's listener is
 * attached, and this mapping registers no source converters, so neither is ever called. Every
 * test built on that factory runs Silent for this reason.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class RecordCorrectionsManagerReadOnlyTest {

    private final MockedMapStructAdapters adapters = new MockedMapStructAdapters();

    @Mock
    private InvalidRecordDataChangeDao dao;

    private RecordCorrectionsManagerReadOnly manager;

    /**
     * Simulates a list of 10 record corrections from 1 to 10.     
     * */
    @Before
    public void before() {
        manager = cachingProxyOver(newManager());
        when(dao.getByDateCreated(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
                .then(new Answer<List<InvalidRecordDataChangeEntity>>() {
                    @Override
                    public List<InvalidRecordDataChangeEntity> answer(InvocationOnMock invocation) throws Throwable {
                        Long first = (Long) invocation.getArgument(0);
                        Long size = (Long) invocation.getArgument(1);
                        Boolean order = (Boolean) invocation.getArgument(2);
                        Date now = new Date();
                        List<InvalidRecordDataChangeEntity> elements = new ArrayList<InvalidRecordDataChangeEntity>();
                        if (order) {
                            for (long i = first; i > (first - size); i--) {
                                if(i < 1) {
                                    break;
                                }
                                InvalidRecordDataChangeEntity element = new InvalidRecordDataChangeEntity();
                                DateFieldsOnBaseEntityUtils.setDateFields(element, now);
                                element.setDescription("description " + i);
                                element.setId(Long.valueOf(i));
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
                                DateFieldsOnBaseEntityUtils.setDateFields(element, now);                                
                                element.setDescription("description " + i);
                                element.setId(Long.valueOf(i));                                
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

    private RecordCorrectionsManagerReadOnlyImpl newManager() {
        JpaJaxbInvalidRecordDataChangeAdapterImpl adapter = adapters.get(JpaJaxbInvalidRecordDataChangeAdapterImpl.class);

        RecordCorrectionsManagerReadOnlyImpl impl = new RecordCorrectionsManagerReadOnlyImpl();
        ReflectionTestUtils.setField(impl, "dao", dao);
        ReflectionTestUtils.setField(impl, "adapter", adapter);
        return impl;
    }

    /**
     * The {@code @Cacheable} / {@code @CacheEvict} interception the three cache tests assert on,
     * built standalone: the same interceptor and the same annotation-reading operation source
     * Spring installs, over a fresh in-memory cache manager. Fresh per test, so no test can see
     * another test's entries.
     */
    private RecordCorrectionsManagerReadOnly cachingProxyOver(RecordCorrectionsManagerReadOnlyImpl target) {
        CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.setCacheOperationSource(new AnnotationCacheOperationSource());
        interceptor.setCacheManager(new ConcurrentMapCacheManager("invalid-record-data-change-page-desc", "invalid-record-data-change-page-asc"));
        interceptor.afterPropertiesSet();
        // Flips CacheAspectSupport.initialized; until it is set the interceptor is a no-op.
        interceptor.afterSingletonsInstantiated();

        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(interceptor);
        return (RecordCorrectionsManagerReadOnly) proxyFactory.getProxy();
    }
}
