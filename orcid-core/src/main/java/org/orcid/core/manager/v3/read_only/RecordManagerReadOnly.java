package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.dev1.record.Record;
import org.orcid.jaxb.model.v3.dev1.common.OrcidIdentifier;

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
