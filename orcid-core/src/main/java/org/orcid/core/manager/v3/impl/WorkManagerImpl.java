package org.orcid.core.manager.v3.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.adapter.jsonidentifier.converter.JSONWorkExternalIdentifiersConverterV3;
import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverter;
import org.orcid.core.contributors.roles.works.WorkContributorRoleConverter;
import org.orcid.core.exception.ExceedMaxNumberOfElementsException;
import org.orcid.core.exception.MissingGroupableExternalIDException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidForbiddenException;
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
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.jaxb.model.common.ActionType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkBulk;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.ajaxForm.WorkForm;
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

    @Resource
    private PIDResolverService resolver;

    @Resource(name = "contributorUtilsV3")
    private ContributorUtils contributorUtils;

    @Resource
    private ContributorsRolesAndSequencesConverter contributorsRolesAndSequencesConverter;

    @Resource
    private WorkContributorRoleConverter workContributorsRoleConverter;

    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;
    
    private Integer maxWorksToWrite;
    
    public WorkManagerImpl(@Value("${org.orcid.core.works.bulk.read.max:100}") Integer bulkReadSize, @Value("${org.orcid.core.works.bulk.write.max:100}") Integer bulkWriteSize) {
        super(bulkReadSize);
        this.maxWorksToWrite = (bulkWriteSize == null) ? 100 : bulkWriteSize;
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
        Source activeSource = sourceManager.retrieveActiveSource();
        
        if (isApiRequest) {
            activityValidator.validateWork(work, activeSource, true, isApiRequest, null);
            // If it is the user adding the peer review, allow him to add
            // duplicates
            if (!(activeSource.getSourceOrcid() != null && activeSource.getSourceOrcid().getPath().equals(orcid))) {
                List<Work> existingWorks = this.findWorks(orcid);       
                if((existingWorks.size() + 1) > this.maxNumOfActivities) {
                    throw new ExceedMaxNumberOfElementsException();
                }
                if (existingWorks != null) {
                    for (Work existing : existingWorks) {
                        activityValidator.checkExternalIdentifiersForDuplicates(work, existing, existing.getSource(),
                                activeSource);
                    }
                }
            }

        } else {
            // validate external ID vocab
            externalIDValidator.validateWork(work.getExternalIdentifiers(), isApiRequest);
        }

        WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        workEntity.setOrcid(orcid);
        workEntity.setAddedToProfileDate(new Date());
        
        //Set the source
        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, workEntity);
        
        setIncomingWorkPrivacy(workEntity, profile, isApiRequest);        
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(workEntity, isApiRequest);
        filterContributors(work, workEntity);
        workDao.persist(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity, work.getExternalIdentifiers(), ActionType.CREATE));
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
        Source activeSource = sourceManager.retrieveActiveSource();
        List<Work> existingWorks = this.findWorks(orcid);
        
        if(workBulk.getBulk() != null && !workBulk.getBulk().isEmpty()) {
            List<BulkElement> bulk = workBulk.getBulk();
            Map<ExternalID, Long> extIDPutCodeMap = new HashMap<ExternalID, Long>();
            Set<ExternalID> existingExternalIdentifiers = buildExistingExternalIdsSet(existingWorks, activeSource, extIDPutCodeMap);            
            if((existingWorks.size() + bulk.size()) > this.maxNumOfActivities) {
                throw new ExceedMaxNumberOfElementsException();
            }
            //Check bulk size
            if(bulk.size() > maxWorksToWrite) {
                Locale locale = localeManager.getLocale();                
                throw new IllegalArgumentException(messageSource.getMessage("apiError.validation_too_many_elements_in_bulk.exception", new Object[]{maxWorksToWrite}, locale));                
            }
            
            ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
                                  
            List<Item> items = new ArrayList<Item>();
            
            for(int i = 0; i < bulk.size(); i++) {
                if(Work.class.isAssignableFrom(bulk.get(i).getClass())){
                    Work work = (Work) bulk.get(i);
                    try {
                        activityValidator.validateWork(work, activeSource, true, true, null);

                        //Validate it is not duplicated
                        if(work.getExternalIdentifiers() != null) {
                            for(ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                                //normalise the provided ID
                                extId.setNormalized(new TransientNonEmptyString(norm.normalise(extId.getType(), extId.getValue())));                                
                                // If the external id exists and is a SELF identifier, then mark it as duplicated                                
                                if(existingExternalIdentifiers.contains(extId) && Relationship.SELF.equals(extId.getRelationship())) {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("clientName", SourceEntityUtils.getSourceName(activeSource));
                                    if(extIDPutCodeMap.containsKey(extId)) {
                                        params.put("putCode", String.valueOf(extIDPutCodeMap.get(extId)));
                                    }                                    
                                    throw new OrcidDuplicatedActivityException(params);
                                }
                            }
                        }
                        //Save the work
                        WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
                        workEntity.setOrcid(orcid);
                        workEntity.setAddedToProfileDate(new Date());

                        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, workEntity);
                        
                        setIncomingWorkPrivacy(workEntity, profile);        
                        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(workEntity, true);
                        filterContributors(work, workEntity);
                        workDao.persist(workEntity);                    
                        
                        //Update the element in the bulk
                        Work updatedWork = jpaJaxbWorkAdapter.toWork(workEntity);
                        bulk.set(i, updatedWork);
                        
                        //Add the work extIds to the list of existing external identifiers
                        addExternalIdsToExistingSet(extIDPutCodeMap, updatedWork, existingExternalIdentifiers);
                        items.add(createItem(workEntity, work.getExternalIdentifiers(), ActionType.CREATE));
                    } catch(Exception e) {
                        //Get the exception 
                        OrcidError orcidError = orcidCoreExceptionMapper.getV3OrcidError(e);
                        bulk.set(i, orcidError);
                    }                                        
                }
            }
            
            workDao.flush();   
            
            if(!items.isEmpty()) {
                notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, items);
            }
        }
        
        return workBulk;
    }
    
    /**
     * Return the list of existing external identifiers for the given user where the source matches the given sourceId
     * 
     * @param existingWorks
     *          The list of existing works for the current user
     * @param activeSource
     *          The client id we are evaluating
     * @return A set of all the existing external identifiers that belongs to the given user and to the given source id                  
     * */
    private Set<ExternalID> buildExistingExternalIdsSet(List<Work> existingWorks, Source activeSource, Map<ExternalID, Long> extIdsPutCodeMap) {
        Set<ExternalID> existingExternalIds = new HashSet<ExternalID>();        
        for(Work work : existingWorks) {
            //If it is the same source
            if(SourceEntityUtils.isTheSameForDuplicateChecking(activeSource, work.getSource())) {
                if(work.getExternalIdentifiers() != null && work.getExternalIdentifiers().getExternalIdentifier() != null) {
                    for(ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                        if(extIdsPutCodeMap != null) {
                            extIdsPutCodeMap.put(extId, work.getPutCode());
                        }
                        //Don't include PART_OF nor FUNDED_BY external ids
                        if(!Relationship.PART_OF.equals(extId.getRelationship()) && !Relationship.FUNDED_BY.equals(extId.getRelationship())) {
                            existingExternalIds.add(extId);
                        }                        
                    }
                }
            }
        }
        
        return existingExternalIds;
    }
    
    private void addExternalIdsToExistingSet(Map<ExternalID, Long> extIDPutCodeMap, Work work, Set<ExternalID> existingExternalIDs) {
        if(work != null && work.getExternalIdentifiers() != null && work.getExternalIdentifiers().getExternalIdentifier() != null) {
            for(ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                //Don't include PART_OF nor FUNDED_BY external ids
                if(!Relationship.PART_OF.equals(extId.getRelationship()) && !Relationship.FUNDED_BY.equals(extId.getRelationship())) {
                    existingExternalIDs.add(extId);
                }
                
                extIDPutCodeMap.put(extId, work.getPutCode());
            }
        }
    }
    
    @Override
    @Transactional
    public Work updateWork(String orcid, Work work, boolean isApiRequest) {
        WorkEntity workEntity = workDao.getWork(orcid, work.getPutCode());
        Work workSaved = jpaJaxbWorkAdapter.toWork(workEntity);
        WorkForm workFormSaved = WorkForm.valueOf(workSaved, maxContributorsForUI);
        Visibility originalVisibility = Visibility.valueOf(workEntity.getVisibility());
        Source activeSource = sourceManager.retrieveActiveSource();

        //Save the original source
        String existingSourceId = workEntity.getSourceId();
        String existingClientSourceId = workEntity.getClientSourceId();
        
        if (workFormSaved.compare(WorkForm.valueOf(work, maxContributorsForUI))) {
            if (activeSource != null) {
                if(!activeSource.equals(workSaved.getSource())) {
                	throw new OrcidForbiddenException(localeManager.resolveMessage("apiError.9010.developerMessage").replace("${activity}", "work"));
                }
            }
            
            LOGGER.info("There is no changes in the work with putCode " + work.getPutCode() + " send it by " + getSourceName(sourceManager.retrieveActiveSource()));
            return workSaved;
        }
        
        if (isApiRequest) {
            activityValidator.validateWork(work, activeSource, false, isApiRequest, originalVisibility);                        
            List<Work> existingWorks = this.findWorks(orcid);       
            
            for (Work existing : existingWorks) {
                // Dont compare the updated peer review with the DB version
                if (!existing.getPutCode().equals(work.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(work, existing, existing.getSource(), activeSource);
                }
            }
        } else {
            //validate external ID vocab
            externalIDValidator.validateWork(work.getExternalIdentifiers(), isApiRequest);            
        }

        orcidSecurityManager.checkSourceAndThrow(workEntity);
        jpaJaxbWorkAdapter.toWorkEntity(work, workEntity);
    	if (workEntity.getVisibility() == null) {
    		workEntity.setVisibility(originalVisibility.name());  
    	}
        
        //Be sure it doesn't overwrite the source
        workEntity.setSourceId(existingSourceId);
        workEntity.setClientSourceId(existingClientSourceId);
        filterContributors(work, workEntity);        
        workDao.merge(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity, work.getExternalIdentifiers(), ActionType.UPDATE));
        return jpaJaxbWorkAdapter.toWork(workEntity);
    }

    @Override
    public boolean checkSourceAndRemoveWork(String orcid, Long workId) {
        boolean result = true;
        WorkEntity workEntity = workDao.getWork(orcid, workId);
        Work work = jpaJaxbWorkAdapter.toWork(workEntity);
        orcidSecurityManager.checkSourceAndThrow(workEntity);
        try {
            workDao.removeWork(orcid, workId);
            workDao.flush();
            notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity, work.getExternalIdentifiers(), ActionType.DELETE));
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
        } else if (isApiRequest && !profile.getClaimed() && incomingWorkVisibility == null) {
            workEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        }
    }

    private List<Item> createItemList(WorkEntity workEntity, ExternalIDs extIds, ActionType type) {
        return Arrays.asList(createItem(workEntity, extIds, type));
    }
    
    private Item createItem(WorkEntity workEntity, ExternalIDs extIds, ActionType type) {
        Item item = new Item();
        item.setItemName(workEntity.getTitle());
        item.setItemType(ItemType.WORK);
        item.setPutCode(String.valueOf(workEntity.getId()));
        item.setActionType(type);
        item.setPutCode(String.valueOf(workEntity.getId()));
        
        if(extIds != null) {
            Map<String, Object> additionalInfo = new HashMap<String, Object>();
            additionalInfo.put("external_identifiers", extIds);        
            item.setAdditionalInfo(additionalInfo);            
        }
        
        return item;
    }
    
    @Override
    public void createNewWorkGroup(List<Long> workIds, String orcid) throws MissingGroupableExternalIDException {
        List<MinimizedWorkEntity> works = workEntityCacheManager.retrieveMinimizedWorks(orcid, workIds, getLastModified(orcid));
        JSONWorkExternalIdentifiersConverterV3 externalIdConverter = new JSONWorkExternalIdentifiersConverterV3(norm, resolver, localeManager);
        ExternalIDs allExternalIDs = new ExternalIDs();
        List<MinimizedWorkEntity> userVersions = new ArrayList<>();
        MinimizedWorkEntity userPreferred = null;
        
        boolean groupableExternalIdFound = false;
        for (MinimizedWorkEntity work : works) {     	
            if (orcid.equals(work.getSourceId())) {
                userVersions.add(work);
                work.setDisplayIndex(0L);
            } else {
                work.setDisplayIndex(1L);
            }
            if (userPreferred == null || userPreferred.getDisplayIndex() < work.getDisplayIndex()) {
                userPreferred = work;
            }
            
            if (work.getExternalIdentifiersJson() != null) {
                ExternalIDs externalIDs = externalIdConverter.convertFrom(work.getExternalIdentifiersJson(), null);
                for (ExternalID externalID : externalIDs.getExternalIdentifier()) {
                    if (!allExternalIDs.getExternalIdentifier().contains(externalID)) {
                        allExternalIDs.getExternalIdentifier().add(externalID);
                    }
                    
                    if (externalID.isGroupAble()) {
                        groupableExternalIdFound = true;
                    }
                }
            }
        }
        
        if (!groupableExternalIdFound) {
            throw new MissingGroupableExternalIDException();
        }
        
        String externalIDsJson = externalIdConverter.convertTo(allExternalIDs, null);
        if (!userVersions.isEmpty()) {
            for (MinimizedWorkEntity userVersion : userVersions) {
                WorkEntity userVersionFullEntity = workDao.getWork(orcid, userVersion.getId());
                userVersionFullEntity.setExternalIdentifiersJson(externalIDsJson);                
                workDao.merge(userVersionFullEntity);
            }
        } else {
            WorkEntity allPreferredMetadata = createCopyOfUserPreferredWork(userPreferred);
            allPreferredMetadata.setExternalIdentifiersJson(externalIDsJson);
            workDao.persist(allPreferredMetadata);
        }
    }

    @Override
    @Transactional
    public Work createWork(String orcid, WorkForm workForm) {
        Work work = workForm.toWork();
        work.setPutCode(null);

        Source activeSource = sourceManager.retrieveActiveSource();

        externalIDValidator.validateWork(work.getExternalIdentifiers(), false);

        WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        workEntity.setOrcid(orcid);
        workEntity.setAddedToProfileDate(new Date());

        //Set the source
        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, workEntity);

        setIncomingWorkPrivacy(workEntity, profile, false);
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(workEntity, false);
        String topContributorsJSON = getTopContributorsJson(workForm);
        if (topContributorsJSON != null) {
            workEntity.setTopContributorsJson(topContributorsJSON);
        } else {
            workEntity.setContributorsJson("{\"contributor\":[]}");
            workEntity.setTopContributorsJson("[]");
        }
        workDao.persist(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity, work.getExternalIdentifiers(), ActionType.CREATE));
        return jpaJaxbWorkAdapter.toWork(workEntity);
    }

    @Override
    @Transactional
    public Work updateWork(String orcid, WorkForm workForm) {
        Work work = workForm.toWork();

        WorkEntity workEntity = workDao.getWork(orcid, work.getPutCode());

        WorkExtended workSaved = jpaJaxbWorkAdapter.toWorkExtended(workEntity);
        WorkForm workFormSaved = WorkForm.valueOf(workSaved, maxContributorsForUI);
        
        if (workFormSaved.compare(workForm)) {
            LOGGER.info("There is no changes in the work with putCode " + work.getPutCode() + " send it by " + getSourceName(sourceManager.retrieveActiveSource()));
            return workSaved;
        }
        
        Visibility originalVisibility = Visibility.valueOf(workEntity.getVisibility());

        //Save the original source
        String existingSourceId = workEntity.getSourceId();
        String existingClientSourceId = workEntity.getClientSourceId();

        externalIDValidator.validateWork(work.getExternalIdentifiers(), false);

        orcidSecurityManager.checkSourceAndThrow(workEntity);
        jpaJaxbWorkAdapter.toWorkEntity(work, workEntity);
        if (workEntity.getVisibility() == null) {
            workEntity.setVisibility(originalVisibility.name());
        }

        //Be sure it doesn't overwrite the source
        workEntity.setSourceId(existingSourceId);
        workEntity.setClientSourceId(existingClientSourceId);
        String topContributorsJSON = getTopContributorsJson(workForm);
        if (topContributorsJSON != null) {
            workEntity.setTopContributorsJson(topContributorsJSON);
        } else {
            workEntity.setContributorsJson("{\"contributor\":[]}");
            workEntity.setTopContributorsJson("[]");
        }
        workDao.merge(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity, work.getExternalIdentifiers(), ActionType.UPDATE));
        return jpaJaxbWorkAdapter.toWork(workEntity);
    }

    private WorkEntity createCopyOfUserPreferredWork(MinimizedWorkEntity preferred) {
        WorkEntity preferredFullData = workDao.find(preferred.getId());

        WorkEntity workEntity = new WorkEntity();
        workEntity.setAddedToProfileDate(new Date());
        workEntity.setCitation(preferredFullData.getCitation());
        workEntity.setCitationType(preferredFullData.getCitationType());
        workEntity.setContributorsJson(preferredFullData.getContributorsJson());
        workEntity.setDescription(preferredFullData.getDescription());
        workEntity.setDisplayIndex(preferredFullData.getDisplayIndex() -1);
        workEntity.setIso2Country(preferredFullData.getIso2Country());
        workEntity.setJournalTitle(preferredFullData.getJournalTitle());
        workEntity.setLanguageCode(preferredFullData.getLanguageCode());        
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

    private void filterContributors(Work work, WorkEntity workEntity) {
        if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null && work.getWorkContributors().getContributor().size() > 0) {
            List<ContributorsRolesAndSequences> topContributors = contributorUtils.getContributorsGroupedByOrcid(work.getWorkContributors().getContributor(), maxContributorsForUI);
            if (topContributors.size() > 0) {
                workEntity.setTopContributorsJson(contributorsRolesAndSequencesConverter.convertTo(topContributors, null));
            }
        } else {
            workEntity.setContributorsJson("{\"contributor\":[]}");
            workEntity.setTopContributorsJson("[]");
        }
    }

    private String getTopContributorsJson(WorkForm work) {
        if (work.getContributorsGroupedByOrcid() != null && !work.getContributorsGroupedByOrcid().isEmpty()) {
            work.getContributorsGroupedByOrcid().forEach(contributorsRolesAndSequences -> {
                if (contributorsRolesAndSequences.getRolesAndSequences() != null && contributorsRolesAndSequences.getRolesAndSequences().size() > 0) {
                    contributorsRolesAndSequences.getRolesAndSequences().forEach(contributorAttributes -> {
                        if (contributorAttributes.getContributorRole() != null) {
                            contributorAttributes.setContributorRole(workContributorsRoleConverter.toDBRole(contributorAttributes.getContributorRole()));
                        }
                    });
                }
            });
        }
        return contributorsRolesAndSequencesConverter.convertTo(work.getContributorsGroupedByOrcid(), null);
    }

    private String getSourceName(Source activeSource) {
        String client = null;
        if (activeSource.getSourceName() != null && activeSource.getSourceName().getContent() != null) {
            client = activeSource.getSourceName().getContent();
        }
        return client;
    }
}
