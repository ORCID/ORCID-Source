package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.client.ClientRedirectUri;

public class Client implements ErrorsInterface, Serializable, Comparable<Client> {

    private static final long serialVersionUID = 2L;

    private List<String> errors = new ArrayList<String>();
    private Text displayName;
    private Text website;
    private Text shortDescription;
    private Text clientId;
    private Text clientSecret;
    private Text type; 
    private Text memberId;
    private Text memberName;
    private Text authenticationProviderId;
    private Checkbox persistentTokenEnabled;
    private List<RedirectUri> redirectUris;    
    private Set<String> scopes;
    private Checkbox allowAutoDeprecate;    
        
    public static Client fromModelObject(org.orcid.jaxb.model.v3.rc2.client.Client modelObject) {
        Client client = new Client();

        client.setClientId(Text.valueOf(modelObject.getId()));

        client.setAllowAutoDeprecate(Checkbox.valueOf(modelObject.isAllowAutoDeprecate()));
        
        client.setPersistentTokenEnabled(Checkbox.valueOf(modelObject.isPersistentTokensEnabled()));
        
        if (modelObject.getAuthenticationProviderId() != null) {
            client.setAuthenticationProviderId(Text.valueOf(modelObject.getAuthenticationProviderId()));
        }

        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        if(modelObject.getClientRedirectUris() != null) {
            for (ClientRedirectUri element : modelObject.getClientRedirectUris()) {
                RedirectUri rUri = RedirectUri.fromModelObject(element);
                redirectUris.add(rUri);
            }
        }
        client.setRedirectUris(redirectUris);        
        client.setType(Text.valueOf(modelObject.getClientType().value()));
        client.setClientSecret(Text.valueOf(modelObject.getDecryptedSecret()));
        client.setShortDescription(Text.valueOf(modelObject.getDescription()));
        client.setMemberId(Text.valueOf(modelObject.getGroupProfileId()));
        client.setDisplayName(Text.valueOf(modelObject.getName()));
        client.setWebsite(Text.valueOf(modelObject.getWebsite()));

        return client;
    }
    
    public static Client fromModelObject(org.orcid.jaxb.model.client_v2.Client modelObject) {
        Client client = new Client();

        client.setClientId(Text.valueOf(modelObject.getId()));

        client.setAllowAutoDeprecate(Checkbox.valueOf(modelObject.isAllowAutoDeprecate()));
        
        client.setPersistentTokenEnabled(Checkbox.valueOf(modelObject.isPersistentTokensEnabled()));
        
        if (modelObject.getAuthenticationProviderId() != null) {
            client.setAuthenticationProviderId(Text.valueOf(modelObject.getAuthenticationProviderId()));
        }

        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        if(modelObject.getClientRedirectUris() != null) {
            for (org.orcid.jaxb.model.client_v2.ClientRedirectUri element : modelObject.getClientRedirectUris()) {
                RedirectUri rUri = RedirectUri.fromModelObject(element);
                redirectUris.add(rUri);
            }
        }
        client.setRedirectUris(redirectUris);        
        client.setType(Text.valueOf(modelObject.getClientType().value()));
        client.setClientSecret(Text.valueOf(modelObject.getDecryptedSecret()));
        client.setShortDescription(Text.valueOf(modelObject.getDescription()));
        client.setMemberId(Text.valueOf(modelObject.getGroupProfileId()));
        client.setDisplayName(Text.valueOf(modelObject.getName()));
        client.setWebsite(Text.valueOf(modelObject.getWebsite()));

        return client;
    }
    
