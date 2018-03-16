package org.orcid.pojo.ajaxForm;

import java.io.Serializable;

public class ScopeInfoForm implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    private String description;
    private String longDescription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }        
}
