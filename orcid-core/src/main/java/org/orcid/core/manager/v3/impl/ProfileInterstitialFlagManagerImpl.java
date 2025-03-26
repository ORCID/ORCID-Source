package org.orcid.core.manager.v3.impl;

import org.orcid.core.manager.v3.ProfileInterstitialFlagManager;
import org.orcid.core.manager.v3.read_only.impl.ProfileInterstitialFlagManagerReadOnlyImpl;
import org.orcid.persistence.dao.ProfileInterstitialFlagDao;
import org.orcid.persistence.jpa.entities.ProfileInterstitialFlagEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public class ProfileInterstitialFlagManagerImpl extends ProfileInterstitialFlagManagerReadOnlyImpl implements ProfileInterstitialFlagManager {
    @Resource
    protected ProfileInterstitialFlagDao profileInterstitialFlagDao;

    @Transactional
    public ProfileInterstitialFlagEntity addInterstitialFlag(String orcid, String interstitialName) {
        if (orcid == null || orcid.isBlank()) {
            throw new IllegalArgumentException("ORCID must not be empty");
        }
        if (interstitialName == null || interstitialName.isBlank()) {
            throw new IllegalArgumentException("Interstitial flag must not be empty");
        }
        return profileInterstitialFlagDao.addInterstitialFlag(orcid, interstitialName);
    }
}
