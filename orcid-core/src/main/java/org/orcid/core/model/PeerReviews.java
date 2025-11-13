package org.orcid.core.model;

import java.io.Serializable;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "peerReviewPublicationGrants", "selfAssertedCount", "total" })
@XmlRootElement(name = "peer-reviews", namespace = "http://www.orcid.org/ns/summary")
@Schema(description = "Peer reviews")
public class PeerReviews implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "peer-review-publication-grants", namespace = "http://www.orcid.org/ns/summary")
    private Integer peerReviewPublicationGrants;
    @XmlElement(name = "self-asserted-count", namespace = "http://www.orcid.org/ns/summary")
    private Integer selfAssertedCount;
    @XmlElement(name = "total", namespace = "http://www.orcid.org/ns/summary")
    private Integer total;

    public Integer getPeerReviewPublicationGrants() {
        return peerReviewPublicationGrants;
    }

    public void setPeerReviewPublicationGrants(Integer peerReviewPublicationGrants) {
        this.peerReviewPublicationGrants = peerReviewPublicationGrants;
    }

    public Integer getSelfAssertedCount() {
        return selfAssertedCount;
    }

    public void setSelfAssertedCount(Integer selfAssertedCount) {
        this.selfAssertedCount = selfAssertedCount;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public int hashCode() {
        return Objects.hash(peerReviewPublicationGrants, selfAssertedCount, total);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PeerReviews other = (PeerReviews) obj;
        return Objects.equals(peerReviewPublicationGrants, other.peerReviewPublicationGrants) && Objects.equals(selfAssertedCount, other.selfAssertedCount)
                && Objects.equals(total, other.total);
    }
}
