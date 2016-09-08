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

import org.orcid.jaxb.model.message.OrcidProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Component
public class S3Updater {

    private final ObjectMapper mapper;    
    
    Logger LOG = LoggerFactory.getLogger(S3Updater.class);

    /**
     * Writes a profile to S3 TODO: implement S3 writer.
     * 
     * @param writeToFileNotS3
     *            if true, write to local file system temp directory instead of
     *            S3
     */
    public S3Updater() {
        mapper = new ObjectMapper();
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);    
    }

    public void updateS3(OrcidProfile profile) throws JsonProcessingException {        
        String elementName = profile.getOrcidIdentifier().getPath();
        String jsonElement = mapper.writeValueAsString(profile);
        String bucketName = "orcid-public-data-dump-dev";

        AWSCredentials credentials = new BasicAWSCredentials("AKIAJ3FMFSKQZCNDFFIQ", "3DrzCw84+IdgHAEsr8YqtyqIsUh4kjPk+CDsdL2l");
        
        final AmazonS3 s3 = new AmazonS3Client(credentials);
        try {
            s3.putObject(bucketName, elementName, jsonElement);
        }
        catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}
