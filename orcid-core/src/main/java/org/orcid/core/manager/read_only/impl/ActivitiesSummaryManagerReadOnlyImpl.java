package org.orcid.core.manager.read_only.impl;

import java.util.List;

import org.orcid.core.manager.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;

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
        List<EducationSummary> educationsList = affiliationsManager.getEducationSummaryList(orcid);
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
        List<EmploymentSummary> employmentList = affiliationsManager.getEmploymentSummaryList(orcid);

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
        List<FundingSummary> fundingSummaries = fundingManager.getFundingSummaryList(orcid);
        Fundings fundings = fundingManager.groupFundings(fundingSummaries, justPublic);        
        activities.setFundings(fundings);

        // Set peer reviews
        List<PeerReviewSummary> peerReviewSummaries = peerReviewManager.getPeerReviewSummaryList(orcid);
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(peerReviewSummaries, justPublic);        
        activities.setPeerReviews(peerReviews);

        // Set works
        List<WorkSummary> workSummaries = workManager.getWorksSummaryList(orcid);
        Works works = workManager.groupWorks(workSummaries, justPublic);        
        activities.setWorks(works);

        return activities;
    }
}
