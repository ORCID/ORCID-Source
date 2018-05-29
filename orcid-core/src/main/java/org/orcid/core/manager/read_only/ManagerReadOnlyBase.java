package org.orcid.core.manager.read_only;

import java.util.Date;

import org.orcid.persistence.aop.ProfileLastModifiedAspect;

public interface ManagerReadOnlyBase {
    void setProfileLastModifiedAspect(ProfileLastModifiedAspect profileLastModifiedAspect);

    long getLastModified(String orcid);
    
    Date getLastModifiedDate(String orcid);
}
