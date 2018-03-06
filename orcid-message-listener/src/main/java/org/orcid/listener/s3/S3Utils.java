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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Region;

public class S3Utils {

    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("There must be 3 params: bucket prefix, key and secret");
        }

        String bucketPrefix = args[0];
        String accessKey = args[1];
        String secretKey = args[2];

        S3Utils.createBuckets(bucketPrefix, accessKey, secretKey);
    }

    // Create S3 buckets for a given prefix
    public static void createBuckets(String bucketPrefix, String accessKey, String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3 = new AmazonS3Client(credentials);

        String api12JsonPrefix = bucketPrefix + "-api-1-2-json-";
        String api12XMLPrefix = bucketPrefix + "-api-1-2-xml-";
        String api20JsonPrefix = bucketPrefix + "-api-2-0-json-";
        String api20XMLPrefix = bucketPrefix + "-api-2-0-xml-";

        for (int i = 0; i <= 10; i++) {
            char lastCharacter = (i == 10 ? 'x' : Character.forDigit(i, 10));

            if (!s3.doesBucketExist(api12JsonPrefix + lastCharacter)) {
                s3.createBucket((api12JsonPrefix + lastCharacter), Region.EU_Ireland);
            }

            if (!s3.doesBucketExist(api12XMLPrefix + lastCharacter)) {
                s3.createBucket((api12XMLPrefix + lastCharacter), Region.EU_Ireland);
            }

            if (!s3.doesBucketExist(api20JsonPrefix + lastCharacter)) {
                s3.createBucket((api20JsonPrefix + lastCharacter), Region.EU_Ireland);
            }

            if (!s3.doesBucketExist(api20XMLPrefix + lastCharacter)) {
                s3.createBucket((api20XMLPrefix + lastCharacter), Region.EU_Ireland);
            }
        }
    }
}
