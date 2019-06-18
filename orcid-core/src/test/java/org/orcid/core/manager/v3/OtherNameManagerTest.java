package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.test.TargetProxyHelper;

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
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(otherNameManager, "sourceManager", mockSourceManager); 
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", mockSourceManager);        
    }
    
    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(otherNameManager, "sourceManager", sourceManager);        
        TargetProxyHelper.injectIntoProxy(orcidSecurityManager, "sourceManager", sourceManager);        
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
    
    private OtherName getOtherName() {
        OtherName otherName = new OtherName();
        otherName.setContent("other-name");
        otherName.setVisibility(Visibility.PUBLIC);
        return otherName;
    }
}
