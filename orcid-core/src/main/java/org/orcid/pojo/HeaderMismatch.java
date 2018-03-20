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
