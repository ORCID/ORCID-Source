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

import javax.persistence.Query;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileGrantDao;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.springframework.transaction.annotation.Transactional;

public class ProfileGrantDaoImpl extends GenericDaoImpl<ProfileFundingEntity, Long> implements ProfileGrantDao {
	
	public ProfileGrantDaoImpl() {
        super(ProfileFundingEntity.class);
    }
	
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
	@Override
	@Transactional
	public boolean removeProfileGrant(String clientOrcid, String profileGrantId) {
		Query query = entityManager.createQuery("delete from ProfileGrantEntity where profile.id=:clientOrcid and id=:profileGrantId");
		query.setParameter("clientOrcid", clientOrcid);
		query.setParameter("profileGrantId", Long.valueOf(profileGrantId));
		return query.executeUpdate() > 0 ? true : false;
	}
	
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
	@Override
	@Transactional
	public boolean updateProfileGrant(String clientOrcid, String profileGrantId, Visibility visibility) {
		Query query = entityManager.createQuery("update ProfileGrantEntity set visibility=:visibility where profile.id=:clientOrcid and id=:profileGrantId");
		query.setParameter("clientOrcid", clientOrcid);
		query.setParameter("profileGrantId", Long.valueOf(profileGrantId));
		query.setParameter("visibility", visibility);
		return query.executeUpdate() > 0 ? true : false;
	}
	
	/**
	 * Creates a new profile grant relationship between an organization and a profile.
	 * 
	 * @param newProfileGrantEntity
	 * 		The object to be persisted
	 * @return the created newProfileGrantEntity with the id assigned on database
	 * */
	@Override
	@Transactional
	public ProfileFundingEntity addProfileGrant(ProfileFundingEntity newProfileGrantEntity) {
		entityManager.persist(newProfileGrantEntity);
		return newProfileGrantEntity;
	}
	
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
	@Override
	public ProfileFundingEntity getProfileGrantEntity(String orgId, String clientOrcid) {
		Query query = entityManager.createQuery("from ProfileGrantEntity where profile.id=:clientOrcid and org.id=:orgId");
		query.setParameter("clientOrcid", clientOrcid);
		query.setParameter("orgId", Long.valueOf(orgId));
		return (ProfileFundingEntity)query.getSingleResult();
	}
	
	/**
     * Get the grant associated with the given profileGrant id
     * 
     * @param profileGrantId
     *            The id of the ProfileGrantEntity object
     * 
     * @return the ProfileGrantEntity object
     * */
	@Override
	public ProfileFundingEntity getProfileGrantEntity(String profileGrantId) {
		Query query = entityManager.createQuery("from ProfileGrantEntity where id=:id");
		query.setParameter("id", Long.valueOf(profileGrantId));
		return (ProfileFundingEntity) query.getSingleResult();
	}
}