    public org.orcid.jaxb.model.v3.rc2.client.Client toModelObject() {
        org.orcid.jaxb.model.v3.rc2.client.Client modelObject = new org.orcid.jaxb.model.v3.rc2.client.Client();

        if (this.getAllowAutoDeprecate() != null) {
            modelObject.setAllowAutoDeprecate(this.getAllowAutoDeprecate().getValue());
        }
        
        if (this.getAuthenticationProviderId() != null) {
            modelObject.setAuthenticationProviderId(this.getAuthenticationProviderId().getValue());
        }

        if (this.getClientId() != null) {
            modelObject.setId(this.getClientId().getValue());
        }

        if (this.getDisplayName() != null) {
            modelObject.setName(this.getDisplayName().getValue());
        }

        if (this.getMemberId() != null) {
            modelObject.setGroupProfileId(this.getMemberId().getValue());
        }

        if (this.getPersistentTokenEnabled() != null) {
            modelObject.setPersistentTokensEnabled(this.getPersistentTokenEnabled().getValue());
        }

        if (this.getRedirectUris() != null) {
            Set<ClientRedirectUri> redirectUriSet = new HashSet<ClientRedirectUri>();
            for(RedirectUri rUri : this.getRedirectUris()) {
                ClientRedirectUri redirectUri = new ClientRedirectUri();
                if(rUri.getScopes() != null) {
                    Set<ScopePathType> scopes = new HashSet<ScopePathType>();
                    for(String scope : rUri.getScopes()) {
                        scopes.add(ScopePathType.fromValue(scope));
                    }
                    redirectUri.setPredefinedClientScopes(scopes);
                }
                redirectUri.setRedirectUri(rUri.getValue().getValue());
                redirectUri.setRedirectUriType(rUri.getType().getValue());
                if(!PojoUtil.isEmpty(rUri.getActType())) {
                    redirectUri.setUriActType(rUri.getActType().getValue());
                }
                if(!PojoUtil.isEmpty(rUri.getGeoArea())) {
                    redirectUri.setUriGeoArea(rUri.getGeoArea().getValue());
                }
                redirectUri.setStatus(rUri.getStatus());
                redirectUriSet.add(redirectUri);
            }
            modelObject.setClientRedirectUris(redirectUriSet);
        }

        if (this.getShortDescription() != null) {
            modelObject.setDescription(this.getShortDescription().getValue());
        }

        if (this.getWebsite() != null) {
            modelObject.setWebsite(this.getWebsite().getValue());
        }
        return modelObject;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Text displayName) {
        this.displayName = displayName;
    }

    public Text getWebsite() {
        return website;
    }

    public void setWebsite(Text website) {
        this.website = website;
    }

    public Text getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Text shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Text getClientId() {
        return clientId;
    }

    public void setClientId(Text clientId) {
        this.clientId = clientId;
    }

    public Text getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(Text clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Text getType() {
        return type;
    }

    public void setType(Text type) {
        this.type = type;
    }

    public List<RedirectUri> getRedirectUris() {
        return redirectUris;
    }

    public void setRedirectUris(List<RedirectUri> redirectUris) {
        this.redirectUris = redirectUris;
    }

    public Checkbox getPersistentTokenEnabled() {
        return persistentTokenEnabled;
    }

    public void setPersistentTokenEnabled(Checkbox persistentTokenEnabled) {
        this.persistentTokenEnabled = persistentTokenEnabled;
    }

    public void setScopes(Set<String> scopes) {
        this.scopes = scopes;
    }
    
    public Set<String> getScopes() {
        return scopes;
    }

    public Text getMemberId() {
        return memberId;
    }

    public void setMemberId(Text memberId) {
        this.memberId = memberId;
    }

    public Text getMemberName() {
        return memberName;
    }

    public void setMemberName(Text memberName) {
        this.memberName = memberName;
    }

    public Text getAuthenticationProviderId() {
        return authenticationProviderId;
    }

    public void setAuthenticationProviderId(Text authenticationProviderId) {
        this.authenticationProviderId = authenticationProviderId;
    }
    
    public Checkbox getAllowAutoDeprecate() {
        return allowAutoDeprecate;
    }

    public void setAllowAutoDeprecate(Checkbox allowAutoDeprecate) {
        this.allowAutoDeprecate = allowAutoDeprecate;
    }

    @Override
    public int compareTo(Client other) {
        if (other == null) {
            return 1;
        } else {
            if (PojoUtil.isEmpty(this.displayName)) {
                if (!PojoUtil.isEmpty(other.getDisplayName())) {
                    return -1;
                }
            } else {
                if (PojoUtil.isEmpty(other.getDisplayName())) {
                    return 1;
                } else {
                    int compare = this.getDisplayName().compareTo(other.getDisplayName());
                    if (compare != 0) {
                        return compare;
                    }
                }
            }

            if (PojoUtil.isEmpty(this.shortDescription)) {
                if (!PojoUtil.isEmpty(other.getShortDescription())) {
                    return -1;
                }
            } else {
                if (PojoUtil.isEmpty(other.getShortDescription())) {
                    return 1;
                } else {
                    int compare = this.getShortDescription().compareTo(other.getShortDescription());
                    if (compare != 0) {
                        return compare;
                    }
                }
            }
        }
        return 0;
    }
}
