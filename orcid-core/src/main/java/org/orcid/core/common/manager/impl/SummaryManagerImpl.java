package org.orcid.core.common.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.common.manager.SummaryManager;
import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Name;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PeerReviewMinimizedSummary;
import org.orcid.pojo.ajaxForm.AffiliationGroupContainer;
import org.orcid.pojo.ajaxForm.AffiliationGroupForm;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.grouping.FundingGroup;
import org.orcid.pojo.grouping.WorkGroup;
import org.orcid.pojo.summary.ExternalIdentifiersSummary;
import org.orcid.pojo.summary.RecordSummary;
import org.orcid.utils.DateUtils;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;

import java.time.LocalDate;


public class SummaryManagerImpl implements SummaryManager {

    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnly;
    
    @Resource(name = "affiliationsManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;
    
    @Resource(name = "externalIdentifierManagerReadOnlyV3")
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;
    
    @Resource
    private WorksCacheManager worksCacheManager;
    
    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;
    
    public RecordSummary getRecordSummary(String orcid) {
        RecordSummary recordSummary = new RecordSummary();
        recordSummary.setOrcid(orcid);
        recordSummary.setName(recordNameManagerReadOnly.fetchDisplayablePublicName(orcid));
        
        // Generate the affiliations summary
        generateAffiliationsSummary(recordSummary);
        
        //Generate the external identifiers summary
        generateExternalIdentifiersSummary(recordSummary);
        
        //Generate the works summary
        generateWorksSummary(recordSummary);
        
        Page<org.orcid.pojo.grouping.WorkGroup> works = publicProfileController.getAllWorkGroupsJson(orcid, "date", true);

        List<WorkGroup> workGroups = works.getGroups();

        AtomicInteger validatedWorks = new AtomicInteger();
        AtomicInteger selfAssertedWorks = new AtomicInteger();

        if (workGroups != null) {
            workGroups.forEach(work -> {                
                AtomicBoolean foundValidateWorkInGroup = new AtomicBoolean(false);
                work.getWorks().forEach(w -> {
                    // If the orcid is not the source, then we count the group as validated
                    if(!orcid.equals(w.getSource()) && !orcid.equals(w.getAssertionOriginOrcid())) {
                        foundValidateWorkInGroup.set(true);                        
                    }                
                });
                
                if(foundValidateWorkInGroup.get()) {
                    validatedWorks.getAndIncrement();
                } else {
                    selfAssertedWorks.getAndIncrement();
                }                
            });
        }

        recordSummary.setSelfAssertedWorks(selfAssertedWorks.get());
        recordSummary.setValidatedWorks(validatedWorks.get());

        List<FundingGroup> fundingGroups = publicProfileController.getFundingsJson(orcid, "date", true);

        AtomicInteger validatedFunds = new AtomicInteger();
        AtomicInteger selfAssertedFunds = new AtomicInteger();

        if (fundingGroups != null) {
            fundingGroups.forEach(fundingGroup -> {
                AtomicBoolean foundValidateFundingInGroup = new AtomicBoolean(false);
                fundingGroup.getFundings().forEach(funding -> {
                    if (!orcid.equals(funding.getSource()) && !orcid.equals(funding.getAssertionOriginOrcid())) {
                        foundValidateFundingInGroup.getAndSet(true);                        
                    }                     
                });
                
                if(foundValidateFundingInGroup.get()) {
                    validatedFunds.getAndIncrement();
                } else {
                    selfAssertedFunds.getAndIncrement();
                }                                
            });
        }

        recordSummary.setSelfAssertedFunds(selfAssertedFunds.get());
        recordSummary.setValidatedFunds(validatedFunds.get());

        List<PeerReviewMinimizedSummary> peerReviewMinimizedSummaryList = peerReviewManagerReadOnly.getPeerReviewMinimizedSummaryList(orcid, true);

        AtomicInteger totalReviewsCount = new AtomicInteger();
        AtomicInteger selfAssertedPeerReviews = new AtomicInteger();
        
        if (peerReviewMinimizedSummaryList != null) {
            peerReviewMinimizedSummaryList.forEach(peerReviewMinimizedSummary -> {
                totalReviewsCount.set(totalReviewsCount.intValue() + peerReviewMinimizedSummary.getPutCodes().size());
                if(orcid.equals(peerReviewMinimizedSummary.getSourceId()) || orcid.equals(peerReviewMinimizedSummary.getAssertionOriginSourceId())) {
                    selfAssertedPeerReviews.getAndIncrement();
                }
            });
            recordSummary.setSelfAssertedPeerReviews(selfAssertedPeerReviews.intValue());
            recordSummary.setPeerReviewsTotal(totalReviewsCount.intValue());
            recordSummary.setPeerReviewPublicationGrants(peerReviewMinimizedSummaryList.size());
        } else {
            recordSummary.setPeerReviewsTotal(0);
            recordSummary.setSelfAssertedPeerReviews(0);
            recordSummary.setPeerReviewPublicationGrants(0);
        }

        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);

        recordSummary.setLastModified(formatDate(DateUtils.convertToXMLGregorianCalendar(profileEntity.getLastModified())));
        recordSummary.setCreation(formatDate(DateUtils.convertToXMLGregorianCalendar(profileEntity.getDateCreated())));

        recordSummary.setOrcid(recordManagerReadOnly.getOrcidIdentifier(orcid).getUri());

        return recordSummary;
    }
    
