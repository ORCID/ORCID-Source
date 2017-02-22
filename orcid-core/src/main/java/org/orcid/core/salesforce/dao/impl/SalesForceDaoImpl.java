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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.SalesForceUnauthorizedException;
import org.orcid.core.salesforce.adapter.SalesForceAdapter;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.SlugUtils;
import org.orcid.core.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.representation.Form;

public class SalesForceDaoImpl implements SalesForceDao, InitializingBean {

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

    @Value("${org.orcid.core.salesForce.apiBaseUrl:https://cs10.salesforce.com}")
    private String apiBaseUrl;

    @Value("${org.orcid.core.salesForce.clientLoggingEnabled:false}")
    private boolean clientLoggingEnabled;

    @Resource
    private SalesForceAdapter salesForceAdapter;

    private Client client = Client.create();

    private String accessToken;

    @Override
    public List<Member> retrieveConsortia() {
        try {
            return retrieveConsortiaFromSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve consortia list, trying again.", e);
            return retrieveConsortiaFromSalesForce(getFreshAccessToken());
        }
    }

    @Override
    public List<Member> retrieveMembers() {
        try {
            return retrieveMembersFromSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve members list, trying again.", e);
            return retrieveMembersFromSalesForce(getFreshAccessToken());
        }
    }

