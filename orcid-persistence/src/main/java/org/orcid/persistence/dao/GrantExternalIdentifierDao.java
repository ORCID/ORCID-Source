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

import org.orcid.persistence.jpa.entities.GrantExternalIdentifierEntity;

public interface GrantExternalIdentifierDao extends
		GenericDao<GrantExternalIdentifierEntity, Long> {
	/**
	 * Removes a grant external identifier
	 * 
	 * @param id
	 *            The id of the external identifier
	 * @return true if the external identifier was deleted
	 * */
	boolean removeGrantExternalIdentifier(String id);

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
	boolean updateGrantExternalIdentifier(String id, String type,
			String value, String url);

	/**
	 * Creates a new grant external identifier
	 * 
	 * @param newGrantExternalIdentifierEntity
	 *            The object to be persisted
	 * @return the created GrantExternalIdentifierEntity with the id assigned
	 *         on database
	 * */
	GrantExternalIdentifierEntity createGrantExternalIdentifier(
			GrantExternalIdentifierEntity newGrantExternalIdentifierEntity);

	/**
	 * Get the external identifier associated with the provided id
	 * 
	 * @param id
	 *            the grant external identifier id
	 * 
	 * @return the GrantExternalIdentifierEntity object associated with the
	 *         provided id
	 * */
	GrantExternalIdentifierEntity getGrantExternalIdentifier(String id);

	/**
	 * Get the list of external identifiers associated with the given
	 * profileGrant id
	 * 
	 * @param orgGrantRelationId
	 *            The id of the orgGrantRelation object
	 * 
	 * @return the GrantExternalIdentifierEntity object
	 * */
	List<GrantExternalIdentifierEntity> getGrantExternalIdentifiers(
			String profileGrantId);
}
