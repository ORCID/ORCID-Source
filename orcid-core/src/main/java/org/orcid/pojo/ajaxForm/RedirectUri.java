/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ScopePathType;

public class RedirectUri implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 2L;
    
    private List<String> errors = new ArrayList<String>(); 
    private List<String> scopes = new ArrayList<String>();
    protected Text value;
    private Text type;
    
    public static RedirectUri toRedirectUri(org.orcid.jaxb.model.clientgroup.RedirectUri orcidRedirectUri){
        RedirectUri redirectUri = new RedirectUri();
        redirectUri.setType(Text.valueOf(orcidRedirectUri.getType().value()));
        redirectUri.setValue(Text.valueOf(orcidRedirectUri.getValue()));
        if(orcidRedirectUri.getScope() != null) {
            List<String> scopes = redirectUri.getScopes();
            for(ScopePathType scope : orcidRedirectUri.getScope()) {
                scopes.add(scope.value());
            }
        }
        return redirectUri;
    }
    
    public org.orcid.jaxb.model.clientgroup.RedirectUri toRedirectUri(){
        org.orcid.jaxb.model.clientgroup.RedirectUri orcidRedirectUri = new org.orcid.jaxb.model.clientgroup.RedirectUri();
        orcidRedirectUri.setValue(this.value.getValue());
        if(this.type != null && this.type.getValue() != null)
            orcidRedirectUri.setType(RedirectUriType.fromValue(this.type.getValue()));
        if(this.scopes != null){
            List<ScopePathType> scopes = new ArrayList<ScopePathType>();
            for(String scope : this.scopes) {
                scopes.add(ScopePathType.fromValue(scope));
            }
            
            orcidRedirectUri.setScope(scopes);
        }
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
    public List<String> getScopes() {
        return scopes;
    }
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    } 
}
