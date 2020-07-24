package org.orcid.core.orgs.load.source;

import java.io.File;

public interface OrgLoadSource {
    
    String getSourceName();
    
    boolean loadLatestOrgs(File importFileDestination);
    
    

}
