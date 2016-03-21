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

import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;

public interface OtherNameManager {
    OtherNames getOtherNames(String orcid, long lastModified);
    
    OtherNames getPublicOtherNames(String orcid, long lastModified);
    
    OtherNames getMinimizedOtherNames(String orcid, long lastModified);
    
    OtherName getOtherName(String orcid, Long putCode);

    boolean deleteOtherName(String orcid, Long putCode, boolean checkSource);

    OtherName createOtherName(String orcid, OtherName otherName, boolean isApiRequest);

    OtherName updateOtherName(String orcid, Long putCode, OtherName otherName, boolean isApiRequest);
    
    OtherNames updateOtherNames(String orcid, OtherNames otherNames);
}
