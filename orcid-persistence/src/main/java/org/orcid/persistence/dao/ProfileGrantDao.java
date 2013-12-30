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
package org.orcid.persistence.dao;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;

public interface ProfileGrantDao extends GenericDao<ProfileFundingEntity, Long> {

	 /**
     * Removes the relationship that exists between a grant and a profile.
     * 
     * @param profileGrantId
     *            The id of the profileGrant that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
	boolean removeProfileGrant(String clientOrcid, String profileGrantId);
	/**
     * Updates the visibility of an existing profile grant relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileGrantId
     *            The id of the profile grant that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileGrant object
     * 
     * @return true if the relationship was updated
     * */
	boolean updateProfileGrant(String clientOrcid, String profileGrantId, Visibility visibility);
	/**
	 * Creates a new profile grant relationship between an organization and a profile.
	 * 
	 * @param newProfileGrantEntity
	 * 		The object to be persisted
	 * @return the created newProfileGrantEntity with the id assigned on database
	 * */
	ProfileFundingEntity addProfileGrant(ProfileFundingEntity newProfileGrantEntity);
	/**
     * Get the grant associated with the client orcid and the organization id
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgId
     *            The id of the organization
     * 
     * @return the ProfileGrantEntity object
     * */
	ProfileFundingEntity getProfileGrantEntity(String orgId, String clientOrcid);
	/**
     * Get the grant associated with the given profileGrant id
     * 
     * @param profileGrantId
     *            The id of the ProfileGrantEntity object
     * 
     * @return the ProfileGrantEntity object
     * */
	ProfileFundingEntity getProfileGrantEntity(String profileGrantId);
}
