package org.orcid.scheduler.loader.source;

public interface OrgLoadSource {
    
    String getSourceName();
    
    boolean downloadOrgData();
    
    boolean loadOrgData();
    
    boolean isEnabled();

}
