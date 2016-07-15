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
package org.orcid.core.manager.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.SalesForceUnauthorizedException;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.pojo.SalesForceConsortium;
import org.orcid.pojo.SalesForceContact;
import org.orcid.pojo.SalesForceDetails;
import org.orcid.pojo.SalesForceIntegration;
import org.orcid.pojo.SalesForceMember;
import org.orcid.pojo.SalesForceOpportunity;
import org.orcid.utils.ReleaseNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.github.slugify.Slugify;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceManagerImpl implements SalesForceManager {

    private static final String SLUG_SEPARATOR = "-";

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesForceManager.class);

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

    @Resource(name = "salesForceMembersListCache")
    private SelfPopulatingCache salesForceMembersListCache;

    @Resource(name = "salesForceMemberDetailsCache")
    private SelfPopulatingCache salesForceMemberDetailsCache;

    private Client client = Client.create();

    private String accessToken;

    private String releaseName = ReleaseNameUtils.getReleaseName();

    private Slugify slugify;
    {
        try {
            slugify = new Slugify();
        } catch (IOException e) {
            throw new RuntimeException("Error initializing slugify", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SalesForceMember> retrieveMembers() {
        return (List<SalesForceMember>) salesForceMembersListCache.get(releaseName).getObjectValue();
    }

    @Override
    public List<SalesForceMember> retrieveFreshMembers() {
        try {
            return retrieveMembersFromSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve members list, trying again.", e);
            return retrieveMembersFromSalesForce(getFreshAccessToken());
        }
    }

    @Override
    public List<SalesForceMember> retrieveConsortia() {
        // XXX Implement cache
        return retrieveFreshConsortia();
    }

    @Override
    public List<SalesForceMember> retrieveFreshConsortia() {
        try {
            return retrieveConsortiaFromSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve consortia list, trying again.", e);
            return retrieveConsortiaFromSalesForce(getFreshAccessToken());
        }
    }

    @Override
    public SalesForceConsortium retrieveConsortium(String consortiumId) {
        // XXX Implement cache
        return retrieveFreshConsortium(consortiumId);
    }

    @Override
    public SalesForceConsortium retrieveFreshConsortium(String consortiumId) {
        try {
            return retrieveConsortiumFromSalesForce(getAccessToken(), consortiumId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve consortium, trying again.", e);
            return retrieveConsortiumFromSalesForce(getFreshAccessToken(), consortiumId);
        }
    }

    @Override
    public SalesForceDetails retrieveDetails(String memberId, String consortiumLeadId) {
        return (SalesForceDetails) salesForceMemberDetailsCache.get(new SalesForceMemberDetailsCacheKey(memberId, consortiumLeadId, releaseName)).getObjectValue();
    }

    @Override
    public SalesForceDetails retrieveFreshDetails(String memberId, String consortiumLeadId) {
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
    public SalesForceDetails retrieveDetailsBySlug(String memberSlug) {
        String id = memberSlug.substring(0, memberSlug.indexOf(SLUG_SEPARATOR));
        validateSalesForceId(id);
        List<SalesForceMember> members = retrieveMembers();
        Optional<SalesForceMember> match = members.stream().filter(e -> id.equals(e.getId())).findFirst();
        if (match.isPresent()) {
            SalesForceMember salesForceMember = match.get();
            return (SalesForceDetails) salesForceMemberDetailsCache.get(new SalesForceMemberDetailsCacheKey(id, salesForceMember.getConsortiumLeadId(), releaseName))
                    .getObjectValue();
        }
        throw new IllegalArgumentException("No member details found for " + memberSlug);
    }

    @Override
    public List<SalesForceContact> retrieveContactsByOpportunityId(String opportunityId) {
        // XXX Implement cache
        return retrieveFreshContactsByOpportunityId(opportunityId);
    }

    @Override
    public List<SalesForceContact> retrieveFreshContactsByOpportunityId(String opportunityId) {
        try {
            return retrieveContactsFromSalesForce(getAccessToken(), opportunityId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve contacts, trying again.", e);
            return retrieveContactsFromSalesForce(getFreshAccessToken(), opportunityId);
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

    @Override
    public void evictAll() {
        salesForceMembersListCache.removeAll();
        salesForceMemberDetailsCache.removeAll();
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<SalesForceMember> retrieveMembersFromSalesForce(String accessToken) throws SalesForceUnauthorizedException {
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
    private List<SalesForceMember> retrieveConsortiaFromSalesForce(String accessToken) throws SalesForceUnauthorizedException {
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
    private SalesForceConsortium retrieveConsortiumFromSalesForce(String accessToken, String consortiumId) throws SalesForceUnauthorizedException {
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
                "SELECT (SELECT Id, Account.Name FROM ConsortiaOpportunities__r WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_End_Date__c > TODAY) from Account WHERE Id='"
                        + validateSalesForceId(consortiumId) + "'");
        return resource;
    }

    private SalesForceConsortium createConsortiumFromResponse(ClientResponse response) {
        JSONObject results = response.getEntity(JSONObject.class);
        try {
            int numFound = extractInt(results, "totalSize");
            if (numFound == 0) {
                return null;
            }
            SalesForceConsortium consortium = new SalesForceConsortium();
            List<SalesForceOpportunity> opportunityList = new ArrayList<>();
            consortium.setOpportunities(opportunityList);
            JSONArray records = results.getJSONArray("records");
            JSONObject firstRecord = records.getJSONObject(0);
            JSONObject opportunities = extractObject(firstRecord, "ConsortiaOpportunities__r");
            JSONArray opportunityRecords = opportunities.getJSONArray("records");
            for (int i = 0; i < opportunityRecords.length(); i++) {
                SalesForceOpportunity salesForceOpportunity = new SalesForceOpportunity();
                JSONObject opportunity = opportunityRecords.getJSONObject(i);
                salesForceOpportunity.setId(extractOpportunityId(opportunity));
                salesForceOpportunity.setTargetAccountId(extractAccountId(opportunity));
                opportunityList.add(salesForceOpportunity);
            }
            return consortium;
        } catch (JSONException e) {
            throw new RuntimeException("Error getting consortium record from SalesForce JSON", e);
        }
    }

    private String extractOpportunityId(JSONObject opportunity) throws JSONException {
        JSONObject opportunityAttributes = extractObject(opportunity, "attributes");
        String opportunityUrl = extractString(opportunityAttributes, "url");
        return extractIdFromUrl(opportunityUrl);
    }

    private String extractAccountId(JSONObject opportunity) throws JSONException {
        JSONObject account = extractObject(opportunity, "Account");
        JSONObject accountAttributes = extractObject(account, "attributes");
        String accountUrl = extractString(accountAttributes, "url");
        String accountId = extractIdFromUrl(accountUrl);
        return accountId;
    }

    private List<SalesForceMember> createMembersListFromResponse(ClientResponse response) {
        List<SalesForceMember> members = new ArrayList<>();
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

    private SalesForceMember createMemberFromSalesForceRecord(JSONObject record) throws JSONException {
        String name = extractString(record, "Name");
        String id = extractString(record, "Id");
        SalesForceMember member = new SalesForceMember();
        member.setName(name);
        member.setId(id);
        member.setSlug(createSlug(id, name));
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
    private SalesForceDetails retrieveDetailsFromSalesForce(String accessToken, String memberId, String consortiumLeadId) throws SalesForceUnauthorizedException {
        SalesForceDetails details = new SalesForceDetails();
        String parentOrgName = retrieveParentOrgNameFromSalesForce(accessToken, consortiumLeadId);
        details.setParentOrgName(parentOrgName);
        details.setParentOrgSlug(createSlug(consortiumLeadId, parentOrgName));
        details.setIntegrations(retrieveIntegrationsFromSalesForce(accessToken, memberId));
        List<SalesForceMember> members = retrieveMembers();
        Optional<SalesForceMember> match = members.stream().filter(e -> memberId.equals(e.getId())).findFirst();
        if (match.isPresent()) {
            SalesForceMember salesForceMember = match.get();
            details.setMember(salesForceMember);
            details.setContacts(findContacts(salesForceMember));
        }
        details.setSubMembers(findSubMembers(memberId));
        return details;
    }

    private List<SalesForceContact> findContacts(SalesForceMember member) {
        String memberId = member.getId();
        String consortiumLeadId = member.getConsortiumLeadId();
        if (consortiumLeadId != null) {
            SalesForceConsortium consortium = retrieveConsortium(consortiumLeadId);
            Optional<SalesForceOpportunity> opp = consortium.getOpportunities().stream().filter(e -> memberId.equals(e.getTargetAccountId())).findFirst();
            if (opp.isPresent()) {
                String oppId = opp.get().getId();
                return retrieveContactsByOpportunityId(oppId);
            }
        } else {
            // It might be a consortium
            Optional<SalesForceMember> consortium = retrieveConsortia().stream().filter(e -> memberId.equals(e.getId())).findFirst();
            if (consortium.isPresent()) {
                String mainOpportunityId = consortium.get().getMainOpportunityId();
                if (mainOpportunityId != null) {
                    return retrieveContactsByOpportunityId(mainOpportunityId);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<SalesForceMember> findSubMembers(String memberId) {
        return retrieveMembers().stream().filter(e -> memberId.equals(e.getConsortiumLeadId())).collect(Collectors.toList());
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
    private List<SalesForceIntegration> retrieveIntegrationsFromSalesForce(String accessToken, String memberId) throws SalesForceUnauthorizedException {
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

    private List<SalesForceIntegration> createIntegrationsListFromResponse(ClientResponse response) {
        List<SalesForceIntegration> integrations = new ArrayList<>();
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

    private SalesForceIntegration createIntegrationFromSalesForceRecord(JSONObject integrationRecord) throws JSONException {
        SalesForceIntegration integration = new SalesForceIntegration();
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
    private List<SalesForceContact> retrieveContactsFromSalesForce(String accessToken, String opportunityId) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of contacts from SalesForce");
        validateSalesForceId(opportunityId);
        WebResource resource = createContactsResource(opportunityId);
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        checkAuthorization(response);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Error getting contacts from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body = " + response.getEntity(String.class));
        }
        return createContactsFromResponse(response);
    }

    private WebResource createContactsResource(String opportunityId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT (SELECT Contact.Name, Contact.Email, Role FROM OpportunityContactRoles WHERE Role IN ('Main Contact','Tech Lead')) FROM Opportunity WHERE Id='"
                        + validateSalesForceId(opportunityId) + "'");
        return resource;
    }

    private List<SalesForceContact> createContactsFromResponse(ClientResponse response) {
        ArrayList<SalesForceContact> contacts = new ArrayList<>();
        JSONObject results = response.getEntity(JSONObject.class);
        try {
            JSONArray records = results.getJSONArray("records");
            if (records.length() > 0) {
                JSONObject firstRecord = records.getJSONObject(0);
                JSONObject opportunityContactRoleObject = extractObject(firstRecord, "OpportunityContactRoles");
                if (opportunityContactRoleObject != null) {
                    JSONArray contactRecords = opportunityContactRoleObject.getJSONArray("records");
                    for (int i = 0; i < contactRecords.length(); i++) {
                        contacts.add(createContactFromSalesForceRecord(contactRecords.getJSONObject(i)));
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting contact records from SalesForce JSON", e);
        }
        return contacts;
    }

    private SalesForceContact createContactFromSalesForceRecord(JSONObject contactRecord) throws JSONException {
        SalesForceContact contact = new SalesForceContact();
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

    private String createSlug(String id, String name) {
        return id + SLUG_SEPARATOR + slugify.slugify(name);
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
