package org.orcid.core.utils;

import javax.annotation.Resource;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidEhcacheManagementService extends ManagementService {

    @Resource
    private MBeanServer mbeanServer;

    @Resource
    private CacheManager coreCacheManager;
    
    public OrcidEhcacheManagementService(CacheManager cacheManager, MBeanServer mBeanServer, boolean registerCacheManager, boolean registerCaches,
            boolean registerCacheConfigurations, boolean registerCacheStatistics) throws CacheException {
        super(cacheManager, mBeanServer, registerCacheManager, registerCaches, registerCacheConfigurations, registerCacheStatistics);
    }

    public OrcidEhcacheManagementService(CacheManager cacheManager, MBeanServer mBeanServer, boolean registerCacheManager, boolean registerCaches,
            boolean registerCacheConfigurations, boolean registerCacheStatistics, boolean registerCacheStores) throws CacheException {
        super(cacheManager, mBeanServer, registerCacheManager, registerCaches, registerCacheConfigurations, registerCacheStatistics, registerCacheStores);
    }

    @Override
    public void init() throws CacheException {
        try {
            mbeanServer.getObjectInstance(createObjectName(coreCacheManager));
        } catch (InstanceNotFoundException e) {
            super.init();
        }
    }

    static ObjectName createObjectName(net.sf.ehcache.CacheManager cacheManager) {
        ObjectName objectName;
        try {            
            objectName = new ObjectName("net.sf.ehcache:type=CacheManager,name=" + cacheManager.getName());
        } catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        return objectName;
    }

}
