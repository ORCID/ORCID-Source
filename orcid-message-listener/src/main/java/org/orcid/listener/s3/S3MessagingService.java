package org.orcid.listener.s3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
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
     */
    @Autowired
    public S3MessagingService(@Value("${org.orcid.message-listener.index.summaries.bucket_name}") String summariesBucketName,
            @Value("${org.orcid.message-listener.index.activities.bucket_name}") String activitiesBucketName,
            @Value("${org.orcid.message-listener.index.summaries.v3.bucket_name}") String v3SummariesBucketName, 
            @Value("${org.orcid.message-listener.index.activities.v3.bucket_name}") String v3ActivitiesBucketName,
            @Value("${org.orcid.message-listener.index.use_eu_west_1:false}") Boolean useEuWest1)
            throws JAXBException {
        try {
            Regions region = Regions.US_EAST_2;
            if(useEuWest1) {
                region = Regions.EU_WEST_1;
            }

            LOG.warn("Using AWS region " + region.getName());

            this.s3 = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .build();
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
    
    public boolean safelyRemoveV2Activity(String elementName) {
        return safelyRemoveV2Activity(elementName, true);
    }
    
    public boolean safelyRemoveV3Activity(String orcid, String elementName) {
        return safelyRemoveV3Activity(orcid, elementName, true);
    }
    
    private boolean safelyRemoveV2Activity(String elementName, boolean retry) {
        try {
            removeV2Activity(elementName);
            return true;
        } catch (Exception e) {
            if(retry) {
                // If the element failed to be deleted, it might be because it doesn't exists anymore
                boolean elementExists = doesV2ActivityExists(elementName);
                if(elementExists) {
                    // If the element still exists, wait 10 secs and try one more time
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch(Exception x) {
                        //Do nothing              
                    }
                    boolean wasRemoved = safelyRemoveV2Activity(elementName, false);
                    if(!wasRemoved) {
                        LOG.error("Unable to remove element " + elementName);
                    }
                    return wasRemoved;
                } else {
                    // If the element does not exists, then this is fine 
                    return true;
                }
            } else {
                LOG.error("Unable to remove element " + elementName);
                return false;
            }
        } 
    }
    
    private boolean safelyRemoveV3Activity(String orcid, String elementName, boolean retry) {
        try {
            removeV3Activity(orcid, elementName);
            return true;
        } catch (Exception e) {
            // If the element failed to be deleted, it might be because it doesn't exists anymore
            if(retry) {
                boolean elementExists = doesV3ActivityExists(orcid, elementName);
                if(elementExists) {
                    // If the element still exists, wait 10 secs and try one more time
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch(Exception x) {
                        //Do nothing                    
                    }
                    boolean wasRemoved = safelyRemoveV3Activity(orcid, elementName, false);
                    if(!wasRemoved) {
                        LOG.error("Unable to remove element " + elementName);
                    }
                    return wasRemoved;
                } else {
                    // If the element does not exists, then this is fine 
                    return true;
                }
            } else {
                LOG.error("Unable to remove element " + elementName);
                return false;
            }            
        }         
    }
    
    private void removeV2Activity(String elementName) throws AmazonClientException, AmazonServiceException {
        s3.deleteObject(this.v2ActivitiesBucketName, elementName);        
    }
    
    private void removeV3Activity(String orcid, String elementName) throws AmazonClientException, AmazonServiceException {
        s3.deleteObject(this.getV3ActivitiesBucketName(orcid), elementName);        
    }
    
    private boolean doesV2ActivityExists(String elementName) throws AmazonServiceException, SdkClientException {
        return s3.doesObjectExist(this.v2ActivitiesBucketName, elementName);
    }
    
    private boolean doesV3ActivityExists(String orcid, String elementName) throws AmazonServiceException, SdkClientException {
        return s3.doesObjectExist(this.getV3ActivitiesBucketName(orcid), elementName);
    }        
}
