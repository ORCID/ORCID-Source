package org.orcid.core.contributors.roles.credit;

import java.io.Serializable;

public enum CreditRole implements Serializable {

    CONCEPTUALIZATION("http://credit.niso.org/contributor-roles/conceptualization/", "conceptualization"), 
    DATA_CURATION("http://credit.niso.org/contributor-roles/data-curation/", "data curation"),     
    FORMAL_ANALYSIS("http://credit.niso.org/contributor-roles/formal-analysis/", "formal analysis"), 
    FUNDING_ACQUISITION("http://credit.niso.org/contributor-roles/funding-acquisition/", "funding acquisition"), 
    INVESTIGATION("http://credit.niso.org/contributor-roles/investigation/", "investigation"), 
    METHODOLOGY("http://credit.niso.org/contributor-roles/methodology/", "methodology"), 
    PROJECT_ADMINISTRATION("http://credit.niso.org/contributor-roles/project-administration/", "project administration"), 
    RESOURCES("http://credit.niso.org/contributor-roles/resources/", "resources"), 
    SOFTWARE("http://credit.niso.org/contributor-roles/software/", "software"), 
    SUPERVISION("http://credit.niso.org/contributor-roles/supervision/", "supervision"),
    VALIDATION("http://credit.niso.org/contributor-roles/validation/", "validation"),
    VISUALIZATION("http://credit.niso.org/contributor-roles/visualization/", "visualization"),
    WRITING_ORIGINAL_DRAFT("http://credit.niso.org/contributor-roles/writing-original-draft/", "writing - original draft"),
    WRITING_REVIEW_EDITING("http://credit.niso.org/contributor-roles/writing-review-editing/", "writing - review & editing");

    private final String value;
    private final String uiValue;

    CreditRole(String v, String ui) {
        value = v;
        uiValue = ui;
    }

    public String value() {
        return value;
    }
    
    public String getUiValue() {
        return uiValue;
    }
    
    public static CreditRole fromValue(String v) {
        for (CreditRole c : CreditRole.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }        
}
