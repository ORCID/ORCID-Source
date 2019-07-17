package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.Name;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordNameManagerReadOnly {
    boolean exists(String orcid);

    Name getRecordName(String orcid);

    Name findByCreditName(String creditName);

    String fetchDisplayableCreditName(String orcid);

    String fetchDisplayableUserName(String orcid);

    String fetchDisplayablePublicName(String orcid);

    String fetchDisplayableDisplayName(String orcid);
}
