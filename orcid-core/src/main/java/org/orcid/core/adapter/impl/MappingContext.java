package org.orcid.core.adapter.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple context holder for mapping operations.
 * Replaces Orika's MappingContext with minimal functionality needed by MapperFacadeSupport.
 * 
 * Supports storing arbitrary properties during mapping for context-dependent logic.
 */
public class MappingContext {
    
    private final Map<Object, Object> properties = new HashMap<>();
    
    /**
     * Set a property in the mapping context.
     * 
     * @param key the property key
     * @param value the property value
     */
    public void setProperty(Object key, Object value) {
        if (key != null) {
            properties.put(key, value);
        }
    }
    
    /**
     * Get a property from the mapping context.
     * 
     * @param key the property key
     * @return the property value, or null if not found
     */
    public Object getProperty(Object key) {
        return properties.get(key);
    }
    
    /**
     * Get a property from the mapping context with a default value.
     * 
     * @param key the property key
     * @param defaultValue the default value if property not found
     * @return the property value, or defaultValue if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(Object key, T defaultValue) {
        Object value = properties.get(key);
        return value != null ? (T) value : defaultValue;
    }
    
    /**
     * Check if a property exists in the context.
     * 
     * @param key the property key
     * @return true if the property exists, false otherwise
     */
    public boolean hasProperty(Object key) {
        return properties.containsKey(key);
    }
    
    /**
     * Clear all properties from the context.
     */
    public void clear() {
        properties.clear();
    }
}
