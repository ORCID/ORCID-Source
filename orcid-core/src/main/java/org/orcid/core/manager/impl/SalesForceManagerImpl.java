package org.orcid.core.manager.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.manager.SalesForceManager;
import org.orcid.persistence.dao.IdentityProviderDao;
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

    @Resource
    private IdentityProviderDao identityProviderDao;

    private Client client = Client.create();

    @Override
    public List<SalesForceMember> retrieveMembers() {
        String accessToken = getAccessToken();
        List<SalesForceMember> members = retrieveMembersFromsSalesForce(accessToken);
        return members;
    }

    private List<SalesForceMember> retrieveMembersFromsSalesForce(String accessToken) {
        List<SalesForceMember> members = new ArrayList<>();
        LOGGER.info("About get SalesForce access token");
        WebResource resource = createMemberListResource();
        ClientResponse response = resource.header("Authorization", "Bearer " + accessToken).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
        if (response.getStatus() != 200) {
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
            throw new RuntimeException("Error getting records from SalesForce JSON", e);
        }
        return members;
    }

    private WebResource createMemberListResource() {
        WebResource resource = client.resource(apiBaseUrl).path("services/data/v20.0/query").queryParam("q",
                "SELECT Account.Id, Account.Name, Account.Website, Account.BillingCountry, Account.Research_Community__c, (SELECT Consortia_Lead__c from Opportunities), Account.Public_Display_Description__c, Account.Logo_Description__c from Account WHERE Active_Member__c=TRUE");
        return resource;
    }

    private SalesForceMember createMemberFromSalesForceRecord(JSONObject record) throws JSONException {
        String name = record.getString("Name");
        SalesForceMember member = new SalesForceMember();
        member.setName(name);
        try {
            member.setWebsiteUrl(new URL(record.getString("Website")));
        } catch (MalformedURLException e) {
            LOGGER.info("Malformed website URL for member: {}", name, e);
        }
        member.setResearchCommunity(record.getString("Research_Community__c"));
        member.setCountry(record.getString("BillingCountry"));
        /// XXX parent org
        member.setDescription(record.getString("Public_Display_Description__c"));
        try {
            member.setLogoUrl(new URL(record.getString("Logo_Description__c")));
        } catch (MalformedURLException e) {
            LOGGER.info("Malformed logo URL for member: {}", name, e);
        }
        return member;
    }

    private String getAccessToken() {
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
