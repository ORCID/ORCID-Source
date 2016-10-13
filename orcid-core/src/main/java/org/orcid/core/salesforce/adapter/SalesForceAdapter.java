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

import static org.orcid.core.utils.JsonUtils.extractObject;
import static org.orcid.core.utils.JsonUtils.extractString;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.SlugUtils;
import org.orcid.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesForceAdapter.class);

    public Consortium createConsortiumFromJson(JSONObject results) {
        try {
            int numFound = JsonUtils.extractInt(results, "totalSize");
            if (numFound == 0) {
                return null;
            }
            Consortium consortium = new Consortium();
            List<Opportunity> opportunityList = new ArrayList<>();
            consortium.setOpportunities(opportunityList);
            JSONArray records = results.getJSONArray("records");
            JSONObject firstRecord = records.getJSONObject(0);
            JSONObject opportunities = extractObject(firstRecord, "ConsortiaOpportunities__r");
            if (opportunities != null) {
                JSONArray opportunityRecords = opportunities.getJSONArray("records");
                for (int i = 0; i < opportunityRecords.length(); i++) {
                    Opportunity salesForceOpportunity = new Opportunity();
                    JSONObject opportunity = opportunityRecords.getJSONObject(i);
                    salesForceOpportunity.setId(extractOpportunityId(opportunity));
                    JSONObject account = extractObject(opportunity, "Account");
                    salesForceOpportunity.setTargetAccountId(extractAccountId(account));
                    salesForceOpportunity.setAccountName(JsonUtils.extractString(account, "Name"));
                    opportunityList.add(salesForceOpportunity);
                }
                return consortium;
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting consortium record from SalesForce JSON", e);
        }
        return null;
    }

    public Map<String, List<Contact>> createContactsFromJson(JSONObject results) {
        Map<String, List<Contact>> map = new HashMap<>();
        try {
            JSONArray records = results.getJSONArray("records");
            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                String oppId = extractString(record, "Id");
                List<Contact> contacts = new ArrayList<>();
                JSONObject opportunityContactRoleObject = extractObject(record, "OpportunityContactRoles");
                if (opportunityContactRoleObject != null) {
                    JSONArray contactRecords = opportunityContactRoleObject.getJSONArray("records");
                    for (int j = 0; j < contactRecords.length(); j++) {
                        contacts.add(createContactFromSalesForceRecord(contactRecords.getJSONObject(j)));
                    }
                }
                map.put(oppId, contacts);
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting contact records from SalesForce JSON", e);
        }
        return map;
    }

    public List<Member> createMembersListFromJson(JSONObject results) {
        List<Member> members = new ArrayList<>();
        try {
            JSONArray records = results.getJSONArray("records");
            for (int i = 0; i < records.length(); i++) {
                members.add(createMemberFromSalesForceRecord(records.getJSONObject(i)));
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting member records from SalesForce JSON", e);
        }
        return members;
    }

    public List<Integration> createIntegrationsListFromJson(JSONObject results) {
        List<Integration> integrations = new ArrayList<>();
        try {
            JSONArray records = results.getJSONArray("records");
            if (records.length() > 0) {
                JSONObject firstRecord = records.getJSONObject(0);
                JSONObject integrationsObject = extractObject(firstRecord, "Integrations__r");
                if (integrationsObject != null) {
                    JSONArray integrationRecords = integrationsObject.getJSONArray("records");
                    for (int i = 0; i < integrationRecords.length(); i++) {
                        integrations.add(createIntegrationFromSalesForceRecord(integrationRecords.getJSONObject(i)));
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting integrations records from SalesForce JSON", e);
        }
        return integrations;
    }

    public String extractParentOrgNameFromJson(JSONObject results) {
        try {
            JSONArray records = results.getJSONArray("records");
            if (records.length() > 0) {
                return extractString(records.getJSONObject(0), "Name");
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting parent org from SalesForce JSON", e);
        }
        return null;
    }

    private String extractOpportunityId(JSONObject opportunity) throws JSONException {
        JSONObject opportunityAttributes = extractObject(opportunity, "attributes");
        String opportunityUrl = extractString(opportunityAttributes, "url");
        return extractIdFromUrl(opportunityUrl);
    }

    private String extractAccountId(JSONObject account) throws JSONException {
        JSONObject accountAttributes = extractObject(account, "attributes");
        String accountUrl = extractString(accountAttributes, "url");
        String accountId = extractIdFromUrl(accountUrl);
        return accountId;
    }

    private Member createMemberFromSalesForceRecord(JSONObject record) throws JSONException {
        String name = extractString(record, "Name");
        String id = extractString(record, "Id");
        Member member = new Member();
        member.setName(name);
        member.setId(id);
        member.setSlug(SlugUtils.createSlug(id, name));
        try {
            member.setWebsiteUrl(extractURL(record, "Website"));
        } catch (MalformedURLException e) {
            LOGGER.info("Malformed website URL for member: {}", name, e);
        }
        member.setResearchCommunity(extractString(record, "Research_Community__c"));
        member.setCountry(extractString(record, "BillingCountry"));
        member.setPublicDisplayEmail(extractString(record, "Public_Display_Email__c"));
        JSONObject opportunitiesObject = extractObject(record, "Opportunities");
        if (opportunitiesObject != null) {
            JSONArray opportunitiesArray = opportunitiesObject.getJSONArray("records");
            if (opportunitiesArray.length() > 0) {
                JSONObject mainOpportunity = opportunitiesArray.getJSONObject(0);
                JSONObject mainOppAttributes = extractObject(mainOpportunity, "attributes");
                member.setMainOpportunityId(extractIdFromUrl(extractString(mainOppAttributes, "url")));
                member.setConsortiumLeadId(extractString(mainOpportunity, "Consortia_Lead__c"));
            }
        }
        member.setDescription(extractString(record, "Public_Display_Description__c"));
        try {
            member.setLogoUrl(extractURL(record, "Logo_Description__c"));
        } catch (MalformedURLException e) {
            LOGGER.info("Malformed logo URL for member: {}", name, e);
        }
        return member;
    }

    private Integration createIntegrationFromSalesForceRecord(JSONObject integrationRecord) throws JSONException {
        Integration integration = new Integration();
        String name = extractString(integrationRecord, "Name");
        integration.setName(name);
        integration.setDescription(extractString(integrationRecord, "Description__c"));
        integration.setStage(extractString(integrationRecord, "Integration_Stage__c"));
        try {
            integration.setResourceUrl(extractURL(integrationRecord, "Integration_URL__c"));
        } catch (MalformedURLException e) {
            LOGGER.info("Malformed resource URL for member: {}", name, e);
        }
        return integration;
    }

    private Contact createContactFromSalesForceRecord(JSONObject contactRecord) throws JSONException {
        Contact contact = new Contact();
        contact.setRole(extractString(contactRecord, "Role"));
        JSONObject contactDetails = extractObject(contactRecord, "Contact");
        contact.setName(extractString(contactDetails, "Name"));
        contact.setEmail(extractString(contactDetails, "Email"));
        return contact;
    }

    private URL extractURL(JSONObject record, String key) throws JSONException, MalformedURLException {
        String urlString = tidyUrl(extractString(record, key));
        return urlString != null ? new URL(urlString) : null;
    }

    private String tidyUrl(String urlString) {
        if (StringUtils.isBlank(urlString)) {
            return null;
        }
        // We were getting a
        // http://www.fileformat.info/info/unicode/char/feff/index.htm at the
        // beginning of one logo URL from SF.
        urlString = urlString.replaceAll("\\p{C}", "");
        urlString = urlString.trim();
        if (!urlString.matches("^.*?://.*")) {
            urlString = "http://" + urlString;
        }
        return urlString;
    }

    private String extractIdFromUrl(String url) {
        if (url == null) {
            return null;
        }
        int slashIndex = url.lastIndexOf('/');
        if (slashIndex == -1) {
            throw new IllegalArgumentException("Unable to extract ID, url = " + url);
        }
        return url.substring(slashIndex + 1);
    }

}
