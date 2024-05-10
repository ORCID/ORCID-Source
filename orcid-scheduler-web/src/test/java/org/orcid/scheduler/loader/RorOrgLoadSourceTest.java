package org.orcid.scheduler.loader;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.OrgDisambiguatedManager;
import org.orcid.core.orgs.OrgDisambiguatedSourceType;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.scheduler.loader.io.FileRotator;
import org.orcid.scheduler.loader.io.OrgDataClient;
import org.orcid.scheduler.loader.source.LoadSourceDisabledException;
import org.orcid.scheduler.loader.source.fighshare.api.FigshareCollectionArticleDetails;
import org.orcid.scheduler.loader.source.fighshare.api.FigshareCollectionArticleFile;
import org.orcid.scheduler.loader.source.fighshare.api.FigshareCollectionArticleSummary;
import org.orcid.scheduler.loader.source.fighshare.api.FigshareCollectionTimeline;
import org.orcid.scheduler.loader.source.ror.RorOrgLoadSource;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-scheduler-context.xml" })
public class RorOrgLoadSourceTest {

    @Mock
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;

    @Mock
    private OrgDisambiguatedDao orgDisambiguatedDao;
    
    @Mock
    private OrgDisambiguatedManager orgDisambiguatedManager;
    
    @Mock
    private FileRotator fileRotator;

    @Mock
    private OrgDataClient orgDataClient;
    
    @InjectMocks
    private RorOrgLoadSource rorOrgLoadSource;

