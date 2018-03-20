/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.activitiesindexer.persistence.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "activities_status")
public class ActivitiesStatusEntity {
    private Date dateCreated;
    private Date lastModified;
    private String orcid;
    private Integer educationsStatus = 0;
    private Integer employmentsStatus = 0;
    private Integer fundingsStatus = 0;
    private Integer peerReviewsStatus = 0;
    private Integer worksStatus = 0;
    private Date educationsLastIndexed;
    private Date employmentsLastIndexed;
    private Date fundingsLastIndexed;
    private Date peerReviewsLastIndexed;
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

    @Column(name = "educations_status")
    public Integer getEducationsStatus() {
        return educationsStatus;
    }

    public void setEducationsStatus(Integer educationsStatus) {
        this.educationsStatus = educationsStatus;
    }

    @Column(name = "employments_status")
    public Integer getEmploymentsStatus() {
        return employmentsStatus;
    }

    public void setEmploymentsStatus(Integer employmentsStatus) {
        this.employmentsStatus = employmentsStatus;
    }

    @Column(name = "fundings_status")
    public Integer getFundingsStatus() {
        return fundingsStatus;
    }

    public void setFundingsStatus(Integer fundingsStatus) {
        this.fundingsStatus = fundingsStatus;
    }

    @Column(name = "peer_reviews_status")
    public Integer getPeerReviewsStatus() {
        return peerReviewsStatus;
    }

    public void setPeerReviewsStatus(Integer peerReviewsStatus) {
        this.peerReviewsStatus = peerReviewsStatus;
    }

    @Column(name = "works_status")
    public Integer getWorksStatus() {
        return worksStatus;
    }

    public void setWorksStatus(Integer worksStatus) {
        this.worksStatus = worksStatus;
    }

    @Column(name = "educations_last_indexed")
    public Date getEducationsLastIndexed() {
        return educationsLastIndexed;
    }

    public void setEducationsLastIndexed(Date educationsLastIndexed) {
        this.educationsLastIndexed = educationsLastIndexed;
    }

    @Column(name = "employments_last_indexed")
    public Date getEmploymentsLastIndexed() {
        return employmentsLastIndexed;
    }

    public void setEmploymentsLastIndexed(Date employmentsLastIndexed) {
        this.employmentsLastIndexed = employmentsLastIndexed;
    }

    @Column(name = "fundings_last_indexed")
    public Date getFundingsLastIndexed() {
        return fundingsLastIndexed;
    }

    public void setFundingsLastIndexed(Date fundingsLastIndexed) {
        this.fundingsLastIndexed = fundingsLastIndexed;
    }

    @Column(name = "peer_reviews_last_indexed")
    public Date getPeerReviewsLastIndexed() {
        return peerReviewsLastIndexed;
    }

    public void setPeerReviewsLastIndexed(Date peerReviewsLastIndexed) {
        this.peerReviewsLastIndexed = peerReviewsLastIndexed;
    }

    @Column(name = "works_last_indexed")
    public Date getWorksLastIndexed() {
        return worksLastIndexed;
    }

    public void setWorksLastIndexed(Date worksLastIndexed) {
        this.worksLastIndexed = worksLastIndexed;
    }
}
