package org.orcid.scheduler.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.scheduler.email.cli.manager.EmailMessageSenderTest;
import org.orcid.scheduler.loader.io.FileRotator;
import org.orcid.scheduler.loader.io.OrgDataClient;
import org.orcid.scheduler.loader.source.LoadSourceDisabledException;
import org.orcid.scheduler.loader.source.fundref.FundrefOrgLoadSource;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-scheduler-context.xml" })
public class FundrefOrgLoadSourceTest {
    
    @Mock
    private FileRotator mockFileRotator;

    @Mock
    private OrgDataClient orgDataClient;
    
    @Mock
    private OrgDisambiguatedDao mockOrgDisambiguatedDao;
    
    @Mock
    private OrgDisambiguatedManager mockOrgDisambiguatedManager;
    
    @Mock
    private JerseyClientHelper mockJerseyClientHelper;
    
    @InjectMocks
    private FundrefOrgLoadSource fundrefOrgLoadSource;
    
    private String localFilePath = "/something/random";
    
    
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "enabled", true);
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "geonamesApiUrl", "https://test/url");
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "localFilePath", localFilePath);
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "userAgent", "userAgent");
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "fundrefDataUrl", "url");
        
        JerseyClientResponse<String, String> response = new JerseyClientResponse<String, String>(200, "test", null);
        when(mockJerseyClientHelper.executeGetRequest(anyString(), anyMap())).thenReturn(response);
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "jerseyClientHelper", mockJerseyClientHelper);
    }

    @Test
    public void testGetSourceName() {
        assertEquals("FUNDREF", fundrefOrgLoadSource.getSourceName());
    }
    
    @Test(expected = LoadSourceDisabledException.class)
    public void testSetDisabled() {
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "enabled", false);
        fundrefOrgLoadSource.loadOrgData();
    }
    
    @Test
    public void testLoadLatestOrgsDownloadFailed() {
        when(orgDataClient.downloadFile(Mockito.eq("url"), Mockito.eq("userAgent"), Mockito.eq(localFilePath))).thenReturn(false);
        assertFalse(fundrefOrgLoadSource.downloadOrgData());
        verify(orgDataClient).downloadFile(Mockito.eq("url"), Mockito.eq("userAgent"), Mockito.eq(localFilePath));
    }
    
    @Test
    public void testDownloadOrgData() {
        when(orgDataClient.downloadFile(Mockito.eq("url"), Mockito.eq("userAgent"), Mockito.eq(localFilePath))).thenReturn(true);
        assertTrue(fundrefOrgLoadSource.downloadOrgData());
        verify(mockFileRotator, Mockito.times(1)).removeFileIfExists(Mockito.eq(localFilePath));
        verify(orgDataClient).downloadFile(Mockito.eq("url"), Mockito.eq("userAgent"), Mockito.eq(localFilePath));
    }
    
    @Test
    public void testLoadLatestOrgsNoExistingOrgs() throws URISyntaxException {
        Path path = Paths.get(FundrefOrgLoadSourceTest.class.getClassLoader().getResource("fundref/fundref-test.rdf").toURI());
        File data = path.toFile();
        assertTrue(data.exists());
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "localFilePath", data.getAbsolutePath());
        
        fundrefOrgLoadSource.loadOrgData();
        
        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedManager, Mockito.times(3)).createOrgDisambiguated(captor.capture());
        
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
        Path path = Paths.get(FundrefOrgLoadSourceTest.class.getClassLoader().getResource("fundref/fundref-test.rdf").toURI());
        File data = path.toFile();
        assertTrue(data.exists());
        ReflectionTestUtils.setField(fundrefOrgLoadSource, "localFilePath", data.getAbsolutePath());
        
        when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.eq("http://dx.doi.org/10.13039/100000001"), Mockito.eq(OrgDisambiguatedSourceType.FUNDREF.name()))).thenReturn(new OrgDisambiguatedEntity());
        when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(Mockito.eq("http://dx.doi.org/10.13039/100000002"), Mockito.eq(OrgDisambiguatedSourceType.FUNDREF.name()))).thenReturn(new OrgDisambiguatedEntity());
        
        fundrefOrgLoadSource.loadOrgData();
        
        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedManager, Mockito.times(2)).updateOrgDisambiguated(captor.capture()); // two existing orgs updated
        
        List<OrgDisambiguatedEntity> entities = captor.getAllValues();
        assertEquals(2, entities.size());
        assertEquals("http://dx.doi.org/10.13039/100000001", entities.get(0).getSourceId());
        assertEquals("FUNDREF", entities.get(0).getSourceType());
        assertEquals("http://dx.doi.org/10.13039/100000002", entities.get(1).getSourceId());
        assertEquals("FUNDREF", entities.get(1).getSourceType());

        captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedManager, Mockito.times(1)).createOrgDisambiguated(captor.capture()); // one new org created
        
        entities = captor.getAllValues();
        assertEquals(1, entities.size());
        assertEquals("http://dx.doi.org/10.13039/100000003", entities.get(0).getSourceId());
        assertEquals("FUNDREF", entities.get(0).getSourceType());
    }        
}

