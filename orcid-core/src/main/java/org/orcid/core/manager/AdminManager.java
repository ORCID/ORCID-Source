package org.orcid.core.manager;

import java.util.List;

import org.orcid.pojo.AdminDelegatesRequest;
import org.orcid.pojo.ProfileDeprecationRequest;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface AdminManager {
    public static final String MANAGED_USER_PARAM = "managed";
    public static final String TRUSTED_USER_PARAM = "trusted";

    boolean deprecateProfile(ProfileDeprecationRequest result, String deprecatedOrcid, String primaryOrcid, String adminUser);
    
    boolean autoDeprecateProfile(ProfileDeprecationRequest result, String deprecatedOrcid, String primaryOrcid);

    AdminDelegatesRequest startDelegationProcess(AdminDelegatesRequest request, String trusted, String managed);

    List<String> getLockReasons();
}
