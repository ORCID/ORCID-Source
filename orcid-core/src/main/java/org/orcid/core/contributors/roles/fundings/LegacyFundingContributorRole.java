package org.orcid.core.contributors.roles.fundings;

import java.io.Serializable;

import org.orcid.core.contributors.roles.LegacyContributorRole;

public enum LegacyFundingContributorRole implements Serializable, LegacyContributorRole {

    LEAD("lead"),
    CO_LEAD("co-lead"),
    SUPPORTED_BY("supported-by"),
    OTHER_CONTRIBUTION("other-contribution");
    
    private final String value;

    LegacyFundingContributorRole(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
    
    public static LegacyFundingContributorRole fromValue(String v) {
        for (LegacyFundingContributorRole c : LegacyFundingContributorRole.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
