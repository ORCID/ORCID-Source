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
package org.orcid.pojo;

/**
 * 
 * @author Will Simpson
 *
 */
public class HeaderMismatch {

    private String headerName;
    private String originalValue;
    private String currentValue;

    public HeaderMismatch(String headerName, String originalValue, String currentValue) {
        this.headerName = headerName;
        this.originalValue = originalValue;
        this.currentValue = currentValue;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    @Override
    public String toString() {
        return "HeaderMismatch [headerName=" + headerName + ", originalValue=" + originalValue + ", currentValue=" + currentValue + "]";
    }

}
