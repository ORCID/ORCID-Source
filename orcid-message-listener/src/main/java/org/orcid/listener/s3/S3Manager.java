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
package org.orcid.listener.s3;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.utils.DateUtils;
import org.orcid.utils.jersey.marshaller.ORCIDMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class S3Manager {

    Logger LOG = LoggerFactory.getLogger(S3Manager.class);

    @Resource
    private S3MessagingService s3MessagingService;

    @Value("${org.orcid.message-listener.index.s3.search.max_elements:3000}")
    private Integer maxElements;
    
    @Resource
    private ORCIDMarshaller marshaller; 
   
    public void setS3MessagingService(S3MessagingService s3MessagingService) {
        this.s3MessagingService = s3MessagingService;
    }

    public void setMarshaller(ORCIDMarshaller m) {
        this.marshaller = m;
    }
    
    public void uploadV2RecordSummary(String orcid, org.orcid.jaxb.model.record_v2.Record record) throws JAXBException, JsonProcessingException, AmazonClientException, AmazonServiceException {
        Date lastModified = DateUtils.convertToDate(record.getHistory().getLastModifiedDate().getValue());
        // Upload XML
        String xmlElementName = getElementName(orcid);
        byte[] xmlElement = marshaller.toXML(record);
        s3MessagingService.sendV2Item(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, false);
    }

    public void uploadV3RecordSummary(String orcid, org.orcid.jaxb.model.v3.release.record.Record record) throws JAXBException, JsonProcessingException,AmazonClientException, AmazonServiceException {
        Date lastModified = DateUtils.convertToDate(record.getHistory().getLastModifiedDate().getValue());
        // Upload XML
        String xmlElementName = getElementName(orcid);
        byte[] xmlElement = marshaller.toXML(record);
        s3MessagingService.sendV3Item(orcid, xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, false);
    }

    public void uploadV2Activity(String orcid, String putCode, ActivityType activityType, Date lastModified, byte [] activity) throws JAXBException, JsonProcessingException,AmazonClientException, AmazonServiceException {
        // Upload XML
        String xmlElementName = getElementName(orcid, putCode, activityType);
        s3MessagingService.sendV2Item(xmlElementName, activity, MediaType.APPLICATION_XML, lastModified, true);
    }

    public void uploadV3Activity(String orcid, String putCode, ActivityType activityType, Date lastModified, byte [] activity) throws JAXBException, JsonProcessingException, AmazonClientException, AmazonServiceException {
        // Upload XML
        String xmlElementName = getElementName(orcid, putCode, activityType);
        s3MessagingService.sendV3Item(orcid, xmlElementName, activity, MediaType.APPLICATION_XML, lastModified, true);
    }

    public void uploadV2OrcidError(String orcid, org.orcid.jaxb.model.error_v2.OrcidError error) throws JAXBException, JsonProcessingException, AmazonClientException, AmazonServiceException {
        Date lastModified = new Date();

        // Upload XML
        String xmlElementName = getElementName(orcid);
        byte[] xmlElement = marshaller.toXML(error);
        s3MessagingService.sendV2Item(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, false);
    }

    public void uploadV3OrcidError(String orcid, org.orcid.jaxb.model.v3.release.error.OrcidError error) throws JAXBException, JsonProcessingException, AmazonClientException, AmazonServiceException {
        Date lastModified = new Date();

        // Upload XML
        String xmlElementName = getElementName(orcid);
        byte[] xmlElement = marshaller.toXML(error);
        s3MessagingService.sendV3Item(orcid, xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, false);
    }

    public Map<ActivityType, Map<String, S3ObjectSummary>> searchActivities(String orcid, APIVersion version) {
        Map<ActivityType, Map<String, S3ObjectSummary>> activitiesOnS3 = new HashMap<ActivityType, Map<String, S3ObjectSummary>>();

        Map<String, S3ObjectSummary> distinctions = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> educations = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> employments = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> fundings = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> invitedPositions = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> memberships = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> peerReviews = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> qualifications = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> researchResources = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> services = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> works = new HashMap<String, S3ObjectSummary>();

        activitiesOnS3.put(ActivityType.DISTINCTIONS, distinctions);
        activitiesOnS3.put(ActivityType.EDUCATIONS, educations);
        activitiesOnS3.put(ActivityType.EMPLOYMENTS, employments);
        activitiesOnS3.put(ActivityType.FUNDINGS, fundings);
        activitiesOnS3.put(ActivityType.INVITED_POSITIONS, invitedPositions);
        activitiesOnS3.put(ActivityType.MEMBERSHIP, memberships);
        activitiesOnS3.put(ActivityType.PEER_REVIEWS, peerReviews);
        activitiesOnS3.put(ActivityType.QUALIFICATIONS, qualifications);
        activitiesOnS3.put(ActivityType.RESEARCH_RESOURCES, researchResources);
        activitiesOnS3.put(ActivityType.SERVICES, services);
        activitiesOnS3.put(ActivityType.WORKS, works);

        String prefix = buildPrefix(orcid);
        String bucketName = (APIVersion.V2.equals(version) ? s3MessagingService.getV2ActivitiesBucketName() : s3MessagingService.getV3ActivitiesBucketName(orcid));
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(prefix).withMaxKeys(maxElements);
        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String activityPath = objectSummary.getKey();
                String putCode = getActivityPutCode(activityPath);
                // To improve performance, sort these if/else based on
                // https://orcid.org/statistics, were the type with more
                // elements should go on top
                if (activityPath.contains(ActivityType.WORKS.getPathDiscriminator())) {
                    works.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.DISTINCTIONS.getPathDiscriminator())) {
                    distinctions.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.EDUCATIONS.getPathDiscriminator())) {
                    educations.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.EMPLOYMENTS.getPathDiscriminator())) {
                    employments.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.FUNDINGS.getPathDiscriminator())) {
                    fundings.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.INVITED_POSITIONS.getPathDiscriminator())) {
                    invitedPositions.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.MEMBERSHIP.getPathDiscriminator())) {
                    memberships.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.PEER_REVIEWS.getPathDiscriminator())) {
                    peerReviews.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.QUALIFICATIONS.getPathDiscriminator())) {
                    qualifications.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.RESEARCH_RESOURCES.getPathDiscriminator())) {
                    researchResources.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.SERVICES.getPathDiscriminator())) {
                    services.put(putCode, objectSummary);
                }
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());

        return activitiesOnS3;
    }

    public void removeV2Activity(String orcid, String putCode, ActivityType type) throws AmazonClientException, AmazonServiceException {
        // Delete the XML activity file
        s3MessagingService.removeV2Activity(getElementName(orcid, putCode, type));
    }

    public void removeV3Activity(String orcid, String putCode, ActivityType type) throws AmazonClientException, AmazonServiceException {
        // Delete the XML activity file
        s3MessagingService.removeV3Activity(orcid, getElementName(orcid, putCode, type));
    }

    public void clearV2Activities(String orcid) throws AmazonClientException, AmazonServiceException {
        String prefix = orcid.substring(16) + "/" + orcid;
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getV2ActivitiesBucketName()).withPrefix(prefix)
                .withMaxKeys(maxElements);
        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String elementName = objectSummary.getKey();
                s3MessagingService.removeV2Activity(elementName);
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());
    }

    public void clearV3Activities(String orcid) throws AmazonClientException, AmazonServiceException {
        String prefix = orcid.substring(16) + "/" + orcid;
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getV3ActivitiesBucketName(orcid)).withPrefix(prefix)
                .withMaxKeys(maxElements);
        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String elementName = objectSummary.getKey();
                s3MessagingService.removeV3Activity(orcid, elementName);
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());
    }

    private String getElementName(String orcid) {
        return orcid.substring(16) + "/" + orcid + ".xml";
    }

    private String getElementName(String orcid, String putCode, ActivityType type) {
        return orcid.substring(16) + "/" + orcid + type.getPathDiscriminator() + orcid + "_" + type.getValue() + "_" + putCode + ".xml";
    }

    private String getActivityPutCode(String activityPath) {
        return activityPath.substring(activityPath.lastIndexOf('_') + 1, activityPath.lastIndexOf('.'));
    }

    private String buildPrefix(String orcid) {
        return orcid.substring(16) + "/" + orcid;
    }

    private String buildPrefix(String orcid, ActivityType type) {
        return orcid.substring(16) + "/" + orcid + "/" + type.getValue();
    }

    public void clearV2ActivitiesByType(String orcid, ActivityType type) throws AmazonClientException, AmazonServiceException {
        String prefix = buildPrefix(orcid, type);
        
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getV2ActivitiesBucketName()).withPrefix(prefix).withMaxKeys(maxElements);

        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String elementName = objectSummary.getKey();
                s3MessagingService.removeV2Activity(elementName);
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());
    }

    public void clearV3ActivitiesByType(String orcid, ActivityType type) throws AmazonClientException, AmazonServiceException {
        String prefix = buildPrefix(orcid, type);
        
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getV3ActivitiesBucketName(orcid)).withPrefix(prefix).withMaxKeys(maxElements);

        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String elementName = objectSummary.getKey();
                s3MessagingService.removeV3Activity(orcid, elementName);
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());
    }
    
}
