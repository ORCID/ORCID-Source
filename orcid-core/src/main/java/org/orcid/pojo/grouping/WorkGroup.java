package org.orcid.pojo.grouping;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.pojo.WorkGroupExtended;
import org.orcid.pojo.WorkSummaryExtended;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.WorkForm;

public class WorkGroup extends ActivityGroup {

    private static final long serialVersionUID = 1L;

    private List<WorkForm> works;

    public List<WorkForm> getWorks() {
        return works;
    }

    public void setWorks(List<WorkForm> works) {
        this.works = works;
    }

    public void addWork(WorkForm work) {
        if(this.works == null) {
            this.works = new ArrayList<WorkForm>();
        }
        this.works.add(work);
    }
    
    public static WorkGroup valueOf(org.orcid.jaxb.model.v3.release.record.summary.WorkGroup workGroup, int id, String orcid) {
        WorkGroup group = new WorkGroup();
        group.setGroupId(id);
        group.setWorks(new ArrayList<>());

        WorkType workType = null;

        Long maxDisplayIndex = null;
        for (WorkSummary workSummary : workGroup.getWorkSummary()) {
            WorkForm workForm = getWorkForm(workSummary);
            group.getWorks().add(workForm);

            Long displayIndex = Long.parseLong(workSummary.getDisplayIndex());
            if (maxDisplayIndex == null || displayIndex > maxDisplayIndex) {
                maxDisplayIndex = displayIndex;
                group.setActivePutCode(workSummary.getPutCode());
                group.setDefaultPutCode(workSummary.getPutCode());
                group.setActiveVisibility(workSummary.getVisibility().name());
            }

            if (orcid.equals(workSummary.getSource().retrieveSourcePath())) {
                group.setUserVersionPresent(true);
            }

            workType = workSummary.getType();
        }

        if (workGroup.getIdentifiers() != null) {
            List<ActivityExternalIdentifier> workExternalIdentifiersList = new ArrayList<ActivityExternalIdentifier>();
            for (ExternalID extId : workGroup.getIdentifiers().getExternalIdentifier()) {
                if (extId.getRelationship() == null) {
                    if (org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.equals(extId.getType())) {
                        if (WorkType.BOOK.equals(workType)) {
                            extId.setRelationship(Relationship.PART_OF);
                        } else {
                            extId.setRelationship(Relationship.SELF);
                        }
                    } else if (org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISBN.equals(extId.getType())) {
                        if (WorkType.BOOK_CHAPTER.equals(workType) || WorkType.CONFERENCE_PAPER.equals(workType)) {
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
            group.setExternalIdentifiers(workExternalIdentifiersList);
        }

        return group;
    }

    public static WorkGroup valueOf(WorkGroupExtended workGroup, int id, String orcid) {
        WorkGroup group = new WorkGroup();
        group.setGroupId(id);
        group.setWorks(new ArrayList<>());

        WorkType workType = null;

        Long maxDisplayIndex = null;
        for (WorkSummaryExtended workSummary : workGroup.getWorkSummary()) {
            WorkForm workForm = getWorkForm(workSummary);
            group.getWorks().add(workForm);

            Long displayIndex = Long.parseLong(workSummary.getDisplayIndex());
            if (maxDisplayIndex == null || displayIndex > maxDisplayIndex) {
                maxDisplayIndex = displayIndex;
                group.setActivePutCode(workSummary.getPutCode());
                group.setDefaultPutCode(workSummary.getPutCode());
                group.setActiveVisibility(workSummary.getVisibility().name());
            }

            if (orcid.equals(workSummary.getSource().retrieveSourcePath())) {
                group.setUserVersionPresent(true);
            }

            workType = workSummary.getType();
        }

        if (workGroup.getIdentifiers() != null) {
            List<ActivityExternalIdentifier> workExternalIdentifiersList = new ArrayList<ActivityExternalIdentifier>();
            for (ExternalID extId : workGroup.getIdentifiers().getExternalIdentifier()) {
                if (extId.getRelationship() == null) {
                    if (org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.equals(extId.getType())) {
                        if (WorkType.BOOK.equals(workType)) {
                            extId.setRelationship(Relationship.PART_OF);
                        } else {
                            extId.setRelationship(Relationship.SELF);
                        }
                    } else if (org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISBN.equals(extId.getType())) {
                        if (WorkType.BOOK_CHAPTER.equals(workType) || WorkType.CONFERENCE_PAPER.equals(workType)) {
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
            group.setExternalIdentifiers(workExternalIdentifiersList);
        }

        return group;
    }

    private static WorkForm getWorkForm(WorkSummary workSummary) {
        WorkForm workForm = new WorkForm();
        workForm.setPutCode(Text.valueOf(workSummary.getPutCode()));

        String title = workSummary.getTitle() != null && workSummary.getTitle().getTitle() != null ? workSummary.getTitle().getTitle().getContent() : "";
        workForm.setTitle(Text.valueOf(title));

        if (workSummary.getJournalTitle() != null) {
            workForm.setJournalTitle(Text.valueOf(workSummary.getJournalTitle().getContent()));
        }

        if (workSummary.getPublicationDate() != null) {
            workForm.setPublicationDate(getPublicationDate(workSummary.getPublicationDate()));
        }

        workForm.setSource(workSummary.getSource().retrieveSourcePath());
        if (workSummary.getSource().getSourceName() != null) {
            workForm.setSourceName(workSummary.getSource().getSourceName().getContent());
        }
        
        if (workSummary.getSource().getAssertionOriginClientId() != null) {
            workForm.setAssertionOriginClientId(workSummary.getSource().getAssertionOriginClientId().getPath());
        }
        
        if (workSummary.getSource().getAssertionOriginOrcid() != null) {
            workForm.setAssertionOriginOrcid(workSummary.getSource().getAssertionOriginOrcid().getPath());
        }
        
        if (workSummary.getSource().getAssertionOriginName() != null) {
            workForm.setAssertionOriginName(workSummary.getSource().getAssertionOriginName().getContent());
        }

        workForm.setWorkType(Text.valueOf(workSummary.getType().value()));
        workForm.setVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(workSummary.getVisibility()));
        WorkForm.populateExternalIdentifiers(workSummary.getExternalIdentifiers(), workForm, workSummary.getType());
        workForm.setCreatedDate(Date.valueOf(workSummary.getCreatedDate()));
        workForm.setLastModified(Date.valueOf(workSummary.getLastModifiedDate()));      
        return workForm;
    }

    private static WorkForm getWorkForm(WorkSummaryExtended workSummary) {
        WorkForm workForm = new WorkForm();
        workForm.setPutCode(Text.valueOf(workSummary.getPutCode()));

        String title = workSummary.getTitle() != null && workSummary.getTitle().getTitle() != null ? workSummary.getTitle().getTitle().getContent() : "";
        workForm.setTitle(Text.valueOf(title));

        if (workSummary.getJournalTitle() != null) {
            workForm.setJournalTitle(Text.valueOf(workSummary.getJournalTitle().getContent()));
        }

        if (workSummary.getPublicationDate() != null) {
            workForm.setPublicationDate(getPublicationDate(workSummary.getPublicationDate()));
        }

        if (workSummary.getSource() != null) {
            workForm.setSource(workSummary.getSource().retrieveSourcePath());
            if (workSummary.getSource().getSourceName() != null) {
                workForm.setSourceName(workSummary.getSource().getSourceName().getContent());
            }

            if (workSummary.getSource().getAssertionOriginClientId() != null) {
                workForm.setAssertionOriginClientId(workSummary.getSource().getAssertionOriginClientId().getPath());
            }

            if (workSummary.getSource().getAssertionOriginOrcid() != null) {
                workForm.setAssertionOriginOrcid(workSummary.getSource().getAssertionOriginOrcid().getPath());
            }

            if (workSummary.getSource().getAssertionOriginName() != null) {
                workForm.setAssertionOriginName(workSummary.getSource().getAssertionOriginName().getContent());
            }
        }

        workForm.setWorkType(Text.valueOf(workSummary.getType().value()));
        workForm.setVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(workSummary.getVisibility()));
        WorkForm.populateExternalIdentifiers(workSummary.getExternalIdentifiers(), workForm, workSummary.getType());
        workForm.setCreatedDate(Date.valueOf(workSummary.getCreatedDate()));
        workForm.setLastModified(Date.valueOf(workSummary.getLastModifiedDate()));
        workForm.setContributorsGroupedByOrcid(workSummary.getContributorsGroupedByOrcid());
        workForm.setNumberOfContributors(workSummary.getNumberOfContributors());        
        return workForm;
    }

    private static Date getPublicationDate(PublicationDate publicationDate) {
        return PojoUtil.convertDate(publicationDate);
    }

}
