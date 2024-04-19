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
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.dao.RecordNameDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.util.ReflectionTestUtils;

public class OtherNameManagerTest extends BaseTest {
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

    @Resource(name = "otherNameManagerV3")
    private OtherNameManager otherNameManager;
    
    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private RecordNameDao recordNameDao;
    
    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManager;
    
    @Resource
    private SourceNameCacheManager sourceNameCacheManager;
    
    @Resource
    private ProfileLastModifiedDao profileLastModifiedDao;
    
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
        TargetProxyHelper.injectIntoProxy(otherNameManager, "sourceManager", mockSourceManager); 
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", mockSourceManager);    
        
        // by default return client details entity with user obo disabled
        Mockito.when(mockClientDetailsManager.findByClientId(Mockito.anyString())).thenReturn(new ClientDetailsEntity());
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", mockClientDetailsManager);
        
        Mockito.when(mockRecordNameDao.exists(Mockito.anyString())).thenReturn(true);
        Mockito.when(mockRecordNameManager.fetchDisplayablePublicName(Mockito.anyString())).thenReturn("test");
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameDao", mockRecordNameDao);
        ReflectionTestUtils.setField(sourceNameCacheManager, "recordNameManagerReadOnlyV3", mockRecordNameManager);
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(otherNameManager, "sourceManager", sourceManager);        
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", sourceManager);  
        
        ReflectionTestUtils.setField(clientDetailsEntityCacheManager, "clientDetailsManager", clientDetailsManager);        
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
    public void testAddOtherNameToUnclaimedRecordPreserveOtherNameVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));   
        OtherName otherName = getOtherName();
        
        otherName = otherNameManager.createOtherName(unclaimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(unclaimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());        
    }
    
    @Test
    public void testAddOtherNameToClaimedRecordPreserveUserDefaultVisibility() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));                
        OtherName otherName = getOtherName();
        
        otherName = otherNameManager.createOtherName(claimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(claimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Visibility.LIMITED, otherName.getVisibility());       
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        OtherName otherName = getOtherName();
        otherName.setContent(otherName.getContent() + " fromUI");
        
        otherName = otherNameManager.createOtherName(claimedOrcid, otherName, false);
        otherName = otherNameManager.getOtherName(claimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        OtherName otherName = getOtherName();
        otherName.setContent(otherName.getContent() + " fromAPI");
        
        otherName = otherNameManager.createOtherName(claimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(claimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Long.valueOf(0), otherName.getDisplayIndex());
    }
    
    @Test
    public void getAllTest() {
        String orcid = "0000-0000-0000-0003";
        OtherNames elements = otherNameManager.getOtherNames(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getOtherNames());
        assertEquals(5, elements.getOtherNames().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        for(OtherName element : elements.getOtherNames()) {
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
                fail("Invalid put code found: " + element.getPutCode());
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
        OtherNames elements = otherNameManager.getPublicOtherNames(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getOtherNames());
        assertEquals(1, elements.getOtherNames().size());
        assertEquals(Long.valueOf(13), elements.getOtherNames().get(0).getPutCode());
    }
    
    @Test
    public void testAssertionOriginUpdate() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_2_ID));                
        OtherName otherName = getOtherName();        
        otherName = otherNameManager.createOtherName(unclaimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(unclaimedOrcid, otherName.getPutCode());
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());        
        
        assertEquals(otherName.getSource().getSourceClientId().getPath(),CLIENT_1_ID);
        assertEquals(otherName.getSource().getSourceClientId().getUri(),"https://testserver.orcid.org/client/"+CLIENT_1_ID);
        assertEquals(otherName.getSource().getAssertionOriginClientId().getPath(),CLIENT_2_ID);
        assertEquals(otherName.getSource().getAssertionOriginClientId().getUri(),"https://testserver.orcid.org/client/"+CLIENT_2_ID);
        
        //make a duplicate
        OtherName otherName2 = getOtherName();
        try {
            otherName2 = otherNameManager.createOtherName(unclaimedOrcid, otherName2, true);
            fail();
        }catch(OrcidDuplicatedElementException e) {
            
        }
        
        //make a duplicate as a different assertion origin
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));                
        otherName2 = otherNameManager.createOtherName(unclaimedOrcid, otherName2, true);
        
        //wrong sources:
        otherName.setContent("x");
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClientWithClientOBO(CLIENT_1_ID, CLIENT_3_ID));
            otherName = otherNameManager.updateOtherName(unclaimedOrcid, otherName.getPutCode(), otherName, true);
            fail();
        }catch(WrongSourceException e) {
        }
        
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
            otherName = otherNameManager.updateOtherName(unclaimedOrcid, otherName.getPutCode(), otherName, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
        try {
            when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_2_ID));  
            otherName = otherNameManager.updateOtherName(unclaimedOrcid, otherName.getPutCode(), otherName, true);
            fail();
        }catch(WrongSourceException e) {
            
        }
    }
    
    @Test
    public void addOtherNameUpdateLastModifiedTest() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient(CLIENT_1_ID));
        String orcid = "4444-4444-4444-4443";
        Date lastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        OtherName otherName = getOtherName();
        otherNameManager.createOtherName(orcid, otherName, false);
        Date updatedLastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        assertTrue("Profile last modified should be updated", lastModified.before(updatedLastModified));
    }
    
    @Test
    public void deleteOtherNameUpdateLastModifiedTest() {
        String orcid = "4444-4444-4444-4443";
        Date lastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        otherNameManager.deleteOtherName(orcid, Long.valueOf(2), false);
        Date updatedLastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        assertTrue("Profile last modified should be updated", lastModified.before(updatedLastModified)); 
    }
    
    @Test
    public void updateOtherNameUpdateLastModifiedTest() {
        when(mockSourceManager.retrieveActiveSource()).thenReturn(Source.forClient("APP-5555555555555555"));
        String orcid = "4444-4444-4444-4443";
        Date lastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        OtherName otherName = otherNameManager.getOtherName(orcid, Long.valueOf(1));
        otherName.setContent("Updated");
        otherNameManager.updateOtherName(orcid, Long.valueOf(1), otherName, false);
        Date updatedLastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        assertTrue("Profile last modified should be updated", lastModified.before(updatedLastModified)); 
    }
    
    private OtherName getOtherName() {
        OtherName otherName = new OtherName();
        otherName.setContent("other-name");
        otherName.setVisibility(Visibility.PUBLIC);
        return otherName;
    }
}
