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
package org.orcid.jaxb.model.record_rc2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc2.LastModifiedDate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "approvalDate", "delegateSummary" })
@XmlRootElement(name = "delegation-details", namespace = "http://www.orcid.org/ns/person")
public class DelegationDetails implements Serializable {
    private static final long serialVersionUID = 2327423760245004561L;
    
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/person", name = "approval-date")
    protected ApprovalDate approvalDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/person", name = "delegate-summary")
    protected DelegateSummary delegateSummary;
    @XmlAttribute(name = "put-code")
    protected Long putCode;

    public ApprovalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(ApprovalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public DelegateSummary getDelegateSummary() {
        return delegateSummary;
    }

    public void setDelegateSummary(DelegateSummary delegateSummary) {
        this.delegateSummary = delegateSummary;
    }

    public Long getPutCode() {
        return putCode;
    }

    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((approvalDate == null) ? 0 : approvalDate.hashCode());
        result = prime * result + ((delegateSummary == null) ? 0 : delegateSummary.hashCode());
        result = prime * result + ((putCode == null) ? 0 : putCode.hashCode());
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
        DelegationDetails other = (DelegationDetails) obj;
        if (approvalDate == null) {
            if (other.approvalDate != null)
                return false;
        } else if (!approvalDate.equals(other.approvalDate))
            return false;
        if (delegateSummary == null) {
            if (other.delegateSummary != null)
                return false;
        } else if (!delegateSummary.equals(other.delegateSummary))
            return false;
        if (putCode == null) {
            if (other.putCode != null)
                return false;
        } else if (!putCode.equals(other.putCode))
            return false;
        return true;
    }

}
