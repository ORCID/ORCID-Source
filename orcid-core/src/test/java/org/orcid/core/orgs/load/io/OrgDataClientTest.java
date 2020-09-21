package org.orcid.core.orgs.load.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class OrgDataClientTest {
    
    @InjectMocks
    private OrgDataClient orgDataClient;
    
    private File tempFile; 

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
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
        ReflectionTestUtils.setField(orgDataClient, "client", getMockedClientWithSuccessfulResponse(getClass().getResourceAsStream("/fundref/fundref-test.rdf")));
        assertTrue(orgDataClient.downloadFile("url", "userAgent", tempFile.getAbsolutePath()));
    }
    
    @Test
    public void testDownloadFileFailure() throws IOException {
        ReflectionTestUtils.setField(orgDataClient, "client", getMockedClientWithFailureResponse());
        assertFalse(orgDataClient.downloadFile("url", "userAgent", tempFile.getAbsolutePath()));
    }
    
    @Test
    public void testGetFailure() throws IOException {
        ReflectionTestUtils.setField(orgDataClient, "client", getMockedClientWithFailureResponse());
        assertNull(orgDataClient.get("url", "userAgent", new GenericType<String>() {}));
    }
    
    @Test
    public void testGet() throws IOException {
        ReflectionTestUtils.setField(orgDataClient, "client", getMockedClientWithSuccessfulEntityResponse());
        assertEquals("success", orgDataClient.get("url", "userAgent", new GenericType<String>() {}));
    }
    
    @SuppressWarnings("resource")
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
    
    @SuppressWarnings("unchecked")
    private Client getMockedClientWithSuccessfulEntityResponse() throws IOException {
        Client client = Mockito.mock(Client.class);
        WebResource webResource = Mockito.mock(WebResource.class);
        ClientResponse response = Mockito.mock(ClientResponse.class);
        WebResource.Builder builder = Mockito.mock(WebResource.Builder.class);
        
        Mockito.when(client.resource(Mockito.eq("url"))).thenReturn(webResource);
        Mockito.when(webResource.header(Mockito.eq("User-Agent"), Mockito.eq("userAgent"))).thenReturn(builder);
        Mockito.when(builder.get(Mockito.eq(ClientResponse.class))).thenReturn(response);
        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.getEntity(Mockito.any(GenericType.class))).thenReturn("success");
        
        return client;
    }
    
}
