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
package org.orcid.activitiesindexer.s3;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.activitiesindexer.s3.S3Manager;
import org.orcid.activitiesindexer.s3.S3MessagingService;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-activities-indexer-test-context.xml" })
public class S3UpdaterTest {

    @Mock
    private S3MessagingService s3MessagingService;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void getBucketChecksumTest() throws JAXBException {
        S3Manager s3 = new S3Manager("bucket-production");
      
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
    
}
