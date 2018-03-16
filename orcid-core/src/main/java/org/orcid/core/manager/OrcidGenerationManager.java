package org.orcid.core.manager;

/**
 * @author Will Simpson (will)
 * @author Declan Newman (declan) Date: 15/02/2012
 */
public interface OrcidGenerationManager {

    static final long ORCID_BASE_MIN = 15000000L;

    static final long ORCID_BASE_MAX = 35000000L;

    /**
     * Should not be used directly (public for testing only). Get an existing
     * one from the queue instead.
     */
    public String createNewOrcid();
}
