package org.orcid.core.contributors.roles.credit;

import java.io.Serializable;

public enum CreditRole implements Serializable {

    CONCEPTUALIZATION("http://credit.niso.org/contributor-roles/conceptualization/", "Conceptualization"), 
    DATA_CURATION("http://credit.niso.org/contributor-roles/data-curation/", "Data curation"), 
    EDITOR("editor", "Editor"), 
    FORMAL_ANALYSIS("http://credit.niso.org/contributor-roles/formal-analysis/", "Formal analysis"), 
    FUNDING_ACQUISITION("http://credit.niso.org/contributor-roles/funding-acquisition/", "Funding acquisition"), 
    INVESTIGATION("http://credit.niso.org/contributor-roles/investigation/", "Investigation"), 
    METHODOLOGY("http://credit.niso.org/contributor-roles/methodology/", "Methodology"), 
    PROJECT_ADMINISTRATION("http://credit.niso.org/contributor-roles/project-administration/", "Project administration"), 
    RESOURCES("http://credit.niso.org/contributor-roles/resources/", "Resources"), 
    SOFTWARE("http://credit.niso.org/contributor-roles/software/", "Software"), 
    SUPERVISION("http://credit.niso.org/contributor-roles/supervision/", "Supervision"),
    VALIDATION("http://credit.niso.org/contributor-roles/validation/", "Validation"),
    VISUALIZATION("http://credit.niso.org/contributor-roles/visualization/", "Visualization"),
    WRITING_ORIGINAL_DRAFT("http://credit.niso.org/contributor-roles/writing-original-draft/", "Writing – original draft"),
    WRITING_REVIEW_EDITING("http://credit.niso.org/contributor-roles/writing-review-editing/", "Writing – review & editing");

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
