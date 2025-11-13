package org.orcid.core.model;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.Resource;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.OrcidIdentifier;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "createdDate", "lastModifiedDate", "creditName", "orcidIdentifier", "externalIdentifiers", "employments", "professionalActivities", "fundings",
        "works", "peerReviews", "emailDomains", "educationQualifications", "researchResources" })
@XmlRootElement(name = "record-summary", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Record summary")
public class RecordSummary implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlTransient
    @Resource
    private OrcidUrlManager orcidUrlManager;
    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    private LastModifiedDate lastModifiedDate;
    @XmlElement(name = "created-date", namespace = "http://www.orcid.org/ns/common")
    private CreatedDate createdDate;
    @XmlElement(name = "credit-name", namespace = "http://www.orcid.org/ns/summary")
    private String creditName;
    @XmlElement(name = "orcid-identifier", namespace = "http://www.orcid.org/ns/common")
    protected OrcidIdentifier orcidIdentifier;
    @XmlElement(name = "external-identifiers", namespace = "http://www.orcid.org/ns/summary")
    private ExternalIdentifiers externalIdentifiers;
    @XmlElement(name = "employments", namespace = "http://www.orcid.org/ns/summary")
    private Employments employments;
    @XmlElement(name = "professional-activities", namespace = "http://www.orcid.org/ns/summary")
    private ProfessionalActivities professionalActivities;
    @XmlElement(name = "fundings", namespace = "http://www.orcid.org/ns/summary")
    private Fundings fundings;
    @XmlElement(name = "works", namespace = "http://www.orcid.org/ns/summary")
    private Works works;
    @XmlElement(name = "peer-reviews", namespace = "http://www.orcid.org/ns/summary")
    private PeerReviews peerReviews;

    @XmlElement(name = "education-qualifications", namespace = "http://www.orcid.org/ns/summary")
    private EducationQualifications educationQualifications;
    @XmlElement(name = "research-resources", namespace = "http://www.orcid.org/ns/summary")
    private ResearchResources researchResources;
    @XmlElement(name = "email-domains", namespace = "http://www.orcid.org/ns/summary")
    private EmailDomains emailDomains;

    public OrcidIdentifier getOrcidIdentifier() {
        return orcidIdentifier;
    }

    public void setOrcidIdentifier(OrcidIdentifier orcidIdentifier) {
        this.orcidIdentifier = orcidIdentifier;
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

    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    public ExternalIdentifiers getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(ExternalIdentifiers externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public Employments getEmployments() {
        return employments;
    }

    public void setEmployments(Employments employments) {
        this.employments = employments;
    }

    public ProfessionalActivities getProfessionalActivities() {
        return professionalActivities;
    }

    public void setProfessionalActivities(ProfessionalActivities professionalActivities) {
        this.professionalActivities = professionalActivities;
    }

    public Fundings getFundings() {
        return fundings;
    }

    public void setFundings(Fundings fundings) {
        this.fundings = fundings;
    }

    public Works getWorks() {
        return works;
    }

    public void setWorks(Works works) {
        this.works = works;
    }

    public PeerReviews getPeerReviews() {
        return peerReviews;
    }

    public void setPeerReviews(PeerReviews peerReviews) {
        this.peerReviews = peerReviews;
    }

    public EducationQualifications getEducationQualifications() {
        return educationQualifications;
    }

    public void setEducationQualifications(EducationQualifications educationQualifications) {
        this.educationQualifications = educationQualifications;
    }

    public ResearchResources getResearchResources() {
        return researchResources;
    }

    public void setResearchResources(ResearchResources researchResources) {
        this.researchResources = researchResources;
    }

    public EmailDomains getEmailDomains() {
        return emailDomains;
    }

    public void setEmailDomains(EmailDomains emailDomains) {
        this.emailDomains = emailDomains;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdDate, creditName, employments, externalIdentifiers, fundings, lastModifiedDate, orcidIdentifier, orcidUrlManager, peerReviews,
                professionalActivities, works, emailDomains, educationQualifications, researchResources );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RecordSummary other = (RecordSummary) obj;
        return Objects.equals(createdDate, other.createdDate) && Objects.equals(creditName, other.creditName) && Objects.equals(employments, other.employments)
                && Objects.equals(externalIdentifiers, other.externalIdentifiers) && Objects.equals(fundings, other.fundings)
                && Objects.equals(lastModifiedDate, other.lastModifiedDate) && Objects.equals(orcidIdentifier, other.orcidIdentifier)
                && Objects.equals(orcidUrlManager, other.orcidUrlManager) && Objects.equals(peerReviews, other.peerReviews)
                && Objects.equals(professionalActivities, other.professionalActivities) && Objects.equals(works, other.works) 
                && Objects.equals(educationQualifications, other.educationQualifications) && Objects.equals(researchResources, other.researchResources) 
                && Objects.equals(emailDomains, other.emailDomains) ;
    }
}
