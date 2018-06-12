package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AffiliationGroup implements Serializable {
    
    private static final long serialVersionUID = -8347171671099477223L;

    private List<AffiliationForm> affiliations;

    private Long activePutCode;

    private AffiliationForm defaultAffiliation;

    private int groupId;

    private String activeVisibility;

    private boolean userVersionPresent;

    private List<WorkExternalIdentifier> workExternalIdentifiers = new ArrayList<>();

    private String affiliationType;

    public List<AffiliationForm> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<AffiliationForm> affiliations) {
        this.affiliations = affiliations;
    }

    public Long getActivePutCode() {
        return activePutCode;
    }

    public void setActivePutCode(Long activePutCode) {
        this.activePutCode = activePutCode;
    }

    public AffiliationForm getDefaultAffiliation() {
        return defaultAffiliation;
    }

    public void setDefaultAffiliation(AffiliationForm defaultAffiliation) {
        this.defaultAffiliation = defaultAffiliation;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getActiveVisibility() {
        return activeVisibility;
    }

    public void setActiveVisibility(String activeVisibility) {
        this.activeVisibility = activeVisibility;
    }

    public boolean isUserVersionPresent() {
        return userVersionPresent;
    }

    public void setUserVersionPresent(boolean userVersionPresent) {
        this.userVersionPresent = userVersionPresent;
    }

    public List<WorkExternalIdentifier> getWorkExternalIdentifiers() {
        return workExternalIdentifiers;
    }

    public void setWorkExternalIdentifiers(List<WorkExternalIdentifier> workExternalIdentifiers) {
        this.workExternalIdentifiers = workExternalIdentifiers;
    }

    public String getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(String affiliationType) {
        this.affiliationType = affiliationType;
    }         
}