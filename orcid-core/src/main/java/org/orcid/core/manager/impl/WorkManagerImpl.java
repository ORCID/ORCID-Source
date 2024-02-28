package org.orcid.core.manager.impl;

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

import org.orcid.core.adapter.v3.converter.ContributorsRolesAndSequencesConverterV2;
import org.orcid.core.exception.ExceedMaxNumberOfElementsException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidForbiddenException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.manager.read_only.impl.WorkManagerReadOnlyImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.manager.validator.ExternalIDValidator;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.ContributorUtils;
import org.orcid.jaxb.model.common.ActionType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.ItemType;
import org.orcid.jaxb.model.record.bulk.BulkElement;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ContributorsRolesAndSequencesV2;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.StringUtils;

public class WorkManagerImpl extends WorkManagerReadOnlyImpl implements WorkManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkManagerImpl.class);

    @Value("${org.orcid.core.activities.max:10000}")
    private long maxNumOfActivities;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private ExternalIDValidator externalIDValidator;

    @Resource
    private ActivityValidator activityValidator;

    @Resource
    private MessageSource messageSource;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private SourceNameCacheManager sourceNameCacheManager;


    @Resource(name = "contributorUtils")
    private ContributorUtils contributorUtils;

    @Resource
    private ContributorsRolesAndSequencesConverterV2 contributorsRolesAndSequencesConverterV2;

    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;

    private Integer maxWorksToWrite;

    public WorkManagerImpl(@Value("${org.orcid.core.works.bulk.read.max:100}") Integer bulkReadSize,
            @Value("${org.orcid.core.works.bulk.write.max:100}") Integer bulkWriteSize) {
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
     */
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
     */
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
     */
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
                if (existingWorks != null) {
                    if ((existingWorks.size() + 1) > this.maxNumOfActivities) {
                        throw new ExceedMaxNumberOfElementsException();
                    }
                    for (Work existing : existingWorks) {
                        activityValidator.checkExternalIdentifiersForDuplicates(work, existing, existing.getSource(), sourceEntity);
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
        if (sourceEntity.getSourceProfile() != null) {
            workEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }

        if (sourceEntity.getSourceClient() != null) {
            workEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        }

        setIncomingWorkPrivacy(workEntity, profile);
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(workEntity, isApiRequest);
        if (isApiRequest) {
            filterContributors(work, workEntity);
        }
        workDao.persist(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity, work.getExternalIdentifiers(), ActionType.CREATE));
        Work updatedWork = jpaJaxbWorkAdapter.toWork(workEntity);
        return updatedWork;
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
        if (workBulk.getBulk() != null && !workBulk.getBulk().isEmpty()) {
            List<BulkElement> bulk = workBulk.getBulk();
            Map<ExternalID, Long> extIDPutCodeMap = new HashMap<ExternalID, Long>();
            Set<ExternalID> existingExternalIdentifiers = buildExistingExternalIdsSet(existingWorks, SourceEntityUtils.getSourceId(sourceEntity), extIDPutCodeMap);
            if ((existingWorks.size() + bulk.size()) > this.maxNumOfActivities) {
                throw new ExceedMaxNumberOfElementsException();
            }

            // Check bulk size
            if (bulk.size() > maxWorksToWrite) {
                Locale locale = localeManager.getLocale();
                throw new IllegalArgumentException(
                        messageSource.getMessage("apiError.validation_too_many_elements_in_bulk.exception", new Object[] { maxWorksToWrite }, locale));
            }
            ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
            List<Item> items = new ArrayList<Item>();
            for (int i = 0; i < bulk.size(); i++) {
                if (Work.class.isAssignableFrom(bulk.get(i).getClass())) {
                    Work work = (Work) bulk.get(i);
                    try {
                        activityValidator.validateWork(work, sourceEntity, true, true, null);

                        // Validate it is not duplicated
                        if (work.getExternalIdentifiers() != null) {
                            for (ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                                // If the external id exists and is a SELF
                                // identifier, then mark it as duplicated
                                if (existingExternalIdentifiers.contains(extId) && Relationship.SELF.equals(extId.getRelationship())) {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("clientName", sourceNameCacheManager.retrieve(SourceEntityUtils.getSourceId(sourceEntity)));
                                    if (extIDPutCodeMap.containsKey(extId)) {
                                        params.put("putCode", String.valueOf(extIDPutCodeMap.get(extId)));
                                    }
                                    throw new OrcidDuplicatedActivityException(params);
                                }
                            }
                        }

                        // Save the work
                        WorkEntity workEntity = jpaJaxbWorkAdapter.toWorkEntity(work);
                        workEntity.setOrcid(orcid);
                        workEntity.setAddedToProfileDate(new Date());

                        // Set source id
                        if (sourceEntity.getSourceProfile() != null) {
                            workEntity.setSourceId(sourceEntity.getSourceProfile().getId());
                        }

                        if (sourceEntity.getSourceClient() != null) {
                            workEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
                        }

                        setIncomingWorkPrivacy(workEntity, profile);
                        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(workEntity, true);
                        filterContributors(work, workEntity);                        
                        workDao.persist(workEntity);

                        // Update the element in the bulk
                        Work updatedWork = jpaJaxbWorkAdapter.toWork(workEntity);
                        bulk.set(i, updatedWork);

                        // Add the work extIds to the list of existing external
                        // identifiers
                        addExternalIdsToExistingSet(extIDPutCodeMap, updatedWork, existingExternalIdentifiers);
                        items.add(createItem(workEntity, work.getExternalIdentifiers(), ActionType.CREATE));
                    } catch (Exception e) {
                        // Get the exception
                        OrcidError orcidError = orcidCoreExceptionMapper.getOrcidError(e);
                        bulk.set(i, orcidError);
                    }
                }
            }

            workDao.flush();

            if (!items.isEmpty()) {
                notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, items);
            }
        }

        return workBulk;
    }

    /**
     * Return the list of existing external identifiers for the given user where
     * the source matches the given sourceId
     * 
     * @param existingWorks
     *            The list of existing works for the current user
     * @param sourceId
     *            The client id we are evaluating
     * @return A set of all the existing external identifiers that belongs to
     *         the given user and to the given source id
     */
    private Set<ExternalID> buildExistingExternalIdsSet(List<Work> existingWorks, String sourceId, Map<ExternalID, Long> extIDPutCodeMap) {
        Set<ExternalID> existingExternalIds = new HashSet<ExternalID>();
        for (Work work : existingWorks) {
            // If it is the same source
            if (work.retrieveSourcePath().equals(sourceId)) {
                if (work.getExternalIdentifiers() != null && work.getExternalIdentifiers().getExternalIdentifier() != null) {
                    for (ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                        if(extIDPutCodeMap != null) {
                            extIDPutCodeMap.put(extId, work.getPutCode());
                        }
                        // Don't include PART_OF nor FUNDED_BY external ids
                        if (!Relationship.PART_OF.equals(extId.getRelationship()) && !Relationship.FUNDED_BY.equals(extId.getRelationship())) {
                            existingExternalIds.add(extId);
                        }
                    }
                }
            }
        }

        return existingExternalIds;
    }

    private void addExternalIdsToExistingSet(Map<ExternalID, Long> extIDPutCodeMap, Work work, Set<ExternalID> existingExternalIDs) {
        if (work != null && work.getExternalIdentifiers() != null && work.getExternalIdentifiers().getExternalIdentifier() != null) {
            for (ExternalID extId : work.getExternalIdentifiers().getExternalIdentifier()) {
                // Don't include PART_OF nor FUNDED_BY external ids
                if (!Relationship.PART_OF.equals(extId.getRelationship()) && !Relationship.FUNDED_BY.equals(extId.getRelationship())) {
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
        String devMessage = localeManager.resolveMessage("apiError.9010.developerMessage").replace("${activity}", "work");

        if (workFormSaved.compare(WorkForm.valueOf(work, maxContributorsForUI))) {
            SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
            String client = null;
            if (sourceEntity.getSourceProfile() != null && sourceEntity.getSourceProfile().getId() != null) {
                client = sourceEntity.getSourceProfile().getId();
                if(!StringUtils.equals(client, orcid) ) {
                	throw new OrcidForbiddenException(devMessage );
                }
            }
            if (sourceEntity.getSourceClient() != null && sourceEntity.getSourceClient().getClientName() != null) {
                client = sourceEntity.getSourceClient().getClientName();
                if(!StringUtils.equals(sourceEntity.getSourceClient().getClientId(), workEntity.getClientSourceId()) ) {
                	throw new OrcidForbiddenException(devMessage );
                }
            }
            LOGGER.info("There is no changes in the work with putCode " + work.getPutCode() + " send it by " + client);
            return workSaved;
        }

        String originalVisibility = workEntity.getVisibility();
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Save the original source
        String existingSourceId = workEntity.getSourceId();
        String existingClientSourceId = workEntity.getClientSourceId();

        if (isApiRequest) {
            activityValidator.validateWork(work, sourceEntity, false, isApiRequest, Visibility.valueOf(originalVisibility));
            List<Work> existingWorks = this.findWorks(orcid);

            for (Work existing : existingWorks) {
                // Dont compare the updated peer review with the DB version
                if (!existing.getPutCode().equals(work.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(work, existing, existing.getSource(), sourceEntity);
                }
            }

            filterContributors(work, workEntity);            
        } else {
            // validate external ID vocab
            externalIDValidator.validateWorkOrPeerReview(work.getExternalIdentifiers());
        }

        orcidSecurityManager.checkSource(workEntity);
        jpaJaxbWorkAdapter.toWorkEntity(work, workEntity);
        workEntity.setVisibility(originalVisibility);

        // Be sure it doesn't overwrite the source
        workEntity.setSourceId(existingSourceId);
        workEntity.setClientSourceId(existingClientSourceId);

        workDao.merge(workEntity);
        workDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity, work.getExternalIdentifiers(), ActionType.UPDATE));
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
            notificationManager.sendAmendEmail(orcid, AmendedSection.WORK, createItemList(workEntity, null, ActionType.DELETE));
        } catch (Exception e) {
            LOGGER.error("Unable to delete work with ID: " + workId);
            result = false;
        }
        return result;
    }

    private void setIncomingWorkPrivacy(WorkEntity workEntity, ProfileEntity profile) {
        String incomingWorkVisibility = workEntity.getVisibility();
        String defaultWorkVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {
            workEntity.setVisibility(defaultWorkVisibility);
        } else if (incomingWorkVisibility == null) {
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

        if (extIds != null) {
            Map<String, Object> additionalInfo = new HashMap<String, Object>();
            additionalInfo.put("external_identifiers", extIds);
            item.setAdditionalInfo(additionalInfo);
        }

        return item;
    }

    private void filterContributors(Work work, WorkEntity workEntity) {
        if (work.getWorkContributors() != null && work.getWorkContributors().getContributor() != null && work.getWorkContributors().getContributor().size() > 0) {
            List<ContributorsRolesAndSequencesV2> topContributors = contributorUtils.getContributorsGroupedByOrcid(work.getWorkContributors().getContributor(), maxContributorsForUI);
            if (topContributors.size() > 0) {
                workEntity.setTopContributorsJson(contributorsRolesAndSequencesConverterV2.convertTo(topContributors, null));
            }
        } else {
            workEntity.setContributorsJson("{\"contributor\":[]}");
            workEntity.setTopContributorsJson("[]");
        }
    }
}
