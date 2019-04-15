package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.release.record.Biography;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface BiographyManagerReadOnly {
    boolean exists(String orcid);
    
    Biography getBiography(String orcid);

    Biography getPublicBiography(String orcid);    
}
