package org.orcid.core.manager.read_only;

import org.orcid.model.record_correction.RecordCorrectionsPage;

public interface RecordCorrectionsManagerReadOnly {
    RecordCorrectionsPage getInvalidRecordDataChangesDescending(Long lastElement, Long pageSize);
    RecordCorrectionsPage getInvalidRecordDataChangesAscending(Long lastElement, Long pageSize);
    void cacheEvict();
}
