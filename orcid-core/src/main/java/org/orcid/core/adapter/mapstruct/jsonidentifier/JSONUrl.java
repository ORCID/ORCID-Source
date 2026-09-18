package org.orcid.core.adapter.mapstruct.jsonidentifier;

public class JSONUrl {
    
    private String value;
    
    public JSONUrl() {
        
    }
    
    public JSONUrl(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
