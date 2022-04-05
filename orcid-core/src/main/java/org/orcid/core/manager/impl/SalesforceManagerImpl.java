package org.orcid.core.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.manager.SalesforceManager;
import org.orcid.core.salesforce.adapter.SalesForceAdapter;
import org.orcid.core.salesforce.dao.SalesforceMicroserviceClient;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.SlugUtils;
import org.orcid.core.salesforce.model.SubMember;
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

            // Set parent org name
            String parentOrgName = memberDetailsJsonObject.has("parentOrgName") ? memberDetailsJsonObject.getString("parentOrgName") : null;
            details.setParentOrgName(parentOrgName);

            if (memberDetailsJsonObject.has("member")) {
                JSONObject memberJsonObject = memberDetailsJsonObject.getJSONObject("member");
                // Set parent slug
                String consortiumLeadId = memberJsonObject.has("Consortium_Lead__c") ? memberJsonObject.getString("Consortium_Lead__c") : null;
                details.setParentOrgSlug(SlugUtils.createSlug(consortiumLeadId, parentOrgName));
                // Set member details
                Member member = salesForceAdapter.createMemberFromSalesForceRecord(memberJsonObject);
                details.setMember(member);
            }

            if (memberDetailsJsonObject.has("integrations")) {
                JSONArray integrations = memberDetailsJsonObject.getJSONArray("integrations");
                if (integrations != null && integrations.length() > 0) {
                    List<Integration> integrationList = salesForceAdapter.createIntegrationsList(integrations);
                    details.setIntegrations(integrationList);
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
                        subMember.setSlug(SlugUtils.createSlug(o.getTargetAccountId(), o.getAccountName()));
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
