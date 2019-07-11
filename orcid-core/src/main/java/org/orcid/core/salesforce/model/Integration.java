package org.orcid.core.salesforce.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Will Simpson
 *
 */
public class Integration implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Boolean badgeAwarded;
    private String description;
    private String level;
    private String stage;
    private List<Achievement> achievements = new ArrayList<>();

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

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    @Override
    public String toString() {
        return "Integration [id=" + id + ", name=" + name + ", badgeAwarded=" + badgeAwarded + ", description=" + description + ", level=" + level + ", stage=" + stage
                + ", achievements=" + achievements + "]";
    }

}
