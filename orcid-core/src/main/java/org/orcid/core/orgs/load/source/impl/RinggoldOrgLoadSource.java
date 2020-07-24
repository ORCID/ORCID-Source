package org.orcid.core.orgs.load.source.impl;

import java.io.File;

import org.orcid.core.orgs.load.source.OrgLoadSource;
import org.springframework.stereotype.Component;

@Component
public class RinggoldOrgLoadSource implements OrgLoadSource {

    @Override
    public String getSourceName() {
        return "RINGGOLD";
    }

    @Override
    public boolean loadLatestOrgs(File importFileDestination) {
        // TODO Auto-generated method stub
        return false;
    }
    
    

}
