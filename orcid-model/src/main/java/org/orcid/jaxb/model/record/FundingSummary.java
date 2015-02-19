package org.orcid.jaxb.model.record;

import javax.xml.bind.annotation.XmlElement;

public class FundingSummary {
    
    @XmlElement(namespace = "http://www.orcid.org/ns/funding", required = true)
    protected FundingType type;
    @XmlElement(required = true, namespace = "http://www.orcid.org/ns/funding")
    protected FundingTitle title;
    @XmlElement(namespace = "http://www.orcid.org/ns/funding")
    protected FundingExternalIdentifiers externalIdentifiers;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected FuzzyDate startDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected FuzzyDate endDate;
    
    public FundingType getType() {
        return type;
    }
    public void setType(FundingType type) {
        this.type = type;
    }
    public FundingTitle getTitle() {
        return title;
    }
    public void setTitle(FundingTitle title) {
        this.title = title;
    }
    public FundingExternalIdentifiers getExternalIdentifiers() {
        return externalIdentifiers;
    }
    public void setExternalIdentifiers(FundingExternalIdentifiers externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
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
}
