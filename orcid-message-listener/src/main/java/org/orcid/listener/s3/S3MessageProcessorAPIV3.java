package org.orcid.listener.s3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Activity;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
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
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid30Manager;
import org.orcid.listener.persistence.managers.Api30RecordStatusManager;
import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.utils.DateUtils;
import org.orcid.utils.listener.BaseMessage;
import org.orcid.utils.listener.RetryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * Core logic for listeners
 * 
 * @author tom
 *
 */
@Component
public class S3MessageProcessorAPIV3 {

    public static final String VND_ORCID_XML = "application/vnd.orcid+xml";
    public static final String VND_ORCID_JSON = "application/vnd.orcid+json";

    Logger LOG = LoggerFactory.getLogger(S3MessageProcessorAPIV3.class);

    @Value("${org.orcid.message-listener.index.v3:false}")
    private boolean isV3IndexerEnabled;
    
    @Resource
    private Orcid30Manager orcid30ApiClient;
    @Resource
    private S3Manager s3Manager;
    @Resource
    private Api30RecordStatusManager api30RecordStatusManager;

    public void update(BaseMessage message) {
        String orcid = message.getOrcid();
        Boolean isSummaryOk = false;
        List<ActivityType> failedElements = new ArrayList<ActivityType>();
        if (isV3IndexerEnabled) {
            Record record = null;
            try {
                record = fetchPublicRecordAndClearIfNeeded(message);
            } catch (Exception e) {
                LOG.error("Unable to fetch public record for " + orcid, e);
                api30RecordStatusManager.allFailed(orcid);
            }

            if (record != null) {
                isSummaryOk = updateSummary(record);
                updateActivities(record, failedElements);
                api30RecordStatusManager.save(orcid, isSummaryOk, failedElements);
            }
        }
    }

    public void retry(RetryMessage message, Boolean retrySummary, List<ActivityType> retryList) {
        if(!isV3IndexerEnabled) {
            return;
        }
        String orcid = message.getOrcid();
        
        Record record = null;
        try {
            record = fetchPublicRecordAndClearIfNeeded(message);
        } catch (Exception e) {
            LOG.error("Unable to fetch public record for " + orcid, e);
            api30RecordStatusManager.allFailed(orcid);
        }
        
        if(record == null) {
            return;
        }
        
        if(retrySummary) {
            if(!updateSummary(record)) {
                api30RecordStatusManager.setSummaryFail(orcid);
            }
        }
        
        ActivitiesSummary as = record.getActivitiesSummary();
        Map<ActivityType, Map<String, S3ObjectSummary>> existingActivities = s3Manager.searchActivities(orcid, APIVersion.V3);
        if (retryList.contains(ActivityType.DISTINCTIONS)) {
            if(!processDistinctions(orcid, as.getDistinctions(), existingActivities.get(ActivityType.DISTINCTIONS))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.DISTINCTIONS);
            }
        }

