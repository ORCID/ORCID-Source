package org.orcid.api.common.util;

import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.FundingContributor;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
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
     * Set the path attribute to every education inside the Educations element.
     * 
     * @param educations
     *            The educations container
     * @param orcid
     *            The activity owner
     * */
    public static void setPathToEducations(Educations educations, String orcid) {
        if(educations != null) { 
            educations.setPath(OrcidApiConstants.EDUCATIONS.replace("{orcid}", orcid));
            for(EducationSummary summary : educations.getSummaries()) {
                setPathToActivity(summary, orcid);
            }            
        }
    }
    
    /**
     * Set the path attribute to every employment inside the Employments element.
     * 
     * @param employments
     *            The employments container
     * @param orcid
     *            The activity owner
     * */
    public static void setPathToEmployments(Employments employments, String orcid) {
        if(employments != null) {  
            employments.setPath(OrcidApiConstants.EMPLOYMENTS.replace("{orcid}", orcid));
            for(EmploymentSummary summary : employments.getSummaries()) {
                setPathToActivity(summary, orcid);
            }            
        }
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
        if(works != null) {
            works.setPath(OrcidApiConstants.WORKS.replace("{orcid}", orcid));
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
        if(fundings != null) {
            fundings.setPath(OrcidApiConstants.FUNDINGS.replace("{orcid}", orcid));
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
        if(peerReviews != null) {
            peerReviews.setPath(OrcidApiConstants.PEER_REVIEWS.replace("{orcid}", orcid));
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
        if (activitiesSummary != null) {
            activitiesSummary.setPath(OrcidApiConstants.ACTIVITIES.replace("{orcid}", orcid));
            ActivityUtils.setPathToEducations(activitiesSummary.getEducations(), orcid);
            ActivityUtils.setPathToEmployments(activitiesSummary.getEmployments(), orcid);
            ActivityUtils.setPathToFundings(activitiesSummary.getFundings(), orcid);
            ActivityUtils.setPathToWorks(activitiesSummary.getWorks(), orcid);
            ActivityUtils.setPathToPeerReviews(activitiesSummary.getPeerReviews(), orcid);

        }
    }
    
    /**
     * Set the path attribute to all works in the workBulk element
     * 
     * @param workBulk
     */
    public static void setPathToBulk(WorkBulk workBulk, String orcid) {
        if (workBulk != null && workBulk.getBulk() != null) {
            workBulk.getBulk().forEach(b -> {
                if (b instanceof Work) {
                    setPathToActivity((Work) b, orcid);
                }
            });
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
    
    public static void cleanEmptyFields(WorkBulk workBulk) {
        if (workBulk != null && workBulk.getBulk() != null) {
            workBulk.getBulk().forEach(b -> cleanEmptyFields(b));
        }
    }
    
    public static void cleanEmptyFields(BulkElement bulk) {
        if (bulk instanceof Work) {
            cleanEmptyFields((Work) bulk);
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
            
            if(work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null) {
                for(Contributor c : work.getWorkContributors().getContributor()) {
                    if(c.getCreditName() != null && PojoUtil.isEmpty(c.getCreditName().getContent())) {
                        c.setCreditName(null);
                    }
                }
            }
        }
    }

    public static void cleanEmptyFields(Funding funding) {
        if(funding != null && funding.getContributors() != null && !funding.getContributors().getContributor().isEmpty()) {
            for(FundingContributor c : funding.getContributors().getContributor()) {
                if(c.getCreditName() != null && PojoUtil.isEmpty(c.getCreditName().getContent())) {
                    c.setCreditName(null);
                }
            }
        }
    }
}
