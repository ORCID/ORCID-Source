package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecords;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.Qualification;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkBulk;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_GroupIdTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegator")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;

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
}
