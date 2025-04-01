package org.orcid.core.manager.v3;

import org.orcid.core.manager.v3.read_only.ProfileInterstitialFlagManagerReadOnly;
import org.orcid.persistence.jpa.entities.ProfileInterstitialFlagEntity;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public interface ProfileInterstitialFlagManager extends ProfileInterstitialFlagManagerReadOnly {
    ProfileInterstitialFlagEntity addInterstitialFlag(String orcid, String interstitialName);

}
