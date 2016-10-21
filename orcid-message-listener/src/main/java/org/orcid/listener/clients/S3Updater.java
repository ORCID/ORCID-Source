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
package org.orcid.listener.clients;

import java.io.StringWriter;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.listener.service.S3MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Component
public class S3Updater {

    Logger LOG = LoggerFactory.getLogger(S3Updater.class);
    
    private final ObjectMapper mapper;

    private final Marshaller marshaller;
   
    private final String bucketPrefix;
    
    @Resource
    private S3MessagingService s3MessagingService;    

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
    public S3Updater(@Value("${org.orcid.message-listener.s3.bucket_prefix}") String bucketPrefix)
            throws JAXBException {        
        mapper = new ObjectMapper();
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        JAXBContext context = JAXBContext.newInstance(OrcidProfile.class, Record.class);
        marshaller = context.createMarshaller();
        // for pretty-print XML in JAXB
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        this.bucketPrefix = bucketPrefix;
    }

    public void updateS3(String orcid, Object profile) throws JsonProcessingException, AmazonClientException, JAXBException {
        // API 1.2            
        if(OrcidProfile.class.isAssignableFrom(profile.getClass())) {
            OrcidProfile orcidProfile = (OrcidProfile) profile;
            putJsonElement(orcid, orcidProfile);
            putXmlElement(orcid, orcidProfile);
            return;
        }
        
        // API 2.0_rc3
        if(Record.class.isAssignableFrom(profile.getClass())) {
            Record record = (Record) profile;
            putJsonElement(orcid, record);
            putXmlElement(orcid, record);
            return;
        }        
    }

    private void putJsonElement(String orcid, OrcidProfile profile) throws JsonProcessingException {
        try {
            String bucket = bucketPrefix + "-api-1-2-json-" + getBucketCheckSum(orcid);
            s3MessagingService.send(bucket, orcid + ".json", toJson(profile));
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
        }
    }

    private void putXmlElement(String orcid, OrcidProfile profile) throws AmazonClientException, JAXBException {
        try {
            String bucket = bucketPrefix + "-api-1-2-xml-" + getBucketCheckSum(orcid);
            s3MessagingService.send(bucket, orcid + ".xml", toXML(profile));
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
        }
    }
    
    private void putJsonElement(String orcid, Record record) throws JsonProcessingException {
        try {
            String bucket = bucketPrefix + "-api-2-0-json-" + getBucketCheckSum(orcid);
            s3MessagingService.send(bucket, orcid + ".json", toJson(record));
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
        }
    }

    private void putXmlElement(String orcid, Record record) throws AmazonClientException, JAXBException {
        try {
            String bucket = bucketPrefix + "-api-2-0-xml-" + getBucketCheckSum(orcid);
            s3MessagingService.send(bucket, orcid + ".xml", toXML(record));
        } catch (AmazonServiceException e) {
            LOG.error(e.getMessage());
        }
    }
    
    private String toJson(Object profile) throws JsonProcessingException {
        return mapper.writeValueAsString(profile);
    }

    private String toXML(Object profile) throws JAXBException {
        StringWriter sw = new StringWriter();
        marshaller.marshal(profile, sw);
        return sw.toString();
    }
    
    public String getBucketCheckSum(String orcid) {
        if(bucketPrefix.endsWith("-qa") || bucketPrefix.endsWith("-sandbox")) {
            return "all"; 
        } else {
            String c = String.valueOf(orcid.charAt(orcid.length() - 1));
            if("X".equals(c)){
                c = "x";
            }
            return c;
        }       
    }
}
