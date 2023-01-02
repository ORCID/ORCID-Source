package org.orcid.frontend.salesforce.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.frontend.salesforce.model.Integration;
import org.orcid.frontend.salesforce.model.Member;
import org.orcid.frontend.salesforce.model.Opportunity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.MapperFacade;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SalesForceAdapter.class);

    @Resource(name = "salesForceMemberMapperFacade")
    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;        
    }

    public List<Member> createMembersListFromJson(JSONObject results) {
        List<Member> members = new ArrayList<>();
        try {
            JSONArray records = results.getJSONArray("records");
            for (int i = 0; i < records.length(); i++) {
                members.add(createMemberFromSalesForceRecord(records.getJSONObject(i)));
            }
        } catch (JSONException e) {
            LOGGER.error("Error getting member records from SalesForce JSON", e);
            throw new RuntimeException("Error getting member records from SalesForce JSON", e);
        }
        return members;
    }
    
    public Member createMemberFromSalesForceRecord(JSONObject record) throws JSONException {
        return mapperFacade.map(record, Member.class);        
    }
    
    public List<Integration> createIntegrationsListFromJson(JSONObject results) {
        List<JSONObject> objectsList;
        try {
            objectsList = extractObjectListFromRecords(results);
            return objectsList.stream().map(e -> mapperFacade.map(e, Integration.class)).collect(Collectors.toList());                       
        } catch (JSONException e) {
            LOGGER.error("Error getting integrations from SalesForce JSON", e);
            throw new RuntimeException("Error getting integrations from SalesForce JSON", e);
        }
    }
    
    public List<Integration> createIntegrationsList(JSONArray integrations) {
        List<Integration> integrationList = new ArrayList<Integration>();
        try {
            for (int i = 0; i < integrations.length(); i++) {
                integrationList.add(mapperFacade.map(integrations.getJSONObject(i), Integration.class));
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting integrations from SalesForce JSON", e);
        }
        return integrationList;
    }
    
    public List<Opportunity> createOpportunitiesList(JSONArray opportunities) {
        List<Opportunity> opportunityList = new ArrayList<Opportunity>();
        try {
            for (int i = 0; i < opportunities.length(); i++) {
                opportunityList.add(mapperFacade.map(opportunities.getJSONObject(i), Opportunity.class));
            }
        } catch (JSONException e) {
            LOGGER.error("Error getting opportunities from SalesForce JSON", e);
            throw new RuntimeException("Error getting opportunities from SalesForce JSON", e);
        }
        return opportunityList;
    }
    
    public static String extractIdFromUrl(String url) {
        if (url == null) {
            return null;
        }
        int slashIndex = url.lastIndexOf('/');
        if (slashIndex == -1) {
            throw new IllegalArgumentException("Unable to extract ID, url = " + url);
        }
        return url.substring(slashIndex + 1);
    }
    
    private List<JSONObject> extractObjectListFromRecords(JSONObject object) throws JSONException {
        List<JSONObject> objects = new ArrayList<>();
        if (object != null) {
            JSONArray records = object.getJSONArray("records");
            for (int i = 0; i < records.length(); i++) {
                objects.add(records.getJSONObject(i));
            }
        }
        return objects;
    }
}
