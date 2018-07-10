package org.orcid.core.manager.v3.impl;

import javax.annotation.Resource;

import org.orcid.core.cache.GenericCacheManager;
import org.orcid.core.cache.OrcidString;
import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;

public class WorksCacheManagerImpl implements WorksCacheManager {
    
    @Resource(name = "groupedWorksGenericCacheManager")
    private GenericCacheManager<OrcidString, Works> groupedWorksGenericCacheManager;
    
    @Override
    public Works getGroupedWorks(String orcid) {
        return groupedWorksGenericCacheManager.retrieve(new OrcidString(orcid));
    }
    
}
