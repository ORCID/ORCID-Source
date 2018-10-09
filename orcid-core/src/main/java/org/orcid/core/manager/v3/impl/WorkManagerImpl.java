package org.orcid.core.manager.v3.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.adapter.jsonidentifier.converter.JSONWorkExternalIdentifiersConverterV3;
import org.orcid.core.exception.ExceedMaxNumberOfElementsException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.GroupingSuggestionManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.manager.v3.read_only.GroupingSuggestionManagerReadOnly;
import org.orcid.core.manager.v3.read_only.impl.WorkManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.ActivityValidator;
import org.orcid.core.manager.v3.validator.ExternalIDValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.rc1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.error.OrcidError;
import org.orcid.jaxb.model.v3.rc1.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.rc1.notification.permission.Item;
import org.orcid.jaxb.model.v3.rc1.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

public class WorkManagerImpl extends WorkManagerReadOnlyImpl implements WorkManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkManagerImpl.class);

    @Value("${org.orcid.core.activities.max:10000}")
    private long maxNumOfActivities;
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;
    
    @Resource(name = "externalIDValidatorV3")
    private ExternalIDValidator externalIDValidator;

    @Resource(name = "activityValidatorV3")
    private ActivityValidator activityValidator;
    
    @Resource(name = "groupingSuggestionManagerV3")
    private GroupingSuggestionManager groupingSuggestionManager;
    
    @Resource(name = "groupingSuggestionManagerReadOnlyV3")
    private GroupingSuggestionManagerReadOnly groupingSuggestionManagerReadOnly;
    
    @Resource
    private MessageSource messageSource;
    
    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private PIDNormalizationService norm;
    
    @Value("${org.orcid.core.works.bulk.max:100}")
    private Long maxBulkSize;
    
    public WorkManagerImpl(@Value("${org.orcid.core.works.bulk.max:100}") Integer bulkReadSize) {
        super(bulkReadSize);
    }
    
    /**
     * Updates the visibility of an existing work
     * 
     * @param workId
     *            The id of the work that will be updated
     * @param visibility
     *            The new visibility value for the profile work relationship
     * @return true if the relationship was updated
     * */
    public boolean updateVisibilities(String orcid, List<Long> workIds, Visibility visibility) {
        return workDao.updateVisibilities(orcid, workIds, visibility.name());
    }

    /**
     * Removes a work.
     * 
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the work was deleted
     * */
    public boolean removeWorks(String clientOrcid, List<Long> workIds) {
        return workDao.removeWorks(clientOrcid, workIds);
    }

    @Override
    public void removeAllWorks(String orcid) {
        workDao.removeWorks(orcid);
    }
    
    /**
     * Sets the display index of the new work
     * 
     * @param orcid
     *            The work owner
     * @param workId
     *            The work id
     * @return true if the work index was correctly set
     * */
    public boolean updateToMaxDisplay(String orcid, Long workId) {
        return workDao.updateToMaxDisplay(orcid, workId);
    }

    @Override
    @Transactional
    public Work createWork(String orcid, Work work, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        
        if (isApiRequest) {
            activityValidator.validateWork(work, sourceEntity, true, isApiRequest, null);
            // If it is the user adding the peer review, allow him to add
            // duplicates
            if (!SourceEntityUtils.getSourceId(sourceEntity).equals(orcid)) {
                List<Work> existingWorks = this.findWorks(orcid);       
                if((existingWorks.size() + 1) > this.maxNumOfActivities) {
                    throw new ExceedMaxNumberOfElementsException();
                }
                if (existingWorks != null) {
                    for (Work existing : existingWorks) {
                        activityValidator.checkExternalIdentifiersForDuplicates(work.getExternalIdentifiers(), existing.getExternalIdentifiers(), existing.getSource(),
                                sourceEntity);
                    }
                }
            }
        } else {
            // validate external ID vocab
            externalIDValidator.validateWorkOrPeerReview(work.getExternalIdentifiers());
        }

        WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        workEntity.setOrcid(orcid);
        workEntity.setAddedToProfileDate(new Date());
        
        // Set source id 
        if(sourceEntity.getSourceProfile() != null) {
            workEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        
        if(sourceEntity.getSourceClient() != null) {
            workEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        } 
        
        setIncomingWorkPrivacy(workEntity, profile, isApiRequest);        
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(workEntity, isApiRequest);        
        workDao.persist(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity));
        return jpaJaxbWorkAdapter.toWork(workEntity);
    }

    
    /**
     * Add a list of works to the given profile
     * 
     * @param works
     *            The list of works that want to be added
     * @param orcid
     *            The id of the user we want to add the works to
     * 
     * @return the work bulk with the put codes of the new works or the error
     *         that indicates why a work can't be added
     */
    @Override
    @Transactional
    public WorkBulk createWorks(String orcid, WorkBulk workBulk) {        
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        List<Work> existingWorks = this.findWorks(orcid);
        
        if(workBulk.getBulk() != null && !workBulk.getBulk().isEmpty()) {
            List<BulkElement> bulk = workBulk.getBulk();
            Set<ExternalID> existingExternalIdentifiers = buildExistingExternalIdsSet(existingWorks, SourceEntityUtils.getSourceId(sourceEntity));
            if((existingWorks.size() + bulk.size()) > this.maxNumOfActivities) {
                throw new ExceedMaxNumberOfElementsException();
            }
            //Check bulk size
            if(bulk.size() > maxBulkSize) {
                Locale locale = localeManager.getLocale();                
                throw new IllegalArgumentException(messageSource.getMessage("apiError.validation_too_many_elements_in_bulk.exception", new Object[]{maxBulkSize}, locale));                
            }
                                    
            for(int i = 0; i < bulk.size(); i++) {
                if(Work.class.isAssignableFrom(bulk.get(i).getClass())){
                    Work work = (Work) bulk.get(i);
                    try {
                        activityValidator.validateWork(work, sourceEntity, true, true, null);

                        //Validate it is not duplicated
                        if(work.getExternalIdentifiers() != null) {
                            for(ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                                //normalise the provided ID
                                extId.setNormalized(new TransientNonEmptyString(norm.normalise(extId.getType(), extId.getValue())));                                
                                // If the external id exists and is a SELF identifier, then mark it as duplicated                                
                                if(existingExternalIdentifiers.contains(extId) && Relationship.SELF.equals(extId.getRelationship())) {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("clientName", SourceEntityUtils.getSourceName(sourceEntity));
                                    throw new OrcidDuplicatedActivityException(params);
                                }
                            }
                        }
                        
                        //Save the work
                        WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
                        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
                        workEntity.setOrcid(orcid);
                        workEntity.setAddedToProfileDate(new Date());
                        
                        // Set source id 
                        if(sourceEntity.getSourceProfile() != null) {
                            workEntity.setSourceId(sourceEntity.getSourceProfile().getId());
                        }
                        
                        if(sourceEntity.getSourceClient() != null) {
                            workEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
                        } 
                        
                        setIncomingWorkPrivacy(workEntity, profile);        
                        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(workEntity, true);        
                        workDao.persist(workEntity);                    
                        
                        //Update the element in the bulk
                        Work updatedWork = jpaJaxbWorkAdapter.toWork(workEntity);
                        bulk.set(i, updatedWork);
                        
                        //Add the work extIds to the list of existing external identifiers
                        addExternalIdsToExistingSet(updatedWork, existingExternalIdentifiers);
                    } catch(Exception e) {
                        //Get the exception 
                        OrcidError orcidError = orcidCoreExceptionMapper.getV3OrcidError(e);
                        bulk.set(i, orcidError);
                    }                                        
                }
            }
            
            workDao.flush();            
        }
        
        return workBulk;
    }
    
    /**
     * Return the list of existing external identifiers for the given user where the source matches the given sourceId
     * 
     * @param existingWorks
     *          The list of existing works for the current user
     * @param sourceId
     *          The client id we are evaluating
     * @return A set of all the existing external identifiers that belongs to the given user and to the given source id                  
     * */
    private Set<ExternalID> buildExistingExternalIdsSet(List<Work> existingWorks, String sourceId) {
        Set<ExternalID> existingExternalIds = new HashSet<ExternalID>();        
        for(Work work : existingWorks) {
            //If it is the same source
            if(work.retrieveSourcePath().equals(sourceId)) {
                if(work.getExternalIdentifiers() != null && work.getExternalIdentifiers().getExternalIdentifier() != null) {
                    for(ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                        //Don't include PART_OF external ids
                        if(!Relationship.PART_OF.equals(extId.getRelationship())) {
                            existingExternalIds.add(extId);
                        }                        
                    }
                }
            }
        }
        
        return existingExternalIds;
    }
    
    private void addExternalIdsToExistingSet(Work work, Set<ExternalID> existingExternalIDs) {
        if(work != null && work.getExternalIdentifiers() != null && work.getExternalIdentifiers().getExternalIdentifier() != null) {
            for(ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                //Don't include PART_OF external ids
                if(!Relationship.PART_OF.equals(extId.getRelationship())) {
                    existingExternalIDs.add(extId);
                }
            }
        }
    }
    
    @Override
    @Transactional
    public Work updateWork(String orcid, Work work, boolean isApiRequest) {
        WorkEntity workEntity = workDao.getWork(orcid, work.getPutCode());
        Visibility originalVisibility = Visibility.valueOf(workEntity.getVisibility());
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        
        //Save the original source
        String existingSourceId = workEntity.getSourceId();
        String existingClientSourceId = workEntity.getClientSourceId();
        
        if (isApiRequest) {
            activityValidator.validateWork(work, sourceEntity, false, isApiRequest, originalVisibility);                        
            List<Work> existingWorks = this.findWorks(orcid);       
            
            for (Work existing : existingWorks) {
                // Dont compare the updated peer review with the DB version
                if (!existing.getPutCode().equals(work.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(work.getExternalIdentifiers(), existing.getExternalIdentifiers(), existing.getSource(), sourceEntity);
                }
            }
        }else{
            //validate external ID vocab
            externalIDValidator.validateWorkOrPeerReview(work.getExternalIdentifiers());            
        }
                        
        orcidSecurityManager.checkSource(workEntity);
        jpaJaxbWorkAdapter.toWorkEntity(work, workEntity);
        
        //Be sure it doesn't overwrite the source
        workEntity.setSourceId(existingSourceId);
        workEntity.setClientSourceId(existingClientSourceId);
        
        workDao.merge(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity));
        return jpaJaxbWorkAdapter.toWork(workEntity);
    }

    @Override
    public boolean checkSourceAndRemoveWork(String orcid, Long workId) {
        boolean result = true;
        WorkEntity workEntity = workDao.getWork(orcid, workId);
        orcidSecurityManager.checkSource(workEntity);
        try {
            workDao.removeWork(orcid, workId);
            workDao.flush();
            notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity));
        } catch (Exception e) {
            LOGGER.error("Unable to delete work with ID: " + workId);
            result = false;
        }
        return result;
    }
    
	private void setIncomingWorkPrivacy(WorkEntity workEntity, ProfileEntity profile) {
		setIncomingWorkPrivacy(workEntity, profile, true);
	}

	private void setIncomingWorkPrivacy(WorkEntity workEntity, ProfileEntity profile, boolean isApiRequest) {
		String incomingWorkVisibility = workEntity.getVisibility();
		String defaultWorkVisibility = profile.getActivitiesVisibilityDefault();

		if ((isApiRequest && profile.getClaimed()) || (incomingWorkVisibility == null && !isApiRequest)) {
			workEntity.setVisibility(defaultWorkVisibility);
		} else if (isApiRequest && !profile.getClaimed()) {
			workEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
		}
	}

    private List<Item> createItemList(WorkEntity workEntity) {
        Item item = new Item();
        item.setItemName(workEntity.getTitle());
        item.setItemType(ItemType.WORK);
        item.setPutCode(String.valueOf(workEntity.getId()));
        return Arrays.asList(item);
    }
    
    @Override
    public void setPreferredAndCreateGroup(Long preferredId, List<Long> workIds, String orcid) {
        updateToMaxDisplay(orcid, preferredId);
        workIds.add(preferredId);
        createNewWorkGroup(workIds, orcid);
    }

    @Override
    public void createNewWorkGroup(List<Long> workIds, String orcid) {
        List<MinimizedWorkEntity> works = workEntityCacheManager.retrieveMinimizedWorks(orcid, workIds, getLastModified(orcid));
        JSONWorkExternalIdentifiersConverterV3 externalIdConverter = new JSONWorkExternalIdentifiersConverterV3(norm, localeManager);
        ExternalIDs allExternalIDs = new ExternalIDs();
        MinimizedWorkEntity userVersion = null;
        MinimizedWorkEntity userPreferred = null;
        
        for (MinimizedWorkEntity work : works) {
            if (orcid.equals(work.getSourceId())) {
                userVersion = work;
            }
            if (userPreferred == null || userPreferred.getDisplayIndex() < work.getDisplayIndex()) {
                userPreferred = work;
            }
            
            ExternalIDs externalIDs = externalIdConverter.convertFrom(work.getExternalIdentifiersJson(), null);
            for (ExternalID externalID : externalIDs.getExternalIdentifier()) {
                if (!allExternalIDs.getExternalIdentifier().contains(externalID)) {
                    allExternalIDs.getExternalIdentifier().add(externalID);
                }
            }
        }
        
        String externalIDsJson = externalIdConverter.convertTo(allExternalIDs, null);
        if (userVersion != null) {
            WorkEntity userVersionFullEntity = workDao.getWork(orcid, userVersion.getId());
            userVersionFullEntity.setExternalIdentifiersJson(externalIDsJson);
            workDao.merge(userVersionFullEntity);
        } else {
            WorkEntity allPreferredMetadata = createCopyOfUserPreferredWork(userPreferred);
            allPreferredMetadata.setExternalIdentifiersJson(externalIDsJson);
            workDao.persist(allPreferredMetadata);
        }
    }
    
    private WorkEntity createCopyOfUserPreferredWork(MinimizedWorkEntity preferred) {
        WorkEntity preferredFullData = workDao.find(preferred.getId());
        
        WorkEntity workEntity = new WorkEntity();
        workEntity.setAddedToProfileDate(new Date());
        workEntity.setCitation(preferredFullData.getCitation());
        workEntity.setCitationType(preferredFullData.getCitationType());
        workEntity.setContributorsJson(preferredFullData.getContributorsJson());
        workEntity.setDateCreated(new Date());
        workEntity.setDescription(preferredFullData.getDescription());
        workEntity.setDisplayIndex(preferredFullData.getDisplayIndex() -1);
        workEntity.setIso2Country(preferredFullData.getIso2Country());
        workEntity.setJournalTitle(preferredFullData.getJournalTitle());
        workEntity.setLanguageCode(preferredFullData.getLanguageCode());
        workEntity.setLastModified(new Date());
        workEntity.setOrcid(preferredFullData.getOrcid());
        workEntity.setPublicationDate(preferredFullData.getPublicationDate());
        workEntity.setSourceId(preferredFullData.getOrcid());
        workEntity.setSubtitle(preferredFullData.getSubtitle());
        workEntity.setTitle(preferredFullData.getTitle());
        workEntity.setTranslatedTitle(preferredFullData.getTranslatedTitle());
        workEntity.setTranslatedTitleLanguageCode(preferredFullData.getTranslatedTitleLanguageCode());
        workEntity.setVisibility(preferredFullData.getVisibility());
        workEntity.setWorkType(preferredFullData.getWorkType());
        workEntity.setWorkUrl(preferredFullData.getWorkUrl());
        return workEntity;
    }
    
}
