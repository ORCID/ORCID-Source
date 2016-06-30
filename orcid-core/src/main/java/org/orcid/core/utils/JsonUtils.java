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

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author Will Simpson
 * 
 */
public class JsonUtils {

    static ObjectMapper mapper = new ObjectMapper(); //thread safe!
    static ObjectMapper mapperFromJSON = new ObjectMapper(); //thread safe!
    static {
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
}
