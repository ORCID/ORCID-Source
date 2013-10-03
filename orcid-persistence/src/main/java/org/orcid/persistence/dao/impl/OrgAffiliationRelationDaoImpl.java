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
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.springframework.transaction.annotation.Transactional;

public class OrgAffiliationRelationDaoImpl extends GenericDaoImpl<OrgAffiliationRelationEntity, Long> implements OrgAffiliationRelationDao {

    public OrgAffiliationRelationDaoImpl() {
        super(OrgAffiliationRelationEntity.class);
    }

    /**
     * Removes the relationship that exists between a affiliation and a profile.
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffiliationRelation that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    @Override
    @Transactional
    public boolean removeOrgAffiliationRelation(String clientOrcid, String orgAffiliationRelationId) {
        Query query = entityManager.createQuery("delete from OrgAffiliationRelationEntity where profile.id=:clientOrcid and id=:orgAffiliationRelationId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("orgAffiliationRelationId", Long.valueOf(orgAffiliationRelationId));
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Updates the visibility of an existing profile affiliation relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffiliationRelation that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile affiliation relationship
     * 
     * @return true if the relationship was updated
     * */
    @Override
    @Transactional
    public boolean updateOrgAffiliationRelation(String clientOrcid, String orgAffiliationRelationId, Visibility visibility) {
        Query query = entityManager
                .createQuery("update OrgAffiliationRelationEntity set visibility=:visibility, lastModified=now() where profile.id=:clientOrcid and id=:orgAffiliationRelationId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("orgAffiliationRelationId", Long.valueOf(orgAffiliationRelationId));
        query.setParameter("visibility", visibility);
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Get the affiliation associated with the client orcid and the orgAffiliationRelationId
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffiliationRelation that will be updated
     * 
     * @return the profileOrgAffiliationRelation object
     * */
    @Override
    @Transactional
    public OrgAffiliationRelationEntity getOrgAffiliationRelation(String clientOrcid, String orgAffiliationRelationId) {
        Query query = entityManager.createQuery("from OrgAffiliationRelationEntity where profile.id=:clientOrcid and id=:orgAffiliationRelationId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("orgAffiliationRelationId", Long.valueOf(orgAffiliationRelationId));
        return (OrgAffiliationRelationEntity) query.getSingleResult();
    }

    /**
     * Creates a new profile entity relationship between the provided affiliation and
     * the given profile.
     * 
     * @param orcid
     *            The profile id
     * 
     * @param orgAffiliationRelationId
     *            The orgAffiliationRelation id
     * 
     * @param visibility
     *            The orgAffiliationRelation visibility
     * 
     * @return true if the profile orgAffiliationRelation relationship was created
     * */
    @Override
    @Transactional
    public boolean addOrgAffiliationRelation(String clientOrcid, long orgAffiliationRelationId, Visibility visibility) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO org_affiliation_relation(orcid, id, date_created, last_modified, added_to_profile_date, visibility, source_id) values(:orcid, :orgAffiliationRelationId, now(), now(), now(), :visibility, :sourceId)");
        query.setParameter("orcid", clientOrcid);
        query.setParameter("orgAffiliationRelationId", orgAffiliationRelationId);
        query.setParameter("visibility", visibility.name());
        query.setParameter("sourceId", clientOrcid);

        return query.executeUpdate() > 0 ? true : false;
    }

}
