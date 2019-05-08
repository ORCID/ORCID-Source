package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class OrgAffiliationRelationDaoImpl extends GenericDaoImpl<OrgAffiliationRelationEntity, Long> implements OrgAffiliationRelationDao {

    private static final String AFFILIATION_TYPE_DISTINCTION = "DISTINCTION";
    
    private static final String AFFILIATION_TYPE_EDUCATION = "EDUCATION";

    private static final String AFFILIATION_TYPE_EMPLOYMENT = "EMPLOYMENT";
    
    private static final String AFFILIATION_TYPE_INVITED_POSITION = "INVITED_POSITION";
    
    private static final String AFFILIATION_TYPE_MEMBERSHIP = "MEMBERSHIP";
    
    private static final String AFFILIATION_TYPE_QUALIFICATION = "QUALIFICATION";
    
    private static final String AFFILIATION_TYPE_SERVICE = "SERVICE";
    
    public OrgAffiliationRelationDaoImpl() {
        super(OrgAffiliationRelationEntity.class);
    }

    /**
     * Removes the relationship that exists between a affiliation and a profile.
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffiliationRelation that will be removed from the client
     *            profile
     * @param userOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    @Override
    @Transactional
    public boolean removeOrgAffiliationRelation(String userOrcid, Long orgAffiliationRelationId) {
        Query query = entityManager.createQuery("delete from OrgAffiliationRelationEntity where profile.id=:userOrcid and id=:orgAffiliationRelationId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("orgAffiliationRelationId", orgAffiliationRelationId);
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Updates the visibility of a single existing profile affiliation relationship
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
    public boolean updateVisibilityOnOrgAffiliationRelation(String userOrcid, Long orgAffiliationRelationId, String visibility) {
        Query query = entityManager
                .createQuery("update OrgAffiliationRelationEntity set visibility=:visibility, lastModified=now() where profile.id=:userOrcid and id=:orgAffiliationRelationId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("orgAffiliationRelationId", orgAffiliationRelationId);
        query.setParameter("visibility", visibility);
        return query.executeUpdate() > 0 ? true : false;
    }
    
    /**
     * Updates the visibility of multiple existing profile affiliation relationships
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param orgAffiliationRelationIds
     *            List of ids of orgAffiliationRelations that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile affiliation relationships
     * 
     * @return true if each relationship was updated
     * */
    @Override
    public boolean updateVisibilitiesOnOrgAffiliationRelation(String userOrcid, ArrayList<Long> orgAffiliationRelationIds, String visibility) {
        Query query = entityManager
                .createQuery("update OrgAffiliationRelationEntity set visibility=:visibility, lastModified=now() where profile.id=:userOrcid and id in (:orgAffiliationRelationIds)");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("orgAffiliationRelationIds", orgAffiliationRelationIds);
        query.setParameter("visibility", visibility);
        return query.executeUpdate() > 0 ? true : false;
    }
    
    /**
     * Get the affiliation associated with the client orcid and the orgAffiliationRelationId
     * 
     * @param userOrcid
     *            The user orcid
     * 
     * @param orgAffiliationRelationId
     *            The id of the orgAffiliationRelation that will be updated
     * 
     * @return the profileOrgAffiliationRelation object
     * */
    @Override
    @Transactional
    public OrgAffiliationRelationEntity getOrgAffiliationRelation(String userOrcid, Long orgAffiliationRelationId) {
        Query query = entityManager.createQuery("from OrgAffiliationRelationEntity where profile.id=:userOrcid and id=:orgAffiliationRelationId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("orgAffiliationRelationId",orgAffiliationRelationId);
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
    public boolean addOrgAffiliationRelation(String clientOrcid, long orgAffiliationRelationId, String visibility) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO org_affiliation_relation(orcid, id, date_created, last_modified, added_to_profile_date, visibility, source_id) values(:orcid, :orgAffiliationRelationId, now(), now(), now(), :visibility, :sourceId)");
        query.setParameter("orcid", clientOrcid);
        query.setParameter("orgAffiliationRelationId", orgAffiliationRelationId);
        query.setParameter("visibility", visibility);
        query.setParameter("sourceId", clientOrcid);

        return query.executeUpdate() > 0 ? true : false;
    }
    
    /**
     * Updates an existing OrgAffiliationRelationEntity
     * 
     * @param OrgAffiliationRelationEntity
     *          The entity to update
     * @return the updated OrgAffiliationRelationEntity
     * */
    public OrgAffiliationRelationEntity updateOrgAffiliationRelationEntity(OrgAffiliationRelationEntity orgAffiliationRelationEntity) {
        OrgAffiliationRelationEntity toUpdate = this.find(orgAffiliationRelationEntity.getId());
        mergeOrgAffiliationRelationEntity(toUpdate, orgAffiliationRelationEntity);
        toUpdate = this.merge(toUpdate);
        return toUpdate;
    }
    
    private void mergeOrgAffiliationRelationEntity(OrgAffiliationRelationEntity existing, OrgAffiliationRelationEntity updated) {
        existing.setDepartment(updated.getDepartment());
        existing.setEndDate(updated.getEndDate());        
        existing.setOrg(updated.getOrg());
        existing.setStartDate(updated.getStartDate());
        existing.setTitle(updated.getTitle());
        existing.setVisibility(updated.getVisibility());
        existing.setLastModified(new Date());
    }

    /**
     * Deletes all org affiliations where the source matches the give app id
     * @param clientSourceId the app id
     * */
    @Override
    @Transactional
    public void removeOrgAffiliationByClientSourceId(String clientSourceId) {
        Query query = entityManager.createNativeQuery("DELETE FROM org_affiliation_relation WHERE client_source_id=:clientSourceId");
        query.setParameter("clientSourceId", clientSourceId);
        query.executeUpdate();
    }
    
    @Override
    @Cacheable(value = "distinctions-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<OrgAffiliationRelationEntity> getDistinctionSummaries(String userOrcid, long lastModified) {
        return getByUserAndType(userOrcid, AFFILIATION_TYPE_DISTINCTION);
    }
    
    @Override
    @Cacheable(value = "educations-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<OrgAffiliationRelationEntity> getEducationSummaries(String userOrcid, long lastModified) {
        return getByUserAndType(userOrcid, AFFILIATION_TYPE_EDUCATION);
    }
    
    @Override
    @Cacheable(value = "employments-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<OrgAffiliationRelationEntity> getEmploymentSummaries(String userOrcid, long lastModified) {
        return getByUserAndType(userOrcid, AFFILIATION_TYPE_EMPLOYMENT);
    }
    
    @Override
    @Cacheable(value = "invited-positions-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<OrgAffiliationRelationEntity> getInvitedPositionSummaries(String userOrcid, long lastModified) {
        return getByUserAndType(userOrcid, AFFILIATION_TYPE_INVITED_POSITION);
    }
    
    @Override
    @Cacheable(value = "memberships-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<OrgAffiliationRelationEntity> getMembershipSummaries(String userOrcid, long lastModified) {
        return getByUserAndType(userOrcid, AFFILIATION_TYPE_MEMBERSHIP);
    }
    
    @Override
    @Cacheable(value = "qualifications-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<OrgAffiliationRelationEntity> getQualificationSummaries(String userOrcid, long lastModified) {
        return getByUserAndType(userOrcid, AFFILIATION_TYPE_QUALIFICATION);
    }
    
    @Override
    @Cacheable(value = "services-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<OrgAffiliationRelationEntity> getServiceSummaries(String userOrcid, long lastModified) {
        return getByUserAndType(userOrcid, AFFILIATION_TYPE_SERVICE);
    }
    
    /**
     * Get all affiliations that belongs to a user and matches given type
     * @param userOrcid
     *          The owner of the affiliation
     * @param type
     *          The affiliation type
     * @return a list of all affiliations that belongs to the given user and matches the given type                 
     * */
    @Override
    public List<OrgAffiliationRelationEntity> getByUserAndType(String userOrcid, String type) {
        TypedQuery<OrgAffiliationRelationEntity> query = entityManager.createQuery("from OrgAffiliationRelationEntity where profile.id=:userOrcid and affiliationType=:affiliationType", OrgAffiliationRelationEntity.class);
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("affiliationType", type);
        return query.getResultList();
    }        
    
    /**
     * Get all affiliations that belongs to the given user
     * @param orcid: the user id
     * @return the list of affiliations that belongs to the user
     * */
    @Override
    public List<OrgAffiliationRelationEntity> getByUser(String orcid) {
        TypedQuery<OrgAffiliationRelationEntity> query = entityManager.createQuery("from OrgAffiliationRelationEntity where profile.id=:orcid order by dateCreated asc", OrgAffiliationRelationEntity.class);
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public void removeAllAffiliations(String orcid) {
        Query query = entityManager.createQuery("delete from OrgAffiliationRelationEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public Boolean updateToMaxDisplay(String orcid, Long putCode) {
        Query query = entityManager.createNativeQuery("UPDATE org_affiliation_relation SET display_index=(select coalesce(MAX(display_index) + 1, 0) from org_affiliation_relation where orcid=:orcid and id != :putCode and org_affiliation_relation_role = (select org_affiliation_relation_role from org_affiliation_relation where id = :putCode)), last_modified=now() WHERE id=:putCode");        
        query.setParameter("putCode", putCode);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    @Override
    public Boolean hasPublicAffiliations(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM org_affiliation_relation WHERE orcid=:orcid AND visibility='PUBLIC'");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger)query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM org_affiliation_relation WHERE client_source_id = source_id AND client_source_id IN :nonPublicClients");
        query.setParameter("nonPublicClients", nonPublicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctClientSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE org_affiliation_relation SET client_source_id = source_id, source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients) {
        Query query = entityManager.createNativeQuery("SELECT id FROM org_affiliation_relation WHERE client_source_id = source_id AND client_source_id IN :publicClients");
        query.setParameter("publicClients", publicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctUserSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE org_affiliation_relation SET source_id = client_source_id, client_source_id = NULL where id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }
}
