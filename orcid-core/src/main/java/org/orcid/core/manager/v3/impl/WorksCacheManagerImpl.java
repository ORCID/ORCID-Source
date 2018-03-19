package org.orcid.core.manager.v3.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.record.summary.Works;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.util.TimeUtil;

public class WorksCacheManagerImpl implements WorksCacheManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorksCacheManagerImpl.class);  

    // Default time to live will be one hour
    private static final int DEFAULT_TTL = 3600;

    // Default time to idle will be one hour
    private static final int DEFAULT_TTI = 3600;

    @Resource(name = "groupedWorksCache")
    private Cache groupedWorksCache;
    private int groupedWorksCacheTTL;
    private int groupedWorksCacheTTI;
    
    @PostConstruct
    private void init() {
        CacheConfiguration config1 = groupedWorksCache.getCacheConfiguration();
        groupedWorksCacheTTI = config1.getTimeToIdleSeconds() > 0 ? TimeUtil.convertTimeToInt(config1.getTimeToIdleSeconds()) : DEFAULT_TTI;
        groupedWorksCacheTTL = config1.getTimeToLiveSeconds() > 0 ? TimeUtil.convertTimeToInt(config1.getTimeToLiveSeconds()) : DEFAULT_TTL;
    }

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Override
    public Works getGroupedWorks(String orcid) {
        Object key = new WorksCacheKey(orcid, profileEntityManagerReadOnly.getLastModified(orcid));
        Works groupedWorks = null;
        try {
            groupedWorksCache.acquireReadLockOnKey(key);
            groupedWorks = toWorks(getElementFromCache(key, orcid));
        } finally {
            groupedWorksCache.releaseReadLockOnKey(key);
        }
        if (groupedWorks == null) {
            try {
                groupedWorksCache.acquireWriteLockOnKey(key);
                groupedWorks = toWorks(getElementFromCache(key, orcid));
                if (groupedWorks == null) {
                    groupedWorks = workManagerReadOnly.getWorksAsGroups(orcid);
                    groupedWorksCache.put(new Element(key, groupedWorks, groupedWorksCacheTTI, groupedWorksCacheTTL));
                }

            } finally {
                groupedWorksCache.releaseWriteLockOnKey(key);
            }
        }
        return groupedWorks;
    }
    
    private Works toWorks(Element element) {
        return (Works) (element != null ? element.getObjectValue() : null);
    }

    private Element getElementFromCache(Object key, String orcid) {
        try {
            return groupedWorksCache.get(key);
        } catch(Exception e) {
            String message;
            if(PojoUtil.isEmpty(orcid)) {
                message = String.format("Exception fetching element: '%s'.\n%s", key, e.getMessage());
            } else {
                message = String.format("Exception fetching element: '%s' that belongs to '%s'.\n%s", key, orcid, e.getMessage());
            }            
            LOGGER.error(message, e);
            throw e;
        }
    }

}
