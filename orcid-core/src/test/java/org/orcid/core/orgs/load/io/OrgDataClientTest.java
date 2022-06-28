package org.orcid.core.orgs.load.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

public class OrgDataClientTest {
    
    @InjectMocks
    private OrgDataClient orgDataClient;
    
    @Mock
    private Response mockResponse;
    
    @Mock
    private WebTarget mockWebTarget; 
    
    @Mock
    private Builder mockBuilder;
    
    @Mock
    protected Client mockJerseyClient;
    
    private File tempFile; 

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        tempFile = File.createTempFile("fundref-test-", ".rdf");        
    }
    
    @After
    public void tearDown() throws IOException {
        if (!tempFile.delete()) {
            throw new IOException("Failed to delete test file " + tempFile.getAbsolutePath());
        }
    }
    
    @Test
    public void testDownloadFileSuccess() throws IOException {
        setMocks(getClass().getResourceAsStream("/fundref/fundref-test.rdf"));
        assertTrue(orgDataClient.downloadFile("url", tempFile.getAbsolutePath()));
    }
    
    @Test
    public void testDownloadFileFailure() throws IOException {
        setMockedClientWithFailureResponse();
        assertFalse(orgDataClient.downloadFile("url", tempFile.getAbsolutePath()));
    }
    
    @Test
    public void testGetFailure() throws IOException {
        setMockedClientWithFailureResponse();
        assertNull(orgDataClient.get("url", "user-agent", new GenericType<String>() {}));
    }
    
    @Test
    public void testGetSuccess() throws IOException {
        setMockedClientWithSuccessfulEntityResponse();
        assertEquals("success", orgDataClient.get("url",  "user-agent", new GenericType<String>() {}));
    }
    
    private void initJerseyMocks() {
        when(mockJerseyClient.target(anyString())).thenReturn(mockWebTarget);
        when(mockWebTarget.request()).thenReturn(mockBuilder);
        when(mockBuilder.header(anyString(), any())).thenReturn(mockBuilder);
        when(mockBuilder.get(eq(Response.class))).thenReturn(mockResponse);        
    }
    
    private void setMocks(InputStream inputStream) throws IOException {
        initJerseyMocks();
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.readEntity(InputStream.class)).thenReturn(inputStream);
        ReflectionTestUtils.setField(orgDataClient, "jerseyClient", mockJerseyClient);
    }
        
    private void setMockedClientWithFailureResponse() throws IOException {
        initJerseyMocks();
        when(mockResponse.getStatus()).thenReturn(403);
        ReflectionTestUtils.setField(orgDataClient, "jerseyClient", mockJerseyClient);
    }
    
    @SuppressWarnings("unchecked")
    private void setMockedClientWithSuccessfulEntityResponse() throws IOException {        
        initJerseyMocks();
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.readEntity(any(GenericType.class))).thenReturn("success");  
        ReflectionTestUtils.setField(orgDataClient, "jerseyClient", mockJerseyClient);
    }
    
}
