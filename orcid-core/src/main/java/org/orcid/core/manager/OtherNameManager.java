package org.orcid.core.manager;

import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;

public interface OtherNameManager extends OtherNameManagerReadOnly {
    boolean deleteOtherName(String orcid, Long putCode, boolean checkSource);

    OtherName createOtherName(String orcid, OtherName otherName, boolean isApiRequest);

    OtherName updateOtherName(String orcid, Long putCode, OtherName otherName, boolean isApiRequest);
    
    OtherNames updateOtherNames(String orcid, OtherNames otherNames);
    
    /**
     * Removes all other names that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all other names will be
     *            removed.
     */
    void removeAllOtherNames(String orcid);
}
