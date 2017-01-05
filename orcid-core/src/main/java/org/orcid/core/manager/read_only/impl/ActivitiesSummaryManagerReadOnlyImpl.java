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
package org.orcid.core.manager.read_only.impl;

import java.util.List;

import org.orcid.core.manager.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.Educations;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc4.Employments;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.Fundings;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc4.Works;

public class ActivitiesSummaryManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ActivitiesSummaryManagerReadOnly {    
    private AffiliationsManagerReadOnly affiliationsManager;
    
    private ProfileFundingManagerReadOnly fundingManager;
    
    private PeerReviewManagerReadOnly peerReviewManager;
    
    private WorkManagerReadOnly workManager;

    public void setAffiliationsManager(AffiliationsManagerReadOnly affiliationsManager) {
        this.affiliationsManager = affiliationsManager;
    }

    public void setFundingManager(ProfileFundingManagerReadOnly fundingManager) {
        this.fundingManager = fundingManager;
    }

    public void setPeerReviewManager(PeerReviewManagerReadOnly peerReviewManager) {
        this.peerReviewManager = peerReviewManager;
    }

    public void setWorkManager(WorkManagerReadOnly workManager) {
        this.workManager = workManager;
    }    

    @Override
    public ActivitiesSummary getActivitiesSummary(String orcid) {
        return getActivitiesSummary(orcid, false);
    }

    @Override
    public ActivitiesSummary getPublicActivitiesSummary(String orcid) {
        return getActivitiesSummary(orcid, true);
    }

    public ActivitiesSummary getActivitiesSummary(String orcid, boolean justPublic) {
        long lastModifiedTime = getLastModified(orcid);
        ActivitiesSummary activities = new ActivitiesSummary();

        // Set educations
        List<EducationSummary> educationsList = affiliationsManager.getEducationSummaryList(orcid, lastModifiedTime);
        Educations educations = new Educations();
        for (EducationSummary summary : educationsList) {
            if (justPublic) {
                if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                    educations.getSummaries().add(summary);
                }
            } else {
                educations.getSummaries().add(summary);
            }
        }
        activities.setEducations(educations);

        // Set employments
        List<EmploymentSummary> employmentList = affiliationsManager.getEmploymentSummaryList(orcid, lastModifiedTime);

        Employments employments = new Employments();
        for (EmploymentSummary summary : employmentList) {
            if (justPublic) {
                if (Visibility.PUBLIC.equals(summary.getVisibility())) {
                    employments.getSummaries().add(summary);
                }
            } else {
                employments.getSummaries().add(summary);
            }
        }
        activities.setEmployments(employments);

        // Set fundings
        List<FundingSummary> fundingSummaries = fundingManager.getFundingSummaryList(orcid, lastModifiedTime);
        Fundings fundings = fundingManager.groupFundings(fundingSummaries, justPublic);        
        activities.setFundings(fundings);

        // Set peer reviews
        List<PeerReviewSummary> peerReviewSummaries = peerReviewManager.getPeerReviewSummaryList(orcid, lastModifiedTime);
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(peerReviewSummaries, justPublic);        
        activities.setPeerReviews(peerReviews);

        // Set works
        List<WorkSummary> workSummaries = workManager.getWorksSummaryList(orcid, lastModifiedTime);
        Works works = workManager.groupWorks(workSummaries, justPublic);        
        activities.setWorks(works);

        return activities;
    }
}
