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
import org.orcid.jaxb.model.record.Activity;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.Educations;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.record.summary.Employments;
import org.orcid.jaxb.model.record.summary.FundingGroup;
import org.orcid.jaxb.model.record.summary.FundingSummary;
import org.orcid.jaxb.model.record.summary.Fundings;
import org.orcid.jaxb.model.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary.WorkGroup;
import org.orcid.jaxb.model.record.summary.WorkSummary;
import org.orcid.jaxb.model.record.summary.Works;
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
        String putCode = activity.getPutCode();
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
