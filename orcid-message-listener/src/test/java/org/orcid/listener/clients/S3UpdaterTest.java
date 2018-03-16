package org.orcid.listener.clients;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.listener.s3.S3MessagingService;
import org.orcid.listener.s3.S3Updater;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

import com.amazonaws.AmazonClientException;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class S3UpdaterTest {

    @Mock
    private S3MessagingService s3MessagingService;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void getBucketChecksumTest() throws JAXBException {
        S3Updater s3 = new S3Updater("bucket-dev");
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-00003"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0003"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-000X"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-000X"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-000X"));
        assertEquals("bucket-dev-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-000X"));
        
        s3 = new S3Updater("bucket-qa");
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-00003"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0003"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-000X"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-000X"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-000X"));
        assertEquals("bucket-qa-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-000X"));
        
        s3 = new S3Updater("bucket-sandbox");
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-00003"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0003"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-000X"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-1-2", "json", "0000-0000-0000-000X"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-000X"));
        assertEquals("bucket-sandbox-all", s3.getBucketName("api-2-0", "json", "0000-0000-0000-000X"));
        
        s3 = new S3Updater("bucket-production");
        assertEquals("bucket-production-api-1-2-xml-0", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-production-api-1-2-xml-1", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-production-api-1-2-xml-2", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-production-api-1-2-xml-3", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0003"));
        assertEquals("bucket-production-api-1-2-xml-4", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-production-api-1-2-xml-5", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-production-api-1-2-xml-6", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-production-api-1-2-xml-7", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-production-api-1-2-xml-8", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-production-api-1-2-xml-9", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-production-api-1-2-xml-x", s3.getBucketName("api-1-2", "xml", "0000-0000-0000-000X"));
        
        assertEquals("bucket-production-api-1-2-json-0", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-production-api-1-2-json-1", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-production-api-1-2-json-2", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-production-api-1-2-json-3", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-production-api-1-2-json-4", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-production-api-1-2-json-5", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-production-api-1-2-json-6", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-production-api-1-2-json-7", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-production-api-1-2-json-8", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-production-api-1-2-json-9", s3.getBucketName("api-1-2", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-production-api-1-2-json-x", s3.getBucketName("api-1-2", "json", "0000-0000-0000-000X"));
        
        assertEquals("bucket-production-api-2-0-xml-0", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-production-api-2-0-xml-1", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-production-api-2-0-xml-2", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-production-api-2-0-xml-3", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0003"));
        assertEquals("bucket-production-api-2-0-xml-4", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-production-api-2-0-xml-5", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-production-api-2-0-xml-6", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-production-api-2-0-xml-7", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-production-api-2-0-xml-8", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-production-api-2-0-xml-9", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-production-api-2-0-xml-x", s3.getBucketName("api-2-0", "xml", "0000-0000-0000-000X"));
        
        assertEquals("bucket-production-api-2-0-json-0", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-production-api-2-0-json-1", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-production-api-2-0-json-2", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-production-api-2-0-json-3", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-production-api-2-0-json-4", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-production-api-2-0-json-5", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-production-api-2-0-json-6", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-production-api-2-0-json-7", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-production-api-2-0-json-8", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-production-api-2-0-json-9", s3.getBucketName("api-2-0", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-production-api-2-0-json-x", s3.getBucketName("api-2-0", "json", "0000-0000-0000-000X"));
        
        assertEquals("bucket-production-api-2-0-activities-xml-0", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0000"));
        assertEquals("bucket-production-api-2-0-activities-xml-1", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0001"));
        assertEquals("bucket-production-api-2-0-activities-xml-2", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0002"));
        assertEquals("bucket-production-api-2-0-activities-xml-3", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0003"));
        assertEquals("bucket-production-api-2-0-activities-xml-4", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0004"));
        assertEquals("bucket-production-api-2-0-activities-xml-5", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0005"));
        assertEquals("bucket-production-api-2-0-activities-xml-6", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0006"));
        assertEquals("bucket-production-api-2-0-activities-xml-7", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0007"));
        assertEquals("bucket-production-api-2-0-activities-xml-8", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0008"));
        assertEquals("bucket-production-api-2-0-activities-xml-9", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-0009"));
        assertEquals("bucket-production-api-2-0-activities-xml-x", s3.getBucketName("api-2-0-activities", "xml", "0000-0000-0000-000X"));
        
        assertEquals("bucket-production-api-2-0-activities-json-0", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0000"));
        assertEquals("bucket-production-api-2-0-activities-json-1", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0001"));
        assertEquals("bucket-production-api-2-0-activities-json-2", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0002"));
        assertEquals("bucket-production-api-2-0-activities-json-3", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0003"));
        assertEquals("bucket-production-api-2-0-activities-json-4", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0004"));
        assertEquals("bucket-production-api-2-0-activities-json-5", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0005"));
        assertEquals("bucket-production-api-2-0-activities-json-6", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0006"));
        assertEquals("bucket-production-api-2-0-activities-json-7", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0007"));
        assertEquals("bucket-production-api-2-0-activities-json-8", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0008"));
        assertEquals("bucket-production-api-2-0-activities-json-9", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-0009"));
        assertEquals("bucket-production-api-2-0-activities-json-x", s3.getBucketName("api-2-0-activities", "json", "0000-0000-0000-000X"));        
    }
    
    @Test
    public void updateS3_OrcidProfileTest() throws JAXBException, AmazonClientException, IOException {        
        String bucketPrefix = "bucket-production";
        String orcid = "0000-0000-0000-000X";
        OrcidMessage om = new OrcidMessage();
        OrcidProfile op = new OrcidProfile();
        op.setOrcidIdentifier(orcid);
        om.setOrcidProfile(op);
        
        S3Updater s3 = new S3Updater(bucketPrefix);
        s3.setS3MessagingService(s3MessagingService);
        s3.updateS3(orcid, om);
        verify(s3MessagingService, times(1)).send(Matchers.eq(bucketPrefix + "-api-1-2-json-x"), Matchers.eq(orcid + ".json"), Matchers.any(), Matchers.any());
        verify(s3MessagingService, times(1)).send(Matchers.eq(bucketPrefix + "-api-1-2-xml-x"), Matchers.eq(orcid + ".xml"), Matchers.any(), Matchers.any());
        verify(s3MessagingService, times(0)).send(Matchers.eq(bucketPrefix + "-api-2-0-json-x"), Matchers.eq(orcid + ".xml"), Matchers.any(), Matchers.any());
        verify(s3MessagingService, times(0)).send(Matchers.eq(bucketPrefix + "-api-2-0-xml-x"), Matchers.eq(orcid + ".xml"), Matchers.any(), Matchers.any());
    }
    
    @Test
    public void updateS3_RecordTest() throws JAXBException, AmazonClientException, IOException {        
        String bucketPrefix = "bucket-production";
        String orcid = "0000-0000-0000-000X";
        Record record = new Record();
        record.setOrcidIdentifier(new OrcidIdentifier(orcid));
        
        S3Updater s3 = new S3Updater(bucketPrefix);
        s3.setS3MessagingService(s3MessagingService);
        s3.updateS3(orcid, record);
        verify(s3MessagingService, times(0)).send(Matchers.eq(bucketPrefix + "-api-1-2-json-x"), Matchers.eq(orcid + ".json"), Matchers.any(), Matchers.any());
        verify(s3MessagingService, times(0)).send(Matchers.eq(bucketPrefix + "-api-1-2-xml-x"), Matchers.eq(orcid + ".xml"), Matchers.any(), Matchers.any());
        verify(s3MessagingService, times(1)).send(Matchers.eq(bucketPrefix + "-api-2-0-json-x"), Matchers.eq(orcid + ".json"), Matchers.any(), Matchers.any());
        verify(s3MessagingService, times(1)).send(Matchers.eq(bucketPrefix + "-api-2-0-xml-x"), Matchers.eq(orcid + ".xml"), Matchers.any(), Matchers.any());
    }
}