    @Override
    public Consortium retrieveConsortium(String consortiumId) {
        try {
            return retrieveConsortiumFromSalesForce(getAccessToken(), consortiumId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve consortium, trying again.", e);
            return retrieveConsortiumFromSalesForce(getFreshAccessToken(), consortiumId);
        }
    }

    @Override
    public MemberDetails retrieveDetails(String memberId, String consortiumLeadId) {
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
    public List<Contact> retrieveAllContactsByAccountId(String accountId) {
        try {
            return retrieveAllContactsFromSalesForceByAccountId(getAccessToken(), accountId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve all contacts, trying again.", e);
            return retrieveAllContactsFromSalesForceByAccountId(getFreshAccessToken(), accountId);
        }
    }

    @Override
    public List<Contact> retrieveContactsWithRolesByAccountId(String accountId) {
        try {
            return retrieveContactsWithRolesFromSalesForceByAccountId(getAccessToken(), accountId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve contacts with roles, trying again.", e);
            return retrieveContactsWithRolesFromSalesForceByAccountId(getFreshAccessToken(), accountId);
        }
    }

    @Override
    public List<ContactRole> retrieveContactRolesByContactIdAndAccountId(String contactId, String accountId) {
        try {
            return retrieveContactRolesFromSalesForceByContactIdAndAccountId(getAccessToken(), contactId, accountId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve contacts, trying again.", e);
            return retrieveContactRolesFromSalesForceByContactIdAndAccountId(getFreshAccessToken(), contactId, accountId);
        }
    }

    @Override
    public String retrievePremiumConsortiumMemberTypeId() {
        try {
            return retrievePremiumConsortiumMemberTypeIdFromSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve premium consortium member type ID, trying again.", e);
            return retrievePremiumConsortiumMemberTypeIdFromSalesForce(getFreshAccessToken());
        }
    }

    @Override
    public String retrieveConsortiumMemberRecordTypeId() {
        try {
            return retrieveConsortiumMemberRecordTypeIdFromSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve consortium member record type ID, trying again.", e);
            return retrieveConsortiumMemberRecordTypeIdFromSalesForce(getFreshAccessToken());
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
    public String createContact(Contact contact) {
        try {
            return createContactInSalesForce(getAccessToken(), contact);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to create contact, trying again.", e);
            return createContactInSalesForce(getFreshAccessToken(), contact);
        }

    }

    private String createContactInSalesForce(String accessToken, Contact contact) {
        LOGGER.info("About to create contact in SalesForce");
        String accountId = contact.getAccountId();
        validateSalesForceId(accountId);
        WebResource resource = createContactResource();
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromContact(contact);
        ClientResponse response = doPostRequest(resource, contactJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating contact in SalesForce");
        return result.optString("id");
    }

    @Override
    public String createContactRole(ContactRole contact) {
        try {
            return createContactRoleInSalesForce(getAccessToken(), contact);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to create contact role, trying again.", e);
            return createContactRoleInSalesForce(getFreshAccessToken(), contact);
        }

    }

    private String createContactRoleInSalesForce(String accessToken, ContactRole contactRole) {
        LOGGER.info("About to create contact role in SalesForce");
        validateSalesForceId(contactRole.getAccountId());
        validateSalesForceId(contactRole.getContactId());
        WebResource resource = createContactRoleResource();
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromContactRole(contactRole);
        ClientResponse response = doPostRequest(resource, contactJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating contact role in SalesForce");
        return result.optString("id");
    }

    @Override
    public void removeContactRole(String contactRoleId) {
        try {
            removeContactRoleInSalesForce(getAccessToken(), contactRoleId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to remove contact role, trying again.", e);
            removeContactRoleInSalesForce(getFreshAccessToken(), contactRoleId);
        }
    }

    private void removeContactRoleInSalesForce(String accessToken, String contactRoleId) {
        LOGGER.info("About to remove contact role in SalesForce");
        validateSalesForceId(contactRoleId);
        WebResource resource = removeContactRoleResource(contactRoleId);
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).type(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class);
        checkAuthorization(response);
        checkResponse(response, 204, "Error removing contact role in SalesForce");
    }

    @Override
    public String createMember(Member member) {
        try {
            return createMemberInSalesForce(getAccessToken(), member);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to create member, trying again.", e);
            return createMemberInSalesForce(getFreshAccessToken(), member);
        }

    }

    private String createMemberInSalesForce(String accessToken, Member member) {
        LOGGER.info("About to create member in SalesForce");
        WebResource resource = createMemberResource();
        JSONObject memberJson = salesForceAdapter.createSaleForceRecordFromMember(member);
        ClientResponse response = doPostRequest(resource, memberJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating member in SalesForce");
        return result.optString("id");
    }

    @Override
    public void updateMember(Member member) {
        try {
            updateMemberInSalesForce(getAccessToken(), member);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to update member, trying again.", e);
            updateMemberInSalesForce(getFreshAccessToken(), member);
        }

    }

    private void updateMemberInSalesForce(String accessToken, Member member) {
        LOGGER.info("About update member in SalesForce");
        String accountId = member.getId();
        validateSalesForceId(accountId);
        WebResource resource = createUpdateMemberResource(accountId);
        JSONObject memberJson = salesForceAdapter.createSaleForceRecordFromMember(member);
        // SalesForce doesn't allow the Id in the body
        memberJson.remove("Id");
        ClientResponse response = doPostRequest(resource, memberJson, accessToken);
        checkAuthorization(response);
        checkResponse(response, 204, "Error updating member in SalesForce");
        return;
    }

    @Override
    public String createOpportunity(Opportunity opportunity) {
        try {
            return createOpportunityInSalesForce(getAccessToken(), opportunity);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to create opportunity, trying again.", e);
            return createOpportunityInSalesForce(getFreshAccessToken(), opportunity);
        }

    }

    private String createOpportunityInSalesForce(String accessToken, Opportunity member) {
        LOGGER.info("About to create opportunity in SalesForce");
        WebResource resource = createOpportunityResource();
        JSONObject memberJson = salesForceAdapter.createSaleForceRecordFromOpportunity(member);
        ClientResponse response = doPostRequest(resource, memberJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating opportunity in SalesForce");
        return result.optString("id");
    }

    private WebResource createMemberResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/sobjects/Account/");
        return resource;
    }

    private WebResource createOpportunityResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/sobjects/Opportunity/");
        return resource;
    }

    private WebResource createUpdateMemberResource(String accountId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/sobjects/Account/" + validateSalesForceId(accountId)).queryParam("_HttpMethod",
                "PATCH");
        return resource;
    }

    private WebResource createContactResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/sobjects/Contact/");
        return resource;
    }

    private WebResource createContactRoleResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/sobjects/Membership_Contact_Role__c/");
        return resource;
    }

    private WebResource removeContactRoleResource(String contactRoleId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/sobjects/Membership_Contact_Role__c/" + validateSalesForceId(contactRoleId));
        return resource;
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
        List<Member> membersList = new ArrayList<>();
        JSONObject jsonObject = retrieveMembersObject(accessToken);
        String nextRecordsUrl = null;
        do {
            membersList.addAll(salesForceAdapter.createMembersListFromJson(jsonObject));
            nextRecordsUrl = JsonUtils.extractString(jsonObject, "nextRecordsUrl");
            if (nextRecordsUrl != null) {
                jsonObject = retrieveMembersNextObject(accessToken, nextRecordsUrl);
            }
        } while (nextRecordsUrl != null);
        return membersList;
    }

    private JSONObject retrieveMembersObject(String accessToken) {
        WebResource resource = createMemberListResource();
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        return checkResponse(response, 200, "Error getting member list from SalesForce");
    }

    private JSONObject retrieveMembersNextObject(String accessToken, String nextRecordsUrl) {
        WebResource nextResource = creatNextRecordsResource(nextRecordsUrl);
        ClientResponse nextResponse = doGetRequest(nextResource, accessToken);
        return checkResponse(nextResponse, 200, "Error getting next results for member list from SalesForce");
    }

    private WebResource createMemberListResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Account.Id, Account.Name, Account.Public_Display_Name__c, Account.Website, Account.BillingCountry, Account.Research_Community__c, (SELECT Consortia_Lead__c from Opportunities WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_End_Date__c>TODAY ORDER BY Membership_Start_Date__c DESC), Account.Public_Display_Description__c, Account.Logo_Description__c, Account.Public_Display_Email__c from Account WHERE Active_Member__c=TRUE");
        return resource;
    }

    private WebResource creatNextRecordsResource(String nextRecordsUrl) {
        WebResource resource = client.resource(apiBaseUrl).path(nextRecordsUrl);
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
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting consortia list from SalesForce");
        return salesForceAdapter.createMembersListFromJson(result);
    }

    private WebResource createConsortiaListResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Id, Name, Public_Display_Name__c, Website, Research_Community__c, BillingCountry, Public_Display_Description__c, Logo_Description__c, (SELECT Opportunity.Id FROM Opportunities WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_End_Date__c>TODAY ORDER BY Membership_Start_Date__c DESC) from Account WHERE Id IN (SELECT Consortia_Lead__c FROM Opportunity) AND Active_Member__c=TRUE");
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
        LOGGER.info("About get consortium from SalesForce");
        validateSalesForceId(consortiumId);
        WebResource resource = createConsortiumResource(consortiumId);
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting consortium from SalesForce");
        return salesForceAdapter.createConsortiumFromJson(result);
    }

    private WebResource createConsortiumResource(String consortiumId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT (SELECT Id, Account.Name, Account.Public_Display_Name__c FROM ConsortiaOpportunities__r WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_End_Date__c>TODAY ORDER BY Membership_Start_Date__c DESC) from Account WHERE Id='"
                        + validateSalesForceId(consortiumId) + "'");
        return resource;
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
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting parent org name from SalesForce");
        return salesForceAdapter.extractParentOrgNameFromJson(result);
    }

    private WebResource createParentOrgResource(String consortiumLeadId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Public_Display_Name__c from Account WHERE Id='" + validateSalesForceId(consortiumLeadId) + "'");
        return resource;
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
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting integrations list from SalesForce");
        return salesForceAdapter.createIntegrationsListFromJson(result);
    }

    private WebResource createIntegrationListResource(String memberId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT (SELECT Integration__c.Name, Integration__c.Description__c, Integration__c.Integration_Stage__c, Integration__c.Integration_URL__c from Account.Integrations__r) from Account WHERE Id='"
                        + validateSalesForceId(memberId) + "'");
        return resource;
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<Contact> retrieveAllContactsFromSalesForceByAccountId(String accessToken, String accountId) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of all contacts from SalesForce");
        validateSalesForceId(accountId);
        WebResource resource = createAllContactsResource(accountId);
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting all contacts from SalesForce");
        return salesForceAdapter.createContactsFromJson(result);
    }

    private WebResource createAllContactsResource(String accountId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "Select Id, AccountId, Name, Email, ORCID_iD_Path__c From Contact Where AccountId='" + validateSalesForceId(accountId) + "'");
        return resource;
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<Contact> retrieveContactsWithRolesFromSalesForceByAccountId(String accessToken, String accountId) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of contacts from SalesForce");
        validateSalesForceId(accountId);
        WebResource resource = createContactsWithRolesResource(accountId);
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting contacts from SalesForce");
        return salesForceAdapter.createContactsWithRolesFromJson(result);
    }

    private WebResource createContactsWithRolesResource(String accountId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "Select (Select Id, Contact__c, Contact__r.FirstName, Contact__r.LastName, Contact__r.Email, Member_Org_Role__c From Membership_Contact_Roles__r) From Account a Where Id='"
                        + validateSalesForceId(accountId) + "'");
        return resource;
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<ContactRole> retrieveContactRolesFromSalesForceByContactIdAndAccountId(String accessToken, String contactId, String accountId)
            throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of contact roles from SalesForce");
        validateSalesForceId(contactId);
        validateSalesForceId(accountId);
        WebResource resource = createContactRolesResource(contactId, accountId);
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting contacts from SalesForce");
        return salesForceAdapter.createContactRolesFromJson(result);
    }

