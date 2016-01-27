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

import org.orcid.jaxb.model.common_rc3.OrcidIdentifier;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "orcidIdentifier" })
@XmlRootElement(name = "primary-record", namespace = "http://www.orcid.org/ns/deprecated")
public class PrimaryRecord implements Serializable {
    private static final long serialVersionUID = -2996270049010073685L;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "orcid-identifier")
    protected OrcidIdentifier orcidIdentifier;

    public OrcidIdentifier getOrcidIdentifier() {
        return orcidIdentifier;
    }

    public void setOrcidIdentifier(OrcidIdentifier orcidIdentifier) {
        this.orcidIdentifier = orcidIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orcidIdentifier == null) ? 0 : orcidIdentifier.hashCode());
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
        PrimaryRecord other = (PrimaryRecord) obj;
        if (orcidIdentifier == null) {
            if (other.orcidIdentifier != null)
                return false;
        } else if (!orcidIdentifier.equals(other.orcidIdentifier))
            return false;
        return true;
    }

}
