package org.orcid.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.common.FuzzyDate;
import org.orcid.jaxb.model.v3.rc1.common.PublicationDate;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.orcid.pojo.ajaxForm.WorkForm;

public class WorkGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<WorkForm> works;

    private Long activePutCode;

    private WorkForm defaultWork;

    private int groupId;

    public String activeVisibility;

    private boolean userVersionPresent;

    private List<WorkExternalIdentifier> workExternalIdentifiers = new ArrayList<>();

    public List<WorkForm> getWorks() {
        return works;
    }

    public void setWorks(List<WorkForm> works) {
        this.works = works;
    }

    public Long getActivePutCode() {
        return activePutCode;
    }

    public void setActivePutCode(Long activePutCode) {
        this.activePutCode = activePutCode;
    }

    public WorkForm getDefaultWork() {
        return defaultWork;
    }

    public void setDefaultWork(WorkForm defaultWork) {
        this.defaultWork = defaultWork;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isUserVersionPresent() {
        return userVersionPresent;
    }

    public void setUserVersionPresent(boolean userVersionPresent) {
        this.userVersionPresent = userVersionPresent;
    }

    public String getActiveVisibility() {
        return activeVisibility;
    }

    public void setActiveVisibility(String activeVisibility) {
        this.activeVisibility = activeVisibility;
    }

    public List<WorkExternalIdentifier> getWorkExternalIdentifiers() {
        return workExternalIdentifiers;
    }

    public void setWorkExternalIdentifiers(List<WorkExternalIdentifier> workExternalIdentifiers) {
        this.workExternalIdentifiers = workExternalIdentifiers;
    }

    public static WorkGroup valueOf(org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup workGroup, int id, String orcid) {
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
                group.setActiveVisibility(workSummary.getVisibility().name());
                group.setDefaultWork(workForm);
            }

            if (workSummary.getSource().retrieveSourcePath().equals(orcid)) {
                group.setUserVersionPresent(true);
            }

            workType = workSummary.getType();
        }

        if (workGroup.getIdentifiers() != null) {
            List<WorkExternalIdentifier> workExternalIdentifiersList = new ArrayList<WorkExternalIdentifier>();
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
                workExternalIdentifiersList.add(WorkExternalIdentifier.valueOf(extId));
            }
            group.setWorkExternalIdentifiers(workExternalIdentifiersList);
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

        workForm.setWorkType(Text.valueOf(workSummary.getType().value()));
        workForm.setVisibility(org.orcid.pojo.ajaxForm.Visibility.valueOf(workSummary.getVisibility()));
        WorkForm.populateExternalIdentifiers(workSummary.getExternalIdentifiers(), workForm, workSummary.getType());
        return workForm;
    }

    private static Date getPublicationDate(PublicationDate publicationDate) {
        if (publicationDate != null) {
            Integer year = PojoUtil.isEmpty(publicationDate.getYear()) ? null : Integer.valueOf(publicationDate.getYear().getValue());
            Integer month = PojoUtil.isEmpty(publicationDate.getMonth()) ? null : Integer.valueOf(publicationDate.getMonth().getValue());
            Integer day = PojoUtil.isEmpty(publicationDate.getDay()) ? null : Integer.valueOf(publicationDate.getDay().getValue());
            if (year != null && year == 0) {
                year = null;
            }
            if (month != null && month == 0) {
                month = null;
            }
            if (day != null && day == 0) {
                day = null;
            }
            return Date.valueOf(FuzzyDate.valueOf(year, month, day));
        }
        return null;
    }

}
