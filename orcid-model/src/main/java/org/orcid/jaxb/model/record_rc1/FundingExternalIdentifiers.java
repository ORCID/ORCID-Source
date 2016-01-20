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
package org.orcid.jaxb.model.record_rc1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc1.Contributor;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "externalIdentifier" })
@XmlRootElement(name = "externalIdentifiers", namespace = "http://www.orcid.org/ns/funding")
public class FundingExternalIdentifiers implements ExternalIdentifiersContainer, Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "externalIdentifier", namespace = "http://www.orcid.org/ns/funding")
    protected List<FundingExternalIdentifier> externalIdentifier;

    /**
     * Gets the value of the fundingExternalIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the contributor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getContributor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Contributor }
     * 
     * 
     */
    public List<FundingExternalIdentifier> getExternalIdentifier() {
        if (externalIdentifier == null)
            externalIdentifier = new ArrayList<FundingExternalIdentifier>();
        return externalIdentifier;
    } 

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((externalIdentifier == null) ? 0 : externalIdentifier.hashCode());
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
        FundingExternalIdentifiers other = (FundingExternalIdentifiers) obj;
        if (externalIdentifier == null) {
            if (other.externalIdentifier != null)
                return false;
        } else {
            if (other.externalIdentifier == null)
                return false;
            else if (!(externalIdentifier.containsAll(other.externalIdentifier) && other.externalIdentifier.containsAll(externalIdentifier) && other.externalIdentifier
                    .size() == externalIdentifier.size())) {
                return false;
            }
        }
        return true;
    }

}
