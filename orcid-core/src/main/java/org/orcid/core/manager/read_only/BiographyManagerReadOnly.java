package org.orcid.core.manager.read_only;

import org.orcid.jaxb.model.record_v2.Biography;

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
