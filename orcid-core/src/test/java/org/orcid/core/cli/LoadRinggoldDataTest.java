package org.orcid.core.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class LoadRinggoldDataTest {

    @Mock
    private OrgDisambiguatedExternalIdentifierDao mockOrgDisambiguatedExternalIdentifierDao;
    @Mock
    private OrgDisambiguatedDao mockOrgDisambiguatedDao;
    @Mock
    private OrgDao mockOrgDao;
    
    private TransactionTemplate mockTransactionTemplate = new TransactionTemplateStub();

    private LoadRinggoldData loader = new LoadRinggoldData();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        loader.setOrgDao(mockOrgDao);
        loader.setOrgDisambiguatedDao(mockOrgDisambiguatedDao);
        loader.setOrgDisambiguatedExternalIdentifierDao(mockOrgDisambiguatedExternalIdentifierDao);
        loader.setTransactionTemplate(mockTransactionTemplate);
    }

    @Test
    public void test_DoNothing() throws URISyntaxException {
        setupInitialMocks();

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_base/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        verify(mockOrgDisambiguatedDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedDao, times(0)).merge(any());

        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).remove(anyLong());        

        verify(mockOrgDao, times(0)).persist(any());
        verify(mockOrgDao, times(0)).merge(any());
    }

    @Test
    public void test_EmptyDB() throws URISyntaxException {
        when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(anyString(), eq("RINGGOLD"))).thenReturn(null);
        when(mockOrgDao.findByNameCityRegionAndCountry(anyString(), anyString(), anyString(), any())).thenReturn(null);

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_base/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        ArgumentCaptor<OrgDisambiguatedEntity> orgDisambiguatedEntityCaptor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedDao, times(4)).persist(orgDisambiguatedEntityCaptor.capture());
        List<OrgDisambiguatedEntity> newOrgDisambiguatedEntities = orgDisambiguatedEntityCaptor.getAllValues();
        assertEquals(4, newOrgDisambiguatedEntities.size());

        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (OrgDisambiguatedEntity entity : newOrgDisambiguatedEntities) {
            assertEquals(IndexingStatus.PENDING, entity.getIndexingStatus());
            assertEquals("RINGGOLD", entity.getSourceType());
            switch (entity.getSourceId()) {
            case "1":
                assertNull(entity.getSourceParentId());
                assertEquals("1. Name", entity.getName());
                assertEquals("City#1", entity.getCity());
                assertEquals("State#1", entity.getRegion());
                assertEquals(Iso3166Country.US, entity.getCountry());
                assertEquals("type/1", entity.getOrgType());                
                found1 = true;
                break;
            case "2":
                assertNull(entity.getSourceParentId());
                assertEquals("2. Name", entity.getName());
                assertEquals("City#2", entity.getCity());
                assertNull(entity.getRegion());
                assertEquals(Iso3166Country.CR, entity.getCountry());
                assertEquals("type/2", entity.getOrgType());
                found2 = true;
                break;
            case "3":
                assertNull(entity.getSourceParentId());
                assertEquals("3. Name", entity.getName());
                assertEquals("City#3", entity.getCity());
                assertNull(entity.getRegion());
                assertEquals(Iso3166Country.US, entity.getCountry());
                assertEquals("type/3", entity.getOrgType());
                found3 = true;
                break;
            case "4":
                assertEquals("1", entity.getSourceParentId());
                assertEquals("4. Name", entity.getName());
                assertEquals("City#4", entity.getCity());
                assertEquals("State#4", entity.getRegion());
                assertEquals(Iso3166Country.CR, entity.getCountry());
                assertEquals("type/4", entity.getOrgType());
                found4 = true;
                break;
            default:
                fail("Invalid ringgold id found: " + entity.getSourceId());
                break;
            }
        }
        ;
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

        ArgumentCaptor<OrgDisambiguatedExternalIdentifierEntity> orgDisambiguatedExaternalIdentifierEntityCaptor = ArgumentCaptor
                .forClass(OrgDisambiguatedExternalIdentifierEntity.class);
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).remove(anyLong());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(8)).persist(orgDisambiguatedExaternalIdentifierEntityCaptor.capture());
        List<OrgDisambiguatedExternalIdentifierEntity> extIds = orgDisambiguatedExaternalIdentifierEntityCaptor.getAllValues();
        assertEquals(8, extIds.size());
        found1 = found2 = found3 = found4 = false;
        boolean found5 = false, found6 = false, found7 = false, found8 = false;
        for (OrgDisambiguatedExternalIdentifierEntity entity : extIds) {
            switch (entity.getIdentifier()) {
            case "1":
                assertEquals("1", entity.getOrgDisambiguated().getSourceId());
                assertEquals("ISNI", entity.getIdentifierType());
                assertEquals("1", entity.getIdentifier());
                found1 = true;
                break;
            case "2":
                assertEquals("1", entity.getOrgDisambiguated().getSourceId());
                assertEquals("IPED", entity.getIdentifierType());
                assertEquals("2", entity.getIdentifier());
                found2 = true;
                break;
            case "3":
                assertEquals("2", entity.getOrgDisambiguated().getSourceId());
                assertEquals("NCES", entity.getIdentifierType());
                assertEquals("3", entity.getIdentifier());
                found3 = true;
                break;
            case "4":
                assertEquals("2", entity.getOrgDisambiguated().getSourceId());
                assertEquals("OFR", entity.getIdentifierType());
                assertEquals("4", entity.getIdentifier());
                found4 = true;
                break;
            case "5":
                assertEquals("3", entity.getOrgDisambiguated().getSourceId());
                assertEquals("ISNI", entity.getIdentifierType());
                assertEquals("5", entity.getIdentifier());
                found5 = true;
                break;
            case "6":
                assertEquals("3", entity.getOrgDisambiguated().getSourceId());
                assertEquals("IPED", entity.getIdentifierType());
                assertEquals("6", entity.getIdentifier());
                found6 = true;
                break;
            case "7":
                assertEquals("4", entity.getOrgDisambiguated().getSourceId());
                assertEquals("NCES", entity.getIdentifierType());
                assertEquals("7", entity.getIdentifier());
                found7 = true;
                break;
            case "8":
                assertEquals("4", entity.getOrgDisambiguated().getSourceId());
                assertEquals("OFR", entity.getIdentifierType());
                assertEquals("8", entity.getIdentifier());
                found8 = true;
                break;
            default:
                fail("Invalid ext id found: " + entity.getIdentifier());
                break;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
        assertTrue(found6);
        assertTrue(found7);
        assertTrue(found8);

        ArgumentCaptor<OrgEntity> orgEntityCaptor = ArgumentCaptor.forClass(OrgEntity.class);
        verify(mockOrgDao, times(0)).merge(any());
        verify(mockOrgDao, times(2)).persist(orgEntityCaptor.capture());
        List<OrgEntity> orgs = orgEntityCaptor.getAllValues();
        assertEquals(2, orgs.size());
        found1 = found2 = false;
        for (OrgEntity entity : orgs) {
            switch (entity.getName()) {
            case "1. Alt Name":
                assertEquals("1", entity.getOrgDisambiguated().getSourceId());
                assertEquals("AltCity#1", entity.getCity());
                assertEquals(Iso3166Country.MX, entity.getCountry());
                found1 = true;
                break;
            case "2. Alt Name":
                assertEquals("2", entity.getOrgDisambiguated().getSourceId());
                assertEquals("AltCity#2", entity.getCity());
                assertEquals(Iso3166Country.BR, entity.getCountry());
                found2 = true;
                break;
            default:
                fail("Invalid org found: " + entity.getName());
                break;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
    }

    @Test
    public void test_UpdateDisambiguatedOrgs() throws URISyntaxException {
        setupInitialMocks();

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_UpdateDisambiguatedOrgs/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        ArgumentCaptor<OrgDisambiguatedEntity> orgDisambiguatedEntityCaptor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedDao, times(4)).merge(orgDisambiguatedEntityCaptor.capture());

        List<OrgDisambiguatedEntity> newOrgDisambiguatedEntities = orgDisambiguatedEntityCaptor.getAllValues();
        assertEquals(4, newOrgDisambiguatedEntities.size());

        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (OrgDisambiguatedEntity entity : newOrgDisambiguatedEntities) {
            switch (entity.getSourceId()) {
            case "1":
                assertNull(entity.getSourceParentId());
                assertEquals(Long.valueOf(1000), entity.getId());
                assertEquals("1. Name - updated", entity.getName());
                assertEquals("City#1", entity.getCity());
                assertEquals("State#1", entity.getRegion());
                assertEquals(Iso3166Country.US, entity.getCountry());
                assertEquals("type/1", entity.getOrgType());
                found1 = true;
                break;
            case "2":
                assertEquals(Long.valueOf(2000), entity.getId());
                assertNull(entity.getSourceParentId());
                assertEquals("2. Name", entity.getName());
                assertEquals("City#2 - updated", entity.getCity());
                assertNull(entity.getRegion());
                assertEquals(Iso3166Country.CR, entity.getCountry());
                assertEquals("type/2", entity.getOrgType());
                found2 = true;
                break;
            case "3":
                assertEquals(Long.valueOf(3000), entity.getId());
                assertNull(entity.getSourceParentId());
                assertEquals("3. Name", entity.getName());
                assertEquals("City#3", entity.getCity());
                assertNull(entity.getRegion());
                assertEquals(Iso3166Country.ZW, entity.getCountry());
                assertEquals("type/3", entity.getOrgType());
                found3 = true;
                break;
            case "4":
                assertEquals(Long.valueOf(4000), entity.getId());
                assertEquals("1", entity.getSourceParentId());
                assertEquals("4. Name", entity.getName());
                assertEquals("City#4", entity.getCity());
                assertEquals("State#4 - updated", entity.getRegion());
                assertEquals(Iso3166Country.CR, entity.getCountry());
                assertEquals("type/4/updated", entity.getOrgType());
                found4 = true;
                break;
            default:
                fail("Invalid ringgold id found: " + entity.getSourceId());
                break;
            }
        }
        ;
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);

        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).remove(anyLong());
        
        verify(mockOrgDao, times(0)).persist(any());
        verify(mockOrgDao, times(0)).merge(any());
    }

    @Test
    public void test_AddDisambiguatedOrg() throws URISyntaxException {
        setupInitialMocks();

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_AddDisambiguatedOrg/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        ArgumentCaptor<OrgDisambiguatedEntity> orgDisambiguatedEntityCaptor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedDao, times(1)).persist(orgDisambiguatedEntityCaptor.capture());
        
        List<OrgDisambiguatedEntity> list = orgDisambiguatedEntityCaptor.getAllValues();
        assertEquals(1, list.size());
        OrgDisambiguatedEntity entity = list.get(0);
        assertEquals("City#5", entity.getCity());
        assertEquals(Iso3166Country.US, entity.getCountry());
        assertEquals(IndexingStatus.PENDING, entity.getIndexingStatus());
        assertEquals("5. Name", entity.getName());
        assertEquals("type/5", entity.getOrgType());
        assertEquals("State#5", entity.getRegion());
        assertEquals("5", entity.getSourceId());
        assertEquals("4", entity.getSourceParentId());
        assertEquals("RINGGOLD", entity.getSourceType());
        
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).remove(anyLong());
        
        verify(mockOrgDao, times(0)).persist(any());
        verify(mockOrgDao, times(0)).merge(any());
    }

    @Test
    public void test_AddOrg() throws URISyntaxException {
        setupInitialMocks();

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_addOrg/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        verify(mockOrgDisambiguatedDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedDao, times(0)).merge(any());

        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).remove(anyLong());
        
        ArgumentCaptor<OrgEntity> orgEntityCaptor = ArgumentCaptor.forClass(OrgEntity.class);
        verify(mockOrgDao, times(1)).persist(orgEntityCaptor.capture());
        verify(mockOrgDao, times(0)).merge(any());
        
        List<OrgEntity> list = orgEntityCaptor.getAllValues();
        assertEquals(1, list.size());
        OrgEntity entity = list.get(0);
        assertEquals("AltCity#3", entity.getCity());
        assertEquals(Iso3166Country.CA, entity.getCountry());
        assertEquals("3. Alt Name", entity.getName());
        assertEquals(Long.valueOf(3000), entity.getOrgDisambiguated().getId());
        assertNull(entity.getRegion());        
    }

    @Test
    public void test_AddExternalIdentifier() throws URISyntaxException {
        setupInitialMocks();

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_AddExternalIdentifier/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        verify(mockOrgDisambiguatedDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedDao, times(0)).merge(any());

        ArgumentCaptor<OrgDisambiguatedExternalIdentifierEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedExternalIdentifierEntity.class);
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(1)).persist(captor.capture());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).remove(anyLong());
        List<OrgDisambiguatedExternalIdentifierEntity> list = captor.getAllValues();
        assertEquals(1, list.size());
        OrgDisambiguatedExternalIdentifierEntity entity = list.get(0);
        assertEquals("9", entity.getIdentifier());
        assertEquals("ISNI", entity.getIdentifierType());
        assertEquals(false, entity.getPreferred());
        assertEquals(Long.valueOf(1000), entity.getOrgDisambiguated().getId());        
        
        verify(mockOrgDao, times(0)).persist(any());
        verify(mockOrgDao, times(0)).merge(any());
    }

    @Test
    public void test_DeprecateADisambiguatedOrg() throws URISyntaxException {
        setupInitialMocks();

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_DeprecateADisambiguatedOrg/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        ArgumentCaptor<OrgDisambiguatedEntity> captor = ArgumentCaptor.forClass(OrgDisambiguatedEntity.class);
        verify(mockOrgDisambiguatedDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedDao, times(1)).merge(captor.capture());
        List<OrgDisambiguatedEntity> list = captor.getAllValues();
        assertEquals(1, list.size());
        OrgDisambiguatedEntity entity = list.get(0);
        assertEquals("City#1", entity.getCity());
        assertEquals(Iso3166Country.US, entity.getCountry());
        assertEquals(Long.valueOf(1000), entity.getId());
        assertEquals(IndexingStatus.REINDEX, entity.getIndexingStatus());
        assertEquals("1. Name", entity.getName());
        assertEquals("type/1", entity.getOrgType());
        assertEquals("State#1", entity.getRegion());
        assertEquals("1", entity.getSourceId());
        assertEquals("4", entity.getSourceParentId());
        assertEquals("RINGGOLD", entity.getSourceType());
        assertEquals("DEPRECATED", entity.getStatus());        
        
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).remove(anyLong());

        verify(mockOrgDao, times(0)).persist(any());
        verify(mockOrgDao, times(0)).merge(any());
    }

    @Test
    public void test_LinkExistingOrgToNewDisambiguatedOrg() throws URISyntaxException {
        setupInitialMocks();

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_LinkExistingOrgToNewDisambiguatedOrg/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        verify(mockOrgDisambiguatedDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedDao, times(0)).merge(any());

        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).remove(anyLong());
        
        ArgumentCaptor<OrgEntity> captor = ArgumentCaptor.forClass(OrgEntity.class);
        verify(mockOrgDao, times(0)).persist(any());
        verify(mockOrgDao, times(1)).merge(captor.capture());
        List<OrgEntity> list = captor.getAllValues();
        assertEquals(1, list.size());
        OrgEntity entity = list.get(0);
        assertEquals("AltCity#3", entity.getCity());
        assertEquals(Iso3166Country.CR, entity.getCountry());
        assertEquals("Testing Org", entity.getName());
        assertEquals(Long.valueOf(3000), entity.getOrgDisambiguated().getId());
        assertEquals(null, entity.getRegion());               
    }

    
    @Test
    public void test_removeAndAddExternalIdentifier() throws URISyntaxException {
        setupInitialMocks();

        Path path = Paths.get(getClass().getClassLoader().getResource("ringgold/test_RemoveAndAddExternalIdentifier/").toURI());
        File testDirectory = path.toFile();
        assertTrue(testDirectory.exists());

        loader.setDirectory(testDirectory);
        loader.execute();

        verify(mockOrgDisambiguatedDao, times(0)).persist(any());
        verify(mockOrgDisambiguatedDao, times(0)).merge(any());

        ArgumentCaptor<OrgDisambiguatedExternalIdentifierEntity> addCaptor = ArgumentCaptor.forClass(OrgDisambiguatedExternalIdentifierEntity.class);
        ArgumentCaptor<Long> removeCaptor = ArgumentCaptor.forClass(Long.class);
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(1)).persist(addCaptor.capture());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(0)).merge(any());
        verify(mockOrgDisambiguatedExternalIdentifierDao, times(1)).remove(removeCaptor.capture());
        List<OrgDisambiguatedExternalIdentifierEntity> addedList = addCaptor.getAllValues();
        assertEquals(1, addedList.size());
        OrgDisambiguatedExternalIdentifierEntity entity = addedList.get(0);
        assertEquals("9", entity.getIdentifier());
        assertEquals("ISNI", entity.getIdentifierType());
        assertEquals(false, entity.getPreferred());
        assertEquals(Long.valueOf(1000), entity.getOrgDisambiguated().getId());        
        
        List<Long> deletedIds = removeCaptor.getAllValues();
        assertEquals(1, deletedIds.size());
        Long deletedId = deletedIds.get(0);
        assertEquals(Long.valueOf(5), deletedId);
        
        verify(mockOrgDao, times(0)).persist(any());
        verify(mockOrgDao, times(0)).merge(any());
    }
    
    private void setupInitialMocks() {
        
        when(mockOrgDisambiguatedDao.findBySourceIdAndSourceType(anyString(), eq("RINGGOLD"))).thenAnswer(new Answer<OrgDisambiguatedEntity>() {

            @Override
            public OrgDisambiguatedEntity answer(InvocationOnMock invocation) throws Throwable {
                Long ringgoldId = Long.valueOf((String) invocation.getArgument(0));
                if (ringgoldId < 1L || ringgoldId > 4L) {
                    return null;
                }
                OrgDisambiguatedEntity entity = new OrgDisambiguatedEntity();
                entity.setId(ringgoldId * 1000); // 1000, 2000, 3000, 4000
                entity.setSourceId(String.valueOf(ringgoldId));
                entity.setSourceType("RINGGOLD");
                OrgDisambiguatedExternalIdentifierEntity extId1, extId2;
                Set<OrgDisambiguatedExternalIdentifierEntity> extIds;
                switch (String.valueOf(ringgoldId)) {
                case "1":
                    entity.setName("1. Name");
                    entity.setCountry(Iso3166Country.US);
                    entity.setCity("City#1");
                    entity.setRegion("State#1");
                    entity.setOrgType("type/1");
                    entity.setSourceParentId(null);
                    entity.setStatus(null);
                    
                    extId1 = new OrgDisambiguatedExternalIdentifierEntity();
                    extId1.setId(1L);
                    extId1.setOrgDisambiguated(entity);
                    extId1.setIdentifier("1");
                    extId1.setIdentifierType("ISNI");
                    
                    extId2 = new OrgDisambiguatedExternalIdentifierEntity();
                    extId2.setId(2L);
                    extId2.setOrgDisambiguated(entity);
                    extId2.setIdentifier("2");
                    extId2.setIdentifierType("IPED");
                    
                    extIds = new HashSet<>();
                    extIds.add(extId1);
                    extIds.add(extId2);
                    entity.setExternalIdentifiers(extIds);
                    break;
                case "2":
                    entity.setName("2. Name");
                    entity.setCountry(Iso3166Country.CR);
                    entity.setCity("City#2");
                    entity.setRegion(null);
                    entity.setOrgType("type/2");
                    entity.setSourceParentId(null);
                    entity.setStatus(null);
                    
                    extId1 = new OrgDisambiguatedExternalIdentifierEntity();
                    extId1.setId(3L);
                    extId1.setOrgDisambiguated(entity);
                    extId1.setIdentifier("3");
                    extId1.setIdentifierType("NCES");
                    
                    extId2 = new OrgDisambiguatedExternalIdentifierEntity();
                    extId2.setId(4L);
                    extId2.setOrgDisambiguated(entity);
                    extId2.setIdentifier("4");
                    extId2.setIdentifierType("OFR");
                    
                    extIds = new HashSet<>();
                    extIds.add(extId1);
                    extIds.add(extId2);
                    entity.setExternalIdentifiers(extIds);
                    break;
                case "3":
                    entity.setName("3. Name");
                    entity.setCountry(Iso3166Country.US);
                    entity.setCity("City#3");
                    entity.setRegion(null);
                    entity.setOrgType("type/3");
                    entity.setSourceParentId(null);
                    entity.setStatus(null);
                    
                    extId1 = new OrgDisambiguatedExternalIdentifierEntity();
                    extId1.setId(5L);
                    extId1.setOrgDisambiguated(entity);
                    extId1.setIdentifier("5");
                    extId1.setIdentifierType("ISNI");
                    
                    extId2 = new OrgDisambiguatedExternalIdentifierEntity();
                    extId2.setId(6L);
                    extId2.setOrgDisambiguated(entity);
                    extId2.setIdentifier("6");
                    extId2.setIdentifierType("IPED");
                    
                    extIds = new HashSet<>();
                    extIds.add(extId1);
                    extIds.add(extId2);
                    entity.setExternalIdentifiers(extIds);
                    break;
                case "4":
                    entity.setName("4. Name");
                    entity.setCountry(Iso3166Country.CR);
                    entity.setCity("City#4");
                    entity.setRegion("State#4");
                    entity.setOrgType("type/4");
                    entity.setSourceParentId("1");
                    entity.setStatus(null);
                    
                    extId1 = new OrgDisambiguatedExternalIdentifierEntity();
                    extId1.setId(7L);
                    extId1.setOrgDisambiguated(entity);
                    extId1.setIdentifier("7");
                    extId1.setIdentifierType("NCES");
                    
                    extId2 = new OrgDisambiguatedExternalIdentifierEntity();
                    extId1.setId(8L);
                    extId2.setOrgDisambiguated(entity);
                    extId2.setIdentifier("8");
                    extId2.setIdentifierType("OFR");
                    
                    extIds = new HashSet<>();
                    extIds.add(extId1);
                    extIds.add(extId2);
                    entity.setExternalIdentifiers(extIds);
                    break;
                }
                return entity;
            }

        });
        
        when(mockOrgDao.findByNameCityRegionAndCountry(anyString(), anyString(), nullable(String.class), any()))
                .thenAnswer(new Answer<OrgEntity>() {

                    @Override
                    public OrgEntity answer(InvocationOnMock invocation) throws Throwable {
                        String name = invocation.getArgument(0);
                        String city = invocation.getArgument(1);
                        String region = invocation.getArgument(2);
                        Iso3166Country country = (Iso3166Country) invocation.getArgument(3);

                        OrgEntity entity = new OrgEntity();
                        entity.setName(name);
                        entity.setCity(city);
                        entity.setRegion(region);
                        entity.setCountry(country);

                        if ("1. Alt Name".equals(name) && "AltCity#1".equals(city) && region == null && Iso3166Country.MX.equals(country)) {
                            OrgDisambiguatedEntity od = new OrgDisambiguatedEntity();
                            // ringgold_id * 1000
                            od.setId(Long.valueOf(1000));
                            entity.setOrgDisambiguated(od);
                        } else if ("2. Alt Name".equals(name) && "AltCity#2".equals(city) && region == null && Iso3166Country.BR.equals(country)) {
                            OrgDisambiguatedEntity od = new OrgDisambiguatedEntity();
                            // ringgold_id * 1000
                            od.setId(Long.valueOf(2000));
                            entity.setOrgDisambiguated(od);
                        } else if("Testing Org".equals(name)) {
                            // This is an org not linked to any disambiguated org yet
                        } else {
                            return null;
                        }

                        return entity;
                    }

                });
    }
    
    @SuppressWarnings("serial")
    private class TransactionTemplateStub extends TransactionTemplate {
        @Override
        public <T> T execute(TransactionCallback<T> action) throws TransactionException {
            action.doInTransaction(null);
            return null;            
        }
    }
}
