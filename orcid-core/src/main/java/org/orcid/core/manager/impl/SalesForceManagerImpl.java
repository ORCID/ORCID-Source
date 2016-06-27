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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.SalesForceUnauthorizedException;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.pojo.SalesForceIntegration;
import org.orcid.pojo.SalesForceMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceManagerImpl implements SalesForceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesForceManager.class);

    @Value("${org.orcid.core.salesForce.clientId}")
    private String clientId;

    @Value("${org.orcid.core.salesForce.clientSecret}")
    private String clientSecret;

    @Value("${org.orcid.core.salesForce.username}")
    private String username;

    @Value("${org.orcid.core.salesForce.password}")
    private String password;

    @Value("${org.orcid.core.salesForce.tokenEndPointUrl:https://login.salesforce.com/services/oauth2/token}")
    private String tokenEndPointUrl;

    @Value("${org.orcid.core.salesForce.apiBaseUrl:https://na11.salesforce.com}")
    private String apiBaseUrl;

    private Client client = Client.create();

    private String accessToken;

    @Override
    public List<SalesForceMember> retrieveMembers() {
        try {
            return retrieveMembersFromsSalesForce(getAccessToken());
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve members list, trying again.");
            return retrieveMembersFromsSalesForce(getFreshAccessToken());
        }
    }

    @Override
    public List<SalesForceIntegration> retrieveIntegrations(String memberId) {
        validateMemberId(memberId);
        try {
            return retrieveIntegrationsFromSalesForce(getAccessToken(), memberId);
        } catch (SalesForceUnauthorizedException e) {
            LOGGER.debug("Unauthorized to retrieve integrations list, trying again.");
            return retrieveIntegrationsFromSalesForce(getFreshAccessToken(), memberId);
        }
    }

    @Override
    public String validateMemberId(String memberId) {
        if (!memberId.matches("[a-zA-Z0-9]+")) {
            // Could be malicious, so give no further info.
            throw new IllegalArgumentException();
        }
        return memberId;
    }

    /**
     * 
     * @throws SalesForceUnauthorizedException
     *             If the status code from SalesForce is 401, e.g. access token
     *             expired.
     * 
     */
    private List<SalesForceMember> retrieveMembersFromsSalesForce(String accessToken) throws SalesForceUnauthorizedException {
        List<SalesForceMember> members = new ArrayList<>();
        LOGGER.info("About get list of members from SalesForce");
        WebResource resource = createMemberListResource();
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        if (response.getStatus() == 401) {
            LOGGER.debug("Unauthorized reponse from members list call = " + response.getEntity(String.class));
            throw new SalesForceUnauthorizedException(
                    "Unauthorized reponse from members list call, status code =  " + response.getStatus() + ", reason = " + response.getStatusInfo().getReasonPhrase());
        }
        if (response.getStatus() != 200) {
            LOGGER.debug("Error response body from members list call = " + response.getEntity(String.class));
            throw new RuntimeException(
                    "Error getting member list from SalesForce, status code =  " + response.getStatus() + ", reason = " + response.getStatusInfo().getReasonPhrase());
        }
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

    private WebResource createMemberListResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Account.Id, Account.Name, Account.Website, Account.BillingCountry, Account.Research_Community__c, (SELECT Consortia_Lead__c from Opportunities), Account.Public_Display_Description__c, Account.Logo_Description__c from Account WHERE Active_Member__c=TRUE");
        return resource;
    }

    private SalesForceMember createMemberFromSalesForceRecord(JSONObject record) throws JSONException {
        String name = extractString(record, "Name");
        SalesForceMember member = new SalesForceMember();
        member.setName(name);
        member.setId(extractString(record, "Id"));
        try {
            member.setWebsiteUrl(extractURL(record, "Website"));
        } catch (MalformedURLException e) {
            LOGGER.info("Malformed website URL for member: {}", name, e);
        }
        member.setResearchCommunity(extractString(record, "Research_Community__c"));
        member.setCountry(extractString(record, "BillingCountry"));
        // XXX parent org
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
    private List<SalesForceIntegration> retrieveIntegrationsFromSalesForce(String accessToken, String memberId) throws SalesForceUnauthorizedException {
        List<SalesForceIntegration> integrations = new ArrayList<>();
        WebResource resource = createIntegrationListResource(memberId);
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        if (response.getStatus() == 401) {
            LOGGER.debug("Unauthorized response from integrations list call = " + response.getEntity(String.class));
            throw new SalesForceUnauthorizedException("Unauthorised response from integrations list call, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase());
        }
        if (response.getStatus() != 200) {
            LOGGER.debug("Error response body from integrations list call = " + response.getEntity(String.class));
            throw new RuntimeException("Error getting integrations list from SalesForce, status code =  " + response.getStatus() + ", reason = "
                    + response.getStatusInfo().getReasonPhrase());
        }
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

    private WebResource createIntegrationListResource(String memberId) {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT (SELECT Integration__c.Name, Integration__c.Description__c, Integration__c.Integration_Stage__c, Integration__c.Integration_URL__c from Account.Integrations__r) from Account WHERE Id='"
                        + validateMemberId(memberId) + "'");
        return resource;
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

    private URL extractURL(JSONObject record, String key) throws JSONException, MalformedURLException {
        String urlString = tidyUrl(extractString(record, key));
        return urlString != null ? new URL(urlString) : null;
    }

    private String tidyUrl(String urlString) {
        if (StringUtils.isBlank(urlString)) {
            return null;
        }
        if (!urlString.matches("^.*?://.*")) {
            urlString = "http://" + urlString;
        }
        return urlString;
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
            throw new RuntimeException(
                    "Error getting access token from SalesForce, status code =  " + response.getStatus() + ", reason = " + response.getStatusInfo().getReasonPhrase());
        }
    }

}
