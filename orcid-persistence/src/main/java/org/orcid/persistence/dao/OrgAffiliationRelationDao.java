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
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

public interface OrgAffiliationRelationDao extends GenericDao<OrgAffiliationRelationEntity, Long> {

    /**
     * Removes the relationship that exists between a affiliation and a profile.
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffilationRelation that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    boolean removeOrgAffiliationRelation(String clientOrcid, String orgAffiliationRelationId);

    /**
     * Updates the visibility of an existing profile affiliation relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffilationRelation that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile orgAffilationRelation relationship
     * 
     * @return true if the relationship was updated
     * */
    boolean updateOrgAffiliationRelation(String clientOrcid, String orgAffiliationRelationId, Visibility visibility);

    /**
     * Get the affiliation associated with the client orcid and the orgAffiliationRelationId
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffilationRelation that will be updated
     * 
     * @return the orgAffiliationRelation object
     * */
    OrgAffiliationRelationEntity getOrgAffiliationRelation(String clientOrcid, String orgAffiliationRelationId);

    /**
     * Creates a new profile entity relationship between the provided orgAffilationRelation and
     * the given profile.
     * 
     * @param orcid
     *            The profile id
     * 
     * @param orgAffiliationRelationId
     *            The orgAffilationRelation id
     * 
     * @param visibility
     *            The orgAffilationRelation visibility
     * 
     * @return true if the profile orgAffilationRelation relationship was created
     * */
    boolean addOrgAffiliationRelation(String clientOrcid, long orgAffiliationRelationId, Visibility visibility);

}
