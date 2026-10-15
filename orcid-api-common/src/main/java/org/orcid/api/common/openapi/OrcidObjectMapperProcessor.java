package org.orcid.api.common.openapi;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector;

import io.swagger.v3.oas.integration.api.ObjectMapperProcessor;

/**
 * Used only by the OpenAPI generator (swagger-maven-plugin-jakarta), never at
 * runtime.
 *
 * The API serialises the model with {@code OrcidJacksonJaxbJsonProvider}, so
 * JSON property names come from JAXB annotations. The generator has to read the
 * model the same way, or every property is named after its Java field.
 *
 * JAXB would also make the generator name every referenced schema after its XML
 * root element, and several model classes share an element name (both
 * {@code Works} are {@code works}). Root names are therefore switched off, so
 * schema names come from {@code @Schema(name)}.
 */
public class OrcidObjectMapperProcessor implements ObjectMapperProcessor {

    @Override
    public void processJsonObjectMapper(ObjectMapper mapper) {
        AnnotationIntrospector jackson = mapper.getSerializationConfig().getAnnotationIntrospector();
        mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(jackson, new JaxbWithoutRootNames(mapper.getTypeFactory())));
    }

    static class JaxbWithoutRootNames extends JakartaXmlBindAnnotationIntrospector {

        private static final long serialVersionUID = 1L;

        JaxbWithoutRootNames(TypeFactory typeFactory) {
            super(typeFactory);
        }

        @Override
        public PropertyName findRootName(AnnotatedClass ac) {
            return null;
        }
    }
}
