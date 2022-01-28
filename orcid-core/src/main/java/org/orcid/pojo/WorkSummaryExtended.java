package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class WorkSummaryExtended extends WorkSummary {
    @XmlElement(namespace = "http://www.orcid.org/ns/work")
    protected WorkContributors contributors;
    
    protected int realNumberOfContributors;
    protected List<String> contributorsGroupedByName;
    protected int numberOfContributorsGroupedByName;
    protected int numberOfContributorsGroupedByOrcid;
    protected List<String> contributorsGroupedByOrcid;

    public WorkContributors getContributors() {
        return contributors;
    }

    public void setContributors(WorkContributors contributors) {
        this.contributors = contributors;
    }

    public int getRealNumberOfContributors() {
        return realNumberOfContributors;
    }

    public void setRealNumberOfContributors(int realNumberOfContributors) {
        this.realNumberOfContributors = realNumberOfContributors;
    }

    public List<String> getContributorsGroupedByName() {
        return contributorsGroupedByName;
    }

    public void setContributorsGroupedByName(List<String> contributorsGroupedByName) {
        this.contributorsGroupedByName = contributorsGroupedByName;
    }

    public int getNumberOfContributorsGroupedByName() {
        return numberOfContributorsGroupedByName;
    }

    public void setNumberOfContributorsGroupedByName(int numberOfContributorsGroupedByName) {
        this.numberOfContributorsGroupedByName = numberOfContributorsGroupedByName;
    }

    public List<String> getContributorsGroupedByOrcid() {
        return contributorsGroupedByOrcid;
    }

    public void setContributorsGroupedByOrcid(List<String> contributorsGroupedByOrcid) {
        this.contributorsGroupedByOrcid = contributorsGroupedByOrcid;
    }

    public int getNumberOfContributorsGroupedByOrcid() {
        return numberOfContributorsGroupedByOrcid;
    }

    public void setNumberOfContributorsGroupedByOrcid(int numberOfContributorsGroupedByOrcid) {
        this.numberOfContributorsGroupedByOrcid = numberOfContributorsGroupedByOrcid;
    }
}


