package org.orcid.frontend.salesforce.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.frontend.salesforce.adapter.SalesForceAdapter;
import org.orcid.frontend.salesforce.client.SalesforceMicroserviceClient;
import org.orcid.frontend.salesforce.model.Integration;
import org.orcid.frontend.salesforce.model.Member;
import org.orcid.frontend.salesforce.model.MemberDetails;
import org.orcid.frontend.salesforce.model.Opportunity;
import org.orcid.frontend.salesforce.model.SubMember;
import org.springframework.stereotype.Component;

@Component
public class SalesforceManagerImpl implements SalesforceManager {

    private static final List<String> visibleStatuses = List.of("In Development", "Complete", "Certified Service Provider", "NONE_PLANNED");
    
    @Resource
    private SalesForceAdapter salesForceAdapter;

    @Resource
    private SalesforceMicroserviceClient client;

    @Override
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
    public MemberDetails retrieveMemberDetails(String memberId) {
        MemberDetails details = new MemberDetails();                
        try {
            String memberDetailsString = client.retrieveMemberDetails(memberId);
            JSONObject memberDetailsJsonObject = new JSONObject(memberDetailsString);

            // Set parent org name
            String parentOrgName = memberDetailsJsonObject.has("parentOrgName") ? memberDetailsJsonObject.getString("parentOrgName") : null;
            details.setParentOrgName(parentOrgName);

            if (memberDetailsJsonObject.has("member")) {
                JSONObject memberJsonObject = memberDetailsJsonObject.getJSONObject("member");
                // Set parent id
                String consortiumLeadId = memberJsonObject.has("Consortium_Lead__c") ? memberJsonObject.getString("Consortium_Lead__c") : null;
                details.setParentId(consortiumLeadId);
                // Set member details
                Member member = salesForceAdapter.createMemberFromSalesForceRecord(memberJsonObject);                
                details.setMember(member);
            }

            if (memberDetailsJsonObject.has("integrations")) {
                JSONArray integrations = memberDetailsJsonObject.getJSONArray("integrations");
                if (integrations != null && integrations.length() > 0) {
                    List<Integration> integrationList = salesForceAdapter.createIntegrationsList(integrations);
                    // Filter by integration status
                    List<Integration> filteredIntegrationList = integrationList.stream()
                            .filter(x -> {
                                if(visibleStatuses.contains(x.getStage())) {
                                    // NONE_PLANNED should be displayed as "None Planned" 
                                    if(x.getStage().equals("NONE_PLANNED")) {
                                        x.setStage("None Planned");
                                    }
                                    return true;
                                }
                                return false;
                            }).collect(Collectors.toList());
                    details.setIntegrations(filteredIntegrationList);
                } else {
                    details.setIntegrations(List.of());
                }
            } else {
                details.setIntegrations(List.of());
            }

            if (memberDetailsJsonObject.has("consortiumOpportunities")) {
                JSONArray consortiumOpportunities = memberDetailsJsonObject.getJSONArray("consortiumOpportunities");
                if (consortiumOpportunities != null && consortiumOpportunities.length() > 0) {
                    List<Opportunity> opportunityList = salesForceAdapter.createOpportunitiesList(consortiumOpportunities);
                    // Create the sub members list
                    List<SubMember> subMembers = opportunityList.stream().map(o -> {
                        SubMember subMember = new SubMember();
                        subMember.setOpportunity(o);
                        subMember.setAccountId(o.getTargetAccountId());
                        return subMember;
                    }).collect(Collectors.toList());
                    details.setSubMembers(subMembers);
                } else {
                    details.setSubMembers(List.of());
                }
            } else {
                details.setSubMembers(List.of());
            }

            if (memberDetailsJsonObject.has("contacts")) {
                JSONArray contacts = memberDetailsJsonObject.getJSONArray("contacts");
                if (contacts != null && contacts.length() > 0) {
                    // TODO
                } else {
                    details.setContacts(List.of());
                }
            } else {
                details.setContacts(List.of());
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

    @Override
    public List<Member> retrieveConsortiaList() {
        List<Member> members = new ArrayList<Member>();

        try {
            String membersListString = client.retrieveConsortiaList();
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

}
