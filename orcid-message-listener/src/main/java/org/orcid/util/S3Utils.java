package org.orcid.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;

public class S3Utils {
    
    public static void main(String [] args) {
        if(args.length != 3) {
            throw new IllegalArgumentException("There must be 3 params: bucket prefix, key and secret");
        }
                
        String bucketPrefix = args[0];
        String accessKey = args[1];
        String secretKey = args[2];
        
        S3Utils.createBuckets(bucketPrefix, accessKey, secretKey);
    }
    
    //Create S3 buckets for a given prefix
    public static void createBuckets(String bucketPrefix, String accessKey, String secretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3 = new AmazonS3Client(credentials);
        s3.setRegion(Region.getRegion(Regions.EU_WEST_1));
        
        String api12JsonPrefix = bucketPrefix + "-api-1-2-json-";
        String api12XMLPrefix = bucketPrefix + "-api-1-2-xml-";
        String api20JsonPrefix = bucketPrefix + "-api-2-0-json-";
        String api20XMLPrefix = bucketPrefix + "-api-2-0-xml-";
                
        try {
            if(!s3.doesBucketExist("testing-bucket-01")) {
                s3.createBucket(new CreateBucketRequest("testing-bucket-01"));
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                        "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                        "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
        
        
    } 
}
