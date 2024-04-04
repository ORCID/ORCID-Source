package org.orcid.core.common.manager.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.orcid.core.common.manager.SummaryManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.WorksCacheManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Group;
import org.orcid.jaxb.model.v3.release.record.GroupableActivity;
import org.orcid.jaxb.model.v3.release.record.GroupsContainer;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.SourceAware;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PeerReviewMinimizedSummary;
import org.orcid.pojo.summary.ExternalIdentifiersSummary;
import org.orcid.pojo.summary.RecordSummary;
import org.orcid.utils.DateUtils;

public class SummaryManagerImpl implements SummaryManager {

    @Resource(name = "recordNameManagerReadOnlyV3")
    private RecordNameManagerReadOnly recordNameManagerReadOnly;

    @Resource(name = "affiliationsManagerReadOnlyV3")
    private AffiliationsManagerReadOnly affiliationsManagerReadOnly;

    @Resource(name = "externalIdentifierManagerReadOnlyV3")
    private ExternalIdentifierManagerReadOnly externalIdentifierManagerReadOnly;

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Resource(name = "profileFundingManagerReadOnlyV3")
    private ProfileFundingManagerReadOnly profileFundingManagerReadOnly;

    @Resource(name = "peerReviewManagerReadOnlyV3")
    private PeerReviewManagerReadOnly peerReviewManagerReadOnly;

    @Resource(name = "recordManagerReadOnlyV3")
    private RecordManagerReadOnly recordManagerReadOnly;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private WorksCacheManager worksCacheManager;

    @Override
    public RecordSummary getRecordSummary(String orcid) {
        RecordSummary recordSummary = new RecordSummary();

        // Set ORCID uri
        recordSummary.setOrcid(recordManagerReadOnly.getOrcidIdentifier(orcid).getUri());

        // Set dates
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        recordSummary.setLastModified(DateUtils.formatDateISO8601(profileEntity.getLastModified()));
        recordSummary.setCreation(DateUtils.formatDateISO8601(profileEntity.getDateCreated()));

        recordSummary.setName(recordNameManagerReadOnly.fetchDisplayablePublicName(orcid));

        // Generate the affiliations summary
        generateAffiliationsSummary(recordSummary, orcid);

        // Generate the external identifiers summary
        generateExternalIdentifiersSummary(recordSummary, orcid);

        // Generate the works summary
        generateWorksSummary(recordSummary, orcid);

        // Generate the funding summary
        generateFundingSummary(recordSummary, orcid);

        // Generate the peer review summary
        generatePeerReviewSummary(recordSummary, orcid);
        recordSummary.setStatus("active");
        return recordSummary;
    }

    public void generateWorksSummary(RecordSummary recordSummary, String orcid) {
        Works works = worksCacheManager.getGroupedWorks(orcid);
        // TODO Remove non public elements
        // TODO There should be a manager that does this, but, the one we have already returns a list of work summaries, so, we need to refactor it to return the same Works element
        Iterator<WorkGroup> workGroupIt = works.getWorkGroup().iterator();
        while (workGroupIt.hasNext()) {
            WorkGroup workGroup = workGroupIt.next();
            Iterator<WorkSummary> summariesIt = workGroup.getWorkSummary().iterator();
            while(summariesIt.hasNext()) {
                WorkSummary w = summariesIt.next();
                if(!Visibility.PUBLIC.equals(w.getVisibility())) {
                    summariesIt.remove();
                }
            }
            if(workGroup.getActivities() == null || workGroup.getActivities().isEmpty()) {
                workGroupIt.remove();
            }
        }
        Pair<Integer, Integer> validAndSelfAssertedStats = calculateSelfAssertedAndValidated(works, orcid);

        recordSummary.setValidatedWorks(validAndSelfAssertedStats.getLeft());
        recordSummary.setSelfAssertedWorks(validAndSelfAssertedStats.getRight());
    }

    public void generateFundingSummary(RecordSummary recordSummary, String orcid) {
        Fundings fundingGroups = profileFundingManagerReadOnly.groupFundings(profileFundingManagerReadOnly.getFundingSummaryList(orcid), true);        
        Pair<Integer, Integer> validAndSelfAssertedStats = calculateSelfAssertedAndValidated(fundingGroups, orcid);

        recordSummary.setValidatedFunds(validAndSelfAssertedStats.getLeft());
        recordSummary.setSelfAssertedFunds(validAndSelfAssertedStats.getRight());
    }

    public void generatePeerReviewSummary(RecordSummary recordSummary, String orcid) {
        List<PeerReviewMinimizedSummary> peerReviewMinimizedSummaryList = peerReviewManagerReadOnly.getPeerReviewMinimizedSummaryList(orcid, true);

        Integer totalReviewsCount = 0;
        Integer selfAssertedPeerReviews = 0;

        if (peerReviewMinimizedSummaryList != null) {
            for (PeerReviewMinimizedSummary pr : peerReviewMinimizedSummaryList) {
                totalReviewsCount += (pr.getPutCodes() == null) ? 0 : pr.getPutCodes().size();
                if (orcid.equals(pr.getSourceId()) || orcid.equals(pr.getAssertionOriginSourceId())) {
                    selfAssertedPeerReviews++;
                }
            }
        }

        recordSummary.setSelfAssertedPeerReviews(selfAssertedPeerReviews);
        recordSummary.setPeerReviewsTotal(totalReviewsCount);
        recordSummary.setPeerReviewPublicationGrants(peerReviewMinimizedSummaryList.size());
    }

