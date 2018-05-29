package org.orcid.core.manager;

import org.orcid.jaxb.model.message.OrcidProfile;

public interface OrcidProfileCacheManager {

    public OrcidProfile retrievePublic(String orcid);
    
    public OrcidProfile retrievePublicBio(String orcid);
    
    public OrcidProfile retrieve(String orcid);
    
    public OrcidProfile retrieveProfileBioAndInternal(String orcid);
    
    @Deprecated 
    public void put(String orcid, OrcidProfile orcidProfile);
    
    public void put(OrcidProfile orcidProfile);
    
    public void removeAll();
    
    public void remove(String orcid);
    
}
