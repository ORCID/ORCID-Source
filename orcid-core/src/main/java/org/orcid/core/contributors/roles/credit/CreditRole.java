package org.orcid.core.contributors.roles.credit;

import java.io.Serializable;

public enum CreditRole implements Serializable {

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

    CreditRole(String v) {
        value = v;
    }

    public String value() {
        return value;
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
