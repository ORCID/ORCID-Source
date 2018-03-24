package org.orcid.core.salesforce.cache;

import javax.annotation.Resource;

import org.orcid.core.salesforce.dao.SalesForceDao;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class MemberCacheEntryFactory implements CacheEntryFactory {

    @Resource
    private SalesForceDao salesForceDao;

    @Override
    public Object createEntry(Object key) throws Exception {
        return salesForceDao.retrieveMember((String) key);
    }

}
