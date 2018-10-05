package org.orcid.core.version;

/**
 * 
 * @author Will Simpson
 *
 */
public class V3Convertible {

    private String currentVersion;
    private Object objectToConvert;
    
    public V3Convertible(Object objectToConvert, String currentVersion) {
        this.objectToConvert = objectToConvert;
        this.currentVersion = currentVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Object getObjectToConvert() {
        return objectToConvert;
    }

    public void setObjectToConvert(Object objectToConvert) {
        this.objectToConvert = objectToConvert;
    };
    
}
