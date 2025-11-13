package org.orcid.pojo.grouping;

import java.util.ArrayList;
import java.util.List;

import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
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

import javax.annotation.Resource;

import static org.orcid.pojo.ajaxForm.PojoUtil.getWorkForm;

public class WorkGroup extends ActivityGroup {

    private static final long serialVersionUID = 1L;

    private List<WorkForm> works;

    private int featuredDisplayIndex;

    public List<WorkForm> getWorks() {
        return works;
    }

    public void setWorks(List<WorkForm> works) {
        this.works = works;
    }

    public int getFeaturedDisplayIndex() {
        return featuredDisplayIndex;
    }

    public void setFeaturedDisplayIndex(int featuredDisplayIndex) {
        this.featuredDisplayIndex = featuredDisplayIndex;
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
}
