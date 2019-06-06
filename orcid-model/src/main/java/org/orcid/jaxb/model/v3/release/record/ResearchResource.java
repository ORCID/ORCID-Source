package org.orcid.jaxb.model.v3.release.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.Filterable;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;

import io.swagger.annotations.ApiModel;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "createdDate", "lastModifiedDate", "source", "proposal", "resourceItems", "displayIndex" })
@XmlRootElement(name = "research-resource", namespace = "http://www.orcid.org/ns/research-resource")
@ApiModel(value = "ResearchResourceV3_0")
public class ResearchResource implements Filterable, Serializable, SourceAware, ExternalIdentifiersAwareActivity {
    private static final long serialVersionUID = -3117752351151578304L;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Source source;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "created-date")
    protected CreatedDate createdDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/research-resource", name = "proposal")
    protected ResearchResourceProposal proposal;

    @XmlElementWrapper(namespace = "http://www.orcid.org/ns/research-resource", name = "resource-items")
    @XmlElement(namespace = "http://www.orcid.org/ns/research-resource", name = "resource-item")
    protected List<ResearchResourceItem> resourceItems;

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

    public List<ResearchResourceItem> getResourceItems() {
        if (resourceItems == null)
            resourceItems = new ArrayList<ResearchResourceItem>();
        return resourceItems;
    }

    public void setResourceItems(List<ResearchResourceItem> resourceItems) {
        this.resourceItems = resourceItems;
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
    
    public String getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(String displayIndex) {
        this.displayIndex = displayIndex;
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
        result = prime * result + ((resourceItems == null) ? 0 : resourceItems.hashCode());
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
        ResearchResource other = (ResearchResource) obj;
        if (proposal == null) {
            if (other.proposal != null)
                return false;
        } else if (!proposal.equals(other.proposal))
            return false;
        if (resourceItems == null) {
            if (other.resourceItems != null)
                return false;
        } else if (!resourceItems.equals(other.resourceItems))
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
    public ExternalIDs getExternalIdentifiers() {
        if(this.getProposal() == null) {
            return null;
        }
        
        return this.getProposal().getExternalIdentifiers();
    }

}
