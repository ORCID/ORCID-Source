package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;

/**
 * @author Angel Montenegro
 */
public class GroupIdRecordManagerTest extends BaseTest  {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");

    private final String CLIENT_ID = "APP-5555555555555555";
    
    @Resource
    private GroupIdRecordManager groupIdRecordManager;        
    
    @Mock
    private SourceManager sourceManager;
    
    @Before
    public void before() {
        TargetProxyHelper.injectIntoProxy(groupIdRecordManager, "sourceManager", sourceManager); 
        when(sourceManager.retrieveSourceEntity()).thenReturn(new SourceEntity(new ClientDetailsEntity(CLIENT_ID)));
    }
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }    
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void testCreateDuplicateGroupIdRecords() {
        GroupIdRecord g1 = new GroupIdRecord();
        g1.setDescription("Description");
        g1.setGroupId("orcid-generated:valid-group-id#1");
        g1.setName("Group # " + System.currentTimeMillis());
        g1.setType("publisher");
        
        //Create the first one
        g1 = groupIdRecordManager.createGroupIdRecord(g1);
        Long putCode = g1.getPutCode();
        assertNotNull(g1.getSource());
        assertNotNull(g1.getSource().getSourceClientId());
        assertEquals(CLIENT_ID, g1.getSource().getSourceClientId().getPath());
        
        //Try to create a duplicate
        try {
            g1.setPutCode(null);
            g1 = groupIdRecordManager.createGroupIdRecord(g1);
            fail();
        } catch(DuplicatedGroupIdRecordException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Try to create a UPPER duplicate
        try {
            g1.setPutCode(null);
            g1.setGroupId("orcid-generated:VALID-GROUP-ID#1");
            g1 = groupIdRecordManager.createGroupIdRecord(g1);
            fail();
        } catch(DuplicatedGroupIdRecordException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Try to create a duplicate mixed cases
        try {
            g1.setPutCode(null);
            g1.setGroupId("orcid-generated:VaLiD-GrOuP-Id#1");
            g1 = groupIdRecordManager.createGroupIdRecord(g1);
            fail();
        } catch(DuplicatedGroupIdRecordException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Create a different one
        g1.setPutCode(null);
        g1.setGroupId("orcid-generated:valid-group-id#2");
        GroupIdRecord g2 = groupIdRecordManager.createGroupIdRecord(g1);
        assertNotNull(g2);
        assertNotNull(g2.getPutCode());
        assertTrue(!g2.getPutCode().equals(putCode));
        assertEquals("orcid-generated:valid-group-id#2", g2.getGroupId());
        assertNotNull(g2.getSource());
        assertNotNull(g2.getSource().getSourceClientId());
        assertEquals(CLIENT_ID, g2.getSource().getSourceClientId().getPath());
        
        //Try to create again a duplicate for any of the two existing
        try {
            g1.setPutCode(null);
            g1.setGroupId("orcid-generated:VaLiD-GrOuP-Id#1");
            g1 = groupIdRecordManager.createGroupIdRecord(g1);
            fail();
        } catch(DuplicatedGroupIdRecordException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            g1.setPutCode(null);
            g1.setGroupId("orcid-generated:VaLiD-GrOuP-Id#2");
            g1 = groupIdRecordManager.createGroupIdRecord(g1);
            fail();
        } catch(DuplicatedGroupIdRecordException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        groupIdRecordManager.deleteGroupIdRecord(putCode);
        groupIdRecordManager.deleteGroupIdRecord(g2.getPutCode());
    }
    
    @Test
    public void testUpdateDuplicateGroupIdRecords() {
        String groupName = "Group # " + System.currentTimeMillis(); 
        GroupIdRecord g1 = new GroupIdRecord();
        g1.setDescription("Description");
        g1.setGroupId("orcid-generated:valid-group-id#1");
        g1.setName(groupName);
        g1.setType("publisher");
        
        //Create the first one
        g1 = groupIdRecordManager.createGroupIdRecord(g1);
        Long putCode1 = g1.getPutCode();
        assertNotNull(g1.getSource());
        assertNotNull(g1.getSource().getSourceClientId());
        assertEquals(CLIENT_ID, g1.getSource().getSourceClientId().getPath());
        
        //Create another one
        g1.setPutCode(null);
        g1.setGroupId("orcid-generated:valid-group-id#2");
        g1 = groupIdRecordManager.createGroupIdRecord(g1);
        Long putCode2 = g1.getPutCode();
        assertNotNull(g1.getSource());
        assertNotNull(g1.getSource().getSourceClientId());
        assertEquals(CLIENT_ID, g1.getSource().getSourceClientId().getPath());
        
        //Create another one
        g1.setPutCode(null);
        g1.setGroupId("orcid-generated:valid-group-id#3");
        g1 = groupIdRecordManager.createGroupIdRecord(g1);
        Long putCode3 = g1.getPutCode();
        assertNotNull(g1.getSource());
        assertNotNull(g1.getSource().getSourceClientId());
        assertEquals(CLIENT_ID, g1.getSource().getSourceClientId().getPath());
        
        //Update #1 with an existing group id
        try {
            GroupIdRecord existingOne = groupIdRecordManager.findByGroupId("orcid-generated:valid-group-id#1").get();
            existingOne.setGroupId("orcid-generated:valid-group-id#2");
            existingOne.setDescription("updated-description");
            groupIdRecordManager.updateGroupIdRecord(existingOne.getPutCode(), existingOne);
            fail();
        } catch(DuplicatedGroupIdRecordException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Update #1 with an existing group id
        try {
            GroupIdRecord existingOne = groupIdRecordManager.findByGroupId("orcid-generated:valid-group-id#1").get();
            existingOne.setGroupId("orcid-generated:valid-group-id#3");
            existingOne.setDescription("updated-description");
            groupIdRecordManager.updateGroupIdRecord(existingOne.getPutCode(), existingOne);
            fail();
        } catch(DuplicatedGroupIdRecordException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Update #1 with a new group id
        GroupIdRecord existingOne = groupIdRecordManager.findByGroupId("orcid-generated:valid-group-id#1").get();
        existingOne.setGroupId("orcid-generated:valid-group-id#1-updated");
        existingOne.setDescription("updated-description");
        existingOne = groupIdRecordManager.updateGroupIdRecord(existingOne.getPutCode(), existingOne);
        assertNotNull(existingOne);
        assertEquals(putCode1, existingOne.getPutCode());
        assertEquals("orcid-generated:valid-group-id#1-updated", existingOne.getGroupId());
        assertEquals("updated-description", existingOne.getDescription());
        assertEquals(groupName, existingOne.getName());
        assertNotNull(existingOne.getSource());
        assertNotNull(existingOne.getSource().getSourceClientId());
        assertEquals(CLIENT_ID, existingOne.getSource().getSourceClientId().getPath());
        
        //Delete them
        groupIdRecordManager.deleteGroupIdRecord(putCode1);
        groupIdRecordManager.deleteGroupIdRecord(putCode2);
        groupIdRecordManager.deleteGroupIdRecord(putCode3);
    }
    
    @Test
    public void testCreateUpdateGetDeleteGroupIdRecord(){
        GroupIdRecord g1 = new GroupIdRecord();
        g1.setDescription("Description");
        g1.setGroupId("orcid-generated:valid-group-id");
        g1.setName("Group # " + System.currentTimeMillis());
        g1.setType("publisher");
        
        //Test create
        g1 = groupIdRecordManager.createGroupIdRecord(g1);        
        Long putCode = g1.getPutCode();
        assertNotNull(putCode);
        assertNotNull(g1.getSource());
        assertNotNull(g1.getSource().getSourceClientId());
        assertEquals(CLIENT_ID, g1.getSource().getSourceClientId().getPath());
        
        //Test find
        assertTrue(groupIdRecordManager.exists(g1.getGroupId()));
        
        Optional<GroupIdRecord> existingByGroupId = groupIdRecordManager.findByGroupId(g1.getGroupId());
        assertTrue(existingByGroupId.isPresent());
        assertNotNull(existingByGroupId.get().getPutCode());
        assertEquals(putCode, existingByGroupId.get().getPutCode());
        assertEquals(g1.getGroupId(), existingByGroupId.get().getGroupId());
        assertNotNull(existingByGroupId.get().getSource());
        assertNotNull(existingByGroupId.get().getSource().getSourceClientId());
        assertEquals(CLIENT_ID, existingByGroupId.get().getSource().getSourceClientId().getPath());
        
        GroupIdRecord existingByPutCode = groupIdRecordManager.getGroupIdRecord(g1.getPutCode());
        assertNotNull(existingByPutCode);
        assertNotNull(existingByPutCode.getPutCode());
        assertEquals(putCode, existingByPutCode.getPutCode());
        assertEquals(g1.getGroupId(), existingByPutCode.getGroupId()); 
        assertNotNull(existingByPutCode.getSource());
        assertNotNull(existingByPutCode.getSource().getSourceClientId());
        assertEquals(CLIENT_ID, existingByPutCode.getSource().getSourceClientId().getPath());
        
        //Test update with invalid value        
        try {
            g1.setGroupId("invalid-group-id");
            groupIdRecordManager.updateGroupIdRecord(g1.getPutCode(), g1);
            fail();
        } catch(OrcidValidationException e) {
            assertEquals("Invalid group-id: '" + g1.getGroupId() + "'", e.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        //Test update with valid value
        try {
            g1.setGroupId("orcid-generated:other-valid-group-id");
            g1 = groupIdRecordManager.updateGroupIdRecord(g1.getPutCode(), g1);
            assertNotNull(g1);
            assertEquals("orcid-generated:other-valid-group-id", g1.getGroupId());
        } catch(Exception e) {
            fail();
        }
        
        //Test create with put code
        try {
            g1.setGroupId("orcid-generated:valid-group-id");
            groupIdRecordManager.createGroupIdRecord(g1);
            fail();
        } catch(InvalidPutCodeException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Test create with invalid group id
        try {
            g1.setPutCode(null);
            g1.setGroupId("other-invalid-group-id");
            groupIdRecordManager.createGroupIdRecord(g1);
            fail();
        } catch(OrcidValidationException e) {
            assertEquals("Invalid group-id: '" + g1.getGroupId() + "'", e.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        //Test delete
        groupIdRecordManager.deleteGroupIdRecord(putCode);
        try {
            groupIdRecordManager.deleteGroupIdRecord(Long.valueOf(-1L));    
        } catch(GroupIdRecordNotFoundException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
}