    public void generateWorksSummary(RecordSummary recordSummary) {
        Works works = worksCacheManager.getGroupedWorks(recordSummary.getOrcid());
    }
    
    public void generateExternalIdentifiersSummary(RecordSummary recordSummary) {
        PersonExternalIdentifiers personExternalIdentifiers = externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(recordSummary.getOrcid());
        recordSummary.setExternalIdentifiers(ExternalIdentifiersSummary.valueOf(personExternalIdentifiers, recordSummary.getOrcid()));
    }
    
    public void generateAffiliationsSummary(RecordSummary recordSummary) {
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliationsMap = affiliationsManagerReadOnly.getGroupedAffiliations(recordSummary.getOrcid(), true);
        
        // EMPLOYMENT
        List<AffiliationGroup<AffiliationSummary>> employmentGroups = affiliationsMap.get(AffiliationType.EMPLOYMENT);
        List<AffiliationSummary> preferredEmployments = new ArrayList<>();
        for(AffiliationGroup<AffiliationSummary> group : employmentGroups) {
            preferredEmployments.add(getDefaultAffiliationFromGroup(group));
        }
        // Sort them by end date by default
        sortAffiliationsByEndDate(preferredEmployments);
        
        List<org.orcid.pojo.summary.AffiliationSummary> employmentsTop3 = new ArrayList<>();
        preferredEmployments.stream().limit(3).forEach(t -> {employmentsTop3.add(org.orcid.pojo.summary.AffiliationSummary.valueof(t, recordSummary.getOrcid(), AffiliationType.EMPLOYMENT.value()));});
        recordSummary.setEmploymentAffiliations(employmentsTop3);
        recordSummary.setEmploymentAffiliationsCount(preferredEmployments.size());
        
        // PROFESIONAL ACTIVITIES
        List<AffiliationGroup<AffiliationSummary>> profesionalActivitesGroups = affiliationsMap.get(AffiliationType.DISTINCTION);
        profesionalActivitesGroups.addAll(affiliationsMap.get(AffiliationType.INVITED_POSITION));
        profesionalActivitesGroups.addAll(affiliationsMap.get(AffiliationType.MEMBERSHIP));
        profesionalActivitesGroups.addAll(affiliationsMap.get(AffiliationType.SERVICE));
        
        List<AffiliationSummary> preferredProfesionalActivities = new ArrayList<>();
        for(AffiliationGroup<AffiliationSummary> group : profesionalActivitesGroups) {
            preferredProfesionalActivities.add(getDefaultAffiliationFromGroup(group));
        }
        // Sort them by end date by default
        sortAffiliationsByEndDate(preferredProfesionalActivities);
        
        List<org.orcid.pojo.summary.AffiliationSummary> professionalActivitiesTop3 = new ArrayList<>();
        preferredProfesionalActivities.stream().limit(3).forEach(t -> {professionalActivitiesTop3.add(org.orcid.pojo.summary.AffiliationSummary.valueof(t, recordSummary.getOrcid(), AffiliationType.EMPLOYMENT.value()));});
        recordSummary.setProfessionalActivities(professionalActivitiesTop3);
        recordSummary.setProfessionalActivitiesCount(preferredProfesionalActivities.size());
    }
    
    private AffiliationSummary getDefaultAffiliationFromGroup(AffiliationGroup<AffiliationSummary> group) {
        AffiliationSummary defaultAffiliation = null;
        Long maxDisplayIndex = -1L;
        for(AffiliationSummary as : group.getActivities()) {
            if(as.getDisplayIndex() != null && Long.valueOf(as.getDisplayIndex()) > maxDisplayIndex) {
                maxDisplayIndex = Long.valueOf(as.getDisplayIndex());
                defaultAffiliation = as;
            }
        }
        return defaultAffiliation;
    }
    
    private void sortAffiliationsByEndDate(List<AffiliationSummary> affiliations) {
        List<AffiliationSummary> summariesWithOutEndDate = new ArrayList<>();
        LocalDate today = LocalDate.now();
        affiliations.forEach(aff -> {
            // TODO: Why do we need to overwrite the end date when it is a Distiction? 
            if((aff instanceof DistinctionSummary) && aff.getStartDate() != null && aff.getStartDate().getYear() != null) {
                aff.setEndDate(aff.getStartDate());
            }
            // To any affiliation with no end date, set the end day to today
            if(aff.getEndDate() == null || aff.getEndDate().getYear() == null || StringUtils.isEmpty(aff.getEndDate().getYear().getValue())) {
                FuzzyDate fd = FuzzyDate.valueOf(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
                aff.setEndDate(fd);
                // Store the id so we can roll this back
                summariesWithOutEndDate.add(aff);
            }
        });
        
        //TODO: Can we just sort this in reverse in one step?
        affiliations.sort(Comparator.comparing(a -> a.getEndDate()));
        Collections.reverse(affiliations);
        
        // Remove the end on the affiliations
        summariesWithOutEndDate.forEach(s -> s.setEndDate(null));
    }        
}
