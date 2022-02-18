package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.common.Visibility;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

public class PeerReviewMinimizedSummary implements Serializable {

    private String orcid;
    private BigInteger groupId;
    private String groupIdValue;
    private Visibility visibility;
    private String name;
    private int duplicated;

    public PeerReviewMinimizedSummary(String orcid, BigInteger groupId, String groupIdValue, Visibility visibility, String name, int duplicated) {
        this.orcid = orcid;
        this.groupId = groupId;
        this.groupIdValue = groupIdValue;
        this.visibility = visibility;
        this.name  = name;
        this.duplicated  = duplicated;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public BigInteger getGroupId() {
        return groupId;
    }

    public void setGroupId(BigInteger groupId) {
        this.groupId = groupId;
    }

    public String getGroupIdValue() {
        return groupIdValue;
    }

    public void setGroupIdValue(String groupIdValue) {
        this.groupIdValue = groupIdValue;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuplicated() {
        return duplicated;
    }

    public void setDuplicated(int duplicated) {
        this.duplicated = duplicated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeerReviewMinimizedSummary that = (PeerReviewMinimizedSummary) o;
        return getDuplicated() == that.getDuplicated() &&
                Objects.equals(getOrcid(), that.getOrcid()) &&
                Objects.equals(getGroupId(), that.getGroupId()) &&
                Objects.equals(getGroupIdValue(), that.getGroupIdValue()) &&
                getVisibility() == that.getVisibility() &&
                Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrcid(), getGroupId(), getGroupIdValue(), getVisibility(), getName(), getDuplicated());
    }
}
