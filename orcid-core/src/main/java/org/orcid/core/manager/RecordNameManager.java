package org.orcid.core.manager;

import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Name;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordNameManager extends RecordNameManagerReadOnly {
    boolean updateRecordName(String orcid, Name name);

    void createRecordName(String orcid, Name name);
}
