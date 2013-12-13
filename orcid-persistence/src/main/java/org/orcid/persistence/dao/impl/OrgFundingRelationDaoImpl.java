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
import org.orcid.persistence.dao.OrgFundingRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgFundingRelationEntity;

public class OrgFundingRelationDaoImpl extends GenericDaoImpl<OrgFundingRelationEntity, Long> implements OrgFundingRelationDao {
	
	public OrgFundingRelationDaoImpl() {
        super(OrgFundingRelationEntity.class);
    }
	
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
	public boolean removeOrgFundingRelation(String clientOrcid, String orgFundingRelationId) {
		Query query = entityManager.createQuery("delete from OrgFundingRelationEntity where profile.id=:clientOrcid and id=:orgFundingRelationId");
		query.setParameter("clientOrcid", clientOrcid);
		query.setParameter("orgFundingRelationId", Long.valueOf(orgFundingRelationId));
		return query.executeUpdate() > 0 ? true : false;
	}
	
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
	public boolean updateOrgFundingRelation(String clientOrcid, String orgFundingRelationId, Visibility visibility) {
		
	}
	
	/**
	 * Creates a new funding relationship between an organization and a profile.
	 * @param newOrgFundingRelationEntity
	 * 		The object to be persisted
	 * @return the created OrgFundingRelationEntity with the id assigned on database
	 * */	
	public OrgFundingRelationEntity addOrgFundingRelation(OrgFundingRelationEntity newOrgFundingRelationEntity) {
		
	}
	
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
	public OrgFundingRelationEntity getOrgFundingRelation(String orgId, String clientOrcid) {
		
	}
	
	/**
     * Get the funding associated with the given orgFundingRelationId
     * 
     * @param orgFundingRelationId
     *            The id of the orgFundingRelation object
     * 
     * @return the OrgFundingRelationEntity object
     * */
	public OrgFundingRelationEntity getOrgFundingRelation(String orgFundingRelationId) {
		
	}
}
