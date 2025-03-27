package org.orcid.core.manager.v3.read_only.impl;


import org.orcid.core.manager.read_only.impl.ManagerReadOnlyBaseImpl;
import org.orcid.core.manager.v3.read_only.ProfileInterstitialFlagManagerReadOnly;
import org.orcid.persistence.dao.ProfileInterstitialFlagDao;

import javax.annotation.Resource;
import java.util.List;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public class ProfileInterstitialFlagManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ProfileInterstitialFlagManagerReadOnly {
    @Resource
    protected ProfileInterstitialFlagDao profileInterstitialFlagDaoReadOnly;

    public void setProfileInterstitialFlagDao(ProfileInterstitialFlagDao profileInterstitialFlagDaoReadOnly) {
        this.profileInterstitialFlagDaoReadOnly = profileInterstitialFlagDaoReadOnly;
    }

    public boolean hasInterstitialFlag(String orcid, String interstitialName) {
        return profileInterstitialFlagDaoReadOnly.hasInterstitialFlag(orcid, interstitialName);
    };
}
