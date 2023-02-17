package org.orcid.core.manager;

/**
 * @author Will Simpson (will)
 * @author Declan Newman (declan) Date: 15/02/2012
 */
public interface OrcidGenerationManager {

    /**
     * Original range of ID's (20M IDs):
     * - FROM 0000-0001-5000-0000 TO 0000-0003-5000-0000
     * */
    static final long ORCID_BASE_MIN = 15000000L;

    static final long ORCID_BASE_MAX = 35000000L;
    
    /**
     * New range of ID's (100M IDs):
     * -  FROM 0009-0000-0000-0000 TO 0009-0010-0000-0000 
     * */
    static final long ORCID_BASE_V2_MIN = 900000000000L;
    
    static final long ORCID_BASE_V2_MAX = 900100000000L;

    /**
     * Should not be used directly (public for testing only). Get an existing
     * one from the queue instead.
     */
    public String createNewOrcid();
}
