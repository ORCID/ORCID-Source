package org.orcid.frontend.web.controllers;

/**
 * @author Angel Montenegro (amontenegro) Date: 29/08/2013
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.v3.dev1.common.OrcidType;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:statistics-core-context.xml" })
public class ManageMembersControllerTest extends DBUnitTest {

    @Resource(name = "membersManagerV3")
    MembersManager membersManager;
    
    @Resource
    ManageMembersController manageMembers;

    @Resource
    private ProfileDao profileDao;

    @Resource
    ClientsController groupAdministratorController;

    @Resource
    ClientDetailsDao clientDetailsDao;
    
    @Resource(name = "sourceManagerV3")
    SourceManager sourceManager;
        
    @Mock
    SourceManager mockSourceManager;
    
    @Before
    public void beforeInstance() {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication());
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(membersManager, "sourceManager", mockSourceManager); 
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceProfile(new ProfileEntity("5555-5555-5555-0000"));
        when(mockSourceManager.retrieveSourceEntity()).thenReturn(sourceEntity);
    }
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(Arrays.asList("/data/EmptyEntityData.xml", "/data/PremiumInstitutionMemberData.xml"));
    }    

    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(membersManager, "sourceManager", sourceManager);
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/PremiumInstitutionMemberData.xml"));
    }    
        
    protected Authentication getAuthentication() {    
        OrcidProfileUserDetails details = new OrcidProfileUserDetails("5555-5555-5555-0000", "premium_institution@group.com", "", OrcidType.GROUP, MemberType.PREMIUM_INSTITUTION);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(details, "5555-5555-5555-0000", Arrays.asList(OrcidWebRole.ROLE_GROUP));
        return auth;
    }
    
    @Test   
    @Transactional
    public void createMemberProfileWithInvalidEmailsTest() throws Exception {
        ProfileEntity profile = profileDao.find("5555-5555-5555-0000");
        assertNotNull(profile);
        assertNotNull(profile.getPrimaryEmail());
        String existingEmail = profile.getPrimaryEmail().getId();
        assertNotNull(existingEmail);
        Member group = new Member();
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
    public void createMemberProfileWithInvalidGroupNameTest() throws Exception {
        Member group = new Member();
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
    public void createMemberProfileWithInvalidTypeTest() throws Exception {
        Member group = new Member();
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
    public void createMemberProfileWithInvalidSalesforceIdTest() throws Exception {
        Member group = new Member();
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
    public void createMemberProfileTest() throws Exception {
        Member group = new Member();
        String email = "group" + System.currentTimeMillis() + "@email.com";
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf(""));
        group = manageMembers.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));
    }

    @Test    
    public void findMemberByOrcidTest() throws Exception {
        Member group = new Member();
        String email = "group" + System.currentTimeMillis() + "@email.com";
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));

        // Test find by orcid
        String orcid = group.getGroupOrcid().getValue();
        Member newGroup = manageMembers.findMember(orcid);
        assertNotNull(newGroup);

        assertFalse(PojoUtil.isEmpty(newGroup.getGroupOrcid()));
        assertFalse(PojoUtil.isEmpty(newGroup.getEmail()));
        assertFalse(PojoUtil.isEmpty(newGroup.getSalesforceId()));
        assertFalse(PojoUtil.isEmpty(newGroup.getGroupName()));

        assertEquals(email, newGroup.getEmail().getValue());
        assertEquals("Group Name", newGroup.getGroupName().getValue());
        assertEquals("1234567890abcde", newGroup.getSalesforceId().getValue());
        assertEquals(orcid, newGroup.getGroupOrcid().getValue());

        // Test find by email
        Member newGroup2 = manageMembers.findMember(email);
        assertNotNull(newGroup2);

        assertFalse(PojoUtil.isEmpty(newGroup2.getGroupOrcid()));
        assertFalse(PojoUtil.isEmpty(newGroup2.getEmail()));
        assertFalse(PojoUtil.isEmpty(newGroup2.getSalesforceId()));
        assertFalse(PojoUtil.isEmpty(newGroup2.getGroupName()));

        assertEquals(email, newGroup2.getEmail().getValue());
        assertEquals("Group Name", newGroup2.getGroupName().getValue());
        assertEquals("1234567890abcde", newGroup2.getSalesforceId().getValue());
        assertEquals(orcid, newGroup2.getGroupOrcid().getValue());
    }
    
    
    @Test
    public void editMemberTest() throws Exception {
        Member group = new Member();
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
        Member updatedGroup = manageMembers.findMember(group.getGroupOrcid().getValue());
        assertNotNull(updatedGroup);
        assertEquals(group.getGroupOrcid().getValue(), updatedGroup.getGroupOrcid().getValue());
        assertEquals("Updated Group Name", updatedGroup.getGroupName().getValue());
    }
    
    @Test
    public void editMemberWithInvalidEmailTest() throws Exception {
        //Create one member
        Member group = new Member();
        String email = "group" + System.currentTimeMillis() + "@email.com";
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(0, group.getErrors().size());
        //Try to create another member with the same email
        group = new Member();
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(1, group.getErrors().size());
        assertEquals(manageMembers.getMessage("group.email.already_used", new ArrayList<String>()), group.getErrors().get(0));
    }
    
    @Test
    public void editMemberWithInvalidSalesforceIdTest() throws Exception {
        //Create one member
        Member group = new Member();
        String email = "group" + System.currentTimeMillis() + "@email.com";
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf("premium-institution"));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = manageMembers.createMember(group);
        assertNotNull(group);
        assertEquals(0, group.getErrors().size());
        //Try to create another member with the same email
        group = new Member();
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
        Client client_0002 = manageMembers.findClient("APP-0000000000000002");
        assertNotNull(client_0002);
        assertNotNull(client_0002.getDisplayName());
        assertEquals("Client # 2", client_0002.getDisplayName().getValue());
        assertNotNull(client_0002.getRedirectUris());
        assertEquals(1, client_0002.getRedirectUris().size());
        assertEquals("http://www.google.com/APP-0000000000000002/redirect/oauth", client_0002.getRedirectUris().get(0).getValue().getValue());
        
        //Client with redirect uri not default
        Client client_0003 = manageMembers.findClient("APP-0000000000000003");
        assertNotNull(client_0003);
        assertNotNull(client_0003.getDisplayName());
        assertEquals("Client # 3", client_0003.getDisplayName().getValue());
        assertNotNull(client_0003.getRedirectUris());
        assertEquals(2, client_0003.getRedirectUris().size());
        
        RedirectUri rUri1 = client_0003.getRedirectUris().get(0);
        if("http://www.google.com/APP-0000000000000003/redirect/oauth".equals(rUri1.getValue().getValue())) {
            assertNotNull(rUri1.getType());
            assertEquals("default", rUri1.getType().getValue());
            assertNotNull(rUri1.getScopes());
            assertEquals(0, rUri1.getScopes().size());
        } else if ("http://www.google.com/APP-0000000000000003/redirect/oauth/grant_read_wizard".equals(rUri1.getValue().getValue())) {
            assertNotNull(rUri1.getType());
            assertEquals("grant-read-wizard", rUri1.getType().getValue());
            assertNotNull(rUri1.getScopes());
            assertEquals(1, rUri1.getScopes().size());
            assertEquals("/funding/read-limited", rUri1.getScopes().get(0));
        } else {
            fail("Invalid redirect uri: " + rUri1.getValue().getValue());
        }
        
        RedirectUri rUri2 = client_0003.getRedirectUris().get(1);
        if("http://www.google.com/APP-0000000000000003/redirect/oauth".equals(rUri2.getValue().getValue())) {
            assertNotNull(rUri2.getType());
            assertEquals("default", rUri2.getType().getValue());
            assertNotNull(rUri2.getScopes());
            assertEquals(0, rUri2.getScopes().size());            
        } else if ("http://www.google.com/APP-0000000000000003/redirect/oauth/grant_read_wizard".equals(rUri2.getValue().getValue())) {
            assertNotNull(rUri2.getType());
            assertEquals("grant-read-wizard", rUri2.getType().getValue());
            assertNotNull(rUri2.getScopes());
            assertEquals(1, rUri2.getScopes().size());
            assertEquals("/funding/read-limited", rUri2.getScopes().get(0));
        } else {
            fail("Invalid redirect uri: " + rUri2.getValue().getValue());
        }
    }
    
    @Test    
    public void editClientWithInvalidRedirectUriTest() throws Exception {
        //Client with all redirect uris default
        Client client_0002 = manageMembers.findClient("APP-0000000000000002");
        assertNotNull(client_0002);
        RedirectUri rUri = new RedirectUri();
        rUri.setType(Text.valueOf("default"));
        rUri.setValue(Text.valueOf("http://érm.com"));
        
        client_0002.getRedirectUris().add(rUri);
        
        client_0002 = manageMembers.updateClient(client_0002);
        
        assertNotNull(client_0002);
        assertEquals(1, client_0002.getErrors().size());
        assertEquals(manageMembers.getMessage("manage.developer_tools.invalid_redirect_uri"), client_0002.getErrors().get(0));                
    }
    
    @Test
    public void editMemberDoesntChangePersistentTokenEnabledValueTest() throws Exception {
        Client clientWithPersistentTokensEnabled = manageMembers.findClient("APP-0000000000000001");
        assertNotNull(clientWithPersistentTokensEnabled);
        assertNotNull(clientWithPersistentTokensEnabled.getDisplayName());
        assertEquals("Client # 1", clientWithPersistentTokensEnabled.getDisplayName().getValue());
        assertNotNull(clientWithPersistentTokensEnabled.getPersistentTokenEnabled());
        assertTrue(clientWithPersistentTokensEnabled.getPersistentTokenEnabled().getValue());
        
        clientWithPersistentTokensEnabled.getDisplayName().setValue("Updated Name");
        manageMembers.updateClient(clientWithPersistentTokensEnabled);
        
        Client updatedClient =  manageMembers.findClient("APP-0000000000000001");
        assertNotNull(updatedClient);
        assertNotNull(updatedClient.getDisplayName());
        assertEquals("Updated Name", updatedClient.getDisplayName().getValue());
        assertNotNull(updatedClient.getPersistentTokenEnabled());
        assertTrue(updatedClient.getPersistentTokenEnabled().getValue());
    }
    
    @Test
    public void editGroupTypeTest() throws Exception {
        Member group_0000 = manageMembers.findMember("5555-5555-5555-0000");
        assertNotNull(group_0000);
        assertNotNull(group_0000.getType());
        assertEquals(MemberType.PREMIUM_INSTITUTION.value(), group_0000.getType().getValue());
        
        // Update group type to basic
        group_0000.setType(Text.valueOf(MemberType.BASIC.value()));
        manageMembers.updateMember(group_0000);                                
        
        group_0000 = manageMembers.findMember("5555-5555-5555-0000");
        assertNotNull(group_0000);
        assertNotNull(group_0000.getType());
        assertEquals(MemberType.BASIC.value(), group_0000.getType().getValue());
        
    }
}
