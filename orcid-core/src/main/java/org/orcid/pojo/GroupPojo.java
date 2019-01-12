package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.Activity;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.GroupableActivity;
import org.orcid.jaxb.model.v3.rc2.record.SourceAware;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;

/** Generic pojo for representing groups of activity summaries.
 * Subclasses must implment a getter for their activities so that serialization has correct name
 * 
 * Same serialisation layout as workgroup.
 * 
 * @author tom
 *
 * @param <T> The summary type.
 */
public class GroupPojo <T extends GroupableActivity & Activity & SourceAware>{

    private List<T> activities;
    
    private Long activePutCode;

    private T defaultActivity;

    private int groupId;

    public String activeVisibility;
    
    private boolean userVersionPresent;
    
    private List<ActivityExternalIdentifier> externalIdentifiers = new ArrayList<>();
    
    public GroupPojo(List<T> summaries, int id, String orcid, ExternalIDs ids){
        setGroupId(id);
        Long maxDisplayIndex = null;
        setActivities(new ArrayList<T>());
        for (T summary : summaries) {
            getActivities().add(summary);

            Long displayIndex = Long.parseLong(summary.getDisplayIndex());
            if (maxDisplayIndex == null || displayIndex > maxDisplayIndex) {
                maxDisplayIndex = displayIndex;
                setActivePutCode(summary.getPutCode());
                setActiveVisibility(summary.getVisibility().name());
                setDefaultActivity(summary);
            }

            if (summary.getSource().retrieveSourcePath().equals(orcid)) {
                setUserVersionPresent(true);
            }
        }

        if (ids != null) {
            for (ExternalID extId : ids.getExternalIdentifier()) {
                if (extId.getRelationship() == null) {
                        extId.setRelationship(Relationship.SELF);
                }
                externalIdentifiers.add(ActivityExternalIdentifier.valueOf(extId));
            }
        }
    }
    
    protected List<T> getActivities() {
        return activities;
    }

    protected void setActivities(List<T> activities) {
        this.activities = activities;
    }

    public Long getActivePutCode() {
        return activePutCode;
    }

    public void setActivePutCode(Long activePutCode) {
        this.activePutCode = activePutCode;
    }

    public T getDefaultActivity() {
        return defaultActivity;
    }

    public void setDefaultActivity(T defaultActivity) {
        this.defaultActivity = defaultActivity;
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

    public List<ActivityExternalIdentifier> getWorkExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setWorkExternalIdentifiers(List<ActivityExternalIdentifier> workExternalIdentifiers) {
        this.externalIdentifiers = workExternalIdentifiers;
    }
    
}
