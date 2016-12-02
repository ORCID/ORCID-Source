/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

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
    
    boolean deprecateProfile(ProfileDeprecationRequest result, String deprecatedOrcid, String primaryOrcid);
    AdminDelegatesRequest startDelegationProcess(AdminDelegatesRequest request, String trusted, String managed);
    String removeSecurityQuestion(String orcid);
}
