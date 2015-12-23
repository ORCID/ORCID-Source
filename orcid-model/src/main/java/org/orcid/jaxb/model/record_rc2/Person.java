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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "otherNames", "biography", "researcherUrls", "emails", "addresses", "keywords", "externalIdentifiers", "delegation", "applications" })
@XmlRootElement(name = "person", namespace = "http://www.orcid.org/ns/person")
public class Person implements Serializable {    
    private static final long serialVersionUID = 2200160976598223346L;
    @XmlElement(name = "name", namespace = "http://www.orcid.org/ns/person")
    Name name;
    @XmlElement(name = "other-names", namespace = "http://www.orcid.org/ns/other-name")
    OtherNames otherNames;
    @XmlElement(name = "biography", namespace = "http://www.orcid.org/ns/person")
    Biography biography;
    @XmlElement(name = "researcher-urls", namespace = "http://www.orcid.org/ns/researcher-url")
    ResearcherUrls researcherUrls;
    @XmlElement(name = "emails", namespace = "http://www.orcid.org/ns/email")
    Emails emails;
    @XmlElement(name = "addresses", namespace = "http://www.orcid.org/ns/address")
    Addresses addresses;
    @XmlElement(name = "keywords", namespace = "http://www.orcid.org/ns/keyword")
    Keywords keywords;
    @XmlElement(name = "external-identifiers", namespace = "http://www.orcid.org/ns/external-identifier")
    ExternalIdentifiers externalIdentifiers;
    @XmlElement(name = "delegation", namespace = "http://www.orcid.org/ns/person")
    Delegation delegation;
    @XmlElement(name = "applications", namespace = "http://www.orcid.org/ns/person")
    Applications applications;

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public OtherNames getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(OtherNames otherNames) {
        this.otherNames = otherNames;
    }

    public Biography getBiography() {
        return biography;
    }

    public void setBiography(Biography biography) {
        this.biography = biography;
    }

    public ResearcherUrls getResearcherUrls() {
        return researcherUrls;
    }

    public void setResearcherUrls(ResearcherUrls researcherUrls) {
        this.researcherUrls = researcherUrls;
    }

    public Emails getEmails() {
        return emails;
    }

    public void setEmails(Emails emails) {
        this.emails = emails;
    }

    public Addresses getAddresses() {
        return addresses;
    }

    public void setAddresses(Addresses addresses) {
        this.addresses = addresses;
    }

    public Keywords getKeywords() {
        return keywords;
    }

    public void setKeywords(Keywords keywords) {
        this.keywords = keywords;
    }

    public ExternalIdentifiers getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(ExternalIdentifiers externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public Delegation getDelegation() {
        return delegation;
    }

    public void setDelegation(Delegation delegation) {
        this.delegation = delegation;
    }

    public Applications getApplications() {
        return applications;
    }

    public void setApplications(Applications applications) {
        this.applications = applications;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
        result = prime * result + ((applications == null) ? 0 : applications.hashCode());
        result = prime * result + ((biography == null) ? 0 : biography.hashCode());
        result = prime * result + ((delegation == null) ? 0 : delegation.hashCode());
        result = prime * result + ((emails == null) ? 0 : emails.hashCode());
        result = prime * result + ((externalIdentifiers == null) ? 0 : externalIdentifiers.hashCode());
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((otherNames == null) ? 0 : otherNames.hashCode());
        result = prime * result + ((researcherUrls == null) ? 0 : researcherUrls.hashCode());
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
        Person other = (Person) obj;
        if (addresses == null) {
            if (other.addresses != null)
                return false;
        } else if (!addresses.equals(other.addresses))
            return false;
        if (applications == null) {
            if (other.applications != null)
                return false;
        } else if (!applications.equals(other.applications))
            return false;
        if (biography == null) {
            if (other.biography != null)
                return false;
        } else if (!biography.equals(other.biography))
            return false;
        if (delegation == null) {
            if (other.delegation != null)
                return false;
        } else if (!delegation.equals(other.delegation))
            return false;
        if (emails == null) {
            if (other.emails != null)
                return false;
        } else if (!emails.equals(other.emails))
            return false;
        if (externalIdentifiers == null) {
            if (other.externalIdentifiers != null)
                return false;
        } else if (!externalIdentifiers.equals(other.externalIdentifiers))
            return false;
        if (keywords == null) {
            if (other.keywords != null)
                return false;
        } else if (!keywords.equals(other.keywords))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (otherNames == null) {
            if (other.otherNames != null)
                return false;
        } else if (!otherNames.equals(other.otherNames))
            return false;
        if (researcherUrls == null) {
            if (other.researcherUrls != null)
                return false;
        } else if (!researcherUrls.equals(other.researcherUrls))
            return false;
        return true;
    }
}
