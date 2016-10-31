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
package org.orcid.api.common.util;

import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc3.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.Activity;
import org.orcid.jaxb.model.record_rc3.Education;
import org.orcid.jaxb.model.record_rc3.Employment;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.Work;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ActivityUtils {

    /**
     * Set the path attribute to an activity, it will look like
     * 
     * /orcid/activity-type/putCode
     * 
     * @param Activity
     *            An activity object
     * @param orcid
     *            The activity owner
     * */
    public static void setPathToActivity(Activity activity, String orcid) {
        Long putCode = activity.getPutCode();
        String activityType = OrcidApiConstants.ACTIVITY_WORK;

        if (Education.class.isInstance(activity) || EducationSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_EDUCATION;
        } else if (Employment.class.isInstance(activity) || EmploymentSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_EMPLOYMENT;
        } else if (Funding.class.isInstance(activity) || FundingSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_FUNDING;
        } else if (PeerReview.class.isInstance(activity) || PeerReviewSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_PEER_REVIEW;
        }
        // Build the path string
        String path = '/' + orcid + '/' + activityType + '/' + putCode;

        activity.setPath(path);
    }

    /**
     * Set the path attribute to every work inside the Works element.
     * 
     * @param Works
     *            The works container
     * @param orcid
     *            The activity owner
     * */
    public static void setPathToWorks(Works works, String orcid) {
        if(works != null && works.getWorkGroup() != null) {
            for(WorkGroup group : works.getWorkGroup()) {
                for(WorkSummary summary : group.getWorkSummary()) {
                    setPathToActivity(summary, orcid);
                }
            }
        }
    }
    
    /**
     * Set the path attribute to every funding inside the Fundings element.
     * 
     * @param Fundings
     *            The fundings container
     * @param orcid
     *            The activity owner
     * */
    public static void setPathToFundings(Fundings fundings, String orcid) {
        if(fundings != null && fundings.getFundingGroup() != null) {
            for(FundingGroup group : fundings.getFundingGroup()) {
                for(FundingSummary summary : group.getFundingSummary()) {
                    setPathToActivity(summary, orcid);
                }
            }
        }
    }
    
    /**
     * Set the path attribute to every peer review inside the PeerReviews element.
     * 
     * @param PeerReviews
     *            The peer reviews container
     * @param orcid
     *            The activity owner
     * */
    public static void setPathToPeerReviews(PeerReviews peerReviews, String orcid) {
        if(peerReviews != null && peerReviews.getPeerReviewGroup() != null) {
            for(PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
                for(PeerReviewSummary summary : group.getPeerReviewSummary()) {
                    setPathToActivity(summary, orcid);
                }
            }
        }
    }    
    
    /**
     * Set the path attribute to all activities in the summary object
     * 
     * @param ActivitiesSummary
     * @param orcid
     *            The activity owner
     * */
    public static void setPathToActivity(ActivitiesSummary activitiesSummary, String orcid) {
        Educations educations = activitiesSummary.getEducations();
        Employments employments = activitiesSummary.getEmployments();
        Fundings fundings = activitiesSummary.getFundings();
        Works works = activitiesSummary.getWorks();
        PeerReviews peerReviews = activitiesSummary.getPeerReviews();

        if (educations != null && !educations.getSummaries().isEmpty()) {
            for (EducationSummary summary : educations.getSummaries()) {
                ActivityUtils.setPathToActivity(summary, orcid);
            }
        }

        if (employments != null && !employments.getSummaries().isEmpty()) {
            for (EmploymentSummary summary : employments.getSummaries()) {
                ActivityUtils.setPathToActivity(summary, orcid);
            }
        }

        if (!fundings.getFundingGroup().isEmpty()) {
            for (FundingGroup group : fundings.getFundingGroup()) {
                if (!group.getFundingSummary().isEmpty()) {
                    for (FundingSummary summary : group.getFundingSummary()) {
                        ActivityUtils.setPathToActivity(summary, orcid);
                    }
                }
            }
        }

        if (!works.getWorkGroup().isEmpty()) {
            for (WorkGroup group : works.getWorkGroup()) {
                if (!group.getWorkSummary().isEmpty()) {
                    for (WorkSummary summary : group.getWorkSummary()) {
                        ActivityUtils.setPathToActivity(summary, orcid);
                    }
                }
            }
        }
        
        if(!peerReviews.getPeerReviewGroup().isEmpty()) {
            for(PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
                if(!group.getPeerReviewSummary().isEmpty()) {
                    for(PeerReviewSummary summary : group.getPeerReviewSummary()) {
                        ActivityUtils.setPathToActivity(summary, orcid);
                    }
                }
            }
        }
    }
    
    public static void cleanEmptyFields(ActivitiesSummary summaries) {
        if(summaries != null) {
            if(summaries.getWorks() != null && summaries.getWorks().getWorkGroup() != null) {
                for(WorkGroup group : summaries.getWorks().getWorkGroup()) {
                    if(group.getWorkSummary() != null) {
                        for(WorkSummary summary : group.getWorkSummary()) {
                            cleanEmptyFields(summary);
                        }
                    }
                }
            }
        }
    }
    
    public static void cleanEmptyFields(Works works) {
        if(works != null) {
            if(works.getWorkGroup() != null) {
                for(WorkGroup group : works.getWorkGroup()) {
                    if(group.getWorkSummary() != null) {
                        for(WorkSummary summary : group.getWorkSummary()) {
                            cleanEmptyFields(summary);
                        }
                    }
                }
            }
        }
    }
    
    public static void cleanEmptyFields(WorkSummary summary) {
        if(summary != null) {
            if(summary.getTitle() != null) {
                if(summary.getTitle().getTranslatedTitle() != null) {
                    if(PojoUtil.isEmpty(summary.getTitle().getTranslatedTitle().getContent())) {
                        summary.getTitle().setTranslatedTitle(null);
                    }
                }
            }
        }
    }
    
    public static void cleanEmptyFields(Work work) {
        if(work != null) {
            if(work.getWorkCitation() != null) {
                if(PojoUtil.isEmpty(work.getWorkCitation().getCitation())) {
                    work.setWorkCitation(null);
                }
            }
            
            if(work.getWorkTitle() != null) {
                if(work.getWorkTitle().getTranslatedTitle() != null) {
                    if(PojoUtil.isEmpty(work.getWorkTitle().getTranslatedTitle().getContent())) {
                        work.getWorkTitle().setTranslatedTitle(null);
                    }
                }
            }
        }
    }
}
