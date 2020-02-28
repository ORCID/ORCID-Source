package org.orcid.core.manager.v3.read_only;

import org.orcid.jaxb.model.v3.release.record.Spam;

/**
 * 
 * @author Daniel Palafox
 * 
 */
public interface SpamManagerReadOnly {
    boolean exists(String orcid);
    
    Spam getSpam(String orcid);
        
}
