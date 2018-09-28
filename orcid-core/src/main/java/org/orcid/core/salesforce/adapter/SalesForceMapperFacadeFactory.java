package org.orcid.core.salesforce.adapter;

import static org.orcid.core.utils.JsonUtils.extractBoolean;
import static org.orcid.core.utils.JsonUtils.extractString;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.salesforce.model.Achievement;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Integration;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.Opportunity;
import org.orcid.core.salesforce.model.OpportunityContactRole;
import org.orcid.core.salesforce.model.OrgId;
import org.springframework.beans.factory.FactoryBean;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.MappingException;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.Property;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.property.IntrospectorPropertyResolver;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceMapperFacadeFactory implements FactoryBean<MapperFacade> {

    @Override
    public MapperFacade getObject() throws Exception {
        return getMemberMapperFacade();
    }

    @Override
    public Class<MapperFacade> getObjectType() {
        return MapperFacade.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public MapperFacade getMemberMapperFacade() throws Exception {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().propertyResolverStrategy(new JSONPropertyResolver()).build();
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new StringConverter());
        converterFactory.registerConverter(new URLConverter());
        converterFactory.registerConverter(new ReverseURLConverter());
        registerMemberMap(mapperFactory);
        registerOpportunityMap(mapperFactory);
        registerContactMap(mapperFactory);
        registerContactRoleMap(mapperFactory);
        registerOpportunityContactRoleMap(mapperFactory);
        registerOrgIdMap(mapperFactory);
        registerIntegrationMap(mapperFactory);
        return mapperFactory.getMapperFacade();
    }

    public void registerMemberMap(MapperFactory mapperFactory) {
        ClassMapBuilder<Member, JSONObject> classMap = mapperFactory.classMap(Member.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new CommunityTypeConverter());
        converterFactory.registerConverter(new ReverseCommunityTypeConverter());
        classMap.field("id", "Id");
        classMap.field("parentId", "ParentId");
        classMap.field("ownerId", "OwnerId");
        classMap.field("name", "Name");
        classMap.field("publicDisplayName", "Public_Display_Name__c");
        classMap.field("websiteUrl", "Website");
        classMap.field("researchCommunity", "Research_Community__c");
        classMap.field("country", "BillingCountry");
        classMap.field("description", "Public_Display_Description__c");
        classMap.field("logoUrl", "Logo_Description__c");
        classMap.field("publicDisplayEmail", "Public_Display_Email__c");
        classMap.field("emailDomains", "Email_domains__c");
        classMap.fieldBToA("Last_membership_start_date__c", "lastMembershipStartDate");
        classMap.fieldBToA("Last_membership_end_date__c", "lastMembershipEndDate");
        classMap.customize(new CustomMapper<Member, JSONObject>() {
            @Override
            public void mapAtoB(Member a, JSONObject b, MappingContext context) {
                if (a.getWebsiteUrl() == null) {
                    try {
                        b.put("Website", JSONObject.NULL);
                    } catch (JSONException e) {
                        new RuntimeException("Error setting website to null", e);
                    }
                }
            }
            @Override
            public void mapBtoA(JSONObject b, Member a, MappingContext context) {
                JSONObject opportunitiesObject = b.optJSONObject("Opportunities");
                if (opportunitiesObject != null) {
                    JSONArray recordsArray = opportunitiesObject.optJSONArray("records");
                    if (recordsArray != null && recordsArray.length() > 0) {
                        try {
                            JSONObject first = recordsArray.getJSONObject(0);
                            Object urlObj = first.getJSONObject("attributes").opt("url");
                            a.setMainOpportunityPath(JSONObject.NULL.equals(urlObj) ? null : urlObj.toString());
                            Object consortiumLeadIdObj = first.opt("Consortia_Lead__c");
                            a.setConsortiumLeadId(JSONObject.NULL.equals(consortiumLeadIdObj) ? null : consortiumLeadIdObj.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException("Error reading first opportunity record", e);
                        }
                    }
                }
            }
        });
        classMap.register();
    }

    private void registerOpportunityMap(MapperFactory mapperFactory) {
        ClassMapBuilder<Opportunity, JSONObject> classMap = mapperFactory.classMap(Opportunity.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        classMap.field("id", "Id");
        classMap.field("ownerId", "OwnerId");
        classMap.field("targetAccountId", "AccountId");
        classMap.field("stageName", "StageName");
        classMap.field("closeDate", "CloseDate");
        classMap.field("type", "Type");
        classMap.field("memberType", "member_type__c");
        classMap.field("membershipStartDate", "Membership_Start_Date__c");
        classMap.field("membershipEndDate", "Membership_End_Date__c");
        classMap.field("consortiumLeadId", "Consortia_Lead__c");
        classMap.field("name", "Name");
        classMap.field("recordTypeId", "RecordTypeId");
        classMap.field("nextStep", "NextStep");
        classMap.field("removalRequested", "Consortium_member_removal_requested__c");
        classMap.fieldBToA("Account.Name", "accountName");
        classMap.fieldBToA("Account.Public_Display_Name__c", "accountPublicDisplayName");
        classMap.register();
    }

    private void registerContactMap(MapperFactory mapperFactory) {
        ClassMapBuilder<Contact, JSONObject> classMap = mapperFactory.classMap(Contact.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        classMap.field("id", "Id");
        classMap.field("orcid", "ORCID_iD_Path__c");
        classMap.field("role.votingContact", "Voting_Contact__c");
        classMap.field("role.current", "Current__c");
        classMap.field("email", "Email");
        classMap.fieldAToB("firstName", "FirstName");
        classMap.fieldAToB("lastName", "LastName");
        classMap.fieldAToB("accountId", "AccountId");
        classMap.fieldBToA("Member_Org_Role__c", "role.roleType");
        classMap.fieldBToA("Contact__r.FirstName", "firstName");
        classMap.fieldBToA("Contact__r.LastName", "lastName");
        classMap.fieldBToA("Contact__r.Email", "email");
        classMap.fieldBToA("Contact__c", "id");
        classMap.fieldBToA("Contact__c", "role.contactId");
        classMap.fieldBToA("AccountId", "role.accountId");
        classMap.fieldBToA("Id", "role.id");
        classMap.fieldBToA("Organization__c", "accountId");
        
        classMap.register();
    }

    private void registerContactRoleMap(MapperFactory mapperFactory) {
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new ContactRoleConverter());
        converterFactory.registerConverter(new ReverseContactRoleConverter());
        ClassMapBuilder<ContactRole, JSONObject> classMap = mapperFactory.classMap(ContactRole.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        classMap.field("id", "Id");
        classMap.field("accountId", "Organization__c");
        classMap.field("contactId", "Contact__c");
        classMap.field("votingContact", "Voting_Contact__c");
        classMap.field("current", "Current__c");
        classMap.field("roleType", "Member_Org_Role__c");
        classMap.register();
    }

    private void registerOpportunityContactRoleMap(MapperFactory mapperFactory) {
        ClassMapBuilder<OpportunityContactRole, JSONObject> classMap = mapperFactory.classMap(OpportunityContactRole.class, JSONObject.class).mapNulls(false)
                .mapNullsInReverse(false);
        classMap.field("id", "Id");
        classMap.field("opportunityId", "OpportunityId");
        classMap.field("contactId", "ContactId");
        classMap.field("roleType", "Role");
        classMap.register();
    }
    
    private void registerOrgIdMap(MapperFactory mapperFactory) {
        ClassMapBuilder<OrgId, JSONObject> classMap = mapperFactory.classMap(OrgId.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        classMap.field("id", "Id");
        classMap.field("orgIdValue", "Name");
        classMap.field("orgIdType", "Identifier_Type__c");
        classMap.field("inactive", "Inactive__c");
        classMap.field("primaryIdForType", "Primary_ID_for_type__c");
        classMap.field("accountId", "Organization__c");
        classMap.field("notes", "Notes__c");
        classMap.register();
    }
    
    private void registerIntegrationMap(MapperFactory mapperFactory) {
        ClassMapBuilder<Integration, JSONObject> classMap = mapperFactory.classMap(Integration.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        classMap.field("id", "Id");
        classMap.field("name", "Name");
        classMap.field("badgeAwarded", "BadgeAwarded__c");
        classMap.field("description", "Description__c");
        classMap.field("level", "Level__c");
        classMap.field("stage", "Integration_Stage__c");
        classMap.field("resourceUrl", "Integration_URL__c");
        classMap.customize(new CustomMapper<Integration, JSONObject>() {
            @Override
            public void mapBtoA(JSONObject b, Integration a, MappingContext context) {
                JSONObject opportunitiesObject = b.optJSONObject("Achievements__r");
                if (opportunitiesObject != null) {
                    JSONArray recordsArray = opportunitiesObject.optJSONArray("records");
                    if (recordsArray != null && recordsArray.length() > 0) {
                        try {
                            List<Achievement> achievements = a.getAchievements();
                            for (int i = 0; i < recordsArray.length(); i++) {
                                JSONObject record = recordsArray.getJSONObject(i);
                                Achievement achievement = new Achievement();
                                achievement.setBadgeId(record.optString("Badge__c"));
                                achievements.add(achievement);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException("Error reading achievements", e);
                        }
                    }
                }
            }
        });
        classMap.register();
    }

    private static class JSONPropertyResolver extends IntrospectorPropertyResolver {
        protected Property getProperty(java.lang.reflect.Type type, String expr, boolean isNestedLookup, Property owner) throws MappingException {
            Property property = null;
            try {
                property = super.getProperty(type, expr, isNestedLookup, null);
            } catch (MappingException e) {
                try {
                    property = super.resolveInlineProperty(type, expr + ":{opt(\"" + expr + "\")|put(\"" + expr + "\",%s)|type="
                            + (isNestedLookup ? "org.codehaus.jettison.json.JSONObject" : "Object") + "}");
                } catch (MappingException e2) {
                    throw e; // throw the original exception
                }
            }
            return property;
        }
    }

    private class StringConverter extends CustomConverter<Object, String> {
        @Override
        public String convert(Object source, Type<? extends String> destinationType) {
            if (JSONObject.NULL.equals(source)) {
                return null;
            }
            return source.toString();
        }
    }

    private class URLConverter extends CustomConverter<URL, Object> {
        @Override
        public Object convert(URL source, Type<? extends Object> destinationType) {
            return source.toString();
        }
    }

    private class ReverseURLConverter extends CustomConverter<Object, URL> {
        @Override
        public URL convert(Object source, Type<? extends URL> destinationType) {
            if (JSONObject.NULL.equals(source)) {
                return null;
            }
            String s = source.toString();
            if (!s.startsWith("http")) {
                s = "http://" + s;
            }
            try {
                return new URL(s);
            } catch (MalformedURLException e) {
                return null;
            }
        }
    }

    private class ContactRoleConverter extends CustomConverter<ContactRoleType, Object> {
        @Override
        public Object convert(ContactRoleType source, Type<? extends Object> destinationType) {
            return source.value();
        }
    }

    private class ReverseContactRoleConverter extends CustomConverter<Object, ContactRoleType> {
        @Override
        public ContactRoleType convert(Object source, Type<? extends ContactRoleType> destinationType) {
            return ContactRoleType.fromValue(source.toString());
        }
    }

    private class CommunityTypeConverter extends CustomConverter<CommunityType, Object> {
        @Override
        public Object convert(CommunityType source, Type<? extends Object> destinationType) {
            return source.value();
        }
    }

    private class ReverseCommunityTypeConverter extends CustomConverter<Object, CommunityType> {
        @Override
        public CommunityType convert(Object source, Type<? extends CommunityType> destinationType) {
            return CommunityType.fromValue(source.toString());
        }
    }

}
