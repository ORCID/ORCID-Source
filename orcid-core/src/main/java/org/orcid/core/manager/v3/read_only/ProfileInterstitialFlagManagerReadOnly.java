package org.orcid.core.manager.v3.read_only;

import java.util.List;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public interface ProfileInterstitialFlagManagerReadOnly {
    boolean hasInterstitialFlag(String orcid, String interstitialName);

    List<String> findByOrcid(String orcid);
}