        if (retryList.contains(ActivityType.EDUCATIONS)) {
            if(!processEducations(orcid, as.getEducations(), existingActivities.get(ActivityType.EDUCATIONS))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.EDUCATIONS);
            }
        }

        if (retryList.contains(ActivityType.EMPLOYMENTS)) {
            if(!processEmployments(orcid, as.getEmployments(), existingActivities.get(ActivityType.EMPLOYMENTS))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.EMPLOYMENTS);
            }
        }

        if (retryList.contains(ActivityType.FUNDINGS)) {
            if(!processFundings(orcid, as.getFundings(), existingActivities.get(ActivityType.FUNDINGS))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.FUNDINGS);
            }
        }

        if (retryList.contains(ActivityType.INVITED_POSITIONS)) {
            if(!processInvitedPositions(orcid, as.getInvitedPositions(), existingActivities.get(ActivityType.INVITED_POSITIONS))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.INVITED_POSITIONS);
            }
        }

        if (retryList.contains(ActivityType.MEMBERSHIP)) {
            if(!processMemberships(orcid, as.getMemberships(), existingActivities.get(ActivityType.MEMBERSHIP))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.MEMBERSHIP);
            }
        }

        if (retryList.contains(ActivityType.PEER_REVIEWS)) {
            if(!processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.PEER_REVIEWS);
            }
        }

        if (retryList.contains(ActivityType.QUALIFICATIONS)) {
            if(!processQualifications(orcid, as.getQualifications(), existingActivities.get(ActivityType.QUALIFICATIONS))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.QUALIFICATIONS);
            }
        }

        if (retryList.contains(ActivityType.RESEARCH_RESOURCES)) {
            if(!processResearchResources(orcid, as.getResearchResources(), existingActivities.get(ActivityType.RESEARCH_RESOURCES))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.RESEARCH_RESOURCES);
            }
        }

        if (retryList.contains(ActivityType.SERVICES)) {
            if(!processServices(orcid, as.getServices(), existingActivities.get(ActivityType.SERVICES))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.SERVICES);
            }
        }

        if (retryList.contains(ActivityType.WORKS)) {
            if(!processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS))) {
                api30RecordStatusManager.setActivityFail(orcid, ActivityType.WORKS);
            }
        }
    }

    private boolean updateSummary(Record record) {
        if (record == null || !isV3IndexerEnabled) {
            return false;
        }
        String orcid = record.getOrcidIdentifier().getPath();
        LOG.info("Processing summary for record " + orcid);
        try {
            // Index only if it is claimed
            if (record.getHistory() != null && record.getHistory().getClaimed() != null) {
                if (record.getHistory().getClaimed() == true) {
                    s3Manager.uploadV3RecordSummary(orcid, record);
                } else {
                    LOG.warn(orcid + " is unclaimed, so, it will not be indexed");
                }
            }
            return true;
        } catch (AmazonClientException e) {
            LOG.error("Unable to fetch record " + orcid + " for 2.0 API: " + e.getMessage(), e);
        } catch (Exception e) {
            // Something else went wrong fetching record from ORCID
            LOG.error("Unable to fetch record " + orcid + " for 2.0 API: " + e.getMessage(), e);
        }

        // Return false as the Record or OrcidError couldn't be feed
        return false;
    }

    /**
     * 
     * Activities indexing
     * 
     */
    private void updateActivities(Record record, List<ActivityType> failedElements) {
        if (record == null || !isV3IndexerEnabled) {
            return;
        }

        String orcid = record.getOrcidIdentifier().getPath();
        LOG.info("Processing activities for record " + orcid);
        if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == true) {
            if (record.getActivitiesSummary() != null) {
                ActivitiesSummary as = record.getActivitiesSummary();
                Map<ActivityType, Map<String, S3ObjectSummary>> existingActivities = s3Manager.searchActivities(orcid, APIVersion.V3);
                if (!processDistinctions(orcid, as.getDistinctions(), existingActivities.get(ActivityType.DISTINCTIONS))) {
                    failedElements.add(ActivityType.DISTINCTIONS);
                }
                if (!processEducations(orcid, as.getEducations(), existingActivities.get(ActivityType.EDUCATIONS))) {
                    failedElements.add(ActivityType.EDUCATIONS);
                }
                if (!processEmployments(orcid, as.getEmployments(), existingActivities.get(ActivityType.EMPLOYMENTS))) {
                    failedElements.add(ActivityType.EMPLOYMENTS);
                }
                if (!processFundings(orcid, as.getFundings(), existingActivities.get(ActivityType.FUNDINGS))) {
                    failedElements.add(ActivityType.FUNDINGS);
                }
                if (!processInvitedPositions(orcid, as.getInvitedPositions(), existingActivities.get(ActivityType.INVITED_POSITIONS))) {
                    failedElements.add(ActivityType.INVITED_POSITIONS);
                }
                if (!processMemberships(orcid, as.getMemberships(), existingActivities.get(ActivityType.MEMBERSHIP))) {
                    failedElements.add(ActivityType.MEMBERSHIP);
                }
                if (!processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS))) {
                    failedElements.add(ActivityType.PEER_REVIEWS);
                }
                if (!processQualifications(orcid, as.getQualifications(), existingActivities.get(ActivityType.QUALIFICATIONS))) {
                    failedElements.add(ActivityType.QUALIFICATIONS);
                }
                if (!processResearchResources(orcid, as.getResearchResources(), existingActivities.get(ActivityType.RESEARCH_RESOURCES))) {
                    failedElements.add(ActivityType.RESEARCH_RESOURCES);
                }
                if (!processServices(orcid, as.getServices(), existingActivities.get(ActivityType.SERVICES))) {
                    failedElements.add(ActivityType.SERVICES);
                }
                if (!processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS))) {
                    failedElements.add(ActivityType.WORKS);
                }
            }
        } else if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == false) {
            LOG.warn(record.getOrcidIdentifier().getPath() + " is unclaimed, so, his activities would not be indexed");
        }
    }

    private boolean processDistinctions(String orcid, Distinctions distinctions, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Distinctions for record " + orcid);
            if (distinctions != null && !distinctions.retrieveGroups().isEmpty()) {
                List<DistinctionSummary> all = new ArrayList<DistinctionSummary>();
                distinctions.retrieveGroups().forEach(g -> {
                    all.addAll(g.getActivities());
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.DISTINCTIONS)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.DISTINCTIONS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Distinctions for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processEducations(String orcid, Educations educations, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Educations for record " + orcid);
            if (educations != null && !educations.retrieveGroups().isEmpty()) {
                List<EducationSummary> all = new ArrayList<EducationSummary>();
                educations.retrieveGroups().forEach(g -> {
                    all.addAll(g.getActivities());
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.EDUCATIONS)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.EDUCATIONS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Educations for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processEmployments(String orcid, Employments employments, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Employments for record " + orcid);
            if (employments != null && !employments.retrieveGroups().isEmpty()) {
                List<EmploymentSummary> all = new ArrayList<EmploymentSummary>();
                employments.retrieveGroups().forEach(g -> {
                    all.addAll(g.getActivities());
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.EMPLOYMENTS)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.EMPLOYMENTS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Employments for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processInvitedPositions(String orcid, InvitedPositions invitedPositions, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Invited Positions for record " + orcid);
            if (invitedPositions != null && !invitedPositions.retrieveGroups().isEmpty()) {
                List<InvitedPositionSummary> all = new ArrayList<InvitedPositionSummary>();
                invitedPositions.retrieveGroups().forEach(g -> {
                    all.addAll(g.getActivities());
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.INVITED_POSITIONS)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.INVITED_POSITIONS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Invited Positions for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processMemberships(String orcid, Memberships memberships, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Memberships for record " + orcid);
            if (memberships != null && !memberships.retrieveGroups().isEmpty()) {
                List<MembershipSummary> all = new ArrayList<MembershipSummary>();
                memberships.retrieveGroups().forEach(g -> {
                    all.addAll(g.getActivities());
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.MEMBERSHIP)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.MEMBERSHIP);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Memberships for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processQualifications(String orcid, Qualifications qualifications, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Qualifications for record " + orcid);
            if (qualifications != null && !qualifications.retrieveGroups().isEmpty()) {
                List<QualificationSummary> all = new ArrayList<QualificationSummary>();
                qualifications.retrieveGroups().forEach(g -> {
                    all.addAll(g.getActivities());
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.QUALIFICATIONS)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.QUALIFICATIONS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Qualifications for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processServices(String orcid, Services services, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Services for record " + orcid);
            if (services != null && !services.retrieveGroups().isEmpty()) {
                List<ServiceSummary> all = new ArrayList<ServiceSummary>();
                services.retrieveGroups().forEach(g -> {
                    all.addAll(g.getActivities());
                });
                if(!processActivities(orcid, all, existingElements, ActivityType.SERVICES)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.SERVICES);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Services for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processFundings(String orcid, Fundings fundingsElement, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Fundings for record " + orcid);
            if (fundingsElement != null && !fundingsElement.getFundingGroup().isEmpty()) {
                List<FundingSummary> fundings = new ArrayList<FundingSummary>();
                for (FundingGroup g : fundingsElement.getFundingGroup()) {
                    fundings.addAll(g.getFundingSummary());
                }
                if(!processActivities(orcid, fundings, existingElements, ActivityType.FUNDINGS)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.FUNDINGS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Fundings for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processPeerReviews(String orcid, PeerReviews peerReviewsElement, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing PeerReviews for record " + orcid);
            if (peerReviewsElement != null && !peerReviewsElement.getPeerReviewGroup().isEmpty()) {
                List<PeerReviewSummary> peerReviews = new ArrayList<PeerReviewSummary>();
                for (PeerReviewGroup g : peerReviewsElement.getPeerReviewGroup()) {
                    for (PeerReviewDuplicateGroup dg : g.getPeerReviewGroup()) {
                        peerReviews.addAll(dg.getPeerReviewSummary());
                    }
                }
                if(!processActivities(orcid, peerReviews, existingElements, ActivityType.PEER_REVIEWS)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.PEER_REVIEWS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Peer Reviews for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processResearchResources(String orcid, ResearchResources researchResourcesElement, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Research Resources for record " + orcid);
            if (researchResourcesElement != null && !researchResourcesElement.getResearchResourceGroup().isEmpty()) {
                List<ResearchResourceSummary> researchResources = new ArrayList<ResearchResourceSummary>();
                for (ResearchResourceGroup g : researchResourcesElement.getResearchResourceGroup()) {
                    researchResources.addAll(g.getResearchResourceSummary());
                }
                if(!processActivities(orcid, researchResources, existingElements, ActivityType.RESEARCH_RESOURCES)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.RESEARCH_RESOURCES);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Research Resources for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processWorks(String orcid, Works worksElement, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Works for record " + orcid);
            if (worksElement != null && !worksElement.getWorkGroup().isEmpty()) {
                List<WorkSummary> works = new ArrayList<WorkSummary>();
                for (WorkGroup g : worksElement.getWorkGroup()) {
                    works.addAll(g.getWorkSummary());
                }
                if(!processActivities(orcid, works, existingElements, ActivityType.WORKS)) {
                    return false;
                }
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.WORKS);
            }
        } catch (Exception e) {
            LOG.info("Unable to process Works for record " + orcid, e);
            return false;
        }
        return true;
    }

    private boolean processActivities(String orcid, List<? extends Activity> activities, Map<String, S3ObjectSummary> existingElements, ActivityType type) {
        try {
            for (Activity x : activities) {
                processActivity(orcid, x, existingElements, type);
            }
            // Remove from S3 all element that still exists on the
            // existingEducations map
            for (String putCode : existingElements.keySet()) {
                s3Manager.removeV3Activity(orcid, putCode, type);
            }

            return true;
        } catch (Exception e) {
            LOG.error("Unable to fetch activities " + type.getValue() + " for orcid " + orcid, e);
        }
        return false;
    }

    private void processActivity(String orcid, Activity activityFromSummary, Map<String, S3ObjectSummary> existingElements, ActivityType type)
            throws AmazonClientException, AmazonServiceException, JAXBException, IOException, InterruptedException {
        Long summaryPutCode = activityFromSummary.getPutCode();
        String summaryPutCodeString = String.valueOf(summaryPutCode);
        Date summaryLastModified = DateUtils.convertToDate(activityFromSummary.getLastModifiedDate().getValue());
        byte [] activity = null;
        if (existingElements.containsKey(summaryPutCodeString)) {
            S3ObjectSummary existingObject = existingElements.get(summaryPutCodeString);            
            Date s3LastModified = existingObject.getLastModified();
            if (summaryLastModified.after(s3LastModified)) {
                activity = fetchActivity(orcid, summaryPutCode, type);
            }
            // Remove it from the existingElements list since it was
            // already processed
            existingElements.remove(summaryPutCodeString);
        } else {
            activity = fetchActivity(orcid, summaryPutCode, type);
        }

        if (activity != null) {
            // Upload it to S3
            s3Manager.uploadV3Activity(orcid, summaryPutCodeString, type, summaryLastModified, activity);
            // Remove it from the existingElements list means that the
            // elements was already processed
            existingElements.remove(summaryPutCodeString);
        }
    }

    private byte[] fetchActivity(String orcid, Long putCode, ActivityType type) throws IOException, InterruptedException {
        switch (type) {
        case DISTINCTIONS:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "distinction");
        case EDUCATIONS:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "education");
        case EMPLOYMENTS:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "employment");
        case FUNDINGS:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "funding");
        case INVITED_POSITIONS:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "invited-position");
        case MEMBERSHIP:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "membership");
        case PEER_REVIEWS:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "peer-review");
        case QUALIFICATIONS:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "qualification");
        case RESEARCH_RESOURCES:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "research-resource");
        case SERVICES:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "service");
        case WORKS:
            return orcid30ApiClient.fetchActivity(orcid, putCode, "work");
        default:
            throw new IllegalArgumentException("Invalid type! Imposible: " + type);
        }
    }

    /**
     * Fetch the public record of the given orcid id, however, if that record is
     * locked or deprecated, it will clear the summary and all its related
     * activities
     * 
     * @param message
     * @return Record element of a valid user or null if the Record is locked or
     *         deprecated
     * @throws Exception
     *             when the record is locked or deprecated but its data couldn't
     *             be cleared in S3 or, when the record ca't be fetched from the
     *             ORCID API
     */
    private Record fetchPublicRecordAndClearIfNeeded(BaseMessage message) throws AmazonClientException, AmazonServiceException, Exception {
        String orcid = message.getOrcid();
        try {
            return orcid30ApiClient.fetchPublicRecord(message);
        } catch (LockedRecordException | DeprecatedRecordException e) {
            // Remove all activities from this record
            s3Manager.clearV3Activities(orcid);
            // Remove the summary
            OrcidError error = null;
            if (e instanceof LockedRecordException) {
                LOG.error("Record " + orcid + " is locked");
                error = ((LockedRecordException) e).getV3OrcidError();
            } else {
                LOG.error("Record " + orcid + " is deprecated");
                error = ((DeprecatedRecordException) e).getV3OrcidError();
            }

            try {
                // Upload deprecated/locked record file
                s3Manager.uploadV3OrcidError(orcid, error);
            } catch (Exception e1) {
                LOG.error("Record " + orcid + " is locked or deprecated, however, S3 couldn't be updated", e1);
                throw new Exception(e1);
            }
            api30RecordStatusManager.save(orcid, true, new ArrayList<ActivityType>());
            return null;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
