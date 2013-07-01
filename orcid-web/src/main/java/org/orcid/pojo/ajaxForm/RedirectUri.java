package org.orcid.pojo.ajaxForm;

import org.orcid.jaxb.model.clientgroup.RedirectUriType;

public class RedirectUri {
    String value;
    RedirectUriType type;
    
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public RedirectUriType getType() {
        return type;
    }

    public void setType(RedirectUriType type) {
        this.type = type;
    }        
    
    
}
