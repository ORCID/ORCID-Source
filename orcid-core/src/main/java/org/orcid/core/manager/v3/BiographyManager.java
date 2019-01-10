package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.BiographyManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.record.Biography;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface BiographyManager extends BiographyManagerReadOnly {
    boolean updateBiography(String orcid, Biography bio);

    void createBiography(String orcid, Biography bio);

    void deleteBiography(String orcid);
}
