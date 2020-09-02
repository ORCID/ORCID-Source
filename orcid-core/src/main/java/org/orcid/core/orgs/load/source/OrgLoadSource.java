package org.orcid.core.orgs.load.source;

public interface OrgLoadSource {
    
    String getSourceName();
    
    boolean downloadOrgData();
    
    boolean loadOrgData();
    
    boolean isEnabled();

}
