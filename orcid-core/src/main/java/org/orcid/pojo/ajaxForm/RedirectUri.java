package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.client.ClientRedirectUri;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;

public class RedirectUri implements ErrorsInterface, Serializable, Comparable<RedirectUri> {

    private static final long serialVersionUID = 2L;
    
    private List<String> errors = new ArrayList<String>(); 
    private List<String> scopes = new ArrayList<String>();
    protected Text value;
    private Text type;
    private Text actType;
    private Text geoArea;
    
    @Deprecated
    public static RedirectUri valueOf(ClientRedirectUriEntity rUri) {
        RedirectUri redirectUri = new RedirectUri();
        redirectUri.setValue(Text.valueOf(rUri.getRedirectUri()));
        redirectUri.setType(Text.valueOf(rUri.getRedirectUriType()));
         
        if(!PojoUtil.isEmpty(rUri.getPredefinedClientScope())) {
            for(String scope : rUri.getPredefinedClientScope().split(" ")) {
                redirectUri.getScopes().add(scope);
            }
        }
        
        redirectUri.setActType(Text.valueOf(rUri.getUriActType()));
        redirectUri.setGeoArea(Text.valueOf(rUri.getUriGeoArea()));
        return redirectUri;
    }
    
    @Deprecated
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
        redirectUri.setActType(Text.valueOf(orcidRedirectUri.getActType()));
        redirectUri.setGeoArea(Text.valueOf(orcidRedirectUri.getGeoArea()));
        return redirectUri;
    }
    
    @Deprecated
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
        if(this.actType != null) {
        	orcidRedirectUri.setActType(this.actType.getValue());
        }
        if(this.geoArea != null) {
        	orcidRedirectUri.setGeoArea(this.geoArea.getValue());
        }
        return orcidRedirectUri;
    }
    
    public static RedirectUri fromModelObject(ClientRedirectUri modelObject) {
        RedirectUri redirectUri = new RedirectUri();
        if(modelObject.getPredefinedClientScopes() != null) {
            for(ScopePathType scope : modelObject.getPredefinedClientScopes()) {
                redirectUri.getScopes().add(scope.value());
            }
        }
        
        redirectUri.setValue(Text.valueOf(modelObject.getRedirectUri()));
        
        redirectUri.setType(Text.valueOf(modelObject.getRedirectUriType()));
        
        redirectUri.setActType(Text.valueOf(modelObject.getUriActType()));
        
        redirectUri.setGeoArea(Text.valueOf(modelObject.getUriGeoArea()));
        return redirectUri;
    }
    
    public static RedirectUri fromModelObject(org.orcid.jaxb.model.client_v2.ClientRedirectUri modelObject) {
        RedirectUri redirectUri = new RedirectUri();
        if(modelObject.getPredefinedClientScopes() != null) {
            for(ScopePathType scope : modelObject.getPredefinedClientScopes()) {
                redirectUri.getScopes().add(scope.value());
            }
        }
        
        redirectUri.setValue(Text.valueOf(modelObject.getRedirectUri()));
        
        redirectUri.setType(Text.valueOf(modelObject.getRedirectUriType()));
        
        redirectUri.setActType(Text.valueOf(modelObject.getUriActType()));
        
        redirectUri.setGeoArea(Text.valueOf(modelObject.getUriGeoArea()));
        return redirectUri;
    }
    
    public ClientRedirectUri toModelObject() {
        ClientRedirectUri element = new ClientRedirectUri();
        if(this.scopes != null) {
            Set<ScopePathType> scopesSet = new HashSet<ScopePathType>();
            for(String scope : this.scopes) {
                scopesSet.add(ScopePathType.fromValue(scope));
            }
            element.setPredefinedClientScopes(scopesSet);
        }
        
        if(!PojoUtil.isEmpty(this.value)) {
            element.setRedirectUri(this.value.getValue());
        }        
        
        if(!PojoUtil.isEmpty(this.type)) {
            element.setRedirectUriType(this.type.getValue());
        }        
        
        if(!PojoUtil.isEmpty(this.actType)) {
            element.setUriActType(this.actType.getValue());
        }        
        
        if(!PojoUtil.isEmpty(this.geoArea)) {
            element.setUriGeoArea(this.geoArea.getValue());
        }
        
        return element;
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

    @Override
    public int compareTo(RedirectUri other) {
        if(other == null) {
            return 1;
        } else {
            if(PojoUtil.isEmpty(this.value)) {
                if(PojoUtil.isEmpty(other.getValue()))
                    return 0;
                else 
                    return -1;
            } else {
                String s1 = this.value.getValue();
                String s2 = PojoUtil.isEmpty(other.getValue()) ? "" : other.getValue().getValue();
                return s1.compareTo(s2);
            }
        }
    }

	public Text getActType() {
		return actType;
	}

	public void setActType(Text actType) {
		this.actType = actType;
	}

	public Text getGeoArea() {
		return geoArea;
	}

	public void setGeoArea(Text geoArea) {
		this.geoArea = geoArea;
	}         
}
