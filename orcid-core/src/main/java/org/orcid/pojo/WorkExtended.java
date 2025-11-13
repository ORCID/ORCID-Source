package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.record.Work;

import java.util.List;

public class WorkExtended extends Work {
    List<ContributorsRolesAndSequences> contributorsGroupedByOrcid;
    Integer featuredDisplayIndex;

    public List<ContributorsRolesAndSequences> getContributorsGroupedByOrcid() {
        return contributorsGroupedByOrcid;
    }

    public void setContributorsGroupedByOrcid(List<ContributorsRolesAndSequences> contributorsGroupedByOrcid) {
        this.contributorsGroupedByOrcid = contributorsGroupedByOrcid;
    }

    public Integer getFeaturedDisplayIndex() {
        return featuredDisplayIndex;
    }

    public void setFeaturedDisplayIndex(Integer featuredDisplayIndex) {
        this.featuredDisplayIndex = featuredDisplayIndex;
    }
}
