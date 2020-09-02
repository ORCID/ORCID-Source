package org.orcid.core.orgs.load.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class HttpFileDownloaderTest {
    
    @InjectMocks
    private HttpFileDownloader downloader;
    
    private File tempFile; 

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(downloader, "userAgent", "java");
        
        tempFile = File.createTempFile("fundref-test-", ".rdf");
        downloader.setLocalFilePath(tempFile.getAbsolutePath());
        downloader.setUrl("url");
    }
    
    @After
    public void tearDown() throws IOException {
        if (!tempFile.delete()) {
            throw new IOException("Failed to delete test file " + tempFile.getAbsolutePath());
        }
    }
    
    @Test
    public void testDownloadFileSuccess() throws IOException {
        ReflectionTestUtils.setField(downloader, "client", getMockedClientWithSuccessfulResponse(getClass().getResourceAsStream("/fundref/fundref-test.rdf")));
        assertTrue(downloader.downloadFile());
    }
    
    @Test
    public void testDownloadFileFailure() throws IOException {
        ReflectionTestUtils.setField(downloader, "client", getMockedClientWithFailureResponse());
        assertFalse(downloader.downloadFile());
    }
    
    private Client getMockedClientWithSuccessfulResponse(InputStream inputStream) throws IOException {
        Client client = Mockito.mock(Client.class);
        WebResource webResource = Mockito.mock(WebResource.class);
        ClientResponse response = Mockito.mock(ClientResponse.class);
        WebResource.Builder builder = Mockito.mock(WebResource.Builder.class);
        
        Mockito.when(client.resource(Mockito.anyString())).thenReturn(webResource);
        Mockito.when(webResource.header(Mockito.anyString(), Mockito.anyString())).thenReturn(builder);
        Mockito.when(builder.get(Mockito.eq(ClientResponse.class))).thenReturn(response);
        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.getEntityInputStream()).thenReturn(inputStream);
        
        return client;
    }
    
    private Client getMockedClientWithFailureResponse() throws IOException {
        Client client = Mockito.mock(Client.class);
        WebResource webResource = Mockito.mock(WebResource.class);
        ClientResponse response = Mockito.mock(ClientResponse.class);
        WebResource.Builder builder = Mockito.mock(WebResource.Builder.class);
        
        Mockito.when(client.resource(Mockito.anyString())).thenReturn(webResource);
        Mockito.when(webResource.header(Mockito.anyString(), Mockito.anyString())).thenReturn(builder);
        Mockito.when(builder.get(Mockito.eq(ClientResponse.class))).thenReturn(response);
        Mockito.when(response.getStatus()).thenReturn(403);
        return client;
    }
    
}
