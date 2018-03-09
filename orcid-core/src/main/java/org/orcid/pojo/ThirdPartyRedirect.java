package org.orcid.pojo;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class ThirdPartyRedirect extends Redirect implements ErrorsInterface {

    private String shortDescription;
    private String displayName;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
