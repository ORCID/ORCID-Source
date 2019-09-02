package org.orcid.listener.s3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.jaxb.model.v3.release.record.Activity;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid30Manager;
import org.orcid.listener.persistence.managers.ActivitiesStatusManager;
import org.orcid.listener.persistence.managers.Api30RecordStatusManager;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.listener.persistence.util.AvailableBroker;
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

    @Value("${org.orcid.message-listener.index.v3.summaries:true}")
    private boolean isSummaryIndexerEnabled;

    @Value("${org.orcid.message-listener.index.v3.activities:true}")
    private boolean isActivitiesIndexerEnabled;

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
        if (isSummaryIndexerEnabled || isActivitiesIndexerEnabled) {
            Record record = null;
            try {
                record = orcid30ApiClient.fetchPublicRecord(message);
            } catch (LockedRecordException | DeprecatedRecordException e) {
                try {
                    OrcidError error = null;
                    if (e instanceof LockedRecordException) {
                        LOG.error("Record " + orcid + " is locked");
                        error = ((LockedRecordException) e).getV3OrcidError();
                    } else {
                        LOG.error("Record " + orcid + " is deprecated");
                        error = ((DeprecatedRecordException) e).getV3OrcidError();
                    }

                    // Upload deprecated/locked record file
                    s3Manager.uploadV3OrcidError(orcid, error);
                    // TODO:
                    // Clean up all activities
                    // Return
                    return;
                } catch (Exception e1) {
                    LOG.error("Record " + orcid + " is locked or deprecated, however, S3 couldn't be updated", e1);
                    // TODO:
                    // Mark them all as failed
                    throw new RuntimeException(e1);
                }
            } catch (Exception e) {
                // something else went wrong fetching record from ORCID and
                // threw a
                // runtime exception
                LOG.error("Unable to fetch record " + orcid + " for 3.0 API: " + e.getMessage(), e);
                // TODO: Mark them all as failed
                throw new RuntimeException(e);
            }
            
            if(isSummaryIndexerEnabled) {
                isSummaryOk = updateSummary(record);
            }
            
            if(isActivitiesIndexerEnabled) {
                
            }
            
        }
    }

    private boolean updateSummary(Record record) {
        if(record == null) {
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
            // something else went wrong fetching record from ORCID and
            // threw a
            // runtime exception
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
    public void update20Activities(BaseMessage message, List<ActivityType> failedElements) {
        if (!isActivitiesIndexerEnabled) {
            return;
        }

        String orcid = message.getOrcid();
        LOG.info("Processing activities for record " + orcid);
        Record record = fetchPublicRecord(message);
        if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == true) {
            if (record.getActivitiesSummary() != null) {
                ActivitiesSummary as = record.getActivitiesSummary();
                Map<ActivityType, Map<String, S3ObjectSummary>> existingActivities = s3Manager.searchActivities(orcid, APIVersion.V3);
                if (RetryMessage.class.isAssignableFrom(message.getClass())) {
                    RetryMessage rm = (RetryMessage) message;
                    @SuppressWarnings("unchecked")
                    Map<ActivityType, Boolean> retryMap = (Map<ActivityType, Boolean>) rm.getRetryTypes();
                    if(retryMap.containsKey(ActivityType.DISTINCTIONS)) {
                        processDistinctions(orcid, as.getDistinctions(), existingActivities.get(ActivityType.DISTINCTIONS));
                    }
                    
                    if (retryMap.containsKey(ActivityType.EDUCATIONS)) {
                        processEducations(orcid, as.getEducations(), existingActivities.get(ActivityType.EDUCATIONS));
                    }

                    if (retryMap.containsKey(ActivityType.EMPLOYMENTS)) {
                        processEmployments(orcid, as.getEmployments(), existingActivities.get(ActivityType.EMPLOYMENTS));
                    }

                    if (retryMap.containsKey(ActivityType.FUNDINGS)) {
                        processFundings(orcid, as.getFundings(), existingActivities.get(ActivityType.FUNDINGS));
                    }
                    
                    if(retryMap.containsKey(ActivityType.INVITED_POSITIONS)) {
                        processInvitedPositions(orcid, as.getInvitedPositions(), existingActivities.get(ActivityType.INVITED_POSITIONS));
                    }
                    
                    if(retryMap.containsKey(ActivityType.MEMBERSHIP)) {
                        processMemberships(orcid, as.getMemberships(), existingActivities.get(ActivityType.MEMBERSHIP));
                    }
                    
                    if (retryMap.containsKey(ActivityType.PEER_REVIEWS)) {
                        processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS));
                    }

                    if(retryMap.containsKey(ActivityType.QUALIFICATIONS)) {
                        processQualifications(orcid, as.getQualifications(), existingActivities.get(ActivityType.QUALIFICATIONS));
                    }
                    
                    if(retryMap.containsKey(ActivityType.RESEARCH_RESOURCES)) {
                        processResearchResources(orcid, as.getResearchResources(), existingActivities.get(ActivityType.RESEARCH_RESOURCES));
                    }
                    
                    if(retryMap.containsKey(ActivityType.SERVICES)) {
                        processServices(orcid, as.getServices(), existingActivities.get(ActivityType.SERVICES));
                    }
                    
                    if (retryMap.containsKey(ActivityType.WORKS)) {
                        processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS));
                    }
                } else {
                    processDistinctions(orcid, as.getDistinctions(), existingActivities.get(ActivityType.DISTINCTIONS));
                    processEducations(orcid, as.getEducations(), existingActivities.get(ActivityType.EDUCATIONS));
                    processEmployments(orcid, as.getEmployments(), existingActivities.get(ActivityType.EMPLOYMENTS));
                    processFundings(orcid, as.getFundings(), existingActivities.get(ActivityType.FUNDINGS));
                    processInvitedPositions(orcid, as.getInvitedPositions(), existingActivities.get(ActivityType.INVITED_POSITIONS));
                    processMemberships(orcid, as.getMemberships(), existingActivities.get(ActivityType.MEMBERSHIP));
                    processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS));
                    processQualifications(orcid, as.getQualifications(), existingActivities.get(ActivityType.QUALIFICATIONS));
                    processResearchResources(orcid, as.getResearchResources(), existingActivities.get(ActivityType.RESEARCH_RESOURCES));
                    processServices(orcid, as.getServices(), existingActivities.get(ActivityType.SERVICES));
                    processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS));
                }
            }
        } else if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == false) {
            LOG.warn(record.getOrcidIdentifier().getPath() + " is unclaimed, so, his activities would not be indexed");
            activitiesStatusManager.markAllAsSent(orcid);
        }
    }

    private boolean processEducations(String orcid, Educations educations, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Educations for record " + orcid);
            if (educations != null && !educations.getEducationGroups().isEmpty()) {
                processActivities(orcid, educations.getEducationGroups(), existingElements, ActivityType.EDUCATIONS);
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.EDUCATIONS);
            }
            return true;
        } catch (AmazonClientException e) {
            LOG.info("Unable to process Educations for record " + orcid, e);           
        }
        return false;
    }

    private void processEmployments(String orcid, Employments employments, Map<String, S3ObjectSummary> existingElements) {
        LOG.info("Processing Employments for record " + orcid);
        if (employments != null && !employments.getSummaries().isEmpty()) {
            processActivities(orcid, employments.getSummaries(), existingElements, ActivityType.EMPLOYMENTS);
        } else {
            s3Manager.clearActivitiesByType(orcid, ActivityType.EMPLOYMENTS);
            activitiesStatusManager.markAsSent(orcid, ActivityType.EMPLOYMENTS);
        }
    }

    private void processFundings(String orcid, Fundings fundingsElement, Map<String, S3ObjectSummary> existingElements) {
        LOG.info("Processing Fundings for record " + orcid);
        if (fundingsElement != null && !fundingsElement.getFundingGroup().isEmpty()) {
            List<FundingSummary> fundings = new ArrayList<FundingSummary>();
            for (FundingGroup g : fundingsElement.getFundingGroup()) {
                fundings.addAll(g.getFundingSummary());
            }
            processActivities(orcid, fundings, existingElements, ActivityType.FUNDINGS);
        } else {
            s3Manager.clearActivitiesByType(orcid, ActivityType.FUNDINGS);
            activitiesStatusManager.markAsSent(orcid, ActivityType.FUNDINGS);
        }
    }

    private void processPeerReviews(String orcid, PeerReviews peerReviewsElement, Map<String, S3ObjectSummary> existingElements) {
        LOG.info("Processing PeerReviews for record " + orcid);
        if (peerReviewsElement != null && !peerReviewsElement.getPeerReviewGroup().isEmpty()) {
            List<PeerReviewSummary> peerReviews = new ArrayList<PeerReviewSummary>();
            for (PeerReviewGroup g : peerReviewsElement.getPeerReviewGroup()) {
                peerReviews.addAll(g.getPeerReviewSummary());
            }
            processActivities(orcid, peerReviews, existingElements, ActivityType.PEER_REVIEWS);
        } else {
            s3Manager.clearActivitiesByType(orcid, ActivityType.PEER_REVIEWS);
            activitiesStatusManager.markAsSent(orcid, ActivityType.PEER_REVIEWS);
        }
    }

    private void processWorks(String orcid, Works worksElement, Map<String, S3ObjectSummary> existingElements) {
        LOG.info("Processing Works for record " + orcid);
        if (worksElement != null && !worksElement.getWorkGroup().isEmpty()) {
            List<WorkSummary> works = new ArrayList<WorkSummary>();
            for (WorkGroup g : worksElement.getWorkGroup()) {
                works.addAll(g.getWorkSummary());
            }
            processActivities(orcid, works, existingElements, ActivityType.WORKS);
        } else {
            s3Manager.clearActivitiesByType(orcid, ActivityType.WORKS);
            activitiesStatusManager.markAsSent(orcid, ActivityType.WORKS);
        }
    }

    private boolean processActivities(String orcid, List<? extends Activity> activities, Map<String, S3ObjectSummary> existingElements, ActivityType type) {
        try {
            for (Activity x : activities) {
                String putCodeString = String.valueOf(x.getPutCode());
                Activity activity = null;
                if (existingElements.containsKey(putCodeString)) {
                    S3ObjectSummary existingObject = existingElements.get(putCodeString);
                    Date elementLastModified = DateUtils.convertToDate(x.getLastModifiedDate().getValue());
                    Date s3LastModified = existingObject.getLastModified();
                    if (elementLastModified.after(s3LastModified)) {
                        activity = fetchActivity(orcid, x.getPutCode(), type);
                    }
                    // Remove it from the existingElements list since it was
                    // already processed
                    existingElements.remove(putCodeString);
                } else {
                    activity = fetchActivity(orcid, x.getPutCode(), type);
                }

                if (activity != null) {
                    // Upload it to S3
                    s3Manager.uploadActivity(orcid, putCodeString, activity);
                    // Remove it from the existingElements list means that the
                    // elements was already processed
                    existingElements.remove(putCodeString);
                }
            }
            // Remove from S3 all element that still exists on the
            // existingEducations map
            for (String putCode : existingElements.keySet()) {
                s3Manager.removeActivity(orcid, putCode, type);
            }

            return true;
        } catch (Exception e) {
            LOG.error("Unable to fetch activities " + type.getValue() + " for orcid " + orcid);            
        }
        return false;
    }

    private Activity fetchActivity(String orcid, Long putCode, ActivityType type) {
        switch (type) {
        case DISTINCTIONS:
        case EDUCATIONS:
            return orcid20ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.EDUCATION);
        case EMPLOYMENTS:
            return orcid20ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.EMPLOYMENT);        
        case FUNDINGS:
            return orcid20ApiClient.fetchFunding(orcid, putCode);
        case INVITED_POSITIONS:
        case MEMBERSHIP:
        case PEER_REVIEWS:
            return orcid20ApiClient.fetchPeerReview(orcid, putCode);
        case QUALIFICATIONS:
        case RESEARCH_RESOURCES:
        case SERVICES:
        case WORKS:
            return orcid20ApiClient.fetchWork(orcid, putCode);
        default:
            throw new IllegalArgumentException("Invalid type! Imposible: " + type);
        }
    }

    private Record fetchPublicRecord(BaseMessage message) {
        String orcid = message.getOrcid();
        try {
            return orcid20ApiClient.fetchPublicRecord(message);
        } catch (LockedRecordException | DeprecatedRecordException e) {
            // Remove all activities from this record
            s3Manager.clearActivities(orcid);
            // Mark all activities as ok
            activitiesStatusManager.markAllAsSent(orcid);
        } catch (Exception e) {
            // Mark all activities as failed
            activitiesStatusManager.markAllAsFailed(orcid);
            throw new RuntimeException(e);
        }

        return null;
    }
}
