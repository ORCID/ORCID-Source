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
package org.orcid.core.salesforce.adapter;

import java.net.MalformedURLException;
import java.net.URL;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.salesforce.model.CommunityType;
import org.orcid.core.salesforce.model.Contact;
import org.orcid.core.salesforce.model.ContactRole;
import org.orcid.core.salesforce.model.ContactRoleType;
import org.orcid.core.salesforce.model.Member;
import org.orcid.core.salesforce.model.Opportunity;
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
        return mapperFactory.getMapperFacade();
    }

    public void registerMemberMap(MapperFactory mapperFactory) {
        ClassMapBuilder<Member, JSONObject> classMap = mapperFactory.classMap(Member.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        ConverterFactory converterFactory = mapperFactory.getConverterFactory();
        converterFactory.registerConverter(new CommunityTypeConverter());
        converterFactory.registerConverter(new ReverseCommunityTypeConverter());
        classMap.field("id", "Id");
        classMap.field("name", "Name");
        classMap.field("publicDisplayName", "Public_Display_Name__c");
        classMap.field("websiteUrl", "Website");
        classMap.field("researchCommunity", "Research_Community__c");
        classMap.field("country", "BillingCountry");
        classMap.field("description", "Public_Display_Description__c");
        classMap.field("logoUrl", "Logo_Description__c");
        classMap.field("publicDisplayEmail", "Public_Display_Email__c");
        classMap.customize(new CustomMapper<Member, JSONObject>() {
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
        classMap.register();
    }

    private void registerContactMap(MapperFactory mapperFactory) {
        ClassMapBuilder<Contact, JSONObject> classMap = mapperFactory.classMap(Contact.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        classMap.field("id", "Id");
        classMap.field("orcid", "ORCID_iD_Path__c");
        classMap.fieldAToB("firstName", "FirstName");
        classMap.fieldAToB("lastName", "LastName");
        classMap.fieldAToB("email", "Email");
        classMap.fieldAToB("accountId", "AccountId");
        classMap.fieldBToA("Member_Org_Role__c", "role.roleType");
        classMap.fieldBToA("Contact__r.FirstName", "firstName");
        classMap.fieldBToA("Contact__r.LastName", "lastName");
        classMap.fieldBToA("Contact__r.Email", "email");
        classMap.fieldBToA("Contact__c", "id");
        classMap.fieldBToA("Contact__c", "role.contactId");
        classMap.fieldBToA("AccountId", "role.accountId");
        classMap.fieldBToA("Id", "role.id");
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
        classMap.field("roleType", "Member_Org_Role__c");
        classMap.register();
    }

    private static class JSONPropertyResolver extends IntrospectorPropertyResolver {
        /** Add this string to the property name to handle as a JSON Array **/
        private static final String ARRAY_EXPRESSION_SUFFIX = "_array";
        /** Use this property name to get the first item from a JSONArray **/
        private static final String FIRST_ITEM_FROM_ARRAY_EXPRESSION = "first";

        protected Property getProperty(java.lang.reflect.Type type, String expr, boolean isNestedLookup, Property owner) throws MappingException {
            Property property = null;
            try {
                property = super.getProperty(type, expr, isNestedLookup, null);
            } catch (MappingException e) {
                try {
                    if (expr.endsWith(ARRAY_EXPRESSION_SUFFIX)) {
                        String key = expr.substring(0, expr.indexOf('_'));
                        property = super.resolveInlineProperty(type, expr + ":{optJSONArray(\"" + key + "\")|put(\"" + key + "\",%s)|type="
                                + (isNestedLookup ? "org.codehaus.jettison.json.JSONArray" : "Object") + "}");
                    } else if (FIRST_ITEM_FROM_ARRAY_EXPRESSION.equals(expr)) {
                        property = super.resolveInlineProperty(type,
                                FIRST_ITEM_FROM_ARRAY_EXPRESSION + ":{opt(0)|put(0, %s)|type=org.codehaus.jettison.json.JSONObject}");
                    } else {
                        property = super.resolveInlineProperty(type, expr + ":{opt(\"" + expr + "\")|put(\"" + expr + "\",%s)|type="
                                + (isNestedLookup ? "org.codehaus.jettison.json.JSONObject" : "Object") + "}");
                    }
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
            String s = source.toString();
            if (!s.startsWith("http")) {
                s += "http://";
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
