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
import org.orcid.persistence.jpa.entities.ProfileGrantEntity;

public interface ProfileGrantDao extends GenericDao<ProfileGrantEntity, Long> {

	 /**
     * Removes the relationship that exists between a funding and a profile.
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
     * Updates the visibility of an existing profile funding relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileGrantId
     *            The id of the profile grant that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile orgFundingRelationId relationship
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
	ProfileGrantEntity addProfileGrant(ProfileGrantEntity newProfileGrantEntity);
	/**
     * Get the funding associated with the client orcid and the organization id
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgId
     *            The id of the organization
     * 
     * @return the ProfileGrantEntity object
     * */
	ProfileGrantEntity getOrgFundingRelation(String orgId, String clientOrcid);
	/**
     * Get the funding associated with the given profileGrant id
     * 
     * @param profileGrantId
     *            The id of the orgFundingRelation object
     * 
     * @return the ProfileGrantEntity object
     * */
	ProfileGrantEntity getOrgFundingRelation(String profileGrantId);
}
