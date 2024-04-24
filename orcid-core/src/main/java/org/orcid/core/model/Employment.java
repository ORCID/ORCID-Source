package org.orcid.core.model;

import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.release.common.FuzzyDate;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "putCode", "endDate", "organizationName", "validated" })
@XmlRootElement(name = "employment", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Employment")
public class Employment {
    @XmlElement(name = "put-code", namespace = "http://www.orcid.org/ns/summary")
    private Long putCode;
    @XmlElement(name = "end-date", namespace = "http://www.orcid.org/ns/common")
    private FuzzyDate endDate;
    @XmlElement(name = "organization-name", namespace = "http://www.orcid.org/ns/summary")
    private String organizationName;
    @XmlElement(name = "validated", namespace = "http://www.orcid.org/ns/summary")
    private boolean validated;

    public Long getPutCode() {
        return putCode;
    }

    public void setPutCode(Long putCode) {
        this.putCode = putCode;
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

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    @Override
    public int hashCode() {
        return Objects.hash(endDate, organizationName, putCode, validated);
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
                && validated == other.validated;
    }

}
