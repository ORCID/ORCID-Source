package org.orcid.core.manager;

import org.orcid.core.manager.read_only.BiographyManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Biography;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface BiographyManager extends BiographyManagerReadOnly {
    boolean updateBiography(String orcid, Biography bio);

    void createBiography(String orcid, Biography bio);
}
