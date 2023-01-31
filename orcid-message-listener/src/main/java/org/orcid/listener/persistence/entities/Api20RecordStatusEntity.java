package org.orcid.listener.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "api_2_0_record_status")
public class Api20RecordStatusEntity {
    private String orcid;
    private Date dateCreated;
    private Date lastModified;
    private Integer summaryStatus;
    private Date summaryLastIndexed;
    private Integer educationsStatus;
    private Date educationsLastIndexed;
    private Integer employmentsStatus;
    private Date employmentsLastIndexed;
    private Integer fundingsStatus;
    private Date fundingsLastIndexed;
    private Integer peerReviewsStatus;
    private Date peerReviewsLastIndexed;
    private Integer worksStatus;
    private Date worksLastIndexed;

    @Id
    @Column(name = "orcid", length = 19)
    public String getId() {
        return orcid;
    }

    public void setId(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name = "last_modified")
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Column(name = "summary_status")
    public Integer getSummaryStatus() {
        return summaryStatus;
    }

    public void setSummaryStatus(Integer summaryStatus) {
        this.summaryStatus = summaryStatus;
    }

    @Column(name = "summary_last_indexed")
    public Date getSummaryLastIndexed() {
        return summaryLastIndexed;
    }

    public void setSummaryLastIndexed(Date summaryLastIndexed) {
        this.summaryLastIndexed = summaryLastIndexed;

    }

    @Column(name = "educations_status")
    public Integer getEducationsStatus() {
        return educationsStatus;
    }

    public void setEducationsStatus(Integer educationsStatus) {
        this.educationsStatus = educationsStatus;
    }

    @Column(name = "educations_last_indexed")
    public Date getEducationsLastIndexed() {
        return educationsLastIndexed;
    }

    public void setEducationsLastIndexed(Date educationsLastIndexed) {
        this.educationsLastIndexed = educationsLastIndexed;
    }

    @Column(name = "employments_status")
    public Integer getEmploymentsStatus() {
        return employmentsStatus;
    }

    public void setEmploymentsStatus(Integer employmentsStatus) {
        this.employmentsStatus = employmentsStatus;
    }

    @Column(name = "employments_last_indexed")
    public Date getEmploymentsLastIndexed() {
        return employmentsLastIndexed;
    }

    public void setEmploymentsLastIndexed(Date employmentsLastIndexed) {
        this.employmentsLastIndexed = employmentsLastIndexed;
    }

    @Column(name = "fundings_status")
    public Integer getFundingsStatus() {
        return fundingsStatus;
    }

    public void setFundingsStatus(Integer fundingsStatus) {
        this.fundingsStatus = fundingsStatus;
    }

    @Column(name = "fundings_last_indexed")
    public Date getFundingsLastIndexed() {
        return fundingsLastIndexed;
    }

    public void setFundingsLastIndexed(Date fundingsLastIndexed) {
        this.fundingsLastIndexed = fundingsLastIndexed;
    }

    @Column(name = "peer_reviews_status")
    public Integer getPeerReviewsStatus() {
        return peerReviewsStatus;
    }

    public void setPeerReviewsStatus(Integer peerReviewsStatus) {
        this.peerReviewsStatus = peerReviewsStatus;
    }

    @Column(name = "peer_reviews_last_indexed")
    public Date getPeerReviewsLastIndexed() {
        return peerReviewsLastIndexed;
    }

    public void setPeerReviewsLastIndexed(Date peerReviewsLastIndexed) {
        this.peerReviewsLastIndexed = peerReviewsLastIndexed;
    }
    
    @Column(name = "works_status")
    public Integer getWorksStatus() {
        return worksStatus;
    }

    public void setWorksStatus(Integer worksStatus) {
        this.worksStatus = worksStatus;
    }

    @Column(name = "works_last_indexed")
    public Date getWorksLastIndexed() {
        return worksLastIndexed;
    }

