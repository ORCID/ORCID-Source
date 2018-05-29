package org.orcid.core.salesforce.cache;

import javax.annotation.Resource;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.orcid.core.salesforce.dao.SalesForceDao;

/**
 * 
 * @author Will Simpson
 *
 */
public class ContactsCacheEntryFactory implements CacheLoaderWriter<Object, Object> {

    @Resource
    private SalesForceDao salesForceDao;

    @Override
    public Object load(Object key) throws Exception {
        String accountId = (String) key;
        return salesForceDao.retrieveContactsWithRolesByAccountId(accountId);
    }

    @Override
    public void write(Object key, Object value) throws Exception {
        // Not needed, populating only
    }

    @Override
    public void delete(Object key) throws Exception {
     // Not needed, populating only
    }

}
