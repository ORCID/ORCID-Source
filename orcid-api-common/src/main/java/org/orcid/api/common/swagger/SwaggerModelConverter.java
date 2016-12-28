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
package org.orcid.api.common.swagger;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.JavaType;

import io.swagger.converter.ModelConverter;
import io.swagger.converter.ModelConverterContext;
import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import io.swagger.util.Json;

/**
 * Manipulates models for serialization into Swagger Schema
 * 
 * @author tom
 *
 */
public class SwaggerModelConverter implements ModelConverter {

    /**
     * Does nothing but pass through the chain
     */
    @Override
    public Model resolve(Type type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain);
        } else {
            return null;
        }
    }

    /**
     * Replace Visibility entities with our own more limited and better ordered
     * version
     */
    @Override
    public Property resolveProperty(Type type, ModelConverterContext context, Annotation[] annotations, Iterator<ModelConverter> chain) {
        JavaType _type = Json.mapper().constructType(type);
        if (_type != null) {
            Class<?> cls = _type.getRawClass();
            if (org.orcid.jaxb.model.common_v2.Visibility.class.isAssignableFrom(cls)) {
                return context.resolveProperty(Visibility.class, null);
            }
            if (org.orcid.jaxb.model.common_rc4.Visibility.class.isAssignableFrom(cls)) {
                return context.resolveProperty(Visibility.class, null);
            }
        }
        if (chain.hasNext()) {
            return chain.next().resolveProperty(type, context, annotations, chain);
        } else {
            return null;
        }
    }

    /**
     * Abridged Visibility XML enumeration for swagger use
     */
    @XmlType(name = "visibility")
    @XmlEnum
    public enum Visibility implements Serializable {

        /**
         * The PROTECTED should only be shared with systems that the researcher
         * or contributor has specifically granted authorization (using OAuth).
         */
        @XmlEnumValue("limited") LIMITED("limited"),

        /**
         * The org.orcid.test.data should be shared only with registered users.
         */
        @XmlEnumValue("registered-only") REGISTERED_ONLY("registered_only"),

        /**
         * The PUBLIC should be publicly available.
         */
        @XmlEnumValue("public") PUBLIC("public");

        private final String value;

        Visibility(String v) {
            value = v;
        }

        public String value() {
            return value;
        }

    }

}
