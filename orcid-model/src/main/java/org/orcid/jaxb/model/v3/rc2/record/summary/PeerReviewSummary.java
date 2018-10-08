package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.rc2.common.CreatedDate;
import org.orcid.jaxb.model.v3.rc2.common.Filterable;
import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.common.Organization;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Activity;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.GroupableActivity;
import org.orcid.jaxb.model.v3.rc2.record.PeerReviewType;
import org.orcid.jaxb.model.v3.rc2.record.Role;
import org.orcid.jaxb.model.v3.rc2.record.SourceAware;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "createdDate", "lastModifiedDate", "source", "role", "externalIdentifiers", "url", "type", "completionDate", "groupId", "organization" })
@XmlRootElement(name = "summary", namespace = "http://www.orcid.org/ns/peer-review")
public class PeerReviewSummary implements Filterable, Activity, GroupableActivity, Serializable, SourceAware {
    
    private static final long serialVersionUID = -7769331531691171324L;    
    @XmlElement(name = "external-ids", namespace = "http://www.orcid.org/ns/common")
    protected ExternalIDs externalIdentifiers;
    @XmlElement(name = "completion-date", namespace = "http://www.orcid.org/ns/peer-review")
    protected FuzzyDate completionDate;
    @XmlElement(required = true, namespace = "http://www.orcid.org/ns/peer-review", name = "convening-organization")
    protected Organization organization;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Source source;
    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(name = "created-date", namespace = "http://www.orcid.org/ns/common")
    protected CreatedDate createdDate;    
    @XmlAttribute(name = "put-code")
    protected Long putCode;
    @XmlAttribute(name = "path")
    protected String path;
    @XmlAttribute
    protected Visibility visibility;
    @XmlAttribute(name = "display-index")
    protected String displayIndex;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review", name = "review-group-id", required = true)
    protected String groupId;    
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review", name = "reviewer-role")
    protected Role role;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review", name = "review-type")
    protected PeerReviewType type;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review", name = "review-url")
    protected Url url;

    public ExternalIDs getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(ExternalIDs externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public FuzzyDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(FuzzyDate completionDate) {
        this.completionDate = completionDate;
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
    
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PeerReviewType getType() {
        return type;
    }

    public void setType(PeerReviewType type) {
        this.type = type;
    }

    @Override
    public String retrieveSourcePath() {
        if (source == null) {
            return null;
        }
        return source.retrieveSourcePath();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }    
    
    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((completionDate == null) ? 0 : completionDate.hashCode());
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((externalIdentifiers == null) ? 0 : externalIdentifiers.hashCode());
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((putCode == null) ? 0 : putCode.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
        result = prime * result + ((organization == null) ? 0 : organization.hashCode());
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
        PeerReviewSummary other = (PeerReviewSummary) obj;
        if (completionDate == null) {
            if (other.completionDate != null)
                return false;
        } else if (!completionDate.equals(other.completionDate))
            return false;
        if (createdDate == null) {
            if (other.createdDate != null)
                return false;
        } else if (!createdDate.equals(other.createdDate))
            return false;
        if (externalIdentifiers == null) {
            if (other.externalIdentifiers != null)
                return false;
        } else if (!externalIdentifiers.equals(other.externalIdentifiers))
            return false;
        if (organization == null) {
            if (other.organization != null)
                return false;
        } else if (!organization.equals(other.organization))
            return false;
        if (lastModifiedDate == null) {
            if (other.lastModifiedDate != null)
                return false;
        } else if (!lastModifiedDate.equals(other.lastModifiedDate))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (putCode == null) {
            if (other.putCode != null)
                return false;
        } else if (!putCode.equals(other.putCode))
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
                //Return opposite, since higher index goes first
                return  index.compareTo(otherIndex) * -1;
            } else {
                return 0;
            }
        }
    }
}
