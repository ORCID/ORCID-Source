package org.orcid.listener.s3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Component
public class S3MessagingService {

    Logger LOG = LoggerFactory.getLogger(S3MessagingService.class);

    private final AmazonS3 s3;    

    /**
     * Initialize the Amazon S3 connection object
     * 
     * @param secretKey
     *          Secret key to connect to S3
     * @param accessKey
     *          Access key to connect to S3
     */    
    @Autowired
    public S3MessagingService(@Value("${org.orcid.message-listener.s3.secretKey}") String secretKey, @Value("${org.orcid.message-listener.s3.accessKey}") String accessKey) throws JAXBException {
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
     *          The name of the bucket.
     * @param elementName
     *          The name of the object to create.
     * @param elementContent
     *          the content of the object to create.
     *          
     * @return true if the element was correctly sent to the bucket
     * 
     **/
    public boolean send(String bucketName, String elementName, byte[] elementContent, String contentType) throws AmazonClientException {
        InputStream is = new ByteArrayInputStream(elementContent);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(elementContent.length);
        s3.putObject(new PutObjectRequest(bucketName, elementName, is, metadata));
        return true;
    }

}
