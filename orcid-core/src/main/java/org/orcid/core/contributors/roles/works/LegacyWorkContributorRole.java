package org.orcid.core.contributors.roles.works;

import java.io.Serializable;

import org.orcid.core.contributors.roles.LegacyContributorRole;

public enum LegacyWorkContributorRole implements Serializable, LegacyContributorRole {

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

    LegacyWorkContributorRole(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static LegacyWorkContributorRole fromValue(String v) {
        for (LegacyWorkContributorRole c : LegacyWorkContributorRole.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
