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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
@XmlType(propOrder = { "lastModifiedDate", "researcherUrls" })
@XmlRootElement(name = "researcher-urls", namespace = "http://www.orcid.org/ns/researcher-url")
public class ResearcherUrls implements LastModifiedAware, Serializable {
    private static final long serialVersionUID = 6312730308815255894L;

    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(name = "researcher-url", namespace = "http://www.orcid.org/ns/researcher-url")
    List<ResearcherUrl> researcherUrls;
    @XmlAttribute
    protected String path;

    public List<ResearcherUrl> getResearcherUrls() {
        return researcherUrls;
    }

    public void setResearcherUrls(List<ResearcherUrl> researcherUrls) {
        this.researcherUrls = researcherUrls;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((researcherUrls == null) ? 0 : researcherUrls.hashCode());
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
        ResearcherUrls other = (ResearcherUrls) obj;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (researcherUrls == null) {
            if (other.researcherUrls != null)
                return false;
        } else if (!researcherUrls.equals(other.researcherUrls))
            return false;
        return true;
    }

    public void updateIndexingStatusOnChilds() {
        if (this.getResearcherUrls() != null && !this.getResearcherUrls().isEmpty()) {
            List<ResearcherUrl> sorted = new ArrayList<ResearcherUrl>();
            List<ResearcherUrl> unsorted = new ArrayList<ResearcherUrl>();
            Long maxDisplayIndex = 0L;
            for (ResearcherUrl o : this.getResearcherUrls()) {
                if (o.getDisplayIndex() == null || Long.valueOf(-1).equals(o.getDisplayIndex())) {
                    unsorted.add(o);
                } else {
                    if (o.getDisplayIndex() > maxDisplayIndex) {
                        maxDisplayIndex = o.getDisplayIndex();
                    }
                    sorted.add(o);
                }
            }

            if (!unsorted.isEmpty()) {
                Collections.sort(unsorted);
                for (ResearcherUrl o : unsorted) {
                    o.setDisplayIndex((maxDisplayIndex++) + 1);
                    sorted.add(o);
                }
            }
        }
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean shouldSetLastModified() {
        if(researcherUrls != null && !researcherUrls.isEmpty()) {
            return true;
        }
        return false;
    }
}
