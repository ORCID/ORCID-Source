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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.orcid.listener.persistence.util.ActivityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Component
public class S3MessagingService {

	Logger LOG = LoggerFactory.getLogger(S3MessagingService.class);

	private final AmazonS3 s3;

	/**
	 * Initialize the Amazon S3 connection object
	 * 
	 * @param secretKey
	 *            Secret key to connect to S3
	 * @param accessKey
	 *            Access key to connect to S3
	 */
	@Autowired
	public S3MessagingService(@Value("${org.orcid.message-listener.s3.secretKey}") String secretKey,
			@Value("${org.orcid.message-listener.s3.accessKey}") String accessKey) throws JAXBException {
		try {
			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
			this.s3 = new AmazonS3Client(credentials);
		} catch (Exception e) {
			LOG.error("Unable to connect to the Amazon S3 service", e);
			throw e;
		}
	}

	/**
	 * Sends the content to the given bucket
	 * 
	 * @param bucketName
	 *            The name of the bucket.
	 * @param elementName
	 *            The name of the object to create.
	 * @param elementContent
	 *            the content of the object to create.
	 * 
	 * @return true if the element was correctly sent to the bucket
	 * 
	 **/
	public boolean send(String bucketName, String elementName, byte[] elementContent, String contentType)
			throws AmazonClientException, AmazonServiceException {
		InputStream is = new ByteArrayInputStream(elementContent);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);
		metadata.setContentLength(elementContent.length);
		s3.putObject(new PutObjectRequest(bucketName, elementName, is, metadata));
		return true;
	}

	public boolean send(String bucketName, String elementName, byte[] elementContent, String contentType,
			Date lastModified) throws AmazonClientException, AmazonServiceException {
		InputStream is = new ByteArrayInputStream(elementContent);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);
		metadata.setContentLength(elementContent.length);
		metadata.setLastModified(lastModified);
		s3.putObject(new PutObjectRequest(bucketName, elementName, is, metadata));
		return true;
	}

	public Map<ActivityType, List<String>> searchActivities(String bucketName, String prefix)
			throws AmazonClientException, AmazonServiceException {
		Map<ActivityType, List<String>> activitiesOnS3 = new HashMap<ActivityType, List<String>>();

		for (ActivityType a : ActivityType.values()) {
			activitiesOnS3.put(a, new ArrayList<String>());
		}

		ListObjectsV2Result objects;
		do {
			objects = s3.listObjectsV2(bucketName, prefix);

			for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
				Date lastModified = objectSummary.getLastModified();
				System.out.println(lastModified);
				String activityPath = objectSummary.getKey();
				for (ActivityType a : ActivityType.values()) {
					if (activityPath.contains(a.getPathDiscriminator())) {
						activitiesOnS3.get(a).add(activityPath);
						break;
					}
				}
			}

			objects.setContinuationToken(objects.getNextContinuationToken());
		} while (objects.isTruncated());

		return activitiesOnS3;
	}

}
