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
package org.orcid.jaxb.model.record.summary_rc2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.record_rc2.Group;
import org.orcid.jaxb.model.record_rc2.GroupableActivity;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "identifiers", "fundingSummary" })
@XmlRootElement(name = "funding-group", namespace = "http://www.orcid.org/ns/activities")
public class FundingGroup implements Group, Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(name = "external-ids", namespace = "http://www.orcid.org/ns/common")
    private ExternalIDs identifiers;
    @XmlElement(name = "summary", namespace = "http://www.orcid.org/ns/funding")
    private List<FundingSummary> fundingSummary;

    public ExternalIDs getIdentifiers() {
        if (identifiers == null)
            identifiers = new ExternalIDs();
        return identifiers;
    }

    public List<FundingSummary> getFundingSummary() {
        if (fundingSummary == null)
            fundingSummary = new ArrayList<FundingSummary>();
        return fundingSummary;
    }

    @Override
    public Collection<? extends GroupableActivity> getActivities() {
        return getFundingSummary();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fundingSummary == null) ? 0 : fundingSummary.hashCode());
        result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
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
        FundingGroup other = (FundingGroup) obj;
        if (fundingSummary == null) {
            if (other.fundingSummary != null)
                return false;
        } else if (!fundingSummary.equals(other.fundingSummary))
            return false;
        if (identifiers == null) {
            if (other.identifiers != null)
                return false;
        } else if (!identifiers.equals(other.identifiers))
            return false;
        return true;
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
