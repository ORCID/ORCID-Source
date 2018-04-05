package org.orcid.core.profile.history;

public enum ProfileHistoryEventType {
    
    SET_DEFAULT_VIS_TO_PRIVATE("Def vis: private"),
    SET_DEFAULT_VIS_TO_PUBLIC("Def vis: public"),
    SET_DEFAULT_VIS_TO_LIMITED("Def vis: limited"),
    ACCEPTED_TERMS_CONDITIONS("Accepted T&Cs");
    
    String label;
    
    ProfileHistoryEventType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }

}
