package org.orcid.core.contributors.roles;

public interface ContributorRoleConverter {
    
    String toRoleValue(String dbRoleString);

    String toLegacyRoleValue(String dbRoleString);

    String toDBRole(String roleValue);
    
    String toLegacyRoleName(String contributorRoleValue);
    
}
