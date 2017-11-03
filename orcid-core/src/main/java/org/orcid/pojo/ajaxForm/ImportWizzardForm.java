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
import java.util.stream.Collectors;

import org.orcid.jaxb.model.v3.dev1.client.ClientRedirectUri;

public class ImportWizzardForm implements Serializable {
    
    class ImportWizzardClientForm {
        public String id;
        public String name;
        public String description;
        public String redirectUri;
        public List<String> actTypes = new ArrayList<String>();
        public List<String> geoAreas = new ArrayList<String>();
    }
    
    private static final long serialVersionUID = -8888090231363714695L;
    private List<ImportWizzardClientForm> clients = new ArrayList<ImportWizzardClientForm>();
    private List<Text> geoAreas = new ArrayList<Text>();
    private List<Text> types = new ArrayList<Text>();
    private Text defaultArea;
    private Text defaultType;
    private Text type;

    public List<ImportWizzardClientForm> getClients() {
        return clients;
    }

    public void setClients(List<ImportWizzardClientForm> clients) {
        this.clients = clients;
    }

    public List<Text> getGeoAreas() {
        return geoAreas;
    }

    public void setGeoAreas(List<Text> geoAreas) {
        this.geoAreas = geoAreas;
    }

    public List<Text> getTypes() {
        return types;
    }

    public void setTypes(List<Text> types) {
        this.types = types;
    }

    public Text getDefaultArea() {
        return defaultArea;
    }

    public void setDefaultArea(Text defaultArea) {
        this.defaultArea = defaultArea;
    }

    public Text getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(Text defaultType) {
        this.defaultType = defaultType;
    }

    public Text getType() {
        return type;
    }

    public void setType(Text type) {
        this.type = type;
    }
    
    public static ImportWizzardForm valueOf(List<org.orcid.jaxb.model.v3.dev1.client.Client> clients) {
        ImportWizzardForm form = new ImportWizzardForm();
        for(org.orcid.jaxb.model.v3.dev1.client.Client client : clients) {
            ImportWizzardClientForm clientForm = new ImportWizzardClientForm();
            clientForm.id = client.getId();
            clientForm.name = client.getName();
            clientForm.description = client.getDescription(); 
            clientForm.redirectUri = client.getClientRedirectUris().
            
            
            
            clientForm.setRedirectUris(new ArrayList<RedirectUri>());
            for (ClientRedirectUri clientRedirectUri : client.getClientRedirectUris()) {
                RedirectUri rUri = new RedirectUri();
                rUri.setValue(Text.valueOf(clientRedirectUri.getRedirectUri()));
                if(clientRedirectUri.getUriActType() != null) {
                    Text actType = Text.valueOf(clientRedirectUri.getUriActType());
                    rUri.setActType(actType);
                    if(!form.getTypes().contains(actType)) {
                        form.getTypes().add(actType);
                    }
                }                
                if(clientRedirectUri.getUriGeoArea() != null) {
                    Text geoArea = Text.valueOf(clientRedirectUri.getUriGeoArea());
                    rUri.setGeoArea(geoArea);
                    if(!form.getGeoAreas().contains(geoArea)) {
                        form.getGeoAreas().add(geoArea);
                    }
                }                                
                rUri.setType(Text.valueOf(clientRedirectUri.getRedirectUriType()));
                rUri.setScopes(clientRedirectUri.getPredefinedClientScopes().stream().map(x -> x.value()).collect(Collectors.toList()));
                clientForm.getRedirectUris().add(rUri);
            }
            form.getClients().add(clientForm);
        }        
        return form;
    }
}