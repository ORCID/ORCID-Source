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

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.orcid.jaxb.model.message.OrcidProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Component
public class S3Updater {

    private final ObjectMapper mapper;
    private final boolean writeToFileNotS3;
    
    @Value("org.orcid.persistence.messaging.dump_indexing.enabled")
    private boolean isDumpEnabled;

    Logger LOG = LoggerFactory.getLogger(S3Updater.class);

    /**
     * Writes a profile to S3 TODO: implement S3 writer.
     * 
     * @param writeToFileNotS3
     *            if true, write to local file system temp directory instead of
     *            S3
     */
    @Autowired
    public S3Updater(@Value("${org.orcid.message-lisener.writeToFileNotS3}") boolean writeToFileNotS3) {
        LOG.info("Creating S3Updater with writeToFileNotS3 = " + writeToFileNotS3);
        mapper = new ObjectMapper();
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        this.writeToFileNotS3 = writeToFileNotS3;        
    }

    public void updateS3(OrcidProfile profile) {
        LOG.info("Updating " + profile.getOrcidIdentifier().getPath() + (writeToFileNotS3 ? " on local filesystem" : " in S3 index."));
        if(!isDumpEnabled) {
            LOG.info("Dump indexing is disabled");
            return;
        }
        
        if (writeToFileNotS3) {
            writeToTempFile(profile);
            return;
        }

        throw new UnsupportedOperationException();

    }

    /**
     * Writes {orcid}.json and {orcid}.xml to the temp directory. Used during
     * development
     * 
     * @param profile
     */
    private void writeToTempFile(OrcidProfile profile) {
        // write xml
        Path path;
        try {
            path = Files.createTempFile(profile.getOrcidIdentifier().getPath() + ".", ".xml");
            Files.write(path, profile.toString().getBytes(StandardCharsets.UTF_8)).toFile().deleteOnExit();
            LOG.info("wrote xml to " + path.toAbsolutePath().toString());
        } catch (IOException e) {
            LOG.error("cannot create xml", e);
        }

        // write json
        try {
            StringWriter json = new StringWriter();
            mapper.writeValue(json, profile);
            path = Files.createTempFile(profile.getOrcidIdentifier().getPath() + ".", ".json");
            Files.write(path, json.toString().getBytes(StandardCharsets.UTF_8)).toFile().deleteOnExit();
            LOG.info("wrote json to " + path.toAbsolutePath().toString());
        } catch (IOException e) {
            LOG.error("cannot create json", e);
        }
    }

}
