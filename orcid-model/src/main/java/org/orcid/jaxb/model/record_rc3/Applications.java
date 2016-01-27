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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc3.Visibility;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "applicationSummary" })
@XmlRootElement(name = "applications", namespace = "http://www.orcid.org/ns/person")
public class Applications implements Serializable {
    private static final long serialVersionUID = -304424337755316316L;

    @XmlElement(name = "application-summary", namespace = "http://www.orcid.org/ns/person")
    protected List<ApplicationSummary> applicationSummary;

    @XmlAttribute
    protected Visibility visibility;

    public List<ApplicationSummary> getApplicationSummary() {
        return applicationSummary;
    }

    public void setApplicationSummary(List<ApplicationSummary> applicationSummary) {
        this.applicationSummary = applicationSummary;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((applicationSummary == null) ? 0 : applicationSummary.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
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
        Applications other = (Applications) obj;
        if (applicationSummary == null) {
            if (other.applicationSummary != null)
                return false;
        } else if (!applicationSummary.equals(other.applicationSummary))
            return false;
        if (visibility != other.visibility)
            return false;
        return true;
    }
}
