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
package org.orcid.core.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.utils.OrcidStringUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * 
 * @author Will Simpson
 * 
 */
public class JsonUtils {

    static ObjectMapper mapper = new ObjectMapper(); // thread safe!
    static ObjectMapper mapperFromJSON = new ObjectMapper(); // thread safe!
    static {
        mapper.registerModule(new InvalidCharactersFilterModule());
        mapperFromJSON.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public static String convertToJsonString(Object object) {
        try {            
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readObjectFromJsonString(String jsonString, Class<T> clazz) {
        try {
            return mapperFromJSON.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject extractObject(JSONObject parent, String key) {
        if (parent.isNull(key)) {
            return null;
        }
        try {
            return parent.getJSONObject(key);
        } catch (JSONException e) {
            throw new RuntimeException("Error extracting json object", e);
        }
    }

    public static String extractString(JSONObject record, String key) {
        if (record.isNull(key)) {
            return null;
        }
        try {
            return record.getString(key);
        } catch (JSONException e) {
            throw new RuntimeException("Error extracting string from json", e);
        }
    }

    public static int extractInt(JSONObject record, String key) {
        if (record.isNull(key)) {
            return -1;
        }
        try {
            return record.getInt(key);
        } catch (JSONException e) {
            throw new RuntimeException("Error extracting int from json", e);
        }
    }
    
    public static Boolean extractBoolean(JSONObject record, String key) {
        //this should not happen
        if (record.isNull(key)) {
            return null;
        }
        try {
            return record.getBoolean(key);
        } catch (JSONException e) {
            throw new RuntimeException("Error extracting boolean from json", e);
        }
    }
    
    public static JsonNode read(File file) {
        try {
            return mapper.readTree(file);
        } catch (IOException e) {
            throw new RuntimeException("Error extracting JsonNode from file", e);
        }
    }    
}

class InvalidCharactersFilterModule extends SimpleModule {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public InvalidCharactersFilterModule() {
        addSerializer(new StdSerializer<String>(String.class) {
            private static final long serialVersionUID = 1L;

            @Override
            public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
                jgen.writeString(OrcidStringUtils.filterInvalidXMLCharacters(value));
            }
        });
    }
}