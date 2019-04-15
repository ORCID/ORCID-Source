package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.release.record.Name;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface RecordNameManagerReadOnly {
    boolean exists(String orcid);
    
    Name getRecordName(String orcid);

    Name findByCreditName(String creditName);    
}
