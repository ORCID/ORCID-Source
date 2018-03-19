package org.orcid.core.salesforce.model;

import java.io.Serializable;
import java.net.URL;

/**
 * 
 * @author Will Simpson
 *
 */
public class Integration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Boolean badgeAwarded;
    private String description;
    private String level;
    private String stage;
    private URL resourceUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Boolean getBadgeAwarded() {
        return badgeAwarded;
    }

    public void setBadgeAwarded(Boolean badgeAwarded) {
        this.badgeAwarded = badgeAwarded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public URL getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(URL resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

}
