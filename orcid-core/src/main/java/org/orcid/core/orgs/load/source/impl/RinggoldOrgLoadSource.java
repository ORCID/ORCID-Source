package org.orcid.core.orgs.load.source.impl;

import java.io.File;

import org.orcid.core.orgs.load.source.OrgLoadSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RinggoldOrgLoadSource implements OrgLoadSource {
    
    @Value(value = "${org.orcid.core.orgs.ringgold.fptHost}")
    private String ftpHost;
    
    @Value(value = "${org.orcid.core.orgs.ringgold.fptPort}")
    private String ftpPort;

    @Value(value = "${org.orcid.core.orgs.ringgold.fptFilePath}")
    private String ftpFilePath;

    @Value(value = "${org.orcid.core.orgs.ringgold.fptUsername}")
    private String ftpUsername;

    @Value(value = "${org.orcid.core.orgs.ringgold.fptPassword}")
    private String ftpPassword;

    @Override
    public String getSourceName() {
        return "RINGGOLD";
    }

    @Override
    public boolean loadLatestOrgs() {
        downloadLatestData();
        importData();
        return true;
    }

    private void downloadLatestData() {
        // TODO Auto-generated method stub
        
    }
    
    private void importData() {
        // TODO Auto-generated method stub
        
    }
    

}
