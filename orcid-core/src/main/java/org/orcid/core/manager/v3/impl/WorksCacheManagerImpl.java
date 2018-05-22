package org.orcid.core.manager.v3.impl;

import javax.annotation.Resource;

import org.orcid.core.cache.GenericCacheManager;
import org.orcid.core.cache.OrcidString;
import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorksCacheManagerImpl implements WorksCacheManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorksCacheManagerImpl.class);  

    @Resource(name = "groupedWorksGenericCacheManager")
    private GenericCacheManager<OrcidString, Works> groupedWorksGenericCacheManager;
    
    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManagerReadOnly profileEntityManagerReadOnly;

    @Override
    public Works getGroupedWorks(String orcid) {
        return groupedWorksGenericCacheManager.retrieve(new OrcidString(orcid));
    }
    
}
