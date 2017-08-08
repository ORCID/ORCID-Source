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
package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.OtherNameManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.OtherNames;

public interface OtherNameManager extends OtherNameManagerReadOnly {
    boolean deleteOtherName(String orcid, Long putCode, boolean checkSource);

    OtherName createOtherName(String orcid, OtherName otherName, boolean isApiRequest);

    OtherName updateOtherName(String orcid, Long putCode, OtherName otherName, boolean isApiRequest);
    
    OtherNames updateOtherNames(String orcid, OtherNames otherNames);
    
    /**
     * Removes all other names that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all other names will be
     *            removed.
     */
    void removeAllOtherNames(String orcid);
}
