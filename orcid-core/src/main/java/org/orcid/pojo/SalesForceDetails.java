/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
