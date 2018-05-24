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
    
    @Resource(name= "miscDao")
    private MiscDao miscDao;
    
    @Resource(name= "miscDaoReadOnly")
    private MiscDao miscDaoReadOnly;
    
    @Override
    public Map<String, Boolean> createStatusMap() {
        Map<String, Boolean> result = new LinkedHashMap<>();
        result.put(TOMCAT_UP, true);
        result.put(DB_CONNECTION_OK, isConnectionOk(miscDao));
        result.put(READ_ONLY_DB_CONNECTION_OK, isConnectionOk(miscDaoReadOnly));
        Boolean overall = result.values().stream().filter(v -> !v).findAny().orElse(true);
        result.put(OVERALL_OK, overall);
        return result;
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
