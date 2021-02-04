package org.orcid.core.contributors;

public interface ContributorRoleConverter {
    
    String toRoleValue(String dbRoleString);

    String toLegacyRoleValue(String dbRoleString);

    String toDBRole(String roleValue);
    
}
