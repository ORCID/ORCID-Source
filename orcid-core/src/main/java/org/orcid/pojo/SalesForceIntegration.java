package org.orcid.pojo;

import java.net.URL;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceIntegration {

    private String name;
    private String description;
    private String stage;
    private URL resourceUrl;

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
