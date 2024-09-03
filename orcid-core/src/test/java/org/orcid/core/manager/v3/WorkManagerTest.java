package org.orcid.core.manager.v3;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.BaseTest;
import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.contributors.roles.credit.CreditRole;
import org.orcid.core.exception.ExceedMaxNumberOfPutCodesException;
import org.orcid.core.exception.MissingGroupableExternalIDException;
import org.orcid.core.exception.OrcidCoreExceptionMapper;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.WorkEntityCacheManager;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.DateFieldsOnBaseEntityUtils;
import org.orcid.jaxb.model.common.CitationType;
import org.orcid.jaxb.model.common.ContributorRole;
import org.orcid.jaxb.model.common.Iso3166Country;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.SequenceType;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.Contributor;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.ContributorEmail;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.Country;
import org.orcid.jaxb.model.v3.release.common.CreatedDate;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.SourceClientId;
import org.orcid.jaxb.model.v3.release.common.Subtitle;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.record.Citation;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.jaxb.model.v3.release.record.WorkContributors;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.togglz.junit.TogglzRule;

public class WorkManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/WorksEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private static final String CLIENT_2_ID = "APP-5555555555555555";
    private static final String CLIENT_3_ID = "APP-5555555555555556";// obo

    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";

    @Resource(name = "workEntityCacheManager")
    private WorkEntityCacheManager workEntityCacheManager;
    
    @Resource(name = "workManagerV3")
    private WorkManager workManager;

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private WorkDao workDao;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter;

    @Mock
    private SourceManager mockSourceManager;
    
    @Mock
    private ClientDetailsManager mockClientDetailsManager;
    
    @Value("${org.orcid.core.works.bulk.read.max:100}")
    private Long bulkReadSize;

    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Captor
    private ArgumentCaptor<List<Long>> idsCaptor;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        
        ReflectionTestUtils.setField(orcidSecurityManager, "sourceManager", mockSourceManager);
        ReflectionTestUtils.setField(workManager, "sourceManager", mockSourceManager);
        
        // by default return client details entity with user obo disabled
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(new ClientDetailsEntity());
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", mockClientDetailsManager);
    }

    @After
    public void after() {
        ReflectionTestUtils.setField(workManager, "workEntityCacheManager", workEntityCacheManager);
        ReflectionTestUtils.setField(orcidSecurityManager, "sourceManager", sourceManager);
        ReflectionTestUtils.setField(workManager, "sourceManager", sourceManager);
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", clientDetailsManager);
        ReflectionTestUtils.setField(workManager, "workDao", workDao);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    public void testAddWorkToUnclaimedRecordPreserveWorkVisibility() {
        Work work = getWork(null);
        work.setVisibility(Visibility.PRIVATE);
        
        work = workManager.createWork(unclaimedOrcid, work, true);
        
        // Keeps incoming visibility
        assertEquals(Visibility.PRIVATE, work.getVisibility());
        assertEquals(CLIENT_1_ID, work.getSource().retrieveSourcePath());
    }

    @Test
    public void testAddWorkToClaimedRecordPreserveUserDefaultVisibility() {        
        Work work = getWork(null);
        work.setVisibility(Visibility.PRIVATE);
        
        work = workManager.createWork(claimedOrcid, work, true);

        // Respects user visibility
        assertEquals(Visibility.LIMITED, work.getVisibility());
        assertEquals(CLIENT_1_ID, work.getSource().retrieveSourcePath());
    }

    @Test
    public void displayIndexIsSetTo_0_FromUI() {
        Work w1 = getWork("fromUI-1");
        w1 = workManager.createWork(claimedOrcid, w1, false);
        WorkEntity w = workDao.find(w1.getPutCode());

        assertNotNull(w1);
        assertEquals(Long.valueOf(0), w.getDisplayIndex());
    }

    @Test
    public void displayIndexIsSetTo_1_FromAPI() {        
        Work w1 = getWork("fromAPI-1");
        w1 = workManager.createWork(claimedOrcid, w1, true);
        WorkEntity w = workDao.find(w1.getPutCode());

        assertNotNull(w1);
        assertEquals(Long.valueOf(1), w.getDisplayIndex());
    }

    @Test
    public void testCreateWork() {
        String orcid = "0000-0000-0000-0003";        

        Work work = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID extId1 = new ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("isbn");
        extId1.setUrl(new Url("http://isbn/1/"));
        extId1.setValue("isbn-1");
        extIds1.getExternalIdentifier().add(extId1);
        work.setWorkExternalIdentifiers(extIds1);
        work.setWorkType(WorkType.BOOK);
        assertNull(work.getPutCode());
        work = workManager.createWork(orcid, work, true);
        assertNotNull(work.getPutCode());
        assertEquals(CLIENT_1_ID, work.getSource().retrieveSourcePath());
        workDao.remove(work.getPutCode());
    }

    @Test
    public void testUpdateWork() {
        String orcid = "0000-0000-0000-0003";        

        Work work = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID extId1 = new ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("isbn");
        extId1.setUrl(new Url("http://isbn/1/"));
        extId1.setValue("isbn-1");
        extIds1.getExternalIdentifier().add(extId1);
        work.setWorkExternalIdentifiers(extIds1);
        work.setWorkType(WorkType.BOOK);
        work.setVisibility(Visibility.PUBLIC);
        work = workManager.createWork(orcid, work, true);

        work.getWorkTitle().getTitle().setContent("updated title");
        work = workManager.updateWork(orcid, work, true);
        assertEquals("updated title", work.getWorkTitle().getTitle().getContent());

        workDao.remove(work.getPutCode());
    }
           
    @Test
    public void testCreateDupWorkTest() {
        String orcid = "0000-0000-0000-0003";
        Long time = System.currentTimeMillis();
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(CLIENT_1_ID));
        when(mockSourceManager.retrieveActiveSource()).thenReturn(source);
        
        // Work # 1
        Work work1 = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID extId1 = new ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("isbn");
        extId1.setUrl(new Url("http://isbn/1/" + time));
        extId1.setValue("isbn-1");
        extIds1.getExternalIdentifier().add(extId1);
        work1.setWorkExternalIdentifiers(extIds1);
        work1.setWorkType(WorkType.BOOK);     
        
        // Create the original work
        work1 = workManager.createWork(orcid, work1, true);
        assertNotNull(work1);
        assertNotNull(work1.getPutCode());
        Long putCode1 = work1.getPutCode();
        
        // Create the work again but with a VERSION_OF identifier
        work1.setPutCode(null);
        work1.getExternalIdentifiers().getExternalIdentifier().get(0).setRelationship(Relationship.VERSION_OF);
        // For VERSION_OF we should add one SELF identifier
        ExternalID extId2 = new ExternalID();
        extId2.setRelationship(Relationship.SELF);
        extId2.setType("arxiv");
        extId2.setUrl(new Url("http://arxiv/1/" + time));
        extId2.setValue("arxiv-1");
        work1.getExternalIdentifiers().getExternalIdentifier().add(extId2);
        
        work1 = workManager.createWork(orcid, work1, true);
        assertNotNull(work1);
        assertNotNull(work1.getPutCode());
        assertNotEquals(putCode1, work1.getPutCode());
        
        Long putCode2 = work1.getPutCode();
        
        // Remove the extra identifier
        work1.getExternalIdentifiers().getExternalIdentifier().remove(1);
        
        // Now try to create it with the same SELF identifier, should fail
        work1.setPutCode(null);
        work1.getExternalIdentifiers().getExternalIdentifier().get(0).setRelationship(Relationship.SELF);
        
        try {
            work1 = workManager.createWork(orcid, work1, true);
            fail();
        } catch(OrcidDuplicatedActivityException e) {
            
        } catch(Exception e) {
            fail(e.getMessage());
        }
        
        // Now create another with a SELF identifier but not the same one, should work
        work1.setPutCode(null);
        work1.getExternalIdentifiers().getExternalIdentifier().get(0).setRelationship(Relationship.SELF);
        work1.getExternalIdentifiers().getExternalIdentifier().get(0).setValue("isbn-2");
        
        work1 = workManager.createWork(orcid, work1, true);
        assertNotNull(work1);
        assertNotNull(work1.getPutCode());
        assertNotEquals(putCode1, work1.getPutCode());
        assertNotEquals(putCode2, work1.getPutCode());
        
        Long putCode4 = work1.getPutCode();
        
        workManager.removeWorks(orcid, Arrays.asList(putCode1, putCode2, putCode2, putCode4));
    }
    
    @Test
    public void testCreateWorksWithBulk_OneSelfOneVersionOf_NoDupError() {
        String orcid = "0000-0000-0000-0003";
        Long time = System.currentTimeMillis();
        Source source = new Source();
        source.setSourceClientId(new SourceClientId(CLIENT_1_ID));
        when(mockSourceManager.retrieveActiveSource()).thenReturn(source);
        
        WorkBulk bulk = new WorkBulk();
        // Work # 1
        Work work1 = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID extId1 = new ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("isbn");
        extId1.setUrl(new Url("http://isbn/1/" + time));
        extId1.setValue("isbn-1");
        extIds1.getExternalIdentifier().add(extId1);
        work1.setWorkExternalIdentifiers(extIds1);
        work1.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work1);
        
        // Work # 2
        Work work2 = new Work();
        WorkTitle title2 = new WorkTitle();
        title2.setTitle(new Title("Work # 2"));
        work2.setWorkTitle(title2);
        ExternalIDs extIds2 = new ExternalIDs();
        ExternalID extId2 = new ExternalID();
        extId2.setRelationship(Relationship.VERSION_OF);
        extId2.setType("isbn");
        extId2.setUrl(new Url("http://isbn/1/" + time));
        extId2.setValue("isbn-1");
        ExternalID extId3 = new ExternalID();
        extId3.setRelationship(Relationship.SELF);
        extId3.setType("doi");
        extId3.setUrl(new Url("http://doi/1/" + time));
        extId3.setValue("doi-1");
        
        extIds2.getExternalIdentifier().add(extId2);
        extIds2.getExternalIdentifier().add(extId3);
        
        work2.setWorkExternalIdentifiers(extIds2);
        work2.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work2);
                
        WorkBulk updatedBulk = workManager.createWorks(orcid, bulk);
        
        assertNotNull(updatedBulk);
        assertEquals(2, updatedBulk.getBulk().size());
        assertTrue(updatedBulk.getBulk().get(0) instanceof Work);
        assertNotNull(((Work)updatedBulk.getBulk().get(0)).getPutCode());
        assertEquals(Relationship.SELF, ((Work)updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("isbn-1", ((Work)updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        
        assertTrue(updatedBulk.getBulk().get(1) instanceof Work);
        assertNotNull(((Work)updatedBulk.getBulk().get(1)).getPutCode());
        assertEquals(Relationship.VERSION_OF, ((Work)updatedBulk.getBulk().get(1)).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("isbn-1", ((Work)updatedBulk.getBulk().get(1)).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());      
        assertEquals(Relationship.SELF, ((Work)updatedBulk.getBulk().get(1)).getExternalIdentifiers().getExternalIdentifier().get(1).getRelationship());
        assertEquals("doi-1", ((Work)updatedBulk.getBulk().get(1)).getExternalIdentifiers().getExternalIdentifier().get(1).getValue());              
    
        workManager.removeWorks(orcid, Arrays.asList(((Work)updatedBulk.getBulk().get(0)).getPutCode(), ((Work)updatedBulk.getBulk().get(1)).getPutCode()));
    }
    
    @Test
    public void testCreateWorksWithBulk_OneSelfOnePartOf_NoDupError() {
        String orcid = "0000-0000-0000-0003";
        Long time = System.currentTimeMillis();        

        WorkBulk bulk = new WorkBulk();
        // Work # 1
        Work work1 = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID extId1 = new ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("isbn");
        extId1.setUrl(new Url("http://isbn/1/" + time));
        extId1.setValue("isbn-1");
        extIds1.getExternalIdentifier().add(extId1);
        work1.setWorkExternalIdentifiers(extIds1);
        work1.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work1);

        // Work # 2
        Work work2 = new Work();
        WorkTitle title2 = new WorkTitle();
        title2.setTitle(new Title("Work # 2"));
        work2.setWorkTitle(title2);
        ExternalIDs extIds2 = new ExternalIDs();
        ExternalID extId2 = new ExternalID();
        extId2.setRelationship(Relationship.PART_OF);
        extId2.setType("isbn");
        extId2.setUrl(new Url("http://isbn/1/" + time));
        extId2.setValue("isbn-1");
        extIds2.getExternalIdentifier().add(extId2);
        
        ExternalID selfId = new ExternalID();
        selfId.setRelationship(Relationship.SELF);
        selfId.setType("doi");
        selfId.setUrl(new Url("http://doi.org/something" + time));
        selfId.setValue("something");
        extIds2.getExternalIdentifier().add(selfId);
        
        work2.setWorkExternalIdentifiers(extIds2);
        work2.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work2);

        WorkBulk updatedBulk = workManager.createWorks(orcid, bulk);

        assertNotNull(updatedBulk);
        assertEquals(2, updatedBulk.getBulk().size());
        assertTrue(updatedBulk.getBulk().get(0) instanceof Work);
        assertNotNull(((Work) updatedBulk.getBulk().get(0)).getPutCode());
        assertEquals(Relationship.SELF, ((Work) updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("isbn-1", ((Work) updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());

        assertTrue(updatedBulk.getBulk().get(1) instanceof Work);
        assertNotNull(((Work) updatedBulk.getBulk().get(1)).getPutCode());
        assertEquals(Relationship.PART_OF, ((Work) updatedBulk.getBulk().get(1)).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("isbn-1", ((Work) updatedBulk.getBulk().get(1)).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());

        workManager.removeWorks(orcid, Arrays.asList(((Work) updatedBulk.getBulk().get(0)).getPutCode(), ((Work) updatedBulk.getBulk().get(1)).getPutCode()));
    }

    @Test
    public void testCreateWorksWithBulk_OneSelfExisting_OnePartOfNew_NoDupError() throws IllegalAccessException {        
        String orcid = "0000-0000-0000-0003";
        
        WorkDao mockDao = Mockito.mock(WorkDao.class);
        ReflectionTestUtils.setField(workManager, "workDao", mockDao);
        
        MinimizedWorkEntity work = getBasicMinimizedWork();
        work.setDisplayIndex(1L);
        work.setId(10000L);
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\", \"workExternalIdentifierType\":\"ISBN\",\"workExternalIdentifierId\":{\"content\":\"1234\"}}]}");
        work.setClientSourceId(CLIENT_1_ID);        
        
        WorkEntityCacheManager cacheManagerMock = Mockito.mock(WorkEntityCacheManager.class);
        // no work where user is source
        List<MinimizedWorkEntity> works = new ArrayList<>();
        works.add(work);
        Mockito.when(cacheManagerMock.retrieveMinimizedWorks(Mockito.anyString(), Mockito.anyLong())).thenReturn(works);
        ReflectionTestUtils.setField(workManager, "workEntityCacheManager", cacheManagerMock);

        WorkBulk bulk = new WorkBulk();
        // Work # 1
        Work work1 = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID partOfExtId1 = new ExternalID();
        partOfExtId1.setRelationship(Relationship.PART_OF);
        partOfExtId1.setType("isbn");
        partOfExtId1.setValue("1234");
        ExternalID selfExtId1 = new ExternalID();
        selfExtId1.setRelationship(Relationship.SELF);
        selfExtId1.setType("doi");
        selfExtId1.setValue("1234");
        
        extIds1.getExternalIdentifier().add(partOfExtId1);
        extIds1.getExternalIdentifier().add(selfExtId1);
        work1.setWorkExternalIdentifiers(extIds1);
        work1.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work1);

        //Verify the work doesnt have source yet
        assertNull(work1.getSource());
        WorkBulk newBulk = workManager.createWorks(orcid, bulk);
                
        //To verify the work was created, verify it have a source
        assertEquals(1, newBulk.getBulk().size());
        assertEquals(CLIENT_1_ID, ((Work) newBulk.getBulk().get(0)).getSource().getSourceClientId().getPath());
    }
    
    @Test
    public void testCreateWorksWithBulk_OnePartOfExisting_OneSelfNew_NoDupError() throws IllegalAccessException {
        String orcid = "0000-0000-0000-0003";
        
        WorkDao mockDao = Mockito.mock(WorkDao.class);
        ReflectionTestUtils.setField(workManager, "workDao", mockDao);
        
        MinimizedWorkEntity work = getBasicMinimizedWork();
        work.setDisplayIndex(1L);
        work.setId(10000L);
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"PART_OF\", \"workExternalIdentifierType\":\"ISBN\",\"workExternalIdentifierId\":{\"content\":\"1234\"}}]}");
        work.setClientSourceId(CLIENT_1_ID);        
        
        WorkEntityCacheManager cacheManagerMock = Mockito.mock(WorkEntityCacheManager.class);
        // no work where user is source
        List<MinimizedWorkEntity> works = new ArrayList<>();
        works.add(work);
        Mockito.when(cacheManagerMock.retrieveMinimizedWorks(Mockito.anyString(), Mockito.anyLong())).thenReturn(works);
        ReflectionTestUtils.setField(workManager, "workEntityCacheManager", cacheManagerMock);

        WorkBulk bulk = new WorkBulk();
        // Work # 1
        Work work1 = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID selfExtId1 = new ExternalID();
        selfExtId1.setRelationship(Relationship.SELF);
        selfExtId1.setType("isbn");
        selfExtId1.setValue("1234");
        
        extIds1.getExternalIdentifier().add(selfExtId1);
        work1.setWorkExternalIdentifiers(extIds1);
        work1.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work1);

        //Verify the work doesnt have source yet
        assertNull(work1.getSource());
        WorkBulk newBulk = workManager.createWorks(orcid, bulk);
                
        //To verify the work was created, verify it have a source
        assertEquals(1, newBulk.getBulk().size());
        assertEquals(CLIENT_1_ID, ((Work) newBulk.getBulk().get(0)).getSource().getSourceClientId().getPath());
    }
    
    @Test
    public void testCreateWorksWithBulk_OneSelfExisting_OneSelfNew_DupError() throws IllegalAccessException {
        String orcid = "0000-0000-0000-0003";
        
        WorkDao mockDao = Mockito.mock(WorkDao.class);
        ReflectionTestUtils.setField(workManager, "workDao", mockDao);
        
        MinimizedWorkEntity work = getBasicMinimizedWork();
        work.setDisplayIndex(1L);
        work.setId(10000L);
        work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[{\"relationship\":\"SELF\", \"workExternalIdentifierType\":\"ISBN\",\"workExternalIdentifierId\":{\"content\":\"1234\"}}]}");
        work.setClientSourceId(CLIENT_1_ID);        
        
        WorkEntityCacheManager cacheManagerMock = Mockito.mock(WorkEntityCacheManager.class);
        // no work where user is source
        List<MinimizedWorkEntity> works = new ArrayList<>();
        works.add(work);
        Mockito.when(cacheManagerMock.retrieveMinimizedWorks(Mockito.anyString(), Mockito.anyLong())).thenReturn(works);
        ReflectionTestUtils.setField(workManager, "workEntityCacheManager", cacheManagerMock);

        WorkBulk bulk = new WorkBulk();
        // Work # 1
        Work work1 = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID selfExtId1 = new ExternalID();
        selfExtId1.setRelationship(Relationship.SELF);
        selfExtId1.setType("isbn");
        selfExtId1.setValue("1234");
        
        extIds1.getExternalIdentifier().add(selfExtId1);
        work1.setWorkExternalIdentifiers(extIds1);
        work1.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work1);

        WorkBulk newBulk = workManager.createWorks(orcid, bulk);
                
        //Verify it returns a dup error
        assertEquals(1, newBulk.getBulk().size());
        OrcidError error = (OrcidError) newBulk.getBulk().get(0);
        assertNotNull(error);
        assertEquals(Integer.valueOf(9021), error.getErrorCode());
        assertEquals("409 Conflict: You have already added this activity (matched by external identifiers), please see element with put-code 10000. If you are trying to edit the item, please use PUT instead of POST.", error.getDeveloperMessage());
    }
    
    @Test
    public void testCreateWorkWithBulk_TwoSelf_DupError() {
        OrcidCoreExceptionMapper orcidCoreExceptionMapper = Mockito.mock(OrcidCoreExceptionMapper.class);        

        ReflectionTestUtils.setField(workManager, "orcidCoreExceptionMapper", orcidCoreExceptionMapper);        

        org.orcid.jaxb.model.v3.release.error.OrcidError orcidError = new org.orcid.jaxb.model.v3.release.error.OrcidError();
        orcidError.setErrorCode(9021);
        orcidError.setDeveloperMessage("409 Conflict: You have already added this activity (matched by external identifiers), please see element with put-code ${putCode}. If you are trying to edit the item, please use PUT instead of POST.");
        Mockito.when(orcidCoreExceptionMapper.getV3OrcidError(Mockito.any())).thenReturn(orcidError);

        
        String orcid = "0000-0000-0000-0003";
        Long time = System.currentTimeMillis();        

        WorkBulk bulk = new WorkBulk();
        // Work # 1
        Work work1 = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID extId1 = new ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("isbn");
        extId1.setUrl(new Url("http://isbn/1/" + time));
        extId1.setValue("isbn-1");
        extIds1.getExternalIdentifier().add(extId1);
        work1.setWorkExternalIdentifiers(extIds1);
        work1.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work1);

        // Work # 2
        Work work2 = new Work();
        WorkTitle title2 = new WorkTitle();
        title2.setTitle(new Title("Work # 2"));
        work2.setWorkTitle(title2);
        ExternalIDs extIds2 = new ExternalIDs();
        ExternalID extId2 = new ExternalID();
        extId2.setRelationship(Relationship.SELF);
        extId2.setType("isbn");
        extId2.setUrl(new Url("http://isbn/1/" + time));
        extId2.setValue("isbn-1");
        extIds2.getExternalIdentifier().add(extId2);
        work2.setWorkExternalIdentifiers(extIds2);
        work2.setWorkType(WorkType.BOOK);
        bulk.getBulk().add(work2);

        WorkBulk updatedBulk = workManager.createWorks(orcid, bulk);

        assertNotNull(updatedBulk);
        assertEquals(2, updatedBulk.getBulk().size());
        assertTrue(updatedBulk.getBulk().get(0) instanceof Work);
        assertNotNull(((Work) updatedBulk.getBulk().get(0)).getPutCode());
        assertEquals(Relationship.SELF, ((Work) updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("isbn-1", ((Work) updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());

        assertTrue(updatedBulk.getBulk().get(1) instanceof OrcidError);
        assertEquals(Integer.valueOf(9021), ((OrcidError) updatedBulk.getBulk().get(1)).getErrorCode());
        assertEquals(
                "409 Conflict: You have already added this activity (matched by external identifiers), please see element with put-code ${putCode}. If you are trying to edit the item, please use PUT instead of POST.",
                ((OrcidError) updatedBulk.getBulk().get(1)).getDeveloperMessage());

        workManager.removeWorks(orcid, Arrays.asList(((Work) updatedBulk.getBulk().get(0)).getPutCode()));
    }

    @Test
    public void testCreateWorkWithBulk_TwoSelf_DupError_CaseSensitive() {
        OrcidCoreExceptionMapper orcidCoreExceptionMapper = Mockito.mock(OrcidCoreExceptionMapper.class);        

        ReflectionTestUtils.setField(workManager, "orcidCoreExceptionMapper", orcidCoreExceptionMapper);        

        org.orcid.jaxb.model.v3.release.error.OrcidError orcidError = new org.orcid.jaxb.model.v3.release.error.OrcidError();
        orcidError.setErrorCode(9021);
        orcidError.setDeveloperMessage("409 Conflict: You have already added this activity (matched by external identifiers), please see element with put-code ${putCode}. If you are trying to edit the item, please use PUT instead of POST.");
        Mockito.when(orcidCoreExceptionMapper.getV3OrcidError(Mockito.any())).thenReturn(orcidError);
        
        String orcid = "0000-0000-0000-0003";
        Long time = System.currentTimeMillis();        

        WorkBulk bulk = new WorkBulk();
        // Work # 1
        Work work1 = new Work();
        WorkTitle title1 = new WorkTitle();
        title1.setTitle(new Title("Work # 1"));
        work1.setWorkTitle(title1);
        ExternalIDs extIds1 = new ExternalIDs();
        ExternalID extId1 = new ExternalID();
        extId1.setRelationship(Relationship.SELF);
        extId1.setType("doi");
        extId1.setUrl(new Url("http://doi.org/10.1/CASE" + time));
        extId1.setValue("10.1/CASE");
        extIds1.getExternalIdentifier().add(extId1);
        work1.setWorkExternalIdentifiers(extIds1);
        work1.setWorkType(WorkType.JOURNAL_ARTICLE);
        bulk.getBulk().add(work1);

        // Work # 2
        Work work2 = new Work();
        WorkTitle title2 = new WorkTitle();
        title2.setTitle(new Title("Work # 2"));
        work2.setWorkTitle(title2);
        ExternalIDs extIds2 = new ExternalIDs();
        ExternalID extId2 = new ExternalID();
        extId2.setRelationship(Relationship.SELF);
        extId2.setType("doi");
        extId2.setUrl(new Url("http://doi.org/10.1/case" + time));
        extId2.setValue("10.1/case"); // this should fail as dois are not case
                                      // sensitive
        extIds2.getExternalIdentifier().add(extId2);
        work2.setWorkExternalIdentifiers(extIds2);
        work2.setWorkType(WorkType.JOURNAL_ARTICLE);
        bulk.getBulk().add(work2);

        WorkBulk updatedBulk = workManager.createWorks(orcid, bulk);

        assertNotNull(updatedBulk);
        assertEquals(2, updatedBulk.getBulk().size());
        assertTrue(updatedBulk.getBulk().get(0) instanceof Work);
        assertNotNull(((Work) updatedBulk.getBulk().get(0)).getPutCode());
        assertEquals(Relationship.SELF, ((Work) updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getRelationship());
        assertEquals("10.1/CASE", ((Work) updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("10.1/case", ((Work) updatedBulk.getBulk().get(0)).getExternalIdentifiers().getExternalIdentifier().get(0).getNormalized().getValue());

        assertTrue(updatedBulk.getBulk().get(1) instanceof OrcidError);
        assertEquals(Integer.valueOf(9021), ((OrcidError) updatedBulk.getBulk().get(1)).getErrorCode());
        assertEquals(
                "409 Conflict: You have already added this activity (matched by external identifiers), please see element with put-code ${putCode}. If you are trying to edit the item, please use PUT instead of POST.",
                ((OrcidError) updatedBulk.getBulk().get(1)).getDeveloperMessage());

        workManager.removeWorks(orcid, Arrays.asList(((Work) updatedBulk.getBulk().get(0)).getPutCode()));
    }

    @Test
    public void testCreateWorksWithBulkAllOK() {
        String orcid = "0000-0000-0000-0003";
        Long time = System.currentTimeMillis();        

        WorkBulk bulk = new WorkBulk();
        for (int i = 0; i < 5; i++) {
            Work work = new Work();
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Bulk work " + i + " " + time));
            work.setWorkTitle(title);

            ExternalIDs extIds = new ExternalIDs();
            ExternalID extId = new ExternalID();
            extId.setRelationship(Relationship.SELF);
            extId.setType("doi");
            extId.setUrl(new Url("http://doi/" + i + "/" + time));
            extId.setValue("doi-" + i + "-" + time);
            extIds.getExternalIdentifier().add(extId);
            work.setWorkExternalIdentifiers(extIds);

            work.setWorkType(WorkType.BOOK);
            bulk.getBulk().add(work);
        }

        bulk = workManager.createWorks(orcid, bulk);

        assertNotNull(bulk);
        assertEquals(5, bulk.getBulk().size());

        for (int i = 0; i < 5; i++) {
            assertTrue(Work.class.isAssignableFrom(bulk.getBulk().get(i).getClass()));
            Work w = (Work) bulk.getBulk().get(i);
            assertNotNull(w.getPutCode());
            assertTrue(0L < w.getPutCode());
            assertEquals("Bulk work " + i + " " + time, w.getWorkTitle().getTitle().getContent());
            assertNotNull(w.getExternalIdentifiers().getExternalIdentifier());
            assertEquals("doi-" + i + "-" + time, w.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());

            Work w1 = workManager.getWork(orcid, w.getPutCode());
            assertNotNull(w1);
            assertEquals("Bulk work " + i + " " + time, w1.getWorkTitle().getTitle().getContent());

            // Delete the work
            assertTrue(workManager.checkSourceAndRemoveWork(orcid, w1.getPutCode()));
        }
    }

    @Test
    public void testCreateWorksWithBulkSomeOKSomeErrors() {
        
        OrcidCoreExceptionMapper orcidCoreExceptionMapper = Mockito.mock(OrcidCoreExceptionMapper.class);        

        ReflectionTestUtils.setField(workManager, "orcidCoreExceptionMapper", orcidCoreExceptionMapper);        

        org.orcid.jaxb.model.v3.release.error.OrcidError orcidErrorActivityTitleValidation = new org.orcid.jaxb.model.v3.release.error.OrcidError();
        orcidErrorActivityTitleValidation.setErrorCode(9022);
        
        org.orcid.jaxb.model.v3.release.error.OrcidError orcidErrorOrcidDuplicatedActivity = new org.orcid.jaxb.model.v3.release.error.OrcidError();
        orcidErrorOrcidDuplicatedActivity.setErrorCode(9021);
        
        Mockito.when(orcidCoreExceptionMapper.getV3OrcidError(Mockito.any()))
            .thenReturn(orcidErrorOrcidDuplicatedActivity)
            .thenReturn(orcidErrorOrcidDuplicatedActivity)
            .thenReturn(orcidErrorActivityTitleValidation);
        
        String orcid = "0000-0000-0000-0003";
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_2_ID));

        // Lets send a bulk of 6 works
        WorkBulk bulk = new WorkBulk();

        // Work # 1 - Fine
        Work work1 = getWork(null);
        work1.getExternalIdentifiers().getExternalIdentifier().clear();
        work1.getExternalIdentifiers().getExternalIdentifier().add(getDuplicateExternalId());
        work1.getWorkTitle().getTitle().setContent("Work # 1");
        bulk.getBulk().add(work1);

        // Work # 2 - Fine
        Work work2 = getWork(null);
        work2.getWorkTitle().getTitle().setContent("Work # 2");
        bulk.getBulk().add(work2);

        // Work # 3 - Duplicated of Work # 1
        Work work3 = getWork(null);
        work3.getExternalIdentifiers().getExternalIdentifier().clear();
        work3.getExternalIdentifiers().getExternalIdentifier().add(getDuplicateExternalId());
        work3.getWorkTitle().getTitle().setContent("Work # 3");
        bulk.getBulk().add(work3);

        // Work # 4 - Fine
        Work work4 = getWork("new-ext-id-" + System.currentTimeMillis());
        work4.getWorkTitle().getTitle().setContent("Work # 4");
        bulk.getBulk().add(work4);

        // Work # 5 - Duplicated of existing work
        Work work5 = getWork(null);
        ExternalID dupExtId = new ExternalID();
        dupExtId.setRelationship(Relationship.SELF);
        dupExtId.setType("doi");
        dupExtId.setValue("1");
        work5.getExternalIdentifiers().getExternalIdentifier().clear();
        work5.getExternalIdentifiers().getExternalIdentifier().add(dupExtId);
        work5.getWorkTitle().getTitle().setContent("Work # 5");
        bulk.getBulk().add(work5);

        // Work # 6 - No title specified
        Work work6 = getWork(null);
        work6.getWorkTitle().getTitle().setContent(null);
        bulk.getBulk().add(work6);

        bulk = workManager.createWorks(orcid, bulk);

        assertNotNull(bulk);
        assertEquals(6, bulk.getBulk().size());

        List<Long> worksToDelete = new ArrayList<Long>();

        for (int i = 0; i < bulk.getBulk().size(); i++) {
            BulkElement element = bulk.getBulk().get(i);
            switch (i) {
            case 0:
            case 1:
            case 3:
                assertTrue(Work.class.isAssignableFrom(element.getClass()));
                Work work = (Work) element;
                assertNotNull(work);
                assertNotNull(work.getPutCode());
                if (i == 0) {
                    assertEquals("Work # 1", work.getWorkTitle().getTitle().getContent());
                } else if (i == 1) {
                    assertEquals("Work # 2", work.getWorkTitle().getTitle().getContent());
                } else {
                    assertEquals("Work # 4", work.getWorkTitle().getTitle().getContent());
                }
                worksToDelete.add(work.getPutCode());
                break;
            case 2:
            case 4:
            case 5:
                assertTrue("Error on id: " + i, OrcidError.class.isAssignableFrom(element.getClass()));
                OrcidError error = (OrcidError) element;
                if (i == 2) {
                    assertEquals(Integer.valueOf(9021), error.getErrorCode());
                } else if (i == 4) {
                    assertEquals(Integer.valueOf(9021), error.getErrorCode());
                } else {
                    assertEquals(Integer.valueOf(9022), error.getErrorCode());
                }
                break;
            }
        }

        // Delete new works
        for (Long putCode : worksToDelete) {
            assertTrue(workManager.removeWorks(orcid, Arrays.asList(putCode)));
        }
    }

    @Test
    public void testCreateWorksWithBulkAllErrors() throws InterruptedException {
        String orcid = "0000-0000-0000-0003";        

        // Set up data:
        // Create one work with a DOI doi-1 so we can create a duplicate
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work #1"));
        work.setWorkTitle(workTitle);

        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(getDuplicateExternalId());
        work.setWorkExternalIdentifiers(extIds);

        work.setWorkType(WorkType.BOOK);

        Work newWork = workManager.createWork(orcid, work, true);
        Long putCode = newWork.getPutCode();

        WorkBulk bulk = new WorkBulk();
        // Work # 1: No ext ids
        Work work1 = getWork("work # 1 " + System.currentTimeMillis());
        work1.getExternalIdentifiers().getExternalIdentifier().clear();

        // Work # 2: No title
        Work work2 = getWork("work # 2 " + System.currentTimeMillis());
        work2.getWorkTitle().getTitle().setContent(null);

        // Work # 3: No work type
        Work work3 = getWork("work # 3 " + System.currentTimeMillis());
        work3.setWorkType(null);

        // Work # 4: Ext id already exists
        Work work4 = getWork("work # 4 " + System.currentTimeMillis());
        work4.getExternalIdentifiers().getExternalIdentifier().add(getDuplicateExternalId());

        bulk.getBulk().add(work1);
        bulk.getBulk().add(work2);
        bulk.getBulk().add(work3);
        bulk.getBulk().add(work4);

        bulk = workManager.createWorks(orcid, bulk);

        assertNotNull(bulk);
        assertEquals(4, bulk.getBulk().size());

        for (int i = 0; i < bulk.getBulk().size(); i++) {
            BulkElement element = bulk.getBulk().get(i);
            assertTrue(OrcidError.class.isAssignableFrom(element.getClass()));
            OrcidError error = (OrcidError) element;
            switch (i) {
            case 0:
                assertEquals(Integer.valueOf(9023), error.getErrorCode());
                break;
            case 1:
                assertEquals(Integer.valueOf(9022), error.getErrorCode());
                break;
            case 2:
                assertEquals(Integer.valueOf(9037), error.getErrorCode());
                break;
            case 3:
                assertEquals(Integer.valueOf(9021), error.getErrorCode());
                break;
            }
        }

        // Delete the work
        assertTrue(workManager.removeWorks(orcid, Arrays.asList(putCode)));
    }

    private ExternalID getDuplicateExternalId() {
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://doi/1"));
        extId.setValue("doi-1");
        return extId;
    }

    @Test
    public void testGroupWorks() throws DatatypeConfigurationException {
        /**
         * @formatter:off
         * They should be grouped as
         * 
         * Group 1: Work 1 + Work 4
         * Group 2: Work 2 + Work 5
         * Group 3: Work 3
         * Group 4: Work 6
         * @formatter:on
         */
        WorkSummary s1 = getWorkSummary("Work 1", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s2 = getWorkSummary("Work 2", "ext-id-2", Visibility.LIMITED);
        WorkSummary s3 = getWorkSummary("Work 3", "ext-id-3", Visibility.PRIVATE);
        WorkSummary s4 = getWorkSummary("Work 4", "ext-id-1", Visibility.PRIVATE);
        WorkSummary s5 = getWorkSummary("Work 5", "ext-id-2", Visibility.PUBLIC);
        WorkSummary s6 = getWorkSummary("Work 6", "ext-id-4", Visibility.PRIVATE);

        List<WorkSummary> workList1 = Arrays.asList(s1, s2, s3, s4, s5, s6);

        Works works1 = workManager.groupWorks(workList1, false);
        assertNotNull(works1);
        assertEquals(4, works1.getWorkGroup().size());
        // Group 1 have all with ext-id-1
        assertEquals(2, works1.getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", works1.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());

        // Group 2 have all with ext-id-2
        assertEquals(2, works1.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", works1.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());

        // Group 3 have ext-id-3
        assertEquals(1, works1.getWorkGroup().get(2).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-3", works1.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());

        // Group 4 have ext-id-4
        assertEquals(1, works1.getWorkGroup().get(3).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(3).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-4", works1.getWorkGroup().get(3).getIdentifiers().getExternalIdentifier().get(0).getValue());

        WorkSummary s7 = getWorkSummary("Work 7", "ext-id-4", Visibility.PRIVATE);
        // Add ext-id-3 to work 7, so, it join group 3 and group 4 in a single
        // group
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setValue("ext-id-3");
        s7.getExternalIdentifiers().getExternalIdentifier().add(extId);

        /**
         * @formatter:off
         * Now, they should be grouped as
         * 
         * Group 1: Work 1 + Work 4
         * Group 2: Work 2 + Work 5
         * Group 3: Work 3 + Work 6 + Work 7
         * @formatter:on
         */
        List<WorkSummary> workList2 = Arrays.asList(s1, s2, s3, s4, s5, s6, s7);

        Works works2 = workManager.groupWorks(workList2, false);
        assertNotNull(works2);
        assertEquals(3, works2.getWorkGroup().size());
        // Group 1 have all with ext-id-1
        assertEquals(2, works2.getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(1, works2.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", works2.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());

        // Group 2 have all with ext-id-2
        assertEquals(2, works2.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals(1, works2.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", works2.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());

        // Group 3 have all with ext-id-3 and ext-id-4
        assertEquals(3, works2.getWorkGroup().get(2).getWorkSummary().size());
        assertEquals(2, works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertThat(works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue(), anyOf(is("ext-id-3"), is("ext-id-4")));
        assertThat(works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(1).getValue(), anyOf(is("ext-id-3"), is("ext-id-4")));
    }

    @Test
    public void testGroupWorksWithDisplayIndex() throws DatatypeConfigurationException {
        /**
         * @formatter:off
         * They should be grouped as
         * 
         * Group 1: Work 3
         * Group 2: Work 4 + Work 1
         * Group 3: Work 5 + Work 2
         * Group 4: Work 6
         * @formatter:on
         */
        WorkSummary s1 = getWorkSummary("Work 1", "ext-id-1", Visibility.PUBLIC, "0");
        WorkSummary s2 = getWorkSummary("Work 2", "ext-id-2", Visibility.LIMITED, "1");
        WorkSummary s3 = getWorkSummary("Work 3", "ext-id-3", Visibility.PRIVATE, "0");
        WorkSummary s4 = getWorkSummary("Work 4", "ext-id-1", Visibility.PRIVATE, "1");
        WorkSummary s5 = getWorkSummary("Work 5", "ext-id-2", Visibility.PUBLIC, "2");
        WorkSummary s6 = getWorkSummary("Work 6", "ext-id-4", Visibility.PRIVATE, "0");

        List<WorkSummary> workList1 = Arrays.asList(s1, s2, s3, s4, s5, s6);

        Works works1 = workManager.groupWorks(workList1, false);
        assertNotNull(works1);
        assertEquals(4, works1.getWorkGroup().size());
        // Group 1 have all with ext-id-3
        assertEquals(1, works1.getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-3", works1.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());

        // Group 2 have all with ext-id-2
        assertEquals(2, works1.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", works1.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());

        // Group 3 have ext-id-3
        assertEquals(2, works1.getWorkGroup().get(2).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", works1.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());

        // Group 4 have ext-id-4
        assertEquals(1, works1.getWorkGroup().get(3).getWorkSummary().size());
        assertEquals(1, works1.getWorkGroup().get(3).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-4", works1.getWorkGroup().get(3).getIdentifiers().getExternalIdentifier().get(0).getValue());

        WorkSummary s7 = getWorkSummary("Work 7", "ext-id-4", Visibility.PRIVATE, "1");
        // Add ext-id-3 to work 7, so, it join group 3 and group 4 in a single
        // group
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setValue("ext-id-3");
        s7.getExternalIdentifiers().getExternalIdentifier().add(extId);

        /**
         * @formatter:off
         * Now, they should be grouped as
         * 
         * 
         * Group 1: Work 3 + Work 6 + Work 7
         * Group 2: Work 4 + Work 1
         * Group 3: Work 5 + Work 2
         * @formatter:on
         */
        List<WorkSummary> workList2 = Arrays.asList(s1, s2, s3, s4, s5, s6, s7);

        Works works2 = workManager.groupWorks(workList2, false);
        assertNotNull(works2);
        assertEquals(3, works2.getWorkGroup().size());

        assertEquals(2, works2.getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(1, works2.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", works2.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        
        assertEquals(2, works2.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals(1, works2.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", works2.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());

        assertEquals(3, works2.getWorkGroup().get(2).getWorkSummary().size());
        assertEquals(2, works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertThat(works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue(), anyOf(is("ext-id-3"), is("ext-id-4")));
        assertThat(works2.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(1).getValue(), anyOf(is("ext-id-3"), is("ext-id-4")));
    }

    @Test
    public void testGroupWorks_groupOnlyPublicWorks1() throws DatatypeConfigurationException {
        WorkSummary s1 = getWorkSummary("Public 1", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s2 = getWorkSummary("Limited 1", "ext-id-2", Visibility.LIMITED);
        WorkSummary s3 = getWorkSummary("Private 1", "ext-id-3", Visibility.PRIVATE);
        WorkSummary s4 = getWorkSummary("Public 2", "ext-id-4", Visibility.PUBLIC);
        WorkSummary s5 = getWorkSummary("Limited 2", "ext-id-5", Visibility.LIMITED);
        WorkSummary s6 = getWorkSummary("Private 2", "ext-id-6", Visibility.PRIVATE);
        WorkSummary s7 = getWorkSummary("Public 3", "ext-id-7", Visibility.PUBLIC);
        WorkSummary s8 = getWorkSummary("Limited 3", "ext-id-8", Visibility.LIMITED);
        WorkSummary s9 = getWorkSummary("Private 3", "ext-id-9", Visibility.PRIVATE);

        List<WorkSummary> workList = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);

        /**
         * They should be grouped as
         * 
         * Group 1: Public 1 Group 2: Public 2 Group 3: Public 3
         */
        Works works = workManager.groupWorks(workList, true);
        assertNotNull(works);
        assertEquals(3, works.getWorkGroup().size());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, works.getWorkGroup().get(0).getWorkSummary().size());
        assertEquals("ext-id-1", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 1", works.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(1, works.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, works.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals("ext-id-4", works.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 2", works.getWorkGroup().get(1).getWorkSummary().get(0).getTitle().getTitle().getContent());
        assertEquals(1, works.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().size());
        assertEquals(1, works.getWorkGroup().get(2).getWorkSummary().size());
        assertEquals("ext-id-7", works.getWorkGroup().get(2).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("Public 3", works.getWorkGroup().get(2).getWorkSummary().get(0).getTitle().getTitle().getContent());
    }

    @Test
    public void testGroupWorks_groupOnlyPublicWorks2() throws DatatypeConfigurationException {
        WorkSummary s1 = getWorkSummary("Public 1", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s2 = getWorkSummary("Limited 1", "ext-id-1", Visibility.LIMITED);
        WorkSummary s3 = getWorkSummary("Private 1", "ext-id-1", Visibility.PRIVATE);
        WorkSummary s4 = getWorkSummary("Public 2", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s5 = getWorkSummary("Limited 2", "ext-id-1", Visibility.LIMITED);
        WorkSummary s6 = getWorkSummary("Private 2", "ext-id-1", Visibility.PRIVATE);
        WorkSummary s7 = getWorkSummary("Public 3", "ext-id-2", Visibility.PUBLIC);
        WorkSummary s8 = getWorkSummary("Limited 3", "ext-id-2", Visibility.LIMITED);
        WorkSummary s9 = getWorkSummary("Private 3", "ext-id-2", Visibility.PRIVATE);

        List<WorkSummary> workList = Arrays.asList(s1, s2, s3, s4, s5, s6, s7, s8, s9);

        /**
         * They should be grouped as
         * 
         * Group 1: Public 1 + Public 2 Group 2: Public 3
         */
        Works works = workManager.groupWorks(workList, true);
        assertNotNull(works);
        assertEquals(2, works.getWorkGroup().size());
        assertEquals(1, works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-1", works.getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(2, works.getWorkGroup().get(0).getWorkSummary().size());
        assertThat(works.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent(), anyOf(is("Public 1"), is("Public 2")));
        assertThat(works.getWorkGroup().get(0).getWorkSummary().get(1).getTitle().getTitle().getContent(), anyOf(is("Public 1"), is("Public 2")));
        assertEquals(1, works.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().size());
        assertEquals("ext-id-2", works.getWorkGroup().get(1).getIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(1, works.getWorkGroup().get(1).getWorkSummary().size());
        assertEquals("Public 3", works.getWorkGroup().get(1).getWorkSummary().get(0).getTitle().getTitle().getContent());
    }

    @Test
    public void testGetAll() {
        List<Work> elements = workManager.findWorks("0000-0000-0000-0003");
        assertNotNull(elements);
        assertEquals(6, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false, found6 = false;
        for (Work element : elements) {
            if (11 == element.getPutCode()) {
                found1 = true;
            } else if (12 == element.getPutCode()) {
                found2 = true;
            } else if (13 == element.getPutCode()) {
                found3 = true;
            } else if (14 == element.getPutCode()) {
                found4 = true;
            } else if (15 == element.getPutCode()) {
                found5 = true;
            } else if (16 == element.getPutCode()) {
                found6 = true;
            } else {
                fail("Invalid element found: " + element.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
        assertTrue(found6);
    }

    @Test
    public void testGetAllSummaries() {
        List<WorkSummary> elements = workManager.getWorksSummaryList("0000-0000-0000-0003");
        assertNotNull(elements);
        assertEquals(6, elements.size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false, found6 = false;
        for (WorkSummary element : elements) {
            if (11 == element.getPutCode()) {
                found1 = true;
            } else if (12 == element.getPutCode()) {
                found2 = true;
            } else if (13 == element.getPutCode()) {
                found3 = true;
            } else if (14 == element.getPutCode()) {
                found4 = true;
            } else if (15 == element.getPutCode()) {
                found5 = true;
            } else if (16 == element.getPutCode()) {
                found6 = true;
            } else {
                fail("Invalid element found: " + element.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
        assertTrue(found6);
    }

    @Test
    public void testGetSummaryUrl() {
        Work w1 = workManager.getWork("0000-0000-0000-0003", 11l);
        WorkSummary w2 = workManager.getWorkSummary("0000-0000-0000-0003", 11l);
        assertEquals("http://testuri.org", w1.getUrl().getValue());
        assertEquals(w1.getUrl(), w2.getUrl());
    }

    @Test
    public void testGetPublic() {
        List<Work> elements = workManager.findPublicWorks("0000-0000-0000-0003");
        assertNotNull(elements);
        assertEquals(1, elements.size());
        assertEquals(Long.valueOf(11), elements.get(0).getPutCode());
    }

    @Test
    public void testFindWorkBulk() {
        String putCodes = "11,12,13";
        WorkBulk workBulk = workManager.findWorkBulk("0000-0000-0000-0003", putCodes);
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(3, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
        assertTrue(workBulk.getBulk().get(2) instanceof Work);
    }

    @Test
    public void testFindWorkBulkInvalidPutCodes() {
        OrcidCoreExceptionMapper orcidCoreExceptionMapper = Mockito.mock(OrcidCoreExceptionMapper.class);        

        ReflectionTestUtils.setField(workManager, "orcidCoreExceptionMapper", orcidCoreExceptionMapper);        

        org.orcid.jaxb.model.v3.release.error.OrcidError orcidError = new org.orcid.jaxb.model.v3.release.error.OrcidError();

        Mockito.when(orcidCoreExceptionMapper.getV3OrcidError(Mockito.any())).thenReturn(orcidError);

        String putCodes = "11,12,13,99999";
        WorkBulk workBulk = workManager.findWorkBulk("0000-0000-0000-0003", putCodes);
        assertNotNull(workBulk);
        assertNotNull(workBulk.getBulk());
        assertEquals(4, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
        assertTrue(workBulk.getBulk().get(2) instanceof Work);
        assertTrue(workBulk.getBulk().get(3) instanceof OrcidError);
    }

    @Test(expected = ExceedMaxNumberOfPutCodesException.class)
    public void testFindWorkBulkTooManyPutCodes() {
        StringBuilder tooManyPutCodes = new StringBuilder("0");
        for (int i = 1; i <= bulkReadSize; i++) {
            tooManyPutCodes.append(",").append(i);
        }

        workManager.findWorkBulk("0000-0000-0000-0003", tooManyPutCodes.toString());
        fail();
    }

    @Test
    public void nonGroupableIdsGenerateEmptyIdsListTest() throws DatatypeConfigurationException {
        WorkSummary s1 = getWorkSummary("Element 1", "ext-id-1", Visibility.PUBLIC);
        WorkSummary s2 = getWorkSummary("Element 2", "ext-id-2", Visibility.LIMITED);
        WorkSummary s3 = getWorkSummary("Element 3", "ext-id-3", Visibility.PRIVATE);

        // s1 will be a part of identifier, so, it will go in its own group
        s1.getExternalIdentifiers().getExternalIdentifier().get(0).setRelationship(Relationship.PART_OF);

        List<WorkSummary> workList = Arrays.asList(s1, s2, s3);

        /**
         * They should be grouped as
         * 
         * Group 1: Element 1 Group 2: Element 2 Group 3: Element 3
         */
        Works works = workManager.groupWorks(workList, false);
        assertNotNull(works);
        assertEquals(3, works.getWorkGroup().size());
        boolean foundEmptyGroup = false;
        boolean found2 = false;
        boolean found3 = false;
        for (WorkGroup group : works.getWorkGroup()) {
            assertEquals(1, group.getWorkSummary().size());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            if (group.getIdentifiers().getExternalIdentifier().isEmpty()) {
                assertEquals("Element 1", group.getWorkSummary().get(0).getTitle().getTitle().getContent());
                assertEquals("ext-id-1", group.getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                foundEmptyGroup = true;
            } else {
                assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
                assertThat(group.getIdentifiers().getExternalIdentifier().get(0).getValue(), anyOf(is("ext-id-2"), is("ext-id-3")));
                if (group.getIdentifiers().getExternalIdentifier().get(0).getValue().equals("ext-id-2")) {
                    assertEquals("Element 2", group.getWorkSummary().get(0).getTitle().getTitle().getContent());
                    assertEquals("ext-id-2", group.getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                    found2 = true;
                } else if (group.getIdentifiers().getExternalIdentifier().get(0).getValue().equals("ext-id-3")) {
                    assertEquals("Element 3", group.getWorkSummary().get(0).getTitle().getTitle().getContent());
                    assertEquals("ext-id-3", group.getWorkSummary().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
                    found3 = true;
                } else {
                    fail("Invalid ext id found " + group.getIdentifiers().getExternalIdentifier().get(0).getValue());
                }
            }
        }
        assertTrue(foundEmptyGroup);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void testCreateNewWorkGroup() throws MissingGroupableExternalIDException, IllegalAccessException {
        WorkDao mockDao = Mockito.mock(WorkDao.class);
        WorkEntityCacheManager cacheManager = Mockito.mock(WorkEntityCacheManager.class);
        
        ReflectionTestUtils.setField(workManager, "workDao", mockDao);
        ReflectionTestUtils.setField(workManager, "workEntityCacheManager", cacheManager);

        // no work where user is source
        List<MinimizedWorkEntity> works = getMinimizedWorksListForGrouping();
        Mockito.when(cacheManager.retrieveMinimizedWorks(Mockito.anyString(), Mockito.anyList(), Mockito.anyLong())).thenReturn(works);

        // full work matching user preferred id should be loaded from db (10 is
        // highest display index)
        Mockito.when(mockDao.find(Mockito.eq(1l))).thenReturn(getUserPreferredWork());

        workManager.createNewWorkGroup(Arrays.asList(1l, 2l, 3l, 4l), "some-orcid");

        // as no work with user as source, new work should be created
        ArgumentCaptor<WorkEntity> captor = ArgumentCaptor.forClass(WorkEntity.class);
        Mockito.verify(mockDao).persist(captor.capture());

        WorkEntity entity = captor.getValue();
        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":null,\"url\":null,\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}},{\"relationship\":null,\"url\":null,\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"doi:10.1/123\"}}]}",
                entity.getExternalIdentifiersJson());
        assertEquals("some title", entity.getTitle());
        assertEquals("some subtitle", entity.getSubtitle());

        // now test where user is source of one work
        works = getMinimizedWorksListForGrouping();
        MinimizedWorkEntity userSource = works.get(0);
        userSource.setSourceId("some-orcid");
        Mockito.when(cacheManager.retrieveMinimizedWorks(Mockito.anyString(), Mockito.anyList(), Mockito.anyLong())).thenReturn(works);

        // full work matching user preferred id should be loaded from db (10 is
        // highest display index)
        WorkEntity userVersionEntity = getUserSourceWorkEntity("some-orcid");
        Mockito.when(mockDao.getWork(Mockito.eq("some-orcid"), Mockito.eq(1l))).thenReturn(userVersionEntity);
        Mockito.when(mockDao.find(Mockito.eq(4l))).thenReturn(getUserPreferredWork());

        workManager.createNewWorkGroup(Arrays.asList(1l, 2l, 3l, 4l), "some-orcid");

        // no new work should be created
        Mockito.verify(mockDao, Mockito.times(1)).persist(Mockito.any(WorkEntity.class));

        assertEquals(
                "{\"workExternalIdentifier\":[{\"relationship\":null,\"url\":null,\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}},{\"relationship\":null,\"url\":null,\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"doi:10.1/123\"}}]}",
                userVersionEntity.getExternalIdentifiersJson());

        // only identifiers should have been updated
        assertEquals("work:title", userSource.getTitle());
        assertEquals("work:subtitle", userSource.getSubtitle());        
    }

    @Test(expected = MissingGroupableExternalIDException.class)
    public void testCreateNewWorkGroupNoGroupableExternalIDs() throws MissingGroupableExternalIDException, IllegalAccessException {
        WorkDao mockDao = Mockito.mock(WorkDao.class);
        WorkEntityCacheManager mockCacheManager = Mockito.mock(WorkEntityCacheManager.class);
        WorkEntityCacheManager oldCacheManager = (WorkEntityCacheManager) ReflectionTestUtils.getField(workManager, "workEntityCacheManager");

        ReflectionTestUtils.setField(workManager, "workDao", mockDao);
        ReflectionTestUtils.setField(workManager, "workEntityCacheManager", mockCacheManager);

        // no work where user is source
        List<MinimizedWorkEntity> works = getMinimizedWorksListForGroupingNoExternalIDs();
        Mockito.when(mockCacheManager.retrieveMinimizedWorks(Mockito.anyString(), Mockito.anyList(), Mockito.anyLong())).thenReturn(works);

        try {
            workManager.createNewWorkGroup(Arrays.asList(1l, 2l, 3l, 4l), "some-orcid");
        } finally {
            // reset dao
            ReflectionTestUtils.setField(workManager, "workDao", workDao);
            ReflectionTestUtils.setField(workManager, "workEntityCacheManager", oldCacheManager);
        }
    }

    @Test
    public void testAssertionOriginUpdate() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_2_ID));
        Work work = getWork(null);
        work = workManager.createWork(claimedOrcid, work, true);
        work = workManager.getWork(claimedOrcid, work.getPutCode());
        assertEquals("Work title", work.getWorkTitle().getTitle().getContent());
        work.getWorkTitle().setTitle(new Title("updated"));
        work = workManager.updateWork(claimedOrcid, work, true);

        assertNotNull(work);
        assertEquals("updated", work.getWorkTitle().getTitle().getContent());
        assertEquals(work.getSource().getSourceClientId().getPath(), CLIENT_1_ID);
        assertEquals(work.getSource().getSourceClientId().getUri(), "https://testserver.orcid.org/client/" + CLIENT_1_ID);
        assertEquals(work.getSource().getAssertionOriginClientId().getPath(), CLIENT_2_ID);
        assertEquals(work.getSource().getAssertionOriginClientId().getUri(), "https://testserver.orcid.org/client/" + CLIENT_2_ID);

        // make a duplicate
        Work work2 = getWork(null);
        try {
            workManager.createWork(claimedOrcid, work2, true);
            fail();
        } catch (OrcidDuplicatedActivityException e) {

        }

        // make a duplicate as a different assertion origin
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));
        workManager.createWork(claimedOrcid, work2, true);

        // wrong sources:
        work.getExternalIdentifiers().getExternalIdentifier().get(0).setValue("x");
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));
            workManager.updateWork(claimedOrcid, work, true);
            fail();
        } catch (WrongSourceException e) {
        }

        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
            workManager.updateWork(claimedOrcid, work, true);
            fail();
        } catch (WrongSourceException e) {

        }
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_2_ID));
            workManager.updateWork(claimedOrcid, work, true);
            fail();
        } catch (WrongSourceException e) {

        }
    }

    public void testCreateNewWorkGroupUserSourceWorks() throws MissingGroupableExternalIDException, IllegalAccessException {
        WorkDao mockDao = Mockito.mock(WorkDao.class);
        WorkEntityCacheManager cacheManager = Mockito.mock(WorkEntityCacheManager.class);
        WorkEntityCacheManager oldCacheManager = (WorkEntityCacheManager) ReflectionTestUtils.getField(workManager, "workEntityCacheManager");

        ReflectionTestUtils.setField(workManager, "workDao", mockDao);
        ReflectionTestUtils.setField(workManager, "workEntityCacheManager", cacheManager);

        MinimizedWorkEntity firstWork = getBasicMinimizedWork();
        firstWork.setId(1l);
        firstWork.setDisplayIndex(1l);
        firstWork.setSourceId("some-orcid");
        firstWork.setExternalIdentifiersJson(
                "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"ISSN\",\"workExternalIdentifierId\":{\"content\":\"1234-5678\"}},{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"doi:10.1/123\"}}]}");

        MinimizedWorkEntity secondWork = getBasicMinimizedWork();
        secondWork.setId(2l);
        secondWork.setSourceId("some-orcid");
        secondWork.setDisplayIndex(1l);
        secondWork.setExternalIdentifiersJson(
                "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"ISSN\",\"workExternalIdentifierId\":{\"content\":\"1234-5678\"}}]}");

        List<MinimizedWorkEntity> works = Arrays.asList(firstWork, secondWork);
        Mockito.when(cacheManager.retrieveMinimizedWorks(Mockito.anyString(), Mockito.anyList(), Mockito.anyLong())).thenReturn(works);

        Mockito.when(mockDao.getWork(Mockito.eq("some-orcid"), Mockito.eq(Long.valueOf(1)))).thenReturn(getUserSourceWorkEntity("some-orcid"));
        Mockito.when(mockDao.getWork(Mockito.eq("some-orcid"), Mockito.eq(Long.valueOf(2)))).thenReturn(getUserSourceWorkEntity("some-orcid"));

        workManager.createNewWorkGroup(Arrays.asList(1l, 2l), "some-orcid");

        // both user versions should be merged
        Mockito.verify(mockDao, Mockito.times(2)).merge(Mockito.any(WorkEntity.class));

        // reset dao
        ReflectionTestUtils.setField(workManager, "workDao", workDao);
        ReflectionTestUtils.setField(workManager, "workEntityCacheManager", oldCacheManager);
    }

    @Test
    public void testCreateWorkFromAPIAndValidateTopContributorsInWorkEntity() {
        String orcid = "0000-0000-0000-0004";
        WorkDao mockDao = Mockito.mock(WorkDao.class);

        ReflectionTestUtils.setField(workManager, "workDao", mockDao);

        Work work = workManager.createWork(orcid, getWorkWith100Contributors(), true);

        ArgumentCaptor<WorkEntity> workEntityCaptor = ArgumentCaptor.forClass(WorkEntity.class);
        Mockito.verify(mockDao).persist(workEntityCaptor.capture());

        WorkEntity workEntity = workEntityCaptor.getValue();

        assertNotNull(work);
        assertNotNull(workEntity);
        assertNotNull(workEntity.getTopContributorsJson());

        workManager.removeWorks(orcid, Arrays.asList(work.getPutCode()));

        ReflectionTestUtils.setField(workManager, "workDao", workDao);
    }


    @Test
    public void testCreateWorkFromAPIAndValidateTopContributors() {
        String orcid = "0000-0000-0000-0004";
        ContributorsRolesAndSequencesConverter mockContributorsRolesAndSequencesConverter = Mockito.mock(ContributorsRolesAndSequencesConverter.class);

        ReflectionTestUtils.setField(workManager, "contributorsRolesAndSequencesConverter", mockContributorsRolesAndSequencesConverter);

        Work work = workManager.createWork(orcid, getWorkWith100Contributors(), true);

        ArgumentCaptor<List<ContributorsRolesAndSequences>> captor = ArgumentCaptor.forClass((Class) List.class);
        Mockito.verify(mockContributorsRolesAndSequencesConverter).convertTo(captor.capture(), any());

        List<ContributorsRolesAndSequences> topContributors = captor.getValue();

        assertNotNull(work);
        assertNotNull(topContributors);
        assertEquals(topContributors.size(), maxContributorsForUI + 1);

        workManager.removeWorks(orcid, Arrays.asList(work.getPutCode()));

        ReflectionTestUtils.setField(workManager, "contributorsRolesAndSequencesConverter", contributorsRolesAndSequencesConverter);
    }

    @Test
    public void testCreateWorkFromAPIAndValidateContributorsDefaultValue() {
        String orcid = "0000-0000-0000-0004";
        WorkDao mockDao = Mockito.mock(WorkDao.class);

        ReflectionTestUtils.setField(workManager, "workDao", mockDao);

        Work work = workManager.createWork(orcid, getWork(null), true);

        ArgumentCaptor<WorkEntity> workEntityCaptor = ArgumentCaptor.forClass(WorkEntity.class);
        Mockito.verify(mockDao).persist(workEntityCaptor.capture());

        WorkEntity workEntity = workEntityCaptor.getValue();

        assertEquals(workEntity.getContributorsJson(), "{\"contributor\":[]}");
        assertEquals(workEntity.getTopContributorsJson(), "[]");

        workManager.removeWorks(orcid, Arrays.asList(work.getPutCode()));

        ReflectionTestUtils.setField(workManager, "workDao", workDao);
    }

    @Test
    public void testCreateWorkFromUIAndValidateTopContributorsInWorkEntity() {
        String orcid = "0000-0000-0000-0004";
        WorkDao mockDao = Mockito.mock(WorkDao.class);

        ReflectionTestUtils.setField(workManager, "workDao", mockDao);

        Work workResult = workManager.createWork(orcid, getWorkWithContributorsMultipleRoles());

        ArgumentCaptor<WorkEntity> workEntityCaptor = ArgumentCaptor.forClass(WorkEntity.class);
        Mockito.verify(mockDao).persist(workEntityCaptor.capture());

        WorkEntity workEntity = workEntityCaptor.getValue();

        assertNotNull(workResult);
        assertNotNull(workEntity);
        assertNotNull(workEntity.getTopContributorsJson());
        assertEquals("[{\"contributorOrcid\":{\"uri\":null,\"path\":\"0000-0000-0000-000X\",\"host\":null},\"creditName\":{\"content\":\"Contributor 1\"},\"contributorEmail\":null,\"contributorAttributes\":null,\"rolesAndSequences\":[{\"contributorSequence\":null,\"contributorRole\":\"FUNDING_ACQUISITION\"},{\"contributorSequence\":null,\"contributorRole\":\"WRITING_REVIEW_EDITING\"}]},{\"contributorOrcid\":{\"uri\":null,\"path\":\"0000-0000-0000-000X\",\"host\":null},\"creditName\":{\"content\":\"Contributor 2\"},\"contributorEmail\":null,\"contributorAttributes\":null,\"rolesAndSequences\":[{\"contributorSequence\":null,\"contributorRole\":\"WRITING_REVIEW_EDITING\"},{\"contributorSequence\":null,\"contributorRole\":\"SOFTWARE\"}]},{\"contributorOrcid\":{\"uri\":null,\"path\":\"0000-0000-0000-000X\",\"host\":null},\"creditName\":{\"content\":\"Contributor 3\"},\"contributorEmail\":null,\"contributorAttributes\":null,\"rolesAndSequences\":[{\"contributorSequence\":null,\"contributorRole\":\"FORMAL_ANALYSIS\"},{\"contributorSequence\":null,\"contributorRole\":\"FUNDING_ACQUISITION\"}]}]", workEntity.getTopContributorsJson());

        workManager.removeWorks(orcid, Arrays.asList(workResult.getPutCode()));

        ReflectionTestUtils.setField(workManager, "workDao", workDao);
    }

    @Test
    public void testUpdateWorkFromUIAndRemoveOneCreditRole() {
        String orcid = "0000-0000-0000-0004";

        WorkForm workForm = getWorkWithContributorsMultipleRoles();

        Work work = workManager.createWork(orcid, workForm);

        workForm.setPutCode(Text.valueOf(work.getPutCode()));
        workForm.getContributorsGroupedByOrcid().forEach(contributorsRolesAndSequences -> {
            List<ContributorAttributes> rolesAndSequences = new ArrayList<>();
            ContributorAttributes ca = new ContributorAttributes();
            ca.setContributorRole(CreditRole.FUNDING_ACQUISITION.value());
            rolesAndSequences.add(ca);
            contributorsRolesAndSequences.setRolesAndSequences(rolesAndSequences);
        });

        Work workResult = workManager.updateWork(orcid, workForm);

        WorkExtended workExtended = workManager.getWorkExtended(orcid, workResult.getPutCode());

        assertNotNull(workExtended);
        assertNotNull(workExtended.getContributorsGroupedByOrcid());
        assertEquals(workExtended.getContributorsGroupedByOrcid().get(0).getRolesAndSequences().size(), 1);
        workManager.removeWorks(orcid, Arrays.asList(workResult.getPutCode()));
    }

    @Test
    public void testCompareWorksDifferentContent() {
        NotificationManager mockNotificationManager = Mockito.mock(NotificationManager.class);
        ReflectionTestUtils.setField(workManager, "notificationManager", mockNotificationManager);

        Work work = new Work();
        fillWork(work);
        work = workManager.createWork(claimedOrcid, work, true);
        WorkForm workSaved = WorkForm.valueOf(work, maxContributorsForUI);

        Work workToUpdate = new Work();
        fillWork(workToUpdate);
        workToUpdate.setPutCode(work.getPutCode());
        workToUpdate.setWorkType(WorkType.DISSERTATION_THESIS);
        workToUpdate.setWorkCitation(new Citation("(Author, 2023a) Or (Author, 2023b)", CitationType.FORMATTED_APA));

        WorkForm workForm = WorkForm.valueOf(workToUpdate, 50);

        assertFalse(workSaved.compare(workForm));

        workManager.updateWork(claimedOrcid, workToUpdate, true);

        Mockito.verify(mockNotificationManager, Mockito.times(2)).sendAmendEmail(any(), any(), any());

        workManager.removeWorks(claimedOrcid, Arrays.asList(work.getPutCode()));
    }

    @Test
    public void testCompareWorksSameContent() {
        NotificationManager mockNotificationManager = Mockito.mock(NotificationManager.class);
        ReflectionTestUtils.setField(workManager, "notificationManager", mockNotificationManager);

        Work work = new Work();
        fillWork(work);
        Work workSaved = workManager.createWork(claimedOrcid, work, true);
        work.setPutCode(workSaved.getPutCode());

        workManager.updateWork(claimedOrcid, workSaved, true);
        
        Mockito.verify(mockNotificationManager, Mockito.times(1)).sendAmendEmail(any(), any(), any());

        workManager.removeWorks(claimedOrcid, Arrays.asList(work.getPutCode()));
    }

    @Test
    public void testCompareWorksNullAndEmptyValues() {
        NotificationManager mockNotificationManager = Mockito.mock(NotificationManager.class);
        ReflectionTestUtils.setField(workManager, "notificationManager", mockNotificationManager);

        Work work = new Work();
        fillWork(work);
        work = workManager.createWork(claimedOrcid, work, true);
        WorkForm workSaved = WorkForm.valueOf(work, maxContributorsForUI);

        Work workToUpdate = new Work();
        fillWork(workToUpdate);
        workToUpdate.setPutCode(work.getPutCode());
        workToUpdate.setWorkContributors(null);
        workToUpdate.setShortDescription("");
        workToUpdate.setJournalTitle(new Title(""));
        workToUpdate.setVisibility(null);
        WorkForm workFormToUpdate = WorkForm.valueOf(workToUpdate, 50);

        assertFalse(workSaved.compare(workFormToUpdate));

        workManager.updateWork(claimedOrcid, workToUpdate, true);

        Mockito.verify(mockNotificationManager, Mockito.times(2)).sendAmendEmail(any(), any(), any());

        workManager.removeWorks(claimedOrcid, Arrays.asList(work.getPutCode()));
    }

    @Test
    public void testCompareWorksPublicationDate() {
        NotificationManager mockNotificationManager = Mockito.mock(NotificationManager.class);
        ReflectionTestUtils.setField(workManager, "notificationManager", mockNotificationManager);

        Work work = new Work();
        fillWork(work);
        work = workManager.createWork(claimedOrcid, work, true);

        Work w = workManager.getWork(claimedOrcid, work.getPutCode());
        WorkForm workSaved = WorkForm.valueOf(w, maxContributorsForUI);

        Work workToUpdate = new Work();
        fillWork(workToUpdate);
        workToUpdate.setPutCode(work.getPutCode());
        workToUpdate.setPublicationDate(new PublicationDate(new FuzzyDate(new Year(2017), new Month(1), new Day(2))));
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("title"));
        title.setSubtitle(new Subtitle("subtitle"));
        title.setTranslatedTitle(new TranslatedTitle("titulo traducido", "es"));
        workToUpdate.setWorkTitle(title);
        workToUpdate.setWorkCitation(new Citation("citation", CitationType.FORMATTED_IEEE));
        WorkForm workFormToUpdate = WorkForm.valueOf(workToUpdate, 50);

        assertFalse(workSaved.compare(workFormToUpdate));

        Work workUpdated = workManager.updateWork(claimedOrcid, workToUpdate, true);

        assertTrue(WorkForm.valueOf(workUpdated, maxContributorsForUI).compare(workFormToUpdate));

        workManager.updateWork(claimedOrcid, workUpdated, true);

        Mockito.verify(mockNotificationManager, Mockito.times(2)).sendAmendEmail(any(), any(), any());

        workManager.removeWorks(claimedOrcid, Arrays.asList(work.getPutCode()));
    }

    @Test
    public void testCompareWorksContributors() {
        NotificationManager mockNotificationManager = Mockito.mock(NotificationManager.class);
        ReflectionTestUtils.setField(workManager, "notificationManager", mockNotificationManager);

        Work work = new Work();
        fillWork(work);
        work = workManager.createWork(claimedOrcid, work, true);

        Work w = workManager.getWork(claimedOrcid, work.getPutCode());
        WorkForm workSaved = WorkForm.valueOf(w, maxContributorsForUI);

        Work workToUpdate = new Work();
        fillWork(workToUpdate);
        workToUpdate.setPutCode(work.getPutCode());

        WorkContributors contributors = getContributors();
        contributors.getContributor().get(0).getContributorAttributes().setContributorRole(ContributorRole.EDITOR.name());
        contributors.getContributor().get(0).setCreditName(new CreditName("credit name 2"));

        workToUpdate.setWorkContributors(contributors);

        WorkForm workFormToUpdate = WorkForm.valueOf(workToUpdate, 50);

        assertFalse(workSaved.compare(workFormToUpdate));

        Work workUpdated = workManager.updateWork(claimedOrcid, workToUpdate, true);

        assertTrue(WorkForm.valueOf(workUpdated, maxContributorsForUI).compare(workFormToUpdate));

        workManager.updateWork(claimedOrcid, workToUpdate, true);

        Mockito.verify(mockNotificationManager, Mockito.times(2)).sendAmendEmail(any(), any(), any());

        workManager.removeWorks(claimedOrcid, Arrays.asList(work.getPutCode()));
    }
    
    @Test
    public void testCompareIdenticalWorksDifferentSource() {
        NotificationManager mockNotificationManager = Mockito.mock(NotificationManager.class);
        ReflectionTestUtils.setField(workManager, "notificationManager", mockNotificationManager);

        Work work = new Work();
        fillWork(work);
        work = workManager.createWork(claimedOrcid, work, true);
        // make a duplicate as a different assertion origin
        Work w = workManager.getWork(claimedOrcid, work.getPutCode());
        WorkForm workSaved = WorkForm.valueOf(w, maxContributorsForUI);
        
        // identical same source
        workManager.updateWork(claimedOrcid, w, true);

        Work workToUpdate = new Work();
        fillWork(workToUpdate);
        workToUpdate.setPutCode(work.getPutCode());

        WorkContributors contributors = getContributors();
        contributors.getContributor().get(0).getContributorAttributes().setContributorRole(ContributorRole.EDITOR.name());
        contributors.getContributor().get(0).setCreditName(new CreditName("credit name 2"));

        workToUpdate.setWorkContributors(contributors);

        WorkForm workFormToUpdate = WorkForm.valueOf(workToUpdate, 50);

        assertFalse(workSaved.compare(workFormToUpdate));
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));
            workManager.updateWork(claimedOrcid, workToUpdate, true);
            fail();
        } catch (Exception e) {
        }
        
        //identical different source
         try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));
            workManager.updateWork(claimedOrcid, w, true);
            fail();
        } catch (Exception e) {
        	
        }

        workManager.removeWorks(claimedOrcid, Arrays.asList(work.getPutCode()));
    }

    @Test
    public void testCompareWorksContributorsGroupedByOrcidId() {
        NotificationManager mockNotificationManager = Mockito.mock(NotificationManager.class);
        ReflectionTestUtils.setField(workManager, "notificationManager", mockNotificationManager);

        WorkForm workForm = getWorkWithContributorsMultipleRoles();

        Work work = workManager.createWork(claimedOrcid, workForm);

        WorkExtended w = workManager.getWorkExtended(claimedOrcid, work.getPutCode());
        WorkForm workSaved = WorkForm.valueOf(w, maxContributorsForUI);

        WorkForm workFormToUpdate = getWorkWithContributorsMultipleRoles();

        workFormToUpdate.setPutCode(Text.valueOf(workSaved.getPutCode().getValue()));
        workFormToUpdate.setContributors(null);
        List<ContributorsRolesAndSequences> contributorsGroupedByOrcid = getContributorsGroupedByOrcidId();
        contributorsGroupedByOrcid.get(0).setCreditName(new CreditName("CONTRIBUTOR 1"));
        workFormToUpdate.setContributorsGroupedByOrcid(contributorsGroupedByOrcid);

        assertFalse(workSaved.compare(workFormToUpdate));

        Work workUpdated = workManager.updateWork(claimedOrcid, workFormToUpdate);
        WorkExtended workExtendedUpdated = workManager.getWorkExtended(claimedOrcid, workUpdated.getPutCode());

        assertTrue(WorkForm.valueOf(workExtendedUpdated, maxContributorsForUI).compare(workFormToUpdate));

        work = workManager.updateWork(claimedOrcid, workFormToUpdate);

        Mockito.verify(mockNotificationManager, Mockito.times(2)).sendAmendEmail(any(), any(), any());

        workManager.removeWorks(claimedOrcid, Arrays.asList(work.getPutCode()));
    }

    @Test
    public void testCompareWorksExternalIdentifiers() {
        NotificationManager mockNotificationManager = Mockito.mock(NotificationManager.class);
        ReflectionTestUtils.setField(workManager, "notificationManager", mockNotificationManager);

        Work work = new Work();
        fillWork(work);
        work = workManager.createWork(claimedOrcid, work, true);

        WorkForm workSaved = WorkForm.valueOf(work, maxContributorsForUI);

        Work workToUpdate = new Work();
        fillWork(workToUpdate);
        workToUpdate.setPutCode(work.getPutCode());

        ExternalIDs externalIDs = getExternalIDs();
        externalIDs.getExternalIdentifier().get(0).setValue("value2");

        workToUpdate.setWorkExternalIdentifiers(externalIDs);

        WorkForm workFormToUpdate = WorkForm.valueOf(workToUpdate, 50);

        workToUpdate = workManager.updateWork(claimedOrcid, workToUpdate, true);

        assertFalse(workSaved.compare(workFormToUpdate));

        Work workUpdated = workManager.updateWork(claimedOrcid, workToUpdate, true);

        assertTrue(WorkForm.valueOf(workUpdated, maxContributorsForUI).compare(workFormToUpdate));

        Mockito.verify(mockNotificationManager, Mockito.times(2)).sendAmendEmail(any(), any(), any());

        workManager.removeWorks(claimedOrcid, Arrays.asList(work.getPutCode()));
    }

    private WorkEntity getUserPreferredWork() {
        WorkEntity userPreferred = new WorkEntity();
        userPreferred.setId(4l);
        userPreferred.setDisplayIndex(4l);
        userPreferred.setTitle("some title");
        userPreferred.setSubtitle("some subtitle");
        userPreferred.setExternalIdentifiersJson(
                "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}},{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"doi:10.1/123\"}}]}");
        return userPreferred;
    }

    private List<MinimizedWorkEntity> getMinimizedWorksListForGrouping() throws IllegalAccessException {
        List<MinimizedWorkEntity> minWorks = new ArrayList<>();

        for (long l = 1; l <= 4; l++) {
            MinimizedWorkEntity work = getBasicMinimizedWork();
            work.setDisplayIndex(l);
            work.setId(l);

            if (l == 4l) {
                work.setExternalIdentifiersJson(
                        "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}},{\"workExternalIdentifierType\":\"DOI\",\"workExternalIdentifierId\":{\"content\":\"doi:10.1/123\"}}]}");
            } else {
                work.setExternalIdentifiersJson(
                        "{\"workExternalIdentifier\":[{\"workExternalIdentifierType\":\"AGR\",\"workExternalIdentifierId\":{\"content\":\"123\"}}]}");
            }
            minWorks.add(work);
        }
        return minWorks;
    }

    private List<MinimizedWorkEntity> getMinimizedWorksListForGroupingNoExternalIDs() throws IllegalAccessException {
        List<MinimizedWorkEntity> minWorks = new ArrayList<>();

        for (long l = 1; l <= 4; l++) {
            MinimizedWorkEntity work = getBasicMinimizedWork();
            work.setDisplayIndex(l);
            work.setId(l);
            work.setExternalIdentifiersJson("{\"workExternalIdentifier\":[]}");            
            minWorks.add(work);
        }
        return minWorks;
    }

    private MinimizedWorkEntity getBasicMinimizedWork() throws IllegalAccessException {
        Date date = new Date();
        MinimizedWorkEntity work = new MinimizedWorkEntity();
        DateFieldsOnBaseEntityUtils.setDateFields(work, date);
        work.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.LIMITED.name());
        work.setDescription("work:description");
        work.setJournalTitle("work:journalTitle");
        work.setLanguageCode("en");
        work.setPublicationDate(new PublicationDateEntity(2000, 1, 1));
        work.setSubtitle("work:subtitle");
        work.setTitle("work:title");
        work.setTranslatedTitle("work:translatedTitle");
        work.setTranslatedTitleLanguageCode("es");
        work.setWorkType(org.orcid.jaxb.model.record_v2.WorkType.ARTISTIC_PERFORMANCE.name());
        work.setWorkUrl("work:url");
        return work;
    }

    private WorkEntity getUserSourceWorkEntity(String sourceId) {
        WorkEntity workEntity = new WorkEntity();
        workEntity.setSourceId(sourceId);
        return workEntity;
    }

    private WorkSummary getWorkSummary(String titleValue, String extIdValue, Visibility visibility) throws DatatypeConfigurationException {
        return getWorkSummary(titleValue, extIdValue, visibility, "0");
    }

    private WorkSummary getWorkSummary(String titleValue, String extIdValue, Visibility visibility, String displayIndex) throws DatatypeConfigurationException {
        WorkSummary summary = new WorkSummary();
        summary.setDisplayIndex(displayIndex);
        waitAMillisecond(); // for testing purposes, let's avoid equal created dates
        summary.setCreatedDate(new CreatedDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar())));
        Title title = new Title(titleValue);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(title);
        summary.setTitle(workTitle);
        summary.setType(WorkType.ARTISTIC_PERFORMANCE);
        summary.setVisibility(visibility);
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setValue(extIdValue);
        extIds.getExternalIdentifier().add(extId);
        summary.setExternalIdentifiers(extIds);
        return summary;
    }

    private void waitAMillisecond() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread shouldn't be interrupted", e);
        }
    }

    private Work getWork(String extIdValue) {
        Work work = new Work();
        WorkTitle title = new WorkTitle();
        if (extIdValue == null) {
            title.setTitle(new Title("Work title"));
        } else {
            title.setTitle(new Title("Work title " + extIdValue));
        }
        work.setWorkTitle(title);
        work.setWorkType(WorkType.BOOK);

        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        if (extIdValue == null) {
            extId.setValue("ext-id-value");
        } else {
            extId.setValue("ext-id-value-" + extIdValue);
        }
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);

        work.setVisibility(Visibility.PUBLIC);
        return work;
    }

    private void fillWork(Work work) {
        work.setCountry(new Country(Iso3166Country.US));
        work.setJournalTitle(new Title("journal-title"));
        work.setLanguageCode("en");
        work.setPublicationDate(new PublicationDate(new FuzzyDate(new Year(2017), new Month(1), new Day(1))));
        work.setShortDescription("short-description");
        work.setUrl(new Url("http://test.orcid.org"));
        work.setVisibility(Visibility.LIMITED);
        work.setWorkCitation(new Citation("citation", CitationType.FORMATTED_HARVARD));
        work.setWorkContributors(getContributors());
        work.setWorkExternalIdentifiers(getExternalIDs());
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("title"));
        title.setSubtitle(new Subtitle("subtitle"));
        title.setTranslatedTitle(new TranslatedTitle("translated title", "en"));
        work.setWorkTitle(title);

        work.setWorkType(WorkType.ARTISTIC_PERFORMANCE);
    }

    private WorkContributors getContributors() {
        List<Contributor> contributors = new ArrayList<>();

        ContributorAttributes attributes = new ContributorAttributes();
        attributes.setContributorRole("http://credit.niso.org/contributor-roles/investigation/");
        attributes.setContributorSequence(SequenceType.FIRST);

        ContributorOrcid contributorOrcid = new ContributorOrcid();
        contributorOrcid.setHost("http://test.orcid.org");
        contributorOrcid.setPath("0000-0000-0000-0000");
        contributorOrcid.setUri("https://test.orcid.org/0000-0000-0000-0000");

        Contributor contributor = new Contributor();
        contributor.setContributorAttributes(attributes);
        contributor.setContributorOrcid(contributorOrcid);
        contributor.setCreditName(new CreditName("credit name"));
        contributor.setContributorEmail(new ContributorEmail("email@test.orcid.org"));

        contributors.add(contributor);

        attributes = new ContributorAttributes();
        attributes.setContributorRole("http://credit.niso.org/contributor-roles/conceptualization/");
        attributes.setContributorSequence(SequenceType.ADDITIONAL);

        contributorOrcid = new ContributorOrcid();
        contributorOrcid.setHost("http://test.orcid.org");
        contributorOrcid.setPath("0000-0000-0000-0001");
        contributorOrcid.setUri("https://test.orcid.org/0000-0000-0000-0000");

        contributor = new Contributor();
        contributor.setContributorAttributes(attributes);
        contributor.setContributorOrcid(contributorOrcid);
        contributor.setCreditName(new CreditName("credit name 2"));
        contributor.setContributorEmail(new ContributorEmail("email@test.orcid.org"));

        contributors.add(contributor);

        attributes = new ContributorAttributes();
        attributes.setContributorRole("http://credit.niso.org/contributor-roles/data-curation/");
        attributes.setContributorSequence(SequenceType.ADDITIONAL);

        contributorOrcid = new ContributorOrcid();
        contributorOrcid.setHost("http://test.orcid.org");
        contributorOrcid.setPath("0000-0000-0000-0002");
        contributorOrcid.setUri("https://test.orcid.org/0000-0000-0000-0000");

        contributor = new Contributor();
        contributor.setContributorAttributes(attributes);
        contributor.setContributorOrcid(contributorOrcid);
        contributor.setCreditName(new CreditName("credit name3"));
        contributor.setContributorEmail(new ContributorEmail("email@test.orcid.org"));

        contributors.add(contributor);

        return new WorkContributors(Stream.of(contributor).collect(Collectors.toList()));
    }

    private ExternalIDs getExternalIDs() {
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("value1");
        id1.setUrl(new Url("http://value1.com"));
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(id1);
        return extIds;
    }

    private List<ContributorsRolesAndSequences> getContributorsGroupedByOrcidId() {
        List<ContributorsRolesAndSequences> contributorsGroupedByOrcid = new ArrayList<>();
        ContributorsRolesAndSequences crs = new ContributorsRolesAndSequences();
        crs.setContributorOrcid(new ContributorOrcid("0000-0000-0000-000X"));
        crs.setCreditName(new CreditName("Contributor 1"));
        List<ContributorAttributes> rolesAndSequences = new ArrayList<>();
        ContributorAttributes ca = new ContributorAttributes();
        ca.setContributorRole(CreditRole.FUNDING_ACQUISITION.value());
        rolesAndSequences.add(ca);
        ca = new ContributorAttributes();
        ca.setContributorRole(CreditRole.WRITING_REVIEW_EDITING.value());
        rolesAndSequences.add(ca);
        crs.setRolesAndSequences(rolesAndSequences);
        contributorsGroupedByOrcid.add(crs);

        crs = new ContributorsRolesAndSequences();
        crs.setContributorOrcid(new ContributorOrcid("0000-0000-0000-000X"));
        crs.setCreditName(new CreditName("Contributor 2"));
        rolesAndSequences = new ArrayList<>();
        ca = new ContributorAttributes();
        ca.setContributorRole(CreditRole.WRITING_REVIEW_EDITING.value());
        rolesAndSequences.add(ca);
        ca = new ContributorAttributes();
        ca.setContributorRole(CreditRole.SOFTWARE.value());
        rolesAndSequences.add(ca);
        crs.setRolesAndSequences(rolesAndSequences);
        contributorsGroupedByOrcid.add(crs);

        crs = new ContributorsRolesAndSequences();
        crs.setContributorOrcid(new ContributorOrcid("0000-0000-0000-000X"));
        crs.setCreditName(new CreditName("Contributor 3"));
        rolesAndSequences = new ArrayList<>();
        ca = new ContributorAttributes();
        ca.setContributorRole(CreditRole.FORMAL_ANALYSIS.value());
        rolesAndSequences.add(ca);
        ca = new ContributorAttributes();
        ca.setContributorRole(CreditRole.FUNDING_ACQUISITION.value());
        rolesAndSequences.add(ca);
        crs.setRolesAndSequences(rolesAndSequences);
        contributorsGroupedByOrcid.add(crs);


        return contributorsGroupedByOrcid;
    }

    private Work getWorkWith100Contributors() {
        Work work = getWork(null);
        List<Contributor> contributorList = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            Contributor contributor = new Contributor();
            contributor.setCreditName(new CreditName("Test " + i));
            contributor.setContributorOrcid(null);
            contributor.setContributorEmail(null);
            contributor.setContributorAttributes(null);
            contributorList.add(contributor);
        }
        work.setWorkContributors(new WorkContributors(contributorList));
        return work;
    }

    private WorkForm getWorkWithContributorsMultipleRoles() {
        Work work = getWork(null);
        WorkForm workForm = WorkForm.valueOf(work, 50);
        workForm.setContributorsGroupedByOrcid(getContributorsGroupedByOrcidId());
        return workForm;
    }
}
