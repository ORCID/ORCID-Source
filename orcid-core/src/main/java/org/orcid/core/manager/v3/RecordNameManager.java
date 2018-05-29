package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.record.Name;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordNameManager extends RecordNameManagerReadOnly {
    boolean updateRecordName(String orcid, Name name);

    void createRecordName(String orcid, Name name);
}
