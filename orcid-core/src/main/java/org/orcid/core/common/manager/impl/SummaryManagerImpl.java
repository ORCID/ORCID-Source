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
import org.orcid.core.manager.v3.read_only.ProfileEmailDomainManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordManagerReadOnly;
import org.orcid.core.manager.v3.read_only.RecordNameManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.core.model.EducationQualification;
import org.orcid.core.model.EducationQualifications;
import org.orcid.core.model.EmailDomain;
import org.orcid.core.model.EmailDomains;
import org.orcid.core.model.Employment;
import org.orcid.core.model.Employments;
import org.orcid.core.model.ExternalIdentifier;
import org.orcid.core.model.ExternalIdentifiers;
import org.orcid.core.model.Fundings;
import org.orcid.core.model.PeerReviews;
import org.orcid.core.model.ProfessionalActivities;
import org.orcid.core.model.ProfessionalActivity;
import org.orcid.core.model.RecordSummary;
import org.orcid.core.model.ResearchResources;
import org.orcid.core.model.Works;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.jaxb.model.v3.release.common.*;
import org.orcid.jaxb.model.v3.release.record.AffiliationType;
import org.orcid.jaxb.model.v3.release.record.Group;
import org.orcid.jaxb.model.v3.release.record.GroupableActivity;
import org.orcid.jaxb.model.v3.release.record.GroupsContainer;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.SourceAware;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.PeerReviewMinimizedSummary;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.summary.AffiliationSummary;
import org.orcid.pojo.summary.EmailDomainSummary;
import org.orcid.pojo.summary.ExternalIdentifiersSummary;
import org.orcid.pojo.summary.RecordSummaryPojo;
import org.orcid.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

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

    @Resource(name = "researchResourceManagerReadOnlyV3")
    private ResearchResourceManagerReadOnly researchResourceManagerReadOnly;

    @Resource(name = "profileEmailDomainManagerReadOnly")
    private ProfileEmailDomainManagerReadOnly profileEmailDomainManagerReadOnly;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private WorksCacheManager worksCacheManager;

    @Resource
    private RedisClient redisClient;

    @Value("${org.orcid.core.utils.cache.redis.summary.enabled:false}")
    private boolean isSummaryCacheEnabled;

    // Set the cache TTL for the summary, 1 day by default
    @Value("${org.orcid.core.utils.cache.redis.summary.ttl:3600}")
    private int summaryCacheTTL;

    @Override
    public RecordSummary getRecordSummary(String orcid) {
        String cacheKey = getCacheKey(orcid);
        // Check the cache
        if (isSummaryCacheEnabled) {
            String summaryString = redisClient.get(cacheKey);
            if (StringUtils.isNotBlank(summaryString)) {
                return JsonUtils.readObjectFromJsonString(summaryString, RecordSummary.class);
            }
        }

        RecordSummary recordSummary = new RecordSummary();

        // Set ORCID identifier
        recordSummary.setOrcidIdentifier(recordManagerReadOnly.getOrcidIdentifier(orcid));

        // Set dates
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        recordSummary.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(profileEntity.getLastModified())));
        recordSummary.setCreatedDate(new CreatedDate(DateUtils.convertToXMLGregorianCalendar(profileEntity.getDateCreated())));

        recordSummary.setCreditName(recordNameManagerReadOnly.fetchDisplayablePublicName(orcid));

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

        // Generate the research resources summary
        generateResearchResourcesSummary(recordSummary, orcid);

        // generate email domains summary
        generateEmailDomainsSummary(recordSummary, orcid);

        // Set the summary in the cache
        if (isSummaryCacheEnabled) {
            redisClient.set(cacheKey, JsonUtils.convertToJsonString(recordSummary), summaryCacheTTL);
        }
        return recordSummary;
    }

    @Override
    public RecordSummaryPojo getRecordSummaryPojo(String orcid) {
        RecordSummary recordSummary = getRecordSummary(orcid);
        RecordSummaryPojo pojo = new RecordSummaryPojo();
        pojo.setStatus("active");

        pojo.setOrcid(recordSummary.getOrcidIdentifier().getUri());
        pojo.setName(recordSummary.getCreditName());

        if (recordSummary.getCreatedDate() != null && recordSummary.getCreatedDate().getValue() != null) {
            pojo.setCreation(DateUtils.formatDateISO8601(recordSummary.getCreatedDate().getValue().toGregorianCalendar().getTime()));
        }

        if (recordSummary.getLastModifiedDate() != null && recordSummary.getLastModifiedDate().getValue() != null) {
            pojo.setLastModified(DateUtils.formatDateISO8601(recordSummary.getLastModifiedDate().getValue().toGregorianCalendar().getTime()));
        }

        if (recordSummary.getExternalIdentifiers() != null && recordSummary.getExternalIdentifiers().getExternalIdentifiers() != null) {
            List<ExternalIdentifiersSummary> externalIdentifiers = new ArrayList<>();
            for (ExternalIdentifier ei : recordSummary.getExternalIdentifiers().getExternalIdentifiers()) {
                ExternalIdentifiersSummary eis = new ExternalIdentifiersSummary();
                eis.setCommonName(ei.getExternalIdType());
                eis.setId(String.valueOf(ei.getPutCode()));
                eis.setReference(ei.getExternalIdValue());
                eis.setUrl(ei.getExternalIdUrl());
                eis.setValidated(ei.isValidated());
                externalIdentifiers.add(eis);
            }
            pojo.setExternalIdentifiers(externalIdentifiers);
        }

        if (recordSummary.getEmployments() != null && recordSummary.getEmployments().getEmployments() != null) {
            List<AffiliationSummary> affiliations = new ArrayList<>();
            for (Employment e : recordSummary.getEmployments().getEmployments()) {
                AffiliationSummary as = new AffiliationSummary();
                as.setStartDate(e.getStartDate() == null ? null : e.getStartDate().toString());
                as.setEndDate(e.getEndDate() == null ? null : e.getEndDate().toString());
                as.setOrganizationName(e.getOrganizationName());
                as.setRole(e.getRole());
                as.setType(e.getType());
                as.setUrl(e.getUrl());
                as.setPutCode(e.getPutCode());
                as.setValidated(e.isValidated());
                affiliations.add(as);
            }
            pojo.setEmploymentAffiliations(affiliations);
            pojo.setEmploymentAffiliationsCount(recordSummary.getEmployments().getCount());
        }

        if (recordSummary.getProfessionalActivities() != null && recordSummary.getProfessionalActivities().getProfessionalActivities() != null) {
            List<AffiliationSummary> professionalActivities = new ArrayList<>();
            for (ProfessionalActivity pa : recordSummary.getProfessionalActivities().getProfessionalActivities()) {
                AffiliationSummary as = new AffiliationSummary();
                as.setEndDate(pa.getEndDate() == null ? null : pa.getEndDate().toString());
                as.setStartDate(pa.getStartDate() == null ? null : pa.getStartDate().toString());
                as.setOrganizationName(pa.getOrganizationName());
                as.setPutCode(pa.getPutCode());
                as.setRole(pa.getRole());
                as.setType(pa.getType());
                as.setUrl(pa.getUrl());
                as.setValidated(pa.isValidated());
                professionalActivities.add(as);
            }

            pojo.setProfessionalActivities(professionalActivities);
            pojo.setProfessionalActivitiesCount(recordSummary.getProfessionalActivities().getCount());
        }

        if (recordSummary.getFundings() != null) {
            pojo.setSelfAssertedFunds(recordSummary.getFundings().getSelfAssertedCount());
            pojo.setValidatedFunds(recordSummary.getFundings().getValidatedCount());
        }

        if (recordSummary.getPeerReviews() != null) {
            pojo.setPeerReviewsTotal(recordSummary.getPeerReviews().getTotal());
            pojo.setPeerReviewPublicationGrants(recordSummary.getPeerReviews().getPeerReviewPublicationGrants());
            pojo.setSelfAssertedPeerReviews(recordSummary.getPeerReviews().getSelfAssertedCount());
        }

        if (recordSummary.getWorks() != null) {
            pojo.setSelfAssertedWorks(recordSummary.getWorks().getSelfAssertedCount());
            pojo.setValidatedWorks(recordSummary.getWorks().getValidatedCount());
        }

        if (recordSummary.getEducationQualifications() != null && recordSummary.getEducationQualifications().getEducationQualifications() != null) {
            List<AffiliationSummary> educationQualifications = new ArrayList<>();
            for (EducationQualification eq : recordSummary.getEducationQualifications().getEducationQualifications()) {
                AffiliationSummary as = new AffiliationSummary();
                as.setEndDate(eq.getEndDate() == null ? null : eq.getEndDate().toString());
                as.setStartDate(eq.getStartDate() == null ? null : eq.getStartDate().toString());
                as.setOrganizationName(eq.getOrganizationName());
                as.setPutCode(eq.getPutCode());
                as.setRole(eq.getRole());
                as.setType(eq.getType());
                as.setUrl(eq.getUrl());
                as.setValidated(eq.isValidated());
                educationQualifications.add(as);
            }

            pojo.setEducationQualifications(educationQualifications);
            pojo.setEducationQualificationsCount(recordSummary.getEducationQualifications().getCount());
        }

        if (recordSummary.getResearchResources() != null) {
            pojo.setSelfAssertedResearchResources(recordSummary.getResearchResources().getSelfAssertedCount());
            pojo.setValidatedResearchResources(recordSummary.getResearchResources().getValidatedCount());
        }

        if (recordSummary.getEmailDomains() != null) {
            List<EmailDomainSummary> emailDomains = new ArrayList<EmailDomainSummary>();
            if (recordSummary.getEmailDomains() != null && recordSummary.getEmailDomains().getEmailDomains() != null) {
                for (EmailDomain ed : recordSummary.getEmailDomains().getEmailDomains()) {
                    EmailDomainSummary eds = new EmailDomainSummary();
                    eds.setValue(ed.getValue());
                    eds.setVerificationDate(ed.getVerificationDate().toString());
                    emailDomains.add(eds);
                }
            }
            pojo.setEmailDomains(emailDomains);
            pojo.setEmailDomainsCount(recordSummary.getEmailDomains().getCount());
        }

        return pojo;
    }

    public void generateAffiliationsSummary(RecordSummary recordSummary, String orcid) {
        Map<AffiliationType, List<AffiliationGroup<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary>>> affiliationsMap = affiliationsManagerReadOnly
                .getGroupedAffiliations(orcid, true);

        // EMPLOYMENT
        List<AffiliationGroup<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary>> employmentGroups = affiliationsMap.get(AffiliationType.EMPLOYMENT);
        List<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> preferredEmployments = new ArrayList<>();
        if (employmentGroups != null) {
            for (AffiliationGroup<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> group : employmentGroups) {
                preferredEmployments.add(getDefaultAffiliationFromGroup(group));
            }
        }
        // Sort them by end date by default
        sortAffiliationsByEndDate(preferredEmployments);

        List<Employment> employmentsTop3 = new ArrayList<>();
        preferredEmployments.stream().limit(3).forEach(t -> {
            Employment e = new Employment();
            e.setEndDate(t.getEndDate());
            e.setStartDate(t.getStartDate());
            e.setOrganizationName((t.getOrganization() == null || StringUtils.isBlank(t.getOrganization().getName())) ? null : t.getOrganization().getName());
            e.setPutCode(t.getPutCode());
            e.setRole(t.getRoleTitle());
            e.setUrl((t.getUrl() == null || StringUtils.isBlank(t.getUrl().getValue())) ? null : t.getUrl().getValue());
            e.setValidated(!SourceUtils.isSelfAsserted(t.getSource(), orcid));
            employmentsTop3.add(e);
        });

        Employments e = new Employments();
        e.setCount(preferredEmployments.size());
        recordSummary.setEmployments(e);
        if (!employmentsTop3.isEmpty()) {
            e.setEmployments(employmentsTop3);
        }
        recordSummary.setEmployments(e);

        // PROFESIONAL ACTIVITIES
        List<AffiliationGroup<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary>> profesionalActivitesGroups = new ArrayList<>();
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

        List<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> preferredProfesionalActivities = new ArrayList<>();
        for (AffiliationGroup<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> group : profesionalActivitesGroups) {
            preferredProfesionalActivities.add(getDefaultAffiliationFromGroup(group));
        }
        // Sort them by end date by default
        sortAffiliationsByEndDate(preferredProfesionalActivities);

        List<ProfessionalActivity> professionalActivitiesTop3 = new ArrayList<>();
        preferredProfesionalActivities.stream().limit(3).forEach(t -> {
            ProfessionalActivity p = new ProfessionalActivity();
            p.setOrganizationName((t.getOrganization() == null || StringUtils.isBlank(t.getOrganization().getName())) ? null : t.getOrganization().getName());
            p.setPutCode(t.getPutCode());
            p.setStartDate(t.getStartDate());
            p.setEndDate(t.getEndDate());
            p.setRole(t.getRoleTitle());
            if (t instanceof DistinctionSummary) {
                p.setType(AffiliationType.DISTINCTION.value());
            } else if (t instanceof InvitedPositionSummary) {
                p.setType(AffiliationType.INVITED_POSITION.value());
            } else if (t instanceof MembershipSummary) {
                p.setType(AffiliationType.MEMBERSHIP.value());
            } else if (t instanceof ServiceSummary) {
                p.setType(AffiliationType.SERVICE.value());
            }
            p.setUrl((t.getUrl() == null || StringUtils.isBlank(t.getUrl().getValue())) ? null : t.getUrl().getValue());
            p.setValidated(!SourceUtils.isSelfAsserted(t.getSource(), orcid));
            professionalActivitiesTop3.add(p);
        });
        ProfessionalActivities pa = new ProfessionalActivities();
        pa.setCount(preferredProfesionalActivities.size());
        if (!professionalActivitiesTop3.isEmpty()) {
            pa.setProfessionalActivities(professionalActivitiesTop3);
        }

        recordSummary.setProfessionalActivities(pa);

        // EDUCATION Y QUALIFICATION
        List<AffiliationGroup<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary>> educationQualificationsGroups = new ArrayList<>();
        if (affiliationsMap.containsKey(AffiliationType.EDUCATION)) {
            educationQualificationsGroups.addAll(affiliationsMap.get(AffiliationType.EDUCATION));
        }
        if (affiliationsMap.containsKey(AffiliationType.QUALIFICATION)) {
            educationQualificationsGroups.addAll(affiliationsMap.get(AffiliationType.QUALIFICATION));
        }

        List<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> preferredEducationQualifications = new ArrayList<>();
        for (AffiliationGroup<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> group : educationQualificationsGroups) {
            preferredEducationQualifications.add(getDefaultAffiliationFromGroup(group));
        }
        // Sort them by end date by default
        sortAffiliationsByEndDate(preferredEducationQualifications);

        List<EducationQualification> educationQualificationsTop3 = new ArrayList<EducationQualification>();
        preferredEducationQualifications.stream().limit(3).forEach(t -> {
            EducationQualification eq = new EducationQualification();
            eq.setOrganizationName((t.getOrganization() == null || StringUtils.isBlank(t.getOrganization().getName())) ? null : t.getOrganization().getName());
            eq.setPutCode(t.getPutCode());
            eq.setStartDate(t.getStartDate());
            eq.setEndDate(t.getEndDate());
            eq.setRole(t.getRoleTitle());
            if (t instanceof EducationSummary) {
                eq.setType(AffiliationType.EDUCATION.value());
            } else if (t instanceof QualificationSummary) {
                eq.setType(AffiliationType.QUALIFICATION.value());
            }
            eq.setUrl((t.getUrl() == null || StringUtils.isBlank(t.getUrl().getValue())) ? null : t.getUrl().getValue());
            eq.setValidated(!SourceUtils.isSelfAsserted(t.getSource(), orcid));
            educationQualificationsTop3.add(eq);
        });

        EducationQualifications eqList = new EducationQualifications();
        eqList.setCount(preferredEducationQualifications.size());
        if (!educationQualificationsTop3.isEmpty()) {
            eqList.setEducationQualifications(educationQualificationsTop3);
        }

        recordSummary.setEducationQualifications(eqList);
    }

    public void generateExternalIdentifiersSummary(RecordSummary recordSummary, String orcid) {
        PersonExternalIdentifiers personExternalIdentifiers = externalIdentifierManagerReadOnly.getPublicExternalIdentifiers(orcid);
        if (personExternalIdentifiers == null || personExternalIdentifiers.getExternalIdentifiers().isEmpty()) {
            return;
        }
        ExternalIdentifiers eis = new ExternalIdentifiers();
        eis.setExternalIdentifiers(new ArrayList<>());
        for (PersonExternalIdentifier pei : personExternalIdentifiers.getExternalIdentifiers()) {
            ExternalIdentifier ei = new ExternalIdentifier();
            ei.setExternalIdType(pei.getType());
            ei.setExternalIdUrl((pei.getUrl() == null || StringUtils.isEmpty(pei.getUrl().getValue())) ? null : pei.getUrl().getValue());
            ei.setExternalIdValue(pei.getValue());
            ei.setPutCode(pei.getPutCode());
            ei.setValidated(!SourceUtils.isSelfAsserted(pei.getSource(), orcid));
            eis.getExternalIdentifiers().add(ei);
        }
        recordSummary.setExternalIdentifiers(eis);
    }

    public void generateWorksSummary(RecordSummary recordSummary, String orcid) {
        org.orcid.jaxb.model.v3.release.record.summary.Works works = worksCacheManager.getGroupedWorks(orcid);
        Iterator<WorkGroup> workGroupIt = works.getWorkGroup().iterator();
        while (workGroupIt.hasNext()) {
            WorkGroup workGroup = workGroupIt.next();
            Iterator<WorkSummary> summariesIt = workGroup.getWorkSummary().iterator();
            while (summariesIt.hasNext()) {
                WorkSummary w = summariesIt.next();
                if (!Visibility.PUBLIC.equals(w.getVisibility())) {
                    summariesIt.remove();
                }
            }
            if (workGroup.getActivities() == null || workGroup.getActivities().isEmpty()) {
                workGroupIt.remove();
            }
        }
        Pair<Integer, Integer> validAndSelfAssertedStats = calculateSelfAssertedAndValidated(works, orcid);

        Works worksModel = new Works();
        worksModel.setSelfAssertedCount(validAndSelfAssertedStats.getRight());
        worksModel.setValidatedCount(validAndSelfAssertedStats.getLeft());
        recordSummary.setWorks(worksModel);
    }

    public void generateFundingSummary(RecordSummary recordSummary, String orcid) {
        org.orcid.jaxb.model.v3.release.record.summary.Fundings fundingGroups = profileFundingManagerReadOnly
                .groupFundings(profileFundingManagerReadOnly.getFundingSummaryList(orcid), true);
        Pair<Integer, Integer> validAndSelfAssertedStats = calculateSelfAssertedAndValidated(fundingGroups, orcid);

        Fundings fundingsModel = new Fundings();
        fundingsModel.setSelfAssertedCount(validAndSelfAssertedStats.getRight());
        fundingsModel.setValidatedCount(validAndSelfAssertedStats.getLeft());
        recordSummary.setFundings(fundingsModel);
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

        PeerReviews pr = new PeerReviews();
        pr.setPeerReviewPublicationGrants(peerReviewMinimizedSummaryList.size());
        pr.setSelfAssertedCount(selfAssertedPeerReviews);
        pr.setTotal(totalReviewsCount);
        recordSummary.setPeerReviews(pr);
    }

    public void generateResearchResourcesSummary(RecordSummary recordSummary, String orcid) {
        org.orcid.jaxb.model.v3.release.record.summary.ResearchResources researchResourcesGroups = researchResourceManagerReadOnly
                .groupResearchResources(researchResourceManagerReadOnly.getResearchResourceSummaryList(orcid), true);
        Pair<Integer, Integer> validAndSelfAssertedStats = calculateSelfAssertedAndValidated(researchResourcesGroups, orcid);

        ResearchResources researchResourceModel = new ResearchResources();
        researchResourceModel.setSelfAssertedCount(validAndSelfAssertedStats.getRight());
        researchResourceModel.setValidatedCount(validAndSelfAssertedStats.getLeft());
        recordSummary.setResearchResources(researchResourceModel);
    }

    public void generateEmailDomainsSummary(RecordSummary recordSummary, String orcid) {
        // Fill email domains
        List<ProfileEmailDomainEntity> emailDomains = null;
        if (Features.EMAIL_DOMAINS.isActive()) {
            emailDomains = profileEmailDomainManagerReadOnly.getPublicEmailDomains(orcid);
            List<EmailDomain> edList = new ArrayList<EmailDomain>();
            if (emailDomains != null && !emailDomains.isEmpty()) {
                EmailDomain ed = null;
                for (ProfileEmailDomainEntity ped : emailDomains) {
                    ed = new EmailDomain();
                    ed.setValue(ped.getEmailDomain());
                    ed.setVerificationDate( new VerificationDate(DateUtils.convertToXMLGregorianCalendar(ped.getDateCreated())));
                    edList.add(ed);
                }
            }
            List<EmailDomain> emailDomainsTop3 = new ArrayList<EmailDomain>();
            edList.stream().limit(3).forEach(t -> {
                EmailDomain ed = new EmailDomain();
                ed.setValue(t.getValue());
                ed.setVerificationDate(t.getVerificationDate());
                emailDomainsTop3.add(ed);
            });

            EmailDomains eds = new EmailDomains();
            eds.setCount(edList.size());
            if (!emailDomainsTop3.isEmpty()) {
                eds.setEmailDomains(emailDomainsTop3);
            }
            
            recordSummary.setEmailDomains(eds);
        }
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

    private org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary getDefaultAffiliationFromGroup(
            AffiliationGroup<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> group) {
        org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary defaultAffiliation = null;
        Long maxDisplayIndex = null;
        for (org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary as : group.getActivities()) {
            if (maxDisplayIndex == null || (as.getDisplayIndex() != null && Long.valueOf(as.getDisplayIndex()) > maxDisplayIndex)) {
                maxDisplayIndex = Long.valueOf(as.getDisplayIndex());
                defaultAffiliation = as;
            }
        }
        return defaultAffiliation;
    }

    private void sortAffiliationsByEndDate(List<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> affiliations) {
        List<org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> summariesWithOutEndDate = new ArrayList<>();
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

    private String getCacheKey(String orcid) {
        return orcid + "-summary";
    }
}
