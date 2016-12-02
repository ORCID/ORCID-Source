/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.InvalidPutCodeException;
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecord;

/**
 * @author Angel Montenegro
 */
public class GroupIdRecordManagerTest extends BaseTest  {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml");
    
    @Resource
    private GroupIdRecordManager groupIdRecordManager;
    
    
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
        
        //Create another one
        g1.setPutCode(null);
        g1.setGroupId("orcid-generated:valid-group-id#2");
        g1 = groupIdRecordManager.createGroupIdRecord(g1);
        Long putCode2 = g1.getPutCode();
        
        //Create another one
        g1.setPutCode(null);
        g1.setGroupId("orcid-generated:valid-group-id#3");
        g1 = groupIdRecordManager.createGroupIdRecord(g1);
        Long putCode3 = g1.getPutCode();
        
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
        
        //Test find
        assertTrue(groupIdRecordManager.exists(g1.getGroupId()));
        
        Optional<GroupIdRecord> existingByGroupId = groupIdRecordManager.findByGroupId(g1.getGroupId());
        assertTrue(existingByGroupId.isPresent());
        assertNotNull(existingByGroupId.get().getPutCode());
        assertEquals(putCode, existingByGroupId.get().getPutCode());
        assertEquals(g1.getGroupId(), existingByGroupId.get().getGroupId());
        
        GroupIdRecord existingByPutCode = groupIdRecordManager.getGroupIdRecord(g1.getPutCode());
        assertNotNull(existingByPutCode);
        assertNotNull(existingByPutCode.getPutCode());
        assertEquals(putCode, existingByPutCode.getPutCode());
        assertEquals(g1.getGroupId(), existingByPutCode.getGroupId());                
        
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
