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

import javax.persistence.NoResultException;

import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidNotClaimedException;
import org.orcid.core.security.aop.LockedException;
import org.orcid.jaxb.model.common_rc2.Filterable;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record_rc2.Biography;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.persistence.jpa.entities.SourceEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface OrcidSecurityManager {

    void setProfileEntityCacheManager(ProfileEntityCacheManager profileEntityCacheManager);
    
    void setSourceManager(SourceManager sourceManager);
    
    void checkVisibility(Filterable filterable, String orcid);   
    
    void checkVisibility(Name name, String orcid);
    
    void checkVisibility(Biography biography, String orcid);         
    
    void checkIsPublic(Filterable filterable);
    
    void checkIsPublic(Biography biography);
    
    void checkSource(SourceEntity existingSource);

    boolean isAdmin();

    boolean isPasswordConfirmationRequired();
    
    boolean hasScope(ScopePathType scope);

    String getClientIdFromAPIRequest();
    
    void checkPermissions(ScopePathType requiredScope, String orcid);
    
    void checkProfile(String orcid) throws NoResultException, OrcidDeprecatedException, OrcidNotClaimedException, LockedException;
}
