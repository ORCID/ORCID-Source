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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUris;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;

public class Client implements ErrorsInterface, Serializable {

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

    public static Client valueOf(ClientDetailsEntity clientDetails) {
        Client client = new Client();
        if (clientDetails != null) {
            client.setClientId(Text.valueOf(clientDetails.getClientId()));
            client.setDisplayName(Text.valueOf(clientDetails.getClientName()));
            client.setShortDescription(Text.valueOf(clientDetails.getClientDescription()));
            client.setWebsite(Text.valueOf(clientDetails.getClientWebsite()));                               
            client.redirectUris = new ArrayList<RedirectUri>();
            if (clientDetails.getClientRegisteredRedirectUris() != null) {
                for (ClientRedirectUriEntity rUri : clientDetails.getClientRegisteredRedirectUris()) {
                    client.redirectUris.add(RedirectUri.valueOf(rUri));
                }
            }            
            
            client.persistentTokenEnabled = new Checkbox();
            client.persistentTokenEnabled.setValue(clientDetails.isPersistentTokensEnabled());     
            if(clientDetails.getClientType() != null)
            client.setType(Text.valueOf(clientDetails.getClientType().value()));
            
            if(clientDetails.isScoped())
                client.setScopes(clientDetails.getScope());
            
            client.setMemberId(Text.valueOf(clientDetails.getGroupProfileId()));
            if(!PojoUtil.isEmpty(clientDetails.getAuthenticationProviderId())) {
                client.setAuthenticationProviderId(Text.valueOf(clientDetails.getAuthenticationProviderId()));
            }
            client.setAllowAutoDeprecate(Checkbox.valueOf(clientDetails.getAllowAutoDeprecate()));
        }
        return client;
    }
    
    public static List<Client> valueOf(List<ClientDetailsEntity> clientDetails) {
        List<Client> clients = new ArrayList<Client>();
        for(ClientDetailsEntity entity : clientDetails) {
            clients.add(Client.valueOf(entity));
        }
        return clients;
    }

    public static Client valueOf(OrcidClient orcidClient) {
        Client client = new Client();
        client.setClientId(Text.valueOf(orcidClient.getClientId()));
        client.setClientSecret(Text.valueOf(orcidClient.getClientSecret()));
        client.setDisplayName(Text.valueOf(orcidClient.getDisplayName()));
        client.setShortDescription(Text.valueOf(orcidClient.getShortDescription()));
        if (orcidClient.getType() != null)
            client.setType(Text.valueOf(orcidClient.getType().value()));
        client.setWebsite(Text.valueOf(orcidClient.getWebsite()));
        
        Checkbox persistentTokenEnabled = new Checkbox();
        persistentTokenEnabled.setValue(orcidClient.isPersistentTokenEnabled());        
        client.setPersistentTokenEnabled(persistentTokenEnabled);
        
        List<RedirectUri> redirectUris = new ArrayList<RedirectUri>();
        RedirectUris orcidRedirectUris = orcidClient.getRedirectUris();
        if (orcidRedirectUris != null && orcidRedirectUris.getRedirectUri() != null) {
            for (org.orcid.jaxb.model.clientgroup.RedirectUri orcidRedirectUri : orcidRedirectUris.getRedirectUri()) {
                redirectUris.add(RedirectUri.toRedirectUri(orcidRedirectUri));
            }
        }

        if(orcidClient.getIdp() != null) {
            client.setAuthenticationProviderId(Text.valueOf(orcidClient.getIdp()));
        }
        
        client.setRedirectUris(redirectUris);
        client.setAllowAutoDeprecate(Checkbox.valueOf(orcidClient.getAllowAutoDeprecate()));
        return client;
    }

    public OrcidClient toOrcidClient() {
        OrcidClient orcidClient = new OrcidClient();
        orcidClient.setDisplayName(this.displayName.getValue());
        orcidClient.setWebsite(this.website.getValue());
        orcidClient.setShortDescription(this.shortDescription.getValue());
        orcidClient.setClientId(this.clientId.getValue());
        if(this.getAuthenticationProviderId() != null) {
            orcidClient.setIdp(this.getAuthenticationProviderId().getValue());
        }
        if (!PojoUtil.isEmpty(this.clientSecret))
            orcidClient.setClientSecret(this.clientSecret.getValue());
        if (!PojoUtil.isEmpty(this.type))
            orcidClient.setType(ClientType.fromValue(this.type.getValue()));

        RedirectUris redirectUris = new RedirectUris();

        for (RedirectUri redirectUri : this.redirectUris) {
            redirectUris.getRedirectUri().add(redirectUri.toRedirectUri());
        }

        orcidClient.setRedirectUris(redirectUris);
        
        if(persistentTokenEnabled != null)
            orcidClient.setPersistentTokenEnabled(persistentTokenEnabled.getValue());
        
        orcidClient.setAllowAutoDeprecate(this.getAllowAutoDeprecate() == null ? false : this.getAllowAutoDeprecate().getValue());
        
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
}
