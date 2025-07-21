package org.orcid.core.manager.v3.impl;

import org.orcid.core.cache.GenericCacheManager;
import org.orcid.core.cache.OrcidString;
import org.orcid.core.manager.v3.WorksExtendedCacheManager;
import org.orcid.pojo.WorksExtended;

import javax.annotation.Resource;

public class WorksExtendedCacheManagerImpl implements WorksExtendedCacheManager {

    @Resource(name = "groupedWorksExtendedGenericCacheManager")
    private GenericCacheManager<OrcidString, WorksExtended> groupedWorksExtendedGenericCacheManager;

    @Resource(name = "featuredGroupedWorksExtendedGenericCacheManager")
    private GenericCacheManager<OrcidString, WorksExtended> featuredGroupedWorksExtendedGenericCacheManager;

    @Override
    public WorksExtended getGroupedWorksExtended(String orcid) {
        return groupedWorksExtendedGenericCacheManager.retrieve(new OrcidString(orcid));
    }

    @Override
    public WorksExtended getFeaturedGroupedWorksExtended(String orcid) {
        return featuredGroupedWorksExtendedGenericCacheManager.retrieve(new OrcidString(orcid));
    }
}
