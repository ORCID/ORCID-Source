package org.orcid.scheduler.loader.manager;

import org.orcid.scheduler.loader.source.OrgLoadSource;

public interface OrgLoadManager {
    
    void loadOrgs();
    
    void loadOrg(OrgLoadSource loader );

}
