package org.orcid.core.salesforce.cache;

import javax.annotation.Resource;

import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.MemberDetails;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class MemberDetailsCacheEntryFactory implements CacheEntryFactory {

    @Resource
    private SalesForceDao salesForceDao;

    @Override
    public Object createEntry(Object key) throws Exception {
        MemberDetailsCacheKey detailsCacheKey = (MemberDetailsCacheKey) key;
        MemberDetails details = salesForceDao.retrieveDetails(detailsCacheKey.getMemberId(), detailsCacheKey.getConsotiumLeadId());
        return details;
    }

}
