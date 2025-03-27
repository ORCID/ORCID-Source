package org.orcid.core.manager.v3.read_only;

/**
 * 
 * @author Andrej Romanov
 * 
 */
public interface ProfileInterstitialFlagManagerReadOnly {
    boolean hasInterstitialFlag(String orcid, String interstitialName);
}
