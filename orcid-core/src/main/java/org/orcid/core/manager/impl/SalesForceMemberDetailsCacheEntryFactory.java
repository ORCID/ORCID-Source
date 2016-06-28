package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.SalesForceManager;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceMemberDetailsCacheEntryFactory implements CacheEntryFactory {

    @Resource
    private SalesForceManager salesForceManager;

    @Override
    public Object createEntry(Object key) throws Exception {
        SalesForceMemberDetailsCacheKey detailsCacheKey = (SalesForceMemberDetailsCacheKey) key;
        return salesForceManager.retrieveFreshDetails(detailsCacheKey.getMemberId(), detailsCacheKey.getConsotiumLeadId());
    }

}
