package org.orcid.core.salesforce.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.SalesForceUnauthorizedException;
import org.orcid.core.salesforce.adapter.SalesForceAdapter;
import org.orcid.core.salesforce.dao.SalesForceDao;
import org.orcid.core.salesforce.model.Badge;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.MemberDetails;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.OpportunityContactRole;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.core.salesforce.model.SlugUtils;
import org.orcid.core.togglz.Features;
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

    private Client client;

    private String accessToken;

    @Override
    public List<Member> retrieveConsortia() {
        return retry(accessToken -> retrieveConsortiaFromSalesForce(accessToken));
    }

    @Override
    public List<Member> retrieveMembers() {
        return retry(accessToken -> retrieveMembersFromSalesForce(accessToken));
    }

    @Override
    public Member retrieveMember(String accountId) {
        return retry(accessToken -> retrieveMemberFromSalesForce(accessToken, accountId));
    }

    @Override
    public List<Member> retrieveMembersByWebsite(String websiteUrl) {
        return retry(accessToken -> retrieveMembersByWebsiteFromSalesForce(accessToken, websiteUrl));
    }

    @Override
    public Consortium retrieveConsortium(String consortiumId) {
        return retry(accessToken -> retrieveConsortiumFromSalesForce(accessToken, consortiumId));
    }

    @Override
    public MemberDetails retrieveDetails(String memberId, String consortiumLeadId) {
        validateSalesForceId(memberId);
        if (consortiumLeadId != null) {
            validateSalesForceId(consortiumLeadId);
        }
        return retry(accessToken -> retrieveDetailsFromSalesForce(accessToken, memberId, consortiumLeadId));
    }

    @Override
    public List<Contact> retrieveAllContactsByAccountId(String accountId) {
        return retry(accessToken -> retrieveAllContactsFromSalesForceByAccountId(accessToken, accountId));
    }

    @Override
    public List<Contact> retrieveContactsWithRolesByAccountId(String accountId) {
        return retrieveContactsWithRolesByAccountId(accountId, false);
    }

    @Override
    public List<Contact> retrieveContactsWithRolesByAccountId(String accountId, boolean includeNonCurrent) {
        return retry(accessToken -> retrieveContactsWithRolesFromSalesForceByAccountId(accessToken, accountId, includeNonCurrent));
    }

    @Override
    public List<ContactRole> retrieveContactRolesByContactIdAndAccountId(String contactId, String accountId) {
        return retry(accessToken -> retrieveContactRolesFromSalesForceByContactIdAndAccountId(accessToken, contactId, accountId));
    }

    @Override
    public String retrievePremiumConsortiumMemberTypeId() {
        return retry(accessToken -> retrievePremiumConsortiumMemberTypeIdFromSalesForce(accessToken));
    }

    @Override
    public String retrieveConsortiumMemberRecordTypeId() {
        return retry(accessToken -> retrieveConsortiumMemberRecordTypeIdFromSalesForce(accessToken));
    }
    
    @Override
    public List<OrgId> retrieveOrgIdsByAccountId(String accountId) {
        return retry(accessToken -> retrieveOrgIdsFromSalesForceByAccountId(accessToken, accountId));
    }
    
    @Override
    public String createOrgId(OrgId orgId) {
        return retry(accessToken -> createOrgIdInSalesForce(accessToken, orgId));
    }
    
    @Override
    public void removeOrgId(String salesForceObjectId) {
        retryConsumer(accessToken -> removeOrgIdInSalesForce(accessToken, salesForceObjectId));
    }


    @Override
    public String createContact(Contact contact) {
        return retry(accessToken -> createContactInSalesForce(accessToken, contact));
    }

    @Override
    public void updateContact(Contact contact) {
        retryConsumer(accessToken -> updateContactInSalesForce(accessToken, contact));
    }

    @Override
    public String createContactRole(ContactRole contact) {
        return retry(accessToken -> createContactRoleInSalesForce(accessToken, contact));
    }

    @Override
    public void updateContactRole(ContactRole contactRole) {
        retryConsumer(accessToken -> updateContactRoleInSalesForce(accessToken, contactRole));
    }

    @Override
    public void removeContactRole(String contactRoleId) {
        retryConsumer(accessToken -> removeContactRoleInSalesForce(accessToken, contactRoleId));
    }

    @Override
    public String createOpportunityContactRole(OpportunityContactRole contactRole) {
        return retry(accessToken -> createOpportunityContactRoleInSalesForce(accessToken, contactRole));
    }

    @Override
    public String createMember(Member member) {
        return retry(accessToken -> createMemberInSalesForce(accessToken, member));
    }

    @Override
    public void updateMember(Member member) {
        retryConsumer(accessToken -> updateMemberInSalesForce(accessToken, member));
    }

    @Override
    public Opportunity retrieveOpportunity(String opportunityId) {
        return retry(accessToken -> retrieveOpportunityFromSalesForce(accessToken, opportunityId));
    }
    
    @Override
    public String createOpportunity(Opportunity opportunity) {
        return retry(accessToken -> createOpportunityInSalesForce(accessToken, opportunity));
    }

    @Override
    public void updateOpportunity(Opportunity opportunity) {
        retryConsumer(accessToken -> updateOpportunityInSalesForce(accessToken, opportunity));
    }

    @Override
    public void removeOpportunity(String opportunityId) {
        retryConsumer(accessToken -> removeOpportunityInSalesForce(accessToken, opportunityId));
    }
    
    @Override
    public List<Badge> retrieveBadges() {
        return retry(accessToken -> retrieveBadgesFromSalesForce(accessToken));
    }

    @Override
    public String getAccessToken() {
        if (accessToken == null) {
            accessToken = getFreshAccessToken();
        }
        return accessToken;
    }

    @Override
    public String validateSalesForceId(String salesForceId) {
        if (!salesForceId.matches("[a-zA-Z0-9]+")) {
            // Could be malicious, so give no further info.
            throw new IllegalArgumentException();
        }
        return salesForceId;
    }

    private String escapeStringInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("'", "\\'");
    }

    private String createOrgIdInSalesForce(String accessToken, OrgId orgId) {
        LOGGER.info("About to create org id in SalesForce");
        WebResource resource = createObjectsResource("/Organization_Identifier__c/");
        JSONObject opportunityJson = salesForceAdapter.createSaleForceRecordFromOrgId(orgId);
        ClientResponse response = doPostRequest(resource, opportunityJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating org id in SalesForce");
        return result.optString("id");
    }
    
    private void removeOrgIdInSalesForce(String accessToken, String salesForceObjectId) {
        LOGGER.info("About to remove org id in SalesForce");
        validateSalesForceId(salesForceObjectId);
        WebResource resource = createObjectsResource("/Organization_Identifier__c/", salesForceObjectId);
        ClientResponse response = doDeleteRequest(resource, accessToken);
        checkAuthorization(response);
        checkResponse(response, 204, "Error removing org id in SalesForce");
    }

    private String createContactInSalesForce(String accessToken, Contact contact) {
        LOGGER.info("About to create contact in SalesForce");
        String accountId = contact.getAccountId();
        validateSalesForceId(accountId);
        WebResource resource = createObjectsResource("/Contact/");
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromContact(contact);
        ClientResponse response = doPostRequest(resource, contactJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating contact in SalesForce");
        return result.optString("id");
    }

    private void updateContactInSalesForce(String accessToken, Contact contact) {
        LOGGER.info("About update contact in SalesForce");
        String contactId = contact.getId();
        validateSalesForceId(contactId);
        WebResource resource = createObjectsResource("/Contact/", contactId).queryParam("_HttpMethod", "PATCH");
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromContact(contact);
        // SalesForce doesn't allow the Id in the body
        contactJson.remove("Id");
        ClientResponse response = doPostRequest(resource, contactJson, accessToken);
        checkAuthorization(response);
        checkResponse(response, 204, "Error updating contact in SalesForce");
        return;
    }

    private String createContactRoleInSalesForce(String accessToken, ContactRole contactRole) {
        LOGGER.info("About to create contact role in SalesForce");
        validateSalesForceId(contactRole.getAccountId());
        validateSalesForceId(contactRole.getContactId());
        WebResource resource = createObjectsResource("/Membership_Contact_Role__c/");
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromContactRole(contactRole);
        ClientResponse response = doPostRequest(resource, contactJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating contact role in SalesForce");
        return result.optString("id");
    }

    private String createOpportunityContactRoleInSalesForce(String accessToken, OpportunityContactRole contactRole) {
        LOGGER.info("About to create opportunity contact role in SalesForce");
        validateSalesForceId(contactRole.getOpportunityId());
        validateSalesForceId(contactRole.getContactId());
        WebResource resource = createObjectsResource("/OpportunityContactRole/");
        JSONObject contactJson = salesForceAdapter.createSaleForceRecordFromOpportunityContactRole(contactRole);
        ClientResponse response = doPostRequest(resource, contactJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating opportunity contact role in SalesForce");
        return result.optString("id");
    }

    private void updateContactRoleInSalesForce(String accessToken, ContactRole contactRole) {
        LOGGER.info("About update contact role in SalesForce");
        String contactRoleId = contactRole.getId();
        validateSalesForceId(contactRoleId);
        WebResource resource = createObjectsResource("/Membership_Contact_Role__C/", contactRoleId).queryParam("_HttpMethod", "PATCH");
        JSONObject contactRoleJson = salesForceAdapter.createSaleForceRecordFromContactRole(contactRole);
        // SalesForce doesn't allow the Id in the body
        contactRoleJson.remove("Id");
        ClientResponse response = doPostRequest(resource, contactRoleJson, accessToken);
        checkAuthorization(response);
        checkResponse(response, 204, "Error updating contact role in SalesForce");
        return;
    }

    private void removeContactRoleInSalesForce(String accessToken, String contactRoleId) {
        LOGGER.info("About to remove contact role in SalesForce");
        validateSalesForceId(contactRoleId);
        WebResource resource = createObjectsResource("/Membership_Contact_Role__c/", contactRoleId);
        ClientResponse response = doDeleteRequest(resource, accessToken);
        checkAuthorization(response);
        checkResponse(response, 204, "Error removing contact role in SalesForce");
    }

    private String createMemberInSalesForce(String accessToken, Member member) {
        LOGGER.info("About to create member in SalesForce");
        WebResource resource = createObjectsResource("/Account/");
        JSONObject memberJson = salesForceAdapter.createSaleForceRecordFromMember(member);
        ClientResponse response = doPostRequest(resource, memberJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating member in SalesForce");
        return result.optString("id");
    }

    private void updateMemberInSalesForce(String accessToken, Member member) {
        LOGGER.info("About update member in SalesForce");
        String accountId = member.getId();
        validateSalesForceId(accountId);
        WebResource resource = createObjectsResource("/Account/", accountId).queryParam("_HttpMethod", "PATCH");
        JSONObject memberJson = salesForceAdapter.createSaleForceRecordFromMember(member);
        // SalesForce doesn't allow the Id in the body
        memberJson.remove("Id");
        ClientResponse response = doPostRequest(resource, memberJson, accessToken);
        checkAuthorization(response);
        checkResponse(response, 204, "Error updating member in SalesForce");
        return;
    }

    private String createOpportunityInSalesForce(String accessToken, Opportunity opportunity) {
        LOGGER.info("About to create opportunity in SalesForce");
        WebResource resource = createObjectsResource("/Opportunity/");
        JSONObject opportunityJson = salesForceAdapter.createSaleForceRecordFromOpportunity(opportunity);
        ClientResponse response = doPostRequest(resource, opportunityJson, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 201, "Error creating opportunity in SalesForce");
        return result.optString("id");
    }

    private void updateOpportunityInSalesForce(String accessToken, Opportunity opportunity) {
        LOGGER.info("About to flag opportunity as closed in SalesForce");
        WebResource resource = createObjectsResource("/Opportunity/", opportunity.getId()).queryParam("_HttpMethod", "PATCH");
        JSONObject memberJson = salesForceAdapter.createSaleForceRecordFromOpportunity(opportunity);
        // SalesForce doesn't allow the Id in the body
        memberJson.remove("Id");
        ClientResponse response = doPostRequest(resource, memberJson, accessToken);
        checkAuthorization(response);
        checkResponse(response, 204, "Error updating opportunity in SalesForce");
    }

    private void removeOpportunityInSalesForce(String accessToken, String opportunityId) {
        LOGGER.info("About to remove opportunity in SalesForce");
        validateSalesForceId(opportunityId);
        WebResource resource = createObjectsResource("/Opportunity/", opportunityId);
        ClientResponse response = doDeleteRequest(resource, accessToken);
        checkAuthorization(response);
        checkResponse(response, 204, "Error removing opportunity in SalesForce");
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

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private Member retrieveMemberFromSalesForce(String accessToken, String accountId) throws SalesForceUnauthorizedException {
        LOGGER.info("About get member from SalesForce");
        List<Member> membersList = new ArrayList<>();
        JSONObject jsonObject = retrieveMembersObject(accessToken, accountId);
        membersList.addAll(salesForceAdapter.createMembersListFromJson(jsonObject));
        return !membersList.isEmpty() ? membersList.get(0) : null;
    }

    private JSONObject retrieveMembersObject(String accessToken) {
        return retrieveMembersObject(accessToken, null);
    }

    private JSONObject retrieveMembersObject(String accessToken, String accountId) {
        StringBuffer query = new StringBuffer();
        query.append(
                "SELECT Account.Id, Account.ParentId, Account.OwnerId, Account.Name, Account.Public_Display_Name__c, Account.Website, Account.BillingCountry, Account.Research_Community__c, ");
        query.append(
                "(SELECT Consortia_Lead__c from Opportunities WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_Start_Date__c<=TODAY AND Membership_End_Date__c>TODAY ORDER BY Membership_Start_Date__c DESC), ");
        query.append(
                "Account.Public_Display_Description__c, Account.Logo_Description__c, Account.Public_Display_Email__c, Account.Last_membership_start_date__c, Account.Last_membership_end_date__c from Account WHERE Active_Member__c=TRUE");
        if (accountId != null) {
            validateSalesForceId(accountId);
            query.append(" AND Account.Id = '");
            query.append(accountId);
            query.append("'");
        }
        WebResource resource = createQueryResource(query.toString());
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        return checkResponse(response, 200, "Error getting member list from SalesForce");
    }

    private JSONObject retrieveMembersNextObject(String accessToken, String nextRecordsUrl) {
        WebResource nextResource = creatNextRecordsResource(nextRecordsUrl);
        ClientResponse nextResponse = doGetRequest(nextResource, accessToken);
        return checkResponse(nextResponse, 200, "Error getting next results for member list from SalesForce");
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
    private List<Member> retrieveMembersByWebsiteFromSalesForce(String accessToken, String websiteUrl) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of members from SalesForce by website");
        WebResource resource = createQueryResource(String.format(
                "SELECT Account.Id, Name, Account.Public_Display_Name__c, Account.Website, Account.CreatedDate from Account WHERE Account.Website Like '%%25%s%%25' Order By Active_Member__c Desc, Account.CreatedDate Asc",
                escapeStringInput(websiteUrl)));
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting members by website from SalesForce");
        return salesForceAdapter.createMembersListFromJson(result);
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
        WebResource resource = createQueryResource(
                "SELECT Id, Name, Public_Display_Name__c, Website, Research_Community__c, BillingCountry, Public_Display_Description__c, Logo_Description__c, "
                        + "(SELECT Opportunity.Id FROM Opportunities WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_Start_Date__c<=TODAY AND Membership_End_Date__c>TODAY ORDER BY Membership_Start_Date__c DESC) "
                        + "FROM Account WHERE Id IN (SELECT Consortia_Lead__c FROM Opportunity WHERE IsClosed=TRUE AND IsWon=TRUE AND Membership_Start_Date__c<=TODAY AND Membership_End_Date__c>TODAY) AND Active_Member__c=TRUE");
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting consortia list from SalesForce");
        return salesForceAdapter.createMembersListFromJson(result);
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
        WebResource resource = createQueryResource(
                "SELECT (SELECT Id, AccountId, Account.Name, Account.Public_Display_Name__c, StageName, NextStep, Consortium_member_removal_requested__c, Consortia_Lead__c FROM ConsortiaOpportunities__r WHERE StageName In ('Negotiation/Review', 'Invoice Paid') AND Membership_Start_Date__c<=TODAY AND Membership_End_Date__c>TODAY ORDER BY Account.Public_Display_Name__c) from Account WHERE Id='%s'",
                consortiumId);
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting consortium from SalesForce");
        return salesForceAdapter.createConsortiumFromJson(result);
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
        WebResource resource = createQueryResource("SELECT Public_Display_Name__c from Account WHERE Id='%s'", consortiumLeadId);
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting parent org name from SalesForce");
        return salesForceAdapter.extractParentOrgNameFromJson(result);
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<Integration> retrieveIntegrationsFromSalesForce(String accessToken, String memberId) throws SalesForceUnauthorizedException {
        String query = new String();
        
        if(Features.NEW_BADGES.isActive()) {
            query = "SELECT Integration__c.Id, Integration__c.Name, Integration__c.Description__c, Integration__c.Integration_Stage__c, Integration__c.Level__c, Integration__c.BadgeAwarded__c, (Select Id, Name, Status__c, Badge__c from Achievements__r Where Status__c = 'Awarded') from Integration__c WHERE Integration__c.inactive__c=FALSE And Organization__c='%s'";
        } else {
            query = "SELECT Integration__c.Id, Integration__c.Name, Integration__c.Description__c, Integration__c.Integration_Stage__c, Integration__c.Level__c, Integration__c.BadgeAwarded__c from Integration__c WHERE Integration__c.inactive__c=FALSE And Organization__c='%s'";
        }
        
        WebResource resource = createQueryResource(query, memberId);
        
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting integrations list from SalesForce");
        return salesForceAdapter.createIntegrationsListFromJson(result);
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
        WebResource resource1 = createQueryResource("Select Id, AccountId, Name, Email, ORCID_iD_Path__c From Contact Where AccountId='%s'", accountId);
        WebResource resource = resource1;
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting all contacts from SalesForce");
        return salesForceAdapter.createContactsFromJson(result);
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<Contact> retrieveContactsWithRolesFromSalesForceByAccountId(String accessToken, String accountId, boolean includeNonCurrent)
            throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of contacts from SalesForce");
        validateSalesForceId(accountId);
        StringBuilder query = new StringBuilder(
                "Select (Select Id, Contact__c, Contact__r.FirstName, Contact__r.LastName, Contact__r.Email, Member_Org_Role__c, Voting_Contact__c, Current__c, Organization__c From Membership_Contact_Roles__r");
        if (!includeNonCurrent) {
            query.append(" Where Current__c = True");
        }
        query.append(" Order By Contact__r.LastName Desc, Contact__r.FirstName Desc) From Account a Where Id='%s'");
        WebResource resource = createQueryResource(query.toString(), accountId);
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting contacts from SalesForce");
        return salesForceAdapter.createContactsWithRolesFromJson(result);
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
        WebResource resource1 = createQueryResource(
                "Select Id, Contact__c, Member_Org_Role__c From Membership_Contact_Role__c Where Contact__c = '%s' And Organization__c='%s'", contactId, accountId);
        WebResource resource = resource1;
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting contacts from SalesForce");
        return salesForceAdapter.createContactRolesFromJson(result);
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
        WebResource resource1 = createQueryResource("Select Id From Member_Type__c Where Name = 'Premium Consortium Member'");
        WebResource resource = resource1;
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting premium consortium member type ID from SalesForce");
        return salesForceAdapter.extractIdFromFirstRecord(result);
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
        WebResource resource = createQueryResource("Select Id, Name, SobjectType From RecordType  Where SobjectType = 'Opportunity' And Name = 'Consortium Member'");
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting consortium member record type ID from SalesForce");
        return salesForceAdapter.extractIdFromFirstRecord(result);
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private Opportunity retrieveOpportunityFromSalesForce(String accessToken, String opportunityId) throws SalesForceUnauthorizedException {
        LOGGER.info("About get opportunity from SalesForce");
        validateSalesForceId(opportunityId);
        WebResource resource = createObjectsResource("/Opportunity/", opportunityId);
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting opportunity from SalesForce");
        return salesForceAdapter.createOpportunityFromSalesForceRecord(result);
    }
    
    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<OrgId> retrieveOrgIdsFromSalesForceByAccountId(String accessToken, String accountId) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of org ids from SalesForce");
        validateSalesForceId(accountId);
        WebResource resource1 = createQueryResource("Select Id, Name, Identifier_Type__c, Inactive__c, Primary_ID_for_type__c, Organization__c, Date_Granted__c, Notes__c From Organization_Identifier__c Where Organization__c = '%s'", accountId);
        WebResource resource = resource1;
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting org ids from SalesForce");
        return salesForceAdapter.createOrgIdsFromJson(result);
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<Badge> retrieveBadgesFromSalesForce(String accessToken) throws SalesForceUnauthorizedException {
        LOGGER.info("About get list of badges from SalesForce");
        WebResource resource = createQueryResource(
                "Select Id, Name, Public_Description__c, Badge_Index__c, Badge_Index_Name__c From Badge__c Where Active__c = true Order By Badge_Index_Name__c");
        ClientResponse response = doGetRequest(resource, accessToken);
        checkAuthorization(response);
        JSONObject result = checkResponse(response, 200, "Error getting badges list from SalesForce");
        return salesForceAdapter.createBadgesListFromJson(result);
    }
    
    private <T> T retry(Function<String, T> function) {
        try {
            return function.apply(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to access SalesForce, trying function again.", e);
            return function.apply(getFreshAccessToken());
        }
    }

    private WebResource createObjectsResource(String path) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/sobjects" + path);
        return resource;
    }

    private WebResource createObjectsResource(String path, String id) {
        validateSalesForceId(id);
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/sobjects" + path + id);
        return resource;
    }

    private WebResource createQueryResource(String query) {
        return client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q", query);
    }

    private WebResource createQueryResource(String query, String... ids) {
        for (String id : ids) {
            validateSalesForceId(id);
        }
        String formattedQuery = String.format(query, ids);
        return client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q", formattedQuery);
    }

    private void retryConsumer(Consumer<String> consumer) {
        try {
            consumer.accept(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to access SalesForce, trying consumer again.", e);
            consumer.accept(getFreshAccessToken());
        }
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

    private ClientResponse doDeleteRequest(WebResource resource, String accessToken) {
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).type(MediaType.APPLICATION_JSON_TYPE).delete(ClientResponse.class);
        return response;
    }

    private void checkAuthorization(ClientResponse response) {
        if (response.getStatus() == 401) {
            throw new SalesForceUnauthorizedException("Unauthorized reponse from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase() + ", body= " + response.getEntity(String.class));
        }
    }

    private JSONObject checkResponse(ClientResponse response, int requiredStatus, String errorMessage) {
        if (response.getStatus() != requiredStatus) {
            throw new RuntimeException(errorMessage + ", status code =  " + response.getStatus() + ", reason = " + response.getStatusInfo().getReasonPhrase()
                    + ", body = " + response.getEntity(String.class));
        }
        if (requiredStatus == 204) {
            return null;
        }
        return response.getEntity(JSONObject.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = Client.create();
        if (clientLoggingEnabled) {
            client.addFilter(new LoggingFilter());
        }
    }

}
