package org.orcid.listener.s3;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

public class S3MessagingServiceTest {
    
    private S3MessagingService s3MessagingService;
    
    @Before
    public void before() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, JAXBException {
        s3MessagingService = new S3MessagingService("v2Summaries", "v2Activites","v3Summaries","v3Activites", false);
    }
    
    @Test
    public void getV2ActivitiesBucketNameTest() {
        assertEquals("v2Activites", s3MessagingService.getV2ActivitiesBucketName());
    }
    
    @Test
    public void getV3ActivitiesBucketNameTest() {
        assertEquals("v3Activites-a", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0000"));
        assertEquals("v3Activites-a", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0001"));
        assertEquals("v3Activites-a", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0002"));
        assertEquals("v3Activites-a", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0003"));
        assertEquals("v3Activites-b", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0004"));
        assertEquals("v3Activites-b", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0005"));
        assertEquals("v3Activites-b", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0006"));
        assertEquals("v3Activites-b", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0007"));
        assertEquals("v3Activites-c", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0008"));
        assertEquals("v3Activites-c", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-0009"));
        assertEquals("v3Activites-c", s3MessagingService.getV3ActivitiesBucketName("0000-0000-0000-000X"));
    }
}
