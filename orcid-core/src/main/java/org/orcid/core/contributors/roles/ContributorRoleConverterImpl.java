package org.orcid.core.contributors.roles;

import java.util.List;
import java.util.Map;

import org.orcid.core.contributors.roles.credit.CreditRole;

/**
 * Class for converting between CRedIT and work and fundings contributor role
 * names for API 2.0, which only supports a fixed set of roles. Therefore CRediT
 * roles are converted, if deemed possible by a set of mappings, to an existing
 * role.
 * 
 * Also acts as a validator for incoming data for API V3, converting from the
 * API role value to the upper case format matching the name of the role enum,
 * which is of the format ued in our database.
 * 
 * Logic based on upper case enum names in DB.
 * 
 * Abstract class so both work and fundings can specify their mappings in the
 * getMapings method
 * 
 * @author georgenash
 *
 */
public abstract class ContributorRoleConverterImpl implements ContributorRoleConverter {

    private Map<CreditRole, LegacyContributorRole> conversionMappings;

    private List<LegacyContributorRole> legacyRoles;

    @Override
    public String toDBRole(String roleValue) {
        try {
            LegacyContributorRole legacyRole = getLegacyRole(roleValue);
            if (legacyRole != null) {
                return legacyRole.name();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            CreditRole creditRole = CreditRole.fromValue(roleValue);
            if (creditRole != null) {
                return creditRole.name();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }
        return null;
    }

    @Override
    public String toLegacyRoleValue(String dbRoleString) {
        if (conversionMappings == null) {
            conversionMappings = getMappings();
        }
        try {
            // already a legacy role value
            LegacyContributorRole legacyRole = getLegacyRole(dbRoleString);
            if (legacyRole != null) {
                return legacyRole.value();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            CreditRole creditRole = CreditRole.valueOf(dbRoleString);
            if (creditRole != null) {
                LegacyContributorRole legacyRole = conversionMappings.get(creditRole);
                return legacyRole != null ? legacyRole.value() : null;
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return null;
    }

    @Override
    public String toRoleValue(String dbRoleString) {
        try {
            LegacyContributorRole legacyRole = getLegacyRole(dbRoleString);
            if (legacyRole != null) {
                return legacyRole.value();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            CreditRole creditRole = CreditRole.valueOf(dbRoleString);
            if (creditRole != null) {
                return creditRole.value();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return null;
    }

    @Override
    public String toLegacyRoleName(String dbRoleString) {
        if (conversionMappings == null) {
            conversionMappings = getMappings();
        }
        try {
            // already a legacy role value
            LegacyContributorRole legacyRole = getLegacyRole(dbRoleString);
            if (legacyRole != null) {
                return legacyRole.name();
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            if (dbRoleString != null) {
                CreditRole creditRole = CreditRole.valueOf(dbRoleString);
                if (creditRole != null) {
                    LegacyContributorRole legacyRole = conversionMappings.get(creditRole);
                    return legacyRole != null ? legacyRole.name() : null;
                }
            }
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return null;
    }

    private LegacyContributorRole getLegacyRole(String name) {
        if (legacyRoles == null) {
            legacyRoles = getLegacyRoles();
        }
        if (name != null) {
            for (LegacyContributorRole role : legacyRoles) {
                if (name.equals(role.name())) {
                    return role;
                }
                if (name.equals(role.value())) {
                    return role;
                }
            }
        }

        return null;
    }

    protected abstract Map<CreditRole, LegacyContributorRole> getMappings();

    public abstract List<LegacyContributorRole> getLegacyRoles();

}
