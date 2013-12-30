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

import java.util.List;

import org.orcid.persistence.jpa.entities.FundingExternalIdentifierEntity;

public interface FundingExternalIdentifierDao extends
		GenericDao<FundingExternalIdentifierEntity, Long> {
	/**
	 * Removes a funding external identifier
	 * 
	 * @param id
	 *            The id of the external identifier
	 * @return true if the external identifier was deleted
	 * */
	boolean removeFundingExternalIdentifier(String id);

	/**
	 * Updates an external identifier
	 * 
	 * @param id
	 *            The id of the external identifier
	 * 
	 * @param type
	 *            the new type
	 * 
	 * @param value
	 *            the new value
	 * 
	 * @param url
	 *            The new url
	 * 
	 * @return true if the external identifier was updated
	 * */
	boolean updateFundingExternalIdentifier(String id, String type,
			String value, String url);

	/**
	 * Creates a new funding external identifier
	 * 
	 * @param newFundingExternalIdentifierEntity
	 *            The object to be persisted
	 * @return the created FundingExternalIdentifierEntity with the id assigned
	 *         on database
	 * */
	FundingExternalIdentifierEntity createFundingExternalIdentifier(
			FundingExternalIdentifierEntity newFundingExternalIdentifierEntity);

	/**
	 * Get the external identifier associated with the provided id
	 * 
	 * @param id
	 *            the funding external identifier id
	 * 
	 * @return the FundingExternalIdentifierEntity object associated with the
	 *         provided id
	 * */
	FundingExternalIdentifierEntity getFundingExternalIdentifier(String id);

	/**
	 * Get the list of external identifiers associated with the given
	 * profileFunding id
	 * 
	 * @param orgFundingRelationId
	 *            The id of the orgFundingRelation object
	 * 
	 * @return the FundingExternalIdentifierEntity object
	 * */
	List<FundingExternalIdentifierEntity> getFundingExternalIdentifiers(
			String profileFundingId);
}
