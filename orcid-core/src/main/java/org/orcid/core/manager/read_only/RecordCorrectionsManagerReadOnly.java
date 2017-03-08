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
package org.orcid.core.manager.read_only;

import org.orcid.model.record_correction.RecordCorrectionsPage;

public interface RecordCorrectionsManagerReadOnly {
    RecordCorrectionsPage getInvalidRecordDataChangesDescending(Long lastElement, Long pageSize);
    RecordCorrectionsPage getInvalidRecordDataChangesAscending(Long lastElement, Long pageSize);
    void cacheEvict();
}
