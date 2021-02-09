package org.orcid.core.contributors.works;

import java.io.Serializable;

public enum LegacyContributorRole implements Serializable {

    AUTHOR("author"), 
    ASSIGNEE("assignee"), 
    EDITOR("editor"), 
    CHAIR_OR_TRANSLATOR("chair-or-translator"), 
    CO_INVESTIGATOR("co-investigator"), 
    CO_INVENTOR("co-inventor"), 
    GRADUATE_STUDENT("graduate-student"), 
    OTHER_INVENTOR("other-inventor"), 
    PRINCIPAL_INVESTIGATOR("principal-investigator"), 
    POSTDOCTORAL_RESEARCHER("postdoctoral-researcher"), 
    SUPPORT_STAFF("support-staff");
    
    private final String value;

    LegacyContributorRole(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static LegacyContributorRole fromValue(String v) {
        for (LegacyContributorRole c : LegacyContributorRole.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
