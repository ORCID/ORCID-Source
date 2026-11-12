package org.orcid.api.common.openapi;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

public class OrcidObjectMapperProcessorTest {

    @XmlRootElement(name = "works")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class Sample {
        @XmlElement(name = "last-modified-date")
        public String lastModifiedDate = "x";
    }

    @Test
    public void propertyNamesComeFromJaxbAnnotations() throws Exception {
        ObjectMapper mapper = processed();
        String json = mapper.writeValueAsString(new Sample());
        assertTrue(json, json.contains("\"last-modified-date\""));
    }

    @Test
    public void xmlRootElementNamesAreNotUsedAsSchemaNames() {
        ObjectMapper mapper = processed();
        SerializationConfig config = mapper.getSerializationConfig();
        BeanDescription description = config.introspectClassAnnotations(Sample.class);
        assertNull(config.getAnnotationIntrospector().findRootName(description.getClassInfo()));
    }

    private static ObjectMapper processed() {
        ObjectMapper mapper = new ObjectMapper();
        new OrcidObjectMapperProcessor().processJsonObjectMapper(mapper);
        return mapper;
    }
}
