package org.orcid.core.manager.v3.read_only.impl;


import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.manager.v3.read_only.ProfileEmailDomainManagerReadOnly;
import org.orcid.persistence.dao.ProfileEmailDomainDao;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public class ProfileEmailDomainManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ProfileEmailDomainManagerReadOnly {
    @Resource
    protected ProfileEmailDomainDao profileEmailDomainDaoReadOnly;

    public void setProfileEmailDomainDao(ProfileEmailDomainDao profileEmailDomainDao) {
        this.profileEmailDomainDaoReadOnly = profileEmailDomainDao;
    }

    public List<ProfileEmailDomainEntity> getEmailDomains(String orcid) {
        return profileEmailDomainDaoReadOnly.findByOrcid(orcid);
    };

    public List<ProfileEmailDomainEntity> getPublicEmailDomains(String orcid) {
        return profileEmailDomainDaoReadOnly.findPublicEmailDomains(orcid);
    };
}
