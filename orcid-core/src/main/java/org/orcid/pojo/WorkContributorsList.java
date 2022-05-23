package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.common.Contributor;

import java.util.Objects;

public class WorkContributorsList {

    protected Contributor contributor;

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkContributorsList that = (WorkContributorsList) o;
        return Objects.equals(getContributor(), that.getContributor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContributor());
    }
}
