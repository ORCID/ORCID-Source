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
import org.orcid.persistence.jpa.entities.OrgFundingRelationEntity;

public interface OrgFundingRelationDao extends GenericDao<OrgFundingRelationEntity, Long> {

	 /**
     * Removes the relationship that exists between a funding and a profile.
     * 
     * @param orgFundingRelationId
     *            The id of the orgFundingRelationId that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
	boolean removeOrgFundingRelation(String clientOrcid, String orgFundingRelationId);
	/**
     * Updates the visibility of an existing profile funding relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgFundingRelationId
     *            The id of the orgFundingRelationId that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile orgFundingRelationId relationship
     * 
     * @return true if the relationship was updated
     * */
	boolean updateOrgFundingRelation(String clientOrcid, String orgFundingRelationId, Visibility visibility);
	/**
	 * Creates a new funding relationship between an organization and a profile.
	 * @param newOrgFundingRelationEntity
	 * 		The object to be persisted
	 * @return the created OrgFundingRelationEntity with the id assigned on database
	 * */
	OrgFundingRelationEntity addOrgFundingRelation(OrgFundingRelationEntity newOrgFundingRelationEntity);
	/**
     * Get the funding associated with the client orcid and the organization id
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgId
     *            The id of the organization
     * 
     * @return the OrgFundingRelationEntity object
     * */
	OrgFundingRelationEntity getOrgFundingRelation(String orgId, String clientOrcid);
	/**
     * Get the funding associated with the given orgFundingRelationId
     * 
     * @param orgFundingRelationId
     *            The id of the orgFundingRelation object
     * 
     * @return the OrgFundingRelationEntity object
     * */
	OrgFundingRelationEntity getOrgFundingRelation(String orgFundingRelationId);
}
