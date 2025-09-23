package org.orcid.core.manager.v3;

import org.orcid.pojo.ActivityTitle;
import org.orcid.pojo.WorkSummaryExtended;
import org.orcid.pojo.WorksExtended;

import java.util.List;

public interface WorksExtendedCacheManager {

    WorksExtended getGroupedWorksExtended(String orcid);

    List<WorkSummaryExtended> getFeaturedGroupedWorksExtended(String orcid);
    
    List<ActivityTitle> getWorksTitle(String orcid);
}
