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
package org.orcid.frontend.web.controllers;

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.frontend.web.util.BaseControllerTest;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ManageMembersControllerTest extends BaseControllerTest {

    @Resource
    ManageMembersController manageMembers;

    @Resource
    private ProfileDao profileDao;

    @Resource
    OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    GroupAdministratorController groupAdministratorController;

    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml"), null);
    }    

    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }
    
    
    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void createMemberProfileWithInvalidEmailsTest() throws Exception {
        ProfileEntity profile = profileDao.find("4444-4444-4444-4441");
        assertNotNull(profile);
        assertNotNull(profile.getPrimaryEmail());
        String existingEmail = profile.getPrimaryEmail().getId();
        assertNotNull(existingEmail);
        Group group = new Group();
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("basic"));
        group.setSalesforceId(Text.valueOf(""));

        // Validate already existing email address
        group.setEmail(Text.valueOf(existingEmail));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.email.already_used", new ArrayList<String>()), group.getErrors().get(0));

        // Validate empty email address
        group.setEmail(Text.valueOf(""));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("NotBlank.group.email", new ArrayList<String>()), group.getErrors().get(0));

        // Validate invalid email address
        group.setEmail(Text.valueOf("invalidemail"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.email.invalid_email", new ArrayList<String>()), group.getErrors().get(0));
    }

    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void createMemberProfileWithInvalidGroupNameTest() throws Exception {
        Group group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setType(Text.valueOf("basic"));
        group.setSalesforceId(Text.valueOf(""));

        // Validate empty group name
        group.setGroupName(Text.valueOf(""));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("NotBlank.group.name", new ArrayList<String>()), group.getErrors().get(0));

        // validate too long group name - 151 chars
        group.setGroupName(Text
                .valueOf("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.name.too_long", new ArrayList<String>()), group.getErrors().get(0));
    }

    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void createMemberProfileWithInvalidTypeTest() throws Exception {
        Group group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setSalesforceId(Text.valueOf(""));

        // Validate empty type
        group.setType(Text.valueOf(""));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("NotBlank.group.type", new ArrayList<String>()), group.getErrors().get(0));

        // Validate invalid type
        group.setType(Text.valueOf("invalid"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.type.invalid", new ArrayList<String>()), group.getErrors().get(0));
    }
    
    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void createMemberProfileWithInvalidSalesforceIdTest() throws Exception {
        Group group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("basic"));        

        // Validate empty type        
        group.setSalesforceId(Text.valueOf("1"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.salesforce_id.invalid_length", new ArrayList<String>()), group.getErrors().get(0));

        // Validate invalid type        
        group.setSalesforceId(Text.valueOf("1234567890abcd!"));
        group = manageMembers.createMember(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.salesforce_id.invalid", new ArrayList<String>()), group.getErrors().get(0));
    }

    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void createMemberProfileTest() throws Exception {
        Group group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf(""));
        group = manageMembers.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));
    }

    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void findMemberByOrcidTest() throws Exception {
        Group group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));

        // Test find by orcid
        String orcid = group.getGroupOrcid().getValue();
        Group newGroup = manageMembers.findMember(orcid);
        assertNotNull(newGroup);

        assertFalse(PojoUtil.isEmpty(newGroup.getGroupOrcid()));
        assertFalse(PojoUtil.isEmpty(newGroup.getEmail()));
        assertFalse(PojoUtil.isEmpty(newGroup.getSalesforceId()));
        assertFalse(PojoUtil.isEmpty(newGroup.getGroupName()));

        assertEquals("group@email.com", newGroup.getEmail().getValue());
        assertEquals("Group Name", newGroup.getGroupName().getValue());
        assertEquals("1234567890abcde", newGroup.getSalesforceId().getValue());
        assertEquals(orcid, newGroup.getGroupOrcid().getValue());

        // Test find by email
        Group newGroup2 = manageMembers.findMember("group@email.com");
        assertNotNull(newGroup2);

        assertFalse(PojoUtil.isEmpty(newGroup2.getGroupOrcid()));
        assertFalse(PojoUtil.isEmpty(newGroup2.getEmail()));
        assertFalse(PojoUtil.isEmpty(newGroup2.getSalesforceId()));
        assertFalse(PojoUtil.isEmpty(newGroup2.getGroupName()));

        assertEquals("group@email.com", newGroup2.getEmail().getValue());
        assertEquals("Group Name", newGroup2.getGroupName().getValue());
        assertEquals("1234567890abcde", newGroup2.getSalesforceId().getValue());
        assertEquals(orcid, newGroup2.getGroupOrcid().getValue());
    }
    
    
    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void editMemberTest() throws Exception {
        Group group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));
        
        group.setEmail(Text.valueOf("new_email@user.com"));
        group.setSalesforceId(Text.valueOf(""));
        group.setGroupName(Text.valueOf("Updated Group Name"));
        
        manageMembers.updateMember(group);
        Group updatedGroup = manageMembers.findMember(group.getGroupOrcid().getValue());
        assertNotNull(updatedGroup);
        assertEquals(group.getGroupOrcid().getValue(), updatedGroup.getGroupOrcid().getValue());
        assertEquals("Updated Group Name", updatedGroup.getGroupName().getValue());
    }
    
    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void editMemberWithInvalidEmailTest() throws Exception {
        //Create one member
        Group group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(0, group.getErrors().size());
        //Try to create another member with the same email
        group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.email.already_used", new ArrayList<String>()), group.getErrors().get(0));
    }
    
    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void editMemberWithInvalidSalesforceIdTest() throws Exception {
        //Create one member
        Group group = new Group();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(0, group.getErrors().size());
        //Try to create another member with the same email
        group = new Group();
        group.setEmail(Text.valueOf("group2@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcd!"));
        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.salesforce_id.invalid", new ArrayList<String>()), group.getErrors().get(0));
    }
    
    @Test       
    public void findClientTest() throws Exception {
        //Client with all redirect uris default
        Client client_4443 = manageMembers.findClient("4444-4444-4444-4443");
        assertNotNull(client_4443);
        assertNotNull(client_4443.getDisplayName());
        assertEquals("P. Sellers III", client_4443.getDisplayName().getValue());
        assertNotNull(client_4443.getRedirectUris());
        assertEquals(1, client_4443.getRedirectUris().size());
        assertEquals("http://www.4444-4444-4444-4443.com/redirect/oauth", client_4443.getRedirectUris().get(0).getValue().getValue());
        
        //Client with redirect uri not default
        Client client_4498 = manageMembers.findClient("4444-4444-4444-4498");
        assertNotNull(client_4498);
        assertNotNull(client_4498.getDisplayName());
        assertEquals("U. Test", client_4498.getDisplayName().getValue());
        assertNotNull(client_4498.getRedirectUris());
        assertEquals(2, client_4498.getRedirectUris().size());
        
        RedirectUri rUri1 = client_4498.getRedirectUris().get(0);
        if("http://www.4444-4444-4444-4498.com/redirect/oauth".equals(rUri1.getValue().getValue())) {
            assertNotNull(rUri1.getType());
            assertEquals("default", rUri1.getType().getValue());
            assertNotNull(rUri1.getScopes());
            assertEquals(0, rUri1.getScopes().size());
        } else if ("http://www.4444-4444-4444-4498.com/redirect/oauth/grant_read_wizard".equals(rUri1.getValue().getValue())) {
            assertNotNull(rUri1.getType());
            assertEquals("grant-read-wizard", rUri1.getType().getValue());
            assertNotNull(rUri1.getScopes());
            assertEquals(1, rUri1.getScopes().size());
            assertEquals("/funding/read-limited", rUri1.getScopes().get(0));
        } else {
            fail("Invalid redirect uri: " + rUri1.getValue().getValue());
        }
        
        RedirectUri rUri2 = client_4498.getRedirectUris().get(1);
        if("http://www.4444-4444-4444-4498.com/redirect/oauth".equals(rUri2.getValue().getValue())) {
            assertNotNull(rUri2.getType());
            assertEquals("default", rUri2.getType().getValue());
            assertNotNull(rUri2.getScopes());
            assertEquals(1, rUri2.getScopes().size());
            assertEquals("", rUri2.getScopes().get(0));
        } else if ("http://www.4444-4444-4444-4498.com/redirect/oauth/grant_read_wizard".equals(rUri2.getValue().getValue())) {
            assertNotNull(rUri2.getType());
            assertEquals("grant-read-wizard", rUri2.getType().getValue());
            assertNotNull(rUri2.getScopes());
            assertEquals(1, rUri2.getScopes().size());
            assertEquals("/funding/read-limited", rUri2.getScopes().get(0));
        } else {
            fail("Invalid redirect uri: " + rUri2.getValue().getValue());
        }
    }
}
