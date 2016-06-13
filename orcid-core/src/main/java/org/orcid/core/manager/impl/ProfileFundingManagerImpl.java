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
package org.orcid.core.manager.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.orcid.core.adapter.JpaJaxbFundingAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.notification.amended_rc2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_rc2.Item;
import org.orcid.jaxb.model.notification.permission_rc2.ItemType;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.persistence.dao.FundingSubTypeSolrDao;
import org.orcid.persistence.dao.FundingSubTypeToIndexDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.solr.entities.OrgDefinedFundingTypeSolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class ProfileFundingManagerImpl implements ProfileFundingManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileFundingManagerImpl.class);
    
    @Resource
    private ProfileFundingDao profileFundingDao;
    
    @Resource 
    private FundingSubTypeToIndexDao fundingSubTypeToIndexDao;
    
    @Resource
    private FundingSubTypeSolrDao fundingSubTypeSolrDao;
    
    @Resource
    private JpaJaxbFundingAdapter jpaJaxbFundingAdapter;
    
    @Resource
    private OrgManager orgManager;
    
    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource 
    private ActivityValidator activityValidator;
    
    @Override
    public void setSourceManager(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }
    
    @Resource
    private NotificationManager notificationManager;
    
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
    public boolean removeProfileFunding(String clientOrcid, Long profileFundingId) {
        return profileFundingDao.removeProfileFunding(clientOrcid, profileFundingId);
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
    public boolean updateProfileFundingVisibility(String clientOrcid, Long profileFundingId, Visibility visibility) {
        return profileFundingDao.updateProfileFundingVisibility(clientOrcid, profileFundingId, visibility);
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
    public ProfileFundingEntity addProfileFunding(ProfileFundingEntity newProfileFundingEntity) {
        return profileFundingDao.addProfileFunding(newProfileFundingEntity);
    }
    
    /**
     * Add a new funding subtype to the list of pending for indexing subtypes
     * */
    public void addFundingSubType(String subtype, String orcid) {
        fundingSubTypeToIndexDao.addSubTypes(subtype, orcid);
    }
    
    /**
     * Looks for the org defined funding subtypes that matches a given pattern
     * @param subtype pattern to look for
     * @param limit the max number of results to look for
     * @return a list of all org defined funding subtypes that matches the given pattern
     * */
    public List<String> getIndexedFundingSubTypes(String subtype, int limit) {
        List<OrgDefinedFundingTypeSolrDocument> types = fundingSubTypeSolrDao.getFundingTypes(subtype, 0, 100); 
        List<String> result = new ArrayList<String>();
        for (OrgDefinedFundingTypeSolrDocument type : types) {
            result.add(type.getOrgDefinedFundingType());
        }
        return result; 
    }
    
    /**
     * A process that will process all funding subtypes, filter and index them. 
     * */
    public void indexFundingSubTypes() {
        LOGGER.info("Indexing funding subtypes");
        List<String> subtypes = fundingSubTypeToIndexDao.getSubTypes();
        List<String> wordsToFilter = new ArrayList<String>();
        try {
            wordsToFilter = IOUtils.readLines(getClass().getResourceAsStream("words_to_filter.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Problem reading words_to_filter.txt from classpath", e);
        }
        for(String subtype : subtypes) {                   
            try {
                boolean isInappropriate = false;
                //All filter words are in lower case, so, lowercase the subtype before comparing
                for(String wordToFilter : wordsToFilter) {
                    if(wordToFilter.matches(".*\\b" + Pattern.quote(subtype) + "\\b.*")) {
                        isInappropriate = true;
                        break;
                    }
                }
                
                if(!isInappropriate){
                    OrgDefinedFundingTypeSolrDocument document = new OrgDefinedFundingTypeSolrDocument();
                    document.setOrgDefinedFundingType(subtype);
                    fundingSubTypeSolrDao.persist(document);              
                } else {
                    LOGGER.warn("A word have been flaged as inappropiate: " + subtype);
                }
                fundingSubTypeToIndexDao.removeSubTypes(subtype);
            } catch (Exception e) {
                //If any exception happens, log the error and continue with the next one
                LOGGER.warn("Unable to process subtype " + subtype, e);                
            }                        
        }
        LOGGER.info("Funding subtypes have been correcly indexed");
    }
    
    /**
     * Get the funding associated with the given profileFunding id
     * 
     * @param profileFundingId
     *            The id of the ProfileFundingEntity object
     * 
     * @return the ProfileFundingEntity object
     * */
    public ProfileFundingEntity getProfileFundingEntity(Long profileFundingId) {
        return profileFundingDao.getProfileFundingEntity(profileFundingId);
    }
    
    /**
     * Update an existing profile funding relationship between an organization and a
     * profile.
     * 
     * @param updatedProfileFundingEntity
     *            The object to be persisted
     * @return the updated profileFundingEntity
     * */
    public ProfileFundingEntity updateProfileFunding(ProfileFundingEntity updatedProfileFundingEntity) {
        return profileFundingDao.updateProfileFunding(updatedProfileFundingEntity);
    }
    
    public boolean updateToMaxDisplay(String orcid, Long fundingId) {
        return profileFundingDao.updateToMaxDisplay(orcid, fundingId);
    }
    
    /**
     * Get a funding based on the orcid and funding id
     * @param orcid
     *          The funding owner
     * @param fundingId
     *          The funding id
     * @return the Funding          
     * */
    @Override
    public Funding getFunding(String orcid, Long fundingId) {
        ProfileFundingEntity profileFundingEntity = profileFundingDao.getProfileFunding(orcid, fundingId); 
        return jpaJaxbFundingAdapter.toFunding(profileFundingEntity);
    }
    
    /**
     * Get a funding summary based on the orcid and funding id
     * @param orcid
     *          The funding owner
     * @param fundingId
     *          The funding id
     * @return the FundingSummary          
     * */
    @Override
    public FundingSummary getSummary(String orcid, Long fundingId) {
        ProfileFundingEntity profileFundingEntity = profileFundingDao.getProfileFunding(orcid, fundingId);
        return jpaJaxbFundingAdapter.toFundingSummary(profileFundingEntity);
    }
    
    /**
     * Add a new funding to the given user
     * @param orcid
     *          The user to add the funding
     * @param funding
     *          The funding to add
     * @return the added funding                  
     * */
    @Override
    @Transactional
    public Funding createFunding(String orcid, Funding funding, boolean isApiRequest) {
    	SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
    	activityValidator.validateFunding(funding, sourceEntity, true, isApiRequest, null);

        //Check for duplicates
        List<ProfileFundingEntity> existingFundings = profileFundingDao.getByUser(orcid);
        List<Funding> fundings = jpaJaxbFundingAdapter.toFunding(existingFundings);
        if(fundings != null && isApiRequest) {
            for(Funding exstingFunding : fundings) {
                activityValidator.checkFundingExternalIdentifiersForDuplicates(funding.getExternalIdentifiers(),
            			exstingFunding.getExternalIdentifiers(), exstingFunding.getSource(), sourceEntity);
            }
        }
                
        ProfileFundingEntity profileFundingEntity = jpaJaxbFundingAdapter.toProfileFundingEntity(funding);
        
        //Updates the give organization with the latest organization from database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(funding);
        profileFundingEntity.setOrg(updatedOrganization);
        
        //Set the source
        if(sourceEntity.getSourceProfile() != null) {
            profileFundingEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        if(sourceEntity.getSourceClient() != null) {
            profileFundingEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        } 
        
        ProfileEntity profile = profileDao.find(orcid);
        profileFundingEntity.setProfile(profile);
        setIncomingWorkPrivacy(profileFundingEntity, profile);
        profileFundingDao.persist(profileFundingEntity);
        profileFundingDao.flush();
        if(isApiRequest) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.FUNDING, createItem(profileFundingEntity));
        }        
        return jpaJaxbFundingAdapter.toFunding(profileFundingEntity);
    }

    private void setIncomingWorkPrivacy(ProfileFundingEntity profileFundingEntity, ProfileEntity profile) {
        Visibility incomingWorkVisibility = profileFundingEntity.getVisibility();
        Visibility defaultWorkVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {            
            profileFundingEntity.setVisibility(defaultWorkVisibility);            
        } else if (incomingWorkVisibility == null) {
            profileFundingEntity.setVisibility(Visibility.PRIVATE);
        }
    }
    
    
    /**
     * Updates a funding that belongs to the given user
     * @param orcid
     *          The user
     * @param funding
     *          The funding to update
     * @return the updated funding                  
     * */
    @Override    
    public Funding updateFunding(String orcid, Funding funding, boolean isApiRequest) {
    	SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
    	ProfileFundingEntity pfe = profileFundingDao.getProfileFunding(orcid, funding.getPutCode());
        Visibility originalVisibility = pfe.getVisibility();
        
        //Save the original source
        String existingSourceId = pfe.getSourceId();
        String existingClientSourceId = pfe.getClientSourceId();
        
        activityValidator.validateFunding(funding, sourceEntity, false, isApiRequest, originalVisibility);
    	if(!isApiRequest) {
    	    List<ProfileFundingEntity> existingFundings = profileFundingDao.getByUser(orcid);
            for(ProfileFundingEntity existingFunding : existingFundings) {
                Funding existing = jpaJaxbFundingAdapter.toFunding(existingFunding);
                if(!existing.getPutCode().equals(funding.getPutCode())) {
                    activityValidator.checkFundingExternalIdentifiersForDuplicates(funding.getExternalIdentifiers(),
                                     existing.getExternalIdentifiers(), existing.getSource(), sourceEntity);
                }
            }
    	}    	
    	        
        orcidSecurityManager.checkSource(pfe);
        
        jpaJaxbFundingAdapter.toProfileFundingEntity(funding, pfe);
        pfe.setVisibility(originalVisibility);
        
        
        //Be sure it doesn't overwrite the source
        pfe.setSourceId(existingSourceId);
        pfe.setClientSourceId(existingClientSourceId);
        
        //Updates the give organization with the latest organization from database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(funding);
        pfe.setOrg(updatedOrganization);
        
        pfe = profileFundingDao.merge(pfe);
        profileFundingDao.flush();
        if(!isApiRequest) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.FUNDING, createItem(pfe));
        }
        return jpaJaxbFundingAdapter.toFunding(pfe);
    }
    
    /**
     * Deletes a given funding, if and only if, the client that requested the delete is the source of the funding
     * @param orcid
     *          the funding owner
     * @param fundingId
     *          The funding id                 
     * @return true if the funding was deleted, false otherwise
     * */
    @Override
    @Transactional    
    public boolean checkSourceAndDelete(String orcid, Long fundingId) {
        ProfileFundingEntity pfe = profileFundingDao.getProfileFunding(orcid, fundingId);
        orcidSecurityManager.checkSource(pfe);        
        Item item = createItem(pfe);
        boolean result = profileFundingDao.removeProfileFunding(orcid, fundingId);
        notificationManager.sendAmendEmail(orcid, AmendedSection.FUNDING, item);
        return result;
    }

    
    /**
     * Get the list of fundings summaries that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of fundings that belongs to this user
     * */
    @Override
    @Cacheable(value = "fundings-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<FundingSummary> getFundingSummaryList(String userOrcid, long lastModified) {
        List<ProfileFundingEntity> fundingEntities = profileFundingDao.getByUser(userOrcid);
        return jpaJaxbFundingAdapter.toFundingSummary(fundingEntities);
    }
    
    /**
     * Get the list of fundings that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of fundings that belongs to this user
     * */
    @Override
    @Cacheable(value = "fundings", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<Funding> getFundingList(String userOrcid, long lastModified) {
        List<ProfileFundingEntity> fundingEntities = profileFundingDao.getByUser(userOrcid);
        return jpaJaxbFundingAdapter.toFunding(fundingEntities);
    }
    
    
    private Item createItem(ProfileFundingEntity profileFundingEntity) {
        Item item = new Item();
        item.setItemName(profileFundingEntity.getTitle());
        item.setItemType(ItemType.FUNDING);
        item.setPutCode(String.valueOf(profileFundingEntity.getId()));
        return item;
    }
    
}
