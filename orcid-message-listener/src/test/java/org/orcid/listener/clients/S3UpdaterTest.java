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
package org.orcid.listener.clients;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.fasterxml.jackson.core.JsonProcessingException;

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
        S3Updater s3 = new S3Updater("bucket-qa");
        assertEquals("all", s3.getBucketCheckSum("0000-0000-0000-0000"));
        s3 = new S3Updater("bucket-sandbox");
        assertEquals("all", s3.getBucketCheckSum("0000-0000-0000-0000"));
        s3 = new S3Updater("bucket-production");
        assertEquals("0", s3.getBucketCheckSum("0000-0000-0000-0000"));
        assertEquals("1", s3.getBucketCheckSum("0000-0000-0000-0001"));
        assertEquals("x", s3.getBucketCheckSum("0000-0000-0000-000X"));
    }
    
    @Test
    public void updateS3_OrcidProfileTest() throws JAXBException, JsonProcessingException, AmazonClientException {        
        String bucketPrefix = "bucket-production";
        String orcid = "0000-0000-0000-000X";
        OrcidMessage om = new OrcidMessage();
        OrcidProfile op = new OrcidProfile();
        op.setOrcidIdentifier(orcid);
        om.setOrcidProfile(op);
        
        S3Updater s3 = new S3Updater(bucketPrefix);
        s3.setS3MessagingService(s3MessagingService);
        s3.updateS3(orcid, om);
        verify(s3MessagingService, times(1)).send(Matchers.eq(bucketPrefix + "-api-1-2-json-x"), Matchers.eq(orcid + ".json"), Matchers.anyString());
        verify(s3MessagingService, times(1)).send(Matchers.eq(bucketPrefix + "-api-1-2-xml-x"), Matchers.eq(orcid + ".xml"), Matchers.anyString());
        verify(s3MessagingService, times(0)).send(Matchers.eq(bucketPrefix + "-api-2-0-json-x"), Matchers.eq(orcid + ".xml"), Matchers.anyString());
        verify(s3MessagingService, times(0)).send(Matchers.eq(bucketPrefix + "-api-2-0-xml-x"), Matchers.eq(orcid + ".xml"), Matchers.anyString());
    }
    
    @Test
    public void updateS3_RecordTest() throws JAXBException, JsonProcessingException, AmazonClientException {        
        String bucketPrefix = "bucket-production";
        String orcid = "0000-0000-0000-000X";
        Record record = new Record();
        record.setOrcidIdentifier(new OrcidIdentifier(orcid));
        
        S3Updater s3 = new S3Updater(bucketPrefix);
        s3.setS3MessagingService(s3MessagingService);
        s3.updateS3(orcid, record);
        verify(s3MessagingService, times(0)).send(Matchers.eq(bucketPrefix + "-api-1-2-json-x"), Matchers.eq(orcid + ".json"), Matchers.anyString());
        verify(s3MessagingService, times(0)).send(Matchers.eq(bucketPrefix + "-api-1-2-xml-x"), Matchers.eq(orcid + ".xml"), Matchers.anyString());
        verify(s3MessagingService, times(1)).send(Matchers.eq(bucketPrefix + "-api-2-0-json-x"), Matchers.eq(orcid + ".json"), Matchers.anyString());
        verify(s3MessagingService, times(1)).send(Matchers.eq(bucketPrefix + "-api-2-0-xml-x"), Matchers.eq(orcid + ".xml"), Matchers.anyString());
    }
}
