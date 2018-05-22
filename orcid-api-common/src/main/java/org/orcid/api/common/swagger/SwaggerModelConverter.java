package org.orcid.api.common.swagger;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.record_v2.CitationType;

import com.fasterxml.jackson.annotation.JsonValue;
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
            if (    org.orcid.jaxb.model.common_rc1.Visibility.class.isAssignableFrom(cls) ||
                    org.orcid.jaxb.model.common_rc2.Visibility.class.isAssignableFrom(cls) ||
                    org.orcid.jaxb.model.common_rc3.Visibility.class.isAssignableFrom(cls) ||
                    org.orcid.jaxb.model.common_rc4.Visibility.class.isAssignableFrom(cls) || 
                    org.orcid.jaxb.model.common_v2.Visibility.class.isAssignableFrom(cls) ||
                    org.orcid.jaxb.model.v3.rc1.common.Visibility.class.isAssignableFrom(cls)) {
                return context.resolveProperty(Visibility.class, null);
            }
            if (    org.orcid.jaxb.model.record_rc1.Citation.class.isAssignableFrom(cls) ||
                    org.orcid.jaxb.model.record_rc2.Citation.class.isAssignableFrom(cls) ||
                    org.orcid.jaxb.model.record_rc3.Citation.class.isAssignableFrom(cls) ||
                    org.orcid.jaxb.model.record_rc4.Citation.class.isAssignableFrom(cls) || 
                    org.orcid.jaxb.model.record_v2.Citation.class.isAssignableFrom(cls) ||
                    org.orcid.jaxb.model.v3.rc1.record.Citation.class.isAssignableFrom(cls)) {
                return context.resolveProperty(Citation.class, null);
            }
            if (_type.getRawClass() != null && _type.getRawClass().getCanonicalName().contains("model.v3")){
                //do not use v3 models.  Note this is brittle and depends on package name.
                return context.resolveProperty(Object.class, null);
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
        @XmlEnumValue("public") PUBLIC("public"),

        /**
         * The PRIVATE should be available to the source only
         */
        @XmlEnumValue("private") PRIVATE("private");

        private final String value;

        Visibility(String v) {
            value = v;
        }

        public String value() {
            return value;
        }
        
        @JsonValue
        public String jsonValue() {
            return this.name();
        }

    }
    
    /**
     * Citation XML enumeration for swagger use - minus the default value
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "citation", propOrder = { "citationType", "citation" })
    public class Citation implements Serializable {
        private static final long serialVersionUID = 1L;
        @XmlElement(name = "citation-type", required = true, namespace = "http://www.orcid.org/ns/work")
        protected CitationType citationType;
        @XmlElement(name = "citation-value", required = true, namespace = "http://www.orcid.org/ns/work")
        protected String citation;

        public Citation() {
        }

        public Citation(String citation, CitationType citationType) {
            this.citation = citation;
            this.citationType = citationType;
        }

        /**
         * Gets the value of the workCitationType property.
         *
         * @return possible object is {@link CitationType }
         */
        public CitationType getWorkCitationType() {
            return citationType;
        }

        /**
         * Sets the value of the workCitationType property.
         *
         * @param value
         *            allowed object is {@link CitationType }
         */
        public void setWorkCitationType(CitationType value) {
            this.citationType = value;
        }

        /**
         * Gets the value of the citation property.
         *
         * @return possible object is {@link Citation }
         */
        public String getCitation() {
            return citation;
        }

        /**
         * Sets the value of the citation property.
         *
         * @param value
         *            allowed object is {@link Citation }
         */
        public void setCitation(String value) {
            this.citation = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Citation)) {
                return false;
            }

            Citation citation1 = (Citation) o;

            if (citation != null ? !citation.equals(citation1.citation) : citation1.citation != null) {
                return false;
            }
            if (citationType != citation1.citationType) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = citationType != null ? citationType.hashCode() : 0;
            result = 31 * result + (citation != null ? citation.hashCode() : 0);
            return result;
        }
    }

}
