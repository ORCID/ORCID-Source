package org.orcid.core.salesforce.model;

import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 *
 */
public class Achievement implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String badgeId;

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

    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    @Override
    public String toString() {
        return "Achievement [id=" + id + ", name=" + name + ", badgeId=" + badgeId + "]";
    }

}
