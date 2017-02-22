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
package org.orcid.core.salesforce.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.Opportunity;

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
    public void testCreateOpportunityFromSalesForceRecord() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/core/salesforce/salesforce_opportunities_list.json"));
        JSONArray inputArray = new JSONArray(inputString);
        Opportunity opportunity = salesForceAdapter.createOpportunityFromSalesForceRecord(inputArray.getJSONObject(1));
        assertEquals("[ORG2 ACCOUNT ID]", opportunity.getTargetAccountId());
        assertEquals("Invoice Paid", opportunity.getStageName());
        assertEquals("2016-12-21", opportunity.getCloseDate());
        assertEquals("New", opportunity.getType());
        assertEquals("[PREMIUM CONSORTIUM MEMBER ID]", opportunity.getMemberType());
        assertEquals("2017-01-01", opportunity.getMembershipStartDate());
        assertEquals("2017-12-31", opportunity.getMembershipEndDate());
        assertEquals("[ORG1 ACCOUNT ID]", opportunity.getConsortiumLeadId());
        assertEquals("2017 Membership-Org 2 Consortium Member", opportunity.getName());
        assertEquals("[CONSORTIUM MEMBER RECORD TYPE ID]", opportunity.getRecordTypeId());
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
    }

    @Test
    public void testCreateSalesForceRecordFromContact() {
        Contact contact = new Contact();
        contact.setAccountId("1234");
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromContact(contact);
        assertEquals("{\"AccountId\":\"1234\"}", contactJson.toString());
    }

}
