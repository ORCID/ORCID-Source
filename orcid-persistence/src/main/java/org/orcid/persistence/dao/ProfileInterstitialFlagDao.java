package org.orcid.persistence.dao;

import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.persistence.jpa.entities.ProfileInterstitialFlagEntity;

import java.util.List;

public interface ProfileInterstitialFlagDao extends GenericDao<ProfileInterstitialFlagEntity, Long> {
    ProfileInterstitialFlagEntity addInterstitialFlag(String orcid, String interstitialName);

    boolean hasInterstitialFlag(String orcid, String interstitialName);

    List<ProfileInterstitialFlagEntity> findByOrcid(String orcid);
}