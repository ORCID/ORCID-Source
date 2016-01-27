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
package org.orcid.jaxb.model.record_rc3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "applicationOrcid", "applicationName", "applicationWebsite", "approvalDate", "scopePaths", "groupOrcid", "groupName" })
@XmlRootElement(name = "application-summary", namespace = "http://www.orcid.org/ns/person")
public class ApplicationSummary implements Serializable {
    private static final long serialVersionUID = -1346001792572747128L;
    @XmlElement(name = "application-orcid", namespace = "http://www.orcid.org/ns/common")
    protected ApplicationOrcid applicationOrcid;
    @XmlElement(name = "application-name", namespace = "http://www.orcid.org/ns/person")
    protected String applicationName;
    @XmlElement(name = "application-website", namespace = "http://www.orcid.org/ns/person")
    protected ApplicationWebsite applicationWebsite;
    @XmlElement(name = "approval-date", namespace = "http://www.orcid.org/ns/common")
    protected ApprovalDate approvalDate;
    @XmlElement(name = "scope-paths", namespace = "http://www.orcid.org/ns/person")
    protected ScopePaths scopePaths;
    @XmlElement(name = "application-group-orcid", namespace = "http://www.orcid.org/ns/common")
    protected GroupOrcid groupOrcid;
    @XmlElement(name = "application-group-name", namespace = "http://www.orcid.org/ns/person")
    protected String groupName;

    public ApplicationOrcid getApplicationOrcid() {
        return applicationOrcid;
    }

    public void setApplicationOrcid(ApplicationOrcid applicationOrcid) {
        this.applicationOrcid = applicationOrcid;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public ApplicationWebsite getApplicationWebsite() {
        return applicationWebsite;
    }

    public void setApplicationWebsite(ApplicationWebsite applicationWebsite) {
        this.applicationWebsite = applicationWebsite;
    }

    public ApprovalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(ApprovalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public ScopePaths getScopePaths() {
        return scopePaths;
    }

    public void setScopePaths(ScopePaths scopePaths) {
        this.scopePaths = scopePaths;
    }

    public GroupOrcid getGroupOrcid() {
        return groupOrcid;
    }

    public void setGroupOrcid(GroupOrcid groupOrcid) {
        this.groupOrcid = groupOrcid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((applicationName == null) ? 0 : applicationName.hashCode());
        result = prime * result + ((applicationOrcid == null) ? 0 : applicationOrcid.hashCode());
        result = prime * result + ((applicationWebsite == null) ? 0 : applicationWebsite.hashCode());
        result = prime * result + ((approvalDate == null) ? 0 : approvalDate.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((groupOrcid == null) ? 0 : groupOrcid.hashCode());
        result = prime * result + ((scopePaths == null) ? 0 : scopePaths.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApplicationSummary other = (ApplicationSummary) obj;
        if (applicationName == null) {
            if (other.applicationName != null)
                return false;
        } else if (!applicationName.equals(other.applicationName))
            return false;
        if (applicationOrcid == null) {
            if (other.applicationOrcid != null)
                return false;
        } else if (!applicationOrcid.equals(other.applicationOrcid))
            return false;
        if (applicationWebsite == null) {
            if (other.applicationWebsite != null)
                return false;
        } else if (!applicationWebsite.equals(other.applicationWebsite))
            return false;
        if (approvalDate == null) {
            if (other.approvalDate != null)
                return false;
        } else if (!approvalDate.equals(other.approvalDate))
            return false;
        if (groupName == null) {
            if (other.groupName != null)
                return false;
        } else if (!groupName.equals(other.groupName))
            return false;
        if (groupOrcid == null) {
            if (other.groupOrcid != null)
                return false;
        } else if (!groupOrcid.equals(other.groupOrcid))
            return false;
        if (scopePaths == null) {
            if (other.scopePaths != null)
                return false;
        } else if (!scopePaths.equals(other.scopePaths))
            return false;
        return true;
    }
}
