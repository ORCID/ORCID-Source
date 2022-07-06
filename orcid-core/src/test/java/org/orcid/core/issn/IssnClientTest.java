package org.orcid.core.issn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.groupIds.issn.IssnClient;
import org.orcid.core.groupIds.issn.IssnData;
import org.orcid.core.groupIds.issn.IssnPortalUrlBuilder;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.springframework.test.util.ReflectionTestUtils;

public class IssnClientTest {
    
    private IssnClient issnClient = new IssnClient();
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        IssnPortalUrlBuilder mockUrlBuilder = Mockito.mock(IssnPortalUrlBuilder.class);
        Mockito.when(mockUrlBuilder.buildJsonIssnPortalUrlForIssn(Mockito.anyString())).thenReturn("anything");
        ReflectionTestUtils.setField(issnClient, "issnPortalUrlBuilder", mockUrlBuilder);
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
    
    @Test
    public void testGetIssnDataBadCharacters() throws IOException, JSONException {
        ReflectionTestUtils.setField(issnClient, "client", getMockedClient(getJsonInputStreamBadCharacters()));
        IssnData data = issnClient.getIssnData("doesn't matter");
        assertFalse("\u0098The \u009CJournal of cell biology.".equals(data.getMainTitle()));
        assertEquals("The Journal of cell biology.", data.getMainTitle());
        assertTrue("\u0098The \u009CJournal of cell biology.".getBytes().length != data.getMainTitle().getBytes().length);
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
    
    private InputStream getJsonInputStreamBadCharacters()  throws IOException {
        return getClass().getResourceAsStream("/issn-response-bad-characters.json");
    }
    
    private JerseyClientHelper getMockedClient(InputStream inputStream) throws IOException {
        JerseyClientHelper jerseyClientHelper = Mockito.mock(JerseyClientHelper.class);  
        Mockito.when(jerseyClientHelper.executeGetRequest(any(), eq(InputStream.class), eq(String.class))).thenReturn(new JerseyClientResponse(200, inputStream, null));
        return jerseyClientHelper;
    }

}
