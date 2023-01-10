package org.orcid.listener.s3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import javax.xml.bind.JAXBException;

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
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Component
public class S3MessagingService {

    Logger LOG = LoggerFactory.getLogger(S3MessagingService.class);

    private final AmazonS3 s3;

    private final String v2SummariesBucketName;
    
    private final String v2ActivitiesBucketName;
    
    private final String v3SummariesBucketName;
    
    private final String v3ActivitiesBucketName;
    
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
            @Value("${org.orcid.message-listener.s3.accessKey}") String accessKey, 
            @Value("${org.orcid.message-listener.index.summaries.bucket_name}") String summariesBucketName, 
            @Value("${org.orcid.message-listener.index.activities.bucket_name}") String activitiesBucketName,
            @Value("${org.orcid.message-listener.index.summaries.v3.bucket_name}") String v3SummariesBucketName, 
            @Value("${org.orcid.message-listener.index.activities.v3.bucket_name}") String v3ActivitiesBucketName)
            throws JAXBException {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            this.s3 = new AmazonS3Client(credentials);
            this.v2SummariesBucketName = summariesBucketName;
            this.v2ActivitiesBucketName = activitiesBucketName;
            this.v3ActivitiesBucketName = v3ActivitiesBucketName;
            this.v3SummariesBucketName = v3SummariesBucketName;
        } catch (Exception e) {
            LOG.error("Unable to connect to the Amazon S3 service", e);
            throw e;
        }
    }

    public String getV2ActivitiesBucketName() {
        return v2ActivitiesBucketName;
    }

    public String getV3ActivitiesBucketName(String orcid) {
        char last = orcid.charAt(orcid.length() - 1);
        String bucketName = v3ActivitiesBucketName;
        switch(last){
        case '0':
        case '1':
        case '2':
        case '3':
            bucketName += "-a";
            break;
        case '4':
        case '5':
        case '6':
        case '7':
            bucketName += "-b";
            break;
        default: 
            bucketName += "-c";
            break;                
        }
        return bucketName;
    }    
    
    public ListObjectsV2Result listObjects(ListObjectsV2Request request) {
        return s3.listObjectsV2(request);
    }
    
    public boolean sendV2Item(String elementName, byte[] elementContent, String contentType, Date lastModified, boolean isActivity) throws AmazonClientException, AmazonServiceException {
        InputStream is = new ByteArrayInputStream(elementContent);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(elementContent.length);
        metadata.setLastModified(lastModified);
        if(isActivity) {
            s3.putObject(this.v2ActivitiesBucketName, elementName, is, metadata);            
        } else {
            s3.putObject(this.v2SummariesBucketName, elementName, is, metadata);
        }
        return true;
    }

    public boolean sendV3Item(String orcid, String elementName, byte[] elementContent, String contentType, Date lastModified, boolean isActivity) throws AmazonClientException, AmazonServiceException {
        InputStream is = new ByteArrayInputStream(elementContent);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(elementContent.length);
        metadata.setLastModified(lastModified);
        if(isActivity) {
            s3.putObject(this.getV3ActivitiesBucketName(orcid), elementName, is, metadata);            
        } else {
            s3.putObject(this.v3SummariesBucketName, elementName, is, metadata);
        }
        return true;
    }
    
    
    public void removeV2Activity(String elementName) throws AmazonClientException, AmazonServiceException {
        s3.deleteObject(this.v2ActivitiesBucketName, elementName);        
    }
    
    public void removeV3Activity(String orcid, String elementName) throws AmazonClientException, AmazonServiceException {
        s3.deleteObject(this.getV3ActivitiesBucketName(orcid), elementName);        
    }
}
