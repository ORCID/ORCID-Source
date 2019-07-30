package org.orcid.core.manager.v3;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidGenerationManager;
import org.orcid.core.manager.v3.EmailManager;
import org.orcid.core.manager.v3.MembersManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class MembersManagerTest extends DBUnitTest {
    
    @Resource(name = "emailManagerV3")
    EmailManager emailManager;  
    
    @Resource
    MembersManager membersManager; 
    
    @Resource
    ProfileDao profileDao;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource(name = "sourceManagerV3")
    SourceManager sourceManager;
    
    @Resource
    private OrcidGenerationManager orcidGenerationManager;
    
    @Resource
    private EncryptionManager encryptionManager;
    
    @Mock
    EmailManager mockEmailManager;
        
    @Mock
    SourceManager mockSourceManager;
    
    @Mock 
    ProfileHistoryEventManager mockProfileHistoryEventManager;
    
    @Mock 
    OrcidGenerationManager mockOrcidGenerationManager;
    
    @Mock 
    EncryptionManager mockEncryptionManager;
    
    @Resource
    ProfileHistoryEventManager profileHistoryEventManager;
    
    @Resource
    EmailFrequencyManager emailFrequencyManager;
    
    @Resource
    EmailFrequencyManager mockEmailFrequencyManager;
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(membersManager, "sourceManager", mockSourceManager); 
        TargetProxyHelper.injectIntoProxy(emailFrequencyManager, "profileHistoryEventManager", mockProfileHistoryEventManager); 
        
        SourceEntity sourceEntity = new SourceEntity();
        sourceEntity.setSourceProfile(new ProfileEntity("5555-5555-5555-0000"));
        when(mockSourceManager.retrieveActiveSourceEntity()).thenReturn(sourceEntity);

    }
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        initDBUnitData(Arrays.asList("/data/EmptyEntityData.xml", "/data/PremiumInstitutionMemberData.xml"));
    }    

    @After
    public void after() {
        TargetProxyHelper.injectIntoProxy(membersManager, "sourceManager", sourceManager);
        TargetProxyHelper.injectIntoProxy(emailFrequencyManager, "profileHistoryEventManager", profileHistoryEventManager);         
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        removeDBUnitData(Arrays.asList("/data/PremiumInstitutionMemberData.xml"));
    }

    @Test
    public void createMember() {
        String email = "group" + System.currentTimeMillis() + "@email.com";
        Member group = new Member();
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf(MemberType.PREMIUM.value()));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = membersManager.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));     
        assertNotNull(group);
        assertTrue(OrcidStringUtils.isValidOrcid(group.getGroupOrcid().getValue()));
        Map<String, String> map = emailManager.findOricdIdsByCommaSeparatedEmails(email);
        assertNotNull(map);
        assertEquals(group.getGroupOrcid().getValue(), map.get(email));
        ProfileEntity entity = profileDao.find(group.getGroupOrcid().getValue());
        assertNotNull(entity);
        assertEquals(true, entity.isReviewed());
    }
    
    @Test
    public void updateMember() throws Exception {
        Member group = new Member();
        group.setEmail(Text.valueOf("group@email.com"));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf(MemberType.PREMIUM.value()));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = membersManager.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));
        
        group.setEmail(Text.valueOf("new_email@user.com"));
        group.setSalesforceId(Text.valueOf(""));
        group.setGroupName(Text.valueOf("Updated Group Name"));
        
        membersManager.updateMemeber(group);
        Member updatedGroup = membersManager.getMember(group.getGroupOrcid().getValue());
        assertNotNull(updatedGroup);
        assertEquals(group.getGroupOrcid().getValue(), updatedGroup.getGroupOrcid().getValue());
        assertEquals("Updated Group Name", updatedGroup.getGroupName().getValue());
    }
    
    @Test    
    public void findMemberTest() throws Exception {
        Member group = new Member();
        String email = "group" + System.currentTimeMillis() + "@email.com";
        group.setEmail(Text.valueOf(email));
        group.setGroupName(Text.valueOf("Group Name"));
        group.setType(Text.valueOf(MemberType.PREMIUM.value()));
        group.setSalesforceId(Text.valueOf("1234567890abcde"));
        group = membersManager.createMember(group);
        assertEquals(0, group.getErrors().size());
        assertFalse(PojoUtil.isEmpty(group.getGroupOrcid()));

        // Test find by orcid
        String orcid = group.getGroupOrcid().getValue();
        Member newGroup = membersManager.getMember(orcid);
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
        Member newGroup2 = membersManager.getMember(email);
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

}
