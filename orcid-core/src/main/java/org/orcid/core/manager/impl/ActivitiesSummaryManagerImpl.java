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
package org.orcid.core.manager.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.ActivitiesSummaryManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.version.impl.Api2_0_rc3_LastModifiedDatesHelper;
import org.orcid.jaxb.model.common_rc3.Visibility;
import org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc3.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc3.Works;

public class ActivitiesSummaryManagerImpl implements ActivitiesSummaryManager {    
    @Resource
    private AffiliationsManager affiliationsManager;
    
    @Resource
    private ProfileFundingManager fundingManager;
    
    @Resource
    private PeerReviewManager peerReviewManager;
    
    @Resource
    private WorkManager workManager;
    
    @Resource
    private ProfileEntityManager profileEntityManager;

    @Override
    public ActivitiesSummary getActivitiesSummary(String orcid) {
        return getActivitiesSummary(orcid, false);
    }

    @Override
    public ActivitiesSummary getPublicActivitiesSummary(String orcid) {
        return getActivitiesSummary(orcid, true);
    }

    public ActivitiesSummary getActivitiesSummary(String orcid, boolean justPublic) {
        Date lastModified = profileEntityManager.getLastModified(orcid);
        long lastModifiedTime = lastModified.getTime();
        ActivitiesSummary activities = new ActivitiesSummary();

        // Set educations
        List<EducationSummary> educationsList = affiliationsManager.getEducationSummaryList(orcid, lastModifiedTime);
        if (!educationsList.isEmpty()) {
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
            Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(educations);
            activities.setEducations(educations);
        }

        // Set employments
        List<EmploymentSummary> employmentList = affiliationsManager.getEmploymentSummaryList(orcid, lastModifiedTime);
        if (!employmentList.isEmpty()) {
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
            Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(employments);
            activities.setEmployments(employments);
        }

        // Set fundings
        List<FundingSummary> fundingSummaries = fundingManager.getFundingSummaryList(orcid, lastModifiedTime);
        Fundings fundings = fundingManager.groupFundings(fundingSummaries, justPublic);
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(fundings);
        activities.setFundings(fundings);

        // Set peer reviews
        List<PeerReviewSummary> peerReviewSummaries = peerReviewManager.getPeerReviewSummaryList(orcid, lastModifiedTime);
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(peerReviewSummaries, justPublic);
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(peerReviews);
        activities.setPeerReviews(peerReviews);

        // Set works
        List<WorkSummary> workSummaries = workManager.getWorksSummaryList(orcid, lastModifiedTime);
        Works works = workManager.groupWorks(workSummaries, justPublic);
        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(works);
        activities.setWorks(works);

        Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(activities);
        return activities;
    }
}
