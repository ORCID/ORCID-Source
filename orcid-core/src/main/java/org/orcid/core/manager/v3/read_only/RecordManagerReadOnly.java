package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.rc1.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.Record;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordManagerReadOnly {
    Record getPublicRecord(String orcid);

    Record getRecord(String orcid);

    public OrcidIdentifier getOrcidIdentifier(String orcid);
}