    public void setWorksLastIndexed(Date worksLastIndexed) {
        this.worksLastIndexed = worksLastIndexed;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
        result = prime * result + ((educationsLastIndexed == null) ? 0 : educationsLastIndexed.hashCode());
        result = prime * result + ((educationsStatus == null) ? 0 : educationsStatus.hashCode());
        result = prime * result + ((employmentsLastIndexed == null) ? 0 : employmentsLastIndexed.hashCode());
        result = prime * result + ((employmentsStatus == null) ? 0 : employmentsStatus.hashCode());
        result = prime * result + ((fundingsLastIndexed == null) ? 0 : fundingsLastIndexed.hashCode());
        result = prime * result + ((fundingsStatus == null) ? 0 : fundingsStatus.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((peerReviewsLastIndexed == null) ? 0 : peerReviewsLastIndexed.hashCode());
        result = prime * result + ((peerReviewsStatus == null) ? 0 : peerReviewsStatus.hashCode());
        result = prime * result + ((summaryLastIndexed == null) ? 0 : summaryLastIndexed.hashCode());
        result = prime * result + ((summaryStatus == null) ? 0 : summaryStatus.hashCode());
        result = prime * result + ((worksLastIndexed == null) ? 0 : worksLastIndexed.hashCode());
        result = prime * result + ((worksStatus == null) ? 0 : worksStatus.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Api20RecordStatusEntity other = (Api20RecordStatusEntity) obj;
        if (dateCreated == null) {
            if (other.dateCreated != null)
                return false;
        } else if (!dateCreated.equals(other.dateCreated))
            return false;
        if (educationsLastIndexed == null) {
            if (other.educationsLastIndexed != null)
                return false;
        } else if (!educationsLastIndexed.equals(other.educationsLastIndexed))
            return false;
        if (educationsStatus == null) {
            if (other.educationsStatus != null)
                return false;
        } else if (!educationsStatus.equals(other.educationsStatus))
            return false;
        if (employmentsLastIndexed == null) {
            if (other.employmentsLastIndexed != null)
                return false;
        } else if (!employmentsLastIndexed.equals(other.employmentsLastIndexed))
            return false;
        if (employmentsStatus == null) {
            if (other.employmentsStatus != null)
                return false;
        } else if (!employmentsStatus.equals(other.employmentsStatus))
            return false;
        if (fundingsLastIndexed == null) {
            if (other.fundingsLastIndexed != null)
                return false;
        } else if (!fundingsLastIndexed.equals(other.fundingsLastIndexed))
            return false;
        if (fundingsStatus == null) {
            if (other.fundingsStatus != null)
                return false;
        } else if (!fundingsStatus.equals(other.fundingsStatus))
            return false;
        if (lastModified == null) {
            if (other.lastModified != null)
                return false;
        } else if (!lastModified.equals(other.lastModified))
            return false;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        if (peerReviewsLastIndexed == null) {
            if (other.peerReviewsLastIndexed != null)
                return false;
        } else if (!peerReviewsLastIndexed.equals(other.peerReviewsLastIndexed))
            return false;
        if (peerReviewsStatus == null) {
            if (other.peerReviewsStatus != null)
                return false;
        } else if (!peerReviewsStatus.equals(other.peerReviewsStatus))
            return false;
        if (summaryLastIndexed == null) {
            if (other.summaryLastIndexed != null)
                return false;
        } else if (!summaryLastIndexed.equals(other.summaryLastIndexed))
            return false;
        if (summaryStatus == null) {
            if (other.summaryStatus != null)
                return false;
        } else if (!summaryStatus.equals(other.summaryStatus))
            return false;
        if (worksLastIndexed == null) {
            if (other.worksLastIndexed != null)
                return false;
        } else if (!worksLastIndexed.equals(other.worksLastIndexed))
            return false;
        if (worksStatus == null) {
            if (other.worksStatus != null)
                return false;
        } else if (!worksStatus.equals(other.worksStatus))
            return false;
        return true;
    }

}
