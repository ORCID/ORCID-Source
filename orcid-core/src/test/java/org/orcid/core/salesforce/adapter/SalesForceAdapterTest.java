package org.orcid.core.salesforce.adapter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.orcid.core.salesforce.model.Member;

public class SalesForceAdapterTest {

    private SalesForceAdapter salesForceAdapter = new SalesForceAdapter();

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
        assertEquals("Research Institute", member.getResearchCommunity());
        assertEquals("New Zealand", member.getCountry());
        assertEquals("This is the public display description for Org 2 Consortium Member", member.getDescription());
        assertEquals("https://dl.dropboxusercontent.com/s/yk2tgl9ze7z7y2g/test_logo.png", member.getLogoUrl().toString());
        assertEquals("orcid@org2.org", member.getPublicDisplayEmail());
        assertEquals("006J000000LThfbIAD", member.getMainOpportunityId());
        assertEquals("001J000001pZwWWIA0", member.getConsortiumLeadId());
    }

}
