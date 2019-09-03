package org.orcid.listener.s3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Activity;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Record;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.release.record.summary.Memberships;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.Qualifications;
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
                record = fetchPublicRecordAndClearIfNeeded(message);
            } catch (Exception e) {
                //TODO: Mark all as failed since
            }

            if(record != null) {
                if (isSummaryIndexerEnabled) {
                    isSummaryOk = updateSummary(record);
                }

                if (isActivitiesIndexerEnabled) {

                }
            }
            
        }
    }
    
    public void retry(Record record, Boolean retrySummary, Map<ActivityType, Boolean> retryMap) {
        String orcid = record.getOrcidIdentifier().getPath();
        ActivitiesSummary as = record.getActivitiesSummary();
        Map<ActivityType, Map<String, S3ObjectSummary>> existingActivities = s3Manager.searchActivities(orcid, APIVersion.V3);
        //TODO: update each activity section on the DB
        if (retryMap.containsKey(ActivityType.DISTINCTIONS)) {
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

        if (retryMap.containsKey(ActivityType.INVITED_POSITIONS)) {
            processInvitedPositions(orcid, as.getInvitedPositions(), existingActivities.get(ActivityType.INVITED_POSITIONS));
        }

        if (retryMap.containsKey(ActivityType.MEMBERSHIP)) {
            processMemberships(orcid, as.getMemberships(), existingActivities.get(ActivityType.MEMBERSHIP));
        }

        if (retryMap.containsKey(ActivityType.PEER_REVIEWS)) {
            processPeerReviews(orcid, as.getPeerReviews(), existingActivities.get(ActivityType.PEER_REVIEWS));
        }

        if (retryMap.containsKey(ActivityType.QUALIFICATIONS)) {
            processQualifications(orcid, as.getQualifications(), existingActivities.get(ActivityType.QUALIFICATIONS));
        }

        if (retryMap.containsKey(ActivityType.RESEARCH_RESOURCES)) {
            processResearchResources(orcid, as.getResearchResources(), existingActivities.get(ActivityType.RESEARCH_RESOURCES));
        }

        if (retryMap.containsKey(ActivityType.SERVICES)) {
            processServices(orcid, as.getServices(), existingActivities.get(ActivityType.SERVICES));
        }

        if (retryMap.containsKey(ActivityType.WORKS)) {
            processWorks(orcid, as.getWorks(), existingActivities.get(ActivityType.WORKS));
        }
    }

    private boolean updateSummary(Record record) {
        if (record == null) {
            return false;
        }
        if(!isSummaryIndexerEnabled) {
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
    public void updateActivities(Record record, List<ActivityType> failedElements) {
        if (record == null) {
            return;
        }
        
        if (!isActivitiesIndexerEnabled) {
            return;
        }

        String orcid = record.getOrcidIdentifier().getPath();
        LOG.info("Processing activities for record " + orcid);   
        //TODO: populate the failedElements list
        if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == true) {
            if (record.getActivitiesSummary() != null) {
                ActivitiesSummary as = record.getActivitiesSummary();
                Map<ActivityType, Map<String, S3ObjectSummary>> existingActivities = s3Manager.searchActivities(orcid, APIVersion.V3);                
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
        } else if (record != null && record.getHistory() != null && record.getHistory().getClaimed() != null && record.getHistory().getClaimed() == false) {
            LOG.warn(record.getOrcidIdentifier().getPath() + " is unclaimed, so, his activities would not be indexed");
            activitiesStatusManager.markAllAsSent(orcid);
        }
    }

    /**
     * Affiliations
     * */
    private boolean processDistinctions(String orcid, Distinctions distinctions, Map<String, S3ObjectSummary> existingElements) {
        try {
            LOG.info("Processing Distinctions for record " + orcid);
            if (distinctions != null && !distinctions.retrieveGroups().isEmpty()) {                
                processAffiliations(orcid, distinctions.retrieveGroups(), existingElements, ActivityType.DISTINCTIONS);                                                        
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
                processAffiliations(orcid, educations.retrieveGroups(), existingElements, ActivityType.EDUCATIONS);                                                        
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
                processAffiliations(orcid, employments.retrieveGroups(), existingElements, ActivityType.EMPLOYMENTS);                                                        
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.EDUCATIONS);
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
                processAffiliations(orcid, invitedPositions.retrieveGroups(), existingElements, ActivityType.INVITED_POSITIONS);                                                        
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
                processAffiliations(orcid, memberships.retrieveGroups(), existingElements, ActivityType.MEMBERSHIP);                                                        
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
                processAffiliations(orcid, qualifications.retrieveGroups(), existingElements, ActivityType.QUALIFICATIONS);                                                        
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
                processAffiliations(orcid, services.retrieveGroups(), existingElements, ActivityType.SERVICES);                                                        
            } else {
                s3Manager.clearV3ActivitiesByType(orcid, ActivityType.SERVICES);
            }            
        } catch (Exception e) {
            LOG.info("Unable to process Services for record " + orcid, e);
            return false;
        }
        return true;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Fundings
     * */
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
    
    private void processAffiliations(String orcid, Collection<?> affGroups, Map<String, S3ObjectSummary> existingElements, ActivityType type) throws AmazonServiceException, AmazonClientException, JsonProcessingException, JAXBException {
        for(Object o : affGroups) {
            @SuppressWarnings("unchecked")
            AffiliationGroup<? extends AffiliationSummary> affGroup = (AffiliationGroup<? extends AffiliationSummary>) o;
            for(AffiliationSummary affSummary : affGroup.getActivities()) {
                processActivity(orcid, affSummary, existingElements, type);                
            }
        }
        
        // Remove from S3 all element that still exists on the
        // existingElements map
        for (String putCode : existingElements.keySet()) {
            s3Manager.removeV3Activity(orcid, putCode, type);
        } 
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
            LOG.error("Unable to fetch activities " + type.getValue() + " for orcid " + orcid);
        }
        return false;
    }

    private void processActivity(String orcid, Activity x, Map<String, S3ObjectSummary> existingElements, ActivityType type) throws AmazonClientException, AmazonServiceException , JsonProcessingException, JAXBException {
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
            s3Manager.uploadV3Activity(orcid, putCodeString, activity);
            // Remove it from the existingElements list means that the
            // elements was already processed
            existingElements.remove(putCodeString);
        }
    }
    
    private Activity fetchActivity(String orcid, Long putCode, ActivityType type) {
        switch (type) {
        case DISTINCTIONS:
            return orcid30ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.DISTINCTION);
        case EDUCATIONS:
            return orcid30ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.EDUCATION);
        case EMPLOYMENTS:
            return orcid30ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.EMPLOYMENT);
        case FUNDINGS:
            return orcid30ApiClient.fetchFunding(orcid, putCode);
        case INVITED_POSITIONS:
            return orcid30ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.INVITED_POSITION);
        case MEMBERSHIP:
            return orcid30ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.MEMBERSHIP);
        case PEER_REVIEWS:
            return orcid30ApiClient.fetchPeerReview(orcid, putCode);
        case QUALIFICATIONS:
            return orcid30ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.QUALIFICATION);
        case RESEARCH_RESOURCES:
            return orcid30ApiClient.fetchResearchResource(orcid, putCode);
        case SERVICES:
            return orcid30ApiClient.fetchAffiliation(orcid, putCode, AffiliationType.SERVICE);
        case WORKS:
            return orcid30ApiClient.fetchWork(orcid, putCode);
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
     * @return Record element of a valid user or null if the Record is locked or deprecated
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
            return null;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
