/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import java.util.List;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

public interface ProfileFundingManager {

    /**
     * Removes the relationship that exists between a funding and a profile.
     * 
     * @param profileFundingId
     *            The id of the profileFunding that will be removed from the
     *            client profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    boolean removeProfileFunding(String clientOrcid, String profileFundingId);

    /**
     * Updates the visibility of an existing profile funding relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileFundingId
     *            The id of the profile funding that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileFunding object
     * 
     * @return true if the relationship was updated
     * */
    boolean updateProfileFunding(String clientOrcid, String profileFundingId, Visibility visibility);

    /**
     * Creates a new profile funding relationship between an organization and a
     * profile.
     * 
     * @param newProfileFundingEntity
     *            The object to be persisted
     * @return the created newProfileFundingEntity with the id assigned on
     *         database
     * */
    ProfileFundingEntity addProfileFunding(ProfileFundingEntity newProfileFundingEntity);
        
    /**
     * Add a new funding subtype to the list of pending for indexing subtypes
     * */
    void addFundingSubType(String subtype, String orcid);        
    
    /**
     * A process that will process all funding subtypes, filter and index them. 
     * */
    void indexFundingSubTypes();
    
    /**
     * Looks for the org defined funding subtypes that matches a given pattern
     * @param subtype pattern to look for
     * @param limit the max number of results to look for
     * @return a list of all org defined funding subtypes that matches the given pattern
     * */
    List<String> getIndexedFundingSubTypes(String subtype, int limit);
    
}
