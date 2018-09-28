package org.orcid.core.salesforce.adapter;

import static org.orcid.core.utils.JsonUtils.extractObject;
import static org.orcid.core.utils.JsonUtils.extractString;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.salesforce.model.Consortium;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.OpportunityContactRole;
import org.orcid.core.salesforce.model.OrgId;
import org.orcid.core.utils.JsonUtils;
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
                    Opportunity salesForceOpportunity = createOpportunityFromSalesForceRecord(opportunityRecords.getJSONObject(i));
                    opportunityList.add(salesForceOpportunity);
                }
                return consortium;
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting consortium record from SalesForce JSON", e);
        }
        return null;
    }

    Contact createContactFromJson(JSONObject record) {
        return mapperFacade.map(record, Contact.class);
    }

    public List<Contact> createContactsFromJson(JSONObject object) {
        try {
            List<JSONObject> objectsList = extractObjectListFromRecords(object);
            return objectsList.stream().map(e -> mapperFacade.map(e, Contact.class)).collect(Collectors.toList());
        } catch (JSONException e) {
            throw new RuntimeException("Error getting all contacts list from SalesForce JSON", e);
        }
    }

    public List<Contact> createContactsWithRolesFromJson(JSONObject object) {
        try {
            JSONObject firstRecord = extractFirstRecord(object);
            JSONObject contactRoles = firstRecord.optJSONObject("Membership_Contact_Roles__r");
            List<JSONObject> objectsList = extractObjectListFromRecords(contactRoles);
            return objectsList.stream().map(e -> mapperFacade.map(e, Contact.class)).collect(Collectors.toList());
        } catch (JSONException e) {
            throw new RuntimeException("Error getting contacts with roles list from SalesForce JSON", e);
        }
    }

    public JSONObject createSaleForceRecordFromContact(Contact contact) {
        return mapperFacade.map(contact, JSONObject.class);
    }

    public List<ContactRole> createContactRolesFromJson(JSONObject object) {
        try {
            List<JSONObject> contactRoles = extractObjectListFromRecords(object);
            return contactRoles.stream().map(e -> mapperFacade.map(e, ContactRole.class)).collect(Collectors.toList());
        } catch (JSONException e) {
            throw new RuntimeException("Error getting contact roles list from SalesForce JSON", e);
        }
    }

    public JSONObject createSaleForceRecordFromContactRole(ContactRole contactRole) {
        return mapperFacade.map(contactRole, JSONObject.class);
    }

    public JSONObject createSaleForceRecordFromOpportunityContactRole(OpportunityContactRole contactRole) {
        return mapperFacade.map(contactRole, JSONObject.class);
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
        List<JSONObject> objectsList;
        try {
            objectsList = extractObjectListFromRecords(results);
            return objectsList.stream().map(e -> mapperFacade.map(e, Integration.class)).collect(Collectors.toList());
        } catch (JSONException e) {
            throw new RuntimeException("Error getting integrations from SalesForce JSON", e);
        }
    }

    public String extractParentOrgNameFromJson(JSONObject results) {
        try {
            JSONArray records = results.getJSONArray("records");
            if (records.length() > 0) {
                // return extractString(records.getJSONObject(0), "Name");
                return extractString(records.getJSONObject(0), "Public_Display_Name__c");
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error getting parent org from SalesForce JSON", e);
        }
        return null;
    }

    Member createMemberFromSalesForceRecord(JSONObject record) throws JSONException {
        return mapperFacade.map(record, Member.class);
    }

    public JSONObject createSaleForceRecordFromMember(Member member) {
        return mapperFacade.map(member, JSONObject.class);
    }

    private Integration createIntegrationFromSalesForceRecord(JSONObject integrationRecord) throws JSONException {
        return mapperFacade.map(integrationRecord, Integration.class);
    }

    public Opportunity createOpportunityFromSalesForceRecord(JSONObject jsonObject) {
        return mapperFacade.map(jsonObject, Opportunity.class);
    }

    public JSONObject createSaleForceRecordFromOpportunity(Opportunity opportunity) {
        JSONObject jsonObject = mapperFacade.map(opportunity, JSONObject.class);
        return jsonObject;
    }
    
    public OrgId createOrgIdFromJson(JSONObject record) {
        return mapperFacade.map(record, OrgId.class);
    }

    public List<OrgId> createOrgIdsFromJson(JSONObject object) {
        try {
            List<JSONObject> objectsList = extractObjectListFromRecords(object);
            return objectsList.stream().map(e -> mapperFacade.map(e, OrgId.class)).collect(Collectors.toList());
        } catch (JSONException e) {
            throw new RuntimeException("Error getting org id list from SalesForce JSON", e);
        }
    }

    public JSONObject createSaleForceRecordFromOrgId(OrgId orgId) {
        return mapperFacade.map(orgId, JSONObject.class);
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

    private JSONObject extractFirstRecord(JSONObject object) throws JSONException {
        JSONArray records = object.getJSONArray("records");
        return records.getJSONObject(0);
    }

    public String extractIdFromFirstRecord(JSONObject object) {
        JSONObject firstRecord;
        try {
            firstRecord = extractFirstRecord(object);
            return firstRecord.optString("Id");
        } catch (JSONException e) {
            throw new RuntimeException("Error getting ID from first record", e);
        }
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

}
