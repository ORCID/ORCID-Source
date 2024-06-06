package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.OrcidDuplicatedElementException;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

public class ExternalIdentifierManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String CLIENT_1_ID = "APP-5555555555555555";
    private static final String CLIENT_2_ID = "APP-5555555555555556";
    private String claimedOrcid = "0000-0000-0000-0002";
    private String unclaimedOrcid = "0000-0000-0000-0001";

    @Mock
    private SourceManager sourceManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;
    
    @Resource
    private ExternalIdentifierDao externalIdentifierDao;
    
    @Resource
    private ProfileLastModifiedDao profileLastModifiedDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(externalIdentifierManager, "sourceManager", sourceManager);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    public void testAddExternalIdentifierToUnclaimedRecordPreserveExternalIdentifierVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId = getExternalIdentifier();

        extId = externalIdentifierManager.createExternalIdentifier(unclaimedOrcid, extId, true);
        extId = externalIdentifierManager.getExternalIdentifier(unclaimedOrcid, extId.getPutCode());

        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
    }

    @Test
    public void testAddExternalIdentifierToClaimedRecordPreserveUserDefaultVisibility() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId = getExternalIdentifier();

        extId = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        extId = externalIdentifierManager.getExternalIdentifier(claimedOrcid, extId.getPutCode());

        assertNotNull(extId);
        assertEquals(Visibility.LIMITED, extId.getVisibility());
    }
    
    @Test
    public void testAddEqualsExternalIdentifiersFromDifferentSource() {
        PersonExternalIdentifier extId = getExternalIdentifier();
        extId.setType(extId.getType() + System.currentTimeMillis());
        // Create from client # 1
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId1 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        assertNotNull(extId1);
        assertNotNull(extId1.getPutCode());
        
        // Create from client # 2
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_2_ID)));
        PersonExternalIdentifier extId2 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        assertNotNull(extId2);
        assertNotNull(extId2.getPutCode());
        
        // Verify both ext ids are not the same one
        assertNotEquals(extId1.getPutCode(), extId2.getPutCode());        
    }
    
    @Test(expected = OrcidDuplicatedElementException.class)
    public void testAddEqualsExternalIdentifiersFromSameSource() {
        PersonExternalIdentifier extId = getExternalIdentifier();
        extId.setType(extId.getType() + System.currentTimeMillis());
        // Create from client # 1
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId1 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        assertNotNull(extId1);
        assertNotNull(extId1.getPutCode());
        
        // Remove the put code and the source
        extId1.setPutCode(null);
        extId1.setSource(null);
        externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        fail();
    }
    
    @Test
    public void displayIndexIsSetTo_0_FromUI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId = getExternalIdentifier();
        extId.setType(extId.getType() + System.currentTimeMillis());
        PersonExternalIdentifier extId1 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, false);
        extId1 = externalIdentifierManager.getExternalIdentifier(claimedOrcid, extId1.getPutCode());
        
        assertNotNull(extId1);
        assertEquals(Long.valueOf(0), extId1.getDisplayIndex());
    }
    
    @Test
    public void displayIndexIsSetTo_1_FromAPI() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        PersonExternalIdentifier extId = getExternalIdentifier();
        extId.setType(extId.getType() + System.currentTimeMillis());
        PersonExternalIdentifier extId1 = externalIdentifierManager.createExternalIdentifier(claimedOrcid, extId, true);
        extId1 = externalIdentifierManager.getExternalIdentifier(claimedOrcid, extId1.getPutCode());
        
        assertNotNull(extId1);
        assertEquals(Long.valueOf(1), extId1.getDisplayIndex());
    }
    
    @Test
    public void getAllTest() {
        String orcid = "0000-0000-0000-0003";
        PersonExternalIdentifiers elements = externalIdentifierManager.getExternalIdentifiers(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getExternalIdentifiers());
        assertEquals(7, elements.getExternalIdentifiers().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false, found5 = false, found6 = false, found7 = false;
        for(PersonExternalIdentifier element : elements.getExternalIdentifiers()) {
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
            } else if(18 == element.getPutCode()){
                found6 = true;
            } else if(19 == element.getPutCode()){
                found7 = true;
            } else {
                fail("Invalid put code found: " + element.getPutCode());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
        assertTrue(found6);
        assertTrue(found7);
    }
    
    @Test
    public void getPublicTest() {
        String orcid = "0000-0000-0000-0003";        
        PersonExternalIdentifiers elements = externalIdentifierManager.getPublicExternalIdentifiers(orcid);
        assertNotNull(elements);
        assertNotNull(elements.getExternalIdentifiers());
        assertEquals(3, elements.getExternalIdentifiers().size());
        assertEquals(Long.valueOf(19), elements.getExternalIdentifiers().get(0).getPutCode());
        assertEquals(Long.valueOf(18), elements.getExternalIdentifiers().get(1).getPutCode());
        assertEquals(Long.valueOf(13), elements.getExternalIdentifiers().get(2).getPutCode());
    }
    
    @Test
    public void addExternalIdentifierUpdateLastModifiedTest() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        String orcid = "4444-4444-4444-4443";
        Date lastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        PersonExternalIdentifier extId = getExternalIdentifier();
        externalIdentifierManager.createExternalIdentifier(orcid, extId, false);
        Date updatedLastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        assertTrue("Profile last modified should be updated", lastModified.before(updatedLastModified));        
    }
    
    @Test 
    public void deleteExternalIdentifierUpdateLastModifiedTest() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        String orcid = "4444-4444-4444-4443";
        Date lastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);        
        externalIdentifierManager.deleteExternalIdentifier(orcid, Long.valueOf(1), false);
        Date updatedLastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        assertTrue("Profile last modified should be updated", lastModified.before(updatedLastModified));
    }
    
    @Test 
    public void updateExternalIdentifierUpdateLastModifiedTest() {
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_1_ID)));
        String orcid = "4444-4444-4444-4442";
        Date lastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        PersonExternalIdentifier extId = externalIdentifierManager.getExternalIdentifier(orcid, Long.valueOf(2));
        assertNotNull(extId);
        assertEquals("abc123", extId.getValue());        
        extId.setValue("Updated!");
        externalIdentifierManager.updateExternalIdentifier(orcid, extId, false);        
        Date updatedLastModified = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        assertTrue("Profile last modified should be updated", lastModified.before(updatedLastModified));  
    }
    
    private PersonExternalIdentifier getExternalIdentifier() {
        PersonExternalIdentifier extId = new PersonExternalIdentifier();
        extId.setRelationship(Relationship.SELF);
        extId.setType("person-ext-id-type");
        extId.setValue("person-ext-id-value");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setVisibility(Visibility.PUBLIC);
        return extId;
    }
}
