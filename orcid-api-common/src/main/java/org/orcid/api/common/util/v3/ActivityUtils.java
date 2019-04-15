package org.orcid.api.common.util.v3;

import java.util.List;

import org.orcid.core.api.OrcidApiConstants;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.record.Activity;
import org.orcid.jaxb.model.v3.release.record.Distinction;
import org.orcid.jaxb.model.v3.release.record.Education;
import org.orcid.jaxb.model.v3.release.record.Employment;
import org.orcid.jaxb.model.v3.release.record.Funding;
import org.orcid.jaxb.model.v3.release.record.FundingContributor;
import org.orcid.jaxb.model.v3.release.record.InvitedPosition;
import org.orcid.jaxb.model.v3.release.record.Membership;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Affiliations;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceGroup;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Services;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
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
     */
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
        } else if (Distinction.class.isInstance(activity) || DistinctionSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_DISTINCTION;
        } else if (InvitedPosition.class.isInstance(activity) || InvitedPositionSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_INVITED_POSITION;
        } else if (Membership.class.isInstance(activity) || MembershipSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_MEMBERSHIP;
        } else if (Qualification.class.isInstance(activity) || QualificationSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_QUALIFICATION;
        } else if (Service.class.isInstance(activity) || ServiceSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_SERVICE;
        } else if (ResearchResource.class.isInstance(activity) || ResearchResourceSummary.class.isInstance(activity)) {
            activityType = OrcidApiConstants.ACTIVITY_RESEARCH_RESOURCE;
        }

        // Build the path string
        String path = '/' + orcid + '/' + activityType + '/' + putCode;

        activity.setPath(path);
    }

    /**
     * Set the path attribute to every affiliation inside the affiliations
     * element.
     * 
     * @param affiliations
     *            The affiliations container
     * @param orcid
     *            The activity owner
     */
    public static void setPathToAffiliations(Affiliations<? extends AffiliationSummary> affiliations, String orcid) {
        if (affiliations != null) {

            if (Distinctions.class.isInstance(affiliations)) {
                affiliations.setPath(OrcidApiConstants.DISTINCTIONS.replace("{orcid}", orcid));
            } else if (Educations.class.isInstance(affiliations)) {
                affiliations.setPath(OrcidApiConstants.EDUCATIONS.replace("{orcid}", orcid));
            } else if (Employments.class.isInstance(affiliations)) {
                affiliations.setPath(OrcidApiConstants.EMPLOYMENTS.replace("{orcid}", orcid));
            } else if (InvitedPositions.class.isInstance(affiliations)) {
                affiliations.setPath(OrcidApiConstants.INVITED_POSITIONS.replace("{orcid}", orcid));
            } else if (Memberships.class.isInstance(affiliations)) {
                affiliations.setPath(OrcidApiConstants.MEMBERSHIPS.replace("{orcid}", orcid));
            } else if (Qualifications.class.isInstance(affiliations)) {
                affiliations.setPath(OrcidApiConstants.QUALIFICATIONS.replace("{orcid}", orcid));
            } else if (Services.class.isInstance(affiliations)) {
                affiliations.setPath(OrcidApiConstants.SERVICES.replace("{orcid}", orcid));
            }

            for (AffiliationGroup<? extends AffiliationSummary> group : affiliations.retrieveGroups()) {
                for (AffiliationSummary summary : group.getActivities()) {
                    setPathToActivity(summary, orcid);
                }
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
     */
    public static void setPathToWorks(Works works, String orcid) {
        if (works != null) {
            works.setPath(OrcidApiConstants.WORKS.replace("{orcid}", orcid));
            for (WorkGroup group : works.getWorkGroup()) {
                for (WorkSummary summary : group.getWorkSummary()) {
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
     */
    public static void setPathToFundings(Fundings fundings, String orcid) {
        if (fundings != null) {
            fundings.setPath(OrcidApiConstants.FUNDINGS.replace("{orcid}", orcid));
            for (FundingGroup group : fundings.getFundingGroup()) {
                for (FundingSummary summary : group.getFundingSummary()) {
                    setPathToActivity(summary, orcid);
                }
            }
        }
    }

    /**
     * Set the path attribute to every peer review inside the PeerReviews
     * element.
     * 
     * @param PeerReviews
     *            The peer reviews container
     * @param orcid
     *            The activity owner
     */
    public static void setPathToPeerReviews(PeerReviews peerReviews, String orcid) {
        if (peerReviews != null) {
            peerReviews.setPath(OrcidApiConstants.PEER_REVIEWS.replace("{orcid}", orcid));
            for (PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
                for (PeerReviewDuplicateGroup duplicateGroup : group.getPeerReviewGroup()) {
                    List<PeerReviewSummary> summaries = duplicateGroup.getPeerReviewSummary();
                    for (PeerReviewSummary summary : summaries) {
                        setPathToActivity(summary, orcid);
                    }
                }
            }
        }
    }

    /**
     * Set the path attribute to every peer review inside the PeerReviews
     * element.
     * 
     * @param PeerReviews
     *            The peer reviews container
     * @param orcid
     *            The activity owner
     */
    public static void setPathToResearchResources(ResearchResources rr, String orcid) {
        if (rr != null) {
            rr.setPath(OrcidApiConstants.RESEARCH_RESOURCES.replace("{orcid}", orcid));
            for (ResearchResourceGroup group : rr.getResearchResourceGroup()) {
                for (ResearchResourceSummary summary : group.getResearchResourceSummary()) {
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
     */
    public static void setPathToActivity(ActivitiesSummary activitiesSummary, String orcid) {
        if (activitiesSummary != null) {
            activitiesSummary.setPath(OrcidApiConstants.ACTIVITIES.replace("{orcid}", orcid));
            ActivityUtils.setPathToAffiliations(activitiesSummary.getDistinctions(), orcid);
            ActivityUtils.setPathToAffiliations(activitiesSummary.getEducations(), orcid);
            ActivityUtils.setPathToAffiliations(activitiesSummary.getEmployments(), orcid);
            ActivityUtils.setPathToAffiliations(activitiesSummary.getInvitedPositions(), orcid);
            ActivityUtils.setPathToAffiliations(activitiesSummary.getMemberships(), orcid);
            ActivityUtils.setPathToAffiliations(activitiesSummary.getQualifications(), orcid);
            ActivityUtils.setPathToAffiliations(activitiesSummary.getServices(), orcid);
            ActivityUtils.setPathToFundings(activitiesSummary.getFundings(), orcid);
            ActivityUtils.setPathToWorks(activitiesSummary.getWorks(), orcid);
            ActivityUtils.setPathToPeerReviews(activitiesSummary.getPeerReviews(), orcid);
            ActivityUtils.setPathToResearchResources(activitiesSummary.getResearchResources(), orcid);
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
        if (summaries != null) {
            if (summaries.getWorks() != null && summaries.getWorks().getWorkGroup() != null) {
                for (WorkGroup group : summaries.getWorks().getWorkGroup()) {
                    if (group.getWorkSummary() != null) {
                        for (WorkSummary summary : group.getWorkSummary()) {
                            cleanEmptyFields(summary);
                        }
                    }
                }
            }
        }
    }

    public static void cleanEmptyFields(Works works) {
        if (works != null) {
            if (works.getWorkGroup() != null) {
                for (WorkGroup group : works.getWorkGroup()) {
                    if (group.getWorkSummary() != null) {
                        for (WorkSummary summary : group.getWorkSummary()) {
                            cleanEmptyFields(summary);
                        }
                    }
                }
            }
        }
    }

    public static void cleanEmptyFields(WorkSummary summary) {
        if (summary != null) {
            if (summary.getTitle() != null) {
                if (summary.getTitle().getTranslatedTitle() != null) {
                    if (PojoUtil.isEmpty(summary.getTitle().getTranslatedTitle().getContent())) {
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
        if (work != null) {
            if (work.getWorkCitation() != null) {
                if (PojoUtil.isEmpty(work.getWorkCitation().getCitation())) {
                    work.setWorkCitation(null);
                }
            }

            if (work.getWorkTitle() != null) {
                if (work.getWorkTitle().getTranslatedTitle() != null) {
                    if (PojoUtil.isEmpty(work.getWorkTitle().getTranslatedTitle().getContent())) {
                        work.getWorkTitle().setTranslatedTitle(null);
                    }
                }
            }

            if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null) {
                for (Contributor c : work.getWorkContributors().getContributor()) {
                    if (c.getCreditName() != null && PojoUtil.isEmpty(c.getCreditName().getContent())) {
                        c.setCreditName(null);
                    }
                }
            }
        }
    }

    public static void cleanEmptyFields(Funding funding) {
        if (funding != null && funding.getContributors() != null && !funding.getContributors().getContributor().isEmpty()) {
            for (FundingContributor c : funding.getContributors().getContributor()) {
                if (c.getCreditName() != null && PojoUtil.isEmpty(c.getCreditName().getContent())) {
                    c.setCreditName(null);
                }
            }
        }
    }
}
