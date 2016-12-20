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
package org.orcid.listener.common;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.error_rc4.OrcidError;
import org.orcid.jaxb.model.message.OrcidDeprecated;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.listener.clients.Orcid12APIClient;
import org.orcid.listener.clients.Orcid20APIClient;
import org.orcid.listener.clients.S3Updater;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class ExceptionHandler {

	@Value("${org.orcid.message-listener.api12Enabled:true}")
	private boolean is12IndexingEnabled;

	@Value("${org.orcid.message-listener.api20Enabled:true}")
	private boolean is20IndexingEnabled;

	@Resource
	private Orcid12APIClient orcid12ApiClient;

	@Resource
	private Orcid20APIClient orcid20ApiClient;

	@Resource
	private S3Updater s3Updater;

	/**
	 * If the record is locked: - blank it in 1.2 bucket
	 * 
	 * @throws JAXBException
	 * @throws AmazonClientException
	 * @throws JsonProcessingException
	 * @throws DeprecatedRecordException
	 */
	public void handle12LockedRecordException(String orcid, OrcidMessage errorMessage)
			throws JsonProcessingException, AmazonClientException, JAXBException {
		// Update 1.2 buckets
		if (is12IndexingEnabled) {
			s3Updater.updateS3(orcid, errorMessage);
		}
	}

	/**
	 * If the record is deprecated: - blank it in 1.2 bucket
	 * 
	 * @throws JAXBException
	 * @throws AmazonClientException
	 * @throws JsonProcessingException
	 * @throws DeprecatedRecordException
	 * @throws LockedRecordException
	 */
	public void handle12DeprecatedRecordException(String orcid, OrcidDeprecated errorMessage)
			throws JsonProcessingException, AmazonClientException, JAXBException {
		// Update 1.2 buckets
		if (is12IndexingEnabled) {
			s3Updater.updateS3(orcid, errorMessage);
		}
	}

	/**
	 * If the record is deprecated:
	 *
	 * - blank it in 2.0 bucket
	 * 
	 * @throws JAXBException
	 * @throws AmazonClientException
	 * @throws JsonProcessingException
	 * @throws DeprecatedRecordException
	 * @throws LockedRecordException
	 */
	public void handle20Exception(String orcid, OrcidError orcidError)
			throws JsonProcessingException, AmazonClientException, JAXBException {
		// Update 2.0 buckets
		if (is20IndexingEnabled) {
			s3Updater.updateS3(orcid, orcidError);
		}
	}
}
