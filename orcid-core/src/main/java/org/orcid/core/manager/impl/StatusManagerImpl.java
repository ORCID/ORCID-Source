package org.orcid.core.manager.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import org.orcid.core.manager.StatusManager;
import org.orcid.persistence.dao.MiscDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class StatusManagerImpl implements StatusManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatusManagerImpl.class);
    
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
        if (!overall) {
            LOGGER.error("Status check failed: " + result);
        }
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
