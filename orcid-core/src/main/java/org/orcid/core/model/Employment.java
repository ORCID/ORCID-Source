package org.orcid.core.model;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "putCode", "type", "organizationName", "role", "url", "startDate", "endDate", "validated" })
@XmlRootElement(name = "employment", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Employment")
public class Employment implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "put-code", namespace = "http://www.orcid.org/ns/summary")
    protected Long putCode;
    @XmlElement(name = "start-date", namespace = "http://www.orcid.org/ns/common")
    protected FuzzyDate startDate;
    @XmlElement(name = "end-date", namespace = "http://www.orcid.org/ns/common")
    protected FuzzyDate endDate;
    @XmlElement(name = "type", namespace = "http://www.orcid.org/ns/summary")
    protected final String type = AffiliationType.EMPLOYMENT.value();
    @XmlElement(name = "organization-name", namespace = "http://www.orcid.org/ns/summary")
    protected String organizationName;
    @XmlElement(name = "role", namespace = "http://www.orcid.org/ns/summary")
    protected String role;
    @XmlElement(name = "url", namespace = "http://www.orcid.org/ns/summary")
    protected String url;
    @XmlElement(name = "validated", namespace = "http://www.orcid.org/ns/summary")
    protected boolean validated;
    public Long getPutCode() {
        return putCode;
    }
    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }
    public FuzzyDate getStartDate() {
        return startDate;
    }
    public void setStartDate(FuzzyDate startDate) {
        this.startDate = startDate;
    }
    public FuzzyDate getEndDate() {
        return endDate;
    }
    public void setEndDate(FuzzyDate endDate) {
        this.endDate = endDate;
    }
    public String getOrganizationName() {
        return organizationName;
    }
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public boolean isValidated() {
        return validated;
    }
    public void setValidated(boolean validated) {
        this.validated = validated;
    }    
    public String getType() {
        return type;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    @Override
    public int hashCode() {
        return Objects.hash(endDate, organizationName, putCode, role, startDate, url, validated);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Employment other = (Employment) obj;
        return Objects.equals(endDate, other.endDate) && Objects.equals(organizationName, other.organizationName) && Objects.equals(putCode, other.putCode)
                && Objects.equals(role, other.role) && Objects.equals(startDate, other.startDate) && Objects.equals(url, other.url) && validated == other.validated;
    }        
}
