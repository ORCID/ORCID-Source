package org.orcid.core.salesforce.cache;

import java.util.Map;

import javax.annotation.Resource;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.orcid.core.salesforce.dao.SalesForceDao;

/**
 * 
 * @author Will Simpson
 *
 */
public class ContactsForAuthenticationCacheEntryFactory implements CacheLoaderWriter<Object, Object> {

    @Resource
    private SalesForceDao salesForceDao;

    @Override
    public Object load(Object key) throws Exception {
        Map<String, String> ids = (Map<String, String>) key;
        String accountId = ids.get("accountId");
        String consortiumLeadId = ids.get("consortiumLeadId");
        return salesForceDao.retrieveContactsAllowedToEdit(accountId, consortiumLeadId);
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
