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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.OtherNames;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

public class OtherNameManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource(name = "otherNameManagerV3")
    private OtherNameManager otherNameManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(otherNameManager, "sourceManager", sourceManager); 
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testAddOtherNameToUnclaimedRecordPreserveOtherNameVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        OtherName otherName = getOtherName();
        
        otherName = otherNameManager.createOtherName(unclaimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(unclaimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());        
    }
    
    @Test
    public void testAddOtherNameToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));                
        OtherName otherName = getOtherName();
        
        otherName = otherNameManager.createOtherName(claimedOrcid, otherName, true);
        otherName = otherNameManager.getOtherName(claimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Visibility.LIMITED, otherName.getVisibility());       
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        OtherName otherName = getOtherName();
        otherName.setContent(otherName.getContent() + " fromUI");
        
        otherName = otherNameManager.createOtherName(claimedOrcid, otherName, false);
        otherName = otherNameManager.getOtherName(claimedOrcid, otherName.getPutCode());
        
        assertNotNull(otherName);
        assertEquals(Long.valueOf(1), otherName.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
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
    
    private OtherName getOtherName() {
        OtherName otherName = new OtherName();
        otherName.setContent("other-name");
        otherName.setVisibility(Visibility.PUBLIC);
        return otherName;
    }
}
