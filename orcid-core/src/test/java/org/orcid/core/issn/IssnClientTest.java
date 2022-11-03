package org.orcid.core.issn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.groupIds.issn.IssnClient;
import org.orcid.core.groupIds.issn.IssnData;
import org.orcid.core.groupIds.issn.IssnPortalUrlBuilder;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.springframework.test.util.ReflectionTestUtils;

public class IssnClientTest {
    
    private IssnClient issnClient = new IssnClient();
    
    @Mock
    private HttpRequestUtils mockHttpRequestUtils;
    
    @Mock
    private HttpResponse<String> mockResponse;
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        IssnPortalUrlBuilder mockUrlBuilder = Mockito.mock(IssnPortalUrlBuilder.class);
        Mockito.when(mockUrlBuilder.buildJsonIssnPortalUrlForIssn(Mockito.anyString())).thenReturn("anything");
        ReflectionTestUtils.setField(issnClient, "issnPortalUrlBuilder", mockUrlBuilder);
        ReflectionTestUtils.setField(issnClient, "httpRequestUtils", mockHttpRequestUtils);
    }
    
    @Test
    public void testGetIssnData() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStream());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("my-issn-0");
        assertEquals("Nature chemistry.", data.getMainTitle());
        // Should ignore ISSN from the metadata
        assertEquals("my-issn-0", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataWhenNoIssnInMetadata() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamNoIssnInMetadata());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("my-issn-1");
        assertEquals("Nature chemistry.", data.getMainTitle());
        // Should ignore ISSN from the metadata
        assertEquals("my-issn-1", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataNoMainTitle() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamNoMainTitle());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("my-issn-2");
        assertEquals("Journal of food engineering", data.getMainTitle());
        // Should ignore ISSN from the metadata
        assertEquals("my-issn-2", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataNoMainTitleNameArray() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamNoMainTitleNameArray());
        when(mockResponse.statusCode()).thenReturn(200);
                
        IssnData data = issnClient.getIssnData("my-issn-3");
        assertEquals("Shalom (Glyvrar)", data.getMainTitle());
        // Should ignore ISSN from the metadata
        assertEquals("my-issn-3", data.getIssn());        
    }
    
    @Test
    public void testGetIssnDataBadCharacters() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamBadCharacters());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("my-issn-2");
        assertFalse("\u0098The \u009CJournal of cell biology.".equals(data.getMainTitle()));
        assertEquals("The Journal of cell biology.", data.getMainTitle());
        assertTrue("\u0098The \u009CJournal of cell biology.".getBytes().length != data.getMainTitle().getBytes().length);
    }

    private String getJsonInputStream() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamNoIssnInMetadata() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-no-issn-in-metadata.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamNoMainTitle() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-no-mainTitle.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamNoMainTitleNameArray() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-no-mainTitle-name-array.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamBadCharacters()  throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-bad-characters.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }        
}
