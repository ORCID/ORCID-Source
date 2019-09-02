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

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.listener.persistence.util.APIVersion;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Component
public class S3Manager {

    Logger LOG = LoggerFactory.getLogger(S3Manager.class);

    private final ObjectMapper mapper;

    @Resource
    private S3MessagingService s3MessagingService;

    private final JAXBContext jaxbContext_2_0_api;
    private final JAXBContext jaxbContext_2_0_activities_api;

    private final JAXBContext jaxbContext_3_0_api;
    private final JAXBContext jaxbContext_3_0_activities_api;

    @Value("${org.orcid.message-listener.index.s3.search.max_elements:3000}")
    private Integer maxElements;

    public S3Manager() throws JAXBException {
        mapper = new ObjectMapper();
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        // Initialize JAXBContext
        this.jaxbContext_2_0_api = JAXBContext.newInstance(Record.class, ActivitiesSummary.class, OrcidError.class);
        this.jaxbContext_2_0_activities_api = JAXBContext.newInstance(Education.class, Employment.class, Funding.class, Work.class, PeerReview.class);
        this.jaxbContext_3_0_api = JAXBContext.newInstance(org.orcid.jaxb.model.v3.release.record.Record.class,
                org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary.class, org.orcid.jaxb.model.v3.release.error.OrcidError.class);
        this.jaxbContext_3_0_activities_api = JAXBContext.newInstance(org.orcid.jaxb.model.v3.release.record.Education.class,
                org.orcid.jaxb.model.v3.release.record.Employment.class, org.orcid.jaxb.model.v3.release.record.Funding.class,
                org.orcid.jaxb.model.v3.release.record.Work.class, org.orcid.jaxb.model.v3.release.record.PeerReview.class);
    }

    /**
     * Writes a profile to S3
     * 
     * @param writeToFileNotS3
     *            if true, write to local file system temp directory instead of
     *            S3
     * @throws JAXBException
     */
    @Autowired
    public S3Manager(@Value("${org.orcid.message-listener.s3.bucket_prefix}") String bucketPrefix) throws JAXBException {
        mapper = new ObjectMapper();
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        // Initialize JAXBContext
        this.jaxbContext_2_0_api = JAXBContext.newInstance(Record.class, ActivitiesSummary.class, OrcidError.class);
        this.jaxbContext_2_0_activities_api = JAXBContext.newInstance(Education.class, Employment.class, Funding.class, Work.class, PeerReview.class);
        this.jaxbContext_3_0_api = JAXBContext.newInstance(org.orcid.jaxb.model.v3.release.record.Record.class,
                org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary.class, org.orcid.jaxb.model.v3.release.error.OrcidError.class);
        this.jaxbContext_3_0_activities_api = JAXBContext.newInstance(org.orcid.jaxb.model.v3.release.record.Education.class,
                org.orcid.jaxb.model.v3.release.record.Employment.class, org.orcid.jaxb.model.v3.release.record.Funding.class,
                org.orcid.jaxb.model.v3.release.record.Work.class, org.orcid.jaxb.model.v3.release.record.PeerReview.class);
    }

    public void setS3MessagingService(S3MessagingService s3MessagingService) {
        this.s3MessagingService = s3MessagingService;
    }

    @Deprecated
    private byte[] toXML(Object object) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Marshaller marshaller = null;
        Class<?> c = object.getClass();
        if (org.orcid.jaxb.model.record_v2.Record.class.isAssignableFrom(c) || org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.error_v2.OrcidError.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_2_0_api.createMarshaller();
        } else if (org.orcid.jaxb.model.record_v2.Education.class.isAssignableFrom(c) || org.orcid.jaxb.model.record_v2.Employment.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.record_v2.Funding.class.isAssignableFrom(c) || org.orcid.jaxb.model.record_v2.Work.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.record_v2.PeerReview.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_2_0_activities_api.createMarshaller();
        } else if (org.orcid.jaxb.model.v3.release.record.Record.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.error.OrcidError.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_3_0_api.createMarshaller();
        } else if (org.orcid.jaxb.model.v3.release.record.Distinction.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Education.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Employment.class.isAssignableFrom(c) || org.orcid.jaxb.model.v3.release.record.Funding.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.InvitedPosition.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Membership.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.PeerReview.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Qualification.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.ResearchResource.class.isAssignableFrom(c)
                || org.orcid.jaxb.model.v3.release.record.Service.class.isAssignableFrom(c) || org.orcid.jaxb.model.v3.release.record.Work.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_3_0_activities_api.createMarshaller();
        } else {
            throw new IllegalArgumentException("Unable to unmarshall class " + c);
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(object, baos);
        return baos.toByteArray();
    }

