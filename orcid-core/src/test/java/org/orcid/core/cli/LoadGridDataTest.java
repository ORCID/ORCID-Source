package org.orcid.core.cli;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;

@SuppressWarnings("deprecation")
public class LoadGridDataTest {

    @Mock
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;
    @Mock
    private OrgDisambiguatedDao orgDisambiguatedDao;

    private LoadGridData loadGridData = new LoadGridData();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        loadGridData.setOrgDisambiguatedDao(orgDisambiguatedDao);
        loadGridData.setOrgDisambiguatedExternalIdentifierDao(orgDisambiguatedExternalIdentifierDao);
    }

    @Test
    public void execute_Stats_Test_1() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_1_org_5_external_identifiers.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();
        assertEquals(1L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(5L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(0L, loadGridData.getDeprecatedOrgs());
        assertEquals(0L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
    }

    @Test
    public void execute_Stats_Test_2() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_4_orgs_27_external_identifiers.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();
        assertEquals(4L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(27L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(0L, loadGridData.getDeprecatedOrgs());
        assertEquals(0L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
    }

    @Test
    public void execute_Stats_Test_3() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.r.1", "GRID")).thenReturn(new OrgDisambiguatedEntity());
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.r.2", "GRID")).thenReturn(new OrgDisambiguatedEntity());
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.o.1", "GRID")).thenReturn(new OrgDisambiguatedEntity());
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.o.2", "GRID")).thenReturn(new OrgDisambiguatedEntity());

        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_2_deprecated_2_obsoleted_orgs.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();
        assertEquals(0L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(0L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(2L, loadGridData.getDeprecatedOrgs());
        assertEquals(2L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
    }
    
    @Test
    public void execute_JustAddOneExeternalIdentifier_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.1", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("grid.1");
                entity.setCity("City One");
                entity.setCountry(Iso3166Country.US);
                entity.setOrgType("type_1");
                entity.setRegion("Alabama");
                entity.setSourceType("GRID");
                entity.setUrl("http://link1.com");
                return entity;
            }
        });
        OrgDisambiguatedExternalIdentifierEntity extId = new OrgDisambiguatedExternalIdentifierEntity();
        extId.setPreferred(Boolean.FALSE);
        OrgDisambiguatedExternalIdentifierEntity extIdPreferred = new OrgDisambiguatedExternalIdentifierEntity();
        extIdPreferred.setPreferred(Boolean.TRUE);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ISNI1", "ISNI")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "FUNDREF1", "FUNDREF")).thenReturn(extIdPreferred);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ORGREF1", "ORGREF")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA1", "WIKIDATA")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "http://en.wikipedia.org/wiki/org_1", "WIKIPEDIA_URL")).thenReturn(extId);

        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_1_org_6_external_identifiers.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();

        verify(orgDisambiguatedDao, times(0)).persist(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedDao, times(0)).merge(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, times(1)).persist(Matchers.any(OrgDisambiguatedExternalIdentifierEntity.class));
        assertEquals(0L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(1L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(0L, loadGridData.getDeprecatedOrgs());
        assertEquals(0L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
    }

    @Test
    public void execute_UpdateExistingInstitute_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.1", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("grid.1");
                entity.setCity("City One");
                entity.setCountry(Iso3166Country.US);
                entity.setOrgType("type_1");
                entity.setRegion("Alabama");
                entity.setSourceType("GRID");
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
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "FUNDREF1", "FUNDREF")).thenReturn(extIdPreferred);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ORGREF1", "ORGREF")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA1", "WIKIDATA")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "http://en.wikipedia.org/wiki/org_1", "WIKIPEDIA_URL")).thenReturn(extId);

        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_1_org_updated_5_external_identifiers.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();

        assertEquals(0L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(0L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(1L, loadGridData.getUpdatedOrgs());
        assertEquals(0L, loadGridData.getDeprecatedOrgs());
        assertEquals(0L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
        
        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);

        verify(orgDisambiguatedDao).merge(captor.capture());

        OrgDisambiguatedEntity orgToBeUpdated = captor.getValue();
        assertEquals(Iso3166Country.CR, orgToBeUpdated.getCountry());
        assertEquals(Long.valueOf(1), orgToBeUpdated.getId());
        assertEquals("City One Updated", orgToBeUpdated.getCity());
        assertEquals(IndexingStatus.PENDING, orgToBeUpdated.getIndexingStatus());
        assertEquals("org_1_updated", orgToBeUpdated.getName());
        assertEquals("type_1,type_2", orgToBeUpdated.getOrgType());
        assertEquals("San Jose", orgToBeUpdated.getRegion());
        assertEquals("grid.1", orgToBeUpdated.getSourceId());
        assertEquals("GRID", orgToBeUpdated.getSourceType());
        assertEquals("active", orgToBeUpdated.getStatus());
        assertEquals("http://link1.com/updated", orgToBeUpdated.getUrl());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void execute_NothingToCreateNothingToUpdate_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.1", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("grid.1");
                entity.setCity("City One");
                entity.setCountry(Iso3166Country.US);
                entity.setOrgType("type_1");
                entity.setRegion("Alabama");
                entity.setSourceType("GRID");
                entity.setUrl("http://link1.com");
                return entity;
            }
        });
        OrgDisambiguatedExternalIdentifierEntity extId = new OrgDisambiguatedExternalIdentifierEntity();
        extId.setPreferred(Boolean.FALSE);
        OrgDisambiguatedExternalIdentifierEntity extIdPreferred = new OrgDisambiguatedExternalIdentifierEntity();
        extIdPreferred.setPreferred(Boolean.TRUE);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ISNI1", "ISNI")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "FUNDREF1", "FUNDREF")).thenReturn(extIdPreferred);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ORGREF1", "ORGREF")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA1", "WIKIDATA")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "http://en.wikipedia.org/wiki/org_1", "WIKIPEDIA_URL")).thenReturn(extId);

        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_1_org_5_external_identifiers.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();

        verify(orgDisambiguatedDao, times(0)).persist(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedDao, times(0)).merge(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, times(0)).persist(Matchers.any(OrgDisambiguatedExternalIdentifierEntity.class));
        assertEquals(0L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(0L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(0L, loadGridData.getDeprecatedOrgs());
        assertEquals(0L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void execute_DeprecatedObsoleteInstitutes_1_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.o.1", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setSourceId("grid.o.1");
                return entity;
            }
        });
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.o.2", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(2L);
                entity.setSourceId("grid.o.2");
                return entity;
            }
        });
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.r.1", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(3L);
                entity.setSourceId("grid.r.1");
                return entity;
            }
        });

        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.r.2", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(4L);
                entity.setSourceId("grid.r.2");
                return entity;
            }
        });

        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_2_deprecated_2_obsoleted_orgs.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();

        assertEquals(0L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(0L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(2L, loadGridData.getDeprecatedOrgs());
        assertEquals(2L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
        verify(orgDisambiguatedDao, times(0)).persist(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedDao, times(4)).merge(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, times(0)).persist(Matchers.any(OrgDisambiguatedExternalIdentifierEntity.class));

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);

        verify(orgDisambiguatedDao, times(4)).merge(captor.capture());

        int obsoleteCount = 0;
        int deprecatedCount = 0;
        List<OrgDisambiguatedEntity> orgsToBeUpdated = captor.getAllValues();
        for (OrgDisambiguatedEntity entity : orgsToBeUpdated) {
            if ("OBSOLETE".equals(entity.getStatus())) {
                if (entity.getId() == 1L) {
                    assertEquals("grid.o.1", entity.getSourceId());
                } else if (entity.getId() == 2L) {
                    assertEquals("grid.o.2", entity.getSourceId());
                } else {
                    fail("Invalid obsolete org id: " + entity.getId());
                }
                obsoleteCount++;
            } else if ("DEPRECATED".equals(entity.getStatus())) {
                if (entity.getId() == 3L) {
                    assertEquals("grid.1", entity.getSourceParentId());
                } else if (entity.getId() == 4L) {
                    assertEquals("grid.2", entity.getSourceParentId());
                } else {
                    fail("Invalid deprecated org id: " + entity.getId());
                }
                deprecatedCount++;
            }
        }
        assertEquals(2, deprecatedCount);
        assertEquals(2, obsoleteCount);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void execute_DeprecatedObsoleteInstitutes_2_Test() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_2_deprecated_2_obsoleted_orgs.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();

        assertEquals(0L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(0L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(2L, loadGridData.getDeprecatedOrgs());
        assertEquals(2L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
        verify(orgDisambiguatedDao, times(4)).persist(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedDao, times(0)).merge(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, times(0)).persist(Matchers.any(OrgDisambiguatedExternalIdentifierEntity.class));

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);

        verify(orgDisambiguatedDao, times(4)).persist(captor.capture());

        int obsoleteCount = 0;
        int deprecatedCount = 0;
        List<OrgDisambiguatedEntity> orgsToBeUpdated = captor.getAllValues();
        for (OrgDisambiguatedEntity entity : orgsToBeUpdated) {
            if ("OBSOLETE".equals(entity.getStatus())) {
                assertThat(entity.getSourceId(), anyOf(is("grid.o.1"), is("grid.o.2")));
                obsoleteCount++;
            } else if ("DEPRECATED".equals(entity.getStatus())) {
                assertThat(entity.getSourceId(), anyOf(is("grid.r.1"), is("grid.r.2")));
                deprecatedCount++;
            }
        }
        assertEquals(2, deprecatedCount);
        assertEquals(2, obsoleteCount);
    }
    
    @Test
    public void execute_AddMissingWikipediaExtId_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.1", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("grid.1");
                entity.setCity("City One");
                entity.setCountry(Iso3166Country.US);
                entity.setOrgType("type_1");
                entity.setRegion("Alabama");
                entity.setSourceType("GRID");
                entity.setStatus("active");
                entity.setUrl("http://link1.com");
                return entity;
            }
        });
        OrgDisambiguatedExternalIdentifierEntity extId = new OrgDisambiguatedExternalIdentifierEntity();
        extId.setPreferred(false);
        
        OrgDisambiguatedExternalIdentifierEntity extIdPreferred = new OrgDisambiguatedExternalIdentifierEntity();
        extIdPreferred.setPreferred(true);
        
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ISNI1", "ISNI")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "FUNDREF1", "FUNDREF")).thenReturn(extIdPreferred);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "ORGREF1", "ORGREF")).thenReturn(extId);
        when(orgDisambiguatedExternalIdentifierDao.findByDetails(1L, "WIKIDATA1", "WIKIDATA")).thenReturn(extId);        

        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_1_org_5_external_identifiers.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();

        assertEquals(0L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(1L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(0L, loadGridData.getDeprecatedOrgs());
        assertEquals(0L, loadGridData.getObsoletedOrgs());
        assertEquals(0L, loadGridData.getUpdatedExternalIdentifiers());
        
        ArgumentCaptor<OrgDisambiguatedExternalIdentifierEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedExternalIdentifierEntity.class);

        verify(orgDisambiguatedExternalIdentifierDao).persist(captor.capture());

        OrgDisambiguatedExternalIdentifierEntity orgToBeUpdated = captor.getValue();
        assertEquals("http://en.wikipedia.org/wiki/org_1", orgToBeUpdated.getIdentifier());
        assertEquals("WIKIPEDIA_URL", orgToBeUpdated.getIdentifierType());
        assertEquals(Boolean.TRUE, orgToBeUpdated.getPreferred());
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void execute_UpdatePreferredIndicator_Test() throws URISyntaxException {
        when(orgDisambiguatedDao.findBySourceIdAndSourceType("grid.1", "GRID")).thenAnswer(new Answer<OrgDisambiguatedEntity>() {
            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(1L);
                entity.setName("org_1");
                entity.setSourceId("grid.1");
                entity.setCity("City One");
                entity.setCountry(Iso3166Country.US);
                entity.setOrgType("type_1");
                entity.setRegion("Alabama");
                entity.setSourceType("GRID");
                entity.setUrl("http://link1.com");
                return entity;
            }
        });
        // On DB WIKIDATA1 is preferred, but in the file WIKIDATA2 is the preferred one
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
        
        Path path = Paths.get(getClass().getClassLoader().getResource("grid/grid_1_org_2_ext_ids_#2_preferred.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();

        verify(orgDisambiguatedDao, times(0)).persist(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedDao, times(0)).merge(Matchers.any(OrgDisambiguatedEntity.class));
        verify(orgDisambiguatedExternalIdentifierDao, times(0)).persist(Matchers.any(OrgDisambiguatedExternalIdentifierEntity.class));
        assertEquals(0L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(0L, loadGridData.getAddedExternalIdentifiers());
        assertEquals(0L, loadGridData.getUpdatedOrgs());
        assertEquals(0L, loadGridData.getDeprecatedOrgs());
        assertEquals(0L, loadGridData.getObsoletedOrgs());
        assertEquals(2L, loadGridData.getUpdatedExternalIdentifiers());        
        
        ArgumentCaptor<OrgDisambiguatedExternalIdentifierEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedExternalIdentifierEntity.class);
        verify(orgDisambiguatedExternalIdentifierDao, times(2)).merge(captor.capture());
        
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
}
