package org.orcid.core.manager.v3;

import org.orcid.jaxb.model.v3.release.record.summary.Works;

public interface WorksCacheManager {
    
    Works getGroupedWorks(String orcid);

}
