package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;

import javax.xml.bind.annotation.XmlElement;

public class WorkSummaryExtended extends WorkSummary {
    @XmlElement(namespace = "http://www.orcid.org/ns/work")
    protected WorkContributors contributors;

    public WorkContributors getContributors() {
        return contributors;
    }

    public void setContributors(WorkContributors contributors) {
        this.contributors = contributors;
    }
}
