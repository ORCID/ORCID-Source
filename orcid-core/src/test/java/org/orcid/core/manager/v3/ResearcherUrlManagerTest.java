package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.util.ReflectionTestUtils;

public class ResearcherUrlManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private static final String CLIENT_2_ID = "APP-5555555555555556";//obo
    private static final String CLIENT_3_ID = "4444-4444-4444-4498";//obo

    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";

    @Mock
    private SourceManager mockSourceManager;

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;
    
    @Resource(name = "orcidSecurityManagerV3")
    OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "researcherUrlManagerV3")
    private ResearcherUrlManager researcherUrlManager;
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManager;
    
    @Mock
    private ClientDetailsManager mockClientDetailsManager;
    
    @Mock
    private RecordNameDao mockRecordNameDao;
    
    @Mock
    private RecordNameManagerReadOnly mockRecordNameManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", mockSourceManager);
        TargetProxyHelper.injectIntoProxy(researcherUrlManager, "sourceManager", mockSourceManager);
        
        // by default return client details entity with user obo disabled
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(new ClientDetailsEntity());
        
        Mockito.when(mockRecordNameDao.exists(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockRecordNameManager.fetchDisplayablePublicName(Mockito.anyString())).thenReturn("test");
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameDao", mockRecordNameDao);
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameManagerReadOnlyV3", mockRecordNameManager);
    }

    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(researcherUrlManager, "sourceManager", sourceManager);        
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", sourceManager);  
        
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameDao", recordNameDao);        
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameManagerReadOnlyV3", recordNameManager);   
    }
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    public void testAddResearcherUrlToUnclaimedRecordPreserveResearcherUrlVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        ResearcherUrl rUrl = getResearcherUrl();
        
        rUrl = researcherUrlManager.createResearcherUrl(unclaimedOrcid, rUrl, true);
        rUrl = researcherUrlManager.getResearcherUrl(unclaimedOrcid, rUrl.getPutCode());

        assertNotNull(rUrl);
        assertEquals(Visibility.PUBLIC, rUrl.getVisibility());
    }

    @Test
    public void testAddResearcherUrToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        
        ProfileEntity profile = profileDao.find(claimedOrcid);
        profile.setIndexingStatus(IndexingStatus.DONE);
        profileDao.merge(profile);
        
        profile = profileDao.find(claimedOrcid);
        Date lastModified = profile.getLastModified();
        
        ResearcherUrl rUrl = getResearcherUrl();
        
        rUrl = researcherUrlManager.createResearcherUrl(claimedOrcid, rUrl, true);
        rUrl = researcherUrlManager.getResearcherUrl(claimedOrcid, rUrl.getPutCode());

        assertNotNull(rUrl);
        assertEquals(Visibility.LIMITED, rUrl.getVisibility());
        
        profile = profileDao.find(claimedOrcid);
        assertTrue(profile.getLastModified().getTime() > lastModified.getTime());
        assertTrue(IndexingStatus.PENDING.equals(profile.getIndexingStatus()));
    }

    @Test
    public void displayIndexIsSetTo_0_FromUI() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));        
        ResearcherUrl rUrl = getResearcherUrl();
        rUrl.getUrl().setValue(rUrl.getUrl().getValue() + "/fromUI");
        
        rUrl = researcherUrlManager.createResearcherUrl(claimedOrcid, rUrl, false);
        rUrl = researcherUrlManager.getResearcherUrl(claimedOrcid, rUrl.getPutCode());
        
        assertNotNull(rUrl);
        assertEquals(Long.valueOf(0), rUrl.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromAPI() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));        
        ResearcherUrl rUrl = getResearcherUrl();
        rUrl.getUrl().setValue(rUrl.getUrl().getValue() + "/fromAPI");
        
        rUrl = researcherUrlManager.createResearcherUrl(claimedOrcid, rUrl, true);
        rUrl = researcherUrlManager.getResearcherUrl(claimedOrcid, rUrl.getPutCode());
                
        assertNotNull(rUrl);
        assertEquals(Long.valueOf(1), rUrl.getDisplayIndex());
    }
    
    @Test
    public void getAllTest() {
        String orcid = "0000-0000-0000-0003";
        ResearcherUrls elements = researcherUrlManager.getResearcherUrls(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getResearcherUrls());
        assertEquals(5, elements.getResearcherUrls().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        for(ResearcherUrl element : elements.getResearcherUrls()) {
            if(13 == element.getPutCode()){
                found1 = true;
            } else if(14 == element.getPutCode()){
                found2 = true;
            } else if(15 == element.getPutCode()){
                found3 = true;
            } else if(16 == element.getPutCode()){
                found4 = true;
            } else if(17 == element.getPutCode()){
                found5 = true;
            } else {
                fail("Invalid element found: " + element.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
    }
    
    @Test
    public void getPublicTest() {
        String orcid = "0000-0000-0000-0003";        
        ResearcherUrls elements = researcherUrlManager.getPublicResearcherUrls(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getResearcherUrls());
        assertEquals(1, elements.getResearcherUrls().size());
        assertEquals(Long.valueOf(13), elements.getResearcherUrls().get(0).getPutCode());
    }
    
    @Test
    public void testAssertionOriginUpdate() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_2_ID));                
        ResearcherUrl rUrl = getResearcherUrl();
        
        rUrl = researcherUrlManager.createResearcherUrl(unclaimedOrcid, rUrl, true);
        rUrl = researcherUrlManager.getResearcherUrl(unclaimedOrcid, rUrl.getPutCode());

        assertNotNull(rUrl);
        
        assertEquals(rUrl.getSource().getSourceClientId().getPath(),CLIENT_1_ID);
        assertEquals(rUrl.getSource().getSourceClientId().getUri(),"https://testserver.orcid.org/client/"+CLIENT_1_ID);
        assertEquals(rUrl.getSource().getAssertionOriginClientId().getPath(),CLIENT_2_ID);
        assertEquals(rUrl.getSource().getAssertionOriginClientId().getUri(),"https://testserver.orcid.org/client/"+CLIENT_2_ID);
        
        //make a duplicate
        ResearcherUrl rUrl2 = getResearcherUrl();
        try {
            rUrl2 = researcherUrlManager.createResearcherUrl(unclaimedOrcid, rUrl2, true);
            fail();
        }catch(OrcidDuplicatedElementException e) {
            
        }
        
        //make a duplicate as a different assertion origin
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));                
        rUrl2 = researcherUrlManager.createResearcherUrl(unclaimedOrcid, rUrl2, true);
        
        rUrl.setUrl(new Url("http://no.no"));
        //wrong sources:
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));
            researcherUrlManager.updateResearcherUrl(unclaimedOrcid, rUrl, true);
            fail();
        }catch(WrongSourceException e) {
        }
        
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));  
            researcherUrlManager.updateResearcherUrl(unclaimedOrcid, rUrl, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_2_ID));  
            researcherUrlManager.updateResearcherUrl(unclaimedOrcid, rUrl, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
    }
    
    
    private ResearcherUrl getResearcherUrl() {
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://orcid.org"));
        rUrl.setUrlName("ORCID Site");
        rUrl.setVisibility(Visibility.PUBLIC);
        return rUrl;
    }
}
