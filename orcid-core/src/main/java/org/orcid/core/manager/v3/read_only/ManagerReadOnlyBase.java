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
package org.orcid.core.manager.v3.read_only;

import java.util.Date;

import org.orcid.persistence.aop.ProfileLastModifiedAspect;

public interface ManagerReadOnlyBase {
    void setProfileLastModifiedAspect(ProfileLastModifiedAspect profileLastModifiedAspect);

    long getLastModified(String orcid);
    
    Date getLastModifiedDate(String orcid);
}
