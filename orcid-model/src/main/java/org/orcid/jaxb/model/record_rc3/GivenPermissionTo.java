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

import org.orcid.jaxb.model.common_rc3.LastModifiedDate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "delegationDetails" })
@XmlRootElement(name = "given-permission-to", namespace = "http://www.orcid.org/ns/person")
public class GivenPermissionTo implements Serializable {
    private static final long serialVersionUID = 4364407407203629185L;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/person", name = "delegation-details")
    protected DelegationDetails delegationDetails;

    public DelegationDetails getDelegationDetails() {
        return delegationDetails;
    }

    public void setDelegationDetails(DelegationDetails delegationDetails) {
        this.delegationDetails = delegationDetails;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delegationDetails == null) ? 0 : delegationDetails.hashCode());
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
        GivenPermissionTo other = (GivenPermissionTo) obj;
        if (delegationDetails == null) {
            if (other.delegationDetails != null)
                return false;
        } else if (!delegationDetails.equals(other.delegationDetails))
            return false;
        return true;
    }
}
