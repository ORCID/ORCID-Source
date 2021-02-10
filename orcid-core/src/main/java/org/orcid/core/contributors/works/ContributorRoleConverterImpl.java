package org.orcid.core.contributors.works;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.orcid.core.contributors.ContributorRoleConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for converting between CRedIT role names, used only in ORCiD API v3.0,
 * to legacy role names.
 * 
 * Used for cases where the role names have been stored using v3.0 in CRediT
 * role name format.
 * 
 * Also acts as a validator for incoming data, converting from the API role
 * value to the upper case format matching the name of the role enum, which is
 * of the format ued in our database.
 * 
 * Logic based on upper case enum names in DB.
 * 
 * @author georgenash
 *
 */
public class ContributorRoleConverterImpl implements ContributorRoleConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContributorRoleConverterImpl.class);

    private final Map<CreditContributorRole, LegacyContributorRole> conversionMappings = new HashedMap<>();

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
        if (conversionMappings.isEmpty()) {
            LOGGER.warn("No mappings set for conversion from CRediT roles to legacy roles!");
        }
        try {
            // already a legacy role value
            LegacyContributorRole legacyRole = LegacyContributorRole.valueOf(dbRoleString);
            return legacyRole.value();
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            CreditContributorRole creditRole = CreditContributorRole.valueOf(dbRoleString);
            LegacyContributorRole legacyRole = conversionMappings.get(creditRole);
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

    /**
     * Takes a CSV list of CreditContributorRole to LegacyContributorRole
     * mappings. Mappings are used for V3 CRediT roles names to legacy pre V3
     * role names.
     * 
     * No validation so arg must be correct.
     * 
     * mappingsString example: "INVESTIGATION,CO_INVESTIGATOR"
     * 
     */
    @Override
    public void setMappingsString(String mappingsString) {
        String[] mappings = mappingsString.split(",");
        int index = 0;

        while (true) {
            if (mappings.length < index + 2) {
                break;
            }
            CreditContributorRole creditRole = CreditContributorRole.valueOf(mappings[index]);
            LegacyContributorRole legacyRole = LegacyContributorRole.valueOf(mappings[index + 1]);
            conversionMappings.put(creditRole, legacyRole);
            
            index += 2;
        }
    }

    @Override
    public String toLegacyRoleName(String dbRoleString) {
        if (conversionMappings.isEmpty()) {
            LOGGER.warn("No mappings set for conversion from CRediT roles to legacy roles!");
        }
        try {
            // already a legacy role value
            LegacyContributorRole legacyRole = LegacyContributorRole.valueOf(dbRoleString);
            return legacyRole.name();
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            CreditContributorRole creditRole = CreditContributorRole.valueOf(dbRoleString);
            LegacyContributorRole legacyRole = conversionMappings.get(creditRole);
            return legacyRole != null ? legacyRole.name() : null;
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return null;
    }

}
