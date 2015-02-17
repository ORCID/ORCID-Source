package org.orcid.core.manager;

import org.orcid.jaxb.model.record.Activity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface OrcidSecurityManager {

    void checkVisibility(Activity activity);
    
}
