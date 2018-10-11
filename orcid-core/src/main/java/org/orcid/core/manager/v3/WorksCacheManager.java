package org.orcid.core.manager.v3;

import org.orcid.jaxb.model.v3.rc2.record.summary.Works;

public interface WorksCacheManager {
    
    Works getGroupedWorks(String orcid);

}
