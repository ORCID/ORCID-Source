package org.orcid.core.issn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
    public void testGetIssnDataUseKeyTitle() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamKeyTitle());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("1755-4349");
        assertEquals("Nature chemistry (Online) - Key Title", data.getMainTitle());
        assertEquals("1755-4349", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataUseMainTitle() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamMainTitle());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("0260-8774");
        assertEquals("Journal of food engineering - Main title", data.getMainTitle());
        assertEquals("0260-8774", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataUseNameArray() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamNameArray());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("0260-8774");
        assertEquals("Journal of food engineering - Array", data.getMainTitle());
        assertEquals("0260-8774", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataUseNameArrayAsString() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamNameArrayAsString());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("0260-8774");
        assertEquals("Journal of food engineering - String", data.getMainTitle());
        assertEquals("0260-8774", data.getIssn());
    }
    
    @Test
    public void testGetIssnDataNoTitle() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamNoTitle());
        when(mockResponse.statusCode()).thenReturn(200);
                
        IssnData data = issnClient.getIssnData("0260-8774");
        assertNull(data);        
    }
    
    @Test
    public void testGetIssnDataBadCharactersKeyTitle() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamBadCharactersKeyTitle());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("0021-9525");
        assertFalse("\u0098The \u009CJournal of cell biology - Key Title".equals(data.getMainTitle()));
        assertEquals("The Journal of cell biology - Key Title", data.getMainTitle());
        assertTrue("\u0098The \u009CJournal of cell biology - Key Title".getBytes().length != data.getMainTitle().getBytes().length);
    }

    @Test
    public void testGetIssnDataBadCharactersMainTitle() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamBadCharactersMainTitle());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("0021-9525");
        assertFalse("\u0098The \u009CJournal of cell biology - Main Title".equals(data.getMainTitle()));
        assertEquals("The Journal of cell biology - Main Title", data.getMainTitle());
        assertTrue("\u0098The \u009CJournal of cell biology - Main Title".getBytes().length != data.getMainTitle().getBytes().length);
    }
    
    @Test
    public void testGetIssnDataBadCharactersNameArray() throws IOException, JSONException, InterruptedException, URISyntaxException {
        when(mockHttpRequestUtils.doGet(any())).thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(getJsonInputStreamBadCharactersNameArray());
        when(mockResponse.statusCode()).thenReturn(200);
        
        IssnData data = issnClient.getIssnData("0021-9525");
        assertFalse("\u0098The \u009CJournal of cell biology - Name Array".equals(data.getMainTitle()));
        assertEquals("The Journal of cell biology - Name Array", data.getMainTitle());
        assertTrue("\u0098The \u009CJournal of cell biology - Name Array".getBytes().length != data.getMainTitle().getBytes().length);
    }        
    
    private String getJsonInputStreamKeyTitle() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-use-key-title.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamMainTitle() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-use-main-title.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamNameArray() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-use-name-array.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamNameArrayAsString() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-use-name-array-as-string.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }    
    
    private String getJsonInputStreamNoTitle() throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-no-title.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamBadCharactersKeyTitle()  throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-bad-characters-key-title.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
    
    private String getJsonInputStreamBadCharactersMainTitle()  throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-bad-characters-main-title.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }   
    
    private String getJsonInputStreamBadCharactersNameArray()  throws IOException {
        InputStream is = getClass().getResourceAsStream("/issn-response-bad-characters-name-array.json");
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }   
}