    public void uploadV2RecordSummary(String orcid, org.orcid.jaxb.model.record_v2.Record record) throws JAXBException, JsonProcessingException {
        Date lastModified = DateUtils.convertToDate(record.getHistory().getLastModifiedDate().getValue());
        // Upload XML
        String xmlElementName = getElementName(orcid);
        byte[] xmlElement = toXML(record);
        s3MessagingService.sendV2Item(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, false);
    }

    public void uploadV3RecordSummary(String orcid, org.orcid.jaxb.model.v3.release.record.Record record) throws JAXBException, JsonProcessingException {
        Date lastModified = DateUtils.convertToDate(record.getHistory().getLastModifiedDate().getValue());
        // Upload XML
        String xmlElementName = getElementName(orcid);
        byte[] xmlElement = toXML(record);
        s3MessagingService.sendV3Item(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, false);
    }

    public void uploadV2Activity(String orcid, String putCode, org.orcid.jaxb.model.record_v2.Activity activity) throws JAXBException, JsonProcessingException {
        Date lastModified = DateUtils.convertToDate(activity.getLastModifiedDate().getValue());
        // Upload XML
        String xmlElementName = getElementName(orcid, putCode, ActivityType.inferFromActivity(activity));
        byte[] xmlElement = toXML(activity);
        s3MessagingService.sendV2Item(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, true);
    }

    public void uploadV3Activity(String orcid, String putCode, org.orcid.jaxb.model.v3.release.record.Activity activity) throws JAXBException, JsonProcessingException {
        Date lastModified = DateUtils.convertToDate(activity.getLastModifiedDate().getValue());
        // Upload XML
        String xmlElementName = getElementName(orcid, putCode, ActivityType.inferFromActivity(activity));
        byte[] xmlElement = toXML(activity);
        s3MessagingService.sendV3Item(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, true);
    }

    public void uploadV2OrcidError(String orcid, org.orcid.jaxb.model.error_v2.OrcidError error) throws JAXBException, JsonProcessingException {
        Date lastModified = new Date();

        // Upload XML
        String xmlElementName = getElementName(orcid);
        byte[] xmlElement = toXML(error);
        s3MessagingService.sendV2Item(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, false);
    }

    public void uploadV3OrcidError(String orcid, org.orcid.jaxb.model.v3.release.error.OrcidError error) throws JAXBException, JsonProcessingException {
        Date lastModified = new Date();

        // Upload XML
        String xmlElementName = getElementName(orcid);
        byte[] xmlElement = toXML(error);
        s3MessagingService.sendV3Item(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified, false);
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
        String bucketName = (APIVersion.V2.equals(version) ? s3MessagingService.getV2ActivitiesBucketName() : s3MessagingService.getV3ActivitiesBucketName());
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

    public void removeV2Activity(String orcid, String putCode, ActivityType type) {
        // Delete the XML activity file
        s3MessagingService.removeV2Activity(getElementName(orcid, putCode, type));
    }

    public void removeV3Activity(String orcid, String putCode, ActivityType type) {
        // Delete the XML activity file
        s3MessagingService.removeV3Activity(getElementName(orcid, putCode, type));
    }

    public void clearV2Activities(String orcid) {
        String prefix = orcid.substring(16) + "/activities/" + orcid;
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

    public void clearV3Activities(String orcid) {
        String prefix = orcid.substring(16) + "/activities/" + orcid;
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getV3ActivitiesBucketName()).withPrefix(prefix)
                .withMaxKeys(maxElements);
        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String elementName = objectSummary.getKey();
                s3MessagingService.removeV3Activity(elementName);
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
        
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getV3ActivitiesBucketName()).withPrefix(prefix).withMaxKeys(maxElements);

        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String elementName = objectSummary.getKey();
                s3MessagingService.removeV3Activity(elementName);
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());
    }
    
}