    private WebResource createContactRolesResource(String contactId, String accountId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "Select Id, Contact__c, Member_Org_Role__c From Membership_Contact_Role__c Where Contact__c = '" + validateSalesForceId(contactId)
                        + "' And Organization__c='" + validateSalesForceId(accountId) + "'");
        return resource;
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private String retrievePremiumConsortiumMemberTypeIdFromSalesForce(String accessToken) throws SalesForceUnauthorizedException {
        LOGGER.info("About get premium consortium member type ID from SalesForce");
        WebResource resource = createPremiumConsortiumMemberTypeIdResource();
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting premium consortium member type ID from SalesForce");
        return salesForceAdapter.extractIdFromFirstRecord(result);
    }

    private WebResource createPremiumConsortiumMemberTypeIdResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "Select Id From Member_Type__c Where Name =  'Premium Consortium Member'");
        return resource;
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private String retrieveConsortiumMemberRecordTypeIdFromSalesForce(String accessToken) throws SalesForceUnauthorizedException {
        LOGGER.info("About get consortium member record type ID from SalesForce");
        WebResource resource = createConsortiumMemberRecordTypeIdResource();
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting consortium member record type ID from SalesForce");
        return salesForceAdapter.extractIdFromFirstRecord(result);
    }

    private WebResource createConsortiumMemberRecordTypeIdResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "Select Id, Name, SobjectType From RecordType  Where SobjectType = 'Opportunity' And Name = 'Consortium Member'");
        return resource;
    }

    @Override
    public String getAccessToken() {
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

    private ClientResponse doGetRequest(WebResource resource, String accessToken) {
        return resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
    }
    
    private ClientResponse doPostRequest(WebResource resource, JSONObject bodyJson, String accessToken) {
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, bodyJson);
        return response;
    }

    private void checkAuthorization(ClientResponse response) {
        if (response.getStatus() == 401) {
            throw new SalesForceUnauthorizedException("Unauthorized reponse from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body= " + response.getEntity(String.class));
        }
    }
    
    private JSONObject checkResponse(ClientResponse nextResponse, int requiredStatus, String errorMessage) {
        if (nextResponse.getStatus() != 200) {
            throw new RuntimeException(errorMessage + ", status code =  " + nextResponse.getStatus() + ", reason = " + nextResponse.getStatusInfo().getReasonPhrase()
                    + ", body = " + nextResponse.getEntity(String.class));
        }
        return nextResponse.getEntity(JSONObject.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (clientLoggingEnabled) {
            client.addFilter(new LoggingFilter());
        }
    }

}
