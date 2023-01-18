package org.orcid.scheduler.loader.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-scheduler-context.xml" })
public class OrgDataClientTest {
    
    @InjectMocks
    private OrgDataClient orgDataClient;
    
    @Mock
    private JerseyClientHelper jerseyClientHelper;
    
    private File tempFile; 

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        tempFile = File.createTempFile("fundref-test-", ".rdf");        
        ReflectionTestUtils.setField(orgDataClient, "jerseyClientHelperForOrgLoaders", jerseyClientHelper);
    }
    
    @After
    public void tearDown() throws IOException {
        if (!tempFile.delete()) {
            throw new IOException("Failed to delete test file " + tempFile.getAbsolutePath());
        }
    }
    
    @Test
    public void testDownloadFileSuccess() throws IOException {
        setMockedClientWithSuccessfulResponse(getClass().getResourceAsStream("/fundref/fundref-test.rdf"));        
        assertTrue(orgDataClient.downloadFile("url", "userAgent", tempFile.getAbsolutePath()));
    }
    
    @Test
    public void testDownloadFileFailure() throws IOException {
        JerseyClientResponse<InputStream, String> response = new JerseyClientResponse<InputStream, String>(403, null, null);
        when(jerseyClientHelper.executeGetRequest(anyString(), isNull(), isNull(), eq(false), anyMap(), anyMap(), eq(InputStream.class), eq(String.class))).thenReturn(response);
        assertFalse(orgDataClient.downloadFile("url", "userAgent", tempFile.getAbsolutePath()));
    }
    
    @Test
    public void testGetFailure() throws IOException {
        setMockedClientWithFailureResponse();
        assertNull(orgDataClient.get("url", "userAgent", String.class));
    }
    
    @Test
    public void testGet() throws IOException {
        setMockedClientWithSuccessfulEntityResponse();
        assertEquals("success", orgDataClient.get("url", "userAgent", String.class));
    }
    
    private void setMockedClientWithSuccessfulResponse(InputStream inputStream) throws IOException {
        JerseyClientResponse<InputStream, String> response = new JerseyClientResponse<InputStream, String>(200, inputStream, null);
        when(jerseyClientHelper.executeGetRequest(anyString(), isNull(), isNull(), eq(false), anyMap(), anyMap(), eq(InputStream.class), eq(String.class))).thenReturn(response);        
    }
    
    private void setMockedClientWithFailureResponse() throws IOException {
        JerseyClientResponse<String, String> response = new JerseyClientResponse<String, String>(403, null, null);
        when(jerseyClientHelper.executeGetRequest(anyString(), isNull(), isNull(), eq(false), anyMap(), anyMap(), eq(String.class), eq(String.class))).thenReturn(response);
    }
        
    private void setMockedClientWithSuccessfulEntityResponse() throws IOException {
        JerseyClientResponse<String, String> response = new JerseyClientResponse<String, String>(200, "success", null);
        when(jerseyClientHelper.executeGetRequest(anyString(), isNull(), isNull(), eq(false), anyMap(), anyMap(), eq(String.class), eq(String.class))).thenReturn(response);
    }
    
}
