package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.rc2.common.OrcidIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.Record;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordManagerReadOnly {
    Record getPublicRecord(String orcid, boolean filterVersionOfIdentifiers);

    Record getRecord(String orcid, boolean filterVersionOfIdentifiers);

    public OrcidIdentifier getOrcidIdentifier(String orcid);
}
