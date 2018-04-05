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
package org.orcid.activitiesindexer.s3;

import java.io.IOException;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandler {

    @Value("${org.orcid.message-listener.api12Enabled:true}")
    private boolean is12IndexingEnabled;

    @Value("${org.orcid.message-listener.api20Enabled:true}")
    private boolean is20IndexingEnabled;

    @Resource
    private S3Manager s3Updater;

    /**
     * If the record is locked: - blank it in 1.2 bucket
     */
    public void handle12LockedRecordException(String orcid, OrcidMessage errorMessage) throws IOException, JAXBException {
        // Update 1.2 buckets
        if (is12IndexingEnabled) {
            s3Updater.updateS3(orcid, errorMessage);
        }
    }

    /**
     * If the record is deprecated: - blank it in 1.2 bucket
     * 
     * @throws JAXBException
     * @throws IOException
     * 
     */
    public void handle12DeprecatedRecordException(String orcid, OrcidDeprecated errorMessage) throws IOException, JAXBException {
        // Update 1.2 buckets
        if (is12IndexingEnabled) {
            s3Updater.updateS3(orcid, errorMessage);
        }
    }

    /**
     * If the record is deprecated or locked:
     *
     * - blank it in 2.0 bucket
     * 
     * @throws JAXBException
     * @throws IOException
     * 
     */
    public void handle20Exception(String orcid, OrcidError orcidError) throws IOException, JAXBException {
        // Update 2.0 buckets
        if (is20IndexingEnabled) {
            s3Updater.updateS3(orcid, orcidError);
        }
    }
    
    /**
     * If the record is deprecated or locked:
     *
     * - blank it in 2.0 bucket
     * 
     * @throws JAXBException
     * @throws IOException
     * 
     */
    public void handle20ActivitiesException(String orcid, OrcidError orcidError) throws IOException, JAXBException {
        // Update 2.0 buckets
        if (is20IndexingEnabled) {
            s3Updater.setErrorOnActivitiesBucket(orcid, orcidError);
        }
    }
}
