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
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
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

    private final String bucketPrefix;

    @Resource
    private S3MessagingService s3MessagingService;

    private final JAXBContext jaxbContext_1_2_api;
    private final JAXBContext jaxbContext_2_0_api;
    private final JAXBContext jaxbContext_2_0_activities_api;

    @Value("${org.orcid.message-listener.index.s3.search.max_elements:3000}")
    private Integer maxElements;

    public void setS3MessagingService(S3MessagingService s3MessagingService) {
        this.s3MessagingService = s3MessagingService;
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

        this.bucketPrefix = bucketPrefix;

        // Initialize JAXBContext
        this.jaxbContext_1_2_api = JAXBContext.newInstance(OrcidMessage.class, OrcidDeprecated.class);
        this.jaxbContext_2_0_api = JAXBContext.newInstance(Record.class, ActivitiesSummary.class, OrcidError.class);
        this.jaxbContext_2_0_activities_api = JAXBContext.newInstance(Education.class, Employment.class, Funding.class, Work.class, PeerReview.class);
    }

    @Deprecated
    public void updateS3(String orcid, byte[] element, String mediaType) throws IOException {
        if (S3MessageProcessor.VND_ORCID_XML.equals(mediaType)) {
            s3MessagingService.send(getBucketName("api-1-2", "xml", orcid), orcid + ".xml", element, mediaType);
        } else {
            s3MessagingService.send(getBucketName("api-1-2", "json", orcid), orcid + ".json", element, mediaType);
        }
    }

    @Deprecated
    public void updateS3(String orcid, Object object) throws IOException, JAXBException {
        // API 1.2
        if (OrcidMessage.class.isAssignableFrom(object.getClass())) {
            OrcidMessage orcidProfile = (OrcidMessage) object;
            putJsonElement(orcid, orcidProfile);
            putXmlElement(orcid, orcidProfile);
            return;
        }

        // API 1.2 ERROR
        if (OrcidDeprecated.class.isAssignableFrom(object.getClass())) {
            OrcidDeprecated error = (OrcidDeprecated) object;
            putJsonElement(orcid, error);
            putXmlElement(orcid, error);
            return;
        }

        // API 2.0
        if (Record.class.isAssignableFrom(object.getClass())) {
            Record record = (Record) object;
            putJsonElement(orcid, record);
            putXmlElement(orcid, record);
            return;
        }

        // API 2.0 Activities
        if (ActivitiesSummary.class.isAssignableFrom(object.getClass())) {
            ActivitiesSummary as = (ActivitiesSummary) object;
            putJsonElement(orcid, as);
            putXmlElement(orcid, as);
            return;
        }

        // API 2.0 Error
        if (OrcidError.class.isAssignableFrom(object.getClass())) {
            OrcidError error = (OrcidError) object;
            putJsonElement(orcid, error, false);
            putXmlElement(orcid, error, false);
            return;
        }
    }

    public void setErrorOnActivitiesBucket(String orcid, OrcidError error) throws AmazonClientException, IOException, JAXBException {
        putJsonElement(orcid, error, true);
        putXmlElement(orcid, error, true);
    }

    @Deprecated
    private void putJsonElement(String orcid, OrcidMessage profile) throws AmazonClientException, JsonProcessingException {
        String bucket = getBucketName("api-1-2", "json", orcid);
        s3MessagingService.send(bucket, orcid + ".json", toJson(profile), MediaType.APPLICATION_JSON);
    }

    @Deprecated
    private void putXmlElement(String orcid, OrcidMessage profile) throws AmazonClientException, IOException, JAXBException {
        String bucket = getBucketName("api-1-2", "xml", orcid);
        s3MessagingService.send(bucket, orcid + ".xml", toXML(profile), MediaType.APPLICATION_XML);
    }

    private void putJsonElement(String orcid, OrcidDeprecated error) throws AmazonClientException, JsonProcessingException {
        String bucket = getBucketName("api-1-2", "json", orcid);
        s3MessagingService.send(bucket, orcid + ".json", toJson(error), MediaType.APPLICATION_JSON);
    }

    private void putXmlElement(String orcid, OrcidDeprecated error) throws AmazonClientException, IOException, JAXBException {
        String bucket = getBucketName("api-1-2", "xml", orcid);
        s3MessagingService.send(bucket, orcid + ".xml", toXML(error), MediaType.APPLICATION_XML);
    }

    private void putJsonElement(String orcid, Record record) throws AmazonClientException, JsonProcessingException {
        String bucket = getBucketName("api-2-0", "json", orcid);
        s3MessagingService.send(bucket, orcid + ".json", toJson(record), MediaType.APPLICATION_JSON);
    }

    private void putXmlElement(String orcid, Record record) throws AmazonClientException, IOException, JAXBException {
        String bucket = getBucketName("api-2-0", "xml", orcid);
        s3MessagingService.send(bucket, orcid + ".xml", toXML(record), MediaType.APPLICATION_XML);
    }

    private void putJsonElement(String orcid, ActivitiesSummary as) throws AmazonClientException, JsonProcessingException {
        String bucket = getBucketName("api-2-0-activities", "json", orcid);
        s3MessagingService.send(bucket, orcid + "_activities.json", toJson(as), MediaType.APPLICATION_JSON);
    }

    private void putXmlElement(String orcid, ActivitiesSummary as) throws AmazonClientException, IOException, JAXBException {
        String bucket = getBucketName("api-2-0-activities", "xml", orcid);
        s3MessagingService.send(bucket, orcid + "_activities.xml", toXML(as), MediaType.APPLICATION_XML);
    }

    private void putJsonElement(String orcid, OrcidError error, boolean activities) throws AmazonClientException, JsonProcessingException {
        if (activities) {
            String bucket = getBucketName("api-2-0-activities", "json", orcid);
            s3MessagingService.send(bucket, orcid + "_activities.json", toJson(error), MediaType.APPLICATION_JSON);
        } else {
            String bucket = getBucketName("api-2-0", "json", orcid);
            s3MessagingService.send(bucket, orcid + ".json", toJson(error), MediaType.APPLICATION_JSON);
        }
    }

    private void putXmlElement(String orcid, OrcidError error, boolean activities) throws AmazonClientException, IOException, JAXBException {
        if (activities) {
            String bucket = getBucketName("api-2-0-activities", "xml", orcid);
            s3MessagingService.send(bucket, orcid + "_activities.xml", toXML(error), MediaType.APPLICATION_XML);
        } else {
            String bucket = getBucketName("api-2-0", "xml", orcid);
            s3MessagingService.send(bucket, orcid + ".xml", toXML(error), MediaType.APPLICATION_XML);
        }
    }

    private byte[] toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsBytes(object);
    }

    private byte[] toXML(Object object) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Marshaller marshaller = null;
        Class<?> c = object.getClass();
        if (OrcidMessage.class.isAssignableFrom(c) || OrcidDeprecated.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_1_2_api.createMarshaller();
        } else if (Record.class.isAssignableFrom(c) || ActivitiesSummary.class.isAssignableFrom(c) || OrcidError.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_2_0_api.createMarshaller();
        } else if (Education.class.isAssignableFrom(c) || Employment.class.isAssignableFrom(c) || Funding.class.isAssignableFrom(c) || Work.class.isAssignableFrom(c)
                || PeerReview.class.isAssignableFrom(c)) {
            marshaller = jaxbContext_2_0_activities_api.createMarshaller();
        } else {
            throw new IllegalArgumentException("Unable to unmarshall class " + c);
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(object, baos);
        return baos.toByteArray();
    }

    public String getBucketName(String apiVersion, String format, String orcid) {
        if (bucketPrefix.endsWith("-dev") || bucketPrefix.endsWith("-qa") || bucketPrefix.endsWith("-sandbox")) {
            return bucketPrefix + "-all";
        } else {
            String c = String.valueOf(orcid.charAt(orcid.length() - 1));
            if ("X".equals(c)) {
                c = "x";
            }
            return bucketPrefix + '-' + apiVersion + '-' + format + '-' + c;
        }
    }

    public void uploadRecordSummary(String orcid, Record record) throws JAXBException, JsonProcessingException {
        Date lastModified = DateUtils.convertToDate(record.getHistory().getLastModifiedDate().getValue());
        // Upload XML
        String xmlElementName = getElementName(orcid, "xml");
        byte[] xmlElement = toXML(record);
        s3MessagingService.send(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified);

        // Upload JSON
        String jsonElementName = getElementName(orcid, "json");
        byte[] jsonElement = toJson(record);
        s3MessagingService.send(jsonElementName, jsonElement, MediaType.APPLICATION_JSON, lastModified);
    }

    public void uploadActivity(String orcid, String putCode, Activity activity) throws JAXBException, JsonProcessingException {
        Date lastModified = DateUtils.convertToDate(activity.getLastModifiedDate().getValue());
        // Upload XML
        String xmlElementName = getElementName(orcid, putCode, ActivityType.inferFromActivity(activity), "xml");
        byte[] xmlElement = toXML(activity);
        s3MessagingService.send(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified);

        // Upload JSON
        String jsonElementName = getElementName(orcid, putCode, ActivityType.inferFromActivity(activity), "json");
        byte[] jsonElement = toJson(activity);
        s3MessagingService.send(jsonElementName, jsonElement, MediaType.APPLICATION_JSON, lastModified);
    }

    public void uploadOrcidError(String orcid, OrcidError error) throws JAXBException, JsonProcessingException {
        Date lastModified = new Date();

        // Upload XML
        String xmlElementName = getElementName(orcid, "xml");
        byte[] xmlElement = toXML(error);
        s3MessagingService.send(xmlElementName, xmlElement, MediaType.APPLICATION_XML, lastModified);

        // Upload JSON
        String jsonElementName = getElementName(orcid, "json");
        byte[] jsonElement = toJson(error);
        s3MessagingService.send(jsonElementName, jsonElement, MediaType.APPLICATION_JSON, lastModified);
    }

    public Map<ActivityType, Map<String, S3ObjectSummary>> searchActivities(String orcid) {
        Map<ActivityType, Map<String, S3ObjectSummary>> activitiesOnS3 = new HashMap<ActivityType, Map<String, S3ObjectSummary>>();

        Map<String, S3ObjectSummary> educations = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> employments = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> fundings = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> works = new HashMap<String, S3ObjectSummary>();
        Map<String, S3ObjectSummary> peerReviews = new HashMap<String, S3ObjectSummary>();

        activitiesOnS3.put(ActivityType.EDUCATIONS, educations);
        activitiesOnS3.put(ActivityType.EMPLOYMENTS, employments);
        activitiesOnS3.put(ActivityType.FUNDINGS, fundings);
        activitiesOnS3.put(ActivityType.WORKS, works);
        activitiesOnS3.put(ActivityType.PEER_REVIEWS, peerReviews);

        String prefix = buildPrefix(orcid);
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getBucketName()).withPrefix(prefix).withMaxKeys(maxElements);
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
                } else if (activityPath.contains(ActivityType.EDUCATIONS.getPathDiscriminator())) {
                    educations.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.EMPLOYMENTS.getPathDiscriminator())) {
                    employments.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.FUNDINGS.getPathDiscriminator())) {
                    fundings.put(putCode, objectSummary);
                } else if (activityPath.contains(ActivityType.PEER_REVIEWS.getPathDiscriminator())) {
                    peerReviews.put(putCode, objectSummary);
                }
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());

        return activitiesOnS3;
    }

    public void removeActivity(String orcid, String putCode, ActivityType type) {
        // Delete the XML activity file
        s3MessagingService.removeElement(getElementName(orcid, putCode, type, "xml"));
        // Delete the JSON activity file
        s3MessagingService.removeElement(getElementName(orcid, putCode, type, "json"));
    }

    public void clearActivities(String orcid) {
        String prefix = orcid.substring(16) + "/activities/" + orcid;
        final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getBucketName()).withPrefix(prefix).withMaxKeys(maxElements);
        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String elementName = objectSummary.getKey();
                s3MessagingService.removeElement(elementName);
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());
    }

    public void clearActivitiesByType(String orcid, ActivityType type) {
        // Clear xml activities
        removeElements(buildPrefix(orcid, type, "xml"));
        // Clear json activities
        removeElements(buildPrefix(orcid, type, "json"));
    }

    private void removeElements(String prefix) {
        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(s3MessagingService.getBucketName()).withPrefix(prefix).withMaxKeys(maxElements);

        ListObjectsV2Result objects;
        do {
            objects = s3MessagingService.listObjects(req);
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                String elementName = objectSummary.getKey();
                s3MessagingService.removeElement(elementName);
            }
            req.setContinuationToken(objects.getNextContinuationToken());
        } while (objects.isTruncated());
    }

    private String getElementName(String orcid, String format) {
        return orcid.substring(16) + "/summaries/" + format + "/" + orcid + "." + format;
    }

    private String getElementName(String orcid, String putCode, ActivityType type, String format) {
        return orcid.substring(16) + "/activities/" + orcid + "/" + format + type.getPathDiscriminator() + orcid + "_" + type.getValue() + "_" + putCode + "." + format;
    }

    private String getActivityPutCode(String activityPath) {
        return activityPath.substring(activityPath.lastIndexOf('_') + 1, activityPath.lastIndexOf('.'));
    }

    private String buildPrefix(String orcid) {
        return orcid.substring(16) + "/activities/" + orcid + "/xml/";
    }

    private String buildPrefix(String orcid, ActivityType type, String format) {
        return orcid.substring(16) + "/activities/" + orcid + "/" + format + "/" + type.getValue();
    }
}
