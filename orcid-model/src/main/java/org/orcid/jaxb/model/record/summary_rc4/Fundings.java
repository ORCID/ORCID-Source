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
package org.orcid.jaxb.model.record.summary_rc4;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc4.LastModifiedDate;
import org.orcid.jaxb.model.record_rc4.Group;
import org.orcid.jaxb.model.record_rc4.GroupsContainer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "fundingGroup" })
@XmlRootElement(name = "fundings", namespace = "http://www.orcid.org/ns/activities")
public class Fundings implements GroupsContainer, Serializable {
    
    private static final long serialVersionUID = -1446924819201177350L;
    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(name = "group", namespace = "http://www.orcid.org/ns/activities", required = false)
    List<FundingGroup> fundingGroup;
    @XmlAttribute
    protected String path;
    
    public List<FundingGroup> getFundingGroup() {
        if (fundingGroup == null)
            fundingGroup = new ArrayList<FundingGroup>();
        return fundingGroup;
    }

    public Collection<? extends Group> retrieveGroups() {
        return getFundingGroup();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fundingGroup == null) ? 0 : fundingGroup.hashCode());
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
        Fundings other = (Fundings) obj;
        if (fundingGroup == null) {
            if (other.fundingGroup != null)
                return false;
        } else if (!fundingGroup.equals(other.fundingGroup))
            return false;
        return true;
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