    private Pair<Integer, Integer> calculateSelfAssertedAndValidated(GroupsContainer c, String orcid) {
        Integer validated = 0;
        Integer selfAsserted = 0;
        for (Group g : c.retrieveGroups()) {
            boolean validatedFound = false;
            for (GroupableActivity ga : g.getActivities()) {
                if (ga instanceof SourceAware) {
                    SourceAware activity = (SourceAware) ga;
                    Source source = activity.getSource();
                    if (!orcid.equals(source.retrieveSourcePath()) && !orcid.equals(source.retrieveAssertionOriginPath())) {
                        validatedFound = true;
                        break;
                    }
                }
            }
            if (validatedFound) {
                validated++;
            } else {
                selfAsserted++;
            }
        }
        return Pair.of(validated, selfAsserted);
    }

    public void generateExternalIdentifiersSummary(RecordSummary recordSummary, String orcid) {
        PersonExternalIdentifiers personExternalIdentifiers = externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(orcid);
        recordSummary.setExternalIdentifiers(ExternalIdentifiersSummary.valueOf(personExternalIdentifiers, orcid));
    }

    public void generateAffiliationsSummary(RecordSummary recordSummary, String orcid) {
        Map<AffiliationType, List<AffiliationGroup<AffiliationSummary>>> affiliationsMap = affiliationsManagerReadOnly.getGroupedAffiliations(orcid,
                true);

        // EMPLOYMENT
        List<AffiliationGroup<AffiliationSummary>> employmentGroups = affiliationsMap.get(AffiliationType.EMPLOYMENT);
        List<AffiliationSummary> preferredEmployments = new ArrayList<>();
        if (employmentGroups != null) {
            for (AffiliationGroup<AffiliationSummary> group : employmentGroups) {
                preferredEmployments.add(getDefaultAffiliationFromGroup(group));
            }
        }
        // Sort them by end date by default
        sortAffiliationsByEndDate(preferredEmployments);

        List<org.orcid.pojo.summary.AffiliationSummary> employmentsTop3 = new ArrayList<>();
        preferredEmployments.stream().limit(3).forEach(t -> {
            employmentsTop3.add(org.orcid.pojo.summary.AffiliationSummary.valueof(t, orcid, AffiliationType.EMPLOYMENT.value()));
        });
        recordSummary.setEmploymentAffiliations(employmentsTop3);
        recordSummary.setEmploymentAffiliationsCount(preferredEmployments.size());

        // PROFESIONAL ACTIVITIES
        List<AffiliationGroup<AffiliationSummary>> profesionalActivitesGroups = new ArrayList<>();
        if (affiliationsMap.containsKey(AffiliationType.DISTINCTION)) {
            profesionalActivitesGroups.addAll(affiliationsMap.get(AffiliationType.DISTINCTION));
        }
        if (affiliationsMap.containsKey(AffiliationType.INVITED_POSITION)) {
            profesionalActivitesGroups.addAll(affiliationsMap.get(AffiliationType.INVITED_POSITION));
        }
        if (affiliationsMap.containsKey(AffiliationType.MEMBERSHIP)) {
            profesionalActivitesGroups.addAll(affiliationsMap.get(AffiliationType.MEMBERSHIP));
        }
        if (affiliationsMap.containsKey(AffiliationType.SERVICE)) {
            profesionalActivitesGroups.addAll(affiliationsMap.get(AffiliationType.SERVICE));
        }
        List<AffiliationSummary> preferredProfesionalActivities = new ArrayList<>();
        for (AffiliationGroup<AffiliationSummary> group : profesionalActivitesGroups) {
            preferredProfesionalActivities.add(getDefaultAffiliationFromGroup(group));
        }
        // Sort them by end date by default
        sortAffiliationsByEndDate(preferredProfesionalActivities);

        List<org.orcid.pojo.summary.AffiliationSummary> professionalActivitiesTop3 = new ArrayList<>();
        preferredProfesionalActivities.stream().limit(3).forEach(t -> {
            professionalActivitiesTop3.add(org.orcid.pojo.summary.AffiliationSummary.valueof(t, orcid, AffiliationType.EMPLOYMENT.value()));
        });
        recordSummary.setProfessionalActivities(professionalActivitiesTop3);
        recordSummary.setProfessionalActivitiesCount(preferredProfesionalActivities.size());
    }

    private AffiliationSummary getDefaultAffiliationFromGroup(AffiliationGroup<AffiliationSummary> group) {
        AffiliationSummary defaultAffiliation = null;
        Long maxDisplayIndex = null;
        for (AffiliationSummary as : group.getActivities()) {
            if (maxDisplayIndex == null || (as.getDisplayIndex() != null && Long.valueOf(as.getDisplayIndex()) > maxDisplayIndex)) {
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
            // TODO: Why do we need to overwrite the end date when it is a
            // Distinction?
            if ((aff instanceof DistinctionSummary) && aff.getStartDate() != null && aff.getStartDate().getYear() != null) {
                aff.setEndDate(aff.getStartDate());
            }
            // To any affiliation with no end date, set the end day to today
            if (aff.getEndDate() == null || aff.getEndDate().getYear() == null || StringUtils.isEmpty(aff.getEndDate().getYear().getValue())) {
                FuzzyDate fd = FuzzyDate.valueOf(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
                aff.setEndDate(fd);
                // Store the id so we can roll this back
                summariesWithOutEndDate.add(aff);
            }
        });

        // TODO: Can we just sort this in reverse in one step?
        affiliations.sort(Comparator.comparing(a -> a.getEndDate()));
        Collections.reverse(affiliations);

        // Remove the end on the affiliations
        summariesWithOutEndDate.forEach(s -> s.setEndDate(null));
    }
}
