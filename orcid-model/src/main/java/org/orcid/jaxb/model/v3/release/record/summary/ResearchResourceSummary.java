package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.rc2.common.CreatedDate;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.common.VisibilityType;
import org.orcid.jaxb.model.v3.rc2.record.Activity;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIdentifiersContainer;
import org.orcid.jaxb.model.v3.rc2.record.GroupableActivity;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResourceProposal;
import org.orcid.jaxb.model.v3.rc2.record.SourceAware;

import io.swagger.annotations.ApiModel;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "createdDate", "lastModifiedDate", "source", "proposal" })
@XmlRootElement(name = "research-resource", namespace = "http://www.orcid.org/ns/research-resource")
@ApiModel(value = "ResearchResourceSummaryV3_0_rc2")
public class ResearchResourceSummary implements VisibilityType, Activity, GroupableActivity, Serializable, SourceAware {
    /**
     * 
     */
    private static final long serialVersionUID = 861106386991037888L;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Source source;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "created-date")
    protected CreatedDate createdDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/research-resource", name = "proposal")
    protected ResearchResourceProposal proposal;

    @XmlAttribute(name = "put-code")
    protected Long putCode;
    @XmlAttribute(name = "path")
    protected String path;
    @XmlAttribute
    protected Visibility visibility;
    @XmlAttribute(name = "display-index")
    protected String displayIndex;

    public ResearchResourceProposal getProposal() {
        return proposal;
    }

    public void setProposal(ResearchResourceProposal proposal) {
        this.proposal = proposal;
    }

    public void setDisplayIndex(String displayIndex) {
        this.displayIndex = displayIndex;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public CreatedDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(CreatedDate createdDate) {
        this.createdDate = createdDate;
    }

    public Long getPutCode() {
        return putCode;
    }

    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public String retrieveSourcePath() {
        if (source == null) {
            return null;
        }
        return source.retrieveSourcePath();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((proposal == null) ? 0 : proposal.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
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
        ResearchResourceSummary other = (ResearchResourceSummary) obj;
        if (proposal == null) {
            if (other.proposal != null)
                return false;
        } else if (!proposal.equals(other.proposal))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (visibility != other.visibility)
            return false;
        return true;
    }

    @Override
    public ExternalIdentifiersContainer getExternalIdentifiers() {
        return proposal.getExternalIdentifiers();
    }

    @Override
    public String getDisplayIndex() {
        return displayIndex;
    }

    @Override
    public int compareTo(GroupableActivity activity) {
        Long index = Long.valueOf(this.getDisplayIndex() == null ? "0" : this.getDisplayIndex());
        Long otherIndex = Long.valueOf(activity.getDisplayIndex() == null ? "0" : activity.getDisplayIndex());
        if (index == null) {
            if (otherIndex == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (otherIndex == null) {
                return 1;
            } else if (index instanceof Comparable) {
                // Return opposite, since higher index goes first
                return index.compareTo(otherIndex) * -1;
            } else {
                return 0;
            }
        }
    }

}
