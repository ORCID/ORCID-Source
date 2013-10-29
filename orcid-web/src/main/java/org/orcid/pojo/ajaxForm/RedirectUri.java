package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.RedirectUriType;

public class RedirectUri implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 2L;
    
    private List<String> errors = new ArrayList<String>();    
    protected Text value;
    private Text type;
    
    public static RedirectUri toRedirectUri(org.orcid.jaxb.model.clientgroup.RedirectUri orcidRedirectUri){
        RedirectUri redirectUri = new RedirectUri();
        redirectUri.setType(Text.valueOf(orcidRedirectUri.getType().value()));
        redirectUri.setValue(Text.valueOf(orcidRedirectUri.getValue()));
        return redirectUri;
    }
    
    public org.orcid.jaxb.model.clientgroup.RedirectUri toRedirectUri(){
        org.orcid.jaxb.model.clientgroup.RedirectUri orcidRedirectUri = new org.orcid.jaxb.model.clientgroup.RedirectUri();
        orcidRedirectUri.setValue(this.value.getValue());
        if(this.type != null && this.type.getValue() != null)
            orcidRedirectUri.setType(RedirectUriType.fromValue(this.type.getValue()));
        return orcidRedirectUri;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    public Text getValue() {
        return value;
    }
    public void setValue(Text value) {
        this.value = value;
    }
    public Text getType() {
        return type;
    }
    public void setType(Text type) {
        this.type = type;
    }        
}
