package org.orcid.core.orgs.load.source.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.core.orgs.load.io.FileRotator;
import org.orcid.core.orgs.load.io.HttpFileDownloader;
import org.orcid.core.orgs.load.source.LoadSourceDisabledException;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class FundrefOrgLoadSourceTest {
    
    @Mock
    private FileRotator mockFileRotator;

    @Mock
    private HttpFileDownloader mockHttpFileDownloader;
    
    @Mock
    private OrgDisambiguatedDao mockOrgDisambiguatedDao;
    
    @InjectMocks
    private FundrefOrgLoadSource fundrefOrgLoadSource;
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        fundrefOrgLoadSource.setEnabled(true);
        
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "geoNamesApiClient", getMockedGeoNamesClientWithSuccessfulResponse());
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "geonamesApiUrl", "https://test/url");
    }

    @Test
    public void testGetSourceName() {
        assertEquals("FUNDREF", fundrefOrgLoadSource.getSourceName());
    }
    
    @Test(expected = LoadSourceDisabledException.class)
    public void testSetDisabled() {
        fundrefOrgLoadSource.setEnabled(false);
        fundrefOrgLoadSource.loadLatestOrgs();
    }
    
    @Test
    public void testLoadLatestOrgsDownloadFailed() {
        when(mockHttpFileDownloader.getLocalFilePath()).thenReturn("/something/random");
        when(mockHttpFileDownloader.downloadFile()).thenReturn(false);
        
        assertFalse(fundrefOrgLoadSource.loadLatestOrgs());
        
        verify(mockHttpFileDownloader).getLocalFilePath();
        verify(mockHttpFileDownloader).downloadFile();
    }
    
    @Test
    public void testLoadLatestOrgsNoExistingOrgs() throws URISyntaxException {
        Path path = Paths.get(getClass().getResource("/fundref/fundref-test.rdf").toURI());
        File data = path.toFile();
        assertTrue(data.exists());
        
        when(mockHttpFileDownloader.getLocalFilePath()).thenReturn(data.getAbsolutePath());
        when(mockHttpFileDownloader.downloadFile()).thenReturn(true);
        
        fundrefOrgLoadSource.loadLatestOrgs();
        
        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedDao, Mockito.times(3)).persist(captor.capture());
        
        List<OrgDisambiguatedEntity> entities = captor.getAllValues();
        assertEquals(3, entities.size());
        assertEquals("http://dx.doi.org/10.13039/100000001", entities.get(0).getSourceId());
        assertEquals("FUNDREF", entities.get(0).getSourceType());
        assertEquals("http://dx.doi.org/10.13039/100000002", entities.get(1).getSourceId());
        assertEquals("FUNDREF", entities.get(1).getSourceType());
        assertEquals("http://dx.doi.org/10.13039/100000003", entities.get(2).getSourceId());
        assertEquals("FUNDREF", entities.get(2).getSourceType());
    }
    
    @Test
    public void testLoadLatestOrgsTwoExistingOrgs() throws URISyntaxException {
        Path path = Paths.get(getClass().getResource("/fundref/fundref-test.rdf").toURI());
        File data = path.toFile();
        assertTrue(data.exists());
        
        when(mockHttpFileDownloader.getLocalFilePath()).thenReturn(data.getAbsolutePath());
        when(mockHttpFileDownloader.downloadFile()).thenReturn(true);
        
        when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.eq("http://dx.doi.org/10.13039/100000001"), Mockito.eq(OrgDisambiguatedSourceType.FUNDREF.name()))).thenReturn(new OrgDisambiguatedEntity());
        when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.eq("http://dx.doi.org/10.13039/100000002"), Mockito.eq(OrgDisambiguatedSourceType.FUNDREF.name()))).thenReturn(new OrgDisambiguatedEntity());
        
        fundrefOrgLoadSource.loadLatestOrgs();
        
        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedDao, Mockito.times(2)).merge(captor.capture()); // two existing orgs updated
        
        List<OrgDisambiguatedEntity> entities = captor.getAllValues();
        assertEquals(2, entities.size());
        assertEquals("http://dx.doi.org/10.13039/100000001", entities.get(0).getSourceId());
        assertEquals("FUNDREF", entities.get(0).getSourceType());
        assertEquals("http://dx.doi.org/10.13039/100000002", entities.get(1).getSourceId());
        assertEquals("FUNDREF", entities.get(1).getSourceType());

        captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedDao, Mockito.times(1)).persist(captor.capture()); // one new org created
        
        entities = captor.getAllValues();
        assertEquals(1, entities.size());
        assertEquals("http://dx.doi.org/10.13039/100000003", entities.get(0).getSourceId());
        assertEquals("FUNDREF", entities.get(0).getSourceType());
    }
    
    @SuppressWarnings("unchecked")
    private Client getMockedGeoNamesClientWithSuccessfulResponse() throws IOException {
        Client client = Mockito.mock(Client.class);
        WebResource webResource = Mockito.mock(WebResource.class);
        ClientResponse response = Mockito.mock(ClientResponse.class);
        
        Mockito.when(client.resource(Mockito.anyString())).thenReturn(webResource);
        Mockito.when(webResource.queryParams(Mockito.any(MultivaluedMap.class))).thenReturn(webResource);
        Mockito.when(webResource.get(Mockito.eq(ClientResponse.class))).thenReturn(response);
        Mockito.when(response.getStatus()).thenReturn(200);
        Mockito.when(response.getEntity(Mockito.eq(String.class))).thenReturn("test");
        
        return client;
    }
}

