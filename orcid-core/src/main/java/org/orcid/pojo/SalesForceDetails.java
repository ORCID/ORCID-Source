package org.orcid.pojo;

import java.util.List;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceDetails {

    private String parentOrgName;
    private List<SalesForceIntegration> integrations;

    public String getParentOrgName() {
        return parentOrgName;
    }

    public void setParentOrgName(String parentOrgName) {
        this.parentOrgName = parentOrgName;
    }

    public List<SalesForceIntegration> getIntegrations() {
        return integrations;
    }

    public void setIntegrations(List<SalesForceIntegration> integrations) {
        this.integrations = integrations;
    }

}
