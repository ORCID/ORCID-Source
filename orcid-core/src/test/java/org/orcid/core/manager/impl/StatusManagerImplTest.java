package org.orcid.core.manager.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.StatusManager;
import org.orcid.persistence.dao.MiscDao;
import org.orcid.test.TargetProxyHelper;

/**
 * 
 * @author Will Simpson
 *
 */
public class StatusManagerImplTest {

    @Mock
    private MiscDao miscDao;

    @Mock
    private MiscDao miscDaoReadOnly;

    @Mock
    private Runtime runtime;

    private StatusManager statusManager = new StatusManagerImpl();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(statusManager, "miscDao", miscDao);
        TargetProxyHelper.injectIntoProxy(statusManager, "miscDaoReadOnly", miscDaoReadOnly);
        TargetProxyHelper.injectIntoProxy(statusManager, "runtime", runtime);
    }

    @Test
    public void testHeapSpaceOk() {
        when(runtime.maxMemory()).thenReturn(100L);
        when(runtime.freeMemory()).thenReturn(5L);
        when(runtime.totalMemory()).thenReturn(85L);
        when(miscDao.retrieveDatabaseDatetime()).thenReturn(new Date());
        when(miscDaoReadOnly.retrieveDatabaseDatetime()).thenReturn(new Date());
        Map<String, Boolean> statusMap = statusManager.createStatusMap();
        assertTrue(statusMap.get("heapSpaceOk"));
        assertTrue(statusMap.get("overallOk"));
    }

    @Test
    public void testHeapSpaceOkWhenTotalOverButEnoughFree() {
        when(runtime.maxMemory()).thenReturn(100L);
        when(runtime.freeMemory()).thenReturn(10L);
        when(runtime.totalMemory()).thenReturn(95L);
        when(miscDao.retrieveDatabaseDatetime()).thenReturn(new Date());
        when(miscDaoReadOnly.retrieveDatabaseDatetime()).thenReturn(new Date());
        Map<String, Boolean> statusMap = statusManager.createStatusMap();
        assertTrue(statusMap.get("heapSpaceOk"));
        assertTrue(statusMap.get("overallOk"));
    }

    @Test
    public void testHeapSpaceNotOk() {
        when(runtime.maxMemory()).thenReturn(100L);
        when(runtime.freeMemory()).thenReturn(5L);
        when(runtime.totalMemory()).thenReturn(95L);
        when(miscDao.retrieveDatabaseDatetime()).thenReturn(new Date());
        when(miscDaoReadOnly.retrieveDatabaseDatetime()).thenReturn(new Date());
        Map<String, Boolean> statusMap = statusManager.createStatusMap();
        assertFalse(statusMap.get("heapSpaceOk"));
        assertFalse(statusMap.get("overallOk"));
    }

}
