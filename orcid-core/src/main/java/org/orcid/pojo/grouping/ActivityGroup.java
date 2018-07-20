package org.orcid.pojo.grouping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;

public abstract class ActivityGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long activePutCode;

    private int groupId;

    public String activeVisibility;

    private boolean userVersionPresent;

    private List<ActivityExternalIdentifier> externalIdentifiers = new ArrayList<>();

    public Long getActivePutCode() {
        return activePutCode;
    }

    public void setActivePutCode(Long activePutCode) {
        this.activePutCode = activePutCode;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isUserVersionPresent() {
        return userVersionPresent;
    }

    public void setUserVersionPresent(boolean userVersionPresent) {
        this.userVersionPresent = userVersionPresent;
    }

    public String getActiveVisibility() {
        return activeVisibility;
    }

    public void setActiveVisibility(String activeVisibility) {
        this.activeVisibility = activeVisibility;
    }

    public List<ActivityExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ActivityExternalIdentifier> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

}
