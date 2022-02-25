package org.orcid.core.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.jena.atlas.json.JsonArray;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.manager.SalesforceManager;
import org.orcid.core.salesforce.adapter.SalesForceAdapter;
import org.orcid.core.salesforce.dao.SalesforceMicroserviceClient;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.SlugUtils;
import org.springframework.stereotype.Component;

@Component
public class SalesforceManagerImpl implements SalesforceManager {

	@Resource
    private SalesForceAdapter salesForceAdapter;
	
	@Resource
	private SalesforceMicroserviceClient client;
	
	public List<Member> retrieveMembers() {
		List<Member> members = new ArrayList<Member>();
		
		try {
			String membersListString = client.retrieveMembers();
			JSONObject membersListJsonObject = new JSONObject(membersListString);			            
			members = salesForceAdapter.createMembersListFromJson(membersListJsonObject);            			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return members;
	}

	@Override
	public MemberDetails retrieveMemberDetails(String memberSlug) {
		MemberDetails details = new MemberDetails();
		try {
			String memberDetailsString = client.retrieveMemberDetails(SlugUtils.extractIdFromSlug(memberSlug));
			JSONObject memberDetailsJsonObject = new JSONObject(memberDetailsString);			            
			if(memberDetailsJsonObject.has("member")) {
				JSONObject memberJsonObject = memberDetailsJsonObject.getJSONObject("member");
				Member member= salesForceAdapter.createMemberFromSalesForceRecord(memberJsonObject);
				details.setMember(member);
			}
			
			if(memberDetailsJsonObject.has("integrations")) {
				JSONArray integrations = memberDetailsJsonObject.getJSONArray("integrations");
				if(integrations != null && integrations.length() > 0) {
					List<Integration> integrationList = salesForceAdapter.createIntegrationsListFromJson(memberDetailsJsonObject)
					details.setIntegrations(integrationList);
				}
			}
			
			if(memberDetailsJsonObject.has("consortiumOpportunities")) {
				JSONArray consortiumOpportunities = memberDetailsJsonObject.getJSONArray("consortiumOpportunities");
				if(consortiumOpportunities != null && consortiumOpportunities.length() > 0) {
					
				}
			}
			
			if(memberDetailsJsonObject.has("contacts")) {
				JSONArray contacts = memberDetailsJsonObject.getJSONArray("contacts");
				if(contacts != null && contacts.length() > 0) {
					
				}
			}
			
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return details;
	}
	
}
