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
package org.orcid.core.salesforce.dao.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.SalesForceUnauthorizedException;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.SlugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

public class SalesForceDaoImpl implements SalesForceDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesForceDaoImpl.class);
    
    @Value("${org.orcid.core.salesForce.clientId}")
    private String clientId;

    @Value("${org.orcid.core.salesForce.clientSecret}")
    private String clientSecret;

    @Value("${org.orcid.core.salesForce.username}")
    private String username;

    @Value("${org.orcid.core.salesForce.password}")
    private String password;

    @Value("${org.orcid.core.salesForce.tokenEndPointUrl:https://test.salesforce.com/services/oauth2/token}")
    private String tokenEndPointUrl;

    @Value("${org.orcid.core.salesForce.apiBaseUrl:https://cs43.salesforce.com}")
    private String apiBaseUrl;
    
    private Client client = Client.create();

    private String accessToken;
    
    @Override
    public List<Member> retrieveFreshConsortia() {
        try {
            return retrieveConsortiaFromSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve consortia list, trying again.", e);
            return retrieveConsortiaFromSalesForce(getFreshAccessToken());
        }
    }
    
    @Override
    public List<Member> retrieveFreshMembers() {
        try {
            return retrieveMembersFromSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve members list, trying again.", e);
            return retrieveMembersFromSalesForce(getFreshAccessToken());
        }
    }
    
    @Override
    public Consortium retrieveFreshConsortium(String consortiumId) {
        try {
            return retrieveConsortiumFromSalesForce(getAccessToken(), consortiumId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve consortium, trying again.", e);
            return retrieveConsortiumFromSalesForce(getFreshAccessToken(), consortiumId);
        }
    }
    
    @Override
    public MemberDetails retrieveFreshDetails(String memberId, String consortiumLeadId) {
        validateSalesForceId(memberId);
        if (consortiumLeadId != null) {
            validateSalesForceId(consortiumLeadId);
        }
        try {
            return retrieveDetailsFromSalesForce(getAccessToken(), memberId, consortiumLeadId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve details, trying again.", e);
            return retrieveDetailsFromSalesForce(getFreshAccessToken(), memberId, consortiumLeadId);
        }
    }
    
    @Override
    public Map<String, List<Contact>> retrieveFreshContactsByOpportunityId(Collection<String> opportunityIds) {
        try {
            return retrieveContactsFromSalesForce(getAccessToken(), opportunityIds);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve contacts, trying again.", e);
            return retrieveContactsFromSalesForce(getFreshAccessToken(), opportunityIds);
        }
    }
    
    @Override
    public String validateSalesForceId(String salesForceId) {
        if (!salesForceId.matches("[a-zA-Z0-9]+")) {
            // Could be malicious, so give no further info.
            throw new IllegalArgumentException();
        }
        return salesForceId;
    }

    private String validateSalesForceIdsAndConcatenate(Collection<String> salesForceIds) {
        salesForceIds.stream().forEach(e -> validateSalesForceId(e));
        return "'" + String.join("','", salesForceIds) + "'";
    }

    
    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<Member> retrieveMembersFromSalesForce(String accessToken) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of members from SalesForce");
        WebResource resource = createMemberListResource();
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        checkAuthorization(response);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error getting member list from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body = " + response.getEntity(String.class));
        }
        return createMembersListFromResponse(response);
    }

    private WebResource createMemberListResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Account.Id, Account.Name, Account.Website, Account.BillingCountry, Account.Research_Community__c, (SELECT Consortia_Lead__c from Opportunities WHERE Membership_Start_Date__c=THIS_YEAR AND Membership_End_Date__c>TODAY ORDER BY Membership_Start_Date__c DESC), Account.Public_Display_Description__c, Account.Logo_Description__c from Account WHERE Active_Member__c=TRUE");
        return resource;
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<Member> retrieveConsortiaFromSalesForce(String accessToken) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of consortia from SalesForce");
        WebResource resource = createConsortiaListResource();
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        checkAuthorization(response);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error getting consortia list from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body = " + response.getEntity(String.class));
        }
        return createMembersListFromResponse(response);
    }

    private WebResource createConsortiaListResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Id, Name, Website, Research_Community__c, BillingCountry, Public_Display_Description__c, Logo_Description__c, (SELECT Opportunity.Id FROM Opportunities WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_Start_Date__c=THIS_YEAR AND Membership_End_Date__c>TODAY ORDER BY Membership_Start_Date__c DESC) from Account WHERE Id IN (SELECT Consortia_Lead__c FROM Opportunity) AND Active_Member__c=TRUE");
        return resource;
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private Consortium retrieveConsortiumFromSalesForce(String accessToken, String consortiumId) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of consortium from SalesForce");
        validateSalesForceId(consortiumId);
        WebResource resource = createConsortiumResource(consortiumId);
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        checkAuthorization(response);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error getting consortium from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body = " + response.getEntity(String.class));
        }
        return createConsortiumFromResponse(response);
    }

    private WebResource createConsortiumResource(String consortiumId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT (SELECT Id, Account.Name FROM ConsortiaOpportunities__r WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_Start_Date__c=THIS_YEAR AND Membership_End_Date__c>TODAY ORDER BY Membership_Start_Date__c DESC) from Account WHERE Id='"
                        + validateSalesForceId(consortiumId) + "'");
        return resource;
    }

    private Consortium createConsortiumFromResponse(ClientResponse response) {
        JSONObject results = response.getEntity(JSONObject.class);
        try {
            int numFound = extractInt(results, "totalSize");
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
                    salesForceOpportunity.setAccountName(extractString(account, "Name"));
                    opportunityList.add(salesForceOpportunity);
                }
                return consortium;
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting consortium record from SalesForce JSON", e);
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

    private List<Member> createMembersListFromResponse(ClientResponse response) {
        List<Member> members = new ArrayList<>();
        JSONObject results = response.getEntity(JSONObject.class);
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

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private MemberDetails retrieveDetailsFromSalesForce(String accessToken, String memberId, String consortiumLeadId) throws SalesForceUnauthorizedException {
        MemberDetails details = new MemberDetails();
        String parentOrgName = retrieveParentOrgNameFromSalesForce(accessToken, consortiumLeadId);
        details.setParentOrgName(parentOrgName);
        details.setParentOrgSlug(SlugUtils.createSlug(consortiumLeadId, parentOrgName));
        details.setIntegrations(retrieveIntegrationsFromSalesForce(accessToken, memberId));
        return details;
    }
    
    private String retrieveParentOrgNameFromSalesForce(String accessToken, String consortiumLeadId) {
        if (consortiumLeadId == null) {
            return null;
        }
        WebResource resource = createParentOrgResource(consortiumLeadId);
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        checkAuthorization(response);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error getting integrations list from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body = " + response.getEntity(String.class));
        }
        return extractParentOrgNameFromResponse(response);
    }

    private WebResource createParentOrgResource(String consortiumLeadId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Name from Account WHERE Id='" + validateSalesForceId(consortiumLeadId) + "'");
        return resource;
    }

    private String extractParentOrgNameFromResponse(ClientResponse response) {
        JSONObject results = response.getEntity(JSONObject.class);
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

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<Integration> retrieveIntegrationsFromSalesForce(String accessToken, String memberId) throws SalesForceUnauthorizedException {
        WebResource resource = createIntegrationListResource(memberId);
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        checkAuthorization(response);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error getting integrations list from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body = " + response.getEntity(String.class));
        }
        return createIntegrationsListFromResponse(response);
    }

    private WebResource createIntegrationListResource(String memberId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT (SELECT Integration__c.Name, Integration__c.Description__c, Integration__c.Integration_Stage__c, Integration__c.Integration_URL__c from Account.Integrations__r) from Account WHERE Id='"
                        + validateSalesForceId(memberId) + "'");
        return resource;
    }

    private List<Integration> createIntegrationsListFromResponse(ClientResponse response) {
        List<Integration> integrations = new ArrayList<>();
        JSONObject results = response.getEntity(JSONObject.class);
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

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private Map<String, List<Contact>> retrieveContactsFromSalesForce(String accessToken, Collection<String> opportunityIds)
            throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of contacts from SalesForce");
        validateSalesForceIdsAndConcatenate(opportunityIds);
        WebResource resource = createContactsResource(opportunityIds);
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        checkAuthorization(response);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error getting contacts from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body = " + response.getEntity(String.class));
        }
        return createContactsFromResponse(response);
    }

    private WebResource createContactsResource(Collection<String> opportunityIds) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Id, (SELECT Contact.Name, Contact.Email, Role FROM OpportunityContactRoles WHERE Role IN ('" + MAIN_CONTACT_ROLE + "','" + TECH_LEAD_ROLE
                        + "')) FROM Opportunity WHERE Id IN (" + validateSalesForceIdsAndConcatenate(opportunityIds) + ")");
        return resource;
    }

    private Map<String, List<Contact>> createContactsFromResponse(ClientResponse response) {
        Map<String, List<Contact>> map = new HashMap<>();
        JSONObject results = response.getEntity(JSONObject.class);
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

    private Contact createContactFromSalesForceRecord(JSONObject contactRecord) throws JSONException {
        Contact contact = new Contact();
        contact.setRole(extractString(contactRecord, "Role"));
        JSONObject contactDetails = extractObject(contactRecord, "Contact");
        contact.setName(extractString(contactDetails, "Name"));
        contact.setEmail(extractString(contactDetails, "Email"));
        return contact;
    }

    private JSONObject extractObject(JSONObject parent, String key) throws JSONException {
        if (parent.isNull(key)) {
            return null;
        }
        return parent.getJSONObject(key);
    }

    private String extractString(JSONObject record, String key) throws JSONException {
        if (record.isNull(key)) {
            return null;
        }
        return record.getString(key);
    }

    private int extractInt(JSONObject record, String key) throws JSONException {
        if (record.isNull(key)) {
            return -1;
        }
        return record.getInt(key);
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

    private String getAccessToken() {
        if (accessToken == null) {
            accessToken = getFreshAccessToken();
        }
        return accessToken;
    }

    private String getFreshAccessToken() {
        LOGGER.info("About get SalesForce access token");
        WebResource resource = client.resource(tokenEndPointUrl);
        Form form = new Form();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", username);
        form.add("password", password);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, form);
        if (response.getStatus() == 200) {
            try {
                return response.getEntity(JSONObject.class).getString("access_token");
            } catch (ClientHandlerException | UniformInterfaceException | JSONException e) {
                throw new RuntimeException("Unable to extract access token from response", e);
            }
        } else {
            throw new RuntimeException("Error getting access token from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body = " + response.getEntity(String.class));
        }
    }

    private void checkAuthorization(ClientResponse response) {
        if (response.getStatus() == 401) {
            throw new SalesForceUnauthorizedException("Unauthorized reponse from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body= " + response.getEntity(String.class));
        }
    }

}
