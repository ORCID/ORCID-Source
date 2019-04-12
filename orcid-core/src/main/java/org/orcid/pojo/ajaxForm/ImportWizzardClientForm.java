package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImportWizzardClientForm implements Serializable {
    private static final long serialVersionUID = -8197311524314039635L;
    public String id;
    public String name;
    public String description;
    public String redirectUri;
    public String scopes;
    public List<String> actTypes = new ArrayList<String>();
    public List<String> geoAreas = new ArrayList<String>();
    private String status;
    private String clientWebsite;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }    
    
    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }

    public List<String> getActTypes() {
        return actTypes;
    }

    public void setActTypes(List<String> actTypes) {
        this.actTypes = actTypes;
    }

    public List<String> getGeoAreas() {
        return geoAreas;
    }

    public void setGeoAreas(List<String> geoAreas) {
        this.geoAreas = geoAreas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClientWebsite() {
        return clientWebsite;
    }

    public void setClientWebsite(String clientWebsite) {
        this.clientWebsite = clientWebsite;
    }

}
