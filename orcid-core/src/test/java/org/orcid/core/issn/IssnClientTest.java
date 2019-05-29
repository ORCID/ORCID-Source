package org.orcid.core.issn;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.orcid.core.issn.client.IssnClient;
import org.springframework.test.util.ReflectionTestUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class IssnClientTest {
    
    private IssnClient issnClient = new IssnClient();
    
    @Before
    public void setUp() throws IOException {
        ReflectionTestUtils.setField(issnClient, "url", "anything");
    }
    
    @Test
    public void testGetIssnData() throws IOException, JSONException {
        ReflectionTestUtils.setField(issnClient, "client", getMockedClient(getJsonInputStream()));
        IssnData data = issnClient.getIssnData("doesn't matter");
        assertEquals("Nature chemistry.", data.getMainTitle());
        assertEquals("1755-4349", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataNoMainTitle() throws IOException, JSONException {
        ReflectionTestUtils.setField(issnClient, "client", getMockedClient(getJsonInputStreamNoMainTitle()));
        IssnData data = issnClient.getIssnData("doesn't matter");
        assertEquals("Journal of food engineering", data.getMainTitle());
        assertEquals("0260-8774", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataNoMainTitleNameArray() throws IOException, JSONException {
        ReflectionTestUtils.setField(issnClient, "client", getMockedClient(getJsonInputStreamNoMainTitleNameArray()));
        IssnData data = issnClient.getIssnData("doesn't matter");
        assertEquals("Shalom (Glyvrar)", data.getMainTitle());
        assertEquals("0906-8724", data.getIssn());
    }

    private InputStream getJsonInputStream() throws IOException {
        return getClass().getResourceAsStream("/issn-response.json");
    }
    
    private InputStream getJsonInputStreamNoMainTitle() throws IOException {
        return getClass().getResourceAsStream("/issn-response-no-mainTitle.json");
    }
    
    private InputStream getJsonInputStreamNoMainTitleNameArray() throws IOException {
        return getClass().getResourceAsStream("/issn-response-no-mainTitle-name-array.json");
    }
    
    private Client getMockedClient(InputStream inputStream) throws IOException {
        Client client = Mockito.mock(Client.class);
        WebResource webResource = Mockito.mock(WebResource.class);
        ClientResponse response = Mockito.mock(ClientResponse.class);
        
        Mockito.when(client.resource(Mockito.anyString())).thenReturn(webResource);
        Mockito.when(webResource.get(Mockito.eq(ClientResponse.class))).thenReturn(response);
        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.getEntityInputStream()).thenReturn(inputStream);
        
        return client;
    }

}
