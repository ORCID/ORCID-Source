package org.orcid.jaxb.model.v3.release.search.expanded;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.annotations.ApiModel;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "orcidId", "givenNames", "familyNames", "creditName", "otherNames", "emails", "institutionNames" })
@ApiModel(value = "ExpandedResultV3_0")
public class ExpandedResult implements Serializable {
    
    private static final long serialVersionUID = -7750679507838375771L;
    
    @XmlElement(name = "orcid-id", namespace = "http://www.orcid.org/ns/expanded-search")
    protected String orcidId;

    @XmlElement(name = "given-names", namespace = "http://www.orcid.org/ns/expanded-search")
    protected String givenNames;
    
    @XmlElement(name = "family-names", namespace = "http://www.orcid.org/ns/expanded-search")
    protected String familyNames;
    
    @XmlElement(name = "credit-name", namespace = "http://www.orcid.org/ns/expanded-search")
    protected String creditName;
    
    @XmlElement(name = "other-name", namespace = "http://www.orcid.org/ns/expanded-search")
    protected String[] otherNames;
    
    @XmlElement(name = "email", namespace = "http://www.orcid.org/ns/expanded-search")
    protected String[] emails;
    
    @XmlElement(name = "institution-name", namespace = "http://www.orcid.org/ns/expanded-search")
    protected String[] institutionNames;
    
    public String getOrcidId() {
        return orcidId;
    }

    public void setOrcidId(String orcidId) {
        this.orcidId = orcidId;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getFamilyNames() {
        return familyNames;
    }

    public void setFamilyNames(String familyNames) {
        this.familyNames = familyNames;
    }

    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    public String[] getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(String[] otherNames) {
        this.otherNames = otherNames;
    }

    public String[] getEmails() {
        return emails;
    }

    public void setEmails(String[] emails) {
        this.emails = emails;
    }

    public String[] getInstitutionNames() {
        return institutionNames;
    }

    public void setInstitutionNames(String[] institutionNames) {
        this.institutionNames = institutionNames;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orcidId == null) ? 0 : orcidId.hashCode());
        result = prime * result + ((emails == null) ? 0 : emails.hashCode());
        result = prime * result + ((givenNames == null) ? 0 : givenNames.hashCode());
        result = prime * result + ((familyNames == null) ? 0 : familyNames.hashCode());
        result = prime * result + ((creditName == null) ? 0 : creditName.hashCode());
        result = prime * result + ((institutionNames == null) ? 0 : institutionNames.hashCode());
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
        ExpandedResult other = (ExpandedResult) obj;
        if (orcidId == null) {
            if (other.orcidId != null)
                return false;
        } else if (!orcidId.equals(other.orcidId))
            return false;
        if (emails == null) {
            if (other.emails != null)
                return false;
        } else if (!emails.equals(other.emails))
            return false;
        if (givenNames == null) {
            if (other.givenNames != null)
                return false;
        } else if (!givenNames.equals(other.givenNames))
            return false;
        if (familyNames == null) {
            if (other.familyNames != null)
                return false;
        } else if (!familyNames.equals(other.familyNames))
            return false;
        if (creditName == null) {
            if (other.creditName != null)
                return false;
        } else if (!creditName.equals(other.creditName))
            return false;
        if (institutionNames == null) {
            if (other.institutionNames != null)
                return false;
        } else if (!institutionNames.equals(other.institutionNames))
            return false;
        if (otherNames == null) {
            if (other.otherNames != null)
                return false;
        } else if (!otherNames.equals(other.otherNames))
            return false;
        return true;
    }

}
