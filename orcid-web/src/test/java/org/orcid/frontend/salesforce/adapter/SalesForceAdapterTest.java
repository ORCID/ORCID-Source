package org.orcid.frontend.salesforce.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.frontend.salesforce.model.Achievement;
import org.orcid.frontend.salesforce.model.Integration;
import org.orcid.frontend.salesforce.model.Member;

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
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/frontend/salesforce/salesforce_members_list.json"), Charset.defaultCharset());
        JSONObject inputObject = new JSONObject(inputString);
        JSONArray records = inputObject.getJSONArray("records");
        Member member = salesForceAdapter.createMemberFromSalesForceRecord(records.getJSONObject(0));
        assertEquals("001J000001pZwWXIA0", member.getId());
        assertEquals("Org 2 Consortium Member", member.getName());
        assertEquals("Org 2 Consortium Member Public name", member.getPublicDisplayName());
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
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/frontend/salesforce/salesforce_members_list.json"), Charset.defaultCharset());
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
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/frontend/salesforce/salesforce_members_list.json"), Charset.defaultCharset());
        JSONObject inputObject = new JSONObject(inputString);
        List<Member> membersList = salesForceAdapter.createMembersListFromJson(inputObject);
        assertEquals(4, membersList.size());
        Member member = membersList.get(0);
        assertEquals("001J000001pZwWXIA0", member.getId());
        assertEquals("Org 2 Consortium Member", member.getName());
        assertEquals("Org 2 Consortium Member Public name", member.getPublicDisplayName());
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
    public void testCreateIntegrationsListFromJson() throws IOException, JSONException {
        String inputString = IOUtils.toString(getClass().getResourceAsStream("/org/orcid/frontend/salesforce/salesforce_integrations_list.json"), Charset.defaultCharset());
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

}
