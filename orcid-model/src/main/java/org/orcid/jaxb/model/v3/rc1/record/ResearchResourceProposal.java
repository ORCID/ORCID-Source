package org.orcid.jaxb.model.v3.rc1.record;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.Url;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "title", "hosts", "externalIdentifiers", "startDate", "endDate", "url" })
@XmlRootElement(name = "proposal", namespace = "http://www.orcid.org/ns/research-resource")
public class ResearchResourceProposal {

    @XmlElement(namespace = "http://www.orcid.org/ns/research-resource", name = "title")
    protected ResearchResourceTitle title;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "start-date")
    protected FuzzyDate startDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "end-date")
    protected FuzzyDate endDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/research-resource", name = "hosts")
    protected ResearchResourceHosts hosts;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "external-ids")
    protected ExternalIDs externalIdentifiers;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "url")
    protected Url url;

    public ResearchResourceTitle getTitle() {
        return title;
    }

    public void setTitle(ResearchResourceTitle title) {
        this.title = title;
    }

    public ResearchResourceHosts getHosts() {
        if (hosts == null)
            hosts = new ResearchResourceHosts();
        return hosts;
    }

    public void setHosts(ResearchResourceHosts hosts) {
        this.hosts = hosts;
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

    public ExternalIDs getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(ExternalIDs externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

}
