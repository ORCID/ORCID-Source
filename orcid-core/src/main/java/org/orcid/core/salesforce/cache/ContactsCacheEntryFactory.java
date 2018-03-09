package org.orcid.core.salesforce.cache;

import javax.annotation.Resource;

import org.orcid.core.salesforce.dao.SalesForceDao;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class ContactsCacheEntryFactory implements CacheEntryFactory {

    @Resource
    private SalesForceDao salesForceDao;

    @Override
    public Object createEntry(Object key) throws Exception {
        String accountId = (String) key;
        return salesForceDao.retrieveContactsWithRolesByAccountId(accountId);
    }

}
