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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common.CreditName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "givenNames", "familyName", "creditName", "otherNames" })
@XmlRootElement(name = "persona-details", namespace = "http://www.orcid.org/ns/personal-details")
public class PersonalDetails {
    @XmlElement(name = "given-names", namespace = "http://www.orcid.org/ns/personal-details")
    private GivenNames givenNames;
    @XmlElement(name = "family-name", namespace = "http://www.orcid.org/ns/personal-details")
    private FamilyName familyName;
    @XmlElement(name = "credit-name", namespace = "http://www.orcid.org/ns/common")
    private CreditName creditName;
    @XmlElement(name = "other-names", namespace = "http://www.orcid.org/ns/other-name")
    private OtherNames otherNames;

    public GivenNames getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(GivenNames givenNames) {
        this.givenNames = givenNames;
    }

    public FamilyName getFamilyName() {
        return familyName;
    }

    public void setFamilyName(FamilyName familyName) {
        this.familyName = familyName;
    }

    public CreditName getCreditName() {
        return creditName;
    }

    public void setCreditName(CreditName creditName) {
        this.creditName = creditName;
    }

    public OtherNames getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(OtherNames otherNames) {
        this.otherNames = otherNames;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((creditName == null) ? 0 : creditName.hashCode());
        result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
        result = prime * result + ((givenNames == null) ? 0 : givenNames.hashCode());
        result = prime * result + ((otherNames == null) ? 0 : otherNames.hashCode());
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
        PersonalDetails other = (PersonalDetails) obj;
        if (creditName == null) {
            if (other.creditName != null)
                return false;
        } else if (!creditName.equals(other.creditName))
            return false;
        if (familyName == null) {
            if (other.familyName != null)
                return false;
        } else if (!familyName.equals(other.familyName))
            return false;
        if (givenNames == null) {
            if (other.givenNames != null)
                return false;
        } else if (!givenNames.equals(other.givenNames))
            return false;
        if (otherNames == null) {
            if (other.otherNames != null)
                return false;
        } else if (!otherNames.equals(other.otherNames))
            return false;
        return true;
    }
}
