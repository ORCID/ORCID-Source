package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;

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
