package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.SpamManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.Spam;

/**
 *
 * @author Daniel Palafox
 *
 */
public interface SpamManager extends SpamManagerReadOnly{
    boolean removeSpam(String orcid);             
    
    boolean createOrUpdateSpam(String orcid);
    
}
