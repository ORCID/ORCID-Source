package org.orcid.pojo.ajaxForm;

import java.util.List;

public class Client {
    private String displayName;
    private String website;
    private String shortDescription;
    private List<RedirectUri> redirectUri;
    
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getWebsite() {
        return website;
    }
    public void setWebsite(String website) {
        this.website = website;
    }
    public String getShortDescription() {
        return shortDescription;
    }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public List<RedirectUri> getRedirectUri() {
        return redirectUri;
    }
    public void setRedirectUri(List<RedirectUri> redirectUri) {
        this.redirectUri = redirectUri;
    }
}
