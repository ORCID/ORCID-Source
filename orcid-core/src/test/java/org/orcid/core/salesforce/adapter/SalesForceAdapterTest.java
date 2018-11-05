package org.orcid.core.salesforce.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.orcid.core.salesforce.model.Achievement;
import org.orcid.core.salesforce.model.Badge;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.OrgId;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceAdapterTest {

    private SalesForceAdapter salesForceAdapter = new SalesForceAdapter();
    private SalesForceMapperFacadeFactory salesForceMapperFacadeFactory = new SalesForceMapperFacadeFactory();
    {
        try {
            salesForceAdapter.setMapperFacade(salesForceMapperFacadeFactory.getMemberMapperFacade());
        } catch (Exception e) {
            throw new RuntimeException("Error initializing mapper", e);
        }
    }

    @Test
    public void testCreateMemberFromSalesForceRecord() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_members_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        JSONArray records = inputObject.getJSONArray("records");
        Member member = salesForceAdapter.createMemberFromSalesForceRecord(records.getJSONObject(0));
        assertEquals("001J000001pZwWXIA0", member.getId());
        assertEquals("Org 2 Consortium Member", member.getName());
        assertEquals("001J000001pZwWXIA0-org-2-consortium-member", member.getSlug());
        assertEquals("http://org2.edu", member.getWebsiteUrl().toString());
        assertEquals(CommunityType.RESEARCH_INSTITUTE, member.getResearchCommunity());
        assertEquals("New Zealand", member.getCountry());
        assertEquals("This is the public display description for Org 2 Consortium Member", member.getDescription());
        assertEquals("https://dl.dropboxusercontent.com/s/yk2tgl9ze7z7y2g/test_logo.png", member.getLogoUrl().toString());
        assertEquals("orcid@org2.org", member.getPublicDisplayEmail());
        assertEquals("006J000000LThfbIAD", member.getMainOpportunityId());
        assertEquals("001J000001pZwWWIA0", member.getConsortiumLeadId());
        assertEquals("2017-02-03", member.getLastMembershipStartDate());
    }

    @Test
    public void testCreateMemberFromSalesForceRecordWithNullValues() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_members_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        JSONArray records = inputObject.getJSONArray("records");
        JSONObject record = records.getJSONObject(1);
        record.put("Name", (String) null);
        Member member = salesForceAdapter.createMemberFromSalesForceRecord(record);
        assertEquals("001J000001pZwWYIA0", member.getId());
        assertNull(member.getName());
        assertEquals("http://org3.edu", member.getWebsiteUrl().toString());
        assertEquals(CommunityType.RESEARCH_INSTITUTE, member.getResearchCommunity());
        assertEquals("New Zealand", member.getCountry());
        assertEquals("This is the public display description for Org 3 Consortium Member", member.getDescription());
        assertEquals("https://dl.dropboxusercontent.com/s/yk2tgl9ze7z7y2g/test_logo.png", member.getLogoUrl().toString());
        assertEquals("orcid@org3.org", member.getPublicDisplayEmail());
        assertEquals("006J000000LThfcIAD", member.getMainOpportunityId());
        assertNull(member.getConsortiumLeadId());
        assertNull(member.getLastMembershipStartDate());
    }

    @Test
    public void testCreateMembersListFromJson() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_members_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        List<Member> membersList = salesForceAdapter.createMembersListFromJson(inputObject);
        assertEquals(4, membersList.size());
        Member member = membersList.get(0);
        assertEquals("001J000001pZwWXIA0", member.getId());
        assertEquals("Org 2 Consortium Member", member.getName());
        assertEquals("001J000001pZwWXIA0-org-2-consortium-member", member.getSlug());
        assertEquals("http://org2.edu", member.getWebsiteUrl().toString());
        assertEquals(CommunityType.RESEARCH_INSTITUTE, member.getResearchCommunity());
        assertEquals("New Zealand", member.getCountry());
        assertEquals("This is the public display description for Org 2 Consortium Member", member.getDescription());
        assertEquals("https://dl.dropboxusercontent.com/s/yk2tgl9ze7z7y2g/test_logo.png", member.getLogoUrl().toString());
        assertEquals("orcid@org2.org", member.getPublicDisplayEmail());
        assertEquals("006J000000LThfbIAD", member.getMainOpportunityId());
        assertEquals("001J000001pZwWWIA0", member.getConsortiumLeadId());
        assertEquals("2017-02-03", member.getLastMembershipStartDate());
    }

    @Test
    public void testCreateSalesForceRecordFromMember() throws MalformedURLException {
        Member member = new Member();
        member.setName("Org 1 Consortium Lead New Name");
        member.setWebsiteUrl(new URL("http://org1newsite.org"));
        JSONObject record = salesForceAdapter.createSaleForceRecordFromMember(member);
        assertEquals(
                "{\"Name\":\"Org 1 Consortium Lead New Name\",\"Public_Display_Name__c\":\"Org 1 Consortium Lead New Name\",\"Website\":\"http:\\/\\/org1newsite.org\"}",
                record.toString());
    }
    
    @Test
    public void testCreateSalesForceRecordFromMemberWithNoWebsite() throws MalformedURLException {
        Member member = new Member();
        member.setName("Org 1 Consortium Lead New Name");
        member.setWebsiteUrl(null);
        JSONObject record = salesForceAdapter.createSaleForceRecordFromMember(member);
        assertEquals(
                "{\"Name\":\"Org 1 Consortium Lead New Name\",\"Public_Display_Name__c\":\"Org 1 Consortium Lead New Name\",\"Website\":null}",
                record.toString());
    }

    @Test
    public void testCreateOpportunityFromSalesForceRecord() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_opportunities_list.json"));
        JSONArray inputArray = new JSONArray(inputString);
        Opportunity opportunity = salesForceAdapter.createOpportunityFromSalesForceRecord(inputArray.getJSONObject(1));
        assertEquals("[ORG2 ACCOUNT ID]", opportunity.getTargetAccountId());
        assertEquals("Another consortium member org", opportunity.getAccountName());
        assertEquals("Another consortium member org Public Display Name", opportunity.getAccountPublicDisplayName());
        assertEquals("Invoice Paid", opportunity.getStageName());
        assertEquals("2016-12-21", opportunity.getCloseDate());
        assertEquals("New", opportunity.getType());
        assertEquals("[PREMIUM CONSORTIUM MEMBER ID]", opportunity.getMemberType());
        assertEquals("2017-01-01", opportunity.getMembershipStartDate());
        assertEquals("2017-12-31", opportunity.getMembershipEndDate());
        assertEquals("[ORG1 ACCOUNT ID]", opportunity.getConsortiumLeadId());
        assertEquals("2017 Membership-Org 2 Consortium Member", opportunity.getName());
        assertEquals("[CONSORTIUM MEMBER RECORD TYPE ID]", opportunity.getRecordTypeId());
        assertEquals("Next step description", opportunity.getNextStep());
        assertTrue(opportunity.isRemovalRequested());
    }

    @Test
    public void testCreateContactFromJson() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_contacts_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        JSONArray records = inputObject.getJSONArray("records");
        JSONObject record = records.getJSONObject(0);
        JSONObject contactRoles = record.getJSONObject("Membership_Contact_Roles__r");
        JSONArray contactRoleRecords = contactRoles.getJSONArray("records");
        JSONObject contactRole = contactRoleRecords.getJSONObject(0);
        Contact contact = salesForceAdapter.createContactFromJson(contactRole);
        assertEquals("Contact1FirstName Contact1LastName", contact.getName());
        assertEquals("contact1@mailinator.com", contact.getEmail());
        assertEquals(ContactRoleType.MAIN_CONTACT, contact.getRole().getRoleType());
        assertTrue(contact.getRole().isVotingContact());
        assertTrue(contact.getRole().isCurrent());
    }

    @Test
    public void testCreateContactsFromJson() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_contacts_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        List<Contact> contactsList = salesForceAdapter.createContactsWithRolesFromJson(inputObject);
        assertEquals(2, contactsList.size());
        Contact contact = contactsList.get(0);
        assertEquals("Contact1FirstName Contact1LastName", contact.getName());
        assertEquals("contact1@mailinator.com", contact.getEmail());
        assertEquals(ContactRoleType.MAIN_CONTACT, contact.getRole().getRoleType());
        assertTrue(contact.getRole().isVotingContact());
        assertTrue(contact.getRole().isCurrent());
    }

    @Test
    public void testCreateSalesForceRecordFromContact() {
        Contact contact = new Contact();
        contact.setAccountId("1234");
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromContact(contact);
        assertEquals("{\"AccountId\":\"1234\"}", contactJson.toString());
    }
    
    @Test
    public void testCreateOrgIdFromJson() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_org_ids_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        JSONArray records = inputObject.getJSONArray("records");
        JSONObject record = records.getJSONObject(0);
        OrgId orgId = salesForceAdapter.createOrgIdFromJson(record);
        assertEquals("abcd", orgId.getOrgIdValue());
        assertEquals("FundRef ID", orgId.getOrgIdType());
        assertFalse(orgId.getInactive());
    }

    @Test
    public void testCreateOrgIdsFromJson() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_org_ids_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        List<OrgId> orgIdsList = salesForceAdapter.createOrgIdsFromJson(inputObject);
        assertEquals(2, orgIdsList.size());
        OrgId orgId = orgIdsList.get(0);
        assertEquals("abcd", orgId.getOrgIdValue());
        assertEquals("FundRef ID", orgId.getOrgIdType());
        assertFalse(orgId.getInactive());
    }

    @Test
    public void testCreateSalesForceRecordFromOrgId() {
        OrgId contact = new OrgId();
        contact.setAccountId("1234");
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromOrgId(contact);
        assertEquals("{\"Organization__c\":\"1234\"}", contactJson.toString());
    }
    
    @Test
    public void testCreateIntegrationsListFromJson() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_integrations_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        
        List<Integration> membersList = salesForceAdapter.createIntegrationsListFromJson(inputObject);
        
        assertEquals(3, membersList.size());
        
        Integration integrationNewStyleBadgeAwarded = membersList.get(0);
        assertEquals("Will's custom system new style", integrationNewStyleBadgeAwarded.getName());
        List<Achievement> achievements = integrationNewStyleBadgeAwarded.getAchievements();
        assertNotNull(achievements);
        assertEquals(2, achievements.size());
        assertEquals("a0N3D000001Jy5cUAC", achievements.get(0).getBadgeId());
        assertEquals("a0N3D000001Jy5eUAC", achievements.get(1).getBadgeId());
        
        Integration integrationNoBadge = membersList.get(1);
        assertEquals("Integration using Will's vendor system", integrationNoBadge.getName());
        
        
        Integration integrationOldStyleBadAwarded = membersList.get(2);
        assertEquals("Will's custom system old style", integrationOldStyleBadAwarded.getName());
    }
    
    @Test
    public void testCreateBadgesListFromJson() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_badges_list.json"));
        JSONObject inputObject = new JSONObject(inputString);
        
        List<Badge> badgesList = salesForceAdapter.createBadgesListFromJson(inputObject);
        
        assertEquals(5, badgesList.size());
        
        Badge authenticateBadge = badgesList.get(0);
        assertEquals("a0N3D000001Jy5eUAC", authenticateBadge.getId());
        assertEquals("AUTHENTICATE", authenticateBadge.getName());
        assertEquals("Authenticating ORCID iDs using API ensures that the iD belongs to the researcher, that it is correct (e.g., no data entry typographical errors), and that the researcher agrees to it being used. In addition, the API enables verification through an OAuth pr", authenticateBadge.getPublicDescription());
        assertEquals(1.0f, authenticateBadge.getIndex(), 0);
        assertEquals("1 - AUTHENTICATE", authenticateBadge.getIndexAndName());
    }

}
