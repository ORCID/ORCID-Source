package org.orcid.core.contributors.works;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.orcid.core.contributors.ContributorRoleConverter;

/**
 * Class for converting between CR
 * 
 * Logic based on upper case enum names in DB.
 * 
 * Methods for identifying the correct enum name for the given value, for the
 * legacy value given the name of an enum, and the non-legacy value for the name
 * of an enum.
 * 
 * @author georgenash
 *
 */
public class WorkContributorRoleConverter implements ContributorRoleConverter {

    private static final Map<CreditContributorRole, LegacyContributorRole> MAPPINGS = new HashedMap<>();

    static {
        // only 3 map to legacy roles, others will map to null
        MAPPINGS.put(CreditContributorRole.WRITING_ORIGINAL_DRAFT, LegacyContributorRole.AUTHOR);
        MAPPINGS.put(CreditContributorRole.WRITING_REVIEW_EDITING, LegacyContributorRole.EDITOR);
        MAPPINGS.put(CreditContributorRole.INVESTIGATION, LegacyContributorRole.CO_INVESTIGATOR);
    }

    @Override
    public String toDBRole(String roleValue) {
        try {
            LegacyContributorRole legacyRole = LegacyContributorRole.fromValue(roleValue);
            return legacyRole.name();
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            CreditContributorRole creditRole = CreditContributorRole.fromValue(roleValue);
            return creditRole.name();
        } catch (IllegalArgumentException e) {
            // ignore
        }
        return null;
    }

    @Override
    public String toLegacyRoleValue(String dbRoleString) {
        try {
            LegacyContributorRole legacyRole = LegacyContributorRole.valueOf(dbRoleString);
            return legacyRole.value();
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            CreditContributorRole creditRole = CreditContributorRole.valueOf(dbRoleString);
            LegacyContributorRole legacyRole = MAPPINGS.get(creditRole);
            return legacyRole != null ? legacyRole.value() : null;
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return null;
    }

    @Override
    public String toRoleValue(String dbRoleString) {
        try {
            LegacyContributorRole legacyRole = LegacyContributorRole.valueOf(dbRoleString);
            return legacyRole.value();
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            CreditContributorRole creditRole = CreditContributorRole.valueOf(dbRoleString);
            return creditRole.value();
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return null;
    }

}