    @Before
    public void before() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);
        
        ReflectionTestUtils.setField(rorOrgLoadSource, "userAgent", "userAgent");
        ReflectionTestUtils.setField(rorOrgLoadSource, "rorZenodoRecordsUrl", "rorZenodoRecordsUrl");
        ReflectionTestUtils.setField(rorOrgLoadSource, "enabled", true);
        
        when(orgDataClient.get(Mockito.eq("rorZenodoRecordsUrl"), Mockito.eq("userAgent"), Mockito.any())).thenReturn(Arrays.asList(getFigsharerorCollectionArticleSummary(1, "2019-01-01T07:00:00"), getFigsharerorCollectionArticleSummary(2, "2020-01-01T07:00:00"), getFigsharerorCollectionArticleSummary(3, "2020-06-01T07:00:00")));
        when(orgDataClient.downloadFile(Mockito.eq("downloadUrl/1"), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(orgDataClient.downloadFile(Mockito.eq("downloadUrl/2"), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        when(orgDataClient.downloadFile(Mockito.eq("downloadUrl/3"), Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        
        Path path = Paths.get(getClass().getClassLoader().getResource("ror/dummy.zip").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "zipFilePath", testFile.getAbsolutePath());
    }
    
    @Test
    public void testGetSourceName() {
        assertEquals("ROR", rorOrgLoadSource.getSourceName());
    }
    
    @Test(expected = LoadSourceDisabledException.class)
    public void testSetDisabled() {
        ReflectionTestUtils.setField(rorOrgLoadSource, "enabled", false);
        rorOrgLoadSource.loadOrgData();
    }
    
    @Test
    public void testDownloadOrgData() throws URISyntaxException {
        // doesn't matter which data file we choose as this test isn't testing data loading
        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_1_org_5_external_identifiers.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());

        rorOrgLoadSource.downloadOrgData();
        
        verify(fileRotator, Mockito.times(1)).removeFileIfExists(Mockito.eq(testFile.getAbsolutePath()));
        
        // verify collection with identifier 3 (see setUp method) is chosen
        verify(orgDataClient, Mockito.never()).get(Mockito.eq("rorZenodoRecordsUrl"), Mockito.eq("userAgent"), Mockito.any());
    }

    @Test
    public void execute_Stats_Test_1() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_1_org_5_external_identifiers.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());

        rorOrgLoadSource.loadOrgData();

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(orgDisambiguatedManager, Mockito.times(1)).createOrgDisambiguated(captor.capture());
        OrgDisambiguatedEntity persisted = captor.getValue();
        assertNotEquals(OrganizationStatus.DEPRECATED.name(), persisted.getStatus());
        assertNotEquals(OrganizationStatus.OBSOLETE.name(), persisted.getStatus());

        verify(orgDisambiguatedManager, times(4)).createOrgDisambiguatedExternalIdentifier(any(OrgDisambiguatedExternalIdentifierEntity.class));
        verify(orgDisambiguatedManager, never()).updateOrgDisambiguated(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).merge(any(OrgDisambiguatedExternalIdentifierEntity.class));
    }

    @Test
    public void execute_Stats_Test_2() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_4_orgs_27_external_identifiers.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());
        rorOrgLoadSource.loadOrgData();

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(orgDisambiguatedManager, Mockito.times(4)).createOrgDisambiguated(captor.capture());
        for (OrgDisambiguatedEntity persisted : captor.getAllValues()) {
            assertNotEquals(OrganizationStatus.DEPRECATED.name(), persisted.getStatus());
            assertNotEquals(OrganizationStatus.OBSOLETE.name(), persisted.getStatus());
        }

        verify(orgDisambiguatedManager, times(24)).createOrgDisambiguatedExternalIdentifier(any(OrgDisambiguatedExternalIdentifierEntity.class));
        verify(orgDisambiguatedManager, never()).updateOrgDisambiguated(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).merge(any(OrgDisambiguatedExternalIdentifierEntity.class));
    }

    @Test
    public void execute_Stats_Test_3() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.r.1", OrgDisambiguatedSourceType.ROR.name())).thenReturn(new OrgDisambiguatedEntity());
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.r.2", OrgDisambiguatedSourceType.ROR.name())).thenReturn(new OrgDisambiguatedEntity());
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.o.1", OrgDisambiguatedSourceType.ROR.name())).thenReturn(new OrgDisambiguatedEntity());
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.o.2", OrgDisambiguatedSourceType.ROR.name())).thenReturn(new OrgDisambiguatedEntity());

        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_2_deprecated_2_obsoleted_orgs.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());
        rorOrgLoadSource.loadOrgData();

        verify(orgDisambiguatedDao, Mockito.never()).persist(Mockito.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).persist(any(OrgDisambiguatedExternalIdentifierEntity.class));

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(orgDisambiguatedManager, times(3)).updateOrgDisambiguated(captor.capture());

        int deprecated = 0;
        int obsolete = 0;

        for (OrgDisambiguatedEntity persisted : captor.getAllValues()) {
            if (OrganizationStatus.DEPRECATED.name().equals(persisted.getStatus())) {
                deprecated++;
            } else if (OrganizationStatus.OBSOLETE.name().equals(persisted.getStatus())) {
                obsolete++;
            }
        }
        assertEquals(2L, deprecated);
        assertEquals(1L, obsolete);

        verify(orgDisambiguatedExternalIdentifierDao, never()).merge(any(OrgDisambiguatedExternalIdentifierEntity.class));
    }

    @Test
    public void execute_JustAddOneExeternalIdentifier_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.1", OrgDisambiguatedSourceType.ROR.name())).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("ror.1");
                entity.setCity("City One");
                entity.setCountry(Iso3166Country.US.name());
                entity.setOrgType("type_1");
                entity.setRegion("Alabama");
                entity.setSourceType(OrgDisambiguatedSourceType.ROR.name());
                entity.setUrl("http://link1.com");
                return entity;
            }
        });
        OrgDisambiguatedExternalIdentifierEntity extId = new OrgDisambiguatedExternalIdentifierEntity();
        extId.setPreferred(Boolean.FALSE);
        OrgDisambiguatedExternalIdentifierEntity extIdPreferred = new OrgDisambiguatedExternalIdentifierEntity();
        extIdPreferred.setPreferred(Boolean.TRUE);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ISNI1", "ISNI")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "FUNDREF1", OrgDisambiguatedSourceType.FUNDREF.name())).thenReturn(extIdPreferred);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ORGREF1", "ORGREF")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA1", "WIKIDATA")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "http://en.wikipedia.org/wiki/org_1", "WIKIPEDIA_URL")).thenReturn(extId);

        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_1_org_6_external_identifiers.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());
        rorOrgLoadSource.loadOrgData();

        verify(orgDisambiguatedManager, times(1)).createOrgDisambiguatedExternalIdentifier(any(OrgDisambiguatedExternalIdentifierEntity.class));

        verify(orgDisambiguatedDao, never()).persist(Mockito.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedManager, times(1)).createOrgDisambiguatedExternalIdentifier(any(OrgDisambiguatedExternalIdentifierEntity.class));
        verify(orgDisambiguatedDao, never()).merge(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).merge(any(OrgDisambiguatedExternalIdentifierEntity.class));
    }

    @Test
    public void execute_UpdateExistingInstitute_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.1", OrgDisambiguatedSourceType.ROR.name())).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("ror.1");
                entity.setCity("Adelaide");
                entity.setCountry(Iso3166Country.AU.name());
                entity.setOrgType("type_1");
                entity.setSourceType(OrgDisambiguatedSourceType.ROR.name());
                entity.setStatus("active");
                entity.setUrl("http://link1.com");
                return entity;
            }
        });
        OrgDisambiguatedExternalIdentifierEntity extId = new OrgDisambiguatedExternalIdentifierEntity();
        extId.setPreferred(Boolean.FALSE);
        OrgDisambiguatedExternalIdentifierEntity extIdPreferred = new OrgDisambiguatedExternalIdentifierEntity();
        extIdPreferred.setPreferred(Boolean.TRUE);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ISNI1", "ISNI")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "FUNDREF1", OrgDisambiguatedSourceType.FUNDREF.name())).thenReturn(extIdPreferred);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ORGREF1", "ORGREF")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA1", "WIKIDATA")).thenReturn(extId);
 
        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_1_org_updated_5_external_identifiers.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());
        rorOrgLoadSource.loadOrgData();

        verify(orgDisambiguatedDao, never()).persist(Mockito.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).persist(any(OrgDisambiguatedExternalIdentifierEntity.class));

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(orgDisambiguatedManager, times(1)).updateOrgDisambiguated(captor.capture());
        verify(orgDisambiguatedExternalIdentifierDao, never()).merge(any(OrgDisambiguatedExternalIdentifierEntity.class));

        OrgDisambiguatedEntity orgToBeUpdated = captor.getValue();
        assertNotEquals(OrganizationStatus.DEPRECATED.name(), orgToBeUpdated.getStatus());
        assertNotEquals(OrganizationStatus.OBSOLETE.name(), orgToBeUpdated.getStatus());
        assertEquals(Iso3166Country.AU.name(), orgToBeUpdated.getCountry());
        assertEquals(Long.valueOf(1), orgToBeUpdated.getId());
        assertEquals("Adelaide Updated", orgToBeUpdated.getCity());
        assertEquals(IndexingStatus.PENDING, orgToBeUpdated.getIndexingStatus());
        assertEquals("org_1_updated", orgToBeUpdated.getName());
        assertEquals("ror.1", orgToBeUpdated.getSourceId());
        assertEquals(OrgDisambiguatedSourceType.ROR.name(), orgToBeUpdated.getSourceType());
        assertEquals("active", orgToBeUpdated.getStatus());
        assertEquals("http://link1.com/updated", orgToBeUpdated.getUrl());
    }

    @Test
    public void execute_NothingToCreateNothingToUpdate_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.1", OrgDisambiguatedSourceType.ROR.name())).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("ror.1");
                entity.setCity("City One");
                entity.setCountry(Iso3166Country.US.name());
                entity.setOrgType("type_1");
                entity.setRegion("Alabama");
                entity.setSourceType(OrgDisambiguatedSourceType.ROR.name());
                entity.setUrl("http://link1.com");
                return entity;
            }
        });
        OrgDisambiguatedExternalIdentifierEntity extId = new OrgDisambiguatedExternalIdentifierEntity();
        extId.setPreferred(Boolean.FALSE);
        OrgDisambiguatedExternalIdentifierEntity extIdPreferred = new OrgDisambiguatedExternalIdentifierEntity();
        extIdPreferred.setPreferred(Boolean.TRUE);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ISNI1", "ISNI")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "FUNDREF1", OrgDisambiguatedSourceType.FUNDREF.name())).thenReturn(extIdPreferred);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ORGREF1", "ORGREF")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA1", "WIKIDATA")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "http://en.wikipedia.org/wiki/org_1", "WIKIPEDIA_URL")).thenReturn(extId);

        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_1_org_5_external_identifiers.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());
        rorOrgLoadSource.loadOrgData();

        verify(orgDisambiguatedDao, never()).persist(Mockito.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).persist(any(OrgDisambiguatedExternalIdentifierEntity.class));
        verify(orgDisambiguatedDao, never()).merge(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).merge(any(OrgDisambiguatedExternalIdentifierEntity.class));
    }

    @Test
    public void execute_DeprecatedObsoleteInstitutes_1_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.o.1", OrgDisambiguatedSourceType.ROR.name())).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setSourceId("ror.o.1");
                return entity;
            }
        });
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.o.2", OrgDisambiguatedSourceType.ROR.name())).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(2L);
                entity.setSourceId("ror.o.2");
                return entity;
            }
        });
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.r.1", OrgDisambiguatedSourceType.ROR.name())).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(3L);
                entity.setSourceId("ror.r.1");
                return entity;
            }
        });

        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.r.2", OrgDisambiguatedSourceType.ROR.name())).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(4L);
                entity.setSourceId("ror.r.2");
                return entity;
            }
        });

        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_2_deprecated_2_obsoleted_orgs.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());
        rorOrgLoadSource.loadOrgData();

        verify(orgDisambiguatedDao, never()).persist(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).persist(any(OrgDisambiguatedExternalIdentifierEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).merge(any(OrgDisambiguatedExternalIdentifierEntity.class));
        verify(orgDisambiguatedDao, times(0)).persist(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, times(0)).persist(any(OrgDisambiguatedExternalIdentifierEntity.class));

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(orgDisambiguatedManager, times(3)).updateOrgDisambiguated(captor.capture());

        int obsoleteCount = 0;
        int deprecatedCount = 0;
        List<OrgDisambiguatedEntity> orgsToBeUpdated = captor.getAllValues();
        for (OrgDisambiguatedEntity entity : orgsToBeUpdated) {
            if ("OBSOLETE".equals(entity.getStatus())) {
                if (entity.getId() == 1L) {
                    assertEquals("ror.o.1", entity.getSourceId());
                } else if (entity.getId() == 2L) {
                    assertEquals("ror.o.2", entity.getSourceId());
                } else {
                    fail("Invalid obsolete org id: " + entity.getId());
                }
                obsoleteCount++;
            } else if ("DEPRECATED".equals(entity.getStatus())) {
                if (entity.getId() == 3L) {
                    assertEquals("ror.1", entity.getSourceParentId());
                } else if (entity.getId() == 4L) {
                    assertEquals("ror.2", entity.getSourceParentId());
                } else {
                    fail("Invalid deprecated org id: " + entity.getId());
                }
                deprecatedCount++;
            }
        }
        assertEquals(2, deprecatedCount);
        assertEquals(1, obsoleteCount);
    }

    @Test
    public void execute_DeprecatedObsoleteInstitutes_2_Test() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_2_deprecated_2_obsoleted_orgs.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());
        rorOrgLoadSource.loadOrgData();

        verify(orgDisambiguatedDao, never()).merge(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).merge(any(OrgDisambiguatedExternalIdentifierEntity.class));
        verify(orgDisambiguatedDao, times(0)).merge(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, times(0)).persist(any(OrgDisambiguatedExternalIdentifierEntity.class));

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);

        verify(orgDisambiguatedManager, times(4)).createOrgDisambiguated(captor.capture());

        int obsoleteCount = 0;
        int deprecatedCount = 0;
        List<OrgDisambiguatedEntity> orgsToBeUpdated = captor.getAllValues();
        for (OrgDisambiguatedEntity entity : orgsToBeUpdated) {
            if ("OBSOLETE".equals(entity.getStatus())) {
                assertThat(entity.getSourceId(), anyOf(is("ror.o.1"), is("ror.o.2")));
                obsoleteCount++;
            } else if ("DEPRECATED".equals(entity.getStatus())) {
                assertThat(entity.getSourceId(), anyOf(is("ror.r.1"), is("ror.r.2")));
                deprecatedCount++;
            }
        }
        assertEquals(2, deprecatedCount);
        assertEquals(2, obsoleteCount);
    }

    @Test
    public void execute_UpdatePreferredIndicator_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("ror.1", OrgDisambiguatedSourceType.ROR.name())).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("ror.1");
                entity.setCity("City One");
                entity.setCountry(Iso3166Country.US.name());
                entity.setOrgType("type_1");
                entity.setRegion("Alabama");
                entity.setSourceType(OrgDisambiguatedSourceType.ROR.name());
                entity.setUrl("http://link1.com");
                return entity;
            }
        });
        
        // On DB WIKIDATA1 is preferred, but in the file WIKIDATA2 is the
        // preferred one
        OrgDisambiguatedExternalIdentifierEntity wikidata1 = new OrgDisambiguatedExternalIdentifierEntity();
        wikidata1.setIdentifier("WIKIDATA1");
        wikidata1.setIdentifierType("WIKIDATA");
        wikidata1.setPreferred(Boolean.TRUE);

        OrgDisambiguatedExternalIdentifierEntity wikidata2 = new OrgDisambiguatedExternalIdentifierEntity();
        wikidata2.setIdentifier("WIKIDATA2");
        wikidata2.setIdentifierType("WIKIDATA");
        wikidata2.setPreferred(Boolean.FALSE);

        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA1", "WIKIDATA")).thenReturn(wikidata1);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA2", "WIKIDATA")).thenReturn(wikidata2);

        Path path = Paths.get(getClass().getClassLoader().getResource("ror/ror_1_org_2_ext_ids_#2_preferred.json").toURI());
        File testFile = path.toFile();
        ReflectionTestUtils.setField(rorOrgLoadSource, "localDataPath", testFile.getAbsolutePath());
        
        rorOrgLoadSource.loadOrgData();

        verify(orgDisambiguatedDao, never()).merge(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, never()).persist(any(OrgDisambiguatedExternalIdentifierEntity.class));
        verify(orgDisambiguatedManager, times(2)).updateOrgDisambiguatedExternalIdentifier(any(OrgDisambiguatedExternalIdentifierEntity.class));

        verify(orgDisambiguatedDao, never()).persist(any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedDao, never()).merge(any(OrgDisambiguatedEntity.class));

        ArgumentCaptor<OrgDisambiguatedExternalIdentifierEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedExternalIdentifierEntity.class);
        verify(orgDisambiguatedManager, times(2)).updateOrgDisambiguatedExternalIdentifier(captor.capture());

        List<OrgDisambiguatedExternalIdentifierEntity> extIdsToBeUpdated = captor.getAllValues();

        OrgDisambiguatedExternalIdentifierEntity wikidata1ExtId = extIdsToBeUpdated.get(0);
        assertEquals("WIKIDATA1", wikidata1ExtId.getIdentifier());
        assertEquals("WIKIDATA", wikidata1ExtId.getIdentifierType());
        assertEquals(Boolean.FALSE, wikidata1ExtId.getPreferred());

        OrgDisambiguatedExternalIdentifierEntity wikidata2ExtId = extIdsToBeUpdated.get(1);
        assertEquals("WIKIDATA2", wikidata2ExtId.getIdentifier());
        assertEquals("WIKIDATA", wikidata2ExtId.getIdentifierType());
        assertEquals(Boolean.TRUE, wikidata2ExtId.getPreferred());
    }
    

    private FigshareCollectionArticleSummary getFigsharerorCollectionArticleSummary(int id, String date) {
        FigshareCollectionArticleSummary summary = new FigshareCollectionArticleSummary();
        summary.setId(id);
        FigshareCollectionTimeline timeline = new FigshareCollectionTimeline();
        timeline.setPosted(date);
        summary.setTimeline(timeline);
        return summary;
    }
    
    private FigshareCollectionArticleDetails getFigsharerorCollectionArticleDetails(int identifier) {
        FigshareCollectionArticleDetails details = new FigshareCollectionArticleDetails();
        FigshareCollectionArticleFile file = new FigshareCollectionArticleFile();
        file.setName("ror.zip");
        file.setDownloadUrl("downloadUrl/" + identifier);
        details.setFiles(Arrays.asList(file));
        return details;
    }

}
