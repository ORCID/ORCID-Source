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
package org.orcid.core.version;

/**
 * 
 * @author Will Simpson
 *
 */
public class V2Convertible {

    private String currentVersion;
    private Object objectToConvert;
    
    public V2Convertible(Object objectToConvert, String currentVersion) {
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
