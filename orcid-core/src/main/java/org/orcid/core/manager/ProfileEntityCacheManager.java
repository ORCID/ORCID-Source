package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.ProfileEntity;

public interface ProfileEntityCacheManager {

    public ProfileEntity retrieve(String orcid) throws IllegalArgumentException;
        
    public void removeAll();
    
    public void remove(String orcid);
    
}
