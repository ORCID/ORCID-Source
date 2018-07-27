package org.orcid.pojo.grouping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.pojo.ajaxForm.PeerReviewForm;

public class PeerReviewGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PeerReviewDuplicateGroup> duplicateGroups;

    private String description;

    private String name;

    private String type;

    private long groupId;

    public List<PeerReviewDuplicateGroup> getPeerReviewDuplicateGroups() {
        return duplicateGroups;
    }

    public void setPeerReviewDuplicateGroups(List<PeerReviewDuplicateGroup> duplicateGroups) {
        this.duplicateGroups = duplicateGroups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static PeerReviewGroup getInstance(org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup peerReviewGroup, GroupIdRecord groupIdRecord) {
        String groupName = groupIdRecord != null && groupIdRecord.getName() != null ? groupIdRecord.getName() : "";
        String groupDescription = groupIdRecord != null && groupIdRecord.getDescription() != null ? groupIdRecord.getDescription() : "";
        String type = groupIdRecord != null && groupIdRecord.getType() != null ? groupIdRecord.getType() : "";

        PeerReviewGroup group = new PeerReviewGroup();
        group.setName(groupName);
        group.setType(type);
        group.setDescription(groupDescription);
        group.setGroupId(groupIdRecord.getPutCode());
        group.setPeerReviewDuplicateGroups(new ArrayList<>());

        for (org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewDuplicateGroup duplicateGroup : peerReviewGroup.getPeerReviewGroup()) {
            int highestDisplayIndex = 0;
            PeerReviewDuplicateGroup duplicateGroupPojo = new PeerReviewDuplicateGroup();
            duplicateGroupPojo.setPeerReviews(new ArrayList<>());
            for (PeerReviewSummary summary : duplicateGroup.getPeerReviewSummary()) {
                int displayIndex = summary.getDisplayIndex() != null ? Integer.parseInt(summary.getDisplayIndex()) : 0;
                if (displayIndex >= highestDisplayIndex) {
                    highestDisplayIndex = displayIndex;
                    duplicateGroupPojo.setActivePutCode(summary.getPutCode());
                }
                // any unique number used for id
                duplicateGroupPojo.setId(summary.getPutCode());
                PeerReviewForm peerReviewForm = PeerReviewForm.valueOf(summary);
                duplicateGroupPojo.getPeerReviews().add(peerReviewForm);
            }
            group.getPeerReviewDuplicateGroups().add(duplicateGroupPojo);
        }
        return group;
    }

}
