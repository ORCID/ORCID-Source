package org.orcid.core.contributors.works;

import java.io.Serializable;

public enum CreditContributorRole implements Serializable {

    CONCEPTUALIZATION("conceptualization"), 
    DATA_CURATION("data curation"), 
    EDITOR("editor"), 
    FORMAL_ANALYSIS("formal analysis"), 
    FUNDING_ACQUISITION("funding acquisition"), 
    INVESTIGATION("investigation"), 
    METHODOLOGY("methodology"), 
    PROJECT_ADMINISTRATION("project administration"), 
    RESOURCES("resources"), 
    SOFTWARE("software"), 
    SUPERVISION("supervision"),
    VALIDATION("validation"),
    VISUALIZATION("visualization"),
    WRITING_ORIGINAL_DRAFT("writing – original draft"),
    WRITING_REVIEW_EDITING("writing – review & editing");

    private final String value;

    CreditContributorRole(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static CreditContributorRole fromValue(String v) {
        for (CreditContributorRole c : CreditContributorRole.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
