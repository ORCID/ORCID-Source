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
import org.orcid.jaxb.model.v3.rc2.common.Country;
import org.orcid.jaxb.model.v3.rc2.common.Iso3166Country;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Addresses;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class AddressManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");
    
    private static final String CLIENT_1_ID = "4444-4444-4444-4498";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";
    
    @Mock
    private SourceManager sourceManager;
    
    @Resource(name = "addressManagerV3")
    private AddressManager addressManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(addressManager, "sourceManager", sourceManager);        
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testAddAddressToUnclaimedRecordPreserveAddressVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));   
        Address address = getAddress(Iso3166Country.CR);
        
        address = addressManager.createAddress(unclaimedOrcid, address, true);
        address = addressManager.getAddress(unclaimedOrcid, address.getPutCode());
        
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());        
    }
    
    @Test
    public void testAddAddressToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));                
        Address address = getAddress(Iso3166Country.US);
        
        address = addressManager.createAddress(claimedOrcid, address, true);
        address = addressManager.getAddress(claimedOrcid, address.getPutCode());
        
        assertNotNull(address);
        assertEquals(Visibility.LIMITED, address.getVisibility());  
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromUI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));                
        Address address = getAddress(Iso3166Country.MX);
        
        address = addressManager.createAddress(claimedOrcid, address, false);
        address = addressManager.getAddress(claimedOrcid, address.getPutCode());
        
        assertNotNull(address);
        assertEquals(Long.valueOf(1), address.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromAPI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));                
        Address address = getAddress(Iso3166Country.PE);
        
        address = addressManager.createAddress(claimedOrcid, address, true);
        address = addressManager.getAddress(claimedOrcid, address.getPutCode());
        
        assertNotNull(address);
        assertEquals(Long.valueOf(0), address.getDisplayIndex());       
    }
    
    @Test
    public void getAllTest() {
        String orcid = "0000-0000-0000-0003";
        Addresses elements = addressManager.getAddresses(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getAddress());
        assertEquals(5, elements.getAddress().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false;
        for(Address element : elements.getAddress()) {
            if(9 == element.getPutCode()){
                found1 = true;
            } else if(10 == element.getPutCode()){
                found2 = true;
            } else if(11 == element.getPutCode()){
                found3 = true;
            } else if(12 == element.getPutCode()){
                found4 = true;
            } else if(13 == element.getPutCode()){
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
        Addresses elements = addressManager.getPublicAddresses(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getAddress());
        assertEquals(1, elements.getAddress().size());
        assertEquals(Long.valueOf(9), elements.getAddress().get(0).getPutCode());
    }
    
    private Address getAddress(Iso3166Country country) {
        Address address = new Address();
        address.setCountry(new Country(country));
        address.setVisibility(Visibility.PUBLIC);
        return address;
    }
}
