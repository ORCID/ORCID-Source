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
package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.GrantExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.GrantExternalIdentifierEntity;
import org.springframework.transaction.annotation.Transactional;

public class GrantExternalIdentifierDaoImpl extends GenericDaoImpl<GrantExternalIdentifierEntity, Long> implements GrantExternalIdentifierDao  {

	public GrantExternalIdentifierDaoImpl() {
		super(GrantExternalIdentifierEntity.class);
	}

	/**
	 * Removes a grant external identifier
	 * 
	 * @param id
	 *            The id of the external identifier
	 * @return true if the external identifier was deleted
	 * */
	@Override
	@Transactional
	public boolean removeGrantExternalIdentifier(String id) {
		Query query = entityManager.createQuery("delete from GrantExternalIdentifierEntity where id=:id");
		query.setParameter("id", Long.valueOf(id));
		return query.executeUpdate() > 0 ? true : false;
	}

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
	@Override
	@Transactional
	public boolean updateGrantExternalIdentifier(String id, String type,
			String value, String url) {
		Query query = entityManager.createQuery("update GrantExternalIdentifierEntity set type=:type, value=:value, url=:url where id=:id");
		query.setParameter("id", Long.valueOf(id));
		query.setParameter("type", type);
		query.setParameter("value", value);
		query.setParameter("url", url);		
		return query.executeUpdate() > 0 ? true : false;
	}

	/**
	 * Creates a new grant external identifier
	 * 
	 * @param newGrantExternalIdentifierEntity
	 *            The object to be persisted
	 * @return the created GrantExternalIdentifierEntity with the id assigned
	 *         on database
	 * */
	@Override
	@Transactional
	public GrantExternalIdentifierEntity createGrantExternalIdentifier(
			GrantExternalIdentifierEntity newGrantExternalIdentifierEntity) {
		entityManager.persist(newGrantExternalIdentifierEntity);
		return newGrantExternalIdentifierEntity;	
	}

	/**
	 * Get the external identifier associated with the provided id
	 * 
	 * @param id
	 *            the grant external identifier id
	 * 
	 * @return the GrantExternalIdentifierEntity object associated with the
	 *         provided id
	 * */
	@Override
	public GrantExternalIdentifierEntity getGrantExternalIdentifier(String id) {
		Query query = entityManager.createQuery("from GrantExternalIdentifierEntity where id=:id");
		query.setParameter("id", Long.valueOf(id));
		return (GrantExternalIdentifierEntity)query.getSingleResult();		
	}

	/**
	 * Get the list of external identifiers associated with the given
	 * profileGrantEntity object
	 * 
	 * @param profileGrantId
	 *            The id of the orgGrantRelation object
	 * 
	 * @return the ProfileGrantEntity object
	 * */
	@Override
	public List<GrantExternalIdentifierEntity> getGrantExternalIdentifiers(
			String profileGrantId) {
		Query query = entityManager.createQuery("from GrantExternalIdentifierEntity where orgGrant.id=:id");
		query.setParameter("id", Long.valueOf(profileGrantId));
		return query.getResultList();
	}
}
