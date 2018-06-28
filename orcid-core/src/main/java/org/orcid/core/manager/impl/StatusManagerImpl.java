package org.orcid.core.manager.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import org.orcid.core.manager.StatusManager;
import org.orcid.persistence.dao.MiscDao;

/**
 * 
 * @author Will Simpson
 *
 */
public class StatusManagerImpl implements StatusManager {

    private static final String OVERALL_OK = "overallOk";
    private static final String READ_ONLY_DB_CONNECTION_OK = "readOnlyDbConnectionOk";
    private static final String DB_CONNECTION_OK = "dbConnectionOk";
    private static final String TOMCAT_UP = "tomcatUp";
    private static final String HEAP_SPACE_OK = "heapSpaceOk";

    @Resource(name = "miscDao")
    private MiscDao miscDao;

    @Resource(name = "miscDaoReadOnly")
    private MiscDao miscDaoReadOnly;
    
    @Resource
    private Runtime runtime;

    @Override
    public Map<String, Boolean> createStatusMap() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        result.put(TOMCAT_UP, true);
        result.put(DB_CONNECTION_OK, isConnectionOk(miscDao));
        result.put(READ_ONLY_DB_CONNECTION_OK, isConnectionOk(miscDaoReadOnly));
        result.put(HEAP_SPACE_OK, isHeapSpaceOk());
        Boolean overall = result.values().stream().filter(v -> !v).findAny().orElse(true);
        result.put(OVERALL_OK, overall);
        return result;
    }

    private boolean isHeapSpaceOk() {
        // total memory is the amount currently allocated to the jvm (can go up
        // and down at runtime, depending on heap size)
        long totalMemory = runtime.totalMemory();
        // free memory is the amount of allocated memory that is not used by
        // heap objects
        long freeMemory = runtime.freeMemory();
        // used memory is the amount of memory actually used by heap objects
        long usedMemory = totalMemory - freeMemory;
        // max memory is set by -Xmx
        long maxMemory = runtime.maxMemory();
        return usedMemory < maxMemory * 0.9;
    }

    private boolean isConnectionOk(MiscDao miscDao) {
        try {
            Date dbDate = miscDao.retrieveDatabaseDatetime();
            if (dbDate != null) {
                return true;
            }
        } catch (PersistenceException e) {
            return false;
        }
        return false;
    }

}
