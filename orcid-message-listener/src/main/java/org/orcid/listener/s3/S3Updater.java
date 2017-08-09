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

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Component
public class S3Updater {

    Logger LOG = LoggerFactory.getLogger(S3Updater.class);

    private final ObjectMapper mapper;

    private final String bucketPrefix;

    @Resource
    private S3MessagingService s3MessagingService;

    private final JAXBContext jaxbContext_1_2_api;
    private final JAXBContext jaxbContext_2_0_api;

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
    public S3Updater(@Value("${org.orcid.message-listener.s3.bucket_prefix}") String bucketPrefix) throws JAXBException {
        mapper = new ObjectMapper();
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        this.bucketPrefix = bucketPrefix;

        // Initialize JAXBContext
        this.jaxbContext_1_2_api = JAXBContext.newInstance(OrcidMessage.class, OrcidDeprecated.class);
        this.jaxbContext_2_0_api = JAXBContext.newInstance(Record.class, ActivitiesSummary.class, OrcidError.class);
    }

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
            putJsonElement(orcid, error);
            putXmlElement(orcid, error);
            return;
        }
    }

    private void putJsonElement(String orcid, OrcidMessage profile) throws JsonProcessingException {
        try {
            String bucket = getBucketName("api-1-2", "json", orcid);
            s3MessagingService.send(bucket, orcid + ".json", toJson(profile), MediaType.APPLICATION_JSON);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putXmlElement(String orcid, OrcidMessage profile) throws IOException, JAXBException {
        try {
            String bucket = getBucketName("api-1-2", "xml", orcid);
            s3MessagingService.send(bucket, orcid + ".xml", toXML(profile), MediaType.APPLICATION_XML);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putJsonElement(String orcid, OrcidDeprecated error) throws JsonProcessingException {
        try {
            String bucket = getBucketName("api-1-2", "json", orcid);
            s3MessagingService.send(bucket, orcid + ".json", toJson(error), MediaType.APPLICATION_JSON);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putXmlElement(String orcid, OrcidDeprecated error) throws IOException, JAXBException {
        try {
            String bucket = getBucketName("api-1-2", "xml", orcid);
            s3MessagingService.send(bucket, orcid + ".xml", toXML(error), MediaType.APPLICATION_XML);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putJsonElement(String orcid, Record record) throws JsonProcessingException {
        try {
            String bucket = getBucketName("api-2-0", "json", orcid);
            s3MessagingService.send(bucket, orcid + ".json", toJson(record), MediaType.APPLICATION_JSON);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putXmlElement(String orcid, Record record) throws IOException, JAXBException {
        try {
            String bucket = getBucketName("api-2-0", "xml", orcid);
            s3MessagingService.send(bucket, orcid + ".xml", toXML(record), MediaType.APPLICATION_XML);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putJsonElement(String orcid, ActivitiesSummary as) throws JsonProcessingException {
        try {
            String bucket = getBucketName("api-2-0-activities", "json", orcid);
            s3MessagingService.send(bucket, orcid + "_activities.json", toJson(as), MediaType.APPLICATION_JSON);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putXmlElement(String orcid, ActivitiesSummary as) throws IOException, JAXBException {
        try {
            String bucket = getBucketName("api-2-0-activities", "xml", orcid);
            s3MessagingService.send(bucket, orcid + "_activities.xml", toXML(as), MediaType.APPLICATION_XML);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putJsonElement(String orcid, OrcidError error) throws JsonProcessingException {
        try {
            String bucket = getBucketName("api-2-0", "json", orcid);
            s3MessagingService.send(bucket, orcid + ".json", toJson(error), MediaType.APPLICATION_JSON);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private void putXmlElement(String orcid, OrcidError error) throws IOException, JAXBException {
        try {
            String bucket = getBucketName("api-2-0", "xml", orcid);
            s3MessagingService.send(bucket, orcid + ".xml", toXML(error), MediaType.APPLICATION_XML);
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
            throw e;
        }
    }

    private byte[] toJson(Object object) throws JsonProcessingException {
        return mapper.writeValueAsBytes(object);
    }

    private byte[] toXML(Object object) throws JAXBException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Marshaller marshaller = null;
        if (OrcidMessage.class.isAssignableFrom(object.getClass()) || OrcidDeprecated.class.isAssignableFrom(object.getClass())) {
            marshaller = jaxbContext_1_2_api.createMarshaller();
        } else if (Record.class.isAssignableFrom(object.getClass()) || ActivitiesSummary.class.isAssignableFrom(object.getClass())
                || OrcidError.class.isAssignableFrom(object.getClass())) {
            marshaller = jaxbContext_2_0_api.createMarshaller();
        } else {
            throw new IllegalArgumentException("Unable to unmarshall class " + object.getClass());
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
}
