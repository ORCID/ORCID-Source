package org.orcid.frontend.salesforce.adapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.mapstruct.Mapper;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.frontend.salesforce.model.Achievement;
import org.orcid.frontend.salesforce.model.Contact;
import org.orcid.frontend.salesforce.model.ContactRole;
import org.orcid.frontend.salesforce.model.ContactRoleType;
import org.orcid.frontend.salesforce.model.Integration;
import org.orcid.frontend.salesforce.model.Member;
import org.orcid.frontend.salesforce.model.Opportunity;

@Mapper(componentModel = "spring")
public interface SalesForceMapper {

    default Member toMember(JSONObject json) {
        if (json == null) return null;
        Member m = new Member();
        m.setId(getString(json, "Id"));
        m.setOwnerId(getString(json, "OwnerId"));
        m.setName(getString(json, "Name"));
        m.setPublicDisplayName(getString(json, "Public_Display_Name__c"));
        m.setWebsiteUrl(getUrl(json, "Website"));
        m.setResearchCommunity(getCommunityType(json, "Research_Community__c"));
        m.setCountry(getString(json, "BillingCountry"));
        m.setDescription(getString(json, "Public_Display_Description__c"));
        m.setLogoUrl(getUrl(json, "Logo_Description__c"));
        m.setPublicDisplayEmail(getString(json, "Public_Display_Email__c"));
        m.setEmailDomains(getString(json, "Email_domains__c"));
        m.setRecordTypeId(getString(json, "RecordTypeId"));
        m.setConsortiumLeadId(getString(json, "Consortium_Lead__c"));
        
        Boolean isConsortiaMember = getBoolean(json, "Consortia_Member__c");
        if (isConsortiaMember != null) m.setIsConsortiaMember(isConsortiaMember);

        m.setLastMembershipStartDate(getString(json, "Last_membership_start_date__c"));
        m.setLastMembershipEndDate(getString(json, "Last_membership_end_date__c"));

        try {
            JSONObject opportunities = json.optJSONObject("Opportunities");
            if (opportunities != null) {
                JSONArray records = opportunities.optJSONArray("records");
                if (records != null && records.length() > 0) {
                    JSONObject first = records.getJSONObject(0);
                    JSONObject attributes = first.optJSONObject("attributes");
                    if (attributes != null && !JSONObject.NULL.equals(attributes.opt("url"))) {
                        m.setMainOpportunityPath(attributes.optString("url", null));
                    }
                    if (first.has("Consortia_Lead__c") && !JSONObject.NULL.equals(first.opt("Consortia_Lead__c"))) {
                        m.setConsortiumLeadId(first.optString("Consortia_Lead__c", null));
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error reading first opportunity record", e);
        }
        return m;
    }

    default Opportunity toOpportunity(JSONObject json) {
        if (json == null) return null;
        Opportunity o = new Opportunity();
        o.setId(getString(json, "Id"));
        o.setOwnerId(getString(json, "OwnerId"));
        o.setTargetAccountId(getString(json, "AccountId"));
        o.setStageName(getString(json, "StageName"));
        o.setCloseDate(getString(json, "CloseDate"));
        o.setType(getString(json, "Type"));
        o.setMemberType(getString(json, "member_type__c"));
        o.setMembershipStartDate(getString(json, "Membership_Start_Date__c"));
        o.setMembershipEndDate(getString(json, "Membership_End_Date__c"));
        o.setConsortiumLeadId(getString(json, "Consortia_Lead__c"));
        o.setName(getString(json, "Name"));
        o.setRecordTypeId(getString(json, "RecordTypeId"));
        o.setNextStep(getString(json, "NextStep"));
        
        Boolean removalRequested = getBoolean(json, "Consortium_member_removal_requested__c");
        if (removalRequested != null) o.setRemovalRequested(removalRequested);

        JSONObject account = json.optJSONObject("Account");
        if (account != null) {
            o.setAccountName(getString(account, "Name"));
            o.setAccountPublicDisplayName(getString(account, "Public_Display_Name__c"));
        }
        return o;
    }

    default Contact toContact(JSONObject json) {
        if (json == null) return null;
        Contact c = new Contact();
        
        // Match Orika priority mappings for ID fields
        String id = getString(json, "Contact__c");
        if (id == null) id = getString(json, "Id");
        c.setId(id);

        c.setOrcid(getString(json, "ORCID_iD_Path__c"));
        
        String email = getString(json, "Email");
        
        // Nested relation lookup
        JSONObject contactR = json.optJSONObject("Contact__r");
        if (contactR != null) {
            c.setFirstName(getString(contactR, "FirstName"));
            c.setLastName(getString(contactR, "LastName"));
            if (email == null) email = getString(contactR, "Email");
        }
        c.setEmail(email);
        c.setAccountId(getString(json, "Organization__c"));

        // Match Role nesting
        ContactRole role = new ContactRole();
        Boolean votingContact = getBoolean(json, "Voting_Contact__c");
        if (votingContact != null) role.setVotingContact(votingContact);
        
        Boolean current = getBoolean(json, "Current__c");
        if (current != null) role.setCurrent(current);
        
        role.setRoleType(getContactRoleType(json, "Member_Org_Role__c"));
        role.setContactId(getString(json, "Contact__c"));
        role.setAccountId(getString(json, "AccountId"));
        role.setId(getString(json, "Id"));
        c.setRole(role);

        return c;
    }

    default ContactRole toContactRole(JSONObject json) {
        if (json == null) return null;
        ContactRole cr = new ContactRole();
        cr.setId(getString(json, "Id"));
        cr.setAccountId(getString(json, "Organization__c"));
        cr.setContactId(getString(json, "Contact__c"));
        
        Boolean votingContact = getBoolean(json, "Voting_Contact__c");
        if (votingContact != null) cr.setVotingContact(votingContact);
        
        Boolean current = getBoolean(json, "Current__c");
        if (current != null) cr.setCurrent(current);
        
        cr.setRoleType(getContactRoleType(json, "Member_Org_Role__c"));
        return cr;
    }

    default Integration toIntegration(JSONObject json) {
        if (json == null) return null;
        Integration i = new Integration();
        i.setId(getString(json, "Id"));
        i.setName(getString(json, "Name"));
        
        // Correctly mapped using getBoolean instead of getString!
        Boolean badgeAwarded = getBoolean(json, "BadgeAwarded__c");
        if (badgeAwarded != null) {
            i.setBadgeAwarded(badgeAwarded);
        }
        
        i.setDescription(getString(json, "Description__c"));
        i.setLevel(getString(json, "Level__c"));
        i.setStage(getString(json, "Integration_Stage__c"));

        List<Achievement> achievements = new ArrayList<>();
        try {
            JSONObject achR = json.optJSONObject("Achievements__r");
            if (achR != null) {
                JSONArray records = achR.optJSONArray("records");
                if (records != null) {
                    for (int j = 0; j < records.length(); j++) {
                        JSONObject r = records.getJSONObject(j);
                        Achievement a = new Achievement();
                        a.setBadgeId(getString(r, "Badge__c"));
                        achievements.add(a);
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Error reading achievements", e);
        }
        i.setAchievements(achievements);
        return i;
    }

    // ========================================================================
    // Safe JSON Extractor Helpers
    // ========================================================================

    default String getString(JSONObject json, String key) {
        if (json == null || !json.has(key) || JSONObject.NULL.equals(json.opt(key))) {
            return null;
        }
        return json.optString(key, null);
    }

    default Boolean getBoolean(JSONObject json, String key) {
        if (json == null || !json.has(key) || JSONObject.NULL.equals(json.opt(key))) {
            return null;
        }
        return json.optBoolean(key);
    }

    default URL getUrl(JSONObject json, String key) {
        String val = getString(json, key);
        if (val == null) return null;
        if (!val.startsWith("http")) {
            val = "http://" + val; // Replicates Orika URL Converter
        }
        try {
            return new URL(val);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    default CommunityType getCommunityType(JSONObject json, String key) {
        String val = getString(json, key);
        return val == null ? null : CommunityType.fromValue(val);
    }

    default ContactRoleType getContactRoleType(JSONObject json, String key) {
        String val = getString(json, key);
        return val == null ? null : ContactRoleType.fromValue(val);
    }
}