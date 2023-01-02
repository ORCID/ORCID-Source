package org.orcid.listener.s3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.utils.DateUtils;
import org.orcid.jaxb.model.error_v2.OrcidError;
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
import org.orcid.jaxb.model.record_v2.AffiliationType;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid20Manager;
import org.orcid.listener.persistence.managers.Api20RecordStatusManager;
import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.utils.listener.BaseMessage;
import org.orcid.utils.listener.RetryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Core logic for listeners
 * 
 * @author tom
 *
 */
@Component
public class S3MessageProcessorAPIV2 {

    public static final String VND_ORCID_XML = "application/vnd.orcid+xml";
    public static final String VND_ORCID_JSON = "application/vnd.orcid+json";

    Logger LOG = LoggerFactory.getLogger(S3MessageProcessorAPIV2.class);

    @Value("${org.orcid.message-listener.index.v3:false}")
    private boolean isV3IndexerEnabled;
    
    @Resource
    private Orcid20Manager orcid20ApiClient;
    @Resource
    private S3Manager s3Manager;
    @Resource
    private Api20RecordStatusManager api20RecordStatusManager;

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
                api20RecordStatusManager.allFailed(orcid);
            }

            if (record != null) {
                isSummaryOk = updateSummary(record);
                updateActivities(record, failedElements);
                api20RecordStatusManager.save(orcid, isSummaryOk, failedElements);
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
            api20RecordStatusManager.allFailed(orcid);
        }
        
        if(record == null) {
            return;
        }
        
        if(retrySummary) {
            if(!updateSummary(record)) {
                api20RecordStatusManager.setSummaryFail(orcid);
            }
        }
        
        ActivitiesSummary as = record.getActivitiesSummary();
        Map<ActivityType, Map<String, S3ObjectSummary>> existingActivities = s3Manager.searchActivities(orcid, APIVersion.V3);
        
        if (retryList.contains(ActivityType.EDUCATIONS)) {
            if(!processEducations(orcid, as.getEducations(), existingActivities.get(ActivityType.EDUCATIONS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.EDUCATIONS);
            }
        }

        if (retryList.contains(ActivityType.EMPLOYMENTS)) {
            if(!processEmployments(orcid, as.getEmployments(), existingActivities.get(ActivityType.EMPLOYMENTS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.EMPLOYMENTS);
            }
        }

        if (retryList.contains(ActivityType.FUNDINGS)) {
            if(!processFundings(orcid, as.getFundings(), existingActivities.get(ActivityType.FUNDINGS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.FUNDINGS);
            }
        }

        if (retryList.contains(ActivityType.PEER_REVIEWS)) {
            if(!processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.PEER_REVIEWS);
            }
        }

        if (retryList.contains(ActivityType.WORKS)) {
            if(!processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS))) {
                api20RecordStatusManager.setActivityFail(orcid, ActivityType.WORKS);
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
                    s3Manager.uploadV2RecordSummary(orcid, record);
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
                if (!processEducations(orcid, as.getEducations(), existingActivities.get(ActivityType.EDUCATIONS))) {
                    failedElements.add(ActivityType.EDUCATIONS);
                }
                if (!processEmployments(orcid, as.getEmployments(), existingActivities.get(ActivityType.EMPLOYMENTS))) {
                    failedElements.add(ActivityType.EMPLOYMENTS);
                }
                if (!processFundings(orcid, as.getFundings(), existingActivities.get(ActivityType.FUNDINGS))) {
                    failedElements.add(ActivityType.FUNDINGS);
                }
                if (!processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS))) {
                    failedElements.add(ActivityType.PEER_REVIEWS);
                }
                if (!processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS))) {
                    failedElements.add(ActivityType.WORKS);
                }
            }
        } else if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == false) {
            LOG.warn(record.getOrcidIdentifier().getPath() + " is unclaimed, so, his activities would not be indexed");
        }
    }   

    private boolean processEducations(String orcid, Educations educations, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Educations for record " + orcid);
            if (educations != null && !educations.getSummaries().isEmpty()) {
                List<EducationSummary> all = new ArrayList<EducationSummary>();
                educations.getSummaries().forEach(g -> {
                    all.add(g);
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
            if (employments != null && !employments.getSummaries().isEmpty()) {
                List<EmploymentSummary> all = new ArrayList<EmploymentSummary>();
                employments.getSummaries().forEach(g -> {
                    all.add(g);
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
                    for (PeerReviewSummary ps : g.getPeerReviewSummary()) {
                        peerReviews.add(ps);
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

    private void processActivity(String orcid, Activity x, Map<String, S3ObjectSummary> existingElements, ActivityType type)
            throws AmazonClientException, AmazonServiceException, JsonProcessingException, JAXBException {
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
            s3Manager.uploadV2Activity(orcid, putCodeString, activity);
            // Remove it from the existingElements list means that the
            // elements was already processed
            existingElements.remove(putCodeString);
        }
    }

    private Activity fetchActivity(String orcid, Long putCode, ActivityType type) {
        switch (type) {
        case EDUCATIONS:
            return orcid20ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.EDUCATION);
        case EMPLOYMENTS:
            return orcid20ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.EMPLOYMENT);
        case FUNDINGS:
            return orcid20ApiClient.fetchFunding(orcid, putCode);
        case PEER_REVIEWS:
            return orcid20ApiClient.fetchPeerReview(orcid, putCode);
        case WORKS:
            return orcid20ApiClient.fetchWork(orcid, putCode);
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
            return orcid20ApiClient.fetchPublicRecord(message);
        } catch (LockedRecordException | DeprecatedRecordException e) {
            // Remove all activities from this record
            s3Manager.clearV2Activities(orcid);
            // Remove the summary
            OrcidError error = null;
            if (e instanceof LockedRecordException) {
                LOG.error("Record " + orcid + " is locked");
                error = ((LockedRecordException) e).getOrcidError();
            } else {
                LOG.error("Record " + orcid + " is deprecated");
                error = ((DeprecatedRecordException) e).getOrcidError();
            }

            try {
                // Upload deprecated/locked record file
                s3Manager.uploadV2OrcidError(orcid, error);
            } catch (Exception e1) {
                LOG.error("Record " + orcid + " is locked or deprecated, however, S3 couldn't be updated", e1);
                throw new Exception(e1);
            }
            api20RecordStatusManager.save(orcid, true, new ArrayList<ActivityType>());
            return null;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
