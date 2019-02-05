package org.orcid.jaxb.model.v3.rc2.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.rc2.common.CreatedDate;
import org.orcid.jaxb.model.v3.rc2.common.Filterable;
import org.orcid.jaxb.model.v3.rc2.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.common.Organization;
import org.orcid.jaxb.model.v3.rc2.common.OrganizationHolder;
import org.orcid.jaxb.model.v3.rc2.common.Source;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;

import io.swagger.annotations.ApiModel;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "createdDate", "lastModifiedDate", "source", "putCode", "path", "departmentName", "roleTitle", "startDate", "endDate", "organization", "url",
        "externalIdentifiers", "displayIndex" })
@ApiModel(value = "AffiliationV3_0_rc2")
public abstract class Affiliation implements OrganizationHolder, Filterable, Activity, SourceAware {
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "department-name")
    protected String departmentName;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "role-title")
    protected String roleTitle;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "start-date", required = true)
    protected FuzzyDate startDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "end-date")
    protected FuzzyDate endDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", required = true)
    protected Organization organization;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Source source;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "created-date")
    protected CreatedDate createdDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "url")
    protected Url url;
    @XmlElement(name = "external-ids", namespace = "http://www.orcid.org/ns/common")
    protected ExternalIDs externalIdentifiers;

    @XmlAttribute(name = "put-code")
    protected Long putCode;
    @XmlAttribute(name = "path")
    protected String path;
    @XmlAttribute
    protected Visibility visibility;
    @XmlAttribute(name = "display-index")
    protected String displayIndex;

    /**
     * Gets the value of the departmentName property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Sets the value of the departmentName property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setDepartmentName(String value) {
        this.departmentName = value;
    }

    /**
     * Gets the value of the roleTitle property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRoleTitle() {
        return roleTitle;
    }

    /**
     * Sets the value of the roleTitle property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setRoleTitle(String value) {
        this.roleTitle = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return possible object is {@link FuzzyDate }
     * 
     */
    public FuzzyDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *            allowed object is {@link FuzzyDate }
     * 
     */
    public void setStartDate(FuzzyDate value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return possible object is {@link FuzzyDate }
     * 
     */
    public FuzzyDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *            allowed object is {@link FuzzyDate }
     * 
     */
    public void setEndDate(FuzzyDate value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the organization property.
     * 
     * @return possible object is {@link Organization }
     * 
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the value of the organization property.
     * 
     * @param value
     *            allowed object is {@link Organization }
     * 
     */
    public void setOrganization(Organization value) {
        this.organization = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return possible object is {@link Source }
     * 
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *            allowed object is {@link Source }
     * 
     */
    public void setSource(Source value) {
        this.source = value;
    }

    /**
     * Gets the value of the visibility property.
     * 
     * @return possible object is {@link Visibility }
     * 
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     * 
     * @param value
     *            allowed object is {@link Visibility }
     * 
     */
    public void setVisibility(Visibility value) {
        this.visibility = value;
    }

    /**
     * Gets the value of the putCode property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public Long getPutCode() {
        return putCode;
    }

    /**
     * Sets the value of the putCode property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPutCode(Long value) {
        this.putCode = value;
    }

    /**
     * Gets the value of the path property.
     * 
     * @return possible object is {@link Object }
     * 
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the value of the path property.
     * 
     * @param path
     *            allowed object is {@link Object }
     * 
     */
    public void setPath(String path) {
        this.path = path;
    }

    public String retrieveSourcePath() {
        if (source == null) {
            return null;
        }
        return source.retrieveSourcePath();
    }

    public CreatedDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(CreatedDate value) {
        createdDate = value;
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate value) {
        lastModifiedDate = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return possible object is {@link Url }
     * 
     */
    public Url getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *            allowed object is {@link Url }
     * 
     */
    public void setUrl(Url value) {
        this.url = value;
    }

    public ExternalIDs getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(ExternalIDs externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public ExternalIDs getExternalIDs() {
        return externalIdentifiers;
    }

    public void setExternalIDs(ExternalIDs externalIDs) {
        this.externalIdentifiers = externalIDs;
    }

    public String getDisplayIndex() {
        return displayIndex;
    }
    
    public void setDisplayIndex(String displayIndex) {
        this.displayIndex = displayIndex;
    }
    
    /**
     * 
     * Note that put-code is not part of hashCode or equals! This is to allow
     * better de-duplication.
     * 
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((departmentName == null) ? 0 : departmentName.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((organization == null) ? 0 : organization.hashCode());
        result = prime * result + ((roleTitle == null) ? 0 : roleTitle.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((externalIdentifiers == null) ? 0 : externalIdentifiers.hashCode());
        return result;
    }

    /**
     * 
     * Note that put-code is not part of hashCode or equals! This is to allow
     * better de-duplication.
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Affiliation other = (Affiliation) obj;
        if (departmentName == null) {
            if (other.departmentName != null)
                return false;
        } else if (!departmentName.equals(other.departmentName))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (organization == null) {
            if (other.organization != null)
                return false;
        } else if (!organization.equals(other.organization))
            return false;
        if (roleTitle == null) {
            if (other.roleTitle != null)
                return false;
        } else if (!roleTitle.equals(other.roleTitle))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (visibility != other.visibility)
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (externalIdentifiers == null) {
            if (other.externalIdentifiers != null)
                return false;
        } else if (!externalIdentifiers.equals(other.externalIdentifiers))
            return false;

        /*
         * Breaks our deduping if (lastModifiedDate != other.lastModifiedDate)
         * return false; if (createdDate != other.createdDate) return false;
         */

        return true;
    }
}