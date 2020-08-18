package org.orcid.core.orgs.load.source;

public interface OrgLoadSource {
    
    String getSourceName();
    
    boolean loadLatestOrgs();
    
    boolean isEnabled();

    void setEnabled(boolean enabled);


}
