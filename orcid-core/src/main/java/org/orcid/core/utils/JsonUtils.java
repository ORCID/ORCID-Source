package org.orcid.core.utils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * @author Will Simpson
 * 
 */
public class JsonUtils {

    static ObjectMapper mapper = new ObjectMapper(); // thread safe!
    static ObjectMapper prettyMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    static ObjectMapper mapperFromJSON = new ObjectMapper(); // thread safe!
    static {        
        mapperFromJSON.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }
    
    public static String convertToJsonStringPrettyPrint(Object object) {
        try {            
            return prettyMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static JsonNode readTree(String jsonString) {
        try {
            return mapperFromJSON.readTree(jsonString);
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
    
    public static JsonNode read(Reader reader) {
        try {
            return mapper.readTree(reader);
        } catch (IOException e) {
            throw new RuntimeException("Error extracting JsonNode from file", e);
        }
    }
}