package org.orcid.core.manager.v3;

import org.orcid.pojo.WorksExtended;

public interface WorksExtendedCacheManager {

    WorksExtended getGroupedWorksExtended(String orcid);

    WorksExtended getFeaturedGroupedWorksExtended(String orcid);
}
