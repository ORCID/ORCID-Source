package org.orcid.core.manager.v3.impl;

import org.orcid.core.cache.GenericCacheManager;
import org.orcid.core.cache.OrcidString;
import org.orcid.core.manager.v3.WorksExtendedCacheManager;
import org.orcid.pojo.ActivityTitle;
import org.orcid.pojo.WorkSummaryExtended;
import org.orcid.pojo.WorksExtended;

import javax.annotation.Resource;
import java.util.List;

public class WorksExtendedCacheManagerImpl implements WorksExtendedCacheManager {

    @Resource(name = "groupedWorksExtendedGenericCacheManager")
    private GenericCacheManager<OrcidString, WorksExtended> groupedWorksExtendedGenericCacheManager;

    @Resource(name = "featuredGroupedWorksExtendedGenericCacheManager")
    private GenericCacheManager<OrcidString, List<WorkSummaryExtended>> featuredGroupedWorksExtendedGenericCacheManager;
    
    @Resource(name = "worksTitleGenericCacheManager")
    private GenericCacheManager<OrcidString, List<ActivityTitle>> worksTitleGenericCacheManager;

    @Override
    public WorksExtended getGroupedWorksExtended(String orcid) {
        return groupedWorksExtendedGenericCacheManager.retrieve(new OrcidString(orcid));
    }

    @Override
    public List<WorkSummaryExtended> getFeaturedGroupedWorksExtended(String orcid) {
        return featuredGroupedWorksExtendedGenericCacheManager.retrieve(new OrcidString(orcid));
    }
    
    @Override
    public List<ActivityTitle> getWorksTitle(String orcid) {
        return worksTitleGenericCacheManager.retrieve(new OrcidString(orcid));
    }
    
    
}
