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

import java.net.URL;

import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.salesforce.model.Member;
import org.springframework.beans.factory.FactoryBean;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
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

        ClassMapBuilder<Member, JSONObject> classMap = mapperFactory.classMap(Member.class, JSONObject.class).mapNulls(false).mapNullsInReverse(false);
        classMap.field("id", "Id");
        classMap.field("name", "Name");
        classMap.field("websiteUrl", "Website");
        classMap.field("researchCommunity", "Research_Community__c");
        classMap.field("country", "BillingCountry");
        classMap.field("description", "Public_Display_Description__c");
        classMap.field("logoUrl", "Logo_Description__c");
        classMap.field("publicDisplayEmail", "Public_Display_Email__c");
        classMap.field("mainOpportunityPath", "Opportunities.records_array.first.attributes.url");
        classMap.field("consortiumLeadId", "Opportunities.records_array.first.Consortia_Lead__c");
        classMap.register();
        return mapperFactory.getMapperFacade();
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

}
