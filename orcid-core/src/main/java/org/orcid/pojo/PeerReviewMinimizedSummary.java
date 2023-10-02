package org.orcid.pojo;

import org.orcid.jaxb.model.v3.release.common.Visibility;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PeerReviewMinimizedSummary implements Serializable {
    private static final long serialVersionUID = 5900200502612060021L;

    private String orcid;
    private BigInteger groupId;
    private String groupIdValue;
    private List<BigInteger> putCodes = new ArrayList<>();
    private Visibility visibility;
    private boolean visibilityError;
    private String name;
    private int duplicated;
    private String sourceId;
    private String clientSourceId;
    private String assertionOriginSourceId;
    
    public PeerReviewMinimizedSummary(String orcid, BigInteger groupId, String groupIdValue, BigInteger putCode, Visibility visibility, String name, int duplicated) {
        this.orcid = orcid;
        this.groupId = groupId;
        this.groupIdValue = groupIdValue;
        this.visibility = visibility;
        this.name  = name;
        this.duplicated  = duplicated;
        addPutCode(putCode);
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

    public List<BigInteger> getPutCodes() {
        return putCodes;
    }

    public void setPutCodes(List<BigInteger> putCodes) {
        this.putCodes = putCodes;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public boolean getVisibilityError() {
        return visibilityError;
    }

    public void setVisibilityError(boolean visibilityError) {
        this.visibilityError = visibilityError;
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

    public void addPutCode(BigInteger putCode) {
        this.putCodes.add(putCode);
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getClientSourceId() {
        return clientSourceId;
    }

    public void setClientSourceId(String clientSourceId) {
        this.clientSourceId = clientSourceId;
    }

    public String getAssertionOriginSourceId() {
        return assertionOriginSourceId;
    }

    public void setAssertionOriginSourceId(String assertionOriginSourceId) {
        this.assertionOriginSourceId = assertionOriginSourceId;
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
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getSourceId(), that.getSourceId()) &&
                Objects.equals(getClientSourceId(), that.getClientSourceId()) &&
                Objects.equals(getAssertionOriginSourceId(), that.getAssertionOriginSourceId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrcid(), getGroupId(), getGroupIdValue(), getVisibility(), getName(), getDuplicated(), getSourceId(), getClientSourceId(), getAssertionOriginSourceId());
    }
}
