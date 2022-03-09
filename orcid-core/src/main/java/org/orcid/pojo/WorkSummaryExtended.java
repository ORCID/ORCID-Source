package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class WorkSummaryExtended extends WorkSummary {
    @XmlElement(namespace = "http://www.orcid.org/ns/work")
    protected WorkContributors contributors;
    
    protected List<ContributorsRolesAndSequences> contributorsGroupedByOrcid;

    public WorkContributors getContributors() {
        return contributors;
    }

    public void setContributors(WorkContributors contributors) {
        this.contributors = contributors;
    }

    public List<ContributorsRolesAndSequences> getContributorsGroupedByOrcid() {
        return contributorsGroupedByOrcid;
    }

    public void setContributorsGroupedByOrcid(List<ContributorsRolesAndSequences> contributorsGroupedByOrcid) {
        this.contributorsGroupedByOrcid = contributorsGroupedByOrcid;
    }
}


