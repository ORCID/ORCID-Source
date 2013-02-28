/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */

package org.orcid.jaxb.model.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}affiliation-details" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}affiliation-department" minOccurs="0"/>
 *         &lt;element ref="{http://www.orcid.org/ns/orcid}affiliation-type" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "affiliationName", "affiliationType", "address", "departmentName", "roleTitle", "startDate", "endDate" })
@XmlRootElement(name = "affiliation")
public class Affiliation implements Serializable, VisibilityType {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "affiliation-name")
    protected String affiliationName;
    @XmlElement(name = "affiliation-type")
    private AffiliationType affiliationType;

    protected Address address;

    @XmlElement(name = "department-name")
    protected String departmentName;
    @XmlElement(name = "role-title")
    protected String roleTitle;
    @XmlElement(name = "start-date")
    protected StartDate startDate;
    @XmlElement(name = "end-date")
    protected EndDate endDate;

    @XmlAttribute
    protected Visibility visibility;

    public String getAffiliationName() {
        return affiliationName;
    }

    public void setAffiliationName(String affiliationName) {
        this.affiliationName = affiliationName;
    }

    public AffiliationType getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(AffiliationType affiliationType) {
        this.affiliationType = affiliationType;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public StartDate getStartDate() {
        return startDate;
    }

    public void setStartDate(StartDate startDate) {
        this.startDate = startDate;
    }

    public EndDate getEndDate() {
        return endDate;
    }

    public void setEndDate(EndDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Affiliation)) {
            return false;
        }

        Affiliation that = (Affiliation) o;

        if (address != null ? !address.equals(that.address) : that.address != null) {
            return false;
        }
        if (affiliationName != null ? !affiliationName.equals(that.affiliationName) : that.affiliationName != null) {
            return false;
        }
        if (affiliationType != that.affiliationType) {
            return false;
        }
        if (departmentName != null ? !departmentName.equals(that.departmentName) : that.departmentName != null) {
            return false;
        }
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) {
            return false;
        }
        if (roleTitle != null ? !roleTitle.equals(that.roleTitle) : that.roleTitle != null) {
            return false;
        }
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) {
            return false;
        }
        if (visibility != that.visibility) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = affiliationName != null ? affiliationName.hashCode() : 0;
        result = 31 * result + (affiliationType != null ? affiliationType.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (departmentName != null ? departmentName.hashCode() : 0);
        result = 31 * result + (roleTitle != null ? roleTitle.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (visibility != null ? visibility.hashCode() : 0);
        return result;
    }
}
