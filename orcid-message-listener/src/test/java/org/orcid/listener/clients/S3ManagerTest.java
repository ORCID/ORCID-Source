package org.orcid.listener.clients;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.record_v2.History;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.listener.s3.S3Manager;
import org.orcid.listener.s3.S3MessagingService;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

import com.amazonaws.AmazonClientException;
import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class S3ManagerTest {

    @Mock
    private S3MessagingService s3MessagingService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getBucketChecksumTest() throws JAXBException {
        S3Manager s3 = new S3Manager("bucket-dev");
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

        s3 = new S3Manager("bucket-qa");
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

        s3 = new S3Manager("bucket-sandbox");
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

        s3 = new S3Manager("bucket-production");
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

        S3Manager s3 = new S3Manager(bucketPrefix);
        s3.setS3MessagingService(s3MessagingService);
        s3.updateS3(orcid, om);
        verify(s3MessagingService, times(1)).send(eq(bucketPrefix + "-api-1-2-json-x"), eq(orcid + ".json"), any(), any());
        verify(s3MessagingService, times(1)).send(eq(bucketPrefix + "-api-1-2-xml-x"), eq(orcid + ".xml"), any(), any());
        verify(s3MessagingService, times(0)).send(eq(bucketPrefix + "-api-2-0-json-x"), eq(orcid + ".xml"), any(), any());
        verify(s3MessagingService, times(0)).send(eq(bucketPrefix + "-api-2-0-xml-x"), eq(orcid + ".xml"), any(), any());
    }

    @Test
    public void updateS3_RecordTest() throws JAXBException, AmazonClientException, IOException {
        String bucketPrefix = "bucket-production";
        String orcid = "0000-0000-0000-000X";
        Record record = new Record();
        record.setOrcidIdentifier(new OrcidIdentifier(orcid));

        S3Manager s3 = new S3Manager(bucketPrefix);
        s3.setS3MessagingService(s3MessagingService);
        s3.updateS3(orcid, record);
        verify(s3MessagingService, times(0)).send(eq(bucketPrefix + "-api-1-2-json-x"), eq(orcid + ".json"), any(), any());
        verify(s3MessagingService, times(0)).send(eq(bucketPrefix + "-api-1-2-xml-x"), eq(orcid + ".xml"), any(), any());
        verify(s3MessagingService, times(1)).send(eq(bucketPrefix + "-api-2-0-json-x"), eq(orcid + ".json"), any(), any());
        verify(s3MessagingService, times(1)).send(eq(bucketPrefix + "-api-2-0-xml-x"), eq(orcid + ".xml"), any(), any());
    }

    @Test
    public void uploadRecordSummaryTest() throws JAXBException, JsonProcessingException {
        String orcid = "0000-0000-0000-0000";
        Date now = new Date();
        LastModifiedDate lmd = new LastModifiedDate();
        lmd.setValue(DateUtils.convertToXMLGregorianCalendar(now));
        Record r = new Record();
        History h = new History();
        h.setLastModifiedDate(lmd);
        r.setHistory(h);
        S3Manager s3 = new S3Manager();
        s3.setS3MessagingService(s3MessagingService);
        s3.uploadRecordSummary(orcid, r);
        verify(s3MessagingService, times(1)).send(eq("000/0000-0000-0000-0000.xml"), any(byte[].class), eq(MediaType.APPLICATION_XML), eq(now), eq(false));
    }

    @Test
    public void uploadActivityTest() throws JAXBException, JsonProcessingException {
        String orcid = "0000-0000-0000-0000";
        Date now = new Date();
        LastModifiedDate lmd = new LastModifiedDate();
        lmd.setValue(DateUtils.convertToXMLGregorianCalendar(now));
        Work w = new Work();
        w.setLastModifiedDate(lmd);

        S3Manager s3 = new S3Manager();
        s3.setS3MessagingService(s3MessagingService);
        s3.uploadActivity(orcid, "1234", w);
        verify(s3MessagingService, times(1)).send(eq("000/0000-0000-0000-0000/works/0000-0000-0000-0000_works_1234.xml"), any(byte[].class), eq(MediaType.APPLICATION_XML),
                eq(now), eq(true));
    }

    @Test
    public void uploadOrcidErrorTest() throws JAXBException, JsonProcessingException {
        String orcid = "0000-0000-0000-0000";
        OrcidError error = new OrcidError();
        S3Manager s3 = new S3Manager();
        s3.setS3MessagingService(s3MessagingService);
        s3.uploadOrcidError(orcid, error);

        verify(s3MessagingService, times(1)).send(eq("000/0000-0000-0000-0000.xml"), any(byte[].class), eq(MediaType.APPLICATION_XML), any(Date.class), eq(false));
    }
}
