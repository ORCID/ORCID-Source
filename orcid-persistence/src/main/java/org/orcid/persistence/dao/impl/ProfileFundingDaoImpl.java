/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.springframework.transaction.annotation.Transactional;

public class ProfileFundingDaoImpl extends GenericDaoImpl<ProfileFundingEntity, Long> implements ProfileFundingDao {

    public ProfileFundingDaoImpl() {
        super(ProfileFundingEntity.class);
    }

    /**
     * Removes the relationship that exists between a funding and a profile.
     * 
     * @param profileFundingId
     *            The id of the profileFunding that will be removed from the
     *            client profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     * */
    @Override
    @Transactional
    public boolean removeProfileFunding(String clientOrcid, String profileFundingId) {
        Query query = entityManager.createQuery("delete from ProfileFundingEntity where profile.id=:clientOrcid and id=:profileFundingId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("profileFundingId", Long.valueOf(profileFundingId));
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
    public boolean updateProfileFundingVisibility(String clientOrcid, String profileFundingId, Visibility visibility) {
        Query query = entityManager.createQuery("update ProfileFundingEntity set visibility=:visibility where profile.id=:clientOrcid and id=:profileFundingId");
        query.setParameter("clientOrcid", clientOrcid);
        query.setParameter("profileFundingId", Long.valueOf(profileFundingId));
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
    public ProfileFundingEntity getProfileFundingEntity(String profileFundingId) {
        Query query = entityManager.createQuery("from ProfileFundingEntity where id=:id");
        query.setParameter("id", Long.valueOf(profileFundingId));
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
        existing.setExternalIdentifiers(updated.getExternalIdentifiers());
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
    public boolean updateToMaxDisplay(String orcid, String id) {
        Query query = entityManager.createNativeQuery("UPDATE profile_funding SET display_index = (select max(display_index) + 1 from profile_funding where orcid=:orcid and id != :id ) WHERE id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", Long.valueOf(id));
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
}
