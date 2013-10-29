package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUris;

public class Client implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 2L;
    
    private List<String> errors = new ArrayList<String>();
    private Text displayName;
    private Text website;
    private Text shortDescription;
    private Text clientId;
    private Text clientSecret;
    private Text type; 
    private List<RedirectUri> redirectUris;
    
    public static Client valueOf(OrcidClient orcidClient){
        Client client = new Client();   
        client.setClientId(Text.valueOf(orcidClient.getClientId()));
        client.setClientSecret(Text.valueOf(orcidClient.getClientSecret()));
        client.setDisplayName(Text.valueOf(orcidClient.getDisplayName()));
        client.setShortDescription(Text.valueOf(orcidClient.getShortDescription()));
        client.setType(Text.valueOf(orcidClient.getType().value()));
        client.setWebsite(Text.valueOf(orcidClient.getWebsite()));
        
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUris orcidRedirectUris = orcidClient.getRedirectUris();        
        if(orcidRedirectUris != null && orcidRedirectUris.getRedirectUri() != null){
            for(org.orcid.jaxb.model.clientgroup.RedirectUri orcidRedirectUri : orcidRedirectUris.getRedirectUri()){
                redirectUris.add(RedirectUri.toRedirectUri(orcidRedirectUri));
            }
        }
                
        return client;
    }
    
    public OrcidClient toOrcidClient(){
        OrcidClient orcidClient = new OrcidClient();
        orcidClient.setDisplayName(this.displayName.getValue());
        orcidClient.setWebsite(this.website.getValue());
        orcidClient.setShortDescription(this.shortDescription.getValue());
        orcidClient.setClientId(this.clientId.getValue());
        orcidClient.setClientSecret(this.clientSecret.getValue());
        orcidClient.setType(ClientType.valueOf(this.type.getValue()));
        RedirectUris redirectUris = new RedirectUris();
        
        for(RedirectUri redirectUri : this.redirectUris){
            redirectUris.getRedirectUri().add(redirectUri.toRedirectUri());
        }
        
        return orcidClient;
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
}
