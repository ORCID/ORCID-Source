package org.orcid.core.manager.v3.read_only.impl;

import java.util.Date;

import org.orcid.core.manager.v3.read_only.ManagerReadOnlyBase;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;

public class ManagerReadOnlyBaseImpl implements ManagerReadOnlyBase {
    protected ProfileLastModifiedAspect profileLastModifiedAspect;
    
    @Override
    public void setProfileLastModifiedAspect(ProfileLastModifiedAspect profileLastModifiedAspect) {
        this.profileLastModifiedAspect = profileLastModifiedAspect;
    }

    @Override
    public long getLastModified(String orcid) {
        Date lastModified = profileLastModifiedAspect.retrieveLastModifiedDate(orcid);
        return (lastModified == null) ? 0 : lastModified.getTime();
    }  
    
    @Override
    public Date getLastModifiedDate(String orcid) {
        return profileLastModifiedAspect.retrieveLastModifiedDate(orcid);        
    }  
}
