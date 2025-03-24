package org.orcid.pojo.ajaxForm;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.common.CitationType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.*;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.WorkExtended;
import org.orcid.utils.DateUtils;
import org.orcid.utils.OrcidStringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkForm extends VisibilityForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Date publicationDate;    

    private Text putCode;

    private Text shortDescription;

    private Text url;

    private Text journalTitle;

    private Text languageCode;

    private Text languageName;

    private Citation citation;

    private Text countryCode;

    private Text countryName;

    private List<Contributor> contributors;

    private List<ContributorsRolesAndSequences> contributorsGroupedByOrcid;

    private int numberOfContributors;

    private List<ActivityExternalIdentifier> workExternalIdentifiers = new ArrayList<>();

    private String source;

    private String sourceName;
    
    private String assertionOriginOrcid;
    
    private String assertionOriginClientId;
    
    private String assertionOriginName;
    
    private Text title;

    private Text subtitle;

    private TranslatedTitleForm translatedTitle;

    private Text workType;

    protected String citationForDisplay;

    private String dateSortString;

    private Date createdDate;

    private Date lastModified;        

    public static WorkForm valueOf(Work work, int maxContributorsForUI) {
        if (work == null)
            return null;

        WorkForm w = new WorkForm();

        // Set work id
        if (work.getPutCode() != null) {
            w.setPutCode(Text.valueOf(work.getPutCode()));
        }

        // Set language
        if (!PojoUtil.isEmpty(work.getLanguageCode())) {
            w.setLanguageCode(Text.valueOf(work.getLanguageCode()));
        }

        // Set type
        if (work.getWorkType() != null) {
            w.setWorkType(Text.valueOf(work.getWorkType().value()));
        }

        if (work.getWorkTitle() != null) {
            // Set title
            if (work.getWorkTitle().getTitle() != null) {
                w.setTitle(Text.valueOf(work.getWorkTitle().getTitle().getContent()));
            }
            // Set translated title
            if (work.getWorkTitle().getTranslatedTitle() != null) {
                TranslatedTitleForm tt = new TranslatedTitleForm();
                tt.setContent(work.getWorkTitle().getTranslatedTitle().getContent());
                tt.setLanguageCode(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
                w.setTranslatedTitle(tt);
            }
            // Set subtitle
            if (work.getWorkTitle().getSubtitle() != null) {
                w.setSubtitle(Text.valueOf(work.getWorkTitle().getSubtitle().getContent()));
            }
        }

        // Set journal title
        if (work.getJournalTitle() != null ) {
            w.setJournalTitle(Text.valueOf(work.getJournalTitle().getContent()));
        }

        // Set description
        if (work.getShortDescription() != null) {
            w.setShortDescription(Text.valueOf(work.getShortDescription()));
        }

        // Set url
        if (work.getUrl() != null ) {
            w.setUrl(Text.valueOf(work.getUrl().getValue()));
        }

        // Set visibility
        if (work.getVisibility() != null) {
            w.setVisibility(Visibility.valueOf(work.getVisibility()));
        }

        // Set country
        if (work.getCountry() != null && work.getCountry().getValue() != null) {
            w.setCountryCode(Text.valueOf(work.getCountry().getValue().name()));
        }

        // Set publication date
        FuzzyDate fuzzyPublicationDate = null;
        if (work.getPublicationDate() != null) {
            org.orcid.jaxb.model.v3.release.common.PublicationDate publicationDate = work.getPublicationDate();
            Integer year = PojoUtil.isEmpty(publicationDate.getYear()) ? null : Integer.valueOf(publicationDate.getYear().getValue());
            Integer month = PojoUtil.isEmpty(publicationDate.getMonth()) ? null : Integer.valueOf(publicationDate.getMonth().getValue());
            Integer day = PojoUtil.isEmpty(publicationDate.getDay()) ? null : Integer.valueOf(publicationDate.getDay().getValue());
            if(year != null && year == 0) {
                year = null;
            }
            if(month != null && month == 0) {
                month = null;
            }
            if (day != null && day == 0) {
                day = null;
            }
            fuzzyPublicationDate = FuzzyDate.valueOf(year, month, day);
            w.setPublicationDate(Date.valueOf(fuzzyPublicationDate));
        }
        w.setDateSortString(PojoUtil.createDateSortString(null, fuzzyPublicationDate));

        // Set citation
        if (work.getWorkCitation() != null) {            
            Citation citation = new Citation();
            if(!PojoUtil.isEmpty(work.getWorkCitation().getCitation())) {
                citation.setCitation(Text.valueOf(work.getWorkCitation().getCitation()));
            }
            if(work.getWorkCitation().getWorkCitationType() != null) {
                citation.setCitationType(Text.valueOf(work.getWorkCitation().getWorkCitationType().value()));
            }
            
            w.setCitation(citation);
        }

        if (work instanceof WorkExtended) {
            List<ContributorsRolesAndSequences> contributorsGroupedByOrcid = ((WorkExtended) work).getContributorsGroupedByOrcid();
            w.setContributorsGroupedByOrcid(((WorkExtended) work).getContributorsGroupedByOrcid());
            if (contributorsGroupedByOrcid != null) {
                w.setNumberOfContributors(contributorsGroupedByOrcid.size());
            } else {
                w.setNumberOfContributors(0);
            }
        } else {
            // Set contributors
            populateContributors(work, w, maxContributorsForUI);
        }
        // Set external identifiers
        populateExternalIdentifiers(work, w);

        // Set created date
        w.setCreatedDate(Date.valueOf(work.getCreatedDate()));

        // Set last modified
        w.setLastModified(Date.valueOf(work.getLastModifiedDate()));

        if(work.getSource() != null) {
            // Set source
            w.setSource(work.getSource().retrieveSourcePath());
            if(work.getSource().getSourceName() != null) {
                w.setSourceName(work.getSource().getSourceName().getContent());
            }
            
            if (work.getSource().getAssertionOriginClientId() != null) {
                w.setAssertionOriginClientId(work.getSource().getAssertionOriginClientId().getPath());
            }
            
            if (work.getSource().getAssertionOriginOrcid() != null) {
                w.setAssertionOriginOrcid(work.getSource().getAssertionOriginOrcid().getPath());
            }
            
            if (work.getSource().getAssertionOriginName() != null) {
                w.setAssertionOriginName(work.getSource().getAssertionOriginName().getContent());
            }
        }
        return w;
    }

    public static WorkForm valueOf(WorkSummary work) {
        if (work == null)
            return null;

        WorkForm w = new WorkForm();

        // Set work id
        if (work.getPutCode() != null) {
            w.setPutCode(Text.valueOf(work.getPutCode()));
        }

        // Set type
        if (work.getType() != null) {
            w.setWorkType(Text.valueOf(work.getType().value()));
        }

        if (work.getTitle() != null) {
            // Set title
            if (work.getTitle().getTitle() != null) {
                w.setTitle(Text.valueOf(work.getTitle().getTitle().getContent()));
            }
            // Set translated title
            if (work.getTitle().getTranslatedTitle() != null) {
                TranslatedTitleForm tt = new TranslatedTitleForm();
                tt.setContent(work.getTitle().getTranslatedTitle().getContent());
                tt.setLanguageCode(work.getTitle().getTranslatedTitle().getLanguageCode());
                w.setTranslatedTitle(tt);
            }
            // Set subtitle
            if (work.getTitle().getSubtitle() != null) {
                w.setSubtitle(Text.valueOf(work.getTitle().getSubtitle().getContent()));
            }
        }

        // Set journal title
        if (work.getJournalTitle() != null ) {
            w.setJournalTitle(Text.valueOf(work.getJournalTitle().getContent()));
        }

        // Set url
        if (work.getUrl() != null ) {
            w.setUrl(Text.valueOf(work.getUrl().getValue()));
        }

        // Set visibility
        if (work.getVisibility() != null) {
            w.setVisibility(Visibility.valueOf(work.getVisibility()));
        }

        // Set publication date
        FuzzyDate fuzzyPublicationDate = null;
        if (work.getPublicationDate() != null) {
            org.orcid.jaxb.model.v3.release.common.PublicationDate publicationDate = work.getPublicationDate();
            Integer year = PojoUtil.isEmpty(publicationDate.getYear()) ? null : Integer.valueOf(publicationDate.getYear().getValue());
            Integer month = PojoUtil.isEmpty(publicationDate.getMonth()) ? null : Integer.valueOf(publicationDate.getMonth().getValue());
            Integer day = PojoUtil.isEmpty(publicationDate.getDay()) ? null : Integer.valueOf(publicationDate.getDay().getValue());
            if(year != null && year == 0) {
                year = null;
            }
            if(month != null && month == 0) {
                month = null;
            }
            if (day != null && day == 0) {
                day = null;
            }
            fuzzyPublicationDate = FuzzyDate.valueOf(year, month, day);
            w.setPublicationDate(Date.valueOf(fuzzyPublicationDate));
        }
        w.setDateSortString(PojoUtil.createDateSortString(null, fuzzyPublicationDate));

        // Set created date
        w.setCreatedDate(Date.valueOf(work.getCreatedDate()));

        // Set last modified
        w.setLastModified(Date.valueOf(work.getLastModifiedDate()));

        if(work.getSource() != null) {
            // Set source
            w.setSource(work.getSource().retrieveSourcePath());
            if(work.getSource().getSourceName() != null) {
                w.setSourceName(work.getSource().getSourceName().getContent());
            }

            if (work.getSource().getAssertionOriginClientId() != null) {
                w.setAssertionOriginClientId(work.getSource().getAssertionOriginClientId().getPath());
            }

            if (work.getSource().getAssertionOriginOrcid() != null) {
                w.setAssertionOriginOrcid(work.getSource().getAssertionOriginOrcid().getPath());
            }

            if (work.getSource().getAssertionOriginName() != null) {
                w.setAssertionOriginName(work.getSource().getAssertionOriginName().getContent());
            }
        }
        return w;
    }

    public static WorkForm valueOf(org.orcid.jaxb.model.record_v2.Work work, int maxContributorsForUI) {
        if (work == null)
            return null;

        WorkForm w = new WorkForm();

        // Set work id
        if (work.getPutCode() != null) {
            w.setPutCode(Text.valueOf(work.getPutCode()));
        }

        // Set language
        if (!PojoUtil.isEmpty(work.getLanguageCode())) {
            w.setLanguageCode(Text.valueOf(work.getLanguageCode()));
        }

        // Set type
        if (work.getWorkType() != null) {
            w.setWorkType(Text.valueOf(work.getWorkType().value()));
        }

        if (work.getWorkTitle() != null) {
            // Set title
            if (work.getWorkTitle().getTitle() != null) {
                w.setTitle(Text.valueOf(work.getWorkTitle().getTitle().getContent()));
            }
            // Set translated title
            if (work.getWorkTitle().getTranslatedTitle() != null) {
                TranslatedTitleForm tt = new TranslatedTitleForm();
                tt.setContent(work.getWorkTitle().getTranslatedTitle().getContent());
                tt.setLanguageCode(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
                w.setTranslatedTitle(tt);
            }
            // Set subtitle
            if (work.getWorkTitle().getSubtitle() != null) {
                w.setSubtitle(Text.valueOf(work.getWorkTitle().getSubtitle().getContent()));
            }
        }

        // Set journal title
        if (work.getJournalTitle() != null ) {
            w.setJournalTitle(Text.valueOf(work.getJournalTitle().getContent()));
        }

        // Set description
        if (work.getShortDescription() != null) {
            w.setShortDescription(Text.valueOf(work.getShortDescription()));
        }

        // Set url
        if (work.getUrl() != null ) {
            w.setUrl(Text.valueOf(work.getUrl().getValue()));
        }

        // Set visibility
        if (work.getVisibility() != null) {
            w.setVisibility(Visibility.valueOf(work.getVisibility()));
        }

        // Set country
        if (work.getCountry() != null && work.getCountry().getValue() != null) {
            w.setCountryCode(Text.valueOf(work.getCountry().getValue().name()));
        }

        // Set publication date
        FuzzyDate fuzzyPublicationDate = null;
        if (work.getPublicationDate() != null) {
            org.orcid.jaxb.model.common_v2.PublicationDate publicationDate = work.getPublicationDate();
            Integer year = PojoUtil.isEmpty(publicationDate.getYear()) ? null : Integer.valueOf(publicationDate.getYear().getValue());
            Integer month = PojoUtil.isEmpty(publicationDate.getMonth()) ? null : Integer.valueOf(publicationDate.getMonth().getValue());
            Integer day = PojoUtil.isEmpty(publicationDate.getDay()) ? null : Integer.valueOf(publicationDate.getDay().getValue());
            if(year != null && year == 0) {
                year = null;
            }
            if(month != null && month == 0) {
                month = null;
            }
            if (day != null && day == 0) {
                day = null;
            }
            fuzzyPublicationDate = FuzzyDate.valueOf(year, month, day);
            w.setPublicationDate(Date.valueOf(fuzzyPublicationDate));
        }
        w.setDateSortString(PojoUtil.createDateSortString(null, fuzzyPublicationDate));

        // Set citation
        if (work.getWorkCitation() != null) {
            Citation citation = new Citation();
            if(!PojoUtil.isEmpty(work.getWorkCitation().getCitation())) {
                citation.setCitation(Text.valueOf(work.getWorkCitation().getCitation()));
            }
            if(work.getWorkCitation().getWorkCitationType() != null) {
                citation.setCitationType(Text.valueOf(work.getWorkCitation().getWorkCitationType().value()));
            }

            w.setCitation(citation);
        }
            // Set contributors
        populateContributors(work, w, maxContributorsForUI);
        // Set external identifiers
        populateExternalIdentifiers(work, w);

        // Set created date
        w.setCreatedDate(Date.valueOf(work.getCreatedDate()));

        // Set last modified
        w.setLastModified(Date.valueOf(work.getLastModifiedDate()));

        if(work.getSource() != null) {
            // Set source
            w.setSource(work.getSource().retrieveSourcePath());
            if(work.getSource().getSourceName() != null) {
                w.setSourceName(work.getSource().getSourceName().getContent());
            }
        }
        return w;
    }

    private static void populateExternalIdentifiers(Work work, WorkForm workForm) {
        if(work.getExternalIdentifiers() != null) {        
            populateExternalIdentifiers(work.getExternalIdentifiers(), workForm, work.getWorkType());
        }
    }

    private static void populateExternalIdentifiers(org.orcid.jaxb.model.record_v2.Work work, WorkForm workForm) {
        if(work.getExternalIdentifiers() != null) {
            populateExternalIdentifiers(work.getExternalIdentifiers(), workForm, work.getWorkType());
        }
    }

    public static void populateExternalIdentifiers(ExternalIDs extIds, WorkForm workForm, WorkType workType) {
        if (extIds != null) {
            List<ActivityExternalIdentifier> workExternalIdentifiersList = new ArrayList<ActivityExternalIdentifier>();
            for (ExternalID extId : extIds.getExternalIdentifier()) {
                if(extId.getRelationship() == null) {
                    if(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.equals(extId.getType())) {
                        if(WorkType.BOOK.equals(workType)) {
                            extId.setRelationship(Relationship.PART_OF);
                        } else {
                            extId.setRelationship(Relationship.SELF);
                        }
                    } else if(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISBN.equals(extId.getType())) {
                        if(WorkType.BOOK_CHAPTER.equals(workType) || WorkType.CONFERENCE_PAPER.equals(workType)) {
                            extId.setRelationship(Relationship.PART_OF);
                        } else {
                            extId.setRelationship(Relationship.SELF);
                        }
                    } else {
                        extId.setRelationship(Relationship.SELF);
                    }
                }
                workExternalIdentifiersList.add(ActivityExternalIdentifier.valueOf(extId));
            }
            workForm.setWorkExternalIdentifiers(workExternalIdentifiersList);
        }
    }

    public static void populateExternalIdentifiers(org.orcid.jaxb.model.record_v2.ExternalIDs extIds, WorkForm workForm, org.orcid.jaxb.model.record_v2.WorkType workType) {
        if (extIds != null) {
            List<ActivityExternalIdentifier> workExternalIdentifiersList = new ArrayList<ActivityExternalIdentifier>();
            for (org.orcid.jaxb.model.record_v2.ExternalID extId : extIds.getExternalIdentifier()) {
                if(extId.getRelationship() == null) {
                    if(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.equals(extId.getType())) {
                        if(WorkType.BOOK.equals(workType)) {
                            extId.setRelationship(org.orcid.jaxb.model.record_v2.Relationship.PART_OF);
                        } else {
                            extId.setRelationship(org.orcid.jaxb.model.record_v2.Relationship.SELF);
                        }
                    } else if(org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISBN.equals(extId.getType())) {
                        if(WorkType.BOOK_CHAPTER.equals(workType) || WorkType.CONFERENCE_PAPER.equals(workType)) {
                            extId.setRelationship(org.orcid.jaxb.model.record_v2.Relationship.PART_OF);
                        } else {
                            extId.setRelationship(org.orcid.jaxb.model.record_v2.Relationship.SELF);
                        }
                    } else {
                        extId.setRelationship(org.orcid.jaxb.model.record_v2.Relationship.SELF);
                    }
                }
                workExternalIdentifiersList.add(ActivityExternalIdentifier.valueOf(extId));
            }
            workForm.setWorkExternalIdentifiers(workExternalIdentifiersList);
        }
    }

    private static void populateExternalIdentifiers(WorkForm workForm, Work work) {
        ExternalIDs workExternalIds = new ExternalIDs();
        if(workForm.getWorkExternalIdentifiers() != null && !workForm.getWorkExternalIdentifiers().isEmpty()) {
            for(ActivityExternalIdentifier wfExtId : workForm.getWorkExternalIdentifiers()) {
                ExternalID wExtId = new ExternalID();
                if(!PojoUtil.isEmpty(wfExtId.getExternalIdentifierId())) {
                    wExtId.setValue(wfExtId.getExternalIdentifierId().getValue());
                }
                
                if(!PojoUtil.isEmpty(wfExtId.getExternalIdentifierType())) {
                    wExtId.setType(wfExtId.getExternalIdentifierType().getValue());
                }
                
                if(!PojoUtil.isEmpty(wfExtId.getRelationship())) {
                    wExtId.setRelationship(Relationship.fromValue(wfExtId.getRelationship().getValue()));
                }
                
                if(!PojoUtil.isEmpty(wfExtId.getUrl())) {
                    wExtId.setUrl(new org.orcid.jaxb.model.v3.release.common.Url(wfExtId.getUrl().getValue()));
                }
                workExternalIds.getExternalIdentifier().add(wExtId);
            }
        }
        work.setWorkExternalIdentifiers(workExternalIds);
    }

    private static void populateContributors(org.orcid.jaxb.model.record_v2.Work work, WorkForm workForm, int maxContributorsForUI) {
        List<Contributor> contributorsList = new ArrayList<Contributor>();
        if(work.getWorkContributors() != null) {
            List<org.orcid.jaxb.model.common_v2.Contributor> contributors = null;
            if (work.getWorkContributors().getContributor().size() > maxContributorsForUI) {
                contributors = work.getWorkContributors().getContributor().subList(0, maxContributorsForUI);
            } else {
                contributors = work.getWorkContributors().getContributor();
            }            
            if (contributors != null) {
                for (org.orcid.jaxb.model.common_v2.Contributor contributor : contributors) {
                    contributorsList.add(Contributor.valueOf(contributor));
                }
            }

        }       
        workForm.setContributors(contributorsList);
    }

    private static void populateContributors(Work work, WorkForm workForm, int maxContributorsForUI) {
        List<Contributor> contributorsList = new ArrayList<Contributor>();
        if(work.getWorkContributors() != null) {
            List<org.orcid.jaxb.model.v3.release.common.Contributor> contributors = null;
            if (work.getWorkContributors().getContributor().size() > maxContributorsForUI) {
                contributors = work.getWorkContributors().getContributor().subList(0, maxContributorsForUI);
            } else {
                contributors = work.getWorkContributors().getContributor();
            }
            if (contributors != null) {
                for (org.orcid.jaxb.model.v3.release.common.Contributor contributor : contributors) {
                    contributorsList.add(Contributor.valueOf(contributor));
                }
            }

        }
        workForm.setContributors(contributorsList);
    }

    private static void populateContributors(WorkForm workForm, Work work) {
        org.orcid.jaxb.model.v3.release.record.WorkContributors contributors = new org.orcid.jaxb.model.v3.release.record.WorkContributors();
        if(workForm.getContributors() != null && !workForm.getContributors().isEmpty()) {
            for(Contributor wfContributor : workForm.getContributors()) {
                org.orcid.jaxb.model.v3.release.common.Contributor workContributor = new org.orcid.jaxb.model.v3.release.common.Contributor();
                org.orcid.jaxb.model.v3.release.common.ContributorAttributes contributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
                if(!PojoUtil.isEmpty(wfContributor.getContributorRole())) {
                    contributorAttributes.setContributorRole(org.orcid.jaxb.model.common.ContributorRole.fromValue(wfContributor.getContributorRole().getValue()).value());
                }
                
                if(!PojoUtil.isEmpty(wfContributor.getContributorSequence())) {
                    contributorAttributes.setContributorSequence(org.orcid.jaxb.model.common.SequenceType.fromValue(wfContributor.getContributorSequence().getValue()));
                }                
                workContributor.setContributorAttributes(contributorAttributes);
                
                if(!PojoUtil.isEmpty(wfContributor.getCreditName())) {
                    org.orcid.jaxb.model.v3.release.common.CreditName creditName = new org.orcid.jaxb.model.v3.release.common.CreditName(); 
                    creditName.setContent(wfContributor.getCreditName().getValue());
                    workContributor.setCreditName(creditName);
                }
                
                org.orcid.jaxb.model.v3.release.common.ContributorOrcid contributorOrcid = new org.orcid.jaxb.model.v3.release.common.ContributorOrcid(); 
                if(!PojoUtil.isEmpty(wfContributor.getOrcid())) {
                    contributorOrcid.setPath(wfContributor.getOrcid().getValue());
                }
                
                if(!PojoUtil.isEmpty(wfContributor.getUri())) {
                    contributorOrcid.setUri(wfContributor.getUri().getValue());
                }
                workContributor.setContributorOrcid(contributorOrcid);                                
                contributors.getContributor().add(workContributor);
            }
        }
        work.setWorkContributors(contributors);
    }

    private static void populateContributorsGroupedByOrcid(WorkForm workForm, Work work) {
        org.orcid.jaxb.model.v3.release.record.WorkContributors contributors = new org.orcid.jaxb.model.v3.release.record.WorkContributors();
        if(workForm.getContributorsGroupedByOrcid() != null && !workForm.getContributorsGroupedByOrcid().isEmpty()) {
            for (ContributorsRolesAndSequences contributor : workForm.getContributorsGroupedByOrcid()) {
                if (contributor.getRolesAndSequences() != null && !contributor.getRolesAndSequences().isEmpty()) {
                    for (ContributorAttributes ca : contributor.getRolesAndSequences()) {
                        org.orcid.jaxb.model.v3.release.common.Contributor workContributor = new org.orcid.jaxb.model.v3.release.common.Contributor();
                        org.orcid.jaxb.model.v3.release.common.ContributorAttributes contributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
                        if (ca.getContributorRole() != null) {
                            contributorAttributes.setContributorRole(ca.getContributorRole());
                        }

                        if (ca.getContributorSequence() != null) {
                            contributorAttributes.setContributorSequence(ca.getContributorSequence());
                        }
                        workContributor.setContributorAttributes(contributorAttributes);

                        if (contributor.getCreditName() != null) {
                            workContributor.setCreditName(contributor.getCreditName());
                        }

                        if (contributor.getContributorOrcid() != null) {
                            workContributor.setContributorOrcid(contributor.getContributorOrcid());
                        }

                        contributors.getContributor().add(workContributor);
                    }
                } else {
                    org.orcid.jaxb.model.v3.release.common.Contributor workContributor = new org.orcid.jaxb.model.v3.release.common.Contributor();
                    org.orcid.jaxb.model.v3.release.common.ContributorAttributes contributorAttributes = new org.orcid.jaxb.model.v3.release.common.ContributorAttributes();
                    if (contributor.getCreditName() != null) {
                        workContributor.setCreditName(contributor.getCreditName());
                    }

                    if (contributor.getContributorOrcid() != null) {
                        workContributor.setContributorOrcid(contributor.getContributorOrcid());
                    }

                    contributors.getContributor().add(workContributor);
                }
            }
            work.setWorkContributors(contributors);
        }
    }

    public Work toWork() {
        Work work = new Work();
        
        // Set work id
        if (!PojoUtil.isEmpty(this.getPutCode())) {
            work.setPutCode(Long.valueOf(this.getPutCode().getValue()));
        }

        // Set language
        if (!PojoUtil.isEmpty(this.getLanguageCode())) {
            work.setLanguageCode(this.getLanguageCode().getValue());
        }

        // Set type
        if (!PojoUtil.isEmpty(this.getWorkType())) {
            work.setWorkType(WorkType.fromValue(this.getWorkType().getValue()));
        }        
        
        org.orcid.jaxb.model.v3.release.record.WorkTitle workTitle = new org.orcid.jaxb.model.v3.release.record.WorkTitle();
        // Set title
        if(!PojoUtil.isEmpty(this.getTitle())) {            
            workTitle.setTitle(new org.orcid.jaxb.model.v3.release.common.Title(this.getTitle().getValue()));
        }
        
        // Set translated title        
        if(this.getTranslatedTitle() != null && !PojoUtil.isEmpty(this.getTranslatedTitle().getContent())) {
            org.orcid.jaxb.model.v3.release.common.TranslatedTitle translatedTitle = new org.orcid.jaxb.model.v3.release.common.TranslatedTitle();
            translatedTitle.setContent(this.getTranslatedTitle().getContent());
            translatedTitle.setLanguageCode(this.getTranslatedTitle().getLanguageCode());
            workTitle.setTranslatedTitle(translatedTitle);
        }
        
        // Set subtitle
        if (!PojoUtil.isEmpty(this.getSubtitle())) {
            org.orcid.jaxb.model.v3.release.common.Subtitle subtitle = new  org.orcid.jaxb.model.v3.release.common.Subtitle();
            subtitle.setContent(this.getSubtitle().getValue());
            workTitle.setSubtitle(subtitle);
        }
        
        work.setWorkTitle(workTitle);
        
        // Set journal title
        if(!PojoUtil.isEmpty(this.getJournalTitle())) {
            work.setJournalTitle(new org.orcid.jaxb.model.v3.release.common.Title(this.getJournalTitle().getValue()));            
        }

        // Set description
        if (!PojoUtil.isEmpty(this.getShortDescription())) {
            work.setShortDescription(this.getShortDescription().getValue());
        }

        // Set url
        if (!PojoUtil.isEmpty(this.getUrl())) {
            work.setUrl(new Url(this.getUrl().getValue()));
        } else {
            work.setUrl(new Url());
        }

        // Set visibility
        if (this.getVisibility() != null && this.getVisibility().getVisibility() != null) {
            work.setVisibility(org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(this.getVisibility().getVisibility().value()));
        }
        
        // Set country
        if (!PojoUtil.isEmpty(this.getCountryCode())) {
            work.setCountry(new org.orcid.jaxb.model.v3.release.common.Country(org.orcid.jaxb.model.common.Iso3166Country.fromValue(this.getCountryCode().getValue())));
        }

        // Set publication date        
        if(this.getPublicationDate() != null) {            
            Integer yearInteger = PojoUtil.isEmpty(this.getPublicationDate().getYear()) ? null : Integer.valueOf(this.getPublicationDate().getYear());
            Integer monthInteger = PojoUtil.isEmpty(this.getPublicationDate().getMonth()) ? null : Integer.valueOf(this.getPublicationDate().getMonth());
            Integer dayInteger = PojoUtil.isEmpty(this.getPublicationDate().getDay()) ? null : Integer.valueOf(this.getPublicationDate().getDay());
            org.orcid.jaxb.model.v3.release.common.Year year = null;
            org.orcid.jaxb.model.v3.release.common.Month month = null;
            org.orcid.jaxb.model.v3.release.common.Day day = null;
            if(yearInteger != null) {
                year = new org.orcid.jaxb.model.v3.release.common.Year(yearInteger);
            }
            if(monthInteger != null) {
                month = new org.orcid.jaxb.model.v3.release.common.Month(monthInteger);
            }
            if(dayInteger != null) {
                day = new org.orcid.jaxb.model.v3.release.common.Day(dayInteger);
            }                                                  
            work.setPublicationDate(new org.orcid.jaxb.model.v3.release.common.PublicationDate(year, month, day));
        }
                
        // Set citation
        if(this.getCitation() != null) {
            org.orcid.jaxb.model.v3.release.record.Citation citation = new org.orcid.jaxb.model.v3.release.record.Citation();
            if(!PojoUtil.isEmpty(this.getCitation().getCitation())) {
                citation.setCitation(this.getCitation().getCitation().getValue());
            }
            
            if(!PojoUtil.isEmpty(this.getCitation().getCitationType())) {
                citation.setWorkCitationType(CitationType.fromValue(this.getCitation().getCitationType().getValue()));
            }                        
            work.setWorkCitation(citation);
        }
                        
        // Set contributors
        populateContributors(this, work);

        populateContributorsGroupedByOrcid(this, work);

        // Set external identifiers
        populateExternalIdentifiers(this, work);

        // Set created date
        if(!PojoUtil.isEmpty(this.getCreatedDate())) {
            CreatedDate createdDate = new CreatedDate();
            createdDate.setValue(DateUtils.convertToXMLGregorianCalendar(this.getCreatedDate().toJavaDate()));            
            work.setCreatedDate(createdDate);
        }
        
        // Set last modified
        if(!PojoUtil.isEmpty(this.getLastModified())) {
            org.orcid.jaxb.model.v3.release.common.LastModifiedDate lastModified = new org.orcid.jaxb.model.v3.release.common.LastModifiedDate();
            lastModified.setValue(DateUtils.convertToXMLGregorianCalendar(this.getLastModified().toJavaDate()));
            work.setLastModifiedDate(lastModified);
        }

        if(!PojoUtil.isEmpty(this.getSource())) {
            org.orcid.jaxb.model.v3.release.common.Source source = new org.orcid.jaxb.model.v3.release.common.Source();
            
            if(OrcidStringUtils.isClientId(this.getSource())) {
                source.setSourceClientId(new SourceClientId(this.getSource()));
            } else {
                source.setSourceOrcid(new SourceOrcid(this.getSource()));
            }
                       
            work.setSource(source);
        }
        
        return work;
    }               
                
    public void setCitationForDisplay(String citation) {
        this.citationForDisplay = citation;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Text getPutCode() {
        return putCode;
    }

    public void setPutCode(Text putCode) {
        this.putCode = putCode;
    }

    public Text getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(Text shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public Citation getCitation() {
        return citation;
    }

    public void setCitation(Citation citation) {
        this.citation = citation;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<ContributorsRolesAndSequences> getContributorsGroupedByOrcid() {
        return contributorsGroupedByOrcid;
    }

    public void setContributorsGroupedByOrcid(List<ContributorsRolesAndSequences> contributorsGroupedByOrcid) {
        this.contributorsGroupedByOrcid = contributorsGroupedByOrcid;
    }

    public int getNumberOfContributors() {
        return numberOfContributors;
    }

    public void setNumberOfContributors(int numberOfContributors) {
        this.numberOfContributors = numberOfContributors;
    }

    public List<ActivityExternalIdentifier> getWorkExternalIdentifiers() {
        return workExternalIdentifiers;
    }

    public void setWorkExternalIdentifiers(List<ActivityExternalIdentifier> workExternalIdentifiers) {
        this.workExternalIdentifiers = workExternalIdentifiers;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Text getWorkType() {
        return workType;
    }

    public void setWorkType(Text workType) {
        this.workType = workType;
    }

    public Text getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(Text journalTitle) {
        this.journalTitle = journalTitle;
    }

    public Text getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(Text languageCode) {
        this.languageCode = languageCode;
    }

    public Text getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Text countryCode) {
        this.countryCode = countryCode;
    }

    public Text getLanguageName() {
        return languageName;
    }

    public void setLanguageName(Text languageName) {
        this.languageName = languageName;
    }

    public Text getCountryName() {
        return countryName;
    }

    public void setCountryName(Text countryName) {
        this.countryName = countryName;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    
    public String getAssertionOriginOrcid() {
        return assertionOriginOrcid;
    }

    public void setAssertionOriginOrcid(String assertionOriginOrcid) {
        this.assertionOriginOrcid = assertionOriginOrcid;
    }

    public String getAssertionOriginClientId() {
        return assertionOriginClientId;
    }

    public void setAssertionOriginClientId(String assertionOriginClientId) {
        this.assertionOriginClientId = assertionOriginClientId;
    }

    public String getAssertionOriginName() {
        return assertionOriginName;
    }

    public void setAssertionOriginName(String assertionOriginName) {
        this.assertionOriginName = assertionOriginName;
    }

    public String getDateSortString() {
        return dateSortString;
    }

    public void setDateSortString(String dateSortString) {
        this.dateSortString = dateSortString;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Text getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Text subtitle) {
        this.subtitle = subtitle;
    }

    public TranslatedTitleForm getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(TranslatedTitleForm translatedTitle) {
        this.translatedTitle = translatedTitle;
    }    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((citation == null) ? 0 : citation.hashCode());
        result = prime * result + ((citationForDisplay == null) ? 0 : citationForDisplay.hashCode());
        result = prime * result + ((contributors == null) ? 0 : contributors.hashCode());
        result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
        result = prime * result + ((countryName == null) ? 0 : countryName.hashCode());
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((dateSortString == null) ? 0 : dateSortString.hashCode());
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + ((journalTitle == null) ? 0 : journalTitle.hashCode());
        result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
        result = prime * result + ((languageName == null) ? 0 : languageName.hashCode());
        result = prime * result + ((lastModified == null) ? 0 : lastModified.hashCode());
        result = prime * result + ((publicationDate == null) ? 0 : publicationDate.hashCode());
        result = prime * result + ((putCode == null) ? 0 : putCode.hashCode());
        result = prime * result + ((shortDescription == null) ? 0 : shortDescription.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((sourceName == null) ? 0 : sourceName.hashCode());
        result = prime * result + ((subtitle == null) ? 0 : subtitle.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((translatedTitle == null) ? 0 : translatedTitle.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
        result = prime * result + ((workExternalIdentifiers == null) ? 0 : workExternalIdentifiers.hashCode());
        result = prime * result + ((workType == null) ? 0 : workType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkForm other = (WorkForm) obj;
        if (citation == null) {
            if (other.citation != null)
                return false;
        } else if (!citation.equals(other.citation))
            return false;
        if (citationForDisplay == null) {
            if (other.citationForDisplay != null)
                return false;
        } else if (!citationForDisplay.equals(other.citationForDisplay))
            return false;
        if (contributors == null) {
            if (other.contributors != null)
                return false;
        } else if (!contributors.equals(other.contributors))
            return false;
        if (countryCode == null) {
            if (other.countryCode != null)
                return false;
        } else if (!countryCode.equals(other.countryCode))
            return false;
        if (countryName == null) {
            if (other.countryName != null)
                return false;
        } else if (!countryName.equals(other.countryName))
            return false;
        if (createdDate == null) {
            if (other.createdDate != null)
                return false;
        } else if (!createdDate.equals(other.createdDate))
            return false;
        if (dateSortString == null) {
            if (other.dateSortString != null)
                return false;
        } else if (!dateSortString.equals(other.dateSortString))
            return false;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (journalTitle == null) {
            if (other.journalTitle != null)
                return false;
        } else if (!journalTitle.equals(other.journalTitle))
            return false;
        if (languageCode == null) {
            if (other.languageCode != null)
                return false;
        } else if (!languageCode.equals(other.languageCode))
            return false;
        if (languageName == null) {
            if (other.languageName != null)
                return false;
        } else if (!languageName.equals(other.languageName))
            return false;
        if (lastModified == null) {
            if (other.lastModified != null)
                return false;
        } else if (!lastModified.equals(other.lastModified))
            return false;
        if (publicationDate == null) {
            if (other.publicationDate != null)
                return false;
        } else if (!publicationDate.equals(other.publicationDate))
            return false;
        if (putCode == null) {
            if (other.putCode != null)
                return false;
        } else if (!putCode.equals(other.putCode))
            return false;
        if (shortDescription == null) {
            if (other.shortDescription != null)
                return false;
        } else if (!shortDescription.equals(other.shortDescription))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (sourceName == null) {
            if (other.sourceName != null)
                return false;
        } else if (!sourceName.equals(other.sourceName))
            return false;
        if (subtitle == null) {
            if (other.subtitle != null)
                return false;
        } else if (!subtitle.equals(other.subtitle))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (translatedTitle == null) {
            if (other.translatedTitle != null)
                return false;
        } else if (!translatedTitle.equals(other.translatedTitle))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (visibility == null) {
            if (other.visibility != null)
                return false;
        } else if (!visibility.equals(other.visibility))
            return false;
        if (workExternalIdentifiers == null) {
            if (other.workExternalIdentifiers != null)
                return false;
        } else if (!workExternalIdentifiers.equals(other.workExternalIdentifiers))
            return false;
        if (workType == null) {
            if (other.workType != null)
                return false;
        } else if (!workType.equals(other.workType))
            return false;        
        return true;
    }

    public boolean compare(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkForm other = (WorkForm) obj;

        if (!compareStrings(citationForDisplay, other.citationForDisplay))
            return false;
        if (!compareTexts(countryCode, other.countryCode, true))
            return false;
        if (!compareTexts(countryName, other.countryName, true))
            return false;
        if (!compareTexts(journalTitle, other.journalTitle, false))
            return false;
        if (!compareTexts(languageCode, other.languageCode, true))
            return false;
        if (!compareTexts(languageName, other.languageName, true))
            return false;
        if (!compareTexts(putCode, other.putCode, true))
            return false;
        if (!compareTexts(shortDescription, other.shortDescription, false))
            return false;
        if (!compareTexts(subtitle, other.subtitle, false))
            return false;
        if (!compareTexts(title, other.title, false))
            return false;
        if (!compareTexts(url, other.url, false))
            return false;
        if (!compareTexts(workType, other.workType, true))
            return false;

        if (!isEachObjectNull(citation, other.citation)) {
            if (isAnyObjectNotNull(citation, other.citation)) {
                if (citation == null && other.citation.getCitation() != null && StringUtils.isNotBlank(other.citation.getCitation().getValue())) {
                    return false;
                }
            } else if (
                    citation.getCitation() != null && other.citation.getCitation() != null && !compareTexts(citation.getCitation(), other.citation.getCitation(), false) ||
                    citation.getCitationType() != null && other.citation.getCitationType() != null && !compareTexts(citation.getCitationType(), other.citation.getCitationType(), false)
            )
                return false;
        }
        if (!isEachObjectNull(translatedTitle, other.translatedTitle)) {
            if (isAnyObjectNotNull(translatedTitle, other.translatedTitle)) {
                if (translatedTitle == null && StringUtils.isNotBlank(other.translatedTitle.getContent())) {
                    return false;
                }
            } else if (
                    other.translatedTitle.getContent() != null && !translatedTitle.getContent().equals(other.translatedTitle.getContent()) ||
                    other.translatedTitle.getLanguageCode() != null && !translatedTitle.getLanguageCode().equals(other.translatedTitle.getLanguageCode())
            )
                return false;
        }
        if (!isEachObjectNull(publicationDate, other.publicationDate)) {
            if (isAnyObjectNotNull(publicationDate, other.publicationDate)) {
                if (publicationDate == null && StringUtils.isNotBlank(other.publicationDate.getYear()))
                    return false;
            } else if (
                    !compareStrings(publicationDate.getYear(), other.publicationDate.getYear()) ||
                    !compareStrings(publicationDate.getMonth(), other.publicationDate.getMonth()) ||
                    !compareStrings(publicationDate.getDay(), other.publicationDate.getDay())
            )
                return false;
        }
        if (visibility != null && other.visibility != null && !visibility.getVisibility().value().equals(other.visibility.getVisibility().value()))
            return false;
        if (isAnyObjectNotNull(workExternalIdentifiers, other.workExternalIdentifiers)) {
            return false;
        } else if (workExternalIdentifiers != null && other.workExternalIdentifiers != null && workExternalIdentifiers.size() != other.workExternalIdentifiers.size()) {
            return false;
        } else if (compareExternalIdentifiers(workExternalIdentifiers, other.workExternalIdentifiers))
            return false;
        if (isAnyObjectNotNull(contributors, other.contributors)) {
            if (contributors == null && other.contributors != null && other.contributors.size() > 0) {
                return false;
            }
        } else if (contributors != null && other.contributors != null && contributors.size() != other.contributors.size()) {
            return false;
        } else if (compareContributors(contributors, other.contributors))
            return false;
        if (isAnyObjectNotNull(contributorsGroupedByOrcid, other.contributorsGroupedByOrcid)) {
            if (contributorsGroupedByOrcid == null && other.contributorsGroupedByOrcid != null && other.contributorsGroupedByOrcid.size() > 0) {
                return false;
            }
        } else if (contributorsGroupedByOrcid != null && other.contributorsGroupedByOrcid != null && contributorsGroupedByOrcid.size() != other.contributorsGroupedByOrcid.size()) {
            return false;
        } else if (compareContributorsGroupedByOrcid(contributorsGroupedByOrcid, other.contributorsGroupedByOrcid))
            return false;
        return true;
    }

    private boolean compareExternalIdentifiers(List<ActivityExternalIdentifier> a, List<ActivityExternalIdentifier> b) {
        if (isEachObjectNull(a, b)) {
            return false;
        }
        for (int i = 0; i < a.size() ; i++) {
            if (!a.get(i).compare(b.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean compareContributors(List<Contributor> a, List<Contributor> b) {
        if (isEachObjectNull(a, b)) {
            return false;
        }
        for (int i = 0; i < a.size() ; i++) {
            if (!a.get(i).compare(b.get(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean compareContributorsGroupedByOrcid(List<ContributorsRolesAndSequences> a, List<ContributorsRolesAndSequences> b) {
        if (isEachObjectNull(a, b)) {
            return false;
        }
        for (int i = 0; i < a.size() ; i++) {
            if (!a.get(i).compare(b.get(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean compareTexts(Text a, Text b, boolean ignoreCase) {
        if (isEachObjectNull(a, b)) {
            return true;
        } else if (isAnyObjectNotNull(a, b)) {
            if (a == null && b.getValue() == null) {
                return true;
            } else if (a == null && StringUtils.isBlank(b.getValue())) {
                return true;
            }
            return false;
        } else if (a.getValue() != null && b.getValue() != null) {
            if (ignoreCase) {
                if (!a.getValue().equalsIgnoreCase(b.getValue())) {
                    return false;
                }
            } else {
                if (!a.getValue().equals(b.getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean compareStrings(String a, String b) {
        if (isEachObjectNull(a, b)) {
            return true;
        } else if (isAnyObjectNotNull(a, b)) {
            if (a == null && b != null && StringUtils.isNotBlank(b)) {
                return true;
            }
            return false;
        } else return a.equalsIgnoreCase(b);
    }

    public static boolean isEachObjectNull(Object a, Object b) {
        return a == null && b == null;
    }

    public static boolean isAnyObjectNotNull(Object a, Object b) {
        if (a == null && b != null || a != null && b == null) {
            return true;
        }
        return false;
    }

}
