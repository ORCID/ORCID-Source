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
package org.orcid.jaxb.model.record.peer_review;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common.CreatedDate;
import org.orcid.jaxb.model.common.FuzzyDate;
import org.orcid.jaxb.model.common.LastModifiedDate;
import org.orcid.jaxb.model.common.Organization;
import org.orcid.jaxb.model.common.OrganizationHolder;
import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.common.VisibilityType;
import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.WorkExternalIdentifiers;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "role", "organization", "externalIdentifiers", "url", "type", "completionDate", "subject", "source", "lastModifiedDate", "createdDate"})
@XmlRootElement(name = "peer-review", namespace = "http://www.orcid.org/ns/peer-review")
public class PeerReview implements VisibilityType, Activity, Serializable, OrganizationHolder {
    private static final long serialVersionUID = -1112309604310926743L;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review") 
    protected Role role;
    @XmlElement(required = true, namespace = "http://www.orcid.org/ns/peer-review")
    protected Organization organization;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review", name = "external-identifiers")
    protected WorkExternalIdentifiers externalIdentifiers;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review")
    protected Url url;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review")
    protected PeerReviewType type;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review", name = "completion-date")
    protected FuzzyDate completionDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/peer-review")
    protected Subject subject;

    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Source source;
    @XmlAttribute(name = "put-code")
    protected String putCode;
    @XmlAttribute
    protected Visibility visibility;
    @XmlAttribute(name = "path")
    protected String path;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "created-date")
    protected CreatedDate createdDate;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public WorkExternalIdentifiers getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(WorkExternalIdentifiers externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }
    
    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public PeerReviewType getType() {
        return type;
    }

    public void setType(PeerReviewType type) {
        this.type = type;
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
    
    public String getPutCode() {
        return putCode;        
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }
    
    public Visibility getVisibility() {
        return visibility;        
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
    
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
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
        result = prime * result + ((completionDate == null) ? 0 : completionDate.hashCode());
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((externalIdentifiers == null) ? 0 : externalIdentifiers.hashCode());
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + ((organization == null) ? 0 : organization.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((subject == null) ? 0 : subject.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        PeerReview other = (PeerReview) obj;
        
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
        if (lastModifiedDate == null) {
            if (other.lastModifiedDate != null)
                return false;
        } else if (!lastModifiedDate.equals(other.lastModifiedDate))
            return false;
        if (organization == null) {
            if (other.organization != null)
                return false;
        } else if (!organization.equals(other.organization))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (role != other.role)
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (subject == null) {
            if (other.subject != null)
                return false;
        } else if (!subject.equals(other.subject))
            return false;
        if (type != other.type)
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (visibility != other.visibility)
            return false;
            
        return true;
    }

    /**
     * Indicates if two peer reviews are ORCID duplicated. Two peer review will
     * be duplicated if they have the same subject, the same external
     * identifiers, the same role, the same type and same completion date
     * 
     * @return true if the two peer reviews are duplicated according to ORCID
     *         requirements
     * */
    public boolean isDuplicated(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PeerReview other = (PeerReview) obj;
        if (!subject.equals(other)) {
            return false;
        }

        if (!externalIdentifiers.equals(other.getExternalIdentifiers())) {
            return false;
        }

        if (!role.equals(other.getRole())) {
            return false;
        }

        if (!type.equals(other.getType())) {
            return false;
        }

        if (!completionDate.equals(other.getCompletionDate())) {
            return false;
        }        
        return true;
    }
}
