package org.orcid.core.orgs.load.manager;

import org.orcid.core.orgs.load.source.OrgLoadSource;

public interface OrgLoadManager {
    
    void loadOrgs();
    
    void loadOrg(OrgLoadSource loader );

}
