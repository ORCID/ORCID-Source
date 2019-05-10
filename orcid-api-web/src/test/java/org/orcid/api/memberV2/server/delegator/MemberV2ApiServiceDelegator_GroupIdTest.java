package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.issn.IssnData;
import org.orcid.core.issn.client.IssnClient;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.common_v2.CreatedDate;
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.persistence.dao.GroupIdRecordDao;
import org.orcid.persistence.jpa.entities.GroupIdRecordEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.Utils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegator_GroupIdTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV2ApiServiceDelegator")
    protected MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testGetGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.viewGroupIdRecord(Long.valueOf("2"));
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        Utils.verifyLastModified(groupIdRecord.getLastModifiedDate());
        assertEquals(Long.valueOf(2), groupIdRecord.getPutCode());
        assertEquals("issn:0000002", groupIdRecord.getGroupId());
        assertEquals("TestGroup2", groupIdRecord.getName());
        assertEquals("TestDescription2", groupIdRecord.getDescription());
        assertEquals("publisher", groupIdRecord.getType());
    }

    @Test
    public void testCreateGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.createGroupIdRecord(Utils.getGroupIdRecord());
        assertNotNull(response.getMetadata().get("Location").get(0));
    }

    @Test
    public void testUpdateGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        Utils.verifyLastModified(groupIdRecord.getLastModifiedDate());
        LastModifiedDate before = groupIdRecord.getLastModifiedDate();
        // Verify the name
        assertEquals(groupIdRecord.getName(), "TestGroup3");
        // Set a new name for update
        groupIdRecord.setName("TestGroup33");
        serviceDelegator.updateGroupIdRecord(groupIdRecord, Long.valueOf("3"));

        // Get the entity again and verify the name
        response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecordNew = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecordNew);
        Utils.verifyLastModified(groupIdRecordNew.getLastModifiedDate());
        assertTrue(groupIdRecordNew.getLastModifiedDate().after(before));
        // Verify the name
        assertEquals(groupIdRecordNew.getName(), "TestGroup33");
    }

    @Test(expected = GroupIdRecordNotFoundException.class)
    public void testDeleteGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        // Verify if the record exists
        Response response = serviceDelegator.viewGroupIdRecord(5L);
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        // Delete the record
        serviceDelegator.deleteGroupIdRecord(5L);
        // Throws a record not found exception
        serviceDelegator.viewGroupIdRecord(5L);
    }

    @Test
    public void testGetGroupIdRecords() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        /*
         * At this point there should be at least 3 group ids and no more than
         * 5, since we are not sure if testDeleteGroupIdRecord and
         * testCreateGroupIdRecord have ran or not
         */

        // So, get a page with all
        Response response = serviceDelegator.viewGroupIdRecords("5", "1");
        assertNotNull(response);
        GroupIdRecords groupIdRecords1 = (GroupIdRecords) response.getEntity();
        assertNotNull(groupIdRecords1);
        assertNotNull(groupIdRecords1.getGroupIdRecord());

        int total = groupIdRecords1.getTotal();
        if (total < 3 || total > 5) {
            fail("There are more group ids than the expected, we are expecting between 3 and 5, total: " + total);
        }
    }
    
    @Test
    public void testFindGroupIdByName() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.findGroupIdRecordByName("TestGroup1");
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals("TestGroup1", groupIdRecord.getName());
    }
    
    @Test
    public void testFindGroupIdByGroupId() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.findGroupIdRecordByGroupId("issn:0000001");
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals("issn:0000001", groupIdRecord.getGroupId());
    }
    
    @Test
    public void testFindGroupIdRecordByNonExistentIssnGroupId() {
        GroupIdRecordManager groupIdRecordManager = (GroupIdRecordManager) ReflectionTestUtils.getField(serviceDelegator, "groupIdRecordManager");
        GroupIdRecordDao groupIdRecordDao = (GroupIdRecordDao) ReflectionTestUtils.getField(groupIdRecordManager, "groupIdRecordDao");
        IssnClient issnClient = (IssnClient) ReflectionTestUtils.getField(groupIdRecordManager, "issnClient");
        
        GroupIdRecordDao mockGroupIdDao = Mockito.mock(GroupIdRecordDao.class);
        Mockito.doNothing().when(mockGroupIdDao).persist(Mockito.any(GroupIdRecordEntity.class));
        Mockito.when(mockGroupIdDao.findByGroupId(Mockito.eq("issn:98765432"))).thenThrow(NoResultException.class);
        ReflectionTestUtils.setField(groupIdRecordManager, "groupIdRecordDao", mockGroupIdDao);
        
        GroupIdRecord record = new GroupIdRecord();
        record.setGroupId("issn:98765432");
        GregorianCalendar cal = new GregorianCalendar();
        record.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(cal)));
        record.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(cal)));
        record.setName("some journal");
        record.setType("journal");
        record.setSource(new Source()); // XXX ORCID - which client?
        
        IssnClient mockIssnClient = Mockito.mock(IssnClient.class);
        IssnData issnData = new IssnData();
        issnData.setIssn("98765432");
        issnData.setMainTitle("some journal");
        Mockito.when(mockIssnClient.getIssnData(Mockito.eq("98765432"))).thenReturn(issnData);
        
        ReflectionTestUtils.setField(groupIdRecordManager, "issnClient", mockIssnClient);
        
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.findGroupIdRecordByGroupId("issn:98765432");
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals("issn:98765432", groupIdRecord.getGroupId());
        
        ArgumentCaptor<GroupIdRecordEntity> captor = ArgumentCaptor.forClass(GroupIdRecordEntity.class);
        Mockito.verify(mockGroupIdDao).persist(captor.capture());
        
        GroupIdRecordEntity entity = captor.getValue();
        assertEquals("journal", entity.getGroupType());
        assertEquals("issn:98765432", entity.getGroupId());
        assertEquals("some journal", entity.getGroupName());
        
        ReflectionTestUtils.setField(groupIdRecordManager, "issnClient", issnClient);
        ReflectionTestUtils.setField(groupIdRecordManager, "groupIdRecordDao", groupIdRecordDao);
    }
    
    @Test
    public void testCreateGroupIdRecordWithNonExistentIssnGroupId() {
        GroupIdRecordManager groupIdRecordManager = (GroupIdRecordManager) ReflectionTestUtils.getField(serviceDelegator, "groupIdRecordManager");
        GroupIdRecordDao groupIdRecordDao = (GroupIdRecordDao) ReflectionTestUtils.getField(groupIdRecordManager, "groupIdRecordDao");
        IssnClient issnClient = (IssnClient) ReflectionTestUtils.getField(groupIdRecordManager, "issnClient");
        
        GroupIdRecordDao mockGroupIdDao = Mockito.mock(GroupIdRecordDao.class);
        Mockito.doNothing().when(mockGroupIdDao).persist(Mockito.any(GroupIdRecordEntity.class));
        Mockito.when(mockGroupIdDao.findByGroupId(Mockito.eq("issn:98765432"))).thenThrow(NoResultException.class);
        ReflectionTestUtils.setField(groupIdRecordManager, "groupIdRecordDao", mockGroupIdDao);
        
        GroupIdRecord record = new GroupIdRecord();
        record.setGroupId("issn:98765432");
        GregorianCalendar cal = new GregorianCalendar();
        record.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(cal)));
        record.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(cal)));
        record.setName("some journal");
        record.setType("journal");
        record.setSource(new Source()); // XXX ORCID - which client?
        
        IssnClient mockIssnClient = Mockito.mock(IssnClient.class);
        IssnData issnData = new IssnData();
        issnData.setIssn("98765432");
        issnData.setMainTitle("some journal");
        Mockito.when(mockIssnClient.getIssnData(Mockito.eq("98765432"))).thenReturn(issnData);
        
        ReflectionTestUtils.setField(groupIdRecordManager, "issnClient", mockIssnClient);
        
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        
        try {
            serviceDelegator.createGroupIdRecord(record);
            fail();
        } catch (DuplicatedGroupIdRecordException e) {
            ArgumentCaptor<GroupIdRecordEntity> captor = ArgumentCaptor.forClass(GroupIdRecordEntity.class);
            Mockito.verify(mockGroupIdDao).persist(captor.capture());
            
            GroupIdRecordEntity entity = captor.getValue();
            assertEquals("journal", entity.getGroupType());
            assertEquals("issn:98765432", entity.getGroupId());
            assertEquals("some journal", entity.getGroupName());
            
            ReflectionTestUtils.setField(groupIdRecordManager, "issnClient", issnClient);
            ReflectionTestUtils.setField(groupIdRecordManager, "groupIdRecordDao", groupIdRecordDao);
        }
    }
    
}
