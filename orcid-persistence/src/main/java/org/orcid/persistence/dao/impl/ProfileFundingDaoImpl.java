package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ProfileFundingDaoImpl extends GenericDaoImpl<ProfileFundingEntity, Long> implements ProfileFundingDao {

    public ProfileFundingDaoImpl() {
        super(ProfileFundingEntity.class);
    }

    /**
     * Find and retrieve a profile funding that have the given id and belongs to the given user
     * 
     * @param userOrcid
     *            The owner of the funding
     * @param profileFundingId
     *            The id of the element
     * @return a profile funding entity that have the give id and belongs to the given user 
     * */
    @Override
    public ProfileFundingEntity getProfileFunding(String userOrcid, Long profileFundingId) {
        Query query = entityManager.createQuery("from ProfileFundingEntity where profile.id=:userOrcid and id=:profileFundingId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("profileFundingId", profileFundingId);
        return (ProfileFundingEntity) query.getSingleResult();
    }
    
    /**
     * Removes the relationship that exists between a funding and a profile.
     * 
     * @param profileFundingId
     *            The id of the profileFunding that will be removed from the
     *            client profile
     * @param userOrcid
     *            The user orcid
     * @return true if the relationship was deleted
     * */
    @Override
    @Transactional
    public boolean removeProfileFunding(String userOrcid, Long profileFundingId) {
        Query query = entityManager.createQuery("delete from ProfileFundingEntity where profile.id=:userOrcid and id=:profileFundingId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("profileFundingId", profileFundingId);
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Updates the visibility of an existing profile funding relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileFundingId
     *            The id of the profile funding that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileFunding object
     * 
     * @return true if the relationship was updated
     * */
    @Override
    @Transactional
    public boolean updateProfileFundingVisibility(String clientOrcid, Long profileFundingId, String visibility) {
        Query query = entityManager.createQuery("update ProfileFundingEntity set visibility=:visibility where profile.id=:clientOrcid and id=:profileFundingId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("profileFundingId", profileFundingId);
        query.setParameter("visibility", visibility);
        return query.executeUpdate() > 0 ? true : false;
    }
    
    /**
     * Updates the visibility of multiple existing profile funding relationships
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileFundingIds
     *            The ids of the profile fundings that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileFunding object
     * 
     * @return true if the relationships were updated
     * */
    @Override
    @Transactional
    public boolean updateProfileFundingVisibilities(String clientOrcid, ArrayList<Long> profileFundingIds, String visibility) {
        Query query = entityManager.createQuery("update ProfileFundingEntity set visibility=:visibility where profile.id=:clientOrcid and id in (:profileFundingIds)");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("profileFundingIds", profileFundingIds);
        query.setParameter("visibility", visibility);
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Creates a new profile funding relationship between an organization and a
     * profile.
     * 
     * @param newProfileFundingEntity
     *            The object to be persisted
     * @return the created newProfileFundingEntity with the id assigned on
     *         database
     * */
    @Override
    @Transactional
    public ProfileFundingEntity addProfileFunding(ProfileFundingEntity newProfileFundingEntity) {
        entityManager.persist(newProfileFundingEntity);
        return newProfileFundingEntity;
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
     * @return the ProfileFundingEntity object
     * */
    @Override
    public ProfileFundingEntity getProfileFundingEntity(String orgId, String clientOrcid) {
        Query query = entityManager.createQuery("from ProfileFundingEntity where profile.id=:clientOrcid and org.id=:orgId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("orgId", Long.valueOf(orgId));
        return (ProfileFundingEntity) query.getSingleResult();
    }

    /**
     * Get the funding associated with the given profileFunding id
     * 
     * @param profileFundingId
     *            The id of the ProfileFundingEntity object
     * 
     * @return the ProfileFundingEntity object
     * */
    @Override
    public ProfileFundingEntity getProfileFundingEntity(Long profileFundingId) {
        Query query = entityManager.createQuery("from ProfileFundingEntity where id=:id");
        query.setParameter("id", profileFundingId);
        return (ProfileFundingEntity) query.getSingleResult();
    }

    /**
     * Get all the profile fundings where the amount is not null
     * 
     * @return a list of all profile fundings where the amount is not null
     * */
    public List<ProfileFundingEntity> getProfileFundingWithAmount() {
        TypedQuery<ProfileFundingEntity> query = entityManager.createQuery("from ProfileFundingEntity where amount is not null and numeric_amount is null", ProfileFundingEntity.class);
        return query.getResultList();
    }
    
    /**
     * Edits a profileFunding
     * 
     * @param profileFunding
     *            The profileFunding to be edited
     * @return the updated profileFunding
     * */
    @Override
    @Transactional
    public ProfileFundingEntity updateProfileFunding(ProfileFundingEntity profileFunding) {
        ProfileFundingEntity toUpdate = this.find(profileFunding.getId());
        mergeProfileFunding(toUpdate, profileFunding);
        toUpdate = this.merge(toUpdate);
        return toUpdate;
    }
    
    private void mergeProfileFunding(ProfileFundingEntity existing, ProfileFundingEntity updated) {
        existing.setContributorsJson(updated.getContributorsJson());
        existing.setCurrencyCode(updated.getCurrencyCode());
        existing.setDescription(updated.getDescription());
        existing.setEndDate(updated.getEndDate());
        existing.setExternalIdentifiersJson(updated.getExternalIdentifiersJson());        
        existing.setNumericAmount(updated.getNumericAmount());
        existing.setOrg(updated.getOrg());
        existing.setOrganizationDefinedType(updated.getOrganizationDefinedType());
        existing.setStartDate(updated.getStartDate());
        existing.setTitle(updated.getTitle());
        existing.setTranslatedTitle(updated.getTranslatedTitle());
        existing.setTranslatedTitleLanguageCode(updated.getTranslatedTitleLanguageCode());
        existing.setType(updated.getType());
        existing.setUrl(updated.getUrl());
        existing.setVisibility(updated.getVisibility());   
        existing.setLastModified(new Date());
    }
    
    @Override
    @Transactional
    public boolean updateToMaxDisplay(String orcid, Long id) {
        Query query = entityManager.createNativeQuery("UPDATE profile_funding SET display_index = (select coalesce(MAX(display_index) + 1, 0) from profile_funding where orcid=:orcid and id != :id ), last_modified=now() WHERE id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", id);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @SuppressWarnings("unchecked")    
    public List<BigInteger> findFundingNeedingExternalIdentifiersMigration(int chunkSize) {
        Query query = entityManager.createNativeQuery("SELECT id FROM profile_funding WHERE id IN (SELECT profile_funding_id FROM funding_external_identifier) AND external_identifiers_json IS NULL LIMIT :chunkSize");
        query.setParameter("chunkSize", chunkSize);                
        List<BigInteger> results = query.getResultList();        
        return results;
    }
    
    @Override
    @Transactional
    public void setFundingExternalIdentifiersInJson(BigInteger id, String extIdsJson) {
        Query query = entityManager.createNativeQuery("UPDATE profile_funding SET external_identifiers_json=:extIdsJson WHERE id=:id");
        query.setParameter("id", id);
        query.setParameter("extIdsJson", extIdsJson);
        query.executeUpdate();
    }
    
    /**
     * Deletes all funding where the source matches the give app id
     * @param clientSourceId the app id
     * */
    @Override
    @Transactional
    public void removeFundingByClientSourceId(String clientSourceId) {
        Query query = entityManager.createNativeQuery("DELETE FROM profile_funding WHERE client_source_id=:clientSourceId");
        query.setParameter("clientSourceId", clientSourceId);
        query.executeUpdate();
    }
    
    @Override
    @Cacheable(value = "fundings", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<ProfileFundingEntity> getByUser(String userOrcid, long lastModified) {
        TypedQuery<ProfileFundingEntity> query = entityManager.createQuery("from ProfileFundingEntity where profile.id=:userOrcid order by displayIndex desc, dateCreated asc", ProfileFundingEntity.class);
        query.setParameter("userOrcid", userOrcid);
        return query.getResultList();
    }
    
    /**
     * Returns a list of external ids of fundings that still have old external identifiers
     * @param limit
     *          The batch number to fetch
     * @return a list of funding ids with old ext ids          
     * */
    @Override
    @SuppressWarnings("unchecked")   
    public List<BigInteger> getFundingWithOldExtIds(long limit) {
        Query query = entityManager.createNativeQuery("SELECT distinct(id) FROM (SELECT id, json_array_elements(json_extract_path(external_identifiers_json, 'fundingExternalIdentifier')) AS j FROM profile_funding WHERE external_identifiers_json is not null limit :limit) AS a WHERE (j->'relationship') is null;");
        query.setParameter("limit", limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public boolean increaseDisplayIndexOnAllElements(String orcid) {
        Query query = entityManager.createNativeQuery("update profile_funding set display_index=(display_index + 1), last_modified=now() where orcid=:orcid");                
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    @Override
    @Transactional
    public void removeAllFunding(String orcid) {
        Query query = entityManager.createQuery("delete from ProfileFundingEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }
    
    @Override
    public Boolean hasPublicFunding(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM profile_funding WHERE orcid=:orcid AND visibility='PUBLIC'");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger)query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }
}
