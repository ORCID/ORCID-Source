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

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private SalesForceMember member;
    private String parentOrgName;
    private List<SalesForceIntegration> integrations;
    private List<SalesForceContact> contacts;

    public SalesForceMember getMember() {
        return member;
    }

    public void setMember(SalesForceMember member) {
        this.member = member;
    }

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

    public List<SalesForceContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<SalesForceContact> contacts) {
        this.contacts = contacts;
    }

}
